package com.eliassen.crucible.web.drivers;

import java.io.File;
import java.util.Locale;

import org.apache.commons.lang3.SystemUtils;
import com.eliassen.crucible.common.helpers.FileHelper;
import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.web.drivers.mocks.MockWebdriver;
import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverFactory
{
	public static final String FIREFOX_DRIVER_PROPERTY = "webdriver.gecko.driver";
	public static final String CHROME_DRIVER_PROPERTY = "webdriver.chrome.driver";
	public static final String EDGE_DRIVER_PROPERTY = "webdriver.edge.driver";
	public static final String SAFARI_DRIVER_PROPERTY = "webdriver.safari.driver";
	public static final String WEBDRIVER_HTTP_FACTORY_PROPERTY = "webdriver.http.factory";
	public static final String WINDOWS_EXTENSION = ".exe";

	public static final String CHROMEDRIVER_NAME = "chromedriver";
	public static final String EDGEDRIVER_NAME = "msedgedriver";
	public static final String FIREFOXDRIVER_NAME = "geckodriver";

	public static final String SAFARIDRIVER_NAME = "safaridriver";

	public static final String WEB_DRIVER_MANAGER = "WEB_DRIVER_MANAGER";

	public String operatingSystem;

	public DriverFactory()
	{
		setDriverPaths();
	}
	
	private void setDriverPaths()
	{
		System.setProperty(WEBDRIVER_HTTP_FACTORY_PROPERTY,"jdk-http-client");

		//TODO
		//Add paths for other drivers
		if(SystemUtils.IS_OS_WINDOWS)
		{
			System.setProperty(CHROME_DRIVER_PROPERTY,"./" + CHROMEDRIVER_NAME + WINDOWS_EXTENSION);
			System.setProperty(FIREFOX_DRIVER_PROPERTY,"./" + FIREFOXDRIVER_NAME + WINDOWS_EXTENSION);
			System.setProperty(EDGE_DRIVER_PROPERTY,"./" + EDGEDRIVER_NAME + WINDOWS_EXTENSION);
			operatingSystem = "Windows";
		}
		else if(SystemUtils.IS_OS_LINUX)
		{
			System.setProperty(CHROME_DRIVER_PROPERTY,"./" + CHROMEDRIVER_NAME);
			System.setProperty(FIREFOX_DRIVER_PROPERTY,"./" + FIREFOXDRIVER_NAME);
			System.setProperty(EDGE_DRIVER_PROPERTY,"./" + EDGEDRIVER_NAME);
			operatingSystem = "Linux";
		}
		else if(SystemUtils.IS_OS_MAC)
		{
			System.setProperty(CHROME_DRIVER_PROPERTY,"./" + CHROMEDRIVER_NAME);
			System.setProperty(SAFARI_DRIVER_PROPERTY,"./" + SAFARIDRIVER_NAME);
			operatingSystem = "Mac";
		}
	}
		
	public CrucibleWebdriver createDriver()
	{
		String browserName = SystemHelper.getCommandLineParameter(SystemHelper.BROWSER);
		DriverName driverName = DriverName.valueOf(browserName.toLowerCase(Locale.ROOT));

		if(driverName != null && !browserName.isEmpty())
		{
			return createDriver(driverName);
		}
		else
		{
			return createDriver(DriverName.chrome);
		}
	}
	
	public CrucibleWebdriver createDriver(DriverName driverName)
	{
		return createDriver(driverName, false);
	}

	public CrucibleWebdriver createDriver(DriverName driverName, boolean useProxy)
	{
		CrucibleWebdriver driver;
		boolean useWebDriverManager = SystemHelper.getApplicationSettingBoolean(WEB_DRIVER_MANAGER) &&
				!SystemHelper.isRunningInDocker();

		switch(driverName)
		{
			case firefox:
			case firefox_headless:
				if(useWebDriverManager) {
					WebDriverManager.firefoxdriver().setup();
				}
				driver = new CrucibleFirefoxWebdriver(driverName,useProxy);
				break;
			case remote:
				//TODO transfer from Driver
			case edge:
			case edge_headless:
				if(useWebDriverManager) {
					WebDriverManager.edgedriver().setup();
				}
				driver = new CrucibleEdgeWebdriver(driverName,useProxy);
				break;
			case mock:
				driver = new MockWebdriver();
				break;
			case chrome:
			case chrome_headless:
			case chrome_incognito:
			case chrome_incognito_headless:
			default:
				if(useWebDriverManager) {
					WebDriverManager.chromedriver().setup();
				}
				driver = new CrucibleChromeWebdriver(driverName,useProxy);
				break;
		}
		driver.setImplicitTimeout();
		driver.setPageLoadTTimeout();
		return driver;
	}

	public static void extractDriver(String driverName)
	{
		FileHelper fileHelper = new FileHelper();
		try {
			if (!SystemUtils.IS_OS_MAC) {
				fileHelper.ExtractFile(driverName, "a+rwx");
			} else {
				fileHelper.ExtractFile(driverName, "755", "." + File.separator);
			}
		}
		catch (NullPointerException npe)
		{
			if(driverName.contains("./"))
			{
				extractDriver(driverName.replace("./",""));
			}
			else
			{
				throw npe;
			}
		}
	}
}
