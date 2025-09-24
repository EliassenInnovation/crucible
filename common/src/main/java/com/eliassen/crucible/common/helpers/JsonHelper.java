package com.eliassen.crucible.common.helpers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class JsonHelper
{
    public static void writeJsonToDisk(JSONObject jsonObject, String filePath) throws IOException
    {
        FileHelper.writeTextToDisk(jsonObject.toString(4), filePath);
    }

    public static String getPrettyJson(JSONObject object)
    {
        return object.toString(4);
    }

    public static String getPrettyJson(JSONArray array)
    {
        return array.toString(4);
    }

    public static JSONObject getJSONFileContent(String jsonPath)
    {
        JSONObject JSON;

        JSON = new JSONObject(new FileHelper().getTextFileContent(jsonPath));

        return JSON;
    }
}
