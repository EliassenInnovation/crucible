package com.eliassen.crucible.models.cucumber;

import lombok.Data;

@Data
public class TagLocation {
    private int line;
    private int column;
}
