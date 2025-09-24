package com.eliassen.crucible.encryption.unitTests;

import com.eliassen.crucible.encryption.EncryptionHelper;
import com.eliassen.crucible.encryption.JSONHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsonTests
{
    @Test
    public void test_ShouldGetDataFromJsonFile()
    {
        String TestDataFilePath = "./secret.json";
        String expectedValue = "value";

        String testValue = JSONHelper.getValueFromJson(EncryptionHelper.KEY_NAME,TestDataFilePath);

        assertEquals(expectedValue, testValue);
    }
}
