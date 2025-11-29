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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import managers.UserManager;
import utils.ValidationUtils;

/**
 * Modern Sign Up View - Clean Minimalist Design
 */
public class SignUpViewFX {
    private Stage stage;
    private TextField txtFullName, txtUsername, txtEmail;
    private PasswordField txtPassword, txtConfirmPassword;
    
    public SignUpViewFX() {
        stage = new Stage();
        stage.setTitle("Resume Builder - Sign Up");
        stage.initStyle(StageStyle.UNDECORATED);
        createUI();
    }
    
    private void createUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");
        
        // Left side - Decorative panel
        VBox leftPanel = createLeftPanel();
        
        // Right side - Sign up form
        ScrollPane rightScroll = new ScrollPane();
        rightScroll.setFitToWidth(true);
        rightScroll.setStyle("-fx-background-color: #16213e; -fx-background: #16213e;");
        VBox rightPanel = createRightPanel();
        rightScroll.setContent(rightPanel);
        
        HBox mainLayout = new HBox();
        mainLayout.getChildren().addAll(leftPanel, rightScroll);
        HBox.setHgrow(rightScroll, Priority.ALWAYS);
        
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
        
        Scene scene = new Scene(root, 950, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    private VBox createLeftPanel() {
        VBox panel = new VBox(30);
        panel.setAlignment(Pos.CENTER);
        panel.setPrefWidth(380);
        panel.setPadding(new Insets(50));
        
        Stop[] stops = new Stop[] {
            new Stop(0, Color.web("#11998e")),
            new Stop(1, Color.web("#38ef7d"))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        panel.setBackground(new Background(new BackgroundFill(gradient, null, null)));
        
        // Logo
        Circle logoCircle = new Circle(50);
        logoCircle.setFill(Color.WHITE);
        logoCircle.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.3)));
        
        Text logoText = new Text("CV");
        logoText.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        logoText.setFill(Color.web("#11998e"));
        
        StackPane logo = new StackPane(logoCircle, logoText);
        
        Text welcomeText = new Text("Join Us Today!");
        welcomeText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        welcomeText.setFill(Color.WHITE);
        
        Text subText = new Text("Create your account and start\nbuilding amazing resumes");
        subText.setFont(Font.font("Segoe UI", 14));
        subText.setFill(Color.rgb(255, 255, 255, 0.8));
        subText.setStyle("-fx-text-alignment: center;");
        
        // Features list
        VBox features = new VBox(12);
        features.setAlignment(Pos.CENTER_LEFT);
        features.setPadding(new Insets(30, 0, 0, 0));
        
        String[] featureList = {"✓ Professional templates", "✓ Easy to use builder", "✓ Export to PDF & Word", "✓ Save your progress"};
        for (String f : featureList) {
            Text featureText = new Text(f);
            featureText.setFont(Font.font("Segoe UI", 14));
            featureText.setFill(Color.WHITE);
            features.getChildren().add(featureText);
        }
        
        panel.getChildren().addAll(logo, welcomeText, subText, features);
        
        return panel;
    }
    
    private VBox createRightPanel() {
        VBox panel = new VBox(20);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(40, 60, 40, 60));
        panel.setStyle("-fx-background-color: #16213e;");
        
        Text title = new Text("Create Account");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setFill(Color.WHITE);
        
        Text subtitle = new Text("Fill in your details to get started");
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setFill(Color.web("#888"));
        
        // Form fields
        VBox fullNameBox = createInputField("Full Name", "Enter your full name", false);
        txtFullName = (TextField) fullNameBox.getChildren().get(1);
        
        VBox usernameBox = createInputField("Username", "Choose a username", false);
        txtUsername = (TextField) usernameBox.getChildren().get(1);
        
        VBox emailBox = createInputField("Email Address", "Enter your email", false);
        txtEmail = (TextField) emailBox.getChildren().get(1);
        
        VBox passwordBox = createInputField("Password", "Create a password", true);
        txtPassword = (PasswordField) passwordBox.getChildren().get(1);
        
        VBox confirmBox = createInputField("Confirm Password", "Confirm your password", true);
        txtConfirmPassword = (PasswordField) confirmBox.getChildren().get(1);
        
        // Sign up button
        Button signUpBtn = new Button("Create Account");
        signUpBtn.setPrefWidth(320);
        signUpBtn.setPrefHeight(50);
        signUpBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        signUpBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #11998e, #38ef7d); " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 25; " +
            "-fx-cursor: hand;"
        );
        signUpBtn.setEffect(new DropShadow(10, Color.rgb(17, 153, 142, 0.5)));
        
        signUpBtn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), signUpBtn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });
        signUpBtn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), signUpBtn);
            st.setToX(1);
            st.setToY(1);
            st.play();
        });
        signUpBtn.setOnAction(e -> handleSignUp());
        
        // Login link
        HBox loginBox = new HBox(5);
        loginBox.setAlignment(Pos.CENTER);
        Text hasAccount = new Text("Already have an account?");
        hasAccount.setFill(Color.web("#888"));
        hasAccount.setFont(Font.font("Segoe UI", 13));
        
        Hyperlink loginLink = new Hyperlink("Sign In");
        loginLink.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        loginLink.setStyle("-fx-text-fill: #11998e; -fx-border-color: transparent;");
        loginLink.setOnAction(e -> {
            new LoginViewFX().show();
            stage.close();
        });
        
        loginBox.getChildren().addAll(hasAccount, loginLink);
        
        panel.getChildren().addAll(title, subtitle, fullNameBox, usernameBox, emailBox, passwordBox, confirmBox, signUpBtn, loginBox);
        
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
                    "-fx-border-color: #11998e; " +
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
    
    private void handleSignUp() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();
        
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill in all fields!");
            return;
        }
        
        if (!ValidationUtils.isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Please enter a valid email!");
            return;
        }
        
        if (password.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Password must be at least 6 characters!");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Passwords do not match!");
            return;
        }
        
        // Check which field already exists for better error message
        if (UserManager.usernameExists(username)) {
            showAlert(Alert.AlertType.ERROR, "Username already exists! Please choose a different username.");
            return;
        }
        
        if (UserManager.emailExists(email)) {
            showAlert(Alert.AlertType.ERROR, "Email already exists! If you have an account, please use the Sign In button to login.");
            return;
        }
        
        boolean success = UserManager.registerUser(username, email, password, fullName);
        
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Account created successfully! Please login.");
            new LoginViewFX().show();
            stage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration failed! Please check your input and try again.");
        }
    }
    
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : "Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void show() {
        stage.show();
    }
}
