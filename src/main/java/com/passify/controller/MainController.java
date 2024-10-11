package com.passify.controller;

import com.passify.model.PasswordDAO;
import com.passify.model.PasswordModel;
import com.passify.model.UserModel;
import com.passify.utils.JDBC_Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the main application view.
 * This class handles user interactions in the main scene,
 * including loading and displaying passwords by category,
 * adding new passwords, and managing user sessions.
 */
public class MainController {

    @FXML
    private Button accountButton;
    @FXML
    private Button addNewPasswordButton;
    @FXML
    private Button all_itemsButton;
    @FXML
    private VBox appListHolder; // Holds the list of password entries
    @FXML
    private Button cardTypeButton;
    @FXML
    private Button favouritesButton;
    @FXML
    private Button identityTypeButton;
    @FXML
    private Button loginTypeButton;
    @FXML
    private BorderPane mainBorderPane; // Main layout for the application
    @FXML
    private Button miscCategoryButton;
    @FXML
    private VBox navigationPanel; // Panel for navigation buttons
    @FXML
    private VBox pageHolder; // Container for displaying detailed views
    @FXML
    private TextField searchBar; // Search bar for filtering passwords
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
    private Connection connection; // Database connection
    private UserModel currentUser; // Current logged-in user

    // No-argument constructor for FXML
    public MainController() {
    }

    /**
     * Initializes the controller with the provided database connection and user model.
     *
     * @param connection the database connection
     * @param user the currently logged-in user
     * @throws SQLException if a database access error occurs
     */
    public void initializeDependencies(Connection connection, UserModel user) throws SQLException {
        this.currentUser = user; // Store the current user
        this.connection = connection; // Set the database connection
        this.userId = user.getUserId(); // Get user ID from UserModel
        this.passwordDAO = new PasswordDAO(connection); // Initialize PasswordDAO with the DB connection
    }

    /**
     * Initializes the controller. This method sets up action listeners for buttons.
     */
    @FXML
    public void initialize() {
        // Initialize buttons with action listeners
        all_itemsButton.setOnAction(this::loadAllPasswords);
        workCategoryButton.setOnAction(this::loadWorkPasswords);
        socialCategoryButton.setOnAction(this::loadSocialPasswords);
        miscCategoryButton.setOnAction(this::loadMiscPasswords);
    }

    /**
     * Loads all saved passwords when the all_itemsButton is clicked.
     *
     * @param event the ActionEvent triggered by the button click
     */
    @FXML
    private void loadAllPasswords(ActionEvent event) {
        System.out.println("All Items button clicked.");
        loadAllSavedPasswords(); // Load all saved passwords directly
    }

    /**
     * Loads passwords categorized as work when the workCategoryButton is clicked.
     *
     * @param event the ActionEvent triggered by the button click
     */
    @FXML
    private void loadWorkPasswords(ActionEvent event) {
        System.out.println("Work Category button clicked.");
        loadPasswordsByCategory(PasswordModel.Category.WORK); // Load passwords by category enum
    }

    /**
     * Loads passwords categorized as social when the socialCategoryButton is clicked.
     *
     * @param event the ActionEvent triggered by the button click
     */
    @FXML
    private void loadSocialPasswords(ActionEvent event) {
        System.out.println("Social Category button clicked.");
        loadPasswordsByCategory(PasswordModel.Category.SOCIAL); // Load passwords by category enum
    }

    /**
     * Loads passwords categorized as miscellaneous when the miscCategoryButton is clicked.
     *
     * @param event the ActionEvent triggered by the button click
     */
    @FXML
    private void loadMiscPasswords(ActionEvent event) {
        System.out.println("Miscellaneous Category button clicked.");
        loadPasswordsByCategory(PasswordModel.Category.MISC); // Load passwords by category enum
    }

    /**
     * Loads and displays all saved passwords for the current user.
     */
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

    /**
     * Loads and displays passwords filtered by the specified category.
     *
     * @param category the category to filter passwords by
     */
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

    /**
     * Displays the provided list of passwords in the appListHolder.
     *
     * @param passwords the list of PasswordModel objects to display
     */
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

    /**
     * Loads the details of a selected password for viewing or editing.
     *
     * @param password the PasswordModel object whose details are to be loaded
     */
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

    /**
     * Retrieves the VBox that holds the page content.
     *
     * @return the VBox that holds the page content
     */
    public VBox getPageHolder() {
        return pageHolder;
    }

    /**
     * Sets the content of the pageHolder to the specified Parent content.
     *
     * @param content the new content to display in the pageHolder
     */
    public void setPageHolderContent(Parent content) {
        pageHolder.getChildren().clear(); // Clear any existing content
        pageHolder.getChildren().add(content); // Add the new content to the pageHolder
    }

    /**
     * Handles the action of adding a new password.
     */
    @FXML
    private void handleAddNewPassword() {
        try {
            // Load the edit_form.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/edit_form.fxml"));
            Parent root = loader.load();

            // Get the controller and set up the form for adding a new password
            EditFormController controller = loader.getController();
            controller.initialize(connection, null, currentUser, this); // Pass null as currentPassword for "Add" functionality

            // Clear existing content in the pageHolder
            pageHolder.getChildren().clear();
            // Add the new form to the pageHolder
            pageHolder.getChildren().add(root);

            System.out.println("Add New Password form loaded successfully in the pageHolder.");
        } catch (IOException | SQLException e) {
            System.out.println("Error loading the add new password form: " + e.getMessage());
        }
    }

    /**
     * Handles the action of signing out the current user.
     *
     * @param event the ActionEvent triggered by the button click
     */
    @FXML
    private void handleSignOut(ActionEvent event) {
        System.out.println("Sign out button clicked.");
        signOut(); // Call the sign-out method
    }

    /**
     * Signs out the current user and loads the login screen.
     */
    private void signOut() {
        try {
            // Load the login screen (login_screen.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/login_screen.fxml"));
            Parent loginView = loader.load();

            // Get the LoginController and set up any necessary data (if required)
            LoginController loginController = loader.getController();
            // You can initialize the loginController if needed
            Connection connection = JDBC_Connector.getConnection(); // Get a new connection
            loginController.setConnection(connection);

            // Switch back to the login scene
            Scene loginScene = new Scene(loginView);
            Stage currentStage = (Stage) signoutButton.getScene().getWindow(); // Get the current stage (window)
            currentStage.setScene(loginScene); // Set the login scene
            currentStage.show(); // Show the new scene

            System.out.println("User signed out and returned to login screen.");
        } catch (IOException | SQLException e) {
            e.printStackTrace(); // Handle errors
            System.out.println("Failed to load login screen during sign out.");
        }
    }
}
