package com.eliassen.crucible.core.tests;

import com.eliassen.crucible.core.helpers.TestHelperBase;
import org.junit.Test;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;

public class TestHelperBaseTests
{
	String usersJsonPath = "./testUsers.json";

	@Test
	public final void test_load_test_GetUserPassword()
	{
		String expectedPassword = "password";
		
		String testPassword = TestHelperBase.getUserPassword("test", usersJsonPath, false);
		
		assertEquals(expectedPassword, testPassword);
	}

	@Test
	public final void test_load_test_with_environment_GetUserPassword()
	{
		String expectedPassword = "password2";

		String environment = "testEnvironment";

		String testPassword = TestHelperBase.getUserPassword(environment,"test2", usersJsonPath, false);

		assertEquals(expectedPassword, testPassword);
	}
	
	@Test
	public final void test_load_test_GetUserUsername()
	{
		String expectedUsername = "testUser";
		
		String testUserName = TestHelperBase.getUserUsername("test", usersJsonPath);
		
		assertEquals(expectedUsername, testUserName);
	}

	@Test
	public final void test_load_test_with_environment_GetUserUsername()
	{
		String expectedUsername = "testUser2";

		String environment = "testEnvironment";

		String testUserName = TestHelperBase.getUserUsername(environment,"test2", usersJsonPath);

		assertEquals(expectedUsername, testUserName);
	}

	@Test
	public final void test_load_test_with_environment_GetUserUsername_Should_Get_Default_Environment_User()
	{
		String expectedUsername = "testUser";

		String environment = "someOtherEnvironmentName";

		String testUserName = TestHelperBase.getUserUsername(environment,"test", usersJsonPath);

		assertEquals(expectedUsername, testUserName);
	}

	@Test
	public final void test_getMonthNumber() throws ParseException
	{
		int num = TestHelperBase.getMonthNumberFromMonthString("January");

		assertEquals(1,num);
	}
}
