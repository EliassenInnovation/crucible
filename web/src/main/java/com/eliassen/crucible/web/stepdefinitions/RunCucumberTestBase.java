package com.eliassen.crucible.web.stepdefinitions;

import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.core.helpers.ParallelHelper;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import io.cucumber.core.cli.Main;
import org.junit.AfterClass;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class RunCucumberTestBase {

    public static final String ENVIRONMENTS = "environments";

    public static void mainLogic(String[] args, String[] cucumberOptions) throws IOException {
        try {
            List<String> cucumberOptionsList = Stream.concat(Stream.of(cucumberOptions), Stream.of(args)).toList();
            byte exitStatus;
            String cucumberOptionsExpression = String.join(",",cucumberOptionsList);
            System.setProperty("cucumber.expression",cucumberOptionsExpression);

            boolean isParallelTest = SystemHelper.getCommandLineParameter(ENVIRONMENTS) != null;

            if(isParallelTest){
                ParallelHelper parallelHelper = new ParallelHelper();
                String[] environments =SystemHelper.getCommandLineParameter(ENVIRONMENTS).split(",");
                ParallelHelper.ParallelRunResults parallelRunResults = parallelHelper.runInParallel(environments);

                exitStatus = parallelRunResults.exitCodes.stream()
                        .filter(status -> status != 0)
                        .findFirst()
                        .orElse((byte) 0);
            } else {
                exitStatus = Main.run(cucumberOptionsList.toArray(String[]::new), Thread.currentThread().getContextClassLoader());
            }

            System.exit(exitStatus);
        } finally {
            if (CurrentPage.getDriver() != null) {
                quitDriver();
            }
        }
    }

    @AfterClass
    public static void quitDriver() {
        if (CurrentPage.getDriver() != null) {
            CurrentPage.killDriver();
        }
    }
}
