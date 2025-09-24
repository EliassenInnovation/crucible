package com.eliassen.crucible.stepdefinitionlibrary.helper;

import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.stepdefinitionlibrary.constants.StepDefinitionLibraryConstants;
import org.json.JSONArray;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class StepDefinitionFunctions
{
    public static String getStepDefinitionLibrarySettingString(String settingName)
    {
        return SystemHelper.getConfigSetting(StepDefinitionLibraryConstants.STEP_DEFINITION_SETTING_ROOT + settingName);
    }

    public static boolean getStepDefinitionLibrarySettingBoolean(String settingName)
    {
        return SystemHelper.getConfigSettingBoolean(StepDefinitionLibraryConstants.STEP_DEFINITION_SETTING_ROOT + settingName);
    }

    public static JSONArray getStepDefinitionLibrarySettingArray(String settingName)
    {
        return SystemHelper.getConfigSettingArray(StepDefinitionLibraryConstants.STEP_DEFINITION_SETTING_ROOT + settingName);
    }

    public static Set<String> getStepDefinitionsMissingFromControlSet(final Set<String> setToTest, final Set<String> controlSet)
    {
        Set<String> missingTags = setToTest.stream().filter(t -> !controlSet.contains(t)).collect(Collectors.toCollection(TreeSet::new));
        return missingTags;
    }
}
