package com.eliassen.crucible.db.helpers;

import com.eliassen.crucible.db.objects.DBInfo;
import com.eliassen.crucible.db.objects.DBType;

public class DriverHelper
{
    public static DBInfo getInfo(DBType dbType)
    {
        DBInfo info = new DBInfo();

        switch(dbType)
        {
            case DERBY:
                info.setDriverString("org.apache.derby.jdbc.EmbeddedDriver");
                info.setProtocol("jdbc:derby:");
                info.setPort(null);
                info.setDbType(DBType.DERBY);
                break;
            case ORACLE:
            case ORACLE_TNS:
                info.setDriverString("oracle.jdbc.driver.OracleDriver");
                info.setProtocol("jdbc:oracle:thin:@");
                info.setPort("1521");
                info.setDbType(dbType);
                break;
            case MS_SQL_SERVER:
                info.setDriverString("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                info.setProtocol("jdbc:sqlserver:");
                info.setPort(null);
                info.setDbType(DBType.MS_SQL_SERVER);
                break;
            case MY_SQL:
                info.setDriverString("com.mysql.cj.jdbc.Driver");
                info.setProtocol("jdbc:mysql:");
                info.setPort("3306");
                info.setDbType(DBType.MY_SQL);
                break;
            case H2:
                info.setDriverString("org.h2.Driver");
                info.setProtocol("jdbc:h2:tcp:");
                info.setPort("1996");
                info.setDbType(DBType.H2);
                break;
            case POSTGRESQL:
                info.setDriverString("org.postgresql.Driver");
                info.setProtocol("jdbc:postgresql://");
                info.setPort("5432");
                info.setDbType(DBType.POSTGRESQL);
        }

        return info;
    }
}
