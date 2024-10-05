package com.passify.controller;

import com.passify.model.PasswordDAO;
import com.passify.model.PasswordModel;
import com.passify.model.UserModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MainController {

    @FXML
    private Button accountButton;
    @FXML
    private Button addNewPasswordButton;
    @FXML
    private Button all_itemsButton;
    @FXML
    private VBox appListHolder;
    @FXML
    private Button cardTypeButton;
    @FXML
    private Button favouritesButton;
    @FXML
    private Button identityTypeButton;
    @FXML
    private Button loginTypeButton;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private Button miscCategoryButton;
    @FXML
    private VBox navigationPanel;
    @FXML
    private VBox pageHolder;
    @FXML
    private TextField searchBar;
    @FXML
    private Button signoutButton;
    @FXML
    private Button socialCategoryButton;
    @FXML
    private Button trashButton;
    @FXML
    private Button workCategoryButton;

    private PasswordDAO passwordDAO; // Instance for accessing password data
    private String userId; // User ID for fetching passwords
    private Connection connection;
    private UserModel currentUser;
    // No-argument constructor for FXML
    public MainController() {
    }

    // Constructor to initialize PasswordDAO
    public void initializeDependencies(Connection connection, UserModel user) throws SQLException {
        this.currentUser = user;
        this.connection = connection;
        this.userId = user.getUserId(); // Get user ID from UserModel
        this.passwordDAO = new PasswordDAO(connection); // Initialize PasswordDAO with the DB connection
    }

    @FXML
    public void initialize() {
        // Initialize buttons with action listeners
        all_itemsButton.setOnAction(this::loadAllPasswords);
        workCategoryButton.setOnAction(this::loadWorkPasswords);
        socialCategoryButton.setOnAction(this::loadSocialPasswords);
        miscCategoryButton.setOnAction(this::loadMiscPasswords);
    }

    // Load all passwords
    @FXML
    private void loadAllPasswords(ActionEvent event) {
        System.out.println("All Items button clicked.");
        loadAllSavedPasswords(); // Load all saved passwords directly
    }

    // Load work passwords
    @FXML
    private void loadWorkPasswords(ActionEvent event) {
        System.out.println("Work Category button clicked.");
        loadPasswordsByCategory(PasswordModel.Category.WORK); // Load passwords by category enum
    }

    // Load social passwords
    @FXML
    private void loadSocialPasswords(ActionEvent event) {
        System.out.println("Social Category button clicked.");
        loadPasswordsByCategory(PasswordModel.Category.SOCIAL); // Load passwords by category enum
    }

    // Load miscellaneous passwords
    @FXML
    private void loadMiscPasswords(ActionEvent event) {
        System.out.println("Miscellaneous Category button clicked.");
        loadPasswordsByCategory(PasswordModel.Category.MISC); // Load passwords by category enum
    }

    // Helper method to load all saved passwords
    private void loadAllSavedPasswords() {
        try {
            Optional<List<PasswordModel>> passwordsOpt = passwordDAO.getAllPasswordsForUser(userId); // Get all passwords

            if (passwordsOpt.isPresent()) {
                displayPasswords(passwordsOpt.get()); // Display retrieved passwords
                System.out.println("Loaded all saved passwords successfully.");
            } else {
                appListHolder.getChildren().clear(); // Clear the list if no items found
                System.out.println("No passwords found for the user.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL errors appropriately
            System.out.printf("Failed to load all passwords due to SQL error: %s%n", e.getMessage());
        }
    }

    // Helper method to load passwords by category
    private void loadPasswordsByCategory(PasswordModel.Category category) {
        System.out.printf("Loading passwords for category: %s%n", category);
        try {
            Optional<List<PasswordModel>> passwordsOpt = passwordDAO.getAllPasswordsForUser(userId); // Get all passwords
            List<PasswordModel> filteredPasswords = passwordsOpt
                    .map(passwords -> passwords.stream()
                            .filter(password -> password.getCategory() == category) // Filter by category
                            .toList()) // Collect filtered passwords
                    .orElse(List.of()); // If empty, return empty list

            if (!filteredPasswords.isEmpty()) {
                displayPasswords(filteredPasswords); // Display retrieved passwords
                System.out.printf("Loaded %s passwords successfully.%n", category);
            } else {
                appListHolder.getChildren().clear(); // Clear the list if no items found
                System.out.printf("No passwords found for category: %s%n", category);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL errors appropriately
            System.out.printf("Failed to load passwords for category %s due to SQL error: %s%n", category, e.getMessage());
        }
    }

    // Method to display passwords in the appListHolder
    private void displayPasswords(List<PasswordModel> passwords) {
        appListHolder.getChildren().clear(); // Clear existing items
        for (PasswordModel password : passwords) {
            try {
                // Load AppCard FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/app-card.fxml"));
                Parent appCard = loader.load();

                // Get the controller and initialize it
                AppCardController appCardController = loader.getController();
                appCardController.initialize(passwordDAO, password, this); // Initialize with PasswordModel and MainController

                appListHolder.getChildren().add(appCard); // Add AppCard to the list holder
            } catch (IOException e) {
                e.printStackTrace(); // Handle errors while loading AppCard FXML
                System.out.println("Failed to load AppCard FXML.");
            }
        }
        System.out.println("Displayed passwords in the appListHolder.");
    }


    public void loadPasswordDetails(PasswordModel password) {
        try {
            // Load the password_details.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/password_details.fxml"));
            Parent passwordDetailsView = loader.load();

            // Get the PasswordDetailsController
            PasswordDetailsController passwordDetailsController = loader.getController();

            // Set the mainController reference
            passwordDetailsController.setMainController(this);

            // Initialize the controller with necessary dependencies
            passwordDetailsController.initialize(connection, password, currentUser, this);

            // Clear the existing content in the pageHolder
            pageHolder.getChildren().clear();
            // Add the new view to the pageHolder
            pageHolder.getChildren().add(passwordDetailsView);

            System.out.println("Password details loaded successfully.");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to load password details FXML.");
        }
    }

    public VBox getPageHolder() {
        return pageHolder;
    }

    public void setPageHolderContent(Parent content) {
        pageHolder.getChildren().clear(); // Clear any existing content
        pageHolder.getChildren().add(content); // Add the new content to the pageHolder
    }

    // Placeholder for future methods (e.g., signOut, search functionality, etc.)
    private void signOut() {
        // Implement sign-out logic (e.g., redirect to login screen)
        System.out.println("Sign out method called.");
    }
}
