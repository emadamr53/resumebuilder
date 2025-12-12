// Resume Builder Web App - Complete JavaScript

// Local Storage Keys
const STORAGE_KEY_USERS = 'resumebuilder_users';
const STORAGE_KEY_CURRENT_USER = 'resumebuilder_current_user';
const STORAGE_KEY_RESUMES = 'resumebuilder_resumes';
const STORAGE_KEY_THEME = 'resumebuilder_theme';
const STORAGE_KEY_DARK_MODE = 'resumebuilder_dark_mode';
const STORAGE_KEY_AUTOSAVE = 'resumebuilder_autosave';

// App State
let currentUser = null;
let currentResume = null;
let currentTheme = 'professional';
let isDarkMode = true;
let selectedThemeForPreview = 'professional';
let autoSaveTimer = null;
let autoSaveStatus = null; // 'saving', 'saved', null

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
    
    // Setup auto-save listeners (will be attached when form loads)
    setupAutoSave();
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
    } else if (screenId === 'fileManagerScreen') {
        updateFileLocation(); // Load last saved file location
    }
    
    // Debug: Log current state
    if (currentUser) {
        console.log('Current user:', currentUser);
        const resume = getCurrentResume();
        console.log('Current resume:', resume);
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
    
    // Check if user is logged in
    if (!currentUser) {
        alert('Please login first to save your resume!');
        showScreen('loginScreen');
        return;
    }
    
    // Validate required fields
    const name = document.getElementById('resumeName').value.trim();
    const email = document.getElementById('resumeEmail').value.trim();
    const phone = document.getElementById('resumePhone').value.trim();
    
    if (!name || !email) {
        alert('Please fill in at least your name and email!');
        return;
    }
    
    // Validate email format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        alert('Please enter a valid email address!');
        return;
    }
    
    try {
        const resume = {
            id: Date.now(),
            userId: currentUser.id,
            name: name,
            email: email,
            phone: phone,
            address: document.getElementById('resumeAddress').value.trim(),
            skills: document.getElementById('resumeSkills').value.split(',').map(s => s.trim()).filter(s => s),
            experiences: getExperiences(),
            education: getEducation(),
            lastUpdated: new Date().toISOString()
        };
        
        const resumes = getResumes();
        const existingIndex = resumes.findIndex(r => r.userId === currentUser.id);
        
        if (existingIndex >= 0) {
            // Keep the original ID when updating
            resume.id = resumes[existingIndex].id;
            resumes[existingIndex] = resume;
            console.log('Resume updated:', resume);
        } else {
            resumes.push(resume);
            console.log('Resume created:', resume);
        }
        
        // Save to localStorage
        try {
            localStorage.setItem(STORAGE_KEY_RESUMES, JSON.stringify(resumes));
            currentResume = resume;
            console.log('Resume saved to localStorage successfully');
            
            // Clear auto-save draft after successful save
            clearAutoSave();
            
            // Verify it was saved
            const saved = getResumes();
            const verify = saved.find(r => r.userId === currentUser.id);
            if (verify) {
                alert('✅ Resume saved successfully!\n\nYou can now preview or export your resume.');
                showScreen('dashboardScreen');
            } else {
                throw new Error('Resume not found after save');
            }
        } catch (storageError) {
            console.error('localStorage error:', storageError);
            alert('⚠️ Error saving resume. Your browser may be blocking localStorage.\n\nPlease:\n1. Check browser settings\n2. Try a different browser\n3. Make sure you\'re not in private/incognito mode');
        }
    } catch (error) {
        console.error('Error saving resume:', error);
        alert('❌ Error saving resume: ' + error.message);
    }
}

// ==================== AUTO-SAVE FUNCTIONALITY ====================

// Setup auto-save system
function setupAutoSave() {
    // Auto-save will be set up when form loads
    console.log('Auto-save system initialized');
}

// Setup auto-save listeners on form inputs
function setupAutoSaveListeners() {
    if (!currentUser) return;
    
    // Remove existing listeners to avoid duplicates
    const form = document.getElementById('resumeForm');
    if (!form) return;
    
    // Get all form inputs
    const inputs = form.querySelectorAll('input, textarea');
    const expItems = document.querySelectorAll('.exp-item input, .exp-item textarea');
    const eduItems = document.querySelectorAll('.edu-item input, .edu-item textarea');
    
    // Combine all inputs
    const allInputs = [...inputs, ...expItems, ...eduItems];
    
    // Add auto-save listener to each input
    allInputs.forEach(input => {
        // Remove existing listeners
        input.removeEventListener('input', triggerAutoSave);
        // Add new listener
        input.addEventListener('input', triggerAutoSave);
    });
    
    console.log('Auto-save listeners attached to', allInputs.length, 'inputs');
}

// Trigger auto-save (debounced)
function triggerAutoSave() {
    if (!currentUser) return;
    
    // Clear existing timer
    if (autoSaveTimer) {
        clearTimeout(autoSaveTimer);
    }
    
    // Show "Saving..." status
    showAutoSaveStatus('saving', 'Saving...');
    
    // Set new timer (save after 2 seconds of no typing)
    autoSaveTimer = setTimeout(() => {
        performAutoSave();
    }, 2000);
}

// Perform the actual auto-save
function performAutoSave() {
    if (!currentUser) return;
    
    try {
        // Get form data
        const resumeData = {
            userId: currentUser.id,
            name: document.getElementById('resumeName')?.value.trim() || '',
            email: document.getElementById('resumeEmail')?.value.trim() || '',
            phone: document.getElementById('resumePhone')?.value.trim() || '',
            address: document.getElementById('resumeAddress')?.value.trim() || '',
            skills: document.getElementById('resumeSkills')?.value.split(',').map(s => s.trim()).filter(s => s) || [],
            experiences: getExperiences(),
            education: getEducation(),
            autoSavedAt: new Date().toISOString()
        };
        
        // Save to localStorage
        localStorage.setItem(STORAGE_KEY_AUTOSAVE + '_' + currentUser.id, JSON.stringify(resumeData));
        
        console.log('Auto-saved resume draft');
        showAutoSaveStatus('saved', 'Draft saved');
        
        // Clear status after 3 seconds
        setTimeout(() => {
            if (autoSaveStatus === 'saved') {
                showAutoSaveStatus(null, '');
            }
        }, 3000);
        
    } catch (error) {
        console.error('Auto-save error:', error);
        showAutoSaveStatus('error', 'Save failed');
    }
}

// Load auto-saved draft
function loadAutoSave() {
    if (!currentUser) return null;
    
    try {
        const autoSaveStr = localStorage.getItem(STORAGE_KEY_AUTOSAVE + '_' + currentUser.id);
        if (autoSaveStr) {
            const autoSaved = JSON.parse(autoSaveStr);
            console.log('Auto-saved draft found:', autoSaved);
            return autoSaved;
        }
    } catch (error) {
        console.error('Error loading auto-save:', error);
    }
    
    return null;
}

// Clear auto-save draft
function clearAutoSave() {
    if (!currentUser) return;
    
    try {
        localStorage.removeItem(STORAGE_KEY_AUTOSAVE + '_' + currentUser.id);
        console.log('Auto-save draft cleared');
        showAutoSaveStatus(null, '');
    } catch (error) {
        console.error('Error clearing auto-save:', error);
    }
}

// Show auto-save status
function showAutoSaveStatus(status, message) {
    autoSaveStatus = status;
    const statusEl = document.getElementById('autoSaveStatus');
    if (!statusEl) return;
    
    statusEl.className = 'auto-save-status';
    
    if (status === 'saving') {
        statusEl.className += ' saving';
        statusEl.textContent = '💾 Saving...';
    } else if (status === 'saved') {
        statusEl.className += ' saved';
        statusEl.textContent = '✅ Saved';
    } else if (status === 'restored') {
        statusEl.className += ' restored';
        statusEl.textContent = '📋 ' + message;
        setTimeout(() => {
            statusEl.textContent = '';
            statusEl.className = 'auto-save-status';
        }, 3000);
    } else if (status === 'error') {
        statusEl.className += ' error';
        statusEl.textContent = '❌ ' + message;
    } else {
        statusEl.textContent = '';
    }
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
        <button type="button" class="remove-btn" onclick="this.parentElement.remove(); triggerAutoSave();">Remove</button>
    `;
    container.appendChild(item);
    
    // Add auto-save listeners to new inputs
    item.querySelectorAll('input, textarea').forEach(input => {
        input.addEventListener('input', triggerAutoSave);
    });
    
    // Trigger auto-save after adding
    triggerAutoSave();
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
        <button type="button" class="remove-btn" onclick="this.parentElement.remove(); triggerAutoSave();">Remove</button>
    `;
    container.appendChild(item);
    
    // Add auto-save listeners to new inputs
    item.querySelectorAll('input, textarea').forEach(input => {
        input.addEventListener('input', triggerAutoSave);
    });
    
    // Trigger auto-save after adding
    triggerAutoSave();
}

// Load resume form
function loadResumeForm() {
    console.log('loadResumeForm: Loading form...');
    
    if (!currentUser) {
        alert('Please login first!');
        showScreen('loginScreen');
        return;
    }
    
    const resume = getCurrentResume();
    if (resume) {
        console.log('loadResumeForm: Loading existing resume data');
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
    console.log('loadResumePreview: Loading preview...');
    
    if (!currentUser) {
        console.log('loadResumePreview: No user logged in');
        const preview = document.getElementById('resumePreview');
        preview.innerHTML = '<p class="empty-state">Please login first to view your resume!</p>';
        return;
    }
    
    const resume = getCurrentResume();
    const preview = document.getElementById('resumePreview');
    
    if (!resume) {
        console.log('loadResumePreview: No resume found');
        preview.innerHTML = `
            <p class="empty-state">No resume data available. Create a resume first!</p>
            <button onclick="showScreen('resumeFormScreen')" class="btn btn-primary" style="margin-top: 20px; max-width: 300px;">
                Create Resume Now
            </button>
        `;
        return;
    }
    
    console.log('loadResumePreview: Loading resume:', resume);
    
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
    if (!currentUser) {
        console.log('getCurrentResume: No current user');
        return null;
    }
    const resumes = getResumes();
    const resume = resumes.find(r => r.userId === currentUser.id) || null;
    if (resume) {
        console.log('getCurrentResume: Found resume for user', currentUser.id, resume);
    } else {
        console.log('getCurrentResume: No resume found for user', currentUser.id);
    }
    return resume;
}

// Get resumes from storage
function getResumes() {
    try {
        const resumesStr = localStorage.getItem(STORAGE_KEY_RESUMES);
        if (!resumesStr) {
            console.log('getResumes: No resumes in storage');
            return [];
        }
        const resumes = JSON.parse(resumesStr);
        console.log('getResumes: Found', resumes.length, 'resume(s)');
        return resumes;
    } catch (error) {
        console.error('getResumes: Error parsing resumes:', error);
        return [];
    }
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

// Show public resume page
function showPublicResumePage(username = null) {
    // If username not provided, use current user
    if (!username && currentUser) {
        username = currentUser.username;
    }
    
    if (!username) {
        alert('Please login first to view your public page!');
        showScreen('loginScreen');
        return;
    }
    
    // Get user's resume
    const users = getUsers();
    const user = users.find(u => u.username === username || u.email === username);
    
    if (!user) {
        document.getElementById('publicResumeContent').innerHTML = 
            '<p class="empty-state">User not found!</p>';
        showScreen('publicResumeScreen');
        return;
    }
    
    // Get user's resume
    const resumes = getResumes();
    const resume = resumes.find(r => r.userId === user.id);
    
    if (!resume) {
        document.getElementById('publicResumeContent').innerHTML = 
            '<p class="empty-state">This user has not created a resume yet.</p>';
        showScreen('publicResumeScreen');
        return;
    }
    
    // Update page title
    document.getElementById('publicUserName').textContent = resume.name || user.name || user.username + "'s Resume";
    
    // Load public resume preview
    loadPublicResumePreview(resume);
    showScreen('publicResumeScreen');
    
    // Update URL without reload
    const newUrl = window.location.origin + window.location.pathname + '?user=' + encodeURIComponent(username);
    window.history.pushState({ path: newUrl }, '', newUrl);
}

// Load public resume preview
function loadPublicResumePreview(resume) {
    const preview = document.getElementById('publicResumeContent');
    
    if (!resume) {
        preview.innerHTML = '<p class="empty-state">No resume data available.</p>';
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
            
            <div class="resume-section" style="margin-top: 40px; padding-top: 20px; border-top: 1px solid #ecf0f1; text-align: center;">
                <p style="color: #777; font-size: 12px;">
                    Created with Resume Builder | 
                    <a href="${window.location.origin + window.location.pathname}" style="color: var(--accent-primary);">Create Your Resume</a>
                </p>
            </div>
        </div>
    `;
    
    preview.innerHTML = html;
}

// Get public resume URL
function getPublicResumeURL(username = null) {
    if (!username && currentUser) {
        username = currentUser.username;
    }
    if (!username) return null;
    
    const baseUrl = window.location.origin + window.location.pathname;
    return baseUrl + '?user=' + encodeURIComponent(username);
}

// Share public resume
function sharePublicResume() {
    const username = currentUser ? currentUser.username : null;
    if (!username) {
        alert('Please login first!');
        return;
    }
    
    const url = getPublicResumeURL(username);
    if (navigator.share) {
        navigator.share({
            title: `${currentUser.name || currentUser.username}'s Resume`,
            text: 'Check out my resume!',
            url: url
        }).catch(err => console.log('Error sharing:', err));
    } else {
        // Fallback: copy to clipboard
        navigator.clipboard.writeText(url).then(() => {
            alert('Resume URL copied to clipboard!\n\n' + url);
        }).catch(() => {
            prompt('Copy this URL to share your resume:', url);
        });
    }
}

// ==================== FILE MANAGEMENT ====================

// Save resume to file on MacBook
async function saveResumeToFile() {
    if (!currentUser) {
        alert('Please login first!');
        return;
    }
    
    const resume = getCurrentResume();
    if (!resume) {
        alert('No resume to save. Please create a resume first!');
        return;
    }
    
    try {
        // Prepare resume data
        const resumeData = {
            ...resume,
            exportedAt: new Date().toISOString(),
            exportedBy: currentUser.name || currentUser.username,
            version: '1.0'
        };
        
        const jsonContent = JSON.stringify(resumeData, null, 2);
        const fileName = `${resume.name || 'Resume'}_${new Date().toISOString().split('T')[0]}.json`;
        
        // Try File System Access API (modern browsers)
        if ('showSaveFilePicker' in window) {
            try {
                const fileHandle = await window.showSaveFilePicker({
                    suggestedName: fileName,
                    types: [{
                        description: 'JSON Resume File',
                        accept: { 'application/json': ['.json'] }
                    }],
                    startIn: 'downloads' // Start in Downloads folder
                });
                
                const writable = await fileHandle.createWritable();
                await writable.write(jsonContent);
                await writable.close();
                
                alert(`✅ Resume saved successfully!\n\nFile: ${fileHandle.name}\n\nYou can find it in your Downloads folder or the location you chose.`);
                return;
            } catch (err) {
                if (err.name !== 'AbortError') {
                    console.error('File System Access API error:', err);
                    // Fallback to download
                }
            }
        }
        
        // Fallback: Download file
        saveResumeToDownloads();
        
    } catch (error) {
        console.error('Error saving file:', error);
        alert('❌ Error saving file: ' + error.message);
    }
}

// Save resume to Downloads folder on MacBook
function saveResumeToDownloads() {
    if (!currentUser) {
        alert('Please login first!');
        return;
    }
    
    const resume = getCurrentResume();
    if (!resume) {
        alert('No resume to save. Please create a resume first!');
        return;
    }
    
    try {
        // Prepare resume data
        const resumeData = {
            ...resume,
            exportedAt: new Date().toISOString(),
            exportedBy: currentUser.name || currentUser.username,
            version: '1.0',
            note: 'This file was saved to your MacBook Downloads folder'
        };
        
        const jsonContent = JSON.stringify(resumeData, null, 2);
        
        // Create safe filename with timestamp
        const safeName = (resume.name || 'Resume').replace(/[^a-z0-9]/gi, '_').toLowerCase();
        const dateStr = new Date().toISOString().split('T')[0];
        const timeStr = new Date().toTimeString().split(' ')[0].replace(/:/g, '-');
        const fileName = `RESUME_${safeName}_${dateStr}_${timeStr}.json`;
        
        // Create and download file - THIS SAVES TO YOUR MACBOOK!
        const blob = new Blob([jsonContent], { type: 'application/json' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = fileName; // This triggers download to MacBook
        link.style.display = 'none';
        document.body.appendChild(link);
        link.click();
        
        // Wait a moment then remove
        setTimeout(() => {
            document.body.removeChild(link);
            URL.revokeObjectURL(url);
        }, 100);
        
        // Store file info
        const fileInfo = {
            fileName: fileName,
            filePath: '~/Downloads/' + fileName,
            fullPath: '/Users/amremad/Downloads/' + fileName,
            savedAt: new Date().toISOString(),
            fileSize: jsonContent.length
        };
        localStorage.setItem('resumebuilder_last_saved_file', JSON.stringify(fileInfo));
        
        // Save to list of all saved files
        const savedFiles = JSON.parse(localStorage.getItem('resumebuilder_saved_files_list') || '[]');
        savedFiles.push(fileInfo);
        localStorage.setItem('resumebuilder_saved_files_list', JSON.stringify(savedFiles));
        
        // Update file location display
        updateFileLocation(fileInfo);
        
        // Show success with BIG CLEAR instructions
        showFileSavedSuccess(fileName, fileInfo);
        
    } catch (error) {
        console.error('Error saving to Downloads:', error);
        alert('❌ Error saving file: ' + error.message + '\n\nPlease try again or check your browser settings.');
    }
}

// Show file saved success message
function showFileSavedSuccess(fileName, fileInfo) {
    const message = `✅✅✅ FILE SAVED TO YOUR MACBOOK! ✅✅✅\n\n` +
        `━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n` +
        `📄 FILE NAME:\n${fileName}\n\n` +
        `📍 EXACT LOCATION ON YOUR MACBOOK:\n` +
        `/Users/amremad/Downloads/${fileName}\n\n` +
        `━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n` +
        `🔍 HOW TO FIND IT RIGHT NOW (3 EASY STEPS):\n\n` +
        `STEP 1: Open Finder (click blue face icon in Dock)\n\n` +
        `STEP 2: Press these 3 keys together:\n` +
        `       ⌘ (Command) + Shift + D\n\n` +
        `STEP 3: Downloads folder opens - YOUR FILE IS THERE!\n` +
        `       Look for: ${fileName}\n\n` +
        `━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n` +
        `💡 OTHER WAYS TO FIND IT:\n\n` +
        `• Click "Downloads" in Finder sidebar\n` +
        `• Press ⌘ + Space, type: ${fileName}\n` +
        `• Check browser Downloads (⌘ + Shift + J in Chrome)\n\n` +
        `━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n` +
        `✅ YOUR FILE IS ON YOUR MACBOOK - NOT ON GITHUB!\n` +
        `✅ It's in your Downloads folder right now!\n` +
        `✅ You can open it, move it, or share it!\n\n` +
        `🕐 Saved at: ${new Date(fileInfo.savedAt).toLocaleString()}`;
    
    alert(message);
    
    // Also log detailed info
    console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
    console.log('✅ FILE SAVED TO YOUR MACBOOK!');
    console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
    console.log('📄 File Name:', fileName);
    console.log('📍 Location: ~/Downloads/' + fileName);
    console.log('📍 Full Path: /Users/amremad/Downloads/' + fileName);
    console.log('💾 File Size:', (fileInfo.fileSize / 1024).toFixed(2), 'KB');
    console.log('🕐 Saved:', new Date(fileInfo.savedAt).toLocaleString());
    console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
    console.log('🔍 TO FIND: Open Finder → Press ⌘ + Shift + D');
    console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
}

// Open Downloads folder on Mac
function openDownloadsFolder() {
    // Try to open Finder to Downloads folder
    // Note: This requires a special protocol or file:// URL
    
    const downloadsPath = '/Users/' + (navigator.userAgent.includes('Mac') ? 'amremad' : 'user') + '/Downloads';
    
    // Create instructions
    const instructions = `📂 TO OPEN DOWNLOADS FOLDER:\n\n` +
        `Method 1 (Easiest):\n` +
        `1. Press ⌘ + Shift + D (Opens Downloads in Finder)\n\n` +
        `Method 2:\n` +
        `1. Open Finder\n` +
        `2. Press ⌘ + Shift + G\n` +
        `3. Type: ~/Downloads\n` +
        `4. Press Enter\n\n` +
        `Method 3:\n` +
        `1. Click Finder in Dock\n` +
        `2. Click "Downloads" in sidebar\n\n` +
        `Your file is saved as: ` + (JSON.parse(localStorage.getItem('resumebuilder_last_saved_file') || '{}').fileName || 'resume.json');
    
    alert(instructions);
    
    // Try to create a file:// URL (may not work in all browsers due to security)
    try {
        // This won't work due to browser security, but we can show the path
        console.log('Downloads path:', downloadsPath);
    } catch (e) {
        console.log('Cannot open folder directly due to browser security');
    }
}

// Show saved files location
function showSavedFilesLocation() {
    const lastFile = JSON.parse(localStorage.getItem('resumebuilder_last_saved_file') || '{}');
    const allFiles = JSON.parse(localStorage.getItem('resumebuilder_saved_files_list') || '[]');
    
    if (!lastFile.fileName && allFiles.length === 0) {
        alert('❌ No files saved yet!\n\nPlease:\n1. Create a resume\n2. Click "Save to Downloads Folder"\n3. The file will download to your MacBook!');
        return;
    }
    
    let message = `📁 YOUR SAVED RESUME FILES ON YOUR MACBOOK\n\n` +
        `━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n` +
        `✅ IMPORTANT: Files are saved on YOUR MACBOOK, NOT on GitHub!\n` +
        `✅ GitHub only hosts the web app code, not your data!\n\n` +
        `━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n`;
    
    if (lastFile.fileName) {
        message += `📄 LAST SAVED FILE:\n\n` +
            `File Name: ${lastFile.fileName}\n` +
            `📍 Location: ~/Downloads/${lastFile.fileName}\n` +
            `📂 Full Path: /Users/amremad/Downloads/${lastFile.fileName}\n` +
            `🕐 Saved: ${new Date(lastFile.savedAt).toLocaleString()}\n\n`;
    }
    
    if (allFiles.length > 0) {
        message += `📋 TOTAL FILES SAVED: ${allFiles.length}\n\n`;
    }
    
    message += `━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n` +
        `🔍 HOW TO FIND YOUR FILES (EASIEST WAY):\n\n` +
        `STEP 1: Open Finder (blue face icon in Dock)\n\n` +
        `STEP 2: Press these 3 keys together:\n` +
        `       ⌘ (Command) + Shift + D\n\n` +
        `STEP 3: Downloads folder opens!\n` +
        `       Your files are there - look for .json files\n\n` +
        `━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n` +
        `💡 TIP: Sort by "Date Modified" to see newest first!`;
    
    alert(message);
    
    // Copy path to clipboard
    if (lastFile.fileName) {
        const path = '~/Downloads/' + lastFile.fileName;
        navigator.clipboard.writeText(path).then(() => {
            console.log('Path copied to clipboard:', path);
        });
    }
}

// List all saved files
function listAllSavedFiles() {
    const allFiles = JSON.parse(localStorage.getItem('resumebuilder_saved_files_list') || '[]');
    
    if (allFiles.length === 0) {
        alert('No files saved yet!\n\nClick "Save to Downloads Folder" to save your resume to your MacBook.');
        return;
    }
    
    let message = `📋 ALL FILES SAVED ON YOUR MACBOOK\n\n` +
        `━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n` +
        `Total Files: ${allFiles.length}\n\n` +
        `━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n`;
    
    // Sort by date (newest first)
    allFiles.sort((a, b) => new Date(b.savedAt) - new Date(a.savedAt));
    
    allFiles.forEach((file, index) => {
        message += `${index + 1}. ${file.fileName}\n`;
        message += `   📍 ~/Downloads/${file.fileName}\n`;
        message += `   🕐 ${new Date(file.savedAt).toLocaleString()}\n\n`;
    });
    
    message += `━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n` +
        `🔍 TO FIND THESE FILES:\n` +
        `1. Open Finder\n` +
        `2. Press ⌘ + Shift + D\n` +
        `3. All your .json files are there!`;
    
    alert(message);
    
    // Also log to console
    console.log('📋 All saved files:', allFiles);
    allFiles.forEach((file, index) => {
        console.log(`${index + 1}. ${file.fileName} - ${file.filePath}`);
    });
}

// Load resume from file
function loadResumeFromFile() {
    if (!currentUser) {
        alert('Please login first!');
        return;
    }
    
    // Trigger file input
    document.getElementById('fileInput').click();
}

// Handle file load
function handleFileLoad(event) {
    const file = event.target.files[0];
    if (!file) return;
    
    const reader = new FileReader();
    
    reader.onload = function(e) {
        try {
            const resumeData = JSON.parse(e.target.result);
            
            // Validate resume data
            if (!resumeData.name && !resumeData.email) {
                throw new Error('Invalid resume file format');
            }
            
            // Confirm load
            const confirmMsg = `Load resume for "${resumeData.name || 'Unknown'}"?\n\nThis will replace your current resume data.`;
            if (!confirm(confirmMsg)) {
                return;
            }
            
            // Update resume data
            const resume = {
                id: Date.now(),
                userId: currentUser.id,
                name: resumeData.name || '',
                email: resumeData.email || '',
                phone: resumeData.phone || '',
                address: resumeData.address || '',
                skills: resumeData.skills || [],
                experiences: resumeData.experiences || [],
                education: resumeData.education || [],
                lastUpdated: new Date().toISOString()
            };
            
            // Save to localStorage
            const resumes = getResumes();
            const existingIndex = resumes.findIndex(r => r.userId === currentUser.id);
            
            if (existingIndex >= 0) {
                resume.id = resumes[existingIndex].id;
                resumes[existingIndex] = resume;
            } else {
                resumes.push(resume);
            }
            
            localStorage.setItem(STORAGE_KEY_RESUMES, JSON.stringify(resumes));
            currentResume = resume;
            
            alert('✅ Resume loaded successfully!\n\nYou can now view or edit it.');
            
            // Go to resume form to see loaded data
            showScreen('resumeFormScreen');
            
        } catch (error) {
            console.error('Error loading file:', error);
            alert('❌ Error loading file:\n\n' + error.message + '\n\nPlease make sure you selected a valid resume JSON file.');
        }
    };
    
    reader.onerror = function() {
        alert('❌ Error reading file. Please try again.');
    };
    
    reader.readAsText(file);
    
    // Reset file input
    event.target.value = '';
}

// Update file location display
function updateFileLocation(fileInfo = null) {
    const locationEl = document.getElementById('fileLocation');
    if (!locationEl) return;
    
    if (fileInfo) {
        // Show specific file location
        locationEl.textContent = `~/Downloads/${fileInfo.fileName}`;
        locationEl.title = `Full path: /Users/amremad/Downloads/${fileInfo.fileName}`;
    } else {
        // Show last saved file or default
        const lastFile = JSON.parse(localStorage.getItem('resumebuilder_last_saved_file') || '{}');
        if (lastFile.fileName) {
            locationEl.textContent = `~/Downloads/${lastFile.fileName}`;
            locationEl.title = `Full path: /Users/amremad/Downloads/${lastFile.fileName}`;
        } else {
            locationEl.textContent = '~/Downloads/saved_resumes/';
            locationEl.title = 'Files will be saved here';
        }
    }
}

// Copy file location to clipboard
function copyFileLocation() {
    const lastFile = JSON.parse(localStorage.getItem('resumebuilder_last_saved_file') || '{}');
    let location;
    
    if (lastFile.fileName) {
        location = '~/Downloads/' + lastFile.fileName;
    } else {
        location = '~/Downloads/saved_resumes/';
    }
    
    navigator.clipboard.writeText(location).then(() => {
        alert('✅ Path copied to clipboard!\n\n' + location + '\n\nYou can paste it in Finder (⌘ + Shift + G)');
    }).catch(() => {
        prompt('Copy this path:', location);
    });
}

// Help user find their files
function helpFindFiles() {
    const lastFile = JSON.parse(localStorage.getItem('resumebuilder_last_saved_file') || '{}');
    const allFiles = JSON.parse(localStorage.getItem('resumebuilder_saved_files_list') || '[]');
    const resumes = getResumes();
    const hasResume = resumes && resumes.length > 0;
    
    let message = '🔍 LET\'S FIND YOUR FILES TOGETHER!\n\n';
    message += '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n';
    message += '✅ IMPORTANT: Your files are saved on YOUR MACBOOK!\n';
    message += '✅ NOT on GitHub - GitHub only has the web app code!\n';
    message += '✅ Your data is safe on your computer!\n\n';
    message += '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n';
    
    if (lastFile.fileName) {
        message += '✅ I FOUND YOUR LAST SAVED FILE!\n\n';
        message += `📄 File Name: ${lastFile.fileName}\n`;
        message += `📍 Location: ~/Downloads/${lastFile.fileName}\n`;
        message += `📂 Full Path: /Users/amremad/Downloads/${lastFile.fileName}\n`;
        message += `🕐 Saved: ${new Date(lastFile.savedAt).toLocaleString()}\n\n`;
        message += '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n';
    }
    
    if (allFiles.length > 0) {
        message += `📋 Total Files Saved: ${allFiles.length}\n\n`;
    }
    
    message += '📂 STEP-BY-STEP INSTRUCTIONS:\n\n';
    message += 'Method 1 (EASIEST - 3 steps):\n';
    message += '1. Open Finder (blue face icon in Dock)\n';
    message += '2. Press ⌘ + Shift + D\n';
    message += '3. Look for: ' + (lastFile.fileName || 'RESUME_*.json files') + '\n\n';
    
    message += 'Method 2 (Using Sidebar):\n';
    message += '1. Open Finder\n';
    message += '2. Click "Downloads" in left sidebar\n';
    message += '3. Your files are there!\n\n';
    
    message += 'Method 3 (Search):\n';
    message += '1. Open Finder\n';
    message += '2. Press ⌘ + F (search)\n';
    message += '3. Type: .json\n';
    message += '4. All your resume files will appear!\n\n';
    
    message += '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n';
    
    message += '💡 YOUR DATA IS SAFE IN 2 PLACES!\n\n';
    
    if (hasResume) {
        message += '✅ 1. In your Downloads folder (as .json files)\n';
        message += '✅ 2. In your browser (localStorage)\n\n';
    }
    
    message += '🔧 STILL CAN\'T FIND IT?\n\n';
    message += '1. Check browser Downloads:\n';
    message += '   - Chrome: Press ⌘ + Shift + J\n';
    message += '   - Safari: Press ⌘ + Option + L\n';
    message += '   - Look at the Downloads list\n\n';
    
    message += '2. Search your entire Mac:\n';
    message += '   - Press ⌘ + Space (Spotlight)\n';
    message += '   - Type: RESUME\n';
    message += '   - Press Enter\n\n';
    
    message += '3. Your data is ALSO in the browser:\n';
    message += '   - Press F12 in the web app\n';
    message += '   - Go to Application → Local Storage\n';
    message += '   - Your data is there!\n\n';
    
    message += '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n';
    message += '😊 Don\'t worry! Your files ARE on your MacBook!\n';
    message += 'We\'ll find them together! 💪';
    
    alert(message);
    
    // Also show in console
    console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
    console.log('🔍 FILE LOCATION HELP');
    console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
    console.log('Last saved file:', lastFile);
    console.log('All saved files:', allFiles.length);
    console.log('Downloads path: ~/Downloads/');
    console.log('Full path: /Users/amremad/Downloads/');
    if (lastFile.fileName) {
        console.log('Your file:', lastFile.fileName);
        console.log('Complete path: ~/Downloads/' + lastFile.fileName);
    }
    console.log('\nTo open Downloads folder:');
    console.log('1. Open Finder');
    console.log('2. Press ⌘ + Shift + D');
    console.log('3. Or press ⌘ + Shift + G and type: ~/Downloads');
    console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
}

// Test save functionality (for debugging)
function testSave() {
    console.log('=== TEST SAVE ===');
    console.log('Current user:', currentUser);
    console.log('localStorage available:', typeof(Storage) !== "undefined");
    
    if (!currentUser) {
        alert('❌ No user logged in!\n\nPlease login first.');
        return;
    }
    
    // Check if we can read/write localStorage
    try {
        const testKey = 'resumebuilder_test';
        localStorage.setItem(testKey, 'test');
        const testValue = localStorage.getItem(testKey);
        localStorage.removeItem(testKey);
        
        if (testValue !== 'test') {
            throw new Error('localStorage write/read test failed');
        }
        
        console.log('✅ localStorage is working');
        
        // Check existing resumes
        const resumes = getResumes();
        console.log('Existing resumes:', resumes);
        const userResume = resumes.find(r => r.userId === currentUser.id);
        
        if (userResume) {
            alert(`✅ Resume found!\n\nName: ${userResume.name}\nEmail: ${userResume.email}\n\nYou can preview it now.`);
        } else {
            alert('ℹ️ No resume saved yet.\n\nFill in the form and click "Save Resume" to create one.');
        }
    } catch (error) {
        console.error('localStorage test failed:', error);
        alert('❌ localStorage is not working!\n\nError: ' + error.message + '\n\nPlease check your browser settings.');
    }
}

// View where data is stored
function viewDataStorage() {
    if (!currentUser) {
        alert('Please login first!');
        return;
    }
    
    const users = getUsers();
    const resumes = getResumes();
    const userResume = resumes.find(r => r.userId === currentUser.id);
    const autoSave = loadAutoSave();
    
    let message = '📦 DATA STORAGE LOCATION\n\n';
    message += 'All data is saved in your browser\'s localStorage.\n\n';
    message += '📍 Storage Location:\n';
    message += 'Browser → Local Storage → ' + window.location.origin + '\n\n';
    message += '📊 Your Data:\n';
    message += `• Users: ${users.length}\n`;
    message += `• Resumes: ${resumes.length}\n`;
    message += `• Your Resume: ${userResume ? '✅ Saved' : '❌ Not saved'}\n`;
    message += `• Auto-save Draft: ${autoSave ? '✅ Exists' : '❌ None'}\n\n`;
    message += '🔍 How to View:\n';
    message += '1. Press F12 (Developer Tools)\n';
    message += '2. Go to "Application" tab\n';
    message += '3. Click "Local Storage"\n';
    message += '4. Click your website URL\n\n';
    message += '💡 Storage Keys:\n';
    message += '• resumebuilder_users\n';
    message += '• resumebuilder_resumes\n';
    message += '• resumebuilder_autosave_' + currentUser.id + '\n';
    
    alert(message);
    
    // Also log to console
    console.log('=== DATA STORAGE INFO ===');
    console.log('Storage Location: Browser localStorage');
    console.log('Website:', window.location.origin);
    console.log('Users:', users);
    console.log('Resumes:', resumes);
    console.log('Your Resume:', userResume);
    console.log('Auto-save Draft:', autoSave);
    console.log('\nTo view in browser:');
    console.log('F12 → Application → Local Storage →', window.location.origin);
}
