/**
 * Logger class for logging messages in the Crucible DB framework.
 * Extends LoggerBase to inherit common logging functionality.
 */
package com.eliassen.crucible.db.helpers;

import com.eliassen.crucible.common.helpers.LoggerBase;
import com.eliassen.crucible.db.main.CentralCommand;

/**
 * Logger class for logging messages.
 */
public class Logger extends LoggerBase {

    /**
     * Default constructor for Logger.
     */
    public Logger() {}

    /**
     * Logs a message to the scenario if it exists, otherwise to the console.
     * @param message The message to be logged.
     */
    public static void log(String message) {
        if (CentralCommand.getScenario() != null) {
            CentralCommand.getScenario().log(message);
        } else {
            System.out.println(message);
        }
    }

    /**
     * Logs a message to the scenario if logToScenario is true and the scenario exists, otherwise to the console.
     * @param message The message to be logged.
     * @param logToScenario Whether to log the message to the scenario.
     */
    public static void log(String message, boolean logToScenario) {
        if (CentralCommand.getScenario() != null && logToScenario) {
            CentralCommand.getScenario().log(message);
        } else {
            System.out.println(message);
        }
    }
}
