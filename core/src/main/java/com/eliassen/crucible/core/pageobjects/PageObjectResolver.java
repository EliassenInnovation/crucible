package com.eliassen.crucible.core.pageobjects;

import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.core.sharedobjects.ProgressHandler;
import com.eliassen.crucible.core.helpers.Logger;

import java.lang.reflect.InvocationTargetException;

public class PageObjectResolver
{
	public static final String BASE_NAME_SPACE = "baseNameSpace";
	public static final String PROGRESS_HANDLER_NAME = "progressHandlerName";
	public static final String ADDITIONAL_PAGE_OBJECT_PATHS_BY_ENVIRONMENT = "additionalPageObjectPathsByEnvironment";

	/**
	 * @param pageName: The class name of the page object file, with no extension (ie "Main" not "Main.java")
	 * @param additionalPaths: If the file is not in the client library's pageObject base name space, additional paths
		* 	 * 			of the name space needed to navigate down to the file (ie for a page object located in
		* 	 * 			main/java/pageObjects/studentSummary, pass in new String[]{"studentSummary"}
	 * @param baseNameSpace: The baseNameSpace for the client library's pageObjects
	 * @param handler: the client-specific ProgressHandler (if there is one)
	 * @return PageObjectBase: the requested pageObject
	 * @throws Exception
	 */
	public PageObjectBase getPageObjectByName(String pageName, String[] additionalPaths, String baseNameSpace,
											  ProgressHandler handler) throws Exception
	{
		PageObjectBase pageObject = null;

		if(pageName == null || pageName.isEmpty())
		{
			throw new Exception("Page Name must be defined!");
		}

		StringBuilder className = new StringBuilder(baseNameSpace);

		if(additionalPaths != null && additionalPaths.length > 0)
		{
			for(String path : additionalPaths)
			{
				className.append(path);
				className.append(".");
			}
		}

		className.append(pageName);

		try
		{
			pageObject = (PageObjectBase)Class.forName(className.toString()).getDeclaredConstructor().newInstance();
		}
		catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e)
		{
			e.printStackTrace();
			Logger.log("PLEASE SEE ME!!");
			Logger.log("Failed in creating page object: " + pageName);
			throw e;
		}

		pageObject.setProgressHandler(handler);

		return pageObject;
	}

	public PageObjectBase getPageObjectByName(String pageName)
	{
		try
		{
			return getPageObjectByName(pageName, null);
		} catch (Exception e)
		{
			Logger.log("Page name not defined in feature file");
			return null;
		}
	}

	public PageObjectBase getPageObjectByName(String pageName, String[] additionalPaths) throws Exception{
		String baseNameSpace = SystemHelper.getApplicationSetting(BASE_NAME_SPACE);
		if(additionalPaths == null){
			additionalPaths = getAdditionalPathsForEnvironmentFromConfig();
		}
		String progressHandlerName = SystemHelper.getApplicationSetting(PROGRESS_HANDLER_NAME);
		return getPageObjectByName(pageName, additionalPaths, baseNameSpace, getProgressHandlerByName(progressHandlerName,baseNameSpace));
	}

	/**
	 * Expects dot notation of additional paths for the environment
	 * @return
	 */
	private String[] getAdditionalPathsForEnvironmentFromConfig() {
		String additionalPathsString = SystemHelper.getConfigSetting(ADDITIONAL_PAGE_OBJECT_PATHS_BY_ENVIRONMENT + "." +
				SystemHelper.getEnvironment());

		String[] additionalPaths = new String[]{};
		if(additionalPathsString != null){
			additionalPaths = additionalPathsString.split("\\.");
		}

		return additionalPaths;
	}

	public ProgressHandler getProgressHandlerByName(String progressHandlerName, String baseNameSpace) throws Exception {
		ProgressHandler progressHandler = null;

		if(progressHandlerName == null || progressHandlerName.isEmpty())
		{
			throw new Exception("Progress Handler Name must be defined!");
		}

		StringBuilder className = new StringBuilder(baseNameSpace);

		className.append(progressHandlerName);

		try
		{
			progressHandler = (ProgressHandler) Class.forName(className.toString()).getDeclaredConstructor().newInstance();
		}
		catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e)
		{
			e.printStackTrace();
			Logger.log("PLEASE SEE ME!!");
			Logger.log("Failed in creating progress handler: " + progressHandlerName);
			throw e;
		}

		return progressHandler;
	}
}
