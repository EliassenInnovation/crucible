package com.eliassen.crucible.stepdefinitionlibrary;

import com.eliassen.crucible.stepdefinitionlibrary.constants.StepDefinitionLibraryConstants;
import com.eliassen.crucible.stepdefinitionlibrary.helper.StepDefinitionFunctions;
import com.eliassen.crucible.stepdefinitionlibrary.shared.StepDefinitionModel;
import com.eliassen.crucible.stepdefinitionlibrary.worker.StepDefinitionGrabber;
import com.eliassen.crucible.stepdefinitionlibrary.worker.StepDefinitionMarkdownWriter;
import com.eliassen.crucible.common.helpers.FileHelper;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

public class Main
{
    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException, IOException
    {
        StepDefinitionGrabber stepDefinitionGrabber = new StepDefinitionGrabber();
        StepDefinitionMarkdownWriter markdownWriter = new StepDefinitionMarkdownWriter();

        FileHelper.ensureDirectoryExists(StepDefinitionFunctions
                        .getStepDefinitionLibrarySettingString(StepDefinitionLibraryConstants.DOCUMENTATION_DIRECTORY));
        Set<StepDefinitionModel> stepDefinitions = stepDefinitionGrabber.getStepDefinitionsFromLibrary();
        System.out.println("Step definition count: " + stepDefinitions.size());

        //Missing Documentation
        Map<String, Set<StepDefinitionModel>> stepDefinitionsMissingDocumentationByParentClass =
                stepDefinitionGrabber.organizeStepDefinitionsByParentClass(
                stepDefinitionGrabber.getStepDefinitionsMissingDocumentation(stepDefinitions));

        markdownWriter.createStepDefinitionsMissingDocumentationFile(
                StepDefinitionFunctions.getStepDefinitionLibrarySettingString(
                        StepDefinitionLibraryConstants.DOCUMENTATION_DIRECTORY) +
                        "/" +
                        StepDefinitionFunctions.getStepDefinitionLibrarySettingString(
                                StepDefinitionLibraryConstants.STEP_DEFINITIONS_MISSING_DOCUMENTATION_FILE_NAME),
                                stepDefinitionsMissingDocumentationByParentClass);

        //With Documentation
        Map<String, Set<StepDefinitionModel>> stepDefinitionsWithDocumentationByParentClass =
                stepDefinitionGrabber.organizeStepDefinitionsByParentClass(
                        stepDefinitionGrabber.getStepDefinitionsWithDocumentation(stepDefinitions));

        markdownWriter.createStepDefinitionsDocumentationFile(
                StepDefinitionFunctions.getStepDefinitionLibrarySettingString(
                        StepDefinitionLibraryConstants.DOCUMENTATION_DIRECTORY) +
                        "/" +
                        StepDefinitionFunctions.getStepDefinitionLibrarySettingString(
                                StepDefinitionLibraryConstants.STEP_DEFINITION_DOCUMENTATION_FILE_NAME),
                                stepDefinitionsWithDocumentationByParentClass);
    }
}
