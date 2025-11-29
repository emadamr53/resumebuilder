package managers;

import models.Resume;
import java.io.*;

/**
 * Word Export Manager - Exports resume to Word format with text content
 */
public class WordExportManager {
    
    /**
     * Helper method to repeat a string (Java 8 compatible)
     */
    private static String repeat(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
    
    public static void exportToWord(Resume resume) {
        exportToWord(resume, "exported_resume.txt");
    }
    
    public static boolean exportToWord(Resume resume, String filePath) {
        // Save as .txt file (can be opened in Word)
        if (!filePath.toLowerCase().endsWith(".txt")) {
            if (filePath.endsWith(".docx") || filePath.endsWith(".doc")) {
                filePath = filePath.substring(0, filePath.lastIndexOf('.')) + ".txt";
            } else {
                filePath = filePath + ".txt";
            }
        }
        
        System.out.println("=== Starting Word Export (as Text) ===");
        System.out.println("Target file: " + filePath);
        
        // Export as text file
        return exportToWordAsText(resume, filePath);
    }
    /**
     * Export as plain text (can be opened in Word)
     */
    private static boolean exportToWordAsText(Resume resume, String filePath) {
        try {
            System.out.println("=== Exporting as Text File ===");
            
            // Ensure .txt extension
            if (!filePath.toLowerCase().endsWith(".txt")) {
                if (filePath.endsWith(".docx")) {
                    filePath = filePath.substring(0, filePath.length() - 5) + ".txt";
                } else if (filePath.endsWith(".doc")) {
                    filePath = filePath.substring(0, filePath.length() - 4) + ".txt";
                } else {
                    filePath = filePath + ".txt";
                }
            }
            
            // Create parent directories
            java.io.File file = new java.io.File(filePath);
            java.io.File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            // Write resume as formatted text
            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
                writer.println(repeat("=", 60));
                writer.println("                    RESUME");
                writer.println(repeat("=", 60));
                writer.println();
                
                if (resume.getName() != null && !resume.getName().isEmpty()) {
                    writer.println("NAME: " + resume.getName().toUpperCase());
                    writer.println();
                }
                
                writer.println("CONTACT INFORMATION:");
                writer.println(repeat("-", 60));
                if (resume.getEmail() != null && !resume.getEmail().isEmpty()) {
                    writer.println("Email: " + resume.getEmail());
                }
                if (resume.getPhone() != null && !resume.getPhone().isEmpty()) {
                    writer.println("Phone: " + resume.getPhone());
                }
                if (resume.getAddress() != null && !resume.getAddress().isEmpty()) {
                    writer.println("Address: " + resume.getAddress());
                }
                writer.println();
                
                if (resume.getDegree() != null && !resume.getDegree().isEmpty()) {
                    writer.println("EDUCATION:");
                    writer.println(repeat("-", 60));
                    writer.println("Degree: " + resume.getDegree());
                    if (resume.getInstitution() != null && !resume.getInstitution().isEmpty()) {
                        writer.println("Institution: " + resume.getInstitution());
                    }
                    if (resume.getYear() != null && !resume.getYear().isEmpty()) {
                        writer.println("Year: " + resume.getYear());
                    }
                    writer.println();
                }
                
                if (resume.getJobTitle() != null && !resume.getJobTitle().isEmpty()) {
                    writer.println("PROFESSIONAL EXPERIENCE:");
                    writer.println(repeat("-", 60));
                    writer.println("Job Title: " + resume.getJobTitle());
                    if (resume.getCompany() != null && !resume.getCompany().isEmpty()) {
                        writer.println("Company: " + resume.getCompany());
                    }
                    if (resume.getDuration() != null && !resume.getDuration().isEmpty()) {
                        writer.println("Duration: " + resume.getDuration());
                    }
                    if (resume.getDescription() != null && !resume.getDescription().isEmpty()) {
                        writer.println("Description: " + resume.getDescription());
                    }
                    writer.println();
                }
                
                if (resume.getSkills() != null && !resume.getSkills().isEmpty()) {
                    writer.println("SKILLS:");
                    writer.println(repeat("-", 60));
                    String[] skills = resume.getSkills().split(",");
                    for (String skill : skills) {
                        writer.println("• " + skill.trim());
                    }
                }
                
                writer.println();
                writer.println(repeat("=", 60));
            }
            
            // Verify file
            java.io.File savedFile = new java.io.File(filePath);
            if (savedFile.exists() && savedFile.length() > 0) {
                System.out.println("=== SUCCESS (Text Format) ===");
                System.out.println("File: " + filePath);
                System.out.println("Size: " + savedFile.length() + " bytes");
                System.out.println("Note: This is a .txt file that can be opened in Word");
                return true;
            } else {
                System.err.println("Failed to create text file");
                return false;
            }
            
        } catch (IOException e) {
            System.err.println("Error creating text file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
