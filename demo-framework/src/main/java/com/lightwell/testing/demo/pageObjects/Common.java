package com.lightwell.testing.demo.pageObjects;

public class Common extends DemoPageObjectBase
{
    public final static String NAME = "Common";

    public Common()
    {
        super();
        setPageName(NAME);
    }

    @Override
    public void fillPageTable()
    {
        addSubViews(new String[]{});
        addUrls();

        store("users json path", "users.json");
        store("grabHeaders","true");
    }
}