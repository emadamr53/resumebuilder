package managers;

import models.User;
import utils.DatabaseManager;
import utils.EncryptionUtils;
import utils.ValidationUtils;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manager class for user operations
 * @author habib
 */
public class UserManager {
    private static final Logger logger = Logger.getLogger(UserManager.class.getName());
    
    static {
        DatabaseManager.initializeDatabase();
    }
    
    /**
     * Register a new user
     * @return true if successful, false if email/username already exists or validation fails
     */
    public static boolean registerUser(String username, String email, String password, String fullName) {
        if (!ValidationUtils.isValidUsername(username) || 
            !ValidationUtils.isValidEmail(email) ||
            !ValidationUtils.isValidPassword(password)) {
            return false;
        }
        
        // Check if username or email already exists BEFORE trying to insert
        if (usernameExists(username)) {
            logger.log(Level.INFO, "Registration failed: Username already exists: " + username);
            return false;
        }
        
        if (emailExists(email)) {
            logger.log(Level.INFO, "Registration failed: Email already exists: " + email);
            return false;
        }
        
        try (Connection conn = DatabaseManager.getConnection()) {
            String hashedPassword = EncryptionUtils.hashPassword(password);
            String sql = "INSERT INTO users (username, email, password, full_name) VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, email);
                pstmt.setString(3, hashedPassword);
                pstmt.setString(4, fullName);
                pstmt.executeUpdate();
                logger.info("User registered successfully: " + username);
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error registering user: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Authenticate user login
     */
    public static User login(String username, String password) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? OR email = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, username);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String storedHash = rs.getString("password");
                        if (EncryptionUtils.verifyPassword(password, storedHash)) {
                            return new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("email"),
                                null, // Don't return password
                                rs.getString("full_name")
                            );
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error during login: " + e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * Get user by ID
     */
    public static User getUserById(int userId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM users WHERE id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            null,
                            rs.getString("full_name")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error getting user: " + e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * Check if username exists
     */
    public static boolean usernameExists(String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error checking username: " + e.getMessage(), e);
        }
        return false;
    }
    
    /**
     * Check if email exists
     */
    public static boolean emailExists(String email) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error checking email: " + e.getMessage(), e);
        }
        return false;
    }
    
    /**
     * Reset password for a user by email
     * @param email User's email address
     * @param newPassword New password to set
     * @return true if successful, false if email doesn't exist or validation fails
     */
    public static boolean resetPassword(String email, String newPassword) {
        // Validate email and password
        if (!ValidationUtils.isValidEmail(email) || !ValidationUtils.isValidPassword(newPassword)) {
            logger.log(Level.INFO, "Password reset failed: Invalid email or password format");
            return false;
        }
        
        // Check if email exists
        if (!emailExists(email)) {
            logger.log(Level.INFO, "Password reset failed: Email does not exist: " + email);
            return false;
        }
        
        try (Connection conn = DatabaseManager.getConnection()) {
            String hashedPassword = EncryptionUtils.hashPassword(newPassword);
            String sql = "UPDATE users SET password = ? WHERE email = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, hashedPassword);
                pstmt.setString(2, email);
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    logger.info("Password reset successfully for email: " + email);
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error resetting password: " + e.getMessage(), e);
        }
        return false;
    }
}
