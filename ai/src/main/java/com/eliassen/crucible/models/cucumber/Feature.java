package com.eliassen.crucible.models.cucumber;

import lombok.Data;

import java.util.List;

@Data
public class Feature {
    private int line;
    private List<Element> elements;
    private String name;
    private String description;
    private String id;
    private String keyword;
    private String uri;
    private List<Tag> tags;
}
