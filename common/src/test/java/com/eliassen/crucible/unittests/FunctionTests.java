package com.eliassen.crucible.unittests;

import com.eliassen.crucible.common.helpers.Functions;
import io.cucumber.datatable.DataTable;
import io.cucumber.datatable.DataTableTypeRegistry;
import io.cucumber.datatable.DataTableTypeRegistryTableConverter;
import org.junit.Test;

import java.math.BigInteger;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FunctionTests
{
    @Test
    public void test_convertDataTableToHashtable_findExpectedKeysInHashtable()
    {
        DataTable dataTable = getTestDataTable(new String[] {"key|value","name|Matt","position|first"});
        Hashtable<String,String> convertedTable = Functions.convertDataTableToHashtable(dataTable);

        String[] expectedKeys = {"name","position"};
        for(String expectedKey : expectedKeys)
        {
            assertTrue(convertedTable.containsKey(expectedKey));
        }
    }

    @Test
    public void test_convertDataTableToHashtable_findExpectedValuesInHashtable()
    {
        DataTable dataTable = getTestDataTable(new String[] {"key|value","name|Matt","position|first"});
        Hashtable<String,String> convertedTable = Functions.convertDataTableToHashtable(dataTable);

        String[] expectedValues = {"Matt","first"};
        for(String expectedValue : expectedValues)
        {
            assertTrue(convertedTable.containsValue(expectedValue));
        }
    }

    private DataTable getTestDataTable(String[] rawArray)
    {
        List<List<String>> rawValues = new LinkedList<>();
        for(String rawValue : rawArray)
        {
            List<String> rawList = new LinkedList<>();
            String[] values = rawValue.split("[|]");
            rawList.addAll(Arrays.asList(values));
            rawValues.add(rawList);
        }

        DataTableTypeRegistry registry = new DataTableTypeRegistry(Locale.ENGLISH);
        DataTable dataTable = DataTable.create(rawValues, new DataTableTypeRegistryTableConverter(registry));

        return dataTable;
    }

    @Test
    public void pow_test()
    {
        int x = 9;
        int y = 15;
        long product = (long)Math.pow(x,y);
        long bigProduct = myPow(x,y).longValue();
        assertEquals(product,bigProduct);
    }

    private BigInteger myPow(int x, int y)
    {
        BigInteger value;

        if(y == 0)
        {
            value = BigInteger.valueOf(1);
        }
        else if (y == 1)
        {
            value = BigInteger.valueOf(x);
        }
        else if (y == -1)
        {
            value = BigInteger.valueOf(1 / x);
        }
        else
        {
            if (y % 2 == 0)
            {
                value = myPow(x,y/2).multiply(myPow (x,y/2));
            }
            else
            {
                value = myPow(x,(y-1)/2).multiply(myPow(x,(y-1)/2)).multiply(BigInteger.valueOf(x));
            }
        }

        return value;
    }
}
