# 📦 Where Your Data is Saved

## Quick Answer

**All data is saved in your browser's localStorage** - this is client-side storage on your device.

## Storage Location

### Browser localStorage
- **Location**: Your browser's local storage (not on a server)
- **Access**: Only accessible on the same browser and device
- **Persistence**: Data stays even after closing the browser
- **Privacy**: Data never leaves your device

## What Gets Saved

1. **User Accounts** → `resumebuilder_users`
2. **Current Login** → `resumebuilder_current_user`
3. **Resumes** → `resumebuilder_resumes`
4. **Auto-Save Drafts** → `resumebuilder_autosave_[userId]`
5. **Theme Settings** → `resumebuilder_theme`
6. **Dark Mode** → `resumebuilder_dark_mode`

## How to View Your Data

### Option 1: Browser Developer Tools (Easiest)

1. Open the web app: https://emadamr53.github.io/resumebuilder/
2. Press **F12** (or Right-click → Inspect)
3. Click **Application** tab (Chrome) or **Storage** tab (Firefox)
4. Expand **Local Storage** in left sidebar
5. Click on `https://emadamr53.github.io`
6. See all your saved data!

### Option 2: Browser Console

1. Press **F12** → Go to **Console** tab
2. Type these commands:

```javascript
// See all your data
console.log('Users:', JSON.parse(localStorage.getItem('resumebuilder_users')));
console.log('Resumes:', JSON.parse(localStorage.getItem('resumebuilder_resumes')));
console.log('Current User:', JSON.parse(localStorage.getItem('resumebuilder_current_user')));
```

## Important Notes

⚠️ **Data is Browser-Specific**
- Chrome data ≠ Firefox data
- Data on one computer ≠ data on another
- Clearing browser data = deleting everything

⚠️ **No Cloud Backup**
- Data is only on your device
- No sync between devices
- No server backup

⚠️ **Privacy**
- Data stays on your device
- Never sent to any server
- Only you can access it

## Storage Limits

- **Typical limit**: 5-10 MB per website
- **Resume size**: ~1-5 KB each
- **Can store**: Hundreds of resumes easily

## Backup Your Data

To backup your data, open browser console (F12) and run:

```javascript
// Export all data
const backup = {
    users: JSON.parse(localStorage.getItem('resumebuilder_users') || '[]'),
    resumes: JSON.parse(localStorage.getItem('resumebuilder_resumes') || '[]'),
    currentUser: JSON.parse(localStorage.getItem('resumebuilder_current_user') || 'null')
};

// Download as file
const blob = new Blob([JSON.stringify(backup, null, 2)], { type: 'application/json' });
const url = URL.createObjectURL(blob);
const a = document.createElement('a');
a.href = url;
a.download = 'resume-backup.json';
a.click();
console.log('Backup downloaded!');
```

---

**Summary**: All data is saved locally in your browser's localStorage. It's private, secure, and stays on your device!

