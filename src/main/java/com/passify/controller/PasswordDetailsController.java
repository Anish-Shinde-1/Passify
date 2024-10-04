package com.passify.controller;

import com.passify.model.EncryptionKeyDAO;
import com.passify.model.PasswordDAO;
import com.passify.model.PasswordModel;
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

    // Injecting the database connection from the main application or other controller
    private Connection connection;

    // Injecting the EncryptionKeyDAO
    private EncryptionKeyDAO encryptionKeyDAO;

    public void initialize(Connection connection, PasswordModel password, EncryptionKeyDAO encryptionKeyDAO) throws SQLException {
        this.connection = connection;
        this.encryptionKeyDAO = encryptionKeyDAO; // Assign the passed EncryptionKeyDAO
        this.passwordDAO = new PasswordDAO(connection, encryptionKeyDAO); // Pass both parameters

        this.currentPassword = password;

        populatePasswordDetails();
    }

    // Populate UI with data from PasswordModel
    private void populatePasswordDetails() {
        if (currentPassword != null) {
            appName.setText(currentPassword.getAppName());
            appCategory.setText(currentPassword.getCategoryId());
            appUsername.setText(currentPassword.getAppUsername());
            appPassword.setText(currentPassword.getEncryptedPassword());  // Decrypt if necessary
            appEmail.setText(currentPassword.getAppEmail());
            appUrl.setText(currentPassword.getAppUrl());
            appNotes.setText(currentPassword.getAppNotes());
            updateFavoriteButtonState(); // Ensure button state reflects current favorite status
        } else {
            logAndAlert("Current password data is missing.", null);
        }
    }

    // Copy password to clipboard
    @FXML
    private void handleCopyAppPassword() {
        try {
            copyToClipboard(appPassword.getText());
            System.out.println("Password copied to clipboard");
        } catch (Exception e) {
            logAndAlert("Password wasn't copied", e);
        }
    }

    // Copy username to clipboard
    @FXML
    private void handleCopyAppUsername() {
        try {
            copyToClipboard(appUsername.getText());
            System.out.println("AppUsername copied to clipboard");
        } catch (Exception e) {
            logAndAlert("AppUsername wasn't copied", e);
        }
    }

    // Copy email to clipboard
    @FXML
    private void handleCopyAppEmail() {
        try {
            copyToClipboard(appEmail.getText());
            System.out.println("AppEmail copied to clipboard");
        } catch (Exception e) {
            logAndAlert("AppEmail wasn't copied", e);
        }
    }

    @FXML
    private void handleEditPassword() {
        try {
            // Load the new FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/edit_form.fxml"));
            Parent root = loader.load();

            // Get the controller for the new form
            EditFormController editFormController = loader.getController();
            // Pass the connection, current password, and encryption key DAO
            editFormController.initialize(connection, currentPassword, encryptionKeyDAO);

            // Set the new scene to the current stage
            Stage currentStage = (Stage) favouritePasswordButton.getScene().getWindow(); // Get the current window
            currentStage.setScene(new Scene(root)); // Set the new scene

            // Optionally, set the title for the new scene
            currentStage.setTitle("Edit Password");

            // Show the updated window (if there are other UI elements to consider)
            currentStage.show();

        } catch (IOException | SQLException e) {
            logAndAlert("Failed to load edit form or update password.", e);
        }
    }

    // Move password to trash (soft delete)
    @FXML
    private void handleTrashPassword() {
        try {
            if (passwordDAO.trashPassword(currentPassword.getPasswordId())) {
                System.out.println("Password moved to trash.");
                closeWindow();
            } else {
                logAndAlert("Failed to move password to trash", null);
            }
        } catch (SQLException e) {
            logAndAlert("An error occurred while moving the password to trash.", e);
        }
    }

    // Mark the password as a favorite
    @FXML
    private void handleFavouritePassword() {
        try {
            currentPassword.setFavourite(!currentPassword.isFavourite());

            // Assuming you have the userId for the update
            String userId = currentPassword.getUserId(); // Adjust as necessary to obtain the user ID

            // Call updatePassword with both PasswordModel and userId
            if (passwordDAO.updatePassword(currentPassword, userId)) { // Make sure both parameters are passed
                System.out.println("Password marked as favorite.");
                updateFavoriteButtonState();
            } else {
                logAndAlert("Failed to update favorite status.", null);
            }
        } catch (SQLException e) {
            logAndAlert("An error occurred while updating the favorite status.", e);
        }
    }


    // Method to copy text to clipboard
    private void copyToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }

    // Update favorite button state
    private void updateFavoriteButtonState() {
        ImageView bookmarkIcon = (ImageView) favouritePasswordButton.getGraphic();
        String filledBookmarkPath = "/com/passify/icons/FilledBookmark.png";
        String bookmarkPath = "/com/passify/icons/Bookmark.png";

        if (currentPassword.isFavourite()) {
            bookmarkIcon.setImage(new Image(getClass().getResourceAsStream(filledBookmarkPath)));
        } else {
            bookmarkIcon.setImage(new Image(getClass().getResourceAsStream(bookmarkPath)));
        }
    }

    // Method to close the current window
    private void closeWindow() {
        if (stage != null) {
            stage.close();
        }
    }

    // Setter for the Stage (if needed for closing the window or further interaction)
    public void setStage(Stage stage) {
        this.stage = stage;
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
