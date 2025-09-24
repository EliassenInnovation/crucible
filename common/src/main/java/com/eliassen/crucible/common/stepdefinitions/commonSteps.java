package com.eliassen.crucible.common.stepdefinitions;

import io.cucumber.java.DataTableType;

public class commonSteps
{
    @DataTableType(replaceWithEmptyString = "[empty]")
    public String stringType(String cell) {
        return cell;
    }
}
