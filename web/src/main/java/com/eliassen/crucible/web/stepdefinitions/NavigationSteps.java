package com.eliassen.crucible.web.stepdefinitions;

import com.eliassen.crucible.core.helpers.TestHelperBase;
import com.eliassen.crucible.web.helpers.NavHelper;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.util.concurrent.TimeUnit;

public class NavigationSteps
{
    @And("^I click on (?:the )?\"([^\"]*)\"$")
    public void iClickOn(String elementName)
    {
        NavHelper.clickOn(elementName);
    }

    @Given("^I choose \"(.*)\" as my browser$")
    public void iChoose(String browserName)
    {
        CurrentPage.setDevice(browserName);
    }
    
    @Given("^I create the browser chosen at the command line$")
    public void iCreateTheBrowserChosenAtTheCommandLine()
    {
    	NavHelper.createBrowser();
    }

    @Given("^I create the browser chosen at the command line with a proxy$")
    public void iCreatetheBrowserChosenAtTheCommandLineWithAProxy()
    {
        NavHelper.createBrowserWithProxy();
    }

    @Then("^I navigate to the page$")
    public void iNavigateToThePage()
    {
        CurrentPage.goTo();
    }

    /**
     * Max 120 seconds
     * @param seconds
     */
    @Given("I wait \"{double}\" seconds")
    public void iWaitSeconds(double seconds)
    {
        TestHelperBase.wait(seconds);
    }

    /**
     *
     * @param minutes
     */
    @Given("I wait \"{double}\" minutes")
    public void iWaitMinutes(double minutes)
    {
        TestHelperBase.wait(minutes, TimeUnit.MINUTES);
    }

    //TODO
    @Given("^I give the animation a few seconds$")
    public void iGiveTheAnimationAFewSeconds()
    {
    	int seconds = 2;
        TestHelperBase.wait(seconds);
    }

    @Given("^I move the mouse down \"(.*)\" and right \"(.*)\"$")
    public void iMoveTheMouseDownAndRight(int p0, int p1)
    {
        NavHelper.moveMouse(p0, p1);
    }

    @Given("^I move the mouse to the \"(.*)\"$")
    public void iMoveTheMouseToThe(String elementName)
    {
        NavHelper.moveMouseToElement(elementName);
    }

    @Given("I click on the select (?:the )?\"([^\"]*)\" and choose \"(.*)\"$")
    public void iClickOnTheSelectAndChoose(String matSelectName, String optionName)
    {
        try
        {
            NavHelper.clickOn(matSelectName);
        }
        catch(ElementNotInteractableException e)
        {
            NavHelper.scrollElementIntoView(matSelectName);
            NavHelper.clickOn(matSelectName);
        }

    	NavHelper.clickOn(optionName);
    }
    
    @Given("^I set the environment to \"(.*)\"$")
    public void iSetTheEnvironmentTo(String environmentName)
    {
    	CurrentPage.setEnvironment(environmentName);
    }
    
    @Given("^I enter \"(.*)\" into (?:the )?\"([^\"]*)\"$")
    public void iEnterInto(String text, String elementName)
    {
    	CurrentPage.enterText(elementName, text);
    }

    @And("^I scroll until (?:the )?\"([^\"]*)\" is visible$")
    public void scrollUntilIsVisible(String elementName)
    {
        NavHelper.scrollElementIntoView(elementName);
    }

    @And("^I clear (?:the )?\"([^\"]*)\"$")
    public void iClear(String elementName)
    {
        NavHelper.clear(elementName);
    }

    @And("^I hit the tab key$")
    public void iHitTheTabKey()
    {
        Actions actions = new Actions(CurrentPage.getDriver().getInstance());
        actions.sendKeys(Keys.TAB).build().perform();
    }

    @And("^I enter tab$")
    public void iEnterTab()
    {
        Actions action = new Actions(CurrentPage.getDriver().getInstance());
        action.sendKeys(Keys.TAB).build().perform();
    }

    @And("^I enter backspace$")
    public void iEnterBackspace()
    {
        Actions action = new Actions(CurrentPage.getDriver().getInstance());
        action.sendKeys(Keys.BACK_SPACE).build().perform();
    }


    @And("^I scroll until (?:the )?\"([^\"]*)\" is visible then click on it$")
    public void iScrolluUntilIsVisibleThenClickOnIt(String elementName)
    {
        CurrentPage.scrollIntoView(elementName);
        CurrentPage.clickOn(elementName);
    }

    @And("^I wait until (?:the )?\"([^\"]*)\" is visible$")
    public void iWaitUntilIsVisible(String elementIAmYearningToSee)
    {
        NavHelper.waitForElementToBeVisible(CurrentPage.getPageObjectItem(elementIAmYearningToSee));
    }

    @Given("I click on the select \"(.*)\" and choose (?:the )?\"([^\"]*)\" and click (?:the )?\"([^\"]*)\" button$")
    public void iClickOnTheSelectAndChooseAndClickButton(String matSelectName, String optionName, String buttonName)
    {
        try
        {
            NavHelper.clickOn(matSelectName);
        } catch (ElementNotInteractableException var4)
        {
            NavHelper.scrollElementIntoView(matSelectName);
            NavHelper.clickOn(matSelectName);
        }

        NavHelper.clickOn(optionName);
        NavHelper.clickOn(buttonName);
    }

    @And("I go back a page")
    public void iGoBackAPage()
    {
        CurrentPage.getDriver().navigate().back();
    }

    @Given("I click on (?:the )?\"([^\"]*)\" and enter (?:the )?\"([^\"]*)\" then choose (?:the )?\"([^\"]*)\"$")
    public void iClickOnTheAndEnterAndChoose(String searchInput, String text, String optionName)
    {
        try
        {
            NavHelper.clickOn(searchInput);
        } catch (ElementNotInteractableException var4)
        {
            NavHelper.scrollElementIntoView(searchInput);
            NavHelper.clickOn(searchInput);
        }
        CurrentPage.enterText(searchInput, text);
        NavHelper.clickOn(optionName);
    }

    @And("^I hit the tab key in (?:the )?\"([^\"]*)\"$")
    public void iHitTheTabKeyIn(String elementName)
    {
        WebElement element = CurrentPage.element(elementName);
        element.sendKeys(Keys.TAB);
    }

    @And("^I hit the enter key in (?:the )?\"([^\"]*)\"$")
    public void iHitTheEnterKeyIn(String elementName)
    {
        WebElement element = CurrentPage.element(elementName);
        element.sendKeys(Keys.ENTER);
    }

    @And("I wait for (?:the )?\"([^\"]*)\" to be enabled$")
    public void iWaitForToBeEnabled(String elementName)
    {
        WebElement element = CurrentPage.element(elementName);
        NavHelper.waitForElementToBeEnabled(element);
    }
}
