package com.eliassen.crucible.frameworkbrowser.helper;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class DataHelper
{
    public static List<String> convertJsonArrayToStringList(JSONArray jsonArray)
    {
        List<String> list = new ArrayList<>();
        for(int x = 0; x < jsonArray.length(); x++)
        {
            list.add(jsonArray.getString(x));
        }
        return list;
    }

    public static String[] convertJsonArrayToStringArray(JSONArray jsonArray)
    {
        return convertJsonArrayToStringList(jsonArray).toArray(new String[jsonArray.length()]);
    }
}
