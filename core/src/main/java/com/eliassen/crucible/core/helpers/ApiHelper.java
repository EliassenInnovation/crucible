/**
 * Provides a set of utility methods for making HTTP requests and handling API responses.
 * This class is used to simplify the process of interacting with APIs and provides a standardized way of handling requests and responses.
 */
package com.eliassen.crucible.core.helpers;

import io.cucumber.datatable.DataTable;
import org.json.JSONArray;
import org.json.JSONObject;
import com.eliassen.crucible.common.helpers.JsonHelper;
import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.core.sharedobjects.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ApiHelper class provides methods for making HTTP requests and handling API responses.
 */
public class ApiHelper
{
    /**
     * Constants for HTTP request headers and methods.
     */
    public static final String AUTHORIZATION = "Authorization";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String GET = "GET";
    public static final String DELETE = "DELETE";
    public static final String NOT_FOUND = "notfound";
    public static final String RESPONSE_CODE = "responsecode";
    public static final String PAYLOAD = "payload";

    /**
     * Sends a GET request to the specified URL.
     * @param request The ApiRequest object containing the request details.
     * @return The ApiResponse object containing the response details.
     * @deprecated Use CallApi method instead.
     */
    @Deprecated
    public static ApiResponse sendGetRequest(ApiRequest request)
    {
        if (request.parameters != null){
            // Nullify parameters to avoid modifying the URL
            request.parameters = null;
        }
        request.setMethodType(GET);
        return sendRequest(request);
    }

    /**
     * Sends a POST request to the specified URL.
     * @param request The ApiRequest object containing the request details.
     * @return The ApiResponse object containing the response details.
     * @deprecated Use CallApi method instead.
     */
    @Deprecated
    public static ApiResponse sendPostRequest(ApiRequest request){
        request.setMethodType(POST);
        return sendRequest(request);
    }

    /**
     * Sends a PUT request to the specified URL.
     * @param request The ApiRequest object containing the request details.
     * @return The ApiResponse object containing the response details.
     * @deprecated Use CallApi method instead.
     */
    @Deprecated
    public static ApiResponse sendPutRequest(ApiRequest request){
        request.setMethodType(PUT);
        return sendRequest(request);
    }

    /**
     * Sends a DELETE request to the specified URL.
     * @param request The ApiRequest object containing the request details.
     * @return The ApiResponse object containing the response details.
     * @deprecated Use CallApi method instead.
     */
    @Deprecated
    public static ApiResponse sendDeleteRequest(ApiRequest request){
        request.setMethodType(DELETE);
        return sendRequest(request);
    }

    /**
     * Sends an HTTP request to the specified URL.
     * @param request The ApiRequest object containing the request details.
     * @return The ApiResponse object containing the response details.
     * @deprecated Use CallApi method instead.
     */
    @Deprecated
    public static ApiResponse sendRequest(ApiRequest request)
    {
        ApiResponse response = null;

        try
        {
            response = sendRequest(request, new URL(request.url));
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Sends an HTTP request to the specified URL.
     * @param request The ApiRequest object containing the request details.
     * @param url The URL to send the request to.
     * @return The ApiResponse object containing the response details.
     */
    @Deprecated
    public static ApiResponse sendRequest(ApiRequest request, URL url)
    {
        HttpURLConnection connection = null;
        try
        {
            // Record the start time of the request
            Instant start = Instant.now();

            // Add query parameters to the URL if present
            if(request.hasQueryParameters() && request.parameters != null && !request.parameters.isEmpty())
            {
                String urlString = url.toString();
                urlString += "/?" + setParams(request.parameters);
                url = new URL(urlString);
            }

            // Open a connection to the URL
            connection = (HttpURLConnection) url.openConnection();

            // Set request properties
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod(request.methodType);

            // Add headers to the request
            for(String key : request.headers.keySet())
            {
                connection.setRequestProperty(key, request.headers.get(key));
            }

            // Write the request payload or form parameters to the output stream
            if(request.hasPayload() || (request.hasFormParameters() && request.parameters != null)) {
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                if (request.parameters != null && request.hasFormParameters()){
                    writer.write(setParams(request.parameters));
                }
                if(request.hasPayload()) {
                    writer.write(request.getPayloadString());
                }
                writer.close();
            }

            // Read the response from the input stream
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer jsonString = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }

            br.close();

            // Create an ApiResponse object from the response
            ApiResponse response = new ApiResponse(connection.getResponseCode(), headerFormat(connection.getHeaderFields()),jsonString.toString());
            connection.disconnect();

            // Record the end time of the request and calculate the execution time
            Instant end = Instant.now();
            response.executionTime = Duration.between(start, end).toMillis();

            return response ;
        }
        catch (IOException i)
        {
            try
            {
                // Handle IOException by returning an ApiResponse with the error code and message
                return new ApiResponse(connection.getResponseCode(),null,connection.getResponseMessage());
            }
            catch (IOException ioException)
            {
                throw new RuntimeException(ioException.getMessage());
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Converts a Parameters object to a URL-encoded string.
     * @param params The Parameters object to be converted.
     * @return The URL-encoded string representation of the Parameters object.
     */
    public static String setParams(Parameters params){
        String paramsString = "";
        for(String key : params.keySet())
        {
            if(!paramsString.equals(""))
            {
                paramsString += "&";
            }

            paramsString += key + "=" + params.get(key);
        }
        return paramsString;
    }

    /**
     * Formats the response headers into a Hashtable.
     * @param headersMap The Map of response headers.
     * @return The Hashtable representation of the response headers.
     */
    public static Hashtable<String, String> headerFormat( Map<String, List<String>> headersMap){
        Hashtable<String, String> headers = new Hashtable<String, String>();
        for(String key: headersMap.keySet()){
            if( key != null) {
                headers.put(key, (headersMap.get(key)).get(0));
            }
        }
        return headers;
    }

    /**
     * Creates a URL for an API request based on the provided ApiInfo object.
     * @param apiInfo The ApiInfo object containing the API details.
     * @return The created API URL.
     */
    public static String createApiUrl(ApiInfo apiInfo)
    {
        String apiUrl = "";

        if(!apiInfo.apiUrl.contains("http"))
        {
            String environmentName = SystemHelper.getCommandLineParameter(SystemHelper.ENVIRONMENT);

            // Append the appName to the environmentName if present
            if(apiInfo.appName != null)
            {
                environmentName += apiInfo.appName;
            }

            // Get the environment URL from the MasterMind object
            String environmentUrl = MasterMind.getPageObjectItem("apps_" + environmentName);

            // Append the API URL to the environment URL
            apiUrl = environmentUrl + apiInfo.apiUrl;
        }
        else
        {
            apiUrl = apiInfo.apiUrl;
        }

        return apiUrl;
    }

    /**
     * Creates a URL for an API request based on the provided API URL string.
     * @param apiPart The API URL string.
     * @return The created API URL.
     * @deprecated Use createApiUrl(ApiInfo) instead.
     */
    @Deprecated
    public static String createApiUrl(String apiPart)
    {
        ApiInfo info = new ApiInfo();
        info.apiUrl = apiPart;
        return createApiUrl(info);
    }

    /**
     * Sends a POST request to the specified URL with the provided payload and headers.
     * @param requestUrl The URL to send the request to.
     * @param payload The payload to be sent with the request.
     * @param headers The headers to be sent with the request.
     * @return The response from the server.
     * @deprecated Use CallApi method instead.
     */
    @Deprecated
    public static String sendPostRequest(String requestUrl, String payload, Headers headers) {
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            Iterator var5 = headers.keySet().iterator();

            while(var5.hasNext()) {
                String key = (String)var5.next();
                connection.setRequestProperty(key, (String)headers.get(key));
            }

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(payload);
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer jsonString = new StringBuffer();

            String line;
            while((line = br.readLine()) != null) {
                jsonString.append(line);
            }

            br.close();
            connection.disconnect();
            return jsonString.toString();
        } catch (Exception var9) {
            throw new RuntimeException(var9.getMessage());
        }
    }

    /**
     * Makes an API call using the provided ApiInfo and Headers objects.
     * @param apiInfo The ApiInfo object containing the API details.
     * @param headers The Headers object containing the request headers.
     * @param urlParameters The URL parameters to be used in the API call.
     * @return The ApiResponse object containing the response details.
     */
    public static ApiResponse CallApi(ApiInfo apiInfo, Headers headers, Object[] urlParameters)
    {
        String apiUrl = createApiUrl(apiInfo);
        if(urlParameters != null)
        {
            apiUrl = String.format(apiUrl, urlParameters);
        }

        ApiRequest request = new ApiRequestBuilder()
                .setMethodType(apiInfo.method)
                .setUrl(apiUrl)
                .setHeaders(headers)
                .build();

        return CallApi(request);
    }

    /**
     * Makes an API call using the provided ApiRequest object.
     * @param request The ApiRequest object containing the request details.
     * @return The ApiResponse object containing the response details.
     */
    public static ApiResponse CallApi(ApiRequest request)
    {
        boolean shouldLogRequest = (MasterMind.getScenario().getSourceTagNames().contains("@logRequest") ||
                Boolean.parseBoolean(SystemHelper.getApplicationSetting("logAllApiRequests"))) &&
                canLogType(request.getPreventApiLogging(), LogType.REQUEST);

        boolean shouldLogResponse = (MasterMind.getScenario().getSourceTagNames().contains("@logResponse") ||
                Boolean.parseBoolean(SystemHelper.getApplicationSetting("logAllApiResponses"))) &&
                canLogType(request.getPreventApiLogging(), LogType.RESPONSE);

        if(shouldLogRequest)
        {
            logRequest(request);
        }

        ApiResponse response = ApiHelper.sendRequest(request);

        MasterMind.storePersisted(RESPONSE_CODE,Integer.toString(response.code));
        MasterMind.store(PAYLOAD, response.payload);

        if(shouldLogResponse)
        {
            logResponse();
        }

        return response;
    }

    /**
     * Checks if a specific log type is allowed based on the PreventAPILogging enum.
     * @param preventAPILogging The PreventAPILogging enum value.
     * @param logType The LogType enum value.
     * @return True if the log type is allowed, false otherwise.
     */
    private static boolean canLogType(PreventAPILogging preventAPILogging, LogType logType)
    {
        if(preventAPILogging.equals(PreventAPILogging.ALLOW_ALL))
        {
            return true;
        }
        else
        {
            switch(logType) {
                case REQUEST:
                    switch (preventAPILogging) {
                        case NOT_REQUEST:
                        case NOT_RESPONSE_AND_NOT_REQUEST:
                            return false;
                        default:
                            return true;
                    }
                case RESPONSE:
                    switch (preventAPILogging) {
                        case NOT_RESPONSE:
                        case NOT_RESPONSE_AND_NOT_REQUEST:
                            return false;
                        default:
                            return true;
                    }
            }
        }

        return false;
    }

    /**
     * Makes an API call using the provided ApiInfo, Headers, and JSONObject payload.
     * @param apiInfo The ApiInfo object containing the API details.
     * @param headers The Headers object containing the request headers.
     * @param payload The JSONObject payload to be sent with the request.
     * @return The ApiResponse object containing the response details.
     */
    public static ApiResponse CallApi(ApiInfo apiInfo, Headers headers, JSONObject payload)
    {
        String apiUrl = createApiUrl(apiInfo);
        ApiRequest request = new ApiRequestBuilder()
                .setMethodType(apiInfo.method)
                .setUrl(apiUrl)
                .setHeaders(headers)
                .setJsonPayload(payload)
                .build();

        return CallApi(request);
    }

    /**
     * Retrieves the payload from the MasterMind storage.
     * @return The JSONObject representation of the payload.
     */
    public static JSONObject getPayloadFromStorage()
    {
        JSONObject response = new JSONObject(MasterMind.retrieve(PAYLOAD));
        return response;
    }

    /**
     * Adds URL parameters to the ApiInfo object based on the provided DataTable.
     * @param info The ApiInfo object to be modified.
     * @param dataTable The DataTable containing the URL parameters.
     * @return The modified ApiInfo object.
     */
    public static ApiInfo addUrlParametersFromDataTable(ApiInfo info, DataTable dataTable)
    {
        Hashtable<String,String> table = TestHelperBase.convertDataTableToHashtable(dataTable);
        String url = info.apiUrl;
        if(!url.endsWith("/") && !url.endsWith("?"))
        {
            url += "?";
        }
        StringBuilder builder = new StringBuilder(url);
        String[] keys = table.keySet().toArray(new String[table.keySet().size()]);
        for(int x = 0; x < keys.length; x++)
        {
            if(x > 0)
            {
                builder.append("&");
            }

            builder.append(keys[x]);
            builder.append("=");
            builder.append(table.get(keys[x]).replace(" ", "%20"));
        }

        info.apiUrl = builder.toString();

        return info;
    }

    /**
     * Logs the request details.
     * @param request The ApiRequest object containing the request details.
     */
    public static void logRequest(ApiRequest request)
    {
        Logger.log("URL: " + request.url);

        if(request.parameters != null)
        {
            Logger.log("Form parameters :" + request.parameters.toString());
        }

        if (request.hasPayload())
        {
            Logger.log("Request Payload: " + JsonHelper.getPrettyJson(request.jsonPayload));
        }
    }

    /**
     * Logs the response details.
     */
    public static void logResponse()
    {
        String payload = MasterMind.retrieve(ApiHelper.PAYLOAD);
        if(payload != null && !payload.isEmpty()) {
            Logger.log("Response Payload:");
            try {
                JSONObject payloadObject = new JSONObject(payload);
                if (payload != null && !payload.isEmpty()) {

                    Logger.log(TestHelperBase.getPrettyJson(payloadObject));
                }
            } catch (org.json.JSONException je) {
                try {
                    JSONArray payloadArray = new JSONArray(payload);
                    if (payload != null && !payload.isEmpty()) {
                        Logger.log(TestHelperBase.getPrettyJson(payloadArray));
                    }
                } catch (org.json.JSONException je2) {
                    Logger.log("Payload is not JSON!");
                    Logger.log(payload);
                }
            }
        }
        else
        {
            if (payload == null) {
                Logger.logError("PAYLOAD IS NULL!!");
            }
            else {
                Logger.logError("PAYLOAD IS EMPTY!");
            }
        }
    }
}
