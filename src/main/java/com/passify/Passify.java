package com.passify;

import com.passify.controller.LoginController;
import com.passify.utils.JDBC_Connector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;

public class Passify extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Establish a connection to the database
            Connection connection = JDBC_Connector.getConnection();
            if (connection == null) {
                System.err.println("Failed to establish database connection.");
                return;
            }

            // Load the login screen layout from the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/login_screen.fxml"));
            Parent loginView = loader.load();

            // Set up and display the main application window with the login screen
            Scene scene = new Scene(loginView);
            primaryStage.setTitle("Passify - Password Manager");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false); // Prevent resizing of the application window
            primaryStage.show();

            // Inject the database connection into the LoginController
            LoginController controller = loader.getController();
            controller.setConnection(connection);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading the application: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
