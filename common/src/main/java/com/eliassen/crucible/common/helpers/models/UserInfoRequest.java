package com.eliassen.crucible.common.helpers.models;

public class UserInfoRequest
{
    /**
     * path to the users.json file
     */
    public String UsersJsonPath;
    /**
     * Environment name
     */
    public String EnvirontmentName;
    /**
     * Common name for the user
     */
    public String UserType;
    /**
     * username or password
     */
    public String DesiredInfo;
}
