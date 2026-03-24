package com.elassen.crucible.ai.tests;

import com.eliassen.crucible.core.helpers.ApiHelper;
import com.eliassen.crucible.core.sharedobjects.ApiRequest;
import com.eliassen.crucible.core.sharedobjects.ApiRequestBuilder;
import com.eliassen.crucible.core.sharedobjects.ApiResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class JenkinsConnectTests {
    final static URI JENKINS_URI = URI.create("http://10.152.2.12:8080");
    final static String API_JSON = "/api/json";
    final static String FOLDER_CLASS = "com.cloudbees.hudson.plugins.folder.Folder";
    final static String JOB_API_TEMPLATE = "/job/%s";

    ApiRequest apiRequest;
    ApiResponse apiResponse;

    @BeforeEach
    public void init(){
        apiRequest = new ApiRequestBuilder()
                .setMethodType(ApiHelper.GET)
                .setUrl(JENKINS_URI + API_JSON)
                .build();

        apiResponse = ApiHelper.callApi(apiRequest);
    }

    @Test
    public void canSeeJenkins() {
        assertNotNull(apiResponse);
    }

    @Test
    public void canSeeFolders(){
        List<String> folderNames = getFolderNamesFromResponse(apiResponse);

        assertTrue(folderNames.size() > 0);
    }

    @Test
    public void canGetToWebJobs(){
        List<String> folderNames = getFolderNamesFromResponse(apiResponse);
        String WEB = "web";
        if(folderNames.contains(WEB)){
            apiRequest.url = JENKINS_URI + String.format(JOB_API_TEMPLATE,"web") + API_JSON;
            apiResponse = ApiHelper.callApi(apiRequest);
            assertNotNull(apiResponse.getJSONPayload());
        } else {
            fail("No web folder at this level");
        }
    }

    @Test
    public void canSeeJobsInWeb(){
        List<String> folderNames = getFolderNamesFromResponse(apiResponse);
        String WEB = "web";
        if(folderNames.contains(WEB)){
            apiRequest.url = JENKINS_URI + String.format(JOB_API_TEMPLATE,"web") + API_JSON;
            apiResponse = ApiHelper.callApi(apiRequest);
            JSONObject payload = apiResponse.getJSONPayload();
            

        } else {
            fail("No web folder at this level");
        }
    }

    @Test
    public void canSeeBuildArtifacts(){

    }



    public List<String> getFolderNamesFromResponse(ApiResponse apiResponse){
        JSONArray jobs = apiResponse.getJSONPayload().getJSONArray("jobs");

        List<String> folderNames = IntStream.range(0, jobs.length())
                .mapToObj(i -> {
                    JSONObject jsonObject = jobs.optJSONObject(i);
                    if (jsonObject != null && jsonObject.has("_class")
                            && jsonObject.getString("_class").equals(FOLDER_CLASS)) {
                        return jsonObject.getString("name");
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return folderNames;
    }
}
