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
import managers.ResumeManager;
import managers.ThemeManager;
import managers.ThemeManager.ThemeColors;
import models.Resume;
import models.User;

/**
 * Modern Main Menu View - Dashboard Style
 */
public class MainViewFX {
    private Stage stage;
    
    public MainViewFX() {
        stage = new Stage();
        stage.setTitle("Resume Builder - Dashboard");
        stage.initStyle(StageStyle.UNDECORATED);
        createUI();
    }
    
    private BorderPane root;
    private ThemeColors colors;
    
    private void createUI() {
        colors = ThemeManager.getColors();
        root = new BorderPane();
        applyTheme();
        
        // Top bar
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        // Center content
        VBox centerContent = createCenterContent();
        root.setCenter(centerContent);
        
        Scene scene = new Scene(root, 1100, 750);
        stage.setScene(scene);
        stage.centerOnScreen();
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    private void applyTheme() {
        colors = ThemeManager.getColors();
        root.setStyle("-fx-background-color: " + colors.bgPrimary + ";");
        // Refresh UI when theme changes
        if (root.getTop() != null) {
            root.setTop(createTopBar());
        }
        if (root.getCenter() != null) {
            root.setCenter(createCenterContent());
        }
    }
    
    private HBox createTopBar() {
        colors = ThemeManager.getColors();
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(15, 30, 15, 30));
        bar.setStyle("-fx-background-color: " + colors.bgSecondary + ";");
        
        // Logo
        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        
        Circle logoCircle = new Circle(20);
        Stop[] stops = new Stop[] {
            new Stop(0, Color.web(colors.accentPrimary)),
            new Stop(1, Color.web(colors.accentSecondary))
        };
        logoCircle.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops));
        
        Text logoText = new Text("Resume Builder");
        logoText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        logoText.setFill(Color.web(colors.textPrimary));
        
        logoBox.getChildren().addAll(logoCircle, logoText);
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // User info
        HBox userBox = new HBox(15);
        userBox.setAlignment(Pos.CENTER_RIGHT);
        
        User currentUser = SessionManager.getCurrentUser();
        String userName = currentUser != null ? 
            (currentUser.getFullName() != null ? currentUser.getFullName() : currentUser.getUsername()) : "User";
        
        Circle avatar = new Circle(18);
        avatar.setFill(Color.web(colors.accentPrimary));
        Text avatarText = new Text(userName.substring(0, 1).toUpperCase());
        avatarText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        avatarText.setFill(Color.WHITE);
        StackPane avatarPane = new StackPane(avatar, avatarText);
        
        Text userNameText = new Text(userName);
        userNameText.setFont(Font.font("Segoe UI", 14));
        userNameText.setFill(Color.web(colors.textPrimary));
        
        // Theme toggle button
        Button themeBtn = new Button(ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK ? "☀️" : "🌙");
        themeBtn.setStyle(
            "-fx-background-color: " + colors.bgCard + "; " +
            "-fx-text-fill: " + colors.textPrimary + "; " +
            "-fx-font-size: 16px; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 20; " +
            "-fx-padding: 5 12; " +
            "-fx-border-color: " + colors.border + "; " +
            "-fx-border-radius: 20;"
        );
        themeBtn.setOnAction(e -> {
            ThemeManager.toggleTheme();
            applyTheme();
            themeBtn.setText(ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK ? "☀️" : "🌙");
        });
        themeBtn.setOnMouseEntered(e -> themeBtn.setStyle(
            "-fx-background-color: " + colors.bgHover + "; " +
            "-fx-text-fill: " + colors.textPrimary + "; " +
            "-fx-font-size: 16px; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 20; " +
            "-fx-padding: 5 12; " +
            "-fx-border-color: " + colors.accentPrimary + "; " +
            "-fx-border-radius: 20;"
        ));
        themeBtn.setOnMouseExited(e -> themeBtn.setStyle(
            "-fx-background-color: " + colors.bgCard + "; " +
            "-fx-text-fill: " + colors.textPrimary + "; " +
            "-fx-font-size: 16px; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 20; " +
            "-fx-padding: 5 12; " +
            "-fx-border-color: " + colors.border + "; " +
            "-fx-border-radius: 20;"
        ));
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: " + colors.error + "; " +
            "-fx-font-size: 13px; " +
            "-fx-cursor: hand; " +
            "-fx-border-color: " + colors.error + "; " +
            "-fx-border-radius: 15; " +
            "-fx-padding: 5 15;"
        );
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(
            "-fx-background-color: " + colors.error + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 13px; " +
            "-fx-cursor: hand; " +
            "-fx-border-color: " + colors.error + "; " +
            "-fx-border-radius: 15; " +
            "-fx-padding: 5 15;"
        ));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: " + colors.error + "; " +
            "-fx-font-size: 13px; " +
            "-fx-cursor: hand; " +
            "-fx-border-color: " + colors.error + "; " +
            "-fx-border-radius: 15; " +
            "-fx-padding: 5 15;"
        ));
        logoutBtn.setOnAction(e -> {
            SessionManager.logout();
            new LoginViewFX().show();
            stage.close();
        });
        
        // Close button
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colors.textMuted + "; -fx-font-size: 16px; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> System.exit(0));
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color: " + colors.error + "; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand; -fx-background-radius: 3;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colors.textMuted + "; -fx-font-size: 16px; -fx-cursor: hand;"));
        
        userBox.getChildren().addAll(avatarPane, userNameText, themeBtn, logoutBtn, closeBtn);
        
        bar.getChildren().addAll(logoBox, spacer, userBox);
        
        return bar;
    }
    
    private VBox createCenterContent() {
        colors = ThemeManager.getColors();
        VBox content = new VBox(40);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50));
        
        // Welcome message
        User currentUser = SessionManager.getCurrentUser();
        String userName = currentUser != null ? 
            (currentUser.getFullName() != null ? currentUser.getFullName() : currentUser.getUsername()) : "User";
        
        Text welcomeText = new Text("Welcome back, " + userName + "!");
        welcomeText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        welcomeText.setFill(Color.web(colors.textPrimary));
        
        Text subText = new Text("What would you like to do today?");
        subText.setFont(Font.font("Segoe UI", 16));
        subText.setFill(Color.web(colors.textSecondary));
        
        // Action cards
        HBox cardsRow = new HBox(30);
        cardsRow.setAlignment(Pos.CENTER);
        
        VBox createCard = createActionCard(
            "Create Resume",
            "Start building a new\nprofessional resume",
            "#667eea", "#764ba2",
            "📝"
        );
        createCard.setOnMouseClicked(e -> {
            new ResumeFormViewFX().show();
            stage.close();
        });
        
        VBox editCard = createActionCard(
            "Edit Resume",
            "Continue working on\nyour existing resume",
            "#11998e", "#38ef7d",
            "✏️"
        );
        editCard.setOnMouseClicked(e -> {
            Resume resume = ResumeManager.getLastResume();
            if (resume != null) {
                new ResumeFormViewFX(resume).show();
                stage.close();
            } else {
                showAlert("No resume found. Create one first!");
            }
        });
        
        VBox previewCard = createActionCard(
            "Preview CV",
            "See how your resume\nlooks to employers",
            "#f093fb", "#f5576c",
            "👁️"
        );
        previewCard.setOnMouseClicked(e -> {
            new ProfessionalCVViewFX().show();
            stage.close();
        });
        
        VBox exportCard = createActionCard(
            "Export Resume",
            "Download as PDF\nor Word document",
            "#4facfe", "#00f2fe",
            "💾"
        );
        exportCard.setOnMouseClicked(e -> {
            new ExportViewFX().show();
            stage.close();
        });
        
        cardsRow.getChildren().addAll(createCard, editCard, previewCard, exportCard);
        
        // Second row with AI feature and Theme selection
        HBox cardsRow2 = new HBox(30);
        cardsRow2.setAlignment(Pos.CENTER);
        
        VBox themeCard = createActionCard(
            "🎨 Choose Theme",
            "Select CV template\n(Classic, Modern, etc.)",
            "#f39c12", "#e67e22",
            "🎨"
        );
        themeCard.setOnMouseClicked(e -> {
            new ThemeSelectionViewFX().show();
            stage.close();
        });
        
        VBox aiJobCard = createActionCard(
            "🤖 AI Job Finder",
            "Get job suggestions\nbased on your skills",
            "#00d2ff", "#3a7bd5",
            "🎯"
        );
        aiJobCard.setOnMouseClicked(e -> {
            new JobSuggestionViewFX().show();
        });
        
        VBox qrCard = createActionCard(
            "📱 Download App",
            "Scan QR code to\ndownload on mobile",
            "#f093fb", "#f5576c",
            "📱"
        );
        qrCard.setOnMouseClicked(e -> {
            new QRCodeViewFX().show();
        });
        
        cardsRow2.getChildren().addAll(themeCard, aiJobCard, qrCard);
        
        content.getChildren().addAll(welcomeText, subText, cardsRow, cardsRow2);
        
        return content;
    }
    
    private VBox createActionCard(String title, String description, String color1, String color2, String icon) {
        colors = ThemeManager.getColors();
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(220);
        card.setPrefHeight(200);
        card.setPadding(new Insets(25));
        card.setStyle(
            "-fx-background-color: " + colors.bgCard + "; " +
            "-fx-background-radius: 20; " +
            "-fx-cursor: hand; " +
            "-fx-border-color: " + colors.border + "; " +
            "-fx-border-radius: 20;"
        );
        card.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, ThemeManager.getCurrentTheme() == ThemeManager.Theme.DARK ? 0.3 : 0.1)));
        
        // Icon with gradient background
        Circle iconCircle = new Circle(30);
        Stop[] stops = new Stop[] {
            new Stop(0, Color.web(color1)),
            new Stop(1, Color.web(color2))
        };
        iconCircle.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops));
        
        Text iconText = new Text(icon);
        iconText.setFont(Font.font(24));
        
        StackPane iconPane = new StackPane(iconCircle, iconText);
        
        Text titleText = new Text(title);
        titleText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        titleText.setFill(Color.web(colors.textPrimary));
        
        Text descText = new Text(description);
        descText.setFont(Font.font("Segoe UI", 12));
        descText.setFill(Color.web(colors.textSecondary));
        descText.setStyle("-fx-text-alignment: center;");
        
        card.getChildren().addAll(iconPane, titleText, descText);
        
        // Hover animation
        card.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), card);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
            card.setStyle(
                "-fx-background-color: " + colors.bgHover + "; " +
                "-fx-background-radius: 20; " +
                "-fx-cursor: hand; " +
                "-fx-border-color: " + color1 + "; " +
                "-fx-border-radius: 20; " +
                "-fx-border-width: 2;"
            );
        });
        
        card.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), card);
            st.setToX(1);
            st.setToY(1);
            st.play();
            card.setStyle(
                "-fx-background-color: " + colors.bgCard + "; " +
                "-fx-background-radius: 20; " +
                "-fx-cursor: hand; " +
                "-fx-border-color: " + colors.border + "; " +
                "-fx-border-radius: 20;"
            );
        });
        
        return card;
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void show() {
        stage.show();
    }
}
