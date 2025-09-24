package com.eliassen.crucible.core.helpers;

import com.github.javafaker.Faker;

public class RandomValues
{
    static Faker _faker;

    private static Faker faker() {
        if (_faker == null) {
            _faker = new Faker();
        }

        return _faker;
    }

    public RandomValues() {}

    public static String getRandomPokemon() {
        return faker().pokemon().name();
    }

    public static String AmOrPm(byte x)
    {
        String half = "";

        if (x == 1) {
            half = "AM";
        } else {
            half = "PM";
        }
        return half;
    }

    public static String RandomAmOrPm()
    {
        int x = RandomNumbers.randomByte();
        String half = AmOrPm((byte)x);
        return half;
    }

}
