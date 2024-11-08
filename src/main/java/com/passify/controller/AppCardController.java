package com.passify.controller;

import com.passify.model.PasswordDAO;
import com.passify.model.PasswordModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.MouseEvent;

public class AppCardController {

    @FXML
    private AnchorPane appCard;

    @FXML
    private Label appName;

    @FXML
    private Label appUrl;

    private PasswordDAO passwordDAO;
    private PasswordModel passwordModel;
    private MainController mainController;

    public void initialize(PasswordDAO passwordDAO, PasswordModel passwordModel, MainController mainController) {
        this.passwordDAO = passwordDAO;
        this.passwordModel = passwordModel;
        this.mainController = mainController;

        // Display the app name and email/URL with null checks
        appName.setText(passwordModel.getAppName() != null ? passwordModel.getAppName() : "Unknown App");
        appUrl.setText(passwordModel.getAppEmail() != null ? passwordModel.getAppEmail() : "No Email Provided");

        // Set up the click action on the AnchorPane to view details
        appCard.setOnMouseClicked(this::handleViewDetails); // Link click event to handler
    }

    // Handle the action when the application card is clicked
    public void handleViewDetails(MouseEvent event) {
        if (mainController != null) {
            mainController.loadPasswordDetails(passwordModel); // Load the password details for the selected application
        }
    }
}
