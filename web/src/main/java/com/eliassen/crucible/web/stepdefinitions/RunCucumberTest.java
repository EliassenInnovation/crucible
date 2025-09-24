package com.eliassen.crucible.web.stepdefinitions;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty",
                "json:target/cucumber-reports/Cucumber.json",
                "junit:cucumber-reports/cucumber-junit.xml"},
        glue = {
                "com.eliassen.crucible.core.stepdefinitions",
                "com.eliassen.crucible.web.stepdefinitions",
                "com.eliassen.crucible.common.stepdefinitions"
        },
        features = {"src/test/resources/features"})
public class RunCucumberTest extends RunCucumberTestBase {
    private static String[] cucumberOptions = {
            "--plugin", "pretty",
            "--plugin", "json:cucumber-reports/cucumber.json",
            "--plugin", "junit:cucumber-reports/cucumber-junit.xml",
            "--glue", "com.eliassen.crucible.core.stepdefinitions",
            "--glue", "com.eliassen.crucible.web.stepdefinitions",
            "--glue", "com.eliassen.crucible.common.stepdefinitions"};

    public static void main(String[] args) throws IOException {
        mainLogic(args, cucumberOptions);
    }
}
