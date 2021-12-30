package com.dgangan.avcom.db;

import org.apache.commons.dbcp2.BasicDataSource;
import util.PropertiesLoader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBCPDataSource {

    private static BasicDataSource ds = null;
    private static final String propertiesFile = "src/resources/database.properties";

    public static void createConnectionPool() throws IOException{
        if(ds == null){
            Properties dbProps = PropertiesLoader.loadPropertiesFile(propertiesFile);
            ds = new BasicDataSource();
            ds.setUrl(dbProps.getProperty("db_url"));
            ds.setUsername(dbProps.getProperty("db_user"));
            ds.setPassword(dbProps.getProperty("db_password"));
            ds.setMinIdle(5);
            ds.setMaxIdle(10);
            ds.setMaxOpenPreparedStatements(100);
        }
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    private DBCPDataSource(){ }
}
