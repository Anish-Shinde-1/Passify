package com.passify.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

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

}
