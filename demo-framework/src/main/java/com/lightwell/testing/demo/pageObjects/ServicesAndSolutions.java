package com.lightwell.testing.demo.pageObjects;

public class ServicesAndSolutions extends DemoPageObjectBase
{
    public final static String NAME = "ServicesAndSolutions";

    public ServicesAndSolutions()
    {
        super();
        setPageName(NAME);
        this.pageUrlPart = "services-solutions/";
    }

    @Override
    public void fillPageTable()
    {
        addSubViews(new String[]{"Lightwell"});

        store("services and solutions page","services-solutions");
    }
}
