package com.eliassen.crucible.core.helpers;

import java.util.Random;

public class RandomNumbers
{
    public static int randomNumberFromOneToMax(int max)
    {
        int num = new Random().nextInt(max) + 1;
        return num;
    }

    public static byte randomByte()
    {
        return (byte)new Random().nextInt(1);
    }
}
