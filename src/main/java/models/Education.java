package models;

/**
 * Education model class for managing education entries
 * @author habib
 */
public class Education {
    private int id;
    private int resumeId;
    private String institution;
    private String degree;
    private String fieldOfStudy;
    private String graduationYear;
    private String gpa;
    private String description;
    
    public Education() {
    }
    
    public Education(String institution, String degree, String graduationYear) {
        this.institution = institution;
        this.degree = degree;
        this.graduationYear = graduationYear;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getResumeId() { return resumeId; }
    public void setResumeId(int resumeId) { this.resumeId = resumeId; }
    
    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }
    
    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }
    
    public String getFieldOfStudy() { return fieldOfStudy; }
    public void setFieldOfStudy(String fieldOfStudy) { this.fieldOfStudy = fieldOfStudy; }
    
    public String getGraduationYear() { return graduationYear; }
    public void setGraduationYear(String graduationYear) { this.graduationYear = graduationYear; }
    
    public String getGpa() { return gpa; }
    public void setGpa(String gpa) { this.gpa = gpa; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
