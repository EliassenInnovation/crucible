package com.eliassen.crucible.web.drivers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.time.Duration;

import org.apache.commons.lang3.SystemUtils;
import com.eliassen.crucible.common.helpers.FileHelper;
import com.eliassen.crucible.core.helpers.Logger;
import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.web.helpers.ScreenShotter;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;

public abstract class CrucibleWebdriver implements WebDriver
{
	public static final String CHROME = "chrome";

	protected MutableCapabilities options;
	private WebDriver instance;

	protected CrucibleWebdriver() {	}
	public WebDriver getInstance()
	{
		return instance;
	}
	protected void setInstance(WebDriver instance)
	{
		 this.instance = instance;
	}

	public boolean hasQuit()
	{
		boolean hasQuit = true;
		if(getInstance() != null)
		{
			String instanceAsString = getInstance().toString();
			hasQuit = instanceAsString.contains("null");
		}

		return hasQuit;
	}

	public boolean isClosed()
	{
		boolean isClosed = true;
		if(getInstance() != null)
		{
			try
			{
				getInstance().getCurrentUrl();
				isClosed = false;
			}
			catch(Exception e)
			{
				//do nothing
			}
		}

		return isClosed;
	}
	
	@Override
	public void get(String url) 
	{
		instance.get(url);
	}

	@Override
	public String getCurrentUrl() 
	{
		return instance.getCurrentUrl();
	}

	@Override
	public String getTitle() 
	{
		return instance.getTitle();
	}

	@Override
	public List<WebElement> findElements(By by)
	{
		return instance.findElements(by);
	}

	@Override
	public WebElement findElement(By by)
	{
		try {
			return instance.findElement(by);
		} catch (NoSuchElementException nse){
			if(SystemHelper.getApplicationSettingBoolean("noSuchElementScreenShots")) {
				new ScreenShotter().safeAttachScreenshot(CurrentPage.getScenario(), "Point of failure");
			}
			throw nse;
		}
	}
	
	public WebElement findElement(String elementXPath)
	{
		return instance.findElement(By.xpath(elementXPath));
	}

	@Override
	public String getPageSource() 
	{
		return instance.getPageSource();
	}

	@Override
	public void close() 
	{
		instance.close();
	}

	@Override
	public void quit() 
	{
		try {
			instance.close();
			instance.quit();
		}
	    catch(org.openqa.selenium.WebDriverException e) {
			Logger.logError("There was an error quitting the webdriver. " + e.getMessage());
		}
	}

	@Override
	public Set<String> getWindowHandles() 
	{
		return instance.getWindowHandles();
	}

	@Override
	public String getWindowHandle() 
	{
		return instance.getWindowHandle();
	}

	@Override
	public TargetLocator switchTo() 
	{
		return instance.switchTo();
	}

	@Override
	public Navigation navigate() 
	{
		return instance.navigate();
	}

	@Override
	public Options manage() 
	{
		return instance.manage();
	}
	
	public void goTo(String url)
	{
		try {
			instance.navigate().to(url);
		} catch (TimeoutException t) {
			String navigationAttempts = "navigation attempts";
			int navigationAttemptsCount = 0;
			if(CurrentPage.isPersisted(navigationAttempts)){
				navigationAttemptsCount = Integer.parseInt(CurrentPage.retrievePersisted(navigationAttempts));
			}

			if(navigationAttemptsCount > 2){
				throw t;
			} else {
				Logger.log("Failed to navigate, attempted " + navigationAttemptsCount + " times. Trying again in 20 seconds");
                try {
                    Thread.sleep(20000);
					goTo(url);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

				CurrentPage.storePersisted(navigationAttempts,String.valueOf(++navigationAttemptsCount));
            }

		}
	}

	private WebDriver resolveDriver(DriverName driverName, boolean useProxy)
	{
		WebDriver webDriver = null;
		switch(driverName)
		{
			case firefox, firefox_headless:
				webDriver = new FirefoxDriver();
				webDriver.manage().window().setSize(new Dimension(1600,900));
				webDriver.manage().deleteAllCookies();
				webDriver.manage().window().maximize();
				break;
			case remote:
				String hubAddress = SystemHelper.getConfigSetting("selenium_hub.address");
				URL hubUrl = null;
				try
				{
					hubUrl = new URL(hubAddress);
				} catch (MalformedURLException e)
				{
					e.printStackTrace();
				}
				Collection<String> tagNames = CurrentPage.getScenario().getSourceTagNames();
				if(tagNames.contains("@" + DriverName.chrome.toString()))
				{
					options = CrucibleChromeWebdriver.getChromeOptions();
					options.setCapability(CapabilityType.PLATFORM_NAME, Platform.ANY);
					options.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
					options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
				}

				webDriver = new RemoteWebDriver(hubUrl, options);

				break;

			case edge, edge_headless:
				options = CrucibleEdgeWebdriver.getEdgeOptions();

				if(driverName.equals(DriverName.edge_headless))
				{
					((EdgeOptions)options).addArguments("headless");
					((EdgeOptions)options).addArguments("window-size=1600,900");
				}

				try
				{
					webDriver = new EdgeDriver((EdgeOptions) options);
				}
				catch(IllegalStateException ise)
				{
					String edgeDriverName = System.getProperty("webdriver.edge.driver");

					FileHelper fileHelper = new FileHelper();
					fileHelper.ExtractFile(edgeDriverName, "a+rwx");

					webDriver = new EdgeDriver((EdgeOptions)options);
				}

				webDriver.manage().window().setSize(new Dimension(1600,900));
				webDriver.manage().window().maximize();
				break;

			case chrome, chrome_headless, chrome_incognito, chrome_incognito_headless:
			default:
				options = CrucibleChromeWebdriver.getChromeOptions();

				if(driverName.equals(DriverName.chrome_headless) ||
				   driverName.equals(DriverName.chrome_incognito_headless))
				{
					((ChromeOptions)options).addArguments("--headless");
					((ChromeOptions)options).addArguments("--window-size=1600,900");
				}

				if(driverName.equals(DriverName.chrome_incognito) ||
						driverName.equals(DriverName.chrome_incognito_headless))
				{
					((ChromeOptions)options).addArguments("--incognito");
				}

				try
				{
					webDriver = new ChromeDriver((ChromeOptions)options);
				}
				catch(IllegalStateException ise)
				{
					String chromeDriverName = System.getProperty("webdriver.chrome.driver");

					FileHelper fileHelper = new FileHelper();

					if(!SystemUtils.IS_OS_MAC)
					{
						fileHelper.ExtractFile(chromeDriverName, "a+rwx");
					}
					else
					{
						fileHelper.ExtractFile(chromeDriverName, "755", "." + File.separator);
					}


					webDriver = new ChromeDriver((ChromeOptions)options);
				}

				break;
		}

		return webDriver;
	}

	public void enterText(WebElement element, String text) 
	{
   	 	element.sendKeys(text);
	}

	public boolean driverReusable()
	{
		String allowDriverReuseKey = "allowDriverReuse";
		String allowDriverReuse = SystemHelper.getApplicationSetting(allowDriverReuseKey);

		return Boolean.parseBoolean(allowDriverReuse);
	}

	public void setImplicitTimeout()
	{
		setImplicitTimeoutInSeconds(WaitManager.getImplicitWait());
	}

	public void setImplicitTimeoutInSeconds(double interval)
	{
		setImplicitTimeout((long)(interval * 1000));
	}

	public void setImplicitTimeoutInMilliseconds(long interval)
	{
		setImplicitTimeout(interval);
	}

	public void setImplicitTimeout(long interval)
	{
		getInstance().manage().timeouts().implicitlyWait(Duration.ofMillis(interval));
	}

	public void setPageLoadTTimeout()
	{
		setPageLoadTTimeoutInSeconds(WaitManager.getPageLoadTimeout());
	}

	public void setPageLoadTTimeoutInSeconds(double interval)
	{
		setPageLoadTTimeout((long)(interval*1000));
	}

	public void setPageLoadTTimeoutInMilliseconds(long interval)
	{
		setPageLoadTTimeout(interval);
	}

	public void setPageLoadTTimeout(long interval)
	{
		getInstance().manage().timeouts().pageLoadTimeout(Duration.ofMillis(interval));
	}

	/**
	 * Checks first to see if there is an @downloadPath tag and returns that
	 * It will then check to see if the JENKINS_HOME environment variable is set. If so it returns $JENKINS_HOME/userContent/downloads
	 * Finally it will return $user.home/downloads if the previous two conditions are not met.
	 * @return
	 */
	public static String getDownloadFilePath(){
		String downloadFilepath = null;
		String jenkinsHome = SystemHelper.getEnvironmentVariable("JENKINS_HOME");

		if(CurrentPage.isPersisted("downloadPath_tag")){
			downloadFilepath = CurrentPage.retrievePersisted("downloadPath_tag");
		} else if(jenkinsHome != null && !jenkinsHome.isEmpty()) {
			downloadFilepath = jenkinsHome + File.separator + "userContent" + File.separator + "downloads";
		} else {
			downloadFilepath = System.getProperty("user.home") + File.separator + "Downloads";
		}

		return downloadFilepath;
	}

	public MutableCapabilities getOptions() {
		return options;
	}
}
