package com.eliassen.crucible.demo.stepDefinitions;

import com.eliassen.crucible.web.stepdefinitions.RunCucumberTestBase;
import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

import java.io.IOException;

public class RunCucumberTest extends RunCucumberTestBase
{
    private static String[] cucumberOptions = {
            "--plugin", "pretty",
            "--plugin", "json:cucumber-reports/cucumber.json",
            "--plugin", "junit:cucumber-reports/cucumber-junit.xml",
            "--plugin", "html:cucumber-rpoerts/cucumber.html",
            "--glue" , "com.eliassen.crucible.core.stepdefinitions",
            "--glue" , "com.eliassen.crucible.web.stepdefinitions",
            "--glue" , "com.lightwell.testing.demo.stepDefinitions",
            "--glue" , "com.Lightwell.dbtesting.common.stepDefinitions",
            "--glue" , "com.Lightwell.common.stepdefinitions"};

    public static void main(String[] args) throws IOException {
        mainLogic(args,cucumberOptions);
    }
}
