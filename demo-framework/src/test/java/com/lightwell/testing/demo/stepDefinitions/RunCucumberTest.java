package com.lightwell.testing.demo.stepDefinitions;

import com.eliassen.crucible.web.stepdefinitions.RunCucumberTestBase;
import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

import java.io.IOException;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty",
                "json:target/cucumber-reports/Cucumber.json",
                "junit:cucumber-reports/cucumber-junit.xml"},
        glue = {
                "com.eliassen.crucible.core.stepdefinitions",
                "com.eliassen.crucible.web.stepdefinitions",
                "com.lightwell.testing.demo.stepDefinitions",
                "com.Lightwell.dbtesting.common.stepDefinitions",
                "com.Lightwell.common.stepdefinitions"
        },
        features = {"src/test/resources/com/lightwell/testing/demo/features"})
public class RunCucumberTest extends RunCucumberTestBase
{
    private static String[] cucumberOptions = {
            "--plugin", "pretty",
            "--plugin", "json:cucumber-reports/cucumber.json",
            "--plugin", "junit:cucumber-reports/cucumber-junit.xml",
            "--glue" , "com.eliassen.crucible.core.stepdefinitions",
            "--glue" , "com.eliassen.crucible.web.stepdefinitions",
            "--glue" , "com.lightwell.testing.demo.stepDefinitions",
            "--glue" , "com.Lightwell.dbtesting.common.stepDefinitions",
            "--glue" , "com.Lightwell.common.stepdefinitions"};

    public static void main(String[] args) throws IOException {
        mainLogic(args,cucumberOptions);
    }
}
