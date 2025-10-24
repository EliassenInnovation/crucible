package com.eliassen.crucible.core.pageobjects;

import java.util.Hashtable;

/**
 * A custom Hashtable implementation that stores thread-specific data in a case-insensitive manner.
 */
public class ThreadObjectTable extends Hashtable<String, Object> {

    /**
     * Adds a new key-value pair to the table, converting the key to lowercase before storing it.
     * If the value is null, it is replaced with an empty string before storing.
     *
     * @param _key   The key to add.
     * @param _value The value associated with the key.
     * @return The previous value associated with the key, or null if there was no previous value.
     */
    @Override
    public Object put(String _key, Object _value) {
        if (_value == null) {
            _value = "";
        }
        return super.put(_key.toLowerCase(), _value);
    }

    /**
     * Checks whether the table contains a non-empty value for the specified key.
     *
     * @param _key The key to check for.
     * @return True if the table contains a non-empty value for the key, false otherwise.
     */
    public boolean has(String _key) {
        boolean has = false;
        if (this.containsKey(_key) && this.get(_key) != null) {
            if (this.get(_key) instanceof String && ((String) this.get(_key)).isEmpty()) {
                has = false;
            } else {
                has = true;
            }
        }

        return has;
    }
}
