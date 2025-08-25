package com.hepsiburada.drivers;

// ConfigurationManager: Platform ve cihaz konfigürasyonlarını okumak için kullanılır
// Bu import olmadan Android/iOS ayarları ve APK yolu alınamaz
import com.hepsiburada.config.ConfigurationManager;

// Appium driver interface'leri: Mobil test automation için temel driver sınıfları
// AppiumDriver: Genel mobil driver (iOS ve Android için ortak özellikler)
// AndroidDriver: Android'e özel driver (UiAutomator2 desteği)
// IOSDriver: iOS'e özel driver (XCUITest desteği)
// Bu import'lar olmadan mobil cihaz kontrolü imkansız
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

// Log4j2: Debugging, monitoring ve production logging için
// Bu import'lar olmadan driver yaratma süreçleri takip edilemez
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Selenium DesiredCapabilities: Appium driver ayarlarını tanımlamak için
// Bu import olmadan platform-specific ayarlar belirlenemez
import org.openqa.selenium.remote.DesiredCapabilities;

// Java Standard Library imports:
// File: APK/IPA dosya varlığını kontrol etmek için
// URL: Appium server bağlantı adresi için
// Duration: Timeout ayarları için
// MalformedURLException: Hatalı URL handling için
// Bu import'lar olmadan file operations ve network connection yapılamaz
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * DRIVER FACTORY CLASS - FACTORY DESIGN PATTERN İMPLEMENTASYONU
 * 
 * Bu class'ın projede rolü:
 * - Factory Design Pattern uygular (object creation complexity'sini gizler)
 * - Android ve iOS için farklı driver instance'ları yaratır
 * - Platform-specific capabilities management yapar
 * - Thread-safe driver storage sağlar (parallel execution için kritik)
 * - Driver lifecycle management (create, configure, cleanup) yapar
 * 
 * Kullanılmazsa etki:
 * - Her test sınıfı kendi driver yaratma logic'ini yazmak zorunda kalır
 * - Platform-specific configurations her yerde tekrarlanır (code duplication)
 * - Thread safety problemi oluşur (parallel execution'da driver conflicts)
 * - Centralized driver management olmaz (maintenance zorlaşır)
 * - Consistent timeout ve capability settings uygulanamaz
 * 
 * Diğer class'larla ilişkisi:
 * - DriverManager: Bu class'ı kullanarak simplified API sağlar (facade pattern)
 * - ConfigurationManager: Platform ve device configurations alır (dependency)
 * - Test classes: Dolaylı olarak bu class'tan yaratılan driver'ları kullanır
 * - Page Objects: Bu class'tan dönen driver instance'larına bağımlıdır
 * 
 * Design Patterns:
 * - Factory Pattern: Platform'a göre uygun driver yaratır
 * - Singleton Pattern: ThreadLocal ile thread-specific instance management
 * - Template Method Pattern: Common driver setup flow, platform-specific implementations
 */
public final class DriverFactory {
    
    // Logger instance: Bu class'ın tüm işlemlerini loglamak için
    // Static final: Memory efficient, immutable, class-level access
    // DriverFactory.class: Log'larda hangi class'tan geldiğini belirtir
    private static final Logger logger = LogManager.getLogger(DriverFactory.class);
    
    // ThreadLocal driver storage: Her thread için ayrı driver instance
    // ThreadLocal<AppiumDriver>: Thread safety sağlar, parallel execution destekler
    // Static: Class-level access, tüm static metodlardan erişilebilir
    // Final: Reference değiştirilemez (immutable reference)
    // Bu değişken olmadan: Parallel testlerde driver conflicts oluşur
    private static final ThreadLocal<AppiumDriver> driverThreadLocal = new ThreadLocal<>();
    
    /**
     * Private constructor: Factory class'ının instance yaratılmasını engeller
     * Bu constructor olmadan: Bu class'tan nesne yaratılabilir (istenmeyen)
     * Factory pattern gereği: Sadece static factory methodları kullanılmalı
     * Utility class pattern: Instance'a gerek olmayan static methodlar içerir
     */
    private DriverFactory() {
        // Utility class should not be instantiated
        // Bu yorum: Geliştiricilere neden private olduğunu açıklar
    }
    
    /**
     * ANA DRIVER BAŞLATMA METODİ - FACTORY PATTERN CORE METHOD
     * 
     * Method amacı: Platform'a göre uygun Appium driver yaratır ve konfigüre eder
     * Parametreler: platform - Hedef platform adı ("Android" veya "iOS", case-insensitive)
     * Return değeri: Konfigüre edilmiş ve kullanıma hazır AppiumDriver instance'ı
     * 
     * Kullanılmazsa etki:
     * - Mobil test otomasyonu çalışamaz (driver yok = test yok)
     * - Platform-specific testing imkansız
     * - Appium server ile connection kurulamaz
     * - Test framework tamamen işlevsiz hale gelir
     * 
     * Diğer metodlarla kıyasla:
     * - getAndroidCapabilities/getIOSCapabilities(): Delegate methodlar, bu method onları orchestrate eder
     * - configureDriverTimeouts(): Bu method timeout configuration'ı da yapar
     * - En kritik factory method (entire framework'ün giriş noktası)
     * - Template method pattern: Common flow, platform-specific implementations
     * 
     * Çağrıldığı yerler:
     * - DriverManager.initializeDriver() methods
     * - Test setup hooks (TestHooks.before() method)
     * - BaseTest class initialization
     * - CI/CD pipeline test execution scripts
     * 
     * Bağımlılıkları:
     * - ConfigurationManager (server URL, platform settings)
     * - Platform-specific capability methods
     * - Appium server (external dependency)
     * - APK/IPA files (application binary)
     */
    public static AppiumDriver initializeDriver(String platform) {
        // Platform bilgisini logla (hangi platform için initialization başlıyor)
        // Bu log olmadan: Hangi platform için driver yaratıldığı takip edilemez
        logger.info("Initializing driver for platform: {}", platform);
        
        // Driver instance holder: Platform'a göre Android veya iOS driver tutacak
        // Polymorphism: AppiumDriver interface, concrete implementations AndroidDriver/IOSDriver
        AppiumDriver driver;
        
        // Capabilities container: Appium'a gönderilecek tüm ayarları tutar
        // DesiredCapabilities: Key-value pairs olarak driver configurasyonu
        // Bu obje olmadan: Driver ayarları Appium'a gönderilemez
        DesiredCapabilities capabilities = new DesiredCapabilities();
        
        try {
            // Appium server URL'ini configuration'dan al ve URL object'e çevir
            // Bu satır olmadan: Appium server'a bağlanılamaz, driver yaratılamaz
            URL serverUrl = new URL(ConfigurationManager.getFrameworkConfig().getAppiumServerUrl());
            
            // Platform'a göre uygun driver yaratma logic'i (Factory Pattern core)
            // toLowerCase(): Case-insensitive platform matching ("Android"/"android"/"ANDROID" hepsi kabul)
            switch (platform.toLowerCase()) {
                case "android":
                    // Android capabilities'lerini al (UiAutomator2, APK path, permissions vs.)
                    // Bu satır olmadan: Android-specific ayarlar uygulanamaz
                    capabilities = getAndroidCapabilities();
                    
                    // AndroidDriver instance yarat (UiAutomator2 backend kullanır)
                    // serverUrl: Appium server connection point
                    // capabilities: Android-specific configurations
                    // Bu satır olmadan: Android cihaz kontrolü imkansız
                    driver = new AndroidDriver(serverUrl, capabilities);
                    
                    // Android driver başarılı yaratılma log'u
                    logger.info("Android driver initialized successfully");
                    break;
                    
                case "ios":
                    // iOS capabilities'lerini al (XCUITest, Bundle ID, WDA port vs.)
                    // Bu satır olmadan: iOS-specific ayarlar uygulanamaz
                    capabilities = getIOSCapabilities();
                    
                    // IOSDriver instance yarat (XCUITest backend kullanır)
                    // serverUrl: Appium server connection point
                    // capabilities: iOS-specific configurations
                    // Bu satır olmadan: iOS cihaz kontrolü imkansız
                    driver = new IOSDriver(serverUrl, capabilities);
                    
                    // iOS driver başarılı yaratılma log'u
                    logger.info("iOS driver initialized successfully");
                    break;
                    
                default:
                    // Desteklenmeyen platform için hata fırlat
                    // Bu case olmadan: Invalid platform'lar için belirsiz behavior
                    throw new IllegalArgumentException("Unsupported platform: " + platform);
            }
            
            // Driver timeout ayarlarını konfigüre et (implicit, explicit, script timeouts)
            // Bu satır olmadan: Default timeout'lar kullanılır (genellikle yetersiz)
            configureDriverTimeouts(driver);
            
            // Driver'ı ThreadLocal storage'a kaydet (thread safety için kritik)
            // Bu satır olmadan: Parallel execution'da driver conflicts, çökmeler
            driverThreadLocal.set(driver);
            
            // Başarılı initialization completion log'u
            // Bu log olmadan: Driver'ın başarıyla yaratılıp yaratılmadığı belli olmaz
            logger.info("Driver initialization completed for platform: {}", platform);
            
            // Yaratılan ve konfigüre edilen driver'ı return et
            // Bu return olmadan: Calling method driver instance'ını alamaz
            return driver;
            
        } catch (MalformedURLException e) {
            // Hatalı Appium server URL durumu (syntax error, invalid format)
            // URL format örneği: http://127.0.0.1:4723/wd/hub
            logger.error("Invalid Appium server URL: {}", 
                ConfigurationManager.getFrameworkConfig().getAppiumServerUrl(), e);
            
            // Runtime exception'a wrap et (checked -> unchecked conversion)
            // Bu throw olmadan: Hata silent kalır, undefined behavior
            throw new RuntimeException("Invalid Appium server URL", e);
            
        } catch (Exception e) {
            // Diğer tüm hatalar (network, capabilities, driver creation errors)
            // Generic exception handling: SessionNotCreated, ConnectionRefused vs.
            logger.error("Failed to initialize driver for platform: {}", platform, e);
            
            // Runtime exception'a wrap et (standardize error handling)
            // Bu throw olmadan: Calling code exception handle edemez
            throw new RuntimeException("Driver initialization failed", e);
        }
    }
    
    /**
     * ANDROID CAPABILITIES YARATMA METODİ
     * 
     * Method amacı: Android cihazlar için gerekli tüm Appium capabilities'lerini hazırlar
     * Parametreler: Parametre almaz, konfigürasyon dosyalarından bilgileri okur
     * Return değeri: Android'e özgü ayarlarla dolu DesiredCapabilities objesi
     * 
     * Kullanılmazsa etki:
     * - Android driver yaratılamaz (capabilities eksik)
     * - UiAutomator2 backend başlatılamaz
     * - APK install edilemez
     * - Permissions otomatik verilemez
     * - Timeout ayarları yanlış olur (test instability)
     * 
     * Diğer metodlarla kıyasla:
     * - getIOSCapabilities()'den farkı: Android-specific ayarlar (UiAutomator2, APK, systemPort)
     * - initializeDriver()'dan çağrılır (delegation pattern)
     * - Private method: Sadece bu class içinde kullanılır
     * - Specialized factory method: Android-only concerns
     * 
     * Çağrıldığı yerler:
     * - initializeDriver() method'unda Android case'inde
     * - Sadece Android platform seçildiğinde çalışır
     * 
     * Bağımlılıkları:
     * - ConfigurationManager.FrameworkConfig (genel ayarlar)
     * - ConfigurationManager.AndroidConfig (Android-specific ayarlar)
     * - APK file existence (file system dependency)
     */
    private static DesiredCapabilities getAndroidCapabilities() {
        // Android capabilities setup başlangıç log'u
        // Bu log olmadan: Android setup süreçleri takip edilemez
        logger.info("Setting up Android capabilities");
        
        // Boş capabilities objesi yarat (Appium'a gönderilecek ayarlar container'ı)
        // DesiredCapabilities: Key-value map structure (String key, Object value)
        // Bu obje olmadan: Appium'a hiçbir ayar gönderilemez
        DesiredCapabilities capabilities = new DesiredCapabilities();
        
        // Konfigürasyon objelerini al (file-based configuration reading)
        // frameworkConfig: Genel framework ayarları (platform, device, timeouts)
        // androidConfig: Android'e özgü ayarlar (automation, permissions, ports)
        // Bu objeler olmadan: Konfigürasyon bilgileri okunamaz, hard-coded değerler kullanılır
        ConfigurationManager.FrameworkConfig frameworkConfig = ConfigurationManager.getFrameworkConfig();
        ConfigurationManager.AndroidConfig androidConfig = ConfigurationManager.getAndroidConfig();
        
        // PLATFORM CAPABILITIES: Temel platform tanım ayarları
        
        // Platform adı: Appium'a Android olduğunu söyler
        // Bu satır olmadan: Appium hangi platform için çalışacağını bilmez
        capabilities.setCapability("platformName", "Android");
        
        // Android version: Cihazın Android sürümü (örn: "11.0", "12.0")
        // Bu satır olmadan: Version-specific features çalışmaz
        capabilities.setCapability("platformVersion", frameworkConfig.getPlatformVersion());
        
        // Cihaz adı: Test edilecek cihaz/emulator adı
        // Bu satır olmadan: Appium hangi cihazı kullanacağını bilmez
        capabilities.setCapability("deviceName", frameworkConfig.getDeviceName());
        
        // Automation engine: UiAutomator2 (modern Android automation)
        // Bu satır olmadan: Eski UiAutomator1 kullanılır (deprecated, instable)
        capabilities.setCapability("automationName", androidConfig.getAutomationName());
        
        // DEVICE UDID: Spesifik cihaz ID'si (real device için gerekli)
        // Null check: UDID boş ise set etme (emulator için optional)
        if (frameworkConfig.getDeviceUdid() != null && !frameworkConfig.getDeviceUdid().isEmpty()) {
            // UDID set et: Specific device targeting için
            // Bu capability olmadan: Multiple device durumunda wrong device seçilebilir
            capabilities.setCapability("udid", frameworkConfig.getDeviceUdid());
        }
        
        // APP INSTALLATION CAPABILITIES: APK file handling
        
        // APK dosya path'ini configuration'dan al
        // Bu path: src/test/resources/apps/Hepsiburada.apk gibi bir path
        String appPath = frameworkConfig.getAppPath();
        
        // APK dosyasının varlığını kontrol et
        // File object: Java file system access için
        File appFile = new File(appPath);
        
        if (appFile.exists()) {
            // APK dosyası varsa: Dosya path'ini capability'ye set et
            // getAbsolutePath(): Relative path'i absolute'e çevir (Appium requirement)
            // Bu satır olmadan: APK install edilemez, app açılmaz
            capabilities.setCapability("app", appFile.getAbsolutePath());
        } else {
            // APK dosyası yoksa: Package ve Activity ile çalışmaya çalış
            // Bu durum: APK zaten cihazda yüklü ise kullanılır
            logger.warn("App file not found at: {}. Using app package and activity instead.", appPath);
            
            // App package name: com.hepsiburada.ecommerce gibi
            // Bu satır olmadan: Hangi app'i açacağı belli olmaz
            capabilities.setCapability("appPackage", androidConfig.getAppPackage());
            
            // App activity: Ana activity class adı (entry point)
            // Bu satır olmadan: App açılmaz, hangi activity'den başlayacağı belli olmaz
            capabilities.setCapability("appActivity", androidConfig.getAppActivity());
        }
        
        // ANDROID-SPECIFIC CAPABILITIES: Platform'a özgü ayarlar
        
        // Permissions auto-grant: İzinleri otomatik ver (location, camera, vs.)
        // Bu capability olmadan: Test sırasında permission dialog'ları çıkar, testler kırılır
        capabilities.setCapability("autoGrantPermissions", androidConfig.isAutoGrantPermissions());
        
        // No reset: App data'sını sıfırlama (session state korunur)
        // Bu capability olmadan: Her test temiz slate'le başlar (sometimes desired)
        capabilities.setCapability("noReset", androidConfig.isNoReset());
        
        // Full reset: Complete app uninstall/reinstall
        // Bu capability olmadan: App state contamination oluşabilir
        capabilities.setCapability("fullReset", androidConfig.isFullReset());
        
        // System port: UiAutomator2 server için özel port
        // Bu capability olmadan: Default port conflicts (parallel execution'da)
        capabilities.setCapability("systemPort", androidConfig.getSystemPort());
        
        // STABILITY CAPABILITIES: Test reliability için timeout ayarları
        
        // Command timeout: Driver command'ların max beklenme süresi (5 dakika)
        // Bu timeout olmadan: Stuck commands forever wait, resource leak
        capabilities.setCapability("newCommandTimeout", 300);
        
        // APK install timeout: App installation için max süre (90 saniye)
        // Bu timeout olmadan: Slow installs fail, large APKs timeout
        capabilities.setCapability("androidInstallTimeout", 90000);
        
        // ADB command timeout: Android Debug Bridge operations için
        // Bu timeout olmadan: ADB commands hang, automation stuck
        capabilities.setCapability("adbExecTimeout", 20000);
        
        // UiAutomator2 server startup timeout: Automation backend başlatma süresi
        // Bu timeout olmadan: Slow devices'larda server startup fail
        capabilities.setCapability("uiautomator2ServerLaunchTimeout", 60000);
        
        // PERFORMANCE CAPABILITIES: Optimization ayarları
        
        // Device initialization skip: Bazı device setup adımlarını atla (false = full setup)
        // Bu setting olmadan: Incomplete device setup, intermittent failures
        capabilities.setCapability("skipDeviceInitialization", false);
        
        // Server installation skip: UiAutomator2 server'ı force reinstall
        // Bu setting olmadan: Stale server components, weird behaviors
        capabilities.setCapability("skipServerInstallation", false);
        
        // Hidden API policy error ignore: Android 9+ hidden API restrictions
        // Bu setting olmadan: Android 9+ cihazlarda automation failures
        capabilities.setCapability("ignoreHiddenApiPolicyError", true);
        
        // Android capabilities başarılı setup completion log'u
        // Bu log olmadan: Capabilities setup'ının başarılı olup olmadığı belli olmaz
        logger.info("Android capabilities configured successfully");
        
        // Hazırlanan capabilities objesini return et
        // Bu return olmadan: initializeDriver() method capabilities alamaz
        return capabilities;
    }
    
    /**
     * iOS CAPABILITIES YARATMA METODİ
     * 
     * Method amacı: iOS cihazlar (simulator/real device) için gerekli tüm Appium capabilities'lerini hazırlar
     * Parametreler: Parametre almaz, konfigürasyon dosyalarından bilgileri okur
     * Return değeri: iOS'e özgü ayarlarla dolu DesiredCapabilities objesi
     * 
     * Kullanılmazsa etki:
     * - iOS driver yaratılamaz (capabilities eksik)
     * - XCUITest backend başlatılamaz
     * - IPA install edilemez
     * - WebDriverAgent başlatılamaz
     * - iOS specific features çalışmaz (simulator/real device handling)
     * 
     * Diğer metodlarla kıyasla:
     * - getAndroidCapabilities()'den farkı: iOS-specific ayarlar (XCUITest, bundleId, WDA ports)
     * - initializeDriver()'dan çağrılır (delegation pattern)
     * - Private method: Sadece bu class içinde kullanılır
     * - Specialized factory method: iOS-only concerns
     * 
     * Çağrıldığı yerler:
     * - initializeDriver() method'unda iOS case'inde
     * - Sadece iOS platform seçildiğinde çalışır
     * 
     * Bağımlılıkları:
     * - ConfigurationManager.FrameworkConfig (genel ayarlar)
     * - ConfigurationManager.IOSConfig (iOS-specific ayarlar)
     * - IPA file existence (file system dependency)
     * - WebDriverAgent availability (Xcode tools)
     */
    private static DesiredCapabilities getIOSCapabilities() {
        // iOS capabilities setup başlangıç log'u
        // Bu log olmadan: iOS setup süreçleri takip edilemez
        logger.info("Setting up iOS capabilities");
        
        // Boş capabilities objesi yarat (Appium'a gönderilecek ayarlar container'ı)
        // DesiredCapabilities: Key-value map structure (iOS-specific configurations için)
        // Bu obje olmadan: Appium'a hiçbir iOS ayarı gönderilemez
        DesiredCapabilities capabilities = new DesiredCapabilities();
        
        // Konfigürasyon objelerini al (file-based configuration reading)
        // frameworkConfig: Genel framework ayarları (platform, device, timeouts)
        // iosConfig: iOS'e özgü ayarlar (simulator, bundle ID, WDA ports)
        // Bu objeler olmadan: iOS konfigürasyonu okunamaz, hard-coded değerler kullanılır
        ConfigurationManager.FrameworkConfig frameworkConfig = ConfigurationManager.getFrameworkConfig();
        ConfigurationManager.IOSConfig iosConfig = ConfigurationManager.getIOSConfig();
        
        // PLATFORM CAPABILITIES: Temel platform tanım ayarları
        
        // Platform adı: Appium'a iOS olduğunu söyler
        // Bu satır olmadan: Appium hangi platform için çalışacağını bilmez
        capabilities.setCapability("platformName", "iOS");
        
        // iOS version: Cihazın/simulator'ın iOS sürümü (örn: "15.0", "16.0")
        // Bu satır olmadan: Version-specific features çalışmaz
        capabilities.setCapability("platformVersion", frameworkConfig.getPlatformVersion());
        
        // Cihaz adı: Test edilecek cihaz/simulator adı (iPhone 14, iPad Pro vs.)
        // Bu satır olmadan: Appium hangi cihazı kullanacağını bilmez
        capabilities.setCapability("deviceName", iosConfig.getSimulatorName());
        
        // Automation engine: XCUITest (modern iOS automation framework)
        // Bu satır olmadan: Eski UIAutomation kullanılır (deprecated, iOS 10+ desteklemez)
        capabilities.setCapability("automationName", iosConfig.getAutomationName());
        
        // DEVICE UDID: Spesifik cihaz ID'si (real device için gerekli)
        // Null check: UDID boş ise set etme (simulator için optional)
        if (frameworkConfig.getDeviceUdid() != null && !frameworkConfig.getDeviceUdid().isEmpty()) {
            // UDID set et: Specific device targeting için
            // Bu capability olmadan: Multiple device durumunda wrong device seçilebilir
            capabilities.setCapability("udid", frameworkConfig.getDeviceUdid());
        }
        
        // APP INSTALLATION CAPABILITIES: IPA file handling
        
        // IPA dosya path'ini configuration'dan al
        // Bu path: src/test/resources/apps/Hepsiburada.ipa gibi bir path
        String appPath = frameworkConfig.getAppPath();
        
        // IPA dosyasının varlığını kontrol et
        // File object: Java file system access için
        File appFile = new File(appPath);
        
        if (appFile.exists()) {
            // IPA dosyası varsa: Dosya path'ini capability'ye set et
            // getAbsolutePath(): Relative path'i absolute'e çevir (Appium requirement)
            // Bu satır olmadan: IPA install edilemez, app açılmaz
            capabilities.setCapability("app", appFile.getAbsolutePath());
        } else {
            // IPA dosyası yoksa: Bundle ID ile çalışmaya çalış
            // Bu durum: App zaten cihazda yüklü ise kullanılır (com.hepsiburada.app gibi)
            // Bu satır olmadan: Hangi app'i açacağı belli olmaz
            capabilities.setCapability("bundleId", iosConfig.getBundleId());
        }
        
        // iOS-SPECIFIC CAPABILITIES: Platform'a özgü ayarlar
        
        // WebDriverAgent local port: WDA server için özel port (parallel execution için)
        // Bu capability olmadan: Port conflicts oluşur, multiple test runs fails
        capabilities.setCapability("wdaLocalPort", iosConfig.getWdaLocalPort());
        
        // Use new WDA: Yeni WebDriverAgent instance kullanma (false = existing reuse)
        // Bu capability false olmadan: Her test için yeni WDA, slow startup
        capabilities.setCapability("useNewWDA", false);
        
        // Reset on session start only: Session başında reset (not every command)
        // Bu capability olmadan: Her command'da unnecessary reset, performance hit
        capabilities.setCapability("resetOnSessionStartOnly", false);
        
        // STABILITY CAPABILITIES: iOS test reliability için özel timeout ayarları
        
        // Command timeout: Driver command'ların max beklenme süresi (5 dakika)
        // Bu timeout olmadan: Stuck commands forever wait, resource leak
        capabilities.setCapability("newCommandTimeout", 300);
        
        // WDA startup retries: WebDriverAgent başlatma retry sayısı
        // Bu retry olmadan: WDA startup fails permanently, no second chance
        capabilities.setCapability("wdaStartupRetries", 3);
        
        // WDA startup retry interval: Retry'lar arasında bekleme süresi (20 saniye)
        // Bu interval olmadan: Rapid retry attempts, resource contention
        capabilities.setCapability("wdaStartupRetryInterval", 20000);
        
        // iOS capabilities başarılı setup completion log'u
        // Bu log olmadan: Capabilities setup'ının başarılı olup olmadığı belli olmaz
        logger.info("iOS capabilities configured successfully");
        
        // Hazırlanan capabilities objesini return et
        // Bu return olmadan: initializeDriver() method capabilities alamaz
        return capabilities;
    }
    
    /**
     * DRIVER TIMEOUT AYARLARI YAPILANDIRMA METODİ
     * 
     * Method amacı: AppiumDriver instance'ı için tüm timeout ayarlarını konfigüre eder
     * Parametreler: driver - Timeout ayarları uygulanacak AppiumDriver instance'ı
     * Return değeri: Void (driver objesini direkt modify eder)
     * 
     * Kullanılmazsa etki:
     * - Default timeout değerleri kullanılır (çok kısa, testlerde failures)
     * - Implicit wait yok = immediate NoSuchElement exceptions
     * - Page load timeout yok = hybrid app'lerde hangs
     * - Script timeout yok = JavaScript executions fail
     * - Test stability büyük ölçüde azalır
     * 
     * Diğer metodlarla kıyasla:
     * - initializeDriver()'dan çağrılır (timeout setup son adım)
     * - Helper method: Specific responsibility (timeout configuration)
     * - Private method: Internal implementation detail
     * - ConfigurationManager'e bağımlı (externalized timeout values)
     * 
     * Çağrıldığı yerler:
     * - initializeDriver() method'unda driver yaratıldıktan sonra
     * - Her platform için (Android ve iOS) çalışır
     * 
     * Bağımlılıkları:
     * - ConfigurationManager.FrameworkConfig (timeout values)
     * - AppiumDriver API (timeout management interface)
     * - Duration API (time units conversion)
     */
    private static void configureDriverTimeouts(AppiumDriver driver) {
        // Driver timeout configuration başlangıç log'u
        // Bu log olmadan: Timeout setup process'i takip edilemez
        logger.info("Configuring driver timeouts");
        
        // Framework konfigürasyon objesini al (timeout değerleri için)
        // Bu obje olmadan: Timeout değerleri hard-coded olur, flexibility kaybolur
        ConfigurationManager.FrameworkConfig config = ConfigurationManager.getFrameworkConfig();
        
        // IMPLICIT WAIT TIMEOUT: Element arama için otomatik bekleme süresi
        // Bu timeout olmadan: Element bulunamadığında immediate NoSuchElementException
        // Implicit wait: Tüm findElement() calls için global waiting strategy
        // Duration.ofSeconds(): Saniye cinsinden timeout'u Duration object'e çevir
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitTimeout()));
        
        // PAGE LOAD TIMEOUT: Hybrid app'lerde sayfa yüklenme max bekleme süresi
        // Bu timeout olmadan: Slow loading pages forever wait, testler stuck
        // Hybrid apps: Native + WebView content (HTML sayfaları için kritik)
        // Duration conversion: Config'den gelen int değeri Duration'a çevir
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getExplicitTimeout()));
        
        // SCRIPT TIMEOUT: JavaScript execution için max bekleme süresi
        // Bu timeout olmadan: Long running JS scripts hang forever
        // JavaScript execution: executeScript() API calls için (async JS operations)
        // Duration object: Java 8+ time API standardı (old ms-based APIs deprecated)
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(config.getExplicitTimeout()));
        
        // Driver timeouts başarılı configuration completion log'u
        // Bu log olmadan: Timeout setup'ının başarılı olup olmadığı belli olmaz
        logger.info("Driver timeouts configured successfully");
    }
    
    /**
     * THREADLOCAL'DAN MEVCUT DRIVER INSTANCE'INI ALMA METODİ
     * 
     * Method amacı: Mevcut thread için initialize edilmiş AppiumDriver instance'ını return eder
     * Parametreler: Parametre almaz (thread-specific storage'dan okur)
     * Return değeri: Mevcut thread'e ait AppiumDriver instance'ı
     * 
     * Kullanılmazsa etki:
     * - Test sınıfları ve page object'ler driver instance'ına erişemez
     * - Mobil element operations yapılamaz (click, type, find vs.)
     * - Test automation completely breaks (no driver = no automation)
     * - Framework'ün temel fonksiyonalitesi çalışmaz
     * 
     * Diğer metodlarla kıyasla:
     * - initializeDriver(): O method driver yaratır, bu method yaratılanı return eder
     * - isDriverInitialized(): Bu method exception fırlatır, o method boolean döner
     * - Accessor method: Data retrieval only, no side effects
     * - Public API: Framework'ün external interface'inin bir parçası
     * - Most frequently called method: Her driver operation öncesi çağrılır
     * 
     * Çağrıldığı yerler:
     * - Page object classes (element interactions için)
     * - Test step definitions (WebDriver operations için)
     * - BaseTest class (driver state checks için)
     * - Utility methods (screenshot, video recording)
     * - Assertion helpers (element validations)
     * 
     * Bağımlılıkları:
     * - driverThreadLocal: ThreadLocal storage (thread safety için)
     * - initializeDriver(): Driver instance yaratılmış olmalı
     * - Current thread context: Thread-specific data access
     */
    public static AppiumDriver getDriver() {
        // ThreadLocal storage'dan mevcut thread'in driver'ını al
        // ThreadLocal.get(): Thread-specific value retrieval
        // Bu satır olmadan: Driver instance alınamaz, null pointer exceptions
        AppiumDriver driver = driverThreadLocal.get();
        
        // Driver null check: Initialize edilmemiş durumda error handling
        if (driver == null) {
            // Driver not initialized error log (critical error için error level)
            // Bu log olmadan: Null driver hatası root cause'u belli olmaz
            logger.error("Driver is not initialized. Call initializeDriver() first.");
            
            // IllegalStateException fırlat: Driver initialization dependency'si eksik
            // RuntimeException subclass: Method signature'da throws declaration gerektirmez
            // Bu exception olmadan: Null driver'la operation attempt, NullPointerException
            throw new IllegalStateException("Driver is not initialized. Call initializeDriver() first.");
        }
        
        // Validate edilmiş driver instance'ını return et
        // Bu return olmadan: Calling code driver'a erişemez
        return driver;
    }
    
    /**
     * DRIVER INITIALIZE DURUMU KONTROL METODİ
     * 
     * Method amacı: Mevcut thread için driver'ın initialize edilip edilmediğini safe check yapar
     * Parametreler: Parametre almaz (thread-specific storage kontrol eder)
     * Return değeri: boolean - true: driver mevcut, false: driver yok
     * 
     * Kullanılmazsa etki:
     * - Unsafe driver access attempts (getDriver() calls without checking)
     * - IllegalStateException'lar handling edilemez
     * - Conditional driver operations yapılamaz
     * - Test teardown/cleanup logic'leri düzgün çalışmaz
     * 
     * Diğer metodlarla kıyasla:
     * - getDriver(): Exception fırlatır vs. boolean döner (safe checking)
     * - quitDriver(): Bu method check yapar, o method cleanup yapar
     * - Guard method: Defensive programming pattern
     * - Non-throwing: Exception-safe driver state checking
     * - Lightweight: Minimal overhead checking
     * 
     * Çağrıldığı yerler:
     * - Test teardown hooks (cleanup gerekip gerekmediği check)
     * - BaseTest.tearDown() (conditional driver quit)
     * - Retry logic (driver state validation)
     * - Error handling scenarios (recovery attempts)
     * - Conditional test flows (driver-dependent operations)
     * 
     * Bağımlılıkları:
     * - driverThreadLocal: ThreadLocal storage (thread safety için)
     * - Current thread context: Thread-specific data access
     */
    public static boolean isDriverInitialized() {
        // ThreadLocal'dan driver'ı al ve null check yap
        // != null: Driver instance mevcut mu kontrolü
        // Bu return olmadan: Driver durumu check edilemez, unsafe operations
        return driverThreadLocal.get() != null;
    }
    
    /**
     * DRIVER SONLANDIRMA VE TEMIZLEME METODİ
     * 
     * Method amacı: Mevcut thread'in driver instance'ını güvenli şekilde sonlandırır ve temizler
     * Parametreler: Parametre almaz (thread-specific driver'ı operate eder)
     * Return değeri: Void (cleanup operation, side effects var)
     * 
     * Kullanılmazsa etki:
     * - Memory leaks (driver sessions sonlanmaz)
     * - Resource leaks (WebDriverAgent/UiAutomator2 processes zombie kalır)
     * - Port conflicts (next test runs fail, ports busy)
     * - Device state contamination (app state, permissions)
     * - CI/CD pipeline'ları crash (resource exhaustion)
     * 
     * Diğer metodlarla kıyasla:
     * - initializeDriver(): Opposite operation (create vs. destroy)
     * - getDriver(): Bu method destroy yapar, o method retrieve yapar
     * - resetApp(): App restart vs. complete driver quit
     * - Cleanup method: Resource management pattern
     * - Exception-safe: Finally block garantisi ile cleanup
     * 
     * Çağrıldığı yerler:
     * - Test teardown hooks (@After, @AfterMethod)
     * - BaseTest.tearDown() methods
     * - Exception handling scenarios (error recovery)
     * - Test suite finalization
     * - CI/CD cleanup phases
     * 
     * Bağımlılıkları:
     * - driverThreadLocal: ThreadLocal storage (thread safety için)
     * - AppiumDriver.quit(): Driver session termination API
     * - Exception handling: Graceful cleanup guarantee
     */
    public static void quitDriver() {
        // ThreadLocal'dan driver instance'ını al (null olabilir)
        // Direct access: isDriverInitialized() çağırmadan efficiency için
        AppiumDriver driver = driverThreadLocal.get();
        
        // Driver null check: Sadece mevcut driver'ları quit et
        if (driver != null) {
            // Try-catch-finally block: Exception-safe cleanup guarantee
            try {
                // Driver quit operation başlangıç log'u
                // Bu log olmadan: Quit operation'ları track edilemez
                logger.info("Quitting driver session");
                
                // AppiumDriver quit: Session sonlandırma (server'a quit command)
                // Bu satır olmadan: Driver session active kalır, resource leak
                driver.quit();
                
                // Successful quit operation completion log'u
                // Bu log olmadan: Quit success/failure belli olmaz
                logger.info("Driver session quit successfully");
                
            } catch (Exception e) {
                // Quit operation exception handling (network errors, server issues)
                // Exception yakalama: Cleanup process'i kesintiye uğratmamak için
                // Bu catch olmadan: Exception durumunda ThreadLocal cleanup skip
                logger.error("Error occurred while quitting driver", e);
                
            } finally {
                // ThreadLocal cleanup: Driver reference'ını thread'den temizle
                // Finally block: Exception durumunda da çalışır guarantee
                // Bu satır olmadan: Thread'de stale driver reference kalır, memory leak
                driverThreadLocal.remove();
                
                // ThreadLocal cleanup completion log'u
                // Bu log olmadan: Memory cleanup'ının yapılıp yapılmadığı belli olmaz
                logger.info("Driver removed from ThreadLocal");
            }
        } else {
            // No driver warning: Already quit veya hiç initialize edilmemiş
            // Warning level: Error değil ama abnormal durum
            // Bu log olmadan: Unnecessary quit calls silent kalır
            logger.warn("No driver instance found to quit");
        }
    }
    
    /**
     * UYGULAMA SIFIRLAMA METODİ (APP RESTART EQUİVALENTİ)
     * 
     * Method amacı: Mevcut app'i tamamen restart eder (force close + fresh start)
     * Parametreler: Parametre almaz (mevcut driver instance'ı kullanır)
     * Return değeri: Void (app state reset operation)
     * 
     * Kullanılmazsa etki:
     * - Test scenario'ları arasında app state contamination
     * - Previous test data app memory'sinde kalır
     * - Clean slate testing yapılamaz
     * - Flaky tests (intermittent failures due to state)
     * - Test isolation principles violation
     * 
     * Diğer metodlarla kıyasla:
     * - quitDriver(): Complete driver destruction vs. app restart only
     * - backgroundApp(): Temporary hide vs. complete restart
     * - Driver reset değil, app reset (session continues)
     * - State cleanup: App-level vs. driver-level
     * - Faster than quit + reinitialize driver
     * 
     * Çağrıldığı yerler:
     * - Test data cleanup scenarios
     * - Between test methods (state isolation)
     * - Error recovery (corrupt app state)
     * - Fresh start scenarios (onboarding tests)
     * - Performance test preparations
     * 
     * Bağımlılıkları:
     * - getDriver(): Active driver session gerekli
     * - AppiumDriver.resetApp(): App restart API
     * - App must support restart (some apps crash on reset)
     */
    public static void resetApp() {
        // Mevcut driver instance'ını al (null check getDriver() içinde)
        // getDriver(): Driver validation + retrieval (IllegalStateException if null)
        AppiumDriver driver = getDriver();
        
        // Try-catch block: Reset operation exception handling
        try {
            // App reset operation başlangıç log'u
            // Bu log olmadan: Reset operations tracking yapılamaz
            logger.info("Resetting application");
            
            // AppiumDriver resetApp: App'i force close + fresh restart
            // Bu satır olmadan: App state contaminated kalır, test isolation broken
            driver.resetApp();
            
            // Successful reset operation completion log'u
            // Bu log olmadan: Reset success/failure belli olmaz
            logger.info("Application reset successfully");
            
        } catch (Exception e) {
            // Reset operation exception handling (app crash, platform issues)
            // Error log: Detailed exception information for debugging
            // Bu error log olmadan: Reset failures root cause analysis yapılamaz
            logger.error("Error occurred while resetting application", e);
            
            // RuntimeException wrap: Standardize exception handling
            // Bu throw olmadan: Calling code reset failure'ından habersiz kalır
            throw new RuntimeException("Failed to reset application", e);
        }
    }
    
    /**
     * UYGULAMAYI BACKGROUND'A ALMA METODİ
     * 
     * Method amacı: App'i belirtilen süre boyunca background'a alır, sonra foreground'a getirir
     * Parametreler: duration - Background'da kalma süresi (saniye cinsinden)
     * Return değeri: Void (background/foreground operation)
     * 
     * Kullanılmazsa etki:
     * - Background/foreground transition testleri yapılamaz
     * - App lifecycle testing eksik kalır
     * - Background data refresh scenarios test edilemez
     * - Memory management tests yapılamaz
     * - Real-world usage patterns simulate edilemez
     * 
     * Diğer metodlarla kıyasla:
     * - resetApp(): Complete restart vs. temporary background
     * - quitDriver(): Session destroy vs. temporary hide
     * - Non-destructive: App state korunur
     * - Lifecycle testing: App suspend/resume cycle
     * - Real user behavior simulation
     * 
     * Çağrıldığı yerler:
     * - Background refresh testing
     * - App lifecycle validation
     * - Memory management tests
     * - Performance degradation tests
     * - Real-world scenario simulations
     * 
     * Bağımlılıkları:
     * - getDriver(): Active driver session gerekli
     * - AppiumDriver.runAppInBackground(): Background operation API
     * - Duration API: Time unit conversion
     * - Platform background policies (Android/iOS differences)
     */
    public static void backgroundApp(int duration) {
        // Mevcut driver instance'ını al (validation getDriver() içinde)
        // getDriver(): Driver null check + retrieval (exception if not initialized)
        AppiumDriver driver = getDriver();
        
        // Try-catch block: Background operation exception handling
        try {
            // Background operation başlangıç log'u (duration bilgisi ile)
            // {} placeholder: SLF4J parameterized logging (performance efficient)
            // Bu log olmadan: Background operations monitoring yapılamaz
            logger.info("Putting application in background for {} seconds", duration);
            
            // AppiumDriver runAppInBackground: App'i specified duration boyunca background'a al
            // Duration.ofSeconds(): int seconds'u Duration object'e çevir (Java 8+ Time API)
            // Bu satır olmadan: Background transition test edilemez, app foreground'da kalır
            driver.runAppInBackground(Duration.ofSeconds(duration));
            
            // Background operation completion log'u (app foreground'a döndü)
            // Bu log olmadan: Background/foreground cycle completion tracking yapılamaz
            logger.info("Application returned from background");
            
        } catch (Exception e) {
            // Background operation exception handling (platform errors, timing issues)
            // Error logging: Detailed exception information for troubleshooting
            // Bu error log olmadan: Background failures root cause belli olmaz
            logger.error("Error occurred while backgrounding application", e);
            
            // RuntimeException wrap: Standardized exception handling pattern
            // Bu throw olmadan: Calling code background failure'ından habersiz kalır
            throw new RuntimeException("Failed to background application", e);
        }
    }
    
    /**
     * MEVCUT DRIVER'IN PLATFORM ADI ALMA METODİ
     * 
     * Method amacı: Aktif driver'ın hangi platform için çalıştığını return eder
     * Parametreler: Parametre almaz (mevcut driver'ın capabilities'lerini okur)
     * Return değeri: String - Platform adı ("Android" veya "iOS")
     * 
     * Kullanılmazsa etki:
     * - Platform-specific test logic yazılamaz
     * - Conditional assertions yapılamaz
     * - Cross-platform test reports eksik kalır
     * - Platform-aware error handling yapılamaz
     * - Dynamic test behavior adjustment imkansız
     * 
     * Diğer metodlarla kıyasla:
     * - getDeviceName(): Device name vs. platform name
     * - Static ConfigurationManager calls vs. runtime driver state
     * - Metadata retrieval: Driver state inspection
     * - Runtime information: Actual platform vs. configured platform
     * - Validation helper: Test logic'de platform check için
     * 
     * Çağrıldığı yerler:
     * - Platform-specific test assertions
     * - Conditional test flows (if Android then... else...)
     * - Error messages (platform context için)
     * - Test reporting (platform information)
     * - Debug logging (platform identification)
     * 
     * Bağımlılıkları:
     * - getDriver(): Active driver session gerekli
     * - AppiumDriver.getCapabilities(): Driver metadata API
     * - "platformName" capability: Appium standard capability
     */
    public static String getPlatformName() {
        // Mevcut driver instance'ını al (validation getDriver() içinde)
        // getDriver(): Driver null check + retrieval (exception if not initialized)
        AppiumDriver driver = getDriver();
        
        // Driver capabilities'lerinden platform name'i al ve string'e çevir
        // getCapabilities(): Driver'ın initialization sırasında set edilen capabilities
        // getCapability("platformName"): Appium standard capability key
        // toString(): Object'i string'e çevir (capability value'lar Object type)
        // Bu return olmadan: Platform-specific test logic yapılamaz
        return driver.getCapabilities().getCapability("platformName").toString();
    }
    
    /**
     * MEVCUT DRIVER'IN CİHAZ ADI ALMA METODİ
     * 
     * Method amacı: Aktif driver'ın hangi cihaz/emulator üzerinde çalıştığını return eder
     * Parametreler: Parametre almaz (mevcut driver'ın capabilities'lerini okur)
     * Return değeri: String - Cihaz adı ("Samsung Galaxy S21", "iPhone 14", "Android Emulator", vs.)
     * 
     * Kullanılmazsa etki:
     * - Device-specific test reports eksik bilgi
     * - Debug sırasında hangi cihazda çalıştığı belli olmaz
     * - Device-aware test logic yazılamaz
     * - Performance test results context'siz kalır
     * - Parallel execution'da device tracking yapılamaz
     * 
     * Diğer metodlarla kıyasla:
     * - getPlatformName(): Platform vs. device (Android vs. Samsung Galaxy)
     * - Static configuration vs. runtime driver state
     * - Device identification: Actual device vs. configured device
     * - Metadata helper: Device context information
     * - Runtime inspection: Driver capabilities query
     * 
     * Çağrıldığı yerler:
     * - Test reports (device information için)
     * - Debug logging (device context identification)
     * - Device-specific test logic (screen size, capabilities vs.)
     * - Performance measurements (device-aware metrics)
     * - Parallel execution monitoring (device assignment tracking)
     * 
     * Bağımlılıkları:
     * - getDriver(): Active driver session gerekli
     * - AppiumDriver.getCapabilities(): Driver metadata API
     * - "deviceName" capability: Appium standard capability
     * - Device configuration: Framework/platform configs
     */
    public static String getDeviceName() {
        // Mevcut driver instance'ını al (validation getDriver() içinde)
        // getDriver(): Driver null check + retrieval (exception if not initialized)
        AppiumDriver driver = getDriver();
        
        // Driver capabilities'lerinden device name'i al ve string'e çevir
        // getCapabilities(): Driver'ın initialization sırasında set edilen capabilities
        // getCapability("deviceName"): Appium standard capability key
        // toString(): Object'i string'e çevir (capability value'lar Object type)
        // Bu return olmadan: Device-specific test context bilgisi alınamaz
        return driver.getCapabilities().getCapability("deviceName").toString();
    }
}
}