package views;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.print.Printer;
import javafx.print.Paper;
import javafx.print.PageOrientation;
import javafx.print.Printer.MarginType;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import managers.ResumeManager;
import managers.ThemeManager;
import managers.ThemeSettingsManager;
import models.Resume;
import models.CVTheme;
import models.CVThemeSettings;

/**
 * Professional CV Preview - Paper Style Design
 */
public class ProfessionalCVViewFX {
    private Stage stage;
    private Resume resume;
    private CVTheme currentTheme;
    private CVThemeSettings themeSettings;
    private VBox cvContent; // Store reference to CV content for printing
    
    public ProfessionalCVViewFX() {
        stage = new Stage();
        stage.setTitle("Resume Builder - Preview");
        stage.initStyle(StageStyle.UNDECORATED);
        resume = ResumeManager.getLastResume();
        currentTheme = ThemeManager.getSelectedTheme();
        // Always get fresh settings to ensure customizations are applied
        themeSettings = ThemeSettingsManager.getSettings();
        if (themeSettings == null) {
            themeSettings = new CVThemeSettings(currentTheme);
        }
        System.out.println("Using theme: " + currentTheme);
        System.out.println("Header font: " + themeSettings.getHeaderFont());
        System.out.println("Primary color: " + themeSettings.getPrimaryColor());
        createUI();
    }
    
    private void createUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f0f23;");
        
        // Top bar
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        // CV Preview
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #0f0f23; -fx-background: #0f0f23;");
        
        cvContent = createPreviewContent();
        scrollPane.setContent(cvContent);
        
        root.setCenter(scrollPane);
        
        Scene scene = new Scene(root, 900, 800);
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
        
        Text title = new Text("CV Preview");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setFill(Color.WHITE);
        
        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        
        Button themeBtn = new Button("🎨 Change Theme");
        themeBtn.setStyle(
            "-fx-background-color: #11998e; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 13px; " +
            "-fx-background-radius: 15; " +
            "-fx-padding: 8 20;"
        );
        themeBtn.setOnAction(e -> {
            new ThemeCustomizationViewFX().show();
            stage.close();
        });
        
        Button printBtn = new Button("🖨️ Print");
        printBtn.setStyle(
            "-fx-background-color: #667eea; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 13px; " +
            "-fx-background-radius: 15; " +
            "-fx-padding: 8 20;"
        );
        printBtn.setOnAction(e -> handlePrint());
        
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 16px; ");
        closeBtn.setOnAction(e -> {
            new MainViewFX().show();
            stage.close();
        });
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px;  -fx-background-radius: 3;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 16px; "));
        
        HBox rightBox = new HBox(15, themeBtn, printBtn, closeBtn);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        
        bar.getChildren().addAll(backBtn, spacer1, title, spacer2, rightBox);
        
        return bar;
    }
    
    private VBox createPreviewContent() {
        // Always use professional theme with custom settings applied
        // The custom settings override the base theme
        return createProfessionalTheme();
    }
    
    private VBox createProfessionalTheme() {
        VBox wrapper = new VBox();
        wrapper.setAlignment(Pos.TOP_CENTER);
        wrapper.setPadding(new Insets(40));
        
        // Paper (using custom settings)
        VBox paper = new VBox(0);
        paper.setMaxWidth(themeSettings.getPaperWidth());
        paper.setMinHeight(900);
        // Allow paper to grow to fit all content
        paper.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        paper.setStyle(
            "-fx-background-color: " + themeSettings.getBackgroundColor() + "; " +
            
                                                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 5);"
        );
        paper.setPadding(new Insets(themeSettings.getPadding(), themeSettings.getPadding() + 10, themeSettings.getPadding(), themeSettings.getPadding() + 10));
        
        if (resume == null) {
            Text noResume = new Text("No resume found. Please create one first.");
            noResume.setFont(Font.font(themeSettings.getBodyFont(), 16));
            noResume.setFill(Color.GRAY);
            paper.getChildren().add(noResume);
            paper.setAlignment(Pos.CENTER);
        } else {
            // Header - Name (using custom settings)
            Text nameText = new Text(resume.getName() != null ? resume.getName().toUpperCase() : "YOUR NAME");
            nameText.setFont(Font.font(themeSettings.getHeaderFont(), FontWeight.BOLD, themeSettings.getHeaderFontSize()));
            nameText.setFill(Color.web(themeSettings.getHeaderTextColor()));
            
            // Contact info (using custom settings)
            HBox contactBox = new HBox(20);
            contactBox.setAlignment(Pos.CENTER);
            contactBox.setPadding(new Insets(10, 0, 20, 0));
            
            if (resume.getEmail() != null && !resume.getEmail().isEmpty()) {
                String emailPrefix = themeSettings.isUseIcons() ? "✉ " : "";
                        
                Text emailText = new Text(emailPrefix + resume.getEmail());
                emailText.setFont(Font.font(themeSettings.getBodyFont(), themeSettings.getBodyFontSize()));
                emailText.setFill(Color.web(themeSettings.getBodyTextColor()));
                contactBox.getChildren().add(emailText);
            }
            if (resume.getPhone() != null && !resume.getPhone().isEmpty()) {
                String phonePrefix = themeSettings.isUseIcons() ? "📱 " : "";
                                    
                Text phoneText = new Text(phonePrefix + resume.getPhone());
                phoneText.setFont(Font.font(themeSettings.getBodyFont(), themeSettings.getBodyFontSize()));
                phoneText.setFill(Color.web(themeSettings.getBodyTextColor()));
                contactBox.getChildren().add(phoneText);
            }
            if (resume.getAddress() != null && !resume.getAddress().isEmpty()) {
                String addressPrefix = themeSettings.isUseIcons() ? "📍 " : "";
                                    
                Text addressText = new Text(addressPrefix + resume.getAddress());
                addressText.setFont(Font.font(themeSettings.getBodyFont(), themeSettings.getBodyFontSize()));
                addressText.setFill(Color.web(themeSettings.getBodyTextColor()));
                contactBox.getChildren().add(addressText);
            }
            
            // Divider (using custom color)
            double dividerWidth = themeSettings.getPaperWidth() - themeSettings.getPadding() * 2 - 20;
            Line divider = new Line(0, 0, dividerWidth, 0);
            divider.setStroke(Color.web(themeSettings.getDividerColor()));
            divider.setStrokeWidth(3);
            
            VBox headerBox = new VBox(5, nameText, contactBox, divider);
            headerBox.setAlignment(Pos.CENTER);
            
            paper.getChildren().add(headerBox);
            
            // Education Section (using custom colors)
            if (hasEducation()) {
                VBox eduSection = createCVSection("EDUCATION", themeSettings.getPrimaryColor());
                
                VBox eduContent = new VBox(5);
                eduContent.setPadding(new Insets(10, 0, 0, 20));
                
                Text degreeText = new Text(resume.getDegree());
                degreeText.setFont(Font.font(themeSettings.getBodyFont(), FontWeight.BOLD, themeSettings.getSectionFontSize()));
                degreeText.setFill(Color.web(themeSettings.getHeaderTextColor()));
                
                Text institutionText = new Text(resume.getInstitution());
                institutionText.setFont(Font.font(themeSettings.getBodyFont(), themeSettings.getBodyFontSize()));
                institutionText.setFill(Color.web(themeSettings.getBodyTextColor()));
                
                Text yearText = new Text(resume.getYear());
                yearText.setFont(Font.font(themeSettings.getBodyFont(), themeSettings.getBodyFontSize() - 1));
                yearText.setFill(Color.web(themeSettings.getBodyTextColor()));
                
                eduContent.getChildren().addAll(degreeText, institutionText, yearText);
                eduSection.getChildren().add(eduContent);
                paper.getChildren().add(eduSection);
            }
            
            // Experience Section (using custom colors)
            if (hasExperience()) {
                VBox expSection = createCVSection("PROFESSIONAL EXPERIENCE", themeSettings.getSecondaryColor());
                
                VBox expContent = new VBox(5);
                expContent.setPadding(new Insets(10, 0, 0, 20));
                
                HBox titleLine = new HBox(10);
                Text jobText = new Text(resume.getJobTitle());
                jobText.setFont(Font.font(themeSettings.getBodyFont(), FontWeight.BOLD, themeSettings.getSectionFontSize()));
                jobText.setFill(Color.web(themeSettings.getHeaderTextColor()));
                
                Text durationText = new Text("(" + resume.getDuration() + ")");
                durationText.setFont(Font.font(themeSettings.getBodyFont(), themeSettings.getBodyFontSize() - 1));
                durationText.setFill(Color.web(themeSettings.getBodyTextColor()));
                
                titleLine.getChildren().addAll(jobText, durationText);
                
                Text companyText = new Text(resume.getCompany());
                companyText.setFont(Font.font(themeSettings.getBodyFont(), themeSettings.getBodyFontSize()));
                companyText.setFill(Color.web(themeSettings.getBodyTextColor()));
                
                Text descText = new Text(resume.getDescription());
                descText.setFont(Font.font(themeSettings.getBodyFont(), themeSettings.getBodyFontSize() - 1));
                descText.setFill(Color.web(themeSettings.getBodyTextColor()));
                descText.setWrappingWidth((int)(themeSettings.getPaperWidth() - themeSettings.getPadding() * 2 - 40));
                
                expContent.getChildren().addAll(titleLine, companyText, descText);
                expSection.getChildren().add(expContent);
                paper.getChildren().add(expSection);
            }
            
            // Skills Section (using custom colors)
            if (resume.getSkills() != null && !resume.getSkills().isEmpty()) {
                VBox skillsSection = createCVSection("SKILLS", themeSettings.getTertiaryColor());
                
                FlowPane skillsFlow = new FlowPane(10, 10);
                skillsFlow.setPadding(new Insets(10, 0, 0, 20));
                
                String[] skills = resume.getSkills().split(",");
                for (String skill : skills) {
                    Label skillLabel = new Label(skill.trim());
                    skillLabel.setFont(Font.font(themeSettings.getBodyFont(), themeSettings.getBodyFontSize() - 1));
                    skillLabel.setStyle(
                        "-fx-background-color: #ecf0f1; " +
                        "-fx-text-fill: " + themeSettings.getHeaderTextColor() + "; " +
                        "-fx-padding: 5 12; " +
                        
                                                            "-fx-background-radius: 15;"
                    );
                    skillsFlow.getChildren().add(skillLabel);
                }
                
                skillsSection.getChildren().add(skillsFlow);
                paper.getChildren().add(skillsSection);
            }
        }
        
            wrapper.getChildren().add(paper);
        return wrapper;
    }
    
    private VBox createClassicTheme() {
        VBox wrapper = new VBox();
        wrapper.setAlignment(Pos.TOP_CENTER);
        wrapper.setPadding(new Insets(40));
        
        VBox paper = new VBox(0);
        paper.setMaxWidth(650);
        paper.setMinHeight(900);
        paper.setStyle(
            "-fx-background-color: #faf8f3; " + // Cream/beige background
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 5);"
        );
        paper.setPadding(new Insets(50, 60, 50, 60));
        
        if (resume == null) {
            Text noResume = new Text("No resume found. Please create one first.");
            noResume.setFont(Font.font("Times New Roman", 16));
            noResume.setFill(Color.GRAY);
            paper.getChildren().add(noResume);
            paper.setAlignment(Pos.CENTER);
        } else {
            // Classic header with serif font
            Text nameText = new Text(resume.getName() != null ? resume.getName().toUpperCase() : "YOUR NAME");
            nameText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 28));
            nameText.setFill(Color.web("#5d4e37")); // Brown
            
            HBox contactBox = new HBox(15);
            contactBox.setAlignment(Pos.CENTER);
            contactBox.setPadding(new Insets(8, 0, 15, 0));
            
            if (resume.getEmail() != null && !resume.getEmail().isEmpty()) {
                Text emailText = new Text(resume.getEmail());
                emailText.setFont(Font.font("Times New Roman", 10));
                emailText.setFill(Color.web("#6b5d47"));
                contactBox.getChildren().add(emailText);
            }
            if (resume.getPhone() != null && !resume.getPhone().isEmpty()) {
                Text phoneText = new Text(" | " + resume.getPhone());
                phoneText.setFont(Font.font("Times New Roman", 10));
                phoneText.setFill(Color.web("#6b5d47"));
                contactBox.getChildren().add(phoneText);
            }
            
            Line divider = new Line(0, 0, 530, 0);
            divider.setStroke(Color.web("#8B7355")); // Brown
            divider.setStrokeWidth(2);
            
            VBox headerBox = new VBox(5, nameText, contactBox, divider);
            headerBox.setAlignment(Pos.CENTER);
            paper.getChildren().add(headerBox);
            
            // Add sections with classic styling
            addClassicSection(paper, "EDUCATION", resume.getDegree(), resume.getInstitution(), resume.getYear());
            addClassicSection(paper, "EXPERIENCE", resume.getJobTitle() + " (" + resume.getDuration() + ")", resume.getCompany(), resume.getDescription());
            if (resume.getSkills() != null && !resume.getSkills().isEmpty()) {
                addClassicSkillsSection(paper, resume.getSkills());
            }
        }
        
        wrapper.getChildren().add(paper);
        return wrapper;
    }
    
    private VBox createModernTheme() {
        VBox wrapper = new VBox();
        wrapper.setAlignment(Pos.TOP_CENTER);
        wrapper.setPadding(new Insets(40));
        
        VBox paper = new VBox(0);
        paper.setMaxWidth(650);
        paper.setMinHeight(900);
        paper.setStyle(
            "-fx-background-color: white; " +
            
                                                            "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.2), 30, 0, 0, 8);"
        );
        paper.setPadding(new Insets(40, 50, 40, 50));
        
        if (resume == null) {
            Text noResume = new Text("No resume found. Please create one first.");
            noResume.setFont(Font.font("Segoe UI", 16));
            noResume.setFill(Color.GRAY);
            paper.getChildren().add(noResume);
            paper.setAlignment(Pos.CENTER);
        } else {
            // Modern header with gradient effect
            Text nameText = new Text(resume.getName() != null ? resume.getName() : "YOUR NAME");
            nameText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
            nameText.setFill(Color.web("#667eea")); // Purple
            
            VBox contactBox = new VBox(5);
            contactBox.setAlignment(Pos.CENTER_LEFT);
            contactBox.setPadding(new Insets(10, 0, 20, 0));
            
            if (resume.getEmail() != null && !resume.getEmail().isEmpty()) {
                Text emailText = new Text("✉ " + resume.getEmail());
                emailText.setFont(Font.font("Segoe UI", 12));
                emailText.setFill(Color.web("#666"));
                contactBox.getChildren().add(emailText);
            }
            if (resume.getPhone() != null && !resume.getPhone().isEmpty()) {
                Text phoneText = new Text("📱 " + resume.getPhone());
                phoneText.setFont(Font.font("Segoe UI", 12));
                phoneText.setFill(Color.web("#666"));
                contactBox.getChildren().add(phoneText);
            }
            
            VBox headerBox = new VBox(5, nameText, contactBox);
            paper.getChildren().add(headerBox);
            
            // Modern sections with rounded boxes
            addModernSection(paper, "EDUCATION", resume.getDegree(), resume.getInstitution(), resume.getYear(), "#667eea");
            addModernSection(paper, "EXPERIENCE", resume.getJobTitle() + " (" + resume.getDuration() + ")", resume.getCompany(), resume.getDescription(), "#764ba2");
            if (resume.getSkills() != null && !resume.getSkills().isEmpty()) {
                addModernSkillsSection(paper, resume.getSkills());
            }
        }
        
        wrapper.getChildren().add(paper);
        return wrapper;
    }
    
    private VBox createCreativeTheme() {
        VBox wrapper = new VBox();
        wrapper.setAlignment(Pos.TOP_CENTER);
        wrapper.setPadding(new Insets(40));
        
        VBox paper = new VBox(0);
        paper.setMaxWidth(650);
        paper.setMinHeight(900);
        paper.setStyle(
            "-fx-background-color: #fff5f5; " + // Light pink background
            "-fx-effect: dropshadow(gaussian, rgba(231,76,60,0.3), 25, 0, 0, 6);"
        );
        paper.setPadding(new Insets(45, 55, 45, 55));
        
        if (resume == null) {
            Text noResume = new Text("No resume found. Please create one first.");
            noResume.setFont(Font.font("Segoe UI", 16));
            noResume.setFill(Color.GRAY);
            paper.getChildren().add(noResume);
            paper.setAlignment(Pos.CENTER);
        } else {
            // Creative header with bold colors
            Text nameText = new Text(resume.getName() != null ? resume.getName().toUpperCase() : "YOUR NAME");
            nameText.setFont(Font.font("Arial Black", FontWeight.BOLD, 30));
            nameText.setFill(Color.web("#e74c3c")); // Red
            
            HBox contactBox = new HBox(15);
            contactBox.setAlignment(Pos.CENTER);
            contactBox.setPadding(new Insets(12, 0, 20, 0));
            
            if (resume.getEmail() != null && !resume.getEmail().isEmpty()) {
                Text emailText = new Text("✉ " + resume.getEmail());
                emailText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
                emailText.setFill(Color.web("#c0392b"));
                contactBox.getChildren().add(emailText);
            }
            if (resume.getPhone() != null && !resume.getPhone().isEmpty()) {
                Text phoneText = new Text("📱 " + resume.getPhone());
                phoneText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
                phoneText.setFill(Color.web("#c0392b"));
                contactBox.getChildren().add(phoneText);
            }
            
            VBox headerBox = new VBox(5, nameText, contactBox);
            headerBox.setAlignment(Pos.CENTER);
            paper.getChildren().add(headerBox);
            
            // Creative sections with vibrant colors
            addCreativeSection(paper, "EDUCATION", resume.getDegree(), resume.getInstitution(), resume.getYear(), "#e74c3c");
            addCreativeSection(paper, "EXPERIENCE", resume.getJobTitle() + " (" + resume.getDuration() + ")", resume.getCompany(), resume.getDescription(), "#f39c12");
            if (resume.getSkills() != null && !resume.getSkills().isEmpty()) {
                addCreativeSkillsSection(paper, resume.getSkills());
            }
        }
        
        wrapper.getChildren().add(paper);
        return wrapper;
    }
    
    // Helper methods for different themes
    private void addClassicSection(VBox paper, String title, String line1, String line2, String line3) {
        if (line1 == null && line2 == null) return;
        
        VBox section = new VBox(8);
        section.setPadding(new Insets(20, 0, 0, 0));
        
        Text titleText = new Text(title);
        titleText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));
        titleText.setFill(Color.web("#5d4e37"));
        
        VBox content = new VBox(5);
        if (line1 != null && !line1.isEmpty()) {
            Text t1 = new Text(line1);
            t1.setFont(Font.font("Times New Roman", FontWeight.BOLD, 12));
            t1.setFill(Color.web("#2c3e50"));
            content.getChildren().add(t1);
        }
        if (line2 != null && !line2.isEmpty()) {
            Text t2 = new Text(line2);
            t2.setFont(Font.font("Times New Roman", 11));
            t2.setFill(Color.web("#555"));
            content.getChildren().add(t2);
        }
        if (line3 != null && !line3.isEmpty()) {
            Text t3 = new Text(line3);
            t3.setFont(Font.font("Times New Roman", 10));
            t3.setFill(Color.web("#777"));
            content.getChildren().add(t3);
        }
        
        section.getChildren().addAll(titleText, content);
        paper.getChildren().add(section);
    }
    
    private void addClassicSkillsSection(VBox paper, String skills) {
        VBox section = new VBox(8);
        section.setPadding(new Insets(20, 0, 0, 0));
        
        Text titleText = new Text("SKILLS");
        titleText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));
        titleText.setFill(Color.web("#5d4e37"));
        
        Text skillsText = new Text(skills);
        skillsText.setFont(Font.font("Times New Roman", 11));
        skillsText.setFill(Color.web("#555"));
        skillsText.setWrappingWidth(480);
        
        section.getChildren().addAll(titleText, skillsText);
        paper.getChildren().add(section);
    }
    
    private void addModernSection(VBox paper, String title, String line1, String line2, String line3, String color) {
        if (line1 == null && line2 == null) return;
        
        VBox section = new VBox(10);
        section.setPadding(new Insets(25, 0, 0, 0));
        
        Text titleText = new Text(title);
        titleText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        titleText.setFill(Color.web(color));
        
        VBox content = new VBox(6);
        content.setPadding(new Insets(5, 0, 0, 15));
        if (line1 != null && !line1.isEmpty()) {
            Text t1 = new Text(line1);
            t1.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            t1.setFill(Color.web("#2c3e50"));
            content.getChildren().add(t1);
        }
        if (line2 != null && !line2.isEmpty()) {
            Text t2 = new Text(line2);
            t2.setFont(Font.font("Segoe UI", 12));
            t2.setFill(Color.web("#555"));
            content.getChildren().add(t2);
        }
        if (line3 != null && !line3.isEmpty()) {
            Text t3 = new Text(line3);
            t3.setFont(Font.font("Segoe UI", 11));
            t3.setFill(Color.web("#666"));
            t3.setWrappingWidth(480);
            content.getChildren().add(t3);
        }
        
        section.getChildren().addAll(titleText, content);
        paper.getChildren().add(section);
    }
    
    private void addModernSkillsSection(VBox paper, String skills) {
        VBox section = new VBox(10);
        section.setPadding(new Insets(25, 0, 0, 0));
        
        Text titleText = new Text("SKILLS");
        titleText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        titleText.setFill(Color.web("#11998e"));
        
        FlowPane skillsFlow = new FlowPane(8, 8);
        skillsFlow.setPadding(new Insets(5, 0, 0, 15));
        String[] skillArray = skills.split(",");
        for (String skill : skillArray) {
            Label skillLabel = new Label(skill.trim());
            skillLabel.setFont(Font.font("Segoe UI", 11));
            skillLabel.setStyle(
                "-fx-background-color: #667eea; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 6 14; " +
                "-fx-background-radius: 20;"
            );
            skillsFlow.getChildren().add(skillLabel);
        }
        
        section.getChildren().addAll(titleText, skillsFlow);
        paper.getChildren().add(section);
    }
    
    private void addCreativeSection(VBox paper, String title, String line1, String line2, String line3, String color) {
        if (line1 == null && line2 == null) return;
        
        VBox section = new VBox(10);
        section.setPadding(new Insets(20, 0, 0, 0));
        
        HBox titleBox = new HBox(10);
        Rectangle colorBox = new Rectangle(5, 20);
        colorBox.setFill(Color.web(color));
        Text titleText = new Text(title);
        titleText.setFont(Font.font("Arial Black", FontWeight.BOLD, 15));
        titleText.setFill(Color.web(color));
        titleBox.getChildren().addAll(colorBox, titleText);
        
        VBox content = new VBox(6);
        content.setPadding(new Insets(8, 0, 0, 20));
        if (line1 != null && !line1.isEmpty()) {
            Text t1 = new Text(line1);
            t1.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            t1.setFill(Color.web("#2c3e50"));
            content.getChildren().add(t1);
        }
        if (line2 != null && !line2.isEmpty()) {
            Text t2 = new Text(line2);
            t2.setFont(Font.font("Segoe UI", 12));
            t2.setFill(Color.web("#555"));
            content.getChildren().add(t2);
        }
        if (line3 != null && !line3.isEmpty()) {
            Text t3 = new Text(line3);
            t3.setFont(Font.font("Segoe UI", 11));
            t3.setFill(Color.web("#666"));
            t3.setWrappingWidth(480);
            content.getChildren().add(t3);
        }
        
        section.getChildren().addAll(titleBox, content);
        paper.getChildren().add(section);
    }
    
    private void addCreativeSkillsSection(VBox paper, String skills) {
        VBox section = new VBox(10);
        section.setPadding(new Insets(20, 0, 0, 0));
        
        HBox titleBox = new HBox(10);
        Rectangle colorBox = new Rectangle(5, 20);
        colorBox.setFill(Color.web("#9b59b6"));
        Text titleText = new Text("SKILLS");
        titleText.setFont(Font.font("Arial Black", FontWeight.BOLD, 15));
        titleText.setFill(Color.web("#9b59b6"));
        titleBox.getChildren().addAll(colorBox, titleText);
        
        FlowPane skillsFlow = new FlowPane(8, 8);
        skillsFlow.setPadding(new Insets(8, 0, 0, 20));
        String[] skillArray = skills.split(",");
        for (String skill : skillArray) {
            Label skillLabel = new Label(skill.trim());
            skillLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
            skillLabel.setStyle(
                "-fx-background-color: #e74c3c; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 7 15; " +
                        
                                                            "-fx-background-radius: 18;"
            );
            skillsFlow.getChildren().add(skillLabel);
        }
        
        section.getChildren().addAll(titleBox, skillsFlow);
        paper.getChildren().add(section);
    }
    
    private VBox createCVSection(String title, String color) {
        VBox section = new VBox(0);
        section.setPadding(new Insets(themeSettings.getSectionSpacing(), 0, 0, 0));
        
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Line colorBar = new Line(0, 0, 4, 0);
        colorBar.setStroke(Color.web(color));
        colorBar.setStrokeWidth(20);
        
        Text titleText = new Text(title);
        titleText.setFont(Font.font(themeSettings.getBodyFont(), FontWeight.BOLD, themeSettings.getSectionFontSize()));
        titleText.setFill(Color.web(color));
        
        headerBox.getChildren().addAll(colorBar, titleText);
        section.getChildren().add(headerBox);
        
        return section;
    }
    
    private boolean hasEducation() {
        return resume != null && (
            (resume.getInstitution() != null && !resume.getInstitution().isEmpty()) ||
            (resume.getDegree() != null && !resume.getDegree().isEmpty())
        );
    }
    
    private boolean hasExperience() {
        return resume != null && (
            (resume.getJobTitle() != null && !resume.getJobTitle().isEmpty()) ||
            (resume.getCompany() != null && !resume.getCompany().isEmpty())
        );
    }
    
    private void handlePrint() {
        try {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job == null) {
                showAlert("No printer available. Please check your printer settings.");
                return;
            }
            
            // Show print dialog
            if (job.showPrintDialog(stage)) {
                // Use the existing CV content or create new one
                VBox printContent = cvContent != null ? cvContent : createPreviewContent();
                
                // Ensure content is properly laid out
                printContent.applyCss();
                printContent.layout();
                
                // Wait a moment for layout to complete
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Get printer and page layout
                Printer printer = job.getPrinter();
                javafx.print.PageLayout pageLayout = printer.createPageLayout(
                    Paper.A4,
                    PageOrientation.PORTRAIT,
                    MarginType.DEFAULT
                );
                
                // Set up the print job with page layout
                job.getJobSettings().setPageLayout(pageLayout);
                
                // Print the content
                boolean success = job.printPage(printContent);
                
                if (success) {
                    job.endJob();
                    showAlert("Resume printed successfully!");
                } else {
                    job.cancelJob();
                    showAlert("Failed to print. Please try again.");
                }
            } else {
                // User cancelled
                job.cancelJob();
            }
        } catch (Exception e) {
            System.err.println("Error printing: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error printing resume: " + e.getMessage());
        }
    }
    
    private void showAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.INFORMATION
        );
        alert.setTitle("Print");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void show() {
        stage.show();
    }
    
    /**
     * Get the preview content node for PDF export (screenshot)
     * This creates the same preview content without the window frame
     */
    public static javafx.scene.Node getPreviewContentNode(Resume resume) {
        CVTheme theme = ThemeManager.getSelectedTheme();
        CVThemeSettings settings = ThemeSettingsManager.getSettings();
        
        // Create a temporary instance to use instance methods
        ProfessionalCVViewFX tempView = new ProfessionalCVViewFX();
        tempView.resume = resume;
        tempView.currentTheme = theme;
        tempView.themeSettings = settings;
        
        // Always use professional theme method but with custom settings applied
        return tempView.createProfessionalTheme();
    }
    
    // Legacy method for backward compatibility - keeping old implementation
    private static javafx.scene.Node getPreviewContentNodeLegacy(Resume resume) {
        VBox wrapper = new VBox();
        wrapper.setAlignment(Pos.TOP_CENTER);
        wrapper.setPadding(new Insets(40));
        
        // Paper
        VBox paper = new VBox(0);
        paper.setMaxWidth(650);
        paper.setMinHeight(900);
        paper.setStyle(
            "-fx-background-color: white; " +
            
                                                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 20, 0, 0, 5);"
        );
        paper.setPadding(new Insets(50, 60, 50, 60));
        
        if (resume == null) {
            Text noResume = new Text("No resume found. Please create one first.");
            noResume.setFont(Font.font("Segoe UI", 16));
            noResume.setFill(Color.GRAY);
            paper.getChildren().add(noResume);
            paper.setAlignment(Pos.CENTER);
        } else {
            // Header - Name
            Text nameText = new Text(resume.getName() != null ? resume.getName().toUpperCase() : "YOUR NAME");
            nameText.setFont(Font.font("Georgia", FontWeight.BOLD, 32));
            nameText.setFill(Color.web("#2c3e50"));
            
            // Contact info
            HBox contactBox = new HBox(20);
            contactBox.setAlignment(Pos.CENTER);
            contactBox.setPadding(new Insets(10, 0, 20, 0));
            
            if (resume.getEmail() != null && !resume.getEmail().isEmpty()) {
                Text emailText = new Text("✉ " + resume.getEmail());
                emailText.setFont(Font.font("Segoe UI", 11));
                emailText.setFill(Color.web("#666"));
                contactBox.getChildren().add(emailText);
            }
            if (resume.getPhone() != null && !resume.getPhone().isEmpty()) {
                Text phoneText = new Text("📱 " + resume.getPhone());
                phoneText.setFont(Font.font("Segoe UI", 11));
                phoneText.setFill(Color.web("#666"));
                contactBox.getChildren().add(phoneText);
            }
            if (resume.getAddress() != null && !resume.getAddress().isEmpty()) {
                Text addressText = new Text("📍 " + resume.getAddress());
                addressText.setFont(Font.font("Segoe UI", 11));
                addressText.setFill(Color.web("#666"));
                contactBox.getChildren().add(addressText);
            }
            
            // Divider
            Line divider = new Line(0, 0, 530, 0);
            divider.setStroke(Color.web("#3498db"));
            divider.setStrokeWidth(3);
            
            VBox headerBox = new VBox(5, nameText, contactBox, divider);
            headerBox.setAlignment(Pos.CENTER);
            
            paper.getChildren().add(headerBox);
            
            // Education Section
            if ((resume.getInstitution() != null && !resume.getInstitution().isEmpty()) ||
                (resume.getDegree() != null && !resume.getDegree().isEmpty())) {
                VBox eduSection = createCVSectionStatic("EDUCATION", "#3498db");
                
                VBox eduContent = new VBox(5);
                eduContent.setPadding(new Insets(10, 0, 0, 20));
                
                Text degreeText = new Text(resume.getDegree());
                degreeText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                degreeText.setFill(Color.web("#2c3e50"));
                
                Text institutionText = new Text(resume.getInstitution());
                institutionText.setFont(Font.font("Segoe UI", 12));
                institutionText.setFill(Color.web("#555"));
                
                Text yearText = new Text(resume.getYear());
                yearText.setFont(Font.font("Segoe UI", 11));
                yearText.setFill(Color.web("#888"));
                
                eduContent.getChildren().addAll(degreeText, institutionText, yearText);
                eduSection.getChildren().add(eduContent);
                paper.getChildren().add(eduSection);
            }
            
            // Experience Section
            if ((resume.getJobTitle() != null && !resume.getJobTitle().isEmpty()) ||
                (resume.getCompany() != null && !resume.getCompany().isEmpty())) {
                VBox expSection = createCVSectionStatic("PROFESSIONAL EXPERIENCE", "#e74c3c");
                
                VBox expContent = new VBox(5);
                expContent.setPadding(new Insets(10, 0, 0, 20));
                
                HBox titleLine = new HBox(10);
                Text jobText = new Text(resume.getJobTitle());
                jobText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                jobText.setFill(Color.web("#2c3e50"));
                
                Text durationText = new Text("(" + resume.getDuration() + ")");
                durationText.setFont(Font.font("Segoe UI", 11));
                durationText.setFill(Color.web("#888"));
                
                titleLine.getChildren().addAll(jobText, durationText);
                
                Text companyText = new Text(resume.getCompany());
                companyText.setFont(Font.font("Segoe UI", 12));
                companyText.setFill(Color.web("#555"));
                
                Text descText = new Text(resume.getDescription());
                descText.setFont(Font.font("Segoe UI", 11));
                descText.setFill(Color.web("#666"));
                descText.setWrappingWidth(480);
                
                expContent.getChildren().addAll(titleLine, companyText, descText);
                expSection.getChildren().add(expContent);
                paper.getChildren().add(expSection);
            }
            
            // Skills Section
            if (resume.getSkills() != null && !resume.getSkills().isEmpty()) {
                VBox skillsSection = createCVSectionStatic("SKILLS", "#27ae60");
                
                FlowPane skillsFlow = new FlowPane(10, 10);
                skillsFlow.setPadding(new Insets(10, 0, 0, 20));
                
                String[] skills = resume.getSkills().split(",");
                for (String skill : skills) {
                    Label skillLabel = new Label(skill.trim());
                    skillLabel.setFont(Font.font("Segoe UI", 11));
                    skillLabel.setStyle(
                        "-fx-background-color: #ecf0f1; " +
                        "-fx-text-fill: #2c3e50; " +
                        "-fx-padding: 5 12; " +
                        
                                                            "-fx-background-radius: 15;"
                    );
                    skillsFlow.getChildren().add(skillLabel);
                }
                
                skillsSection.getChildren().add(skillsFlow);
                paper.getChildren().add(skillsSection);
            }
        }
        
        wrapper.getChildren().add(paper);
        return wrapper;
    }
    
    private static VBox createCVSectionStatic(String title, String color) {
        VBox section = new VBox(0);
        section.setPadding(new Insets(25, 0, 0, 0));
        
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Line colorBar = new Line(0, 0, 4, 0);
        colorBar.setStroke(Color.web(color));
        colorBar.setStrokeWidth(20);
        
        Text titleText = new Text(title);
        titleText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        titleText.setFill(Color.web(color));
        
        headerBox.getChildren().addAll(colorBar, titleText);
        section.getChildren().add(headerBox);
        
        return section;
    }
}
