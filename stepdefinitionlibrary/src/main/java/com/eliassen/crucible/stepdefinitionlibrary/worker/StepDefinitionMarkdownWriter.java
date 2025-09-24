package com.eliassen.crucible.stepdefinitionlibrary.worker;

import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.frameworkbrowser.constants.FrameworkBrowserConstants;
import com.eliassen.crucible.frameworkbrowser.worker.MarkdownCreator;
import com.eliassen.crucible.stepdefinitionlibrary.constants.StepDefinitionLibraryConstants;
import com.eliassen.crucible.stepdefinitionlibrary.shared.StepDefinitionModel;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class StepDefinitionMarkdownWriter extends MarkdownCreator
{
    private String getStepDefinitionDocumentationDirectory(){
        return SystemHelper.getConfigSetting(StepDefinitionLibraryConstants.STEP_DEFINITION_SETTING_ROOT +
                FrameworkBrowserConstants.DOCUMENTATION_DIRECTORY);
    }

    public void createStepDefinitionsMissingDocumentationFile(String filenamePath,
                  Map<String, Set<StepDefinitionModel>> stepDefinitionsMissingDocumentationByFileName)
                  throws IOException
    {
        String content = createStepDefinitionsMissingDocumentationContent(stepDefinitionsMissingDocumentationByFileName);
        markdownCreationCommon(filenamePath, content, getStepDefinitionDocumentationDirectory());
    }

    private String createStepDefinitionsMissingDocumentationContent(
            Map<String, Set<StepDefinitionModel>> stepDefinitionsMissingDocumentationByFileName)
    {
        StringBuilder content = new StringBuilder(heading("Step Definitions Missing Documentation"));

        for(Map.Entry<String, Set<StepDefinitionModel>> entrySet : stepDefinitionsMissingDocumentationByFileName.entrySet())
        {
            content.append(heading(3,entrySet.getKey()));

            for(StepDefinitionModel stepDefModel : entrySet.getValue())
            {
                for (String matcher : stepDefModel.getMatchers())
                {
                    content.append(bold("Matcher:")).append(" ").append(matcher).append("<br>");
                }
                content.append(bold("Method Signature:")).append(" ").append(stepDefModel.getSignature()).append("<br><br>");
            }
            content.append("  \n");
        }

        return content.toString();
    }

    public void createStepDefinitionsDocumentationFile(String fileNamePath,
                   Map<String, Set<StepDefinitionModel>> stepDefinitionsWithDocumentationByParentClass) throws IOException
    {
        String content = createStepDefinitionsDocumentationContent(stepDefinitionsWithDocumentationByParentClass);
        markdownCreationCommon(fileNamePath, content, getStepDefinitionDocumentationDirectory());
    }

    private String createStepDefinitionsDocumentationContent(
            Map<String, Set<StepDefinitionModel>> stepDefinitionsWithDocumentationByFileName)
    {
        StringBuilder content = new StringBuilder(heading("Step Definition Documentation"));

        for(Map.Entry<String, Set<StepDefinitionModel>> entrySet : stepDefinitionsWithDocumentationByFileName.entrySet())
        {
            content.append(heading(3,entrySet.getKey()));

            for(StepDefinitionModel stepDefModel : entrySet.getValue())
            {
                for (String matcher : stepDefModel.getMatchers())
                {
                    content.append(bold("Matcher:")).append(" ").append(matcher).append("<br>");
                }
                content.append(bold("Method Signature:")).append(" ").append(stepDefModel.getSignature()).append("<br>");
                content.append(bold("Documentation:")).append("<br>").append(cleanedDocumentation(stepDefModel.getComment()))
                        .append("<br><br>");
            }
            content.append("\n");
        }

        return content.toString();
    }

    private String cleanedDocumentation(String comment)
    {
        StringBuilder documentationBuilder = new StringBuilder();
        String[] documentationLines = comment.split("\r\n");

        for(String line : documentationLines)
        {
            line = line.replace("/**", "").trim();
            line = line.replace("*/", "").trim();
            line = line.replaceAll("^\\*", "").trim();
            if(!line.isEmpty())
            {
                if(!documentationBuilder.toString().isEmpty())
                {
                    documentationBuilder.append("<br>");
                }
                documentationBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;").append(line);
            }
        }

        return documentationBuilder.toString();
    }

}
