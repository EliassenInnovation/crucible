/**
 * Utility class for generating random values using the JavaFaker library.
 */
package com.eliassen.crucible.core.helpers;

import com.github.javafaker.Faker;

/**
 * Provides methods for generating random values.
 */
public class RandomValues {

    /**
     * Static instance of Faker, initialized lazily.
     */
    private static final Faker faker = new Faker();

    /**
     * Default constructor for RandomValues.
     */
    public RandomValues() {}

    /**
     * Generates a random Pok�mon name.
     * @return A random Pok�mon name.
     */
    public static String getRandomPokemon() {
        return faker.pokemon().name();
    }

    /**
     * Returns "Am" or "Pm" based on the input byte.
     * @param x The input byte (1 for "Am", other values for "Pm").
     * @return "Am" or "Pm".
     */
    public static String AmOrPm(byte x) {
        String half = x == 1 ? "Am" : "Pm";
        return half;
    }

    /**
     * Generates a random "Am" or "Pm" value.
     * @return A random "Am" or "Pm" value.
     */
    public static String RandomAmOrPm() {
        int x = RandomNumbers.randomByte();
        String half = AmOrPm((byte) x);
        return half;
    }
}
