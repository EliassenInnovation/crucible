package com.lightwell.testing.demo.pageObjects;

import com.eliassen.crucible.core.sharedobjects.ProgressHandler;
import com.eliassen.crucible.web.helpers.TestHelper;

public class DemoProgressHandler extends ProgressHandler
{
    @Override
    public void checkProgress()
    {
        TestHelper.wait(1);
    }
}