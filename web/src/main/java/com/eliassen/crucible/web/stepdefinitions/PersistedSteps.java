package com.eliassen.crucible.web.stepdefinitions;

import com.eliassen.crucible.core.helpers.Logger;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import io.cucumber.java.en.And;

public class PersistedSteps {
    @And("I persist {string} as {string}")
    public void iPersistAs(String value, String key) {
        CurrentPage.storePersisted(key,value);
    }


    @And("I retrieve {string} from pesristed storage")
    public void iRetrieveFromPesristedStorage(String key) {
        String value = CurrentPage.retrievePersisted(key);
        Logger.log("Value was: " + value);
    }
}
