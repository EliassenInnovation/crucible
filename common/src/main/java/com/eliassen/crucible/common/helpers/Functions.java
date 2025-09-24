package com.eliassen.crucible.common.helpers;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import io.cucumber.datatable.DataTable;

public class Functions
{
    public static boolean isNumeric(Object possibleNumber) {
        if (possibleNumber == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(possibleNumber.toString());
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static Hashtable<String, String> convertHashtableToStringStringHashtable(Hashtable<Object, Object> source)
    {
        Hashtable<String,String> newTable = new Hashtable<>();

        for(Object key: source.keySet()){
            newTable.put(key.toString(),source.get(key).toString());
        }

        return newTable;
    }

    public static Hashtable<String,String> convertDataTableToHashtable(DataTable table)
    {
        List<Map<String,String>> values = table.asMaps(String.class, String.class);

        Hashtable<String,String> valueTable = new Hashtable<>();

        for(Map columns : values)
        {
            valueTable.put(columns.get("key").toString(), columns.get("value").toString());
        }

        return valueTable;
    }
}
