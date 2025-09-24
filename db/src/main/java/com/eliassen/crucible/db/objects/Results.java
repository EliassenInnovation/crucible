package com.eliassen.crucible.db.objects;

import com.eliassen.crucible.db.helpers.ListHelper;

import java.util.ArrayList;
import java.util.Hashtable;

public class Results extends ArrayList<ResultRow>
{
    public ResultRow row(int index)
    {
        return this.get(index);
    }

    /**
     * @param keyColumn
     * The column you want to use as the key of the collection
     * @return
     */
    public Hashtable<String,ArrayList<ResultRow>> getKeyedCollection(String keyColumn)
    {
        Hashtable<String,ArrayList<ResultRow>> keyedCollection = new Hashtable<>();

        for(ResultRow row : this)
        {
            String key = row.getString(keyColumn);
            if(!keyedCollection.containsKey(key))
            {
                keyedCollection.put(key,new ArrayList<ResultRow>());
            }
            keyedCollection.get(key).add(row);
        }

        return keyedCollection;
    }

    public ArrayList<Object> getColumnOfValues(String columnName)
    {
        return ListHelper.getColumnOfValuesFromResults(this, columnName);
    }

    public String getColumnOfValuesAsCSV(String columnName)
    {
        ArrayList<Object> columnValues = getColumnOfValues(columnName);
        String csv = ListHelper.getListOfValuesAsSingleCSVValue(columnValues);
        return csv;
    }
}
