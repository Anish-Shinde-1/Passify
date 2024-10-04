package com.passify.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBC_Connector {
    private static final String URL = "jdbc:mysql://localhost:3306/demopasswordmanager";
    private static final String USER = "root";
    private static final String PASSWORD = "anish2004";

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Load the MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Establish the connection
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connection established successfully.");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to connect to the database.");
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null; // Reset the connection to null after closing
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
