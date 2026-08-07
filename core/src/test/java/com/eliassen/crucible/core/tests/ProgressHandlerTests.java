package com.eliassen.crucible.core.tests;

import com.eliassen.crucible.core.sharedobjects.MasterMind;
import com.eliassen.crucible.core.sharedobjects.ProgressHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ProgressHandlerTests {

    /** Concrete handler that also exposes the protected seams for assertion. */
    private static class TestProgressHandler extends ProgressHandler {
        boolean angularEnabled() {
            return angularWaitEnabled();
        }

        long maxWaitMillis() {
            return getMaxWaitMillis();
        }
    }

    private TestProgressHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TestProgressHandler();
        // Ensure no driver is registered so the driver-dependent path is not exercised.
        MasterMind.getCurrentThreadObjects().remove(MasterMind.DRIVER);
        System.clearProperty(ProgressHandler.PROGRESS_HANDLER_MAX_WAIT);
    }

    @AfterEach
    void tearDown() {
        System.clearProperty(ProgressHandler.PROGRESS_HANDLER_MAX_WAIT);
    }

    @Test
    void checkProgress_isNoOp_whenNoDriverIsRegistered() {
        // No driver on the thread: settling must return quietly, never throw.
        assertDoesNotThrow(handler::checkProgress);
    }

    @Test
    void angularWait_isDisabledByDefault() {
        assertFalse(handler.angularEnabled());
    }

    @Test
    void getMaxWaitMillis_defaultsTo15Seconds_thenHonoursConfiguredSubSecondValue() {
        // Kept in one test on purpose: SystemHelper caches resolved settings per thread, so the
        // default (nothing configured) must be asserted before a value is ever read for this key.
        assertEquals(15000L, handler.maxWaitMillis());

        System.setProperty(ProgressHandler.PROGRESS_HANDLER_MAX_WAIT, "2.5");
        assertEquals(2500L, handler.maxWaitMillis());
    }
}
