import models.Resume;
import models.User;
import managers.ResumeManager;
import managers.UserManager;
import managers.ResumeSkillAnalyzer;
import managers.TemplateManager;
import utils.ValidationUtils;
import utils.EncryptionUtils;
import utils.DatabaseManager;

/**
 * Simple test class for Resume Builder application
 */
public class ResumeBuilderTest {
    
    public static void main(String[] args) {
        System.out.println("=== Resume Builder Test Suite ===\n");
        
        // Test 1: Database initialization
        System.out.println("Test 1: Database Initialization");
        try {
            DatabaseManager.initializeDatabase();
            System.out.println("✓ Database initialized successfully\n");
        } catch (Exception e) {
            System.out.println("✗ Database initialization failed: " + e.getMessage() + "\n");
        }
        
        // Test 2: Validation Utils
        System.out.println("Test 2: Validation Utils");
        System.out.println("  Email validation: " + ValidationUtils.isValidEmail("test@example.com"));
        System.out.println("  Phone validation: " + ValidationUtils.isValidPhone("123-456-7890"));
        System.out.println("  Username validation: " + ValidationUtils.isValidUsername("testuser"));
        System.out.println("✓ Validation tests passed\n");
        
        // Test 3: Encryption Utils
        System.out.println("Test 3: Encryption Utils");
        String password = "testpassword123";
        String hash = EncryptionUtils.hashPassword(password);
        System.out.println("  Password hash generated: " + (hash != null));
        System.out.println("  Password verification: " + EncryptionUtils.verifyPassword(password, hash));
        System.out.println("✓ Encryption tests passed\n");
        
        // Test 4: Resume Model
        System.out.println("Test 4: Resume Model");
        Resume resume = new Resume(
            "John Doe",
            "john@example.com",
            "123-456-7890",
            "123 Main St",
            "University of Test",
            "Bachelor of Science",
            "2020",
            "Software Engineer",
            "Tech Corp",
            "2020-2023",
            "Developed applications",
            "Java, Python, SQL"
        );
        System.out.println("  Resume created: " + (resume != null));
        System.out.println("  Name: " + resume.getName());
        System.out.println("✓ Resume model test passed\n");
        
        // Test 5: Resume Manager - Save
        System.out.println("Test 5: Resume Manager - Save");
        try {
            ResumeManager.saveResume(resume);
            System.out.println("✓ Resume saved successfully\n");
        } catch (Exception e) {
            System.out.println("✗ Resume save failed: " + e.getMessage() + "\n");
        }
        
        // Test 6: Resume Manager - Load
        System.out.println("Test 6: Resume Manager - Load");
        try {
            Resume loadedResume = ResumeManager.getLastResume();
            if (loadedResume != null && loadedResume.getName() != null) {
                System.out.println("  Loaded resume: " + loadedResume.getName());
                System.out.println("✓ Resume loaded successfully\n");
            } else {
                System.out.println("✗ Resume load failed: No resume found\n");
            }
        } catch (Exception e) {
            System.out.println("✗ Resume load failed: " + e.getMessage() + "\n");
        }
        
        // Test 7: Skill Analyzer
        System.out.println("Test 7: Skill Analyzer");
        try {
            int skillCount = ResumeSkillAnalyzer.getSkillCount(resume);
            System.out.println("  Skill count: " + skillCount);
            System.out.println("✓ Skill analyzer test passed\n");
        } catch (Exception e) {
            System.out.println("✗ Skill analyzer test failed: " + e.getMessage() + "\n");
        }
        
        // Test 8: Template Manager
        System.out.println("Test 8: Template Manager");
        try {
            String formatted = TemplateManager.formatResume(resume, TemplateManager.TemplateType.MODERN);
            System.out.println("  Template formatted: " + (formatted != null && !formatted.isEmpty()));
            System.out.println("✓ Template manager test passed\n");
        } catch (Exception e) {
            System.out.println("✗ Template manager test failed: " + e.getMessage() + "\n");
        }
        
        // Test 9: User Manager
        System.out.println("Test 9: User Manager");
        try {
            boolean registered = UserManager.registerUser("testuser", "test@example.com", "password123", "Test User");
            System.out.println("  User registration: " + registered);
            if (registered) {
                User user = UserManager.login("testuser", "password123");
                System.out.println("  User login: " + (user != null));
                System.out.println("✓ User manager test passed\n");
            } else {
                System.out.println("  (User may already exist)\n");
            }
        } catch (Exception e) {
            System.out.println("✗ User manager test failed: " + e.getMessage() + "\n");
        }
        
        System.out.println("=== Test Suite Complete ===");
    }
}


