# 🌐 Web App Hosting Guide

## Quick Start - Host on GitHub Pages (Free & Easy)

### Step 1: Create GitHub Repository

1. Go to https://github.com/new
2. Create a new repository named `resumebuilder`
3. Make it **public** (required for free GitHub Pages)

### Step 2: Upload Files

```bash
cd web-app
git init
git add .
git commit -m "Initial commit - Resume Builder Web App"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/resumebuilder.git
git push -u origin main
```

### Step 3: Enable GitHub Pages

1. Go to repository **Settings** → **Pages**
2. Under **Source**, select **main** branch
3. Select **/ (root)** folder
4. Click **Save**

### Step 4: Your Web App URL

Your app will be available at:
```
https://YOUR_USERNAME.github.io/resumebuilder/
```

### Step 5: Update QR Code

Edit `src/main/java/utils/QRCodeGenerator.java`:
```java
return "https://YOUR_USERNAME.github.io/resumebuilder/";
```

---

## Alternative: Host on Netlify (Free & Easy)

### Step 1: Go to Netlify

1. Visit https://netlify.com
2. Sign up for free account

### Step 2: Deploy

1. Drag and drop the `web-app` folder to Netlify
2. Or connect your GitHub repository
3. Netlify will automatically deploy

### Step 3: Your URL

Your app will be at:
```
https://your-app-name.netlify.app
```

---

## Create App Icons

The web app needs icon files. Create them:

### Option 1: Use Online Tool

1. Go to https://realfavicongenerator.net
2. Upload a 512x512 PNG image
3. Download the generated icons
4. Place `icon-192.png` and `icon-512.png` in the `web-app` folder

### Option 2: Create Manually

```bash
# If you have a 1024x1024 icon.png:
cd web-app
sips -z 192 192 icon.png --out icon-192.png
sips -z 512 512 icon.png --out icon-512.png
```

---

## Testing Locally

### Using Python:

```bash
cd web-app
python3 -m http.server 8000
```

Then open: http://localhost:8000

### Using Node.js:

```bash
cd web-app
npx http-server -p 8000
```

---

## Features

✅ **Progressive Web App (PWA)**
- Installable on mobile devices
- Works offline
- App-like experience

✅ **Mobile Responsive**
- Works on all screen sizes
- Touch-friendly interface
- Native app feel

✅ **Full Functionality**
- User authentication
- Resume creation & editing
- AI Job Finder
- Export options

---

## Custom Domain (Optional)

### On GitHub Pages:

1. Go to repository **Settings** → **Pages**
2. Under **Custom domain**, enter your domain
3. Follow DNS setup instructions

### On Netlify:

1. Go to **Domain settings**
2. Add your custom domain
3. Follow DNS configuration

---

## Update QR Code in Desktop App

After hosting, update the QR code URL:

1. Open `src/main/java/utils/QRCodeGenerator.java`
2. Change `generateDownloadURL()` to return your web app URL
3. Rebuild the desktop app

---

## Troubleshooting

### Icons not showing:
- Make sure `icon-192.png` and `icon-512.png` exist in `web-app` folder
- Check file paths in `manifest.json`

### Service worker not working:
- Make sure you're using HTTPS (or localhost)
- Check browser console for errors

### App not installable:
- Make sure `manifest.json` is correct
- Check that service worker is registered
- Use HTTPS (required for PWA)

---

**That's it! Your web app is ready to share via QR code! 🎉**

