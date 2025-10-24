/**
 * Utility class for validating input using regular expressions.
 */
package com.eliassen.crucible.core.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides methods for validating input using regular expressions.
 */
public class RegexValidator {

    /**
     * Regular expression pattern for validating email addresses.
     */
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+([_A-Za-z0-9-+]+)*@[A-Za-z0-9-]+([A-Za-z0-9]+)*[.]([A-Za-z]{2,})$";

    /**
     * Regular expression pattern for validating location (e.g., city, state).
     */
    public static final String LOCATION_PATTERN = "[_A-Za-z]+(,)+[_A-Za-z]$";

    /**
     * Regular expression pattern for validating phone numbers (XXX-XXX-XXXX).
     */
    public static final String PHONE_PATTERN = "^[0-9]{3}+(-)+[0-9]{3}+(-)[0-9]{4}$";

    /**
     * Regular expression pattern for validating dates (MM/DD/YYYY or MM-DD-YYYY or MM.DD.YYYY).
     */
    public static final String DATE_PATTERN = "^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.]([0-9][0-9][0-9]*[0-9]*)$";

    /**
     * Regular expression pattern for validating social security numbers (XXX-XX-XXXX).
     */
    public static final String SSN_PATTERN = "^[0-8][0-9]{2}-[0-9]{2}-[0-9]{4}$";

    /**
     * Validates the input against the specified regular expression pattern.
     * @param text The input to be validated.
     * @param patternString The regular expression pattern to validate against.
     * @return True if the input matches the regular expression pattern, false otherwise.
     */
    public static boolean validate(String text, String patternString) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    /**
     * Validates an email address.
     * @param text The email address to be validated.
     * @return True if the email address is valid, false otherwise.
     */
    public static boolean validateEmail(String text) {
        return validate(text, EMAIL_PATTERN);
    }

    /**
     * Validates a location (e.g., city, state).
     * @param text The location to be validated.
     * @return True if the location is valid, false otherwise.
     */
    public static boolean validateLocation(String text) {
        return validate(text, LOCATION_PATTERN);
    }

    /**
     * Validates a phone number.
     * @param text The phone number to be validated.
     * @return True if the phone number is valid, false otherwise.
     */
    public static boolean validatePhone(String text) {
        return validate(text, PHONE_PATTERN);
    }

    /**
     * Validates a date.
     * @param text The date to be validated.
     * @return True if the date is valid, false otherwise.
     */
    public static boolean validateDate(String text) {
        return validate(text, DATE_PATTERN);
    }
}
