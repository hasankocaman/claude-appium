package com.hepsiburada.utils;

import com.hepsiburada.config.ConfigurationManager;
import com.hepsiburada.drivers.DriverManager;
import io.appium.java_client.AppiumDriver;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Screenshot Utilities class for capturing and managing screenshots
 * Provides methods for taking, saving, and managing screenshot files
 * 
 * @author Hepsiburada Test Automation Team
 */
public final class ScreenshotUtils {
    
    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    
    private ScreenshotUtils() {
        // Utility class should not be instantiated
    }
    
    /**
     * Take screenshot and return as byte array
     * @return Screenshot as byte array
     */
    public static byte[] takeScreenshot() {
        logger.debug("Taking screenshot as byte array");
        
        try {
            if (!DriverManager.isDriverInitialized()) {
                logger.error("Driver is not initialized - cannot take screenshot");
                throw new IllegalStateException("Driver is not initialized");
            }
            
            AppiumDriver driver = DriverManager.getDriver();
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            byte[] screenshot = takesScreenshot.getScreenshotAs(OutputType.BYTES);
            
            logger.debug("Screenshot captured successfully as byte array");
            return screenshot;
            
        } catch (Exception e) {
            logger.error("Failed to take screenshot as byte array", e);
            throw new RuntimeException("Failed to take screenshot", e);
        }
    }
    
    /**
     * Take screenshot and save to file
     * @param testName Test name for file naming
     * @return Screenshot file path
     */
    public static String takeScreenshot(String testName) {
        logger.info("Taking screenshot for test: {}", testName);
        
        try {
            if (!DriverManager.isDriverInitialized()) {
                logger.error("Driver is not initialized - cannot take screenshot");
                throw new IllegalStateException("Driver is not initialized");
            }
            
            AppiumDriver driver = DriverManager.getDriver();
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            
            // Generate screenshot file path
            String filePath = generateScreenshotFilePath(testName);
            File destinationFile = new File(filePath);
            
            // Create directory if it doesn't exist
            destinationFile.getParentFile().mkdirs();
            
            // Copy file to destination
            FileUtils.copyFile(sourceFile, destinationFile);
            
            logger.info("Screenshot saved successfully: {}", filePath);
            return filePath;
            
        } catch (IOException e) {
            logger.error("Failed to save screenshot for test: {}", testName, e);
            throw new RuntimeException("Failed to save screenshot", e);
        } catch (Exception e) {
            logger.error("Failed to take screenshot for test: {}", testName, e);
            throw new RuntimeException("Failed to take screenshot", e);
        }
    }
    
    /**
     * Save screenshot from byte array to file
     * @param screenshot Screenshot as byte array
     * @param testName Test name for file naming
     * @return Screenshot file path
     */
    public static String saveScreenshot(byte[] screenshot, String testName) {
        logger.info("Saving screenshot for test: {}", testName);
        
        try {
            // Generate screenshot file path
            String filePath = generateScreenshotFilePath(testName);
            File destinationFile = new File(filePath);
            
            // Create directory if it doesn't exist
            destinationFile.getParentFile().mkdirs();
            
            // Write byte array to file
            FileUtils.writeByteArrayToFile(destinationFile, screenshot);
            
            logger.info("Screenshot saved successfully: {}", filePath);
            return filePath;
            
        } catch (IOException e) {
            logger.error("Failed to save screenshot for test: {}", testName, e);
            throw new RuntimeException("Failed to save screenshot", e);
        }
    }
    
    /**
     * Generate screenshot file path with timestamp
     * @param testName Test name
     * @return Screenshot file path
     */
    private static String generateScreenshotFilePath(String testName) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String sanitizedTestName = sanitizeFileName(testName);
        String fileName = String.format("%s_%s_%s.png", 
            sanitizedTestName, 
            DriverManager.getPlatformName().toLowerCase(),
            timestamp
        );
        
        String screenshotDir = ConfigurationManager.getFrameworkConfig().getScreenshotPath();
        return screenshotDir + File.separator + fileName;
    }
    
    /**
     * Sanitize file name by removing invalid characters
     * @param fileName Original file name
     * @return Sanitized file name
     */
    private static String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "unknown";
        }
        
        // Replace invalid characters with underscore
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
    
    /**
     * Take screenshot on test failure
     * @param testName Test name
     * @param throwable Test failure cause
     * @return Screenshot file path
     */
    public static String takeScreenshotOnFailure(String testName, Throwable throwable) {
        logger.info("Taking failure screenshot for test: {}", testName);
        
        try {
            String failureMessage = throwable != null ? throwable.getMessage() : "unknown_error";
            String screenshotName = String.format("FAILED_%s_%s", testName, 
                sanitizeFileName(failureMessage).substring(0, Math.min(50, sanitizeFileName(failureMessage).length()))
            );
            
            return takeScreenshot(screenshotName);
            
        } catch (Exception e) {
            logger.error("Failed to take failure screenshot for test: {}", testName, e);
            return "";
        }
    }
    
    /**
     * Take screenshot on test pass (if enabled)
     * @param testName Test name
     * @return Screenshot file path
     */
    public static String takeScreenshotOnPass(String testName) {
        logger.info("Taking success screenshot for test: {}", testName);
        
        try {
            if (ConfigurationManager.getFrameworkConfig().isScreenshotOnPass()) {
                String screenshotName = String.format("PASSED_%s", testName);
                return takeScreenshot(screenshotName);
            } else {
                logger.debug("Screenshot on pass is disabled");
                return "";
            }
            
        } catch (Exception e) {
            logger.error("Failed to take success screenshot for test: {}", testName, e);
            return "";
        }
    }
    
    /**
     * Take screenshot for test step
     * @param testName Test name
     * @param stepName Step name
     * @return Screenshot file path
     */
    public static String takeScreenshotForStep(String testName, String stepName) {
        logger.debug("Taking step screenshot for test: {} - step: {}", testName, stepName);
        
        try {
            if (ConfigurationManager.getFrameworkConfig().isScreenshotOnStep()) {
                String screenshotName = String.format("%s_STEP_%s", testName, stepName);
                return takeScreenshot(screenshotName);
            } else {
                logger.debug("Screenshot on step is disabled");
                return "";
            }
            
        } catch (Exception e) {
            logger.error("Failed to take step screenshot for test: {} - step: {}", testName, stepName, e);
            return "";
        }
    }
    
    /**
     * Clean up old screenshot files
     * @param daysOld Number of days old files to clean up
     */
    public static void cleanupOldScreenshots(int daysOld) {
        logger.info("Cleaning up screenshot files older than {} days", daysOld);
        
        try {
            String screenshotDir = ConfigurationManager.getFrameworkConfig().getScreenshotPath();
            File directory = new File(screenshotDir);
            
            if (!directory.exists()) {
                logger.debug("Screenshot directory does not exist: {}", screenshotDir);
                return;
            }
            
            File[] files = directory.listFiles();
            if (files == null) {
                logger.debug("No files found in screenshot directory: {}", screenshotDir);
                return;
            }
            
            long cutoffTime = System.currentTimeMillis() - (daysOld * 24L * 60L * 60L * 1000L);
            int deletedCount = 0;
            
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".png") && file.lastModified() < cutoffTime) {
                    if (file.delete()) {
                        deletedCount++;
                        logger.debug("Deleted old screenshot: {}", file.getName());
                    } else {
                        logger.warn("Failed to delete old screenshot: {}", file.getName());
                    }
                }
            }
            
            logger.info("Cleanup completed - deleted {} old screenshot files", deletedCount);
            
        } catch (Exception e) {
            logger.error("Failed to cleanup old screenshots", e);
        }
    }
    
    /**
     * Get screenshot directory path
     * @return Screenshot directory path
     */
    public static String getScreenshotDirectory() {
        return ConfigurationManager.getFrameworkConfig().getScreenshotPath();
    }
    
    /**
     * Check if screenshot directory exists and create if needed
     * @return true if directory exists or was created successfully
     */
    public static boolean ensureScreenshotDirectoryExists() {
        try {
            String screenshotDir = getScreenshotDirectory();
            File directory = new File(screenshotDir);
            
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    logger.info("Screenshot directory created: {}", screenshotDir);
                } else {
                    logger.error("Failed to create screenshot directory: {}", screenshotDir);
                    return false;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("Error ensuring screenshot directory exists", e);
            return false;
        }
    }
}