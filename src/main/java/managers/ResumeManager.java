package managers;

import models.Resume;
import utils.DatabaseManager;
import views.SessionManager;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
}
