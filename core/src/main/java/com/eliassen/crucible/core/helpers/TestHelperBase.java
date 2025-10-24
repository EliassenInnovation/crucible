package com.eliassen.crucible.core.helpers;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.eliassen.crucible.common.helpers.FileHelper;
import com.eliassen.crucible.common.helpers.Functions;
import com.eliassen.crucible.common.helpers.JsonHelper;
import com.eliassen.crucible.common.helpers.UserHelper;
import com.eliassen.crucible.core.sharedobjects.MasterMind;
import io.cucumber.datatable.DataTable;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Base class for test helpers, providing various utility methods.
 */
public class TestHelperBase extends UserHelper {
    /**
     * Gets a pretty-printed JSON representation of a JSONObject.
     * @param object The JSONObject to format.
     * @return The formatted JSON string.
     */
    public static String getPrettyJson(JSONObject object) {
        return JsonHelper.getPrettyJson(object);
    }

    /**
     * Gets a pretty-printed JSON representation of a JSONArray.
     * @param array The JSONArray to format.
     * @return The formatted JSON string.
     */
    public static String getPrettyJson(JSONArray array) {
        return JsonHelper.getPrettyJson(array);
    }

    /**
     * Gets the contents of a JSON file.
     * @param jsonPath The path to the JSON file.
     * @return The JSONObject representing the file contents.
     */
    public JSONObject getJSONFileContent(String jsonPath) {
        return JsonHelper.getJSONFileContent(jsonPath);
    }

    /**
     * Gets the contents of a text file.
     * @param filePath The path to the text file.
     * @return The file contents as a string.
     */
    public String getTextFileContent(String filePath) {
        return new FileHelper().getTextFileContent(filePath);
    }

    /**
     * Waits for a specified duration.
     * @param duration The duration to wait, in seconds.
     */
    public static void wait(int duration) {
        wait((double) duration);
    }

    /**
     * Waits for a specified duration in the given time unit.
     * @param duration The duration to wait.
     * @param timeUnit The time unit of the duration.
     */
    public static void wait(int duration, TimeUnit timeUnit) {
        wait((double) duration, true, timeUnit);
    }

    /**
     * Waits for a specified duration in the given time unit.
     * @param duration The duration to wait.
     * @param timeUnit The time unit of the duration.
     */
    public static void wait(double duration, TimeUnit timeUnit) {
        wait(duration, true, timeUnit);
    }

    /**
     * Waits a certain number of whole seconds.
     * Will output to the scenario and console.
     * @param seconds The number of seconds to wait.
     */
    public static void wait(double seconds) {
        boolean notify = true;
        wait(seconds, notify, TimeUnit.SECONDS);
    }

    /**
     * Waits a certain number of seconds without notifying the scenario or console.
     * @param seconds The number of seconds to wait.
     */
    public static void waitSilently(int seconds) {
        boolean notify = false;
        wait(seconds, notify, TimeUnit.SECONDS);
    }

    /**
     * Waits for a specified duration in the given time unit.
     * @param duration The duration to wait.
     * @param timeUnit The time unit of the duration.
     */
    public static void wait(long duration, TimeUnit timeUnit) {
        boolean notify = true;
        wait(duration, notify, timeUnit);
    }

    /**
     * Waits for a specified duration in the given time unit without notifying the scenario or console.
     * @param duration The duration to wait.
     * @param timeUnit The time unit of the duration.
     */
    public static void waitSilently(long duration, TimeUnit timeUnit) {
        boolean notify = false;
        wait(duration, notify, timeUnit);
    }

    /**
     * Waits for a specified duration in the given time unit.
     * @param rawWaitDuration The duration to wait.
     * @param notify Whether to notify the scenario and console.
     * @param timeUnit The time unit of the duration.
     */
    private static void wait(double rawWaitDuration, boolean notify, TimeUnit timeUnit) {
        long waitDurationInMilliseconds = 0;
        String timeUnitToReport = "";

        try {
            switch (timeUnit) {
                case MINUTES:
                    waitDurationInMilliseconds = Double.valueOf(rawWaitDuration * 60 * 1000).longValue();
                    timeUnitToReport = "minutes";
                    break;
                case SECONDS:
                    waitDurationInMilliseconds = Double.valueOf(rawWaitDuration * 1000).longValue();
                    timeUnitToReport = "seconds";
                    break;
                case MILLISECONDS:
                    waitDurationInMilliseconds = Double.valueOf(rawWaitDuration).longValue();
                    timeUnitToReport = "milliseconds";
                    break;
                default:
                    throw new InvalidParameterException("TimeUnit " + timeUnit.toString() + " is not a valid option");
            }

            if (notify) {
                Logger.log("Waiting " + rawWaitDuration + " " + timeUnitToReport);
            }
            Thread.sleep(waitDurationInMilliseconds);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a DataTable to a JSONArray.
     * @param dataTable The DataTable to convert.
     * @return The JSONArray representation of the DataTable.
     */
    public static JSONArray convertDataTableToJson(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return new JSONArray(objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert the datatable to JSON: " + e);
        }
    }

    /**
     * Converts a DataTable to a Hashtable.
     * @param table The DataTable to convert.
     * @return The Hashtable representation of the DataTable.
     */
    public static Hashtable<String, String> convertDataTableToHashtable(DataTable table) {
        return Functions.convertDataTableToHashtable(table);
    }

    /**
     * Converts a Hashtable to a JSON string.
     * @param table The Hashtable to convert.
     * @return The JSON string representation of the Hashtable.
     */
    public static String convertHashtableToJSONString(Hashtable<String, String> table) {
        String jsonString = "{";

        for (String key : table.keySet()) {
            if (jsonString.length() > 1) {
                jsonString += ",";
            }

            String value = table.get(key);
            if (value.equals("array")) {
                jsonString += "\"" + key + "\":[]";
            } else if (value.equals(NULL)) {
                jsonString += "\"" + key + "\": null";
            } else {
                jsonString += "\"" + key + "\":\"" + value + "\"";
            }
        }

        jsonString += "}";

        return jsonString;
    }

    /**
     * Generates a new GUID.
     * @return The generated GUID.
     */
    public static String getNewGUID() {
        String guid = UUID.randomUUID().toString();

        return guid;
    }

    /**
     * Gets the month number from a month string.
     * @param monthString The month string (e.g. "Jan", "February", etc.).
     * @return The month number (1-12).
     * @throws ParseException If the month string is invalid.
     */
    public static int getMonthNumberFromMonthString(String monthString) throws ParseException {
        return getMonthNumberFromMonthString(monthString, Locale.ENGLISH);
    }

    /**
     * Gets the month number from a month string in a specific locale.
     * @param monthString The month string (e.g. "Jan", "February", etc.).
     * @param locale The locale to use for parsing the month string.
     * @return The month number (1-12).
     * @throws ParseException If the month string is invalid.
     */
    public static int getMonthNumberFromMonthString(String monthString, Locale locale) throws ParseException {
        LocalDate date = convertDateToLocalDate(new SimpleDateFormat("MMM", locale).parse(monthString));

        int num = date.getMonthValue();

        return num;
    }

    /**
     * Converts a Date to a LocalDate.
     * @param date The Date to convert.
     * @return The LocalDate representation of the Date.
     */
    public static LocalDate convertDateToLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * Gets a sample object from a JSON file.
     * @param sampleObjectName The name of the sample object to retrieve.
     * @return The JSONObject representing the sample object.
     */
    public static JSONObject getSampleObject(String sampleObjectName) {
        String sampleObjectsFileName = "sampleObjects.json";
        JSONObject sampleObjects = new TestHelperBase().getJSONFileContent(sampleObjectsFileName);
        JSONObject requestedObject = sampleObjects.getJSONObject(sampleObjectName);

        return requestedObject;
    }

    /**
     * Adds a value to a CSV list in storage.
     * @param key The key of the CSV list.
     * @param value The value to add to the list.
     */
    public static void addToCSVListInStorage(String key, String value) {
        String list = MasterMind.retrieve(key);
        if (list == null) {
            list = value;
        } else {
            list += "," + value;
        }

        MasterMind.store(key, list);
    }

    /**
     * Adds a value to a CSV list in persisted storage.
     * @param key The key of the CSV list.
     * @param value The value to add to the list.
     */
    public static void addToCSVListInPersistedStorage(String key, String value) {
        String list = MasterMind.retrievePersisted(key);
        if (list == null) {
            list = value;
        } else {
            list += "," + value;
        }

        MasterMind.storePersisted(key, list);
    }

    /**
     * Checks if a string is numeric.
     * @param text The string to check.
     * @return True if the string is numeric, false otherwise.
     */
    public static boolean isNumeric(String text) {
        if (text == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(text);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
