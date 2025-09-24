package com.lightwell.testing.demo.pageObjects;

public class DbDemo extends DemoPageObjectBase
{
    public final static String NAME = "DbDemo";

    public DbDemo()
    {
        super();
        setPageName(NAME);
    }

    @Override
    public void fillPageTable()
    {
        addSubViews(new String[]{});
    }

    @Override
    public void addQueries()
    {
        addQuery("create people table", "CREATE TABLE PEOPLE (ID int PRIMARY KEY NOT NULL, LASTNAME varchar(50) NOT NULL, FIRSTNAME varchar(50) NOT NULL, PHONE varchar(25) NOT NULL)");
        addQuery("select from people", "SELECT * FROM PEOPLE");
    }

}