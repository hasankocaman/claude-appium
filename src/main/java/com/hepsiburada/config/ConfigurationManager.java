package com.hepsiburada.config;

// Owner Framework: Type-safe configuration management için kullanılır
// Config: Interface-based configuration definitions için base interface
// ConfigFactory: Configuration interface'lerinin implementation'larını yaratır
// Bu import'lar olmadan: Type-safe configuration management yapılamaz
import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;

/**
 * CONFIGURATION MANAGER CLASS - MERKEZI KONFİGÜRASYON YÖNETİMİ
 * 
 * Bu class'ın projede rolü:
 * - Tüm framework konfigürasyon ayarlarını merkezi olarak yönetir
 * - Type-safe configuration access sağlar (interface-based)
 * - Multiple environment support (dev, test, prod) sunar
 * - External configuration files (.properties) ile integration
 * - Default values ve override mechanisms sağlar
 * - System properties ve environment variables support
 * 
 * Kullanılmazsa etki:
 * - Hard-coded configuration values (flexibility yok)
 * - Environment-specific ayarlar yapılamaz
 * - Configuration scatter across multiple files
 * - Type safety yok (string-based config access)
 * - Runtime configuration changes imkansız
 * - Maintenance nightmare (scattered configs)
 * 
 * Diğer class'larla ilişkisi:
 * - DriverFactory: Device ve platform configurations kullanır
 * - BaseTest: Test timeout ve retry settings alır
 * - Screenshot/Video utilities: Media configuration alır
 * - Report managers: Reporting enable/disable flags alır
 * - All test classes: Environment-specific URLs ve settings alır
 * 
 * Design Patterns:
 * - Factory Pattern: ConfigFactory ile interface implementations
 * - Singleton Pattern: Static final instances (memory efficient)
 * - Interface Segregation: Platform-specific config interfaces
 * - Configuration Pattern: External configuration management
 * 
 * @author Hepsiburada Test Automation Team
 */
public final class ConfigurationManager {
    
    /**
     * Private constructor: Utility class'ının instance yaratılmasını engeller
     * Bu constructor olmadan: Bu class'tan nesne yaratılabilir (istenmeyen)
     * Utility class pattern: Instance'a gerek olmayan static methodlar içerir
     * Configuration manager: Sadece static factory methodları kullanılmalı
     */
    private ConfigurationManager() {
        // Utility class should not be instantiated
        // Bu yorum: Geliştiricilere neden private olduğunu açıklar
    }
    
    /**
     * FRAMEWORK KONFİGÜRASYON INTERFACE - ANA FRAMEWORK AYARLARI
     * 
     * Interface amacı: Framework'ün temel ayarlarını type-safe şekilde expose eder
     * Property sources: Multiple configuration sources (files, system props, env vars)
     * Default values: Her property için fallback values tanımlı
     * 
     * Kullanılmazsa etki:
     * - Framework ayarları hard-coded olur
     * - Environment-specific configuration imkansız
     * - Type-safe config access yok
     * - Default values manuel olarak handle edilmeli
     * 
     * Configuration sources priority (high to low):
     * 1. System properties (-Dproperty=value)
     * 2. Environment variables 
     * 3. Environment-specific properties (framework-${env}.properties)
     * 4. Default properties (framework.properties)
     * 5. @DefaultValue annotations
     */
    @Config.Sources({
        "classpath:config/framework.properties",
        "classpath:config/framework-${env}.properties",
        "system:properties",
        "system:env"
    })
    public interface FrameworkConfig extends Config {
        
        // Application Configuration
        @Key("app.name")
        @DefaultValue("Hepsiburada")
        String getAppName();
        
        @Key("app.package")
        @DefaultValue("com.hepsiburada.ecommerce")
        String getAppPackage();
        
        @Key("app.activity")
        @DefaultValue("com.hepsiburada.ecommerce.MainActivity")
        String getAppActivity();
        
        @Key("app.path")
        @DefaultValue("src/test/resources/apps/Hepsiburada.apk")
        String getAppPath();
        
        // Appium Server Configuration
        @Key("appium.server.url")
        @DefaultValue("http://127.0.0.1:4723/wd/hub")
        String getAppiumServerUrl();
        
        @Key("appium.server.host")
        @DefaultValue("127.0.0.1")
        String getAppiumServerHost();
        
        @Key("appium.server.port")
        @DefaultValue("4723")
        int getAppiumServerPort();
        
        // Device Configuration
        @Key("device.platform")
        @DefaultValue("Android")
        String getPlatformName();
        
        @Key("device.platform.version")
        @DefaultValue("11.0")
        String getPlatformVersion();
        
        @Key("device.name")
        @DefaultValue("Android Emulator")
        String getDeviceName();
        
        @Key("device.udid")
        String getDeviceUdid();
        
        @Key("device.automation.name")
        @DefaultValue("UiAutomator2")
        String getAutomationName();
        
        // Test Configuration
        @Key("test.timeout.implicit")
        @DefaultValue("10")
        int getImplicitTimeout();
        
        @Key("test.timeout.explicit")
        @DefaultValue("30")
        int getExplicitTimeout();
        
        @Key("test.retry.count")
        @DefaultValue("1")
        int getRetryCount();
        
        @Key("test.parallel.threads")
        @DefaultValue("1")
        int getParallelThreads();
        
        // Screenshot Configuration
        @Key("screenshot.on.failure")
        @DefaultValue("true")
        boolean isScreenshotOnFailure();
        
        @Key("screenshot.on.pass")
        @DefaultValue("false")
        boolean isScreenshotOnPass();
        
        @Key("screenshot.on.step")
        @DefaultValue("true")
        boolean isScreenshotOnStep();
        
        @Key("screenshot.path")
        @DefaultValue("target/screenshots")
        String getScreenshotPath();
        
        // Video Recording Configuration
        @Key("video.recording.enabled")
        @DefaultValue("true")
        boolean isVideoRecordingEnabled();
        
        @Key("video.recording.path")
        @DefaultValue("target/videos")
        String getVideoRecordingPath();
        
        @Key("video.delete.on.pass")
        @DefaultValue("true")
        boolean isDeleteVideoOnPass();
        
        // Reporting Configuration
        @Key("report.allure.enabled")
        @DefaultValue("true")
        boolean isAllureReportEnabled();
        
        @Key("report.extent.enabled")
        @DefaultValue("true")
        boolean isExtentReportEnabled();
        
        @Key("report.cucumber.enabled")
        @DefaultValue("true")
        boolean isCucumberReportEnabled();
        
        // Environment Configuration
        @Key("environment")
        @DefaultValue("test")
        String getEnvironment();
        
        @Key("base.url")
        @DefaultValue("https://www.hepsiburada.com")
        String getBaseUrl();
        
        // Logging Configuration
        @Key("logging.level")
        @DefaultValue("INFO")
        String getLoggingLevel();
        
        /**
         * Log dosyalarının kaydedileceği path alır
         * @return Log directory path
         * Kullanıldığı yerler: Log4j file appenders, log file operations
         * Default: "target/logs" (Maven standard directory)
         */
        @Key("logging.path")
        @DefaultValue("target/logs")
        String getLoggingPath();
        
        // INTERFACE SON - TÜM FRAMEWORK AYARLARI TANIMLI
        // Bu interface Owner framework tarafından runtime'da implement edilir
        // Type-safe property access için proxy pattern kullanılır
    }
    
    /**
     * iOS KONFİGÜRASYON INTERFACE - iOS PLATFORM ÖZEL AYARLARI
     * 
     * Interface amacı: iOS platform'una özgü configuration ayarlarını type-safe şekilde expose eder
     * Property sources: iOS-specific properties dosyası ve system properties
     * Platform scope: Sadece iOS testing için gerekli ayarlar
     * 
     * Kullanılmazsa etki:
     * - iOS-specific ayarlar hard-coded olur
     * - iOS cihaz/simulator configuration eksik
     * - WebDriverAgent port conflicts
     * - Bundle ID management zorlaşır
     * 
     * iOS vs Android farkları:
     * - Bundle ID vs Package name
     * - WebDriverAgent vs UiAutomator2 ports
     * - Simulator vs Emulator configuration
     * - XCUITest vs UiAutomator2 settings
     */
    @Config.Sources({
        "classpath:config/ios.properties",
        "system:properties"
    })
    public interface IOSConfig extends Config {
        
        /**
         * iOS app Bundle ID alır (iOS app identification için)
         * @return iOS bundle identifier (reverse domain format)
         * Kullanıldığı yerler: iOS driver capabilities, app targeting
         * Default: "com.hepsiburada.ios" (iOS app bundle ID)
         * Format: Reverse domain notation (com.company.app)
         */
        @Key("ios.bundle.id")
        @DefaultValue("com.hepsiburada.ios")
        String getBundleId();
        
        /**
         * iOS simulator adı alır (target simulator identification için)
         * @return Simulator device name
         * Kullanıldığı yerler: iOS driver capabilities, simulator selection
         * Default: "iPhone 14" (modern iPhone simulator)
         * Options: iPhone 13, iPhone 14, iPad Pro, vs.
         */
        @Key("ios.simulator.name")
        @DefaultValue("iPhone 14")
        String getSimulatorName();
        
        /**
         * iOS simulator version alır (iOS version targeting için)
         * @return iOS simulator version string
         * Kullanıldığı yerler: iOS driver capabilities, version-specific logic
         * Default: "16.0" (iOS 16 - stable modern version)
         * Format: Major.minor (15.0, 16.0, 17.0)
         */
        @Key("ios.simulator.version")
        @DefaultValue("16.0")
        String getSimulatorVersion();
        
        /**
         * iOS automation engine adı alır (iOS automation backend için)
         * @return iOS automation framework identifier
         * Kullanıldığı yerler: iOS driver capabilities, automation engine selection
         * Default: "XCUITest" (modern iOS automation framework)
         * Note: UIAutomation deprecated, sadece XCUITest kullanılmalı
         */
        @Key("ios.automation.name")
        @DefaultValue("XCUITest")
        String getAutomationName();
        
        /**
         * WebDriverAgent local port alır (WDA server port configuration için)
         * @return WDA server local port number
         * Kullanıldığı yerler: iOS driver capabilities, port conflict avoidance
         * Default: 8100 (standard WDA port, Android systemPort'tan farklı)
         * Range: 8100-8199 (iOS port range, parallel execution için)
         */
        @Key("ios.wda.local.port")
        @DefaultValue("8100")
        int getWdaLocalPort();
        
        // iOS INTERFACE SON - TÜM iOS ÖZEL AYARLARI TANIMLI
        // Bu interface iOS testing için gerekli tüm configuration'ları içerir
        // WebDriverAgent, Bundle ID, Simulator ayarları dahil
    }
    
    /**
     * ANDROID KONFİGÜRASYON INTERFACE - ANDROID PLATFORM ÖZEL AYARLARI
     * 
     * Interface amacı: Android platform'una özgü configuration ayarlarını type-safe şekilde expose eder
     * Property sources: Android-specific properties dosyası ve system properties
     * Platform scope: Sadece Android testing için gerekli ayarlar
     * 
     * Kullanılmazsa etki:
     * - Android-specific ayarlar hard-coded olur
     * - UiAutomator2 port configuration eksik
     * - Permission handling otomatik olmaz
     * - App reset behavior kontrol edilemez
     * 
     * Android vs iOS farkları:
     * - Package name + Activity vs Bundle ID
     * - UiAutomator2 vs XCUITest configuration
     * - System port vs WDA port
     * - Permission grant vs iOS sandbox model
     */
    @Config.Sources({
        "classpath:config/android.properties",
        "system:properties"
    })
    public interface AndroidConfig extends Config {
        
        /**
         * Android app package name alır (app identification için)
         * @return Android package identifier (reverse domain format)
         * Kullanıldığı yerler: Android driver capabilities, app targeting
         * Default: "com.hepsiburada.ecommerce" (Android app package)
         * Format: Reverse domain notation (com.company.app)
         */
        @Key("android.app.package")
        @DefaultValue("com.hepsiburada.ecommerce")
        String getAppPackage();
        
        /**
         * Android main activity class alır (app entry point için)
         * @return Activity class full qualified name
         * Kullanıldığı yerler: Android driver capabilities, app startup
         * Default: "com.hepsiburada.ecommerce.MainActivity" (main entry activity)
         * Format: Full class path (package + class name)
         */
        @Key("android.app.activity")
        @DefaultValue("com.hepsiburada.ecommerce.MainActivity")
        String getAppActivity();
        
        /**
         * Android automation engine adı alır (Android automation backend için)
         * @return Android automation framework identifier
         * Kullanıldığı yerler: Android driver capabilities, automation engine selection
         * Default: "UiAutomator2" (modern Android automation framework)
         * Note: UiAutomator1 deprecated, sadece UiAutomator2 kullanılmalı
         */
        @Key("android.automation.name")
        @DefaultValue("UiAutomator2")
        String getAutomationName();
        
        /**
         * Android permission auto-grant aktif mi kontrol eder
         * @return true: permissions otomatik ver, false: manual grant
         * Kullanıldığı yerler: Android driver capabilities, permission handling
         * Default: true (test automation için permission popup'ları engeller)
         * Impact: false ise location, camera, vs. permissions manuel gerekir
         */
        @Key("android.auto.grant.permissions")
        @DefaultValue("true")
        boolean isAutoGrantPermissions();
        
        /**
         * Android app no-reset aktif mi kontrol eder
         * @return true: app data korun, false: reset yap
         * Kullanıldığı yerler: Android driver capabilities, app state management
         * Default: false (clean state testing için reset yap)
         * Impact: true ise app state testler arasında korunur
         */
        @Key("android.no.reset")
        @DefaultValue("false")
        boolean isNoReset();
        
        /**
         * Android app full-reset aktif mi kontrol eder
         * @return true: complete uninstall/reinstall, false: normal reset
         * Kullanıldığı yerler: Android driver capabilities, deep app reset
         * Default: false (performance için normal reset)
         * Impact: true ise slow ama complete clean state
         */
        @Key("android.full.reset")
        @DefaultValue("false")
        boolean isFullReset();
        
        /**
         * Android UiAutomator2 system port alır (UiAutomator2 server port)
         * @return UiAutomator2 server port number
         * Kullanıldığı yerler: Android driver capabilities, port conflict avoidance
         * Default: 8200 (standard Android port, iOS WDA port'tan farklı)
         * Range: 8200-8299 (Android port range, parallel execution için)
         */
        @Key("android.system.port")
        @DefaultValue("8200")
        int getSystemPort();
        
        // ANDROID INTERFACE SON - TÜM ANDROID ÖZEL AYARLARI TANIMLI
        // Bu interface Android testing için gerekli tüm configuration'ları içerir
        // UiAutomator2, Package/Activity, Permission ayarları dahil
    }
    
    // STATIC CONFIGURATION INSTANCES - SINGLETON PATTERN İLE CONFIG OBJELERİ
    
    // Framework configuration instance: Genel framework ayarları için singleton
    // ConfigFactory.create(): Owner framework proxy pattern ile interface implement eder
    // Static final: Thread-safe, memory efficient, immutable reference
    // Bu instance olmadan: Framework ayarlarına erişilemez, hard-coded values kullanılır
    private static final FrameworkConfig FRAMEWORK_CONFIG = ConfigFactory.create(FrameworkConfig.class);
    
    // iOS configuration instance: iOS-specific ayarlar için singleton
    // ConfigFactory.create(): ios.properties dosyasından ayarları okur
    // Static final: iOS testing için tek instance, thread-safe access
    // Bu instance olmadan: iOS ayarları alınamaz, iOS testing yapılamaz
    private static final IOSConfig IOS_CONFIG = ConfigFactory.create(IOSConfig.class);
    
    // Android configuration instance: Android-specific ayarlar için singleton
    // ConfigFactory.create(): android.properties dosyasından ayarları okur  
    // Static final: Android testing için tek instance, thread-safe access
    // Bu instance olmadan: Android ayarları alınamaz, Android testing yapılamaz
    private static final AndroidConfig ANDROID_CONFIG = ConfigFactory.create(AndroidConfig.class);
    
    /**
     * FRAMEWORK KONFİGÜRASYONU ALMA METODİ
     * 
     * Method amacı: Framework genel ayarlarına erişim sağlar (singleton access)
     * Parametreler: Parametre almaz (static singleton instance döner)
     * Return değeri: FrameworkConfig interface implementation
     * 
     * Kullanılmazsa etki:
     * - Framework ayarlarına erişilemez
     * - Driver timeout, app path, server URL vs. alınamaz
     * - Test execution konfigürasyonu eksik
     * - Environment-specific ayarlar uygulanamaz
     * 
     * Çağrıldığı yerler:
     * - DriverFactory: App path, server URL, timeout ayarları için
     * - Test classes: Environment, retry count, parallelization ayarları için
     * - Utility classes: Screenshot, video, logging paths için
     * - Report managers: Reporting enable/disable flags için
     * 
     * Thread Safety: Static final field, thread-safe access guaranteed
     */
    public static FrameworkConfig getFrameworkConfig() {
        // Static singleton instance return et
        // Bu return olmadan: Framework configuration'a erişilemez
        return FRAMEWORK_CONFIG;
    }
    
    /**
     * iOS KONFİGÜRASYONU ALMA METODİ
     * 
     * Method amacı: iOS platform-specific ayarlarına erişim sağlar
     * Parametreler: Parametre almaz (static singleton instance döner)
     * Return değeri: IOSConfig interface implementation
     * 
     * Kullanılmazsa etki:
     * - iOS-specific ayarlar alınamaz
     * - Bundle ID, WebDriverAgent port, simulator config eksik
     * - iOS driver capabilities oluşturulamaz
     * - iOS testing completely broken
     * 
     * Çağrıldığı yerler:
     * - DriverFactory.getIOSCapabilities(): iOS driver configuration için
     * - iOS test utilities: Simulator management için
     * - iOS-specific test logic: Platform behavior için
     * 
     * Platform Dependency: Sadece iOS testing aktif olduğunda kullanılır
     */
    public static IOSConfig getIOSConfig() {
        // Static singleton instance return et
        // Bu return olmadan: iOS configuration'a erişilemez
        return IOS_CONFIG;
    }
    
    /**
     * ANDROID KONFİGÜRASYONU ALMA METODİ
     * 
     * Method amacı: Android platform-specific ayarlarına erişim sağlar
     * Parametreler: Parametre almaz (static singleton instance döner)
     * Return değeri: AndroidConfig interface implementation
     * 
     * Kullanılmazsa etki:
     * - Android-specific ayarlar alınamaz
     * - Package, Activity, System port, permission config eksik
     * - Android driver capabilities oluşturulamaz
     * - Android testing completely broken
     * 
     * Çağrıldığı yerler:
     * - DriverFactory.getAndroidCapabilities(): Android driver configuration için
     * - Android test utilities: Emulator/device management için
     * - Android-specific test logic: Platform behavior için
     * 
     * Platform Dependency: Sadece Android testing aktif olduğunda kullanılır
     */
    public static AndroidConfig getAndroidConfig() {
        // Static singleton instance return et
        // Bu return olmadan: Android configuration'a erişilemez
        return ANDROID_CONFIG;
    }
    
    // CONFIGURATION MANAGER SON - TÜM PLATFORM CONFIGURATION'LARI HAZIR
    // Bu class framework'ün configuration management core'u
    // Type-safe, environment-aware, platform-specific configuration access sağlar
}