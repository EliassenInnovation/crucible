package com.eliassen.crucible.core.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidator
{
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+([_A-Za-z0-9-+]+)*@[A-Za-z0-9-]+([A-Za-z0-9]+)*[.]([A-Za-z]{2,})$";
    public static final String LOCATION_PATTERN = "[_A-Za-z]+(,)+[_A-Za-z]$";
    public static final String PHONE_PATTERN = "^[0-9]{3}+(-)+[0-9]{3}+(-)[0-9]{4}$";
    public static final String DATE_PATTERN = "^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.]([0-9][0-9][0-9]*[0-9]*)$";

    public static final String SSN_PATTERN = "^[0-8][0-9]{2}-[0-9]{2}-[0-9]{4}$";

    public static boolean validate(String text, String patternString)
    {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    public static boolean validateEmail(String text) { return validate(text, EMAIL_PATTERN); }

    public static boolean validateLocation(String text) { return validate(text, LOCATION_PATTERN);}

    public static boolean validatePhone(String text) { return validate(text, PHONE_PATTERN); }

    public static boolean validateDate(String text) {return validate(text, DATE_PATTERN);}
}
