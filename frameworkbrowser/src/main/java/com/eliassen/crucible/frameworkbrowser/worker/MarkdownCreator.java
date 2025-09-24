package com.eliassen.crucible.frameworkbrowser.worker;

import com.eliassen.crucible.common.helpers.FileHelper;
import com.eliassen.crucible.common.helpers.JsonHelper;
import com.eliassen.crucible.frameworkbrowser.constants.FrameworkBrowserConstants;
import com.eliassen.crucible.common.helpers.SystemHelper;

import java.io.File;
import java.io.IOException;

public class MarkdownCreator
{
    protected void markdownCreationCommon(String fileNamePath, String content) throws IOException
    {

        markdownCreationCommon(fileNamePath,content,SystemHelper.getApplicationSetting(
                FrameworkBrowserConstants.DOCUMENTATION_DIRECTORY));
    }

    protected void markdownCreationCommon(String fileNamePath, String content, String documentationDirectory) throws IOException
    {
        FileHelper.ensureDirectoryExists(documentationDirectory);
        if(!fileNamePath.endsWith(".md"))
        {
            fileNamePath += ".md";
        }

        FileHelper.writeTextToDisk(content,fileNamePath);
    }

    public void fillMarkdownFileWithTemplate(File file, String templateName, String templateFileName) throws IOException
    {
        String templateContent = JsonHelper.getJSONFileContent(templateFileName)
                .getJSONObject(templateName).getString("content");
        FileHelper.writeTextToDisk(templateContent, file.getPath());
    }

    public void createFileFromTemplate(File file, String templateName, String templateFileName) throws IOException
    {
        file.createNewFile();
        fillMarkdownFileWithTemplate(file,templateName,templateFileName);
    }

    public String heading(String text){
        return heading(1, text);
    }

    public String heading(int level, String text){
        if(level < 1){
            level = 1;
        } else if (level > 6){
            level = 6;
        }

        StringBuilder headingText = new StringBuilder();
        for (int x = 0; x < level; x++){
            headingText.append("#");
        }
        headingText.append(" ").append(text);
        int headingLineLength = text.length();

        headingText.append("\n");

        return headingText.toString();
    }

    public String bold(String text){
        return "**" + text + "**";
    }
}
