package com.eliassen.crucible.demo.stepDefinitions;

import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import com.eliassen.crucible.demo.pageObjects.DemoPageObjectResolver;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;

public class BasicSteps
{
    @ParameterType("\\S+\\.\\S+")
    public String stringConstant(String constantName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        String constantsPackage = "com.lightwell.testing.demo.helpers.constants.";
        String[] parts = constantName.split("\\.");
        String constantClassName = parts[0];
        String constantFieldName = parts[1];
        Class constantsClass = Class.forName(constantsPackage + constantClassName);
        Field constant = constantsClass.getDeclaredField(constantFieldName);
        Object value = constant.get(constantsClass);
        return value.toString();
    }

    @ParameterType("\"(.*?)\"")
    public String otherStringConstant(String constantName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        String constantsPackage = "com.lightwell.testing.demo.helpers.constants.Constants";
        String constantNameCleaned = constantName.replace(" ","_").toUpperCase();
        Class constantsClass = Class.forName(constantsPackage);
        Field constant = constantsClass.getDeclaredField(constantNameCleaned);
        Object value = constant.get(constantsClass);
        return value.toString();
    }

    @Given("I am using {string} objects")
    public void iAmUsingObjects(String pageObjectName)
    {
        CurrentPage.setPageObject(new DemoPageObjectResolver().getPageObjectByName(pageObjectName));
    }

    @Given("I am using the {string} view")
    public void iAmUsingTheView(String pageObjectName)
    {
        CurrentPage.setPageObject(new DemoPageObjectResolver().getPageObjectByName(pageObjectName));
    }

    @And("I add necessary cookies")
    public void iAddNecessaryCookies() {
        CurrentPage.getDriver().manage().addCookie(new Cookie("__hs_opt_out", "no"));
        CurrentPage.getDriver().manage().addCookie(new Cookie("__hs_initial_opt_in", "true"));
    }

    @Given("I want to convert {string} to display block")
    public void iWantToConvertToDisplayBlock(String elementName)
    {
        String xpath = CurrentPage.getPageObjectItem(elementName);
        String javascript = "document.evaluate(\""+ xpath + "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.style='display:block'";
        CurrentPage.executeJavascript(javascript);
    }

    @Given("I want to convert {string} to active")
    public void iWantToConvertToActive(String elementName)
    {
        String xpath = CurrentPage.getPageObjectItem(elementName);
        String javascript = "document.evaluate(\""+ xpath + "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.cssClass += ' active'";
        CurrentPage.executeJavascript(javascript);
    }

    @And("I click on the {string} that is in {string}")
    public void iClickOnTheThatIsIn(String link, String containingElement)
    {
        WebElement element = CurrentPage.element(containingElement);
        String linkXpath = CurrentPage.getPageObjectItem(link);
        element.findElement(By.xpath("." + linkXpath)).click();
        element.findElement(By.xpath("." + linkXpath)).click();
    }

}
