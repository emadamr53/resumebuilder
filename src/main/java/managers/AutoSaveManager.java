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
    private static final long AUTO_SAVE_INTERVAL = 30000; // 30 seconds
    
    /**
     * Start auto-saving for a resume
     */
    public static void startAutoSave(Supplier<Resume> resumeSupplier) {
        stopAutoSave(); // Stop any existing auto-save
        
        timer = new Timer(true); // Daemon thread
        currentTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    Resume resume = resumeSupplier.get();
                    if (resume != null) {
                        ResumeManager.saveResume(resume);
                        System.out.println("Auto-saved resume");
                    }
                } catch (Exception e) {
                    System.err.println("Error during auto-save: " + e.getMessage());
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
