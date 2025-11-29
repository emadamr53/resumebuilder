#!/bin/bash

# Create a simple app icon programmatically
# This creates a basic icon with "CV" text

echo "🎨 Creating app icon..."

# Create a temporary directory
mkdir -p icon_temp
cd icon_temp

# Create a simple SVG icon (we'll convert it to PNG)
cat > icon.svg << 'EOF'
<svg width="1024" height="1024" xmlns="http://www.w3.org/2000/svg">
  <defs>
    <linearGradient id="grad" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" style="stop-color:#667eea;stop-opacity:1" />
      <stop offset="100%" style="stop-color:#764ba2;stop-opacity:1" />
    </linearGradient>
  </defs>
  <rect width="1024" height="1024" rx="200" fill="url(#grad)"/>
  <text x="512" y="600" font-family="Arial, sans-serif" font-size="400" font-weight="bold" fill="white" text-anchor="middle">CV</text>
</svg>
EOF

# Check if we can convert SVG (requires rsvg-convert or ImageMagick)
if command -v rsvg-convert &> /dev/null; then
    rsvg-convert -w 1024 -h 1024 icon.svg -o icon.png
elif command -v convert &> /dev/null; then
    convert -background none -size 1024x1024 icon.svg icon.png
else
    # Fallback: Create using Python if available
    if command -v python3 &> /dev/null; then
        python3 << 'PYTHON'
from PIL import Image, ImageDraw, ImageFont
import sys

# Create image
img = Image.new('RGB', (1024, 1024), color='#667eea')
draw = ImageDraw.Draw(img)

# Draw gradient background (simplified)
for i in range(1024):
    r = int(102 + (118 - 102) * i / 1024)
    g = int(126 + (75 - 126) * i / 1024)
    b = int(234 + (162 - 234) * i / 1024)
    draw.rectangle([(0, i), (1024, i+1)], fill=(r, g, b))

# Draw rounded rectangle
draw.rounded_rectangle([50, 50, 974, 974], radius=200, fill=(102, 126, 234))

# Draw text
try:
    font = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 400)
except:
    font = ImageFont.load_default()

text = "CV"
bbox = draw.textbbox((0, 0), text, font=font)
text_width = bbox[2] - bbox[0]
text_height = bbox[3] - bbox[1]
x = (1024 - text_width) / 2
y = (1024 - text_height) / 2 - 50
draw.text((x, y), text, fill='white', font=font)

img.save('icon.png')
PYTHON
    else
        echo "⚠️  Cannot create icon automatically. Please create icon.png manually (1024x1024)."
        exit 1
    fi
fi

# Convert to .icns
if [ -f icon.png ]; then
    echo "📦 Converting to .icns format..."
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
    
    iconutil -c icns icon.iconset
    mv icon.icns ../AppIcon.icns
    
    echo "✅ Icon created: AppIcon.icns"
    cd ..
    rm -rf icon_temp
else
    echo "❌ Failed to create icon.png"
    cd ..
    exit 1
fi

