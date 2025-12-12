package views;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
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
import managers.AutoSaveManager;
import models.Resume;
import utils.ValidationUtils;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Modern Resume Form View - Clean Card Design
 */
public class ResumeFormViewFX {
    private Stage stage;
    private boolean isEditMode = false;
    
    private TextField txtName, txtEmail, txtPhone, txtAddress;
    private TextField txtInstitution, txtDegree, txtYear;
    private TextField txtJobTitle, txtCompany, txtDuration;
    private TextArea txtDescription, txtSkills;
    
    // Debounce timer for auto-save on field changes
    private Timer debounceTimer;
    private TimerTask debounceTask;
    
    public ResumeFormViewFX() {
        stage = new Stage();
        stage.setTitle("Resume Builder - Create Resume");
        stage.initStyle(StageStyle.UNDECORATED);
        createUI();
    }
    
    public ResumeFormViewFX(Resume resume) {
        this();
        isEditMode = true;
        stage.setTitle("Resume Builder - Edit Resume");
        loadResumeData(resume);
    }
    
    private void createUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f0f23;");
        
        // Top bar
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        // Main content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #0f0f23; -fx-background: #0f0f23;");
        
        VBox content = createContent();
        scrollPane.setContent(content);
        
        root.setCenter(scrollPane);
        
        Scene scene = new Scene(root, 1100, 800);
        stage.setScene(scene);
        stage.centerOnScreen();
        
        // Stop auto-save when window is closed
        stage.setOnCloseRequest(e -> {
            AutoSaveManager.stopAutoSave();
            if (debounceTimer != null) {
                debounceTimer.cancel();
                debounceTimer = null;
            }
        });
        
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
        
        // Back button
        Button backBtn = new Button("← Back");
        backBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #888; " +
            "-fx-font-size: 14px;"
        );
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px;"
        ));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #888; " +
            "-fx-font-size: 14px;"
        ));
        backBtn.setOnAction(e -> {
            AutoSaveManager.stopAutoSave(); // Stop auto-save when leaving
            if (debounceTimer != null) {
                debounceTimer.cancel();
                debounceTimer = null;
            }
            new MainViewFX().show();
            stage.close();
        });
        
        // Title
        Text title = new Text(isEditMode ? "Edit Resume" : "Create Resume");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setFill(Color.WHITE);
        
        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        
        // Close button
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 16px;");
        closeBtn.setOnAction(e -> {
            AutoSaveManager.stopAutoSave(); // Stop auto-save when closing
            if (debounceTimer != null) {
                debounceTimer.cancel();
                debounceTimer = null;
            }
            new MainViewFX().show();
            stage.close();
        });
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 3;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 16px;"));
        
        bar.getChildren().addAll(backBtn, spacer1, title, spacer2, closeBtn);
        
        return bar;
    }
    
    private VBox createContent() {
        VBox content = new VBox(30);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(30, 50, 50, 50));
        
        // Personal Information Section
        VBox personalSection = createSection("Personal Information", "#667eea", "#764ba2");
        GridPane personalGrid = new GridPane();
        personalGrid.setHgap(20);
        personalGrid.setVgap(15);
        
        txtName = createTextField("Full Name");
        txtEmail = createTextField("Email Address");
        txtPhone = createTextField("Phone Number");
        txtAddress = createTextField("Address");
        
        personalGrid.add(createFieldBox("Full Name", txtName), 0, 0);
        personalGrid.add(createFieldBox("Email", txtEmail), 1, 0);
        personalGrid.add(createFieldBox("Phone", txtPhone), 0, 1);
        personalGrid.add(createFieldBox("Address", txtAddress), 1, 1);
        
        personalSection.getChildren().add(personalGrid);
        
        // Education Section
        VBox educationSection = createSection("Education", "#11998e", "#38ef7d");
        GridPane educationGrid = new GridPane();
        educationGrid.setHgap(20);
        educationGrid.setVgap(15);
        
        txtInstitution = createTextField("Institution Name");
        txtDegree = createTextField("Degree / Certificate");
        txtYear = createTextField("Graduation Year");
        
        educationGrid.add(createFieldBox("Institution", txtInstitution), 0, 0);
        educationGrid.add(createFieldBox("Degree", txtDegree), 1, 0);
        educationGrid.add(createFieldBox("Year", txtYear), 0, 1);
        
        educationSection.getChildren().add(educationGrid);
        
        // Experience Section
        VBox experienceSection = createSection("Professional Experience", "#f093fb", "#f5576c");
        GridPane experienceGrid = new GridPane();
        experienceGrid.setHgap(20);
        experienceGrid.setVgap(15);
        
        txtJobTitle = createTextField("Job Title");
        txtCompany = createTextField("Company Name");
        txtDuration = createTextField("Duration");
        txtDescription = createTextArea("Describe your responsibilities...");
        
        experienceGrid.add(createFieldBox("Job Title", txtJobTitle), 0, 0);
        experienceGrid.add(createFieldBox("Company", txtCompany), 1, 0);
        experienceGrid.add(createFieldBox("Duration", txtDuration), 0, 1);
        
        VBox descBox = createFieldBox("Description", txtDescription);
        experienceGrid.add(descBox, 0, 2, 2, 1);
        
        experienceSection.getChildren().add(experienceGrid);
        
        // Skills Section
        VBox skillsSection = createSection("Skills & Competencies", "#4facfe", "#00f2fe");
        txtSkills = createTextArea("List your skills (e.g., Java, Python, Communication, Leadership)");
        txtSkills.setPrefRowCount(3);
        
        VBox skillsBox = createFieldBox("Skills", txtSkills);
        skillsSection.getChildren().add(skillsBox);
        
        // Save Button
        Button saveBtn = new Button("💾  Save Resume");
        saveBtn.setPrefWidth(250);
        saveBtn.setPrefHeight(55);
        saveBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        saveBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 30;"
        );
        saveBtn.setEffect(new DropShadow(15, Color.rgb(102, 126, 234, 0.5)));
        saveBtn.setOnAction(e -> handleSave());
        
        saveBtn.setOnMouseEntered(e -> saveBtn.setEffect(new DropShadow(25, Color.rgb(102, 126, 234, 0.7))));
        saveBtn.setOnMouseExited(e -> saveBtn.setEffect(new DropShadow(15, Color.rgb(102, 126, 234, 0.5))));
        
        HBox buttonBox = new HBox(saveBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        content.getChildren().addAll(personalSection, educationSection, experienceSection, skillsSection, buttonBox);
        
        return content;
    }
    
    private VBox createSection(String title, String color1, String color2) {
        VBox section = new VBox(20);
        section.setPadding(new Insets(25));
        section.setStyle(
            "-fx-background-color: #1a1a2e; " +
            "-fx-background-radius: 15;"
        );
        section.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.3)));
        
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Circle dot = new Circle(8);
        Stop[] stops = new Stop[] {
            new Stop(0, Color.web(color1)),
            new Stop(1, Color.web(color2))
        };
        dot.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops));
        
        Text titleText = new Text(title);
        titleText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleText.setFill(Color.WHITE);
        
        headerBox.getChildren().addAll(dot, titleText);
        section.getChildren().add(headerBox);
        
        return section;
    }
    
    private VBox createFieldBox(String label, Control field) {
        VBox box = new VBox(8);
        
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lbl.setTextFill(Color.web("#888"));
        
        box.getChildren().addAll(lbl, field);
        return box;
    }
    
    private TextField createTextField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setPrefWidth(400);
        field.setPrefHeight(45);
        field.setStyle(
            "-fx-background-color: #0f3460; " +
            "-fx-text-fill: white; " +
            "-fx-prompt-text-fill: #555; " +
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
                    "-fx-prompt-text-fill: #555; " +
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
                    "-fx-prompt-text-fill: #555; " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-color: #2a4a7a; " +
                    "-fx-border-radius: 10; " +
                    "-fx-padding: 10 15;"
                );
            }
        });
        
        return field;
    }
    
    private TextArea createTextArea(String placeholder) {
        TextArea area = new TextArea();
        area.setPromptText(placeholder);
        area.setPrefRowCount(4);
        area.setWrapText(true);
        area.setStyle(
            "-fx-control-inner-background: #0f3460; " +
            "-fx-text-fill: white; " +
            "-fx-prompt-text-fill: #555; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #2a4a7a; " +
            "-fx-border-radius: 10;"
        );
        
        return area;
    }
    
    private void loadResumeData(Resume resume) {
        if (resume == null) return;
        
        if (resume.getName() != null) txtName.setText(resume.getName());
        if (resume.getEmail() != null) txtEmail.setText(resume.getEmail());
        if (resume.getPhone() != null) txtPhone.setText(resume.getPhone());
        if (resume.getAddress() != null) txtAddress.setText(resume.getAddress());
        
        if (resume.getInstitution() != null) txtInstitution.setText(resume.getInstitution());
        if (resume.getDegree() != null) txtDegree.setText(resume.getDegree());
        if (resume.getYear() != null) txtYear.setText(resume.getYear());
        
        if (resume.getJobTitle() != null) txtJobTitle.setText(resume.getJobTitle());
        if (resume.getCompany() != null) txtCompany.setText(resume.getCompany());
        if (resume.getDuration() != null) txtDuration.setText(resume.getDuration());
        if (resume.getDescription() != null) txtDescription.setText(resume.getDescription());
        
        if (resume.getSkills() != null) txtSkills.setText(resume.getSkills());
    }
    
    private void handleSave() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please enter your name!");
            return;
        }
        
        if (!email.isEmpty() && !ValidationUtils.isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Please enter a valid email!");
            return;
        }
        
        Resume resume = new Resume(
            name, email,
            txtPhone.getText().trim(),
            txtAddress.getText().trim(),
            txtInstitution.getText().trim(),
            txtDegree.getText().trim(),
            txtYear.getText().trim(),
            txtJobTitle.getText().trim(),
            txtCompany.getText().trim(),
            txtDuration.getText().trim(),
            txtDescription.getText().trim(),
            txtSkills.getText().trim()
        );
        
        ResumeManager.saveResume(resume);
        
        // Trigger immediate auto-save after manual save
        AutoSaveManager.triggerAutoSave(resume);
        
        showAlert(Alert.AlertType.INFORMATION, "Resume saved successfully!");
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
        // Start auto-save when form is shown
        startAutoSave();
    }
    
    /**
     * Start auto-saving the resume every 10 seconds and on field changes
     */
    private void startAutoSave() {
        // Start periodic auto-save (every 10 seconds)
        AutoSaveManager.startAutoSave(() -> {
            // Build Resume object from current form fields
            return buildResumeFromFields();
        });
        
        // Add change listeners to all fields for immediate auto-save
        setupAutoSaveListeners();
    }
    
    /**
     * Setup change listeners on all text fields to trigger auto-save immediately
     * Uses debounce to wait 2 seconds after user stops typing before saving
     */
    private void setupAutoSaveListeners() {
        debounceTimer = new Timer(true); // Daemon thread
        
        Runnable triggerSave = () -> {
            Platform.runLater(() -> {
                Resume resume = buildResumeFromFields();
                if (resume != null) {
                    // Trigger immediate save
                    AutoSaveManager.triggerAutoSave(resume);
                    System.out.println("💾 Auto-saved on field change!");
                }
            });
        };
        
        // Helper method to schedule debounced save
        Runnable scheduleSave = () -> {
            if (debounceTask != null) {
                debounceTask.cancel();
            }
            debounceTask = new TimerTask() {
                @Override
                public void run() {
                    triggerSave.run();
                }
            };
            debounceTimer.schedule(debounceTask, 2000); // Wait 2 seconds after user stops typing
        };
        
        // Add listeners to all text fields
        txtName.textProperty().addListener((obs, oldVal, newVal) -> scheduleSave.run());
        txtEmail.textProperty().addListener((obs, oldVal, newVal) -> scheduleSave.run());
        txtPhone.textProperty().addListener((obs, oldVal, newVal) -> scheduleSave.run());
        txtAddress.textProperty().addListener((obs, oldVal, newVal) -> scheduleSave.run());
        txtInstitution.textProperty().addListener((obs, oldVal, newVal) -> scheduleSave.run());
        txtDegree.textProperty().addListener((obs, oldVal, newVal) -> scheduleSave.run());
        txtYear.textProperty().addListener((obs, oldVal, newVal) -> scheduleSave.run());
        txtJobTitle.textProperty().addListener((obs, oldVal, newVal) -> scheduleSave.run());
        txtCompany.textProperty().addListener((obs, oldVal, newVal) -> scheduleSave.run());
        txtDuration.textProperty().addListener((obs, oldVal, newVal) -> scheduleSave.run());
        txtDescription.textProperty().addListener((obs, oldVal, newVal) -> scheduleSave.run());
        txtSkills.textProperty().addListener((obs, oldVal, newVal) -> scheduleSave.run());
    }
    
    /**
     * Build a Resume object from the current form field values
     * This is used for auto-save (no validation)
     * Now saves even with partial data - as long as at least one field has content
     */
    private Resume buildResumeFromFields() {
        // Check if at least one field has content
        boolean hasContent = !txtName.getText().trim().isEmpty() ||
                           !txtEmail.getText().trim().isEmpty() ||
                           !txtPhone.getText().trim().isEmpty() ||
                           !txtAddress.getText().trim().isEmpty() ||
                           !txtInstitution.getText().trim().isEmpty() ||
                           !txtDegree.getText().trim().isEmpty() ||
                           !txtYear.getText().trim().isEmpty() ||
                           !txtJobTitle.getText().trim().isEmpty() ||
                           !txtCompany.getText().trim().isEmpty() ||
                           !txtDuration.getText().trim().isEmpty() ||
                           !txtDescription.getText().trim().isEmpty() ||
                           !txtSkills.getText().trim().isEmpty();
        
        if (!hasContent) {
            return null; // Don't auto-save completely empty forms
        }
        
        // Use "Draft" as default name if name is empty
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            name = "Draft Resume";
        }
        
        return new Resume(
            name,
            txtEmail.getText().trim(),
            txtPhone.getText().trim(),
            txtAddress.getText().trim(),
            txtInstitution.getText().trim(),
            txtDegree.getText().trim(),
            txtYear.getText().trim(),
            txtJobTitle.getText().trim(),
            txtCompany.getText().trim(),
            txtDuration.getText().trim(),
            txtDescription.getText().trim(),
            txtSkills.getText().trim()
        );
    }
}
