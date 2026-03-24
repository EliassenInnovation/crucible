package com.eliassen.crucible.models.cucumber;

import lombok.Data;

import java.util.List;

@Data
public class Match {
    private List<Argument> arguments;
    private String location;
}
