package com.eliassen.crucible.demo.helpers.functions;

import com.eliassen.crucible.demo.shared.testObjects.Person;

public class PersonFunctions
{
    public static Person ChangeFirstName(Person person, String newFirstName)
    {
        person.setFirstName(newFirstName);
        return person;
    }

    public static Person ChangeLastName(Person person, String newLastName)
    {
        person.setLastName(newLastName);
        return person;
    }

    public static Person ChangePhoneNumber(Person person, String newPhoneNumber)
    {
        person.setPhoneNumber(newPhoneNumber);
        return person;
    }

    public static boolean PersonsAreTheSame(Person personOne, Person personTwo)
    {
        return personOne.equals(personTwo);
    }

    public static String GetPersonDataLine(Person person)
    {
        return person.toString();
    }
}