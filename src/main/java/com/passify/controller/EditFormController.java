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
    private TextField appPassword; // Keep it as TextField for visibility

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

    // Reference to the parent controller (e.g., MainController)
    private MainController parentController;

    // Initialize method to set up the controller with dependencies
    public void initialize(Connection connection, PasswordModel currentPassword, UserModel currentUser, MainController parentController) throws SQLException {
        this.connection = connection;
        this.passwordDAO = new PasswordDAO(connection);
        this.currentUser = currentUser;
        this.parentController = parentController;

        // Populate the category list
        populateCategoryList();

        // Check if currentPassword is null to determine if we're adding or editing
        if (currentPassword == null) {
            passwordFormLabel.setText("Add New Password");
            clearFormFields(); // Clear input fields for a new password entry
        } else {
            setPassword(currentPassword); // Populate fields with existing data
            passwordFormLabel.setText("Edit Password Details");
        }
    }

    private void clearFormFields() {
        appName.clear();
        appUsername.clear();
        appPassword.clear(); // Ensure password field is cleared
        appEmail.clear();
        appNotes.clear();
        appUrl.clear(); // Clear URL field
        appCategoryList.setValue(null);  // Reset the choice box
    }

    // Method to populate fields with current password details
    public void setPassword(PasswordModel password) {
        this.currentPassword = password;
        appName.setText(password.getAppName());
        appUsername.setText(password.getAppUsername());

        // Set the password text for editing (ensure this is not null)
        String currentAppPassword = password.getAppPassword();
        if (currentAppPassword != null) {
            appPassword.setText(currentAppPassword);
        } else {
            appPassword.clear(); // Clear field if password is null
        }

        appEmail.setText(password.getAppEmail());
        appNotes.setText(password.getAppNotes());
        appUrl.setText(password.getAppUrl()); // Set the URL

        // Set the category name in the ChoiceBox
        appCategoryList.setValue(password.getCategory().toString());
    }

    // Method to populate the category ChoiceBox with enum values
    private void populateCategoryList() {
        for (PasswordModel.Category category : PasswordModel.Category.values()) {
            appCategoryList.getItems().add(category.toString()); // Add categories to the ChoiceBox
        }
    }

    // Handle the action of generating a password
    @FXML
    private void handleGeneratePassword() {
        String generatedPassword = PasswordGenerator.generatePassword(); // Use the utility to generate a password
        appPassword.setText(generatedPassword); // Set the generated password in the appPassword field
    }

    // Handle the action of saving the edited password details
    @FXML
    private void handleSaveEdit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(currentPassword == null ? "Confirm Add" : "Confirm Save");
        alert.setHeaderText(currentPassword == null ? "Are you sure you want to add this new password?" : "Are you sure you want to save the changes?");
        alert.setContentText(currentPassword == null ? "This action will add a new password." : "This action will update the password details.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Gather form data
                String appNameText = appName.getText();
                String appCategoryName = appCategoryList.getValue();

                // Validate category selection
                if (appCategoryName == null || appCategoryName.isEmpty()) {
                    logAndAlert("Category must be selected.", null);
                    return; // Exit early if no category is selected
                }

                PasswordModel.Category appCategory = PasswordModel.Category.valueOf(appCategoryName);
                String appUsernameText = appUsername.getText();
                String appPasswordText = appPassword.getText(); // Retrieve plain text for encryption
                String appEmailText = appEmail.getText();
                String appUrlText = appUrl.getText();
                String appNotesText = appNotes.getText();

                // Check if the password is empty before processing
                if (appPasswordText == null || appPasswordText.trim().isEmpty()) {
                    logAndAlert("Password cannot be empty.", null);
                    return; // Exit early if the password is empty
                }

                // Debugging statement to check the password
                System.out.println("App Password: " + appPasswordText); // Check what the password is

                if (currentPassword == null) {
                    // Create a new PasswordModel instance
                    PasswordModel newPassword = new PasswordModel(
                            currentUser.getUserId(),
                            null, // Password will be encrypted later
                            appPasswordText,
                            null, // Salt will be generated during encryption
                            appCategory,
                            appNameText,
                            appUsernameText,
                            appUrlText,
                            appEmailText,
                            appNotesText,
                            "saved",
                            false
                    );

                    // Add the new password using the DAO
                    if (passwordDAO.addPassword(newPassword, currentUser)) {
                        System.out.println("New password added successfully.");
                        logAndAlert("New password added successfully.", null);
                        navigateBackToPasswordDetails();
                    } else {
                        logAndAlert("Failed to add new password.", null);
                    }
                } else {
                    // Update the existing password details
                    currentPassword.setAppName(appNameText);
                    currentPassword.setCategory(appCategory);
                    currentPassword.setAppUsername(appUsernameText);
                    currentPassword.setAppPassword(appPasswordText); // Set plain text for encryption
                    currentPassword.setAppEmail(appEmailText);
                    currentPassword.setAppUrl(appUrlText);
                    currentPassword.setAppNotes(appNotesText);

                    // Update the password using the DAO
                    if (passwordDAO.updatePassword(currentPassword, currentUser)) {
                        System.out.println("Password details updated successfully.");
                        logAndAlert("Password details updated succesfully", null);
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
