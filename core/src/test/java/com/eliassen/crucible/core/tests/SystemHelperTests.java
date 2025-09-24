package com.eliassen.crucible.core.tests;

import com.eliassen.crucible.common.helpers.SystemHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SystemHelperTests
{
    @Test
    public void test_GetConfigSettingUsingDotNotation()
    {
        String expectedValue = "http://192.168.56.101:4444/wd/hub";
        String pathToSetting = "selenium_hub.address";
        String actualValue = SystemHelper.getConfigSetting(pathToSetting);
        assertEquals(expectedValue,actualValue);
    }

    @Test
    public void getGitVariable_ShouldBeNull()
    {
        String gitUrl = SystemHelper.getEnvironmentVariable("GIT_URL");
        assertNull(gitUrl);
    }
}
