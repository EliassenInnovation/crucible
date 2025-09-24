package com.lightwell.testing.demo.stepDefinitions.espn;

import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import com.lightwell.testing.demo.helpers.api.ApiName;
import com.lightwell.testing.demo.helpers.api.DemoApiHelper;
import io.cucumber.java.en.And;

public class ESPNApiSteps
{
    @And("I call ESPN Associated Links API")
    public void iCallESPNAssociatedLinksAPI()
    {
        CurrentPage.store(DemoApiHelper.GRAB_HEADERS,"false");
        DemoApiHelper.callApi(ApiName.ESPNAssociatedLinksApi);
    }
}
