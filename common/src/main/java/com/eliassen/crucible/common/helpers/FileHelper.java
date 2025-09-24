package com.eliassen.crucible.common.helpers;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;

public class FileHelper {
    public void ExtractFile(String fileName, String permissions, String path) {
        ExtractFile(fileName, permissions, path, path);
    }

    public void ExtractFile(String fileName, String permissions) {
        ExtractFile(fileName, permissions, null, null);
    }

    public void ExtractFile(String fileName, String permissions, String sourcePath, String destinationPath) {
        String sourceURI, destinationURI;

        if (sourcePath != null && !sourcePath.isEmpty()) {
            sourceURI = sourcePath + fileName;
        } else {
            sourceURI = fileName;
        }

        if (destinationPath != null && !destinationPath.isEmpty()) {
            destinationURI = destinationPath + fileName;
        } else {
            destinationURI = fileName;
        }

        File newFile = new File(destinationURI);

        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(sourceURI);
            FileUtils.copyInputStreamToFile(in, newFile);

            if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) {
                Process process = Runtime.getRuntime().exec("chmod " + permissions + " " + destinationURI);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTextFileContent(String filePath) {
        String fileContent = null;
        try {
            FileInputStream fish = new FileInputStream(filePath);

            fileContent = new String(fish.readAllBytes());
        } catch (FileNotFoundException f) {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(filePath);
            try {
                fileContent = new String(in.readAllBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return fileContent;
    }

    public static boolean ensureDirectoryExists(String directoryName) {
        boolean directoryExists = true;
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directoryExists = directory.mkdir();
        }
        return directoryExists;
    }

    public static void writeTextToDisk(String text, String filePath) throws IOException {
        try (FileWriter fileWriter = new FileWriter(filePath)){
            fileWriter.write(text);
        }
    }
}
