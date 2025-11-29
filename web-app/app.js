// Resume Builder Web App - Main JavaScript

// Local Storage Keys
const STORAGE_KEY_USERS = 'resumebuilder_users';
const STORAGE_KEY_CURRENT_USER = 'resumebuilder_current_user';
const STORAGE_KEY_RESUMES = 'resumebuilder_resumes';

// Initialize App
let currentUser = null;
let currentResume = null;

// Initialize on load
document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    setupEventListeners();
    initializePWA();
});

// Check if user is logged in
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
    
    // Logout
    document.getElementById('logoutBtn').addEventListener('click', handleLogout);
    
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
        skills: document.getElementById('resumeSkills').value.split(',').map(s => s.trim()),
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
        if (resume.experiences) {
            resume.experiences.forEach(exp => {
                const item = document.createElement('div');
                item.className = 'exp-item';
                item.innerHTML = `
                    <input type="text" data-field="jobTitle" value="${exp.jobTitle || ''}" placeholder="Job Title">
                    <input type="text" data-field="company" value="${exp.company || ''}" placeholder="Company">
                    <input type="text" data-field="location" value="${exp.location || ''}" placeholder="Location">
                    <input type="text" data-field="startDate" value="${exp.startDate || ''}" placeholder="Start Date">
                    <input type="text" data-field="endDate" value="${exp.endDate || ''}" placeholder="End Date">
                    <textarea data-field="description" placeholder="Description">${exp.description || ''}</textarea>
                    <button type="button" class="remove-btn" onclick="this.parentElement.remove()">Remove</button>
                `;
                expContainer.appendChild(item);
            });
        }
        
        // Load education
        const eduContainer = document.getElementById('educationContainer');
        eduContainer.innerHTML = '';
        if (resume.education) {
            resume.education.forEach(edu => {
                const item = document.createElement('div');
                item.className = 'edu-item';
                item.innerHTML = `
                    <input type="text" data-field="institution" value="${edu.institution || ''}" placeholder="Institution">
                    <input type="text" data-field="degree" value="${edu.degree || ''}" placeholder="Degree">
                    <input type="text" data-field="field" value="${edu.field || ''}" placeholder="Field">
                    <input type="text" data-field="year" value="${edu.year || ''}" placeholder="Year">
                    <input type="text" data-field="gpa" value="${edu.gpa || ''}" placeholder="GPA">
                    <button type="button" class="remove-btn" onclick="this.parentElement.remove()">Remove</button>
                `;
                eduContainer.appendChild(item);
            });
        }
    }
}

// Load resume preview
function loadResumePreview() {
    const resume = getCurrentResume();
    const preview = document.getElementById('resumePreview');
    
    if (!resume) {
        preview.innerHTML = '<p class="empty-state">No resume data available. Create a resume first!</p>';
        return;
    }
    
    let html = `
        <h1>${resume.name || 'Your Name'}</h1>
        <p>${resume.email || ''} | ${resume.phone || ''}</p>
        ${resume.address ? `<p>${resume.address}</p>` : ''}
        
        <h2>Skills</h2>
        <p>${resume.skills?.join(', ') || 'No skills listed'}</p>
        
        <h2>Experience</h2>
        ${resume.experiences?.map(exp => `
            <div style="margin-bottom: 15px;">
                <strong>${exp.jobTitle || 'Job Title'}</strong> - ${exp.company || 'Company'}<br>
                ${exp.location ? `${exp.location} | ` : ''}${exp.startDate || ''} - ${exp.endDate || ''}<br>
                ${exp.description ? `<p style="margin-top: 5px;">${exp.description}</p>` : ''}
            </div>
        `).join('') || '<p>No experience listed</p>'}
        
        <h2>Education</h2>
        ${resume.education?.map(edu => `
            <div style="margin-bottom: 15px;">
                <strong>${edu.degree || 'Degree'}</strong> in ${edu.field || 'Field'}<br>
                ${edu.institution || 'Institution'}${edu.year ? `, ${edu.year}` : ''}${edu.gpa ? ` | GPA: ${edu.gpa}` : ''}
            </div>
        `).join('') || '<p>No education listed</p>'}
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

// Search jobs
function searchJobs() {
    const skills = document.getElementById('jobSkills').value;
    const location = document.getElementById('jobLocation').value;
    const results = document.getElementById('jobResults');
    
    results.innerHTML = '<div class="loading">Searching for jobs...</div>';
    
    // Simulate job search (replace with real API call)
    setTimeout(() => {
        const mockJobs = [
            {
                title: `${skills || 'Software'} Developer`,
                company: 'Tech Corp',
                location: location || 'Remote',
                salary: '$80K - $120K',
                type: 'Full-time'
            },
            {
                title: `Senior ${skills || 'Software'} Engineer`,
                company: 'Innovation Labs',
                location: location || 'Remote',
                salary: '$120K - $160K',
                type: 'Full-time'
            },
            {
                title: `${skills || 'Software'} Developer`,
                company: 'StartupXYZ',
                location: location || 'Remote',
                salary: '$70K - $100K',
                type: 'Full-time'
            }
        ];
        
        if (mockJobs.length === 0) {
            results.innerHTML = '<div class="empty-state">No jobs found. Try different keywords.</div>';
        } else {
            results.innerHTML = mockJobs.map(job => `
                <div class="job-card">
                    <h3>${job.title}</h3>
                    <div class="company">${job.company}</div>
                    <div class="details">📍 ${job.location} | ${job.type}</div>
                    <div class="salary">💰 ${job.salary}</div>
                    <button class="btn btn-primary" onclick="applyToJob('${job.title}', '${job.company}')">Apply Now</button>
                </div>
            `).join('');
        }
    }, 1000);
}

// Apply to job
function applyToJob(title, company) {
    alert(`Applying to ${title} at ${company}...\n\nThis would open LinkedIn Easy Apply in a real implementation.`);
}

// Export as PDF
function exportAsPDF() {
    alert('PDF export would be implemented here.\n\nIn a real app, this would use a PDF generation library like jsPDF.');
}

// Export as Word
function exportAsWord() {
    alert('Word export would be implemented here.\n\nIn a real app, this would use a library like docx.');
}

// Initialize PWA
function initializePWA() {
    // Register service worker if available
    if ('serviceWorker' in navigator) {
        navigator.serviceWorker.register('/sw.js').catch(() => {
            // Service worker registration failed, continue without it
        });
    }
    
    // Handle install prompt
    let deferredPrompt;
    window.addEventListener('beforeinstallprompt', (e) => {
        e.preventDefault();
        deferredPrompt = e;
        showInstallPrompt();
    });
}

// Show install prompt
function showInstallPrompt() {
    const prompt = document.createElement('div');
    prompt.className = 'install-prompt';
    prompt.innerHTML = `
        <p>Install Resume Builder as an app?</p>
        <button onclick="installApp()">Install</button>
    `;
    document.body.appendChild(prompt);
}

// Install app
function installApp() {
    if (deferredPrompt) {
        deferredPrompt.prompt();
        deferredPrompt.userChoice.then((choiceResult) => {
            if (choiceResult.outcome === 'accepted') {
                console.log('User accepted the install prompt');
            }
            deferredPrompt = null;
        });
    }
}

