package com.lightwell.testing.demo.pageObjects.espn;

import com.lightwell.testing.demo.pageObjects.DemoPageObjectBase;

public class MiamiDolphins extends DemoPageObjectBase
    {
        public final static String NAME = "MiamiDolphins";

        public MiamiDolphins()
        {
            super();
            setPageName(NAME);
        }

        @Override
        public void fillPageTable()
        {
            addSubViews(new String[]{"espn.ESPN"});
            addUrls();

            store("miami dolphins link","//a//span[text()='Miami Dolphins']");
        }
    }
