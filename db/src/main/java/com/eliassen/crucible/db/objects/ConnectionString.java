package com.eliassen.crucible.db.objects;

public class ConnectionString
{
    public static final String HOST_NAME = "host_name";
    public static final String DB_NAME = "db_name";
    public static final String ACTIVE_DIRECTORY = "active_directory";
    public static final String TNS_ENTRY_NAME = "tns_entry_name";
    public static final String ENCRYPT = "encrypt";
    public static final String TRUST_SERVER_CERTIFICATE = "trustServerCertificate";

    String host_name;
    String db_name;
    boolean trustServerCertificate;
    boolean encrypt;

    boolean active_directory;
    String tns_entry_name;
    boolean noAuthentication;

    public String getHost_name() {
        return host_name;
    }

    public void setHost_name(String host_name) {
        this.host_name = host_name;
    }

    public String getDb_name() {
        return db_name;
    }

    public void setDb_name(String db_name) {
        this.db_name = db_name;
    }

    public boolean isTrustServerCertificate() {
        return trustServerCertificate;
    }

    public void setTrustServerCertificate(boolean trustServerCertificate) {
        this.trustServerCertificate = trustServerCertificate;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypted) {
        this.encrypt = encrypt;
    }

    public String getTns_entry_name() {
        return tns_entry_name;
    }

    public void setTns_entry_name(String tnsEntryName) {
        this.tns_entry_name = tnsEntryName;
    }

    public boolean isActive_directory() {
        return active_directory;
    }

    public void setActive_directory(boolean active_directory) {
        this.active_directory = active_directory;
    }

    public boolean isNoAuthentication(){
        return noAuthentication;
    }

    public void setNoAuthentication(boolean noAuthentication){
        this.noAuthentication = noAuthentication;
    }
}
