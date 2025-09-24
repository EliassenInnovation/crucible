package com.eliassen.crucible.db.helpers;

import com.eliassen.crucible.common.helpers.LoggerBase;
import com.eliassen.crucible.db.main.CentralCommand;

public class Logger extends LoggerBase
{
    public Logger()
    {

    }

    public static void log(String message)
    {
        if(CentralCommand.getScenario() != null)
        {
            CentralCommand.getScenario().log(message);
        }
        else
        {
            System.out.println(message);
        }
    }

    public static void log(String message, boolean logToScenario)
    {
        if(CentralCommand.getScenario() != null && logToScenario)
        {
            CentralCommand.getScenario().log(message);
        }
        else
        {
            System.out.println(message);
        }
    }
}
