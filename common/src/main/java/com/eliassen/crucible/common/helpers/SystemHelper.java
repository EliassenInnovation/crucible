package com.eliassen.crucible.common.helpers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class SystemHelper {
    private SystemHelper(){}

    public static final String URL = "url";
    public static final String ENVIRONMENT = "environment";
    public static final String BROWSER = "browser";
    public static final String PASSWORD = "password";
    public static final String SETTINGS = "settings";

    protected static final Map<String, Object> configValues = new HashMap<>();

    public static String getCommandLineParameter(String parameterName) {
        Object cachedValue = getCachedConfigSetting(parameterName);
        return cachedValue != null ? cachedValue.toString() : storeAndRetrieveSystemProperty(parameterName);
    }

    private static String storeAndRetrieveSystemProperty(String parameterName) {
        String prop = System.getProperty(parameterName);

        if (prop == null && parameterName.contains(".")) {
            String leafVariable = parameterName.substring(parameterName.lastIndexOf(".") + 1);
            prop = System.getenv(leafVariable);
        }

        if (prop != null) {
            prop = parameterName.equals(PASSWORD) ? prop : prop.toLowerCase();
            configValues.put(parameterName, prop); // Store in configValues
        }
        return prop;
    }

    public static String getEnvironment() {
        return getCommandLineParameter(ENVIRONMENT);
    }

    public static String getEnvironmentVariable(String variableName) {
        String prop = System.getenv(variableName);

        // If the variable contains a dot, extract the leaf name and check again
        if (prop == null && variableName.contains(".")) {
            String leafVariable = variableName.substring(variableName.lastIndexOf(".") + 1);
            prop = System.getenv(leafVariable);
        }

        return prop;
    }

    public static void setEnvironmentVariable(String variableName, String value) {
        System.setProperty(variableName, value);
    }

    public static String getApplicationSetting(String settingName) {
        return getConfigSettingString(SETTINGS + "." + settingName);
    }

    public static String getApplicationSettingString(String settingName) {
        return getConfigSettingString(SETTINGS + "." + settingName);
    }

    public static Integer getApplicationSettingInt(String settingName) {
        return getConfigSettingInt(SETTINGS + "." + settingName);
    }

    public static Double getApplicationSettingDouble(String settingName) {
        return getConfigSettingDouble(SETTINGS + "." + settingName);
    }

    public static boolean getApplicationSettingBoolean(String settingName) {
        return getConfigSettingBoolean(SETTINGS + "." + settingName);
    }

    public static JSONArray getApplicationSettingArray(String settingName) {
        return getConfigSettingArray(SETTINGS + "." + settingName);
    }

    public static String getConfigSetting(String pathToSetting) {
        Object setting = getConfigSettingGeneric(pathToSetting);
        return setting != null ? setting.toString() : null;
    }

    public static String getConfigSettingString(String pathToSetting) {
        return getConfigSetting(pathToSetting);
    }

    public static Boolean getConfigSettingBoolean(String pathToSetting) {
        Object booleanSetting = getConfigSettingGeneric(pathToSetting);
        if (booleanSetting instanceof Boolean) {
            return (boolean) booleanSetting;
        } else {
            return booleanSetting != null ? Boolean.parseBoolean(booleanSetting.toString()) : false;
        }
    }

    public static Integer getConfigSettingInt(String pathToSetting) {
        Object intSetting = getConfigSettingGeneric(pathToSetting);
        if (intSetting instanceof Integer) {
            return (int) intSetting;
        } else {
            return intSetting != null ? Integer.parseInt(intSetting.toString()) : null;
        }
    }

    public static Double getConfigSettingDouble(String pathToSetting) {
        Object doubleSetting = getConfigSettingGeneric(pathToSetting);
        if (doubleSetting instanceof Double) {
            return (double) doubleSetting;
        } else {
            return doubleSetting != null ? Double.parseDouble(doubleSetting.toString()) : null;
        }
    }

    public static JSONArray getConfigSettingArray(String pathToSetting) {
        Object arraySetting = getConfigSettingGeneric(pathToSetting);
        if (arraySetting instanceof JSONArray jsonArray) {
            return jsonArray;
        } else {
            return null;
        }
    }

    public static Object getConfigSettingGeneric(String pathToSetting) {
        // Step 1: Check the in-memory cache (map)
        Object cachedValue = getCachedConfigSetting(pathToSetting);
        if (cachedValue != null) {
            return cachedValue;
        }

        // Step 2: Check command line parameters
        // getCommandLineParameter will cache the value if it finds one
        String commandLineValue = getCommandLineParameter(pathToSetting);
        if (commandLineValue != null) {
            return commandLineValue;
        }

        // Step 3: Check environment variables
        // never cached
        String environmentVariableValue = SystemHelper.getEnvironmentVariable(pathToSetting);
        if (environmentVariableValue != null) {
            return environmentVariableValue;
        }

        // Step 4: Check the JSON configuration file
        Object valueFromJson = getConfigFromJson(pathToSetting, "config.json");
        if (valueFromJson != null) {
            cacheConfigSetting(pathToSetting, valueFromJson);
        }

        return valueFromJson;
    }

    private static Object getConfigFromJson(String pathToSetting, String jsonFileName) {
        JSONObject config = JsonHelper.getJSONFileContent(jsonFileName);
        String[] paths = pathToSetting.split("\\.");

        for (String path : paths) {
            if (config.has(path)) {
                Object value = config.get(path);
                if (value instanceof JSONObject jsonObject) {
                    config = jsonObject; // Drill down into nested JSON
                } else {
                    return value; // Return the found value
                }
            } else {
                return null; // Path does not exist in JSON
            }
        }
        return config; // Return the final JSON object if fully traversed
    }

    private static Object getCachedConfigSetting(String configSettingName) {
        return configValues.get(configSettingName); // Returns null if not found
    }

    private static void cacheConfigSetting(String configSettingName, Object value) {
        configValues.put(configSettingName, value);
    }

    public static boolean isRunningInDocker() {
        try (Stream<String> stream = Files.lines(Paths.get("/proc/1/cgroup"))) {
            return stream.anyMatch(line -> line.contains("docker"));
        } catch (IOException e) {
            return false;
        }
    }
}
