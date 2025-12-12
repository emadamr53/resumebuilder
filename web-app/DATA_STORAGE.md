# 📦 Data Storage Guide

## Where Your Data is Saved

All data in the Resume Builder web app is saved in your **browser's localStorage**. This is client-side storage, meaning:

- ✅ Data is stored **locally on your device**
- ✅ Data is **private** to your browser
- ✅ Data **persists** even after closing the browser
- ✅ Data is **per-browser** (Chrome, Firefox, Safari each have separate storage)
- ✅ Data is **per-domain** (only accessible on the same website)

## Storage Keys Used

The app uses these localStorage keys:

1. **`resumebuilder_users`** - All user accounts (sign up data)
2. **`resumebuilder_current_user`** - Currently logged in user
3. **`resumebuilder_resumes`** - All saved resumes
4. **`resumebuilder_autosave_[userId]`** - Auto-saved drafts (per user)
5. **`resumebuilder_theme`** - Selected CV theme
6. **`resumebuilder_dark_mode`** - Dark/light mode preference

## How to View Your Data

### Method 1: Browser Developer Tools (Recommended)

1. **Open the web app** in your browser
2. **Press F12** (or Right-click → Inspect)
3. Go to **Application** tab (Chrome/Edge) or **Storage** tab (Firefox)
4. Click **Local Storage** in the left sidebar
5. Click on your website URL (e.g., `https://emadamr53.github.io`)
6. You'll see all the stored data keys and values

### Method 2: Browser Console

1. **Press F12** to open Developer Tools
2. Go to **Console** tab
3. Type these commands:

```javascript
// View all users
JSON.parse(localStorage.getItem('resumebuilder_users'))

// View current user
JSON.parse(localStorage.getItem('resumebuilder_current_user'))

// View all resumes
JSON.parse(localStorage.getItem('resumebuilder_resumes'))

// View auto-save draft (replace USER_ID with your user ID)
JSON.parse(localStorage.getItem('resumebuilder_autosave_USER_ID'))
```

### Method 3: Check Storage Size

```javascript
// Check how much storage is used
let total = 0;
for (let key in localStorage) {
    if (localStorage.hasOwnProperty(key)) {
        total += localStorage[key].length + key.length;
    }
}
console.log('Total storage used:', (total / 1024).toFixed(2), 'KB');
```

## Data Structure

### User Data
```json
{
  "id": 1234567890,
  "name": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "password": "plaintext_password"
}
```

### Resume Data
```json
{
  "id": 1234567891,
  "userId": 1234567890,
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "123-456-7890",
  "address": "123 Main St",
  "skills": ["Java", "Python", "React"],
  "experiences": [
    {
      "jobTitle": "Software Engineer",
      "company": "Tech Corp",
      "location": "New York",
      "startDate": "Jan 2020",
      "endDate": "Present",
      "description": "Developed web applications..."
    }
  ],
  "education": [
    {
      "institution": "University",
      "degree": "Bachelor's",
      "field": "Computer Science",
      "year": "2020",
      "gpa": "3.8"
    }
  ],
  "lastUpdated": "2024-11-30T12:00:00.000Z"
}
```

## Important Notes

### ⚠️ Data is Browser-Specific
- Data saved in Chrome won't appear in Firefox
- Data saved on one computer won't appear on another
- Clearing browser data will delete everything

### ⚠️ Data is Not Synced
- No cloud backup
- No sync between devices
- No server-side storage

### ⚠️ Privacy
- Data is stored locally on your device
- Not sent to any server
- Only accessible on the same browser/device

## Exporting Your Data

### Export Resume as JSON

You can export your resume data manually:

```javascript
// In browser console (F12)
const resume = JSON.parse(localStorage.getItem('resumebuilder_resumes'));
const currentUser = JSON.parse(localStorage.getItem('resumebuilder_current_user'));
const myResume = resume.find(r => r.userId === currentUser.id);

// Copy to clipboard
navigator.clipboard.writeText(JSON.stringify(myResume, null, 2));
console.log('Resume data copied to clipboard!');
```

### Backup All Data

```javascript
// Backup everything
const backup = {
    users: JSON.parse(localStorage.getItem('resumebuilder_users') || '[]'),
    currentUser: JSON.parse(localStorage.getItem('resumebuilder_current_user') || 'null'),
    resumes: JSON.parse(localStorage.getItem('resumebuilder_resumes') || '[]'),
    theme: localStorage.getItem('resumebuilder_theme'),
    darkMode: localStorage.getItem('resumebuilder_dark_mode')
};

// Download as file
const blob = new Blob([JSON.stringify(backup, null, 2)], { type: 'application/json' });
const url = URL.createObjectURL(blob);
const a = document.createElement('a');
a.href = url;
a.download = 'resume-builder-backup.json';
a.click();
```

## Clearing Data

### Clear All Data
```javascript
// WARNING: This will delete everything!
localStorage.clear();
location.reload();
```

### Clear Specific Data
```javascript
// Clear only resumes
localStorage.removeItem('resumebuilder_resumes');

// Clear only auto-save
const userId = JSON.parse(localStorage.getItem('resumebuilder_current_user')).id;
localStorage.removeItem('resumebuilder_autosave_' + userId);
```

## Storage Limits

- **localStorage limit**: Usually 5-10 MB per domain
- **Typical resume size**: ~1-5 KB
- **Can store**: Hundreds of resumes easily

## Troubleshooting

### Data Not Saving?
1. Check if localStorage is enabled in browser settings
2. Check if you're in private/incognito mode (may have restrictions)
3. Check browser console for errors (F12)
4. Try a different browser

### Data Disappeared?
1. Check if you cleared browser data
2. Check if you're using a different browser
3. Check if you're on a different device
4. Check if localStorage is full (unlikely)

### Want to Transfer Data?
1. Export data using the backup method above
2. Open the app on the new browser/device
3. Import the JSON data using browser console

## Future Enhancements

Possible future features:
- Cloud sync (Google Drive, Dropbox)
- Server-side storage
- Data export/import UI
- Multi-device sync

---

**Remember**: All data is stored locally in your browser. Make backups if you need to keep your data safe!

