package com.eliassen.crucible.db.stepDefinitions;

import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.db.helpers.DBFileHelper;
import com.eliassen.crucible.db.helpers.DBJsonHelper;
import com.eliassen.crucible.db.helpers.DriverHelper;
import com.eliassen.crucible.db.main.CentralCommand;
import com.eliassen.crucible.db.main.DBObject;
import com.eliassen.crucible.db.objects.ConnectionString;
import com.eliassen.crucible.db.objects.DBInfo;
import com.eliassen.crucible.db.objects.DBType;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;

import java.sql.SQLException;

public class BasicSteps
{
    @Before
    public void setScenario(Scenario scenario)
    {
        CentralCommand.setScenario(scenario);
    }

    @Given("^I connect to SQL Server as the \"(.*)\" user$")
    public void iConnectToTheSQLServerDBAsTheUser(String user)
    {
        CentralCommand.setDBObject(getSqlServerShared(user, CentralCommand.DEFAULT));
    }

    @Given("I connect to the {string} SQL Server as the {string} user")
    public void iConnectToTheSqlServerAsTheUser(String dbName, String user)
    {
        CentralCommand.setDBObject(dbName,getSqlServerShared(user,dbName));
    }

    @Given("I connect to the MYSQL Server as the {string} user")
    public void iConnectToTheMySqlServerAsTheUser(String user)
    {
        String usersJsonPath = DBObject.usersJSONPath;
        String environmentName = SystemHelper.getEnvironment();
        ConnectionString connectionString = DBJsonHelper.getConnectionString(environmentName, CentralCommand.DEFAULT);
        String userName = DBJsonHelper.getUserUsername(user, usersJsonPath);
        String password = DBJsonHelper.getUserPassword(user, usersJsonPath, true);
        CentralCommand.setDBObject(new DBObject(DBType.MY_SQL, connectionString.getHost_name(), connectionString.getDb_name(), userName, password));
    }

    private DBObject getSqlServerShared(String user, String dbName)
    {
        String environmentName = SystemHelper.getEnvironment();
        String specificDbName = "";

        if(dbName.equals(CentralCommand.DEFAULT))
        {
            specificDbName = environmentName;
        }
        else
        {
            specificDbName = DBJsonHelper.getConnectionStringName(environmentName,dbName);
        }

        DBInfo info = DriverHelper.getInfo(DBType.MS_SQL_SERVER);
        ConnectionString connectionString = DBJsonHelper.getConnectionString(environmentName, dbName);

        info.setActiveDirectory(connectionString.isActive_directory());
        info.setEncrypt(connectionString.isEncrypt());
        info.setTrustServerCertificate(connectionString.isTrustServerCertificate());

        String usersJsonPath = DBObject.usersJSONPath;
        String hostName = connectionString.getHost_name();

        if(!hostName.startsWith("//"))
        {
            if(hostName.startsWith("/"))
            {
                hostName = "/" + hostName;
            }
            else
            {
                hostName = "//" + hostName;
            }
        }
        info.setHostName(hostName);

        info.setDbName(connectionString.getDb_name());
        info.setUserName(DBJsonHelper.getUserUsername(user, usersJsonPath, specificDbName));
        info.setPassword(DBJsonHelper.getUserPassword(user, usersJsonPath, true, specificDbName));

        return new DBObject(info);
    }

    @Given("^I connect to SQL Server using active directory$")
    public void iConnectToTheSQLServerUsingActiveDirectory(String user)
    {
        CentralCommand.setDBObject(getSqlServerShared(user, CentralCommand.DEFAULT));
    }

    @Given("^I connect to Oracle DB as the \"(.*)\" user$")
    public void iConnectToTheOracleDBAsTheUser(String user)
    {
        String usersJsonPath = DBObject.usersJSONPath;
        String environmentName = SystemHelper.getEnvironment();
        ConnectionString connectionString = DBJsonHelper.getConnectionString(environmentName, CentralCommand.DEFAULT);
        String hostName = connectionString.getHost_name();
        String dbName = connectionString.getDb_name();
        String userName = DBJsonHelper.getUserUsername(user, usersJsonPath);
        String password = DBJsonHelper.getUserPassword(user, usersJsonPath, true);
        CentralCommand.setDBObject(new DBObject(DBType.ORACLE,hostName, dbName, userName, password));
    }

    @Given("I connect to Oracle DB using the TNS entry and the {string} user")
    public void iConnectToTheOracleDBUSingTheTNSEntryAndTheUser(String user)
    {
        String usersJsonPath = DBObject.usersJSONPath;
        String environmentName = SystemHelper.getEnvironment();
        ConnectionString connectionString = DBJsonHelper.getConnectionString(environmentName, CentralCommand.DEFAULT);
        String tnsEntryName = connectionString.getTns_entry_name();
        String userName = DBJsonHelper.getUserUsername(user, usersJsonPath);
        String password = DBJsonHelper.getUserPassword(user, usersJsonPath, true);
        CentralCommand.setDBObject(new DBObject(DBType.ORACLE_TNS,tnsEntryName, userName, password));

        DBFileHelper dbFileHelper = new DBFileHelper();
        dbFileHelper.PutTnsFileAtRoot();
    }

    @Given("I connect to the H2 DB as the {string} user")
    public void iConnectToTheH2DBAsTheUser(String user)
    {
        String usersJsonPath = DBObject.usersJSONPath;
        String environmentName = SystemHelper.getEnvironment();
        ConnectionString connectionString = DBJsonHelper.getConnectionString(environmentName, CentralCommand.DEFAULT);
        String hostName = connectionString.getHost_name();
        String dbName = connectionString.getDb_name();

        String userName = null;
        String password = null;
        if(!connectionString.isNoAuthentication())
        {
            userName = DBJsonHelper.getUserUsername(user, usersJsonPath);
            password = DBJsonHelper.getUserPassword(user, usersJsonPath, true);
        }

        CentralCommand.setDBObject(new DBObject(DBType.H2,hostName, dbName, userName, password));
    }

    @And("^I run the \"(.*)\" query$")
    public void iRunTheQuery(String queryName) throws SQLException {
        CentralCommand.setResults(CentralCommand.db().executeQueryByName(queryName));
    }

    @And("I run the {string} query against the {string} database")
    public void iRunTheQueryAgainstTheDatabase(String queryName, String databaseName) throws SQLException {
        CentralCommand.setResults(CentralCommand.db(databaseName).executeQueryByName(queryName));
    }
}
