package com.eliassen.crucible.db.helpers;

import com.eliassen.crucible.common.helpers.JsonHelper;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Set;

public class QueryTable extends Hashtable<String, String>
{
    private static QueryTable _queryList;
    public static final String GET_ONE_ROW_TO_TEST_CONNECTION = "get one row to test connection";

    public static QueryTable getQueryList()
    {
        if(_queryList == null)
        {
            _queryList = new QueryTable();
            _queryList = fillQueryList(_queryList);
        }

        return _queryList;
    }

    private static QueryTable fillQueryList(QueryTable queryList)
    {
        String jsonPath = "queries.json";
        try {
            JSONObject queries = JsonHelper.getJSONFileContent(jsonPath);
            Set<String> keys = queries.keySet();

            for (String key : keys) {
                queryList.put(key, (String) queries.get(key));
            }
        }
        catch(NullPointerException n)
        {
            //queries.json not found
        }

        return queryList;
    }

    public static String get(String queryName)
    {
        if(_queryList == null)
        {
            getQueryList();
        }
        return _queryList.getValue(queryName);
    }

    private String getValue(String queryName)
    {
        return super.get(queryName.toLowerCase());
    }

    @Override
    public String put(String key, String value)
        {
            return super.put(key.toLowerCase(), value);
        }

}
