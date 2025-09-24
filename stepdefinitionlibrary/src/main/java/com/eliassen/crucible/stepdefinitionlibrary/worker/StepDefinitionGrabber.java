package com.eliassen.crucible.stepdefinitionlibrary.worker;

import com.eliassen.crucible.frameworkbrowser.helper.FileGrabber;
import com.eliassen.crucible.frameworkbrowser.helper.GetResourceFilesRequest;
import com.eliassen.crucible.frameworkbrowser.helper.GetResourceFilesRequestBuilder;
import com.eliassen.crucible.frameworkbrowser.shared.Parser;
import com.eliassen.crucible.stepdefinitionlibrary.constants.StepDefinitionLibraryConstants;
import com.eliassen.crucible.stepdefinitionlibrary.helper.StepDefinitionFunctions;
import com.eliassen.crucible.stepdefinitionlibrary.shared.StepDefinitionModel;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StepDefinitionGrabber extends FileGrabber
{

    private FileGrabber fileGrabber;

    @Override
    public String getParserFileName()
    {
        return StepDefinitionFunctions.getStepDefinitionLibrarySettingString(StepDefinitionLibraryConstants.PARSER_JSON);
    }

    public Set<StepDefinitionModel> getStepDefinitionsFromLibrary() throws ClassNotFoundException,
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
    {
        Map<String, Object> stepDefinitionFiles = getStepDefinitionFiles();
        return getStepDefinitionsFromFiles(stepDefinitionFiles);
    }

    private Set<StepDefinitionModel> getStepDefinitionsFromFiles(Map<String, Object> stepDefinitionFiles) throws ClassNotFoundException
    {
        Set<StepDefinitionModel> stepDefinitions = new HashSet<>();

        for(Map.Entry<String, Object> record : stepDefinitionFiles.entrySet())
        {
            if(record.getValue() instanceof Map)
            {
                stepDefinitions.addAll(getStepDefinitionsFromFiles((Map)record.getValue()));
            }
            else
            {
                stepDefinitions.addAll(getStepDefinitionsFromResource(record.getValue().toString()));
            }
        }

        return stepDefinitions;
    }

    public Set<StepDefinitionModel> getStepDefinitionsMissingDocumentation(Set<StepDefinitionModel> stepDefinitions){
        return stepDefinitions.stream().filter(s -> !s.hasComment()).collect(Collectors.toSet());
    }

    public Set<StepDefinitionModel> getStepDefinitionsWithDocumentation(Set<StepDefinitionModel> stepDefinitions){
        return stepDefinitions.stream().filter(s -> s.hasComment()).collect(Collectors.toSet());
    }

    public Map<String,Set<StepDefinitionModel>> organizeStepDefinitionsByParentClass(Set<StepDefinitionModel> stepDefinitions){
        Map<String,Set<StepDefinitionModel>> stepDefinitionsByParentClass = new HashMap<>();

        for(StepDefinitionModel stepDef : stepDefinitions)
        {
            if(!stepDefinitionsByParentClass.containsKey(stepDef.getFileName())){
                stepDefinitionsByParentClass.put(stepDef.getFileName(), new HashSet<>());
            }
            stepDefinitionsByParentClass.get(stepDef.getFileName()).add(stepDef);
        }

        return stepDefinitionsByParentClass;
    }

    private Set<StepDefinitionModel> getStepDefinitionsFromResource(String path) throws ClassNotFoundException
    {
        String content = getFileContent(path);
        String filename = getPathLeaf(path);
        Set<StepDefinitionModel> stepDefinitions = getStepDefinitionsFromFileContent(content, filename);
        return stepDefinitions;
    }

    private Set<StepDefinitionModel> getStepDefinitionsFromFileContent(String content, String filename)
            throws ClassNotFoundException
    {
        Set<StepDefinitionModel> stepDefinitions = new HashSet<>();

        Parser stepDefinitionAndDocumentationParser = getParserByName(
                StepDefinitionLibraryConstants.STEP_DEFINITION_AND_DOCUMENTATION_PARSER);

        Pattern regex = Pattern.compile(stepDefinitionAndDocumentationParser.getExpression());
        Matcher regexMatcher = regex.matcher(content);

        String match;
        while (regexMatcher.find())
        {
            match = regexMatcher.group(1);

            StepDefinitionModel model = new StepDefinitionModel();
            model.setComment(getMatchFromText(match,getParserByName(
                    StepDefinitionLibraryConstants.STEP_DEFINITION_DOCUMENTATION_ONLY_PARSER)));
            model.setMatchers(getMatchFromText(match,getParserByName(
                    StepDefinitionLibraryConstants.STEP_DEFINITION_MATCHER_PARSER)));
            model.setSignature(getMatchFromText(match,getParserByName(
                    StepDefinitionLibraryConstants.STEP_DEFINITION_SIGNATURE_PARSER)));
            model.setFileName(filename);

            stepDefinitions.add(model);
        }

        return stepDefinitions;
    }

    private String getMatchFromText(String text, Parser parser)
    {
        Pattern regex = Pattern.compile(parser.getExpression());
        Matcher regexMatcher = regex.matcher(text);
        String match = "";
        while (regexMatcher.find())
        {
            match = regexMatcher.group(1);
        }
        return match;
    }

    public Map<String, Object> getStepDefinitionFiles() throws ClassNotFoundException, InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException
    {
        GetResourceFilesRequest request = new GetResourceFilesRequestBuilder()
                .setUseFeatureNames(StepDefinitionFunctions.getStepDefinitionLibrarySettingBoolean(StepDefinitionLibraryConstants.USE_FEATURE_NAMES))
                .setUseExternalJar(StepDefinitionFunctions.getStepDefinitionLibrarySettingBoolean(StepDefinitionLibraryConstants.USE_EXTERNAL_JAR))
                .setBaseName(StepDefinitionFunctions.getStepDefinitionLibrarySettingString(StepDefinitionLibraryConstants.JAR_PATH))
                .setSource(StepDefinitionFunctions.getStepDefinitionLibrarySettingString(StepDefinitionLibraryConstants.SOURCE))
                .setExcludeFileTypes(StepDefinitionFunctions.getStepDefinitionLibrarySettingArray(StepDefinitionLibraryConstants.EXCLUDE_FILE_TYPES))
                .setBasePath(StepDefinitionFunctions.getStepDefinitionLibrarySettingString(StepDefinitionLibraryConstants.BASE_PATH))
                .build();
        
        this.request = request;

        return getResourceFiles(request);
    }
}
