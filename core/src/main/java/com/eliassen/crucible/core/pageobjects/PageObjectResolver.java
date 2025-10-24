package com.eliassen.crucible.core.pageobjects;

import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.core.sharedobjects.ProgressHandler;
import com.eliassen.crucible.core.helpers.Logger;

import java.lang.reflect.InvocationTargetException;

/**
 * Resolves page objects based on the provided parameters.
 */
public class PageObjectResolver {

    /**
     * Key for the base namespace in the application settings.
     */
    public static final String BASE_NAME_SPACE = "baseNameSpace";

    /**
     * Key for the progress handler name in the application settings.
     */
    public static final String PROGRESS_HANDLER_NAME = "progressHandlerName";

    /**
     * Key for the additional page object paths by environment in the configuration settings.
     */
    public static final String ADDITIONAL_PAGE_OBJECT_PATHS_BY_ENVIRONMENT = "additionalPageObjectPathsByEnvironment";

    /**
     * Retrieves a page object by name, using the provided additional paths, base namespace, and progress handler.
     *
     * @param pageName        The class name of the page object file, with no extension (e.g., "Main" not "Main.java").
     * @param additionalPaths If the file is not in the client library's pageObject base namespace, additional paths
     *                        of the namespace needed to navigate down to the file (e.g., for a page object located in
     *                        main/java/pageObjects/studentSummary, pass in new String[]{"studentSummary"}).
     * @param baseNameSpace   The base namespace for the client library's pageObjects.
     * @param handler         The client-specific ProgressHandler (if there is one).
     * @return The requested page object.
     * @throws Exception If the page name is not defined or if there is an error creating the page object.
     */
    public PageObjectBase getPageObjectByName(String pageName, String[] additionalPaths, String baseNameSpace,
                                              ProgressHandler handler) throws Exception {
        PageObjectBase pageObject = null;

        if (pageName == null || pageName.isEmpty()) {
            throw new Exception("Page Name must be defined!");
        }

        StringBuilder className = new StringBuilder(baseNameSpace);

        if (additionalPaths != null && additionalPaths.length > 0) {
            for (String path : additionalPaths) {
                className.append(".").append(path);
            }
        }

        className.append(".").append(pageName);

        try {
            pageObject = (PageObjectBase) Class.forName(className.toString()).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            Logger.log("PLEASE SEE ME!!");
            Logger.log("Failed in creating page object: " + pageName);
            throw e;
        }

        pageObject.setProgressHandler(handler);

        return pageObject;
    }

    /**
     * Retrieves a page object by name, using the default additional paths and progress handler.
     *
     * @param pageName The class name of the page object file, with no extension.
     * @return The requested page object, or null if the page name is not defined.
     */
    public PageObjectBase getPageObjectByName(String pageName) {
        try {
            return getPageObjectByName(pageName, null);
        } catch (Exception e) {
            Logger.log("Page name not defined in feature file");
            return null;
        }
    }

    /**
     * Retrieves a page object by name, using the provided additional paths.
     *
     * @param pageName        The class name of the page object file, with no extension.
     * @param additionalPaths Additional paths to the page object.
     * @return The requested page object.
     * @throws Exception If there is an error creating the page object.
     */
    public PageObjectBase getPageObjectByName(String pageName, String[] additionalPaths) throws Exception {
        String baseNameSpace = SystemHelper.getApplicationSetting(BASE_NAME_SPACE);
        if (additionalPaths == null) {
            additionalPaths = getAdditionalPathsForEnvironmentFromConfig();
        }
        String progressHandlerName = SystemHelper.getApplicationSetting(PROGRESS_HANDLER_NAME);
        return getPageObjectByName(pageName, additionalPaths, baseNameSpace, getProgressHandlerByName(progressHandlerName, baseNameSpace));
    }

    /**
     * Retrieves the additional paths for the environment from the configuration settings.
     *
     * @return The additional paths for the environment.
     */
    private String[] getAdditionalPathsForEnvironmentFromConfig() {
        String additionalPathsString = SystemHelper.getConfigSetting(ADDITIONAL_PAGE_OBJECT_PATHS_BY_ENVIRONMENT + "." +
                SystemHelper.getEnvironment());

        String[] additionalPaths = new String[]{};
        if (additionalPathsString != null) {
            additionalPaths = additionalPathsString.split("\\.");
        }

        return additionalPaths;
    }

    /**
     * Retrieves a progress handler by name, using the provided base namespace.
     *
     * @param progressHandlerName The name of the progress handler.
     * @param baseNameSpace       The base namespace for the progress handler.
     * @return The requested progress handler.
     * @throws Exception If the progress handler name is not defined or if there is an error creating the progress handler.
     */
    public ProgressHandler getProgressHandlerByName(String progressHandlerName, String baseNameSpace) throws Exception {
        ProgressHandler progressHandler = null;

        if (progressHandlerName == null || progressHandlerName.isEmpty()) {
            throw new Exception("Progress Handler Name must be defined!");
        }

        StringBuilder className = new StringBuilder(baseNameSpace);

        className.append(".").append(progressHandlerName);

        try {
            progressHandler = (ProgressHandler) Class.forName(className.toString()).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            Logger.log("PLEASE SEE ME!!");
            Logger.log("Failed in creating progress handler: " + progressHandlerName);
            throw e;
        }

        return progressHandler;
    }
}
