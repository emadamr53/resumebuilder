package utils;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for file storage operations
 * @author habib
 */
public class FileStorage {
    private static final String BASE_DIR = "resume_data";
    private static final String RESUMES_DIR = BASE_DIR + "/resumes";
    private static final String EXPORTS_DIR = BASE_DIR + "/exports";
    private static final String TEMPLATES_DIR = BASE_DIR + "/templates";
    
    static {
        initializeDirectories();
    }
    
    /**
     * Initialize all required directories
     */
    private static void initializeDirectories() {
        try {
            Files.createDirectories(Paths.get(RESUMES_DIR));
            Files.createDirectories(Paths.get(EXPORTS_DIR));
            Files.createDirectories(Paths.get(TEMPLATES_DIR));
        } catch (IOException e) {
            System.err.println("Error creating directories: " + e.getMessage());
        }
    }
    
    /**
     * Save content to a file
     */
    public static boolean saveFile(String content, String fileName, String subDirectory) {
        try {
            Path dir = Paths.get(BASE_DIR, subDirectory);
            Files.createDirectories(dir);
            Path file = dir.resolve(fileName);
            Files.write(file, content.getBytes());
            return true;
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Read content from a file
     */
    public static String readFile(String fileName, String subDirectory) {
        try {
            Path file = Paths.get(BASE_DIR, subDirectory, fileName);
            if (Files.exists(file)) {
                return new String(Files.readAllBytes(file));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Delete a file
     */
    public static boolean deleteFile(String fileName, String subDirectory) {
        try {
            Path file = Paths.get(BASE_DIR, subDirectory, fileName);
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            System.err.println("Error deleting file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * List all files in a directory
     */
    public static List<String> listFiles(String subDirectory) {
        List<String> files = new ArrayList<>();
        try {
            Path dir = Paths.get(BASE_DIR, subDirectory);
            if (Files.exists(dir)) {
                Files.list(dir).forEach(path -> files.add(path.getFileName().toString()));
            }
        } catch (IOException e) {
            System.err.println("Error listing files: " + e.getMessage());
        }
        return files;
    }
    
    /**
     * Check if a file exists
     */
    public static boolean fileExists(String fileName, String subDirectory) {
        Path file = Paths.get(BASE_DIR, subDirectory, fileName);
        return Files.exists(file);
    }
    
    /**
     * Get the full path to a file
     */
    public static String getFilePath(String fileName, String subDirectory) {
        return Paths.get(BASE_DIR, subDirectory, fileName).toString();
    }
}
