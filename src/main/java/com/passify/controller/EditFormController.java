package com.passify.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class EditFormController {

    @FXML
    private ChoiceBox<?> appCategoryList;

    @FXML
    private TextField appEmail;

    @FXML
    private TextField appName;

    @FXML
    private TextField appNotes;

    @FXML
    private PasswordField appPassword;

    @FXML
    private TextField appUsername;

    @FXML
    private Button cancelEditButton;

    @FXML
    private Pane editPasswordPanel;

    @FXML
    private Button generatePasswordButton;

    @FXML
    private Button saveEditButton;

}
