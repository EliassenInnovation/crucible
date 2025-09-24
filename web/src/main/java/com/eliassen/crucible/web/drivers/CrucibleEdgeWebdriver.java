package com.eliassen.crucible.web.drivers;

import com.eliassen.crucible.common.helpers.FileHelper;
import com.eliassen.crucible.core.helpers.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.CapabilityType;

import java.util.HashMap;

public class CrucibleEdgeWebdriver extends CrucibleWebdriver {
    public CrucibleEdgeWebdriver(DriverName driverName, boolean useProxy) {
        WebDriver driver = null;
        options = getEdgeOptions();

        if (driverName.equals(DriverName.edge_headless)) {
            ((EdgeOptions) options).addArguments("--headless");
            ((EdgeOptions) options).addArguments("--window-size=1600,900");
        }

        try {
            driver = new EdgeDriver((EdgeOptions) options);
        } catch (IllegalStateException ise) {
            try {
                String edgeDriverName = System.getProperty(DriverFactory.EDGE_DRIVER_PROPERTY);

                FileHelper fileHelper = new FileHelper();
                fileHelper.ExtractFile(edgeDriverName, "a+rwx");

                driver = new EdgeDriver((EdgeOptions) options);
            } catch (IllegalStateException ise2) {
                Logger.logError("Failed to instantiate Edge driver!");
                throw ise;
            }
        }

        driver.manage().window().setSize(new Dimension(1600, 900));
        driver.manage().window().maximize();
        setInstance(driver);
    }

    public static EdgeOptions getEdgeOptions() {
        HashMap<String, Object> edgePrefs = new HashMap<String, Object>();

        edgePrefs.put("download.prompt_for_download", false);
        edgePrefs.put("download.default_directory", getDownloadFilePath());

        EdgeOptions options = new EdgeOptions();
        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        options.addArguments("disable-notifications");
        options.addArguments("no-sandbox");
        options.setExperimentalOption("prefs", edgePrefs);

        return options;
    }
}
