package com.eliassen.crucible.models.cucumber;

import lombok.Data;

import java.util.List;

@Data
public class Element {
    private String start_timestamp;
    private int line;
    private String name;
    private String description;
    private String id;
    private String type;
    private String keyword;
    private List<Step> steps;
    private List<Step> before;
    private List<Step> after;
    private List<Tag> tags;
}
