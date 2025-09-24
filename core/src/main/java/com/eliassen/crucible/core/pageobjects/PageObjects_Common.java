package com.eliassen.crucible.core.pageobjects;

public abstract class PageObjects_Common 
{
	public CommonObject[] getCommonObjects()
	{
		return null;
	}
	
	public class CommonObject
	{
		public String key;
		public String value;
		
		public CommonObject(String _key, String _value)
		{
			key = _key;
			value = _value;
		}
	}
}
