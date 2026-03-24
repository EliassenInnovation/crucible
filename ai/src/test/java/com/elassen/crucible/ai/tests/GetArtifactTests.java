package com.elassen.crucible.ai.tests;

import com.eliassen.crucible.core.helpers.ApiHelper;
import com.eliassen.crucible.core.sharedobjects.ApiRequest;
import com.eliassen.crucible.core.sharedobjects.ApiRequestBuilder;
import com.eliassen.crucible.core.sharedobjects.ApiResponse;
import com.eliassen.crucible.core.sharedobjects.Headers;
import com.eliassen.crucible.models.cucumber.Cucumber;
import com.eliassen.crucible.models.cucumber.Element;
import com.eliassen.crucible.models.cucumber.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class GetArtifactTests {
    final static URI CUCUMBER_JSON_URI = URI.create("http://10.152.2.12:8080/job/web/job/Account_Information/job/Account_Information/ws/cucumber-reports/cucumber.json");
    final static String AUTHORIZATION_TOKEN = "Basic bWJhcm5hOjExZWE3YjJmYjQ3ZTRiZWFkMzAzOWI1NDI1ZDc5NzA3Njk=";
    final static String USERNAME = "mbarna";

    ApiRequest apiRequest;
    ApiResponse apiResponse;
    ObjectMapper objectMapper;

    @BeforeEach
    public void init() {
        objectMapper = new ObjectMapper();

        Headers headers = new Headers();
        headers.put("Authorization",AUTHORIZATION_TOKEN);
        headers.put("username",USERNAME);

        apiRequest = new ApiRequestBuilder()
                .setMethodType(ApiHelper.GET)
                .setUrl(CUCUMBER_JSON_URI.toString())
                .setHeaders(headers)
                .build();

        apiResponse = ApiHelper.callApi(apiRequest);
    }

    @Test
    public void canSeeJenkins() {
        assertNotNull(apiResponse);
    }

    @Test
    public void canGetJson() {
        assertTrue(apiResponse.payload.length() > 0);
    }

    @Test
    public void canTurnJsonIntoCucumberClass() throws JsonProcessingException {
        Cucumber cucumber = objectMapper.readValue(apiResponse.payload, Cucumber.class);
        assertNotNull(cucumber);
    }

    @Test
    public void canFindTheFailureInCucumberJson() throws JsonProcessingException {
        Cucumber cucumber = objectMapper.readValue(apiResponse.payload, Cucumber.class);
        Map<String, Map<Feature, List<Element>>> failedFeaturesMap = cucumber.stream()
                .<Map.Entry<String, Map<Feature, List<Element>>>>map(feature -> {
                    List<Element> failedElements = feature.getElements().stream()
                            .filter(element ->
                                    element.getSteps().stream()
                                            .anyMatch(step -> "failed".equals(step.getResult().getStatus())) ||
                                            element.getBefore().stream()
                                                    .anyMatch(hook -> "failed".equals(hook.getResult().getStatus())) ||
                                            element.getAfter().stream()
                                                    .anyMatch(hook -> "failed".equals(hook.getResult().getStatus()))
                            )
                            .collect(Collectors.toList());

                    Map<Feature, List<Element>> featureMap = failedElements.isEmpty()
                            ? Collections.emptyMap()
                            : Collections.singletonMap(feature, failedElements);

                    return new AbstractMap.SimpleEntry<>(feature.getName(), featureMap);
                })
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (map1, map2) -> {
                            Map<Feature, List<Element>> merged = new HashMap<>(map1);
                            merged.putAll(map2);
                            return merged;
                        },
                        LinkedHashMap::new
                ));

        assertNotNull(failedFeaturesMap);
    }

}
