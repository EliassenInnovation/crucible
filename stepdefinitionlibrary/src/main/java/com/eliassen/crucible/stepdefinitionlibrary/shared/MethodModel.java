package com.eliassen.crucible.stepdefinitionlibrary.shared;

public class MethodModel
{
    private String fileName;
    private String signature;

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public boolean hasFileName()
    {
        return fileName != null && !this.fileName.isEmpty();
    }

    public String getSignature()
    {
        return signature;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
    }

    public boolean hasSignature()
    {
        return this.signature != null && !this.signature.isEmpty();
    }
}
