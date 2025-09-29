package com.eliassen.crucible.demo.helpers.api;

import com.eliassen.crucible.core.helpers.ApiHelper;
import com.eliassen.crucible.core.sharedobjects.*;
import com.eliassen.crucible.web.helpers.TestHelper;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import io.cucumber.datatable.DataTable;
import org.json.JSONObject;
import org.w3c.dom.Document;

public class DemoApiHelper
{
    public static final String NOT_FOUND = "notfound";
    public static final String RESPONSE_CODE = "responsecode";
    public static final String PAYLOAD = "payload";
    public static final String API_META_DATA = "apiMetaData.json";
    public static final String URL_PART = "urlPart";
    public static final String METHOD = "method";
    public static final String APP_NAME = "appName";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String TOKEN = "token";
    public static final String AUTHORIZATION = "authorization";
    public static final String GRAB_HEADERS = "grabHeaders";

    private DemoApiHelper(){}

    public static String getAccessToken()
    {
        HttpsTrustManager.allowAllSSL();
        return "";
    }

    public static Headers getHeaders()
    {
        Headers headers = new Headers();

        if(!CurrentPage.retrieve(GRAB_HEADERS).equals("false")) {
            headers.put("Content-Type", "application/json");

            headers.put(AUTHORIZATION, getAccessToken());
        }

        return headers;
    }

    public static ApiInfo getApiInfo(ApiName apiName)
    {
        ApiInfo apiInfo = new ApiInfo();
        JSONObject apiMetaData = new TestHelper().getJSONFileContent(API_META_DATA);
        JSONObject specificApi = apiMetaData.getJSONObject(apiName.toString());
        apiInfo.apiUrl = specificApi.getString(URL_PART);
        apiInfo.method = specificApi.getString(METHOD);
        if(specificApi.has(APP_NAME))
        {
            apiInfo.appName = specificApi.getString(APP_NAME);
        }
        else
        {
            apiInfo.appName = null;
        }

        return apiInfo;
    }

    public static ApiResponse callApi(ApiName apiName)
    {
        return callApi(apiName,null,null);
    }

    public static ApiResponse callApi(ApiName apiName, Object[] urlParameters)
    {
        ApiInfo apiInfo = getApiInfo(apiName);
        Headers headers = getHeaders();

        return ApiHelper.CallApi(apiInfo,headers,urlParameters);
    }

    public static ApiResponse callApi(ApiName apiName, JSONObject payload)
    {
        return callApi(apiName, null, payload);
    }

    public static ApiResponse callApi(ApiName apiName, Object[] urlParameters, JSONObject payload)
    {
        ApiInfo apiInfo = getApiInfo(apiName);
        Headers headers = getHeaders();

        apiInfo.apiUrl = String.format(apiInfo.apiUrl,urlParameters);

        return ApiHelper.CallApi(apiInfo, headers, payload);
    }

    public static ApiResponse callApi(ApiName apiName, DataTable dataTable)
    {
        ApiInfo apiInfo = getApiInfo(apiName);
        apiInfo = ApiHelper.addUrlParametersFromDataTable(apiInfo, dataTable);
        Headers headers = getHeaders();

        apiInfo.apiUrl = ApiHelper.createApiUrl(apiInfo);
        ApiRequest request = new ApiRequestBuilder()
                .setMethodType(apiInfo.method)
                .setUrl(apiInfo.apiUrl)
                .setHeaders(headers)
                .build();

        return ApiHelper.CallApi(request);
    }

    public static ApiResponse callApi(ApiName apiName, String payload)
    {
        ApiInfo apiInfo = getApiInfo(apiName);
        Headers headers = getHeaders();
        apiInfo.apiUrl = ApiHelper.createApiUrl(apiInfo);
        ApiRequest request = new ApiRequestBuilder()
                .setMethodType(apiInfo.method)
                .setUrl(apiInfo.apiUrl)
                .setHeaders(headers)
                .setStringPayload(payload)
                .build();

        return ApiHelper.CallApi(request);
    }

    public static String callApi(ApiName apiName, Document xmlPayload)
    {
        //TODO
        //this assumes it's a POST
        Headers headers = getHeaders();
        ApiInfo apiInfo = getApiInfo(apiName);
        String url = ApiHelper.createApiUrl(apiInfo);
        return ApiHelper.sendPostRequest(url,xmlPayload.toString(), headers);
    }
}
