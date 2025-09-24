package com.eliassen.crucible.core.tests;

import com.eliassen.crucible.core.helpers.RandomValues;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RandomValuesTests
{
    @Test
    public void randomAmOrPmTest_zeroReturnsPm()
    {
        byte num = 0;
        String expected = "PM";
        String result = RandomValues.AmOrPm(num);
        assertEquals(expected, result);
    }

    @Test
    public void randomAmOrPmTest_oneReturnsAm()
    {
        byte num = 1;
        String expected = "AM";
        String result = RandomValues.AmOrPm(num);
        assertEquals(expected, result);
    }

    @Test
    public void randomPokemon_ShouldGetAName()
    {
        String name = RandomValues.getRandomPokemon();
        assertNotNull(name);
    }
}
