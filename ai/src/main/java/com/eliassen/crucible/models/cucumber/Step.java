package com.eliassen.crucible.models.cucumber;

import lombok.Data;

import java.util.List;

@Data
public class Step {
    private Result result;
    private int line;
    private String name;
    private Match match;
    private String keyword;
    private List<String> output;
    private List<Embedding> embeddings;
}
