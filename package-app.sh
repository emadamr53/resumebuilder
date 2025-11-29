#!/bin/bash

# Package Resume Builder as macOS Desktop App
# This script creates a .app bundle that can be double-clicked to run

APP_NAME="ResumeBuilder"
APP_DIR="${APP_NAME}.app"
CONTENTS_DIR="${APP_DIR}/Contents"
MACOS_DIR="${CONTENTS_DIR}/MacOS"
RESOURCES_DIR="${CONTENTS_DIR}/Resources"
JAVA_DIR="${CONTENTS_DIR}/Java"

echo "📦 Packaging ${APP_NAME} as macOS Desktop App..."

# Clean previous build
rm -rf "${APP_DIR}"

# Create app bundle structure
mkdir -p "${MACOS_DIR}"
mkdir -p "${RESOURCES_DIR}"
mkdir -p "${JAVA_DIR}"

# Copy JavaFX SDK
echo "📋 Copying JavaFX SDK..."
cp -R javafx-sdk-21 "${JAVA_DIR}/"

# Copy libraries
echo "📚 Copying libraries..."
cp -R lib "${JAVA_DIR}/"

# Copy compiled classes
echo "💻 Copying compiled classes..."
mkdir -p "${JAVA_DIR}/classes"
if [ -d "build/classes" ]; then
    cp -R build/classes/* "${JAVA_DIR}/classes/"
fi

# Create Info.plist
echo "📝 Creating Info.plist..."
cat > "${CONTENTS_DIR}/Info.plist" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>CFBundleExecutable</key>
    <string>${APP_NAME}</string>
    <key>CFBundleIdentifier</key>
    <string>com.amremad.resumebuilder</string>
    <key>CFBundleName</key>
    <string>${APP_NAME}</string>
    <key>CFBundleVersion</key>
    <string>1.0</string>
    <key>CFBundleShortVersionString</key>
    <string>1.0</string>
    <key>CFBundlePackageType</key>
    <string>APPL</string>
    <key>CFBundleSignature</key>
    <string>????</string>
    <key>CFBundleIconFile</key>
    <string>AppIcon</string>
    <key>LSMinimumSystemVersion</key>
    <string>10.13</string>
    <key>NSHighResolutionCapable</key>
    <true/>
    <key>NSRequiresAquaSystemAppearance</key>
    <false/>
</dict>
</plist>
EOF

# Create launcher script
echo "🚀 Creating launcher script..."
cat > "${MACOS_DIR}/${APP_NAME}" << 'EOF'
#!/bin/bash

# Get the directory where the app bundle is located
APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
JAVA_DIR="${APP_DIR}/Contents/Java"
JAVAFX_PATH="${JAVA_DIR}/javafx-sdk-21/lib"

# Find Java
JAVA_HOME=""
if [ -d "/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home" ]; then
    JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home"
elif [ -d "/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home" ]; then
    JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"
elif [ -d "/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home" ]; then
    JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home"
else
    # Try to find any Java
    JAVA_HOME=$(/usr/libexec/java_home 2>/dev/null)
fi

if [ -z "$JAVA_HOME" ]; then
    osascript -e 'display dialog "Java not found! Please install JDK 17 or higher." buttons {"OK"} default button "OK" with icon stop'
    exit 1
fi

JAVA="${JAVA_HOME}/bin/java"

# Run the application
cd "${JAVA_DIR}"
"${JAVA}" \
    --module-path "${JAVAFX_PATH}" \
    --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base \
    --enable-native-access=ALL-UNNAMED \
    -cp "classes:lib/sqlite-jdbc-3.44.1.0.jar:lib/slf4j-api-2.0.9.jar:lib/slf4j-simple-2.0.9.jar" \
    com.mycompany.mavenproject2.Launcher
EOF

chmod +x "${MACOS_DIR}/${APP_NAME}"

# Create a simple app icon (if icon file exists, use it; otherwise create placeholder)
if [ -f "AppIcon.icns" ]; then
    echo "🎨 Using existing AppIcon.icns..."
    cp AppIcon.icns "${RESOURCES_DIR}/AppIcon.icns"
else
    echo "⚠️  AppIcon.icns not found. Checking for icon.png..."
    # Try to create from icon.png if available
    if [ -f "icon.png" ]; then
        echo "📦 Converting icon.png to .icns..."
        mkdir -p icon.iconset
        sips -z 16 16     icon.png --out icon.iconset/icon_16x16.png 2>/dev/null
        sips -z 32 32     icon.png --out icon.iconset/icon_16x16@2x.png 2>/dev/null
        sips -z 32 32     icon.png --out icon.iconset/icon_32x32.png 2>/dev/null
        sips -z 64 64     icon.png --out icon.iconset/icon_32x32@2x.png 2>/dev/null
        sips -z 128 128   icon.png --out icon.iconset/icon_128x128.png 2>/dev/null
        sips -z 256 256   icon.png --out icon.iconset/icon_128x128@2x.png 2>/dev/null
        sips -z 256 256   icon.png --out icon.iconset/icon_256x256.png 2>/dev/null
        sips -z 512 512   icon.png --out icon.iconset/icon_256x256@2x.png 2>/dev/null
        sips -z 512 512   icon.png --out icon.iconset/icon_512x512.png 2>/dev/null
        sips -z 1024 1024 icon.png --out icon.iconset/icon_512x512@2x.png 2>/dev/null
        iconutil -c icns icon.iconset 2>/dev/null
        if [ -f "icon.icns" ]; then
            mv icon.icns "${RESOURCES_DIR}/AppIcon.icns"
            rm -rf icon.iconset
            echo "✅ Icon created from icon.png"
        else
            echo "   (Could not create icon. App will use default icon)"
            touch "${RESOURCES_DIR}/AppIcon.icns"
        fi
    else
        echo "   To add a custom icon:"
        echo "   1. Create icon.png (1024x1024) or AppIcon.icns"
        echo "   2. Place it in project root"
        echo "   3. Run this script again"
        touch "${RESOURCES_DIR}/AppIcon.icns"
    fi
fi

echo ""
echo "✅ App bundle created: ${APP_DIR}"
echo ""
echo "📌 To use the app:"
echo "   1. Double-click ${APP_DIR} to launch"
echo "   2. Or drag it to Applications folder"
echo ""
echo "🎨 To add a custom icon:"
echo "   1. Create AppIcon.icns file"
echo "   2. Place it in project root"
echo "   3. Run this script again"
echo ""

