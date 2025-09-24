package com.eliassen.crucible.core.pageobjects;

public interface iPageObject
{
	void store(String key, String value);
	String retrieve(String key);
	abstract String getPageName();

	void fillPageTable();
	String getURL(String environmentName);
}
