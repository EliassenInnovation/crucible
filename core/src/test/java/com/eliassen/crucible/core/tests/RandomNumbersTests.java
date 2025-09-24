package com.eliassen.crucible.core.tests;

import com.eliassen.crucible.core.helpers.RandomNumbers;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RandomNumbersTests
{
    @Test
    public void randomNumbersFromOneToMax_shouldReturnIntFromOneToMaxInclusive()
    {
        int max = 1;
        int expected = 1;
        int result = RandomNumbers.randomNumberFromOneToMax(max);
        assertEquals(expected, result);
    }
}
