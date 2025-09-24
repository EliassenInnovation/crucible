package com.eliassen.crucible.web.stepdefinitions;

import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import io.cucumber.java.en.Then;

import static org.junit.Assert.assertTrue;

public class NavigationAssertionSteps
{
    @Then("I check that I am on the {string}")
    public void iCheckThatIAmOnThe(String expectedPage)
    {
        CurrentPage.checkProgress();
        String expectedPageURL = CurrentPage.getPageObjectItem(expectedPage.toLowerCase());
        String actualPage = CurrentPage.actualURL();
        assertTrue(actualPage.contains(expectedPageURL));
    }
}
