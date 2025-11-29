package utils;

import java.util.regex.Pattern;

/**
 * Utility class for input validation
 * @author habib
 */
public class ValidationUtils {
    
    // Email pattern
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    // Phone pattern (allows various formats)
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[\\d\\s\\-\\(\\)\\+]+$");
    
    /**
     * Validate email address
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate phone number
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        String cleaned = phone.replaceAll("[\\s\\-\\(\\)]", "");
        return PHONE_PATTERN.matcher(phone).matches() && cleaned.length() >= 10;
    }
    
    /**
     * Validate that a string is not empty
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Validate that a string has minimum length
     */
    public static boolean hasMinLength(String value, int minLength) {
        return value != null && value.trim().length() >= minLength;
    }
    
    /**
     * Validate that a string has maximum length
     */
    public static boolean hasMaxLength(String value, int maxLength) {
        return value == null || value.length() <= maxLength;
    }
    
    /**
     * Validate username (alphanumeric and underscore, 3-20 chars)
     */
    public static boolean isValidUsername(String username) {
        if (username == null) return false;
        return username.matches("^[a-zA-Z0-9_]{3,20}$");
    }
    
    /**
     * Validate password strength (at least 6 characters)
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    /**
     * Validate year format (4 digits)
     */
    public static boolean isValidYear(String year) {
        if (year == null || year.trim().isEmpty()) return false;
        try {
            int yearInt = Integer.parseInt(year.trim());
            return yearInt >= 1900 && yearInt <= 2100;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Sanitize input to prevent basic injection
     */
    public static String sanitize(String input) {
        if (input == null) return "";
        return input.trim()
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;");
    }
    
    /**
     * Validate resume data
     */
    public static boolean validateResumeData(String name, String email, String phone) {
        return isNotEmpty(name) && isValidEmail(email) && isValidPhone(phone);
    }
}
