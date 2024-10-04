//package com.passify.test;
//
//import com.passify.model.UserDAO;
//import com.passify.model.UserModel;
//import com.passify.utils.Hashing;
//import com.passify.utils.PasswordGenerator;
//import com.passify.utils.SaltGenerator;
//import com.passify.utils.JDBC_Connector;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.security.NoSuchAlgorithmException;
//import java.security.spec.InvalidKeySpecException;
//
//public class UserDAOTest {
//
//    private UserDAO userDAO;
//
//    public static void main(String[] args) {
//        UserDAOTest test = new UserDAOTest();
//        test.setUp();
//        test.testCreateUser();
//        test.testGetUserByEmail();
//        test.testUpdateUser();
//        test.testDeleteUser();
//        test.cleanUp();
//    }
//
//    // Set up the UserDAO with the database connection from JDBC_Connector
//    public void setUp() {
//        try {
//            Connection connection = JDBC_Connector.getConnection();
//            if (connection != null) {
//                // Create the User table if it doesn't exist
//                connection.createStatement().execute("CREATE TABLE IF NOT EXISTS User (" +
//                        "user_id VARCHAR(255) PRIMARY KEY, " +
//                        "user_name VARCHAR(255), " +
//                        "hashed_password VARCHAR(255), " +
//                        "hash_salt VARCHAR(255), " +
//                        "user_email VARCHAR(255), " +
//                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
//                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
//                        ")");
//                userDAO = new UserDAO(connection);
//            } else {
//                System.out.println("Database connection is not available.");
//            }
//        } catch (SQLException e) {
//            System.err.println("Error setting up the database: " + e.getMessage());
//        }
//    }
//
//    // Test creating a user
//    public void testCreateUser() {
//        try {
//            // Generate a password and salt
//            String generatedPassword = PasswordGenerator.generatePassword();
//
//            // Create the user
//            UserModel newUser = userDAO.createUser("JohnDoe", generatedPassword, "john.doe@example.com"); // only 3 parameters
//            if (newUser != null) {
//                System.out.println("User created: " + newUser);
//            } else {
//                System.out.println("User creation failed.");
//            }
//        } catch (Exception e) {
//            System.err.println("Error during user creation: " + e.getMessage());
//        }
//    }
//
//    // Test retrieving a user by email
//    public void testGetUserByEmail() {
//        UserModel user = userDAO.getUserByEmail("john.doe@example.com").orElse(null);
//        if (user != null) {
//            System.out.println("User retrieved by email: " + user);
//        } else {
//            System.out.println("User not found by email.");
//        }
//    }
//
//    // Test updating a user
//    public void testUpdateUser() {
//        UserModel user = userDAO.getUserByEmail("john.doe@example.com").orElse(null);
//        if (user != null) {
//            boolean updated = userDAO.updateUser(user.getUserId(), "JaneDoe", user.getHashedPassword(), user.getHashSalt(), "jane.doe@example.com");
//            System.out.println("User update " + (updated ? "successful" : "failed"));
//        } else {
//            System.out.println("User not found to update.");
//        }
//    }
//
//    // Test deleting a user
//    public void testDeleteUser() {
//        UserModel user = userDAO.getUserByEmail("jane.doe@example.com").orElse(null);
//        if (user != null) {
//            boolean deleted = userDAO.deleteUser(user.getUserId());
//            System.out.println("User deletion " + (deleted ? "successful" : "failed"));
//        } else {
//            System.out.println("User not found to delete.");
//        }
//    }
//
//    // Clean up and close the connection
//    public void cleanUp() {
//        try {
//            JDBC_Connector.closeConnection();
//            System.out.println("Database cleaned up.");
//        } catch (Exception e) {
//            System.err.println("Error cleaning up the database: " + e.getMessage());
//        }
//    }
//}
