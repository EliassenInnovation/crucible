/**
 * Provides methods for generating random data for testing purposes.
 * Utilizes the JavaFaker library to generate fake data.
 */
package com.eliassen.crucible.core.helpers;

import com.eliassen.crucible.core.sharedobjects.MasterMind;
import com.github.javafaker.Faker;

/**
 * DataGenerator class provides various methods to generate random data.
 */
public class DataGenerator {
    /**
     * Static instance of Faker to avoid repeated instantiation.
     */
    static Faker _faker;

    /**
     * Constants for storing random first and last names in the MasterMind dictionary.
     */
    public static final String RANDOM_FIRST_NAME = "random first name";
    public static final String RANDOM_LAST_NAME = "random last name";

    /**
     * Returns the Faker instance, initializing it if necessary.
     * @return Faker instance
     */
    public static Faker faker() {
        if (_faker == null) {
            _faker = new Faker();
        }
        return _faker;
    }

    /**
     * Generates a random Hobbit quote.
     * @return Random Hobbit quote
     */
    public static String getHobbitQuote() {
        return faker().hobbit().quote();
    }

    /**
     * Generates a random funny name.
     * @return Random funny name
     */
    public static String getRandomFunnyName() {
        return faker().funnyName().name();
    }

    /**
     * Generates a random funny name and stores it as "random first name" and "random last name" in the MasterMind dictionary.
     */
    public static void generateRandomName() {
        String fullName = getRandomFunnyName();
        String firstName = fullName.split(" ")[0];
        String lastName = fullName.split(" ")[1];
        MasterMind.store(RANDOM_FIRST_NAME, firstName);
        MasterMind.store(RANDOM_LAST_NAME, lastName);
    }

    /**
     * Generates a random phone number.
     * @return Random phone number
     */
    public static String getRandomPhoneNumber() {
        return faker().phoneNumber().cellPhone();
    }

    /**
     * Generates a random 10-digit phone number in the format XXX-XXX-XXXX.
     * @return Random 10-digit phone number
     */
    public static String getRandom10DigitPhoneNumber() {
        return faker().numerify("###").toString() + "-" + faker().numerify("###").toString() + "-" + faker().numerify("####").toString();
    }

    /**
     * Generates a random email address.
     * @return Random email address
     */
    public static String getRandomEmail() {
        return faker().internet().emailAddress();
    }

    /**
     * Generates a random job title.
     * @return Random job title
     */
    public static String getRandomTitle() {
        return faker().company().profession();
    }

    /**
     * Generates a random Social Security Number (SSN).
     * @return Random SSN
     */
    public static String getRandomSSN() {
        return faker().idNumber().ssnValid();
    }
}
