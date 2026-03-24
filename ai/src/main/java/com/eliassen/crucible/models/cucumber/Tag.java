package com.eliassen.crucible.models.cucumber;

import lombok.Data;

@Data
public class Tag {
    private String name;
    private String type;
    private TagLocation location;
}
