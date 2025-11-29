package managers;

import models.CVThemeSettings;
import models.CVTheme;
import java.util.prefs.Preferences;
import java.util.Base64;
import java.io.*;

/**
 * Theme Settings Manager - Handles saving and loading custom theme settings
 */
public class ThemeSettingsManager {
    private static final String SETTINGS_PREF_KEY_PREFIX = "cv_theme_settings_";
    private static final Preferences prefs = Preferences.userNodeForPackage(ThemeSettingsManager.class);
    private static CVThemeSettings currentSettings = null;
    
    /**
     * Get settings key for current user
     */
    private static String getSettingsKey() {
        var currentUser = views.SessionManager.getCurrentUser();
        int userId = currentUser != null ? currentUser.getId() : 0;
        return SETTINGS_PREF_KEY_PREFIX + userId;
    }
    
    private static int lastUserId = -1;
    
    /**
     * Get current theme settings
     */
    public static CVThemeSettings getSettings() {
        var currentUser = views.SessionManager.getCurrentUser();
        int currentUserId = currentUser != null ? currentUser.getId() : 0;
        
        // If user changed, clear cache
        if (currentUserId != lastUserId) {
            currentSettings = null;
            lastUserId = currentUserId;
        }
        
        if (currentSettings == null) {
            currentSettings = loadSettings();
            if (currentSettings == null) {
                // Create default settings based on selected theme
                CVTheme theme = ThemeManager.getSelectedTheme();
                currentSettings = new CVThemeSettings(theme);
            }
        }
        return currentSettings;
    }
    
    /**
     * Save theme settings
     */
    public static void saveSettings(CVThemeSettings settings) {
        if (settings != null) {
            currentSettings = settings;
            try {
                // Serialize to string
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(settings);
                oos.close();
                
                String encoded = Base64.getEncoder().encodeToString(baos.toByteArray());
                String key = getSettingsKey();
                prefs.put(key, encoded);
                prefs.flush();
            } catch (Exception e) {
                System.err.println("Error saving theme settings: " + e.getMessage());
            }
        }
    }
    
    /**
     * Load theme settings
     */
    private static CVThemeSettings loadSettings() {
        try {
            String key = getSettingsKey();
            String encoded = prefs.get(key, null);
            if (encoded != null && !encoded.isEmpty()) {
                byte[] data = Base64.getDecoder().decode(encoded);
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                ObjectInputStream ois = new ObjectInputStream(bais);
                CVThemeSettings settings = (CVThemeSettings) ois.readObject();
                ois.close();
                return settings;
            }
        } catch (Exception e) {
            System.err.println("Error loading theme settings: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Reset to default settings
     */
    public static void resetSettings() {
        CVTheme theme = ThemeManager.getSelectedTheme();
        currentSettings = new CVThemeSettings(theme);
        saveSettings(currentSettings);
    }
    
    /**
     * Update settings when theme changes
     */
    public static void updateTheme(CVTheme theme) {
        CVThemeSettings settings = getSettings();
        settings.setBaseTheme(theme);
        saveSettings(settings);
    }
    
    /**
     * Clear cached settings (call when user logs out)
     */
    public static void clearCache() {
        currentSettings = null;
        lastUserId = -1;
    }
}

