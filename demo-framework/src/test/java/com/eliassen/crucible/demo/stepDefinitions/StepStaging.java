package com.eliassen.crucible.demo.stepDefinitions;

import com.eliassen.crucible.core.helpers.Logger;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.*;

public class StepStaging {

    public static String getTitleText()
    {
        return CurrentPage.executeJavascript("let titleText = document.querySelector('title'); " +
                "return titleText.innerText;").toString();
    }

    public static void addClass(String className, String id)
    {
        CurrentPage.executeJavascript("let element = document.getElementById('" + id + "');" +
                "element.classList.add('" + className + "');");
    }

    public static void addClassUsingXpath(String className, String xpath)
    {
        StringBuilder javascript = new StringBuilder();
        javascript.append("document.evaluate(\"");
        javascript.append(xpath);
        javascript.append("\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.classList.add('");
        javascript.append(className);
        javascript.append("')");

        CurrentPage.executeJavascript(javascript.toString());
    }

    public static void setStyle(String xpath, String style)
    {
        StringBuilder javascript = new StringBuilder();
        javascript.append("document.evaluate(\"");
        javascript.append(xpath);
        javascript.append("\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.style='");
        javascript.append(style);
        javascript.append("'");

        CurrentPage.executeJavascript(javascript.toString());
    }

    //this is a necessary alternative to isVisible because some sites cause things to not display
    // by changing the width to 0.
    public static boolean isWideEnoughToSee(WebElement element) {
        String rawValue = element.getCssValue("width");
        String justDigits = "";
        for (int i = 0; i < rawValue.length(); i++)
        {
            char c = rawValue.charAt(i);
            int cValue = (int) c;
            if(cValue <= 57 && cValue >=48) {
                justDigits += c;
            }
//            char c = rawValue.charAt(i);
//            if (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6'
//                    || c == '7' || c == '8' || c == '9')
//            {
//                justDigits += c;
//            }

        }
        return Integer.parseInt(justDigits) > 0;
    }

    @Then("^The page title is \"(.*)\"$")
    public void titleIs(String expectedTitle)
    {
        String titleText = getTitleText();
        assertEquals(expectedTitle, titleText);
    }


    @And("I hover over {string}")
    public void iHoverOver(String elementName)
    {
        WebElement element = CurrentPage.element(elementName);
        String elementId = element.getAttribute("id");
        String className = "mega-toggle-on";
        addClass(className, elementId);
    }

    @And("I set the style on {string} to {string}")
    public void iSetTheStyleOnTo(String elementName, String style)
    {
        String xpath = CurrentPage.getPageObjectItem(elementName);
        setStyle(xpath, style);
    }

    @And("I add the {string} class to {string}")
    public void iAddTheClassTo(String className, String elementName)
    {
        String xpath = CurrentPage.getPageObjectItem(elementName);
        addClassUsingXpath(className, xpath);
    }

    @Then("I check that {string} is large enough to see")
    public void iCheckThatIsLargeEnoughToSee(String elementName)
    {
        WebElement element = CurrentPage.element(elementName);
        boolean isLargeEnough = isWideEnoughToSee(element);
        assertTrue(isLargeEnough);
    }

    @Then("I check that {string} is too small to see")
    public void iCheckThatIsTooSmallToSee(String elementName)
    {
        WebElement element = CurrentPage.element(elementName);
        boolean isLargeEnough = isWideEnoughToSee(element);
        assertFalse(isLargeEnough);
    }

    @And("I log \"{stringConstant}\"")
    public void iLog(String value) {
        Logger.log("Constant value: " + value);
    }

    @And("I also want to log {otherStringConstant}")
    public void iAlsoWantToLog(String value) {
        Logger.log("Constant value: " + value);
    }
}


