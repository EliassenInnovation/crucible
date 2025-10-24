package com.eliassen.crucible.core.sharedobjects;

import com.eliassen.crucible.core.helpers.Logger;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * Represents an API request with its associated metadata.
 */
public class ApiRequest {

    /**
     * The HTTP method type of the request (e.g., GET, POST, PUT, DELETE).
     */
    public String methodType;

    /**
     * The URL of the API endpoint.
     */
    public String url;

    /**
     * The headers associated with the request.
     */
    public Headers headers;

    /**
     * The parameters associated with the request.
     */
    public Parameters parameters;

    /**
     * The JSON payload of the request, if applicable.
     */
    public JSONObject jsonPayload;

    /**
     * The string payload of the request, if applicable.
     */
    public String stringPayload;

    /**
     * A flag indicating whether the request has query parameters.
     */
    private boolean queryParameters;

    /**
     * An enum indicating whether API logging should be prevented.
     */
    private PreventAPILogging preventAPILogging;

    /**
     * Retrieves the prevent API logging setting.
     *
     * @return The prevent API logging setting, defaulting to VALUE_NOT_SET if not set.
     */
    public PreventAPILogging getPreventApiLogging() {
        if (this.preventAPILogging == null) {
            this.preventAPILogging = PreventAPILogging.VALUE_NOT_SET;
        }
        return this.preventAPILogging;
    }

    /**
     * Constructs a new ApiRequest instance with the specified parameters.
     *
     * @param methodType        The HTTP method type.
     * @param url               The URL of the API endpoint.
     * @param headers           The headers associated with the request.
     * @param jsonPayload       The JSON payload of the request.
     * @param parameters        The parameters associated with the request.
     * @param preventAPILogging The prevent API logging setting.
     * @param stringPayload     The string payload of the request.
     */
    public ApiRequest(String methodType, String url, Headers headers, JSONObject jsonPayload, Parameters parameters,
                      PreventAPILogging preventAPILogging, String stringPayload) {
        this.methodType = methodType;
        this.url = url;
        this.headers = headers;
        this.jsonPayload = jsonPayload;
        this.parameters = parameters;
        this.preventAPILogging = preventAPILogging;
        this.stringPayload = stringPayload;
    }

    /**
     * Sets whether the request has form parameters.
     *
     * @param hasFormParameters True if the request has form parameters, false otherwise.
     */
    public void setHasFormParameters(boolean hasFormParameters) {
        queryParameters = !hasFormParameters;
    }

    /**
     * Sets whether the request has query parameters.
     *
     * @param hasQueryParameters True if the request has query parameters, false otherwise.
     */
    public void setHasQueryParameters(boolean hasQueryParameters) {
        queryParameters = hasQueryParameters;
    }

    /**
     * Checks whether the request has query parameters.
     *
     * @return True if the request has query parameters, false otherwise.
     */
    public boolean hasQueryParameters() {
        return queryParameters;
    }

    /**
     * Checks whether the request has form parameters.
     *
     * @return True if the request has form parameters, false otherwise.
     */
    public boolean hasFormParameters() {
        return !queryParameters;
    }

    /**
     * Sets the HTTP method type of the request.
     *
     * @param methodType The new HTTP method type.
     */
    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    /**
     * Sets the Content-Length header based on the JSON payload.
     */
    public void setContentLength() {
        byte[] bytes = this.jsonPayload.toString().getBytes(StandardCharsets.UTF_8);
        Logger.log("Content length: " + bytes.length);
        this.headers.put("Content-Length", Integer.toString(bytes.length));
    }

    /**
     * Checks whether the request has a payload (either JSON or string).
     *
     * @return True if the request has a payload, false otherwise.
     */
    public boolean hasPayload() {
        if (jsonPayload != null || (stringPayload != null && !stringPayload.isEmpty())) {
            return true;
        }
        return false;
    }

    /**
     * Retrieves the payload as a string.
     *
     * @return The payload as a string, or null if no payload exists.
     */
    public String getPayloadString() {
        String payloadString = null;

        if (hasPayload()) {
            if (jsonPayload != null) {
                payloadString = jsonPayload.toString();
            } else {
                payloadString = stringPayload;
            }
        }

        return payloadString;
    }
}
