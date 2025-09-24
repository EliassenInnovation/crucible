package com.lightwell.testing.demo.unitTests;

import com.lightwell.testing.demo.shared.testObjects.Person;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.lightwell.testing.demo.helpers.functions.PersonFunctions.*;

public class PersonFunctionTests {
    Person testPerson1;
    Person testPerson2;
    Person testPerson3;
    public static final String SHAWN_FIRST_NAME = "Shawn";
    public static final String SHAWN_LAST_NAME = "Barrett";
    public static final String SHAWN_PHONE_NUMBER = "5555555555";

    @Before
    public void initialize() {
        testPerson1 = new Person(SHAWN_FIRST_NAME, SHAWN_LAST_NAME, SHAWN_PHONE_NUMBER);
        testPerson2 = new Person("Justin", "Harrison", "7777777777");
        testPerson3 = new Person("Katie", "Rusch", "3333333333");
    }

    @Test
    public void test_personsAreTheSame() {
        Person testPerson4 = testPerson1;

        Assert.assertTrue(PersonsAreTheSame(testPerson1, testPerson4));
    }

    @Test
    public void test_personsAreNotTheSame()
    {
        Assert.assertFalse(PersonsAreTheSame(testPerson1, testPerson2));
    }

    @Test
    public void test_getPersonDataLine()
    {
        String dataLineForShawnBarrett = "Name: Shawn Barrett | Phone number: 5555555555";
        String dataLineForJustinHarrison = "Name: Justin Harrison | Phone number: 7777777777";
        String dataLineForKatieRusch = "Name: Katie Rusch | Phone number: 3333333333";

        Assert.assertEquals(dataLineForShawnBarrett, GetPersonDataLine(testPerson1));
        Assert.assertEquals(dataLineForJustinHarrison, GetPersonDataLine(testPerson2));
        Assert.assertEquals(dataLineForKatieRusch, GetPersonDataLine(testPerson3));
    }
}