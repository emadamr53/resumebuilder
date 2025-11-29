package utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple QR Code Generator
 * Creates QR codes for app download links
 */
public class QRCodeGenerator {
    
    /**
     * Generate QR code image from text
     * This is a simplified QR code generator
     * For production, consider using a library like ZXing
     */
    public static Image generateQRCode(String text, int size) {
        // For now, create a simple visual representation
        // In production, use a proper QR code library
        
        Canvas canvas = new Canvas(size, size);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // White background
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, size, size);
        
        // Black border
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, size, 10);
        gc.fillRect(0, 0, 10, size);
        gc.fillRect(size - 10, 0, 10, size);
        gc.fillRect(0, size - 10, size, 10);
        
        // Create pattern (simplified QR code pattern)
        int moduleSize = size / 25;
        boolean[][] pattern = generateQRPattern(text, 25);
        
        for (int y = 0; y < 25; y++) {
            for (int x = 0; x < 25; x++) {
                if (pattern[y][x]) {
                    gc.fillRect(x * moduleSize, y * moduleSize, moduleSize, moduleSize);
                }
            }
        }
        
        // Position markers (corners)
        drawPositionMarker(gc, 2, 2, moduleSize);
        drawPositionMarker(gc, 25 - 5, 2, moduleSize);
        drawPositionMarker(gc, 2, 25 - 5, moduleSize);
        
        WritableImage image = new WritableImage(size, size);
        canvas.snapshot(null, image);
        return image;
    }
    
    /**
     * Generate QR pattern (simplified)
     */
    private static boolean[][] generateQRPattern(String text, int size) {
        boolean[][] pattern = new boolean[size][size];
        
        // Simple pattern based on text hash
        int hash = text.hashCode();
        
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                // Skip position markers area
                if ((x < 7 && y < 7) || 
                    (x >= size - 7 && y < 7) || 
                    (x < 7 && y >= size - 7)) {
                    continue;
                }
                
                // Generate pattern
                int value = (hash + x * 31 + y * 17) % 3;
                pattern[y][x] = (value == 0);
            }
        }
        
        return pattern;
    }
    
    /**
     * Draw position marker (corner squares)
     */
    private static void drawPositionMarker(GraphicsContext gc, int x, int y, int moduleSize) {
        gc.setFill(Color.BLACK);
        // Outer square
        gc.fillRect(x * moduleSize, y * moduleSize, 7 * moduleSize, 7 * moduleSize);
        // Inner white square
        gc.setFill(Color.WHITE);
        gc.fillRect((x + 1) * moduleSize, (y + 1) * moduleSize, 5 * moduleSize, 5 * moduleSize);
        // Center black square
        gc.setFill(Color.BLACK);
        gc.fillRect((x + 2) * moduleSize, (y + 2) * moduleSize, 3 * moduleSize, 3 * moduleSize);
    }
    
    /**
     * Generate download URL for the app
     * This points to the web app that works on mobile browsers
     */
    public static String generateDownloadURL() {
        // Update this URL to your hosted web app
        // Options:
        // 1. GitHub Pages: https://YOUR_USERNAME.github.io/resumebuilder/
        // 2. Netlify: https://your-app.netlify.app
        // 3. Your own domain: https://resumebuilder.yourdomain.com
        
        return "https://amremad.github.io/resumebuilder/";
        
        // For local testing:
        // return "http://localhost:8000/";
    }
    
    /**
     * Generate deep link URL (for mobile app)
     */
    public static String generateDeepLink() {
        // Deep link format: resumebuilder://open
        return "resumebuilder://open?source=qr";
    }
}

