package com.eliassen.crucible.web.stepdefinitions;

import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import io.cucumber.core.cli.Main;
import org.junit.AfterClass;

import java.io.IOException;
import java.util.stream.Stream;

public class RunCucumberTestBase {
    public static void mainLogic(String[] args, String[] cucumberOptions) throws IOException {
        try {
            Stream<String> _cucumberOptions = Stream.concat(Stream.of(cucumberOptions), Stream.of(args));
            byte exitStatus = Main.run(_cucumberOptions.toArray(String[]::new), Thread.currentThread().getContextClassLoader());
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
