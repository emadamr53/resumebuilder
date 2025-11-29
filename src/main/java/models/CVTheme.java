package models;

/**
 * CV Theme Enum - Defines available resume themes
 */
public enum CVTheme {
    CLASSIC("Classic", "Traditional and timeless design"),
    MODERN("Modern", "Clean and contemporary style"),
    PROFESSIONAL("Professional", "Corporate and formal layout"),
    CREATIVE("Creative", "Bold and artistic presentation");
    
    private final String displayName;
    private final String description;
    
    CVTheme(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
}




