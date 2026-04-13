package com.eliassen.crucible.web.helpers;

import com.eliassen.crucible.core.helpers.Logger;
import com.eliassen.crucible.core.sharedobjects.MasterMind;
import com.eliassen.crucible.web.drivers.WaitManager;
import com.eliassen.crucible.web.drivers.mocks.MockWebElement;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import com.eliassen.crucible.web.drivers.CrucibleWebdriver;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;


public class NavHelper {
    private NavHelper(){}

    public static void clickOn(String elementName) {
        String elementPath = CurrentPage.getPageObjectItem(elementName);
        try {
            waitForElementToBeVisible(CurrentPage.getDriver().findElement(By.xpath(elementPath)));
            waitForElementToBeClickable(CurrentPage.getDriver().findElement(By.xpath(elementPath)));
            CurrentPage.getDriver().findElement(By.xpath(elementPath)).click();
        } catch (StaleElementReferenceException stere) {
            clickOn(elementPath);
        } catch (Exception e) {
            Logger.log(e.getMessage());
            boolean success = clickTheOldFashionedWay(elementPath, CurrentPage.getDriver().findElement(By.xpath(elementPath)));
            if (!success) {
                throw e;
            }
        }
    }

    public static boolean clickTheOldFashionedWay(String elementPath, WebElement element) {
        if (element.isDisplayed()) {
            String jsString = "xPathResult = document.evaluate(\"" + elementPath + "\", document);" +
                    "if(xPathResult){element = xPathResult.iterateNext();}" +
                    "element.click();";
            CurrentPage.executeJavascript(jsString);
            return true;
        } else {
            return false;
        }
    }

    public static WebElement getElement(String elementPath) {
        return CurrentPage.element(elementPath);
    }

    public static void moveMouse(int down, int right) {
        Actions actions = new Actions(CurrentPage.getDriver().getInstance());
        actions.moveByOffset(right, down).build().perform();
    }

    public static void moveMouseToElement(String elementName) {
        Actions actions = new Actions(CurrentPage.getDriver().getInstance());
        WebElement element = getElement(elementName);
        actions.moveToElement(element).build().perform();
    }

    public static void scrollElementIntoView(String elementName) {
        WebElement element = CurrentPage.element(elementName, false);
        scrollElementIntoView(element);
    }

    public static void scrollElementIntoView(WebElement element) {
        Coordinates coordinate = ((Locatable) element).getCoordinates();
        coordinate.onPage();
        coordinate.inViewPort();
    }

    public static void enterText(String elementName, String text) {
        WebElement element = getElement(elementName);
        CurrentPage.getDriver().enterText(element, text);
        waitForElementToContainText(element, text);
    }

    public static void createBrowser() {
        if (CurrentPage.getDriver() == null ||
                CurrentPage.getDriver().hasQuit() ||
                CurrentPage.getDriver().isClosed() ||
                !CurrentPage.getDriver().driverReusable()) {
            String browserName = System.getProperty("browser");
            String driverName;

            if (CurrentPage.isPersisted("browser_tag")) {
                browserName = CurrentPage.retrievePersisted("browser_tag");
            }

            if (browserName != null && !browserName.isEmpty()) {
                driverName = browserName;
            } else {
                driverName = CrucibleWebdriver.CHROME;
            }

            CurrentPage.setDevice(driverName);
        }
    }

    public static void createBrowserWithProxy() {
        String browserName = System.getProperty("browser");
        String driverName;
        if (browserName != null && !browserName.isEmpty()) {
            driverName = browserName;
        } else {
            driverName = CrucibleWebdriver.CHROME;
        }

        CurrentPage.setDevice(driverName, true);
    }

    public static void waitForElementToBeClickable(WebElement element) {
        CurrentPage.checkProgress();
        WebDriverWait wait = new WebDriverWait(CurrentPage.getDriver(),
                WaitManager.getWaitDuration(WaitManager.CLICKABLE_EXPLICIT_WAIT));
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public static void waitForElementToBeVisible(WebElement element) {
        CurrentPage.checkProgress();
        WebDriverWait wait = new WebDriverWait(CurrentPage.getDriver(),
                WaitManager.getWaitDuration(WaitManager.VISIBLE_EXPLICIT_WAIT));
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public static void waitForElementToContainText(WebElement element, String text) {
        CurrentPage.checkProgress();
        if (!(element instanceof MockWebElement)) {
            WebDriverWait wait = new WebDriverWait(CurrentPage.getDriver(),
                    WaitManager.getWaitDuration(WaitManager.TEXT_EXPLICIT_WAIT, TimeUnit.MILLISECONDS));
            if (element.getTagName().equals("input") || element.getTagName().equals("textarea")) {
                wait.until(ExpectedConditions.textToBePresentInElementValue(element, text));
            } else {
                wait.until((ExpectedConditions.textToBePresentInElement(element, text)));
            }
        }
    }

    //TODO double check
    public static void waitForElementToBeVisible(String xpath) {
        CurrentPage.checkProgress();
        WebDriverWait wait = new WebDriverWait(CurrentPage.getDriver(),
                WaitManager.getWaitDuration(WaitManager.VISIBLE_EXPLICIT_WAIT));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
    }

    /**
     * Waits for isEnabled to be true
     *
     * @param element
     */
    public static void waitForElementToBeEnabled(WebElement element) {
        CurrentPage.checkProgress();
        WebDriverWait wait = new WebDriverWait(CurrentPage.getDriver().getInstance(),
                WaitManager.getWaitDuration(WaitManager.CLICKABLE_EXPLICIT_WAIT));
        wait.until((ExpectedCondition<Boolean>) driver -> element.isEnabled());
    }

    /**
     * Uses WebElement.clear() to clear an element. Includes a wait.
     *
     * @param elementNameOrXpath
     */
    public static void clear(String elementNameOrXpath) {
        WebElement element = CurrentPage.element(elementNameOrXpath);
        NavHelper.waitForElementToBeClickable(element);
        element.clear();
    }
}
