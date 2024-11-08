package com.passify.controller;

import com.passify.model.PasswordDAO;
import com.passify.model.PasswordModel;
import com.passify.model.UserModel;
import com.passify.utils.PasswordGenerator;
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
    private Label passwordFormLabel;

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
    private TextField appUrl;

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

    private MainController parentController; // Reference to the parent controller for navigation

    // Initialize the controller with necessary data and set up UI
    public void initialize(Connection connection, PasswordModel currentPassword, UserModel currentUser, MainController parentController) throws SQLException {
        this.connection = connection; // Set database connection
        this.passwordDAO = new PasswordDAO(connection); // Initialize password DAO
        this.currentUser = currentUser; // Set current user
        this.parentController = parentController; // Set parent controller

        populateCategoryList(); // Populate categories in dropdown

        if (currentPassword == null) {
            passwordFormLabel.setText("Add New Password"); // Set form label for adding new password
            clearFormFields(); // Clear form fields for new entry
        } else {
            setPassword(currentPassword); // Populate fields with existing password data
            passwordFormLabel.setText("Edit Password Details"); // Set form label for editing password
        }
    }

    // Clear form fields when adding a new password
    private void clearFormFields() {
        appName.clear();
        appUsername.clear();
        appPassword.clear();
        appEmail.clear();
        appNotes.clear();
        appUrl.clear();
        appCategoryList.setValue(null); // Reset category selection
    }

    // Set form fields based on existing password data
    public void setPassword(PasswordModel password) {
        this.currentPassword = password;
        appName.setText(password.getAppName());
        appUsername.setText(password.getAppUsername());

        String currentAppPassword = password.getAppPassword();
        if (currentAppPassword != null) {
            appPassword.setText(currentAppPassword); // Set password if available
        } else {
            appPassword.clear(); // Clear password field if not available
        }

        appEmail.setText(password.getAppEmail());
        appNotes.setText(password.getAppNotes());
        appUrl.setText(password.getAppUrl());
        appCategoryList.setValue(password.getCategory().toString()); // Set the category value
    }

    // Populate the category dropdown with available categories
    private void populateCategoryList() {
        for (PasswordModel.Category category : PasswordModel.Category.values()) {
            appCategoryList.getItems().add(category.toString()); // Add each category to the dropdown
        }
    }

    // Generate a random password and populate the password field
    @FXML
    private void handleGeneratePassword() {
        String generatedPassword = PasswordGenerator.generatePassword(); // Generate a password
        appPassword.setText(generatedPassword); // Set generated password in the field
    }

    // Save the edited password details to the database
    @FXML
    private void handleSaveEdit() {
        // Confirm save action with the user
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(currentPassword == null ? "Confirm Add" : "Confirm Save");
        alert.setHeaderText(currentPassword == null ? "Are you sure you want to add this new password?" : "Are you sure you want to save the changes?");
        alert.setContentText(currentPassword == null ? "This action will add a new password." : "This action will update the password details.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Collect form data
                String appNameText = appName.getText();
                String appCategoryName = appCategoryList.getValue();

                if (appCategoryName == null || appCategoryName.isEmpty()) {
                    logAndAlert("Category must be selected.", null); // Show error if no category is selected
                    return;
                }

                PasswordModel.Category appCategory = PasswordModel.Category.valueOf(appCategoryName);
                String appUsernameText = appUsername.getText();
                String appPasswordText = appPassword.getText();
                String appEmailText = appEmail.getText();
                String appUrlText = appUrl.getText();
                String appNotesText = appNotes.getText();

                if (appPasswordText == null || appPasswordText.trim().isEmpty()) {
                    logAndAlert("Password cannot be empty.", null); // Show error if password is empty
                    return;
                }

                System.out.println("App Password: " + appPasswordText); // Debugging password value

                if (currentPassword == null) {
                    // Add new password to the database
                    PasswordModel newPassword = new PasswordModel(
                            currentUser.getUserId(),
                            null,
                            appPasswordText,
                            null,
                            appCategory,
                            appNameText,
                            appUsernameText,
                            appUrlText,
                            appEmailText,
                            appNotesText,
                            "saved",
                            false
                    );

                    if (passwordDAO.addPassword(newPassword, currentUser)) {
                        System.out.println("New password added successfully.");
                        logAndAlertSuccess("New password added successfully.");
                        navigateBackToPasswordDetails();
                    } else {
                        logAndAlert("Failed to add new password.", null);
                    }
                } else {
                    // Update existing password details
                    currentPassword.setAppName(appNameText);
                    currentPassword.setCategory(appCategory);
                    currentPassword.setAppUsername(appUsernameText);
                    currentPassword.setAppPassword(appPasswordText);
                    currentPassword.setAppEmail(appEmailText);
                    currentPassword.setAppUrl(appUrlText);
                    currentPassword.setAppNotes(appNotesText);

                    if (passwordDAO.updatePassword(currentPassword, currentUser)) {
                        System.out.println("Password details updated successfully.");
                        logAndAlertSuccess("Password details updated successfully");
                        navigateBackToPasswordDetails();
                    } else {
                        logAndAlert("Failed to update password details.", null);
                    }
                }
            } catch (SQLException e) {
                logAndAlert("An error occurred while saving password details.", e);
            } catch (IllegalArgumentException e) {
                logAndAlert("Invalid category selected.", e);
            }
        }
    }

    // Handle canceling the edit operation
    @FXML
    private void handleCancelEdit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cancel");
        alert.setHeaderText("Are you sure you want to cancel the edits?");
        alert.setContentText("Any unsaved changes will be lost.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            navigateBackToPasswordDetails(); // Navigate back to password details view
        }
    }

    // Navigate back to the password details page
    private void navigateBackToPasswordDetails() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/password_details.fxml"));
            Parent passwordDetailsView = loader.load();

            PasswordDetailsController passwordDetailsController = loader.getController();
            passwordDetailsController.initialize(connection, currentPassword, currentUser, parentController); // Pass necessary data

            parentController.setPageHolderContent(passwordDetailsView); // Set the new view in the parent controller
        } catch (IOException | SQLException e) {
            logAndAlert("Failed to navigate back to password details.", e);
        }
    }

    // Log an error and show an alert
    private void logAndAlert(String message, Exception e) {
        System.out.println(message);
        if (e != null) {
            e.printStackTrace(); // Print exception stack trace
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        if (e != null) {
            alert.setContentText(e.getMessage());
        }
        alert.showAndWait(); // Wait for user to acknowledge the alert
    }

    // Log a success message and show a success alert
    private void logAndAlertSuccess(String message) {
        System.out.println(message);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
