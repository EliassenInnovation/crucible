package com.eliassen.crucible.db.main;

import com.eliassen.crucible.db.helpers.QueryTable;
import com.eliassen.crucible.db.objects.Results;
import io.cucumber.java.Scenario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CentralCommand
{
    public static final String DB_ACCESS_TOKEN = "dbaccesstoken";
    public static final String DEFAULT = "default";

    private static HashMap<String,DBObject> dbObjects;
    private static Results results;
    private static ArrayList<Results> resultSets;
    private static Scenario scenario;
    public static boolean keepConnectionOpen = false;

    public static Map<String, DBObject> getDbObjects()
    {
        if(dbObjects == null)
        {
            dbObjects = new HashMap<>();
        }
        return dbObjects;
    }

    public static DBObject getDefaultDb()
    {
        Optional<DBObject> maybeObject = dbObjects.values().stream().findFirst();
        if(maybeObject.isEmpty())
        {
            throw new RuntimeException("Default db object is null!");
        }
        else
        {
            return maybeObject.get();
        }
    }

    public static void setDBObject(DBObject object)
    {
        setDBObject(DEFAULT,object);
    }

    public static void setDBObject(String name, DBObject object)
    {
        setDBObject(name,object,false);
    }

    public static void setDBObject(DBObject object, boolean keepConnectionOpen)
    {
        setDBObject(DEFAULT,object,keepConnectionOpen);
    }

    public static void setDBObject(String name, DBObject object, boolean keepConnectionOpen)
    {
        getDbObjects().put(name,object);
        CentralCommand.keepConnectionOpen = keepConnectionOpen;
    }

    public static DBObject db()
    {
        return getDbObjects().get(DEFAULT);
    }

    public static DBObject db(String databaseName)
    {
        return getDbObjects().get(databaseName);
    }

    public static void setResults(Results _results)
    {
        resultSets = new ArrayList<>();
        resultSets.add(_results);
    }

    public static void setResults(ArrayList<Results> _resultSets)
    {
        resultSets = _resultSets;
    }

    public static Results getResults()
    {
        if(resultSets.size() > 0)
        {
            return resultSets.get(0);
        }
        else
        {
            return new Results();
        }
    }

    public static void addResults(Results results)
    {
        getResultSets().add(results);
    }

    public static void addResults(ArrayList<Results> _resultSets)
    {
        getResultSets().addAll(_resultSets);
    }

    public static Results getResults(int index)
    {
        return resultSets.get(index);
    }

    public static ArrayList<Results> getResultSets()
    {
        if(resultSets == null)
        {
            resultSets = new ArrayList<>();
        }
        return resultSets;
    }

    public static void store(String key, String value)
    {
        getDefaultDb().store(key, value);
    }

    public static String retrieve(String key)
    {
        return getDefaultDb().retrieve(key);
    }

    public static void setScenario(Scenario _scenario)
    {
        scenario = _scenario;
    }

    public static Scenario getScenario()
    {
        return scenario;
    }

    public static boolean hasDbAccessToken() {
        return retrieve(DB_ACCESS_TOKEN) != null;
    }

    public static String getDbAccessToken() {
        return retrieve(DB_ACCESS_TOKEN);
    }

    public static void setDbAccessToken(String dbAccessToken){
        store(DB_ACCESS_TOKEN, dbAccessToken);
    }

    public static void addQuery(String key, String query)
    {
        QueryTable.getQueryList().put(key,query);
    }
}
