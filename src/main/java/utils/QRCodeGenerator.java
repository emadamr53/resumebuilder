package utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

/**
 * QR Code Generator using ZXing library
 * Creates scannable QR codes for app download links
 */
public class QRCodeGenerator {
    
    /**
     * Generate QR code image from text using ZXing library
     * @param text The text/URL to encode in the QR code
     * @param size The size of the QR code image (width and height in pixels)
     * @return A JavaFX Image containing the QR code
     */
    public static Image generateQRCode(String text, int size) {
        try {
            // Validate input
            if (text == null || text.trim().isEmpty()) {
                System.err.println("QR Code Error: Text is null or empty");
                return createErrorImage(size);
            }
            
            if (size <= 0 || size > 2000) {
                System.err.println("QR Code Error: Invalid size: " + size);
                size = 300; // Default to 300 if invalid
            }
            
            // Set encoding hints
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);
            
            // Create QR code writer
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            
            // Encode the text into a BitMatrix
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size, hints);
            
            // Create JavaFX WritableImage
            WritableImage image = new WritableImage(size, size);
            PixelWriter pixelWriter = image.getPixelWriter();
            
            // Convert BitMatrix to JavaFX Image
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (bitMatrix.get(x, y)) {
                        // Black pixel for QR code module
                        pixelWriter.setColor(x, y, Color.BLACK);
                    } else {
                        // White pixel for background
                        pixelWriter.setColor(x, y, Color.WHITE);
                    }
                }
            }
            
            System.out.println("QR Code generated successfully for: " + text);
            return image;
            
        } catch (WriterException e) {
            System.err.println("QR Code WriterException: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback: return a simple error image
            return createErrorImage(size);
        } catch (NoClassDefFoundError e) {
            System.err.println("QR Code Library Error: ZXing library not found in classpath!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return createErrorImage(size);
        } catch (Exception e) {
            System.err.println("Unexpected error generating QR code: " + e.getMessage());
            e.printStackTrace();
            return createErrorImage(size);
        }
    }
    
    /**
     * Create a simple error image if QR code generation fails
     */
    private static Image createErrorImage(int size) {
        WritableImage image = new WritableImage(size, size);
        PixelWriter pixelWriter = image.getPixelWriter();
        
        // Fill with white background
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                pixelWriter.setColor(x, y, Color.WHITE);
            }
        }
        
        // Draw a red X to indicate error
        Color errorColor = Color.RED;
        int thickness = size / 20;
        for (int i = 0; i < size; i++) {
            // Diagonal from top-left to bottom-right
            for (int j = -thickness; j <= thickness; j++) {
                int x = i + j;
                int y = i + j;
                if (x >= 0 && x < size && y >= 0 && y < size) {
                    pixelWriter.setColor(x, y, errorColor);
                }
            }
            // Diagonal from top-right to bottom-left
            for (int j = -thickness; j <= thickness; j++) {
                int x = size - 1 - i + j;
                int y = i + j;
                if (x >= 0 && x < size && y >= 0 && y < size) {
                    pixelWriter.setColor(x, y, errorColor);
                }
            }
        }
        
        return image;
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

