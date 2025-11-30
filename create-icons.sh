#!/bin/bash

# Create PWA icons for web app
# This creates simple placeholder icons - replace with your custom icons later

echo "🎨 Creating PWA icons..."

cd "$(dirname "$0")"

# Create a simple 512x512 icon using sips (macOS built-in)
if command -v sips &> /dev/null; then
    # Create a simple colored square as placeholder
    # You should replace this with your actual icon
    
    # Create 512x512 icon
    echo "Creating icon-512.png..."
    # Using a simple method - create via ImageMagick or Python if available
    if command -v convert &> /dev/null; then
        convert -size 512x512 xc:"#667eea" -fill white -gravity center -pointsize 200 -annotate +0+0 "CV" icon-512.png
    elif command -v python3 &> /dev/null; then
        python3 << 'PYTHON'
from PIL import Image, ImageDraw, ImageFont
import os

# Create 512x512 image
img = Image.new('RGB', (512, 512), color='#667eea')
draw = ImageDraw.Draw(img)

# Draw rounded rectangle
draw.rounded_rectangle([20, 20, 492, 492], radius=50, fill=(102, 126, 234))

# Draw text
try:
    font = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 200)
except:
    font = ImageFont.load_default()

text = "CV"
bbox = draw.textbbox((0, 0), text, font=font)
text_width = bbox[2] - bbox[0]
text_height = bbox[3] - bbox[1]
x = (512 - text_width) / 2
y = (512 - text_height) / 2 - 20
draw.text((x, y), text, fill='white', font=font)

img.save('icon-512.png')
print("✅ Created icon-512.png")
PYTHON
    else
        echo "⚠️  Cannot create icons automatically."
        echo "   Please create icon-512.png (512x512) and icon-192.png (192x192) manually"
        exit 1
    fi
    
    # Create 192x192 icon from 512x512
    if [ -f icon-512.png ]; then
        echo "Creating icon-192.png..."
        sips -z 192 192 icon-512.png --out icon-192.png
        echo "✅ Created icon-192.png"
    fi
else
    echo "⚠️  sips not available. Please create icons manually:"
    echo "   - icon-192.png (192x192 pixels)"
    echo "   - icon-512.png (512x512 pixels)"
fi

echo ""
echo "✅ Icons created!"
echo "   You can replace these with your custom icons later."

