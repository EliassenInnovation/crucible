package com.eliassen.crucible.web.sharedobjects;

import io.cucumber.java.Scenario;
import com.eliassen.crucible.core.pageobjects.PageObjectBase;
import com.eliassen.crucible.core.pageobjects.ThreadObjectTable;
import com.eliassen.crucible.core.sharedobjects.MasterMind;
import com.eliassen.crucible.web.drivers.CrucibleWebdriver;
import com.eliassen.crucible.web.drivers.DriverFactory;
import com.eliassen.crucible.web.drivers.DriverName;
import com.eliassen.crucible.web.helpers.NavHelper;
import com.eliassen.crucible.web.helpers.ScreenShotter;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CurrentPage {

    private CurrentPage(){}

    private static ScreenShotter screenShotter;
    public static ScreenShotter getScreenShotter() {
        if(screenShotter == null){
            screenShotter = new ScreenShotter();
        }
        return screenShotter;
    }

    public static String actualURL() {
        return getDriver().getCurrentUrl();
    }

    public static String getPageURL(){
        return MasterMind.getPageURL();
    }

    public static CrucibleWebdriver getDriver() {
        return (CrucibleWebdriver) MasterMind.getCurrentThreadObjects().get(MasterMind.DRIVER);
    }

    public static void killDriver() {
        getDriver().quit();
        MasterMind.getCurrentThreadObjects().remove(MasterMind.DRIVER);
    }

    public static WebElement element(String elementName, boolean checkForVisibility) {
        String xpath = getPageObjectItem(elementName);
        return getElementByXpath(xpath, checkForVisibility);
    }

    public static WebElement element(String elementNameOrXpath) {
        if (!elementNameOrXpath.contains("//")) {
            elementNameOrXpath = getPageObjectItem(elementNameOrXpath);
        }
        return getElementByXpath(elementNameOrXpath);
    }

    public static WebElement getElementByXpath(String xpath) {
        boolean checkForVisibility = true;
        return getElementByXpath(xpath, checkForVisibility);
    }

    public static WebElement getElementByXpath(String xpath, boolean checkForVisibility) {
        MasterMind.checkProgress();
        if (checkForVisibility) {
            NavHelper.waitForElementToBeVisible(xpath);
        }
        return getDriver().findElement(By.xpath(xpath));
    }

    public static List<WebElement> getElementsByXpath(String xpath) {
        MasterMind.checkProgress();
        return getDriver().findElements(By.xpath(xpath));
    }

    public static void goTo() {
        String url = MasterMind.getPageURL();
        getDriver().goTo(url);
    }

    public static void clickOn(String elementNameOrXpath) {
        //if is xpath
        if (elementNameOrXpath.contains("//")) {
            NavHelper.clickOn(CurrentPage.getElementByXpath(elementNameOrXpath), elementNameOrXpath);
        } else {
            NavHelper.clickOn(elementNameOrXpath);
        }
    }

    public static void enterText(String elementName, String text) {
        NavHelper.enterText(elementName, text);
    }

    public static void setDevice(String deviceName) {
        setDevice(deviceName, false);
    }

    public static void setDevice(String deviceName, boolean useProxy) {
        DriverFactory df = new DriverFactory();
        DriverName driverName = DriverName.valueOf(deviceName.toLowerCase());
        MasterMind.getCurrentThreadObjects().put(MasterMind.DRIVER, df.createDriver(driverName, useProxy));
    }

    public static void setDevice(CrucibleWebdriver crucibleWebdriver) {
        MasterMind.getCurrentThreadObjects().put(MasterMind.DRIVER, crucibleWebdriver);
    }

    /**
     * switch to a stored webdriver instance
     * @param driverName
     */
    public static void switchToWebDriver(String driverName){
        MasterMind.getCurrentThreadObjects().put(MasterMind.DRIVER, retrieveObject(driverName));
    }

    public static void storeCurrentWebDriver(String driverName) {
        storeObject(driverName,getDriver());
    }

    public static void scrollIntoView(String elementNameOrXpath) {
        NavHelper.scrollElementIntoView(CurrentPage.element(elementNameOrXpath));
    }

    public static void scrollIntoView(WebElement element) {
        NavHelper.scrollElementIntoView(element);
    }

    public static void clear(String elementNameOrXpath){
        NavHelper.clear(elementNameOrXpath);
    }

    public static Object executeJavascript(String javascript) {
        JavascriptExecutor executor = (JavascriptExecutor) getDriver().getInstance();
        return executor.executeScript(javascript);
    }

    public static String getPageObjectItem(String itemName){
        return MasterMind.getPageObjectItem(itemName);
    }

    public static String retrieve(String key) {
        return MasterMind.retrieve(key);
    }

    public static void store(String key, String value) {
        MasterMind.store(key,value);
    }

    public static Scenario getScenario() {
        return MasterMind.getScenario();
    }

    public static void setScenario(Scenario scenario) {
        MasterMind.setScenario(scenario);
    }

    public static boolean isPersisted(String key) {
        return MasterMind.isPersisted(key);
    }

    public static void storePersisted(String key, String value) {
        MasterMind.storePersisted(key,value);
    }

    public static String retrievePersisted(String key) {
        return MasterMind.retrievePersisted(key);
    }

    public static void setEnvironment(String environmentName) {
        MasterMind.setEnvironment(environmentName);
    }

    public static String getEnvironment(){
        return MasterMind.getEnvironment();
    }

    public static void checkProgress() {
        MasterMind.checkProgress();
    }

    public static ThreadObjectTable getCurrentThreadObjects() {
        return MasterMind.getCurrentThreadObjects();
    }

    public static void setPageObject(PageObjectBase pageObject) {
        MasterMind.setPageObject(pageObject);
    }

    public static PageObjectBase getPageObject(){
        return MasterMind.getPageObject();
    }

    public static <T> T retrieveObject(String key){
        return (T)MasterMind.retrieveObject(key);
    }

    public static void storeObject(String key, Object value){
        MasterMind.storeObject(key,value);
    }

    public static void takeScreenShot(){
        getScreenShotter().safeAttachScreenshot(getScenario());
    }

    /**
     * tagName can be in any of the following formats
     * Casing does not matter
     * - @tagName
     * - tagName
     * - tagName_tag
     * @param tagName
     * @return
     */
    public static boolean hasTag(String tagName){
        String key = curateTagNameForStorage(tagName);
        return isPersisted(key);
    }

    /**
     * turns @tagName or tagName into tagName_tag
     * @param tagName
     * @return
     */
    public static String curateTagNameForStorage(String tagName){
        String curatedTagName = tagName.replace("@", "");
        if(!curatedTagName.endsWith("_tag")){
            curatedTagName += "_tag";
        }
        return curatedTagName;
    }

    /**
     * tagName can be in any of the following formats
     * Casing does not matter
     * - @tagName
     * - tagName
     * - tagName_tag
     * @param tagName
     * @return
     */
    public static String getTagValue(String tagName){
        return retrievePersisted(curateTagNameForStorage(tagName));
    }
}
