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
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.awt.event.ActionListener;
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
    private PasswordField reEnteredMasterPassword;

    @FXML
    private Button registerButton;

    @FXML
    private TextField userEmail;

    @FXML
    private TextField userName;

    private UserDAO userDAO;

    // Initialize the controller with a database connection
    public LoginController() {
        try {
            Connection connection = JDBC_Connector.getConnection();
            this.userDAO = new UserDAO(connection);
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    // Modify this method in your LoginController to accept the connection
    public void setConnection(Connection connection) {
        try {
            this.userDAO = new UserDAO(connection);
        } catch (SQLException e) {
            System.err.println("Error initializing UserDAO: " + e.getMessage());
        }
    }


    // Method to handle login
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = userEmail.getText().trim(); // Use userEmail for login
        String password = masterPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            alertLabel.setText("Please fill in all fields.");
            return;
        }

        // Fetch the user by email
        Optional<UserModel> userOpt = userDAO.getUserByEmail(email);
        if (userOpt.isPresent()) {
            UserModel user = userOpt.get();
            try {
                boolean loginSuccess = userDAO.verifyUserPassword(user.getUserId(), password);

                if (loginSuccess) {
                    // Proceed to the main application or dashboard
                    switchToMainScene(); // Switch to main.fxml
                } else {
                    alertLabel.setText("Invalid email or password.");
                }
            } catch (Exception e) {
                alertLabel.setText("Error during login: " + e.getMessage());
            }
        } else {
            alertLabel.setText("User not found. Please register.");
        }
    }

    // Method to handle registration
    @FXML
    private void handleRegister(ActionEvent event) {
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
            return;
        }

        // Create new user
        UserModel newUser = userDAO.createUser(username, password, email);
        if (newUser != null) {
            alertLabel.setText("Registration successful! You can now log in.");
            clearFields();
        } else {
            alertLabel.setText("Registration failed. Username or email may already be in use.");
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
    private void switchToMainScene() {
        try {
            Parent mainView = FXMLLoader.load(getClass().getResource("/com/passify/views/main.fxml"));
            Scene mainScene = new Scene(mainView);
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.setScene(mainScene);
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();  // Print full stack trace for debugging
            alertLabel.setText("Error loading main screen: " + e.getMessage());
        }
    }

}
