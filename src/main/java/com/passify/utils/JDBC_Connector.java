package com.passify.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBC_Connector {

    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/PasswordManager";  // MySQL URL with database name
    private static final String USER = "root";  // MySQL username
    private static final String PASSWORD = "anish2004";  // MySQL password

    // Singleton connection instance to ensure only one connection is used throughout
    private static Connection connection;

    /**
     * This method provides a connection to the MySQL database.
     * It uses a singleton pattern to ensure only one connection is open at a time.
     *
     * @return Connection object to the database
     */
    public static Connection getConnection() {
        if (connection == null) {  // Only establish a new connection if none exists
            try {
                // Load the MySQL JDBC driver (required to communicate with MySQL)
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Establish the connection to the database using URL, username, and password
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connection established successfully.");
            } catch (ClassNotFoundException | SQLException e) {
                // Handle possible exceptions: ClassNotFoundException if driver not found,
                // and SQLException if connection fails
                e.printStackTrace();
                System.out.println("Failed to connect to the database.");
            }
        }
        return connection;  // Return the connection object
    }

    /**
     * Closes the current connection to the database if it exists.
     */
    public static void closeConnection() {
        if (connection != null) {  // Check if a connection is open
            try {
                connection.close();  // Close the connection
                connection = null;   // Reset the connection instance to null after closing
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                // Handle any exceptions during the connection closing process
                e.printStackTrace();
            }
        }
    }
}
