package views;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import managers.UserManager;

/**
 * Forgot Password View - Simple password reset
 */
public class ForgotPasswordViewFX {
    private Stage stage;
    private TextField txtEmail;
    private PasswordField txtNewPassword;
    private PasswordField txtConfirmPassword;
    
    public ForgotPasswordViewFX() {
        stage = new Stage();
        stage.setTitle("Resume Builder - Forgot Password");
        stage.initStyle(StageStyle.UNDECORATED);
        createUI();
    }
    
    private void createUI() {
        // Root container
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");
        
        // Main panel
        VBox mainPanel = new VBox(25);
        mainPanel.setAlignment(Pos.CENTER);
        mainPanel.setPadding(new Insets(60));
        mainPanel.setStyle("-fx-background-color: #16213e;");
        mainPanel.setMaxWidth(450);
        mainPanel.setMaxHeight(600);
        
        // Title
        Text title = new Text("Reset Password");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setFill(Color.WHITE);
        
        Text subtitle = new Text("Enter your email and new password");
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setFill(Color.web("#888"));
        
        // Email field
        VBox emailBox = createInputField("Email Address", "Enter your email", false);
        txtEmail = (TextField) emailBox.getChildren().get(1);
        
        // New Password field
        VBox newPasswordBox = createInputField("New Password", "Enter new password", true);
        txtNewPassword = (PasswordField) newPasswordBox.getChildren().get(1);
        
        // Confirm Password field
        VBox confirmPasswordBox = createInputField("Confirm Password", "Confirm new password", true);
        txtConfirmPassword = (PasswordField) confirmPasswordBox.getChildren().get(1);
        
        // Reset button
        Button resetBtn = new Button("Reset Password");
        resetBtn.setPrefWidth(350);
        resetBtn.setPrefHeight(50);
        resetBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        resetBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 25;");
        resetBtn.setEffect(new DropShadow(10, Color.rgb(102, 126, 234, 0.5)));
        
        resetBtn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), resetBtn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });
        resetBtn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), resetBtn);
            st.setToX(1);
            st.setToY(1);
            st.play();
        });
        resetBtn.setOnAction(e -> handleResetPassword());
        
        // Back to login link
        HBox backBox = new HBox(5);
        backBox.setAlignment(Pos.CENTER);
        Text backText = new Text("Remember your password?");
        backText.setFill(Color.web("#888"));
        backText.setFont(Font.font("Segoe UI", 13));
        
        Hyperlink backLink = new Hyperlink("Sign In");
        backLink.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        backLink.setStyle("-fx-text-fill: #667eea; -fx-border-color: transparent;");
        backLink.setOnAction(e -> {
            new LoginViewFX().show();
            stage.close();
        });
        
        backBox.getChildren().addAll(backText, backLink);
        
        mainPanel.getChildren().addAll(title, subtitle, emailBox, newPasswordBox, confirmPasswordBox, resetBtn, backBox);
        
        // Center the panel
        HBox centerBox = new HBox(mainPanel);
        centerBox.setAlignment(Pos.CENTER);
        root.setCenter(centerBox);
        
        // Close button
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 18px; ");
        closeBtn.setOnAction(e -> {
            new LoginViewFX().show();
            stage.close();
        });
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 18px; "));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 18px; "));
        
        HBox topBar = new HBox(closeBtn);
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setPadding(new Insets(10));
        root.setTop(topBar);
        
        Scene scene = new Scene(root, 500, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        
        // Add fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
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
        
        field.setPrefWidth(350);
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
    
    private void handleResetPassword() {
        String email = txtEmail.getText().trim();
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();
        
        // Validation
        if (email.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "All fields are required!");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Passwords do not match!");
            return;
        }
        
        // Reset password
        boolean success = UserManager.resetPassword(email, newPassword);
        
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Password reset successfully!\n\nYou can now sign in with your new password.");
            // Go back to login
            new LoginViewFX().show();
            stage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed to reset password!\n\nPlease check:\n- Email exists in our system\n- Password meets requirements (min 6 characters)");
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


