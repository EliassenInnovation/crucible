package com.eliassen.crucible.frameworkbrowser.helper;

import org.json.JSONArray;

public class GetResourceFilesRequestBuilder
{
    private String source;
    private String baseName;
    private String basePath;
    private String[] excludeFileTypes;

    private boolean useExternalJar;
    private boolean useFeatureNames;

    public GetResourceFilesRequestBuilder setSource(String source)
    {
        this.source = source;
        return this;
    }

    public GetResourceFilesRequestBuilder setBaseName(String baseName)
    {
        this.baseName = baseName;
        return this;
    }

    public GetResourceFilesRequestBuilder setBasePath(String basePath)
    {
        this.basePath = basePath;
        return this;
    }

    public GetResourceFilesRequestBuilder setExcludeFileTypes(String[] excludeFileTypes)
    {
        this.excludeFileTypes = excludeFileTypes;
        return this;
    }

    public GetResourceFilesRequestBuilder setExcludeFileTypes(JSONArray excludeFileTypes)
    {
        this.excludeFileTypes = DataHelper.convertJsonArrayToStringArray(excludeFileTypes);
        return this;
    }

    public GetResourceFilesRequestBuilder setUseExternalJar(boolean useExternalJar)
    {
        this.useExternalJar = useExternalJar;
        return this;
    }

    public GetResourceFilesRequestBuilder setUseFeatureNames(boolean useFeatureNames)
    {
        this.useFeatureNames = useFeatureNames;
        return this;
    }

    public GetResourceFilesRequest build(){
        return new GetResourceFilesRequest(source,baseName,basePath, excludeFileTypes, useExternalJar, useFeatureNames);
    }
}
