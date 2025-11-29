# 📱 QR Code Setup Guide

## How the QR Code Works

When users scan the QR code from the app, it opens a download page on their mobile device where they can:
- Download the iOS app
- Download the Android app  
- Download the Mac desktop app
- Learn about app features

---

## 🚀 Setting Up the Download Page

### Option 1: GitHub Pages (Free & Easy)

1. **Create a GitHub repository:**
   ```bash
   git init
   git add download-page.html
   git commit -m "Add download page"
   git remote add origin https://github.com/YOUR_USERNAME/resumebuilder.git
   git push -u origin main
   ```

2. **Enable GitHub Pages:**
   - Go to repository Settings → Pages
   - Select `main` branch
   - Save

3. **Your URL will be:**
   ```
   https://YOUR_USERNAME.github.io/resumebuilder/download-page.html
   ```

4. **Update the URL in code:**
   - Edit `src/main/java/utils/QRCodeGenerator.java`
   - Change `generateDownloadURL()` to return your GitHub Pages URL

---

### Option 2: Netlify (Free & Easy)

1. **Go to:** https://netlify.com
2. **Drag and drop** `download-page.html` to deploy
3. **Get your URL:** `https://your-app-name.netlify.app`
4. **Update** `QRCodeGenerator.java` with your URL

---

### Option 3: Your Own Server

1. Upload `download-page.html` to your web server
2. Update `QRCodeGenerator.java` with your URL

---

## 📱 Creating Mobile Apps

### For iOS (App Store):

1. **Use Xcode:**
   - Create new iOS project
   - Convert JavaFX app to Swift/Objective-C
   - Or use JavaFXPorts for iOS

2. **Or use React Native/Flutter:**
   - Rewrite UI in React Native or Flutter
   - Keep backend logic in Java if needed

### For Android (Play Store):

1. **Use Android Studio:**
   - Create Android project
   - Use JavaFXPorts or Gluon Mobile
   - Package as APK

2. **Or use React Native/Flutter:**
   - Cross-platform solution

---

## 🔗 Deep Linking (Advanced)

To make the QR code open the app directly if installed:

1. **iOS:** Use Universal Links
   ```swift
   // In your iOS app
   func application(_ application: UIApplication, 
                    continue userActivity: NSUserActivity,
                    restorationHandler: @escaping ([UIUserActivityRestoring]?) -> Void) -> Bool {
       // Handle deep link
   }
   ```

2. **Android:** Use App Links
   ```xml
   <!-- In AndroidManifest.xml -->
   <intent-filter android:autoVerify="true">
       <action android:name="android.intent.action.VIEW" />
       <category android:name="android.intent.category.DEFAULT" />
       <category android:name="android.intent.category.BROWSABLE" />
       <data android:scheme="https"
             android:host="yourdomain.com"
             android:pathPrefix="/app" />
   </intent-filter>
   ```

---

## 🎨 Customizing the Download Page

Edit `download-page.html` to:
- Change colors and styling
- Add your logo
- Update download links
- Add screenshots
- Add app store badges

---

## ✅ Testing

1. **Generate QR code in app:**
   - Open app → Click "📱 Download App"
   - QR code appears

2. **Test on phone:**
   - Open camera app
   - Point at QR code
   - Should open download page

3. **Test download links:**
   - Click download buttons
   - Verify they work

---

## 📝 Current Setup

- **QR Code URL:** Points to `https://amremad.github.io/resumebuilder/download-page.html`
- **Download Page:** `download-page.html` (ready to upload)
- **Deep Link:** `resumebuilder://open` (for future mobile apps)

---

**That's it! Your QR code is ready to share! 🎉**

