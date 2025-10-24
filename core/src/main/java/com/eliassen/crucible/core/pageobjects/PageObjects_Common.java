package com.eliassen.crucible.core.pageobjects;

/**
 * Abstract base class for common page objects.
 */
public abstract class PageObjects_Common {

    /**
     * Retrieves an array of common objects associated with this page object.
     *
     * @return An array of CommonObject instances, or null if no common objects are defined.
     */
    public CommonObject[] getCommonObjects() {
        return null;
    }

    /**
     * Represents a common object with a key-value pair.
     */
    public class CommonObject {
        /**
         * The key of the common object.
         */
        public String key;

        /**
         * The value of the common object.
         */
        public String value;

        /**
         * Constructs a new CommonObject instance with the specified key and value.
         *
         * @param _key   The key of the common object.
         * @param _value The value of the common object.
         */
        public CommonObject(String _key, String _value) {
            key = _key;
            value = _value;
        }
    }
}
