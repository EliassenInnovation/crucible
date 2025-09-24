package com.eliassen.crucible.web.tests;

import com.eliassen.crucible.web.drivers.CrucibleFirefoxWebdriver;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import static org.junit.Assert.assertTrue;

public class FirefoxDriverTests {
    @Test
    public void canSeeFirefoxProfileOverrides(){
        FirefoxOptions options = new FirefoxOptions();
        CrucibleFirefoxWebdriver.checkForOverrideProfileOptions(options);
        assertTrue(options.asMap().size() > 1);
    }

    @Test
    public void testForAlternateDownloadPath(){
        // Create a Firefox profile
        FirefoxOptions options = new FirefoxOptions();

        // Set preferences for file download
        String downloadDir = "C:\\temp"; // Replace with the path to your download directory
        options.addPreference("browser.download.folderList", 2); // 0 for desktop; 1 for default directory; 2 for custom path
        options.addPreference("browser.download.dir", downloadDir);
        options.addPreference("browser.download.useDownloadDir", true);
        options.addPreference("browser.helperApps.neverAsk.saveToDisk",
                "application/zip"); // MIME type for PDF files, add more as needed

        // Optional: Disable Firefox's built-in PDF viewer
        //options.addPreference("pdfjs.disabled", true);

        // Set the path to the geckodriver executable
        // System.setProperty("webdriver.gecko.driver", "/path/to/geckodriver");

        // Initialize the WebDriver with the configured options
        WebDriver driver = new FirefoxDriver(options);

        driver.get("https://www.dundeecity.gov.uk/sites/default/files/publications/civic_renewal_forms.zip");
        // Your test code here

        // Remember to close the driver
         driver.quit();
    }
}
