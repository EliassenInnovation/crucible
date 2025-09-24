package com.eliassen.crucible.web.tests;

import com.eliassen.crucible.web.drivers.WaitManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WaitManagerTests {

    @BeforeEach
    void resetWaitManager() {
        // Reset all static variables before each test
        WaitManager.resetAllWaits();
    }

    @Test
    void testDefaultExplicitWaitTimeFallback() {
        assertEquals(15, WaitManager.getDefaultExplicitWaitTime());
    }

    @Test
    void testGlobalExplicitWaitFallbackToDefault() {
        assertEquals(15, WaitManager.getGlobalExplicitWait());
    }


    @Test
    void testImplicitWaitFallbackToDefault() {
        WaitManager.setImplicitWait(-1);
        assertEquals(5.0, WaitManager.getImplicitWait()); // Should default to 5
    }

    @Test
    void testPageLoadTimeoutFallbackToDefault() {
        assertEquals(5, WaitManager.getPageLoadTimeout()); // Should default to 5
    }


    @Test
    void testExplicitWaitsFallbackToGlobal() {

        assertEquals(WaitManager.getGlobalExplicitWait(), WaitManager.getClickableExplicitWait());
    }

    @Test
    void testSetExplicitWaitsWithValidValues() {
        WaitManager.setGlobalExplicitWait(25);
        assertEquals(25, WaitManager.getGlobalExplicitWait());

        WaitManager.setImplicitWait(3);
        assertEquals(3, WaitManager.getImplicitWait());

        WaitManager.setPageLoadTimeout(8);
        assertEquals(8, WaitManager.getPageLoadTimeout());

        WaitManager.setClickableExplicitWait(7);
        assertEquals(7, WaitManager.getClickableExplicitWait());
    }

    @Test
    void testSetExplicitWaitsWithInvalidValues() {
        WaitManager.setGlobalExplicitWait(-5); // Invalid
        assertEquals(15, WaitManager.getGlobalExplicitWait()); // Should revert to default

        WaitManager.setImplicitWait(-10);
        assertEquals(5, WaitManager.getImplicitWait()); // Should revert to implicit default

        WaitManager.setPageLoadTimeout(-2);
        assertEquals(5, WaitManager.getPageLoadTimeout()); // Should revert to implicit default

        WaitManager.setClickableExplicitWait(-1);
        assertEquals(15, WaitManager.getClickableExplicitWait()); // Should revert to global
    }

    @Test
    void testReadyzWaitValues(){
        WaitManager.setReadyzWaitTime(7);
        assertEquals(7,WaitManager.getReadyzWaitTime());
    }

    @Test
    void testFallBackDefaultValueReadyzWait(){
        assertEquals(15, WaitManager.getDefaultReadyzWaitTime());
    }

}

