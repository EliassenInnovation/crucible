package com.lightwell.testing.demo.pageObjects;

public class NewIdeas extends DemoPageObjectBase
{
    public final static String NAME = "NewIdeas";

    public NewIdeas()
    {
        super();
        setPageName(NAME);
    }

    @Override
    public void fillPageTable()
    {
        addSubViews(new String[]{"Lightwell"});

        store("browse all resources button","//a[@href='https://www.lightwellinc.com/resources/resource-library']");
        store("spark new ideas heading","//h2[contains(@class,'elementor-heading-title') and contains(text(),'Spark Some New Ideas')]");

        //urls
        store("resource library url part","resource-library");
    }
}
