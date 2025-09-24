package com.eliassen.crucible.core.sharedobjects;

import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.core.pageobjects.PageObjectBase;
import com.eliassen.crucible.core.pageobjects.PageObjectTable;
import com.eliassen.crucible.core.pageobjects.ThreadObjectTable;
import io.cucumber.java.Scenario;

public abstract class MasterMind {
    public static final String NONE = "none";
    public static final String LOCAL = "local";
    public static final String PARAMETER = "parameter";
    public static final String URL_PARAMETER = "urlparameter";

    public static final String PAGE_OBJECT = "pageobject";
    public static final String PERSISTED_STORAGE = "persistedstorage";
    public static final String PROGRESS_HANDLER = "progresshandler";
    public static final String PROXY = "proxy";
    public static final String DRIVER = "driver";
    public static final String SCENARIO = "scenario";

    private static ThreadObjectTable _threadObjects;
    private static ProgressHandler progressHandler = null;

    public static String getThreadName() {
        return Thread.currentThread().getName();
    }

    public static ThreadObjectTable getCurrentThreadObjects() {
        String threadName = getThreadName().toLowerCase();

        if (_threadObjects == null) {
            _threadObjects = new ThreadObjectTable();
        }

        if (!_threadObjects.has(threadName)) {
            ThreadObjectTable currentThreadTable = new ThreadObjectTable();
            _threadObjects.put(threadName, currentThreadTable);
        }

        return (ThreadObjectTable) _threadObjects.get(threadName);
    }

    public static void setPageObject(PageObjectBase _pageObject) {
        getCurrentThreadObjects().put(PAGE_OBJECT, _pageObject);
        setProgressHandler(_pageObject.getProgressHandler());
    }

    public static PageObjectBase getPageObject() {
        return (PageObjectBase) getCurrentThreadObjects().get(PAGE_OBJECT);
    }

    public static void setScenario(Scenario scenario) {
        getCurrentThreadObjects().put(SCENARIO, scenario);
    }

    public static Scenario getScenario() {
        try {
            if (getCurrentThreadObjects().get(SCENARIO) != null) {
                return (Scenario) getCurrentThreadObjects().get(SCENARIO);
            } else {
                return null;
            }
        } catch (NullPointerException n) {
            System.out.println("Thread objects null: " + getCurrentThreadObjects() == null);
            return null;
        }
    }

    private static void setProgressHandler(ProgressHandler _progressHandler) {
        getCurrentThreadObjects().put(PROGRESS_HANDLER, _progressHandler);
    }

    public static void checkProgress() {
        ((ProgressHandler) getCurrentThreadObjects().get(PROGRESS_HANDLER)).checkProgress();
    }

    public static String getPageURL() {
        String environmentName = getEnvironment();
        if (SystemHelper.getCommandLineParameter(SystemHelper.URL) != null) {
            return getURLParameter();
        } else {
            return getPageObject().getURL(environmentName);
        }
    }

    public static String getEnvironment() {
        if (!getCurrentThreadObjects().has(SystemHelper.ENVIRONMENT)) {
            //entered on the command line as -Denvironment = XXX
            getCurrentThreadObjects().put(SystemHelper.ENVIRONMENT, SystemHelper.getCommandLineParameter(SystemHelper.ENVIRONMENT));
        }

        if (getCurrentThreadObjects().has(SystemHelper.ENVIRONMENT)) {
            return (String) getCurrentThreadObjects().get(SystemHelper.ENVIRONMENT);
        } else {
            return LOCAL;
        }
    }

    public static void setEnvironment(String _environmentName) {
        getCurrentThreadObjects().put(SystemHelper.ENVIRONMENT, _environmentName);
    }

    public static String getURLParameter() {
        String urlParameter = null;

        //entered on the command line as -Durl = XXX
        urlParameter = SystemHelper.getCommandLineParameter(SystemHelper.URL);

        if (urlParameter != null && !urlParameter.isEmpty()) {
            store(URL_PARAMETER, urlParameter);
            return urlParameter;
        } else {
            return NONE;
        }
    }

    public static String getPageObjectItem(String item) {
        return getPageObject().retrieve(item.toLowerCase());
    }

    public static void store(String key, String value) {
        getPageObject().store(key, value);
    }

    public static String retrieve(String key) {
        return getPageObject().retrieve(key);
    }

    public static void storeObject(String key, Object value) { getPageObject().storeObject(key, value); }
    public static <T> T retrieveObject(String key) {
        return (T)getPageObject().retrieveObject(key);
    }

    private static PageObjectTable getPersistedStorage() {
        ThreadObjectTable current = getCurrentThreadObjects();

        if (!current.has(PERSISTED_STORAGE)) {
            current.put(PERSISTED_STORAGE, new PageObjectTable());
        }

        return (PageObjectTable) current.get(PERSISTED_STORAGE);
    }

    public static void storePersisted(String key, String value) {
        getPersistedStorage().put(key, value);
    }

    public static String retrievePersisted(String key) {
        return getPersistedStorage().get(key);
    }

    public static boolean isPersisted(String key) {
        return getPersistedStorage().containsKey(key);
    }
}
