package com.eliassen.crucible.core.sharedobjects;

import java.util.HashMap;
import java.util.Map;

public class Parameters extends HashMap<String,String>
{
    public Parameters(){}

    public Parameters(Map<String,String> table)
    {
        this.putAll(table);
    }
}
