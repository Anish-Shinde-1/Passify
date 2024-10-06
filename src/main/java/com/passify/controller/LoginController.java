package com.passify.controller;

import com.passify.model.UserDAO;
import com.passify.model.UserModel;
import com.passify.utils.JDBC_Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class LoginController {

    @FXML
    private Label alertLabel;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField masterPassword;

    @FXML
    private PasswordField reEnteredMasterPassword; // This will be used for re-entering the password during login

    @FXML
    private Button registerButton;

    @FXML
    private TextField userEmail;

    @FXML
    private TextField userName;

    private UserDAO userDAO;
    private Connection connection; // Add a connection variable

    // Initialize the controller with a database connection
    public LoginController() {
        try {
            Connection connection = JDBC_Connector.getConnection();
            this.userDAO = new UserDAO(connection);
            System.out.println("UserDAO initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Database connection error in constructor: " + e.getMessage());
        }
    }

    public void setConnection(Connection connection) throws SQLException {
        this.connection = connection; // Set the connection
        this.userDAO = new UserDAO(connection); // Initialize UserDAO with the provided connection
    }

    // Method to handle login
    @FXML
    private void handleLogin(ActionEvent event) {
        System.out.println("Login button pressed."); // Debug statement

        String username = userName.getText().trim();
        String email = userEmail.getText().trim(); // Use userEmail for login
        String password = masterPassword.getText();
        String reEnteredPassword = reEnteredMasterPassword.getText(); // Get the re-entered password

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || reEnteredPassword.isEmpty()) {
            alertLabel.setText("Please fill in all fields.");
            return;
        }

        // Check if both passwords match
        if (!password.equals(reEnteredPassword)) {
            alertLabel.setText("Passwords do not match. Please re-enter your password.");
            return;
        }

        // Fetch the user by email
        Optional<UserModel> userOpt = userDAO.getUserByEmail(email);
        if (userOpt.isPresent()) {
            UserModel user = userOpt.get();
            try {
                boolean loginSuccess = userDAO.verifyUserPassword(user.getUserId(), password);
                System.out.println("Login attempt for user: " + email); // Debug statement

                if (loginSuccess) {
                    System.out.println("Login successful for user: " + email); // Debug statement
                    // Proceed to the main application or dashboard
                    switchToMainScene(user.getUserId()); // Switch to main.fxml
                } else {
                    alertLabel.setText("Invalid email or password.");
                    System.err.println("Invalid login attempt for user: " + email); // Debug statement
                }
            } catch (Exception e) {
                alertLabel.setText("Error during login: " + e.getMessage());
                System.err.println("Exception during login: " + e.getMessage()); // Debug statement
            }
        } else {
            alertLabel.setText("User not found. Please register.");
            System.out.println("User not found: " + email); // Debug statement
        }
    }

    // Method to handle registration
    @FXML
    private void handleRegister(ActionEvent event) {
        System.out.println("Register button pressed."); // Debug statement
        String username = userName.getText().trim();
        String password = masterPassword.getText();
        String confirmPassword = reEnteredMasterPassword.getText();
        String email = userEmail.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
            alertLabel.setText("Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            alertLabel.setText("Passwords do not match.");
            return;
        }

        // Check if user already exists by email
        Optional<UserModel> existingUserOpt = userDAO.getUserByEmail(email);
        if (existingUserOpt.isPresent()) {
            alertLabel.setText("Email already registered. Please login.");
            System.out.println("Email already registered: " + email); // Debug statement
            return;
        }

        // Create new user
        UserModel newUser = userDAO.createUser(username, password, email);
        if (newUser != null) {
            alertLabel.setText("Registration successful! You can now log in.");
            System.out.println("Registration successful for user: " + email); // Debug statement
            clearFields();
        } else {
            alertLabel.setText("Registration failed. Username or email may already be in use.");
            System.err.println("Registration failed for user: " + username); // Debug statement
        }
    }

    // Method to clear input fields after successful registration
    private void clearFields() {
        userName.clear();
        masterPassword.clear();
        reEnteredMasterPassword.clear();
        userEmail.clear();
        alertLabel.setText(""); // Clear alert messages
    }

    // Method to switch to the main application scene
//    private void switchToMainScene(String email) {
//        try {
//            Optional<UserModel> userOpt = userDAO.getUserByEmail(email);
//            if (userOpt.isPresent()) {
//                UserModel user = userOpt.get(); // Get the UserModel instance
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/main.fxml"));
//                Parent mainView = loader.load();
//
//                // Get the controller and initialize it
//                MainController mainController = loader.getController();
//                Connection connection = JDBC_Connector.getConnection(); // Get a new connection
//                mainController.initializeDependencies(connection, user); // Pass the UserModel instance
//
//                Scene mainScene = new Scene(mainView);
//                Stage currentStage = (Stage) loginButton.getScene().getWindow();
//                currentStage.setScene(mainScene);
//                currentStage.show();
//                System.out.println("Switched to main scene for user: " + email); // Debug statement
//            }
//        } catch (Exception e) {
//            e.printStackTrace();  // Print full stack trace for debugging
//            alertLabel.setText("Error loading main screen: " + e.getMessage());
//            System.err.println("Exception loading main screen: " + e.getMessage()); // Debug statement
//        }
//    }

    private void switchToMainScene(String userId) {
        try {
            // Fetch the user by ID and handle the Optional<UserModel>
            Optional<UserModel> userOpt = userDAO.getUserById(userId);
            if (userOpt.isPresent()) {
                UserModel user = userOpt.get(); // Get the UserModel instance

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/main.fxml"));
                System.out.println("Attempting to load main.fxml..."); // Debug statement
                Parent mainView = loader.load();

                // Get the controller and initialize it
                MainController mainController = loader.getController();
                if (mainController == null) {
                    System.err.println("MainController is null!"); // Debug statement
                    return;
                }

                // Initialize the MainController with the connection and user
                Connection connection = JDBC_Connector.getConnection(); // Get a new connection
                if (connection == null) {
                    System.err.println("Failed to establish a new database connection for mainController.");
                    return;
                }
                mainController.initializeDependencies(connection, user); // Pass the UserModel instance
                System.out.println("Initialized MainController successfully."); // Debug statement

                Scene mainScene = new Scene(mainView);
                Stage currentStage = (Stage) loginButton.getScene().getWindow();
                currentStage.setScene(mainScene);
                currentStage.show();
                System.out.println("Switched to main scene for user: " + user.getUserName()); // Use user.getUserName() to show the logged in user
            } else {
                System.err.println("User not found in the database: " + userId); // Debug statement
            }
        } catch (Exception e) {
            e.printStackTrace();  // Print full stack trace for debugging
            alertLabel.setText("Error loading main screen: " + e.getMessage());
            System.err.println("Exception loading main screen: " + e.getMessage()); // Debug statement
        }
    }


}
