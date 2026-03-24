package com.eliassen.crucible.models;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Suite {
    private List<TestCase> cases;
}
