// Resume Builder Web App - Complete JavaScript

// Local Storage Keys
const STORAGE_KEY_USERS = 'resumebuilder_users';
const STORAGE_KEY_CURRENT_USER = 'resumebuilder_current_user';
const STORAGE_KEY_RESUMES = 'resumebuilder_resumes';
const STORAGE_KEY_THEME = 'resumebuilder_theme';
const STORAGE_KEY_DARK_MODE = 'resumebuilder_dark_mode';

// App State
let currentUser = null;
let currentResume = null;
let currentTheme = 'professional';
let isDarkMode = true;
let selectedThemeForPreview = 'professional';

// Theme configurations
const themes = {
    classic: {
        name: 'Classic',
        primaryColor: '#8B7355',
        secondaryColor: '#A0826D',
        bgColor: '#F5F5DC',
        textColor: '#2C2C2C'
    },
    modern: {
        name: 'Modern',
        primaryColor: '#667eea',
        secondaryColor: '#764ba2',
        bgColor: '#FFFFFF',
        textColor: '#1a1a2e'
    },
    professional: {
        name: 'Professional',
        primaryColor: '#2c3e50',
        secondaryColor: '#34495e',
        bgColor: '#FFFFFF',
        textColor: '#2c3e50'
    },
    creative: {
        name: 'Creative',
        primaryColor: '#e74c3c',
        secondaryColor: '#c0392b',
        bgColor: '#FFFFFF',
        textColor: '#2c3e50'
    }
};

// Initialize App
document.addEventListener('DOMContentLoaded', () => {
    loadThemeSettings();
    checkURLParams(); // Check for public resume page
    checkAuth();
    setupEventListeners();
    initializePWA();
    applyTheme();
});

// Check URL parameters for public resume page
function checkURLParams() {
    const urlParams = new URLSearchParams(window.location.search);
    const username = urlParams.get('user');
    
    if (username) {
        // Show public resume page
        showPublicResumePage(username);
        return true;
    }
    return false;
}

// Load theme settings from storage
function loadThemeSettings() {
    const savedTheme = localStorage.getItem(STORAGE_KEY_THEME);
    const savedDarkMode = localStorage.getItem(STORAGE_KEY_DARK_MODE);
    
    if (savedTheme) {
        currentTheme = savedTheme;
        selectedThemeForPreview = savedTheme;
    }
    
    if (savedDarkMode !== null) {
        isDarkMode = savedDarkMode === 'true';
    } else {
        // Default to dark mode if system preference is dark
        isDarkMode = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
    }
}

// Setup event listeners
function setupEventListeners() {
    // Login form
    document.getElementById('loginForm').addEventListener('submit', handleLogin);
    
    // Signup form
    document.getElementById('signupForm').addEventListener('submit', handleSignup);
    
    // Screen navigation
    document.getElementById('showSignup').addEventListener('click', (e) => {
        e.preventDefault();
        showScreen('signupScreen');
    });
    
    document.getElementById('showLogin').addEventListener('click', (e) => {
        e.preventDefault();
        showScreen('loginScreen');
    });
    
    // Forgot password
    document.getElementById('showForgotPassword').addEventListener('click', (e) => {
        e.preventDefault();
        showScreen('forgotPasswordScreen');
    });
    
    document.getElementById('showLoginFromForgot').addEventListener('click', (e) => {
        e.preventDefault();
        showScreen('loginScreen');
    });
    
    // Forgot password form
    document.getElementById('forgotPasswordForm').addEventListener('submit', handleForgotPassword);
    
    // Logout
    document.getElementById('logoutBtn').addEventListener('click', handleLogout);
    
    // Theme toggle
    document.getElementById('themeToggle').addEventListener('click', toggleDarkMode);
    
    // Resume form
    document.getElementById('resumeForm').addEventListener('submit', handleResumeSave);
}

// Show screen
function showScreen(screenId) {
    document.querySelectorAll('.screen').forEach(screen => {
        screen.classList.remove('active');
    });
    document.getElementById(screenId).classList.add('active');
    
    // Load data if needed
    if (screenId === 'previewScreen') {
        loadResumePreview();
    } else if (screenId === 'resumeFormScreen') {
        loadResumeForm();
    } else if (screenId === 'qrCodeScreen') {
        generateQRCode();
    } else if (screenId === 'themeSelectionScreen') {
        highlightSelectedTheme();
    } else if (screenId === 'publicResumeScreen') {
        // Public resume page is handled by showPublicResumePage
    }
}

// Handle login
function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;
    
    const users = getUsers();
    const user = users.find(u => u.email === email && u.password === password);
    
    if (user) {
        currentUser = user;
        localStorage.setItem(STORAGE_KEY_CURRENT_USER, JSON.stringify(user));
        showScreen('dashboardScreen');
        updateWelcomeText();
    } else {
        alert('Invalid email or password!');
    }
}

// Handle signup
function handleSignup(e) {
    e.preventDefault();
    const name = document.getElementById('signupName').value;
    const username = document.getElementById('signupUsername').value;
    const email = document.getElementById('signupEmail').value;
    const password = document.getElementById('signupPassword').value;
    
    const users = getUsers();
    
    if (users.find(u => u.email === email)) {
        alert('Email already exists!');
        return;
    }
    
    if (users.find(u => u.username === username)) {
        alert('Username already taken!');
        return;
    }
    
    const newUser = {
        id: Date.now(),
        name,
        username,
        email,
        password
    };
    
    users.push(newUser);
    localStorage.setItem(STORAGE_KEY_USERS, JSON.stringify(users));
    
    currentUser = newUser;
    localStorage.setItem(STORAGE_KEY_CURRENT_USER, JSON.stringify(newUser));
    
    showScreen('dashboardScreen');
    updateWelcomeText();
    alert('Account created successfully!');
}

// Handle logout
function handleLogout() {
    currentUser = null;
    localStorage.removeItem(STORAGE_KEY_CURRENT_USER);
    showScreen('loginScreen');
}

// Handle forgot password
function handleForgotPassword(e) {
    e.preventDefault();
    const email = document.getElementById('forgotEmail').value;
    const newPassword = document.getElementById('forgotNewPassword').value;
    const confirmPassword = document.getElementById('forgotConfirmPassword').value;
    
    // Validation
    if (!email || !newPassword || !confirmPassword) {
        alert('All fields are required!');
        return;
    }
    
    if (newPassword.length < 6) {
        alert('Password must be at least 6 characters long!');
        return;
    }
    
    if (newPassword !== confirmPassword) {
        alert('Passwords do not match!');
        return;
    }
    
    // Get users from storage
    const users = getUsers();
    const userIndex = users.findIndex(u => u.email === email);
    
    if (userIndex === -1) {
        alert('Email not found in our system!');
        return;
    }
    
    // Update password
    users[userIndex].password = newPassword;
    localStorage.setItem(STORAGE_KEY_USERS, JSON.stringify(users));
    
    alert('Password reset successfully! You can now sign in with your new password.');
    showScreen('loginScreen');
    
    // Clear form
    document.getElementById('forgotPasswordForm').reset();
}

// Update welcome text
function updateWelcomeText() {
    if (currentUser) {
        document.getElementById('welcomeText').textContent = `Welcome back, ${currentUser.name || currentUser.username}!`;
    }
}

// Get users from storage
function getUsers() {
    const usersStr = localStorage.getItem(STORAGE_KEY_USERS);
    return usersStr ? JSON.parse(usersStr) : [];
}

// Handle resume save
function handleResumeSave(e) {
    e.preventDefault();
    
    const resume = {
        id: Date.now(),
        userId: currentUser.id,
        name: document.getElementById('resumeName').value,
        email: document.getElementById('resumeEmail').value,
        phone: document.getElementById('resumePhone').value,
        address: document.getElementById('resumeAddress').value,
        skills: document.getElementById('resumeSkills').value.split(',').map(s => s.trim()).filter(s => s),
        experiences: getExperiences(),
        education: getEducation()
    };
    
    const resumes = getResumes();
    const existingIndex = resumes.findIndex(r => r.userId === currentUser.id);
    
    if (existingIndex >= 0) {
        resumes[existingIndex] = resume;
    } else {
        resumes.push(resume);
    }
    
    localStorage.setItem(STORAGE_KEY_RESUMES, JSON.stringify(resumes));
    currentResume = resume;
    
    alert('Resume saved successfully!');
    showScreen('dashboardScreen');
}

// Get experiences from form
function getExperiences() {
    const items = [];
    document.querySelectorAll('.exp-item').forEach(item => {
        items.push({
            jobTitle: item.querySelector('[data-field="jobTitle"]')?.value || '',
            company: item.querySelector('[data-field="company"]')?.value || '',
            location: item.querySelector('[data-field="location"]')?.value || '',
            startDate: item.querySelector('[data-field="startDate"]')?.value || '',
            endDate: item.querySelector('[data-field="endDate"]')?.value || '',
            description: item.querySelector('[data-field="description"]')?.value || ''
        });
    });
    return items;
}

// Get education from form
function getEducation() {
    const items = [];
    document.querySelectorAll('.edu-item').forEach(item => {
        items.push({
            institution: item.querySelector('[data-field="institution"]')?.value || '',
            degree: item.querySelector('[data-field="degree"]')?.value || '',
            field: item.querySelector('[data-field="field"]')?.value || '',
            year: item.querySelector('[data-field="year"]')?.value || '',
            gpa: item.querySelector('[data-field="gpa"]')?.value || ''
        });
    });
    return items;
}

// Add experience field
function addExperience() {
    const container = document.getElementById('experienceContainer');
    const item = document.createElement('div');
    item.className = 'exp-item';
    item.innerHTML = `
        <input type="text" data-field="jobTitle" placeholder="Job Title" required>
        <input type="text" data-field="company" placeholder="Company" required>
        <input type="text" data-field="location" placeholder="Location">
        <input type="text" data-field="startDate" placeholder="Start Date (e.g., Jan 2020)">
        <input type="text" data-field="endDate" placeholder="End Date (e.g., Present)">
        <textarea data-field="description" placeholder="Job Description"></textarea>
        <button type="button" class="remove-btn" onclick="this.parentElement.remove()">Remove</button>
    `;
    container.appendChild(item);
}

// Add education field
function addEducation() {
    const container = document.getElementById('educationContainer');
    const item = document.createElement('div');
    item.className = 'edu-item';
    item.innerHTML = `
        <input type="text" data-field="institution" placeholder="Institution" required>
        <input type="text" data-field="degree" placeholder="Degree (e.g., Bachelor's)">
        <input type="text" data-field="field" placeholder="Field of Study">
        <input type="text" data-field="year" placeholder="Graduation Year">
        <input type="text" data-field="gpa" placeholder="GPA (optional)">
        <button type="button" class="remove-btn" onclick="this.parentElement.remove()">Remove</button>
    `;
    container.appendChild(item);
}

// Load resume form
function loadResumeForm() {
    const resume = getCurrentResume();
    if (resume) {
        document.getElementById('resumeName').value = resume.name || '';
        document.getElementById('resumeEmail').value = resume.email || '';
        document.getElementById('resumePhone').value = resume.phone || '';
        document.getElementById('resumeAddress').value = resume.address || '';
        document.getElementById('resumeSkills').value = resume.skills?.join(', ') || '';
        
        // Load experiences
        const expContainer = document.getElementById('experienceContainer');
        expContainer.innerHTML = '';
        if (resume.experiences && resume.experiences.length > 0) {
            resume.experiences.forEach(exp => {
                const item = document.createElement('div');
                item.className = 'exp-item';
                item.innerHTML = `
                    <input type="text" data-field="jobTitle" value="${escapeHtml(exp.jobTitle || '')}" placeholder="Job Title">
                    <input type="text" data-field="company" value="${escapeHtml(exp.company || '')}" placeholder="Company">
                    <input type="text" data-field="location" value="${escapeHtml(exp.location || '')}" placeholder="Location">
                    <input type="text" data-field="startDate" value="${escapeHtml(exp.startDate || '')}" placeholder="Start Date">
                    <input type="text" data-field="endDate" value="${escapeHtml(exp.endDate || '')}" placeholder="End Date">
                    <textarea data-field="description" placeholder="Description">${escapeHtml(exp.description || '')}</textarea>
                    <button type="button" class="remove-btn" onclick="this.parentElement.remove()">Remove</button>
                `;
                expContainer.appendChild(item);
            });
        }
        
        // Load education
        const eduContainer = document.getElementById('educationContainer');
        eduContainer.innerHTML = '';
        if (resume.education && resume.education.length > 0) {
            resume.education.forEach(edu => {
                const item = document.createElement('div');
                item.className = 'edu-item';
                item.innerHTML = `
                    <input type="text" data-field="institution" value="${escapeHtml(edu.institution || '')}" placeholder="Institution">
                    <input type="text" data-field="degree" value="${escapeHtml(edu.degree || '')}" placeholder="Degree">
                    <input type="text" data-field="field" value="${escapeHtml(edu.field || '')}" placeholder="Field">
                    <input type="text" data-field="year" value="${escapeHtml(edu.year || '')}" placeholder="Year">
                    <input type="text" data-field="gpa" value="${escapeHtml(edu.gpa || '')}" placeholder="GPA">
                    <button type="button" class="remove-btn" onclick="this.parentElement.remove()">Remove</button>
                `;
                eduContainer.appendChild(item);
            });
        }
    }
}

// Escape HTML to prevent XSS
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Load resume preview
function loadResumePreview() {
    const resume = getCurrentResume();
    const preview = document.getElementById('resumePreview');
    
    if (!resume) {
        preview.innerHTML = '<p class="empty-state">No resume data available. Create a resume first!</p>';
        return;
    }
    
    const theme = themes[selectedThemeForPreview] || themes.professional;
    
    let html = `
        <div class="resume-content theme-${selectedThemeForPreview}">
            <div class="resume-header">
                <h1>${escapeHtml(resume.name || 'Your Name')}</h1>
                <div class="contact-info">
                    ${resume.email ? `<span>${escapeHtml(resume.email)}</span>` : ''}
                    ${resume.phone ? `<span>${escapeHtml(resume.phone)}</span>` : ''}
                    ${resume.address ? `<span>${escapeHtml(resume.address)}</span>` : ''}
                </div>
            </div>
            
            ${resume.skills && resume.skills.length > 0 ? `
            <div class="resume-section">
                <h2>Skills</h2>
                <div class="skills-list">
                    ${resume.skills.map(skill => `<span class="skill-tag">${escapeHtml(skill)}</span>`).join('')}
                </div>
            </div>
            ` : ''}
            
            ${resume.experiences && resume.experiences.length > 0 ? `
            <div class="resume-section">
                <h2>Experience</h2>
                ${resume.experiences.map(exp => `
                    <div class="experience-item">
                        <div class="exp-header">
                            <strong>${escapeHtml(exp.jobTitle || 'Job Title')}</strong>
                            <span class="exp-company">${escapeHtml(exp.company || 'Company')}</span>
                        </div>
                        ${exp.location || exp.startDate || exp.endDate ? `
                        <div class="exp-meta">
                            ${exp.location ? `<span>📍 ${escapeHtml(exp.location)}</span>` : ''}
                            ${exp.startDate || exp.endDate ? `<span>📅 ${escapeHtml(exp.startDate || '')} - ${escapeHtml(exp.endDate || '')}</span>` : ''}
                        </div>
                        ` : ''}
                        ${exp.description ? `<p class="exp-description">${escapeHtml(exp.description)}</p>` : ''}
                    </div>
                `).join('')}
            </div>
            ` : ''}
            
            ${resume.education && resume.education.length > 0 ? `
            <div class="resume-section">
                <h2>Education</h2>
                ${resume.education.map(edu => `
                    <div class="education-item">
                        <div class="edu-header">
                            <strong>${escapeHtml(edu.degree || 'Degree')}</strong>
                            ${edu.field ? `<span>in ${escapeHtml(edu.field)}</span>` : ''}
                        </div>
                        <div class="edu-meta">
                            ${edu.institution ? `<span>${escapeHtml(edu.institution)}</span>` : ''}
                            ${edu.year ? `<span>${escapeHtml(edu.year)}</span>` : ''}
                            ${edu.gpa ? `<span>GPA: ${escapeHtml(edu.gpa)}</span>` : ''}
                        </div>
                    </div>
                `).join('')}
            </div>
            ` : ''}
        </div>
    `;
    
    preview.innerHTML = html;
}

// Get current user's resume
function getCurrentResume() {
    if (!currentUser) return null;
    const resumes = getResumes();
    return resumes.find(r => r.userId === currentUser.id) || null;
}

// Get resumes from storage
function getResumes() {
    const resumesStr = localStorage.getItem(STORAGE_KEY_RESUMES);
    return resumesStr ? JSON.parse(resumesStr) : [];
}

// Check authentication
function checkAuth() {
    const userStr = localStorage.getItem(STORAGE_KEY_CURRENT_USER);
    if (userStr) {
        currentUser = JSON.parse(userStr);
        showScreen('dashboardScreen');
        updateWelcomeText();
    } else {
        showScreen('loginScreen');
    }
}

// Theme Management
function selectTheme(themeName) {
    currentTheme = themeName;
    selectedThemeForPreview = themeName;
    highlightSelectedTheme();
}

function highlightSelectedTheme() {
    document.querySelectorAll('.theme-card').forEach(card => {
        card.classList.remove('selected');
        if (card.dataset.theme === selectedThemeForPreview) {
            card.classList.add('selected');
        }
    });
}

function applyTheme() {
    localStorage.setItem(STORAGE_KEY_THEME, selectedThemeForPreview);
    currentTheme = selectedThemeForPreview;
    loadResumePreview();
    showScreen('previewScreen');
}

function toggleDarkMode() {
    isDarkMode = !isDarkMode;
    localStorage.setItem(STORAGE_KEY_DARK_MODE, isDarkMode.toString());
    applyTheme();
    updateThemeToggleButton();
}

function updateThemeToggleButton() {
    const btn = document.getElementById('themeToggle');
    btn.textContent = isDarkMode ? '☀️' : '🌙';
    btn.title = isDarkMode ? 'Switch to Light Mode' : 'Switch to Dark Mode';
}

function applyTheme() {
    document.body.classList.toggle('light-mode', !isDarkMode);
    updateThemeToggleButton();
}

// Search jobs
function searchJobs() {
    const skills = document.getElementById('jobSkills').value;
    const location = document.getElementById('jobLocation').value;
    const results = document.getElementById('jobResults');
    
    // Use skills from resume if available
    if (!skills && currentResume && currentResume.skills) {
        document.getElementById('jobSkills').value = currentResume.skills.join(', ');
    }
    
    results.innerHTML = '<div class="loading">🔍 Searching for jobs...</div>';
    
    // Simulate AI-powered job search
    setTimeout(() => {
        const skillArray = skills.toLowerCase().split(',').map(s => s.trim());
        const mockJobs = generateJobSuggestions(skillArray, location);
        
        if (mockJobs.length === 0) {
            results.innerHTML = '<div class="empty-state">No jobs found. Try different keywords.</div>';
        } else {
            results.innerHTML = mockJobs.map(job => `
                <div class="job-card">
                    <h3>${escapeHtml(job.title)}</h3>
                    <div class="company">${escapeHtml(job.company)}</div>
                    <div class="details">📍 ${escapeHtml(job.location)} | ${escapeHtml(job.type)}</div>
                    <div class="salary">💰 ${escapeHtml(job.salary)}</div>
                    <div class="match-score">${escapeHtml(job.match)} match</div>
                    <button class="btn btn-primary" onclick="applyToJob('${escapeHtml(job.title)}', '${escapeHtml(job.company)}')">Apply Now</button>
                </div>
            `).join('');
        }
    }, 1000);
}

// Generate job suggestions based on skills
function generateJobSuggestions(skills, location) {
    const jobMappings = {
        'java': ['Java Developer', 'Backend Developer', 'Software Engineer'],
        'python': ['Python Developer', 'Data Scientist', 'Machine Learning Engineer'],
        'javascript': ['Frontend Developer', 'Full Stack Developer', 'React Developer'],
        'react': ['React Developer', 'Frontend Developer', 'UI Developer'],
        'node': ['Node.js Developer', 'Backend Developer', 'Full Stack Developer'],
        'sql': ['Database Administrator', 'Data Analyst', 'Backend Developer'],
        'aws': ['Cloud Engineer', 'DevOps Engineer', 'Cloud Architect'],
        'docker': ['DevOps Engineer', 'Platform Engineer', 'SRE']
    };
    
    const companies = ['Google', 'Microsoft', 'Amazon', 'Apple', 'Meta', 'Netflix', 'Spotify', 'Uber'];
    const jobTypes = ['Full-time', 'Remote', 'Contract', 'Part-time'];
    
    const jobs = [];
    const addedTitles = new Set();
    
    skills.forEach((skill, index) => {
        const jobTitles = jobMappings[skill] || [`${skill.charAt(0).toUpperCase() + skill.slice(1)} Developer`];
        
        jobTitles.forEach(title => {
            if (!addedTitles.has(title) && jobs.length < 8) {
                addedTitles.add(title);
                const company = companies[index % companies.length];
                const jobType = jobTypes[index % jobTypes.length];
                const salary = '$' + (80 + (index * 15)) + 'K - $' + (120 + (index * 20)) + 'K';
                const match = (85 + Math.floor(Math.random() * 15)) + '%';
                
                jobs.push({
                    title,
                    company,
                    location: location || 'Remote',
                    type: jobType,
                    salary,
                    match
                });
            }
        });
    });
    
    // Fill remaining slots if needed
    while (jobs.length < 6) {
        const company = companies[jobs.length % companies.length];
        const jobType = jobTypes[jobs.length % jobTypes.length];
        const salary = '$' + (70 + (jobs.length * 10)) + 'K - $' + (100 + (jobs.length * 15)) + 'K';
        const match = (75 + Math.floor(Math.random() * 20)) + '%';
        
        jobs.push({
            title: 'Software Engineer',
            company,
            location: location || 'Remote',
            type: jobType,
            salary,
            match
        });
    }
    
    return jobs;
}

// Apply to job
function applyToJob(title, company) {
    alert(`Applying to ${title} at ${company}...\n\nThis would open LinkedIn Easy Apply in a real implementation.`);
}

// Export as PDF
function exportAsPDF() {
    const resume = getCurrentResume();
    if (!resume) {
        alert('No resume to export. Please create a resume first.');
        return;
    }
    
    try {
        const { jsPDF } = window.jspdf;
        const doc = new jsPDF();
        
        // Set theme colors
        const theme = themes[selectedThemeForPreview] || themes.professional;
        
        // Header
        doc.setFillColor(parseInt(theme.primaryColor.substring(1, 3), 16), 
                        parseInt(theme.primaryColor.substring(3, 5), 16), 
                        parseInt(theme.primaryColor.substring(5, 7), 16));
        doc.rect(0, 0, 210, 40, 'F');
        
        doc.setTextColor(255, 255, 255);
        doc.setFontSize(24);
        doc.text(resume.name || 'Your Name', 20, 25);
        
        doc.setFontSize(10);
        let yPos = 35;
        if (resume.email) doc.text(resume.email, 20, yPos);
        if (resume.phone) doc.text(resume.phone, 20, yPos + 5);
        if (resume.address) doc.text(resume.address, 20, yPos + 10);
        
        let currentY = 50;
        doc.setTextColor(0, 0, 0);
        
        // Skills
        if (resume.skills && resume.skills.length > 0) {
            doc.setFontSize(16);
            doc.text('Skills', 20, currentY);
            currentY += 10;
            doc.setFontSize(10);
            doc.text(resume.skills.join(', '), 20, currentY);
            currentY += 15;
        }
        
        // Experience
        if (resume.experiences && resume.experiences.length > 0) {
            doc.setFontSize(16);
            doc.text('Experience', 20, currentY);
            currentY += 10;
            doc.setFontSize(10);
            resume.experiences.forEach(exp => {
                doc.setFont(undefined, 'bold');
                doc.text(`${exp.jobTitle || 'Job Title'} - ${exp.company || 'Company'}`, 20, currentY);
                currentY += 7;
                doc.setFont(undefined, 'normal');
                if (exp.description) {
                    const lines = doc.splitTextToSize(exp.description, 170);
                    doc.text(lines, 20, currentY);
                    currentY += lines.length * 5;
                }
                currentY += 5;
            });
        }
        
        // Education
        if (resume.education && resume.education.length > 0) {
            doc.setFontSize(16);
            doc.text('Education', 20, currentY);
            currentY += 10;
            doc.setFontSize(10);
            resume.education.forEach(edu => {
                doc.setFont(undefined, 'bold');
                doc.text(`${edu.degree || 'Degree'}${edu.field ? ' in ' + edu.field : ''}`, 20, currentY);
                currentY += 7;
                doc.setFont(undefined, 'normal');
                let eduText = '';
                if (edu.institution) eduText += edu.institution;
                if (edu.year) eduText += (eduText ? ', ' : '') + edu.year;
                if (edu.gpa) eduText += (eduText ? ' | ' : '') + 'GPA: ' + edu.gpa;
                if (eduText) {
                    doc.text(eduText, 20, currentY);
                    currentY += 7;
                }
                currentY += 5;
            });
        }
        
        // Save PDF
        doc.save(`${resume.name || 'resume'}_resume.pdf`);
        alert('PDF exported successfully!');
    } catch (error) {
        console.error('PDF export error:', error);
        alert('Error exporting PDF. Please try again.');
    }
}

// Export as Word (using simple HTML download)
function exportAsWord() {
    const resume = getCurrentResume();
    if (!resume) {
        alert('No resume to export. Please create a resume first.');
        return;
    }
    
    try {
        let htmlContent = `
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${resume.name || 'Resume'}</title>
    <style>
        body { font-family: Arial, sans-serif; padding: 40px; }
        h1 { color: #2c3e50; border-bottom: 2px solid #2c3e50; padding-bottom: 10px; }
        h2 { color: #34495e; margin-top: 30px; border-bottom: 1px solid #bdc3c7; padding-bottom: 5px; }
        .contact-info { margin: 10px 0; }
        .section { margin: 20px 0; }
        .skill-tag { display: inline-block; background: #ecf0f1; padding: 5px 10px; margin: 5px; border-radius: 3px; }
    </style>
</head>
<body>
    <h1>${escapeHtml(resume.name || 'Your Name')}</h1>
    <div class="contact-info">
        ${resume.email ? `<p>Email: ${escapeHtml(resume.email)}</p>` : ''}
        ${resume.phone ? `<p>Phone: ${escapeHtml(resume.phone)}</p>` : ''}
        ${resume.address ? `<p>Address: ${escapeHtml(resume.address)}</p>` : ''}
    </div>
    
    ${resume.skills && resume.skills.length > 0 ? `
    <div class="section">
        <h2>Skills</h2>
        <p>${resume.skills.map(s => `<span class="skill-tag">${escapeHtml(s)}</span>`).join(' ')}</p>
    </div>
    ` : ''}
    
    ${resume.experiences && resume.experiences.length > 0 ? `
    <div class="section">
        <h2>Experience</h2>
        ${resume.experiences.map(exp => `
            <div>
                <h3>${escapeHtml(exp.jobTitle || 'Job Title')} - ${escapeHtml(exp.company || 'Company')}</h3>
                ${exp.location || exp.startDate || exp.endDate ? `<p><em>${escapeHtml(exp.location || '')} ${exp.startDate || ''} - ${exp.endDate || ''}</em></p>` : ''}
                ${exp.description ? `<p>${escapeHtml(exp.description)}</p>` : ''}
            </div>
        `).join('')}
    </div>
    ` : ''}
    
    ${resume.education && resume.education.length > 0 ? `
    <div class="section">
        <h2>Education</h2>
        ${resume.education.map(edu => `
            <div>
                <h3>${escapeHtml(edu.degree || 'Degree')}${edu.field ? ' in ' + escapeHtml(edu.field) : ''}</h3>
                <p>${escapeHtml(edu.institution || '')}${edu.year ? ', ' + escapeHtml(edu.year) : ''}${edu.gpa ? ' | GPA: ' + escapeHtml(edu.gpa) : ''}</p>
            </div>
        `).join('')}
    </div>
    ` : ''}
</body>
</html>
        `;
        
        const blob = new Blob(['\ufeff', htmlContent], { type: 'application/msword' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `${resume.name || 'resume'}_resume.doc`;
        link.click();
        URL.revokeObjectURL(url);
        
        alert('Word document exported successfully!');
    } catch (error) {
        console.error('Word export error:', error);
        alert('Error exporting Word document. Please try again.');
    }
}

// Generate QR Code
function generateQRCode() {
    const resume = getCurrentResume();
    if (!resume || !currentUser) {
        document.getElementById('qrCodeContainer').innerHTML = '<p class="empty-state">Create a resume first to generate QR code!</p>';
        return;
    }
    
    // Generate QR code for public resume page
    const publicUrl = getPublicResumeURL(currentUser.username);
    if (!publicUrl) {
        document.getElementById('qrCodeContainer').innerHTML = '<p class="empty-state">Unable to generate QR code. Please login first.</p>';
        return;
    }
    
    const qrUrl = publicUrl;
    
    document.getElementById('qrUrl').innerHTML = `
        <strong>Public Resume URL:</strong><br>
        <a href="${qrUrl}" target="_blank" style="color: var(--accent-primary); word-break: break-all;">${qrUrl}</a>
    `;
    
    // Clear previous QR code
    const canvas = document.getElementById('qrCanvas');
    const ctx = canvas.getContext('2d');
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    // Generate QR code
    QRCode.toCanvas(canvas, qrUrl, {
        width: 300,
        margin: 2,
        color: {
            dark: '#000000',
            light: '#FFFFFF'
        }
    }, function (error) {
        if (error) {
            console.error('QR Code generation error:', error);
            document.getElementById('qrCodeContainer').innerHTML = '<p class="empty-state">Error generating QR code. Please try again.</p>';
        }
    });
}

// Download QR Code
function downloadQRCode() {
    const canvas = document.getElementById('qrCanvas');
    if (!canvas) return;
    
    const link = document.createElement('a');
    link.download = 'resume-qr-code.png';
    link.href = canvas.toDataURL();
    link.click();
}

// Initialize PWA
function initializePWA() {
    // Register service worker if available
    if ('serviceWorker' in navigator) {
        // Use relative path for service worker
        const swPath = './sw.js';
        navigator.serviceWorker.register(swPath).catch((err) => {
            console.log('Service worker registration failed:', err);
            // Continue without service worker
        });
    }
    
    // Handle install prompt
    let deferredPrompt;
    window.addEventListener('beforeinstallprompt', (e) => {
        e.preventDefault();
        deferredPrompt = e;
        // Could show install prompt UI here
    });
}

// Export resume (general function)
function exportResume() {
    showScreen('exportScreen');
}
