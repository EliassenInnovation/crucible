package com.eliassen.crucible.core.tests;

import com.eliassen.crucible.core.helpers.DataGenerator;
import com.eliassen.crucible.core.helpers.RegexValidator;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DataGeneratorTests
{
	@Test
	public void test_HobbitQuote()
	{
		String quote = DataGenerator.getHobbitQuote();
		assertFalse(quote.isEmpty());
	}

	@Test
	public void test_getRandomName_NameContainsASpace()
	{
		String name = DataGenerator.getRandomFunnyName();
		assertTrue(name.contains(" "));
	}

	@Test
	public void randomSSN_isCorrectFormat()
	{
		String ssn = DataGenerator.getRandomSSN();
		assertTrue(ssn.matches(RegexValidator.SSN_PATTERN));
	}
}
