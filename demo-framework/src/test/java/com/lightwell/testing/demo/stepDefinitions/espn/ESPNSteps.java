package com.lightwell.testing.demo.stepDefinitions.espn;


import com.eliassen.crucible.core.helpers.Logger;
import com.eliassen.crucible.core.pageobjects.PageObjectBase;
import com.eliassen.crucible.web.drivers.CrucibleWebdriver;
import com.eliassen.crucible.web.helpers.TestHelper;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import com.lightwell.testing.demo.pageObjects.DemoPageObjectResolver;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ESPNSteps {

    final static String TEAM_URL = "teamurl";
    final static String COLUMN_NUMBER = "columnnumber";

    @Then("My remembered {string} is not equal to the text from {string}")
    public void myRememberedIsNotEqualToTextFrom(String key, String elementName) {

        TestHelper.wait(2);
        String retrievedText = CurrentPage.retrieve(key);
        Logger.log("Remembered: " + retrievedText);
        WebElement element = CurrentPage.element(elementName);
        String text = element.getText();
        Logger.log("Expected: " + text);
        assertFalse(retrievedText.compareTo(text) == 0);
    }

    @Then("My stored {string} is not equal to the text from {string}")
    public void myStoredIsNotEqualToTextFrom(String key, String elementName) {

        TestHelper.wait(2);
        String retrievedText = CurrentPage.retrievePersisted(key);
        Logger.log("Remembered: " + retrievedText);
        WebElement element = CurrentPage.element(elementName);
        String text = element.getText();
        Logger.log("Expected: " + text);
        assertFalse(retrievedText.compareTo(text) == 0);
    }

    @And("I grab the link for the {string}")
    public void iGrabTheLinkForThe(String cityAndTeamName) {
        CrucibleWebdriver driver = CurrentPage.getDriver();
        MutableCapabilities options = driver.getOptions();
        String teamNameXpathTemplate = "//span[text()='%s']//ancestor::a[@data-teamabbrev]";
        String formattedXpath = String.format(teamNameXpathTemplate, cityAndTeamName);
        WebElement element = CurrentPage.getElementsByXpath(formattedXpath).get(0);
        String href = element.getAttribute("href");
        CurrentPage.store(TEAM_URL, href);
    }

    @And("I navigate to the {string} team page")
    public void iNavigateToTheTeamPage(String cityAndTeamName) {
        //We go to the espn homepage first because the DOM is different on the team pages
        CurrentPage.clickOn("espn logo");
        String teamNameXpathTemplate = "//span[text()='%s']//ancestor::a[@data-teamabbrev]";
        String formattedXpath = String.format(teamNameXpathTemplate, cityAndTeamName);
        WebElement element = CurrentPage.getElementsByXpath(formattedXpath).get(0);
        String href = element.getAttribute("href");
        CurrentPage.getDriver().goTo(href);
    }

    @And("I am using the {string} ESPN view")
    public void iAmUsingTheESPNView(String pageObjectName) throws Exception {
        String[] additionalPaths = {"espn"};
        PageObjectBase pageObject = new DemoPageObjectResolver().getPageObjectByName(pageObjectName, additionalPaths);
        CurrentPage.setPageObject(pageObject);

    }

    @And("I figure out the column number for {string} in the {string} table")
    public void iFigureOutTheColumnNumberFor(String columnTitle, String tableCategory) {
        String xpathTemplateForTR = "//div[contains(text(),'%s')]/..//th[contains(@title,'%s')]/../th";
        String formattedXpath = String.format(xpathTemplateForTR, tableCategory, columnTitle);
        int columnNumber = -1;
        List<WebElement> columnHeaders = CurrentPage.getElementsByXpath(formattedXpath);

        //setting the column number for the particular stat
        for (int x = 0; x < columnHeaders.size(); x++) {
            if (columnHeaders.get(x).getAttribute("title").equals(columnTitle)) {
                columnNumber = x + 1;
            }
        }

        CurrentPage.store(COLUMN_NUMBER, Integer.toString(columnNumber));
    }

    @And("I sum all the {string} in the {string} table")
    public void iSumAllThe(String columnTitle, String tableCategory) {
        String xpathTemplateForTDs = "//div[contains(text(),'%s')]/..//th[contains(@title,'%s')]/../../../tbody/tr/td[%s]";
        String columnNumber = CurrentPage.retrieve(COLUMN_NUMBER);
        String formattedXpath = String.format(xpathTemplateForTDs, tableCategory, columnTitle, columnNumber);
        Logger.log("formatted xpath = " + formattedXpath);

        List<WebElement> statCells = CurrentPage.getElementsByXpath(formattedXpath);
        int totalStats = 0;
        double avgStat = 0;
        int longestYards = 0;
        String formattedAvgStat = "";
        for (int x = 0; x < statCells.size() - 1; x++) {
            if(!columnTitle.equals("Longest Pass") && !columnTitle.equals("Long Rushing")){
                if (statCells.get(x).getText().contains(".")) {
                    avgStat += Double.parseDouble(statCells.get(x).getText());
                } else {
                    totalStats += Integer.parseInt(statCells.get(x).getText().replace(",", ""));
                }
            }else if((columnTitle.equals("Longest Pass") || columnTitle.equals("Long Rushing")) && (x == 0)){
                longestYards = longestPlay(statCells);
            }
        }

        avgStat = avgStat / statCells.size() - 1;
        formattedAvgStat = String.format("%.1f",avgStat);
        Logger.log("statCells.size = " + statCells.size());

        //Adding Career Totals.
        if(columnTitle.equals("Adjusted QBR")) {
            CurrentPage.store(columnTitle + "careertotal", "Cannot Determine Correctness");
        }else{
            CurrentPage.store(columnTitle + "careertotal", statCells.get(statCells.size() - 1).getText().replace(",", ""));
        }

        //Adding the sum of each year totals.
        if (totalStats > 0 && !columnTitle.equals("Adjusted QBR")) {
            CurrentPage.store(columnTitle + "result", Integer.toString(totalStats));
        } else if (avgStat > 0 && !columnTitle.equals("Adjusted QBR")) {
            CurrentPage.store(columnTitle + "result", formattedAvgStat);
        } else if(columnTitle.equals("Adjusted QBR")){
            CurrentPage.store(columnTitle + "result", "Cannot Determine Correctness");
        }

        if(columnTitle.equals("Longest Pass") || columnTitle.equals("Long Rushing")) {
            CurrentPage.store(columnTitle + "result", Integer.toString(longestYards));
        }
    }

    private int longestPlay(List<WebElement> stats){
        int highest = 0;
        for(int i =0; i<stats.size(); i++){
            if (Integer.parseInt(stats.get(i).getText()) > highest) {
                highest = Integer.parseInt(stats.get(i).getText());
            }
        }
        return highest;
    }


    @Then("My remembered {string} total is equal to the career total")
    public void myRememberedTotalIsEqualToTheCareerTotal(String columnTitle) {
        String total = CurrentPage.retrieve(columnTitle.toLowerCase() + "result");
        String careerTotal = CurrentPage.retrieve(columnTitle.toLowerCase() + "careertotal");
        Logger.log("Total: " + total);
        Logger.log("Career Total: " + careerTotal);
        assertEquals(careerTotal, total);
    }
}
