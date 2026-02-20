package com.eliassen.crucible.web.drivers;

import java.io.File;
import java.util.Locale;

import org.apache.commons.lang3.SystemUtils;
import com.eliassen.crucible.common.helpers.FileHelper;
import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.web.drivers.mocks.MockWebdriver;

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
				driver = new CrucibleFirefoxWebdriver(driverName,useProxy);
				break;
			case remote:
				//TODO transfer from Driver
			case edge:
			case edge_headless:
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
				driver = new CrucibleChromeWebdriver(driverName,useProxy);
				break;
		}
		driver.setImplicitTimeout();
		driver.setPageLoadTTimeout();
		return driver;
	}
}
