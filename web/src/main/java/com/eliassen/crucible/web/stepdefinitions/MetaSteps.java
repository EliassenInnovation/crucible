package com.eliassen.crucible.web.stepdefinitions;

import com.eliassen.crucible.core.helpers.ParallelHelper;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import io.cucumber.java.en.And;

public class MetaSteps {
    @And("I take a screenshot")
    public void iTakeAScreenshot()
    {
        CurrentPage.takeScreenShot();
    }

    /**
     * Will store the paths as screenshot_path_1, screenshot_path_2, etc
     */
    @And("I store the screenshot paths from the parallel runs")
    public void iStoreTheScreenshotsPathsFromTheParallelRuns(){
        ParallelHelper.ParallelRunResults parallelRunResults = CurrentPage.retrieveObject(ParallelHelper.PARALLEL_RUN_RESULTS);
        for(int x = 0; x < parallelRunResults.screenShotPaths.size(); x++){
            CurrentPage.store("screenshot_path_" + (x+1), parallelRunResults.screenShotPaths.get(x));
        }
    }
}
