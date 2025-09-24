package com.eliassen.crucible.stepdefinitionlibrary.shared;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StepDefinitionModel extends MethodModel
{
    private String comment;
    private List<String> matchers;

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public boolean hasComment()
    {
        return this.comment != null && !this.comment.isEmpty();
    }

    public List<String> getMatchers()
    {
        return matchers;
    }

    public void setMatchers(List<String> matchers)
    {
        this.matchers = matchers;
    }

    public void setMatchers(String matchers){
        this.matchers = Arrays.stream(matchers.split("\n")).filter(a -> a.length() > 0).collect(Collectors.toList());
    }

    public boolean hasMatcher()
    {
        return this.matchers != null && !this.matchers.isEmpty();
    }
}
