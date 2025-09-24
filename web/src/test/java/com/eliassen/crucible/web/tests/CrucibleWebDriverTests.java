package com.eliassen.crucible.web.tests;

import com.eliassen.crucible.web.drivers.CrucibleChromeWebdriver;
import com.eliassen.crucible.web.drivers.DriverName;
import org.junit.Test;

public class CrucibleWebDriverTests {
    private CrucibleChromeWebdriver driver;

    @Test
    public void quit_DoesNotThrowException_IfBrowserIsClosedIncorrectly() {
        driver = new CrucibleChromeWebdriver(DriverName.chrome, false);
        driver.navigate();
        driver.getInstance().close();
        driver.quit();
    }
}