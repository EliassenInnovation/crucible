package com.eliassen.crucible.core.pageobjects;

import java.util.HashMap;

public class ObjectMap extends HashMap<String, Object>
{
    private static final long serialVersionUID = -5889616561901598599L;

    @Override
    public Object put(String key, Object value){
        return super.put(key.toLowerCase(), value);
    }

    @Override
    public Object get(Object key) {
        return super.get(key.toString().toLowerCase());
    }

    public boolean containsKey(String key) {
        return super.containsKey(key.toLowerCase());
    }
}