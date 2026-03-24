package com.eliassen.crucible.core.stepdefinitions;

import com.eliassen.crucible.core.helpers.ParallelHelper;
import com.eliassen.crucible.core.sharedobjects.MasterMind;
import io.cucumber.java.en.And;

import java.util.List;

public class MetaSteps {
    /**
     * 
     * @param tagExpression
     * @param environmentsCsv
     */
    @And("I run the(se) {string} tag(s) against the {string} environment(s)")
    public void iRunTheseTagsAgainstTheseEnvironments(String tagExpression, String environmentsCsv){
        String[] environemnts = environmentsCsv.split(",");
        ParallelHelper parallelHelper = new ParallelHelper();

        ParallelHelper.ParallelRunResults parallelRunResults = parallelHelper.runInParallel(environemnts,tagExpression);
        MasterMind.storeObject(ParallelHelper.PARALLEL_RUN_RESULTS,parallelRunResults);
    }
}
