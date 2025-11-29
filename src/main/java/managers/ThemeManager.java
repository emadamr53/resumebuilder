package managers;

import models.CVTheme;
import java.util.prefs.Preferences;

/**
 * Theme Manager - Handles CV theme selection and storage
 */
public class ThemeManager {
    private static final String THEME_PREF_KEY = "selected_cv_theme";
    private static final Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
    private static CVTheme currentTheme = CVTheme.PROFESSIONAL; // Default theme
    
    /**
     * Theme Colors for UI theming (backward compatibility)
     */
    public static class ThemeColors {
        public String background;
        public String bgPrimary;
        public String bgSecondary;
        public String bgCard;
        public String bgHover;
        public String cardBackground;
        public String textPrimary;
        public String textSecondary;
        public String textMuted;
        public String primary;
        public String secondary;
        public String accentPrimary;
        public String accentSecondary;
        public String border;
        public String success;
        public String error;
        public String warning;
        
        // Constructor for dark theme
        public ThemeColors() {
            setDarkTheme();
        }
        
        public void setDarkTheme() {
            background = "#0f0f23";
            bgPrimary = "#0f0f23";
            bgSecondary = "#1a1a2e";
            bgCard = "#1a1a2e";
            bgHover = "#2a2a3e";
            cardBackground = "#1a1a2e";
            textPrimary = "#ffffff";
            textSecondary = "#888888";
            textMuted = "#666666";
            primary = "#667eea";
            secondary = "#764ba2";
            accentPrimary = "#667eea";
            accentSecondary = "#764ba2";
            border = "#2a2a3e";
            success = "#27ae60";
            error = "#e74c3c";
            warning = "#f39c12";
        }
        
        public void setLightTheme() {
            background = "#f5f5f5";
            bgPrimary = "#ffffff";
            bgSecondary = "#f0f0f0";
            bgCard = "#ffffff";
            bgHover = "#e8e8e8";
            cardBackground = "#ffffff";
            textPrimary = "#2c3e50";
            textSecondary = "#555555";
            textMuted = "#777777";
            primary = "#667eea";
            secondary = "#764ba2";
            accentPrimary = "#667eea";
            accentSecondary = "#764ba2";
            border = "#dddddd";
            success = "#27ae60";
            error = "#e74c3c";
            warning = "#f39c12";
        }
    }
    
    /**
     * UI Theme enum (for backward compatibility)
     */
    public enum Theme {
        DARK, LIGHT
    }
    
    private static Theme uiTheme = Theme.DARK;
    private static ThemeColors darkColors = new ThemeColors();
    private static ThemeColors lightColors = new ThemeColors();
    
    static {
        lightColors.setLightTheme();
    }
    
    /**
     * Get current UI theme (for backward compatibility)
     */
    public static Theme getCurrentTheme() {
        return uiTheme;
    }
    
    /**
     * Set UI theme (for backward compatibility)
     */
    public static void setCurrentTheme(Theme theme) {
        uiTheme = theme;
    }
    
    /**
     * Toggle UI theme (for backward compatibility)
     */
    public static void toggleTheme() {
        uiTheme = (uiTheme == Theme.DARK) ? Theme.LIGHT : Theme.DARK;
    }
    
    /**
     * Get UI theme colors (for backward compatibility)
     */
    public static ThemeColors getColors() {
        return (uiTheme == Theme.DARK) ? darkColors : lightColors;
    }
    
    /**
     * Get the currently selected theme
     */
    public static CVTheme getSelectedTheme() {
        try {
            String themeName = prefs.get(THEME_PREF_KEY, currentTheme.name());
            currentTheme = CVTheme.valueOf(themeName);
        } catch (IllegalArgumentException e) {
            // If stored theme is invalid, use default
            currentTheme = CVTheme.PROFESSIONAL;
        }
        return currentTheme;
    }
    
    /**
     * Set the selected theme
     */
    public static void setSelectedTheme(CVTheme theme) {
        if (theme != null) {
            currentTheme = theme;
            prefs.put(THEME_PREF_KEY, theme.name());
            try {
                prefs.flush();
            } catch (Exception e) {
                System.err.println("Error saving theme preference: " + e.getMessage());
            }
        }
    }
    
    /**
     * Reset to default theme
     */
    public static void resetTheme() {
        setSelectedTheme(CVTheme.PROFESSIONAL);
    }
}
