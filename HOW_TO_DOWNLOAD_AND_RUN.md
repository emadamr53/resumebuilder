# 📥 How to Download & Run the App

## 🚀 Quick Start (3 Steps)

### Step 1: Extract the ZIP File
1. Go to **Downloads** folder
2. Find **`AmrEmadResumeBuilder.zip`**
3. **Double-click** to extract (or right-click → Extract)

---

### Step 2: Build in NetBeans
1. **Open NetBeans IDE**
2. **File → Open Project**
3. Navigate to: `/Users/amremad/Downloads/AmrEmadResumeBuilder`
4. Select the folder and click **Open Project**
5. **Right-click** on the project → **Clean and Build** (or press `Shift+F11`)

---

### Step 3: Create Desktop App
1. **Open Terminal**
2. Run these commands:
   ```bash
   cd "/Users/amremad/Downloads/AmrEmadResumeBuilder"
   ./package-app.sh
   ```
3. Wait for it to finish (you'll see "✅ App bundle created")

---

### Step 4: Launch the App! 🎉
1. Look for **`ResumeBuilder.app`** in the project folder
2. **Double-click** it to launch!
3. Or drag it to your **Applications** folder for easy access

---

## 📱 Alternative: Run from NetBeans

If you just want to run it without packaging:

1. Open project in NetBeans
2. **Right-click** project → **Run** (or press `F6`)
3. The app will launch!

---

## 🎨 Add Custom Icon (Optional)

1. Create a 1024x1024 PNG image
2. Save as `icon.png` in project root
3. Run this command:
   ```bash
   mkdir icon.iconset
   sips -z 16 16 icon.png --out icon.iconset/icon_16x16.png
   sips -z 32 32 icon.png --out icon.iconset/icon_16x16@2x.png
   sips -z 32 32 icon.png --out icon.iconset/icon_32x32.png
   sips -z 64 64 icon.png --out icon.iconset/icon_32x32@2x.png
   sips -z 128 128 icon.png --out icon.iconset/icon_128x128.png
   sips -z 256 256 icon.png --out icon.iconset/icon_128x128@2x.png
   sips -z 256 256 icon.png --out icon.iconset/icon_256x256.png
   sips -z 512 512 icon.png --out icon.iconset/icon_256x256@2x.png
   sips -z 512 512 icon.png --out icon.iconset/icon_512x512.png
   sips -z 1024 1024 icon.png --out icon.iconset/icon_512x512@2x.png
   iconutil -c icns icon.iconset
   mv icon.icns AppIcon.icns
   ```
4. Re-run `./package-app.sh`

---

## 🆘 Troubleshooting

### "Java not found" error:
- Install JDK 17+ from: https://adoptium.net/
- Choose **macOS aarch64** (for Apple Silicon Macs)

### "Permission denied" error:
```bash
chmod +x package-app.sh
```

### App won't launch:
- Check Terminal for error messages
- Make sure you built the project in NetBeans first

---

## 📍 File Locations

- **ZIP File:** `/Users/amremad/Downloads/AmrEmadResumeBuilder.zip`
- **Project:** `/Users/amremad/Downloads/AmrEmadResumeBuilder`
- **Desktop App:** `/Users/amremad/Downloads/AmrEmadResumeBuilder/ResumeBuilder.app`

---

**That's it! Enjoy your Resume Builder app! 🎉**

