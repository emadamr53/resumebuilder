package managers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AI-powered Job Suggestion Manager
 * Analyzes skills and suggests relevant jobs and companies
 */
public class AIJobSuggestionManager {
    private static final Logger logger = Logger.getLogger(AIJobSuggestionManager.class.getName());
    
    // Skill to Job mapping database
    private static final Map<String, List<String>> SKILL_TO_JOBS = new HashMap<>();
    
    static {
        // Programming Languages
        SKILL_TO_JOBS.put("java", List.of("Java Developer", "Backend Developer", "Software Engineer", "Android Developer", "Full Stack Developer"));
        SKILL_TO_JOBS.put("python", List.of("Python Developer", "Data Scientist", "Machine Learning Engineer", "Backend Developer", "DevOps Engineer"));
        SKILL_TO_JOBS.put("javascript", List.of("Frontend Developer", "Full Stack Developer", "React Developer", "Node.js Developer", "Web Developer"));
        SKILL_TO_JOBS.put("c++", List.of("Systems Programmer", "Game Developer", "Embedded Systems Engineer", "Software Engineer", "Performance Engineer"));
        SKILL_TO_JOBS.put("c#", List.of(".NET Developer", "Unity Game Developer", "Backend Developer", "Software Engineer", "Windows Developer"));
        SKILL_TO_JOBS.put("swift", List.of("iOS Developer", "Mobile Developer", "Apple Platform Developer", "Software Engineer"));
        SKILL_TO_JOBS.put("kotlin", List.of("Android Developer", "Mobile Developer", "Backend Developer", "Software Engineer"));
        SKILL_TO_JOBS.put("go", List.of("Backend Developer", "DevOps Engineer", "Cloud Engineer", "Systems Engineer"));
        SKILL_TO_JOBS.put("rust", List.of("Systems Programmer", "Blockchain Developer", "Performance Engineer", "Backend Developer"));
        SKILL_TO_JOBS.put("php", List.of("PHP Developer", "Web Developer", "Backend Developer", "WordPress Developer"));
        SKILL_TO_JOBS.put("ruby", List.of("Ruby Developer", "Rails Developer", "Backend Developer", "Full Stack Developer"));
        SKILL_TO_JOBS.put("sql", List.of("Database Administrator", "Data Analyst", "Backend Developer", "Data Engineer"));
        
        // Frameworks & Technologies
        SKILL_TO_JOBS.put("react", List.of("React Developer", "Frontend Developer", "Full Stack Developer", "UI Developer"));
        SKILL_TO_JOBS.put("angular", List.of("Angular Developer", "Frontend Developer", "Full Stack Developer", "UI Developer"));
        SKILL_TO_JOBS.put("vue", List.of("Vue.js Developer", "Frontend Developer", "Full Stack Developer", "UI Developer"));
        SKILL_TO_JOBS.put("node", List.of("Node.js Developer", "Backend Developer", "Full Stack Developer", "API Developer"));
        SKILL_TO_JOBS.put("spring", List.of("Java Developer", "Backend Developer", "Microservices Developer", "Enterprise Developer"));
        SKILL_TO_JOBS.put("django", List.of("Python Developer", "Backend Developer", "Full Stack Developer", "Web Developer"));
        SKILL_TO_JOBS.put("flask", List.of("Python Developer", "Backend Developer", "API Developer", "Web Developer"));
        SKILL_TO_JOBS.put("docker", List.of("DevOps Engineer", "Cloud Engineer", "Platform Engineer", "SRE"));
        SKILL_TO_JOBS.put("kubernetes", List.of("DevOps Engineer", "Cloud Engineer", "Platform Engineer", "SRE", "Infrastructure Engineer"));
        SKILL_TO_JOBS.put("aws", List.of("Cloud Engineer", "AWS Solutions Architect", "DevOps Engineer", "Cloud Developer"));
        SKILL_TO_JOBS.put("azure", List.of("Cloud Engineer", "Azure Developer", "DevOps Engineer", "Cloud Architect"));
        SKILL_TO_JOBS.put("gcp", List.of("Cloud Engineer", "GCP Developer", "DevOps Engineer", "Data Engineer"));
        
        // Data & AI
        SKILL_TO_JOBS.put("machine learning", List.of("Machine Learning Engineer", "Data Scientist", "AI Engineer", "Research Scientist"));
        SKILL_TO_JOBS.put("deep learning", List.of("Deep Learning Engineer", "AI Researcher", "Computer Vision Engineer", "NLP Engineer"));
        SKILL_TO_JOBS.put("tensorflow", List.of("Machine Learning Engineer", "AI Developer", "Data Scientist", "Research Engineer"));
        SKILL_TO_JOBS.put("pytorch", List.of("Machine Learning Engineer", "AI Developer", "Research Scientist", "Deep Learning Engineer"));
        SKILL_TO_JOBS.put("data analysis", List.of("Data Analyst", "Business Analyst", "Data Scientist", "BI Developer"));
        SKILL_TO_JOBS.put("tableau", List.of("Data Analyst", "BI Developer", "Data Visualization Specialist", "Business Analyst"));
        SKILL_TO_JOBS.put("power bi", List.of("BI Developer", "Data Analyst", "Business Analyst", "Report Developer"));
        
        // Other Skills
        SKILL_TO_JOBS.put("project management", List.of("Project Manager", "Scrum Master", "Product Owner", "Program Manager"));
        SKILL_TO_JOBS.put("agile", List.of("Scrum Master", "Agile Coach", "Project Manager", "Product Owner"));
        SKILL_TO_JOBS.put("ui/ux", List.of("UI/UX Designer", "Product Designer", "UX Researcher", "Interaction Designer"));
        SKILL_TO_JOBS.put("graphic design", List.of("Graphic Designer", "Visual Designer", "Brand Designer", "Creative Director"));
        SKILL_TO_JOBS.put("marketing", List.of("Digital Marketing Manager", "Marketing Specialist", "Content Marketing Manager", "SEO Specialist"));
        SKILL_TO_JOBS.put("sales", List.of("Sales Representative", "Account Executive", "Business Development Manager", "Sales Manager"));
        SKILL_TO_JOBS.put("communication", List.of("Communications Specialist", "PR Manager", "Content Writer", "Marketing Coordinator"));
        SKILL_TO_JOBS.put("leadership", List.of("Team Lead", "Engineering Manager", "Director", "VP of Engineering"));
    }
    
    /**
     * Analyze skills and suggest matching jobs
     */
    public static List<JobSuggestion> suggestJobsFromSkills(String skills) {
        List<JobSuggestion> suggestions = new ArrayList<>();
        Map<String, Integer> jobScores = new HashMap<>();
        
        if (skills == null || skills.trim().isEmpty()) {
            return suggestions;
        }
        
        String[] skillArray = skills.toLowerCase().split("[,;\\s]+");
        
        for (String skill : skillArray) {
            skill = skill.trim();
            if (skill.isEmpty()) continue;
            
            // Check exact match
            if (SKILL_TO_JOBS.containsKey(skill)) {
                for (String job : SKILL_TO_JOBS.get(skill)) {
                    jobScores.merge(job, 10, Integer::sum);
                }
            }
            
            // Check partial match
            for (Map.Entry<String, List<String>> entry : SKILL_TO_JOBS.entrySet()) {
                if (entry.getKey().contains(skill) || skill.contains(entry.getKey())) {
                    for (String job : entry.getValue()) {
                        jobScores.merge(job, 5, Integer::sum);
                    }
                }
            }
        }
        
        // Sort by score and create suggestions
        jobScores.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(10)
            .forEach(entry -> {
                int matchPercent = Math.min(100, entry.getValue() * 10);
                suggestions.add(new JobSuggestion(
                    entry.getKey(),
                    "Based on your skills profile",
                    matchPercent + "% match",
                    getSalaryRange(entry.getKey()),
                    getJobDescription(entry.getKey())
                ));
            });
        
        return suggestions;
    }
    
    /**
     * Search for jobs using JSearch API (RapidAPI) - Free tier available
     */
    public static List<JobListing> searchJobs(String query, String location) {
        List<JobListing> listings = new ArrayList<>();
        
        try {
            // Using JSearch API from RapidAPI (has free tier)
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
            String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8.toString());
            
            // For demo purposes, return mock data
            // In production, you would use actual API calls
            listings = getMockJobListings(query, location);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error searching jobs: " + e.getMessage(), e);
            listings = getMockJobListings(query, location);
        }
        
        return listings;
    }
    
    /**
     * Get mock job listings for demonstration
     * Replace with real API calls in production
     */
    private static List<JobListing> getMockJobListings(String query, String location) {
        List<JobListing> listings = new ArrayList<>();
        
        // Generate realistic mock data based on query
        String[] companies = {
            "Google", "Microsoft", "Amazon", "Apple", "Meta",
            "Netflix", "Spotify", "Uber", "Airbnb", "Twitter",
            "IBM", "Oracle", "Salesforce", "Adobe", "Intel",
            "NVIDIA", "Tesla", "SpaceX", "Stripe", "Shopify"
        };
        
        String[] jobTypes = {"Full-time", "Part-time", "Contract", "Remote"};
        
        for (int i = 0; i < 8; i++) {
            String company = companies[i % companies.length];
            String jobType = jobTypes[i % jobTypes.length];
            String salary = "$" + (80 + (i * 15)) + "K - $" + (120 + (i * 20)) + "K";
            
            listings.add(new JobListing(
                query + " at " + company,
                company,
                location.isEmpty() ? "Remote" : location,
                salary,
                jobType,
                "Posted " + (i + 1) + " days ago",
                "https://careers." + company.toLowerCase() + ".com"
            ));
        }
        
        return listings;
    }
    
    /**
     * Get estimated salary range for a job title
     */
    private static String getSalaryRange(String jobTitle) {
        String title = jobTitle.toLowerCase();
        
        if (title.contains("senior") || title.contains("lead") || title.contains("architect")) {
            return "$120,000 - $180,000";
        } else if (title.contains("manager") || title.contains("director")) {
            return "$130,000 - $200,000";
        } else if (title.contains("engineer") || title.contains("developer")) {
            return "$80,000 - $140,000";
        } else if (title.contains("analyst")) {
            return "$60,000 - $100,000";
        } else if (title.contains("designer")) {
            return "$70,000 - $120,000";
        } else {
            return "$50,000 - $90,000";
        }
    }
    
    /**
     * Get job description template
     */
    private static String getJobDescription(String jobTitle) {
        return "We are looking for a talented " + jobTitle + " to join our team. " +
               "You will work on cutting-edge projects and collaborate with amazing people.";
    }
    
    /**
     * Job Suggestion data class
     */
    public static class JobSuggestion {
        public final String title;
        public final String reason;
        public final String matchScore;
        public final String salaryRange;
        public final String description;
        
        public JobSuggestion(String title, String reason, String matchScore, String salaryRange, String description) {
            this.title = title;
            this.reason = reason;
            this.matchScore = matchScore;
            this.salaryRange = salaryRange;
            this.description = description;
        }
    }
    
    /**
     * Job Listing data class
     */
    public static class JobListing {
        public final String title;
        public final String company;
        public final String location;
        public final String salary;
        public final String jobType;
        public final String postedDate;
        public final String applyUrl;
        
        public JobListing(String title, String company, String location, String salary, 
                         String jobType, String postedDate, String applyUrl) {
            this.title = title;
            this.company = company;
            this.location = location;
            this.salary = salary;
            this.jobType = jobType;
            this.postedDate = postedDate;
            this.applyUrl = applyUrl;
        }
    }
}

