package com.eliassen.crucible.core.stepdefinitions;

import com.eliassen.crucible.core.helpers.DataGenerator;
import com.eliassen.crucible.core.sharedobjects.MasterMind;

import io.cucumber.java.en.And;

public class FakerSteps 
{
	@And("^I think of a Hobbit quote and remember it as \"(.*)\"$")
	public void iThinkOfAHobbitQuoteAndRememberItAs(String key) 
	{
		String hobbitQuote = DataGenerator.getHobbitQuote();
		MasterMind.store(key, hobbitQuote);
	}

	//TODO - specific
//	@And("I enter a random pokemon into \"(.*)\"$")
//	public void iEnterARandomPokemonInto(String elementName)
//	{
//		String randomPokemon = RandomValues.getRandomPokemon();
//		CurrentObjectBase.enterText(elementName, randomPokemon);
//	}
//
//	@And("I enter a random SSN into \"(.*)\"$")
//	public void iEnterARandomSSNInto(String elementName)
//	{
//		String randomSSN = DataGenerator.getRandomSSN();
//		CurrentObjectBase.enterText(elementName, randomSSN);
//	}

	@And("I create a random first and last name$")
	public void iCreateARandomFirstAndLastName()
	{
		DataGenerator.generateRandomName();
	}

	//TODO - specific
//	@And("I enter a random title into \"(.*)\"$")
//	public void iEnterARandomTitleInto(String elementName)
//	{
//		String randomOccupation = DataGenerator.getRandomTitle();
//		CurrentObjectBase.enterText(elementName, randomOccupation);
//	}
//
//	@And("I enter a random phone number into \"(.*)\"$")
//	public void iEnterARandomPhoneNumberInto(String elementName)
//	{
//		String randomPhoneNumber = DataGenerator.getRandomPhoneNumber();
//		CurrentObjectBase.enterText(elementName, randomPhoneNumber);
//	}
//
//	@And("I enter a random 10 digit phone number into \"(.*)\"$")
//	public void iEnterARandom10DigitPhoneNumberInto(String elementName) {
//		String randomPhoneNumber = DataGenerator.getRandom10DigitPhoneNumber();
//		CurrentObjectBase.enterText(elementName, randomPhoneNumber);
//	}
//
//	@And("I enter a random email address into \"(.*)\"$")
//	public void iEnterARandomEmailAddressInto(String elementName)
//	{
//		String randomEmailAddress = DataGenerator.getRandomEmail();
//		CurrentObjectBase.enterText(elementName, randomEmailAddress);
//	}
}
