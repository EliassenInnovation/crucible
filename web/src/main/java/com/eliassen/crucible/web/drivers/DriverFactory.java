package com.eliassen.crucible.web.drivers;

import java.util.Locale;
import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.web.drivers.mocks.MockWebdriver;

public class DriverFactory
{
	public static final String WEBDRIVER_HTTP_FACTORY_PROPERTY = "webdriver.http.factory";
	public static final String WEB_DRIVER_MANAGER = "WEB_DRIVER_MANAGER";

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
