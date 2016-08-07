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

    public static Connector connector = new Connector();
    private String user = "lazycoder";
    private String password = "1";
    private String port = "3000";
    public Connection connection;

    public Connector() {

    }

    public Connection ConnectDB() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        String url = "jdbc:mysql://127.0.0.1:3000/nhatro24";
        // Load the Connector/J driver
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        // Establish connection to MySQL
        this.connection = null;
        try {
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return this.connection;
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
                    locationData = new LocationData(rs.getInt(column1), rs.getString(column2));
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

    public static Connector getInstance() {
        return Connector.connector;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        this.connection = ConnectDB();
    }
}
