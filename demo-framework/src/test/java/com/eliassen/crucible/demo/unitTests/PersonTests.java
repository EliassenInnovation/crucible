package com.eliassen.crucible.demo.unitTests;

import com.eliassen.crucible.demo.helpers.functions.PersonFunctions;
import com.eliassen.crucible.demo.shared.testObjects.Person;
import org.junit.Test;

import static org.junit.Assert.*;

public class PersonTests {

    Person resultPerson = new Person("z", "c", "345983465");
    static Person person = new Person("Tony", "Smith", "8675309");

    @Test
    public void validateChangeLastName()
    {
        String testLastName = "Watson";
        PersonFunctions.ChangeLastName(person, testLastName);
        assertEquals(person.getLastName(), testLastName);
    }

    @Test
    public void validateChangeLastNameWithSpecialCharacters()
    {
        String testLastNameSpecialCharacters = "W@tS0n";
        PersonFunctions.ChangeLastName(person, testLastNameSpecialCharacters);
        assertEquals(person.getLastName(), testLastNameSpecialCharacters);
    }

    @Test
    public void validateChangeFirstName()
    {
        String testFirstName = "Austin";
        person = PersonFunctions.ChangeFirstName(person, testFirstName);
        assertEquals(person.getFirstName(), testFirstName);
    }

    @Test
    public void validateChangeFirstNameWithSpecialCharacters()
    {
        String testFirstName = "$@M";
        person = PersonFunctions.ChangeFirstName(person, testFirstName);
        assertEquals(person.getFirstName(), testFirstName);
    }

    @Test
    public void getFirstNamePass() {
        assertEquals("z", resultPerson.getFirstName());
    }

    @Test
    public void getFirstNameFail() {
        assertNotEquals("a", resultPerson.getFirstName());
    }

    @Test
    public void setFirstNameTest()
    {
        Person p = new Person();
        p.setFirstName("Adam");
        assertEquals("Adam", p.getFirstName());
    }

    @Test
    public void getPersonsFirstNameWithConstructor()
    {
        Person p = new Person("Bob", "Smith", "1234");
        assertEquals("Bob", p.getFirstName());
    }

    @Test
    public void getPersonsLastNameWithConstructor()
    {
        Person p = new Person("Bob", "Smith", "1234");
        assertEquals("Smith", p.getLastName());
    }

    @Test
    public void setLastNameTest()
    {
        Person p = new Person();
        p.setLastName("Young");
        assertEquals("Young", p.getLastName());
    }

    @Test
    public void getLastNamePass()
    {
        assertEquals("c", resultPerson.getLastName());
    }

    @Test
    public void getPhoneNumberPass()
    {
        assertEquals("345983465", resultPerson.getPhoneNumber());
    }

    @Test
    public void getPhoneNumberFail() {
        assertNotEquals("1234567890", resultPerson.getPhoneNumber());
    }

    @Test
    public void testEqualsPass() {
        Person expectedPerson = new Person("z", "c", "345983465");
        assertTrue(expectedPerson.equals(resultPerson));
    }

    @Test
    public void equalsFail()
    {
        Person expectedPerson = new Person("a", "b", "1118675309");
        assertFalse(expectedPerson.equals(resultPerson));
    }

    @Test
    public void setPhoneNumberPass()
    {
        String originalPhoneNumber = "1111111111";
        String newPhoneNumber = "2222222222";

        Person newPerson = new Person("a", "b", originalPhoneNumber);
        newPerson.setPhoneNumber(newPhoneNumber);

        assertEquals(newPhoneNumber, newPerson.getPhoneNumber());
    }

    @Test
    public void setPhoneNumberFail()
    {
        String originalPhoneNumber = "1111111111";
        String newPhoneNumber = "2222222222";

        Person newPerson = new Person("a", "b", originalPhoneNumber);
        newPerson.setPhoneNumber(newPhoneNumber);

        assertNotEquals(originalPhoneNumber, newPerson.getPhoneNumber());
    }


    @Test
    public void getFullNamePass()
    {
        Person fullNameTestPerson = new Person("Stunt", "Fair-child", "9999999999");
        assertEquals("Stunt", fullNameTestPerson.getFirstName());
        assertEquals("Fair-child", fullNameTestPerson.getLastName());
        assertEquals("Stunt Fair-child", fullNameTestPerson.getFullName());
    }

    @Test
    public void getFullNameFail()
    {
        Person fullNameTestPerson = new Person(null, null, null);
        assertNotEquals(null, fullNameTestPerson.getFirstName());
        assertNotEquals(null, fullNameTestPerson.getLastName());
        assertNotEquals(null, fullNameTestPerson.getFullName());
    }

    @Test
    public void toStringPass()
    {
        Person toStringTestPerson = new Person("Stunt", "Fair-child", "9999999999");
        assertEquals("Name: Stunt Fair-child | Phone number: 9999999999", toStringTestPerson.toString());
    }

    @Test
    public void toStringFail()
    {
        Person toStringTestPerson = new Person("Stunt", "Fair-child", "9999999999");
        assertNotEquals("Name: Fair-child Stunt | Phone number: 9999999999", toStringTestPerson.toString());
        assertNotEquals(null, toStringTestPerson.toString());
    }

    @Test
    public void validateChangePhoneNumber()
    {
        String updatePhoneNumber = "8193346788";
        Person person = new Person("Prathiksha", "testing", "6247834566");
        PersonFunctions.ChangePhoneNumber(person, updatePhoneNumber);
        assertEquals(person.getPhoneNumber(), updatePhoneNumber);
    }

    @Test
    public void validateChangePhoneNumberWithSpecialCharacters()
    {
        String phoneNumberWithSpecialCharacters = "819334#$$";
        Person person = new Person("Prathiksha", "testing", "6247834566");
        PersonFunctions.ChangePhoneNumber(person, phoneNumberWithSpecialCharacters);
        assertEquals(person.getPhoneNumber(), phoneNumberWithSpecialCharacters);
    }
}
