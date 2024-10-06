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

    // Injecting the database connection from the main application or other controller
    private Connection connection;
    private UserModel currentUser; // Assuming you want to track the current user

    public void initialize(Connection connection, PasswordModel password, UserModel user, MainController mainController) throws SQLException {
        this.connection = connection;
        this.passwordDAO = new PasswordDAO(connection); // Only require the PasswordDAO
        this.currentUser = user; // Store the current user
        this.currentPassword = password;
        this.mainController = mainController; // Store the MainController

        populatePasswordDetails();
    }

    // Populate UI with data from PasswordModel
    private void populatePasswordDetails() {
        if (currentPassword != null) {
            appName.setText(currentPassword.getAppName());
            appCategory.setText(currentPassword.getCategory().name()); // Assuming enum for category
            appUsername.setText(currentPassword.getAppUsername());
            appPassword.setText("                "); // Do not show the encrypted password
            appEmail.setText(currentPassword.getAppEmail());
            appUrl.setText(currentPassword.getAppUrl());
            appNotes.setText(currentPassword.getAppNotes());
            updateFavoriteButtonState();
        }
//        else {
//            logAndAlert("Current password data is missing.", null);
//        }
    }

    // Copy password to clipboard
    @FXML
    private void handleCopyAppPassword() {
        try {
            // Decrypt password before copying
            Optional<PasswordModel> passwordOpt = passwordDAO.getPasswordById(currentPassword.getPasswordId(), currentUser);
            if (passwordOpt.isPresent()) {
                String decryptedPassword = passwordOpt.get().getAppPassword(); // Corrected to appPassword
                copyToClipboard(decryptedPassword);
                decryptedPassword = null; // Clear sensitive data
                showCopyConfirmation("Password copied to clipboard!");
                System.out.println("Password copied to clipboard");
            } else {
                logAndAlert("Unable to retrieve the password for copying.", null);
            }
        } catch (Exception e) {
            logAndAlert("Password wasn't copied", e);
        }
    }

    // Copy username to clipboard
    @FXML
    private void handleCopyAppUsername() {
        try {
            String username = appUsername.getText();
            copyToClipboard(username);
            showCopyConfirmation("Username copied to clipboard!"); // Show confirmation message
            System.out.println("AppUsername copied to clipboard");
        } catch (Exception e) {
            logAndAlert("AppUsername wasn't copied", e);
        }
    }

    // Copy email to clipboard
    @FXML
    private void handleCopyAppEmail() {
        try {
            String email = appEmail.getText();
            copyToClipboard(email);
            showCopyConfirmation("Email copied to clipboard!"); // Show confirmation message
            System.out.println("AppEmail copied to clipboard");
        } catch (Exception e) {
            logAndAlert("AppEmail wasn't copied", e);
        }
    }

    // Method to show a confirmation alert when something is copied
    private void showCopyConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Copy Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleEditPassword() {
        try {
            // Load the edit_form.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/edit_form.fxml"));
            Parent editFormView = loader.load();

            // Get the EditFormController and initialize it
            EditFormController editFormController = loader.getController();

            // Pass the MainController (this.mainController) along with other arguments
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

            // Update the password's favorite status
            if (passwordDAO.updatePassword(currentPassword, currentUser)) { // Assuming updatePassword requires currentUser
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

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // Helper method to log and display errors in UI
    private void logAndAlert(String message, Exception e) {
        System.out.println(message);
        if (e != null) {
            e.printStackTrace();
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        if (e != null) {
            alert.setContentText(e.getMessage());
        }
        alert.showAndWait();
    }
}
