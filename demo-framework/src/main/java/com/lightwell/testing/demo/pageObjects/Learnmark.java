package com.lightwell.testing.demo.pageObjects;

public class Learnmark  extends DemoPageObjectBase{

    public final static String NAME = "Learnmark";

    public Learnmark()
    {
        super();
        setPageName(NAME);
    }

    @Override
    public void fillPageTable()
    {
        addSubViews(new String[]{"Common"});

        store("url_demo","https://learnmarkeducation.com/");
        store("nucleus tab","//span/a[@href='/nucleus'][text()='Nucleus']");
        store("nucleus text", "//h1/span[contains (text(), 'Nucleus')]");
    }
}

