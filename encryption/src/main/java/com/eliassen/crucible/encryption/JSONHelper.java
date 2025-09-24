package com.eliassen.crucible.encryption;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class JSONHelper {
    public static final String ENCRYPTED_PASSWORD = "encryptedPassword";
    public static final String DECRYPTED_PASSWORD = "decryptedPassword";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String USERTYPE = "usertype";
    public static final String DEFAULT_ENVIRONMENT = "default";
    public static final String USERS_JSON_PATH = "users json path";
    public static final String USER_NOT_FOUND = "user not found";

    public JSONObject getJSONFileContent(String jsonPath) {
        String jsonContent = null;
        JSONObject JSON;
        try(FileInputStream fish = new FileInputStream(jsonPath)) {
            jsonContent = new String(fish.readAllBytes());
        } catch (FileNotFoundException f) {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(jsonPath);
            try {
                jsonContent = new String(in.readAllBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JSON = new JSONObject(jsonContent);

        return JSON;
    }

    public static String getValueFromJson(String key, String jsonPath) {
        JSONObject jsonFile = new JSONHelper().getJSONFileContent(jsonPath);
        String value = jsonFile.getString(key);

        return value;
    }
}
