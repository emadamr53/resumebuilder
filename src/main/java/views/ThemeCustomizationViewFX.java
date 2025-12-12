package views;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import models.CVThemeSettings;

/**
 * Theme Customization View - Advanced customization for CV themes
 */
public class ThemeCustomizationViewFX {
    private Stage stage;
    private CVThemeSettings settings;
    
    // UI Controls
    private ComboBox<String> baseThemeCombo;
    private ComboBox<String> headerFontCombo;
    private ComboBox<String> bodyFontCombo;
    private Slider headerFontSizeSlider;
    private Slider sectionFontSizeSlider;
    private Slider bodyFontSizeSlider;
    private ColorPicker primaryColorPicker;
    private ColorPicker secondaryColorPicker;
    private ColorPicker tertiaryColorPicker;
    private ColorPicker headerTextColorPicker;
    private ColorPicker bodyTextColorPicker;
    private ColorPicker backgroundColorPicker;
    private ColorPicker dividerColorPicker;
    private CheckBox useIconsCheck;
    private CheckBox useBordersCheck;
    private ComboBox<String> borderStyleCombo;
    
    public ThemeCustomizationViewFX() {
        stage = new Stage();
        stage.setTitle("Customize CV Theme");
        stage.initStyle(StageStyle.UNDECORATED);
        settings = ThemeSettingsManager.getSettings();
        createUI();
    }
    
    private void createUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f0f23;");
        
        // Top bar
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        // Center content with scroll
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #0f0f23;");
        
        VBox content = createContent();
        scrollPane.setContent(content);
        root.setCenter(scrollPane);
        
        Scene scene = new Scene(root, 1000, 750);
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
            new ThemeSelectionViewFX().show();
            stage.close();
        });
        
        Text title = new Text("Customize CV Theme");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setFill(Color.WHITE);
        
        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        
        Button previewBtn = new Button("👁️ Preview");
        previewBtn.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 13px; " +
            "-fx-background-radius: 15; " +
            "-fx-padding: 8 20;"
        );
        previewBtn.setOnAction(e -> {
            saveSettings();
            new ProfessionalCVViewFX().show();
            stage.close();
        });
        
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 16px; ");
        closeBtn.setOnAction(e -> {
            new MainViewFX().show();
            stage.close();
        });
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px;  -fx-background-radius: 3;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 16px; "));
        
        HBox rightBox = new HBox(15, previewBtn, closeBtn);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        
        bar.getChildren().addAll(backBtn, spacer1, title, spacer2, rightBox);
        return bar;
    }
    
    private VBox createContent() {
        VBox content = new VBox(30);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(30));
        
        // Base Theme Selection
        VBox themeSection = createSection("Base Theme", createThemeSelector());
        
        // Font Settings
        VBox fontSection = createSection("Font Settings", createFontControls());
        
        // Color Settings
        VBox colorSection = createSection("Color Settings", createColorControls());
        
        // Layout Settings
        VBox layoutSection = createSection("Layout & Style", createLayoutControls());
        
        // Action Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button resetBtn = new Button("Reset to Default");
        resetBtn.setStyle(
            "-fx-background-color: #95a5a6; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-background-radius: 15; " +
            "-fx-padding: 10 25;"
        );
        resetBtn.setOnAction(e -> resetToDefaults());
        
        Button saveBtn = new Button("💾 Save & Apply");
        saveBtn.setStyle(
            "-fx-background-color: #27ae60; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 15; " +
            "-fx-padding: 10 30;"
        );
        saveBtn.setOnAction(e -> {
            saveSettings();
            new ProfessionalCVViewFX().show();
            stage.close();
        });
        
        buttonBox.getChildren().addAll(resetBtn, saveBtn);
        
        content.getChildren().addAll(themeSection, fontSection, colorSection, layoutSection, buttonBox);
        return content;
    }
    
    private VBox createSection(String title, GridPane controls) {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle(
            "-fx-background-color: #1a1a2e; " +
            "-fx-background-radius: 15; " +
            "-fx-border-color: #2a2a3e; " +
            "-fx-border-radius: 15; " +
            "-fx-border-width: 1;"
        );
        
        Text sectionTitle = new Text(title);
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        sectionTitle.setFill(Color.WHITE);
        
        section.getChildren().addAll(sectionTitle, controls);
        return section;
    }
    
    private GridPane createThemeSelector() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10, 0, 0, 0));
        
        Label themeLabel = new Label("Base Theme:");
        themeLabel.setStyle("-fx-text-fill: #bbb; -fx-font-size: 14px;");
        
        baseThemeCombo = new ComboBox<>();
        baseThemeCombo.getItems().addAll("Classic", "Modern", "Professional", "Creative");
        baseThemeCombo.setValue(settings.getBaseTheme().getDisplayName());
        baseThemeCombo.setStyle("-fx-background-color: #2a2a3e; -fx-text-fill: white;");
        baseThemeCombo.setOnAction(e -> {
            String selected = baseThemeCombo.getValue();
            CVTheme newTheme = CVTheme.valueOf(selected.toUpperCase());
            settings.setBaseTheme(newTheme);
            updateControlsFromSettings();
        });
        
        grid.add(themeLabel, 0, 0);
        grid.add(baseThemeCombo, 1, 0);
        
        return grid;
    }
    
    private GridPane createFontControls() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10, 0, 0, 0));
        
        String[] fonts = {"Arial", "Arial Black", "Calibri", "Cambria", "Comic Sans MS", 
                         "Courier New", "Georgia", "Helvetica", "Impact", "Lucida Console",
                         "Palatino", "Segoe UI", "Times New Roman", "Trebuchet MS", "Verdana"};
        
        // Header Font
        Label headerFontLabel = new Label("Header Font:");
        headerFontLabel.setStyle("-fx-text-fill: #bbb; -fx-font-size: 14px;");
        headerFontCombo = new ComboBox<>();
        headerFontCombo.getItems().addAll(fonts);
        headerFontCombo.setValue(settings.getHeaderFont());
        headerFontCombo.setStyle("-fx-background-color: #2a2a3e; -fx-text-fill: white;");
        headerFontCombo.setOnAction(e -> settings.setHeaderFont(headerFontCombo.getValue()));
        
        // Body Font
        Label bodyFontLabel = new Label("Body Font:");
        bodyFontLabel.setStyle("-fx-text-fill: #bbb; -fx-font-size: 14px;");
        bodyFontCombo = new ComboBox<>();
        bodyFontCombo.getItems().addAll(fonts);
        bodyFontCombo.setValue(settings.getBodyFont());
        bodyFontCombo.setStyle("-fx-background-color: #2a2a3e; -fx-text-fill: white;");
        bodyFontCombo.setOnAction(e -> settings.setBodyFont(bodyFontCombo.getValue()));
        
        // Header Font Size
        Label headerSizeLabel = new Label("Header Size:");
        headerSizeLabel.setStyle("-fx-text-fill: #bbb; -fx-font-size: 14px;");
        headerFontSizeSlider = createFontSizeSlider(settings.getHeaderFontSize(), 20, 50, 
            (obs, oldVal, newVal) -> settings.setHeaderFontSize(newVal.intValue()));
        
        // Section Font Size
        Label sectionSizeLabel = new Label("Section Size:");
        sectionSizeLabel.setStyle("-fx-text-fill: #bbb; -fx-font-size: 14px;");
        sectionFontSizeSlider = createFontSizeSlider(settings.getSectionFontSize(), 10, 20,
            (obs, oldVal, newVal) -> settings.setSectionFontSize(newVal.intValue()));
        
        // Body Font Size
        Label bodySizeLabel = new Label("Body Size:");
        bodySizeLabel.setStyle("-fx-text-fill: #bbb; -fx-font-size: 14px;");
        bodyFontSizeSlider = createFontSizeSlider(settings.getBodyFontSize(), 8, 16,
            (obs, oldVal, newVal) -> settings.setBodyFontSize(newVal.intValue()));
        
        grid.add(headerFontLabel, 0, 0);
        grid.add(headerFontCombo, 1, 0);
        grid.add(headerSizeLabel, 0, 1);
        grid.add(headerFontSizeSlider, 1, 1);
        grid.add(bodyFontLabel, 0, 2);
        grid.add(bodyFontCombo, 1, 2);
        grid.add(sectionSizeLabel, 0, 3);
        grid.add(sectionFontSizeSlider, 1, 3);
        grid.add(bodySizeLabel, 0, 4);
        grid.add(bodyFontSizeSlider, 1, 4);
        
        return grid;
    }
    
    private GridPane createColorControls() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10, 0, 0, 0));
        
        // Primary Color
        Label primaryLabel = new Label("Primary Color:");
        primaryLabel.setStyle("-fx-text-fill: #bbb; -fx-font-size: 14px;");
        primaryColorPicker = new ColorPicker(Color.web(settings.getPrimaryColor()));
        primaryColorPicker.setOnAction(e -> settings.setPrimaryColor(colorToHex(primaryColorPicker.getValue())));
        
        // Secondary Color
        Label secondaryLabel = new Label("Secondary Color:");
        secondaryLabel.setStyle("-fx-text-fill: #bbb; -fx-font-size: 14px;");
        secondaryColorPicker = new ColorPicker(Color.web(settings.getSecondaryColor()));
        secondaryColorPicker.setOnAction(e -> settings.setSecondaryColor(colorToHex(secondaryColorPicker.getValue())));
        
        // Tertiary Color
        Label tertiaryLabel = new Label("Tertiary Color:");
        tertiaryLabel.setStyle("-fx-text-fill: #bbb; -fx-font-size: 14px;");
        tertiaryColorPicker = new ColorPicker(Color.web(settings.getTertiaryColor()));
        tertiaryColorPicker.setOnAction(e -> settings.setTertiaryColor(colorToHex(tertiaryColorPicker.getValue())));
        
        // Header Text Color
        Label headerTextLabel = new Label("Header Text:");
        headerTextLabel.setStyle("-fx-text-fill: #bbb; -fx-font-size: 14px;");
        headerTextColorPicker = new ColorPicker(Color.web(settings.getHeaderTextColor()));
        headerTextColorPicker.setOnAction(e -> settings.setHeaderTextColor(colorToHex(headerTextColorPicker.getValue())));
        
        // Body Text Color
        Label bodyTextLabel = new Label("Body Text:");
        bodyTextLabel.setStyle("-fx-text-fill: #bbb; -fx-font-size: 14px;");
        bodyTextColorPicker = new ColorPicker(Color.web(settings.getBodyTextColor()));
        bodyTextColorPicker.setOnAction(e -> settings.setBodyTextColor(colorToHex(bodyTextColorPicker.getValue())));
        
        // Background Color
        Label bgLabel = new Label("Background:");
        bgLabel.setStyle("-fx-text-fill: #bbb; -fx-font-size: 14px;");
        backgroundColorPicker = new ColorPicker(Color.web(settings.getBackgroundColor()));
        backgroundColorPicker.setOnAction(e -> settings.setBackgroundColor(colorToHex(backgroundColorPicker.getValue())));
        
        // Divider Color
        Label dividerLabel = new Label("Divider Color:");
        dividerLabel.setStyle("-fx-text-fill: #bbb; -fx-font-size: 14px;");
        dividerColorPicker = new ColorPicker(Color.web(settings.getDividerColor()));
        dividerColorPicker.setOnAction(e -> settings.setDividerColor(colorToHex(dividerColorPicker.getValue())));
        
        grid.add(primaryLabel, 0, 0);
        grid.add(primaryColorPicker, 1, 0);
        grid.add(secondaryLabel, 0, 1);
        grid.add(secondaryColorPicker, 1, 1);
        grid.add(tertiaryLabel, 0, 2);
        grid.add(tertiaryColorPicker, 1, 2);
        grid.add(headerTextLabel, 0, 3);
        grid.add(headerTextColorPicker, 1, 3);
        grid.add(bodyTextLabel, 0, 4);
        grid.add(bodyTextColorPicker, 1, 4);
        grid.add(bgLabel, 0, 5);
        grid.add(backgroundColorPicker, 1, 5);
        grid.add(dividerLabel, 0, 6);
        grid.add(dividerColorPicker, 1, 6);
        
        return grid;
    }
    
    private GridPane createLayoutControls() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10, 0, 0, 0));
        
        // Use Icons
        Label iconsLabel = new Label("Use Icons:");
        iconsLabel.setStyle("-fx-text-fill: #bbb; -fx-font-size: 14px;");
        useIconsCheck = new CheckBox();
        useIconsCheck.setSelected(settings.isUseIcons());
        useIconsCheck.setOnAction(e -> settings.setUseIcons(useIconsCheck.isSelected()));
        useIconsCheck.setStyle("-fx-text-fill: white;");
        
        // Use Borders
        Label bordersLabel = new Label("Use Borders:");
        bordersLabel.setStyle("-fx-text-fill: #bbb; -fx-font-size: 14px;");
        useBordersCheck = new CheckBox();
        useBordersCheck.setSelected(settings.isUseBorders());
        useBordersCheck.setOnAction(e -> settings.setUseBorders(useBordersCheck.isSelected()));
        useBordersCheck.setStyle("-fx-text-fill: white;");
        
        // Border Style
        Label borderStyleLabel = new Label("Border Style:");
        borderStyleLabel.setStyle("-fx-text-fill: #bbb; -fx-font-size: 14px;");
        borderStyleCombo = new ComboBox<>();
        borderStyleCombo.getItems().addAll("Solid", "Dashed", "None");
        borderStyleCombo.setValue(settings.getBorderStyle().substring(0, 1).toUpperCase() + settings.getBorderStyle().substring(1));
        borderStyleCombo.setStyle("-fx-background-color: #2a2a3e; -fx-text-fill: white;");
        borderStyleCombo.setOnAction(e -> settings.setBorderStyle(borderStyleCombo.getValue().toLowerCase()));
        
        grid.add(iconsLabel, 0, 0);
        grid.add(useIconsCheck, 1, 0);
        grid.add(bordersLabel, 0, 1);
        grid.add(useBordersCheck, 1, 1);
        grid.add(borderStyleLabel, 0, 2);
        grid.add(borderStyleCombo, 1, 2);
        
        return grid;
    }
    
    private Slider createFontSizeSlider(int value, int min, int max, javafx.beans.value.ChangeListener<? super Number> listener) {
        Slider slider = new Slider(min, max, value);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(2);
        slider.setMinorTickCount(1);
        slider.setSnapToTicks(true);
        slider.valueProperty().addListener(listener);
        return slider;
    }
    
    private String colorToHex(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }
    
    private void updateControlsFromSettings() {
        headerFontCombo.setValue(settings.getHeaderFont());
        bodyFontCombo.setValue(settings.getBodyFont());
        headerFontSizeSlider.setValue(settings.getHeaderFontSize());
        sectionFontSizeSlider.setValue(settings.getSectionFontSize());
        bodyFontSizeSlider.setValue(settings.getBodyFontSize());
        primaryColorPicker.setValue(Color.web(settings.getPrimaryColor()));
        secondaryColorPicker.setValue(Color.web(settings.getSecondaryColor()));
        tertiaryColorPicker.setValue(Color.web(settings.getTertiaryColor()));
        headerTextColorPicker.setValue(Color.web(settings.getHeaderTextColor()));
        bodyTextColorPicker.setValue(Color.web(settings.getBodyTextColor()));
        backgroundColorPicker.setValue(Color.web(settings.getBackgroundColor()));
        dividerColorPicker.setValue(Color.web(settings.getDividerColor()));
        useIconsCheck.setSelected(settings.isUseIcons());
        useBordersCheck.setSelected(settings.isUseBorders());
        borderStyleCombo.setValue(settings.getBorderStyle().substring(0, 1).toUpperCase() + settings.getBorderStyle().substring(1));
    }
    
    private void saveSettings() {
        ThemeSettingsManager.saveSettings(settings);
        ThemeManager.setSelectedTheme(settings.getBaseTheme());
    }
    
    private void resetToDefaults() {
        CVTheme currentTheme = settings.getBaseTheme();
        settings = new CVThemeSettings(currentTheme);
        updateControlsFromSettings();
    }
    
    public void show() {
        stage.show();
    }
}

