package com.eliassen.crucible.web.drivers.mocks;

import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A mock implementation of the WebElement interface for testing purposes.
 * Each method is overridden to provide basic, non-operative functionality
 * or return mock values. This class is useful for simulating interactions
 * with web elements without the need for an actual web browser.
 */
public class MockWebElement implements WebElement {

    /**
     * Simulates clicking on a web element. This method does not perform any action.
     */
    @Override
    public void click() {
        // Mock implementation
    }

    /**
     * Simulates submitting a form. This method does not perform any action.
     */
    @Override
    public void submit() {
        // Mock implementation
    }

    /**
     * Simulates sending keys to a web element. This method does not perform any action.
     *
     * @param keysToSend The CharSequence of keys to send to the web element.
     */
    @Override
    public void sendKeys(CharSequence... keysToSend) {
        // Mock implementation
    }

    /**
     * Simulates clearing the text of a web element. This method does not perform any action.
     */
    @Override
    public void clear() {
        // Mock implementation
    }

    /**
     * Returns a mock tag name of the element.
     *
     * @return "tagNameValue".
     */
    @Override
    public String getTagName() {
        return "tagNameValue";
    }

    /**
     * Returns a mock DOM property value.
     *
     * @param name The name of the DOM property.
     * @return "domPropertyValue"y.
     */
    @Override
    public String getDomProperty(String name) {
        return "domPropertyValue";
    }

    /**
     * Returns a mock DOM attribute value.
     *
     * @param name The name of the DOM attribute.
     * @return "domAttributeValue".
     */
    @Override
    public String getDomAttribute(String name) {
        return "domAttributeValue";
    }

    /**
     * Returns a mock attribute value.
     *
     * @param name The name of the attribute.
     * @return "attributeValue".
     */
    @Override
    public String getAttribute(String name) {
        return "attributeValue";
    }

    /**
     * Returns a mock ARIA role of the web element.
     *
     * @return "ariaRoleValue".
     */
    @Override
    public String getAriaRole() {
        return "ariaRoleValue";
    }

    /**
     * Returns a mock accessible name of the web element.
     *
     * @return "accessibleNameValue".
     */
    @Override
    public String getAccessibleName() {
        return "accessibleNameValue";
    }

    /**
     * Simulates the check to see if the element is selected.
     *
     * @return false to indicate that the element is not selected.
     */
    @Override
    public boolean isSelected() {
        return false;
    }

    /**
     * Simulates the check to see if the element is enabled.
     *
     * @return true to indicate that the element is enabled.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Returns a mock text of the web element.
     *
     * @return "textValue".
     */
    @Override
    public String getText() {
        return "textValue";
    }

    /**
     * Simulates finding multiple elements within this element.
     *
     * @param by The locating mechanism.
     * @return An empty list, as this is a mock implementation.
     */
    @Override
    public List<WebElement> findElements(By by) {
        return new ArrayList<>();
    }

    /**
     * Simulates finding a single element within this element.
     *
     * @param by The locating mechanism.
     * @return null, as this is a mock implementation.
     */
    @Override
    public WebElement findElement(By by) {
        return null;
    }

    /**
     * Returns a mock shadow root of the web element.
     *
     * @return null, indicating no shadow root is available in this mock.
     */
    @Override
    public SearchContext getShadowRoot() {
        return null;
    }

    /**
     * Simulates the check to see if the element is displayed.
     *
     * @return false to indicate that the element is not displayed.
     */
    @Override
    public boolean isDisplayed() {
        return true;
    }

    /**
     * Returns a mock location of the web element.
     *
     * @return null, indicating that location is not applicable in this mock.
     */
    @Override
    public Point getLocation() {
        return null;
    }

    /**
     * Returns a mock size of the web element.
     *
     * @return null, indicating that size is not applicable in this mock.
     */
    @Override
    public Dimension getSize() {
        return null;
    }

    /**
     * Returns a mock rectangle representing the location and size of the web element.
     *
     * @return null, indicating that rectangle information is not applicable in this mock.
     */
    @Override
    public Rectangle getRect() {
        return null;
    }

    /**
     * Returns a mock CSS value for a given property name.
     *
     * @param propertyName The CSS property name for which to retrieve the value.
     * @return "cssValueValue".
     */
    @Override
    public String getCssValue(String propertyName) {
        return "cssValueValue";
    }

    /**
     * Simulates taking a screenshot of the element. This method does not perform any action and returns null.
     *
     * @param target The OutputType to define the format of the output.
     * @param <X> The return type defined by the OutputType.
     * @return null, as taking a screenshot is not applicable in this mock.
     * @throws WebDriverException If the screenshot cannot be captured.
     */
    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return null;
    }
}
