package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for encryption and hashing operations
 * @author habib
 */
public class EncryptionUtils {
    
    /**
     * Hash a password using SHA-256
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Verify a password against a hash
     */
    public static boolean verifyPassword(String password, String hash) {
        String passwordHash = hashPassword(password);
        return passwordHash != null && passwordHash.equals(hash);
    }
    
    /**
     * Simple XOR encryption (for basic obfuscation, not secure for production)
     */
    public static String encrypt(String data, String key) {
        if (data == null || key == null) return null;
        StringBuilder encrypted = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            encrypted.append((char) (data.charAt(i) ^ key.charAt(i % key.length())));
        }
        return Base64.getEncoder().encodeToString(encrypted.toString().getBytes());
    }
    
    /**
     * Simple XOR decryption
     */
    public static String decrypt(String encryptedData, String key) {
        if (encryptedData == null || key == null) return null;
        try {
            String data = new String(Base64.getDecoder().decode(encryptedData));
            StringBuilder decrypted = new StringBuilder();
            for (int i = 0; i < data.length(); i++) {
                decrypted.append((char) (data.charAt(i) ^ key.charAt(i % key.length())));
            }
            return decrypted.toString();
        } catch (Exception e) {
            System.err.println("Error decrypting data: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Generate a simple key from a string
     */
    public static String generateKey(String seed) {
        return hashPassword(seed);
    }
}
