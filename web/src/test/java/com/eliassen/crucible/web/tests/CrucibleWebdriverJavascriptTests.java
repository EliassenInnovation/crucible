package com.eliassen.crucible.web.tests;

import com.eliassen.crucible.web.drivers.mocks.MockWebdriver;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;

class CrucibleWebdriverJavascriptTests {

    /**
     * The wrapped MockDriver instance is not a JavascriptExecutor, so the wrapper must degrade
     * gracefully (return null) rather than throwing a ClassCastException. This is what lets the
     * opt-in Angular settle wait be a safe no-op on drivers that cannot run JavaScript.
     */
    @Test
    void executeScript_returnsNull_whenUnderlyingInstanceCannotRunJavascript() {
        MockWebdriver driver = new MockWebdriver();
        assertDoesNotThrow(() -> assertNull(driver.executeScript("return true;")));
        assertDoesNotThrow(() -> assertNull(driver.executeAsyncScript("return true;")));
    }
}
