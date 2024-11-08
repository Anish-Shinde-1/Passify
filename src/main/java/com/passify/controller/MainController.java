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

    private PasswordDAO passwordDAO;
    private String userId;
    private Connection connection;
    private UserModel currentUser;

    // Constructor for the MainController
    public MainController() {
    }

    // Initializes dependencies like database connection and user model
    public void initializeDependencies(Connection connection, UserModel user) throws SQLException {
        this.currentUser = user;
        this.connection = connection;
        this.userId = user.getUserId();
        this.passwordDAO = new PasswordDAO(connection);
    }

    // Initializes button actions
    @FXML
    public void initialize() {
        all_itemsButton.setOnAction(this::loadAllPasswords); // Action for loading all passwords
        workCategoryButton.setOnAction(this::loadWorkPasswords); // Action for loading work-related passwords
        socialCategoryButton.setOnAction(this::loadSocialPasswords); // Action for loading social passwords
        miscCategoryButton.setOnAction(this::loadMiscPasswords); // Action for loading miscellaneous passwords
    }

    // Loads all saved passwords when the 'All Items' button is clicked
    @FXML
    private void loadAllPasswords(ActionEvent event) {
        loadAllSavedPasswords();
    }

    // Loads passwords in the "Work" category when the work category button is clicked
    @FXML
    private void loadWorkPasswords(ActionEvent event) {
        loadPasswordsByCategory(PasswordModel.Category.WORK);
    }

    // Loads passwords in the "Social" category when the social category button is clicked
    @FXML
    private void loadSocialPasswords(ActionEvent event) {
        loadPasswordsByCategory(PasswordModel.Category.SOCIAL);
    }

    // Loads passwords in the "Miscellaneous" category when the misc category button is clicked
    @FXML
    private void loadMiscPasswords(ActionEvent event) {
        loadPasswordsByCategory(PasswordModel.Category.MISC);
    }

    // Loads all saved passwords for the user from the database
    private void loadAllSavedPasswords() {
        try {
            Optional<List<PasswordModel>> passwordsOpt = passwordDAO.getAllPasswordsForUser(userId);

            if (passwordsOpt.isPresent()) {
                displayPasswords(passwordsOpt.get()); // Display passwords if found
            } else {
                appListHolder.getChildren().clear(); // Clear the list if no passwords found
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Loads and filters passwords by the given category
    private void loadPasswordsByCategory(PasswordModel.Category category) {
        try {
            Optional<List<PasswordModel>> passwordsOpt = passwordDAO.getAllPasswordsForUser(userId);
            List<PasswordModel> filteredPasswords = passwordsOpt
                    .map(passwords -> passwords.stream()
                            .filter(password -> password.getCategory() == category) // Filters by the selected category
                            .toList())
                    .orElse(List.of()); // Return empty list if no passwords are found

            if (!filteredPasswords.isEmpty()) {
                displayPasswords(filteredPasswords); // Display filtered passwords
            } else {
                appListHolder.getChildren().clear(); // Clear list if no passwords found for the category
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Displays the list of passwords in the appListHolder
    private void displayPasswords(List<PasswordModel> passwords) {
        appListHolder.getChildren().clear(); // Clear existing passwords in the list
        for (PasswordModel password : passwords) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/app-card.fxml"));
                Parent appCard = loader.load(); // Load the AppCard FXML

                AppCardController appCardController = loader.getController();
                appCardController.initialize(passwordDAO, password, this); // Initialize AppCard with password data

                appListHolder.getChildren().add(appCard); // Add AppCard to the list
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Loads detailed view of a selected password for editing or viewing
    public void loadPasswordDetails(PasswordModel password) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/password_details.fxml"));
            Parent passwordDetailsView = loader.load();

            PasswordDetailsController passwordDetailsController = loader.getController();
            passwordDetailsController.setMainController(this);
            passwordDetailsController.initialize(connection, password, currentUser, this); // Initialize with data

            pageHolder.getChildren().clear(); // Clear existing content
            pageHolder.getChildren().add(passwordDetailsView); // Add password details view
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Getter for pageHolder (container for page content)
    public VBox getPageHolder() {
        return pageHolder;
    }

    // Sets the content of the pageHolder to a new view
    public void setPageHolderContent(Parent content) {
        pageHolder.getChildren().clear(); // Clear previous content
        pageHolder.getChildren().add(content); // Add new content
    }

    // Handles adding a new password
    @FXML
    private void handleAddNewPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/edit_form.fxml"));
            Parent root = loader.load();

            EditFormController controller = loader.getController();
            controller.initialize(connection, null, currentUser, this); // Initialize the form for adding a new password

            pageHolder.getChildren().clear(); // Clear previous content
            pageHolder.getChildren().add(root); // Add the new password form
        } catch (IOException | SQLException e) {
            System.out.println("Error loading the add new password form: " + e.getMessage());
        }
    }

    // Handles user sign out
    @FXML
    private void handleSignOut(ActionEvent event) {
        signOut(); // Calls the sign out method
    }

    // Signs out the user and redirects to the login screen
    private void signOut() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/login_screen.fxml"));
            Parent loginView = loader.load();

            LoginController loginController = loader.getController();
            Connection connection = JDBC_Connector.getConnection(); // Establishes new database connection
            loginController.setConnection(connection);

            Scene loginScene = new Scene(loginView);
            Stage currentStage = (Stage) signoutButton.getScene().getWindow();
            currentStage.setScene(loginScene); // Switches to the login scene
            currentStage.show();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
