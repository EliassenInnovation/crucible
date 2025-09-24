package com.eliassen.crucible.core.stepdefinitions.stephelpers;

import com.eliassen.crucible.core.helpers.TestHelperBase;
import com.eliassen.crucible.core.sharedobjects.MasterMind;
import org.json.JSONArray;
import org.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ApiStepHelper
{
    public static void objectsInRememberedJSONArrayContainKey(String arrayName, String expectedKey)
    {
        JSONArray jArray = new JSONArray(MasterMind.retrieve(arrayName));
        int arrayLength = jArray.length();
        int found = 0;

        for (Object object : jArray)
        {
            JSONObject jObject = new JSONObject(object.toString());
            found += jObject.has(expectedKey) ? 1 : 0;
        }

        assertEquals("Only " + found + " of the " + arrayLength + " objects contained " + expectedKey, arrayLength, found);
    }

    public static void objectInRememberedJSONObjectContainKey(String jsonName, String expectedKey)
    {
        JSONObject json = new JSONObject(MasterMind.retrieve(jsonName));
        boolean foundExpected = false;
        if(json.toMap().containsKey(expectedKey))
        {
            foundExpected = true;
        }
        assertTrue("Could not find expected key:" + expectedKey, foundExpected);
    }

    public static void objectInRememberedJSONObjectContainVBoolean(String jsonName, String expectedValue, String key)
    {
        JSONObject json = new JSONObject(MasterMind.retrieve(jsonName));
        boolean foundExpected = false;
        boolean value;
        if(expectedValue.equals("false"))
        {
            value = false;
            System.out.println("worked");
        }
        else
        {
            value = true;
        }

        if(json.getBoolean(key) == value)
        {
            foundExpected = true;
        }
        assertTrue("Could not find expected key:" + expectedValue, foundExpected);
    }

    public static void PrintResults(String responseString)
    {
        JSONObject settings = new TestHelperBase().getJSONFileContent("settings.json");
        if (settings.getBoolean("logging"))
        {
            try
            {
                JSONArray array = new JSONArray(responseString);
                array.forEach(System.out::println);
            }
            catch(Exception ex)
            {
                System.out.println(ex.getMessage());
            }
        }
    }
}
