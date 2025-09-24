package com.eliassen.crucible.web.stepdefinitions;

import com.eliassen.crucible.core.pageobjects.PageObjectResolver;
import com.eliassen.crucible.web.drivers.CrucibleWebdriver;
import com.eliassen.crucible.web.helpers.NavHelper;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.util.Collection;

public class BeforeHooks {
    /**
     * This should be run BEFORE anything else runs
     *
     * @param scenario
     */

    @Before(order = 0)
    public void before(Scenario scenario) {
        String FEATURE_NAME = "featurename";

        String name = CurrentPage.retrievePersisted(FEATURE_NAME);
        String featureNameRaw = scenario.getUri().toString();
        String[] parts = featureNameRaw.split("/");
        String featureName = parts[parts.length - 1].split(":")[0];
        if (name == null) {
            CurrentPage.storePersisted(FEATURE_NAME, featureName);
        } else if (!name.equals(featureName)) {
            CrucibleWebdriver crucibleWebdriver = null;

            if (CurrentPage.getDriver() != null && CurrentPage.getDriver().driverReusable()) {
                crucibleWebdriver = CurrentPage.getDriver();
            }
            CurrentPage.getCurrentThreadObjects().clear();

            CurrentPage.storePersisted(FEATURE_NAME, featureName);

            if (crucibleWebdriver != null) {
                CurrentPage.setDevice(crucibleWebdriver);
            }
        }

        CurrentPage.setScenario(scenario);
    }


    @Before(order = 1)
    public void grabTags() {
        Collection<String> tags = CurrentPage.getScenario().getSourceTagNames();

        for (String tag : tags) {
            String[] tagParts = tag.split("=");
            String key = CurrentPage.curateTagNameForStorage(tagParts[0]);

            String value = "";
            //if the tag has an equal sign in it, we need the value
            if(tagParts.length == 2){
                value = tagParts[1];
            }
            CurrentPage.storePersisted(key, value);
        }
    }

    @Before(order=2)
    public void setPageObject(){
        if (CurrentPage.isPersisted("pageObject_tag")) {
            String pageObjectName = CurrentPage.retrievePersisted("pageObject_tag");
            CurrentPage.setPageObject(new PageObjectResolver().getPageObjectByName(pageObjectName));
        }
    }

    @Before(order = 10001)
    public void launchBrowserAndNavigateToPage() {
        if (CurrentPage.isPersisted("pageObject_tag")) {
            try {
                NavHelper.createBrowser();
                CurrentPage.goTo();
            } catch (NullPointerException n) {
                //we do not care
            }
        }
    }
}
