package com.hepsiburada.utils;

// Framework Core Imports: Temel framework bileşenlerine erişim
// ConfigurationManager: Test configuration'ları (screenshot, video, report ayarları)
// DriverManager: Mobile driver lifecycle management
// Bu import'lar olmadan: Framework temel functionality'si çalışmaz
import com.hepsiburada.config.ConfigurationManager;
import com.hepsiburada.drivers.DriverManager;

// Appium Driver: Mobile test automation driver interface
// AppiumDriver: Cross-platform mobile driver (Android/iOS)
// Bu import olmadan: Mobile driver operations yapılamaz
import io.appium.java_client.AppiumDriver;

// Allure Reporting: Advanced test reporting framework
// Allure: Test execution lifecycle management, attachments
// Attachment: Screenshot, video, file attachments için
// Bu import'lar olmadan: Rich test reports oluşturulamaz
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;

// Logging Framework: Test execution tracking ve debugging
// LogManager/Logger: Log4j2 logging system
// Bu import'lar olmadan: Test execution logging yapılamaz
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TestNG Framework: Test lifecycle management
// ITestResult: Test execution result information
// TestNG Annotations: Test lifecycle hooks (@BeforeMethod, @AfterMethod vs.)
// Bu import'lar olmadan: Test lifecycle management yapılamaz
import org.testng.ITestResult;
import org.testng.annotations.*;

// Java Standard Library: I/O ve reflection operations
// ByteArrayInputStream: Video/screenshot data streaming
// Method: Test method reflection (method name, signature)
// Bu import'lar olmadan: File operations ve method introspection yapılamaz
import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;

/**
 * BASE TEST CLASS - TÜM TEST CLASS'LARI İÇİN ORTAK FONKSİYONALİTE
 * 
 * Bu class'ın projede rolü:
 * - Test lifecycle management (setup, teardown, cleanup)
 * - Driver initialization ve management (before/after test)
 * - Screenshot ve video recording automation
 * - Test result handling (pass/fail/skip scenarios)
 * - Reporting integration (Allure, ExtentReports)
 * - Test environment setup ve cleanup
 * - Error handling ve recovery mechanisms
 * 
 * Kullanılmazsa etki:
 * - Her test class'da duplicate setup/teardown code
 * - Inconsistent driver management across tests
 * - Manual screenshot/video handling (error-prone)
 * - No centralized test result handling
 * - Scattered reporting logic
 * - Memory leaks (driver cleanup issues)
 * - Inconsistent error handling patterns
 * 
 * Diğer class'larla ilişkisi:
 * - SearchTests, ProductTests vs.: Bu class'ı extend eder
 * - DriverManager: Driver lifecycle için orchestration
 * - ConfigurationManager: Test behavior configuration
 * - ScreenshotUtils, VideoRecorder: Media capture utilities
 * - ReportManager: Test reporting systems
 * 
 * Design Patterns:
 * - Template Method Pattern: Test lifecycle template, specific implementations
 * - Factory Pattern: Driver initialization delegation
 * - Observer Pattern: Test result event handling
 * - Strategy Pattern: Different reporting strategies
 * 
 * TestNG Integration:
 * - @BeforeSuite/@AfterSuite: Suite-level setup/cleanup
 * - @BeforeClass/@AfterClass: Class-level setup/cleanup
 * - @BeforeMethod/@AfterMethod: Method-level setup/teardown
 * 
 * @author Hepsiburada Test Automation Team
 */
public abstract class BaseTest {
    
    // Logger instance: BaseTest ve subclass'lar için centralized logging
    // Protected static final: Subclass'lar erişebilir, memory efficient
    // BaseTest.class: Log entries'da source class identifier
    // Bu logger olmadan: Test lifecycle tracking ve debugging imkansız
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);
    
    // Driver instance: Test method'larında kullanılacak mobile driver
    // Protected: Subclass'larda test logic'leri erişebilir
    // AppiumDriver: Platform-agnostic interface (Android/iOS both)
    // Method-level lifecycle: Her test method için fresh driver
    // Bu field olmadan: Test classes mobile automation yapamaz
    protected AppiumDriver driver;
    
    // Video recorder instance: Test execution video capture
    // Private: Internal BaseTest management, subclass'lar direct erişim yok
    // Method-level lifecycle: Test başı/sonu video recording
    // Configuration-dependent: Video recording enabled ise kullanılır
    // Bu field olmadan: Test execution video documentation yapılamaz
    private VideoRecorder videoRecorder;
    
    /**
     * TEST SUİTE KURULUM METODİ - SUİTE BAŞLANGICI TEK SEF YARATİLIR
     * 
     * Method amacı: Entire test suite için one-time setup operations yapar
     * Execution timing: Tüm suite'deki testlerden önce tek sefer çalışır
     * Scope: Suite-wide configuration, reporting setup, environment preparation
     * 
     * Kullanılmazsa etki:
     * - Test environment preparation eksik
     * - Reporting systems initialize edilmez
     * - Directory structure oluşturulmaz
     * - Previous run artifacts cleanup yapılmaz
     * - Suite-level configuration missing
     * 
     * Çağrıldığı durumlar:
     * - TestNG suite execution başlangıcında
     * - CI/CD pipeline test runs'larda
     * - Local test execution'larda
     * 
     * alwaysRun=true: Configuration issues olsa bile çalışır
     * 
     * Suite Setup Sequence:
     * 1. Test environment initialization
     * 2. Reporting systems setup
     * 3. Global configurations validation
     */
    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        // Suite setup başlangıç log delimiter (visual separation)
        // Info level: Important milestone, suite lifecycle tracking
        logger.info("=== TEST SUITE SETUP STARTED ===");
        
        // Test environment initialization call (directories, cleanup)
        // initializeTestEnvironment(): File system preparation
        // Bu call olmadan: Test artifacts directory structure eksik
        initializeTestEnvironment();
        
        // Reporting systems setup (Allure, ExtentReports)
        // setupReporting(): Report framework initialization
        // Bu call olmadan: Test results documentation eksik
        setupReporting();
        
        // Suite setup completion log delimiter
        // Info level: Setup completion confirmation
        logger.info("=== TEST SUITE SETUP COMPLETED ===");
    }
    
    /**
     * TEST CLASS KURULUM METODİ - CLASS BAŞLANGICI TEK SEFER YARATİLIR
     * 
     * Method amacı: Specific test class için one-time setup operations yapar
     * Execution timing: Class'daki tüm test method'lardan önce tek sefer
     * Scope: Class-specific configuration, class-level resources
     * 
     * Kullanılmazsa etki:
     * - Class-specific setup eksik (test data, specific configs)
     * - Per-class resource initialization missing
     * - Class context logging eksik
     * - Class-level validation missing
     * 
     * Template Method Pattern:
     * - Base implementation: Common class setup
     * - Subclass override: Specific class requirements
     * - Extension point: Additional class-specific setup
     * 
     * Çağrıldığı durumlar:
     * - Her test class execution'u başlangıcında
     * - Parallel execution'da her class thread'i için
     * - Class-level test suite runs'larda
     * 
     * alwaysRun=true: Class configuration issues olsa bile çalışır
     */
    @BeforeClass(alwaysRun = true)
    public void classSetup() {
        // Class setup başlangıç log (class identification ile)
        // Info level: Class-level milestone, execution tracking
        // getClass().getSimpleName(): Hangi test class'ının setup'u olduğunu belirtir
        logger.info("=== TEST CLASS SETUP STARTED: {} ===", this.getClass().getSimpleName());
        
        // Additional class-specific setup extension point
        // Bu bloğa subclass'lar özel setup logic ekleyebilir
        // Template method pattern: Base structure, specific implementations
        // Örnek: Page object initialization, class-specific test data setup
        
        // Class setup completion log (class identification ile)
        // Info level: Class setup completion confirmation
        logger.info("=== TEST CLASS SETUP COMPLETED: {} ===", this.getClass().getSimpleName());
    }
    
    /**
     * TEST METHOD KURULUM METODİ - HER TEST METHOD ÖNCESİ ÇALIŞIR
     * 
     * Method amacı: Her individual test method için fresh setup yapar
     * Execution timing: Her test method'dan önce çalışır (method-level isolation)
     * Scope: Test-specific resources (driver, video, screenshots, reporting)
     * 
     * Kullanılmazsa etki:
     * - Mobile driver initialization eksik (test çalışmaz)
     * - Test isolation broken (tests affect each other)
     * - Video/screenshot documentation missing
     * - Allure test context missing
     * - Test debugging information insufficient
     * 
     * Test Isolation Strategy:
     * - Fresh driver per test method
     * - Clean test context
     * - Independent media capture
     * - Isolated test reporting
     * 
     * Çağrıldığı durumlar:
     * - Her test method execution öncesi
     * - Test retry scenarios'da
     * - Parallel test execution'da her thread için
     * 
     * Method Setup Sequence:
     * 1. Allure test context setup
     * 2. Mobile driver initialization  
     * 3. Video recording start
     * 4. Initial screenshot capture
     */
    @BeforeMethod(alwaysRun = true)
    public void methodSetup(Method method) {
        // Test method name extraction (reflection-based)
        // method.getName(): Current test method'un name'i
        // Bu name tüm logging ve media files için identifier
        String testName = method.getName();
        
        // Method setup başlangıç log (test identification)
        // Info level: Test method lifecycle tracking
        logger.info("=== TEST METHOD SETUP STARTED: {} ===", testName);
        
        // Allure test context configuration
        // Allure.getLifecycle(): Test execution lifecycle management
        // Test name ve description Allure reports'da görünür
        // Bu configuration olmadan: Allure reports'da test identification eksik
        Allure.getLifecycle().updateTestCase(testResult -> {
            testResult.setName(testName);
            testResult.setDescription("Test method: " + testName);
        });
        
        // Mobile driver initialization (fresh driver per test)
        // initializeDriver(): DriverManager delegation, platform-specific driver
        // Bu initialization olmadan: Test mobile operations yapamaz
        initializeDriver();
        
        // Video recording başlatma (configuration-dependent)
        // startVideoRecording(): Test execution video documentation
        // Configuration check internal'da yapılır
        // Bu call olmadan: Test execution visual documentation eksik
        startVideoRecording(testName);
        
        // Initial screenshot capture (test başlangıç state)
        // takeScreenshot(): Test start state documentation
        // "Test Started" prefix: Screenshot context identification
        // Bu screenshot olmadan: Test başlangıç state documentation eksik
        takeScreenshot("Test Started - " + testName);
        
        // Method setup completion log
        // Info level: Setup completion confirmation, ready for test execution
        logger.info("=== TEST METHOD SETUP COMPLETED: {} ===", testName);
    }
    
    /**
     * TEST METHOD TEMIZLEME METODİ - HER TEST METHOD SONRASI ÇALIŞIR
     * 
     * Method amacı: Her test method sonrası cleanup ve result handling yapar
     * Execution timing: Her test method'dan sonra çalışır (success/failure/skip)
     * Scope: Resource cleanup, result processing, media handling
     * 
     * Kullanılmazsa etki:
     * - Resource leaks (driver sessions active kalır)
     * - Test result documentation eksik
     * - Video/screenshot handling missing
     * - Memory leaks (driver, media resources)
     * - Next test contamination (resource conflicts)
     * 
     * Exception Safety Pattern:
     * - Try-catch: Result handling exceptions'u isolate eder
     * - Finally block: Driver cleanup guaranteed (exception durumunda da)
     * - alwaysRun=true: Configuration issues olsa bile çalışır
     * 
     * Çağrıldığı durumlar:
     * - Her test method completion sonrası (pass/fail/skip)
     * - Test exceptions durumunda
     * - Test interruption scenarios'da
     * 
     * Method Teardown Sequence:
     * 1. Test result analysis ve handling
     * 2. Video recording stop ve processing
     * 3. Driver cleanup (guaranteed in finally)
     * 4. Resource release ve memory cleanup
     */
    @AfterMethod(alwaysRun = true)
    public void methodTeardown(ITestResult result) {
        // Test name extraction from TestNG result
        // result.getMethod().getMethodName(): TestNG API ile test method name
        String testName = result.getMethod().getMethodName();
        
        // Method teardown başlangıç log (test identification)
        // Info level: Test cleanup lifecycle tracking
        logger.info("=== TEST METHOD TEARDOWN STARTED: {} ===", testName);
        
        try {
            // Test result handling (pass/fail/skip scenarios)
            // handleTestResult(): Result-based actions (screenshots, reports)
            // Bu handling olmadan: Test outcomes documentation eksik
            handleTestResult(result);
            
            // Video recording stop ve result-based processing
            // stopVideoRecording(): Video save/delete based on test result
            // Bu call olmadan: Video resources cleanup yapılmaz
            stopVideoRecording(result);
            
        } catch (Exception e) {
            // Teardown process exception handling
            // Error level: Cleanup failure, critical for resource management
            // Bu error handling olmadan: Teardown failures silent kalir
            logger.error("Error in method teardown for test: {}", testName, e);
            
        } finally {
            // Driver quit (guaranteed execution - exception safety)
            // quitDriver(): Mobile driver session termination
            // Finally block: Exception durumunda da driver cleanup yapilir
            // Bu cleanup olmadan: Driver sessions accumulate, resource leak
            quitDriver();
            
            // Method teardown completion log
            // Info level: Cleanup completion confirmation
            logger.info("=== TEST METHOD TEARDOWN COMPLETED: {} ===", testName);
        }
    }
    
    /**
     * TEST CLASS TEMIZLEME METODİ - CLASS SONUNDA TEK SEFER ÇALIŞIR
     * 
     * Method amacı: Test class tamamlandıktan sonra class-level cleanup yapar
     * Execution timing: Class'daki tüm test method'lar bittikten sonra tek sefer
     * Scope: Class-level resource cleanup, class-specific finalization
     * 
     * Kullanılmazsa etki:
     * - Class-level resources cleanup edilmez
     * - Class-specific finalization missing
     * - Class execution lifecycle tracking eksik
     * - Class-level resource leaks
     * 
     * Template Method Pattern:
     * - Base implementation: Common class cleanup
     * - Extension point: Subclass'lar specific cleanup ekleyebilir
     * - Standardized lifecycle: Consistent class resource management
     * 
     * Çağrıldığı durumlar:
     * - Test class'daki tüm method'lar tamamlandıktan sonra
     * - Parallel execution'da class thread completion
     * - Class-level test suite completion
     * 
     * alwaysRun=true: Class issues olsa bile cleanup çalışır
     */
    @AfterClass(alwaysRun = true)
    public void classTeardown() {
        // Class teardown başlangıç log (class identification)
        // Info level: Class-level cleanup milestone tracking
        logger.info("=== TEST CLASS TEARDOWN STARTED: {} ===", this.getClass().getSimpleName());
        
        // Additional class-specific teardown extension point
        // Bu bloğa subclass'lar özel cleanup logic ekleyebilir
        // Template method pattern: Base structure, specific cleanup implementations
        // Örnek: Class-specific resources, database connections, external services
        
        // Class teardown completion log
        // Info level: Class cleanup completion confirmation
        logger.info("=== TEST CLASS TEARDOWN COMPLETED: {} ===", this.getClass().getSimpleName());
    }
    
    /**
     * TEST SUİTE TEMIZLEME METODİ - SUİTE SONUNDA TEK SEFER ÇALIŞIR
     * 
     * Method amacı: Entire test suite completion sonrası finalization operations
     * Execution timing: Suite'daki tüm test'ler tamamlandıktan sonra tek sefer
     * Scope: Suite-wide finalization, report generation, environment cleanup
     * 
     * Kullanılmazsa etki:
     * - Test reports finalize edilmez (incomplete reports)
     * - Test environment cleanup missing (temp files, resources)
     * - Suite-level artifacts cleanup yapılmaz
     * - Final reporting missing
     * - Resource cleanup incomplete
     * 
     * Suite Finalization Responsibilities:
     * - Report generation ve finalization
     * - Environment cleanup (temp files, directories)
     * - Resource release (connections, handles)
     * - Final artifacts processing
     * 
     * Çağrıldığı durumlar:
     * - TestNG suite execution completion
     * - CI/CD pipeline test runs bitiminde
     * - Local test execution tamamlandıktan sonra
     * 
     * Suite Teardown Sequence:
     * 1. Test reports generation ve finalization
     * 2. Test environment cleanup operations
     * 3. Final resource release
     */
    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        // Suite teardown başlangıç log delimiter
        // Info level: Suite finalization milestone
        logger.info("=== TEST SUITE TEARDOWN STARTED ===");
        
        // Test reports generation ve finalization
        // generateReports(): Allure, ExtentReports finalization
        // Bu call olmadan: Test reports incomplete, final artifacts missing
        generateReports();
        
        // Test environment cleanup (temp files, directories)
        // cleanupTestEnvironment(): File system cleanup, resource release
        // Bu call olmadan: Test artifacts accumulate, disk space issues
        cleanupTestEnvironment();
        
        // Suite teardown completion log delimiter
        // Info level: Suite finalization completion
        logger.info("=== TEST SUITE TEARDOWN COMPLETED ===");
    }
    
    /**
     * Initialize test environment
     */
    private void initializeTestEnvironment() {
        logger.info("Initializing test environment");
        
        // Create necessary directories
        FileUtils.createDirectories();
        
        // Clear previous test artifacts
        FileUtils.cleanupPreviousRuns();
        
        logger.info("Test environment initialized successfully");
    }
    
    /**
     * Setup reporting systems
     */
    private void setupReporting() {
        logger.info("Setting up reporting systems");
        
        // Initialize Allure environment
        setupAllureEnvironment();
        
        // Initialize ExtentReports if enabled
        if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
            ExtentReportManager.initializeReports();
        }
        
        logger.info("Reporting systems setup completed");
    }
    
    /**
     * Initialize Appium driver
     */
    private void initializeDriver() {
        try {
            logger.info("Initializing Appium driver");
            driver = DriverManager.initializeDriver();
            logger.info("Appium driver initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize Appium driver", e);
            throw new RuntimeException("Driver initialization failed", e);
        }
    }
    
    /**
     * Quit Appium driver
     */
    private void quitDriver() {
        try {
            if (DriverManager.isDriverInitialized()) {
                logger.info("Quitting Appium driver");
                DriverManager.quitDriver();
                logger.info("Appium driver quit successfully");
            }
        } catch (Exception e) {
            logger.error("Error occurred while quitting driver", e);
        }
    }
    
    /**
     * Handle test result based on status
     * @param result Test result
     */
    private void handleTestResult(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                logger.info("Test PASSED: {}", testName);
                handlePassedTest(testName);
                break;
            case ITestResult.FAILURE:
                logger.error("Test FAILED: {}", testName);
                handleFailedTest(testName, result.getThrowable());
                break;
            case ITestResult.SKIP:
                logger.warn("Test SKIPPED: {}", testName);
                handleSkippedTest(testName);
                break;
            default:
                logger.warn("Test completed with unknown status: {}", testName);
        }
    }
    
    /**
     * Handle passed test
     * @param testName Test name
     */
    private void handlePassedTest(String testName) {
        // Take screenshot if configured
        if (ConfigurationManager.getFrameworkConfig().isScreenshotOnPass()) {
            takeScreenshot("Test Passed - " + testName);
        }
        
        // Add to ExtentReports if enabled
        if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
            ExtentReportManager.logPass("Test passed successfully");
        }
    }
    
    /**
     * Handle failed test
     * @param testName Test name
     * @param throwable Test failure cause
     */
    private void handleFailedTest(String testName, Throwable throwable) {
        // Take screenshot
        if (ConfigurationManager.getFrameworkConfig().isScreenshotOnFailure()) {
            takeScreenshot("Test Failed - " + testName);
        }
        
        // Log failure details
        logger.error("Test failure details for {}: {}", testName, throwable.getMessage(), throwable);
        
        // Add to Allure
        Allure.addAttachment("Failure Details", throwable.getMessage());
        
        // Add to ExtentReports if enabled
        if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
            ExtentReportManager.logFail("Test failed: " + throwable.getMessage());
        }
    }
    
    /**
     * Handle skipped test
     * @param testName Test name
     */
    private void handleSkippedTest(String testName) {
        // Add to ExtentReports if enabled
        if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
            ExtentReportManager.logSkip("Test was skipped");
        }
    }
    
    /**
     * Take screenshot and attach to reports
     * @param screenshotName Screenshot name
     */
    protected void takeScreenshot(String screenshotName) {
        try {
            if (DriverManager.isDriverInitialized()) {
                logger.debug("Taking screenshot: {}", screenshotName);
                byte[] screenshot = ScreenshotUtils.takeScreenshot();
                
                // Attach to Allure
                attachScreenshotToAllure(screenshotName, screenshot);
                
                // Save to file
                ScreenshotUtils.saveScreenshot(screenshot, screenshotName);
                
                logger.debug("Screenshot captured successfully: {}", screenshotName);
            }
        } catch (Exception e) {
            logger.error("Failed to take screenshot: {}", screenshotName, e);
        }
    }
    
    /**
     * Attach screenshot to Allure report
     * @param name Screenshot name
     * @param screenshot Screenshot bytes
     */
    @Attachment(value = "{name}", type = "image/png")
    private byte[] attachScreenshotToAllure(String name, byte[] screenshot) {
        return screenshot;
    }
    
    /**
     * Start video recording for test
     * @param testName Test name
     */
    private void startVideoRecording(String testName) {
        if (ConfigurationManager.getFrameworkConfig().isVideoRecordingEnabled()) {
            try {
                logger.info("Starting video recording for test: {}", testName);
                videoRecorder = new VideoRecorder(testName);
                videoRecorder.startRecording();
                logger.info("Video recording started for test: {}", testName);
            } catch (Exception e) {
                logger.error("Failed to start video recording for test: {}", testName, e);
            }
        }
    }
    
    /**
     * Stop video recording and handle based on test result
     * @param result Test result
     */
    private void stopVideoRecording(ITestResult result) {
        if (videoRecorder != null) {
            try {
                String testName = result.getMethod().getMethodName();
                logger.info("Stopping video recording for test: {}", testName);
                
                String videoPath = videoRecorder.stopRecording();
                
                // Handle video based on test result
                if (result.getStatus() == ITestResult.SUCCESS && 
                    ConfigurationManager.getFrameworkConfig().isDeleteVideoOnPass()) {
                    // Delete video for passed tests if configured
                    FileUtils.deleteFile(videoPath);
                    logger.info("Video deleted for passed test: {}", testName);
                } else {
                    // Attach video to Allure for failed/skipped tests
                    attachVideoToAllure(videoPath);
                    logger.info("Video saved for test: {} at {}", testName, videoPath);
                }
            } catch (Exception e) {
                logger.error("Failed to stop video recording", e);
            }
        }
    }
    
    /**
     * Attach video to Allure report
     * @param videoPath Video file path
     */
    private void attachVideoToAllure(String videoPath) {
        try {
            byte[] videoBytes = FileUtils.readFileAsBytes(videoPath);
            Allure.addAttachment("Test Video", "video/mp4", new ByteArrayInputStream(videoBytes), "mp4");
        } catch (Exception e) {
            logger.error("Failed to attach video to Allure: {}", videoPath, e);
        }
    }
    
    /**
     * Setup Allure environment properties
     */
    private void setupAllureEnvironment() {
        try {
            AllureEnvironmentUtils.setEnvironmentInformation();
        } catch (Exception e) {
            logger.error("Failed to setup Allure environment", e);
        }
    }
    
    /**
     * Generate reports after test execution
     */
    private void generateReports() {
        logger.info("Generating test reports");
        
        try {
            // Finalize ExtentReports if enabled
            if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
                ExtentReportManager.flushReports();
            }
            
            logger.info("Test reports generated successfully");
        } catch (Exception e) {
            logger.error("Failed to generate test reports", e);
        }
    }
    
    /**
     * Cleanup test environment
     */
    private void cleanupTestEnvironment() {
        logger.info("Cleaning up test environment");
        
        try {
            // Additional cleanup can be added here
            
            logger.info("Test environment cleanup completed");
        } catch (Exception e) {
            logger.error("Failed to cleanup test environment", e);
        }
    }
    
    /**
     * Get current driver instance
     * @return AppiumDriver instance
     */
    protected AppiumDriver getDriver() {
        return DriverManager.getDriver();
    }
}