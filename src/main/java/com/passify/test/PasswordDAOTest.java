package com.passify.test;

import com.passify.model.PasswordDAO;
import com.passify.model.PasswordModel;
import com.passify.utils.JDBC_Connector;

import java.sql.Connection;
import java.sql.SQLException;
//import java.util.List;
//import java.util.Optional;
//
//public class PasswordDAOTest {
//
//    private Connection connection;
//    private PasswordDAO passwordDAO;
//    private String passwordId; // Store the password ID for subsequent tests
//
//    public static void main(String[] args) {
//        PasswordDAOTest test = new PasswordDAOTest();
//        test.runTests();
//        JDBC_Connector.closeConnection(); // Close the connection after tests are done
//    }
//
//    public void runTests() {
//        try {
//            setup();
//            testAddPassword(); // Using JohnDoe's userId
//            testGetPasswordById();
//            testUpdatePassword();
//            testDeletePassword();
//            testGetAllPasswordsForUser(); // Using JohnDoe's userId
//        } catch (SQLException e) {
//            System.err.println("Setup error: " + e.getMessage());
//        }
//    }
//
//    private void setup() throws SQLException {
//        connection = JDBC_Connector.getConnection(); // Use the JDBC_Connector class to get the connection
//        createTestTable();
//        passwordDAO = new PasswordDAO(connection);
//    }
//
//    private void createTestTable() throws SQLException {
//        String createTableSQL = "CREATE TABLE IF NOT EXISTS Password (" +
//                "password_id VARCHAR(36) PRIMARY KEY," +
//                "user_id VARCHAR(36)," +
//                "encrypted_password VARCHAR(255)," +
//                "encryption_salt VARCHAR(255)," +
//                "category_id VARCHAR(36)," +
//                "app_name VARCHAR(255)," +
//                "app_username VARCHAR(255)," +
//                "app_url VARCHAR(255)," +
//                "app_email VARCHAR(255)," +
//                "app_notes VARCHAR(255)," +
//                "password_state VARCHAR(20)," +
//                "isFavourite BOOLEAN," +
//                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
//                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
//                ")";
//        try (var statement = connection.createStatement()) {
//            statement.execute(createTableSQL);
//        }
//    }
//
//    private void testAddPassword() {
//        try {
//            PasswordModel password = new PasswordModel();
//            password.setUserId("fb0b80a4-e11b-46c5-a6b8-1cfa42a23929"); // JohnDoe's user ID
//            password.setAppName("test_app_name");
//            password.setAppUsername("test_app_username");
//            password.setAppUrl("http://testapp.com");
//            password.setAppEmail("testemail@example.com");
//            password.setAppNotes("Test notes");
//            password.setCategoryId("1e7a3c61-3eb1-4f3b-8de5-fce7aa6f6213"); // Work category ID
//            password.setPasswordState("saved");
//            password.setFavourite(false);
//
//            boolean result = passwordDAO.addPassword(password, "test_encryption_password");
//            if (result) {
//                passwordId = password.getPasswordId(); // Store the ID for later tests
//            }
//            System.out.println("Test Add Password for JohnDoe: " + (result ? "Passed" : "Failed"));
//        } catch (SQLException e) {
//            System.err.println("Error adding password: " + e.getMessage());
//        }
//    }
//
//    private void testGetPasswordById() {
//        try {
//            Optional<PasswordModel> retrievedPassword = passwordDAO.getPasswordById(passwordId, "test_encryption_password");
//            System.out.println("Test Get Password By ID: " +
//                    (retrievedPassword.isPresent() ? "Passed" : "Failed"));
//        } catch (SQLException e) {
//            System.err.println("Error retrieving password: " + e.getMessage());
//        }
//    }
//
//    private void testUpdatePassword() {
//        try {
//            // Update the password only if it was successfully added
//            if (passwordId != null) {
//                Optional<PasswordModel> optionalPassword = passwordDAO.getPasswordById(passwordId, "test_encryption_password");
//                if (optionalPassword.isPresent()) {
//                    PasswordModel password = optionalPassword.get();
//                    password.setEncryptedPassword("new_encrypted_password");
//                    boolean result = passwordDAO.updatePassword(password);
//                    System.out.println("Test Update Password: " + (result ? "Passed" : "Failed"));
//                } else {
//                    System.out.println("Test Update Password: Failed - Password not found");
//                }
//            } else {
//                System.out.println("Test Update Password: Failed - Password ID is null");
//            }
//        } catch (SQLException e) {
//            System.err.println("Error updating password: " + e.getMessage());
//        }
//    }
//
//    private void testDeletePassword() {
//        try {
//            // Delete the password only if it was successfully added
//            if (passwordId != null) {
//                boolean result = passwordDAO.deletePassword(passwordId);
//                System.out.println("Test Delete Password: " + (result ? "Passed" : "Failed"));
//            } else {
//                System.out.println("Test Delete Password: Failed - Password ID is null");
//            }
//        } catch (SQLException e) {
//            System.err.println("Error deleting password: " + e.getMessage());
//        }
//    }
//
//    private void testGetAllPasswordsForUser() {
//        try {
//            // Retrieve all passwords for the user
//            Optional<List<PasswordModel>> passwords = passwordDAO.getAllPasswordsForUser("fb0b80a4-e11b-46c5-a6b8-1cfa42a23929"); // JohnDoe's user ID
//            System.out.println("Test Get All Passwords For User: " +
//                    (passwords.isPresent() && !passwords.get().isEmpty() ? "Passed" : "Failed"));
//        } catch (SQLException e) {
//            System.err.println("Error retrieving passwords: " + e.getMessage());
//        }
//    }
//}
