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

/**
 * Controller for editing password entries in the application.
 * This class handles the functionality to add or edit password details,
 * including form validation and data management.
 */
public class EditFormController {

    @FXML
    private Label passwordFormLabel; // Label to display the form title

    @FXML
    private ChoiceBox<String> appCategoryList; // Dropdown for selecting application categories

    @FXML
    private TextField appEmail; // TextField for the application's email

    @FXML
    private TextField appName; // TextField for the application's name

    @FXML
    private TextField appNotes; // TextField for additional notes related to the password

    @FXML
    private TextField appPassword; // TextField for the application's password (visible for editing)

    @FXML
    private TextField appUsername; // TextField for the application's username

    @FXML
    private TextField appUrl; // TextField for the application's URL

    @FXML
    private Button cancelEditButton; // Button to cancel the edit operation

    @FXML
    private Pane editPasswordPanel; // Panel containing the edit password form

    @FXML
    private Button generatePasswordButton; // Button to generate a random password

    @FXML
    private Button saveEditButton; // Button to save the edited password details

    private PasswordModel currentPassword; // The password being edited or added
    private PasswordDAO passwordDAO; // Data access object for managing password data
    private Connection connection; // Database connection
    private UserModel currentUser; // Currently logged-in user

    // Reference to the parent controller (e.g., MainController)
    private MainController parentController;

    /**
     * Initializes the EditFormController with the necessary data and sets up the UI.
     *
     * @param connection the database connection
     * @param currentPassword the PasswordModel representing the current password being edited (or null for adding a new one)
     * @param currentUser the UserModel representing the currently logged-in user
     * @param parentController the MainController for navigating back to password details
     * @throws SQLException if a database access error occurs
     */
    public void initialize(Connection connection, PasswordModel currentPassword, UserModel currentUser, MainController parentController) throws SQLException {
        this.connection = connection; // Store the database connection
        this.passwordDAO = new PasswordDAO(connection); // Initialize the PasswordDAO
        this.currentUser = currentUser; // Store the current user
        this.parentController = parentController; // Store the parent controller reference

        populateCategoryList(); // Populate the category list

        // Check if currentPassword is null to determine if we're adding or editing
        if (currentPassword == null) {
            passwordFormLabel.setText("Add New Password"); // Set form title for adding a new password
            clearFormFields(); // Clear input fields for a new password entry
        } else {
            setPassword(currentPassword); // Populate fields with existing data
            passwordFormLabel.setText("Edit Password Details"); // Set form title for editing an existing password
        }
    }

    /**
     * Clears all form fields for new password entry.
     */
    private void clearFormFields() {
        appName.clear();
        appUsername.clear();
        appPassword.clear(); // Ensure password field is cleared
        appEmail.clear();
        appNotes.clear();
        appUrl.clear(); // Clear URL field
        appCategoryList.setValue(null); // Reset the choice box
    }

    /**
     * Populates the UI fields with the details of the given PasswordModel.
     *
     * @param password the PasswordModel containing password details
     */
    public void setPassword(PasswordModel password) {
        this.currentPassword = password; // Store the current password
        appName.setText(password.getAppName());
        appUsername.setText(password.getAppUsername());

        // Set the password text for editing (ensure this is not null)
        String currentAppPassword = password.getAppPassword();
        if (currentAppPassword != null) {
            appPassword.setText(currentAppPassword); // Populate password field
        } else {
            appPassword.clear(); // Clear field if password is null
        }

        appEmail.setText(password.getAppEmail());
        appNotes.setText(password.getAppNotes());
        appUrl.setText(password.getAppUrl()); // Set the URL

        // Set the category name in the ChoiceBox
        appCategoryList.setValue(password.getCategory().toString());
    }

    /**
     * Populates the category ChoiceBox with available category options.
     */
    private void populateCategoryList() {
        for (PasswordModel.Category category : PasswordModel.Category.values()) {
            appCategoryList.getItems().add(category.toString()); // Add categories to the ChoiceBox
        }
    }

    /**
     * Generates a random password and sets it in the password field.
     */
    @FXML
    private void handleGeneratePassword() {
        String generatedPassword = PasswordGenerator.generatePassword(); // Generate a random password
        appPassword.setText(generatedPassword); // Set the generated password in the appPassword field
    }

    /**
     * Handles the action of saving the edited password details.
     */
    @FXML
    private void handleSaveEdit() {
        // Confirm action with the user
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
                    // Create a new PasswordModel instance for adding
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
                        navigateBackToPasswordDetails(); // Navigate back to password details
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
                        logAndAlert("Password details updated successfully", null);
                        navigateBackToPasswordDetails(); // Navigate back to password details
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

    /**
     * Handles the action of canceling the edit operation.
     */
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

    /**
     * Navigates back to the PasswordDetailsController within the same page holder.
     */
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

    /**
     * Logs an error message and displays an alert with the error details.
     *
     * @param message the message to log and display
     * @param e the exception that occurred (can be null)
     */
    private void logAndAlert(String message, Exception e) {
        System.out.println(message); // Log the error message
        if (e != null) {
            e.printStackTrace(); // Print stack trace for debugging
        }

        // Show an error dialog to the user
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        if (e != null) {
            alert.setContentText(e.getMessage()); // Show exception message
        }
        alert.showAndWait(); // Wait for user to acknowledge the alert
    }
}
