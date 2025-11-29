package views;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.QRCodeGenerator;
import managers.ThemeManager;
import managers.ThemeManager.ThemeColors;

import java.awt.Desktop;
import java.net.URI;

/**
 * QR Code View - Shows QR code for mobile app download
 */
public class QRCodeViewFX {
    private Stage stage;
    private ImageView qrCodeView;
    private ThemeColors colors;
    
    public QRCodeViewFX() {
        stage = new Stage();
        stage.setTitle("Download App - Scan QR Code");
        colors = ThemeManager.getColors();
        createUI();
    }
    
    private void createUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + colors.bgPrimary + ";");
        
        // Header
        VBox header = createHeader();
        root.setTop(header);
        
        // Center content
        VBox centerContent = createCenterContent();
        root.setCenter(centerContent);
        
        Scene scene = new Scene(root, 600, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
        
        // Fade in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(30, 40, 20, 40));
        header.setAlignment(Pos.CENTER);
        
        // Gradient background
        Stop[] stops = new Stop[] {
            new Stop(0, Color.web(colors.accentPrimary)),
            new Stop(1, Color.web(colors.accentSecondary))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        header.setBackground(new Background(new BackgroundFill(gradient, null, null)));
        
        // Back button
        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; " +
                        "-fx-font-size: 12px; -fx-cursor: hand; -fx-background-radius: 20;");
        backBtn.setOnAction(e -> stage.close());
        
        // Title
        Text title = new Text("📱 Download Mobile App");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setFill(Color.WHITE);
        
        Text subtitle = new Text("Scan QR code with your phone to download");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setFill(Color.rgb(255, 255, 255, 0.9));
        
        header.getChildren().addAll(backBtn, title, subtitle);
        return header;
    }
    
    private VBox createCenterContent() {
        VBox content = new VBox(25);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));
        
        // QR Code container
        VBox qrContainer = new VBox(15);
        qrContainer.setAlignment(Pos.CENTER);
        qrContainer.setPadding(new Insets(30));
        qrContainer.setStyle("-fx-background-color: " + colors.bgCard + "; -fx-background-radius: 20;");
        qrContainer.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.2)));
        
        // Generate QR code
        String downloadUrl = QRCodeGenerator.generateDownloadURL();
        Image qrImage = QRCodeGenerator.generateQRCode(downloadUrl, 300);
        qrCodeView = new ImageView(qrImage);
        qrCodeView.setFitWidth(300);
        qrCodeView.setFitHeight(300);
        qrCodeView.setPreserveRatio(true);
        
        // Instructions
        Text instruction1 = new Text("1. Open your phone's camera");
        instruction1.setFont(Font.font("Segoe UI", 14));
        instruction1.setFill(Color.web(colors.textPrimary));
        
        Text instruction2 = new Text("2. Point it at this QR code");
        instruction2.setFont(Font.font("Segoe UI", 14));
        instruction2.setFill(Color.web(colors.textPrimary));
        
        Text instruction3 = new Text("3. Tap the notification to download");
        instruction3.setFont(Font.font("Segoe UI", 14));
        instruction3.setFill(Color.web(colors.textPrimary));
        
        qrContainer.getChildren().addAll(qrCodeView, instruction1, instruction2, instruction3);
        
        // Download link section
        VBox linkSection = new VBox(10);
        linkSection.setAlignment(Pos.CENTER);
        
        Text orText = new Text("Or click the link below:");
        orText.setFont(Font.font("Segoe UI", 12));
        orText.setFill(Color.web(colors.textSecondary));
        
        Hyperlink downloadLink = new Hyperlink(downloadUrl);
        downloadLink.setFont(Font.font("Segoe UI", 13));
        downloadLink.setStyle("-fx-text-fill: " + colors.accentPrimary + ";");
        downloadLink.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI(downloadUrl));
            } catch (Exception ex) {
                showAlert("Could not open browser. URL: " + downloadUrl);
            }
        });
        
        // Copy button
        Button copyBtn = new Button("📋 Copy Link");
        copyBtn.setStyle("-fx-background-color: " + colors.accentPrimary + "; " +
                        "-fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand; " +
                        "-fx-background-radius: 10; -fx-padding: 8 20;");
        copyBtn.setOnAction(e -> {
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new java.awt.datatransfer.StringSelection(downloadUrl), null);
            showAlert("Link copied to clipboard!");
        });
        
        linkSection.getChildren().addAll(orText, downloadLink, copyBtn);
        
        // App info
        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER);
        
        Text appName = new Text("Resume Builder App");
        appName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        appName.setFill(Color.web(colors.textPrimary));
        
        Text version = new Text("Version 1.0");
        version.setFont(Font.font("Segoe UI", 12));
        version.setFill(Color.web(colors.textSecondary));
        
        infoBox.getChildren().addAll(appName, version);
        
        content.getChildren().addAll(qrContainer, linkSection, infoBox);
        
        return content;
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

