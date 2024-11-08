package com.passify.controller;

import com.passify.model.PasswordDAO;
import com.passify.model.PasswordModel;
import com.passify.model.UserModel;
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

public class PasswordDetailsController {

    @FXML
    private Label appCategory;
    @FXML
    private TextField appEmail;
    @FXML
    private Label appName;
    @FXML
    private TextField appNotes;
    @FXML
    private PasswordField appPassword;
    @FXML
    private Label appUrl;
    @FXML
    private TextField appUsername;
    @FXML
    private Button copyAppEmail;
    @FXML
    private Button copyAppPassword;
    @FXML
    private Button copyAppUsername;
    @FXML
    private Button editPasswordButton;
    @FXML
    private Button favouritePasswordButton;
    @FXML
    private Pane passwordDetailsPanel;
    @FXML
    private Button trashPasswordButton;

    private PasswordDAO passwordDAO;
    private PasswordModel currentPassword;
    private Stage stage;
    private MainController mainController;

    private Connection connection;
    private UserModel currentUser;

    public void initialize(Connection connection, PasswordModel password, UserModel user, MainController mainController) throws SQLException {
        this.connection = connection;
        this.passwordDAO = new PasswordDAO(connection); // Initialize the PasswordDAO
        this.currentUser = user; // Store the current user
        this.currentPassword = password; // Store the current password
        this.mainController = mainController; // Store the MainController reference

        populatePasswordDetails(); // Populate the UI with the current password details
    }

    // Populate the UI elements with data from the current PasswordModel
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

    // Copies the password to the clipboard after decrypting it
    @FXML
    private void handleCopyAppPassword() {
        try {
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

    // Copies the username to the clipboard
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

    // Copies the email to the clipboard
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

    // Displays a confirmation alert when text is copied to the clipboard
    private void showCopyConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Copy Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait(); // Wait for user to acknowledge
    }

    // Loads the edit password form for modifying the current password
    @FXML
    private void handleEditPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/edit_form.fxml"));
            Parent editFormView = loader.load();

            // Get the EditFormController and initialize it
            EditFormController editFormController = loader.getController();
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

    // Moves the current password to the trash (soft delete)
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

    // Toggles the favorite status of the current password
    @FXML
    private void handleFavouritePassword() {
        try {
            currentPassword.setFavourite(!currentPassword.isFavourite()); // Toggle favorite status

            if (passwordDAO.updatePassword(currentPassword, currentUser)) { // Update password
                System.out.println("Password marked as favorite.");
                updateFavoriteButtonState(); // Update button state
            } else {
                logAndAlert("Failed to update favorite status.", null);
            }
        } catch (SQLException e) {
            logAndAlert("An error occurred while updating the favorite status.", e);
        }
    }

    // Copies the specified text to the system clipboard
    private void copyToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text); // Set the clipboard content
        clipboard.setContent(content); // Update the clipboard
    }

    // Updates the state of the favorite button based on the current password's favorite status
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

    // Closes the current window
    private void closeWindow() {
        if (stage != null) {
            stage.close(); // Close the stage if it exists
        }
    }

    // Sets the Stage for this controller
    public void setStage(Stage stage) {
        this.stage = stage; // Store the Stage reference
    }

    // Sets the main controller for this PasswordDetailsController
    public void setMainController(MainController mainController) {
        this.mainController = mainController; // Store the MainController reference
    }

    // Logs the error message and displays an alert with the error details
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
