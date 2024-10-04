package com.passify.controller;

import com.passify.model.EncryptionKeyDAO;
import com.passify.model.PasswordDAO;
import com.passify.model.PasswordModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class EditFormController {

    @FXML
    private ChoiceBox<String> appCategoryList;

    @FXML
    private TextField appEmail;

    @FXML
    private TextField appName;

    @FXML
    private TextField appNotes;

    @FXML
    private PasswordField appPassword;

    @FXML
    private TextField appUsername;

    @FXML
    private Button cancelEditButton;

    @FXML
    private Pane editPasswordPanel;

    @FXML
    private Button generatePasswordButton;

    @FXML
    private Button saveEditButton;

    private PasswordModel currentPassword;
    private PasswordDAO passwordDAO;

    // Injecting the database connection and encryption key DAO
    private Connection connection;
    private EncryptionKeyDAO encryptionKeyDAO;

    // Initialize method to set up the controller
    public void initialize(Connection connection, PasswordModel currentPassword, EncryptionKeyDAO encryptionKeyDAO) throws SQLException {
        this.connection = connection; // Assign connection
        this.encryptionKeyDAO = encryptionKeyDAO; // Assign EncryptionKeyDAO
        this.passwordDAO = new PasswordDAO(connection, encryptionKeyDAO); // Create PasswordDAO with both parameters

        if (currentPassword != null) {
            setPassword(currentPassword); // Populate fields immediately
        } else {
            logAndAlert("No password data provided.", null);
        }
    }

    // Method to populate fields with current password details
    public void setPassword(PasswordModel password) {
        this.currentPassword = password;
        appName.setText(password.getAppName());
        appCategoryList.setValue(password.getCategoryId()); // Adjust as per your implementation
        appUsername.setText(password.getAppUsername());
        appPassword.setText(password.getEncryptedPassword()); // Consider decrypting if necessary
        appEmail.setText(password.getAppEmail());
        appNotes.setText(password.getAppNotes());
    }

    // Handle the action of saving the edited password details
    @FXML
    private void handleSaveEdit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Save");
        alert.setHeaderText("Are you sure you want to save the changes?");
        alert.setContentText("This action will update the password details.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Collect data from fields
                String appNameText = appName.getText();
                String appCategory = appCategoryList.getValue(); // Adjust type as necessary
                String appUsernameText = appUsername.getText();
                String appPasswordText = appPassword.getText(); // Consider encryption
                String appEmailText = appEmail.getText();
                String appNotesText = appNotes.getText();

                // Update the currentPassword object
                currentPassword.setAppName(appNameText);
                currentPassword.setCategoryId(appCategory); // Adjust as per your implementation
                currentPassword.setAppUsername(appUsernameText);
                currentPassword.setEncryptedPassword(appPasswordText); // Consider encrypting
                currentPassword.setAppEmail(appEmailText);
                currentPassword.setAppNotes(appNotesText);

                // Save changes to the database
                String userId = currentPassword.getUserId(); // Assuming userId is available
                if (passwordDAO.updatePassword(currentPassword, userId)) { // Pass both PasswordModel and userId
                    System.out.println("Password details updated successfully.");
                    navigateBackToPasswordDetails(); // Navigate back to password details
                } else {
                    logAndAlert("Failed to update password details", null);
                }
            } catch (SQLException e) {
                logAndAlert("An error occurred while updating password details.", e);
            }
        }
    }

    // Handle the action of canceling the edit
    @FXML
    private void handleCancelEdit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cancel");
        alert.setHeaderText("Are you sure you want to cancel the edits?");
        alert.setContentText("Any unsaved changes will be lost.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            navigateBackToPasswordDetails(); // Navigate back to password details
        }
    }

    // Navigate back to PasswordDetailsController
    private void navigateBackToPasswordDetails() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/password_details.fxml"));
            Parent root = loader.load();

            // Get the controller for the new form
            PasswordDetailsController passwordDetailsController = loader.getController();
            passwordDetailsController.initialize(connection, currentPassword, encryptionKeyDAO); // Pass the connection, current password, and encryption key DAO

            Stage stage = (Stage) editPasswordPanel.getScene().getWindow(); // Get the current window
            stage.setScene(new Scene(root)); // Set the new scene
            stage.setTitle("Password Details"); // Optionally set a new title
            stage.show(); // Show the updated window
        } catch (IOException | SQLException e) {
            logAndAlert("Failed to navigate back to password details.", e);
        }
    }

    // Method to close the current window
    private void closeWindow() {
        Stage stage = (Stage) editPasswordPanel.getScene().getWindow(); // Get the current window
        stage.close(); // Close the window
    }

    // Helper method to log and display errors in UI
    private void logAndAlert(String message, Exception e) {
        System.out.println(message);
        if (e != null) {
            e.printStackTrace();
        }
        // Show a dialog or alert to the user
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        if (e != null) {
            alert.setContentText(e.getMessage());
        }
        alert.showAndWait();
    }
}
