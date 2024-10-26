package com.passify.controller;

import com.passify.model.PasswordDAO;
import com.passify.model.PasswordModel;
import com.passify.model.UserModel; // Assuming UserModel is defined
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Controller for displaying and managing password details in the application.
 * This class handles actions related to password operations, such as copying
 * details to the clipboard, editing password entries, and managing favorites.
 */
public class PasswordDetailsController {

    @FXML
    private Label appCategory; // Label to display the application's category
    @FXML
    private TextField appEmail; // TextField for the application's email
    @FXML
    private Label appName; // Label to display the application's name
    @FXML
    private TextField appNotes; // TextField for any notes related to the password
    @FXML
    private PasswordField appPassword; // PasswordField to display the application's password
    @FXML
    private Label appUrl; // Label to display the application's URL
    @FXML
    private TextField appUsername; // TextField for the application's username
    @FXML
    private Button copyAppEmail; // Button to copy email to clipboard
    @FXML
    private Button copyAppPassword; // Button to copy password to clipboard
    @FXML
    private Button copyAppUsername; // Button to copy username to clipboard
    @FXML
    private Button editPasswordButton; // Button to edit the password
    @FXML
    private Button favouritePasswordButton; // Button to mark/unmark the password as a favorite
    @FXML
    private Pane passwordDetailsPanel; // Panel that contains the password details UI elements
    @FXML
    private Button trashPasswordButton; // Button to move the password to trash

    private PasswordDAO passwordDAO; // Data access object for managing password data
    private PasswordModel currentPassword; // The password currently being viewed/edited
    private Stage stage; // Stage reference for managing window behavior
    private MainController mainController; // Reference to the main application controller

    // Injecting the database connection from the main application or other controller
    private Connection connection; // Database connection
    private UserModel currentUser; // Currently logged-in user

    /**
     * Initializes the PasswordDetailsController with the necessary data and UI elements.
     *
     * @param connection the database connection
     * @param password the PasswordModel representing the current password
     * @param user the UserModel representing the currently logged-in user
     * @param mainController the main controller for the application
     * @throws SQLException if a database access error occurs
     */
    public void initialize(Connection connection, PasswordModel password, UserModel user, MainController mainController) throws SQLException {
        this.connection = connection;
        this.passwordDAO = new PasswordDAO(connection); // Initialize the PasswordDAO
        this.currentUser = user; // Store the current user
        this.currentPassword = password; // Store the current password
        this.mainController = mainController; // Store the MainController reference

        populatePasswordDetails(); // Populate the UI with the current password details
    }

    /**
     * Populates the UI elements with data from the current PasswordModel.
     */
    private void populatePasswordDetails() {
        if (currentPassword != null) {
            appName.setText(currentPassword.getAppName());
            appCategory.setText(currentPassword.getCategory().name()); 
            appUsername.setText(currentPassword.getAppUsername());
            appPassword.setText("                "); // Do not show the encrypted password
            appEmail.setText(currentPassword.getAppEmail());
            appUrl.setText(currentPassword.getAppUrl());
            appNotes.setText(currentPassword.getAppNotes());
            updateFavoriteButtonState(); // Update favorite button state based on current password
        }
    }

    /**
     * Copies the password to the clipboard after decrypting it.
     */
    @FXML
    private void handleCopyAppPassword() {
        try {
            // Decrypt password before copying
            Optional<PasswordModel> passwordOpt = passwordDAO.getPasswordById(currentPassword.getPasswordId(), currentUser);
            if (passwordOpt.isPresent()) {
                String decryptedPassword = passwordOpt.get().getAppPassword(); // Retrieve the decrypted password
                copyToClipboard(decryptedPassword); // Copy to clipboard
                decryptedPassword = null; // Clear sensitive data
                showCopyConfirmation("Password copied to clipboard!"); // Show confirmation
                System.out.println("Password copied to clipboard");
            } else {
                logAndAlert("Unable to retrieve the password for copying.", null);
            }
        } catch (Exception e) {
            logAndAlert("Password wasn't copied", e);
        }
    }

    /**
     * Copies the username to the clipboard.
     */
    @FXML
    private void handleCopyAppUsername() {
        try {
            String username = appUsername.getText();
            copyToClipboard(username); // Copy to clipboard
            showCopyConfirmation("Username copied to clipboard!"); // Show confirmation
            System.out.println("AppUsername copied to clipboard");
        } catch (Exception e) {
            logAndAlert("AppUsername wasn't copied", e);
        }
    }

    /**
     * Copies the email to the clipboard.
     */
    @FXML
    private void handleCopyAppEmail() {
        try {
            String email = appEmail.getText();
            copyToClipboard(email); // Copy to clipboard
            showCopyConfirmation("Email copied to clipboard!"); // Show confirmation
            System.out.println("AppEmail copied to clipboard");
        } catch (Exception e) {
            logAndAlert("AppEmail wasn't copied", e);
        }
    }

    /**
     * Displays a confirmation alert when text is copied to the clipboard.
     *
     * @param message the message to display in the confirmation alert
     */
    private void showCopyConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Copy Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait(); // Wait for user to acknowledge
    }

    /**
     * Loads the edit password form for modifying the current password.
     */
    @FXML
    private void handleEditPassword() {
        try {
            // Load the edit_form.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/edit_form.fxml"));
            Parent editFormView = loader.load();

            // Get the EditFormController and initialize it
            EditFormController editFormController = loader.getController();

            // Pass the necessary data to the EditFormController
            editFormController.initialize(connection, currentPassword, currentUser, mainController);

            // Load the edit form into the pageHolder of MainController
            mainController.getPageHolder().getChildren().clear(); // Clear previous content
            mainController.getPageHolder().getChildren().add(editFormView); // Add the new view

            System.out.println("Edit password form loaded successfully.");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            logAndAlert("Failed to load edit form.", e);
        }
    }

    /**
     * Moves the current password to the trash (soft delete).
     */
    @FXML
    private void handleTrashPassword() {
        try {
            if (passwordDAO.trashPassword(currentPassword.getPasswordId())) { // Move password to trash
                System.out.println("Password moved to trash.");
                closeWindow(); // Close the current window
            } else {
                logAndAlert("Failed to move password to trash", null);
            }
        } catch (SQLException e) {
            logAndAlert("An error occurred while moving the password to trash.", e);
        }
    }

    /**
     * Toggles the favorite status of the current password.
     */
    @FXML
    private void handleFavouritePassword() {
        try {
            currentPassword.setFavourite(!currentPassword.isFavourite()); // Toggle favorite status

            // Update the password's favorite status
            if (passwordDAO.updatePassword(currentPassword, currentUser)) { // Assuming updatePassword requires currentUser
                System.out.println("Password marked as favorite.");
                updateFavoriteButtonState(); // Update button state
            } else {
                logAndAlert("Failed to update favorite status.", null);
            }
        } catch (SQLException e) {
            logAndAlert("An error occurred while updating the favorite status.", e);
        }
    }

    /**
     * Copies the specified text to the system clipboard.
     *
     * @param text the text to copy to the clipboard
     */
    private void copyToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text); // Set the clipboard content
        clipboard.setContent(content); // Update the clipboard
    }

    /**
     * Updates the state of the favorite button based on the current password's favorite status.
     */
    private void updateFavoriteButtonState() {
        ImageView bookmarkIcon = (ImageView) favouritePasswordButton.getGraphic();
        String filledBookmarkPath = "/com/passify/icons/FilledBookmark.png";
        String bookmarkPath = "/com/passify/icons/Bookmark.png";

        // Change icon based on favorite status
        if (currentPassword.isFavourite()) {
            bookmarkIcon.setImage(new Image(getClass().getResourceAsStream(filledBookmarkPath)));
        } else {
            bookmarkIcon.setImage(new Image(getClass().getResourceAsStream(bookmarkPath)));
        }
    }

    /**
     * Closes the current window.
     */
    private void closeWindow() {
        if (stage != null) {
            stage.close(); // Close the stage if it exists
        }
    }

    /**
     * Sets the Stage for this controller.
     *
     * @param stage the Stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage; // Store the Stage reference
    }

    /**
     * Sets the main controller for this PasswordDetailsController.
     *
     * @param mainController the MainController to set
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController; // Store the MainController reference
    }

    /**
     * Logs the error message and displays an alert with the error details.
     *
     * @param message the message to log and display
     * @param e the exception that occurred (can be null)
     */
    private void logAndAlert(String message, Exception e) {
        System.out.println(message); // Log the error message
        if (e != null) {
            e.printStackTrace(); // Print stack trace for debugging
        }
        Alert alert = new Alert(Alert.AlertType.ERROR); // Create error alert
        alert.setTitle("Error");
        alert.setHeaderText(message);
        if (e != null) {
            alert.setContentText(e.getMessage()); // Show exception message
        }
        alert.showAndWait(); // Wait for user to acknowledge the alert
    }
}
