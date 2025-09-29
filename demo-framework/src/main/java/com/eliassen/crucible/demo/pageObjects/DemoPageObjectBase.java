package com.eliassen.crucible.demo.pageObjects;

import com.eliassen.crucible.core.pageobjects.PageObjectBase;
import com.eliassen.crucible.db.helpers.QueryTable;

public class DemoPageObjectBase extends PageObjectBase
{
    public DemoPageObjectBase()
    {
        resolver = new DemoPageObjectResolver();
        addQueries();
    }

    @Override
    public void fillPageTable()
    {

    }

    public void addQueries(){};

    public void addQuery(String key, String query)
    {
        QueryTable.getQueryList().put(key,query);
    }

}