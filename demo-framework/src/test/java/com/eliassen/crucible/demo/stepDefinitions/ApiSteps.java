package com.eliassen.crucible.demo.stepDefinitions;

import com.eliassen.crucible.core.sharedobjects.ApiResponse;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import com.eliassen.crucible.demo.helpers.api.ApiName;
import com.eliassen.crucible.demo.helpers.api.DemoApiHelper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.json.JSONArray;
import org.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ApiSteps

{
    @And("I call rest countries Calling Code with parameter {string}")
    @Given("I call rest country Calling Code with parameter {string}")
    public void iCallRestCountriesCallingCodeWithParameter(String parameter)
    {
        Object[] parameters = new Object[]{parameter};
        DemoApiHelper.callApi(ApiName.CallingCode, parameters);
    }

    @And("I do not want to grab headers")
    public void iDoNotWantToGrabHeaders()
    {
        CurrentPage.store("grabHeaders","false");
    }

    @And("I call Avatar Characters API")
    public void iCallAvatarCharactersAPI()
    {
        DemoApiHelper.callApi(ApiName.AvatarCharacters);
    }

    @Then("I check that {string} has more {string} than {string}")
    public void iCheckThatHasMoreThan(String name, String param1, String param2)
    {
        ApiResponse response = DemoApiHelper.callApi(ApiName.AvatarCharacters);
        JSONArray responseArray = new JSONArray(response.payload);
        Integer allyCount = 0;
        Integer enemyCount = 0;

        for(Integer i = 0; i < responseArray.length(); i++)
        {
            JSONObject obj = responseArray.getJSONObject(i);

            String objName = obj.getString("name");

            if(objName.equals(name))
            {
                JSONObject personalInfo = obj.getJSONObject("personalInformation");
                JSONArray allies = (JSONArray)personalInfo.get(param1);
                JSONArray enemies = (JSONArray)personalInfo.get(param2);
                allyCount = allies.length();
                enemyCount = enemies.length();
                return;
            }

            assertTrue(allyCount > enemyCount);
        }

    }

    @And("I call Good Jokes API")
    public void iCallGoodJokesAPI()
    {
        DemoApiHelper.callApi(ApiName.GoodJokes);
    }

    @Then("I check that there are {string} jokes of type {string}")
    public void iCheckThatThereAreJokesOfType(String number, String type)
    {
        Integer numberInt = Integer.parseInt(number);
        ApiResponse response = DemoApiHelper.callApi(ApiName.GoodJokes);
        JSONArray responseArray = new JSONArray(response.payload);
        Integer count = 0;

        for(Integer i = 0; i < responseArray.length(); i++)
        {
            JSONObject obj = responseArray.getJSONObject(i);
            String jokeType = obj.getString("type");

            if(jokeType.equals(type))
            {
                count++;
            }
        }
        assertEquals(numberInt,count);
    }
}
