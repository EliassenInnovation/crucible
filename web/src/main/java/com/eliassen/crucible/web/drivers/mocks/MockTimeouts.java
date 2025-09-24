package com.eliassen.crucible.web.drivers.mocks;

import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class MockTimeouts implements WebDriver.Timeouts {
    @Override
    public WebDriver.Timeouts implicitlyWait(long time, TimeUnit unit) {
        return null;
    }

    @Override
    public WebDriver.Timeouts implicitlyWait(Duration duration) {
        return WebDriver.Timeouts.super.implicitlyWait(duration);
    }

    @Override
    public Duration getImplicitWaitTimeout() {
        return WebDriver.Timeouts.super.getImplicitWaitTimeout();
    }

    @Override
    public WebDriver.Timeouts setScriptTimeout(long time, TimeUnit unit) {
        return null;
    }

    @Override
    public WebDriver.Timeouts setScriptTimeout(Duration duration) {
        return WebDriver.Timeouts.super.setScriptTimeout(duration);
    }

    @Override
    public WebDriver.Timeouts scriptTimeout(Duration duration) {
        return WebDriver.Timeouts.super.scriptTimeout(duration);
    }

    @Override
    public Duration getScriptTimeout() {
        return WebDriver.Timeouts.super.getScriptTimeout();
    }

    @Override
    public WebDriver.Timeouts pageLoadTimeout(long time, TimeUnit unit) {
        return null;
    }

    @Override
    public WebDriver.Timeouts pageLoadTimeout(Duration duration) {
        return WebDriver.Timeouts.super.pageLoadTimeout(duration);
    }

    @Override
    public Duration getPageLoadTimeout() {
        return WebDriver.Timeouts.super.getPageLoadTimeout();
    }
}
