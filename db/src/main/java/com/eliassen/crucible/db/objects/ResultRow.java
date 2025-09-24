package com.eliassen.crucible.db.objects;

import oracle.sql.TIMESTAMP;

import java.util.Hashtable;

public class ResultRow extends Hashtable<String, Object>
{
    public int getInt(String key)
    {

        return Integer.parseInt(this.get(key).toString());
    }

    public String getString(String key)
    {
        return this.get(key).toString();
    }

    public TIMESTAMP getOracleTimeStamp(String key)
    {
        TIMESTAMP ts = new TIMESTAMP(this.get(key).toString());
        return ts;
    }
}
