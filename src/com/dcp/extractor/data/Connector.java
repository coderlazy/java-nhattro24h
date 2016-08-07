/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dcp.extractor.data;

import java.sql.*;
import java.math.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LazyCode
 */
public class Connector {

    private static Connector connector = null;
    private static final String user = "lazycoder";
    private static final String password = "1";
    private static final String port = "3000";
    private static Connection connection=null;
    
    private Connector(){
        
    }

    private static Connection ConnectDB() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        String url = "jdbc:mysql://127.0.0.1:3000/nhatro24";
        // Load the Connector/J driver
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        // Establish connection to MySQL
        connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return connection;
    }

    public static Connector getInstance() {
        try{
            if (connector == null) {
                connector = new Connector();
                connection = ConnectDB();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return connector;
    }

    public LocationData runQuery(String query, String column1, String column2) {
        Statement stmt = null;
        ResultSet rs = null;
        LocationData locationData = null;
        try {
            stmt = this.connection.createStatement();
            if (stmt.execute(query)) {
                rs = stmt.getResultSet();
                while (rs.next()) {
                    locationData = new LocationData();
                    locationData.setId(rs.getInt(column1));
                    locationData.setName(rs.getString(column2));
                }
            }
            // Now do something with the ResultSet ....
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                } // ignore

                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                } // ignore

                stmt = null;
            }
        }
        return locationData;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        this.connection = ConnectDB();
    }
}
