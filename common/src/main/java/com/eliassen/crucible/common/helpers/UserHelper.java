package com.eliassen.crucible.common.helpers;

import com.eliassen.crucible.common.helpers.models.UserInfoRequest;
import org.json.JSONObject;
import com.eliassen.crucible.encryption.EncryptionHelper;

public class UserHelper
{
    public static final String ENCRYPTED_PASSWORD = "encryptedPassword";
    public static final String DECRYPTED_PASSWORD = "decryptedPassword";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String USERTYPE = "usertype";
    public static final String DEFAULT_ENVIRONMENT = "default";
    public static final String USERS_JSON_PATH = "users json path";
    public static final String USER_NOT_FOUND = "user not found";
    public static final String NULL = "null";

    public static boolean usersJSONHasEnvironment(String usersJSONPath, String environmentName)
    {
        JSONObject users = JsonHelper.getJSONFileContent(usersJSONPath);

        boolean hasEnvironment = users.has(environmentName);

        return hasEnvironment;
    }

    public static String getUserUsername(String user, String usersJSONPath)
    {
        return getUserUsername(DEFAULT_ENVIRONMENT, user, usersJSONPath);
    }

    public static String getUserUsername(String environmentName, String user, String usersJSONPath)
    {
        UserInfoRequest infoRequest = new UserInfoRequest();
        infoRequest.EnvirontmentName = environmentName;
        infoRequest.UserType = user;
        infoRequest.UsersJsonPath = usersJSONPath;
        infoRequest.DesiredInfo = USERNAME;

        String username = getUserInfo(infoRequest);

        return username;
    }

    public static String getUserPassword(String user, String usersJSONPath, boolean encrypted)
    {
        return getUserPassword(DEFAULT_ENVIRONMENT, user, usersJSONPath, encrypted);
    }

    public static String getUserPassword(String environmentName, String user, String usersJSONPath, boolean encrypted)
    {
        UserInfoRequest infoRequest = new UserInfoRequest();
        infoRequest.EnvirontmentName = environmentName;
        infoRequest.UserType = user;
        infoRequest.UsersJsonPath = usersJSONPath;
        infoRequest.DesiredInfo = encrypted?ENCRYPTED_PASSWORD:PASSWORD;

        String password = getUserInfo(infoRequest);

        if(encrypted && !password.equals(USER_NOT_FOUND))
        {
            password = EncryptionHelper.decryptString(password);
        }

        return password;
    }

    private static String getUserInfo(UserInfoRequest infoRequest)
    {
        JSONObject users = JsonHelper.getJSONFileContent(infoRequest.UsersJsonPath);
        String environmentName = infoRequest.EnvirontmentName;

        if(!usersJSONHasEnvironment(infoRequest.UsersJsonPath,infoRequest.EnvirontmentName))
        {
            environmentName = DEFAULT_ENVIRONMENT;
        }
        else if(!users.getJSONObject(environmentName).has(infoRequest.UserType))
        {
            environmentName = DEFAULT_ENVIRONMENT;
        }

        String desiredInfo = "";
        if(users.getJSONObject(environmentName).getJSONObject(infoRequest.UserType).has(infoRequest.DesiredInfo))
        {
            desiredInfo = users.getJSONObject(environmentName).getJSONObject(infoRequest.UserType).getString(infoRequest.DesiredInfo);
        }
        else {
            desiredInfo = USER_NOT_FOUND;
        }

        return desiredInfo;
    }
}
