package com.eliassen.crucible.db.helpers;

import com.eliassen.crucible.common.helpers.JsonHelper;
import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.common.helpers.UserHelper;
import com.eliassen.crucible.db.main.CentralCommand;
import com.eliassen.crucible.db.main.DBObject;
import com.eliassen.crucible.db.objects.ConnectionString;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

public class DBJsonHelper extends JsonHelper
{
    public static final String CONNECTION_STRINGS = "connection_strings";
    public static final String DATABASES = "databases";
    public static final String DEFAULT_ENVIRONMENT = "default";
    public static final String USERS_JSON_PATH = "users json path";
    public static final String USER_NOT_FOUND = "user not found";
    public static final String ENCRYPTED_PASSWORD = "encryptedPassword";
    public static final String DECRYPTED_PASSWORD = "decryptedPassword";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String USERTYPE = "usertype";

    private static JSONObject getEnvironmentStrings(String environment)
    {
        JSONObject config = getJSONFileContent(DBObject.configFilePath);
        JSONObject connectionStrings = config.getJSONObject(CONNECTION_STRINGS);
        JSONObject environmentStrings = connectionStrings.getJSONObject(environment);

        return environmentStrings;
    }

    public static String getUserUsername(String user, String usersJsonPath)
    {
        return getUserUsername(user, usersJsonPath, SystemHelper.getEnvironment());
    }

    public static String getUserUsername(String user, String usersJsonPath, String userGroup)
    {
        return UserHelper.getUserUsername(userGroup, user, usersJsonPath);
    }

    public static String getUserPassword(String user, String usersJsonPath, boolean encrypted)
    {
        return getUserPassword(user, usersJsonPath, encrypted, SystemHelper.getEnvironment());
    }

    public static String getUserPassword(String user, String usersJsonPath, boolean encrypted, String userGroup)
    {
        return UserHelper.getUserPassword(userGroup,user, usersJsonPath, encrypted);
    }

    public static String getConnectionStringName(String environmentName)
    {
        return getConnectionStringName(environmentName, CentralCommand.DEFAULT);
    }

    public static String getConnectionStringName(String environmentName, String dbName)
    {
        String connectionStringName = environmentName;
        JSONObject environmentStrings = getJSONFileContent(DBObject.configFilePath);
        JSONObject databases = environmentStrings.optJSONObject(DATABASES);
        if(databases != null && !databases.isEmpty()) {
            JSONObject environment = databases.optJSONObject(environmentName);
            if (environment != null && !environment.isEmpty()) {
                connectionStringName = environment.optString(dbName);
            }
        }
        return connectionStringName;
    }

    public static ConnectionString getConnectionString(String environmentName, String dbName)
    {
        String connectionStringName = getConnectionStringName(environmentName,dbName);
        JSONObject environmentStrings = getJSONFileContent(DBObject.configFilePath);
        JSONObject connectionStrings = environmentStrings.optJSONObject(CONNECTION_STRINGS);
        JSONObject connectionString = connectionStrings.optJSONObject(connectionStringName);
        if(connectionString != null && connectionString.isEmpty())
        {
            return null;
        }
        else
        {
            try {
                return new ObjectMapper().readValue(connectionString.toString(),ConnectionString.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Problem parsing the connection string: " + e.getMessage());
            }
        }
    }
}
