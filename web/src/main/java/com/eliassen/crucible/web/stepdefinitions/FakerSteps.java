package com.eliassen.crucible.web.stepdefinitions;

import com.eliassen.crucible.core.helpers.DataGenerator;
import com.eliassen.crucible.core.helpers.RandomValues;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;

import io.cucumber.java.en.And;

public class FakerSteps 
{
	@And("I enter a random pokemon into (?:the )?\"([^\"]*)\"$")
	public void iEnterARandomPokemonInto(String elementName)
	{
		String randomPokemon = RandomValues.getRandomPokemon();
		CurrentPage.enterText(elementName, randomPokemon);
	}

	@And("I enter a random SSN into (?:the )?\"([^\"]*)\"$")
	public void iEnterARandomSSNInto(String elementName)
	{
		String randomSSN = DataGenerator.getRandomSSN();
		CurrentPage.enterText(elementName, randomSSN);
	}

	@And("I enter a random title into (?:the )?\"([^\"]*)\"$")
	public void iEnterARandomTitleInto(String elementName)
	{
		String randomOccupation = DataGenerator.getRandomTitle();
		CurrentPage.enterText(elementName, randomOccupation);
	}

	@And("I enter a random phone number into (?:the )?\"([^\"]*)\"$")
	public void iEnterARandomPhoneNumberInto(String elementName)
	{
		String randomPhoneNumber = DataGenerator.getRandomPhoneNumber();
		CurrentPage.enterText(elementName, randomPhoneNumber);
	}

	@And("I enter a random 10 digit phone number into (?:the )?\"([^\"]*)\"$")
	public void iEnterARandom10DigitPhoneNumberInto(String elementName) {
		String randomPhoneNumber = DataGenerator.getRandom10DigitPhoneNumber();
		CurrentPage.enterText(elementName, randomPhoneNumber);
	}

	@And("I enter a random email address into (?:the )?\"([^\"]*)\"$")
	public void iEnterARandomEmailAddressInto(String elementName)
	{
		String randomEmailAddress = DataGenerator.getRandomEmail();
		CurrentPage.enterText(elementName, randomEmailAddress);
	}
}
