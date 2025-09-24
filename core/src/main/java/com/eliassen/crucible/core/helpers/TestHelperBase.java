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

public class TestHelperBase extends UserHelper {
    public static String getPrettyJson(JSONObject object) {
        return JsonHelper.getPrettyJson(object);
    }

    public static String getPrettyJson(JSONArray array) {
        return JsonHelper.getPrettyJson(array);
    }

    public JSONObject getJSONFileContent(String jsonPath) {
        return JsonHelper.getJSONFileContent(jsonPath);
    }

    public String getTextFileContent(String filePath) {
        return new FileHelper().getTextFileContent(filePath);
    }

    /**
     * Waits for the specified duration
     * @param duration
     */
    public static void wait(int duration) {
        wait((double) duration);
    }

    /**
     * Waits for the specified duration in the given time unit
     * @param duration
     * @param timeUnit
     */
    public static void wait(int duration, TimeUnit timeUnit) {
        wait((double) duration, true, timeUnit);
    }

    /**
     * Waits for the specified duration in the given time unit
     * @param duration
     * @param timeUnit
     */
    public static void wait(double duration, TimeUnit timeUnit) {
        wait(duration, true, timeUnit);
    }

    /**
     * Waits a certain number of whole seconds
     * Will output to the scenario and console
     * @param seconds
     */
    public static void wait(double seconds) {

        boolean notify = true;
        wait(seconds,notify,TimeUnit.SECONDS);
    }

    /**
     * will not output to the scenario or console
     * @param seconds
     */
    public static void waitSilently(int seconds) {
        boolean notify = false;
        wait(seconds, notify, TimeUnit.SECONDS);
    }

    /**
     *
     * @param duration
     * @param timeUnit
     */
    public static void wait(long duration, TimeUnit timeUnit) {
        boolean notify = true;
        wait(duration, notify, timeUnit);
    }

    /**
     *
     * @param duration
     * @param timeUnit
     */
    public static void waitSilently(long duration, TimeUnit timeUnit) {
        boolean notify = false;
        wait(duration, notify, timeUnit);
    }

    private static void wait(double rawWaitDuration, boolean notify, TimeUnit timeUnit) {
        long waitDurationInMilliseconds = 0;
        String timeUnitToReport = "";

        try {
            switch(timeUnit){
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

            Logger.log("Waiting " + rawWaitDuration + " " + timeUnitToReport);
            Thread.sleep(waitDurationInMilliseconds);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray convertDataTableToJson(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return new JSONArray(objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert the datatable to JSON: " + e);
        }
    }

    public static Hashtable<String, String> convertDataTableToHashtable(DataTable table) {
        return Functions.convertDataTableToHashtable(table);
    }

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

    public static String getNewGUID() {
        String guid = UUID.randomUUID().toString();

        return guid;
    }

    public static int getMonthNumberFromMonthString(String monthString) throws ParseException {
        return getMonthNumberFromMonthString(monthString, Locale.ENGLISH);
    }

    public static int getMonthNumberFromMonthString(String monthString, Locale locale) throws ParseException {
        LocalDate date = convertDateToLocalDate(new SimpleDateFormat("MMM", locale).parse(monthString));

        int num = date.getMonthValue();

        return num;
    }

    public static LocalDate convertDateToLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static JSONObject getSampleObject(String sampleObjectName) {
        String sampleObjectsFileName = "sampleObjects.json";
        JSONObject sampleObjects = new TestHelperBase().getJSONFileContent(sampleObjectsFileName);
        JSONObject requestedObject = sampleObjects.getJSONObject(sampleObjectName);

        return requestedObject;
    }

    public static void addToCSVListInStorage(String key, String value) {
        String list = MasterMind.retrieve(key);
        if (list == null) {
            list = value;
        } else {
            list += "," + value;
        }

        MasterMind.store(key, list);
    }

    public static void addToCSVListInPersistedStorage(String key, String value) {
        String list = MasterMind.retrievePersisted(key);
        if (list == null) {
            list = value;
        } else {
            list += "," + value;
        }

        MasterMind.storePersisted(key, list);
    }

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
