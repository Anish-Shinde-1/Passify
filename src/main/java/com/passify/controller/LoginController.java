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
    private PasswordField reEnteredMasterPassword; // Field for re-entering password during login

    @FXML
    private Button registerButton;

    @FXML
    private TextField userEmail;

    @FXML
    private TextField userName;

    private UserDAO userDAO;
    private Connection connection; // Database connection

    // Constructor to initialize the LoginController and database connection
    public LoginController() {
        try {
            Connection connection = JDBC_Connector.getConnection(); // Get the database connection
            this.userDAO = new UserDAO(connection); // Initialize the UserDAO with the connection
            System.out.println("UserDAO initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Database connection error in constructor: " + e.getMessage());
        }
    }

    // Sets the database connection and initializes UserDAO
    public void setConnection(Connection connection) throws SQLException {
        this.connection = connection; // Set the database connection
        this.userDAO = new UserDAO(connection); // Initialize UserDAO with the new connection
    }

    // Handles login action when the login button is pressed
    @FXML
    private void handleLogin(ActionEvent event) {
        System.out.println("Login button pressed."); // Debugging log

        String username = userName.getText().trim();
        String email = userEmail.getText().trim(); // Get the email for login
        String password = masterPassword.getText();
        String reEnteredPassword = reEnteredMasterPassword.getText(); // Get the re-entered password

        // Validate that all fields are filled
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || reEnteredPassword.isEmpty()) {
            alertLabel.setText("Please fill in all fields.");
            return;
        }

        // Check if both passwords match
        if (!password.equals(reEnteredPassword)) {
            alertLabel.setText("Passwords do not match. Please re-enter your password.");
            return;
        }

        // Check if user exists by email
        Optional<UserModel> userOpt = userDAO.getUserByEmail(email);
        if (userOpt.isPresent()) {
            UserModel user = userOpt.get();
            try {
                boolean loginSuccess = userDAO.verifyUserPassword(user.getUserId(), password);
                System.out.println("Login attempt for user: " + email); // Debugging log

                if (loginSuccess) {
                    System.out.println("Login successful for user: " + email); // Debugging log
                    switchToMainScene(user.getUserId()); // Proceed to the main scene
                } else {
                    alertLabel.setText("Invalid email or password.");
                    System.err.println("Invalid login attempt for user: " + email); // Debugging log
                }
            } catch (Exception e) {
                alertLabel.setText("Error during login: " + e.getMessage());
                System.err.println("Exception during login: " + e.getMessage()); // Debugging log
            }
        } else {
            alertLabel.setText("User not found. Please register.");
            System.out.println("User not found: " + email); // Debugging log
        }
    }

    // Handles registration when the register button is pressed
    @FXML
    private void handleRegister(ActionEvent event) {
        System.out.println("Register button pressed.");
        String username = userName.getText().trim();
        String password = masterPassword.getText();
        String confirmPassword = reEnteredMasterPassword.getText();
        String email = userEmail.getText().trim();

        // Validate that all fields are filled
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
            alertLabel.setText("Please fill in all fields.");
            return;
        }

        // Validate password to meet specific criteria
        if (!isValidPassword(password)) {
            alertLabel.setText("Password must be at least 12 characters long,\ncontain 1 uppercase letter, 1 digit, and 1 special symbol.");
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            alertLabel.setText("Please enter a valid email address (e.g., name@example.com).");
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            alertLabel.setText("Passwords do not match.");
            return;
        }

        // Check if user already exists
        Optional<UserModel> existingUserOpt = userDAO.getUserByEmail(email);
        if (existingUserOpt.isPresent()) {
            alertLabel.setText("Email already registered. Please login.");
            System.out.println("Email already registered: " + email);
            return;
        }

        // Register the new user
        UserModel newUser = userDAO.createUser(username, password, email);
        if (newUser != null) {
            alertLabel.setText("Registration successful! You can now log in.");
            System.out.println("Registration successful for user: " + email);
            clearFields(); // Clear the fields after successful registration
        } else {
            alertLabel.setText("Registration failed. Username or email may already be in use.");
            System.err.println("Registration failed for user: " + username); // Debugging log
        }
    }

    // Validates the password based on set rules (length, uppercase, digits, special characters)
    private boolean isValidPassword(String password) {
        if (password.length() < 12) {
            return false; // Password too short
        }

        boolean hasUppercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true; // Special character
            }

            // Early exit if all conditions are met
            if (hasUppercase && hasDigit && hasSpecialChar) {
                return true;
            }
        }

        return hasUppercase && hasDigit && hasSpecialChar; // Return true only if all conditions are met
    }

    // Validates the email format
    private boolean isValidEmail(String email) {
        // Check if email contains '@' and ends with '.com'
        int atIndex = email.indexOf('@');
        if (atIndex < 1) {
            return false; // '@' should not be at the start
        }

        if (!email.endsWith(".com")) {
            return false; // Check if email ends with '.com'
        }

        String domainPart = email.substring(atIndex + 1, email.length() - 4); // Get domain part between '@' and '.com'
        if (domainPart.isEmpty()) {
            return false;
        }

        return true;
    }

    // Clears input fields after successful registration
    private void clearFields() {
        userName.clear();
        masterPassword.clear();
        reEnteredMasterPassword.clear();
        userEmail.clear();
    }

    // Switches to the main application scene after a successful login
    private void switchToMainScene(String userId) {
        try {
            Optional<UserModel> userOpt = userDAO.getUserById(userId);
            if (userOpt.isPresent()) {
                UserModel user = userOpt.get(); // Get the UserModel instance

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/main.fxml"));
                System.out.println("Attempting to load main.fxml..."); // Debugging log
                Parent mainView = loader.load();

                MainController mainController = loader.getController();
                if (mainController == null) {
                    System.err.println("MainController is null!"); // Debugging log
                    return;
                }

                // Initialize the MainController with the user and database connection
                Connection connection = JDBC_Connector.getConnection(); // New connection for the main scene
                if (connection == null) {
                    System.err.println("Failed to establish a new database connection for mainController.");
                    return;
                }
                mainController.initializeDependencies(connection, user); // Pass the user to the MainController
                System.out.println("Initialized MainController successfully."); // Debugging log

                // Switch the scene to the main application view
                Scene mainScene = new Scene(mainView);
                Stage currentStage = (Stage) loginButton.getScene().getWindow();
                currentStage.setScene(mainScene);
                currentStage.show();
                System.out.println("Switched to main scene for user: " + user.getUserName()); // Debugging log
            } else {
                System.err.println("User not found in the database: " + userId); // Debugging log
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for debugging
            alertLabel.setText("Error loading main screen: " + e.getMessage());
            System.err.println("Exception loading main screen: " + e.getMessage()); // Debugging log
        }
    }
}
