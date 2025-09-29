package com.eliassen.crucible.demo.unitTests;

import com.eliassen.crucible.core.sharedobjects.Headers;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import com.eliassen.crucible.demo.helpers.api.DemoApiHelper;
import com.eliassen.crucible.demo.pageObjects.Common;
import org.junit.Assert;
import org.junit.Test;

public class DemoApiHelperTests {
    @Test
    public void test_getHeaders_headersIsNotEmpty()
    {
        //arrange
        Headers headers;
        CurrentPage.setPageObject(new Common());
        //act
        headers = DemoApiHelper.getHeaders();
        //assert
        Assert.assertTrue(headers.size() > 0);
    }
}
