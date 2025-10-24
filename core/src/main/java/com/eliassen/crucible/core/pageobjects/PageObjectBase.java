package com.eliassen.crucible.core.pageobjects;

import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.core.sharedobjects.ProgressHandler;
import org.json.JSONObject;

/**
 * Abstract base class for page objects, providing common functionality and methods.
 */
public abstract class PageObjectBase implements iPageObject {
    /**
     * Progress handler instance.
     */
    private ProgressHandler progressHandler;

    /**
     * Page object resolver instance.
     */
    protected PageObjectResolver resolver;

    /**
     * Constant for URL string.
     */
    public static final String URL_STRING = "url";

    /**
     * URL part for the page.
     */
    public String pageUrlPart = null;

    /**
     * Page table instance.
     */
    private PageObjectTable pageTable;

    /**
     * Object map instance.
     */
    private ObjectMap objectMap;

    /**
     * Page name.
     */
    protected String pageName;

    /**
     * Gets the page table, initializing it if necessary.
     * @return The page table.
     */
    protected PageObjectTable getPageTable() {
        if (pageTable == null) {
            pageTable = new PageObjectTable();
            fillPageTable();
        }
        return pageTable;
    }

    /**
     * Gets the object map, initializing it if necessary.
     * @return The object map.
     */
    public ObjectMap getObjectMap() {
        if (objectMap == null) {
            objectMap = new ObjectMap();
            fillPageTable();
        }
        return objectMap;
    }

    /**
     * Sets the progress handler.
     * @param _progressHandler The progress handler to set.
     */
    public void setProgressHandler(ProgressHandler _progressHandler) {
        progressHandler = _progressHandler;
    }

    /**
     * Gets the progress handler.
     * @return The progress handler.
     */
    public ProgressHandler getProgressHandler() {
        return progressHandler;
    }

    /**
     * Stores a value with the given key.
     * @param key The key to store the value under.
     * @param value The value to store.
     */
    public void store(String key, String value) {
        getPageTable().put(key, value);
    }

    /**
     * Retrieves a value associated with the given key.
     * @param key The key to retrieve the value for.
     * @return The stored value.
     */
    public String retrieve(String key) {
        return getPageTable().get(key);
    }

    /**
     * Stores an object with the given key.
     * @param key The key to store the object under.
     * @param value The object to store.
     */
    public void storeObject(String key, Object value) {
        getObjectMap().put(key, value);
    }

    /**
     * Retrieves an object associated with the given key.
     * @param key The key to retrieve the object for.
     * @return The stored object.
     */
    public <T> T retrieveObject(String key) {
        return (T) getObjectMap().get(key);
    }

    /**
     * Fills the page table with data.
     */
    public abstract void fillPageTable();

    /**
     * Gets the name of the page.
     * @return The page name.
     */
    public String getPageName() {
        return pageName;
    }

    /**
     * Sets the name of the page.
     * @param value The page name to set.
     */
    protected void setPageName(String value) {
        pageName = value;
    }

    /**
     * Gets the URL of the page for the given environment.
     * @param environmentName The name of the environment.
     * @return The URL of the page.
     */
    public String getURL(String environmentName) {
        String environmentURLName = URL_STRING + "_" + environmentName;
        String url = getPageTable().get(environmentURLName);
        if (pageUrlPart != null) {
            url += pageUrlPart;
        }
        return url;
    }

    //TODO need a fix
    //    public String getBaseUrl() throws URISyntaxException {
    //        String currentUrl = CurrentObjectBase.actualURL();
    //        URI uri = new URI(currentUrl);
    //        String baseUrl = uri.getAuthority();
    //
    //        return baseUrl;
    //    }

    /**
     * Adds common objects to the page table.
     * @param common The common objects to add.
     */
    protected void addCommonObjects(PageObjects_Common common) {
        PageObjects_Common.CommonObject[] commonObjects = common.getCommonObjects();
        for (PageObjects_Common.CommonObject commonObject : commonObjects) {
            store(commonObject.key, commonObject.value);
        }
    }

    /**
     * Adds sub-views to the page table.
     * @param pages The names of the sub-views to add.
     */
    protected void addSubViews(String[] pages) {
        for (String page : pages) {
            PageObjectBase base = (PageObjectBase) resolver.getPageObjectByName(page);
            getPageTable().putAll(base.getPageTable());
        }
    }

    /**
     * Adds URLs from the configuration to the page table.
     */
    protected void addUrls() {
        JSONObject urls = new JSONObject(SystemHelper.getConfigSetting("urls"));
        if (urls != null) {
            for (String urlName : urls.keySet()) {
                store(urlName, urls.getString(urlName));
            }
        }
    }
}
