package com.eliassen.crucible.core.pageobjects;

/**
 * Interface for page objects, providing methods for storing and retrieving data,
 * as well as getting the page name and URL.
 */
public interface iPageObject {
    /**
     * Stores a value with the given key.
     * @param key The key to store the value under.
     * @param value The value to store.
     */
    void store(String key, String value);

    /**
     * Retrieves a value associated with the given key.
     * @param key The key to retrieve the value for.
     * @return The stored value.
     */
    String retrieve(String key);

    /**
     * Gets the name of the page.
     * @return The page name.
     */
    abstract String getPageName();

    /**
     * Fills the page table with data.
     */
    void fillPageTable();

    /**
     * Gets the URL of the page for the given environment.
     * @param environmentName The name of the environment.
     * @return The URL of the page.
     */
    String getURL(String environmentName);
}
