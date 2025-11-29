package models;

import java.io.Serializable;

/**
 * CV Theme Settings - Stores all customization options for CV themes
 */
public class CVThemeSettings implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Theme selection
    private CVTheme baseTheme = CVTheme.PROFESSIONAL;
    
    // Font settings
    private String headerFont = "Georgia";
    private String bodyFont = "Segoe UI";
    private int headerFontSize = 32;
    private int sectionFontSize = 14;
    private int bodyFontSize = 12;
    
    // Color settings
    private String primaryColor = "#3498db";      // Main accent color
    private String secondaryColor = "#e74c3c";    // Secondary accent
    private String tertiaryColor = "#27ae60";     // Tertiary accent
    private String headerTextColor = "#2c3e50";    // Header text color
    private String bodyTextColor = "#555555";     // Body text color
    private String backgroundColor = "#ffffff";    // Paper background
    private String dividerColor = "#3498db";      // Divider line color
    
    // Layout settings
    private double paperWidth = 650;
    private double padding = 50;
    private double sectionSpacing = 25;
    
    // Style options
    private boolean useIcons = true;
    private boolean useBorders = true;
    private String borderStyle = "solid"; // solid, dashed, none
    
    // Default constructor
    public CVThemeSettings() {
        // Use defaults
    }
    
    // Constructor with base theme
    public CVThemeSettings(CVTheme baseTheme) {
        this.baseTheme = baseTheme;
        applyThemeDefaults(baseTheme);
    }
    
    private void applyThemeDefaults(CVTheme theme) {
        switch (theme) {
            case CLASSIC:
                headerFont = "Times New Roman";
                bodyFont = "Times New Roman";
                headerFontSize = 28;
                primaryColor = "#8B7355";
                secondaryColor = "#5d4e37";
                backgroundColor = "#faf8f3";
                dividerColor = "#8B7355";
                break;
            case MODERN:
                headerFont = "Segoe UI";
                bodyFont = "Segoe UI";
                headerFontSize = 36;
                primaryColor = "#667eea";
                secondaryColor = "#764ba2";
                backgroundColor = "#ffffff";
                dividerColor = "#667eea";
                break;
            case CREATIVE:
                headerFont = "Arial Black";
                bodyFont = "Segoe UI";
                headerFontSize = 30;
                primaryColor = "#e74c3c";
                secondaryColor = "#f39c12";
                backgroundColor = "#fff5f5";
                dividerColor = "#e74c3c";
                break;
            case PROFESSIONAL:
            default:
                headerFont = "Georgia";
                bodyFont = "Segoe UI";
                headerFontSize = 32;
                primaryColor = "#3498db";
                secondaryColor = "#e74c3c";
                tertiaryColor = "#27ae60";
                backgroundColor = "#ffffff";
                dividerColor = "#3498db";
                break;
        }
    }
    
    // Getters and Setters
    public CVTheme getBaseTheme() { return baseTheme; }
    public void setBaseTheme(CVTheme baseTheme) { 
        this.baseTheme = baseTheme;
        applyThemeDefaults(baseTheme);
    }
    
    public String getHeaderFont() { return headerFont; }
    public void setHeaderFont(String headerFont) { this.headerFont = headerFont; }
    
    public String getBodyFont() { return bodyFont; }
    public void setBodyFont(String bodyFont) { this.bodyFont = bodyFont; }
    
    public int getHeaderFontSize() { return headerFontSize; }
    public void setHeaderFontSize(int headerFontSize) { this.headerFontSize = headerFontSize; }
    
    public int getSectionFontSize() { return sectionFontSize; }
    public void setSectionFontSize(int sectionFontSize) { this.sectionFontSize = sectionFontSize; }
    
    public int getBodyFontSize() { return bodyFontSize; }
    public void setBodyFontSize(int bodyFontSize) { this.bodyFontSize = bodyFontSize; }
    
    public String getPrimaryColor() { return primaryColor; }
    public void setPrimaryColor(String primaryColor) { this.primaryColor = primaryColor; }
    
    public String getSecondaryColor() { return secondaryColor; }
    public void setSecondaryColor(String secondaryColor) { this.secondaryColor = secondaryColor; }
    
    public String getTertiaryColor() { return tertiaryColor; }
    public void setTertiaryColor(String tertiaryColor) { this.tertiaryColor = tertiaryColor; }
    
    public String getHeaderTextColor() { return headerTextColor; }
    public void setHeaderTextColor(String headerTextColor) { this.headerTextColor = headerTextColor; }
    
    public String getBodyTextColor() { return bodyTextColor; }
    public void setBodyTextColor(String bodyTextColor) { this.bodyTextColor = bodyTextColor; }
    
    public String getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(String backgroundColor) { this.backgroundColor = backgroundColor; }
    
    public String getDividerColor() { return dividerColor; }
    public void setDividerColor(String dividerColor) { this.dividerColor = dividerColor; }
    
    public double getPaperWidth() { return paperWidth; }
    public void setPaperWidth(double paperWidth) { this.paperWidth = paperWidth; }
    
    public double getPadding() { return padding; }
    public void setPadding(double padding) { this.padding = padding; }
    
    public double getSectionSpacing() { return sectionSpacing; }
    public void setSectionSpacing(double sectionSpacing) { this.sectionSpacing = sectionSpacing; }
    
    public boolean isUseIcons() { return useIcons; }
    public void setUseIcons(boolean useIcons) { this.useIcons = useIcons; }
    
    public boolean isUseBorders() { return useBorders; }
    public void setUseBorders(boolean useBorders) { this.useBorders = useBorders; }
    
    public String getBorderStyle() { return borderStyle; }
    public void setBorderStyle(String borderStyle) { this.borderStyle = borderStyle; }
}




