package com.eliassen.crucible.web.drivers.mocks;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A mock implementation of the WebDriver interface for testing purposes.
 * This class simulates a web driver's behavior without interacting with a real browser,
 * returning predetermined values for its methods. It's useful for unit testing web-related code.
 */
public class MockDriver implements WebDriver {

    /**
     * Simulates navigating to a specified URL. This method does not perform any actual navigation.
     *
     * @param url The URL to navigate to.
     */
    @Override
    public void get(String url) {
        // Mock implementation
    }

    /**
     * Returns a mock URL representing the current page's URL.
     *
     * @return A String "urlValue", representing the current URL.
     */
    @Override
    public String getCurrentUrl() {
        return "currentUrlValue";
    }

    /**
     * Returns a mock title for the current page.
     *
     * @return A String "titleValue", representing the page title.
     */
    @Override
    public String getTitle() {
        return "titleValue";
    }

    /**
     * Simulates finding multiple elements on the current page.
     *
     * @param by The locating mechanism to use.
     * @return An empty list, as this is a mock implementation.
     */
    @Override
    public List<WebElement> findElements(By by) {
        List<WebElement> elements = new ArrayList<WebElement>();
        elements.add(new MockWebElement());
        return elements;
    }

    /**
     * Simulates finding a single element on the current page.
     *
     * @param by The locating mechanism to use.
     * @return A new instance of MockWebElement, as this is a mock implementation.
     */
    @Override
    public WebElement findElement(By by) {
        return new MockWebElement();
    }

    /**
     * Returns a mock representation of the page source.
     *
     * @return A String "pageSourceValue", representing the page source.
     */
    @Override
    public String getPageSource() {
        return "pageSourceValue";
    }

    /**
     * Simulates closing the current window. This method does not perform any action.
     */
    @Override
    public void close() {
        // Mock implementation
    }

    /**
     * Simulates quitting the browser. This method does not perform any action.
     */
    @Override
    public void quit() {
        // Mock implementation
    }

    /**
     * Returns a mock set of window handles.
     *
     * @return An empty HashSet, as this is a mock implementation.
     */
    @Override
    public Set<String> getWindowHandles() {
        return new HashSet<>();
    }

    /**
     * Returns a mock window handle for the current window.
     *
     * @return A String "windowHandleValue", representing the window handle.
     */
    @Override
    public String getWindowHandle() {
        return "windowHandleValue";
    }

    /**
     * Simulates switching between different windows or frames. This method does not perform any action and returns null.
     *
     * @return null, as this is a mock implementation.
     */
    @Override
    public TargetLocator switchTo() {
        return null;
    }

    /**
     * Provides access to navigate operations. This method does not perform any action and returns null.
     *
     * @return null, as this is a mock implementation.
     */
    @Override
    public Navigation navigate() {
        return new MockNavigation();
    }

    /**
     * Provides access to modifying options or settings of the browser. This method does not perform any action and returns null.
     *
     * @return null, as this is a mock implementation.
     */
    @Override
    public Options manage() {
        return new MockOptions();
    }
}
