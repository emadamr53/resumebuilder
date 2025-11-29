package managers;

import models.Resume;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager class for resume templates
 * @author habib
 */
public class TemplateManager {
    
    public enum TemplateType {
        MODERN,
        CLASSIC,
        CREATIVE,
        PROFESSIONAL
    }
    
    /**
     * Get formatted resume based on template
     */
    public static String formatResume(Resume resume, TemplateType template) {
        if (resume == null) return "";
        
        switch (template) {
            case MODERN:
                return formatModern(resume);
            case CLASSIC:
                return formatClassic(resume);
            case CREATIVE:
                return formatCreative(resume);
            case PROFESSIONAL:
                return formatProfessional(resume);
            default:
                return formatModern(resume);
        }
    }
    
    private static String formatModern(Resume resume) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔═══════════════════════════════════════╗\n");
        sb.append("║          RESUME - MODERN STYLE       ║\n");
        sb.append("╚═══════════════════════════════════════╝\n\n");
        
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("PERSONAL INFORMATION\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("Name: ").append(resume.getName() != null ? resume.getName() : "").append("\n");
        sb.append("Email: ").append(resume.getEmail() != null ? resume.getEmail() : "").append("\n");
        sb.append("Phone: ").append(resume.getPhone() != null ? resume.getPhone() : "").append("\n");
        sb.append("Address: ").append(resume.getAddress() != null ? resume.getAddress() : "").append("\n\n");
        
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("EDUCATION\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("Institution: ").append(resume.getInstitution() != null ? resume.getInstitution() : "").append("\n");
        sb.append("Degree: ").append(resume.getDegree() != null ? resume.getDegree() : "").append("\n");
        sb.append("Year: ").append(resume.getYear() != null ? resume.getYear() : "").append("\n\n");
        
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("EXPERIENCE\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("Job Title: ").append(resume.getJobTitle() != null ? resume.getJobTitle() : "").append("\n");
        sb.append("Company: ").append(resume.getCompany() != null ? resume.getCompany() : "").append("\n");
        sb.append("Duration: ").append(resume.getDuration() != null ? resume.getDuration() : "").append("\n");
        sb.append("Description: ").append(resume.getDescription() != null ? resume.getDescription() : "").append("\n\n");
        
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("SKILLS\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append(resume.getSkills() != null ? resume.getSkills() : "").append("\n");
        
        return sb.toString();
    }
    
    private static String formatClassic(Resume resume) {
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("                RESUME\n");
        sb.append("==========================================\n\n");
        
        sb.append("PERSONAL INFORMATION\n");
        sb.append("------------------------------------------\n");
        sb.append(resume.getName() != null ? resume.getName() : "").append("\n");
        sb.append(resume.getEmail() != null ? resume.getEmail() : "").append(" | ");
        sb.append(resume.getPhone() != null ? resume.getPhone() : "").append("\n");
        sb.append(resume.getAddress() != null ? resume.getAddress() : "").append("\n\n");
        
        sb.append("EDUCATION\n");
        sb.append("------------------------------------------\n");
        sb.append(resume.getDegree() != null ? resume.getDegree() : "").append(", ");
        sb.append(resume.getInstitution() != null ? resume.getInstitution() : "").append("\n");
        sb.append("Graduated: ").append(resume.getYear() != null ? resume.getYear() : "").append("\n\n");
        
        sb.append("EXPERIENCE\n");
        sb.append("------------------------------------------\n");
        sb.append(resume.getJobTitle() != null ? resume.getJobTitle() : "").append(" at ");
        sb.append(resume.getCompany() != null ? resume.getCompany() : "").append("\n");
        sb.append(resume.getDuration() != null ? resume.getDuration() : "").append("\n");
        sb.append(resume.getDescription() != null ? resume.getDescription() : "").append("\n\n");
        
        sb.append("SKILLS\n");
        sb.append("------------------------------------------\n");
        sb.append(resume.getSkills() != null ? resume.getSkills() : "").append("\n");
        
        return sb.toString();
    }
    
    private static String formatCreative(Resume resume) {
        StringBuilder sb = new StringBuilder();
        sb.append("╭───────────────────────────────────────╮\n");
        sb.append("│         ✨ CREATIVE RESUME ✨          │\n");
        sb.append("╰───────────────────────────────────────╯\n\n");
        
        sb.append("👤 ").append(resume.getName() != null ? resume.getName() : "").append("\n");
        sb.append("📧 ").append(resume.getEmail() != null ? resume.getEmail() : "").append("\n");
        sb.append("📱 ").append(resume.getPhone() != null ? resume.getPhone() : "").append("\n");
        sb.append("📍 ").append(resume.getAddress() != null ? resume.getAddress() : "").append("\n\n");
        
        sb.append("🎓 EDUCATION\n");
        sb.append("   • ").append(resume.getDegree() != null ? resume.getDegree() : "").append(" from ");
        sb.append(resume.getInstitution() != null ? resume.getInstitution() : "").append(" (").append(resume.getYear() != null ? resume.getYear() : "").append(")\n\n");
        
        sb.append("💼 EXPERIENCE\n");
        sb.append("   • ").append(resume.getJobTitle() != null ? resume.getJobTitle() : "").append(" @ ");
        sb.append(resume.getCompany() != null ? resume.getCompany() : "").append(" (").append(resume.getDuration() != null ? resume.getDuration() : "").append(")\n");
        sb.append("     ").append(resume.getDescription() != null ? resume.getDescription() : "").append("\n\n");
        
        sb.append("🛠️  SKILLS\n");
        sb.append("   ").append(resume.getSkills() != null ? resume.getSkills() : "").append("\n");
        
        return sb.toString();
    }
    
    private static String formatProfessional(Resume resume) {
        StringBuilder sb = new StringBuilder();
        sb.append("RESUME\n");
        sb.append("═══════════════════════════════════════════\n\n");
        
        sb.append(resume.getName() != null ? resume.getName().toUpperCase() : "").append("\n");
        sb.append(resume.getEmail() != null ? resume.getEmail() : "").append(" | ");
        sb.append(resume.getPhone() != null ? resume.getPhone() : "").append(" | ");
        sb.append(resume.getAddress() != null ? resume.getAddress() : "").append("\n\n");
        
        sb.append("EDUCATION\n");
        sb.append("─────────────────────────────────────────\n");
        sb.append(resume.getInstitution() != null ? resume.getInstitution() : "").append("\n");
        sb.append(resume.getDegree() != null ? resume.getDegree() : "").append(", ");
        sb.append(resume.getYear() != null ? resume.getYear() : "").append("\n\n");
        
        sb.append("PROFESSIONAL EXPERIENCE\n");
        sb.append("─────────────────────────────────────────\n");
        sb.append(resume.getJobTitle() != null ? resume.getJobTitle() : "").append("\n");
        sb.append(resume.getCompany() != null ? resume.getCompany() : "").append(" | ");
        sb.append(resume.getDuration() != null ? resume.getDuration() : "").append("\n");
        sb.append(resume.getDescription() != null ? resume.getDescription() : "").append("\n\n");
        
        sb.append("CORE COMPETENCIES\n");
        sb.append("─────────────────────────────────────────\n");
        sb.append(resume.getSkills() != null ? resume.getSkills() : "").append("\n");
        
        return sb.toString();
    }
    
    /**
     * Get list of available templates
     */
    public static List<String> getAvailableTemplates() {
        List<String> templates = new ArrayList<>();
        for (TemplateType type : TemplateType.values()) {
            templates.add(type.name());
        }
        return templates;
    }
}
