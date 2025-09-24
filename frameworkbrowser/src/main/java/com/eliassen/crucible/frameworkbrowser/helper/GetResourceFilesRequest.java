package com.eliassen.crucible.frameworkbrowser.helper;

public class GetResourceFilesRequest
{
    private String source;
    private String baseName;
    private String basePath;
    private String[] excludeFileTypes;

    private boolean useExternalJar;
    private boolean useFeatureNames;

    public GetResourceFilesRequest(String source, String baseName, String basePath, String[] excludeFileTypes, boolean useExternalJar, boolean useFeatureNames)
    {
        this.source = source;
        this.baseName = baseName;
        this.basePath = basePath;
        this.excludeFileTypes = excludeFileTypes;
        this.useExternalJar = useExternalJar;
        this.useFeatureNames = useFeatureNames;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getBaseName()
    {
        return baseName;
    }

    public void setBaseName(String baseName)
    {
        this.baseName = baseName;
    }

    public String getBasePath()
    {
        return basePath;
    }

    public void setBasePath(String basePath)
    {
        this.basePath = basePath;
    }

    public String[] getExcludeFileTypes()
    {
        return excludeFileTypes;
    }

    public void setExcludeFileTypes(String[] excludeFileTypes)
    {
        this.excludeFileTypes = excludeFileTypes;
    }

    public boolean isUseExternalJar()
    {
        return useExternalJar;
    }

    public void setUseExternalJar(boolean useExternalJar)
    {
        this.useExternalJar = useExternalJar;
    }

    public boolean isUseFeatureNames()
    {
        return useFeatureNames;
    }

    public void setUseFeatureNames(boolean useFeatureNames)
    {
        this.useFeatureNames = useFeatureNames;
    }
}
