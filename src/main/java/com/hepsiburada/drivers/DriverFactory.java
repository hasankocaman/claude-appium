package com.hepsiburada.drivers;

// ConfigurationManager: Platform ve cihaz konfigürasyonlarını okumak için kullanılır
// Bu import olmadan Android/iOS ayarları ve APK yolu alınamaz
import com.google.common.collect.ImmutableMap;
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
 * Bu class'ın projede rolü: Mobil test otomasyonu için Appium driver'larını (AndroidDriver ve IOSDriver) yaratmayı, konfigüre etmeyi ve thread-safe bir şekilde yönetmeyi sağlar. Factory Design Pattern kullanarak platform-specific driver instance'larını oluşturur, merkezi konfigürasyon ve timeout yönetimi sunar, paralel test execution için kritik thread safety sağlar.
 * Kullanılmazsa etki: Her test sınıfı kendi driver yaratma logic'ini yazmak zorunda kalır, platform-specific ayarlar tekrarlanır (code duplication), thread safety problemi oluşur (paralel testlerde çakışmalar), merkezi yönetim eksikliği maintenance zorluğuna yol açar ve tutarlı timeout/capability ayarları uygulanamaz.
 * Diğer class'larla ilişkisi: DriverManager (facade pattern ile bu class'ı kullanır), ConfigurationManager (platform ve cihaz ayarlarını sağlar), Test class'ları (dolaylı olarak driver erişimi için) ve Page Objects (driver instance'larına bağımlıdır) ile sıkı entegrasyon içerir.
 */
public final class DriverFactory {

    // Logger instance: Bu class'ın tüm işlemlerini loglamak için
    // Amaç: Debugging, monitoring ve hata takibi için loglama sağlar
    // Veri tipi seçim nedeni: Logger interface'i, Log4j2'nin esnek ve yapılandırılabilir loglama sistemini kullanır
    // Varsayılan değer: LogManager.getLogger(DriverFactory.class) ile class-specific logger instance'ı oluşturulur
    // Yaşam döngüsü: Class seviyesinde static olarak tanımlı, tüm metodlar tarafından paylaşılır; final olduğu için referans değiştirilemez
    // Kapsam: Private, sadece bu class içinde kullanılır
    private static final Logger logger = LogManager.getLogger(DriverFactory.class);

    // ThreadLocal driver storage: Her thread için ayrı driver instance
    // Amaç: Paralel test execution'da thread safety sağlamak için her thread'in kendi driver'ını izole tutar
    // Veri tipi seçim nedeni: ThreadLocal<AppiumDriver>, thread-specific veri saklama için Java'nın built-in mekanizmasını kullanır
    // Varsayılan değer: new ThreadLocal<>(), başlangıçta null değerle initialize edilir
    // Yaşam döngüsü: Static ve final, class boyunca sabit kalır; thread'ler driver'ı set/remove ile manipüle eder
    // Kapsam: Private, sadece bu class içinde erişilir
    // Bu değişken olmadan: Paralel testlerde driver çakışmaları (race conditions) oluşur
    private static final ThreadLocal<AppiumDriver> driverThreadLocal = new ThreadLocal<>();

    /**
     * Private constructor: Factory class'ının instance yaratılmasını engeller
     * Amaç: Bu class'ın sadece static metodlar aracılığıyla kullanılmasını sağlamak, nesne yaratımını önlemek
     * Parametreler: Parametre almaz, default constructor
     * Return değeri: Yok (void), instance yaratımı engellenir
     * Kullanılmazsa etki: Geliştiriciler yanlışlıkla DriverFactory objesi yaratabilir, bu da gereksiz memory kullanımı ve kafa karışıklığına yol açar
     * Diğer metodlarla kıyasla: initializeDriver gibi factory metodlardan farkı, nesne yaratımı değil, kullanımı kısıtlamaktır; utility pattern'ın bir parçasıdır
     * Çağrıldığı yerler: Doğrudan çağrılmaz, Java'nın default constructor mekanizması tarafından tetiklenir (engellenir)
     * Bağımlılıkları: Yok, bağımsız bir constructor
     */
    private DriverFactory() {
        // Bu satır şunu yapıyor: Private constructor ile instance yaratımını engeller, utility class olduğunu vurgular
        // Mantık: Factory pattern gereği, nesne yaratımı yerine static metodlar kullanılmalı
        // Bu satır olmadan: Public veya default constructor ile nesne yaratılabilir, istenmeyen davranışlar oluşur
        // Utility class should not be instantiated
        // Bu yorum: Geliştiricilere neden private constructor kullanıldığını açıklar
    }

    /**
     * ANA DRIVER BAŞLATMA METODİ - FACTORY PATTERN CORE METHOD
     * Amaç: Verilen platforma göre uygun Appium driver'ı (AndroidDriver veya IOSDriver) yaratır, konfigüre eder ve thread-safe bir şekilde saklar
     * Parametreler: platform - Hedef platform adı ("Android" veya "iOS", case-insensitive) olarak string alır
     * Return değeri: Konfigüre edilmiş AppiumDriver instance'ı, polymorphism ile AndroidDriver veya IOSDriver dönebilir
     * Kullanılmazsa etki: Mobil test otomasyonu çalışamaz, platform-specific testing imkansız olur, Appium server ile bağlantı kurulamaz, framework işlevsiz kalır
     * Diğer metodlarla kıyasla: getAndroidCapabilities/getIOSCapabilities delegate metodlardır, bu metod onları koordine eder; configureDriverTimeouts ile timeout ayarlarını birleştirir; framework'ün ana giriş noktasıdır
     * Çağrıldığı yerler: DriverManager.initializeDriver(), TestHooks.before(), BaseTest setup metodları, CI/CD script'leri
     * Bağımlılıkları: ConfigurationManager (server URL, platform ayarları), getAndroidCapabilities/getIOSCapabilities (platform-specific ayarlar), Appium server, APK/IPA dosyaları
     */
    public static AppiumDriver initializeDriver(String platform) {
        // Bu satır şunu yapıyor: Platform bilgisini loglayarak hangi platform için driver yaratılacağını kaydeder
        // Mantık: Debugging için hangi platformda işlem yapıldığını izlemek
        // Bu satır olmadan: Hangi platform için driver yaratıldığı takip edilemez, hata ayıklama zorlaşır
        logger.info("Initializing driver for platform: {}", platform);

        // Bu satır şunu yapıyor: Platforma göre driver instance'ını tutacak bir değişken tanımlar
        // Mantık: Polymorphism ile AndroidDriver veya IOSDriver saklanabilir, esnek bir yapı sunar
        // Bu satır olmadan: Driver instance'ı atanamaz, metod null döner ve hata oluşur
        AppiumDriver driver;

        // Bu satır şunu yapıyor: Appium'a gönderilecek ayarları tutacak bir capabilities objesi oluşturur
        // Mantık: Key-value çiftleri ile driver konfigürasyonlarını tanımlar, Appium ile iletişim için temel oluşturur
        // Bu satır olmadan: Driver ayarları Appium'a gönderilemez, driver başlatılamaz
        DesiredCapabilities capabilities = new DesiredCapabilities();

        try {
            // Bu satır şunu yapıyor: ConfigurationManager'dan Appium server URL'sini alır ve URL objesine çevirir
            // Mantık: Appium server ile bağlantı kurmak için geçerli bir URL gerekli, ağ bağlantısı sağlar
            // Bu satır olmadan: Appium server'a bağlanılamaz, driver yaratılamaz ve testler başarısız olur
            URL serverUrl = new URL(ConfigurationManager.getFrameworkConfig().getAppiumServerUrl());

            // Bu satır şunu yapıyor: Platforma göre uygun driver yaratma akışını seçer (Factory Pattern)
            // Mantık: Case-insensitive karşılaştırma ile platformu tanımlar, dinamik bir yapı sağlar
            // Bu satır olmadan: Her platform için ayrı metod yazılmalı, kod tekrarına yol açar ve bakım zorlaşır
            switch (platform.toLowerCase()) {
                case "android":
                    // Bu satır şunu yapıyor: Android-specific capabilities'leri alır
                    // Mantık: Android driver için gerekli ayarları konfigüre eder, platforma özgü özellikler ekler
                    // Bu satır olmadan: Android-specific ayarlar uygulanamaz, driver hatalı çalışır
                    capabilities = getAndroidCapabilities();

                    // Bu satır şunu yapıyor: AndroidDriver instance'ını yaratır ve Appium server'a bağlar
                    // Mantık: UiAutomator2 backend ile Android cihaz kontrolü sağlar, cihazla iletişim kurar
                    // Bu satır olmadan: Android cihaz kontrolü imkansız, testler çalışmaz
                    driver = new AndroidDriver(serverUrl, capabilities);

                    // Bu satır şunu yapıyor: Android driver'ın başarılı yaratıldığını loglar
                    // Mantık: Debugging için başarılı başlatmayı kaydeder, hata ayıklama kolaylaşır
                    // Bu satır olmadan: Başarılı başlatma takibi yapılamaz, durum bilinmez
                    logger.info("Android driver initialized successfully");
                    break;

                case "ios":
                    // Bu satır şunu yapıyor: iOS-specific capabilities'leri alır
                    // Mantık: iOS driver için gerekli ayarları konfigüre eder, platforma özgü özellikler ekler
                    // Bu satır olmadan: iOS-specific ayarlar uygulanamaz, driver hatalı çalışır
                    capabilities = getIOSCapabilities();

                    // Bu satır şunu yapıyor: IOSDriver instance'ını yaratır ve Appium server'a bağlar
                    // Mantık: XCUITest backend ile iOS cihaz kontrolü sağlar, cihazla iletişim kurar
                    // Bu satır olmadan: iOS cihaz kontrolü imkansız, testler çalışmaz
                    driver = new IOSDriver(serverUrl, capabilities);

                    // Bu satır şunu yapıyor: iOS driver'ın başarılı yaratıldığını loglar
                    // Mantık: Debugging için başarılı başlatmayı kaydeder, hata ayıklama kolaylaşır
                    // Bu satır olmadan: Başarılı başlatma takibi yapılamaz, durum bilinmez
                    logger.info("iOS driver initialized successfully");
                    break;

                default:
                    // Bu satır şunu yapıyor: Desteklenmeyen platform için hata fırlatır
                    // Mantık: Yanlış platform girişlerini erken yakalar, hata yönetimini kolaylaştırır
                    // Bu satır olmadan: Hatalı platformlar silent kalır, debugging zorlaşır ve testler başarısız olur
                    throw new IllegalArgumentException("Unsupported platform: " + platform);
            }

            // Bu satır şunu yapıyor: Driver için timeout ayarlarını konfigüre eder
            // Mantık: Test stabilitesini artırmak için implicit/explicit/script timeout'ları ayarlar, test güvenilirliğini artırır
            // Bu satır olmadan: Default timeout'lar kullanılır, testler instable olur ve erken fail edebilir
            configureDriverTimeouts(driver);

            // Bu satır şunu yapıyor: Yaratılan driver'ı ThreadLocal'a kaydeder
            // Mantık: Paralel testlerde thread safety sağlar, her thread'in kendi driver'ını izole tutar
            // Bu satır olmadan: Paralel execution'da driver çakışmaları olur, testler çöker
            driverThreadLocal.set(driver);

            // Bu satır şunu yapıyor: Driver başlatma işleminin tamamlandığını loglar
            // Mantık: Başarılı başlatmayı doğrular, debugging için kritik bir geri bildirim sağlar
            // Bu satır olmadan: Başarılı başlatma takibi yapılamaz, durum bilinmez
            logger.info("Driver initialization completed for platform: {}", platform);

            // Bu satır şunu yapıyor: Yaratılan ve konfigüre edilen driver'ı döndürür
            // Mantık: Çağıran metoda driver instance'ı sağlar, testlerin driver'a erişimini mümkün kılar
            // Bu satır olmadan: Çağıran metod null alır, testler çöker ve işlev göremez
            return driver;

        } catch (MalformedURLException e) {
            // Bu satır şunu yapıyor: Hatalı Appium server URL'sini loglar
            // Mantık: Hata kaynağını (URL formatı) ve stack trace'i kaydeder, hata ayıklamayı kolaylaştırır
            // Bu satır olmadan: Hata kaynağı belirsiz kalır, debugging zorlaşır ve kök neden bulunamaz
            logger.error("Invalid Appium server URL: {}",
                    ConfigurationManager.getFrameworkConfig().getAppiumServerUrl(), e);

            // Bu satır şunu yapıyor: MalformedURLException'ı RuntimeException'a sarar ve fırlatır
            // Mantık: Checked exception'ı unchecked'e çevirir, metod signature'ını temiz tutar ve hata yönetimini basitleştirir
            // Bu satır olmadan: Hata silent kalır, testler undefined behavior gösterir ve hata yakalanamaz
            throw new RuntimeException("Invalid Appium server URL", e);

        } catch (Exception e) {
            // Bu satır şunu yapıyor: Diğer tüm hataları (network, capabilities) loglar
            // Mantık: Hata kaynağını ve stack trace'i kaydeder, hata ayıklamayı kolaylaştırır
            // Bu satır olmadan: Hata kaynağı belirsiz kalır, debugging zorlaşır ve kök neden bulunamaz
            logger.error("Failed to initialize driver for platform: {}", platform, e);

            // Bu satır şunu yapıyor: Generic exception'ı RuntimeException'a sarar ve fırlatır
            // Mantık: Hatanın çağıran metoda iletilmesi ve test akışının durdurulması, hata yönetimini standardize eder
            // Bu satır olmadan: Hata silent kalır, undefined behavior oluşur ve testler beklenmedik sonuçlar verebilir
            throw new RuntimeException("Driver initialization failed", e);
        }
    }

    /**
     * ANDROID CAPABILITIES YARATMA METODİ
     * Amaç: Android cihazlar için Appium capabilities'lerini (platform, cihaz, APK, timeout ayarları) hazırlar
     * Parametreler: Parametre almaz, konfigürasyon dosyalarından bilgileri okur
     * Return değeri: Android'e özgü ayarlarla dolu DesiredCapabilities objesi
     * Kullanılmazsa etki: Android driver yaratılamaz, UiAutomator2 backend başlatılamaz, APK install edilemez, permissions otomatik verilemez, timeout ayarları yanlış olur
     * Diğer metodlarla kıyasla: getIOSCapabilities'den farkı Android-specific ayarlar (UiAutomator2, APK, systemPort) içerir; initializeDriver tarafından delegate edilir
     * Çağrıldığı yerler: initializeDriver() metodunda Android case'inde
     * Bağımlılıkları: ConfigurationManager.FrameworkConfig (genel ayarlar), ConfigurationManager.AndroidConfig (Android-specific ayarlar), APK file existence
     */
    private static DesiredCapabilities getAndroidCapabilities() {
        // Bu satır şunu yapıyor: Android capabilities setup işlemini loglar
        // Mantık: Debugging için setup başlangıcını kaydeder, işlem izlenebilirliğini sağlar
        // Bu satır olmadan: Android setup süreçleri takip edilemez, hata ayıklama zorlaşır
        logger.info("Setting up Android capabilities");

        // Bu satır şunu yapıyor: Appium'a gönderilecek ayarları tutacak boş bir capabilities objesi oluşturur
        // Mantık: Key-value çiftleri ile driver konfigürasyonlarını tanımlar, temel yapı oluşturur
        // Bu satır olmadan: Appium'a hiçbir ayar gönderilemez, driver başlatılamaz
        DesiredCapabilities capabilities = new DesiredCapabilities();

        // Bu satır şunu yapıyor: Genel framework ayarlarını ConfigurationManager'dan alır
        // Mantık: Platform-agnostic ayarları (platform, cihaz, timeouts) okumak için, merkezi konfigürasyon sağlar
        // Bu satır olmadan: Ayarlar hard-coded olur, esneklik kaybolur ve bakım zorlaşır
        ConfigurationManager.FrameworkConfig frameworkConfig = ConfigurationManager.getFrameworkConfig();

        // Bu satır şunu yapıyor: Android-specific ayarları ConfigurationManager'dan alır
        // Mantık: Android'e özgü ayarları (automation, permissions, ports) okumak için, platforma özel yapılandırma sağlar
        // Bu satır olmadan: Android ayarları manuel tanımlanmalı, kod tekrarına yol açar ve hata riski artar
        ConfigurationManager.AndroidConfig androidConfig = ConfigurationManager.getAndroidConfig();

        // PLATFORM CAPABILITIES: Temel platform tanım ayarları

        // Bu satır şunu yapıyor: Appium'a platformun Android olduğunu bildirir
        // Mantık: Driver'ın Android için başlatılmasını sağlar, doğru platformu tanımlar
        // Bu satır olmadan: Appium hangi platform için çalışacağını bilmez, driver başlatılamaz
        capabilities.setCapability("platformName", "Android");

        // Bu satır şunu yapıyor: Android cihazın işletim sistemi sürümünü set eder
        // Mantık: Version-specific özelliklerin doğru çalışmasını sağlar, uyumluluk kontrolü yapar
        // Bu satır olmadan: Yanlış versiyon kullanılır, testler başarısız olur ve cihazla uyumsuzluk oluşur
        capabilities.setCapability("platformVersion", frameworkConfig.getPlatformVersion());

        // Bu satır şunu yapıyor: Test edilecek cihaz/emulator adını set eder
        // Mantık: Appium'un hangi cihazı hedefleyeceğini belirler, hedef cihazı tanımlar
        // Bu satır olmadan: Appium hedef cihazı seçemez, testler yanlış cihazda çalışır
        capabilities.setCapability("deviceName", frameworkConfig.getDeviceName());

        // Bu satır şunu yapıyor: Android için automation engine'ini (UiAutomator2) set eder
        // Mantık: Modern Android automation framework'ünü kullanır, performans ve stabilite sağlar
        // Bu satır olmadan: Eski UiAutomator1 kullanılır, testler instable olur ve hatalar artar
        capabilities.setCapability("automationName", androidConfig.getAutomationName());

        // Bu satır şunu yapıyor: Cihaz UDID'sinin varlığını kontrol eder (gerçek cihazlar için)
        // Mantık: Spesifik cihaz hedeflemesini sağlar, doğru cihazı seçer
        // Bu satır olmadan: Yanlış cihaz seçilebilir, testler farklı cihazlarda çalışır
        if (frameworkConfig.getDeviceUdid() != null && !frameworkConfig.getDeviceUdid().isEmpty()) {
            // Bu satır şunu yapıyor: Gerçek cihazın UDID'sini set eder
            // Mantık: Doğru cihazı hedefler, karışıklığı önler ve spesifik cihaz testini sağlar
            // Bu satır olmadan: Yanlış cihaz seçilir, testler beklenmedik sonuçlar üretir
            capabilities.setCapability("udid", frameworkConfig.getDeviceUdid());
        }

        // APP INSTALLATION CAPABILITIES: APK file handling

        // Bu satır şunu yapıyor: APK dosyasının yolunu configuration'dan alır
        // Mantık: Appium'un uygulamayı yüklemesi için dosya yolu sağlar, uygulama başlatma için temel oluşturur
        // Bu satır olmadan: APK yolu alınamaz, uygulama yüklenemez
        String appPath = frameworkConfig.getAppPath();

        // Bu satır şunu yapıyor: APK dosyasının varlığını kontrol eder
        // Mantık: Dosya varlığını doğrulamak için File objesi oluşturur, hata önler
        // Bu satır olmadan: Yanlış dosya yolu hataya yol açar, testler başarısız olur
        File appFile = new File(appPath);

        if (appFile.exists()) {
            // Bu satır şunu yapıyor: APK dosyasının absolute path'ini capability'ye set eder
            // Mantık: Appium'un APK'yı yüklemesini sağlar, uygulama başlatma için dosya yolunu tanımlar
            // Bu satır olmadan: APK yüklenemez, app başlatılamaz ve testler çalışmaz
            capabilities.setCapability("app", appFile.getAbsolutePath());
        } else {
            // Bu satır şunu yapıyor: APK dosyası bulunamazsa uyarı loglar
            // Mantık: Debugging için dosya eksikliğini kaydeder, fallback bildirir ve alternatif yol sunar
            // Bu satır olmadan: Dosya eksikliği silent kalır, hata ayıklama zorlaşır
            logger.warn("App file not found at: {}. Using app package and activity instead.", appPath);

            // Bu satır şunu yapıyor: Uygulamanın package adını set eder
            // Mantık: APK yüklü değilse mevcut uygulamayı başlatır, alternatif bir başlatma yöntemi sunar
            // Bu satır olmadan: Hangi app'i açacağı belli olmaz, testler yanlış uygulamada çalışır
            capabilities.setCapability("appPackage", androidConfig.getAppPackage());

            // Bu satır şunu yapıyor: Uygulamanın ana activity adını set eder
            // Mantık: Uygulamanın giriş noktasını tanımlar, doğru başlangıç noktasını sağlar
            // Bu satır olmadan: App doğru activity ile açılamaz, testler başarısız olur
            capabilities.setCapability("appActivity", androidConfig.getAppActivity());
        }

        // ANDROID-SPECIFIC CAPABILITIES: Platform'a özgü ayarlar

        // Bu satır şunu yapıyor: İzinleri otomatik onaylamayı set eder
        // Mantık: Permission dialog'larını otomatik geçer, test akışını kesintisiz hale getirir
        // Bu satır olmadan: Testler kesintiye uğrar, manuel müdahale gerekir
        capabilities.setCapability("autoGrantPermissions", androidConfig.isAutoGrantPermissions());

        // Bu satır şunu yapıyor: Uygulama verilerinin sıfırlanmamasını set eder
        // Mantık: App state'ini korur, önceki test verilerini tutar
        // Bu satır olmadan: Her testte app sıfırlanır, veri tutarlılığı bozulur
        capabilities.setCapability("noReset", androidConfig.isNoReset());

        // Bu satır şunu yapıyor: Uygulamanın tamamen uninstall/reinstall edilmesini set eder
        // Mantık: Clean slate testing sağlar, temiz bir ortam sunar
        // Bu satır olmadan: App state contamination oluşabilir, testler tutarsız sonuçlar verir
        capabilities.setCapability("fullReset", androidConfig.isFullReset());

        // Bu satır şunu yapıyor: UiAutomator2 server için sistem portunu set eder
        // Mantık: Paralel testlerde port çakışmalarını önler, çoklu test yürütümünü destekler
        // Bu satır olmadan: Paralel testler çöker, port çakışmaları nedeniyle hata alır
        capabilities.setCapability("systemPort", androidConfig.getSystemPort());

        // STABILITY CAPABILITIES: Test reliability için timeout ayarları

        // Bu satır şunu yapıyor: Driver komutlarının max bekleme süresini set eder
        // Mantık: Stuck komutları önler, testlerin zaman aşımına uğramasını engeller
        // Bu satır olmadan: Default timeout kullanılır, testler erken fail eder
        capabilities.setCapability("newCommandTimeout", 300);

        // Bu satır şunu yapıyor: APK yükleme için max süreyi set eder
        // Mantık: Büyük APK'ların yüklenmesini destekler, yavaş cihazlarda hata önler
        // Bu satır olmadan: Yükleme timeout'a takılır, uygulama yüklenemez
        capabilities.setCapability("androidInstallTimeout", 90000);

        // Bu satır şunu yapıyor: ADB komutlarının max süresini set eder
        // Mantık: ADB işlemlerinin askıda kalmasını önler, cihaz iletişimini stabilize eder
        // Bu satır olmadan: ADB komutları stuck kalır, testler durur
        capabilities.setCapability("adbExecTimeout", 20000);

        // Bu satır şunu yapıyor: UiAutomator2 server başlatma süresini set eder
        // Mantık: Yavaş cihazlarda server başlatma için zaman tanır, başlatma başarısını artırır
        // Bu satır olmadan: Server startup fail eder, driver başlatılamaz
        capabilities.setCapability("uiautomator2ServerLaunchTimeout", 60000);

        // PERFORMANCE CAPABILITIES: Optimization ayarları

        // Bu satır şunu yapıyor: Cihaz başlatma adımlarını atlamayı devre dışı bırakır
        // Mantık: Tam cihaz setup'ı sağlar, cihazın doğru durumunu garantiler
        // Bu satır olmadan: Eksik setup instabilitesine yol açar, testler hatalı çalışır
        capabilities.setCapability("skipDeviceInitialization", false);

        // Bu satır şunu yapıyor: UiAutomator2 server kurulumunu atlamayı devre dışı bırakır
        // Mantık: Server'ın güncel ve temiz kurulmasını sağlar, hata riskini azaltır
        // Bu satır olmadan: Eski server bileşenleri hatalara yol açar, testler başarısız olur
        capabilities.setCapability("skipServerInstallation", false);

        // Bu satır şunu yapıyor: Android 9+ hidden API kısıtlamalarını görmezden gelmeyi set eder
        // Mantık: Android 9+ cihazlarda automation hatalarını önler, uyumluluk sağlar
        // Bu satır olmadan: Android 9+ cihazlarda testler fail eder, cihaz desteklenmez
        capabilities.setCapability("ignoreHiddenApiPolicyError", true);

        // Bu satır şunu yapıyor: Android capabilities'lerinin başarılı konfigüre edildiğini loglar
        // Mantık: Debugging için setup tamamlanmasını kaydeder, durum izlenebilirliğini sağlar
        // Bu satır olmadan: Setup başarılı mı bilinmez, hata ayıklama zorlaşır
        logger.info("Android capabilities configured successfully");

        // Bu satır şunu yapıyor: Hazırlanan capabilities objesini döndürür
        // Mantık: initializeDriver metoduna ayarları sağlar, driver başlatma için gerekli veriyi aktarır
        // Bu satır olmadan: initializeDriver null veya eksik ayarlarla çalışır, driver hatalı başlatılır
        return capabilities;
    }

    /**
     * iOS CAPABILITIES YARATMA METODİ
     * Amaç: iOS cihazlar (simulator/real device) için Appium capabilities'lerini (platform, cihaz, IPA, WebDriverAgent ayarları) hazırlar
     * Parametreler: Parametre almaz, konfigürasyon dosyalarından bilgileri okur
     * Return değeri: iOS'e özgü ayarlarla dolu DesiredCapabilities objesi
     * Kullanılmazsa etki: iOS driver yaratılamaz, XCUITest backend başlatılamaz, IPA install edilemez, WebDriverAgent başlatılamaz, iOS specific features çalışmaz
     * Diğer metodlarla kıyasla: getAndroidCapabilities'den farkı iOS-specific ayarlar (XCUITest, bundleId, WDA ports) içerir; initializeDriver tarafından delegate edilir
     * Çağrıldığı yerler: initializeDriver() metodunda iOS case'inde
     * Bağımlılıkları: ConfigurationManager.FrameworkConfig (genel ayarlar), ConfigurationManager.IOSConfig (iOS-specific ayarlar), IPA file existence, WebDriverAgent availability
     */
    private static DesiredCapabilities getIOSCapabilities() {
        // Bu satır şunu yapıyor: iOS capabilities setup işlemini loglar
        // Mantık: Debugging için setup başlangıcını kaydeder, işlem izlenebilirliğini sağlar
        // Bu satır olmadan: iOS setup süreçleri takip edilemez, hata ayıklama zorlaşır
        logger.info("Setting up iOS capabilities");

        // Bu satır şunu yapıyor: Appium'a gönderilecek ayarları tutacak boş bir capabilities objesi oluşturur
        // Mantık: Key-value çiftleri ile iOS-specific konfigürasyonları tanımlar, temel yapı oluşturur
        // Bu satır olmadan: Appium'a hiçbir iOS ayarı gönderilemez, driver başlatılamaz
        DesiredCapabilities capabilities = new DesiredCapabilities();

        // Bu satır şunu yapıyor: Genel framework ayarlarını ConfigurationManager'dan alır
        // Mantık: Platform-agnostic ayarları (platform, cihaz, timeouts) okumak için, merkezi konfigürasyon sağlar
        // Bu satır olmadan: Ayarlar hard-coded olur, esneklik kaybolur ve bakım zorlaşır
        ConfigurationManager.FrameworkConfig frameworkConfig = ConfigurationManager.getFrameworkConfig();

        // Bu satır şunu yapıyor: iOS-specific ayarları ConfigurationManager'dan alır
        // Mantık: iOS'e özgü ayarları (simulator, bundle ID, WDA ports) okumak için, platforma özel yapılandırma sağlar
        // Bu satır olmadan: iOS ayarları manuel tanımlanmalı, kod tekrarına yol açar ve hata riski artar
        ConfigurationManager.IOSConfig iosConfig = ConfigurationManager.getIOSConfig();

        // PLATFORM CAPABILITIES: Temel platform tanım ayarları

        // Bu satır şunu yapıyor: Appium'a platformun iOS olduğunu bildirir
        // Mantık: Driver'ın iOS için başlatılmasını sağlar, doğru platformu tanımlar
        // Bu satır olmadan: Appium hangi platform için çalışacağını bilmez, driver başlatılamaz
        capabilities.setCapability("platformName", "iOS");

        // Bu satır şunu yapıyor: iOS cihazın işletim sistemi sürümünü set eder
        // Mantık: Version-specific özelliklerin doğru çalışmasını sağlar, uyumluluk kontrolü yapar
        // Bu satır olmadan: Yanlış versiyon kullanılır, testler başarısız olur ve cihazla uyumsuzluk oluşur
        capabilities.setCapability("platformVersion", frameworkConfig.getPlatformVersion());

        // Bu satır şunu yapıyor: Test edilecek cihaz/simulator adını set eder
        // Mantık: Appium'un hangi cihazı/simülatörü hedefleyeceğini belirler, hedef cihazı tanımlar
        // Bu satır olmadan: Appium hedef cihazı seçemez, testler yanlış cihazda çalışır
        capabilities.setCapability("deviceName", iosConfig.getSimulatorName());

        // Bu satır şunu yapıyor: iOS için automation engine'ini (XCUITest) set eder
        // Mantık: Modern iOS automation framework'ünü kullanır, performans ve stabilite sağlar
        // Bu satır olmadan: Eski UIAutomation kullanılır, testler çalışmaz ve hatalar artar
        capabilities.setCapability("automationName", iosConfig.getAutomationName());

        // Bu satır şunu yapıyor: Cihaz UDID'sinin varlığını kontrol eder (gerçek cihazlar için)
        // Mantık: Spesifik cihaz hedeflemesini sağlar, doğru cihazı seçer
        // Bu satır olmadan: Yanlış cihaz seçilebilir, testler farklı cihazlarda çalışır
        if (frameworkConfig.getDeviceUdid() != null && !frameworkConfig.getDeviceUdid().isEmpty()) {
            // Bu satır şunu yapıyor: Gerçek cihazın UDID'sini set eder
            // Mantık: Doğru cihazı hedefler, karışıklığı önler ve spesifik cihaz testini sağlar
            // Bu satır olmadan: Yanlış cihaz seçilir, testler beklenmedik sonuçlar üretir
            capabilities.setCapability("udid", frameworkConfig.getDeviceUdid());
        }

        // APP INSTALLATION CAPABILITIES: IPA file handling

        // Bu satır şunu yapıyor: IPA dosyasının yolunu configuration'dan alır
        // Mantık: Appium'un uygulamayı yüklemesi için dosya yolu sağlar, uygulama başlatma için temel oluşturur
        // Bu satır olmadan: IPA yolu alınamaz, uygulama yüklenemez
        String appPath = frameworkConfig.getAppPath();

        // Bu satır şunu yapıyor: IPA dosyasının varlığını kontrol eder
        // Mantık: Dosya varlığını doğrulamak için File objesi oluşturur, hata önler
        // Bu satır olmadan: Yanlış dosya yolu hataya yol açar, testler başarısız olur
        File appFile = new File(appPath);

        if (appFile.exists()) {
            // Bu satır şunu yapıyor: IPA dosyasının absolute path'ini capability'ye set eder
            // Mantık: Appium'un IPA'yı yüklemesini sağlar, uygulama başlatma için dosya yolunu tanımlar
            // Bu satır olmadan: IPA yüklenemez, app başlatılamaz ve testler çalışmaz
            capabilities.setCapability("app", appFile.getAbsolutePath());
        } else {
            // Bu satır şunu yapıyor: IPA dosyası bulunamazsa bundle ID'yi set eder
            // Mantık: App zaten cihazda yüklüyse bundle ID ile başlatır, alternatif bir başlatma yöntemi sunar
            // Bu satır olmadan: Hangi app'i açacağı belli olmaz, testler yanlış uygulamada çalışır
            capabilities.setCapability("bundleId", iosConfig.getBundleId());
        }

        // iOS-SPECIFIC CAPABILITIES: Platform'a özgü ayarlar

        // Bu satır şunu yapıyor: WebDriverAgent için yerel portu set eder
        // Mantık: Paralel testlerde port çakışmalarını önler, çoklu test yürütümünü destekler
        // Bu satır olmadan: Port conflicts oluşur, paralel testler çöker
        capabilities.setCapability("wdaLocalPort", iosConfig.getWdaLocalPort());

        // Bu satır şunu yapıyor: Yeni WebDriverAgent instance kullanımını devre dışı bırakır
        // Mantık: Mevcut WDA'yı reuse ederek başlatma süresini azaltır, performans artırır
        // Bu satır olmadan: Testler yavaşlar, başlatma süresi uzar
        capabilities.setCapability("useNewWDA", false);

        // Bu satır şunu yapıyor: Session başında reset'i devre dışı bırakır
        // Mantık: Gereksiz reset'leri önler, performans artırır ve kaynak kullanımını optimize eder
        // Bu satır olmadan: Testler yavaşlar, gereksiz işlem yapılır
        capabilities.setCapability("resetOnSessionStartOnly", false);

        // STABILITY CAPABILITIES: iOS test reliability için özel timeout ayarları

        // Bu satır şunu yapıyor: Driver komutlarının max bekleme süresini set eder
        // Mantık: Stuck komutları önler, testlerin zaman aşımına uğramasını engeller
        // Bu satır olmadan: Default timeout kullanılır, testler erken fail eder
        capabilities.setCapability("newCommandTimeout", 300);

        // Bu satır şunu yapıyor: WebDriverAgent başlatma deneme sayısını set eder
        // Mantık: WDA başlatma hatalarında retry yapar, başarım oranını artırır
        // Bu satır olmadan: WDA startup fail eder, driver başlatılamaz
        capabilities.setCapability("wdaStartupRetries", 3);

        // Bu satır şunu yapıyor: WDA retry'leri arasında bekleme süresini set eder
        // Mantık: Retry'ler arasında yeterli zaman bırakır, cihaz yükünü azaltır
        // Bu satır olmadan: Hızlı retry'ler cihazı yorar, performans düşer
        capabilities.setCapability("wdaStartupRetryInterval", 20000);

        // Bu satır şunu yapıyor: iOS capabilities'lerinin başarılı konfigüre edildiğini loglar
        // Mantık: Debugging için setup tamamlanmasını kaydeder, durum izlenebilirliğini sağlar
        // Bu satır olmadan: Setup başarılı mı bilinmez, hata ayıklama zorlaşır
        logger.info("iOS capabilities configured successfully");

        // Bu satır şunu yapıyor: Hazırlanan capabilities objesini döndürür
        // Mantık: initializeDriver metoduna ayarları sağlar, driver başlatma için gerekli veriyi aktarır
        // Bu satır olmadan: initializeDriver null veya eksik ayarlarla çalışır, driver hatalı başlatılır
        return capabilities;
    }

    /**
     * DRIVER TIMEOUT AYARLARI YAPILANDIRMA METODİ
     * Amaç: AppiumDriver instance'ı için timeout ayarlarını (implicit, page load, script) konfigüre eder
     * Parametreler: driver - Timeout ayarları uygulanacak AppiumDriver instance'ı
     * Return değeri: Void (driver objesini direkt modify eder)
     * Kullanılmazsa etki: Default timeout değerleri kullanılır (kısa, testlerde failures), implicit wait yok (NoSuchElement exceptions), page load timeout yok (hybrid app'lerde hangs), test stability azalır
     * Diğer metodlarla kıyasla: initializeDriver tarafından çağrılır, sadece timeout ayarlarına odaklanır; getAndroidCapabilities/getIOSCapabilities ile birlikte çalışır
     * Çağrıldığı yerler: initializeDriver() metodunda, driver yaratıldıktan sonra
     * Bağımlılıkları: ConfigurationManager.FrameworkConfig (timeout değerleri), AppiumDriver API (timeout yönetimi), Duration API
     */
    private static void configureDriverTimeouts(AppiumDriver driver) {
        // Bu satır şunu yapıyor: Driver timeout configuration başlangıcını loglar
        // Mantık: Debugging için timeout setup process'ini kaydeder, işlem izlenebilirliğini sağlar
        // Bu satır olmadan: Timeout setup process'i takip edilemez, hata ayıklama zorlaşır
        logger.info("Configuring driver timeouts");

        // Bu satır şunu yapıyor: Framework konfigürasyon objesini alır
        // Mantık: Timeout değerlerini konfigürasyon dosyasından okumak için, merkezi yapılandırma kullanır
        // Bu satır olmadan: Timeout değerleri hard-coded olur, esneklik kaybolur
        ConfigurationManager.FrameworkConfig config = ConfigurationManager.getFrameworkConfig();

        // Bu satır şunu yapıyor: Implicit wait süresini set eder
        // Mantık: Element arama için otomatik bekleme sağlar, element bulma başarımını artırır
        // Bu satır olmadan: Element bulunamadığında immediate NoSuchElementException, testler fail eder
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitTimeout()));

        // Bu satır şunu yapıyor: page yükleme için max timeout süresini set eder
        // Mantık: Hybrid app'lerde page yüklemelerini bekler, yavaş yüklemeleri tolere eder
        // Bu satır olmadan: Slow loading pages stuck kalır, testler zaman aşımına uğrar
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getExplicitTimeout()));

        // Bu satır şunu yapıyor: JavaScript execution için max timeout süresini set eder
        // Mantık: Uzun süren JS script'lerini kontrol eder, script execution hatalarını önler
        // Bu satır olmadan: Long running JS scripts hang kalır, testler durur
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(config.getExplicitTimeout()));

        // Bu satır şunu yapıyor: Driver timeouts başarılı configuration completion log'unu kaydeder
        // Mantık: Debugging için timeout setup'unun tamamlandığını doğrular, durum izlenebilirliğini sağlar
        // Bu satır olmadan: Timeout setup başarılı mı bilinmez, hata ayıklama zorlaşır
        logger.info("Driver timeouts configured successfully");
    }

    /**
     * THREADLOCAL'DAN MEVCUT DRIVER INSTANCE'INI ALMA METODİ
     * Amaç: Mevcut thread için initialize edilmiş AppiumDriver instance'ını return eder
     * Parametreler: Parametre almaz (thread-specific storage'dan okur)
     * Return değeri: Mevcut thread'e ait AppiumDriver instance'ı
     * Kullanılmazsa etki: Test sınıfları ve page object'ler driver'a erişemez, mobil element operations yapılamaz, test automation tamamen kırılır
     * Diğer metodlarla kıyasla: initializeDriver driver yaratır, bu metod yaratılanı return eder; isDriverInitialized boolean döner, bu metod exception fırlatır
     * Çağrıldığı yerler: Page object classes, test step definitions, BaseTest class, utility methods (screenshot, video recording)
     * Bağımlılıkları: driverThreadLocal (thread safety için), initializeDriver (driver yaratımı)
     */
    public static AppiumDriver getDriver() {
        // Bu satır şunu yapıyor: ThreadLocal storage'dan mevcut thread'in driver'ını alır
        // Mantık: Thread-specific value retrieval sağlar, her thread'in kendi driver'ına erişimini garantiler
        // Bu satır olmadan: Driver instance alınamaz, testler driver'a erişemez
        AppiumDriver driver = driverThreadLocal.get();

        // Bu satır şunu yapıyor: Driver'ın null olup olmadığını kontrol eder
        // Mantık: Null driver erişimini önler, erken hata yakalama yapar ve istikrar sağlar
        // Bu satır olmadan: NullPointerException riski artar, testler çöker
        if (driver == null) {
            // Bu satır şunu yapıyor: Null driver hatasını loglar
            // Mantık: Hata kaynağını (initialize eksikliği) netleştirir, hata ayıklamayı kolaylaştırır
            // Bu satır olmadan: Hata kaynağı belirsiz kalır, debugging zorlaşır
            logger.error("Driver is not initialized. Call initializeDriver() first.");

            // Bu satır şunu yapıyor: IllegalStateException fırlatır
            // Mantık: Driver eksikliğini bildirir, test akışını durdurur ve hata yönetimini sağlar
            // Bu satır olmadan: Null driver'la işlem denemesi NullPointerException'a yol açar, testler beklenmedik şekilde biter
            throw new IllegalStateException("Driver is not initialized. Call initializeDriver() first.");
        }

        // Bu satır şunu yapıyor: Validate edilmiş driver instance'ını döndürür
        // Mantık: Çağıran metoda driver sağlar, testlerin driver'a erişimini mümkün kılar
        // Bu satır olmadan: Calling code driver'a erişemez, testler işlevsiz kalır
        return driver;
    }

    /**
     * DRIVER INITIALIZE DURUMU KONTROL METODİ
     * Amaç: Mevcut thread için driver'ın initialize edilip edilmediğini safe check yapar
     * Parametreler: Parametre almaz (thread-specific storage kontrol eder)
     * Return değeri: boolean - true: driver mevcut, false: driver yok
     * Kullanılmazsa etki: Unsafe driver access attempts, IllegalStateException'lar handling edilemez, conditional driver operations yapılamaz
     * Diğer metodlarla kıyasla: getDriver exception fırlatır, bu metod boolean döner; quitDriver ile cleanup kontrolü için kullanılır
     * Çağrıldığı yerler: Test teardown hooks, BaseTest.tearDown(), retry logic, error handling scenarios
     * Bağımlılıkları: driverThreadLocal (thread safety için), current thread context
     */
    public static boolean isDriverInitialized() {
        // Bu satır şunu yapıyor: ThreadLocal'dan driver'ı alır ve null check yapar
        // Mantık: Driver instance mevcut mu kontrol eder, thread güvenliği sağlar
        // Bu satır olmadan: Driver durumu check edilemez, testler yanlış durumlarla çalışır
        return driverThreadLocal.get() != null;
    }

    /**
     * DRIVER SONLANDIRMA VE TEMIZLEME METODİ
     * Amaç: Mevcut thread'in driver instance'ını güvenli şekilde sonlandırır ve temizler
     * Parametreler: Parametre almaz (thread-specific driver'ı operate eder)
     * Return değeri: Void (cleanup operation, side effects var)
     * Kullanılmazsa etki: Memory leaks, resource leaks (WebDriverAgent/UiAutomator2 processes), port conflicts, device state contamination, CI/CD pipeline crash
     * Diğer metodlarla kıyasla: initializeDriver ile zıt işlem (create vs. destroy), getDriver ile fark retrieve vs. destroy, resetApp ile fark app restart vs. driver quit
     * Çağrıldığı yerler: Test teardown hooks, BaseTest.tearDown(), exception handling, test suite finalization, CI/CD cleanup phases
     * Bağımlılıkları: driverThreadLocal (thread safety için), AppiumDriver.quit(), exception handling
     */
    public static void quitDriver() {
        // Bu satır şunu yapıyor: ThreadLocal'dan driver instance'ını alır
        // Mantık: Null olabilir, doğrudan erişim efficiency için ve thread güvenliği sağlar
        // Bu satır olmadan: Driver alınamaz, temizleme işlemi yapılamaz
        AppiumDriver driver = driverThreadLocal.get();

        // Bu satır şunu yapıyor: Driver'ın null olup olmadığını kontrol eder
        // Mantık: Sadece mevcut driver'ları quit eder, gereksiz işlem önler
        // Bu satır olmadan: Null driver üzerinde quit denemesi exception'a yol açar, testler çöker
        if (driver != null) {
            // Bu satır şunu yapıyor: Try-catch-finally block ile exception-safe cleanup başlatır
            // Mantık: Hata olsa bile cleanup garantisi sağlar, kaynak sızıntısını önler
            // Bu satır olmadan: Exception durumunda cleanup skip edilebilir, kaynaklar açık kalır
            try {
                // Bu satır şunu yapıyor: Driver quit operation başlangıcını loglar
                // Mantık: Quit operation'ları track eder, işlem izlenebilirliğini sağlar
                // Bu satır olmadan: Quit operation'ları takip edilemez, durum bilinmez
                logger.info("Quitting driver session");

                // Bu satır şunu yapıyor: AppiumDriver.quit ile session sonlandırır
                // Mantık: Server'a quit command gönderir, kaynakları serbest bırakır
                // Bu satır olmadan: Driver session active kalır, kaynak sızıntısı oluşur
                driver.quit();

                // Bu satır şunu yapıyor: Successful quit operation completion log'unu kaydeder
                // Mantık: Quit success/failure'ı doğrular, hata ayıklamayı kolaylaştırır
                // Bu satır olmadan: Quit success/failure belli olmaz, durum izlenemez
                logger.info("Driver session quit successfully");

            } catch (Exception e) {
                // Bu satır şunu yapıyor: Quit operation exception'ını loglar
                // Mantık: Cleanup process'ini kesintiye uğratmamak için hata kaydeder, hata ayıklamayı sağlar
                // Bu satır olmadan: Exception durumunda ThreadLocal cleanup skip edilir, kaynaklar serbest kalmaz
                logger.error("Error occurred while quitting driver", e);

            } finally {
                // Bu satır şunu yapıyor: ThreadLocal'dan driver reference'ını temizler
                // Mantık: Finally block ile garanti sağlar, memory cleanup yapar
                // Bu satır olmadan: Stale driver reference kalır, memory sızıntısı oluşur
                driverThreadLocal.remove();

                // Bu satır şunu yapıyor: ThreadLocal cleanup completion log'unu kaydeder
                // Mantık: Memory cleanup'ının yapıldığını doğrular, durum izlenebilirliğini sağlar
                // Bu satır olmadan: Memory cleanup durumu belli olmaz, hata ayıklamada zorluk çıkar
                logger.info("Driver removed from ThreadLocal");
            }
        } else {
            // Bu satır şunu yapıyor: No driver durumunda uyarı loglar
            // Mantık: Already quit veya initialize edilmemiş durumu bildirir, gereksiz işlem önler
            // Bu satır olmadan: Unnecessary quit calls silent kalır, durum bilinmez
            logger.warn("No driver instance found to quit");
        }
    }

    /**
     * UYGULAMA SIFIRLAMA METODİ (APP RESTART EQUİVALENTİ)
     * Amaç: Mevcut app'i tamamen restart eder (force close + fresh start)
     * Parametreler: Parametre almaz (mevcut driver instance'ı kullanır)
     * Return değeri: Void (app state reset operation)
     * Kullanılmazsa etki: App state contamination, previous test data app memory'sinde kalır, clean slate testing yapılamaz, flaky tests oluşur
     * Diğer metodlarla kıyasla: quitDriver ile fark complete driver destruction vs. app restart, backgroundApp ile fark temporary hide vs. restart
     * Çağrıldığı yerler: Test data cleanup scenarios, between test methods, error recovery, fresh start scenarios
     * Bağımlılıkları: getDriver (active driver session), AppiumDriver.resetApp(), app support for restart
     */
    public static void resetApp() {
        // Bu satır şunu yapıyor: Mevcut driver instance'ını alır
        // Mantık: getDriver ile null check ve retrieval sağlar, aktif driver'a erişim sağlar
        // Bu satır olmadan: Driver alınamaz, reset işlemi yapılamaz
        AppiumDriver driver = getDriver();

        // Bu satır şunu yapıyor: Try-catch block ile reset operation exception handling başlatır
        // Mantık: Hata durumlarını yakalamak için, hata yönetimini sağlar
        // Bu satır olmadan: Exception durumunda test akışı kırılır, hata yakalanmaz
        try {
            // Bu satır şunu yapıyor: App reset operation başlangıcını loglar
            // Mantık: Reset operations tracking sağlar, işlem izlenebilirliğini artırır
            // Bu satır olmadan: Reset operations monitoring yapılamaz, durum bilinmez
            logger.info("Resetting application");

            // Bu satır şunu yapıyor: AppiumDriver.resetApp ile app'i restart eder
            // Mantık: Force close + fresh start ile app state'i sıfırlar, temiz bir başlangıç sağlar
            // Bu satır olmadan: App state contaminated kalır, testler tutarsız sonuçlar verir
            driver.executeScript("mobile: terminateApp", ImmutableMap.of("appId", "com.hepsiburada.ecommerce"));
            driver.executeScript("mobile: activateApp", ImmutableMap.of("appId", "com.hepsiburada.ecommerce"));

            // Bu satır şunu yapıyor: Successful reset operation completion log'unu kaydeder
            // Mantık: Reset success/failure'ı doğrular, hata ayıklamayı kolaylaştırır
            // Bu satır olmadan: Reset success/failure belli olmaz, durum izlenemez
            logger.info("Application reset successfully");

        } catch (Exception e) {
            // Bu satır şunu yapıyor: Reset operation exception'ını loglar
            // Mantık: Hata kaynağını kaydeder, debugging için kök neden analizi sağlar
            // Bu satır olmadan: Reset failures root cause analysis yapılamaz, hata ayıklama zorlaşır
            logger.error("Error occurred while resetting application", e);

            // Bu satır şunu yapıyor: RuntimeException ile hatayı standardize eder
            // Mantık: Calling code'ye hatayı bildirir, hata yönetimini standardize eder
            // Bu satır olmadan: Calling code reset failure'ından habersiz kalır, testler beklenmedik şekilde devam eder
            throw new RuntimeException("Failed to reset application", e);
        }
    }

    /**
     * UYGULAMAYI BACKGROUND'A ALMA METODİ
     * Amaç: App'i belirtilen süre boyunca background'a alır, sonra foreground'a getirir
     * Parametreler: duration - Background'da kalma süresi (saniye cinsinden) olarak int alır
     * Return değeri: Void (background/foreground operation)
     * Kullanılmazsa etki: Background/foreground transition testleri yapılamaz, app lifecycle testing eksik kalır, background data refresh scenarios test edilemez
     * Diğer metodlarla kıyasla: resetApp ile fark complete restart vs. temporary background, quitDriver ile fark session destroy vs. temporary hide
     * Çağrıldığı yerler: Background refresh testing, app lifecycle validation, memory management tests, real-world scenario simulations
     * Bağımlılıkları: getDriver (active driver session), AppiumDriver.runAppInBackground(), Duration API, platform background policies
     */
    public static void backgroundApp(int duration) {
        // Bu satır şunu yapıyor: Mevcut driver instance'ını alır
        // Mantık: getDriver ile null check ve retrieval sağlar, aktif driver'a erişim sağlar
        // Bu satır olmadan: Driver alınamaz, background işlemi yapılamaz
        AppiumDriver driver = getDriver();

        // Bu satır şunu yapıyor: Try-catch block ile background operation exception handling başlatır
        // Mantık: Hata durumlarını yakalamak için, hata yönetimini sağlar
        // Bu satır olmadan: Exception durumunda test akışı kırılır, hata yakalanmaz
        try {
            // Bu satır şunu yapıyor: Background operation başlangıcını loglar
            // Mantık: Duration bilgisi ile monitoring sağlar, işlem izlenebilirliğini artırır
            // Bu satır olmadan: Background operations monitoring yapılamaz, durum bilinmez
            logger.info("Putting application in background for {} seconds", duration);

            // Bu satır şunu yapıyor: App'i belirtilen süre boyunca background'a alır
            // Mantık: AppiumDriver.runAppInBackground ile transition sağlar, uygulama döngüsünü simüle eder
            // Bu satır olmadan: Background transition test edilemez, lifecycle testleri eksik kalır
            driver.executeScript("mobile: runAppInBackground", ImmutableMap.of("seconds", 5));

            // Bu satır şunu yapıyor: Background operation completion log'unu kaydeder
            // Mantık: App'in foreground'a döndüğünü doğrular, işlem izlenebilirliğini sağlar
            // Bu satır olmadan: Background/foreground cycle completion tracking yapılamaz, durum bilinmez
            logger.info("Application returned from background");

        } catch (Exception e) {
            // Bu satır şunu yapıyor: Background operation exception'ını loglar
            // Mantık: Hata kaynağını kaydeder, troubleshooting için kök neden analizi sağlar
            // Bu satır olmadan: Background failures root cause belli olmaz, hata ayıklamada zorluk çıkar
            logger.error("Error occurred while backgrounding application", e);

            // Bu satır şunu yapıyor: RuntimeException ile hatayı standardize eder
            // Mantık: Calling code'ye hatayı bildirir, hata yönetimini standardize eder
            // Bu satır olmadan: Calling code background failure'ından habersiz kalır, testler beklenmedik şekilde devam eder
            throw new RuntimeException("Failed to background application", e);
        }
    }

    /**
     * MEVCUT DRIVER'IN PLATFORM ADI ALMA METODİ
     * Amaç: Aktif driver'ın hangi platform için çalıştığını return eder
     * Parametreler: Parametre almaz (mevcut driver'ın capabilities'lerini okur)
     * Return değeri: String - Platform adı ("Android" veya "iOS")
     * Kullanılmazsa etki: Platform-specific test logic yazılamaz, conditional assertions yapılamaz, cross-platform test reports eksik kalır
     * Diğer metodlarla kıyasla: getDeviceName ile fark platform vs. device, static configuration vs. runtime driver state
     * Çağrıldığı yerler: Platform-specific test assertions, conditional test flows, error messages, test reporting, debug logging
     * Bağımlılıkları: getDriver (active driver session), AppiumDriver.getCapabilities(), "platformName" capability
     */
    public static String getPlatformName() {
        // Bu satır şunu yapıyor: Mevcut driver instance'ını alır
        // Mantık: getDriver ile null check ve retrieval sağlar, aktif driver'a erişim sağlar
        // Bu satır olmadan: Driver alınamaz, platform bilgisi alınamaz
        AppiumDriver driver = getDriver();

        // Bu satır şunu yapıyor: Driver capabilities'lerinden platform name'i alır ve string'e çevirir
        // Mantık: Runtime driver state'i sorgular, dinamik platform bilgisi sağlar
        // Bu satır olmadan: Platform-specific test logic yapılamaz, testler platforma özgü kararlar veremez
        return driver.getCapabilities().getCapability("platformName").toString();
    }

    /**
     * MEVCUT DRIVER'IN CİHAZ ADI ALMA METODİ
     * Amaç: Aktif driver'ın hangi cihaz/emulator üzerinde çalıştığını return eder
     * Parametreler: Parametre almaz (mevcut driver'ın capabilities'lerini okur)
     * Return değeri: String - Cihaz adı ("Samsung Galaxy S21", "iPhone 14", "Android Emulator", vs.)
     * Kullanılmazsa etki: Device-specific test reports eksik bilgi, debug sırasında cihaz belli olmaz, device-aware test logic yazılamaz
     * Diğer metodlarla kıyasla: getPlatformName ile fark platform vs. device, static configuration vs. runtime driver state
     * Çağrıldığı yerler: Test reports, debug logging, device-specific test logic, performance measurements, parallel execution monitoring
     * Bağımlılıkları: getDriver (active driver session), AppiumDriver.getCapabilities(), "deviceName" capability, device configuration
     */
    public static String getDeviceName() {
        // Bu satır şunu yapıyor: Mevcut driver instance'ını alır
        // Mantık: getDriver ile null check ve retrieval sağlar, aktif driver'a erişim sağlar
        // Bu satır olmadan: Driver alınamaz, cihaz bilgisi alınamaz
        AppiumDriver driver = getDriver();

        // Bu satır şunu yapıyor: Driver capabilities'lerinden device name'i alır ve string'e çevirir
        // Mantık: Runtime driver state'i sorgular, dinamik cihaz bilgisi sağlar
        // Bu satır olmadan: Device-specific test context bilgisi alınamaz, testler cihaz farkındalığı olmadan çalışır
        return driver.getCapabilities().getCapability("deviceName").toString();
    }
}