# 💾 Where to Find Saved Data on Your MacBook

## Quick Answer

The data is stored in your **browser's localStorage database files** on your Mac. The location depends on which browser you're using.

## File Locations on macOS

### 🌐 Google Chrome

**Path:**
```
~/Library/Application Support/Google/Chrome/Default/Local Storage/leveldb/
```

**Full path example:**
```
/Users/amremad/Library/Application Support/Google/Chrome/Default/Local Storage/leveldb/
```

**Files to look for:**
- Files starting with `https_emadamr53.github.io_0`
- These are LevelDB database files (binary format)

### 🦊 Mozilla Firefox

**Path:**
```
~/Library/Application Support/Firefox/Profiles/[PROFILE]/storage/default/https+++emadamr53.github.io/
```

**Full path example:**
```
/Users/amremad/Library/Application Support/Firefox/Profiles/xxxxx.default-release/storage/default/https+++emadamr53.github.io/
```

**Files to look for:**
- `ls/data.sqlite` - SQLite database containing localStorage data

### 🍎 Safari

**Path:**
```
~/Library/Safari/LocalStorage/
```

**Full path example:**
```
/Users/amremad/Library/Safari/LocalStorage/
```

**Files to look for:**
- Files with domain names like `https_emadamr53.github.io_0.localstorage`

### 🟢 Microsoft Edge

**Path:**
```
~/Library/Application Support/Microsoft Edge/Default/Local Storage/leveldb/
```

**Full path example:**
```
/Users/amremad/Library/Application Support/Microsoft Edge/Default/Local Storage/leveldb/
```

## How to Access These Files

### Method 1: Using Finder

1. **Open Finder**
2. Press **⌘ + Shift + G** (Go to Folder)
3. Paste one of these paths (replace `amremad` with your username):

**For Chrome:**
```
~/Library/Application Support/Google/Chrome/Default/Local Storage/leveldb/
```

**For Firefox:**
```
~/Library/Application Support/Firefox/Profiles/
```

**For Safari:**
```
~/Library/Safari/LocalStorage/
```

4. Press **Enter**
5. Look for files related to `emadamr53.github.io`

### Method 2: Using Terminal

Open Terminal and run:

**For Chrome:**
```bash
cd ~/Library/Application\ Support/Google/Chrome/Default/Local\ Storage/leveldb/
ls -la | grep emadamr53
```

**For Firefox:**
```bash
cd ~/Library/Application\ Support/Firefox/Profiles/
find . -name "*emadamr53*" -type f
```

**For Safari:**
```bash
cd ~/Library/Safari/LocalStorage/
ls -la | grep emadamr53
```

## ⚠️ Important Notes

### These Files Are Binary/Encrypted
- **Chrome/Edge**: Uses LevelDB (binary format) - not human-readable
- **Firefox**: Uses SQLite - can be opened with SQLite tools
- **Safari**: Uses binary format - not easily readable

### Don't Edit Directly!
- **Never edit these files directly** - you'll corrupt your data
- Always use the browser's Developer Tools to view/edit data
- Or use the web app itself

## Better Way: View Data in Browser

Instead of finding the files, it's easier to view data in the browser:

### Step 1: Open Developer Tools
1. Open the web app: https://emadamr53.github.io/resumebuilder/
2. Press **F12** or **⌘ + Option + I**

### Step 2: View LocalStorage
1. Click **Application** tab (Chrome) or **Storage** tab (Firefox)
2. Expand **Local Storage** in left sidebar
3. Click on `https://emadamr53.github.io`
4. See all your data in readable format!

## Export Your Data (Recommended)

Instead of finding the files, export your data as JSON:

### Using Browser Console

1. Open the web app
2. Press **F12** → **Console** tab
3. Paste this code:

```javascript
// Export all your data
const backup = {
    users: JSON.parse(localStorage.getItem('resumebuilder_users') || '[]'),
    resumes: JSON.parse(localStorage.getItem('resumebuilder_resumes') || '[]'),
    currentUser: JSON.parse(localStorage.getItem('resumebuilder_current_user') || 'null'),
    theme: localStorage.getItem('resumebuilder_theme'),
    darkMode: localStorage.getItem('resumebuilder_dark_mode')
};

// Download as JSON file
const blob = new Blob([JSON.stringify(backup, null, 2)], { type: 'application/json' });
const url = URL.createObjectURL(blob);
const a = document.createElement('a');
a.href = url;
a.download = 'resume-builder-backup-' + new Date().toISOString().split('T')[0] + '.json';
a.click();
console.log('✅ Backup downloaded to your Downloads folder!');
```

4. The file will download to your **Downloads** folder
5. You can open it with any text editor (TextEdit, VS Code, etc.)

## Quick Access Script

Create a script to quickly open the localStorage folder:

### For Chrome:
```bash
#!/bin/bash
open ~/Library/Application\ Support/Google/Chrome/Default/Local\ Storage/leveldb/
```

Save as `open-chrome-storage.sh`, then:
```bash
chmod +x open-chrome-storage.sh
./open-chrome-storage.sh
```

## Summary

**Physical Location on Mac:**
- Chrome: `~/Library/Application Support/Google/Chrome/Default/Local Storage/leveldb/`
- Firefox: `~/Library/Application Support/Firefox/Profiles/[PROFILE]/storage/default/`
- Safari: `~/Library/Safari/LocalStorage/`

**Easier Method:**
- Use browser Developer Tools (F12) to view data
- Export data as JSON using the console script above
- Use the "📦 View Storage" button in the app

**Remember:**
- Files are binary/encrypted - not human-readable
- Don't edit files directly
- Use browser tools or export to JSON for readable format

---

**Tip**: The easiest way is to use the browser's Developer Tools (F12) or export your data as JSON using the script above!


