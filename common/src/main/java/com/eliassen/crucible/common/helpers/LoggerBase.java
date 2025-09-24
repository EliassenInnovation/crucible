package com.eliassen.crucible.common.helpers;

import org.openqa.selenium.bidi.log.LogLevel;

public class LoggerBase
{
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public LoggerBase()
    {

    }

    public static void log(String message)
    {
        System.out.println(message);
    }

    public static void logError(String message)
    {
        message = decorateMessage(message, LogMessageType.ERROR);
        log(message);
    }

    //TODO finish this
    public static LogLevel getLogLevel(){
        String logLevelName = SystemHelper.getApplicationSetting("LOG_LEVEL");
        return LogLevel.INFO;
    }

    /**
     * Overload
     * Will decorate the message in red
     * @param message
     * @return
     */
    private static String decorateMessage(String message)
    {
        return decorateMessage(message, LogMessageType.ERROR);
    }

    /**
     * decorates the message based on log message type
     * @param message
     * @param logMessageType
     * @return
     */
    private static String decorateMessage(String message, LogMessageType logMessageType)
    {
        switch(logMessageType)
        {
            case ERROR:
                message = ANSI_RED + message + ANSI_RESET;
                break;
        }

        return message;
    }
}
