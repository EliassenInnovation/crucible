package com.eliassen.crucible.core.stepdefinitions;

import io.cucumber.core.cli.Main;
import java.util.stream.Stream;

public class RunCucumberTestBase
{
    public static void mainLogic(String[] args,String[] cucumberOptions) {
        Stream<String> _cucumberOptions = Stream.concat(Stream.of(cucumberOptions), Stream.of(args));
        byte exitStatus = Main.run(_cucumberOptions.toArray(String[]::new),Thread.currentThread().getContextClassLoader());
        //TODO - determine if needed
        //quitDriver();
        System.exit(exitStatus);
    }

    //TODO - specific
//    @AfterClass
//    public static void quitDriver()
//    {
//        if( CurrentObjectBase.getDriver() != null)
//        {
//            CurrentObjectBase.getDriver().quit();
//            //CurrentPage.getDriver().close();
//        }
//    }
}
