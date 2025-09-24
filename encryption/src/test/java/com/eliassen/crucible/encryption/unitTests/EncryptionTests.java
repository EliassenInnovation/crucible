package com.eliassen.crucible.encryption.unitTests;

import com.eliassen.crucible.encryption.EncryptionHelper;
import com.eliassen.crucible.encryption.JSONHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EncryptionTests
{
    String encryptedString = "hAGE6j5tBKoRh8/N33/OQQ==";
    String unencryptedString = "encryptedValue";
    String secretPath = "secret.json";

    @Test
    public void test_ShouldGetCorrectEncryptedString()
    {
        String secretKey = JSONHelper.getValueFromJson(EncryptionHelper.KEY_NAME, secretPath);
        EncryptionHelper.setKey(secretKey);

        String testEncryptedString = EncryptionHelper.encrypt(unencryptedString, secretKey);

        assertEquals(encryptedString, testEncryptedString);
    }

    @Test
    public void test_ShouldGetCorrectDecryptedString()
    {
        String secretKey = JSONHelper.getValueFromJson(EncryptionHelper.KEY_NAME, secretPath);
        EncryptionHelper.setKey(secretKey);

        String testDecryptedString = EncryptionHelper.decrypt(encryptedString, secretKey);

        assertEquals(unencryptedString, testDecryptedString);
    }
}
