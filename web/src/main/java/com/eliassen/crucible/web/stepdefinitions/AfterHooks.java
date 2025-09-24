package com.eliassen.crucible.web.stepdefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.core.helpers.Logger;
import com.eliassen.crucible.web.helpers.DomHelper;
import com.eliassen.crucible.web.helpers.ScreenShotter;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import com.eliassen.crucible.web.drivers.CrucibleWebdriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AfterHooks {
    /**
     * This should be run AFTER everything else
     */
    @After(order = 0)
    public void closeTheBrowser() {
        if (CurrentPage.getDriver() != null && !CurrentPage.getDriver().driverReusable()) {
            CurrentPage.killDriver();
        }

        //we need to also kill any other drivers created
        for(Map.Entry<String,Object> objectEntry : CurrentPage.getPageObject().getObjectMap().entrySet()){
            if(objectEntry.getValue() instanceof CrucibleWebdriver){
                try {
                    ((CrucibleWebdriver) objectEntry.getValue()).quit();
                } catch (WebDriverException w){/* we don't care */}
            }
        }
    }

    /**
     * This should run BEFORE all the other after steps
     *
     * @param scenario
     */
    @After(order = 99999)
    public void grabRCAInfo(Scenario scenario) {
        Collection<String> tags = CurrentPage.getScenario().getSourceTagNames();

        if(tags.contains("@grabConsoleLogs") || (SystemHelper.getConfigSetting("GRAB_CONSOLE_LOGS") != null
                && SystemHelper.getConfigSettingBoolean("GRAB_CONSOLE_LOGS"))){
            LogEntries logEntries = CurrentPage.getDriver().manage().logs().get(LogType.BROWSER);
            List<LogEntry> logs = logEntries.getAll();

            StringBuilder logBuilder = new StringBuilder();

            for (LogEntry log : logs) {
                logBuilder.append(log.getMessage());
                logBuilder.append("\n");
            }
            Logger.log(logBuilder.toString());
        }

        if (scenario.isFailed() && CurrentPage.getDriver() != null) {
            new ScreenShotter().safeAttachScreenshot(scenario);

            //If GRAB_DOM exists and is false or the @noDom tag is present, skip
            if ((SystemHelper.getConfigSetting("GRAB_DOM") != null
                    && !SystemHelper.getConfigSettingBoolean("GRAB_DOM"))) {
                Logger.log("Skipped DOM output: ENV GRAB_DOM set to false");
            } else if (tags.contains("@noDom")) {
                Logger.log("Skipped DOM output: @noDom tag present");
            } else {
                DomHelper.grabDom();
            }
        }
    }
}
