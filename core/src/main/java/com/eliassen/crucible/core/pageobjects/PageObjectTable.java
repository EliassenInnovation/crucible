package com.eliassen.crucible.core.pageobjects;

import java.util.HashMap;

public class PageObjectTable extends HashMap<String, String>
{
	private static final long serialVersionUID = -5889616561901598599L;

	@Override
	public String put(String key, String value)
	{
		return super.put(key.toLowerCase(), value);
	}

	@Override
	public String get(Object key) { return super.get(key.toString().toLowerCase()); }

	public boolean containsKey(String key) {
		return super.containsKey(key.toLowerCase());
	}
}
