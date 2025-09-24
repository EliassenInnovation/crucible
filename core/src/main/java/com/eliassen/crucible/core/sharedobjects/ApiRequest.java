package com.eliassen.crucible.core.sharedobjects;

import com.eliassen.crucible.core.helpers.Logger;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class ApiRequest
{
    public String methodType;
    public String url;
    public Headers headers;
    public Parameters parameters;
    public JSONObject jsonPayload;
    public String stringPayload;
    private boolean queryParameters;
    private PreventAPILogging preventAPILogging;

    public PreventAPILogging getPreventApiLogging()
    {
        if(this.preventAPILogging == null)
        {
            this.preventAPILogging = PreventAPILogging.VALUE_NOT_SET;
        }
        return this.preventAPILogging;
    }

    public ApiRequest(String methodType, String url, Headers headers, JSONObject jsonPayload, Parameters parameters,
                      PreventAPILogging preventAPILogging, String stringPayload)
    {
        this.methodType = methodType;
        this.url = url;
        this.headers = headers;
        this.jsonPayload = jsonPayload;
        this.parameters = parameters;
        this.preventAPILogging = preventAPILogging;
        this.stringPayload = stringPayload;
    }

    public void setHasFormParameters(boolean hasFormParameters){
        queryParameters = !hasFormParameters;
    }

    public void setHasQueryParameters(boolean hasQueryParameters){
        queryParameters = hasQueryParameters;
    }

    public boolean hasQueryParameters(){
        return queryParameters;
    }

    public boolean hasFormParameters(){
        return !queryParameters;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public void setContentLength(){
        byte[] bytes = this.jsonPayload.toString().getBytes(StandardCharsets.UTF_8);
        Logger.log("Content length: " + bytes.length);
        this.headers.put("Content-Length", Integer.toString(bytes.length));
    }

    public boolean hasPayload()
    {
        if(jsonPayload != null || (stringPayload != null && !stringPayload.isEmpty()))
        {
            return true;
        }
        return false;
    }

    public String getPayloadString()
    {
        String payloadString = null;

        if(hasPayload())
        {
            if(jsonPayload != null)
            {
                payloadString = jsonPayload.toString();
            }
            else
            {
                payloadString = stringPayload;
            }
        }

        return payloadString;
    }
}
