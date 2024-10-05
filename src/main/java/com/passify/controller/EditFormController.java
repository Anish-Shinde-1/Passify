package com.passify.controller;

import com.passify.model.PasswordDAO;
import com.passify.model.PasswordModel;
import com.passify.model.UserModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.Parent;
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
    private TextField appPassword;

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
    private Connection connection;
    private UserModel currentUser;

    // Reference to the parent controller (e.g., MainController)
    private MainController parentController;

    // Initialize method to set up the controller with dependencies
    public void initialize(Connection connection, PasswordModel currentPassword, UserModel currentUser, MainController parentController) throws SQLException {
        this.connection = connection;
        this.passwordDAO = new PasswordDAO(connection);
        this.currentUser = currentUser;
        this.parentController = parentController; // Get reference to the parent controller

        if (currentPassword != null) {
            setPassword(currentPassword); // Populate fields with existing password data
        } else {
            logAndAlert("No password data provided.", null);
        }

        // Populate the category list from enum values
        populateCategoryList();
    }

    // Method to populate fields with current password details
    public void setPassword(PasswordModel password) {
        this.currentPassword = password;
        appName.setText(password.getAppName());
        appUsername.setText(password.getAppUsername());
        appPassword.setText(password.getEncryptedPassword()); // Consider decrypting the password for display
        appEmail.setText(password.getAppEmail());
        appNotes.setText(password.getAppNotes());

        // Set the category name in the ChoiceBox
        appCategoryList.setValue(password.getCategory().toString());
    }

    // Method to populate the category ChoiceBox with enum values
    private void populateCategoryList() {
        for (PasswordModel.Category category : PasswordModel.Category.values()) {
            appCategoryList.getItems().add(category.toString()); // Add categories to the ChoiceBox
        }
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
                // Gather the updated data from input fields
                String appNameText = appName.getText();
                String appCategoryName = appCategoryList.getValue();
                PasswordModel.Category appCategory = PasswordModel.Category.valueOf(appCategoryName);
                String appUsernameText = appUsername.getText();
                String appPasswordText = appPassword.getText(); // Encrypt the password if necessary
                String appEmailText = appEmail.getText();
                String appNotesText = appNotes.getText();

                // Update the currentPassword object
                currentPassword.setAppName(appNameText);
                currentPassword.setCategory(appCategory);
                currentPassword.setAppUsername(appUsernameText);
                currentPassword.setEncryptedPassword(appPasswordText); // Encrypt if needed
                currentPassword.setAppEmail(appEmailText);
                currentPassword.setAppNotes(appNotesText);

                // Save the changes to the database
                if (passwordDAO.updatePassword(currentPassword, currentUser)) {
                    System.out.println("Password details updated successfully.");
                    navigateBackToPasswordDetails(); // Navigate back to the password details view
                } else {
                    logAndAlert("Failed to update password details.", null);
                }
            } catch (SQLException e) {
                logAndAlert("An error occurred while updating password details.", e);
            } catch (IllegalArgumentException e) {
                logAndAlert("Invalid category selected.", e);
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
            navigateBackToPasswordDetails(); // Navigate back to the password details view
        }
    }

    // Navigate back to PasswordDetailsController within the same page holder
    private void navigateBackToPasswordDetails() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/password_details.fxml"));
            Parent passwordDetailsView = loader.load();

            // Get the controller for the password details view
            PasswordDetailsController passwordDetailsController = loader.getController();
            passwordDetailsController.initialize(connection, currentPassword, currentUser, parentController); // Pass dependencies

            // Use the parentController to switch views within the pageHolder
            parentController.setPageHolderContent(passwordDetailsView); // Load the password details view into the pageHolder
        } catch (IOException | SQLException e) {
            logAndAlert("Failed to navigate back to password details.", e);
        }
    }

    // Helper method to log and display errors in UI
    private void logAndAlert(String message, Exception e) {
        System.out.println(message);
        if (e != null) {
            e.printStackTrace();
        }

        // Show an error dialog to the user
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        if (e != null) {
            alert.setContentText(e.getMessage());
        }
        alert.showAndWait();
    }
}
