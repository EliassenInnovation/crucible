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

public class ApiHelper
{
    public static final String AUTHORIZATION = "Authorization";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String GET = "GET";
    public static final String DELETE = "DELETE";
    public static final String NOT_FOUND = "notfound";
    public static final String RESPONSE_CODE = "responsecode";
    public static final String PAYLOAD = "payload";

    @Deprecated
    public static ApiResponse sendGetRequest(ApiRequest request)
    {
        if (request.parameters != null){
            //edit the url

            //null parameters
            request.parameters = null;
        }
        request.setMethodType(GET);
        return sendRequest(request);
    }

    @Deprecated
    public static ApiResponse sendPostRequest(ApiRequest request){
        request.setMethodType(POST);
        return sendRequest(request);
    }

    @Deprecated
    public static ApiResponse sendPutRequest(ApiRequest request){
        request.setMethodType(PUT);
        return sendRequest(request);
    }

    @Deprecated
    public static ApiResponse sendDeleteRequest(ApiRequest request){
        request.setMethodType(DELETE);
        return sendRequest(request);
    }

    //allows for unit testing
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

    @Deprecated
    public static ApiResponse sendRequest(ApiRequest request, URL url)
    {
        HttpURLConnection connection = null;
        try
        {
            Instant start = Instant.now();

            if(request.hasQueryParameters() && request.parameters != null && !request.parameters.isEmpty())
            {
                String urlString = url.toString();
                urlString += "/?" + setParams(request.parameters);
                url = new URL(urlString);
            }

            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod(request.methodType);

            for(String key : request.headers.keySet())
            {
                connection.setRequestProperty(key, request.headers.get(key));
            }

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

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer jsonString = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }

            br.close();
            ApiResponse response = new ApiResponse(connection.getResponseCode(), headerFormat(connection.getHeaderFields()),jsonString.toString());
            connection.disconnect();
            Instant end = Instant.now();
            response.executionTime = Duration.between(start, end).toMillis();
            return response ;
        }
        catch (IOException i)
        {
            try
            {
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

    public static Hashtable<String, String> headerFormat( Map<String, List<String>> headersMap){
        Hashtable<String, String> headers = new Hashtable<String, String>();
            for(String key: headersMap.keySet()){
                if( key != null) {
                    headers.put(key, (headersMap.get(key)).get(0));
                }
            }
        return headers;
    }

    /*
    For backward compatibility
     */
    public static String createApiUrl(String apiPart)
    {
        ApiInfo info = new ApiInfo();
        info.apiUrl = apiPart;
        return createApiUrl(info);
    }

    public static String createApiUrl(ApiInfo apiInfo)
    {
        String apiUrl = "";

        if(!apiInfo.apiUrl.contains("http"))
        {
            String environmentName = SystemHelper.getCommandLineParameter(SystemHelper.ENVIRONMENT);

            //necessary for more complex environments
            if(apiInfo.appName != null)
            {
                environmentName += apiInfo.appName;
            }

            String environmentUrl = MasterMind.getPageObjectItem("apps_" + environmentName);

            apiUrl = environmentUrl + apiInfo.apiUrl;
        }
        else
        {
            apiUrl = apiInfo.apiUrl;
        }

        return apiUrl;
    }

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

    public static JSONObject getPayloadFromStorage()
    {
        JSONObject response = new JSONObject(MasterMind.retrieve(PAYLOAD));
        return response;
    }

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
