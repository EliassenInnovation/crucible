package com.eliassen.crucible.models;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TestCase {
    private List<Object> testActions;
    private int age;
    private String className;
    private double duration;
    private String errorDetails;
    private String errorStackTrace;
    private int failedSince;
    private String name;
    private Map<String, Object> properties;
    private boolean skipped;
    private String skippedMessage;
    private String status;
    private String stderr;
    private String stdout;

}
