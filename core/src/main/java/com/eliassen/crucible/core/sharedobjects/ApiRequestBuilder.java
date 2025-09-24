package com.eliassen.crucible.core.sharedobjects;

import org.json.JSONObject;

public class ApiRequestBuilder
{
    private String methodType;
    private String url;
    private Headers headers;
    private PreventAPILogging preventAPILogging;
    private JSONObject jsonPayload;
    private Parameters parameters;
    private String stringPayload;

    public ApiRequestBuilder setMethodType(String methodType)
    {
        this.methodType = methodType;
        return this;
    }

    public ApiRequestBuilder setUrl(String url)
    {
        this.url = url;
        return this;
    }

    public ApiRequestBuilder setHeaders(Headers headers)
    {
        this.headers = headers;
        return this;
    }

    public ApiRequestBuilder setPreventAPILogging(PreventAPILogging preventAPILogging)
    {
        this.preventAPILogging = preventAPILogging;
        return this;
    }

    public ApiRequestBuilder setJsonPayload(JSONObject jsonPayload)
    {
        this.jsonPayload = jsonPayload;
        return this;
    }

    public ApiRequestBuilder setParameters(Parameters parameters)
    {
        this.parameters = parameters;
        return this;
    }

    public ApiRequestBuilder setStringPayload(String stringPayload)
    {
        this.stringPayload = stringPayload;
        return this;
    }

    public ApiRequest build()
    {
        return new ApiRequest(methodType, url, headers, jsonPayload, parameters, preventAPILogging, stringPayload);
    }
}