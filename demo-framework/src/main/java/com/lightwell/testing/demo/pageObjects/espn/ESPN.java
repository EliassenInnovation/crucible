package com.lightwell.testing.demo.pageObjects.espn;

import com.lightwell.testing.demo.pageObjects.DemoPageObjectBase;

public class ESPN extends DemoPageObjectBase
{
    public final static String NAME = "ESPN";

    public ESPN()
    {
        super();
        setPageName(NAME);
    }

    @Override
    public void fillPageTable()
    {
        addSubViews(new String[]{});
        addUrls();

        store("nfl navigation link","//li/a//span[text()='NFL']/../../..");
        store("nfl teams div","//li/a//span[text()='NFL']/../../../div");
        store("espn logo","//a[text()='ESPN']");
        store("next game","//span[contains(@class,'powered-by__eventName')]");
        store("teamurl","//span[text()='%s']//ancestor::a[@data-teamabbrev]");
    }
}
