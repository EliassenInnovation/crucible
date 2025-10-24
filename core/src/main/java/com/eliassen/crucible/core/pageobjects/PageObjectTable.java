package com.eliassen.crucible.core.pageobjects;

import java.util.HashMap;

/**
 * A custom HashMap implementation that stores page object data in a case-insensitive manner.
 */
public class PageObjectTable extends HashMap<String, String> {

    private static final long serialVersionUID = -5889616561901598599L;

    /**
     * Adds a new key-value pair to the map, converting the key to lowercase before storing it.
     *
     * @param key   The key to add.
     * @param value The value associated with the key.
     * @return The previous value associated with the key, or null if there was no previous value.
     */
    @Override
    public String put(String key, String value) {
        return super.put(key.toLowerCase(), value);
    }

    /**
     * Retrieves the value associated with the specified key, converting the key to lowercase before looking it up.
     *
     * @param key The key to look up.
     * @return The value associated with the key, or null if no such key exists.
     */
    @Override
    public String get(Object key) {
        return super.get(key.toString().toLowerCase());
    }

    /**
     * Checks whether the map contains the specified key, converting the key to lowercase before checking.
     *
     * @param key The key to check for.
     * @return True if the map contains the key, false otherwise.
     */
    public boolean containsKey(String key) {
        return super.containsKey(key.toLowerCase());
    }
}
