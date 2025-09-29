package com.eliassen.crucible.demo.stepDefinitions.demoEnvironment;

import com.eliassen.crucible.demo.helpers.api.ApiName;
import com.eliassen.crucible.demo.helpers.api.DemoApiHelper;
import io.cucumber.java.bs.A;
import io.cucumber.java.en.And;

public class DemoApiSteps
{
    @And("I call the demo build 16 json api")
    public void iCallTheDemoBuildJsonApi()
    {
        DemoApiHelper.callApi(ApiName.DemoJar16);
    }

    @And("I call the Get Worst Offender api")
    public void iCallTheGetWorstOffenderApi() {
        DemoApiHelper.callApi(ApiName.GetWorstOffender);
    }

    @And("I ping the Specific Build Api api")
    @And("I call the Specific Build Api api")
    public void iCallTheSpecificBuildApiApi() { DemoApiHelper.callApi(ApiName.SpecificBuildApi); }

    @And("I call the Demo Web Api api")
    public void iCallTheDemoWebApiApi() {
        DemoApiHelper.callApi(ApiName.DemoWebApi); }

    @And("I call the Get Totals Api")
    public void iCallTheGetTotalsApi() {
        DemoApiHelper.callApi(ApiName.GetTotals);
    }

    @And("I call the get fails api")
    public void iCallTheGetFailsApi()
    {
        DemoApiHelper.callApi(ApiName.GetFails);
    }
}