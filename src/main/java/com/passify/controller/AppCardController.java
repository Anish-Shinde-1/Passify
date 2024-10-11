package com.passify.controller;

import com.passify.model.PasswordDAO;
import com.passify.model.PasswordModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.MouseEvent; // Correct import for JavaFX MouseEvent

/**
 * Controller for displaying an application card in the user interface.
 * This class manages the display of an application’s name and URL,
 * and handles user interactions with the card.
 */
public class AppCardController {

    @FXML
    private AnchorPane appCard; // AnchorPane representing the card UI for an application

    @FXML
    private Label appName; // Label displaying the application's name

    @FXML
    private Label appUrl; // Label displaying the application's URL or email

    private PasswordDAO passwordDAO; // Data access object for managing password data
    private PasswordModel passwordModel; // Model representing the password data for the application
    private MainController mainController; // Reference to the main application controller

    /**
     * Initializes the AppCardController with necessary data and UI elements.
     *
     * @param passwordDAO the PasswordDAO instance for accessing password data
     * @param passwordModel the PasswordModel instance containing the password information
     * @param mainController the MainController for navigating to password details
     */
    public void initialize(PasswordDAO passwordDAO, PasswordModel passwordModel, MainController mainController) {
        this.passwordDAO = passwordDAO; // Store the PasswordDAO instance
        this.passwordModel = passwordModel; // Store the PasswordModel instance
        this.mainController = mainController; // Set the reference to the main controller

        // Display the app name and email/URL with null checks
        appName.setText(passwordModel.getAppName() != null ? passwordModel.getAppName() : "Unknown App");
        appUrl.setText(passwordModel.getAppEmail() != null ? passwordModel.getAppEmail() : "No Email Provided");

        // Set up the click action on the AnchorPane to view details
        appCard.setOnMouseClicked(this::handleViewDetails); // Link click event to handler
    }

    /**
     * Handles the action when the application card is clicked.
     * This method navigates to the password details view for the selected application.
     *
     * @param event the MouseEvent triggered by the click
     */
    public void handleViewDetails(MouseEvent event) { // Make this method public
        if (mainController != null) {
            mainController.loadPasswordDetails(passwordModel); // Load the password details for the selected application
        }
    }
}
