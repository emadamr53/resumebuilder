# 🌐 Web App Hosting Guide - NetBeans Edition

## ✅ Current Setup

Your web app files are in the **root directory** of your NetBeans project:
- `index.html` - Main HTML file
- `app.js` - JavaScript application
- `styles.css` - Styles with theme support
- `manifest.json` - PWA manifest
- `sw.js` - Service worker

**GitHub Repository**: `emadamr53/resumebuilder`  
**Live URL**: `https://emadamr53.github.io/resumebuilder/`

---

## 🚀 Quick Start - Test Locally in NetBeans

### Option 1: Using NetBeans Built-in Server

1. **Right-click** on `index.html` in NetBeans
2. Select **Open in Browser** (or press `Shift + F6`)
3. NetBeans will open it in your default browser

### Option 2: Using NetBeans HTTP Server

1. In NetBeans, go to **Tools** → **Options** → **General** → **Web Browser**
2. Set your preferred browser
3. Right-click `index.html` → **View** → **View in Browser**

### Option 3: Manual Local Server

Open terminal in NetBeans (Tools → Terminal) and run:

```bash
# Python (if installed)
python3 -m http.server 8000

# Or Node.js (if installed)
npx http-server -p 8000
```

Then open: `http://localhost:8000`

---

## 📤 Deploy to GitHub Pages from NetBeans

### Step 1: Enable GitHub Pages

1. Go to: https://github.com/emadamr53/resumebuilder/settings/pages
2. Under **Source**, select: **main** branch
3. Under **Folder**, select: **/ (root)**
4. Click **Save**
5. Wait 1-2 minutes for deployment

### Step 2: Verify Deployment

Your app will be live at:
```
https://emadamr53.github.io/resumebuilder/
```

### Step 3: Test QR Code

1. Open the app: https://emadamr53.github.io/resumebuilder/
2. Sign up or login
3. Create a resume
4. Go to **QR Code** screen
5. The QR code will link to: `https://emadamr53.github.io/resumebuilder`

---

## 🔗 Update QR Code in Desktop App (NetBeans)

After deploying, update the QR code URL in your Java desktop app:

### Step 1: Open QR Code Generator

In NetBeans, open:
```
src/main/java/utils/QRCodeGenerator.java
```

### Step 2: Update URL

Find the `generateDownloadURL()` method and update it:

```java
public String generateDownloadURL() {
    return "https://emadamr53.github.io/resumebuilder/";
}
```

### Step 3: Rebuild

1. In NetBeans, click **Clean and Build** (Shift + F11)
2. Run the desktop app (F6)
3. Generate QR code - it will now link to your web app!

---

## 🎨 Web App Features

✅ **Complete Resume Builder**
- User authentication (Sign up/Login)
- Resume creation with personal info, skills, experience, education
- Real-time preview with themes

✅ **Theme System**
- 4 Professional themes: Classic, Modern, Professional, Creative
- Dark/Light mode toggle
- Theme customization

✅ **Export Options**
- PDF export (using jsPDF)
- Word document export
- Professional formatting

✅ **AI Job Finder**
- Skill-based job suggestions
- Location filtering
- Match scoring

✅ **QR Code Generator**
- Generates QR code linking to your GitHub Pages URL
- Downloadable as PNG image
- Perfect for sharing

✅ **Progressive Web App (PWA)**
- Installable on mobile devices
- Works offline (with service worker)
- App-like experience

---

## 📁 Project Structure in NetBeans

```
AmrEmadResumeBuilder/
├── index.html          ← Web app entry point
├── app.js             ← JavaScript application
├── styles.css         ← Styles with themes
├── manifest.json      ← PWA manifest
├── sw.js              ← Service worker
├── src/
│   └── main/
│       └── java/      ← Java desktop app source
│           └── utils/
│               └── QRCodeGenerator.java  ← Update this for QR code
└── web-app/           ← Backup copy of web files
```

---

## 🧪 Testing Checklist

### Local Testing (NetBeans)
- [ ] Open `index.html` in browser from NetBeans
- [ ] Can sign up/login
- [ ] Can create resume
- [ ] Can preview resume
- [ ] Can select themes
- [ ] Dark/light mode toggle works
- [ ] Can generate QR code
- [ ] Can export as PDF/Word

### GitHub Pages Testing
- [ ] Site loads at https://emadamr53.github.io/resumebuilder/
- [ ] All features work on live site
- [ ] QR code links to correct URL
- [ ] Service worker works (check Network tab)
- [ ] PWA can be installed on mobile

---

## 🔧 Troubleshooting

### Issue: Blank Page in Browser
**Solution:**
1. Open browser console (F12)
2. Check for JavaScript errors
3. Make sure all files are in root directory
4. Clear browser cache (Ctrl+Shift+Delete)

### Issue: Service Worker Not Working
**Solution:**
- Service worker requires HTTPS (or localhost)
- Check browser console for errors
- App will still work without service worker

### Issue: QR Code Shows Wrong URL
**Solution:**
1. Check `app.js` - `generateQRCode()` function
2. It should auto-detect GitHub Pages URL
3. If not, manually set: `https://emadamr53.github.io/resumebuilder`

### Issue: Icons Not Showing
**Solution:**
- Create `icon-192.png` and `icon-512.png` in root directory
- Or remove icon references from `manifest.json` (app will still work)

### Issue: GitHub Pages Not Updating
**Solution:**
1. Make sure files are committed and pushed
2. Wait 2-3 minutes after push
3. Clear browser cache
4. Check GitHub Actions tab for build status

---

## 📱 Mobile Testing

1. Open https://emadamr53.github.io/resumebuilder/ on your phone
2. Test all features
3. Try "Add to Home Screen" (PWA install)
4. Scan QR code with phone camera
5. Verify it opens the web app

---

## 🎯 Next Steps

1. ✅ **Test locally** in NetBeans
2. ✅ **Deploy to GitHub Pages**
3. ✅ **Update QR code** in desktop app
4. ✅ **Test on mobile** devices
5. ✅ **Share QR code** with others!

---

## 📝 Notes for NetBeans Users

- The web app files are in the **root directory** (same level as `src/`)
- This allows GitHub Pages to serve them directly
- The Java desktop app is separate in `src/main/java/`
- Both can coexist in the same NetBeans project
- When you build the Java app, it doesn't affect the web files

---

**Your web app is ready! 🎉**

Visit: **https://emadamr53.github.io/resumebuilder/**
