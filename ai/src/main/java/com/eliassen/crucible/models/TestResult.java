package com.eliassen.crucible.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TestResult {
    @JsonProperty("class")
    private String clazz;
    private List<Object> testActions;
    private double duration;
    private String durationString;
    private boolean empty;
    private int failCount;
    private int passCount;
    private int skipCount;
    private List<Suite> suites;

}
