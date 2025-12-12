package managers;

import models.Resume;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

/**
 * Manager class for auto-saving resumes
 * @author habib
 */
public class AutoSaveManager {
    private static Timer timer;
    private static TimerTask currentTask;
    private static final long AUTO_SAVE_INTERVAL = 10000; // 10 seconds (reduced from 30 for more frequent saves)
    
    /**
     * Start auto-saving for a resume
     */
    public static void startAutoSave(Supplier<Resume> resumeSupplier) {
        stopAutoSave(); // Stop any existing auto-save
        
        System.out.println("🚀 Auto-save started! Will save every 10 seconds to saved_resumes folder.");
        
        timer = new Timer(true); // Daemon thread
        currentTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    Resume resume = resumeSupplier.get();
                    if (resume != null) {
                        System.out.println("🔄 Auto-saving resume for: " + resume.getName());
                        boolean saved = ResumeManager.saveResume(resume);
                        if (saved) {
                            System.out.println("✅ Auto-saved successfully to saved_resumes folder!");
                        } else {
                            System.out.println("⚠️ Auto-save completed but may have encountered issues");
                        }
                    } else {
                        System.out.println("⏭️ Skipping auto-save: Resume form is empty");
                    }
                } catch (Exception e) {
                    System.err.println("❌ Error during auto-save: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        
        timer.scheduleAtFixedRate(currentTask, AUTO_SAVE_INTERVAL, AUTO_SAVE_INTERVAL);
    }
    
    /**
     * Stop auto-saving
     */
    public static void stopAutoSave() {
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
            System.out.println("⏹️ Auto-save stopped");
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    
    /**
     * Manually trigger auto-save
     */
    public static void triggerAutoSave(Resume resume) {
        if (resume != null) {
            ResumeManager.saveResume(resume);
        }
    }
}
