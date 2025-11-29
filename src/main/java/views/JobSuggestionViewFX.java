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
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import managers.AIJobSuggestionManager;
import managers.AIJobSuggestionManager.JobSuggestion;
import managers.AIJobSuggestionManager.JobListing;
import managers.LinkedInManager;

import java.util.List;

/**
 * AI-Powered Job Suggestion View
 * Suggests jobs based on skills and finds companies by location
 */
public class JobSuggestionViewFX {
    private Stage stage;
    private TextField txtSkills;
    private TextField txtLocation;
    private VBox resultsContainer;
    private TabPane tabPane;
    private Label linkedInStatusLabel;
    private Button linkedInBtn;
    
    public JobSuggestionViewFX() {
        stage = new Stage();
        stage.setTitle("AI Job Suggestions");
        createUI();
    }
    
    private void createUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f0f1a;");
        
        // Header
        VBox header = createHeader();
        root.setTop(header);
        
        // Main content
        VBox mainContent = createMainContent();
        root.setCenter(mainContent);
        
        Scene scene = new Scene(root, 1000, 700);
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
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Gradient background for header
        Stop[] stops = new Stop[] {
            new Stop(0, Color.web("#667eea")),
            new Stop(1, Color.web("#764ba2"))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        header.setBackground(new Background(new BackgroundFill(gradient, null, null)));
        
        // Back button
        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; " +
                        "-fx-font-size: 12px; -fx-cursor: hand; -fx-background-radius: 20;");
        backBtn.setOnAction(e -> stage.close());
        
        // Title
        Text title = new Text("🤖 AI Job Suggestions");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        title.setFill(Color.WHITE);
        
        Text subtitle = new Text("Find your perfect job match based on your skills and location");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setFill(Color.rgb(255, 255, 255, 0.8));
        
        // LinkedIn connection section
        HBox linkedInBox = createLinkedInSection();
        
        // Layout with LinkedIn on right
        HBox headerContent = new HBox();
        headerContent.setAlignment(Pos.CENTER_LEFT);
        
        VBox leftContent = new VBox(5);
        leftContent.getChildren().addAll(title, subtitle);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        headerContent.getChildren().addAll(leftContent, spacer, linkedInBox);
        
        header.getChildren().addAll(backBtn, headerContent);
        return header;
    }
    
    private HBox createLinkedInSection() {
        HBox section = new HBox(10);
        section.setAlignment(Pos.CENTER_RIGHT);
        section.setPadding(new Insets(5, 0, 5, 0));
        
        // LinkedIn status
        linkedInStatusLabel = new Label();
        linkedInStatusLabel.setFont(Font.font("Segoe UI", 12));
        updateLinkedInStatus();
        
        // LinkedIn connect/disconnect button
        linkedInBtn = new Button();
        updateLinkedInButton();
        
        linkedInBtn.setOnAction(e -> {
            if (LinkedInManager.isConnected()) {
                // Disconnect
                LinkedInManager.disconnect();
                updateLinkedInStatus();
                updateLinkedInButton();
                showAlert("LinkedIn disconnected successfully!");
            } else {
                // Connect - Open LinkedIn for Easy Apply
                LinkedInManager.searchLinkedInJobs(
                    txtSkills.getText().isEmpty() ? "software developer" : txtSkills.getText(),
                    txtLocation.getText().isEmpty() ? "" : txtLocation.getText()
                );
            }
        });
        
        // View Profile button (when connected)
        Button profileBtn = new Button("View Profile");
        profileBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; " +
                          "-fx-font-size: 11px; -fx-cursor: hand; -fx-underline: true;");
        profileBtn.setOnAction(e -> LinkedInManager.openLinkedInProfile());
        
        section.getChildren().addAll(linkedInStatusLabel, linkedInBtn);
        return section;
    }
    
    private void updateLinkedInStatus() {
        if (LinkedInManager.isConnected()) {
            linkedInStatusLabel.setText("✓ Connected as " + LinkedInManager.getConnectedUserName());
            linkedInStatusLabel.setTextFill(Color.web("#00d26a"));
        } else {
            linkedInStatusLabel.setText("Not connected");
            linkedInStatusLabel.setTextFill(Color.rgb(255, 255, 255, 0.6));
        }
    }
    
    private void updateLinkedInButton() {
        if (LinkedInManager.isConnected()) {
            linkedInBtn.setText("Disconnect");
            linkedInBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: #ff6b6b; " +
                               "-fx-font-size: 12px; -fx-cursor: hand; -fx-background-radius: 15; " +
                               "-fx-padding: 5 15;");
        } else {
            linkedInBtn.setText("🔗 Connect LinkedIn");
            linkedInBtn.setStyle("-fx-background-color: #0077b5; -fx-text-fill: white; " +
                               "-fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand; " +
                               "-fx-background-radius: 15; -fx-padding: 5 15;");
        }
    }
    
    private VBox createMainContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_CENTER);
        
        // Search section
        HBox searchSection = createSearchSection();
        
        // Tab pane for results
        tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent;");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Tab suggestionsTab = new Tab("💡 Job Suggestions");
        suggestionsTab.setStyle("-fx-background-color: #1a1a2e;");
        
        Tab listingsTab = new Tab("🔍 Job Listings");
        listingsTab.setStyle("-fx-background-color: #1a1a2e;");
        
        // Results container
        resultsContainer = new VBox(15);
        resultsContainer.setPadding(new Insets(20));
        resultsContainer.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 15;");
        
        ScrollPane scrollPane = new ScrollPane(resultsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setPrefHeight(400);
        
        suggestionsTab.setContent(scrollPane);
        listingsTab.setContent(createListingsPlaceholder());
        
        tabPane.getTabs().addAll(suggestionsTab, listingsTab);
        
        // Initial message
        showInitialMessage();
        
        content.getChildren().addAll(searchSection, tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        
        return content;
    }
    
    private HBox createSearchSection() {
        HBox section = new HBox(15);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 15;");
        
        // Skills input
        VBox skillsBox = new VBox(5);
        Label skillsLabel = new Label("Your Skills");
        skillsLabel.setTextFill(Color.web("#aaa"));
        skillsLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        
        txtSkills = new TextField();
        txtSkills.setPromptText("e.g., Java, Python, React, Machine Learning");
        txtSkills.setPrefWidth(300);
        txtSkills.setStyle(getInputStyle());
        skillsBox.getChildren().addAll(skillsLabel, txtSkills);
        
        // Location input
        VBox locationBox = new VBox(5);
        Label locationLabel = new Label("Your Location");
        locationLabel.setTextFill(Color.web("#aaa"));
        locationLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        
        txtLocation = new TextField();
        txtLocation.setPromptText("e.g., New York, San Francisco, Remote");
        txtLocation.setPrefWidth(250);
        txtLocation.setStyle(getInputStyle());
        locationBox.getChildren().addAll(locationLabel, txtLocation);
        
        // Search button
        Button searchBtn = new Button("🔍 Find Jobs");
        searchBtn.setPrefHeight(45);
        searchBtn.setPrefWidth(150);
        searchBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        searchBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #00d2ff, #3a7bd5); " +
            "-fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;"
        );
        searchBtn.setEffect(new DropShadow(10, Color.rgb(0, 210, 255, 0.5)));
        
        searchBtn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), searchBtn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });
        searchBtn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), searchBtn);
            st.setToX(1);
            st.setToY(1);
            st.play();
        });
        searchBtn.setOnAction(e -> performSearch());
        
        // AI Suggest button
        Button aiBtn = new Button("🤖 AI Suggest");
        aiBtn.setPrefHeight(45);
        aiBtn.setPrefWidth(150);
        aiBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        aiBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
            "-fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;"
        );
        aiBtn.setEffect(new DropShadow(10, Color.rgb(102, 126, 234, 0.5)));
        
        aiBtn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), aiBtn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });
        aiBtn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), aiBtn);
            st.setToX(1);
            st.setToY(1);
            st.play();
        });
        aiBtn.setOnAction(e -> performAISuggestion());
        
        VBox buttonsBox = new VBox(10);
        buttonsBox.setAlignment(Pos.BOTTOM_LEFT);
        buttonsBox.getChildren().addAll(searchBtn, aiBtn);
        
        section.getChildren().addAll(skillsBox, locationBox, buttonsBox);
        return section;
    }
    
    private void showInitialMessage() {
        resultsContainer.getChildren().clear();
        
        VBox messageBox = new VBox(15);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPadding(new Insets(50));
        
        Text emoji = new Text("🎯");
        emoji.setFont(Font.font(60));
        
        Text title = new Text("Find Your Dream Job");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setFill(Color.WHITE);
        
        Text instruction = new Text("Enter your skills and location above, then click\n" +
                                   "'AI Suggest' for personalized job recommendations or\n" +
                                   "'Find Jobs' to search for open positions.");
        instruction.setFont(Font.font("Segoe UI", 14));
        instruction.setFill(Color.web("#888"));
        instruction.setStyle("-fx-text-alignment: center;");
        
        messageBox.getChildren().addAll(emoji, title, instruction);
        resultsContainer.getChildren().add(messageBox);
    }
    
    private void performAISuggestion() {
        String skills = txtSkills.getText().trim();
        
        if (skills.isEmpty()) {
            showAlert("Please enter your skills first!");
            return;
        }
        
        resultsContainer.getChildren().clear();
        
        // Show loading
        ProgressIndicator loading = new ProgressIndicator();
        loading.setStyle("-fx-progress-color: #667eea;");
        resultsContainer.getChildren().add(loading);
        resultsContainer.setAlignment(Pos.CENTER);
        
        // Simulate AI processing
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Simulate AI thinking
            } catch (InterruptedException ignored) {}
            
            List<JobSuggestion> suggestions = AIJobSuggestionManager.suggestJobsFromSkills(skills);
            
            javafx.application.Platform.runLater(() -> {
                resultsContainer.getChildren().clear();
                resultsContainer.setAlignment(Pos.TOP_LEFT);
                
                if (suggestions.isEmpty()) {
                    showNoResults();
                } else {
                    Text header = new Text("🎯 AI-Suggested Jobs for You");
                    header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
                    header.setFill(Color.WHITE);
                    resultsContainer.getChildren().add(header);
                    
                    for (JobSuggestion suggestion : suggestions) {
                        resultsContainer.getChildren().add(createSuggestionCard(suggestion));
                    }
                }
            });
        }).start();
        
        tabPane.getSelectionModel().select(0);
    }
    
    private void performSearch() {
        String skills = txtSkills.getText().trim();
        String location = txtLocation.getText().trim();
        
        if (skills.isEmpty()) {
            showAlert("Please enter job title or skills to search!");
            return;
        }
        
        resultsContainer.getChildren().clear();
        
        // Show loading
        ProgressIndicator loading = new ProgressIndicator();
        loading.setStyle("-fx-progress-color: #00d2ff;");
        resultsContainer.getChildren().add(loading);
        resultsContainer.setAlignment(Pos.CENTER);
        
        // Search for jobs
        new Thread(() -> {
            List<JobListing> listings = AIJobSuggestionManager.searchJobs(skills, location);
            
            javafx.application.Platform.runLater(() -> {
                resultsContainer.getChildren().clear();
                resultsContainer.setAlignment(Pos.TOP_LEFT);
                
                if (listings.isEmpty()) {
                    showNoResults();
                } else {
                    Text header = new Text("🔍 Found " + listings.size() + " Jobs");
                    header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
                    header.setFill(Color.WHITE);
                    resultsContainer.getChildren().add(header);
                    
                    for (JobListing listing : listings) {
                        resultsContainer.getChildren().add(createListingCard(listing));
                    }
                }
            });
        }).start();
        
        tabPane.getSelectionModel().select(0);
    }
    
    private VBox createSuggestionCard(JobSuggestion suggestion) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #252540; -fx-background-radius: 12;");
        card.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.3)));
        
        // Header row
        HBox headerRow = new HBox(10);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        
        Text title = new Text(suggestion.title);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        title.setFill(Color.WHITE);
        
        Label matchBadge = new Label(suggestion.matchScore);
        matchBadge.setStyle("-fx-background-color: #00d26a; -fx-text-fill: white; " +
                          "-fx-padding: 3 10; -fx-background-radius: 10; -fx-font-size: 11px;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        headerRow.getChildren().addAll(title, spacer, matchBadge);
        
        // Salary
        Text salary = new Text("💰 " + suggestion.salaryRange);
        salary.setFont(Font.font("Segoe UI", 14));
        salary.setFill(Color.web("#00d2ff"));
        
        // Description
        Text desc = new Text(suggestion.description);
        desc.setFont(Font.font("Segoe UI", 13));
        desc.setFill(Color.web("#aaa"));
        desc.setWrappingWidth(700);
        
        // Apply button
        Button applyBtn = new Button("Search This Job →");
        applyBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #667eea; " +
                         "-fx-font-size: 13px; -fx-cursor: hand; -fx-border-color: #667eea; " +
                         "-fx-border-radius: 5; -fx-padding: 5 15;");
        applyBtn.setOnAction(e -> {
            txtSkills.setText(suggestion.title);
            performSearch();
        });
        
        card.getChildren().addAll(headerRow, salary, desc, applyBtn);
        
        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #2d2d50; -fx-background-radius: 12;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #252540; -fx-background-radius: 12;"));
        
        return card;
    }
    
    private VBox createListingCard(JobListing listing) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #252540; -fx-background-radius: 12;");
        card.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.3)));
        
        // Company and job type row
        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);
        
        Text company = new Text(listing.company);
        company.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        company.setFill(Color.web("#667eea"));
        
        Label typeBadge = new Label(listing.jobType);
        typeBadge.setStyle("-fx-background-color: #3a3a5c; -fx-text-fill: #aaa; " +
                         "-fx-padding: 2 8; -fx-background-radius: 8; -fx-font-size: 11px;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Text posted = new Text(listing.postedDate);
        posted.setFont(Font.font("Segoe UI", 11));
        posted.setFill(Color.web("#666"));
        
        topRow.getChildren().addAll(company, typeBadge, spacer, posted);
        
        // Job title
        Text title = new Text(listing.title);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        title.setFill(Color.WHITE);
        
        // Location and salary
        HBox detailsRow = new HBox(20);
        Text location = new Text("📍 " + listing.location);
        location.setFont(Font.font("Segoe UI", 13));
        location.setFill(Color.web("#aaa"));
        
        Text salary = new Text("💰 " + listing.salary);
        salary.setFont(Font.font("Segoe UI", 13));
        salary.setFill(Color.web("#00d26a"));
        
        detailsRow.getChildren().addAll(location, salary);
        
        // Buttons row
        HBox buttonsRow = new HBox(10);
        buttonsRow.setAlignment(Pos.CENTER_LEFT);
        
        // Apply button
        Button applyBtn = new Button("Apply Now →");
        applyBtn.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
                         "-fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand; " +
                         "-fx-background-radius: 5; -fx-padding: 8 20;");
        applyBtn.setOnAction(e -> {
            // Open browser to apply
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(listing.applyUrl));
            } catch (Exception ex) {
                showAlert("Could not open browser. Visit: " + listing.applyUrl);
            }
        });
        
        // LinkedIn Easy Apply button
        Button linkedInApplyBtn = new Button("🔗 Easy Apply");
        linkedInApplyBtn.setStyle("-fx-background-color: #0077b5; " +
                                 "-fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand; " +
                                 "-fx-background-radius: 5; -fx-padding: 8 20;");
        linkedInApplyBtn.setOnAction(e -> {
            // Open LinkedIn with job search for easy apply
            LinkedInManager.searchLinkedInJobs(listing.title, listing.location);
        });
        
        buttonsRow.getChildren().addAll(applyBtn, linkedInApplyBtn);
        
        card.getChildren().addAll(topRow, title, detailsRow, buttonsRow);
        
        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #2d2d50; -fx-background-radius: 12;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #252540; -fx-background-radius: 12;"));
        
        return card;
    }
    
    private ScrollPane createListingsPlaceholder() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #1a1a2e;");
        
        Text emoji = new Text("🌐");
        emoji.setFont(Font.font(50));
        
        Text title = new Text("Connect to Job APIs");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setFill(Color.WHITE);
        
        Text desc = new Text("To get real job listings, connect to job APIs like:\n\n" +
                            "• LinkedIn Jobs API\n" +
                            "• Indeed API\n" +
                            "• Glassdoor API\n" +
                            "• JSearch (RapidAPI)\n\n" +
                            "Use 'Find Jobs' button to see demo listings.");
        desc.setFont(Font.font("Segoe UI", 14));
        desc.setFill(Color.web("#888"));
        
        content.getChildren().addAll(emoji, title, desc);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return scrollPane;
    }
    
    private void showNoResults() {
        VBox messageBox = new VBox(15);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPadding(new Insets(50));
        
        Text emoji = new Text("😕");
        emoji.setFont(Font.font(50));
        
        Text title = new Text("No matches found");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        title.setFill(Color.WHITE);
        
        Text instruction = new Text("Try different skills or broaden your search");
        instruction.setFont(Font.font("Segoe UI", 14));
        instruction.setFill(Color.web("#888"));
        
        messageBox.getChildren().addAll(emoji, title, instruction);
        resultsContainer.getChildren().add(messageBox);
    }
    
    private String getInputStyle() {
        return "-fx-background-color: #252540; -fx-text-fill: white; " +
               "-fx-prompt-text-fill: #666; -fx-background-radius: 8; " +
               "-fx-border-color: #3a3a5c; -fx-border-radius: 8; " +
               "-fx-padding: 10 15; -fx-font-size: 14px;";
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Notice");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void show() {
        stage.show();
    }
}

