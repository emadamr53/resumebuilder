package models;

public class Resume {

    private String name;
    private String email;
    private String phone;
    private String address;

    private String institution;
    private String degree;
    private String year;

    private String jobTitle;
    private String company;
    private String duration;
    private String description;

    private String skills;

    // ============================
    // EMPTY CONSTRUCTOR (REQUIRED)
    // ============================
    public Resume() {
    }

    // ============================
    // FULL CONSTRUCTOR (OPTIONAL)
    // ============================
    public Resume(String name, String email, String phone, String address,
                  String institution, String degree, String year,
                  String jobTitle, String company, String duration,
                  String description, String skills) {

        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.institution = institution;
        this.degree = degree;
        this.year = year;
        this.jobTitle = jobTitle;
        this.company = company;
        this.duration = duration;
        this.description = description;
        this.skills = skills;
    }

    // ============================
    // GETTERS & SETTERS
    // ============================
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
}
