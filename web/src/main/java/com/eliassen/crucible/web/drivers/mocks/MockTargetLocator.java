package com.eliassen.crucible.web.drivers.mocks;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;

public class MockTargetLocator implements WebDriver.TargetLocator {
    @Override
    public WebDriver frame(int index) {
        return new MockDriver();
    }

    @Override
    public WebDriver frame(String nameOrId) {
        return new MockDriver();
    }

    @Override
    public WebDriver frame(WebElement frameElement) {
        return new MockDriver();
    }

    @Override
    public WebDriver parentFrame() {
        return new MockDriver();
    }

    @Override
    public WebDriver window(String nameOrHandle) {
        return new MockDriver();
    }

    @Override
    public WebDriver newWindow(WindowType typeHint) {
        return new MockDriver();
    }

    @Override
    public WebDriver defaultContent() {
        return new MockDriver();
    }

    @Override
    public WebElement activeElement() {
        return new MockWebElement();
    }

    @Override
    public Alert alert() {
        return new MockAlert();
    }
}
