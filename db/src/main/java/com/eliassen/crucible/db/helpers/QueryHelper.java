package com.eliassen.crucible.db.helpers;

import com.eliassen.crucible.db.main.CentralCommand;

public class QueryHelper
{
    public static final String LOG_QUERY_TAG = "@logQuery";

    public static void addToCsvListInCentralCommandStorage(String key, String value)
    {
        String list = CentralCommand.retrieve(key);
        if(list == null)
        {
            list = value;
        }
        else
        {
            list += "," + value;
        }

        CentralCommand.store(key, list);
    }

    public static void addToCsvStringListInCentralCommandStorage(String key, String value)
    {
        String list = CentralCommand.retrieve(key);
        if(list == null)
        {
            list = "'" + value + "'";
        }
        else
        {
            list += "," + "'" + value + "'";
        }

        CentralCommand.store(key, list);
    }
}
