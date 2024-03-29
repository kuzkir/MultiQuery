/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.kuzkir.multiquery.helper;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import java.sql.Driver;

/**
 *
 * @author kuzkir
 */
public class ConnectionHelper {
    
    public static String getConnectionURL(Driver driver, String host, int port) {
        if(driver instanceof org.postgresql.Driver)
            return getPgConnectionURL(host,port);
        else if (driver instanceof SQLServerDriver)
            return getMsConnectionURL(host, port);
        
        return null;
    }
    
    public static String getConnectionURL(Driver driver, String host, int port, String dbName) {
        if(driver instanceof org.postgresql.Driver)
            return getPgConnectionURL(host,port,dbName);
        else if (driver instanceof SQLServerDriver)
            return getMsConnectionURL(host, port,dbName);
        
        return null;
    }

    public static String getPgConnectionURL(String host, int port) {
        return getPgConnectionURL(host, port, "postgres");
    }

    public static String getPgConnectionURL(String host, int port, String dbName) {
        return String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
    }

    public static String getMsConnectionURL(String host, int port) {
        return String.format("jdbc:sqlserver://%s:%d", host, port);
    }

    public static String getMsConnectionURL(String host, int port, String dbName) {
        return String.format("jdbc:sqlserver://%s:%d;databaseName=%s", host, port, dbName);
    }
}
