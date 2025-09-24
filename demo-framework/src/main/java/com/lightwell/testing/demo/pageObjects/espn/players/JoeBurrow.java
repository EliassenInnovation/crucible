package com.lightwell.testing.demo.pageObjects.espn.players;

import com.lightwell.testing.demo.pageObjects.DemoPageObjectBase;

public class JoeBurrow extends DemoPageObjectBase
{
    public final static String NAME = "JoeBurrow";

    public JoeBurrow()
    {
        super();
        setPageName(NAME);
        this.pageUrlPart = "nfl/player/stats/_/id/3915511/joe-burrow";
    }

    @Override
    public void fillPageTable()
    {
        addSubViews(new String[]{"espn.ESPN"});

        store("joe burrow","https://www.espn.com/nfl/player/stats/_/id/3915511/joe-burrow");
    }
}

