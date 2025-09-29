package com.eliassen.crucible.demo.helpers.api;

import com.eliassen.crucible.web.helpers.TestHelper;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;

public class LoginHelper
{
    public static String GetUserUsername(String environment, String userType)
    {
        return GetUserInfo(environment, userType, TestHelper.USERNAME, false);
    }

    public static String GetUserEncryptedPassword(String environment, String userType)
    {
        return GetUserInfo(environment, userType, TestHelper.PASSWORD, true);
    }

    public static String GetUserInfo(String environment, String userType, String infoToRetrieve, boolean encrypted)
    {
        String usersJSONPath = CurrentPage.getPageObjectItem(TestHelper.USERS_JSON_PATH);
        String infoRetrieved = null;
        if(infoToRetrieve.equals(TestHelper.USERNAME))
        {
            infoRetrieved = TestHelper.getUserUsername(environment, userType, usersJSONPath);
        }
        else if (infoToRetrieve.equals(TestHelper.PASSWORD))
        {
            infoRetrieved = TestHelper.getUserPassword(environment, userType, usersJSONPath, encrypted);
        }

        return infoRetrieved;
    }
}
