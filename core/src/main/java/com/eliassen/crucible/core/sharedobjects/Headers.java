package com.eliassen.crucible.core.sharedobjects;

import java.util.HashMap;
import java.util.Map;

public class Headers extends HashMap<String,String>
{
    public Headers(){}

    public Headers(Map<String,String> table){this.putAll(table);}
}
