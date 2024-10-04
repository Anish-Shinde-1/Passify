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
            // Load the JDBC connection
            Connection connection = JDBC_Connector.getConnection();
            if (connection == null) {
                System.err.println("Failed to establish database connection.");
                return;
            }

            // Load the login screen (corrected path here)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/passify/views/login_screen.fxml"));
            Parent loginView = loader.load();

            // Set up the scene and stage
            Scene scene = new Scene(loginView);
            primaryStage.setTitle("Passify - Password Manager");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

            // Ensure to pass the database connection to the LoginController
            LoginController controller = loader.getController();
            controller.setConnection(connection); // Pass the connection to the controller

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading the application: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args); // Start the JavaFX application
    }
}
