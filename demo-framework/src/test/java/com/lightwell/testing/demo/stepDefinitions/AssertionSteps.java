package com.lightwell.testing.demo.stepDefinitions;

import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import io.cucumber.java.en.Then;
import org.junit.Assert;

public class AssertionSteps
{
    @Then("I check that I am on the base page")
    public void iCheckThatIAmOnTheBasePage() {
        CurrentPage.checkProgress();
        String expectedPageURL = CurrentPage.getPageURL();
        String actualPage = CurrentPage.actualURL();
        Assert.assertTrue(actualPage.contains(expectedPageURL));
    }
}
