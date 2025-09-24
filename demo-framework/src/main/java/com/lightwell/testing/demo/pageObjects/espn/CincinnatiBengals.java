package com.lightwell.testing.demo.pageObjects.espn;

import com.lightwell.testing.demo.pageObjects.DemoPageObjectBase;

public class CincinnatiBengals extends DemoPageObjectBase
{
    public final static String NAME = "CincinnatiBengals";

    public CincinnatiBengals()
    {
        super();
        setPageName(NAME);
    }

    @Override
    public void fillPageTable()
    {
        addSubViews(new String[]{"espn.ESPN"});
        addUrls();

        store("cincinnati bengals page","https://www.espn.com/nfl/team/_/name/cin/cincinnati-bengals");
        store("cincinnati bengals link","//a//span[text()='Cincinnati Bengals']");
    }
}

