package com.hepsiburada.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.hepsiburada.config.ConfigurationManager;
import com.hepsiburada.drivers.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ExtentReports Manager class for comprehensive test reporting
 * Provides thread-safe ExtentReports management with advanced features
 * 
 * @author Hepsiburada Test Automation Team
 */
public final class ExtentReportManager {
    
    private static final Logger logger = LogManager.getLogger(ExtentReportManager.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    
    // Thread-safe collections and locks
    private static volatile ExtentReports extentReports;
    private static final ConcurrentHashMap<Long, ExtentTest> testMap = new ConcurrentHashMap<>();
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    // Report configuration
    private static String reportPath;
    private static boolean isInitialized = false;
    
    private ExtentReportManager() {
        // Utility class should not be instantiated
    }
    
    /**
     * Initialize ExtentReports with default configuration
     * Creates report file and configures basic settings
     */
    public static void initializeReports() {
        if (isInitialized) {
            logger.debug("ExtentReports already initialized");
            return;
        }
        
        lock.writeLock().lock();
        try {
            if (extentReports == null) {
                setupReportPath();
                extentReports = createExtentReports();
                setupSystemInformation();
                isInitialized = true;
                logger.info("ExtentReports initialized successfully. Report path: {}", reportPath);
            }
        } catch (Exception e) {
            logger.error("Failed to initialize ExtentReports", e);
            throw new RuntimeException("Failed to initialize ExtentReports", e);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Create and configure ExtentReports instance
     * @return Configured ExtentReports instance
     */
    private static ExtentReports createExtentReports() {
        try {
            // Create directory if it doesn't exist
            FileUtils.createDirectory(FileUtils.getExtentReportsDirectory());
            
            // Create Spark Reporter
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            configureSparkReporter(sparkReporter);
            
            // Create ExtentReports instance
            ExtentReports reports = new ExtentReports();
            reports.attachReporter(sparkReporter);
            
            return reports;
            
        } catch (Exception e) {
            logger.error("Failed to create ExtentReports instance", e);
            throw new RuntimeException("Failed to create ExtentReports", e);
        }
    }
    
    /**
     * Configure Spark Reporter with custom settings
     * @param sparkReporter SparkReporter instance to configure
     */
    private static void configureSparkReporter(ExtentSparkReporter sparkReporter) {
        try {
            // Basic configuration
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle("Hepsiburada Mobile Test Report");
            sparkReporter.config().setReportName("Mobile Automation Test Results");
            sparkReporter.config().setEncoding("UTF-8");
            sparkReporter.config().setTimelineEnabled(true);
            
            // Custom CSS styling
            sparkReporter.config().setCss(getCustomCSS());
            
            // Custom JavaScript
            sparkReporter.config().setJs(getCustomJS());
            
            logger.debug("Spark Reporter configured successfully");
            
        } catch (Exception e) {
            logger.error("Failed to configure Spark Reporter", e);
        }
    }
    
    /**
     * Setup system information in the report
     */
    private static void setupSystemInformation() {
        if (extentReports == null) return;
        
        try {
            // Basic system information
            extentReports.setSystemInfo("Application", ConfigurationManager.getFrameworkConfig().getAppName());
            extentReports.setSystemInfo("Environment", ConfigurationManager.getFrameworkConfig().getEnvironment());
            extentReports.setSystemInfo("Platform", ConfigurationManager.getFrameworkConfig().getPlatformName());
            extentReports.setSystemInfo("Platform Version", ConfigurationManager.getFrameworkConfig().getPlatformVersion());
            extentReports.setSystemInfo("Device Name", ConfigurationManager.getFrameworkConfig().getDeviceName());
            extentReports.setSystemInfo("Automation Name", ConfigurationManager.getFrameworkConfig().getAutomationName());
            
            // Java and system information
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
            extentReports.setSystemInfo("Operating System", System.getProperty("os.name"));
            extentReports.setSystemInfo("OS Architecture", System.getProperty("os.arch"));
            extentReports.setSystemInfo("User", System.getProperty("user.name"));
            
            // Test configuration
            extentReports.setSystemInfo("Appium Server", ConfigurationManager.getFrameworkConfig().getAppiumServerUrl());
            extentReports.setSystemInfo("Implicit Wait", ConfigurationManager.getFrameworkConfig().getImplicitTimeout() + " seconds");
            extentReports.setSystemInfo("Explicit Wait", ConfigurationManager.getFrameworkConfig().getExplicitTimeout() + " seconds");
            
            logger.debug("System information added to ExtentReports");
            
        } catch (Exception e) {
            logger.error("Failed to setup system information", e);
        }
    }
    
    /**
     * Create a new test in ExtentReports
     * @param testName Name of the test
     * @return ExtentTest instance
     */
    public static ExtentTest createTest(String testName) {
        return createTest(testName, "");
    }
    
    /**
     * Create a new test in ExtentReports with description
     * @param testName Name of the test
     * @param description Test description
     * @return ExtentTest instance
     */
    public static ExtentTest createTest(String testName, String description) {
        ensureInitialized();
        
        lock.readLock().lock();
        try {
            ExtentTest test = extentReports.createTest(testName, description);
            testMap.put(Thread.currentThread().getId(), test);
            
            // Add device information to test
            if (DriverManager.isDriverInitialized()) {
                test.assignDevice(DriverManager.getDeviceName());
                test.assignCategory(DriverManager.getPlatformName());
            }
            
            logger.debug("Test created in ExtentReports: {}", testName);
            return test;
            
        } catch (Exception e) {
            logger.error("Failed to create test: {}", testName, e);
            throw new RuntimeException("Failed to create test", e);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Get current test instance for the thread
     * @return ExtentTest instance or null if not found
     */
    public static ExtentTest getCurrentTest() {
        return testMap.get(Thread.currentThread().getId());
    }
    
    /**
     * Log a pass status with message
     * @param message Pass message
     */
    public static void logPass(String message) {
        logStatus(Status.PASS, message);
    }
    
    /**
     * Log a fail status with message
     * @param message Fail message
     */
    public static void logFail(String message) {
        logStatus(Status.FAIL, message);
    }
    
    /**
     * Log a skip status with message
     * @param message Skip message
     */
    public static void logSkip(String message) {
        logStatus(Status.SKIP, message);
    }
    
    /**
     * Log an info status with message
     * @param message Info message
     */
    public static void logInfo(String message) {
        logStatus(Status.INFO, message);
    }
    
    /**
     * Log a warning status with message
     * @param message Warning message
     */
    public static void logWarning(String message) {
        logStatus(Status.WARNING, message);
    }
    
    /**
     * Log status with message
     * @param status Status to log
     * @param message Message to log
     */
    private static void logStatus(Status status, String message) {
        ExtentTest test = getCurrentTest();
        if (test != null) {
            try {
                test.log(status, message);
                logger.debug("Logged {} status: {}", status, message);
            } catch (Exception e) {
                logger.error("Failed to log status {} with message: {}", status, message, e);
            }
        } else {
            logger.warn("No active test found for logging status: {} - {}", status, message);
        }
    }
    
    /**
     * Log step with pass status
     * @param stepDescription Step description
     */
    public static void logStep(String stepDescription) {
        logPass("Step: " + stepDescription);
        
        // Take screenshot if configured
        if (ConfigurationManager.getFrameworkConfig().isScreenshotOnStep()) {
            attachScreenshot("Step: " + stepDescription);
        }
    }
    
    /**
     * Log failed step with fail status
     * @param stepDescription Step description
     * @param errorMessage Error message
     */
    public static void logFailedStep(String stepDescription, String errorMessage) {
        String message = String.format("Step Failed: %s - Error: %s", stepDescription, errorMessage);
        logFail(MarkupHelper.createLabel(message, ExtentColor.RED).getMarkup());
        
        // Always take screenshot for failed steps
        attachScreenshot("Failed Step: " + stepDescription);
    }
    
    /**
     * Attach screenshot to current test
     * @param screenshotName Screenshot name
     */
    public static void attachScreenshot(String screenshotName) {
        ExtentTest test = getCurrentTest();
        if (test != null && DriverManager.isDriverInitialized()) {
            try {
                byte[] screenshot = ScreenshotUtils.takeScreenshot();
                String screenshotPath = ScreenshotUtils.saveScreenshot(screenshot, screenshotName);
                
                // Get relative path for report
                String relativePath = getRelativeScreenshotPath(screenshotPath);
                test.addScreenCaptureFromPath(relativePath, screenshotName);
                
                logger.debug("Screenshot attached to test: {}", screenshotName);
                
            } catch (Exception e) {
                logger.error("Failed to attach screenshot: {}", screenshotName, e);
                test.log(Status.WARNING, "Failed to attach screenshot: " + e.getMessage());
            }
        }
    }
    
    /**
     * Attach screenshot from file path
     * @param screenshotPath Screenshot file path
     * @param description Screenshot description
     */
    public static void attachScreenshotFromPath(String screenshotPath, String description) {
        ExtentTest test = getCurrentTest();
        if (test != null && FileUtils.fileExists(screenshotPath)) {
            try {
                String relativePath = getRelativeScreenshotPath(screenshotPath);
                test.addScreenCaptureFromPath(relativePath, description);
                logger.debug("Screenshot attached from path: {}", screenshotPath);
            } catch (Exception e) {
                logger.error("Failed to attach screenshot from path: {}", screenshotPath, e);
            }
        }
    }
    
    /**
     * Attach video to current test
     * @param videoPath Video file path
     * @param description Video description
     */
    public static void attachVideo(String videoPath, String description) {
        ExtentTest test = getCurrentTest();
        if (test != null && FileUtils.fileExists(videoPath)) {
            try {
                String relativePath = getRelativeVideoPath(videoPath);
                test.log(Status.INFO, String.format("<video width='800' controls><source src='%s' type='video/mp4'>%s</video>", 
                        relativePath, description));
                logger.debug("Video attached to test: {}", videoPath);
            } catch (Exception e) {
                logger.error("Failed to attach video: {}", videoPath, e);
            }
        }
    }
    
    /**
     * Create node/child test under current test
     * @param nodeName Node name
     * @return ExtentTest node instance
     */
    public static ExtentTest createNode(String nodeName) {
        return createNode(nodeName, "");
    }
    
    /**
     * Create node/child test under current test with description
     * @param nodeName Node name
     * @param description Node description
     * @return ExtentTest node instance
     */
    public static ExtentTest createNode(String nodeName, String description) {
        ExtentTest test = getCurrentTest();
        if (test != null) {
            try {
                ExtentTest node = test.createNode(nodeName, description);
                logger.debug("Node created: {}", nodeName);
                return node;
            } catch (Exception e) {
                logger.error("Failed to create node: {}", nodeName, e);
            }
        }
        return null;
    }
    
    /**
     * Assign category to current test
     * @param category Category name
     */
    public static void assignCategory(String category) {
        ExtentTest test = getCurrentTest();
        if (test != null && category != null && !category.trim().isEmpty()) {
            try {
                test.assignCategory(category);
                logger.debug("Category assigned to test: {}", category);
            } catch (Exception e) {
                logger.error("Failed to assign category: {}", category, e);
            }
        }
    }
    
    /**
     * Assign author to current test
     * @param author Author name
     */
    public static void assignAuthor(String author) {
        ExtentTest test = getCurrentTest();
        if (test != null && author != null && !author.trim().isEmpty()) {
            try {
                test.assignAuthor(author);
                logger.debug("Author assigned to test: {}", author);
            } catch (Exception e) {
                logger.error("Failed to assign author: {}", author, e);
            }
        }
    }
    
    /**
     * Add additional information to current test
     * @param key Information key
     * @param value Information value
     */
    public static void addTestInfo(String key, String value) {
        ExtentTest test = getCurrentTest();
        if (test != null && key != null && value != null) {
            try {
                test.info(String.format("<b>%s:</b> %s", key, value));
                logger.debug("Test info added - {}: {}", key, value);
            } catch (Exception e) {
                logger.error("Failed to add test info - {}: {}", key, value, e);
            }
        }
    }
    
    /**
     * Flush and finalize reports
     * This should be called after all tests are completed
     */
    public static void flushReports() {
        if (extentReports != null) {
            lock.writeLock().lock();
            try {
                extentReports.flush();
                logger.info("ExtentReports flushed successfully. Report available at: {}", reportPath);
            } catch (Exception e) {
                logger.error("Failed to flush ExtentReports", e);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }
    
    /**
     * Clean up resources and remove current test from thread map
     */
    public static void cleanupTest() {
        try {
            testMap.remove(Thread.currentThread().getId());
            logger.debug("Test cleanup completed for thread: {}", Thread.currentThread().getId());
        } catch (Exception e) {
            logger.error("Failed to cleanup test", e);
        }
    }
    
    /**
     * Reset and reinitialize ExtentReports
     * Useful for test suite reinitialization
     */
    public static void resetReports() {
        lock.writeLock().lock();
        try {
            if (extentReports != null) {
                extentReports.flush();
            }
            extentReports = null;
            testMap.clear();
            isInitialized = false;
            
            logger.info("ExtentReports reset successfully");
            
        } catch (Exception e) {
            logger.error("Failed to reset ExtentReports", e);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Check if ExtentReports is initialized
     * @return true if initialized
     */
    public static boolean isInitialized() {
        return isInitialized && extentReports != null;
    }
    
    /**
     * Get current report file path
     * @return Report file path
     */
    public static String getReportPath() {
        return reportPath;
    }
    
    /**
     * Ensure ExtentReports is initialized before use
     */
    private static void ensureInitialized() {
        if (!isInitialized()) {
            initializeReports();
        }
    }
    
    /**
     * Setup report file path with timestamp
     */
    private static void setupReportPath() {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String fileName = String.format("ExtentReport_%s_%s.html", 
                ConfigurationManager.getFrameworkConfig().getPlatformName().toLowerCase(), 
                timestamp);
        reportPath = FileUtils.getExtentReportsDirectory() + File.separator + fileName;
    }
    
    /**
     * Get relative path for screenshots to work in report
     * @param absolutePath Absolute screenshot path
     * @return Relative path for report
     */
    private static String getRelativeScreenshotPath(String absolutePath) {
        try {
            File reportFile = new File(reportPath);
            File screenshotFile = new File(absolutePath);
            
            // Get relative path from report to screenshot
            String relativePath = reportFile.getParentFile().toPath()
                    .relativize(screenshotFile.toPath()).toString()
                    .replace('\\', '/'); // Use forward slashes for web
            
            return "./" + relativePath;
            
        } catch (Exception e) {
            logger.error("Failed to get relative screenshot path", e);
            return absolutePath; // Fallback to absolute path
        }
    }
    
    /**
     * Get relative path for videos to work in report
     * @param absolutePath Absolute video path
     * @return Relative path for report
     */
    private static String getRelativeVideoPath(String absolutePath) {
        return getRelativeScreenshotPath(absolutePath); // Same logic as screenshots
    }
    
    /**
     * Get custom CSS for report styling
     * @return CSS string
     */
    private static String getCustomCSS() {
        return ".brand-logo { " +
                "color: #FF6600 !important; " +
                "} " +
                ".nav-wrapper { " +
                "background-color: #FF6600 !important; " +
                "} " +
                ".test-node-name { " +
                "font-weight: bold; " +
                "} " +
                ".step-details { " +
                "margin-left: 20px; " +
                "}";
    }
    
    /**
     * Get custom JavaScript for report functionality
     * @return JavaScript string
     */
    private static String getCustomJS() {
        return "// Custom JavaScript for enhanced functionality\n" +
                "$(document).ready(function() {\n" +
                "    // Add custom behavior here\n" +
                "    console.log('Hepsiburada Mobile Test Report Loaded');\n" +
                "});\n";
    }
}