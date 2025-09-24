package com.lightwell.testing.demo.stepDefinitions;

import com.eliassen.crucible.common.helpers.FileHelper;
import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.core.helpers.DataGenerator;
import com.eliassen.crucible.core.helpers.Logger;
import com.eliassen.crucible.db.helpers.DBJsonHelper;
import com.eliassen.crucible.db.helpers.QueryTable;
import com.eliassen.crucible.db.main.CentralCommand;
import com.eliassen.crucible.db.main.DBObject;
import com.eliassen.crucible.db.objects.DBType;
import com.eliassen.crucible.db.objects.ResultRow;
import com.eliassen.crucible.db.objects.Results;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DbSteps
{
    public static final String PEOPLE = "PEOPLE";
    public static final String ID = "ID";
    public static final String LAST_NAME = "LASTNAME";
    public static final String FIRST_NAME = "FIRSTNAME";
    public static final String PHONE = "PHONE";
    public static final String PEOPLE_INSERT_QUERY = "INSERT INTO PEOPLE(ID,LASTNAME,FIRSTNAME,PHONE) VALUES(%s,'%s','%s','%s');";
    public static final String DB_1_ROWS = "db_1_rows";
    public static final String DB_2_ROWS = "db_2_rows";

    @After("@db_a or @db_b")
    public void releaseConnection()
    {
        if(FileHelper.ensureDirectoryExists("db_a"))
        {
            changeConnection("db_a");
            CentralCommand.db().shutDownDb();
        }
        if(FileHelper.ensureDirectoryExists("db_b"))
        {
            changeConnection("db_b");
            CentralCommand.db().shutDownDb();
        }
    }

    @Before("@db_a or @db_b")
    public void cleanUpApacheDatabases()
    {
        deleteDirectoryIfItExists("db_a");
        deleteDirectoryIfItExists("db_b");
    }

    public void deleteDirectoryIfItExists(String directoryName)
    {
        if(FileHelper.ensureDirectoryExists(directoryName))
        {
            File directory = new File(directoryName);
            try
            {
                FileUtils.deleteDirectory(directory);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Given("I connect to the {string} derby database")
    public void iConnectToTheDerbyDatabase(String dbName)
    {
        String configFilePath = DBObject.configFilePath;
        String hostName = DBJsonHelper.getConnectionString(SystemHelper.getEnvironment(),dbName).getHost_name();
        CentralCommand.setDBObject(new DBObject(DBType.DERBY,hostName, dbName, "user", "password", false));
    }

    @And("I seed {string} with \"{int}\" {string} record(s)")
    public void iSeedWithRecords(String dbName, int numberOfThings, String thingType)
    {
        Results rows = createSomeRecords(numberOfThings, thingType);
        seedDb(dbName,rows);
    }

    @And("I seed {string} with my remembered {string} records")
    public void iSeedWithMyRememberedRecords(String dbName, String thingType)
    {
        Results rows = (Results) CurrentPage.getCurrentThreadObjects().get(thingType.toLowerCase(Locale.ROOT) + "records");
        seedDb(dbName,rows);
    }

    @And("I seed {string} with my remembered {string} records but with \"{int}\" percent chaos")
    public void iSeedWithMyRememberedRecordsButWithPercentChaos(String dbName, String thingType, int chaosPercent)
    {
        Results rows = (Results) CurrentPage.getCurrentThreadObjects().get(thingType.toLowerCase(Locale.ROOT) + "records");

        StringBuilder queryBuilder = new StringBuilder();
        changeConnection(dbName);

        checkThatTableExists(PEOPLE);

        String lastName, firstName, phoneNumber;

        for(ResultRow row : rows)
        {
            //random numbers to introduce chaos
            if(d100() < chaosPercent)
            {
                row = createPeopleRecord(row.getInt(ID));
            }

            queryBuilder.append(String.format(PEOPLE_INSERT_QUERY,row.getString(ID),row.getString(LAST_NAME),
                    row.getString(FIRST_NAME),row.getString(PHONE)));
        }

        QueryTable.getQueryList().put("insert",queryBuilder.toString());
        CentralCommand.db().executeNonqueryByName("insert");
    }

    public int d100()
    {
        Random dice = new Random();
        return dice.nextInt(100) + 1;
    }

    public void changeConnection(String dbName)
    {
        CentralCommand.db().closeConnection();
        iConnectToTheDerbyDatabase(dbName);
    }

    public void seedDb(String dbName, Results rows)
    {
        StringBuilder queryBuilder = new StringBuilder();
        changeConnection(dbName);

        checkThatTableExists(PEOPLE);

        for(ResultRow row : rows)
        {
            queryBuilder.append(String.format(PEOPLE_INSERT_QUERY,row.getString(ID),row.getString(LAST_NAME),
                    row.getString(FIRST_NAME),row.getString(PHONE)));
        }

        QueryTable.getQueryList().put("insert",queryBuilder.toString());
        CentralCommand.db().executeNonqueryByName("insert");
    }

    public void checkThatTableExists(String tableName)
    {
        try
        {
            CentralCommand.db().executeQueryByName("select from " + tableName.toLowerCase(Locale.ROOT));
        }
        catch(SQLException e)
        {
            CentralCommand.db().executeNonqueryByName("create " + tableName.toLowerCase(Locale.ROOT) + " table");
        }
    }

    public Results createSomeRecords(int numberOfThings, String thingType)
    {
        Results rows = new Results();

        for(int x = 0; x < numberOfThings; x++)
        {
            //generate a record for each number of things
            switch (thingType.toUpperCase(Locale.ROOT))
            {
                //default is a PEOPLE record
                default:
                    rows.add(createPeopleRecord(x+1));
                    break;
            }
        }

        CurrentPage.getCurrentThreadObjects().put(thingType.toLowerCase(Locale.ROOT) + "records",rows);

        return rows;
    }

    public ResultRow createPeopleRecord(int id)
    {
        ResultRow row = new ResultRow();
        row = new ResultRow();
        row.put(ID,id);
        DataGenerator.generateRandomName();
        row.put(LAST_NAME, CurrentPage.retrieve("random last name").replace("'",""));
        row.put(FIRST_NAME, CurrentPage.retrieve("random first name").replace("'",""));
        row.put(PHONE,DataGenerator.getRandom10DigitPhoneNumber());

        return row;
    }

    @Then("I check there are records in {string}")
    public void iCheckThereAreRecordsIn(String tableName) throws SQLException
    {
        Results results = CentralCommand.db().executeQueryByName("select from " + tableName.toLowerCase(Locale.ROOT)).get(0);
        assertTrue(results.size() > 0);
    }

    @Then("I grab the {string} records from {string} to {string}")
    public void iGrabTheRecordsFromTo(String thingType, String db1Name, String db2Name) throws SQLException
    {
        //grab table 1 records
        changeConnection(db1Name);
        Results db1Rows = CentralCommand.db().executeQueryByName("select from " + thingType.toLowerCase(Locale.ROOT)).get(0);
        CurrentPage.getCurrentThreadObjects().put(DB_1_ROWS, db1Rows);

        //grab table 2 records
        changeConnection(db2Name);
        Results db2Rows = CentralCommand.db().executeQueryByName("select from " + thingType.toLowerCase(Locale.ROOT)).get(0);
        CurrentPage.getCurrentThreadObjects().put(DB_2_ROWS, db2Rows);
    }

    @Then("I confirm the row counts from both dbs match")
    public void iConfirmTheRowCountsFromBothDbsMatch()
    {
        int db1RowsCount = ((Results)CurrentPage.getCurrentThreadObjects().get(DB_1_ROWS)).size();
        int db2RowsCount = ((Results)CurrentPage.getCurrentThreadObjects().get(DB_2_ROWS)).size();
        assertEquals(db1RowsCount,db2RowsCount);
    }

    @Then("I confirm all columns match across both records sets")
    public void iConfirmAllColumnsMatchAcrossBothRecordsSets()
    {
        Results db1Rows = (Results)CurrentPage.getCurrentThreadObjects().get(DB_1_ROWS);
        Results db2Rows = (Results)CurrentPage.getCurrentThreadObjects().get(DB_2_ROWS);

        //first, organize result sets by id
        Hashtable<Integer,ResultRow> db1RowTable = convertResultsWithIdToHashtable(db1Rows);
        Hashtable<Integer,ResultRow> db2RowTable = convertResultsWithIdToHashtable(db2Rows);

        //then compare column by column across all records
        boolean allMatched = true;
        int failedMatchCount = 0;
        for(int key : db1RowTable.keySet())
        {
            boolean currentRowMatched = true;
            ResultRow db1Row = db1RowTable.get(key);
            for(String column : db1Row.keySet())
            {
                if(!db1Row.getString(column).equals(db2RowTable.get(key).getString(column)))
                {
                    allMatched = false;
                    currentRowMatched = false;
                    Logger.logError("Id: " + key + " | column: " + column + " | db1 value: " + db1Row.getString(column) + " <> db2 value: " + db2RowTable.get(key).getString(column));
                }
            }
            if(!currentRowMatched)
            {
                failedMatchCount++;
            }
        }

        assertTrue("Failed to match " + failedMatchCount + " records", allMatched);
    }

    public Hashtable<Integer, ResultRow> convertResultsWithIdToHashtable(Results results)
    {
        Hashtable<Integer, ResultRow> rowTable = new Hashtable<>();

        for(ResultRow row : results)
        {
            rowTable.put(row.getInt(ID),row);
        }

        return rowTable;
    }
}
