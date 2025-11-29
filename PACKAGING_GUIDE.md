# 📦 Packaging & Publishing Guide

## 🎨 Creating App Icon

### For macOS (.icns file):

1. **Create icon images:**
   - Create a 1024x1024 PNG image of your app icon
   - Save as `icon.png`

2. **Convert to .icns:**
   ```bash
   # Install iconutil (comes with macOS)
   mkdir icon.iconset
   
   # Create different sizes
   sips -z 16 16     icon.png --out icon.iconset/icon_16x16.png
   sips -z 32 32     icon.png --out icon.iconset/icon_16x16@2x.png
   sips -z 32 32     icon.png --out icon.iconset/icon_32x32.png
   sips -z 64 64     icon.png --out icon.iconset/icon_32x32@2x.png
   sips -z 128 128   icon.png --out icon.iconset/icon_128x128.png
   sips -z 256 256   icon.png --out icon.iconset/icon_128x128@2x.png
   sips -z 256 256   icon.png --out icon.iconset/icon_256x256.png
   sips -z 512 512   icon.png --out icon.iconset/icon_256x256@2x.png
   sips -z 512 512   icon.png --out icon.iconset/icon_512x512.png
   sips -z 1024 1024 icon.png --out icon.iconset/icon_512x512@2x.png
   
   # Convert to .icns
   iconutil -c icns icon.iconset
   mv icon.icns AppIcon.icns
   ```

3. **Place in project root:**
   - Copy `AppIcon.icns` to the project root directory

---

## 🖥️ Packaging as macOS Desktop App

### Step 1: Build the Project
```bash
# In NetBeans: Clean and Build
# Or from terminal:
cd "/Users/amremad/Downloads/AmrEmadResumeBuilder"
ant clean jar
```

### Step 2: Run Packaging Script
```bash
chmod +x package-app.sh
./package-app.sh
```

### Step 3: Test the App
```bash
# Double-click ResumeBuilder.app
# Or run from terminal:
open ResumeBuilder.app
```

### Step 4: Install to Applications
```bash
# Drag ResumeBuilder.app to Applications folder
# Or copy:
cp -R ResumeBuilder.app /Applications/
```

---

## 📱 Publishing to App Stores

### 🍎 Apple App Store (macOS)

#### Requirements:
1. **Apple Developer Account** ($99/year)
   - Sign up at: https://developer.apple.com

2. **Xcode** (free from Mac App Store)
   - Install Xcode
   - Install Command Line Tools: `xcode-select --install`

3. **Notarization:**
   ```bash
   # Create a Developer ID certificate
   # Then notarize your app:
   xcrun notarytool submit ResumeBuilder.app \
     --apple-id your@email.com \
     --team-id YOUR_TEAM_ID \
     --password YOUR_APP_SPECIFIC_PASSWORD
   ```

4. **Create DMG for distribution:**
   ```bash
   # Install create-dmg (via Homebrew)
   brew install create-dmg
   
   # Create DMG
   create-dmg \
     --volname "Resume Builder" \
     --window-pos 200 120 \
     --window-size 800 400 \
     --icon-size 100 \
     --icon "ResumeBuilder.app" 200 190 \
     --hide-extension "ResumeBuilder.app" \
     --app-drop-link 600 185 \
     "ResumeBuilder.dmg" \
     "ResumeBuilder.app"
   ```

5. **Submit to App Store:**
   - Use Xcode → Product → Archive
   - Upload via App Store Connect

---

### 🤖 Google Play Store (Android)

#### Convert to Android App:

1. **Install Android Studio**

2. **Create Android Project:**
   - File → New → New Project
   - Choose "Empty Activity"
   - Package name: `com.amremad.resumebuilder`

3. **Add JavaFX for Android:**
   - Use JavaFXPorts or Gluon Mobile
   - Add to `build.gradle`:
   ```gradle
   dependencies {
       implementation 'com.gluonhq:charm:6.0.0'
   }
   ```

4. **Package APK:**
   ```bash
   ./gradlew assembleRelease
   ```

5. **Sign APK:**
   ```bash
   jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 \
     -keystore my-release-key.keystore \
     app-release-unsigned.apk alias_name
   ```

6. **Submit to Play Store:**
   - Go to Google Play Console
   - Create new app
   - Upload signed APK

---

### 📦 Alternative: Package as Installer

#### For macOS (using Packages.app):
1. Download Packages: http://s.sudre.free.fr/Software/Packages/about.html
2. Create new project
3. Add ResumeBuilder.app
4. Build installer (.pkg file)

#### For Windows (using Inno Setup):
1. Download Inno Setup: https://jrsoftware.org/isinfo.php
2. Create installer script
3. Build .exe installer

---

## 🚀 Quick Start (Desktop App)

1. **Build project in NetBeans**
2. **Run packaging script:**
   ```bash
   ./package-app.sh
   ```
3. **Double-click `ResumeBuilder.app`**

That's it! Your app is ready to use! 🎉

---

## 📝 Notes

- **JavaFX Runtime:** The app includes JavaFX SDK, so users don't need to install it separately
- **Java Version:** Requires JDK 17+ (included in script detection)
- **File Size:** ~60MB (includes JavaFX SDK)
- **Permissions:** App may need permission to access files/folders

---

## 🆘 Troubleshooting

### App won't launch:
- Check Java is installed: `java -version`
- Check console for errors: Run from terminal to see output

### Icon not showing:
- Make sure AppIcon.icns is in project root
- Re-run packaging script

### Permission denied:
```bash
chmod +x package-app.sh
chmod +x ResumeBuilder.app/Contents/MacOS/ResumeBuilder
```

