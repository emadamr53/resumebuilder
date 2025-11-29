package views;

import models.User;

/**
 * Session Manager - Handles user session state
 */
public class SessionManager {
    private static User currentUser = null;
    
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public static void logout() {
        // Clear resume cache and theme settings cache when logging out
        managers.ResumeManager.clearCache();
        managers.ThemeSettingsManager.clearCache();
        currentUser = null;
    }
    
    public static String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }
    
    public static String getCurrentUserFullName() {
        if (currentUser != null && currentUser.getFullName() != null) {
            return currentUser.getFullName();
        }
        return currentUser != null ? currentUser.getUsername() : "Guest";
    }
}
