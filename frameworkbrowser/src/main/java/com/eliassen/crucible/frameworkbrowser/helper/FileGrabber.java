package com.eliassen.crucible.frameworkbrowser.helper;

import org.apache.commons.lang3.SystemUtils;
import com.eliassen.crucible.frameworkbrowser.shared.Parser;
import com.eliassen.crucible.frameworkbrowser.worker.FileWorker;
import com.eliassen.crucible.common.helpers.SystemHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystems;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class FileGrabber
{
    protected GetResourceFilesRequest request;
    protected FileWorker fileWorker;
    protected Parser[] parsers;

    public Map<String, Object> getResourceFiles() throws ClassNotFoundException, InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException
    {
        GetResourceFilesRequest request = new GetResourceFilesRequestBuilder()
                .setSource(SystemHelper.getConfigSetting("frameworkBrowser.source"))
                .setBaseName(SystemHelper.getConfigSetting("frameworkBrowser.baseName"))
                .setBasePath(SystemHelper.getConfigSetting("frameworkBrowser.basePath"))
                .setExcludeFileTypes(SystemHelper.getConfigSetting("frameworkBrowser.excludedFileTypes").split(","))
                .setUseExternalJar(Boolean.parseBoolean(SystemHelper.getConfigSetting("frameworkBrowser.useExternalJar")))
                .setUseFeatureNames(Boolean.parseBoolean(SystemHelper.getConfigSetting("frameworkBrowser.useFeatureNames")))
                .build();

        return getResourceFiles(request);
    }

    public Map<String, Object> getResourceFiles(GetResourceFilesRequest request) throws ClassNotFoundException,
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
    {
        Map<String, Object> resources = new TreeMap<>();

        if (request.getSource().toLowerCase(Locale.ROOT).equals("files"))
        {
            resources.put(getPathLeaf(request.getBasePath()),
                    getResourceFilesFromDisk(request.getBasePath(),
                            request.isUseFeatureNames(),
                            request.getExcludeFileTypes()));
        }
        else
        {
            String urlString = "";
            if (request.isUseExternalJar())
            {
                urlString = request.getBaseName();
            }
            else
            {
                urlString = getUrlStringForInternalJar(request.getBaseName());
            }
            resources = getResourceFilesFromJar(urlString, request);
        }

        return resources;
    }

    private Map<String, Object> getResourceFilesFromDisk(String basePath, boolean useFeatureNames, String[] excludeFileTypes)
    {
        Map<String,Object> resourcePaths = new TreeMap<>();

        File resource = new File(basePath);
        for(File file : resource.listFiles())
        {
            if (file.isDirectory())
            {
                String filePathLeaf = getPathLeaf(file.getPath());
                resourcePaths.put(filePathLeaf,getResourceFilesFromDisk(file.getPath(), useFeatureNames, excludeFileTypes));
            }
            else
            {
                if(!resourcePaths.containsKey(basePath) && !fileIsExcludedType(file.getName(),excludeFileTypes))
                {
                    String key;
                    if(useFeatureNames)
                    {
                        FileWorker fileWorker = new FileWorker();
                        key = fileWorker.getFeatureName(file.getPath());
                    }
                    else
                    {
                        key = getPathLeaf(file.getPath());
                    }
                    resourcePaths.put(key, file.getPath());
                }
            }
        }
        return resourcePaths;
    }

    public String getPathLeaf(String path)
    {
        String splitter = FileSystems.getDefault().getSeparator();

        if(SystemUtils.IS_OS_WINDOWS)
        {
            if(path.contains("/")){
                splitter = "/";
            }
            else
            {
                splitter = "\\\\";
            }
        }

        String[] pathParts = path.split(splitter);
        return pathParts[pathParts.length - 1];
    }

    public String getUrlStringForInternalJar(String packageName) throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException
    {
        Object referenceClass = Class.forName(packageName).getDeclaredConstructor().newInstance();
        File child = new File(referenceClass.getClass().getResource("").getPath());
        String parentPath = child.getParent();
        String urlString = child.getParent().replaceAll("(!|file:\\\\|file:)", "");

        while (!urlString.endsWith("jar"))
        {
            urlString = new File(urlString).getParent();
        }

        return urlString;
    }

    public Map<String,Object> getResourceFilesFromJar(String urlString, GetResourceFilesRequest request)
    {
        Map<String,Object> resourcePaths = new TreeMap<>();

        JarFile jarFile = null;
        try
        {
            jarFile = new JarFile(urlString);

            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements())
            {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.getName().startsWith(request.getBasePath())
                        && !fileIsExcludedType(jarEntry.getName(), request.getExcludeFileTypes()))
                {
                    resourcePaths = addResourceToMap(resourcePaths, jarEntry, request);
                }
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                jarFile.close();
            }
            catch (Exception e)
            {
            }
        }

        return resourcePaths;
    }

    public Map<String, Object> addResourceToMap(Map<String, Object> resourcePaths, JarEntry jarEntry,
                                                GetResourceFilesRequest request)
    {
        String[] pathParts = jarEntry.getName().split("/");
        Map<String, Object> tempMap = resourcePaths;
        String tempKey = null;

        for(String pathPart : pathParts)
        {
            if (pathPart != null)
            {
                //if the pathPart contains '.' we reached the filename and we're done
                if (pathPart.contains("."))
                {
                    String key = "";
                    if(request.isUseFeatureNames())
                    {
                        FileWorker fileWorker = new FileWorker();
                        key = fileWorker.getFeatureName(jarEntry.getName(), request.getBaseName(),
                                request.isUseExternalJar());
                    }
                    else
                    {
                        key = pathPart;
                    }
                    tempMap.put(key,jarEntry.getName());
                }
                else
                {
                    //check if the path's map has already been created
                    //add it if not.
                    //If it already exists, we don't need to add it again
                    if (!tempMap.containsKey(pathPart))
                    {
                        tempMap.put(pathPart, new TreeMap<>());
                    }

                    //move down a level
                    tempMap = (TreeMap) tempMap.get(pathPart);
                    //necessary for next iteration, if the next iteration is the file
                    tempKey = pathPart;
                }
            }
        }

        return resourcePaths;
    }

    public static boolean fileIsExcludedType(String name, String[] excludeFileTypes)
    {
        for(String exludedFileType : excludeFileTypes)
        {
            if(name.contains("." + exludedFileType))
            {
                return true;
            }
        }

        return false;
    }

    protected Parser[] getParsers()
    {
        if(parsers == null)
        {
            parsers = ParserHelper.getParsers(getParserFileName());
        }
        return parsers;
    }

    public abstract String getParserFileName();

    protected GetResourceFilesRequest getRequest()
    {
        if(request == null)
        {
            throw new NullPointerException("Request is null!");
        }
        return request;
    }

    protected FileWorker getFileWorker()
    {
        if(fileWorker == null)
        {
            fileWorker = new FileWorker();
        }
        return fileWorker;
    }

    protected String getFileContent(String path)
    {
        String content = "";
        if(getRequest().isUseExternalJar())
        {
            content = getFileWorker().loadFileContentFromExternalJar(path, getRequest().getBaseName());
        }
        else
        {
            content = getFileWorker().loadFileContent(path);
        }
        return content;
    }

    protected Parser getParserByName(String parserName) throws ClassNotFoundException
    {
        Parser parser = Arrays.stream(getParsers())
                .filter(p -> p.getParserName().equals(parserName))
                .findFirst()
                .orElse(null);

        if(parser == null)
        {
            throw new ClassNotFoundException("Parser not defined in parsers json file");
        }

        return parser;
    }
}
