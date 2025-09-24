package com.lightwell.testing.demo.shared.testObjects;

public class Person
{
    private static String firstName;
    private String lastName;
    private String phoneNumber;

    public Person(){}

    public Person(String firstName, String lastName, String phoneNumber)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public static String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getFullName()
    {
        StringBuilder fullName = new StringBuilder();
        if(getFirstName() != null)
        {
            fullName.append(getFirstName());
        }
        if(getLastName() != null)
        {
            if(!fullName.isEmpty())
            {
                fullName.append(" ");
            }
            fullName.append(getLastName());
        }
        return fullName.isEmpty() ? null : fullName.toString();
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public boolean equals(Person otherPerson)
    {
        if(this.firstName.equals(otherPerson.getFirstName()) &&
           this.lastName.equals(otherPerson.getLastName()) &&
           this.phoneNumber.equals(otherPerson.getPhoneNumber()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        StringBuilder stringify = new StringBuilder();
        stringify.append("Name: ");
//        stringify.append(getFullName());
        stringify.append(" | ");
        stringify.append("Phone number: ");
        stringify.append(getPhoneNumber());

        return stringify.toString();
    }
}
