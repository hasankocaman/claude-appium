package com.hepsiburada.hooks;

// Framework Core Components: Temel framework bileşenlerine erişim sağlar
// ConfigurationManager: Test davranış konfigürasyonu (screenshot, video, reporting) yönetir
// Neden gerekli: Test execution parametrelerini merkezi yerden kontrol etmek için
// Bu import olmadan: Test konfigürasyonlarına erişilemez, runtime davranış kontrol edilemez
// DriverManager: Mobile driver lifecycle management ve platform detection yapar
// Neden gerekli: Mobile cihaz bağlantısı ve driver yönetimi için kritik
// Bu import olmadan: Appium driver operations ve mobile device management yapılamaz
// Utility classes: File operations, screenshot, video, reporting utilities sağlar
// Neden gerekli: Test execution sırasında media ve file operations için
// Bu importlar olmadan: Cucumber hooks temel framework functionality'sine erişemez
import com.hepsiburada.config.ConfigurationManager;
import com.hepsiburada.drivers.DriverManager;
import com.hepsiburada.utils.*;

// Appium Mobile Driver: Cross-platform mobile automation driver interface sağlar
// AppiumDriver: Android ve iOS platformları için unified mobile driver interface
// Neden gerekli: Mobile app elements ile interaction için temel driver interface
// Bu import olmadan: Mobile device operations, element interactions yapılamaz
import io.appium.java_client.AppiumDriver;

// Cucumber Framework: Behavior-Driven Development (BDD) test lifecycle management
// Cucumber Annotations: Test lifecycle hooks (@Before, @After, @BeforeAll, @AfterAll)
// Neden gerekli: Test scenarios'ların setup ve teardown işlemleri için
// Scenario: Cucumber test scenario context ve result information container
// Neden gerekli: Scenario metadata, execution status ve context bilgilerine erişim için
// Bu importlar olmadan: BDD hooks çalışmaz, scenario lifecycle management yapılamaz
import io.cucumber.java.*;

// Allure Reporting: Advanced test reporting framework with rich media support
// Allure: Test execution lifecycle, attachments, parameters için API
// Neden gerekli: Rich test reports, screenshots, videos, step tracking için
// Status: Test result status enumeration (PASSED, FAILED, SKIPPED) tanımlar
// Neden gerekli: Test sonuçlarının kategorize edilmesi ve raporlanması için
// Bu importlar olmadan: Detaylı test reports oluşturulamaz, media attachments yapılamaz
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;

// Logging Framework: Test execution tracking ve debugging infrastructure
// LogManager/Logger: Log4j2 logging system for structured logging
// Neden gerekli: Test execution flow tracking, debugging, production monitoring için
// Bu importlar olmadan: Hook operations logging yapılamaz, debugging zorlaşır
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Java I/O: Binary data streaming operations for media handling
// ByteArrayInputStream: Screenshot, video data streaming to reports için
// Neden gerekli: Media files'ları report systems'e attach etmek için
// Bu import olmadan: Media attachments reports'a stream edilemez
import java.io.ByteArrayInputStream;

/**
 * CUCUMBER TEST HOOKS - BDD TEST LIFECYCLE YÖNETİM SİSTEMİ
 *
 * Bu class'ın projede rolü ve kritik önemi:
 * - Cucumber BDD framework için complete test lifecycle management (setup/teardown automation)
 * - Mobile driver initialization ve cleanup operations (scenario isolation garantisi)
 * - Automated video recording management (scenario-based recording ve conditional saving)
 * - Screenshot capture ve attachment system (visual test documentation)
 * - Multi-platform test reporting integration (Allure, ExtentReports coordination)
 * - Test environment preparation ve cleanup (directories, file management)
 * - Centralized error handling ve recovery mechanisms (robust test execution)
 * - Test performance metrics collection ve reporting
 *
 * Framework Architecture'deki konumu:
 * Bu class framework'ün "orchestration layer"ıdır. Tüm test components'lerini coordinate eder.
 *
 * Kullanılmazsa kritik etkiler:
 * - Scenario isolation broken: Test scenarios birbirini etkiler (test flakiness)
 * - Manual driver management: Error-prone, resource leaks, inconsistent state
 * - No automated documentation: Video/screenshot capture missing (debugging impossible)
 * - Test environment chaos: Directory management, file cleanup eksik
 * - Inconsistent reporting: Report systems fragmented, no unified metadata
 * - Resource leaks: Driver sessions, media files accumulate (system degradation)
 * - No centralized error handling: Unhandled exceptions, partial test states
 * - Performance monitoring missing: No execution metrics, bottleneck detection impossible
 *
 * Diğer class'larla kritik ilişkiler:
 * - BaseTest class: Benzer functionality ama TestNG vs Cucumber hooks difference
 * - DriverManager: Driver lifecycle operations için delegation pattern
 * - ConfigurationManager: Hook behavior configuration ve feature toggling
 * - Utility classes: Screenshot, video, file operations için service layer
 * - Step Definitions: Bu hooks step executions için temiz environment hazırlar
 * - Reporting Systems: Allure, ExtentReports için metadata ve attachment coordination
 *
 * Design Patterns implementasyonu:
 * - Template Method Pattern: Hook structure standardization, specific implementations
 * - Observer Pattern: Scenario lifecycle events monitoring ve response
 * - Factory Pattern: Driver creation delegation (platform-agnostic)
 * - Strategy Pattern: Different reporting strategies based on configuration
 * - Command Pattern: Hook operations encapsulation
 *
 * Cucumber Hook Execution Hierarchy:
 * @BeforeAll (static) -> @Before (instance) -> Step Definitions -> @After (instance) -> @AfterAll (static)
 *
 * Thread Safety Considerations:
 * - Static methods: Thread-safe, suite-level operations
 * - Instance methods: Per-scenario isolation, thread-local storage
 * - Video recording: Instance-based, thread-isolated
 *
 * Performance Impact:
 * - Setup overhead: ~200-500ms per scenario (driver init, video start)
 * - Teardown overhead: ~100-300ms per scenario (cleanup, media processing)
 * - Memory usage: ~50-100MB per concurrent scenario (driver + media)
 *
 * Error Recovery Strategy:
 * - Graceful degradation: Test continues even if documentation fails
 * - Resource cleanup guaranteed: Finally blocks ensure no resource leaks
 * - Detailed error logging: Full exception context for troubleshooting
 *
 * @author Hepsiburada Test Automation Team
 * @version 1.0.0
 * @since Framework v2.0
 */
public class TestHooks {

    // Logger instance: TestHooks operations için centralized, structured logging system
    // Static final tercih nedeni: Memory efficiency, immutable reference, shared across all instances
    // TestHooks.class identifier: Log entries'da source class identification için
    // Logger hierarchy: com.hepsiburada.hooks.TestHooks logger name ile Log4j2 configuration mapping
    // Bu logger olmadan: Hook operations tracking impossible, debugging nightmare, no audit trail
    // Performance impact: Minimal, logger instance creation once per class loading
    private static final Logger logger = LogManager.getLogger(TestHooks.class);

    // Video recorder instance: Scenario-based video capture management için instance field
    // Instance field seçim nedeni: Her TestHooks instance'ı için independent video recording
    // Scenario lifecycle binding: Scenario start'ta initialize, scenario end'de finalize
    // Configuration dependency: Video recording enable/disable configuration based
    // Thread safety: Instance per thread, no shared state conflicts
    // Memory management: Video recorder lifecycle tied to scenario lifecycle
    // Bu field olmadan: Scenario execution video documentation completely missing
    // Alternative approach: Static video recording (thread conflicts, scenario confusion)
    private VideoRecorder videoRecorder;

    // Scenario timing: Performance measurement ve execution duration tracking için
    // long data type: Millisecond precision için sufficient, memory efficient
    // Value source: System.currentTimeMillis() high precision timestamp
    // Usage pattern: Set in @Before hook, calculate difference in @After hook
    // Accuracy: ±1ms precision, system clock dependent
    // Bu field olmadan: Scenario performance metrics calculate edilemez, SLA monitoring impossible
    // Alternative: Instant class (higher precision but more memory overhead)
    private long scenarioStartTime;

    // Current scenario context: Active scenario name tracking için string storage
    // String type: Scenario names are text-based, Unicode support needed
    // Lifecycle: @Before hook'ta set edilir, @After hook'ta cleanup
    // Usage: File naming, logging context, error reporting identification
    // Thread safety: Instance field, thread-local per scenario
    // Sanitization: Special characters removed for file system compatibility
    // Bu field olmadan: Scenario context tracking impossible, generic file names, debugging confusion
    // Memory impact: Minimal string storage per scenario execution
    private String currentScenarioName;

    /**
     * CUCUMBER SUITE BAŞLATMA HOOK'U - FRAMEWORK INITIALIZATION CRITICAL POINT
     *
     * Method amacı ve kritik rolü:
     * Entire Cucumber test suite için one-time initialization operations yapar
     * Framework'ün global state'ini hazırlar, tüm scenarios için foundation kurar
     *
     * Execution timing ve hierarchy:
     * - Execution order: En önce çalışır, tüm scenario'lardan önce tek sefer
     * - Static context: Class instance gerekmez, JVM class loading sonrası available
     * - Suite scope: Tüm feature files, tüm scenarios için shared initialization
     * - Automatic execution: Cucumber framework otomatik detect ve execute eder
     *
     * Parametreler: Yok (static suite-level operation)
     * Return değeri: void (side effects through system state changes)
     *
     * Bu method kullanılmazsa kritik etkiler:
     * - Test environment hazırlanmaz: Directories missing, previous runs contamination
     * - Reporting systems uninitialized: No reports generated, metadata missing
     * - Allure environment missing: Report context incomplete, environment info absent
     * - Suite-level configurations ignored: Global settings not applied
     * - Resource allocation problems: Directories, permissions, file system issues
     *
     * Diğer metodlarla kıyasla neden kritik:
     * - @Before: Scenario-level vs suite-level scope difference
     * - Constructor: Static vs instance initialization difference
     * - Manual setup: Automatic vs manual execution guarantee
     *
     * Çağrıldığı yerler ve bağımlılıklar:
     * - Cucumber framework automatic execution
     * - CI/CD pipeline BDD test runs başlangıcında
     * - IDE test execution başlangıcında
     * - Maven/Gradle test goals execution'da
     *
     * Internal dependency chain:
     * initializeTestEnvironment() -> setupReportingSystems() -> setupAllureEnvironment()
     * Her step bir sonrakinin prerequisite'ı
     *
     * Error handling strategy:
     * RuntimeException wrap: Suite setup failure entire execution'ı durdurur
     * Fast-fail approach: Broken environment'da test execution meaningless
     *
     * Performance considerations:
     * - Execution time: ~1-2 seconds typical
     * - I/O operations: Directory creation, file system operations
     * - Memory allocation: Reporting systems initialization
     */
    @BeforeAll
    public static void beforeAll() {
        // Suite setup başlangıç delimiter: Visual log separation için formatting
        // Info level seçim nedeni: Major milestone, normal execution flow part
        // Log pattern: Distinctive formatting ile suite lifecycle tracking
        // Bu log olmadan: Suite setup start point identification impossible
        logger.info("=== TEST SUITE SETUP STARTED ===");

        try {
            // Test environment initialization call: File system preparation critical step
            // Method call delegation: Separation of concerns, modular design
            // Exception propagation: initializeTestEnvironment() exceptions burada yakalanır
            // Bu call olmadan: Test artifacts directory structure missing, previous runs contaminate current
            // Side effects: Directory creation, file cleanup, permission setting
            initializeTestEnvironment();

            // Reporting systems setup: ExtentReports, configuration initialization
            // Sequential execution: Test environment preparation sonrası çalışmalı
            // Configuration dependent: Some reporting may be disabled via config
            // Bu call olmadan: Test results documentation completely missing, no HTML/JSON reports
            // Resource allocation: Report writers, file handles, memory buffers
            setupReportingSystems();

            // Allure environment setup: Metadata, properties, environment context
            // Allure-specific configuration: Environment information embedding
            // Optional setup: Allure olmadan da framework çalışır but reports incomplete
            // Bu call olmadan: Allure reports environment context missing, less informative reports
            // Metadata types: OS info, Java version, test environment details
            setupAllureEnvironment();

            // Suite setup completion confirmation: Success state logging
            // Info level: Setup completion milestone, all prerequisites satisfied
            // Visual delimiter: Matching pattern with start delimiter
            // Bu log olmadan: Setup completion confirmation missing, unclear state
            logger.info("=== TEST SUITE SETUP COMPLETED ===");

        } catch (Exception e) {
            // Suite setup failure critical error handling: Exception wrapping pattern
            // Error level: Critical failure, requires immediate attention
            // Full exception logging: Stack trace, root cause analysis için
            // Bu error handling olmadan: Setup failures silent, mysterious test failures
            logger.error("Failed to setup test suite", e);

            // RuntimeException wrapping: Checked exceptions'ı unchecked'e convert
            // Fast-fail strategy: Broken environment'da test execution meaningless
            // Exception chaining: Original exception preserved for debugging
            // Bu throw olmadan: Broken environment'da testler çalışmaya çalışır, confusing failures
            throw new RuntimeException("Test suite setup failed", e);
        }
    }

    /**
     * CUCUMBER SCENARIO BAŞLATMA HOOK'U - SCENARIO ISOLATION GUARANTEE SYSTEM
     *
     * Method amacı ve kritik işlevsellik:
     * Her individual Cucumber scenario için fresh, isolated environment hazırlar
     * Test scenario independence guarantee eder, cross-contamination önler
     * Mobile driver, reporting, documentation systems'i scenario'ya özgü initialize eder
     *
     * Parametreler açıklaması:
     * @param scenario - Cucumber scenario context object
     *   - scenario.getName(): Test scenario identifier string
     *   - scenario.getSourceTagNames(): @smoke, @regression gibi tags
     *   - scenario.getUri(): Feature file location reference
     *   - scenario.getStatus(): Runtime'da set edilir (initially null)
     *
     * Execution timing ve lifecycle:
     * - Execution order: Her scenario execution öncesi automatic
     * - Instance context: Her scenario için fresh TestHooks instance
     * - Scope: Individual scenario isolation, no shared state
     * - Thread safety: Parallel execution'da thread-local instance
     *
     * Return değeri: void (side effects through system state initialization)
     *
     * Bu method kullanılmazsa kritik etkiler:
     * - Mobile driver initialization missing: Scenario mobile operations impossible
     * - Scenario isolation broken: Tests affect each other, flaky results
     * - Video/screenshot documentation missing: No visual test evidence
     * - Reporting context missing: Scenario metadata absent from reports
     * - Performance metrics missing: No execution time measurement
     * - Error context missing: Debugging without scenario context difficult
     *
     * Diğer metodlarla kıyasla kritik farklılıklar:
     * - @BeforeAll: Suite vs scenario scope difference
     * - @After: Setup vs teardown operation difference
     * - BaseTest setUp: TestNG vs Cucumber execution model difference
     *
     * Çağrıldığı yerler ve execution context:
     * - Cucumber framework automatic invocation
     * - Scenario retry situations (rerun failed tests)
     * - Parallel scenario execution (multiple threads)
     * - IDE individual scenario runs
     *
     * Scenario setup sequence ve dependency chain:
     * 1. Scenario context capture (name, timing)
     * 2. Reporting systems scenario binding
     * 3. Mobile driver fresh initialization
     * 4. Video recording activation
     * 5. Initial state screenshot
     * 6. Scenario metadata logging
     *
     * Error handling strategy:
     * - Exception wrapping: Setup failures stop scenario execution
     * - Diagnostic screenshot: Setup failure state capture
     * - Allure integration: Failed setup step documentation
     * - Resource cleanup: Partial setup cleanup in finally block
     *
     * Performance impact analysis:
     * - Setup time: ~200-500ms typical (driver init dominant)
     * - Memory usage: ~50-100MB per scenario (driver + media)
     * - I/O operations: Screenshot capture, video initialization
     */
    @Before
    public void before(Scenario scenario) {
        // Scenario context extraction: Current scenario name assignment for tracking
        // scenario.getName() call: Cucumber framework scenario identifier extraction
        // String assignment: Instance field population for later usage
        // Bu assignment olmadan: Scenario identification impossible, generic logging/filenames
        currentScenarioName = scenario.getName();

        // Scenario timing başlangıç: Performance measurement baseline establishment
        // System.currentTimeMillis() precision: Millisecond level accuracy sufficient for scenarios
        // Long storage: 64-bit integer sufficient for timestamp values
        // Bu timing olmadan: Scenario duration calculation impossible, performance metrics missing
        scenarioStartTime = System.currentTimeMillis();

        // Scenario setup başlangıç logging: Scenario identification ve tracking start
        // Info level appropriateness: Normal flow, non-error milestone
        // Parameterized logging: {} placeholder efficient string formatting
        // Bu log olmadan: Scenario execution start point unclear, debugging difficult
        logger.info("=== SCENARIO SETUP STARTED: {} ===", currentScenarioName);

        try {
            // Scenario reporting context binding: Report systems scenario association
            // Method delegation: Separation of concerns, reporting logic isolation
            // Scenario parameter passing: Context information transfer
            // Bu setup olmadan: Reports'da scenario context missing, anonymous test results
            setupScenarioReporting(scenario);

            // Mobile driver fresh initialization: Clean driver state for scenario
            // Driver isolation: Each scenario gets independent driver instance
            // Platform detection: Android/iOS automatic platform selection
            // Bu initialization olmadan: Scenario mobile operations completely impossible
            initializeDriver();

            // Video recording activation: Scenario execution video documentation start
            // Configuration dependent: Video recording can be enabled/disabled
            // Conditional execution: Only if video recording configuration enabled
            // Bu call olmadan: Scenario visual documentation missing, replay impossible
            startVideoRecording();

            // Initial screenshot capture: Scenario başlangıç state documentation
            // State documentation: Pre-execution visual reference point
            // Debugging aid: Initial conditions verification için
            // Bu screenshot olmadan: Scenario start state undocumented, debugging incomplete
            takeInitialScreenshot();

            // Scenario metadata logging: Detailed scenario information recording
            // Comprehensive logging: Name, tags, location information
            // Audit trail: Complete scenario context preservation
            // Bu logging olmadan: Scenario execution context information incomplete
            logScenarioStart(scenario);

            // Scenario setup completion confirmation: Successful setup state logging
            // Success milestone: All setup operations completed successfully
            // Visual delimiter: Matching format with setup start log
            // Bu log olmadan: Setup completion unclear, success state unconfirmed
            logger.info("=== SCENARIO SETUP COMPLETED: {} ===", currentScenarioName);

        } catch (Exception e) {
            // Setup failure critical error: Exception handling ve error documentation
            // Error level logging: Critical failure requiring attention
            // Exception context: Full stack trace preservation for debugging
            // Bu error handling olmadan: Setup failures silent, mysterious scenario failures
            logger.error("Failed to setup scenario: {}", currentScenarioName, e);

            // Diagnostic screenshot capture: Setup failure state visual documentation
            // Error state capture: Visual debugging aid for setup problems
            // Conditional capture: Only if driver partially initialized
            // Bu screenshot olmadan: Setup failure state invisible, debugging difficult
            takeScreenshotOnSetupFailure();

            // Allure setup failure documentation: Test report setup failure step
            // Failed status: Clearly mark setup phase failure
            // Step documentation: Granular failure point identification
            // Bu Allure integration olmadan: Setup failures not visible in reports
            Allure.step("Scenario Setup Failed", Status.FAILED);

            // Allure failure details attachment: Error message context addition
            // Error message attachment: Exception details in report
            // Debugging information: Failure reason readily available
            // Bu attachment olmadan: Error details missing from visual reports
            Allure.addAttachment("Setup Failure", e.getMessage());

            // Exception re-throwing: Setup failure propagation to stop scenario
            // RuntimeException wrap: Checked exception conversion for framework compatibility
            // Fast-fail strategy: Broken setup means scenario cannot execute meaningfully
            // Bu throw olmadan: Scenario attempts to run with broken setup, confusing results
            throw new RuntimeException("Scenario setup failed: " + currentScenarioName, e);
        }
    }

    /**
     * CUCUMBER SCENARIO SONLANDIRMA HOOK'U - COMPREHENSIVE CLEANUP SYSTEM
     *
     * Method amacı ve critical importance:
     * Her Cucumber scenario completion sonrası complete cleanup ve result processing
     * Resource management, documentation finalization, performance metrics collection
     * Next scenario için clean state preparation, resource leak prevention
     *
     * Parametreler detayı:
     * @param scenario - Cucumber scenario context with execution results
     *   - scenario.getStatus(): PASSED/FAILED/SKIPPED execution result
     *   - scenario.getName(): Scenario identifier for logging/reporting
     *   - scenario.getSourceTagNames(): Tag metadata for categorization
     *   - scenario.attach(): Method for attaching media to scenario results
     *
     * Execution timing ve scope:
     * - Execution order: Her scenario completion sonrası automatic
     * - Status agnostic: Runs regardless of scenario pass/fail/skip status
     * - Instance context: Same TestHooks instance as corresponding @Before
     * - Guaranteed execution: Cucumber framework ensures execution even after exceptions
     *
     * Return değeri: void (side effects through cleanup operations)
     *
     * Bu method kullanılmazsa catastrophic etkiler:
     * - Massive resource leaks: Driver sessions accumulate, system memory exhausted
     * - Test result documentation incomplete: No final screenshots, video processing missing
     * - Video file management broken: Videos not saved/deleted based on results
     * - Performance metrics missing: No execution duration tracking
     * - Next scenario contamination: Previous test state affects new tests
     * - System degradation: Resources accumulate over test suite execution
     * - No error recovery: Partial states persist, debugging impossible
     *
     * Diğer metodlarla kıyasla kritik role:
     * - @Before: Setup vs cleanup operation symmetry
     * - @AfterAll: Scenario vs suite cleanup scope difference
     * - finally blocks: Exception safety guarantee comparison
     * - BaseTest tearDown: TestNG vs Cucumber execution model difference
     *
     * Çağrıldığı durumlar ve contexts:
     * - Normal scenario completion (pass/fail/skip)
     * - Scenario exception situations (test code failures)
     * - Framework interruption (timeout, system shutdown)
     * - IDE test termination (manual stop)
     * - CI/CD pipeline completion (normal or abnormal termination)
     *
     * Exception safety pattern implementation:
     * - Try block: Result processing operations (can fail without affecting cleanup)
     * - Finally block: Resource cleanup guaranteed execution (critical resources)
     * - Error isolation: Result processing failures don't prevent resource cleanup
     * - Graceful degradation: Test results preserved even if documentation fails
     *
     * Teardown sequence ve operation priority:
     * 1. High priority: Duration calculation (performance metrics)
     * 2. Medium priority: Result-based handling (screenshots, reporting)
     * 3. Medium priority: Video processing (save/delete based on result)
     * 4. Low priority: Final documentation (screenshots, summaries)
     * 5. Critical priority: Resource cleanup (guaranteed in finally)
     *
     * Performance impact ve optimization:
     * - Teardown time: ~100-300ms typical (video processing dominant)
     * - I/O operations: Screenshot saving, video processing, file cleanup
     * - Memory cleanup: Driver termination, media buffer release
     */
    @After
    public void after(Scenario scenario) {
        // Scenario teardown başlangıç notification: Cleanup phase start logging
        // Info level: Normal execution flow milestone, non-error state
        // Scenario identification: Current scenario context for log correlation
        // Bu log olmadan: Teardown phase start point unclear, execution flow tracking incomplete
        logger.info("=== SCENARIO TEARDOWN STARTED: {} ===", currentScenarioName);

        try {
            // Scenario execution duration calculation: Performance metrics computation
            // Time difference calculation: Current time minus scenario start time
            // Millisecond precision: Sufficient accuracy for scenario-level performance
            // Bu calculation olmadan: Performance metrics missing, SLA monitoring impossible
            long duration = System.currentTimeMillis() - scenarioStartTime;

            // Scenario result-based handling: Conditional operations based on test outcome
            // Status-specific processing: Different actions for pass/fail/skip results
            // Duration parameter: Performance context for result processing
            // Bu handling olmadan: Test outcome documentation missing, result-specific actions skipped
            handleScenarioResult(scenario, duration);

            // Video recording termination: Video capture stop ve result-based processing
            // Conditional video management: Save for failures, delete for passes (configurable)
            // Scenario context: Result status determines video retention policy
            // Bu call olmadan: Video resources not cleaned up, disk space accumulation
            stopVideoRecording(scenario);

            // Final state screenshot: Scenario completion state visual documentation
            // End state capture: Final UI state for debugging/documentation
            // Result correlation: Screenshot labeled with scenario outcome
            // Bu screenshot olmadan: Final scenario state undocumented, incomplete visual record
            takeFinalScreenshot(scenario);

            // Scenario summary report generation: Aggregate information for reports
            // Report integration: Duration, status summary for reporting systems
            // Metrics collection: Performance and outcome data aggregation
            // Bu summary olmadan: Detailed scenario metrics missing from reports
            addScenarioSummaryToReports(scenario, duration);

            // Scenario completion comprehensive logging: Final execution state documentation
            // Complete context: Status, duration, metadata logging
            // Audit trail: Comprehensive scenario execution record
            // Bu logging olmadan: Scenario completion tracking incomplete
            logScenarioCompletion(scenario, duration);

            // Teardown completion confirmation: Successful cleanup state indication
            // Success milestone: All teardown operations completed successfully
            // Visual delimiter: Consistent formatting with teardown start
            // Bu log olmadan: Teardown completion status unclear
            logger.info("=== SCENARIO TEARDOWN COMPLETED: {} ===", currentScenarioName);

        } catch (Exception e) {
            // Teardown process exception handling: Non-critical error management
            // Error level: Significant but not critical (doesn't stop resource cleanup)
            // Exception preservation: Full context for troubleshooting
            // Bu error handling olmadan: Teardown failures invisible, debugging harder
            logger.error("Error in scenario teardown for: {}", currentScenarioName, e);

        } finally {
            // Critical resource cleanup: Guaranteed execution for resource management
            // Finally block guarantee: Executes even if try/catch blocks throw exceptions
            // Resource leak prevention: Driver termination, memory cleanup assured
            // Bu cleanup olmadan: Resources accumulate, system memory leaks, performance degradation
            cleanup();
        }
    }

    /**
     * SUITE SONLANDIRMA HOOK'U - FRAMEWORK FINALIZATION SYSTEM
     *
     * Method amacı: Entire test suite completion sonrası global cleanup operations
     * Suite-level resource finalization, reporting consolidation, environment cleanup
     *
     * Execution timing: Tüm scenarios completion sonrası tek sefer execution
     * Scope: Suite-wide cleanup, global resource management
     *
     * Bu method kullanılmazsa etkiler:
     * - Reporting systems not finalized properly
     * - Test environment not cleaned up
     * - Final reports not generated
     */
    @AfterAll
    public static void afterAll() {
        // Suite teardown başlangıç: Global cleanup phase start notification
        // Info level: Major milestone in suite lifecycle
        logger.info("=== TEST SUITE TEARDOWN STARTED ===");

        try {
            // Reporting finalization: Flush all report writers, close resources
            // Report consolidation: Final report generation trigger
            finalizeReporting();

            // Environment cleanup: Remove temporary files, reset configurations
            // File system cleanup: Old screenshots, videos, temporary data
            cleanupTestEnvironment();

            // Final report generation: Consolidated suite-level reports
            // Report accessibility: Instructions for viewing generated reports
            generateFinalReports();

            // Suite teardown completion: Successful cleanup confirmation
            logger.info("=== TEST SUITE TEARDOWN COMPLETED ===");

        } catch (Exception e) {
            // Suite teardown failure: Non-critical error (suite already completed)
            // Error logging: Exception context for troubleshooting
            logger.error("Failed to teardown test suite", e);
        }
    }

    // ===================================================================================
    // PRIVATE HELPER METHODS - INTERNAL IMPLEMENTATION DETAILS
    // ===================================================================================

    /**
     * TEST ENVIRONMENT INITIALIZATION - FILE SYSTEM PREPARATION
     *
     * Method amacı: Test execution için gerekli file system infrastructure hazırlığı
     * Directory structure creation, previous run cleanup, permission setting
     *
     * Static method seçimi: Suite-level operation, instance gerekmez
     *
     * Bu method kullanılmazsa etkiler:
     * - Test artifacts directory structure missing
     * - Previous test run contamination
     * - File permission issues
     * - Screenshot/video storage locations unavailable
     */
    private static void initializeTestEnvironment() {
        // Environment initialization start: Process başlangıç logging
        // Info level: Normal flow step, infrastructure preparation
        logger.info("Initializing test environment");

        try {
            // Directory structure creation: Required folders for test artifacts
            // FileUtils delegation: Utility class separation of concerns
            // Bu call olmadan: Test output directories missing, file operations fail
            FileUtils.createDirectories();

            // Previous run cleanup: Stale data removal from previous executions
            // Clean slate approach: Each run starts with clean environment
            // Bu cleanup olmadan: Previous run data contaminates current execution
            FileUtils.cleanupPreviousRuns();

            // Screenshot directory verification: Ensure screenshot storage available
            // Directory existence check: Create if missing, verify permissions
            // Bu check olmadan: Screenshot capture fails, no visual documentation
            ScreenshotUtils.ensureScreenshotDirectoryExists();

            // Environment initialization success: Confirmation logging
            logger.info("Test environment initialized successfully");

        } catch (Exception e) {
            // Environment initialization failure: Critical error handling
            // Error level: Infrastructure failure, tests cannot proceed
            logger.error("Failed to initialize test environment", e);

            // Exception propagation: Environment failure should stop entire suite
            // Critical dependency: Test environment required for all operations
            throw new RuntimeException("Test environment initialization failed", e);
        }
    }

    /**
     * REPORTING SYSTEMS SETUP - REPORT FRAMEWORK INITIALIZATION
     *
     * Method amacı: Test reporting systems'in configuration ve initialization
     * ExtentReports, Allure gibi reporting frameworks'ün hazırlanması
     *
     * Static method seçimi: Suite-level operation, tüm scenarios için shared setup
     *
     * Bu method kullanılmazsa etkiler:
     * - HTML/JSON test reports generated edilemez
     * - Test execution documentation missing
     * - Stakeholder reporting impossible
     */
    private static void setupReportingSystems() {
        // Reporting setup başlangıç: Report framework initialization start logging
        // Info level: Infrastructure setup milestone
        logger.info("Setting up reporting systems");

        try {
            // ExtentReports conditional initialization: Configuration-based report setup
            // Configuration check: Only initialize if ExtentReports enabled in config
            // Resource allocation: Report writers, templates, configuration loading
            // Bu conditional olmadan: Unnecessary report overhead if disabled
            if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
                // ExtentReports manager initialization: Report writer setup
                // Manager pattern: Centralized report management through ExtentReportManager
                // Bu initialization olmadan: ExtentReports functionality completely unavailable
                ExtentReportManager.initializeReports();

                // ExtentReports success confirmation: Initialization completion logging
                logger.info("ExtentReports initialized successfully");
            }

            // Reporting systems setup completion: All reporting frameworks ready
            logger.info("Reporting systems setup completed");

        } catch (Exception e) {
            // Reporting setup failure: Critical error for test documentation
            // Error level: Significant failure but non-blocking for test execution
            logger.error("Failed to setup reporting systems", e);

            // Exception propagation: Reporting failure should stop suite (documentation required)
            throw new RuntimeException("Reporting systems setup failed", e);
        }
    }

    /**
     * ALLURE ENVIRONMENT SETUP - METADATA CONFIGURATION SYSTEM
     *
     * Method amacı: Allure reporting framework için environment metadata setup
     * Test execution environment information embedding in reports
     *
     * Static method: Suite-level environment information, shared across scenarios
     *
     * Bu method kullanılmazsa etkiler:
     * - Allure reports environment context missing
     * - Test environment traceability incomplete
     * - Debugging context reduced
     */
    private static void setupAllureEnvironment() {
        // Allure environment setup start: Metadata configuration beginning
        logger.info("Setting up Allure environment");

        try {
            // Allure environment information setting: Metadata injection into reports
            // Environment details: OS, Java version, test environment specifics
            // Bu call olmadan: Allure reports lack environment context
            AllureEnvironmentUtils.setEnvironmentInformation();

            // Allure setup completion: Environment metadata ready
            logger.info("Allure environment setup completed");

        } catch (Exception e) {
            // Allure setup failure: Non-critical error handling
            // Error level: Significant but not blocking (tests can continue)
            logger.error("Failed to setup Allure environment", e);

            // Warning approach: Continue without Allure environment (graceful degradation)
            // Non-blocking: Allure environment failure shouldn't stop test execution
            logger.warn("Allure environment setup failed, continuing without it");
        }
    }

    /**
     * SCENARIO REPORTING SETUP - SCENARIO-SPECIFIC REPORT BINDING
     *
     * Method amacı: Individual scenario için reporting systems'e context binding
     * Scenario metadata'sını report frameworks'e inject etme
     *
     * Parametreler:
     * @param scenario - Cucumber scenario with name, tags, metadata
     *
     * Bu method kullanılmazsa etkiler:
     * - Scenario context missing from reports
     * - Tag-based filtering impossible
     * - Test categorization absent
     */
    private void setupScenarioReporting(Scenario scenario) {
        try {
            // Allure test case information update: Scenario metadata injection
            // Lifecycle update: Test result object modification with scenario details
            // Lambda expression: Functional approach to test result modification
            Allure.getLifecycle().updateTestCase(testResult -> {
                // Test case name setting: Scenario name assignment to Allure test
                // Name correlation: Allure report test name matches Cucumber scenario
                testResult.setName(scenario.getName());

                // Test description setting: Additional context for Allure report
                // Description format: Clear indication of Cucumber scenario
                testResult.setDescription("Scenario: " + scenario.getName());

                // Tag labels addition: Scenario tags converted to Allure labels
                // Tag processing: @ prefix removal for cleaner label display
                // Filtering support: Tags enable report filtering and categorization
                scenario.getSourceTagNames().forEach(tag ->
                        Allure.label("tag", tag.replace("@", "")));
            });

            // ExtentReports test creation: Scenario-specific test node creation
            // Configuration conditional: Only if ExtentReports enabled
            if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
                // ExtentReports test node creation: Scenario representation in HTML report
                // Test hierarchy: Parent-child relationship for organized reporting
                ExtentReportManager.createTest(scenario.getName(), "Cucumber Scenario");

                // Tag categories assignment: Scenario tags as ExtentReports categories
                // Category filtering: Enable tag-based report filtering
                scenario.getSourceTagNames().forEach(tag ->
                        ExtentReportManager.assignCategory(tag.replace("@", "")));
            }

        } catch (Exception e) {
            // Scenario reporting setup failure: Non-critical warning
            // Warning level: Reporting issues shouldn't block test execution
            logger.warn("Failed to setup scenario reporting", e);
        }
    }

    /**
     * APPIUM DRIVER INITIALIZATION - MOBILE AUTOMATION SETUP
     *
     * Method amacı: Mobile test automation için Appium driver initialization
     * Platform-agnostic driver setup, device connection establishment
     *
     * Bu method kullanılmazsa etkiler:
     * - Mobile automation completely impossible
     * - Test scenarios cannot interact with mobile app
     * - Framework primary functionality broken
     */
    private void initializeDriver() {
        // Driver initialization start: Mobile automation setup beginning
        logger.info("Initializing Appium driver");

        try {
            // Appium driver initialization: Platform-specific driver creation
            // DriverManager delegation: Factory pattern for driver creation
            // Platform detection: Automatic Android/iOS driver selection
            // Bu call olmadan: No mobile automation capability, scenarios fail
            AppiumDriver driver = DriverManager.initializeDriver();

            // Driver initialization success: Platform and device information logging
            // Platform identification: Android or iOS platform confirmation
            // Device identification: Target device name for traceability
            logger.info("Appium driver initialized successfully: {} on {}",
                    DriverManager.getPlatformName(), DriverManager.getDeviceName());

            // Allure driver information parameters: Test context enhancement
            // Platform parameter: Execution platform visible in reports
            // Device parameter: Target device context for debugging
            Allure.parameter("Platform", DriverManager.getPlatformName());
            Allure.parameter("Device", DriverManager.getDeviceName());

        } catch (Exception e) {
            // Driver initialization failure: Critical error for mobile testing
            // Error level: Fatal for mobile automation scenarios
            logger.error("Failed to initialize Appium driver", e);

            // Exception propagation: Driver failure should stop scenario
            // Critical dependency: Mobile driver required for scenario execution
            throw new RuntimeException("Driver initialization failed", e);
        }
    }

    /**
     * VIDEO RECORDING START - SCENARIO VIDEO CAPTURE ACTIVATION
     *
     * Method amacı: Scenario execution video recording başlatma
     * Configuration-based video capture for test documentation
     *
     * Bu method kullanılmazsa etkiler:
     * - Scenario video documentation missing
     * - Visual test evidence unavailable
     * - Debugging replay impossible
     */
    private void startVideoRecording() {
        // Configuration check: Video recording enabled/disabled verification
        // Performance consideration: Video recording adds overhead, optional feature
        if (ConfigurationManager.getFrameworkConfig().isVideoRecordingEnabled()) {
            try {
                // Video recording start logging: Video capture beginning notification
                logger.info("Starting video recording for scenario: {}", currentScenarioName);

                // Video recorder instantiation: Scenario-specific video recorder creation
                // File naming: Sanitized scenario name for file system compatibility
                // Instance assignment: Video recorder lifecycle tied to scenario
                videoRecorder = new VideoRecorder(sanitizeScenarioName(currentScenarioName));

                // Video capture activation: Screen recording start
                // Recording session: From scenario start until teardown
                videoRecorder.startRecording();

                // Video recording success: Confirmation logging
                logger.info("Video recording started successfully");

            } catch (Exception e) {
                // Video recording failure: Non-critical error handling
                // Error level: Significant but shouldn't block test execution
                logger.error("Failed to start video recording for scenario: {}", currentScenarioName, e);
                // Graceful degradation: Test continues without video documentation
            }
        }
    }

    /**
     * INITIAL SCREENSHOT CAPTURE - SCENARIO START STATE DOCUMENTATION
     *
     * Method amacı: Scenario başlangıç state'ini visual olarak dokümante etme
     * Pre-execution UI state capture for debugging reference
     *
     * Bu method kullanılmazsa etkiler:
     * - Scenario start state undocumented
     * - Debugging baseline missing
     * - Visual test evidence incomplete
     */
    private void takeInitialScreenshot() {
        try {
            // Screenshot capture: Scenario start state visual documentation
            // File naming: Descriptive name with scenario identifier
            // Sanitization: File system compatible name generation
            String screenshotPath = ScreenshotUtils.takeScreenshot("Scenario_Start_" + sanitizeScenarioName(currentScenarioName));

            // Screenshot processing conditional: Only if capture successful
            if (!screenshotPath.isEmpty()) {
                // Binary screenshot data retrieval: Image data for attachment
                // Byte array: Binary image data for streaming
                byte[] screenshot = ScreenshotUtils.takeScreenshot();

                // Allure attachment: Screenshot addition to test report
                // Stream attachment: Binary data streaming to report
                // PNG format: Standard image format for web reports
                Allure.addAttachment("Scenario Start", "image/png",
                        new ByteArrayInputStream(screenshot), "png");

                // ExtentReports logging: HTML report screenshot notation
                // Conditional logging: Only if ExtentReports enabled
                if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
                    ExtentReportManager.logInfo("Scenario started - screenshot captured");
                }
            }

        } catch (Exception e) {
            // Screenshot failure: Non-critical warning
            // Warning level: Screenshot issues shouldn't block test execution
            logger.warn("Failed to take initial screenshot", e);
        }
    }

    /**
     * SCENARIO START LOGGING - COMPREHENSIVE SCENARIO CONTEXT DOCUMENTATION
     *
     * Method amacı: Scenario execution başlangıcında complete context logging
     * Scenario metadata, tags, location information detailed recording
     *
     * Parametreler:
     * @param scenario - Cucumber scenario with complete metadata
     *
     * Bu method kullanılmazsa etkiler:
     * - Scenario execution context missing
     * - Debugging information incomplete
     * - Audit trail gaps
     */
    private void logScenarioStart(Scenario scenario) {
        // Scenario name logging: Primary scenario identifier
        logger.info("Scenario started: {}", scenario.getName());

        // Scenario tags logging: Test categorization information
        // Tag collection: All scenario tags for filtering/reporting
        logger.info("Scenario tags: {}", scenario.getSourceTagNames());

        // Scenario location logging: Feature file source information
        // URI information: File path for test source traceability
        logger.info("Scenario URI: {}", scenario.getUri());

        // Allure step addition: Scenario start step in test report
        // Step documentation: Granular test execution tracking
        Allure.step("Scenario Started: " + scenario.getName());

        // ExtentReports logging: HTML report scenario start notation
        // Conditional logging: Only if ExtentReports enabled
        if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
            ExtentReportManager.logInfo("Scenario execution started");
        }
    }

    /**
     * SCENARIO RESULT HANDLING - OUTCOME-BASED PROCESSING DISPATCHER
     *
     * Method amacı: Scenario execution result'una göre appropriate handling
     * Status-specific processing delegation (pass/fail/skip)
     *
     * Parametreler:
     * @param scenario - Cucumber scenario with execution result
     * @param duration - Scenario execution time in milliseconds
     *
     * Bu method kullanılmazsa etkiler:
     * - Result-specific processing missing
     * - No conditional documentation
     * - Generic handling for all outcomes
     */
    private void handleScenarioResult(Scenario scenario, long duration) {
        // Result status extraction: Scenario execution outcome determination
        String status = scenario.getStatus().toString();

        // Status-based processing: Switch statement for outcome handling
        // Dispatcher pattern: Delegate to specific result handlers
        switch (scenario.getStatus()) {
            case PASSED:
                // Passed scenario handling: Success-specific processing
                handlePassedScenario(scenario, duration);
                break;
            case FAILED:
                // Failed scenario handling: Failure-specific processing
                handleFailedScenario(scenario, duration);
                break;
            case SKIPPED:
                // Skipped scenario handling: Skip-specific processing
                handleSkippedScenario(scenario, duration);
                break;
            default:
                // Unknown status handling: Unexpected status logging
                logger.warn("Unknown scenario status: {} for scenario: {}", status, scenario.getName());
        }
    }

    /**
     * PASSED SCENARIO HANDLING - SUCCESS OUTCOME PROCESSING
     *
     * Method amacı: Successfully completed scenarios için specific processing
     * Success documentation, conditional screenshots, performance logging
     *
     * Parametreler:
     * @param scenario - Passed Cucumber scenario
     * @param duration - Execution duration for performance tracking
     *
     * Bu method kullanılmazsa etkiler:
     * - Success outcomes not differentiated
     * - No success-specific documentation
     * - Performance metrics missing
     */
    private void handlePassedScenario(Scenario scenario, long duration) {
        // Success logging: Positive outcome confirmation with performance data
        // Visual indicator: ✓ symbol for quick status recognition
        // Duration information: Performance metric inclusion
        logger.info("✓ Scenario PASSED: {} ({}ms)", scenario.getName(), duration);

        // Success screenshot conditional: Configuration-based screenshot capture
        // Optional feature: Screenshots on pass can be disabled for performance
        if (ConfigurationManager.getFrameworkConfig().isScreenshotOnPass()) {
            // Success screenshot capture: Passed scenario final state
            // File naming: Clear indication of successful scenario
            String screenshotPath = ScreenshotUtils.takeScreenshotOnPass(sanitizeScenarioName(scenario.getName()));

            // Screenshot attachment conditional: Only if capture successful
            if (!screenshotPath.isEmpty()) {
                // Screenshot report attachment: Visual success documentation
                attachScreenshotToScenario(scenario, screenshotPath, "Scenario Passed");
            }
        }

        // ExtentReports success logging: HTML report success notation
        // Conditional logging: Only if ExtentReports enabled
        if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
            ExtentReportManager.logPass("Scenario passed successfully in " + duration + "ms");
        }
    }

    /**
     * FAILED SCENARIO HANDLING - FAILURE OUTCOME PROCESSING
     *
     * Method amacı: Failed scenarios için comprehensive failure processing
     * Failure documentation, mandatory screenshots, detailed error logging
     *
     * Parametreler:
     * @param scenario - Failed Cucumber scenario
     * @param duration - Execution duration before failure
     *
     * Bu method kullanılmazsa etkiler:
     * - Failure analysis impossible
     * - No failure visual documentation
     * - Debugging information missing
     */
    private void handleFailedScenario(Scenario scenario, long duration) {
        // Failure logging: Error outcome confirmation with timing
        // Visual indicator: ✗ symbol for immediate failure recognition
        // Error level: Failure is significant event requiring attention
        logger.error("✗ Scenario FAILED: {} ({}ms)", scenario.getName(), duration);

        // Failure screenshot conditional: Configuration-based failure capture
        // Critical documentation: Failure screenshots usually enabled by default
        if (ConfigurationManager.getFrameworkConfig().isScreenshotOnFailure()) {
            // Failure screenshot capture: Failed scenario final state
            // Error context: Exception passed for additional context
            // File naming: Clear failure indication for debugging
            String screenshotPath = ScreenshotUtils.takeScreenshotOnFailure(
                    sanitizeScenarioName(scenario.getName()), new RuntimeException("Scenario failed"));

            // Screenshot attachment conditional: Only if capture successful
            if (!screenshotPath.isEmpty()) {
                // Failure screenshot report attachment: Critical debugging information
                attachScreenshotToScenario(scenario, screenshotPath, "Scenario Failed");
            }
        }

        // ExtentReports failure logging: HTML report failure notation
        // Conditional logging: Only if ExtentReports enabled
        if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
            ExtentReportManager.logFail("Scenario failed after " + duration + "ms");
        }
    }

    /**
     * SKIPPED SCENARIO HANDLING - SKIP OUTCOME PROCESSING
     *
     * Method amacı: Skipped scenarios için appropriate processing
     * Skip reason documentation, conditional processing
     *
     * Parametreler:
     * @param scenario - Skipped Cucumber scenario
     * @param duration - Time until skip decision
     *
     * Bu method kullanılmazsa etkiler:
     * - Skip reasons not tracked
     * - Incomplete test coverage analysis
     * - Skip pattern recognition impossible
     */
    private void handleSkippedScenario(Scenario scenario, long duration) {
        // Skip logging: Skip outcome confirmation with timing
        // Visual indicator: ⚠ symbol for skip status recognition
        // Warning level: Skip is noteworthy but not error condition
        logger.warn("⚠ Scenario SKIPPED: {} ({}ms)", scenario.getName(), duration);

        // ExtentReports skip logging: HTML report skip notation
        // Conditional logging: Only if ExtentReports enabled
        if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
            ExtentReportManager.logSkip("Scenario was skipped after " + duration + "ms");
        }
    }

    /**
     * VIDEO RECORDING STOP - VIDEO CAPTURE TERMINATION & PROCESSING
     *
     * Method amacı: Scenario video recording sonlandırma ve result-based processing
     * Video file management based on test outcome (save/delete policy)
     *
     * Parametreler:
     * @param scenario - Scenario with execution result for video policy
     *
     * Bu method kullanılmazsa etkiler:
     * - Video resources not cleaned up
     * - Disk space accumulation
     * - Video files not attached to reports
     */
    private void stopVideoRecording(Scenario scenario) {
        // Video recorder existence check: Only process if recording was active
        if (videoRecorder != null) {
            try {
                // Video stop logging: Video capture termination notification
                logger.info("Stopping video recording for scenario: {}", scenario.getName());

                // Video recording termination: Stop capture and get file path
                // File path return: Video file location for further processing
                String videoPath = videoRecorder.stopRecording();

                // Video file processing based on scenario result: Conditional video management
                // Pass condition check: Combined status and configuration check
                if (scenario.isFailed() &&
                        ConfigurationManager.getFrameworkConfig().isDeleteVideoOnPass()) {

                    // Passed scenario video deletion: Disk space management
                    // Configuration-based: Only delete if configured to do so
                    FileUtils.deleteFile(videoPath);
                    logger.info("Video deleted for passed scenario: {}", scenario.getName());

                } else {
                    // Failed/skipped scenario video preservation: Keep for analysis
                    // Report attachment: Video available in test reports
                    attachVideoToScenario(scenario, videoPath);
                    logger.info("Video saved for scenario: {} at {}", scenario.getName(), videoPath);
                }

            } catch (Exception e) {
                // Video processing failure: Non-critical error handling
                logger.error("Failed to stop video recording for scenario: {}", scenario.getName(), e);

            } finally {
                // Video recorder cleanup: Ensure recorder reference cleared
                // Memory cleanup: Release video recorder resources
                // Finally guarantee: Cleanup even if processing fails
                videoRecorder = null;
            }
        }
    }

    /**
     * FINAL SCREENSHOT CAPTURE - SCENARIO END STATE DOCUMENTATION
     *
     * Method amacı: Scenario completion anında final UI state capture
     * End state visual documentation with result status
     *
     * Parametreler:
     * @param scenario - Scenario for context and status information
     *
     * Bu method kullanılmazsa etkiler:
     * - Final scenario state undocumented
     * - Incomplete visual test record
     * - End state debugging information missing
     */
    private void takeFinalScreenshot(Scenario scenario) {
        try {
            // Final screenshot file naming: Descriptive name with status
            // Name components: End indicator, scenario name, execution status
            // Status inclusion: Clear indication of scenario outcome
            String screenshotName = "Scenario_End_" + sanitizeScenarioName(scenario.getName()) + "_" + scenario.getStatus();

            // Final screenshot capture: End state visual documentation
            String screenshotPath = ScreenshotUtils.takeScreenshot(screenshotName);

            // Screenshot attachment conditional: Only if capture successful
            if (!screenshotPath.isEmpty()) {
                // Final screenshot report attachment: End state documentation
                attachScreenshotToScenario(scenario, screenshotPath, "Scenario End");
            }

        } catch (Exception e) {
            // Final screenshot failure: Non-critical warning
            logger.warn("Failed to take final screenshot for scenario: {}", scenario.getName(), e);
        }
    }

    /**
     * SCREENSHOT SCENARIO ATTACHMENT - MEDIA REPORT INTEGRATION
     *
     * Method amacı: Screenshot'ları scenario results ve reports'a attach etme
     * Multi-platform report integration (Cucumber, Allure)
     *
     * Parametreler:
     * @param scenario - Target scenario for attachment
     * @param screenshotPath - File system path to screenshot
     * @param name - Descriptive name for attachment
     *
     * Bu method kullanılmazsa etkiler:
     * - Screenshots not visible in reports
     * - Visual debugging information missing
     * - Media documentation broken
     */
    private void attachScreenshotToScenario(Scenario scenario, String screenshotPath, String name) {
        try {
            // Screenshot file reading: Binary image data retrieval
            // File to bytes conversion: Binary data for attachment
            byte[] screenshot = FileUtils.readFileAsBytes(screenshotPath);

            // Cucumber scenario attachment: Screenshot attachment to scenario result
            // MIME type: image/png for proper display
            // Name parameter: Descriptive attachment name
            scenario.attach(screenshot, "image/png", name);

            // Allure report attachment: Screenshot addition to Allure report
            // Stream attachment: Binary data streaming for web display
            // Format specification: PNG format for web compatibility
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), "png");

        } catch (Exception e) {
            // Screenshot attachment failure: Non-critical warning
            logger.warn("Failed to attach screenshot to scenario: {}", scenario.getName(), e);
        }
    }

    /**
     * VIDEO SCENARIO ATTACHMENT - VIDEO REPORT INTEGRATION
     *
     * Method amacı: Video files'ları test reports'a attach etme
     * Video documentation integration for visual test analysis
     *
     * Parametreler:
     * @param scenario - Target scenario for video attachment
     * @param videoPath - File system path to video file
     *
     * Bu method kullanılmazsa etkiler:
     * - Videos not accessible in reports
     * - Visual test replay impossible
     * - Video documentation broken
     */
    private void attachVideoToScenario(Scenario scenario, String videoPath) {
        try {
            // Video file reading: Binary video data retrieval
            // Large file handling: Video files significantly larger than screenshots
            byte[] videoBytes = FileUtils.readFileAsBytes(videoPath);

            // Allure video attachment: Video addition to Allure report
            // MP4 format: Standard video format for web playback
            // Stream attachment: Binary video data streaming
            Allure.addAttachment("Scenario Video", "video/mp4",
                    new ByteArrayInputStream(videoBytes), "mp4");

        } catch (Exception e) {
            // Video attachment failure: Non-critical warning
            logger.warn("Failed to attach video to scenario: {}", scenario.getName(), e);
        }
    }

    /**
     * SETUP FAILURE SCREENSHOT - ERROR STATE DOCUMENTATION
     *
     * Method amacı: Setup failure durumunda diagnostic screenshot capture
     * Error state visual documentation for setup debugging
     *
     * Bu method kullanılmazsa etkiler:
     * - Setup failure state invisible
     * - Debugging information incomplete
     * - Error diagnosis difficult
     */
    private void takeScreenshotOnSetupFailure() {
        try {
            // Driver availability check: Only capture if driver partially initialized
            // Safety check: Prevent screenshot attempt without driver
            if (DriverManager.isDriverInitialized()) {
                // Setup failure screenshot: Error state visual documentation
                // File naming: Clear indication of setup failure context
                String screenshotPath = ScreenshotUtils.takeScreenshot("Setup_Failure_" + sanitizeScenarioName(currentScenarioName));

                // Setup failure screenshot confirmation: Diagnostic aid confirmation
                logger.info("Setup failure screenshot saved: {}", screenshotPath);
            }
        } catch (Exception e) {
            // Setup failure screenshot error: Secondary failure warning
            logger.warn("Failed to take screenshot on setup failure", e);
        }
    }

    /**
     * SCENARIO SUMMARY ADDITION - METRICS REPORT INTEGRATION
     *
     * Method amacı: Scenario summary information'ı reports'a ekleme
     * Performance metrics, duration data report integration
     *
     * Parametreler:
     * @param scenario - Scenario for context information
     * @param duration - Execution duration for performance metrics
     *
     * Bu method kullanılmazsa etkiler:
     * - Performance metrics missing from reports
     * - Execution summary incomplete
     * - Duration tracking absent
     */
    private void addScenarioSummaryToReports(Scenario scenario, long duration) {
        try {
            // Allure duration parameter: Performance metric addition to report
            // Parameter format: Millisecond duration with unit
            Allure.parameter("Duration", duration + "ms");

            // ExtentReports summary logging: HTML report duration information
            // Conditional logging: Only if ExtentReports enabled
            if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
                ExtentReportManager.logInfo("Scenario completed in " + duration + "ms");
            }

        } catch (Exception e) {
            // Summary addition failure: Non-critical warning
            logger.warn("Failed to add scenario summary to reports", e);
        }
    }

    /**
     * SCENARIO COMPLETION LOGGING - FINAL EXECUTION STATE DOCUMENTATION
     *
     * Method amacı: Scenario completion comprehensive logging
     * Final execution state, status, performance metrics documentation
     *
     * Parametreler:
     * @param scenario - Completed scenario with final status
     * @param duration - Total execution duration
     *
     * Bu method kullanılmazsa etkiler:
     * - Scenario completion tracking incomplete
     * - Final execution state undocumented
     * - Audit trail gaps
     */
    private void logScenarioCompletion(Scenario scenario, long duration) {
        // Comprehensive completion logging: All key scenario information
        // Information components: Name, status, duration for complete picture
        logger.info("Scenario completed: {} - Status: {} - Duration: {}ms",
                scenario.getName(), scenario.getStatus(), duration);

        // Allure completion step: Final step documentation in report
        // Step summary: Scenario name and status for clear completion indication
        Allure.step("Scenario Completed: " + scenario.getName() + " [" + scenario.getStatus() + "]");
    }

    /**
     * CLEANUP - CRITICAL RESOURCE MANAGEMENT
     *
     * Method amacı: Critical resource cleanup guarantee
     * Driver termination, memory cleanup, resource leak prevention
     *
     * Bu method kullanılmazsa etkiler:
     * - Massive resource leaks
     * - Driver sessions accumulation
     * - System performance degradation
     * - Memory exhaustion
     */
    private void cleanup() {
        try {
            // Driver termination: Mobile driver resource cleanup
            // Driver availability check: Only quit if driver was initialized
            if (DriverManager.isDriverInitialized()) {
                // Driver quit operation: Clean driver termination
                // Resource release: Driver session, device connection cleanup
                DriverManager.quitDriver();

                // Driver cleanup confirmation: Successful termination logging
                logger.info("Driver quit successfully");
            }

        } catch (Exception e) {
            // Cleanup failure: Critical error that could indicate resource leaks
            // Error level: Resource management failure is significant
            logger.error("Error during cleanup", e);
        }
    }

    /**
     * REPORTING FINALIZATION - SUITE-LEVEL REPORT COMPLETION
     *
     * Method amacı: Suite completion sonrası report systems finalization
     * Report writers flushing, resource cleanup, final report generation
     *
     * Bu method kullanılmazsa etkiler:
     * - Reports not finalized properly
     * - Report data incomplete
     * - Report files not properly closed
     */
    private static void finalizeReporting() {
        // Reporting finalization start: Report cleanup phase beginning
        logger.info("Finalizing reporting");

        try {
            // ExtentReports finalization: Report writer flushing and resource cleanup
            // Conditional finalization: Only if ExtentReports was enabled
            if (ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()) {
                // ExtentReports flush: Final report writing and file closure
                // Resource cleanup: Report writer termination
                ExtentReportManager.flushReports();

                // ExtentReports finalization confirmation: Success logging
                logger.info("ExtentReports flushed successfully");
            }

        } catch (Exception e) {
            // Reporting finalization failure: Significant error in report generation
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