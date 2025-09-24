package com.eliassen.crucible.core.stepdefinitions;

import com.eliassen.crucible.core.helpers.TestHelperBase;
import com.eliassen.crucible.core.sharedobjects.MasterMind;

import io.cucumber.java.en.Given;

public class UserSteps 
{
	@Given("^I am using the \"(.*)\" user$")
	public void GivenIAmUsingThe(String userType)
	{
		MasterMind.store("usertype", userType);

		String usersJSONPath = MasterMind.getPageObjectItem(TestHelperBase.USERS_JSON_PATH);
		String environment = MasterMind.getEnvironment();
	}
}
