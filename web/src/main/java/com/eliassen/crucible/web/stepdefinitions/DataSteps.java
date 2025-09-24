package com.eliassen.crucible.web.stepdefinitions;

import com.eliassen.crucible.web.helpers.NavHelper;
import com.eliassen.crucible.web.helpers.TestHelper;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;

import io.cucumber.java.en.And;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class DataSteps 
{

	@And("^I take (?:the )?\"([^\"]*)\" and enter it into (?:the )?\"([^\"]*)\"$")
	public void iTakeAndEnterItIntoThe(String key, String elementName)
	{
		String retrievedString = CurrentPage.retrieve(key);
		CurrentPage.enterText(elementName, retrievedString);
	}

	@And("^I remember the text from (?:the )?\"([^\"]*)\" as \"(.*)\"$")
	public void iRememberTheTextFromAs(String elementName, String key)
	{
		String rememberedText = TestHelper.getTextFromElement(elementName);
		CurrentPage.store(key, rememberedText);
	}

	@And("^I store the text from (?:the )?\"([^\"]*)\" as \"(.*)\"$")
	public void iStoreTheTextFromAs(String elementName, String key)
	{
		String rememberedText = TestHelper.getTextFromElement(elementName);
		CurrentPage.storePersisted(key, rememberedText);
	}

	/**
	 * @deprecated not sure why this was deprecated
	 * @param elementName
	 * @param key
	 */
	@Deprecated(since="before time began")
	@And("I persist the text from {string} as {string}")
	public void iPersistTheTextFromAs(String elementName, String key)
	{
		String elementText = TestHelper.getTextFromElement(elementName);
		CurrentPage.storePersisted(key,elementText);
	}


	@And("^I enter my remembered \"(.*)\" into (?:the )?\"([^\"]*)\"$")
	public void iEnterMyRememberedIntoThe(String key, String elementName)
	{
		String rememberedValue = CurrentPage.retrieve(key);
		NavHelper.enterText(elementName, rememberedValue);
	}

	@And("^I store the number of rows from (?:the )?\"([^\"]*)\" table as \"(.*)\"$")
	public void iStoreRowsAs(String elementName, String key)
	{
		WebElement table = CurrentPage.element(elementName);
		List<WebElement> rowList = table.findElements(By.className("mat-ripple"));
		int rows = rowList.size();
		String rowString = Integer.toString(rows);

		CurrentPage.storePersisted(key, rowString);
	}
}
