package com.eliassen.crucible.web.drivers.mocks;

import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class MockTimeouts implements WebDriver.Timeouts {
    @Override
    public WebDriver.Timeouts implicitlyWait(Duration duration) {
        return null;
    }

    @Override
    public Duration getImplicitWaitTimeout() {
        return WebDriver.Timeouts.super.getImplicitWaitTimeout();
    }

    @Override
    public WebDriver.Timeouts scriptTimeout(Duration duration) {
        return null;
    }

    @Override
    public Duration getScriptTimeout() {
        return WebDriver.Timeouts.super.getScriptTimeout();
    }

    @Override
    public WebDriver.Timeouts pageLoadTimeout(Duration duration) {
        return null;
    }

    @Override
    public Duration getPageLoadTimeout() {
        return WebDriver.Timeouts.super.getPageLoadTimeout();
    }
}
