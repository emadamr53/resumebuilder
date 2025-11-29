package com.mycompany.mavenproject2;

import javafx.application.Application;
import javafx.stage.Stage;
import views.LoginViewFX;
import utils.DatabaseManager;

/**
 * Professional Resume Builder Application
 * JavaFX Application Entry Point
 */
public class Mavenproject2 extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Initialize database on startup
        DatabaseManager.initializeDatabase();
        
        // Start with login screen
        LoginViewFX loginView = new LoginViewFX();
        loginView.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
