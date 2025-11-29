package managers;

import java.awt.Desktop;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * LinkedIn Integration Manager
 * Handles OAuth authentication and Easy Apply functionality
 */
public class LinkedInManager {
    private static final Logger logger = Logger.getLogger(LinkedInManager.class.getName());
    
    // LinkedIn OAuth Configuration
    // Get your credentials from: https://www.linkedin.com/developers/apps
    private static String CLIENT_ID = "YOUR_LINKEDIN_CLIENT_ID";
    private static String CLIENT_SECRET = "YOUR_LINKEDIN_CLIENT_SECRET";
    private static final String REDIRECT_URI = "http://localhost:8080/callback";
    private static final String AUTH_URL = "https://www.linkedin.com/oauth/v2/authorization";
    private static final String TOKEN_URL = "https://www.linkedin.com/oauth/v2/accessToken";
    private static final String API_BASE = "https://api.linkedin.com/v2";
    
    // Scopes needed for job applications
    private static final String SCOPES = "r_liteprofile r_emailaddress w_member_social";
    
    // Current session
    private static String accessToken = null;
    private static String linkedInUserId = null;
    private static String linkedInUserName = null;
    private static String linkedInEmail = null;
    private static boolean isConnected = false;
    
    /**
     * Configure LinkedIn API credentials
     */
    public static void configure(String clientId, String clientSecret) {
        CLIENT_ID = clientId;
        CLIENT_SECRET = clientSecret;
    }
    
    /**
     * Check if LinkedIn is connected
     */
    public static boolean isConnected() {
        return isConnected && accessToken != null;
    }
    
    /**
     * Get connected user name
     */
    public static String getConnectedUserName() {
        return linkedInUserName;
    }
    
    /**
     * Get connected email
     */
    public static String getConnectedEmail() {
        return linkedInEmail;
    }
    
    /**
     * Start LinkedIn OAuth flow
     * Opens browser for user authentication
     */
    public static void startOAuthFlow() {
        try {
            String authUrl = AUTH_URL + 
                "?response_type=code" +
                "&client_id=" + URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8.toString()) +
                "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8.toString()) +
                "&scope=" + URLEncoder.encode(SCOPES, StandardCharsets.UTF_8.toString()) +
                "&state=" + generateState();
            
            // Open browser for authentication
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(authUrl));
            }
            
            // Start local server to receive callback
            startCallbackServer();
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error starting OAuth flow", e);
        }
    }
    
    /**
     * Start local server to receive OAuth callback
     */
    private static void startCallbackServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(8080)) {
                serverSocket.setSoTimeout(120000); // 2 minute timeout
                
                Socket socket = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                
                String line = reader.readLine();
                if (line != null && line.contains("code=")) {
                    // Extract authorization code
                    String code = extractParam(line, "code");
                    
                    // Exchange code for access token
                    exchangeCodeForToken(code);
                    
                    // Send success response
                    String response = "HTTP/1.1 200 OK\r\n\r\n" +
                        "<html><body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                        "<h1 style='color: #0077b5;'>✓ LinkedIn Connected!</h1>" +
                        "<p>You can close this window and return to the app.</p>" +
                        "</body></html>";
                    writer.println(response);
                }
                
                socket.close();
            } catch (Exception e) {
                logger.log(Level.WARNING, "Callback server error", e);
            }
        }).start();
    }
    
    /**
     * Exchange authorization code for access token
     */
    private static void exchangeCodeForToken(String code) {
        try {
            URL url = new URL(TOKEN_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            String params = "grant_type=authorization_code" +
                "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8.toString()) +
                "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8.toString()) +
                "&client_id=" + URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8.toString()) +
                "&client_secret=" + URLEncoder.encode(CLIENT_SECRET, StandardCharsets.UTF_8.toString());
            
            try (OutputStream os = conn.getOutputStream()) {
                os.write(params.getBytes(StandardCharsets.UTF_8));
            }
            
            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                
                // Parse access token from JSON response
                accessToken = extractJsonValue(response.toString(), "access_token");
                isConnected = true;
                
                // Fetch user profile
                fetchUserProfile();
                
                logger.info("LinkedIn connected successfully!");
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error exchanging code for token", e);
        }
    }
    
    /**
     * Fetch LinkedIn user profile
     */
    private static void fetchUserProfile() {
        try {
            // Get basic profile
            URL url = new URL(API_BASE + "/me");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            
            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                
                linkedInUserId = extractJsonValue(response.toString(), "id");
                String firstName = extractJsonValue(response.toString(), "localizedFirstName");
                String lastName = extractJsonValue(response.toString(), "localizedLastName");
                linkedInUserName = firstName + " " + lastName;
            }
            
            // Get email
            URL emailUrl = new URL(API_BASE + "/emailAddress?q=members&projection=(elements*(handle~))");
            HttpURLConnection emailConn = (HttpURLConnection) emailUrl.openConnection();
            emailConn.setRequestMethod("GET");
            emailConn.setRequestProperty("Authorization", "Bearer " + accessToken);
            
            if (emailConn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(emailConn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                linkedInEmail = extractJsonValue(response.toString(), "emailAddress");
            }
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error fetching user profile", e);
        }
    }
    
    /**
     * Apply to job using LinkedIn Easy Apply
     */
    public static ApplyResult applyToJob(String jobUrl, String companyName, String jobTitle) {
        if (!isConnected()) {
            return new ApplyResult(false, "Please connect your LinkedIn account first");
        }
        
        try {
            // For Easy Apply, we redirect to LinkedIn's job application page
            // with the user already authenticated
            String linkedInJobUrl = convertToLinkedInJobUrl(jobUrl, companyName, jobTitle);
            
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(linkedInJobUrl));
                return new ApplyResult(true, "Opening LinkedIn Easy Apply...");
            }
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error applying to job", e);
            return new ApplyResult(false, "Error: " + e.getMessage());
        }
        
        return new ApplyResult(false, "Could not open browser");
    }
    
    /**
     * Search for jobs on LinkedIn
     */
    public static String searchLinkedInJobs(String keywords, String location) {
        try {
            String searchUrl = "https://www.linkedin.com/jobs/search/?" +
                "keywords=" + URLEncoder.encode(keywords, StandardCharsets.UTF_8.toString()) +
                "&location=" + URLEncoder.encode(location, StandardCharsets.UTF_8.toString()) +
                "&f_AL=true"; // Easy Apply filter
            
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(searchUrl));
            }
            return searchUrl;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error searching LinkedIn jobs", e);
            return null;
        }
    }
    
    /**
     * Open LinkedIn profile
     */
    public static void openLinkedInProfile() {
        try {
            String profileUrl = "https://www.linkedin.com/in/me/";
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(profileUrl));
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error opening LinkedIn profile", e);
        }
    }
    
    /**
     * Disconnect LinkedIn account
     */
    public static void disconnect() {
        accessToken = null;
        linkedInUserId = null;
        linkedInUserName = null;
        linkedInEmail = null;
        isConnected = false;
        logger.info("LinkedIn disconnected");
    }
    
    /**
     * Convert job URL to LinkedIn job search
     */
    private static String convertToLinkedInJobUrl(String jobUrl, String companyName, String jobTitle) {
        try {
            return "https://www.linkedin.com/jobs/search/?" +
                "keywords=" + URLEncoder.encode(jobTitle + " " + companyName, StandardCharsets.UTF_8.toString()) +
                "&f_AL=true"; // Easy Apply filter
        } catch (Exception e) {
            return "https://www.linkedin.com/jobs/";
        }
    }
    
    /**
     * Generate random state for OAuth
     */
    private static String generateState() {
        return Base64.getEncoder().encodeToString(
            String.valueOf(System.currentTimeMillis()).getBytes()
        );
    }
    
    /**
     * Extract parameter from URL query string
     */
    private static String extractParam(String url, String param) {
        int start = url.indexOf(param + "=");
        if (start == -1) return null;
        start += param.length() + 1;
        int end = url.indexOf("&", start);
        if (end == -1) end = url.indexOf(" ", start);
        if (end == -1) end = url.length();
        return url.substring(start, end);
    }
    
    /**
     * Simple JSON value extractor
     */
    private static String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int start = json.indexOf(searchKey);
        if (start == -1) return null;
        start += searchKey.length();
        
        // Skip whitespace
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }
        
        if (json.charAt(start) == '"') {
            // String value
            start++;
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        } else {
            // Number or other value
            int end = start;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') {
                end++;
            }
            return json.substring(start, end).trim();
        }
    }
    
    /**
     * Apply result data class
     */
    public static class ApplyResult {
        public final boolean success;
        public final String message;
        
        public ApplyResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}

