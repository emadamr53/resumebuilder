package views;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import managers.UserManager;
import models.User;

/**
 * Modern Login View - Clean Minimalist Design
 */
public class LoginViewFX {
    private Stage stage;
    private TextField txtEmail;
    private PasswordField txtPassword;
    
    public LoginViewFX() {
        stage = new Stage();
        stage.setTitle("Resume Builder - Login");
        stage.initStyle(StageStyle.UNDECORATED);
        createUI();
    }
    
    private void createUI() {
        // Root container
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");
        
        // Left side - Decorative panel
        VBox leftPanel = createLeftPanel();
        
        // Right side - Login form
        VBox rightPanel = createRightPanel();
        
        // Split layout
        HBox mainLayout = new HBox();
        mainLayout.getChildren().addAll(leftPanel, rightPanel);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);
        
        root.setCenter(mainLayout);
        
        // Close button
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 18px; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> System.exit(0));
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 18px; -fx-cursor: hand;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 18px; -fx-cursor: hand;"));
        
        HBox topBar = new HBox(closeBtn);
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setPadding(new Insets(10));
        root.setTop(topBar);
        
        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.centerOnScreen();
        
        // Add fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    private VBox createLeftPanel() {
        VBox panel = new VBox(30);
        panel.setAlignment(Pos.CENTER);
        panel.setPrefWidth(400);
        panel.setPadding(new Insets(50));
        
        // Gradient background
        Stop[] stops = new Stop[] {
            new Stop(0, Color.web("#667eea")),
            new Stop(1, Color.web("#764ba2"))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        panel.setBackground(new Background(new BackgroundFill(gradient, null, null)));
        
        // Logo circle
        Circle logoCircle = new Circle(50);
        logoCircle.setFill(Color.WHITE);
        logoCircle.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.3)));
        
        Text logoText = new Text("CV");
        logoText.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        logoText.setFill(Color.web("#667eea"));
        
        StackPane logo = new StackPane(logoCircle, logoText);
        
        // Welcome text
        Text welcomeText = new Text("Welcome Back!");
        welcomeText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        welcomeText.setFill(Color.WHITE);
        
        Text subText = new Text("Build your professional resume\nwith our easy-to-use builder");
        subText.setFont(Font.font("Segoe UI", 14));
        subText.setFill(Color.rgb(255, 255, 255, 0.8));
        subText.setStyle("-fx-text-alignment: center;");
        
        // Decorative circles
        Circle c1 = new Circle(100, Color.rgb(255, 255, 255, 0.1));
        Circle c2 = new Circle(60, Color.rgb(255, 255, 255, 0.1));
        
        panel.getChildren().addAll(logo, welcomeText, subText);
        
        return panel;
    }
    
    private VBox createRightPanel() {
        VBox panel = new VBox(25);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(60));
        panel.setStyle("-fx-background-color: #16213e;");
        
        // Title
        Text title = new Text("Sign In");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setFill(Color.WHITE);
        
        Text subtitle = new Text("Enter your credentials to continue");
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setFill(Color.web("#888"));
        
        // Email field
        VBox emailBox = createInputField("Email Address", "Enter your email", false);
        txtEmail = (TextField) emailBox.getChildren().get(1);
        
        // Password field
        VBox passwordBox = createInputField("Password", "Enter your password", true);
        txtPassword = (PasswordField) passwordBox.getChildren().get(1);
        
        // Login button
        Button loginBtn = new Button("Sign In");
        loginBtn.setPrefWidth(320);
        loginBtn.setPrefHeight(50);
        loginBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        loginBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 25; " +
            "-fx-cursor: hand;"
        );
        loginBtn.setEffect(new DropShadow(10, Color.rgb(102, 126, 234, 0.5)));
        
        loginBtn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), loginBtn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });
        loginBtn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), loginBtn);
            st.setToX(1);
            st.setToY(1);
            st.play();
        });
        loginBtn.setOnAction(e -> handleLogin());
        
        // Forgot password link
        HBox forgotPasswordBox = new HBox();
        forgotPasswordBox.setAlignment(Pos.CENTER_RIGHT);
        Hyperlink forgotPasswordLink = new Hyperlink("Forgot Password?");
        forgotPasswordLink.setFont(Font.font("Segoe UI", 12));
        forgotPasswordLink.setStyle("-fx-text-fill: #667eea; -fx-border-color: transparent;");
        forgotPasswordLink.setOnAction(e -> {
            new ForgotPasswordViewFX().show();
            stage.close();
        });
        forgotPasswordBox.getChildren().add(forgotPasswordLink);
        
        // Sign up link
        HBox signUpBox = new HBox(5);
        signUpBox.setAlignment(Pos.CENTER);
        Text noAccount = new Text("Don't have an account?");
        noAccount.setFill(Color.web("#888"));
        noAccount.setFont(Font.font("Segoe UI", 13));
        
        Hyperlink signUpLink = new Hyperlink("Sign Up");
        signUpLink.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        signUpLink.setStyle("-fx-text-fill: #667eea; -fx-border-color: transparent;");
        signUpLink.setOnAction(e -> {
            new SignUpViewFX().show();
            stage.close();
        });
        
        signUpBox.getChildren().addAll(noAccount, signUpLink);
        
        panel.getChildren().addAll(title, subtitle, emailBox, passwordBox, forgotPasswordBox, loginBtn, signUpBox);
        
        return panel;
    }
    
    private VBox createInputField(String label, String placeholder, boolean isPassword) {
        VBox box = new VBox(8);
        
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lbl.setTextFill(Color.web("#aaa"));
        
        Control field;
        if (isPassword) {
            field = new PasswordField();
            ((PasswordField) field).setPromptText(placeholder);
        } else {
            field = new TextField();
            ((TextField) field).setPromptText(placeholder);
        }
        
        field.setPrefWidth(320);
        field.setPrefHeight(45);
        field.setStyle(
            "-fx-background-color: #0f3460; " +
            "-fx-text-fill: white; " +
            "-fx-prompt-text-fill: #666; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #2a4a7a; " +
            "-fx-border-radius: 10; " +
            "-fx-padding: 10 15;"
        );
        
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                    "-fx-background-color: #0f3460; " +
                    "-fx-text-fill: white; " +
                    "-fx-prompt-text-fill: #666; " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-color: #667eea; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 10; " +
                    "-fx-padding: 10 15;"
                );
            } else {
                field.setStyle(
                    "-fx-background-color: #0f3460; " +
                    "-fx-text-fill: white; " +
                    "-fx-prompt-text-fill: #666; " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-color: #2a4a7a; " +
                    "-fx-border-radius: 10; " +
                    "-fx-padding: 10 15;"
                );
            }
        });
        
        box.getChildren().addAll(lbl, field);
        return box;
    }
    
    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Please fill in all fields!");
            return;
        }
        
        User user = UserManager.login(email, password);
        
        if (user != null) {
            SessionManager.setCurrentUser(user);
            // Clear resume cache when new user logs in
            managers.ResumeManager.clearCache();
            new MainViewFX().show();
            stage.close();
        } else {
            showAlert("Invalid email or password!");
        }
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void show() {
        stage.show();
    }
}
