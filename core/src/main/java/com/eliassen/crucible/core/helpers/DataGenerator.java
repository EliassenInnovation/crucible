package com.eliassen.crucible.core.helpers;

import com.eliassen.crucible.core.sharedobjects.MasterMind;
import com.github.javafaker.Faker;

public class DataGenerator
{
	static Faker _faker;
	public static final String RANDOM_FIRST_NAME = "random first name";
	public static final String RANDOM_LAST_NAME = "random last name";
	
	public static Faker faker()
	{
		if (_faker == null)
		{
			_faker = new Faker();
		}
		
		return _faker;
	}
	
	public static String getHobbitQuote()
	{
		return faker().hobbit().quote();
	}

	public static String getRandomFunnyName()
	{
		return faker().funnyName().name();
	}

	/*
	Generates a random funny name and stores
	it as "random first name" and "random last name"
	in the dictionary
	 */
	public static void generateRandomName()
	{
		String fullName = getRandomFunnyName();
		String firstName = fullName.split( " ")[0];
		String lastName = fullName.split(" ")[1];
		MasterMind.store(RANDOM_FIRST_NAME, firstName);
		MasterMind.store(RANDOM_LAST_NAME, lastName);
	}

	public static String getRandomPhoneNumber()
	{
		return faker().phoneNumber().cellPhone();
	}

	public static String getRandom10DigitPhoneNumber()
	{
		return faker().numerify("###").toString() + "-" + faker().numerify("###").toString() + "-" + faker().numerify("####").toString();
	}

	public static String getRandomEmail()
	{
		return faker().internet().emailAddress();
	}

	public static String getRandomTitle()
	{
		return faker().company().profession();
	}

	public static String getRandomSSN()
	{
		return faker().idNumber().ssnValid();
	}
}
