package com.hepsiburada.hooks;

// Framework Core Components: Temel framework bileşenlerine erişim
// ConfigurationManager: Test behavior configuration (screenshot, video, reporting)
// DriverManager: Mobile driver lifecycle management ve platform detection
// Utility classes: File operations, screenshot, video, reporting utilities
// Bu import'lar olmadan: Cucumber hooks temel framework functionality'sine erişemez
import com.hepsiburada.config.ConfigurationManager;
import com.hepsiburada.drivers.DriverManager;
import com.hepsiburada.utils.*;

// Appium Mobile Driver: Mobile automation driver interface
// AppiumDriver: Cross-platform mobile driver (Android/iOS)
// Bu import olmadan: Mobile driver operations yapılamaz
import io.appium.java_client.AppiumDriver;

// Cucumber Framework: BDD test lifecycle management
// Cucumber Annotations: @Before, @After, @BeforeAll, @AfterAll hooks
// Scenario: Cucumber test scenario context ve result information
// Bu import'lar olmadan: BDD hooks ve scenario management çalışmaz
import io.cucumber.java.*;

// Allure Reporting: Advanced test reporting framework
// Allure: Test execution lifecycle, attachments, parameters
// Status: Test result status enumeration (PASSED, FAILED, SKIPPED)
// Bu import'lar olmadan: Rich test reports oluşturulamaz
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;

// Logging Framework: Test execution tracking ve debugging
// LogManager/Logger: Log4j2 logging system
// Bu import'lar olmadan: Hook operations logging yapılamaz
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Java I/O: Data streaming operations
// ByteArrayInputStream: Screenshot, video data streaming to reports
// Bu import olmadan: Media attachments reports'a eklenemez
import java.io.ByteArrayInputStream;

/**
 * CUCUMBER TEST HOOKS - BDD TEST LİFECYCLE YÖNETİMİ
 * 
 * Bu class'ın projede rolü:
 * - Cucumber BDD test lifecycle management (scenario setup/teardown)
 * - Mobile driver initialization ve cleanup (scenario isolation)
 * - Video recording automation (scenario-based recording)
 * - Screenshot capture ve attachment (visual documentation)
 * - Test reporting integration (Allure, ExtentReports)
 * - Test environment management (directories, cleanup)
 * - Error handling ve recovery mechanisms
 * 
 * Kullanılmazsa etki:
 * - Scenario isolation broken (tests affect each other)
 * - Manual driver management (error-prone, resource leaks)
 * - No video/screenshot documentation
 * - Test environment setup eksik
 * - Inconsistent reporting across scenarios
 * - Resource leaks (driver sessions, media files)
 * - No centralized error handling
 * 
 * Diğer class'larla ilişkisi:
 * - BaseTest: Similar functionality ama TestNG vs Cucumber hooks
 * - DriverManager: Driver lifecycle için delegation
 * - ConfigurationManager: Hook behavior configuration
 * - Utility classes: Screenshot, video, file operations
 * - Step Definitions: Bu hooks step'lerin çalışması için environment hazırlar
 * 
 * Cucumber Hook Types:
 * - @BeforeAll/@AfterAll: Suite-level setup/cleanup (static)
 * - @Before/@After: Scenario-level setup/teardown (instance)
 * - Automatic execution: Cucumber framework tarafından otomatik çağrılır
 * 
 * Design Patterns:
 * - Template Method Pattern: Hook structure, specific implementations
 * - Observer Pattern: Scenario lifecycle events handling
 * - Factory Pattern: Driver creation delegation
 * - Strategy Pattern: Different reporting strategies
 * 
 * @author Hepsiburada Test Automation Team
 */
public class TestHooks {
    
    // Logger instance: TestHooks operations için centralized logging
    // Static final: Memory efficient, immutable reference
    // TestHooks.class: Log entries'da source class identifier
    // Bu logger olmadan: Hook operations tracking ve debugging yapılamaz
    private static final Logger logger = LogManager.getLogger(TestHooks.class);
    
    // Video recorder instance: Scenario-based video capture
    // Instance field: Her TestHooks instance'ı için ayrı video recorder
    // Scenario lifecycle: Scenario başında start, sonunda stop
    // Configuration-dependent: Video recording enabled ise kullanılır
    // Bu field olmadan: Scenario execution video documentation yapılamaz
    private VideoRecorder videoRecorder;
    
    // Scenario timing: Performance measurement ve duration tracking
    // long: Millisecond precision için sufficient
    // Scenario start time: System.currentTimeMillis() ile set edilir
    // Bu field olmadan: Scenario execution duration calculate edilemez
    private long scenarioStartTime;
    
    // Current scenario context: Active scenario name tracking
    // String: Scenario name storage (file naming, logging için)
    // Updated in @Before hook: Her scenario başında set edilir
    // Bu field olmadan: Scenario context logging ve file naming yapılamaz
    private String currentScenarioName;
    
    /**
     * CUCUMBER SU\u0130TE BA\u015eLATMA HOOK'U - T\u00dcM SCENARIO'LARDAN \u00d6NCE TEK SEFER
     * 
     * Method amac\u0131: Entire Cucumber test suite i\u00e7in one-time setup operations yapar
     * Execution timing: T\u00fcm scenario'lardan \u00f6nce tek sefer \u00e7al\u0131\u015f\u0131r
     * Scope: Suite-wide configuration, environment preparation, reporting setup
     * 
     * Kullan\u0131lmazsa etki:\n     * - Test environment haz\u0131rlanmaz (directories, cleanup)\n     * - Reporting systems initialize edilmez\n     * - Allure environment configuration missing\n     * - Suite-level resource allocation eksik\n     * - Global test configurations uygulanmaz\n     * \n     * Cucumber @BeforeAll:\n     * - Static method: Class instance gerekmez\n     * - Suite scope: T\u00fcm feature files i\u00e7in tek execution\n     * - Automatic execution: Cucumber framework otomatik \u00e7a\u011fr\u0131r\n     * \n     * \u00c7a\u011fr\u0131ld\u0131\u011f\u0131 durumlar:\n     * - Cucumber test suite execution ba\u015flang\u0131c\u0131nda\n     * - CI/CD pipeline BDD test runs'larda\n     * - Local BDD test execution'larda\n     * \n     * Suite Setup Sequence:\n     * 1. Test environment initialization (directories, cleanup)\n     * 2. Reporting systems setup (ExtentReports, configurations)\n     * 3. Allure environment preparation (metadata, properties)\n     */\n    @BeforeAll\n    public static void beforeAll() {\n        // Suite setup ba\u015flang\u0131\u00e7 log delimiter (visual separation)\n        // Info level: Major milestone, suite lifecycle tracking\n        logger.info(\"=== TEST SUITE SETUP STARTED ===\");\n        \n        try {\n            // Test environment initialization call (file system preparation)\n            // initializeTestEnvironment(): Directories, cleanup, prerequisites\n            // Bu call olmadan: Test artifacts directory structure eksik, previous runs contamination\n            initializeTestEnvironment();\n            \n            // Reporting systems setup (Allure, ExtentReports initialization)\n            // setupReportingSystems(): Report framework configuration\n            // Bu call olmadan: Test results documentation eksik, reports generate edilemez\n            setupReportingSystems();\n            \n            // Allure environment setup (metadata, properties, environment info)\n            // setupAllureEnvironment(): Allure-specific environment configuration\n            // Bu call olmadan: Allure reports environment context eksik\n            setupAllureEnvironment();\n            \n            // Suite setup completion log delimiter\n            // Info level: Setup success confirmation\n            logger.info(\"=== TEST SUITE SETUP COMPLETED ===\");\n            \n        } catch (Exception e) {\n            // Suite setup critical failure handling\n            // Error level: Suite setup failure, critical for all scenarios\n            // Bu error handling olmadan: Setup failures silent kalabilir\n            logger.error(\"Failed to setup test suite\", e);\n            \n            // RuntimeException wrap: Suite setup failure should stop execution\n            // Bu throw olmadan: Broken environment'da testler \u00e7al\u0131\u015fmaya \u00e7al\u0131\u015f\u0131r\n            throw new RuntimeException(\"Test suite setup failed\", e);\n        }\n    }
    
    /**
     * CUCUMBER SCENARIO BA\u015eLATMA HOOK'U - HER SCENARIO \u00d6NCES\u0130 \u00c7ALI\u015eIR
     * 
     * Method amac\u0131: Her individual Cucumber scenario i\u00e7in fresh setup yapar
     * Parametreler: scenario - Cucumber scenario context (name, tags, status)\n     * Execution timing: Her scenario'dan \u00f6nce \u00e7al\u0131\u015f\u0131r (scenario isolation)\n     * \n     * Kullan\u0131lmazsa etki:\n     * - Mobile driver initialization eksik (scenario \u00e7al\u0131\u015fmaz)\n     * - Scenario isolation broken (scenarios affect each other)\n     * - Video/screenshot documentation missing\n     * - Reporting context eksik (Allure, ExtentReports)\n     * - Scenario timing measurement yap\u0131lamaz\n     * \n     * Scenario Isolation Strategy:\n     * - Fresh mobile driver per scenario\n     * - Independent video recording\n     * - Isolated screenshot capture\n     * - Separate reporting context\n     * \n     * \u00c7a\u011fr\u0131ld\u0131\u011f\u0131 durumlar:\n     * - Her Cucumber scenario execution \u00f6ncesi\n     * - Scenario retry durumlar\u0131nda\n     * - Parallel scenario execution'da her thread i\u00e7in\n     * \n     * Scenario Setup Sequence:\n     * 1. Scenario context initialization (name, timing)\n     * 2. Reporting setup (Allure, ExtentReports context)\n     * 3. Mobile driver initialization\n     * 4. Video recording start\n     * 5. Initial screenshot capture\n     * 6. Scenario start logging\n     */\n    @Before\n    public void before(Scenario scenario) {\n        // Scenario context initialization\n        // scenario.getName(): Cucumber scenario name extraction\n        // Bu assignment olmadan: Scenario context tracking yap\u0131lamaz\n        currentScenarioName = scenario.getName();\n        \n        // Scenario timing ba\u015flang\u0131\u00e7 timestamp (performance measurement i\u00e7in)\n        // System.currentTimeMillis(): High precision timestamp\n        // Bu timing olmadan: Scenario duration calculate edilemez\n        scenarioStartTime = System.currentTimeMillis();\n        \n        // Scenario setup ba\u015flang\u0131\u00e7 log (scenario identification)\n        // Info level: Scenario lifecycle tracking\n        logger.info(\"=== SCENARIO SETUP STARTED: {} ===\", currentScenarioName);\n        \n        try {\n            // Scenario reporting context setup (Allure, ExtentReports)\n            // setupScenarioReporting(): Report frameworks i\u00e7in scenario context\n            // Bu setup olmadan: Reports'da scenario identification ve context eksik\n            setupScenarioReporting(scenario);\n            \n            // Mobile driver initialization (fresh driver per scenario)\n            // initializeDriver(): DriverManager delegation, platform-specific driver\n            // Bu initialization olmadan: Scenario mobile operations yapamaz\n            initializeDriver();\n            \n            // Video recording ba\u015flatma (configuration-dependent)\n            // startVideoRecording(): Scenario execution video documentation\n            // Bu call olmadan: Scenario execution visual documentation eksik\n            startVideoRecording();\n            \n            // Initial screenshot capture (scenario ba\u015flang\u0131\u00e7 state)\n            // takeInitialScreenshot(): Scenario start state documentation\n            // Bu screenshot olmadan: Scenario ba\u015flang\u0131\u00e7 state record edilmez\n            takeInitialScreenshot();\n            \n            // Scenario start logging (scenario metadata, tags, context)\n            // logScenarioStart(): Detailed scenario information logging\n            // Bu logging olmadan: Scenario context information eksik\n            logScenarioStart(scenario);\n            \n            // Scenario setup completion log\n            // Info level: Setup success confirmation, scenario ready for execution\n            logger.info(\"=== SCENARIO SETUP COMPLETED: {} ===\", currentScenarioName);\n            \n        } catch (Exception e) {\n            // Scenario setup critical failure handling\n            // Error level: Setup failure, scenario cannot execute\n            logger.error(\"Failed to setup scenario: {}\", currentScenarioName, e);\n            \n            // Setup failure screenshot capture (debugging i\u00e7in)\n            // takeScreenshotOnSetupFailure(): Setup error state documentation\n            // Bu screenshot olmadan: Setup failure debugging zorla\u015f\u0131r\n            takeScreenshotOnSetupFailure();\n            \n            // Allure report'a setup failure information ekleme\n            // Allure.step(): Failed step documentation\n            // Allure.addAttachment(): Error details attachment\n            Allure.step(\"Scenario Setup Failed\", Status.FAILED);\n            Allure.addAttachment(\"Setup Failure\", e.getMessage());\n            \n            // RuntimeException wrap: Setup failure should stop scenario execution\n            // Bu throw olmadan: Broken setup'la scenario \u00e7al\u0131\u015fmaya \u00e7al\u0131\u015f\u0131r\n            throw new RuntimeException(\"Scenario setup failed: \" + currentScenarioName, e);\n        }\n    }
    
    /**
     * CUCUMBER SCENARIO SONLANDIRMA HOOK'U - HER SCENARIO SONRASI \u00c7ALI\u015eIR
     * 
     * Method amac\u0131: Her Cucumber scenario sonras\u0131 cleanup ve result processing yapar
     * Parametreler: scenario - Cucumber scenario context (result status, timing)
     * Execution timing: Her scenario'dan sonra \u00e7al\u0131\u015f\u0131r (pass/fail/skip)\n     * \n     * Kullan\u0131lmazsa etki:\n     * - Resource leaks (driver sessions, video files)\n     * - Test result documentation eksik\n     * - Video/screenshot handling missing\n     * - Memory leaks (driver, media resources)\n     * - Next scenario contamination (resource conflicts)\n     * - Performance metrics missing\n     * \n     * Exception Safety Pattern:\n     * - Try-catch: Result handling exceptions isolate eder\n     * - Finally block: Cleanup guaranteed (exception durumunda da)\n     * - Error recovery: Teardown failures scenario results'u etkilemez\n     * \n     * \u00c7a\u011fr\u0131ld\u0131\u011f\u0131 durumlar:\n     * - Her scenario completion sonras\u0131 (pass/fail/skip)\n     * - Scenario exceptions durumunda\n     * - Scenario interruption scenarios'da\n     * \n     * Scenario Teardown Sequence:\n     * 1. Scenario duration calculation\n     * 2. Result-based handling (screenshots, reporting)\n     * 3. Video recording stop ve processing\n     * 4. Final screenshot capture\n     * 5. Report summary addition\n     * 6. Scenario completion logging\n     * 7. Resource cleanup (guaranteed in finally)\n     */\n    @After\n    public void after(Scenario scenario) {\n        // Scenario teardown ba\u015flang\u0131\u00e7 log (scenario identification)\n        // Info level: Scenario cleanup lifecycle tracking\n        logger.info(\"=== SCENARIO TEARDOWN STARTED: {} ===\", currentScenarioName);\n        \n        try {\n            // Scenario execution duration calculation (performance metrics)\n            // System.currentTimeMillis() - scenarioStartTime: Total execution time\n            // Bu calculation olmadan: Scenario performance metrics missing\n            long duration = System.currentTimeMillis() - scenarioStartTime;\n            \n            // Scenario result handling (pass/fail/skip based actions)\n            // handleScenarioResult(): Result-specific screenshots, reporting\n            // Bu handling olmadan: Scenario outcomes documentation eksik\n            handleScenarioResult(scenario, duration);\n            \n            // Video recording stop ve result-based processing\n            // stopVideoRecording(): Video save/delete based on scenario result\n            // Bu call olmadan: Video resources cleanup yap\u0131lmaz\n            stopVideoRecording(scenario);\n            \n            // Final screenshot capture (scenario end state)\n            // takeFinalScreenshot(): Scenario completion state documentation\n            // Bu screenshot olmadan: Scenario end state record edilmez\n            takeFinalScreenshot(scenario);\n            \n            // Scenario summary addition to reports (duration, status)\n            // addScenarioSummaryToReports(): Report summary information\n            // Bu summary olmadan: Report metrics eksik\n            addScenarioSummaryToReports(scenario, duration);\n            \n            // Scenario completion logging (status, duration, metadata)\n            // logScenarioCompletion(): Final scenario information\n            // Bu logging olmadan: Scenario completion tracking eksik\n            logScenarioCompletion(scenario, duration);\n            \n            // Scenario teardown completion log\n            // Info level: Teardown success confirmation\n            logger.info(\"=== SCENARIO TEARDOWN COMPLETED: {} ===\", currentScenarioName);\n            \n        } catch (Exception e) {\n            // Teardown process exception handling\n            // Error level: Teardown failure, critical for resource management\n            // Bu error handling olmadan: Teardown failures silent kalir\n            logger.error(\"Error in scenario teardown for: {}\", currentScenarioName, e);\n            \n        } finally {\n            // Resource cleanup (guaranteed execution - exception safety)\n            // cleanup(): Driver quit, memory cleanup, resource release\n            // Finally block: Exception durumunda da cleanup yap\u0131l\u0131r\n            // Bu cleanup olmadan: Resources accumulate, memory leaks\n            cleanup();\n        }\n    }
    
    /**
     * After All Hook - executed once after all scenarios
     * Finalizes reporting and cleans up test environment
     */
    @AfterAll
    public static void afterAll() {
        logger.info("=== TEST SUITE TEARDOWN STARTED ===");
        
        try {
            // Finalize reporting
            finalizeReporting();
            
            // Cleanup test environment
            cleanupTestEnvironment();
            
            // Generate final test reports
            generateFinalReports();
            
            logger.info("=== TEST SUITE TEARDOWN COMPLETED ===");
            
        } catch (Exception e) {
            logger.error("Failed to teardown test suite", e);
        }
    }
    
    // ===================================================================================
    // Private Helper Methods
    // ===================================================================================
    
    /**
     * Initialize test environment
     */
    private static void initializeTestEnvironment() {
        logger.info("Initializing test environment");
        
        try {
            // Create necessary directories
            FileUtils.createDirectories();
            
            // Clean up previous test runs
            FileUtils.cleanupPreviousRuns();
            
            // Ensure all required directories exist
            ScreenshotUtils.ensureScreenshotDirectoryExists();
            
            logger.info("Test environment initialized successfully");
            
        } catch (Exception e) {
            logger.error("Failed to initialize test environment", e);
            throw new RuntimeException("Test environment initialization failed", e);
        }
    }
    
    /**
     * Setup reporting systems
     */
    private static void setupReportingSystems() {
        logger.info("Setting up reporting systems");
        
        try {
            // Initialize ExtentReports if enabled
            if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
                ExtentReportManager.initializeReports();
                logger.info("ExtentReports initialized successfully");
            }
            
            logger.info("Reporting systems setup completed");
            
        } catch (Exception e) {
            logger.error("Failed to setup reporting systems", e);
            throw new RuntimeException("Reporting systems setup failed", e);
        }
    }
    
    /**
     * Setup Allure environment
     */
    private static void setupAllureEnvironment() {
        logger.info("Setting up Allure environment");
        
        try {
            AllureEnvironmentUtils.setEnvironmentInformation();
            logger.info("Allure environment setup completed");
            
        } catch (Exception e) {
            logger.error("Failed to setup Allure environment", e);
            // Don't throw exception, just log warning as it's not critical
            logger.warn("Allure environment setup failed, continuing without it");
        }
    }
    
    /**
     * Setup scenario reporting
     * @param scenario Cucumber scenario
     */
    private void setupScenarioReporting(Scenario scenario) {
        try {
            // Add scenario info to Allure
            Allure.getLifecycle().updateTestCase(testResult -> {
                testResult.setName(scenario.getName());
                testResult.setDescription("Scenario: " + scenario.getName());
                
                // Add tags as labels
                scenario.getSourceTagNames().forEach(tag -> 
                    Allure.label("tag", tag.replace("@", "")));
            });
            
            // Create ExtentReports test if enabled
            if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
                ExtentReportManager.createTest(scenario.getName(), "Cucumber Scenario");
                
                // Add tags as categories
                scenario.getSourceTagNames().forEach(tag -> 
                    ExtentReportManager.assignCategory(tag.replace("@", "")));
            }
            
        } catch (Exception e) {
            logger.warn("Failed to setup scenario reporting", e);
        }
    }
    
    /**
     * Initialize Appium driver
     */
    private void initializeDriver() {
        logger.info("Initializing Appium driver");
        
        try {
            AppiumDriver driver = DriverManager.initializeDriver();
            logger.info("Appium driver initialized successfully: {} on {}", 
                       DriverManager.getPlatformName(), DriverManager.getDeviceName());
            
            // Add driver info to Allure
            Allure.parameter("Platform", DriverManager.getPlatformName());
            Allure.parameter("Device", DriverManager.getDeviceName());
            
        } catch (Exception e) {
            logger.error("Failed to initialize Appium driver", e);
            throw new RuntimeException("Driver initialization failed", e);
        }
    }
    
    /**
     * Start video recording if enabled
     */
    private void startVideoRecording() {
        if (ConfigurationManager.getFrameworkConfig().isVideoRecordingEnabled()) {
            try {
                logger.info("Starting video recording for scenario: {}", currentScenarioName);
                videoRecorder = new VideoRecorder(sanitizeScenarioName(currentScenarioName));
                videoRecorder.startRecording();
                logger.info("Video recording started successfully");
                
            } catch (Exception e) {
                logger.error("Failed to start video recording for scenario: {}", currentScenarioName, e);
                // Don't fail the test for video recording issues
            }
        }
    }
    
    /**
     * Take initial screenshot
     */
    private void takeInitialScreenshot() {
        try {
            String screenshotPath = ScreenshotUtils.takeScreenshot("Scenario_Start_" + sanitizeScenarioName(currentScenarioName));
            
            if (!screenshotPath.isEmpty()) {
                // Attach to Allure
                byte[] screenshot = ScreenshotUtils.takeScreenshot();
                Allure.addAttachment("Scenario Start", "image/png", 
                    new ByteArrayInputStream(screenshot), "png");
                
                // Log to ExtentReports if enabled
                if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
                    ExtentReportManager.logInfo("Scenario started - screenshot captured");
                }
            }
            
        } catch (Exception e) {
            logger.warn("Failed to take initial screenshot", e);
        }
    }
    
    /**
     * Log scenario start
     * @param scenario Cucumber scenario
     */
    private void logScenarioStart(Scenario scenario) {
        logger.info("Scenario started: {}", scenario.getName());
        logger.info("Scenario tags: {}", scenario.getSourceTagNames());
        logger.info("Scenario URI: {}", scenario.getUri());
        
        // Add to Allure
        Allure.step("Scenario Started: " + scenario.getName());
        
        // Log to ExtentReports if enabled
        if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
            ExtentReportManager.logInfo("Scenario execution started");
        }
    }
    
    /**
     * Handle scenario result based on status
     * @param scenario Cucumber scenario
     * @param duration Scenario duration
     */
    private void handleScenarioResult(Scenario scenario, long duration) {
        String status = scenario.getStatus().toString();
        
        switch (scenario.getStatus()) {
            case PASSED:
                handlePassedScenario(scenario, duration);
                break;
            case FAILED:
                handleFailedScenario(scenario, duration);
                break;
            case SKIPPED:
                handleSkippedScenario(scenario, duration);
                break;
            default:
                logger.warn("Unknown scenario status: {} for scenario: {}", status, scenario.getName());
        }
    }
    
    /**
     * Handle passed scenario
     * @param scenario Cucumber scenario
     * @param duration Scenario duration
     */
    private void handlePassedScenario(Scenario scenario, long duration) {
        logger.info("✓ Scenario PASSED: {} ({}ms)", scenario.getName(), duration);
        
        // Take screenshot if configured
        if (ConfigurationManager.getFrameworkConfig().isScreenshotOnPass()) {
            String screenshotPath = ScreenshotUtils.takeScreenshotOnPass(sanitizeScenarioName(scenario.getName()));
            if (!screenshotPath.isEmpty()) {
                attachScreenshotToScenario(scenario, screenshotPath, "Scenario Passed");
            }
        }
        
        // Log to ExtentReports if enabled
        if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
            ExtentReportManager.logPass("Scenario passed successfully in " + duration + "ms");
        }
    }
    
    /**
     * Handle failed scenario
     * @param scenario Cucumber scenario
     * @param duration Scenario duration
     */
    private void handleFailedScenario(Scenario scenario, long duration) {
        logger.error("✗ Scenario FAILED: {} ({}ms)", scenario.getName(), duration);
        
        // Take failure screenshot
        if (ConfigurationManager.getFrameworkConfig().isScreenshotOnFailure()) {
            String screenshotPath = ScreenshotUtils.takeScreenshotOnFailure(
                sanitizeScenarioName(scenario.getName()), new RuntimeException("Scenario failed"));
            if (!screenshotPath.isEmpty()) {
                attachScreenshotToScenario(scenario, screenshotPath, "Scenario Failed");
            }
        }
        
        // Log to ExtentReports if enabled
        if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
            ExtentReportManager.logFail("Scenario failed after " + duration + "ms");
        }
    }
    
    /**
     * Handle skipped scenario
     * @param scenario Cucumber scenario
     * @param duration Scenario duration
     */
    private void handleSkippedScenario(Scenario scenario, long duration) {
        logger.warn("⚠ Scenario SKIPPED: {} ({}ms)", scenario.getName(), duration);
        
        // Log to ExtentReports if enabled
        if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
            ExtentReportManager.logSkip("Scenario was skipped after " + duration + "ms");
        }
    }
    
    /**
     * Stop video recording
     * @param scenario Cucumber scenario
     */
    private void stopVideoRecording(Scenario scenario) {
        if (videoRecorder != null) {
            try {
                logger.info("Stopping video recording for scenario: {}", scenario.getName());
                String videoPath = videoRecorder.stopRecording();
                
                // Handle video based on scenario result
                if (scenario.getStatus() == Status.PASSED && 
                    ConfigurationManager.getFrameworkConfig().isDeleteVideoOnPass()) {
                    // Delete video for passed scenarios if configured
                    FileUtils.deleteFile(videoPath);
                    logger.info("Video deleted for passed scenario: {}", scenario.getName());
                } else {
                    // Attach video to reports for failed/skipped scenarios
                    attachVideoToScenario(scenario, videoPath);
                    logger.info("Video saved for scenario: {} at {}", scenario.getName(), videoPath);
                }
                
            } catch (Exception e) {
                logger.error("Failed to stop video recording for scenario: {}", scenario.getName(), e);
            } finally {
                videoRecorder = null;
            }
        }
    }
    
    /**
     * Take final screenshot
     * @param scenario Cucumber scenario
     */
    private void takeFinalScreenshot(Scenario scenario) {
        try {
            String screenshotName = "Scenario_End_" + sanitizeScenarioName(scenario.getName()) + "_" + scenario.getStatus();
            String screenshotPath = ScreenshotUtils.takeScreenshot(screenshotName);
            
            if (!screenshotPath.isEmpty()) {
                attachScreenshotToScenario(scenario, screenshotPath, "Scenario End");
            }
            
        } catch (Exception e) {
            logger.warn("Failed to take final screenshot for scenario: {}", scenario.getName(), e);
        }
    }
    
    /**
     * Attach screenshot to scenario
     * @param scenario Cucumber scenario
     * @param screenshotPath Screenshot file path
     * @param name Attachment name
     */
    private void attachScreenshotToScenario(Scenario scenario, String screenshotPath, String name) {
        try {
            // Attach to Cucumber scenario
            byte[] screenshot = FileUtils.readFileAsBytes(screenshotPath);
            scenario.attach(screenshot, "image/png", name);
            
            // Attach to Allure
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), "png");
            
        } catch (Exception e) {
            logger.warn("Failed to attach screenshot to scenario: {}", scenario.getName(), e);
        }
    }
    
    /**
     * Attach video to scenario
     * @param scenario Cucumber scenario
     * @param videoPath Video file path
     */
    private void attachVideoToScenario(Scenario scenario, String videoPath) {
        try {
            // Attach to Allure
            byte[] videoBytes = FileUtils.readFileAsBytes(videoPath);
            Allure.addAttachment("Scenario Video", "video/mp4", 
                new ByteArrayInputStream(videoBytes), "mp4");
            
        } catch (Exception e) {
            logger.warn("Failed to attach video to scenario: {}", scenario.getName(), e);
        }
    }
    
    /**
     * Take screenshot on setup failure
     */
    private void takeScreenshotOnSetupFailure() {
        try {
            if (DriverManager.isDriverInitialized()) {
                String screenshotPath = ScreenshotUtils.takeScreenshot("Setup_Failure_" + sanitizeScenarioName(currentScenarioName));
                logger.info("Setup failure screenshot saved: {}", screenshotPath);
            }
        } catch (Exception e) {
            logger.warn("Failed to take screenshot on setup failure", e);
        }
    }
    
    /**
     * Add scenario summary to reports
     * @param scenario Cucumber scenario
     * @param duration Scenario duration
     */
    private void addScenarioSummaryToReports(Scenario scenario, long duration) {
        try {
            // Add duration to Allure
            Allure.parameter("Duration", duration + "ms");
            
            // Log summary to ExtentReports if enabled
            if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
                ExtentReportManager.logInfo("Scenario completed in " + duration + "ms");
            }
            
        } catch (Exception e) {
            logger.warn("Failed to add scenario summary to reports", e);
        }
    }
    
    /**
     * Log scenario completion
     * @param scenario Cucumber scenario
     * @param duration Scenario duration
     */
    private void logScenarioCompletion(Scenario scenario, long duration) {
        logger.info("Scenario completed: {} - Status: {} - Duration: {}ms", 
                   scenario.getName(), scenario.getStatus(), duration);
        
        // Add completion step to Allure
        Allure.step("Scenario Completed: " + scenario.getName() + " [" + scenario.getStatus() + "]");
    }
    
    /**
     * Cleanup after scenario
     */
    private void cleanup() {
        try {
            // Quit driver
            if (DriverManager.isDriverInitialized()) {
                DriverManager.quitDriver();
                logger.info("Driver quit successfully");
            }
            
        } catch (Exception e) {
            logger.error("Error during cleanup", e);
        }
    }
    
    /**
     * Finalize reporting
     */
    private static void finalizeReporting() {
        logger.info("Finalizing reporting");
        
        try {
            // Flush ExtentReports if enabled
            if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
                ExtentReportManager.flushReports();
                logger.info("ExtentReports flushed successfully");
            }
            
        } catch (Exception e) {
            logger.error("Failed to finalize reporting", e);
        }
    }
    
    /**
     * Cleanup test environment
     */
    private static void cleanupTestEnvironment() {
        logger.info("Cleaning up test environment");
        
        try {
            // Cleanup old files if configured
            int cleanupDays = 7; // Could be configurable
            ScreenshotUtils.cleanupOldScreenshots(cleanupDays);
            VideoRecorder.cleanupOldVideos(cleanupDays);
            
            logger.info("Test environment cleanup completed");
            
        } catch (Exception e) {
            logger.error("Failed to cleanup test environment", e);
        }
    }
    
    /**
     * Generate final test reports
     */
    private static void generateFinalReports() {
        logger.info("Generating final test reports");
        
        try {
            // Generate Allure report command info
            logger.info("To generate Allure report, run: allure serve target/allure-results");
            
            // Log ExtentReports location if enabled
            if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
                logger.info("ExtentReports HTML report available in: target/extent-reports/");
            }
            
            logger.info("Final test reports generation completed");
            
        } catch (Exception e) {
            logger.error("Failed to generate final test reports", e);
        }
    }
    
    /**
     * Sanitize scenario name for file names
     * @param scenarioName Original scenario name
     * @return Sanitized scenario name
     */
    private String sanitizeScenarioName(String scenarioName) {
        if (scenarioName == null) {
            return "unknown_scenario";
        }
        
        return scenarioName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}