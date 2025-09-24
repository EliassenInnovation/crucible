package com.eliassen.crucible.frameworkbrowser.shared;

public class Parser implements Comparable<Parser>
{
    private String parserName;
    private String expression;
    private String className;
    private String alternateClassName;
    private int priority;
    boolean enabled = true;
    private boolean interiorMatch = false;
    private boolean alternateFirstRow = false;
    private boolean overrideLaterClasses = false;

    private boolean pastFirstRow = false;

    public boolean isPastFirstRow()
    {
        return pastFirstRow;
    }

    public void setPastFirstRow(boolean pastFirstRow)
    {
        this.pastFirstRow = pastFirstRow;
    }

    public boolean isAlternateFirstRow()
    {
        return alternateFirstRow;
    }

    public void setAlternateFirstRow(boolean alternateFirstRow)
    {
        this.alternateFirstRow = alternateFirstRow;
    }

    public boolean isInteriorMatch()
    {
        return interiorMatch;
    }

    public void setInteriorMatch(boolean interiorMatch)
    {
        this.interiorMatch = interiorMatch;
    }

    public Parser (){}

    public Parser(String expression, String className)
    {
        this.expression = expression;
        this.className = className;
    }

    public Parser(String expression, String className, String alternateClassName)
    {
        this(expression, className);
        this.alternateClassName = alternateClassName;
    }

    public Parser(String expression, String className, String alternateClassName, boolean isInteriorMatch)
    {
        this(expression, className,alternateClassName);
        this.interiorMatch = isInteriorMatch;
    }

    public Parser(String expression, String className, boolean isInteriorMatch)
    {
        this(expression, className);
        this.interiorMatch = isInteriorMatch;
    }

    public String getExpression()
    {
        return expression;
    }

    public void setExpression(String expression)
    {
        this.expression = expression;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getAlternateClassName()
    {
        return alternateClassName;
    }

    public void setAlternateClassName(String alternateClassName)
    {
        this.alternateClassName = alternateClassName;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getParserName()
    {
        return parserName;
    }

    public void setParserName(String parserName)
    {
        this.parserName = parserName;
    }

    public boolean isOverrideLaterClasses() {
        return overrideLaterClasses;
    }

    public void setOverrideLaterClasses(boolean overrideLaterClasses) {
        this.overrideLaterClasses = overrideLaterClasses;
    }

    @Override
    public int compareTo(Parser other)
    {
        if(this.getPriority() < other.getPriority())
        {
            return -1;
        }
        else if (this.getPriority() > other.getPriority())
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
}

