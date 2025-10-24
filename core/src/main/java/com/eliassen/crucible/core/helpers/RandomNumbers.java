/**
 * Utility class for generating random numbers.
 */
package com.eliassen.crucible.core.helpers;

import java.util.Random;

/**
 * Provides methods for generating random numbers.
 */
public class RandomNumbers {

    /**
     * Generates a random integer between 1 and the specified maximum (inclusive).
     * @param max The maximum value (inclusive).
     * @return A random integer between 1 and max.
     */
    public static int randomNumberFromOneToMax(int max) {
        int num = new Random().nextInt(max) + 1;
        return num;
    }

    /**
     * Generates a random byte (either 0 or 1).
     * @return A random byte.
     */
    public static byte randomByte() {
        return (byte) new Random().nextInt(2); // corrected to generate either 0 or 1
    }
}
