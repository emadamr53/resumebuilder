package managers;

import models.Resume;
import utils.DatabaseManager;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Resume Manager - Handles saving and loading resumes from database (per user)
 */
public class ResumeManager {
    private static final Logger logger = Logger.getLogger(ResumeManager.class.getName());
    private static Resume lastResume = null; // Cache for current session
    
    /**
     * Save resume for current user
     */
    public static boolean saveResume(Resume resume) {
        if (resume == null) {
            return false;
        }
        
        Integer userId = getCurrentUserId();
        if (userId == null) {
            logger.log(Level.WARNING, "Cannot save resume: No user logged in");
            return false;
        }
        
        try (Connection conn = DatabaseManager.getConnection()) {
            // Check if resume exists for this user
            String checkSql = "SELECT id FROM resumes WHERE user_id = ?";
            Integer resumeId = null;
            
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, userId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        resumeId = rs.getInt("id");
                    }
                }
            }
            
            if (resumeId != null) {
                // Update existing resume
                String updateSql = """
                    UPDATE resumes SET
                        name = ?, email = ?, phone = ?, address = ?,
                        institution = ?, degree = ?, year = ?,
                        job_title = ?, company = ?, duration = ?, description = ?,
                        skills = ?, updated_at = CURRENT_TIMESTAMP
                    WHERE id = ? AND user_id = ?
                """;
                
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    setResumeParameters(pstmt, resume);
                    pstmt.setInt(12, resumeId);
                    pstmt.setInt(13, userId);
                    
                    int rows = pstmt.executeUpdate();
                    if (rows > 0) {
                        lastResume = resume;
                        logger.info("Resume updated for user " + userId);
                        // Also save to saved_resumes folder as .txt file
                        saveResumeToFile(resume);
                        return true;
                    }
                }
            } else {
                // Insert new resume
                String insertSql = """
                    INSERT INTO resumes (
                        user_id, name, email, phone, address,
                        institution, degree, year,
                        job_title, company, duration, description, skills
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
                
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setInt(1, userId);
                    setResumeParameters(pstmt, resume);
                    
                    int rows = pstmt.executeUpdate();
                    if (rows > 0) {
                        lastResume = resume;
                        logger.info("Resume saved for user " + userId);
                        // Also save to saved_resumes folder as .txt file
                        saveResumeToFile(resume);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving resume: " + e.getMessage(), e);
        }
        return false;
    }
    
    /**
     * Get resume for current user
     */
    public static Resume getLastResume() {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            logger.log(Level.WARNING, "Cannot get resume: No user logged in");
            return null;
        }
        
        // Return cached resume if available
        if (lastResume != null) {
            return lastResume;
        }
        
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM resumes WHERE user_id = ? LIMIT 1";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Resume resume = new Resume();
                        resume.setName(rs.getString("name"));
                        resume.setEmail(rs.getString("email"));
                        resume.setPhone(rs.getString("phone"));
                        resume.setAddress(rs.getString("address"));
                        resume.setInstitution(rs.getString("institution"));
                        resume.setDegree(rs.getString("degree"));
                        resume.setYear(rs.getString("year"));
                        resume.setJobTitle(rs.getString("job_title"));
                        resume.setCompany(rs.getString("company"));
                        resume.setDuration(rs.getString("duration"));
                        resume.setDescription(rs.getString("description"));
                        resume.setSkills(rs.getString("skills"));
                        
                        lastResume = resume; // Cache it
                        logger.info("Resume loaded for user " + userId);
                        return resume;
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading resume: " + e.getMessage(), e);
        }
        
        return null;
    }
    
    /**
     * Clear cached resume (call when user logs out)
     */
    public static void clearCache() {
        lastResume = null;
    }
    
    /**
     * Get current user ID
     */
    private static Integer getCurrentUserId() {
        var currentUser = views.SessionManager.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getId();
        }
        return null;
    }
    
    /**
     * Helper method to set resume parameters in PreparedStatement
     */
    private static void setResumeParameters(PreparedStatement pstmt, Resume resume) throws SQLException {
        int paramIndex = 1;
        pstmt.setString(paramIndex++, resume.getName());
        pstmt.setString(paramIndex++, resume.getEmail());
        pstmt.setString(paramIndex++, resume.getPhone());
        pstmt.setString(paramIndex++, resume.getAddress());
        pstmt.setString(paramIndex++, resume.getInstitution());
        pstmt.setString(paramIndex++, resume.getDegree());
        pstmt.setString(paramIndex++, resume.getYear());
        pstmt.setString(paramIndex++, resume.getJobTitle());
        pstmt.setString(paramIndex++, resume.getCompany());
        pstmt.setString(paramIndex++, resume.getDuration());
        pstmt.setString(paramIndex++, resume.getDescription());
        pstmt.setString(paramIndex++, resume.getSkills());
    }
    
    /**
     * Save resume to saved_resumes folder as .txt file
     */
    private static void saveResumeToFile(Resume resume) {
        if (resume == null) {
            return;
        }
        
        try {
            // Get the project directory path
            // Try multiple methods to find the project root
            String projectDir = null;
            
            // Method 1: Check if current directory is the project (exact match or contains "AmrEmadResumeBuilder")
            File currentDir = new File(System.getProperty("user.dir"));
            String currentDirName = currentDir.getName();
            if (currentDirName.contains("AmrEmadResumeBuilder") || currentDirName.equals("AmrEmadResumeBuilder 3")) {
                projectDir = currentDir.getAbsolutePath();
                logger.info("Found project directory (Method 1): " + projectDir);
            } else {
                // Method 2: Look for saved_resumes folder in current or parent directories
                File searchDir = currentDir;
                for (int i = 0; i < 5 && searchDir != null; i++) {
                    File savedResumesTest = new File(searchDir, "saved_resumes");
                    String searchDirName = searchDir.getName();
                    if (savedResumesTest.exists() || searchDirName.contains("AmrEmadResumeBuilder")) {
                        projectDir = searchDir.getAbsolutePath();
                        logger.info("Found project directory (Method 2): " + projectDir);
                        break;
                    }
                    searchDir = searchDir.getParentFile();
                }
            }
            
            // Method 3: If still not found, try Desktop path
            if (projectDir == null) {
                String userHome = System.getProperty("user.home");
                File desktopProject = new File(userHome, "Desktop/AmrEmadResumeBuilder 3");
                if (desktopProject.exists()) {
                    projectDir = desktopProject.getAbsolutePath();
                    logger.info("Found project directory (Method 3): " + projectDir);
                }
            }
            
            // Method 4: Try to find any folder containing "AmrEmadResumeBuilder" on Desktop
            if (projectDir == null) {
                String userHome = System.getProperty("user.home");
                File desktop = new File(userHome, "Desktop");
                if (desktop.exists() && desktop.isDirectory()) {
                    File[] desktopFiles = desktop.listFiles();
                    if (desktopFiles != null) {
                        for (File file : desktopFiles) {
                            if (file.isDirectory() && file.getName().contains("AmrEmadResumeBuilder")) {
                                projectDir = file.getAbsolutePath();
                                logger.info("Found project directory (Method 4): " + projectDir);
                                break;
                            }
                        }
                    }
                }
            }
            
            // Fallback: Use current directory
            if (projectDir == null) {
                projectDir = System.getProperty("user.dir");
            }
            
            // Create saved_resumes folder path
            File savedResumesDir = new File(projectDir, "saved_resumes");
            if (!savedResumesDir.exists()) {
                boolean created = savedResumesDir.mkdirs();
                if (created) {
                    logger.info("Created saved_resumes directory: " + savedResumesDir.getAbsolutePath());
                    System.out.println("📁 Created saved_resumes folder: " + savedResumesDir.getAbsolutePath());
                } else {
                    logger.warning("Could not create saved_resumes directory");
                }
            }
            
            // Log the save location
            System.out.println("📂 Saving to: " + savedResumesDir.getAbsolutePath());
            
            // Create safe filename
            String safeName = (resume.getName() != null && !resume.getName().trim().isEmpty()) 
                ? resume.getName().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase()
                : "resume";
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String fileName = safeName + "_resume_" + dateStr + ".txt";
            File resumeFile = new File(savedResumesDir, fileName);
            
            // Format and write resume as text
            try (PrintWriter writer = new PrintWriter(new FileWriter(resumeFile, false))) {
                writer.println("═══════════════════════════════════════════════════════════");
                writer.println("                    RESUME");
                writer.println("═══════════════════════════════════════════════════════════");
                writer.println();
                
                // Personal Information
                if (resume.getName() != null && !resume.getName().trim().isEmpty()) {
                    writer.println("NAME: " + resume.getName().toUpperCase());
                    writer.println();
                }
                
                writer.println("CONTACT INFORMATION:");
                writer.println("───────────────────────────────────────────────────────────");
                if (resume.getEmail() != null && !resume.getEmail().trim().isEmpty()) {
                    writer.println("Email: " + resume.getEmail());
                }
                if (resume.getPhone() != null && !resume.getPhone().trim().isEmpty()) {
                    writer.println("Phone: " + resume.getPhone());
                }
                if (resume.getAddress() != null && !resume.getAddress().trim().isEmpty()) {
                    writer.println("Address: " + resume.getAddress());
                }
                writer.println();
                
                // Education
                if (resume.getDegree() != null && !resume.getDegree().trim().isEmpty()) {
                    writer.println("EDUCATION:");
                    writer.println("───────────────────────────────────────────────────────────");
                    writer.println("Degree: " + resume.getDegree());
                    if (resume.getInstitution() != null && !resume.getInstitution().trim().isEmpty()) {
                        writer.println("Institution: " + resume.getInstitution());
                    }
                    if (resume.getYear() != null && !resume.getYear().trim().isEmpty()) {
                        writer.println("Year: " + resume.getYear());
                    }
                    writer.println();
                }
                
                // Experience
                if (resume.getJobTitle() != null && !resume.getJobTitle().trim().isEmpty()) {
                    writer.println("PROFESSIONAL EXPERIENCE:");
                    writer.println("───────────────────────────────────────────────────────────");
                    writer.println("Job Title: " + resume.getJobTitle());
                    if (resume.getCompany() != null && !resume.getCompany().trim().isEmpty()) {
                        writer.println("Company: " + resume.getCompany());
                    }
                    if (resume.getDuration() != null && !resume.getDuration().trim().isEmpty()) {
                        writer.println("Duration: " + resume.getDuration());
                    }
                    if (resume.getDescription() != null && !resume.getDescription().trim().isEmpty()) {
                        writer.println("Description: " + resume.getDescription());
                    }
                    writer.println();
                }
                
                // Skills
                if (resume.getSkills() != null && !resume.getSkills().trim().isEmpty()) {
                    writer.println("SKILLS:");
                    writer.println("───────────────────────────────────────────────────────────");
                    String[] skills = resume.getSkills().split(",");
                    for (String skill : skills) {
                        if (!skill.trim().isEmpty()) {
                            writer.println("• " + skill.trim());
                        }
                    }
                    writer.println();
                }
                
                writer.println("═══════════════════════════════════════════════════════════");
                writer.println("Generated: " + java.time.LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.println("═══════════════════════════════════════════════════════════");
            }
            
            logger.info("Resume saved to file: " + resumeFile.getAbsolutePath());
            System.out.println("✅ Resume saved to: " + resumeFile.getAbsolutePath());
            
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error saving resume to file: " + e.getMessage(), e);
            System.err.println("Warning: Could not save resume to file: " + e.getMessage());
        }
    }
}
