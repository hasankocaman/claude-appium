// Class Amacı: DriverManager, Appium driver'ının yaşam döngüsünü (lifecycle) yöneten bir utility class'tır. Test sınıfları ve page object'ler için AppiumDriver'a erişim sağlar, driver başlatma, sonlandırma ve durum kontrolü gibi işlemleri merkezi bir şekilde koordine eder.
// Neden Gerekli: Testlerde driver yönetimini basitleştirir, kod tekrarını önler (DRY prensibi), ve thread-safe bir API sunar.
// Kullanılmazsa Ne Olur: Test sınıfları doğrudan DriverFactory ile çalışmak zorunda kalır, bu da kod karmaşasına ve tutarsızlığa yol açar. Driver yaşam döngüsü kontrol edilmezse memory leak, açık session'lar veya çakışmalar oluşabilir.
// Diğer Class'larla İlişkisi:
// - DriverFactory (has-a): Gerçek driver yaratımını DriverFactory'e devreder.
// - ConfigurationManager (uses): Platform ve cihaz bilgilerini alır.
// - Page Object'ler (used-by): Driver'a erişim için bu class'ı kullanır.
// - TestHooks/BaseTest (used-by): Test setup/teardown için çağırır.
// Kıyaslama: DriverFactory, düşük seviyeli driver yaratımı yapar; DriverManager ise yüksek seviyeli yönetim sağlar. BaseTest'e kıyasla, sadece driver'la ilgili işlemlere odaklanır, test logic'inden ayrıştırır.
// Önem: Merkezi driver yönetimi olmadan, her test sınıfı kendi driver'ını yönetmek zorunda kalır, bu da bakım maliyetini artırır ve hata riskini yükseltir.

package com.hepsiburada.drivers;

// import com.hepsiburada.config.ConfigurationManager;
// Kütüphane: Projenin kendi config paketi.
// Amaç: Framework konfigürasyonlarını (örn., platform, timeout) properties dosyalarından okumak için kullanılır.
// Neden Gerekli: Konfigürasyon-driven test execution sağlar, hard-coded değerleri önler.
// Alternatifler: YAML (daha okunaklı ama ek dependency gerekir), System.getenv (daha az esnek). Properties seçildi çünkü Java'nın built-in desteği var, basit ve yaygın.
// Kullanılmazsa: Platform bilgisi manuel girilmeli, bu da esneklik kaybına ve hata riskine yol açar.
import com.hepsiburada.config.ConfigurationManager;

// import io.appium.java_client.AppiumDriver;
// Kütüphane: Appium Java Client (io.appium:java-client).
// Amaç: Mobil cihazlar için driver interface'i sağlar (AndroidDriver ve IOSDriver'ın parent'ı).
// Neden Gerekli: Mobil uygulamalarla etkileşim (element bulma, tıklama, gesture) için zorunlu.
// Alternatifler: Selenium WebDriver (sadece web için, mobil destek zayıf). AppiumDriver seçildi çünkü Android/iOS için native destek sunar, polymorphism sağlar.
// Kullanılmazsa: Mobil testler yapılamaz, element etkileşimleri mümkün olmaz.
import io.appium.java_client.AppiumDriver;

// import org.apache.logging.log4j.LogManager;
// Kütüphane: Log4j2 (org.apache.logging.log4j:log4j-api).
// Amaç: Loglama framework'ünün yönetim sınıfını sağlar.
// Neden Gerekli: Uygulama loglarını merkezi şekilde yönetir, debug ve monitoring için kritik.
// Alternatifler: SLF4J (daha soyut ama ek binding gerekir), Java Util Logging (daha az güçlü). Log4j2 seçildi çünkü performanslı, esnek ve özelleştirilebilir.
// Kullanılmazsa: Test süreçleri izlenemez, hata ayıklama zorlaşır.
import org.apache.logging.log4j.LogManager;

// import org.apache.logging.log4j.Logger;
// Kütüphane: Log4j2 (org.apache.logging.log4j:log4j-api).
// Amaç: Log mesajlarını yazmak için kullanılan interface.
// Neden Gerekli: Her class için loglama sağlar, hata takibi ve debugging için kullanılır.
// Alternatifler: System.out (esnek değil, yapılandırılamaz). Logger seçildi çünkü yapılandırılabilir (log level, output format) ve profesyonel.
// Kullanılmazsa: Hatalar ve süreçler takip edilemez, production-grade debugging imkansız.
import org.apache.logging.log4j.Logger;

public final class DriverManager {

    // private static final Logger logger = LogManager.getLogger(DriverManager.class);
    // Değişken: logger
    // Tip: Logger (Log4j2 interface'i)
    // Amaç: Class'ın tüm loglama işlemlerini yönetir, debug/info/error mesajlarını kaydeder.
    // Neden static: Class seviyesinde tek instance yeter, her metodda paylaşılır.
    // Neden final: Logger instance'ı değişmemeli, sabit kalmalı (immutable).
    // Neden LogManager.getLogger(DriverManager.class): Log mesajlarında class adını otomatik ekler, hangi class'tan geldiğini netleştirir.
    // Kullanılmazsa: Loglama yapılamaz, test süreçleri ve hatalar izlenemez, debugging zorlaşır.
    // Alternatifler: System.out.println (esnek değil, yapılandırılamaz). Logger seçildi çünkü log level'ları (DEBUG, INFO) ve output (file, console) özelleştirilebilir.
    private static final Logger logger = LogManager.getLogger(DriverManager.class);

    // private DriverManager() { ... }
    // Constructor: DriverManager
    // Amaç: Class'ın instance oluşturulmasını engeller (utility class pattern).
    // Neden private: Bu class sadece static metodlar içerir, nesne yaratılmasına gerek yok.
    // Neden gerekli: Utility class'ların istenmeyen instantiation'ını önler, Singleton/Utility pattern'ını korur.
    // Kullanılmazsa: Geliştiriciler yanlışlıkla DriverManager objesi yaratabilir, bu da gereksiz memory kullanımı ve kafa karışıklığına yol açar.
    // Kıyaslama: Diğer class'larda (örn., BasePage) public constructor var çünkü instance gerekli; burada sadece static metodlar olduğu için private.
    // Alternatifler: Default constructor bırakılabilir ama bu istenmeyen instantiation riski yaratır.
    private DriverManager() {
        // // Utility class should not be instantiated
        // Bu satır: Geliştiricilere class'ın utility olduğunu hatırlatan bir yorum.
        // Amaç: Kod okunabilirliğini artırmak, niyet beyanı (intent) sunmak.
        // Neden gerekli: JavaDoc benzeri bir dokümantasyon sağlar, maintenance kolaylaştırır.
        // Kullanılmazsa: Kodun amacı net olmaz, yeni geliştiriciler class'ın neden private constructor içerdiğini anlamayabilir.
        // Utility class should not be instantiated
    }

    // public static AppiumDriver initializeDriver() { ... }
    // Metod: initializeDriver (parametresiz)
    // Amaç: Framework konfigürasyon dosyasından platform bilgisini okuyarak Appium driver'ını başlatır.
    // Return: AppiumDriver (AndroidDriver veya IOSDriver olabilir, polymorphism sağlar).
    // Neden Gerekli: Testlerin otomatik platform algılamasıyla başlamasını sağlar, manuel platform belirtme ihtiyacını ortadan kaldırır.
    // Kullanılmazsa: Testler platform bilgisi olmadan çalışamaz, her testte manuel platform set edilmesi gerekir, bu da kod tekrarına yol açar.
    // Kıyaslama: initializeDriver(String platform)'dan farkı, konfigürasyondan otomatik platform okur, daha az parametreyle kullanımı kolaylaştırır.
    // Alternatifler: Manuel platform belirtme (initializeDriver(String)), ama bu daha az esnek. Konfigürasyon-driven yaklaşım seçildi çünkü CI/CD ile uyumlu ve bakım kolay.
    // Çağrıldığı Yerler: TestHooks (@Before), BaseTest setup metodları, otomatik test başlatma senaryoları.
    // Bağımlılıklar: ConfigurationManager (platform bilgisi), DriverFactory (driver yaratımı).
    // Riskler: Konfigürasyon dosyası eksikse veya yanlışsa, exception fırlatır (örneğin, NullPointerException veya IllegalArgumentException).
    public static AppiumDriver initializeDriver() {
        // String platform = ConfigurationManager.getFrameworkConfig().getPlatformName();
        // Bu satır: Konfigürasyon dosyasından platform adını (Android/iOS) alır.
        // Amaç: Driver yaratımı için platform bilgisi sağlamak.
        // Neden gerekli: DriverFactory, hangi tip driver (AndroidDriver/IOSDriver) yaratacağını bilmek için platform bilgisine ihtiyaç duyar.
        // Kullanılmazsa: Platform bilinmez, driver yaratımı başarısız olur, testler çalışmaz.
        // Değişken: platform (String)
        // Neden String: Platform isimleri (Android, iOS) string formatında, Appium DesiredCapabilities ile uyumlu. Enum alternatifi düşünülebilirdi ama string daha esnek (case-insensitive karşılaştırma).
        String platform = ConfigurationManager.getFrameworkConfig().getPlatformName();

        // logger.info("Initializing driver for platform: {}", platform);
        // Bu satır: Driver başlatma işlemini loglar, platform bilgisini içerir.
        // Amaç: Debugging ve monitoring için hangi platformda driver başlatıldığını kaydetmek.
        // Neden gerekli: Test süreçlerini izlemek, hata ayıklamada hangi platformun kullanıldığını anlamak için kritik.
        // Kullanılmazsa: Hangi platformda testlerin çalıştığı takip edilemez, hata analizi zorlaşır.
        // Log4j2 format: {} placeholder'ı platform değerini dinamik ekler, okunabilirliği artırır.
        logger.info("Initializing driver for platform: {}", platform);

        // return DriverFactory.initializeDriver(platform);
        // Bu satır: DriverFactory'e platform parametresini geçirerek driver yaratımını başlatır.
        // Amaç: Gerçek driver instance'ını (AndroidDriver/IOSDriver) döndürmek.
        // Neden gerekli: DriverFactory, düşük seviyeli driver yaratımını handle eder; bu satır olmadan driver başlatılamaz, testler çalışmaz.
        // Kullanılmazsa: Testler driver olmadan element etkileşimi yapamaz, NullPointerException fırlatır.
        // Alternatifler: Doğrudan AndroidDriver/IOSDriver yaratımı (Appium Java Client ile), ama bu kod tekrarına yol açar ve platform esnekliği kaybolur.
        return DriverFactory.initializeDriver(platform);
    }

    // public static AppiumDriver initializeDriver(String platform) { ... }
    // Metod: initializeDriver (parametreli)
    // Amaç: Belirtilen platform (Android/iOS) için Appium driver'ını başlatır.
    // Parametre: platform (String) - Hedef platform adı (örn., "Android", "iOS").
    // Return: AppiumDriver (AndroidDriver veya IOSDriver, polymorphism sağlar).
    // Neden Gerekli: Runtime'da platform seçimiyle driver başlatmayı sağlar, CI/CD pipeline'larında veya data-driven testlerde esneklik sunar.
    // Kullanılmazsa: Dinamik platform seçimi yapılamaz, testler sabit bir platforma bağlı kalır, çapraz-platform testler zorlaşır.
    // Kıyaslama: Parametresiz initializeDriver()'dan farkı, platformu manuel belirtir; bu, daha fazla kontrol sağlar ama konfigürasyon otomasyonunu bypass eder.
    // Alternatifler: Konfigürasyon bazlı başlatma (parametresiz metod), ama bu daha az esnek. Parametreli metod seçildi çünkü test senaryolarında farklı platformlar için dinamik başlatma gerekir.
    // Çağrıldığı Yerler: Parametreli testler, cross-platform runners, CI/CD script'leri.
    // Bağımlılıklar: DriverFactory (driver yaratımı).
    // Riskler: Geçersiz platform adı girilirse (örn., "Windows"), DriverFactory exception fırlatır (IllegalArgumentException).
    public static AppiumDriver initializeDriver(String platform) {
        // logger.info("Initializing driver for platform: {}", platform);
        // Bu satır: Platform bilgisini loglar, hangi platform için driver başlatıldığını kaydeder.
        // Amaç: Debugging için platform takibi sağlamak.
        // Neden gerekli: Test süreçlerinde hangi platformun kullanıldığını anlamak, hata ayıklamada kritik.
        // Kullanılmazsa: Platform bilgisi izlenemez, hata analizi eksik kalır.
        // Log4j2 format: {} placeholder'ı platform değerini dinamik ekler.
        logger.info("Initializing driver for platform: {}", platform);

        // return DriverFactory.initializeDriver(platform);
        // Bu satır: DriverFactory'e platform parametresini geçirerek driver yaratımını başlatır.
        // Amaç: Belirtilen platform için uygun driver instance'ını döndürmek.
        // Neden gerekli: DriverFactory, platforma göre AndroidDriver veya IOSDriver yaratır; bu satır olmadan driver başlatılamaz.
        // Kullanılmazsa: Testler driver olmadan çalışamaz, element etkileşimleri mümkün olmaz.
        // Alternatifler: Doğrudan AppiumDriver yaratımı, ama bu platform esnekliğini ve factory pattern avantajlarını kaybeder.
        return DriverFactory.initializeDriver(platform);
    }

    // public static AppiumDriver getDriver() { ... }
    // Metod: getDriver
    // Amaç: Aktif AppiumDriver instance'ını döndürür, page object'ler ve test sınıfları için driver erişimi sağlar.
    // Parametre: Yok.
    // Return: AppiumDriver (mevcut driver, null olabilir eğer başlatılmamışsa).
    // Neden Gerekli: Page object'ler ve test adımları, UI etkileşimleri için driver'a erişmek zorundadır; bu metod merkezi erişim sağlar.
    // Kullanılmazsa: Page object'ler driver'a ulaşamaz, element bulma/tıklama gibi işlemler yapılamaz, testler çöker.
    // Kıyaslama: isDriverInitialized()'dan farkı, boolean yerine driver instance'ı döndürür; en sık kullanılan metod çünkü her UI etkileşimi için gerekli.
    // Alternatifler: Doğrudan DriverFactory.getDriver() çağırmak, ama bu tight coupling yaratır ve thread-safety riski artırır.
    // Çağrıldığı Yerler: Page object constructor'ları, test adımları, utility metodlar (screenshot, gesture).
    // Bağımlılıklar: DriverFactory (ThreadLocal driver storage).
    // Riskler: Driver başlatılmamışsa null döner, NullPointerException riski var (isDriverInitialized() ile kontrol önerilir).
    public static AppiumDriver getDriver() {
        // return DriverFactory.getDriver();
        // Bu satır: DriverFactory'den aktif driver instance'ını alır ve döndürür.
        // Amaç: Page object'ler ve test sınıfları için driver erişimi sağlamak.
        // Neden gerekli: UI etkileşimleri (findElement, click) için driver zorunlu.
        // Kullanılmazsa: Testler driver olmadan çalışamaz, NullPointerException fırlatır.
        // Alternatifler: Global static driver değişkeni tutmak, ama bu thread-safety sorunlarına yol açar (özellikle paralel testlerde).
        return DriverFactory.getDriver();
    }

    // public static boolean isDriverInitialized() { ... }
    // Metod: isDriverInitialized
    // Amaç: Driver'ın başlatılıp başlatılmadığını kontrol eder, güvenli driver erişimi için guard clause sağlar.
    // Parametre: Yok.
    // Return: Boolean (true: driver başlatılmış, false: başlatılmamış).
    // Neden Gerekli: Driver null kontrolü yaparak NullPointerException önler, defensive programming için kritik.
    // Kullanılmazsa: Driver durumu bilinmeden getDriver() çağrılırsa exception riski artar, testler başarısız olur.
    // Kıyaslama: getDriver()'dan farkı, driver yerine durum kontrolü yapar; daha güvenli bir ön kontrol sağlar.
    // Alternatifler: Doğrudan try-catch ile null kontrolü, ama bu kod karmaşasını artırır. Boolean kontrol seçildi çünkü daha clean ve performanslı.
    // Çağrıldığı Yerler: Test teardown'ları, error handling blokları, conditional driver işlemleri.
    // Bağımlılıklar: DriverFactory (driver state yönetimi).
    // Riskler: Yanlış durum kontrolü durumunda (örn., race condition), hatalı sonuç dönebilir.
    public static boolean isDriverInitialized() {
        // return DriverFactory.isDriverInitialized();
        // Bu satır: DriverFactory'den driver'ın başlatılma durumunu kontrol eder ve sonucu döndürür.
        // Amaç: Driver'ın varlığını doğrulamak, güvenli erişim sağlamak.
        // Neden gerekli: NullPointerException önlemek için driver durumunu bilmek şart.
        // Kullanılmazsa: Driver null olabilir ve testler çöker.
        // Alternatifler: getDriver() sonrası null kontrolü, ama bu daha az elegant ve tekrarlı.
        return DriverFactory.isDriverInitialized();
    }

    // public static void quitDriver() { ... }
    // Metod: quitDriver
    // Amaç: Aktif driver session'ını sonlandırır, cihaz kaynaklarını serbest bırakır.
    // Parametre: Yok.
    // Return: Void.
    // Neden Gerekli: Test sonrası cleanup için zorunlu, açık session'lar memory leak ve cihaz çakışmasına yol açar.
    // Kullanılmazsa: Driver session'ları açık kalır, cihaz kaynakları tükenir, paralel testlerde çakışmalar olur.
    // Kıyaslama: resetApp()'ten farkı, uygulamayı resetlemez, driver'ı tamamen kapatır; backgroundApp()'ten farkı, uygulamayı arka plana almaz.
    // Alternatifler: Doğrudan driver.quit() çağırmak, ama bu thread-safety ve merkezi yönetim avantajlarını kaybeder.
    // Çağrıldığı Yerler: Test teardown (@AfterMethod, @AfterClass), failure handling, CI/CD cleanup.
    // Bağımlılıklar: DriverFactory (session yönetimi).
    // Riskler: Driver zaten kapalıysa, exception fırlatabilir (IllegalStateException); isDriverInitialized() ile kontrol önerilir.
    public static void quitDriver() {
        // logger.info("Quitting driver session");
        // Bu satır: Driver sonlandırma işlemini loglar.
        // Amaç: Cleanup süreçlerini izlemek, debugging için hangi driver'ın ne zaman kapandığını kaydetmek.
        // Neden gerekli: Test süreçlerinde cleanup takibi kritik, hata analizini kolaylaştırır.
        // Kullanılmazsa: Hangi driver'ların kapandığı bilinmez, debugging zorlaşır.
        logger.info("Quitting driver session");

        // DriverFactory.quitDriver();
        // Bu satır: DriverFactory'e driver sonlandırma komutunu gönderir.
        // Amaç: Aktif driver session'ını kapatmak, cihaz kaynaklarını serbest bırakmak.
        // Neden gerekli: Açık session'lar memory leak ve çakışmalara yol açar; bu satır olmadan cleanup tamamlanmaz.
        // Kullanılmazsa: Cihazda açık session'lar kalır, paralel testler çöker.
        // Alternatifler: Doğrudan driver.quit(), ama bu thread-safety ve merkezi kontrol avantajlarını kaybeder.
        DriverFactory.quitDriver();
    }

    // public static void resetApp() { ... }
    // Metod: resetApp
    // Amaç: Mobil uygulamayı sıfırlayarak fresh start durumuna getirir (test isolation için).
    // Parametre: Yok.
    // Return: Void.
    // Neden Gerekli: Testler arasında veri kirliliğini (data pollution) önler, her testin clean state ile başlamasını sağlar.
    // Kullanılmazsa: Önceki testlerin verileri yeni testleri etkiler, flaky testler artar, test isolation kaybolur.
    // Kıyaslama: quitDriver()'dan farkı, driver'ı kapatmaz, sadece uygulamayı sıfırlar; backgroundApp()'ten farkı, uygulamayı arka plana almaz, yeniden başlatır.
    // Alternatifler: Manuel veri temizleme (örn., cache clear), ama bu daha yavaş ve güvenilir değil. resetApp seçildi çünkü Appium'in built-in özelliği, hızlı ve etkili.
    // Çağrıldığı Yerler: Test setup'ları, senaryolar arası cleanup, data-driven testler.
    // Bağımlılıklar: DriverFactory (app lifecycle yönetimi).
    // Riskler: Uygulama sıfırlama desteklenmiyorsa (nadir), exception fırlatabilir (UnsupportedOperationException).
    public static void resetApp() {
        // logger.info("Resetting application");
        // Bu satır: Uygulama sıfırlama işlemini loglar.
        // Amaç: Test isolation süreçlerini izlemek, hangi uygulamanın ne zaman sıfırlandığını kaydetmek.
        // Neden gerekli: Debugging için sıfırlama işlemlerini takip etmek kritik.
        // Kullanılmazsa: Sıfırlama süreçleri izlenemez, test isolation sorunları tespit edilemez.
        logger.info("Resetting application");

        // DriverFactory.resetApp();
        // Bu satır: DriverFactory'e uygulama sıfırlama komutunu gönderir.
        // Amaç: Uygulamayı fresh start durumuna getirmek, test veri kirliliğini önlemek.
        // Neden gerekli: Test isolation için zorunlu; bu satır olmadan önceki testlerin verileri kalır.
        // Kullanılmazsa: Flaky testler artar, test sonuçları güvenilmez olur.
        // Alternatifler: Manuel cache temizleme, ama bu daha karmaşık ve platforma bağımlı.
        DriverFactory.resetApp();
    }

    // public static void backgroundApp(int duration) { ... }
    // Metod: backgroundApp
    // Amaç: Mobil uygulamayı belirtilen süre boyunca arka plana alır, lifecycle testleri için kullanılır.
    // Parametre: duration (int) - Arka planda kalma süresi (saniye cinsinden).
    // Return: Void.
    // Neden Gerekli: Gerçek dünya senaryolarını simüle eder (örn., app arka plana alındığında state korunmalı mı), lifecycle testleri için kritik.
    // Kullanılmazsa: Background behavior testleri yapılamaz, app lifecycle senaryoları test edilemez, gerçek kullanıcı deneyimi doğrulanamaz.
    // Kıyaslama: resetApp()'ten farkı, uygulamayı sıfırlamaz, sadece arka plana alır; quitDriver()'dan farkı, driver'ı kapatmaz, uygulama çalışır durumda kalır.
    // Alternatifler: Manuel lifecycle simülasyonu (örn., ADB komutları), ama bu platforma bağımlı ve karmaşık. Appium'in backgroundApp özelliği seçildi çünkü cross-platform ve basit.
    // Çağrıldığı Yerler: Lifecycle test senaryoları, background/foreground testleri, performans testleri.
    // Bağımlılıklar: DriverFactory (app state yönetimi).
    // Riskler: Negatif duration veya desteklenmeyen platformda exception fırlatabilir (IllegalArgumentException).
    public static void backgroundApp(int duration) {
        // logger.info("Putting application in background for {} seconds", duration);
        // Bu satır: Uygulamanın arka plana alınma işlemini ve süresini loglar.
        // Amaç: Lifecycle testlerini izlemek, hangi uygulamanın ne kadar süre arka planda kaldığını kaydetmek.
        // Neden gerekli: Debugging ve test takibi için kritik, özellikle lifecycle senaryolarında.
        // Kullanılmazsa: Arka plan işlemleri izlenemez, hata analizi zorlaşır.
        // Log4j2 format: {} placeholder'ı duration değerini dinamik ekler.
        logger.info("Putting application in background for {} seconds", duration);

        // DriverFactory.backgroundApp(duration);
        // Bu satır: DriverFactory'e arka plana alma komutunu ve süreyi gönderir.
        // Amaç: Uygulamayı belirtilen süre boyunca arka plana almak, lifecycle davranışını test etmek.
        // Neden gerekli: Gerçek dünya senaryolarını simüle etmek için (örn., notification testleri).
        // Kullanılmazsa: Lifecycle testleri yapılamaz, app state doğrulanamaz.
        // Alternatifler: Platform-specific komutlar (ADB, XCUITest), ama bu cross-platform desteği kaybeder.
        DriverFactory.backgroundApp(duration);
    }

    // public static String getPlatformName() { ... }
    // Metod: getPlatformName
    // Amaç: Şu anki platform adını (Android/iOS) döndürür, platform-specific logic için kullanılır.
    // Parametre: Yok.
    // Return: String (platform adı, örn., "Android", "iOS").
    // Neden Gerekli: Platform-specific kod yazımı (örn., locator stratejileri) ve raporlama için platform bilgisi şart.
    // Kullanılmazsa: Platform-specific test logic yazılamaz, cross-platform testler desteklenemez, raporlar platform bazlı gruplanamaz.
    // Kıyaslama: isAndroid()/isIOS()'tan farkı, boolean yerine string döner, daha fazla bilgi sağlar; getDeviceName()'den farkı, cihaz yerine platform odaklı.
    // Alternatifler: Hard-coded platform bilgisi, ama bu esneklik kaybına yol açar. String dönüş seçildi çünkü Appium DesiredCapabilities ile uyumlu ve esnek.
    // Çağrıldığı Yerler: Platform-specific test adımları, raporlama, conditional locators.
    // Bağımlılıklar: DriverFactory (platform bilgisi saklama).
    // Riskler: Platform bilgisi yoksa null dönebilir, NullPointerException riski var.
    public static String getPlatformName() {
        // return DriverFactory.getPlatformName();
        // Bu satır: DriverFactory'den platform adını alır ve döndürür.
        // Amaç: Platform-specific logic ve raporlama için platform bilgisi sağlamak.
        // Neden gerekli: Testlerin platforma göre davranışı değişir (örn., Android'de id, iOS'ta XCUITest locator).
        // Kullanılmazsa: Platform bilinmez, cross-platform testler çalışmaz.
        // Alternatifler: Konfigürasyondan direkt okuma, ama bu DriverFactory'nin merkezi yönetim avantajını kaybeder.
        return DriverFactory.getPlatformName();
    }

    // public static String getDeviceName() { ... }
    // Metod: getDeviceName
    // Amaç: Test edilen cihazın adını döndürür (örn., "Samsung Galaxy S21"), raporlama ve debugging için kullanılır.
    // Parametre: Yok.
    // Return: String (cihaz adı).
    // Neden Gerekli: Cihaz bazlı raporlama, debugging ve test kategorizasyonu için cihaz bilgisi gerekir.
    // Kullanılmazsa: Test sonuçları cihaz bazlı gruplanamaz, debugging'de cihaz bilgisi eksik kalır.
    // Kıyaslama: getPlatformName()'den farkı, platform yerine spesifik cihaz bilgisi verir; isAndroid()/isIOS()'tan farkı, boolean yerine string döner.
    // Alternatifler: Cihaz adını konfigürasyondan okumak, ama bu DriverFactory'nin merkezi yönetim avantajını kaybeder.
    // Çağrıldığı Yerler: Raporlama, debugging logları, cihaz-specific test logic.
    // Bağımlılıklar: DriverFactory (cihaz bilgisi yönetimi).
    // Riskler: Cihaz bilgisi yoksa null dönebilir, NullPointerException riski var.
    public static String getDeviceName() {
        // return DriverFactory.getDeviceName();
        // Bu satır: DriverFactory'den cihaz adını alır ve döndürür.
        // Amaç: Cihaz bazlı raporlama ve debugging için cihaz bilgisi sağlamak.
        // Neden gerekli: Test sonuçlarını cihazlara göre kategorize etmek, hata analizinde cihaz farklarını anlamak için.
        // Kullanılmazsa: Cihaz bilgisi bilinmez, test raporları eksik kalır.
        // Alternatifler: Konfigürasyondan direkt cihaz adı okuma, ama bu merkezi yönetim avantajını kaybeder.
        return DriverFactory.getDeviceName();
    }

    // public static boolean isAndroid() { ... }
    // Metod: isAndroid
    // Amaç: Mevcut platformun Android olup olmadığını kontrol eder, platform-specific logic için kullanılır.
    // Parametre: Yok.
    // Return: Boolean (true: Android, false: değil).
    // Neden Gerekli: Android-specific test adımları (örn., id locator'lar, Android gesture'lar) için platform kontrolü şart.
    // Kullanılmazsa: Platform-specific kod yazılamaz, testler yanlış locator veya davranış kullanır, hatalar artar.
    // Kıyaslama: isIOS()'tan farkı, Android odaklı; getPlatformName()'den farkı, string yerine boolean döner, conditional logic için daha pratik.
    // Alternatifler: getPlatformName() ile manuel string karşılaştırma, ama bu daha karmaşık ve hata eğilimli.
    // Çağrıldığı Yerler: Android-specific locators, test adımları, timeout stratejileri.
    // Bağımlılıklar: getPlatformName() (platform bilgisi).
    // Riskler: Platform bilgisi null ise, equalsIgnoreCase exception fırlatabilir (NullPointerException).
    public static boolean isAndroid() {
        // return "Android".equalsIgnoreCase(getPlatformName());
        // Bu satır: Platform adını alır ve Android olup olmadığını case-insensitive karşılaştırır.
        // Amaç: Platformun Android olduğunu doğrulamak, conditional logic için boolean sağlamak.
        // Neden gerekli: Android-specific test adımları için platform kontrolü şart.
        // Kullanılmazsa: Yanlış platform varsayımıyla testler çalışır, hatalar (örn., yanlış locator) oluşur.
        // equalsIgnoreCase: Büyük/küçük harf duyarlılığını önler, "Android", "android", "ANDROID" gibi varyasyonları kabul eder.
        return "Android".equalsIgnoreCase(getPlatformName());
    }

    // public static boolean isIOS() { ... }
    // Metod: isIOS
    // Amaç: Mevcut platformun iOS olup olmadığını kontrol eder, platform-specific logic için kullanılır.
    // Parametre: Yok.
    // Return: Boolean (true: iOS, false: değil).
    // Neden Gerekli: iOS-specific test adımları (örn., XCUITest locator'lar, Face ID testleri) için platform kontrolü şart.
    // Kullanılmazsa: Platform-specific kod yazılamaz, testler yanlış locator veya davranış kullanır, hatalar artar.
    // Kıyaslama: isAndroid()'ten farkı, iOS odaklı; getPlatformName()'den farkı, string yerine boolean döner, conditional logic için daha pratik.
    // Alternatifler: getPlatformName() ile manuel string karşılaştırma, ama bu daha karmaşık ve hata eğilimli.
    // Çağrıldığı Yerler: iOS-specific locators, test adımları, gesture implementasyonları.
    // Bağımlılıklar: getPlatformName() (platform bilgisi).
    // Riskler: Platform bilgisi null ise, equalsIgnoreCase exception fırlatabilir (NullPointerException).
    public static boolean isIOS() {
        // return "iOS".equalsIgnoreCase(getPlatformName());
        // Bu satır: Platform adını alır ve iOS olup olmadığını case-insensitive karşılaştırır.
        // Amaç: Platformun iOS olduğunu doğrulamak, conditional logic için boolean sağlamak.
        // Neden gerekli: iOS-specific test adımları için platform kontrolü şart.
        // Kullanılmazsa: Yanlış platform varsayımıyla testler çalışır, hatalar (örn., yanlış locator) oluşur.
        // equalsIgnoreCase: Büyük/küçük harf duyarlılığını önler, "iOS", "ios", "IOS" gibi varyasyonları kabul eder.
        return "iOS".equalsIgnoreCase(getPlatformName());
    }
}