package managers;

import models.Resume;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import views.ProfessionalCVViewFX;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.image.PixelReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

/**
 * PDF Export Manager - Exports resume to PDF format
 */
public class PDFExportManager {
    
    public static void exportPDF(Resume resume) {
        exportToPDF(resume, "exported_resume.pdf");
    }
    
    public static boolean exportToPDF(Resume resume, String filePath) {
        try {
            // Get the preview content node from ProfessionalCVViewFX
            Node previewContent = ProfessionalCVViewFX.getPreviewContentNode(resume);
            
            if (previewContent == null) {
                System.err.println("Error: Could not get preview content");
                return false;
            }
            
            // Set the node to use preferred size to ensure all content is included
            if (previewContent instanceof javafx.scene.control.Control) {
                ((javafx.scene.control.Control) previewContent).setPrefSize(
                    javafx.scene.control.Control.USE_PREF_SIZE, 
                    javafx.scene.control.Control.USE_PREF_SIZE
                );
            }
            
            // Create a temporary scene to ensure proper layout
            javafx.scene.Group group = new javafx.scene.Group(previewContent);
            javafx.scene.Scene tempScene = new javafx.scene.Scene(group);
            tempScene.getStylesheets().clear();
            
            // Apply CSS and layout to ensure all content is measured
            previewContent.applyCss();
            if (previewContent instanceof javafx.scene.Parent) {
                javafx.scene.Parent parent = (javafx.scene.Parent) previewContent;
                
                // Force layout of all children recursively
                layoutNode(parent);
                
                // Layout the parent itself
                parent.layout();
            }
            
            // Wait a moment for layout to complete
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Get the actual bounds of the content after layout
            javafx.geometry.Bounds bounds = previewContent.getBoundsInLocal();
            double contentWidth = Math.max(bounds.getWidth(), 650); // Minimum width
            double contentHeight = Math.max(bounds.getHeight(), bounds.getMaxY()); // Use maxY to get full height
            
            // If it's a VBox, try to get the preferred height
            if (previewContent instanceof javafx.scene.layout.VBox) {
                javafx.scene.layout.VBox vbox = (javafx.scene.layout.VBox) previewContent;
                double prefHeight = vbox.prefHeight(-1);
                if (prefHeight > 0) {
                    contentHeight = Math.max(contentHeight, prefHeight);
                }
            }
            
            System.out.println("Content bounds: " + contentWidth + " x " + contentHeight);
            System.out.println("Bounds maxY: " + bounds.getMaxY());
            
            // Take snapshot with the full content size
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.WHITE); // White background for PDF
            WritableImage snapshot = previewContent.snapshot(params, 
                new WritableImage((int) Math.ceil(contentWidth), (int) Math.ceil(contentHeight)));
            
            // Convert JavaFX WritableImage to BufferedImage
            int width = (int) snapshot.getWidth();
            int height = (int) snapshot.getHeight();
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            PixelReader reader = snapshot.getPixelReader();
            
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    javafx.scene.paint.Color fxColor = reader.getColor(x, y);
                    int argb = (int) (fxColor.getOpacity() * 255) << 24 |
                               (int) (fxColor.getRed() * 255) << 16 |
                               (int) (fxColor.getGreen() * 255) << 8 |
                               (int) (fxColor.getBlue() * 255);
                    bufferedImage.setRGB(x, y, argb);
                }
            }
            
            // Create PDF from image
            try (PDDocument document = new PDDocument()) {
                float pageWidth = PDRectangle.A4.getWidth();
                float pageHeight = PDRectangle.A4.getHeight();
                float imageWidth = bufferedImage.getWidth();
                float imageHeight = bufferedImage.getHeight();
                
                // Scale to fit page width while maintaining aspect ratio
                float scale = pageWidth / imageWidth;
                float scaledWidth = imageWidth * scale;
                float scaledHeight = imageHeight * scale;
                
                System.out.println("Image size: " + imageWidth + " x " + imageHeight);
                System.out.println("Scaled size: " + scaledWidth + " x " + scaledHeight);
                
                // If content is taller than page, scale down to fit on one page
                if (scaledHeight > pageHeight) {
                    // Recalculate scale to fit height
                    scale = pageHeight / imageHeight;
                    scaledWidth = imageWidth * scale;
                    scaledHeight = imageHeight * scale;
                    System.out.println("Content too tall, scaling to fit: " + scaledWidth + " x " + scaledHeight);
                }
                
                // Convert BufferedImage to byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", baos);
                byte[] imageBytes = baos.toByteArray();
                
                // Create PDImageXObject from byte array
                PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageBytes, "resume");
                
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    // Center the image on the page
                    float x = (pageWidth - scaledWidth) / 2;
                    float y = (pageHeight - scaledHeight) / 2;
                    contentStream.drawImage(pdImage, x, y, scaledWidth, scaledHeight);
                }
                
                document.save(filePath);
            System.out.println("PDF Exported Successfully to: " + filePath);
            return true;
            }

        } catch (Exception e) {
            System.err.println("Error exporting PDF: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Recursively layout all nodes to ensure proper sizing
     */
    private static void layoutNode(javafx.scene.Node node) {
        if (node instanceof javafx.scene.Parent) {
            javafx.scene.Parent parent = (javafx.scene.Parent) node;
            parent.layout();
            for (javafx.scene.Node child : parent.getChildrenUnmodifiable()) {
                layoutNode(child);
            }
        }
    }
}
