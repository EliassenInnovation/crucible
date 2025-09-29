package com.eliassen.crucible.demo.pageObjects;

import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.core.pageobjects.PageObjectBase;
import com.eliassen.crucible.core.pageobjects.PageObjectResolver;

public class DemoPageObjectResolver extends PageObjectResolver
{
    public static final String BASE_NAME_SPACE = "baseNameSpace";

    String baseNameASpace = SystemHelper.getApplicationSetting(BASE_NAME_SPACE);

    @Override
    public PageObjectBase getPageObjectByName(String pageName, String[] additionalPaths) throws Exception
    {
        return getPageObjectByName(pageName, additionalPaths, baseNameASpace, new DemoProgressHandler());
    }
}
