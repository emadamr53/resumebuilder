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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import managers.PDFExportManager;
import managers.ResumeManager;
import managers.WordExportManager;
import models.Resume;

import java.io.File;
import java.io.IOException;

/**
 * Modern Export View - Clean Card Design
 */
public class ExportViewFX {
    private Stage stage;
    
    public ExportViewFX() {
        stage = new Stage();
        stage.setTitle("Resume Builder - Export");
        stage.initStyle(StageStyle.UNDECORATED);
        createUI();
    }
    
    private void createUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f0f23;");
        
        // Top bar
        HBox topBar = createTopBar();
        root.setTop(topBar);
        
        // Center content
        VBox content = createContent();
        root.setCenter(content);
        
        Scene scene = new Scene(root, 700, 550);
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
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 14px; -fx-cursor: hand;");
        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 14px; -fx-cursor: hand;"));
        backBtn.setOnAction(e -> {
            new MainViewFX().show();
            stage.close();
        });
        
        Text title = new Text("Export Resume");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setFill(Color.WHITE);
        
        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 16px; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> {
            new MainViewFX().show();
            stage.close();
        });
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand; -fx-background-radius: 3;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-size: 16px; -fx-cursor: hand;"));
        
        bar.getChildren().addAll(backBtn, spacer1, title, spacer2, closeBtn);
        
        return bar;
    }
    
    private VBox createContent() {
        VBox content = new VBox(40);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50));
        
        // Header
        Text headerText = new Text("Choose Export Format");
        headerText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        headerText.setFill(Color.WHITE);
        
        Text subText = new Text("Download your resume in your preferred format");
        subText.setFont(Font.font("Segoe UI", 14));
        subText.setFill(Color.web("#888"));
        
        // Export cards
        HBox cardsRow = new HBox(40);
        cardsRow.setAlignment(Pos.CENTER);
        
        // PDF Card
        VBox pdfCard = createExportCard(
            "PDF Document",
            "Best for printing\nand sharing",
            "#e74c3c", "#c0392b",
            "📄"
        );
        pdfCard.setOnMouseClicked(e -> exportToPDF());
        
        // Word Card
        VBox wordCard = createExportCard(
            "Word Document",
            "Easy to edit\nand customize",
            "#3498db", "#2980b9",
            "📝"
        );
        wordCard.setOnMouseClicked(e -> exportToWord());
        
        cardsRow.getChildren().addAll(pdfCard, wordCard);
        
        content.getChildren().addAll(headerText, subText, cardsRow);
        
        return content;
    }
    
    private VBox createExportCard(String title, String description, String color1, String color2, String icon) {
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(250);
        card.setPrefHeight(220);
        card.setPadding(new Insets(30));
        card.setStyle(
            "-fx-background-color: #1a1a2e; " +
            "-fx-background-radius: 20; " +
            "-fx-cursor: hand;"
        );
        card.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.3)));
        
        // Icon
        Circle iconCircle = new Circle(40);
        Stop[] stops = new Stop[] {
            new Stop(0, Color.web(color1)),
            new Stop(1, Color.web(color2))
        };
        iconCircle.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops));
        
        Text iconText = new Text(icon);
        iconText.setFont(Font.font(32));
        
        StackPane iconPane = new StackPane(iconCircle, iconText);
        
        Text titleText = new Text(title);
        titleText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleText.setFill(Color.WHITE);
        
        Text descText = new Text(description);
        descText.setFont(Font.font("Segoe UI", 12));
        descText.setFill(Color.web("#888"));
        descText.setStyle("-fx-text-alignment: center;");
        
        card.getChildren().addAll(iconPane, titleText, descText);
        
        // Hover effect
        card.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), card);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
            card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, " + color1 + "33, " + color2 + "33); " +
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
                "-fx-background-color: #1a1a2e; " +
                "-fx-background-radius: 20; " +
                "-fx-cursor: hand;"
            );
        });
        
        return card;
    }
    
    private void exportToPDF() {
        Resume resume = ResumeManager.getLastResume();
        if (resume == null) {
            showAlert(Alert.AlertType.ERROR, "No resume found. Please create one first!");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF");
        fileChooser.setInitialFileName("resume.pdf");
        
        // Set initial directory to user's Documents folder
        String userHome = System.getProperty("user.home");
        File documentsDir = new File(userHome, "Documents");
        if (documentsDir.exists() && documentsDir.isDirectory()) {
            fileChooser.setInitialDirectory(documentsDir);
        } else {
            // Fallback to user home if Documents doesn't exist
            fileChooser.setInitialDirectory(new File(userHome));
        }
        
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            boolean success = PDFExportManager.exportToPDF(resume, file.getAbsolutePath());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "PDF exported successfully!\n" + file.getAbsolutePath());
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to export PDF!");
            }
        }
    }
    
    private void exportToWord() {
        Resume resume = ResumeManager.getLastResume();
        if (resume == null) {
            showAlert(Alert.AlertType.ERROR, "No resume found. Please create one first!");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Resume as Text");
        fileChooser.setInitialFileName("resume.txt");
        
        // Set initial directory to user's Documents folder
        String userHome = System.getProperty("user.home");
        File documentsDir = new File(userHome, "Documents");
        if (documentsDir.exists() && documentsDir.isDirectory()) {
            fileChooser.setInitialDirectory(documentsDir);
        } else {
            // Fallback to user home if Documents doesn't exist
            fileChooser.setInitialDirectory(new File(userHome));
        }
        
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            String filePath = file.getAbsolutePath();
            // Ensure .txt extension
            if (!filePath.toLowerCase().endsWith(".txt")) {
                filePath = filePath + ".txt";
            }
            
            // Show progress
            System.out.println("Exporting Word document to: " + filePath);
            
            boolean success = WordExportManager.exportToWord(resume, filePath);
            
            // Wait a moment for file system to update
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            if (success) {
                File savedFile = new File(filePath);
                
                // Check multiple times in case file system is slow
                int attempts = 0;
                while (attempts < 5 && (!savedFile.exists() || savedFile.length() == 0)) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    savedFile = new File(filePath);
                    attempts++;
                }
                
                if (savedFile.exists() && savedFile.length() > 0) {
                    String message = "Resume exported successfully as text file!\n\n" + 
                                   "Saved to: " + savedFile.getAbsolutePath() + "\n" +
                                   "File size: " + (savedFile.length() / 1024) + " KB\n\n" +
                                   "Note: This is a .txt file that can be opened in Word or any text editor.";
                    showAlert(Alert.AlertType.INFORMATION, message);
                    
                    // Try to reveal file in Finder (macOS)
                    try {
                        Runtime.getRuntime().exec(new String[]{"open", "-R", savedFile.getAbsolutePath()});
                    } catch (IOException e) {
                        // Ignore if reveal fails
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "File was not created properly.\n" +
                              "Path: " + filePath + "\n" +
                              "Please check file permissions and try again.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to export resume!\n" +
                          "Please check the console/terminal for error details.");
            }
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
