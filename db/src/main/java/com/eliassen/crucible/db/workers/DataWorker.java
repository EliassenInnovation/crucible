package com.eliassen.crucible.db.workers;

import com.eliassen.crucible.db.helpers.Logger;
import com.eliassen.crucible.db.helpers.QueryHelper;
import com.eliassen.crucible.db.helpers.QueryTable;
import com.eliassen.crucible.db.main.CentralCommand;
import com.eliassen.crucible.db.main.DBObject;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import oracle.jdbc.pool.OracleDataSource;
import com.eliassen.crucible.db.objects.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class DataWorker
{
    public final static String NUMBER_OF_ROWS = "number of rows";
    private String dbName;
    private String hostName;
    private String query;
    private DBInfo info;

    private Connection connection = null;

    private Properties properties = new Properties();

    public DataWorker(DBInfo _info)
    {
        info = _info;

        try
        {
            Class.forName(info.getDriverString()).getDeclaredConstructor().newInstance();
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e)
        {
            Logger.logError("could not find driver: " + e.getMessage());
            Logger.logError("could not find driver: " + Arrays.toString(e.getStackTrace()));
        }

        if(info.getUserName() != null) {
            properties.put("user", info.getUserName());
        }
        if(info.getPassword() != null) {
            properties.put("password", info.getPassword());
        }
        this.dbName = info.getDbName();
        this.hostName = info.getHostName();
    }

    private int getScrollType()
    {
        switch(info.getDbType())
        {
            case MS_SQL_SERVER:
                return ResultSet.TYPE_SCROLL_SENSITIVE;
            default:
                return ResultSet.TYPE_SCROLL_INSENSITIVE;
        }
    }

    private void resultSetClose(ResultSet _set)
    {
        try
        {
            if(_set != null)
            {
                _set.close();
            }
        } catch (SQLException e)
        {
            Logger.logError("resultSetClose: " + e.getMessage());
            Logger.logError("resultSetClose: " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void createConnection()
    {
        try
        {
            if(info.getActiveDirectory())
            {
                SQLServerDataSource ds = new SQLServerDataSource();
                ds.setServerName(hostName);
                ds.setDatabaseName(dbName);

                if(CentralCommand.hasDbAccessToken())
                {
                    ds.setAccessToken(CentralCommand.getDbAccessToken());
                }
                else
                {
                    ds.setAuthentication("ActiveDirectoryInteractive");
                }
                ds.setEncrypt(info.getEncrypt());
                ds.setTrustServerCertificate(info.getTrustServerCertificate());
                connection = ds.getConnection();
            }
            else if(info.getDbType() == DBType.ORACLE_TNS)
            {
                OracleDataSource ods = new OracleDataSource();
                ods.setTNSEntryName(info.getTnsEntryName());
                ods.setUser(info.getUserName());
                ods.setPassword(info.getPassword());
                ods.setDriverType("thin");
                connection = ods.getConnection();
            }
            else
            {
                String connectionString = getConnectionString();
                connection = DriverManager.getConnection(connectionString,
                        properties);
            }
        }
        catch (SQLException e)
        {
            Logger.logError("Failed to createConnection: " + e.getMessage());
        }
    }

    private void checkConnection()
    {
        try
        {
            if (connection == null || connection.isClosed())
            {
                createConnection();
            }
        }
        catch (SQLException e)
        {
            Logger.logError("Failed to checkConnection: " + e.getMessage());
        }
    }

    public Connection getConnection()
    {
        checkConnection();
        return connection;
    }

    public String getConnectionString()
    {
        String connectionString = info.getProtocol() + info.getHostName();
        if(info.getPort() != null)
        {
            connectionString += ":" + info.getPort();
        }
        if(info.getDbType() == DBType.MS_SQL_SERVER)
        {
            connectionString += ";databaseName=" + dbName;
            if(info.getEncrypt())
            {
                connectionString += ";" + ConnectionString.ENCRYPT + "=true";
            }
            if(info.getTrustServerCertificate())
            {
                connectionString += ";" + ConnectionString.TRUST_SERVER_CERTIFICATE + "=true";
            }
        }
        else if (info.getDbType() == DBType.DERBY)
        {
            connectionString = info.getProtocol() + dbName + ";create=true";
        }
        else
        {
            connectionString += "/" + dbName;
        }

        return connectionString;
    }

    public void closeConnection()
    {
        try
        {
            if(connection != null && !connection.isClosed() && !CentralCommand.keepConnectionOpen)
            {
                connection.close();
            }
        }
        catch (SQLException e)
        {
            Logger.logError("closeConnection: " + e.getStackTrace());
        }
    }

    public void shutDownDb()
    {
        if(info.getDbType() == DBType.DERBY)
        {
            String connectionString = info.getProtocol() + dbName + ";shutdown=true";
            try
            {
                DriverManager.getConnection(connectionString);
            } catch (SQLException e)
            {
                //don't need to know the db shutdown
                if(!e.getMessage().contains("shutdown"))
                Logger.logError(e.getMessage());
            }
        }
    }

    public ArrayList<Results> executeQueryByNameAndWhere(String queryName, String whereClause) throws SQLException {
        query = QueryTable.get(queryName);

        query += " " + whereClause;

        ArrayList<Results> resultSets = _executeQuery(query);

        return resultSets;
    }

    private ArrayList<Results> _executeQuery(String query) throws SQLException {
        Results results = new Results();
        ArrayList<Results> resultSets = new ArrayList<Results>();

        ResultSet resultSet = null;
        PreparedStatement getQueryResult = null;

        try
        {
            checkConnection();

            getQueryResult = connection.prepareStatement(query,
                    getScrollType(),
                    ResultSet.CONCUR_UPDATABLE);

            boolean isResultSet = getQueryResult.execute();
            while(isResultSet)
            {
                resultSet = getQueryResult.getResultSet();
                results = resultSetToResults(resultSet);
                resultSets.add(results);
                if(!getQueryResult.isClosed() && !this.connection.isClosed())
                {
                    isResultSet = getQueryResult.getMoreResults();
                }
                else
                {
                    isResultSet = false;
                }
            }
        }
        catch (SQLException e)
        {
            String message = e.getMessage();
            Logger.logError("_executeQuery: " + query
                    + " | " + message);
            throw e;
        }
        finally
        {
            resultSetClose(resultSet);
            preparedStatementClose(getQueryResult);
            closeConnection();
        }

        return resultSets;
    }

    private void preparedStatementClose(PreparedStatement statement) {

        try {
            if(statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Results> executeQueryByName(String queryName) throws SQLException {
        query = QueryTable.get(queryName);

        if(CentralCommand.getScenario().getSourceTagNames().contains(QueryHelper.LOG_QUERY_TAG))
        {
            DBObject.logQuery(queryName, query);
        }

        ArrayList<Results> resultSets = _executeQuery(query);

        return resultSets;
    }

    public void executeNonqueryByName(String queryName)
    {
        query = QueryTable.get(queryName);

        executeNonquery(query);
    }

    public void executeNonquery(String queryString)
    {
        String[] queries = queryString.split(";");
        for(String query : queries)
        {
            PreparedStatement nonquery = null;

            try
            {
                checkConnection();

                nonquery = connection.prepareStatement(query);

                nonquery.executeUpdate();

            }
            catch (SQLException e)
            {
                Logger.logError(e.getMessage());
                e.printStackTrace();
            }
            finally {
                preparedStatementClose(nonquery);
            }
        }

        closeConnection();
    }

    public Results resultSetToResults(ResultSet resultSet)
    {
        Results resultRows = new Results();
        ResultRow row;
        String columnName;
        Object object;

        try
        {
            ResultSetMetaData md = resultSet.getMetaData();

            if(resultSet.getType() != ResultSet.TYPE_FORWARD_ONLY)
            {
                if (resultSet.next())
                {
                    resultSet.first();
                    while (!resultSet.isAfterLast())
                    {
                        row = getRowFromMetaData(md,resultSet);
                        resultRows.add(row);

                        resultSet.next();
                    }
                }
            }
            else
            {
                try
                {
                    while(resultSet.next()) {}
                }
                catch (SQLException s)
                {
                    //throwing error because forward only result set is difficult
                    String x = "";
                }
                String numberOfRows = resultSet.getRow() + "";
                row = new ResultRow();
                row.put(NUMBER_OF_ROWS, numberOfRows);
                resultRows.add(row);
            }
        }
        catch(SQLException | NullPointerException e)
        {
            String message = e.getMessage();
            Logger.logError("ResultSetToResultRows: " + message);
        }

        closeConnection();

        return resultRows;
    }

    private ResultRow getRowFromMetaData(ResultSetMetaData md, ResultSet resultSet) {

        ResultRow row = new ResultRow();
        try {
            for (int c = 1; c < md.getColumnCount() + 1; c++)
            {
                String columnName = md.getColumnName(c);
                Object object = resultSet.getObject(c);

                if (object == null)
                {
                    object = "<null>";
                }

                row.put(columnName, object);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return row;
    }

    public ArrayList<Results> executeQueryWithParameter(String query) throws SQLException {
        ArrayList<Results> resultSets = _executeQuery(query);

        return resultSets;
    }
}
