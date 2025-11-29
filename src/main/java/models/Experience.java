package models;

/**
 * Experience model class for managing work experience entries
 * @author habib
 */
public class Experience {
    private int id;
    private int resumeId;
    private String jobTitle;
    private String company;
    private String location;
    private String startDate;
    private String endDate;
    private boolean isCurrentJob;
    private String description;
    
    public Experience() {
    }
    
    public Experience(String jobTitle, String company, String startDate, String endDate, String description) {
        this.jobTitle = jobTitle;
        this.company = company;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getResumeId() { return resumeId; }
    public void setResumeId(int resumeId) { this.resumeId = resumeId; }
    
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    
    public boolean isCurrentJob() { return isCurrentJob; }
    public void setCurrentJob(boolean isCurrentJob) { this.isCurrentJob = isCurrentJob; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
