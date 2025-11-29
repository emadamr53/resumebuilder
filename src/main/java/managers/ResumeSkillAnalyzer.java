package managers;

import models.Resume;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manager class for analyzing resume skills
 * @author habib
 */
public class ResumeSkillAnalyzer {
    
    // Common skill categories
    private static final Map<String, List<String>> SKILL_CATEGORIES = new HashMap<>();
    
    static {
        SKILL_CATEGORIES.put("Programming", Arrays.asList(
            "Java", "Python", "C++", "C#", "JavaScript", "TypeScript", 
            "Ruby", "PHP", "Go", "Swift", "Kotlin"
        ));
        SKILL_CATEGORIES.put("Web Development", Arrays.asList(
            "HTML", "CSS", "React", "Angular", "Vue", "Node.js", 
            "Django", "Flask", "Spring", "Express"
        ));
        SKILL_CATEGORIES.put("Database", Arrays.asList(
            "SQL", "MySQL", "PostgreSQL", "MongoDB", "Oracle", 
            "SQLite", "Redis", "Cassandra"
        ));
        SKILL_CATEGORIES.put("Tools", Arrays.asList(
            "Git", "Docker", "Kubernetes", "Jenkins", "Maven", 
            "Gradle", "JIRA", "Confluence"
        ));
        SKILL_CATEGORIES.put("Cloud", Arrays.asList(
            "AWS", "Azure", "GCP", "Heroku", "DigitalOcean"
        ));
    }
    
    /**
     * Analyze skills from resume
     */
    public static Map<String, List<String>> analyzeSkills(Resume resume) {
        if (resume == null || resume.getSkills() == null) {
            return new HashMap<>();
        }
        
        String skillsText = resume.getSkills().toLowerCase();
        Map<String, List<String>> categorizedSkills = new HashMap<>();
        
        for (Map.Entry<String, List<String>> entry : SKILL_CATEGORIES.entrySet()) {
            List<String> foundSkills = new ArrayList<>();
            for (String skill : entry.getValue()) {
                if (skillsText.contains(skill.toLowerCase())) {
                    foundSkills.add(skill);
                }
            }
            if (!foundSkills.isEmpty()) {
                categorizedSkills.put(entry.getKey(), foundSkills);
            }
        }
        
        return categorizedSkills;
    }
    
    /**
     * Get skill count
     */
    public static int getSkillCount(Resume resume) {
        if (resume == null || resume.getSkills() == null) {
            return 0;
        }
        
        String skills = resume.getSkills();
        // Count skills separated by comma, semicolon, or newline
        return skills.split("[,;\\n]").length;
    }
    
    /**
     * Get skill recommendations based on existing skills
     */
    public static List<String> getSkillRecommendations(Resume resume) {
        Map<String, List<String>> categorized = analyzeSkills(resume);
        List<String> recommendations = new ArrayList<>();
        
        // Recommend skills from categories that have few skills
        for (Map.Entry<String, List<String>> entry : SKILL_CATEGORIES.entrySet()) {
            List<String> existing = categorized.getOrDefault(entry.getKey(), new ArrayList<>());
            if (existing.size() < 2) {
                // Recommend skills from this category
                for (String skill : entry.getValue()) {
                    if (!existing.contains(skill) && recommendations.size() < 5) {
                        recommendations.add(skill);
                    }
                }
            }
        }
        
        return recommendations;
    }
    
    /**
     * Check if resume has minimum required skills
     */
    public static boolean hasMinimumSkills(Resume resume, int minimum) {
        return getSkillCount(resume) >= minimum;
    }
    
    /**
     * Format skills for display
     */
    public static String formatSkillsForDisplay(Resume resume) {
        if (resume == null || resume.getSkills() == null) {
            return "No skills listed";
        }
        
        Map<String, List<String>> categorized = analyzeSkills(resume);
        if (categorized.isEmpty()) {
            return resume.getSkills();
        }
        
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : categorized.entrySet()) {
            sb.append(entry.getKey()).append(": ");
            sb.append(String.join(", ", entry.getValue()));
            sb.append("\n");
        }
        
        return sb.toString();
    }
}
