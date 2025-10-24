package com.eliassen.crucible.core.pageobjects;

import java.util.HashMap;

/**
 * A custom HashMap implementation that converts keys to lowercase before storing or retrieving values.
 * This ensures that key lookups are case-insensitive.
 */
public class ObjectMap extends HashMap<String, Object> {
    private static final long serialVersionUID = -5889616561901598599L;

    /**
     * Stores a value with the given key, converting the key to lowercase before storage.
     * @param key The key to store the value under.
     * @param value The value to store.
     * @return The previous value associated with the key, or null if none.
     */
    @Override
    public Object put(String key, Object value) {
        return super.put(key.toLowerCase(), value);
    }

    /**
     * Retrieves a value associated with the given key, converting the key to lowercase before lookup.
     * @param key The key to retrieve the value for.
     * @return The stored value, or null if none.
     */
    @Override
    public Object get(Object key) {
        return super.get(key.toString().toLowerCase());
    }

    /**
     * Checks if the map contains a key, converting the key to lowercase before lookup.
     * @param key The key to check for.
     * @return True if the map contains the key, false otherwise.
     */
    public boolean containsKey(String key) {
        return super.containsKey(key.toLowerCase());
    }
}
