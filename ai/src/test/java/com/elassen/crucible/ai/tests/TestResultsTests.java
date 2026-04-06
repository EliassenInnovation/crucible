package com.elassen.crucible.ai.tests;

import com.eliassen.crucible.core.helpers.ApiHelper;
import com.eliassen.crucible.core.sharedobjects.ApiRequest;
import com.eliassen.crucible.core.sharedobjects.ApiRequestBuilder;
import com.eliassen.crucible.core.sharedobjects.ApiResponse;
import com.eliassen.crucible.core.sharedobjects.Parameters;
import com.eliassen.crucible.models.Suite;
import com.eliassen.crucible.models.TestCase;
import com.eliassen.crucible.models.TestResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Ignore("Needs AWS token set")
public class TestResultsTests {
    final static URI DASHBOARD_URI = URI.create("http://localhost:5000/home/GetJunitTestResult");
    final static String API_JSON = "/api/json";
    final static String FOLDER_CLASS = "com.cloudbees.hudson.plugins.folder.Folder";
    final static String JOB_API_TEMPLATE = "/job/%s";

    ApiRequest apiRequest;
    ApiResponse apiResponse;
    ObjectMapper objectMapper;

    @BeforeEach
    public void init() {
        Parameters parameters = new Parameters();
        parameters.put("jobName", "/web/Account_Information/Account_Information");
        parameters.put("buildNumber", "515");

        objectMapper = new ObjectMapper();

        apiRequest = new ApiRequestBuilder()
                .setMethodType(ApiHelper.POST)
                .setUrl(DASHBOARD_URI.toString())
                .setParameters(parameters)
                .build();

        apiResponse = ApiHelper.callApi(apiRequest);
    }

    @Test
    public void canGetJunitResultsFromDashboard() {
        assertNotNull(apiResponse);
        assertTrue(apiResponse.code == 200);
    }

    @Test
    public void getTestResultObjectBack() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject payload = new JSONObject(apiResponse.payload);
        TestResult testResult = objectMapper.readValue(apiResponse.payload, TestResult.class);
        assertNotNull(testResult);
    }

    @Test
    public void getCorrectNumberOfFailures() throws JsonProcessingException {
        JSONObject payload = new JSONObject(apiResponse.payload);
        TestResult testResult = objectMapper.readValue(apiResponse.payload, TestResult.class);
        List<Suite> suites = testResult.getSuites();
        int failedCount = testResult.getFailCount();
        int failedSum = 0;

        for (Suite suite : suites) {
            failedSum += suite.getCases().stream()
                    .filter(testCase -> "FAILED".equals(testCase.getStatus()))
                    .count();
        }
        assertEquals(failedCount, failedSum);
    }

    @Test
    public void grabAllFailedReasonsAndStackTraces() throws JsonProcessingException {
        JSONObject payload = new JSONObject(apiResponse.payload);
        TestResult testResult = objectMapper.readValue(apiResponse.payload, TestResult.class);
        List<Suite> suites = testResult.getSuites();
        List<TestCase> failedCases = new ArrayList<>();
        for (Suite suite : suites) {
            failedCases.addAll(suite.getCases().stream()
                    .filter(testCase -> "FAILED".equals(testCase.getStatus()))
                    .toList());
        }

        assertEquals(testResult.getFailCount(), failedCases.size());
    }


}
