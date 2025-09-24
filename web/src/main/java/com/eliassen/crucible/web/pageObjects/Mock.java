package com.eliassen.crucible.web.pageObjects;

import com.eliassen.crucible.core.pageobjects.PageObjectBase;
import com.eliassen.crucible.core.pageobjects.PageObjectTable;

public class Mock extends PageObjectBase
{
    public final static String NAME = "Mock";

    private PageObjectTable mockTable = new PageObjectTable(){
        @Override
        public String get(Object name) {
            return "//";
        }
    };

    @Override
    protected PageObjectTable getPageTable() {
        return mockTable;
    }

    public Mock()
    {
        super();
        setPageName(NAME);
    }

    @Override
    public void fillPageTable()
    {
        addSubViews(new String[]{});

        store("mock", "//");
    }
}