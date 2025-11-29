package com.mycompany.mavenproject2;

/**
 * Launcher class - Entry point for the application
 * This class is needed because JavaFX Application class cannot be 
 * directly launched in Java 11+ without module path issues
 */
public class Launcher {
    
    public static void main(String[] args) {
        Mavenproject2.main(args);
    }
}

