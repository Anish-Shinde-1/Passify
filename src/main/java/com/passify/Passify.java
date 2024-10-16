package com.passify;

import com.passify.controller.LoginController;
import com.passify.utils.JDBC_Connector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;

/**
 * Passify is the main entry point for the JavaFX Password Manager application.
 * It initializes the JavaFX framework, sets up the login screen, and establishes
 * a connection to the database to be used throughout the application.
 *
 * This class extends the JavaFX {@link Application} class to manage the lifecycle
 * of the application.
 */
public class Passify extends Application {

    /**
     * The start method is the entry point for the JavaFX application.
     * It loads the login screen from an FXML file and establishes a connection to the database.
     *
     * @param primaryStage The primary stage for the application, onto which the login scene is set.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the JDBC connection
            Connection connection = JDBC_Connector.getConnection();
            if (connection == null) {
                System.err.println("Failed to establish database connection.");
                return;
            }

            // Load the login screen from the FXML file (ensure the correct path is provided)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/login_screen.fxml"));
            Parent loginView = loader.load();

            // Set up the scene with the loaded login view and configure the primary stage
            Scene scene = new Scene(loginView);
            primaryStage.setTitle("Passify - Password Manager");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false); // Disallow resizing of the application window
            primaryStage.show(); // Display the primary stage

            // Pass the database connection to the LoginController
            LoginController controller = loader.getController();
            controller.setConnection(connection); // Inject the JDBC connection

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading the application: " + e.getMessage());
        }
    }

    /**
     * The main method serves as the application's entry point.
     * It calls the {@link #launch(String...)} method to start the JavaFX application.
     *
     * @param args Command-line arguments passed to the application (if any).
     */
    public static void main(String[] args) {
        launch(args); // Start the JavaFX application
    }
}
