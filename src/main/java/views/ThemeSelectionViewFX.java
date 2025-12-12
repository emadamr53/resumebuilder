package views;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import managers.ThemeManager;
import managers.ThemeSettingsManager;
import models.CVTheme;

/**
 * Theme Selection View - Allows users to choose CV theme
 */
public class ThemeSelectionViewFX {
    private Stage stage;
    private CVTheme selectedTheme;
    
    public ThemeSelectionViewFX() {
        stage = new Stage();
        stage.setTitle("Choose CV Theme");
        stage.initStyle(StageStyle.UNDECORATED);
        selectedTheme = ThemeManager.getSelectedTheme();
        createUI();
    }
    
    private void createUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f0f23;");
        
        // Top bar
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        // Center content
        VBox centerContent = createCenterContent();
        root.setCenter(centerContent);
        
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    private HBox createTopBar() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(15, 30, 15, 30));
        bar.setStyle("-fx-background-color: #1a1a2e;");
        
        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 14px; ");
        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; "));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 14px; "));
        backBtn.setOnAction(e -> {
            new MainViewFX().show();
            stage.close();
        });
        
        Text title = new Text("Choose CV Theme");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setFill(Color.WHITE);
        
        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 16px; ");
        closeBtn.setOnAction(e -> {
            new MainViewFX().show();
            stage.close();
        });
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px;  -fx-background-radius: 3;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 16px; "));
        
        bar.getChildren().addAll(backBtn, spacer1, title, spacer2, closeBtn);
        return bar;
    }
    
    private VBox createCenterContent() {
        VBox content = new VBox(30);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));
        
        Text subtitle = new Text("Select a theme for your resume");
        subtitle.setFont(Font.font("Segoe UI", 18));
        subtitle.setFill(Color.web("#888"));
        
        // Theme cards in a grid
        GridPane themeGrid = new GridPane();
        themeGrid.setHgap(30);
        themeGrid.setVgap(30);
        themeGrid.setAlignment(Pos.CENTER);
        
        int col = 0;
        int row = 0;
        for (CVTheme theme : CVTheme.values()) {
            VBox themeCard = createThemeCard(theme);
            themeGrid.add(themeCard, col, row);
            col++;
            if (col >= 2) {
                col = 0;
                row++;
            }
        }
        
        // Customize button
        Button customizeBtn = new Button("🎨 Customize Theme");
        customizeBtn.setStyle(
            "-fx-background-color: #11998e; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 25; " +
                                    
                                                                                                                                                            "-fx-padding: 15 40;"
        );
        customizeBtn.setOnAction(e -> {
            ThemeManager.setSelectedTheme(selectedTheme);
            ThemeSettingsManager.updateTheme(selectedTheme);
            new ThemeCustomizationViewFX().show();
            stage.close();
        });
        
        // Apply button
        Button applyBtn = new Button("Apply Theme & Preview");
        applyBtn.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 25; " +
                                    
                                                                                                                                                            "-fx-padding: 15 40;"
        );
        applyBtn.setOnAction(e -> {
            ThemeManager.setSelectedTheme(selectedTheme);
            ThemeSettingsManager.updateTheme(selectedTheme);
            new ProfessionalCVViewFX().show();
            stage.close();
        });
        applyBtn.setOnMouseEntered(e -> applyBtn.setStyle(
            "-fx-background-color: #764ba2; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 25; " +
                        
                                                            "-fx-padding: 15 40;"
        ));
        applyBtn.setOnMouseExited(e -> applyBtn.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 25; " +
                                    
                                                                                                                                                            "-fx-padding: 15 40;"
        ));
        
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(customizeBtn, applyBtn);
        
        content.getChildren().addAll(subtitle, themeGrid, buttonBox);
        return content;
    }
    
    private VBox createThemeCard(CVTheme theme) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setMinWidth(200);
        card.setMinHeight(250);
        
        boolean isSelected = theme == selectedTheme;
        String bgColor = isSelected ? "#667eea" : "#1a1a2e";
        String borderColor = isSelected ? "#764ba2" : "#2a2a3e";
                        
        card.setStyle(
            "-fx-background-color: " + bgColor + "; " +
            "-fx-background-radius: 20; " +
            "-fx-border-color: " + borderColor + "; " +
            "-fx-border-radius: 20; " +
            "-fx-border-width: 2;");
        
        // Theme icon/color preview
        Rectangle colorPreview = new Rectangle(80, 80);
        colorPreview.setFill(getThemeColor(theme));
        colorPreview.setArcWidth(15);
        colorPreview.setArcHeight(15);
        
        // Theme name
        Text themeName = new Text(theme.getDisplayName());
        themeName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        themeName.setFill(Color.WHITE);
        
        // Theme description
        Text description = new Text(theme.getDescription());
        description.setFont(Font.font("Segoe UI", 12));
        description.setFill(Color.web("#bbb"));
        description.setWrappingWidth(180);
        
        // Selection indicator
        if (isSelected) {
            Text checkmark = new Text("✓ Selected");
            checkmark.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            checkmark.setFill(Color.WHITE);
            card.getChildren().addAll(colorPreview, themeName, description, checkmark);
        } else {
            card.getChildren().addAll(colorPreview, themeName, description);
        }
        
        // Click handler
        card.setOnMouseClicked(e -> {
            selectedTheme = theme;
            createUI(); // Refresh to show selection
        });
        
        // Hover effects
        card.setOnMouseEntered(e -> {
            if (!isSelected) {
                card.setStyle(
                    "-fx-background-color: #2a2a3e; " +
                    "-fx-background-radius: 20; " +
                    "-fx-border-color: #667eea; " +
                    "-fx-border-radius: 20; " +
                                    
                                                            "-fx-border-width: 2;");
            }
        });
        
        card.setOnMouseExited(e -> {
            if (!isSelected) {
                card.setStyle(
                    "-fx-background-color: #1a1a2e; " +
                    "-fx-background-radius: 20; " +
                    "-fx-border-color: #2a2a3e; " +
                    "-fx-border-radius: 20; " +
                                    
                                                            "-fx-border-width: 2;");
            }
        });
        
        return card;
    }
    
    private Color getThemeColor(CVTheme theme) {
        switch (theme) {
            case CLASSIC:
                return Color.web("#8B7355"); // Brown/tan
            case MODERN:
                return Color.web("#667eea"); // Purple/blue
            case PROFESSIONAL:
                return Color.web("#2c3e50"); // Dark blue
            case CREATIVE:
                return Color.web("#e74c3c"); // Red
            default:
                return Color.web("#667eea");
        }
    }
    
    public void show() {
        stage.show();
    }
}

