package com.eliassen.crucible.web.drivers;

import com.eliassen.crucible.core.helpers.Logger;
import com.eliassen.crucible.common.helpers.SystemHelper;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class WaitManager {
    private WaitManager(){}

    private static boolean logInvalidWaitTimes = false;

    private static Double globalExplicitWait = null;
    private static Double defaultExplicitWaitTime = null;
    private static Double clickableExplicitWait = null;
    private static Double visibleExplicitWait = null;
    private static Double presenceExplicitWait = null;
    private static Double textExplicitWait = null;
    private static Double attributeExplicitWait = null;
    private static Double frameExplicitWait = null;
    private static Double windowExplicitWait = null;
    private static Double staleElementExplicitWait = null;
    private static Double implicitWait = null;
    private static Double pageLoadTimeout = null;
    private static Double pollingIntervalTime = null;

    // Constants for settings
    public static final String GLOBAL_EXPLICIT_WAIT = "GLOBAL_EXPLICIT_WAIT";
    public static final String DEFAULT_EXPLICIT_WAIT = "DEFAULT_EXPLICIT_WAIT";
    public static final String CLICKABLE_EXPLICIT_WAIT = "CLICKABLE_EXPLICIT_WAIT";
    public static final String VISIBLE_EXPLICIT_WAIT = "VISIBLE_EXPLICIT_WAIT";
    public static final String PRESENCE_EXPLICIT_WAIT = "PRESENCE_EXPLICIT_WAIT";
    public static final String TEXT_EXPLICIT_WAIT = "TEXT_EXPLICIT_WAIT";
    public static final String ATTRIBUTE_EXPLICIT_WAIT = "ATTRIBUTE_EXPLICIT_WAIT";
    public static final String FRAME_EXPLICIT_WAIT = "FRAME_EXPLICIT_WAIT";
    public static final String WINDOW_EXPLICIT_WAIT = "WINDOW_EXPLICIT_WAIT";
    public static final String STALE_ELEMENT_EXPLICIT_WAIT = "STALE_ELEMENT_EXPLICIT_WAIT";
    public static final String IMPLICIT_WAIT = "IMPLICIT_WAIT";
    public static final String PAGE_LOAD_TIMEOUT = "PAGE_LOAD_TIMEOUT";
    public static final String POLLING_INTERVAL = "POLLING_INTERVAL";

    // Default values (stored in seconds)
    private static final Double DEFAULT_EXPLICIT_WAIT_VALUE = 15d;
    private static final Double IMPLICIT_WAIT_DEFAULT = 5d;
    private static final Double PAGE_LOAD_TIMEOUT_DEFAULT = 5d;
    private static final Double POLLING_INTERVAL_DEFAULT = .1d;
    private static final Double READYZ_WAIT_TIME_DEFAULT_VALUE = 15d;

    /**
     * Validates a wait time value and logs an error if invalid.
     *
     * @param settingName The name of the setting being validated.
     * @param value The provided wait time value.
     * @param defaultValue The default value to use if the provided value is invalid.
     * @return The validated wait time value.
     */
    private static double validateAndLog(String settingName, Double value, double defaultValue) {
        if (value != null && value >= 0) {
            return value;
        } else {
            //only log if I set the logging value to true
            if(logInvalidWaitTimes) {
                Logger.logError(String.format("Invalid wait time for %s: %s. Using default value: %s",
                        settingName, value == null ? "null" : value, defaultValue));
            }
            return defaultValue;
        }
    }

    public static double getDefaultExplicitWaitTime() {
        if (defaultExplicitWaitTime == null) {
            Double setting = SystemHelper.getApplicationSettingDouble(DEFAULT_EXPLICIT_WAIT);
            defaultExplicitWaitTime = validateAndLog(DEFAULT_EXPLICIT_WAIT, setting, DEFAULT_EXPLICIT_WAIT_VALUE);
        }
        return defaultExplicitWaitTime;
    }

    public static void setDefaultExplicitWaitTime(double defaultExplicitWaitTime) {
        WaitManager.defaultExplicitWaitTime = validateAndLog(DEFAULT_EXPLICIT_WAIT, defaultExplicitWaitTime, DEFAULT_EXPLICIT_WAIT_VALUE);
    }

    // Global Explicit Wait (Uses Default Explicit Wait as Fallback)
    public static double getGlobalExplicitWait() {
        if (globalExplicitWait == null) {
            Double setting = SystemHelper.getApplicationSettingDouble(GLOBAL_EXPLICIT_WAIT);
            globalExplicitWait = validateAndLog(GLOBAL_EXPLICIT_WAIT, setting, getDefaultExplicitWaitTime());
        }
        return globalExplicitWait;
    }

    public static void setGlobalExplicitWait(double globalExplicitWait) {
        WaitManager.globalExplicitWait = validateAndLog(GLOBAL_EXPLICIT_WAIT, globalExplicitWait, getDefaultExplicitWaitTime());
    }

    // Implicit Wait (Uses 5L as default instead of global/default explicit wait)
    public static double getImplicitWait() {
        if (implicitWait == null) {
            Double setting = SystemHelper.getApplicationSettingDouble(IMPLICIT_WAIT);
            implicitWait = validateAndLog(IMPLICIT_WAIT, setting, IMPLICIT_WAIT_DEFAULT);
        }
        return implicitWait;
    }

    public static void setImplicitWait(double implicitWait) {
        WaitManager.implicitWait = validateAndLog(IMPLICIT_WAIT, implicitWait, IMPLICIT_WAIT_DEFAULT);
    }

    // Other Explicit Waits (Use Global Explicit Wait as Fallback)
    public static double getClickableExplicitWait() {
        if (clickableExplicitWait == null) {
            Double setting = SystemHelper.getApplicationSettingDouble(CLICKABLE_EXPLICIT_WAIT);
            clickableExplicitWait = validateAndLog(CLICKABLE_EXPLICIT_WAIT, setting, getGlobalExplicitWait());
        }
        return clickableExplicitWait;
    }

    public static void setClickableExplicitWait(double clickableExplicitWait) {
        WaitManager.clickableExplicitWait = validateAndLog(CLICKABLE_EXPLICIT_WAIT, clickableExplicitWait, getGlobalExplicitWait());
    }

    public static double getVisibleExplicitWait() {
        if (visibleExplicitWait == null) {
            Double setting = SystemHelper.getApplicationSettingDouble(VISIBLE_EXPLICIT_WAIT);
            visibleExplicitWait = validateAndLog(VISIBLE_EXPLICIT_WAIT, setting, getGlobalExplicitWait());
        }
        return visibleExplicitWait;
    }

    public static void setVisibleExplicitWait(double visibleExplicitWait) {
        WaitManager.visibleExplicitWait = validateAndLog(VISIBLE_EXPLICIT_WAIT, visibleExplicitWait, getGlobalExplicitWait());
    }

    public static double getPresenceExplicitWait() {
        if (presenceExplicitWait == null) {
            Double setting = SystemHelper.getApplicationSettingDouble(PRESENCE_EXPLICIT_WAIT);
            presenceExplicitWait = validateAndLog(PRESENCE_EXPLICIT_WAIT, setting, getGlobalExplicitWait());
        }
        return presenceExplicitWait;
    }

    public static void setPresenceExplicitWait(double presenceExplicitWait) {
        WaitManager.presenceExplicitWait = validateAndLog(PRESENCE_EXPLICIT_WAIT, presenceExplicitWait, getGlobalExplicitWait());
    }

    public static double getTextExplicitWait() {
        if (textExplicitWait == null) {
            Double setting = SystemHelper.getApplicationSettingDouble(TEXT_EXPLICIT_WAIT);
            textExplicitWait = validateAndLog(TEXT_EXPLICIT_WAIT, setting, getGlobalExplicitWait());
        }
        return textExplicitWait;
    }

    public static void setTextExplicitWait(double textExplicitWait) {
        WaitManager.textExplicitWait = validateAndLog(TEXT_EXPLICIT_WAIT, textExplicitWait, getGlobalExplicitWait());
    }

    public static double getAttributeExplicitWait() {
        if (attributeExplicitWait == null) {
            Double setting = SystemHelper.getApplicationSettingDouble(ATTRIBUTE_EXPLICIT_WAIT);
            attributeExplicitWait = validateAndLog(ATTRIBUTE_EXPLICIT_WAIT, setting, getGlobalExplicitWait());
        }
        return attributeExplicitWait;
    }

    public static void setAttributeExplicitWait(double attributeExplicitWait) {
        WaitManager.attributeExplicitWait = validateAndLog(ATTRIBUTE_EXPLICIT_WAIT, attributeExplicitWait, getGlobalExplicitWait());
    }

    public static double getFrameExplicitWait() {
        if (frameExplicitWait == null) {
            Double setting = SystemHelper.getApplicationSettingDouble(FRAME_EXPLICIT_WAIT);
            frameExplicitWait = validateAndLog(FRAME_EXPLICIT_WAIT, setting, getGlobalExplicitWait());
        }
        return frameExplicitWait;
    }

    public static void setFrameExplicitWait(double frameExplicitWait) {
        WaitManager.frameExplicitWait = validateAndLog(FRAME_EXPLICIT_WAIT, frameExplicitWait, getGlobalExplicitWait());
    }

    public static double getWindowExplicitWait() {
        if (windowExplicitWait == null) {
            Double setting = SystemHelper.getApplicationSettingDouble(WINDOW_EXPLICIT_WAIT);
            windowExplicitWait = validateAndLog(WINDOW_EXPLICIT_WAIT, setting, getGlobalExplicitWait());
        }
        return windowExplicitWait;
    }

    public static void setWindowExplicitWait(double windowExplicitWait) {
        WaitManager.windowExplicitWait = validateAndLog(WINDOW_EXPLICIT_WAIT, windowExplicitWait, getGlobalExplicitWait());
    }

    public static double getStaleElementExplicitWait() {
        if (staleElementExplicitWait == null) {
            Double setting = SystemHelper.getApplicationSettingDouble(STALE_ELEMENT_EXPLICIT_WAIT);
            staleElementExplicitWait = validateAndLog(STALE_ELEMENT_EXPLICIT_WAIT, setting, getGlobalExplicitWait());
        }
        return staleElementExplicitWait;
    }

    public static void setStaleElementExplicitWait(double staleElementExplicitWait) {
        WaitManager.staleElementExplicitWait = validateAndLog(STALE_ELEMENT_EXPLICIT_WAIT, staleElementExplicitWait, getGlobalExplicitWait());
    }

    public static double getPageLoadTimeout() {
        if (pageLoadTimeout == null) {
            Double setting = SystemHelper.getApplicationSettingDouble(PAGE_LOAD_TIMEOUT);
            pageLoadTimeout = validateAndLog(PAGE_LOAD_TIMEOUT, setting, PAGE_LOAD_TIMEOUT_DEFAULT);
        }
        return pageLoadTimeout;
    }

    public static void setPageLoadTimeout(double pageLoadTimeout) {
        WaitManager.pageLoadTimeout = validateAndLog(PAGE_LOAD_TIMEOUT, pageLoadTimeout, PAGE_LOAD_TIMEOUT_DEFAULT);
    }

    public static double getPollingIntervalTime() {
        if (pollingIntervalTime == null) {
            Double setting = SystemHelper.getApplicationSettingDouble(POLLING_INTERVAL);
            pollingIntervalTime = validateAndLog(POLLING_INTERVAL, setting, POLLING_INTERVAL_DEFAULT);
        }
        return pollingIntervalTime;
    }

    public static void setPollingIntervalTime(double pollingIntervalTime) {
        WaitManager.pollingIntervalTime = validateAndLog(DEFAULT_EXPLICIT_WAIT, pollingIntervalTime, DEFAULT_EXPLICIT_WAIT_VALUE);
    }

    public static boolean isLogInvalidWaitTimes() {
        return logInvalidWaitTimes;
    }

    public static void setLogInvalidWaitTimes(boolean logInvalidWaitTimes) {
        WaitManager.logInvalidWaitTimes = logInvalidWaitTimes;
    }

    /**
     * Retrieves the wait duration for a given setting.
     *
     * @param settingName The name of the setting.
     * @param timeUnit The desired time unit.
     * @return The corresponding wait duration.
     */
    public static Duration getWaitDuration(String settingName, TimeUnit timeUnit) {
        double seconds = getWaitTimeByName(settingName);
        // Preserve sub-second precision: casting seconds to a whole number first would zero out
        // fractional waits (e.g. the 0.1s polling interval) and drop the fraction from values
        // like 2.5s. The requested TimeUnit only affects the granularity we report in, so both
        // SECONDS and MILLISECONDS resolve to the same millisecond-accurate Duration.
        long millis = Math.round(seconds * 1000);

        if (timeUnit == null) {
            timeUnit = TimeUnit.MILLISECONDS; // Default to milliseconds
        }

        switch (timeUnit) {
            case SECONDS:
            case MILLISECONDS:
                return Duration.ofMillis(millis);
            default:
                Logger.logError("Unsupported TimeUnit: " + timeUnit + ". Defaulting to milliseconds.");
                return Duration.ofMillis(millis);
        }
    }

    /**
     * Retrieves the wait duration in milliseconds.
     *
     * @param settingName The name of the setting.
     * @return The corresponding wait duration.
     */
    public static Duration getWaitDuration(String settingName) {
        return getWaitDuration(settingName, TimeUnit.MILLISECONDS); // Default to milliseconds
    }

    /**
     * Retrieves the wait time associated with a given setting name.
     *
     * @param settingName The name of the setting.
     * @return The corresponding wait time in seconds.
     */
    private static double getWaitTimeByName(String settingName) {
        switch (settingName) {
            case GLOBAL_EXPLICIT_WAIT:
                return getGlobalExplicitWait();
            case DEFAULT_EXPLICIT_WAIT:
                return getDefaultExplicitWaitTime();
            case CLICKABLE_EXPLICIT_WAIT:
                return getClickableExplicitWait();
            case VISIBLE_EXPLICIT_WAIT:
                return getVisibleExplicitWait();
            case PRESENCE_EXPLICIT_WAIT:
                return getPresenceExplicitWait();
            case TEXT_EXPLICIT_WAIT:
                return getTextExplicitWait();
            case ATTRIBUTE_EXPLICIT_WAIT:
                return getAttributeExplicitWait();
            case FRAME_EXPLICIT_WAIT:
                return getFrameExplicitWait();
            case WINDOW_EXPLICIT_WAIT:
                return getWindowExplicitWait();
            case STALE_ELEMENT_EXPLICIT_WAIT:
                return getStaleElementExplicitWait();
            case IMPLICIT_WAIT:
                return getImplicitWait();
            case PAGE_LOAD_TIMEOUT:
                return getPageLoadTimeout();
            case POLLING_INTERVAL:
                return getPollingIntervalTime();
            default:
                Logger.logError("Unknown wait setting: " + settingName);
                return DEFAULT_EXPLICIT_WAIT_VALUE;
        }
    }

    /**
     * Sets the wait time for the specified setting name using a value in milliseconds.
     *
     * @param waitName The name of the setting (must match one of the defined constants).
     * @param milliseconds The wait time value in milliseconds.
     */
    public static void setWaitTime(String waitName, long milliseconds){
        double seconds = milliseconds / 1000.0; // Convert milliseconds to seconds
        setWaitTime(waitName, seconds);
    }

    /**
     * Sets the wait time for the specified setting name using a value in seconds (double).
     *
     * @param waitName The name of the setting (must match one of the defined constants).
     * @param seconds The wait time value in seconds.
     */
    public static void setWaitTime(String waitName, double seconds)
    {
        switch (waitName) {
            case GLOBAL_EXPLICIT_WAIT:
                setGlobalExplicitWait(seconds);
                break;
            case DEFAULT_EXPLICIT_WAIT:
                setDefaultExplicitWaitTime(seconds);
                break;
            case CLICKABLE_EXPLICIT_WAIT:
                setClickableExplicitWait(seconds);
                break;
            case VISIBLE_EXPLICIT_WAIT:
                setVisibleExplicitWait(seconds);
                break;
            case PRESENCE_EXPLICIT_WAIT:
                setPresenceExplicitWait(seconds);
                break;
            case TEXT_EXPLICIT_WAIT:
                setTextExplicitWait(seconds);
                break;
            case ATTRIBUTE_EXPLICIT_WAIT:
                setAttributeExplicitWait(seconds);
                break;
            case FRAME_EXPLICIT_WAIT:
                setFrameExplicitWait(seconds);
                break;
            case WINDOW_EXPLICIT_WAIT:
                setWindowExplicitWait(seconds);
                break;
            case STALE_ELEMENT_EXPLICIT_WAIT:
                setStaleElementExplicitWait(seconds);
                break;
            case IMPLICIT_WAIT:
                setImplicitWait(seconds);
                break;
            case PAGE_LOAD_TIMEOUT:
                setPageLoadTimeout(seconds);
                break;
            case POLLING_INTERVAL:
                setPollingIntervalTime(seconds);
                break;
            default:
                Logger.logError("Unknown wait setting: " + waitName);
        }
    }


    /**
     * Resets all wait time settings to their default state.
     */
    public static void resetAllWaits() {
        globalExplicitWait = null;
        defaultExplicitWaitTime = null;
        clickableExplicitWait = null;
        visibleExplicitWait = null;
        presenceExplicitWait = null;
        textExplicitWait = null;
        attributeExplicitWait = null;
        frameExplicitWait = null;
        windowExplicitWait = null;
        staleElementExplicitWait = null;
        implicitWait = null;
        pageLoadTimeout = null;
    }
}



