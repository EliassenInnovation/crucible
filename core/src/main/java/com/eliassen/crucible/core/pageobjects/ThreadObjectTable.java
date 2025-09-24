package com.eliassen.crucible.core.pageobjects;

import java.util.Hashtable;

public class ThreadObjectTable extends Hashtable<String,Object>
{
    @Override
    public Object put(String _key, Object _value)
    {
        if(_value == null)
        {
            _value = "";
        }
        return super.put(_key.toLowerCase(), _value);
    }

    public boolean has(String _key)
    {
        boolean has = false;
        if(this.containsKey(_key) && this.get(_key) != null)
        {
            if(this.get(_key) instanceof String && ((String) this.get(_key)).isEmpty())
            {
                has = false;
            }
            else
            {
                has = true;
            }
        }

        return has;
    }
}
