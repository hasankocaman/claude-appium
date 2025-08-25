package com.hepsiburada.drivers;

// ConfigurationManager: Framework konfigürasyon ayarlarını okumak için kullanılır
// Bu import olmadan platform bilgisi ve diğer ayarlar okunamaz
import com.hepsiburada.config.ConfigurationManager;

// AppiumDriver: Mobil cihaz kontrolü için ana driver interface'i
// Bu import olmadan mobil uygulama testi yapılamaz
import io.appium.java_client.AppiumDriver;

// Log4j2 kütüphanesi: Uygulama loglarını kaydetmek için kullanılır
// Bu import'lar olmadan test süreçleri takip edilemez ve debug yapılamaz
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * DRIVER MANAGER CLASS - DRIVER YÖNETİM SINIFI
 * 
 * Bu class'ın projede rolü: 
 * - Appium driver'ının yaşam döngüsünü (lifecycle) yönetir
 * - DriverFactory class'ı ile test sınıfları arasında köprü görevi görür
 * - Test sınıflarının driver işlemlerini daha kolay yapabilmesi için basitleştirilmiş API sağlar
 * - Driver'ın başlatılması, durdurulması ve durumunun kontrol edilmesi işlemlerini yönetir
 * 
 * Kullanılmazsa etki: 
 * - Test sınıfları doğrudan DriverFactory ile çalışmak zorunda kalır (daha karmaşık)
 * - Driver yönetimi için tutarlı bir API olmaz
 * - Her test sınıfı kendi driver yönetim kodunu yazmak zorunda kalır
 * - Kodda tekrar (code duplication) olur
 * 
 * Diğer class'larla ilişkisi:
 * - DriverFactory: Gerçek driver yaratım işlemlerini yapar (has-a relationship)
 * - ConfigurationManager: Konfigürasyon bilgilerini alır (uses relationship)
 * - Test sınıfları: Bu class'ı kullanarak driver işlemleri yapar (used-by relationship)
 * - Page Object sınıfları: Driver instance'ını bu class üzerinden alır
 */
public final class DriverManager {
    
    // Logger instance: Bu class'ın tüm log kayıtlarını yönetir
    // Static final: Tüm metodlar tarafından kullanılır, değiştirilmez
    // DriverManager.class: Log kayıtlarında hangi class'tan geldiğini belirtir
    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    
    /**
     * Private constructor: Utility class'ın instance'ının yaratılmasını engeller
     * Bu constructor olmadan: Bu class'tan nesne yaratılabilir (istenmeyen durum)
     * Neden private: Bu class sadece static metodlar içerir, instance'a ihtiyaç yoktur
     * final class + private constructor: Immutable utility pattern'i sağlar
     */
    private DriverManager() {
        // Utility class should not be instantiated
        // Bu yorum: Geliştiricilere bu class'ın utility class olduğunu hatırlatır
    }
    
    /**
     * DRIVER BAŞLATMA METODİ - Konfigürasyona Dayalı
     * 
     * Method amacı: Framework konfigürasyon dosyasından platform bilgisini okuyarak driver'ı başlatır
     * Parametreler: Parametre almaz, platform bilgisini konfigürasyondan otomatik olarak okur
     * Return değeri: Başlatılan AppiumDriver instance'ı döndürür (Android veya iOS driver)
     * 
     * Kullanılmazsa etki: 
     * - Testler platform bilgisini manuel olarak vermek zorunda kalır
     * - Konfigürasyon-driven test execution imkanı olmaz
     * - Her test için platform parametresi geçmek gerekir
     * 
     * Diğer metodlarla kıyasla: 
     * - initializeDriver(String platform)'dan farkı: Platform parametresi gerektirmez
     * - Daha kolay kullanım sağlar çünkü konfigürasyondan otomatik okur
     * - Test sınıflarının platform bilgisiyle uğraşmasını engeller
     * 
     * Çağrıldığı yerler: Test hooks, BaseTest class'ı, test setup metodları
     * Bağımlılıkları: ConfigurationManager (konfigürasyon okuma), DriverFactory (driver yaratma)
     */
    public static AppiumDriver initializeDriver() {
        // Platform bilgisini konfigürasyon dosyasından al
        // Bu satır olmadan: Hangi platform için driver yaratacağımız bilinmez
        String platform = ConfigurationManager.getFrameworkConfig().getPlatformName();
        
        // Driver başlatma işlemini logla (debugging ve monitoring için kritik)
        // Bu satır olmadan: Driver başlatma süreçleri takip edilemez
        logger.info("Initializing driver for platform: {}", platform);
        
        // Gerçek driver yaratma işlemini DriverFactory'e devret
        // Bu satır olmadan: Driver instance'ı yaratılmaz, testler başlamaz
        return DriverFactory.initializeDriver(platform);
    }
    
    /**
     * DRIVER BAŞLATMA METODİ - Platform Parametreli
     * 
     * Method amacı: Belirtilen platform için driver'ı başlatır (Android/iOS)
     * Parametreler: platform - Hedef platform adı ("Android" veya "iOS")
     * Return değeri: Belirtilen platform için başlatılan AppiumDriver instance'ı
     * 
     * Kullanılmazsa etki:
     * - Dinamik platform seçimi yapılamaz 
     * - Runtime'da farklı platformlar için test çalıştırılamaz
     * - CI/CD pipeline'larında platform değiştirme esnekliği olmaz
     * 
     * Diğer metodlarla kıyasla:
     * - Parametresiz initializeDriver()'dan farkı: Platform'u manuel olarak belirtir
     * - Daha esnek kullanım sağlar (runtime platform seçimi)
     * - Test data-driven scenarios için uygun
     * 
     * Çağrıldığı yerler: Parametreli testler, cross-platform test runners, CI/CD scripts
     * Bağımlılıkları: DriverFactory (driver yaratma işlemi)
     */
    public static AppiumDriver initializeDriver(String platform) {
        // Platform bilgisini logla (hangi platform için çalıştığını takip etmek için)
        // Bu satır olmadan: Hangi platform için driver yaratıldığı bilinmez
        logger.info("Initializing driver for platform: {}", platform);
        
        // Platform parametresini DriverFactory'e geçirerek driver yarat
        // Bu satır olmadan: Driver yaratılmaz ve testler başlamaz
        return DriverFactory.initializeDriver(platform);
    }
    
    /**
     * GÜNCEL DRIVER INSTANCE ALMA METODİ
     * 
     * Method amacı: Şu anda aktif olan driver instance'ını döndürür
     * Parametreler: Parametre almaz, mevcut driver'ı döndürür
     * Return değeri: Aktif AppiumDriver instance'ı (null olabilir eğer initialize edilmemişse)
     * 
     * Kullanılmazsa etki:
     * - Page Object sınıfları driver'a erişemez
     * - Element etkileşimleri yapılamaz
     * - Test adımları çalışamaz
     * - Mobil uygulama kontrolü imkansız hale gelir
     * 
     * Diğer metodlarla kıyasla:
     * - isDriverInitialized()'dan farkı: Boolean değer değil, actual driver döndürür
     * - En sık kullanılan method (her element etkileşiminde gerekli)
     * - Thread-safe implementasyon sağlar (ThreadLocal kullanımı ile)
     * 
     * Çağrıldığı yerler: 
     * - Tüm Page Object sınıfları (element bulma, etkileşim)
     * - BasePage constructor'ı
     * - Utility methodlar (screenshot, gesture)
     * - Test assertion metodları
     * 
     * Bağımlılıkları: DriverFactory (ThreadLocal driver storage)
     */
    public static AppiumDriver getDriver() {
        // DriverFactory'den mevcut driver instance'ını al
        // Bu satır olmadan: Driver'a erişim imkansız, testler çalışamaz
        return DriverFactory.getDriver();
    }
    
    /**
     * DRIVER DURUMU KONTROL METODİ
     * 
     * Method amacı: Driver'ın başlatılıp başlatılmadığını kontrol eder
     * Parametreler: Parametre almaz, mevcut durum bilgisini döndürür
     * Return değeri: true = driver başlatılmış, false = driver başlatılmamış
     * 
     * Kullanılmazsa etki:
     * - Driver durumu bilinmeden işlem yapmaya çalışılır
     * - NullPointerException hataları alınır
     * - Güvenli driver kullanımı sağlanamaz
     * - Error handling yapılamaz
     * 
     * Diğer metodlarla kıyasla:
     * - getDriver()'dan farkı: Driver'ın kendisini değil durumunu döndürür
     * - Exception riski olmadan durum kontrolü yapar
     * - Guard clause'larda kullanılır (defensive programming)
     * 
     * Çağrıldığı yerler:
     * - Test teardown metodları (cleanup işlemlerinde)
     * - Error handling blokları
     * - Conditional driver operations
     * - Safety checks in utility methods
     * 
     * Bağımlılıkları: DriverFactory (driver state management)
     */
    public static boolean isDriverInitialized() {
        // DriverFactory'den driver'ın initialize durumunu kontrol et
        // Bu satır olmadan: Driver durumu bilinemez, hatalar oluşur
        return DriverFactory.isDriverInitialized();
    }
    
    /**
     * DRIVER SONLANDIRMA METODİ
     * 
     * Method amacı: Aktif driver session'ını güvenli bir şekilde sonlandırır
     * Parametreler: Parametre almaz, mevcut driver'ı sonlandırır
     * Return değeri: Void, herhangi bir değer döndürmez
     * 
     * Kullanılmazsa etki:
     * - Driver session'ları açık kalır (memory leak)
     * - Cihaz kaynakları serbest bırakılmaz
     * - Multiple session'lar çakışabilir
     * - Test cleanup tamamlanmaz
     * 
     * Diğer metodlarla kıyasla:
     * - resetApp()'den farkı: Uygulamayı resetlemez, driver'ı tamamen kapatır
     * - backgroundApp()'den farkı: Uygulamayı arka plana almaz, session'ı bitirir
     * - En kritik cleanup method'u (resource management için)
     * 
     * Çağrıldığı yerler:
     * - Test teardown hooks (@AfterMethod, @AfterClass)
     * - Test failure handling
     * - Cleanup utility methods
     * - CI/CD pipeline cleanup steps
     * 
     * Bağımlılıkları: DriverFactory (session management)
     */
    public static void quitDriver() {
        // Driver sonlandırma işlemini logla (resource cleanup tracking için)
        // Bu satır olmadan: Hangi driver'ların sonlandırıldığı takip edilemez
        logger.info("Quitting driver session");
        
        // DriverFactory'e driver sonlandırma komutunu gönder
        // Bu satır olmadan: Driver session açık kalır, memory leak oluşur
        DriverFactory.quitDriver();
    }
    
    /**
     * UYGULAMA RESET METODİ
     * 
     * Method amacı: Mobil uygulamayı tamamen resetler (fresh start durumuna getirir)
     * Parametreler: Parametre almaz, mevcut uygulamayı resetler
     * Return değeri: Void, herhangi bir değer döndürmez
     * 
     * Kullanılmazsa etki:
     * - Test data pollution oluşur (önceki testlerin etkisi kalır)
     * - Clean slate testing yapılamaz
     * - State-dependent test failures artar
     * - Test isolation sağlanamaz
     * 
     * Diğer metodlarla kıyasla:
     * - quitDriver()'dan farkı: Driver'ı kapatmaz, sadece uygulamayı resetler
     * - backgroundApp()'den farkı: Arka plana almaz, uygulamayı yeniden başlatır
     * - Test isolation için critical method
     * 
     * Çağrıldığı yerler:
     * - Test setup methods (clean start için)
     * - Between test scenarios
     * - Data cleanup operations
     * - State reset requirements
     * 
     * Bağımlılıkları: DriverFactory (app lifecycle management)
     */
    public static void resetApp() {
        // Uygulama reset işlemini logla (test isolation tracking için)
        // Bu satır olmadan: Hangi uygulamanın ne zaman reset edildiği bilinmez
        logger.info("Resetting application");
        
        // DriverFactory'e uygulama reset komutunu gönder
        // Bu satır olmadan: Uygulama reset olmaz, test data pollution oluşur
        DriverFactory.resetApp();
    }
    
    /**
     * UYGULAMA ARKA PLAN METODİ
     * 
     * Method amacı: Mobil uygulamayı belirtilen süre boyunca arka plana alır
     * Parametreler: duration - Arka planda kalacağı süre (saniye cinsinden)
     * Return değeri: Void, herhangi bir değer döndürmez
     * 
     * Kullanılmazsa etki:
     * - App lifecycle testleri yapılamaz
     * - Background behavior testing imkansız
     * - State preservation testleri çalışmaz
     * - Real-world usage scenarios simulate edilemez
     * 
     * Diğer metodlarla kıyasla:
     * - resetApp()'den farkı: Uygulamayı resetlemez, sadece background'a alır
     * - quitDriver()'dan farkı: Driver'ı kapatmaz, uygulama çalışır durumda kalır
     * - Behavioral testing için özel method
     * 
     * Çağrıldığı yerler:
     * - App lifecycle test scenarios
     * - Background/foreground state tests
     * - Performance testing (memory usage after background)
     * - Real-world usage simulation
     * 
     * Bağımlılıkları: DriverFactory (app state management)
     */
    public static void backgroundApp(int duration) {
        // Background işlemini ve süresini logla (behavior tracking için)
        // Bu satır olmadan: Hangi uygulamanın ne kadar süre background'da kaldığı bilinmez
        logger.info("Putting application in background for {} seconds", duration);
        
        // DriverFactory'e background komutunu ve süreyi gönder
        // Bu satır olmadan: Uygulama background'a alınmaz, lifecycle testleri çalışmaz
        DriverFactory.backgroundApp(duration);
    }
    
    /**
     * PLATFORM ADI ALMA METODİ
     * 
     * Method amacı: Şu anda çalışılan platform adını döndürür (Android/iOS)
     * Parametreler: Parametre almaz, mevcut platform bilgisini döndürür
     * Return değeri: String - Platform adı ("Android" veya "iOS")
     * 
     * Kullanılmazsa etki:
     * - Platform-specific code yazılamaz
     * - Conditional operations yapılamaz
     * - Cross-platform testing logic oluşturulamaz
     * - Platform-based reporting yapılamaz
     * 
     * Diğer metodlarla kıyasla:
     * - isAndroid()/isIOS()'dan farkı: Boolean değil String döndürür
     * - getDeviceName()'den farkı: Device değil platform bilgisi verir
     * - More generic information provider
     * 
     * Çağrıldığı yerler:
     * - Platform-specific test logic
     * - Reporting systems (platform-based grouping)
     * - Conditional element locators
     * - Cross-platform validation methods
     * 
     * Bağımlılıkları: DriverFactory (platform information storage)
     */
    public static String getPlatformName() {
        // DriverFactory'den platform bilgisini al ve döndür
        // Bu satır olmadan: Platform bilgisi alınamaz, platform-specific logic çalışmaz
        return DriverFactory.getPlatformName();
    }
    
    /**
     * CİHAZ ADI ALMA METODİ
     * 
     * Method amacı: Şu anda test edilen cihaz adını döndürür
     * Parametreler: Parametre almaz, mevcut cihaz bilgisini döndürür
     * Return değeri: String - Cihaz adı (örn: "Samsung Galaxy S21", "iPhone 14")
     * 
     * Kullanılmazsa etki:
     * - Device-specific reporting yapılamaz
     * - Test results cihaza göre gruplandırılamaz
     * - Device-based test logic oluşturulamaz
     * - Debugging'de cihaz bilgisi eksik kalır
     * 
     * Diğer metodlarla kıyasla:
     * - getPlatformName()'den farkı: Platform değil spesifik cihaz bilgisi
     * - isAndroid()/isIOS()'dan farkı: Boolean değil String döndürür
     * - More specific device information
     * 
     * Çağrıldığı yerler:
     * - Test reporting (device-based categorization)
     * - Debugging logs (cihaz bilgisi için)
     * - Device-specific test configurations
     * - Performance testing (cihaz bazlı comparison)
     * 
     * Bağımlılıkları: DriverFactory (device information management)
     */
    public static String getDeviceName() {
        // DriverFactory'den cihaz bilgisini al ve döndür
        // Bu satır olmadan: Cihaz bilgisi alınamaz, device-specific operations çalışmaz
        return DriverFactory.getDeviceName();
    }
    
    /**
     * ANDROID PLATFORM KONTROL METODİ
     * 
     * Method amacı: Şu anki platform'un Android olup olmadığını kontrol eder
     * Parametreler: Parametre almaz, mevcut platform bilgisini kontrol eder
     * Return değeri: Boolean - true (Android ise), false (Android değilse)
     * 
     * Kullanılmazsa etki:
     * - Android-specific code yazılamaz
     * - Platform-based conditional logic çalışmaz
     * - Android-only features kullanılamaz
     * - Cross-platform element handling yapılamaz
     * 
     * Diğer metodlarla kıyasla:
     * - isIOS()'dan farkı: iOS değil Android kontrolü yapar
     * - getPlatformName()'den farkı: String comparison değil boolean döndürür
     * - Daha pratik conditional usage için tasarlanmış
     * 
     * Çağrıldığı yerler:
     * - Android-specific element locators
     * - Platform-conditional test steps
     * - Android-only feature testing
     * - Timeout and wait strategies (Android vs iOS farklılıkları)
     * 
     * Bağımlılıkları: getPlatformName() method'u (platform bilgisi alma)
     */
    public static boolean isAndroid() {
        // Platform adını al ve Android olup olmadığını case-insensitive kontrol et
        // equalsIgnoreCase: "android", "ANDROID", "Android" gibi tüm varyasyonları kabul eder
        // Bu satır olmadan: Android platform kontrolü yapılamaz, Android-specific logic çalışmaz
        return "Android".equalsIgnoreCase(getPlatformName());
    }
    
    /**
     * iOS PLATFORM KONTROL METODİ
     * 
     * Method amacı: Şu anki platform'un iOS olup olmadığını kontrol eder
     * Parametreler: Parametre almaz, mevcut platform bilgisini kontrol eder
     * Return değeri: Boolean - true (iOS ise), false (iOS değilse)
     * 
     * Kullanılmazsa etki:
     * - iOS-specific code yazılamaz
     * - iOS-based conditional logic çalışmaz
     * - iOS-only features kullanılamaz
     * - Cross-platform element handling yapılamaz
     * 
     * Diğer metodlarla kıyasla:
     * - isAndroid()'dan farkı: Android değil iOS kontrolü yapar
     * - getPlatformName()'den farkı: String comparison değil boolean döndürür
     * - iOS-specific operations için optimized
     * 
     * Çağrıldığı yerler:
     * - iOS-specific element locators (XCUITest selectors)
     * - iOS-conditional test steps
     * - iOS-only feature testing (Face ID, Touch ID, etc.)
     * - iOS-specific gesture implementations
     * 
     * Bağımlılıkları: getPlatformName() method'u (platform bilgisi alma)
     */
    public static boolean isIOS() {
        // Platform adını al ve iOS olup olmadığını case-insensitive kontrol et
        // equalsIgnoreCase: "ios", "IOS", "iOS" gibi tüm varyasyonları kabul eder
        // Bu satır olmadan: iOS platform kontrolü yapılamaz, iOS-specific logic çalışmaz
        return "iOS".equalsIgnoreCase(getPlatformName());
    }
}