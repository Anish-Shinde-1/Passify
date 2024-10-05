package com.passify.controller;

import com.passify.model.PasswordDAO;
import com.passify.model.PasswordModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.MouseEvent; // Correct import for JavaFX MouseEvent

public class AppCardController {

    @FXML
    private AnchorPane appCard;

    @FXML
    private Label appName;

    @FXML
    private Label appUrl;

    private PasswordDAO passwordDAO; // This will hold the DAO instance
    private PasswordModel passwordModel; // This will hold the password data
    private MainController mainController; // Reference to the main controller

    // Method to initialize the controller with the DAO, PasswordModel, and MainController
    public void initialize(PasswordDAO passwordDAO, PasswordModel passwordModel, MainController mainController) {
        this.passwordDAO = passwordDAO;
        this.passwordModel = passwordModel; // Store the password model
        this.mainController = mainController; // Set the main controller

        // Display the app name and email/URL with null checks
        appName.setText(passwordModel.getAppName() != null ? passwordModel.getAppName() : "Unknown App");
        appUrl.setText(passwordModel.getAppEmail() != null ? passwordModel.getAppEmail() : "No Email Provided");

        // Set up the click action on the AnchorPane
        appCard.setOnMouseClicked(this::handleViewDetails); // Correct the lambda expression
    }

    // Method to handle the AnchorPane click
    public void handleViewDetails(MouseEvent event) { // Make this method public
        if (mainController != null) {
            mainController.loadPasswordDetails(passwordModel); // Load the password details
        }
    }
}
