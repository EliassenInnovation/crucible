package com.eliassen.crucible.core.helpers;

import com.eliassen.crucible.common.helpers.LoggerBase;
import com.eliassen.crucible.core.sharedobjects.MasterMind;
import org.openqa.selenium.bidi.log.LogLevel;

public class Logger extends LoggerBase
{
    public Logger()
    {

    }

    public static void log(String message)
    {
        log(message,LogLevel.INFO);
    }

    //TODO We should always log to the Scenario
    @Deprecated(since = "2/14/2025",forRemoval = true)
    public static void log(String message, boolean logToScenario)
    {
        if(MasterMind.getScenario() != null && logToScenario)
        {
            MasterMind.getScenario().log(message);
        }
        else
        {
            System.out.println(message);
        }
    }

    //TODO incorporate LogLevel
    public static void log(String message, LogLevel logLevel){
        if(MasterMind.getScenario() != null)
        {
            MasterMind.getScenario().log(message);
        }
        else
        {
            System.out.println(message);
        }
    }
}
