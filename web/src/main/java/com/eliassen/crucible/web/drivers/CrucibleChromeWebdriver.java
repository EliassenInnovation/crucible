package com.eliassen.crucible.web.drivers;

import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.core.helpers.Logger;
import com.eliassen.crucible.web.helpers.TestHelper;
import org.json.JSONArray;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.NoSuchDriverException;


import java.util.HashMap;
import java.util.Map;

public class CrucibleChromeWebdriver extends CrucibleWebdriver
{
    public static int retryCount = 0;

    public CrucibleChromeWebdriver(DriverName driverName, boolean useProxy) {
        WebDriver driver = null;
        options = getChromeOptions();

        if (driverName.equals(DriverName.chrome_headless) ||
                driverName.equals(DriverName.chrome_incognito_headless))
        {
            ((ChromeOptions) options).addArguments("--headless");
            ((ChromeOptions) options).addArguments("--window-size=1600,900");
        }

        if (driverName.equals(DriverName.chrome_incognito) ||
                driverName.equals(DriverName.chrome_incognito_headless))
        {
            ((ChromeOptions) options).addArguments("--incognito");
        }

        if(SystemHelper.isRunningInDocker())
        {
            ((ChromeOptions) options).addArguments("--disable-dev-shm-usage");
        }

        try
        {
            driver = new ChromeDriver((ChromeOptions) options);
        }
        catch (IllegalStateException | NoSuchDriverException e)
        {
            try
            {
                String chromeDriverName = System.getProperty(DriverFactory.CHROME_DRIVER_PROPERTY);
                DriverFactory.extractDriver(chromeDriverName);

                driver = new ChromeDriver((ChromeOptions) options);
            }
            catch (IllegalStateException | NoSuchDriverException e2) {
                Logger.logError("Failed to instantiate Chromedriver!");
                if(retryCount <= 3){
                    TestHelper.waitSilently(1);
                    Logger.log("I'll try to get the driver again, this is retry attempt " + ++retryCount);
                    String chromeDriverName = System.getProperty(DriverFactory.CHROME_DRIVER_PROPERTY);
                    DriverFactory.extractDriver(chromeDriverName);
                    driver = new ChromeDriver((ChromeOptions) options);
                } else {
                    throw e2;
                }
            }
        }

        setInstance(driver);
    }

    public static ChromeOptions getChromeOptions()
    {
        Map<String, Object> chromePrefs = new HashMap<String, Object>();
        chromePrefs.put("download.prompt_for_download", false);
        chromePrefs.put("download.default_directory", getDownloadFilePath());

        ChromeOptions options = new ChromeOptions();
        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        options.addArguments("--window-size=1600,900");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--no-sandbox");

        checkForOverrideOptions(options, chromePrefs);
        options.setExperimentalOption("prefs", chromePrefs);

        return options;
    }

    public static void checkForOverrideOptions(ChromeOptions options, Map<String, Object> chromePrefs){
        JSONArray optionsArray = SystemHelper.getApplicationSettingArray("webDriverOptions.chromeDriver");
        if(optionsArray != null && !optionsArray.isEmpty())
        {
            optionsArray.forEach(o -> {
                if(o.toString().contains("=")) {
                    String[] parts = o.toString().split("=");
                    String key = parts[0];
                    Object value = TestHelper.inferDataType(parts[1]);

                    chromePrefs.put(key,value);
                } else {

                    options.addArguments(o.toString());
                }
            });
        }
    }
}
