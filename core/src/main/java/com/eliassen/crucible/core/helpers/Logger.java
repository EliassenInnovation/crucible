/**
 * Logger class for logging messages in the Crucible framework.
 * Extends LoggerBase to inherit common logging functionality.
 */
package com.eliassen.crucible.core.helpers;

import com.eliassen.crucible.common.helpers.LoggerBase;
import com.eliassen.crucible.core.sharedobjects.MasterMind;
import org.openqa.selenium.bidi.log.LogLevel;

/**
 * Logger class for logging messages.
 */
public class Logger extends LoggerBase {

    /**
     * Default constructor for Logger.
     */
    public Logger() {}

    /**
     * Logs a message at the INFO level.
     * @param message The message to be logged.
     */
    public static void log(String message) {
        log(message, LogLevel.INFO);
    }

    /**
     * @deprecated Use log(String message) instead. This method will be removed on 2/14/2025.
     * Logs a message to the scenario if logToScenario is true.
     * @param message The message to be logged.
     * @param logToScenario Whether to log the message to the scenario.
     */
    @Deprecated(since = "2/14/2025", forRemoval = true)
    public static void log(String message, boolean logToScenario) {
        if (MasterMind.getScenario() != null && logToScenario) {
            MasterMind.getScenario().log(message);
        } else {
            System.out.println(message);
        }
    }

    /**
     * Logs a message at the specified log level.
     * TODO: Incorporate LogLevel into the logging mechanism.
     * @param message The message to be logged.
     * @param logLevel The log level of the message.
     */
    public static void log(String message, LogLevel logLevel) {
        if (MasterMind.getScenario() != null) {
            MasterMind.getScenario().log(message);
        } else {
            System.out.println(message);
        }
    }
}
