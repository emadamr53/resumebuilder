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
    try {
        console.log('🚀 Resume Builder App Initializing...');
        loadThemeSettings();
        checkAuth();
        setupEventListeners();
        initializePWA();
        applyTheme();
        console.log('✅ Resume Builder App Initialized Successfully!');
    } catch (error) {
        console.error('❌ App initialization error:', error);
        alert('Error initializing app. Please refresh the page.\nError: ' + error.message);
    }
});

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
    try {
        // Login form
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', handleLogin);
        }
        
        // Signup form
        const signupForm = document.getElementById('signupForm');
        if (signupForm) {
            signupForm.addEventListener('submit', handleSignup);
        } else {
            console.error('Signup form not found!');
        }
        
        // Screen navigation
        const showSignupLink = document.getElementById('showSignup');
        if (showSignupLink) {
            showSignupLink.addEventListener('click', (e) => {
                e.preventDefault();
                showScreen('signupScreen');
            });
        }
        
        const showLoginLink = document.getElementById('showLogin');
        if (showLoginLink) {
            showLoginLink.addEventListener('click', (e) => {
                e.preventDefault();
                showScreen('loginScreen');
            });
        }
        
        // Logout
        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', handleLogout);
        }
        
        // Theme toggle
        const themeToggle = document.getElementById('themeToggle');
        if (themeToggle) {
            themeToggle.addEventListener('click', toggleDarkMode);
        }
        
        // Resume form
        const resumeForm = document.getElementById('resumeForm');
        if (resumeForm) {
            resumeForm.addEventListener('submit', handleResumeSave);
        }
    } catch (error) {
        console.error('Error setting up event listeners:', error);
    }
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
    }
}

// Handle login
function handleLogin(e) {
    e.preventDefault();
    
    try {
        const email = document.getElementById('loginEmail').value.trim().toLowerCase();
        const password = document.getElementById('loginPassword').value;
        
        if (!email || !password) {
            alert('Please enter both email and password!');
            return;
        }
        
        const users = getUsers();
        const user = users.find(u => 
            u.email && u.email.toLowerCase() === email && u.password === password
        );
        
        if (user) {
            currentUser = user;
            localStorage.setItem(STORAGE_KEY_CURRENT_USER, JSON.stringify(user));
            showScreen('dashboardScreen');
            updateWelcomeText();
            
            // Clear login form
            document.getElementById('loginForm').reset();
        } else {
            alert('❌ Invalid email or password! Please check your credentials or sign up for a new account.');
        }
    } catch (error) {
        console.error('Login error:', error);
        alert('An error occurred during login. Please try again.\nError: ' + error.message);
    }
}

// Handle signup
function handleSignup(e) {
    e.preventDefault();
    
    try {
        const name = document.getElementById('signupName').value.trim();
        const username = document.getElementById('signupUsername').value.trim();
        const email = document.getElementById('signupEmail').value.trim();
        const password = document.getElementById('signupPassword').value;
        
        // Validation
        if (!name || !username || !email || !password) {
            alert('Please fill in all fields!');
            return;
        }
        
        if (password.length < 6) {
            alert('Password must be at least 6 characters long!');
            return;
        }
        
        const users = getUsers();
        
        // Check if email already exists
        if (users.find(u => u.email && u.email.toLowerCase() === email.toLowerCase())) {
            alert('Email already exists! Please use a different email.');
            return;
        }
        
        // Check if username already exists
        if (users.find(u => u.username && u.username.toLowerCase() === username.toLowerCase())) {
            alert('Username already taken! Please choose a different username.');
            return;
        }
        
        // Create new user
        const newUser = {
            id: Date.now(),
            name: name,
            username: username,
            email: email.toLowerCase(),
            password: password,
            createdAt: new Date().toISOString()
        };
        
        // Save to storage
        users.push(newUser);
        localStorage.setItem(STORAGE_KEY_USERS, JSON.stringify(users));
        
        // Set as current user
        currentUser = newUser;
        localStorage.setItem(STORAGE_KEY_CURRENT_USER, JSON.stringify(newUser));
        
        // Show success message
        alert('✅ Account created successfully! Welcome, ' + name + '!');
        
        // Navigate to dashboard
        showScreen('dashboardScreen');
        updateWelcomeText();
        
        // Clear signup form
        document.getElementById('signupForm').reset();
        
    } catch (error) {
        console.error('Signup error:', error);
        alert('An error occurred during signup. Please try again.\nError: ' + error.message);
    }
}

// Handle logout
function handleLogout() {
    currentUser = null;
    localStorage.removeItem(STORAGE_KEY_CURRENT_USER);
    showScreen('loginScreen');
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
async function handleResumeSave(e) {
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
    
    // Save to saved_resumes folder as .txt file
    await saveResumeToSavedResumesFolder(resume);
    
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
    if (!resume) {
        document.getElementById('qrCodeContainer').innerHTML = '<p class="empty-state">Create a resume first to generate QR code!</p>';
        return;
    }
    
    // Get the GitHub Pages URL
    const githubPagesUrl = window.location.origin + window.location.pathname.replace(/\/[^/]*$/, '') || 'https://amremad.github.io/resumebuilder/';
    const qrUrl = githubPagesUrl;
    
    document.getElementById('qrUrl').textContent = qrUrl;
    
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
        navigator.serviceWorker.register('sw.js').catch(() => {
            // Service worker registration failed, continue without it
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

// Format resume as readable text
function formatResumeAsText(resume) {
    let text = '';
    text += '═══════════════════════════════════════════════════════════\n';
    text += '                    RESUME\n';
    text += '═══════════════════════════════════════════════════════════\n\n';
    
    // Personal Information
    if (resume.name) text += `NAME: ${resume.name}\n`;
    if (resume.email) text += `EMAIL: ${resume.email}\n`;
    if (resume.phone) text += `PHONE: ${resume.phone}\n`;
    if (resume.address) text += `ADDRESS: ${resume.address}\n`;
    text += '\n';
    
    // Skills
    if (resume.skills && resume.skills.length > 0) {
        text += '───────────────────────────────────────────────────────────\n';
        text += 'SKILLS\n';
        text += '───────────────────────────────────────────────────────────\n';
        text += resume.skills.join(', ') + '\n\n';
    }
    
    // Experience
    if (resume.experiences && resume.experiences.length > 0) {
        text += '───────────────────────────────────────────────────────────\n';
        text += 'EXPERIENCE\n';
        text += '───────────────────────────────────────────────────────────\n';
        resume.experiences.forEach((exp, index) => {
            if (exp.jobTitle || exp.company) {
                text += `\n${index + 1}. ${exp.jobTitle || ''}${exp.company ? ' at ' + exp.company : ''}\n`;
                if (exp.location) text += `   Location: ${exp.location}\n`;
                if (exp.startDate || exp.endDate) {
                    text += `   Period: ${exp.startDate || ''} - ${exp.endDate || ''}\n`;
                }
                if (exp.description) {
                    text += `   Description: ${exp.description}\n`;
                }
            }
        });
        text += '\n';
    }
    
    // Education
    if (resume.education && resume.education.length > 0) {
        text += '───────────────────────────────────────────────────────────\n';
        text += 'EDUCATION\n';
        text += '───────────────────────────────────────────────────────────\n';
        resume.education.forEach((edu, index) => {
            if (edu.institution || edu.degree) {
                text += `\n${index + 1}. `;
                if (edu.degree) text += `${edu.degree}`;
                if (edu.field) text += ` in ${edu.field}`;
                if (edu.institution) text += `\n   Institution: ${edu.institution}`;
                if (edu.year) text += `\n   Year: ${edu.year}`;
                if (edu.gpa) text += `\n   GPA: ${edu.gpa}`;
                text += '\n';
            }
        });
    }
    
    text += '\n═══════════════════════════════════════════════════════════\n';
    text += `Generated: ${new Date().toLocaleString()}\n`;
    text += '═══════════════════════════════════════════════════════════\n';
    
    return text;
}

// Store the saved_resumes folder handle (if user selects it)
let savedResumesFolderHandle = null;

// Save resume to saved_resumes folder as .txt file
async function saveResumeToSavedResumesFolder(resume) {
    if (!resume) return;
    
    try {
        // Create safe filename
        const safeName = (resume.name || 'Resume').replace(/[^a-z0-9]/gi, '_').toLowerCase();
        const dateStr = new Date().toISOString().split('T')[0];
        const fileName = `${safeName}_resume_${dateStr}.txt`;
        const textContent = formatResumeAsText(resume);
        
        // Try to use File System Access API to save directly to saved_resumes folder
        if ('showDirectoryPicker' in window || 'showSaveFilePicker' in window) {
            try {
                // If we have a saved folder handle, use it
                if (savedResumesFolderHandle) {
                    try {
                        const fileHandle = await savedResumesFolderHandle.getFileHandle(fileName, { create: true });
                        const writable = await fileHandle.createWritable();
                        await writable.write(textContent);
                        await writable.close();
                        
                        console.log('✅ Saved directly to saved_resumes folder:', fileName);
                        return;
                    } catch (err) {
                        console.log('Error writing to saved folder, will ask user to select folder again');
                        savedResumesFolderHandle = null; // Reset if there's an error
                    }
                }
                
                // Try to get the saved_resumes folder (first time or if reset)
                if ('showDirectoryPicker' in window) {
                    try {
                        // Ask user to select the saved_resumes folder
                        const folderHandle = await window.showDirectoryPicker({
                            startIn: 'documents'
                        });
                        
                        // Check if this is the saved_resumes folder (by checking folder name)
                        // Note: We can't check the full path, but we can store the handle
                        savedResumesFolderHandle = folderHandle;
                        localStorage.setItem('resumebuilder_saved_folder_selected', 'true');
                        
                        // Now save the file
                        const fileHandle = await folderHandle.getFileHandle(fileName, { create: true });
                        const writable = await fileHandle.createWritable();
                        await writable.write(textContent);
                        await writable.close();
                        
                        console.log('✅ Saved to selected folder:', fileName);
                        return;
                    } catch (err) {
                        if (err.name !== 'AbortError') {
                            console.log('Directory picker cancelled or error, using download method');
                        } else {
                            return; // User cancelled
                        }
                    }
                }
                
                // Fallback: Use file picker
                if ('showSaveFilePicker' in window) {
                    const fileHandle = await window.showSaveFilePicker({
                        suggestedName: fileName,
                        types: [{
                            description: 'Text Resume File',
                            accept: { 'text/plain': ['.txt'] }
                        }]
                    });
                    
                    const writable = await fileHandle.createWritable();
                    await writable.write(textContent);
                    await writable.close();
                    
                    console.log('✅ Saved via file picker:', fileName);
                    return;
                }
            } catch (err) {
                if (err.name !== 'AbortError') {
                    console.log('File System Access API error, using download method:', err);
                    // Fall through to download method
                } else {
                    return; // User cancelled
                }
            }
        }
        
        // Fallback: Download file to Downloads folder
        // User can move it to saved_resumes folder manually
        const blob = new Blob([textContent], { type: 'text/plain' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = fileName;
        link.style.display = 'none';
        document.body.appendChild(link);
        link.click();
        
        // Remove immediately
        setTimeout(() => {
            document.body.removeChild(link);
            URL.revokeObjectURL(url);
        }, 50);
        
        console.log('✅ File downloaded:', fileName);
        console.log('📍 Location: Downloads folder');
        console.log('💡 To save in saved_resumes folder:');
        console.log('   1. Open Finder');
        console.log('   2. Go to: ~/Desktop/AmrEmadResumeBuilder 3/saved_resumes/');
        console.log('   3. Move the downloaded file there');
        
    } catch (error) {
        console.error('Error saving resume to file:', error);
        alert('Error saving file. Please try again.');
    }
}

// Export resume (general function)
function exportResume() {
    showScreen('exportScreen');
}
