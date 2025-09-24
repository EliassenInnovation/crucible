package com.eliassen.crucible.db.objects;

public class DBInfo
{
    private String driverString;
    private String protocol;
    private String port;
    private String hostName;
    private String dbName;
    private String password;
    private String userName;
    private DBType dbType;
    private String tnsEntryName;
    private boolean activeDirectory;
    private boolean encrypt;
    private boolean trustServerCertificate;

    public String getDriverString() {
        return driverString;
    }

    public void setDriverString(String driverString) {
        this.driverString = driverString;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public DBType getDbType() {
        return dbType;
    }

    public void setDbType(DBType dbType) {
        this.dbType = dbType;
    }

    public String getTnsEntryName() {
        return tnsEntryName;
    }

    public void setTnsEntryName(String tnsEntryName) {
        this.tnsEntryName = tnsEntryName;
    }

    public boolean getActiveDirectory() {
        return activeDirectory;
    }

    public void setActiveDirectory(boolean activeDirectory) {
        this.activeDirectory = activeDirectory;
    }

    public boolean getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public boolean getTrustServerCertificate() {
        return trustServerCertificate;
    }

    public void setTrustServerCertificate(boolean trustServerCertificate) {
        this.trustServerCertificate = trustServerCertificate;
    }
}
