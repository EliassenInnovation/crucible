package com.eliassen.crucible.models.cucumber;

import lombok.Data;

import java.math.BigInteger;

@Data
public class Result {
    private BigInteger duration;
    private String status;
    private String error_message;
}
