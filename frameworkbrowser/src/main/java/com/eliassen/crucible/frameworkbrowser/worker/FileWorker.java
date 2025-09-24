package com.eliassen.crucible.frameworkbrowser.worker;

import com.eliassen.crucible.common.helpers.FileHelper;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileWorker
{
    public String getFeatureName(String path)
    {
        return getFeatureName(path, "", false);
    }

    public String getFeatureName(String path, String base, boolean useExternalJar)
    {
        String content = useExternalJar ? loadFileContentFromExternalJar(path,base) :loadFileContent(path);
        String featureNamePattern = "Feature:(.*)[\\r\\n]";
        Pattern regex = Pattern.compile(featureNamePattern);
        Matcher regexMatcher = regex.matcher(content);

        String match = "Did not find a feature name in " + path + "!";
        while (regexMatcher.find())
        {
            match = regexMatcher.group(1);
        }

        return match.trim();
    }

    public String loadFileContentFromExternalJar(String path, String baseName)
    {
        InputStream in = null;
        URL inputURL = null;
        String content = "";

        path = path.replace("\\","/");
        baseName = baseName.replace("\\","/");

        String optionalPathSeparator = "";//PlatformUtil.isWindows() && baseName.contains("/")? "/" : "";

        String inputFile = "jar:file:" + optionalPathSeparator + baseName + "!/" + path;

        try {
            inputURL = new URL(inputFile);
            JarURLConnection conn = (JarURLConnection)inputURL.openConnection();
            in = conn.getInputStream();
            content = new String(in.readAllBytes());
        } catch (MalformedURLException m) {
            System.err.println("Malformed input URL: " + inputURL);
        } catch (IOException i) {
            i.printStackTrace();;
        }

        return content;
    }

    public String loadFileContent(String path)
    {
        String content = "";
        FileHelper fileHelper = new FileHelper();

        try
        {
            content = fileHelper.getTextFileContent(path);

        }
        catch (NullPointerException n)
        {
            System.err.println("Path failed: " + path);
        }

        return content;
    }
}
