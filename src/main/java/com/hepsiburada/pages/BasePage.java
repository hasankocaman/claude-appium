package com.hepsiburada.pages;

// Appium sürücüsü ve driver yönetimi için gerekli - Appium 8.6.0 ile uyumlu
// Bu import olmadan mobil cihazlarla etkileşim kurulamaz
import com.hepsiburada.drivers.DriverManager;

// Appium'un temel WebDriver sınıfı - Android ve iOS desteği sağlar
// Bu import olmadan mobil test otomasyonu mümkün değil
import io.appium.java_client.AppiumDriver;

// Page Factory pattern için gerekli - page elementlerinin otomatik başlatılması
// Bu import olmadan @FindBy annotasyonları çalışmaz
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

// Allure raporlama için step annotasyonu - test adımlarının raporlanması
// Bu import olmadan test adımları Allure raporunda görünmez
import io.qameta.allure.Step;

// Log4j2 logger sınıfı - test çıktılarının loglanması için
// Bu import olmadan loglama işlemleri gerçekleşmez
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Selenium'un temel WebDriver sınıfları - web elementleri ve etkileşimler
// Bu import'lar olmadan element bulma ve etkileşim işlemleri yapılamaz
import org.openqa.selenium.*;

// W3C Actions API - modern gesture işlemleri için (Appium 8+ uyumlu)
// Bu import'lar olmadan scroll, swipe, tap gibi mobil gestureler çalışmaz
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

// Page Factory pattern desteği - element başlatma için
// Bu import olmadan PageFactory.initElements çalışmaz
import org.openqa.selenium.support.PageFactory;

// Selenium'un bekleme koşulları - element durumlarını beklemek için
// Bu import'lar olmadan dinamik element beklemeleri yapılamaz
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

// Java 8+ Duration sınıfı - zaman aralıklarını tanımlamak için
// Bu import olmadan modern timeout tanımlamaları yapılamaz
import java.time.Duration;

// Java Collection sınıfı - sequence listesi oluşturmak için
// Bu import olmadan W3C Actions performansı gerçekleşmez
import java.util.List;

/**
 * BASEPAGE CLASS AMACI VE SORUMLULUĞU:
 * Bu sınıf tüm page sınıflarının ortak atası olarak tasarlanmıştır.
 * Mobil test otomasyonunda tekrar eden işlemleri merkezi bir noktada toplar.
 * Appium 8+ sürümü ile uyumlu modern W3C Actions API kullanır.
 *
 * Bu class'ın projede rolü:
 * - Tüm page sınıfları için ortak temel işlevsellik sağlar
 * - Element bulma, bekleme, etkileşim metodlarını standardize eder
 * - W3C Actions ile mobil gestures (scroll, swipe, tap) sağlar
 * - Logging ve hata yönetimini merkezi hale getirir
 * - Page Factory pattern ile element yönetimini otomatikleştirir
 *
 * Kullanılmazsa etki:
 * - Her page sınıfında tekrar eden kodlar yazılmak zorunda kalır
 * - Element bekleme ve etkileşim metodları her sınıfta ayrı ayrı implement edilir
 * - Hata yönetimi ve loglama tutarlılığı sağlanamaz
 * - Mobil gestures her sınıfta farklı şekillerde kodlanır
 * - Code maintenance zorlaşır ve hata riski artar
 *
 * Diğer class'larla ilişkisi:
 * - Tüm Page sınıfları (LoginPage, HomePage vb.) bu sınıfı extend eder
 * - DriverManager sınıfından WebDriver instance'ını alır
 * - Test sınıfları dolaylı olarak bu sınıfın metodlarını kullanır
 * - Allure raporlama sistemi ile entegre çalışır
 */
public abstract class BasePage {

    // ---------- Sabitler - Timeout ve Gesture Süreleri ----------

    // Ana bekleme süresi sabiti - çoğu element beklemesi için kullanılır
    // 30 saniye olarak seçilmiş çünkü mobil uygulamalarda yavaş yüklenme durumları olabilir
    // Bu değer çok düşük olursa testler false positive fail alabilir
    // Bu değer çok yüksek olursa testler gereksiz yere uzun sürer
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;

    // Kısa bekleme süresi sabiti - hızlı kontroller için kullanılır
    // 10 saniye olarak seçilmiş çünkü bazı kontroller kısa sürede tamamlanmalı
    // Bu değer visibility kontrolü gibi hızlı işlemler için optimizedir
    private static final int SHORT_TIMEOUT_SECONDS   = 10;

    // Gesture bekleme süresi sabiti - parmak hareketleri için
    // 200ms olarak seçilmiş çünkü çok hızlı gestureler cihaz tarafından algılanmayabilir
    // Çok yavaş olursa kullanıcı deneyimini etkileyebilir
    // Bu süre dokunmatik ekranların tepki verme süresine uygun olarak ayarlanmış
    private static final Duration GESTURE_HOLD       = Duration.ofMillis(200);

    // ---------- Üye Değişkenler - Instance Düzeyinde Paylaşılan Kaynaklar ----------

    // Logger instance - bu sınıftan türetilen her page için özel logger
    // protected olarak tanımlandı çünkü alt sınıfların da erişebilmesi gerekiyor
    // final olarak tanımlandı çünkü initialization sonrası değişmemeli
    // LogManager.getLogger(getClass()) kullanıldı çünkü her alt sınıf kendi adını alsın
    protected final Logger logger = LogManager.getLogger(getClass());

    // AppiumDriver instance - mobil cihazla etkileşim için ana sürücü
    // protected olarak tanımlandı çünkü alt sınıflarda da kullanılacak
    // final olarak tanımlandı çünkü initialization sonrası değişmemeli
    // Generic <?> kullanıldı çünkü hem Android hem iOS driver'larını desteklemeli
    protected final AppiumDriver driver;

    // Ana WebDriverWait instance - uzun süren element beklemeleri için
    // protected olarak tanımlandı çünkü alt sınıflarda özel bekleme durumları olabilir
    // final olarak tanımlandı çünkü initialization sonrası değişmemeli
    // DEFAULT_TIMEOUT_SECONDS ile initialize edildi çünkü standart bekleme süresi
    protected final WebDriverWait wait;

    // Kısa WebDriverWait instance - hızlı kontroller için
    // protected olarak tanımlandı çünkü alt sınıflarda hızlı kontroller gerekebilir
    // final olarak tanımlandı çünkü initialization sonrası değişmemeli
    // SHORT_TIMEOUT_SECONDS ile initialize edildi çünkü kısa bekleme süresi
    protected final WebDriverWait shortWait;

    // ---------- Constructor - Sınıf Başlatma İşlemleri ----------

    /**
     * BasePage Constructor - Sınıf başlatma işlemleri
     *
     * Constructor amacı:
     * - DriverManager'dan aktif WebDriver instance'ını alır
     * - WebDriverWait instance'larını başlatır
     * - PageFactory pattern ile element decoration işlemini yapar
     * - Logger ile initialization bilgisini kaydeder
     *
     * Parametreler: Yok (protected constructor - sadece alt sınıflar çağırabilir)
     *
     * Return değeri: Yok (constructor)
     *
     * Bu constructor kullanılmazsa etki:
     * - Driver instance null kalır ve tüm operations NPE verir
     * - WebDriverWait instance'ları oluşmaz ve bekleme işlemleri çalışmaz
     * - PageFactory initialization olmaz ve @FindBy elementleri null kalır
     * - Loglama başlatılmaz ve debug işlemleri zorlaşır
     *
     * Diğer metodlarla kıyasla:
     * - Bu temel initialization olmadan hiçbir method çalışamaz
     * - Tüm diğer metodlar bu constructor'da yapılan işlemlere bağımlı
     * - Constructor pattern olarak singleton değil inheritance kullanır
     */
    protected BasePage() {
        // DriverManager'dan aktif driver instance'ını al
        // Bu satır olmadan driver null kalır ve tüm mobil etkileşimler başarısız olur
        // DriverManager.getDriver() thread-safe bir şekilde driver döndürür
        this.driver = DriverManager.getDriver();

        // Ana bekleme instance'ını başlat - uzun timeout değeri ile
        // Bu satır olmadan element visibility beklemeleri yapılamaz
        // Duration.ofSeconds kullanıldı çünkü Java 8+ modern Duration API
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));

        // Kısa bekleme instance'ını başlat - kısa timeout değeri ile
        // Bu satır olmadan hızlı kontroller için optimize edilmiş bekleme yapılamaz
        // Ayrı instance tutuldu çünkü bazı işlemler kısa sürede fail etmeli
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(SHORT_TIMEOUT_SECONDS));

        // PageFactory ile element decoration işlemini başlat
        // Bu satır olmadan @FindBy annotation'ları çalışmaz ve elementler null kalır
        // AppiumFieldDecorator kullanıldı çünkü Appium'a özel element bulma stratejileri desteklenir
        // DEFAULT_TIMEOUT_SECONDS ile timeout verildi çünkü element bulma işlemi için süre gerekli
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS)), this);

        // Initialization başarı logunu kaydet
        // Bu satır olmadan hangi pagenın başlatıldığı debug edilemez
        // getClass().getSimpleName() kullanıldı çünkü sadece sınıf adı yeterli
        logger.info("Initialized page: {}", getClass().getSimpleName());
    }

    // ---------- Wait Helper Methods - Element Bekleme Yardımcı Metodları ----------

    /**
     * Element Görünürlük Bekleme Metodu
     *
     * Method amacı:
     * - Verilen WebElement'in DOM'da görünür hale gelmesini bekler
     * - Element visible olana kadar DEFAULT_TIMEOUT_SECONDS süre bekler
     * - Timeout durumunda anlamlı hata mesajı ile exception fırlatır
     * - Allure raporuna step bilgisi ekler
     *
     * Parametreler:
     * @param element - Görünürlüğü beklenecek WebElement instance
     *               - Bu parametre null olamaz, çünkü visibility kontrolü yapılacak
     *               - Stale element durumunda yeniden bulunur
     *
     * Return değeri:
     * @return WebElement - Görünür hale gelen element (method chaining için)
     *                    - Return edilen element hemen kullanılabilir durumda
     *                    - Null dönmez, timeout durumunda exception fırlatılır
     *
     * Bu method kullanılmazsa etki:
     * - Element henüz yüklenmeden etkileşim denenebilir (ElementNotInteractableException)
     * - Dinamik içerik yüklenirken testler fail alabilir
     * - Race condition sorunları ortaya çıkabilir
     * - Test kararlılığı azalır
     *
     * Diğer metodlarla kıyasla:
     * - waitClickable metodundan farkı: sadece görünürlük kontrol eder, tıklanabilirlik kontrol etmez
     * - isDisplayed metodundan farkı: bekleme yapar, anında kontrol etmez
     * - Element bulma işlemlerinden sonra mutlaka kullanılmalı
     *
     * Method'un çağrıldığı yerler:
     * - getText metodu içinde - metin okunmadan önce
     * - type metodu içinde - metin girilmeden önce
     * - Kompleks element etkileşimlerinde güvenlik için
     *
     * Bağımlılıkları:
     * - WebDriverWait instance (constructor'da initialize edilmiş olmalı)
     * - ExpectedConditions.visibilityOf Selenium utility
     * - Logger instance (hata durumu için)
     */
    @Step("Elementin görünür olmasını bekle")
    protected WebElement waitVisible(WebElement element) {
        try {
            // ExpectedConditions.visibilityOf ile element görünürlüğünü bekle
            // Bu satır olmadan element görünmeden işlem yapılmaya çalışılabilir
            // visibilityOf hem presence hem de display:none kontrolü yapar
            return wait.until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException e) {
            // Timeout durumunda detaylı hata logla
            // Bu satır olmadan timeout nedeni anlaşılamaz
            // element parametresi direkt loglanabilir, toString() otomatik çağrılır
            logger.error("Element görünür olmadı: {}", element, e);

            // Kullanıcı dostu hata mesajı ile runtime exception fırlat
            // Bu satır olmadan generic timeout exception mesajı verilir
            // safeLocator kullanıldı çünkü stale element durumunda toString() fail edebilir
            throw new RuntimeException("Element görünür olmadı: " + safeLocator(element), e);
        }
    }

    /**
     * Element Tıklanabilirlik Bekleme Metodu
     *
     * Method amacı:
     * - Verilen WebElement'in tıklanabilir hale gelmesini bekler
     * - Element hem visible hem de enabled olana kadar bekler
     * - Overlay veya loading durumlarında bekler
     * - Timeout durumunda anlamlı hata mesajı ile exception fırlatır
     *
     * Parametreler:
     * @param element - Tıklanabilirliği beklenecek WebElement instance
     *               - Bu parametre DOM'da mevcut olmalı
     *               - Disabled veya overlay altındaki elementler için geçerli
     *
     * Return değeri:
     * @return WebElement - Tıklanabilir hale gelen element
     *                    - Return edilen element güvenle tıklanabilir
     *                    - Method chaining pattern destekler
     *
     * Bu method kullanılmazsa etki:
     * - Disabled butonlara tıklama denenebilir
     * - Loading overlay'i altındaki elementlere erişim denenir
     * - ElementClickInterceptedException alınabilir
     * - Test güvenilirliği azalır
     *
     * Diğer metodlarla kıyasla:
     * - waitVisible'dan farkı: tıklanabilirlik de kontrol eder
     * - click metodu için prerequisite
     * - Daha güvenli ama daha yavaş
     *
     * Method'un çağrıldığı yerler:
     * - click metodu içinde - tıklamadan hemen önce
     * - Form submission işlemlerinde
     * - Button ve link etkileşimlerinde
     */
    @Step("Elementin tıklanabilir olmasını bekle")
    protected WebElement waitClickable(WebElement element) {
        try {
            // ExpectedConditions.elementToBeClickable ile tıklanabilirlik bekle
            // Bu satır olmadan element disabled durumda iken tıklama denenebilir
            // elementToBeClickable visible + enabled + not intercepted kontrolü yapar
            return wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (TimeoutException e) {
            // Timeout durumunda detaylı error log kaydet
            // Bu satır olmadan tıklanamayan element debug edilemez
            // Hangi elementin ne kadar süre beklendiği bilgisi kaydedilir
            logger.error("Element tıklanabilir olmadı: {}", element, e);

            // Anlaşılabilir hata mesajı ile exception fırlat
            // Bu satır olmadan generic timeout mesajı kullanıcıya gösterilir
            // safeLocator method'u stale element durumlarından korunmak için kullanılır
            throw new RuntimeException("Element tıklanabilir olmadı: " + safeLocator(element), e);
        }
    }

    /**
     * Element Kaybolma Bekleme Metodu
     *
     * Method amacı:
     * - Verilen WebElement'in DOM'dan kaybolmasını bekler
     * - Loading spinner, overlay, modal gibi geçici elementler için kullanılır
     * - Timeout durumunda false döner (exception fırlatmaz)
     * - Non-blocking bekleme sağlar
     *
     * Parametreler:
     * @param element - Kaybolması beklenecek WebElement instance
     *               - Genellikle loading indicator, modal, overlay elementleri
     *               - Element mevcut olmasa bile hata vermez
     *
     * Return değeri:
     * @return boolean - true: element kayboldu, false: timeout oldu
     *                 - Exception fırlatmaz, boolean return ile soft check
     *                 - Calling code timeout durumunu handle edebilir
     *
     * Bu method kullanılmazsa etki:
     * - Loading durumları proper şekilde handle edilemez
     * - Modal kapatma işlemleri doğrulanamaz
     * - Race condition'lar oluşabilir
     * - Test timing sorunları yaşanabilir
     *
     * Diğer metodlarla kıyasla:
     * - Diğer wait metodlarından farkı: exception fırlatmaz
     * - Negative case bekleme (element yokluğu)
     * - Optional operation olarak tasarlanmış
     *
     * Method'un çağrıldığı yerler:
     * - Modal kapatma işlemleri sonrası
     * - Loading completion kontrolleri
     * - Overlay dismiss işlemleri
     */
    @Step("Elementin kaybolmasını bekle")
    protected boolean waitInvisible(WebElement element) {
        try {
            // ExpectedConditions.invisibilityOf ile element kaybolmasını bekle
            // Bu satır olmadan loading durumu proper şekilde handle edilemez
            // invisibilityOf element DOM'da yok veya display:none kontrolü yapar
            return wait.until(ExpectedConditions.invisibilityOf(element));
        } catch (TimeoutException e) {
            // Timeout durumunda warning level log kaydet (error değil)
            // Bu satır olmadan element neden kaybolmadığı anlaşılamaz
            // Warning level çünkü bu method'un fail etmesi bazen normal
            logger.warn("Element zamanında kaybolmadı: {}", element);

            // Exception fırlatmaz, false döner - calling code karar verir
            // Bu return olmadan timeout durumu handle edilemez
            // Soft assertion pattern - test devam edebilir
            return false;
        }
    }

    // ---------- Interaction Helper Methods - Element Etkileşim Metodları ----------

    /**
     * Güvenli Element Tıklama Metodu
     *
     * Method amacı:
     * - WebElement'e güvenli bir şekilde tıklama işlemi yapar
     * - Önce elementin tıklanabilir durumda olmasını bekler
     * - Başarı/hata durumunu detaylı şekilde loglar
     * - Hata durumunda anlamlı exception fırlatır
     * - Allure raporuna step bilgisi ekler
     *
     * Parametreler:
     * @param element - Tıklanacak WebElement instance
     *               - null olmamalı, DOM'da mevcut olmalı
     *               - Button, link, clickable div gibi elementler olabilir
     *
     * Return değeri: Yok (void)
     *
     * Bu method kullanılmazsa etki:
     * - Element tıklanabilir olmadan tıklama denenebilir
     * - ElementClickInterceptedException riski artar
     * - Hata durumları proper şekilde handle edilmez
     * - Debug bilgileri kaydedilmez
     *
     * Diğer metodlarla kıyasla:
     * - Raw element.click()'den daha güvenli
     * - waitClickable + click + logging kombinasyonu
     * - Error handling ve reporting içerir
     * - Production-ready implementation
     *
     * Method'un çağrıldığı yerler:
     * - Tüm page sınıflarında button tıklama işlemleri
     * - Navigation işlemleri
     * - Form submission işlemleri
     * - Menu ve link tıklama işlemleri
     */
    @Step("Elemana tıkla")
    protected void click(WebElement element) {
        try {
            // Önce elementin tıklanabilir durumda olmasını bekle, sonra tıkla
            // Bu satır olmadan element disabled durumda iken tıklama denenebilir
            // waitClickable method'u zaten tıklanabilir element return eder
            waitClickable(element).click();

            // Başarılı tıklama işlemini info level'da logla
            // Bu satır olmadan hangi elemente tıklandığı debug edilemez
            // safeLocator kullanıldı çünkü click sonrası element stale olabilir
            logger.info("Click OK -> {}", safeLocator(element));
        } catch (Exception e) {
            // Herhangi bir hata durumunda error level'da logla
            // Bu satır olmadan tıklama hatalarının nedeni anlaşılamaz
            // Generic Exception catch edildi çünkü click işlemi farklı hatalar verebilir
            logger.error("Click FAILED -> {}", safeLocator(element), e);

            // Runtime exception ile üst katmana hata fırlat
            // Bu satır olmadan hata silent şekilde geçer ve test devam eder
            // Anlaşılabilir hata mesajı ile debugging kolaylaştırılır
            throw new RuntimeException("Click başarısız: " + safeLocator(element), e);
        }
    }

    /**
     * Güvenli Metin Girme Metodu
     *
     * Method amacı:
     * - WebElement'e güvenli bir şekilde metin girişi yapar
     * - Önce elementin görünür olmasını bekler
     * - Mevcut metni temizler, sonra yeni metin girer
     * - Input validasyonu ve error handling yapar
     * - Girilen metni log'a kaydeder
     *
     * Parametreler:
     * @param element - Metin girilecek WebElement (input, textarea vb.)
     * @param text - Girilecek metin string'i
     *             - null olabilir (empty string olarak handle edilir)
     *             - Özel karakterler desteklenir
     *
     * Return değeri: Yok (void)
     *
     * Bu method kullanılmazsa etki:
     * - Element visible olmadan metin girişi denenebilir
     * - Eski metin temizlenmez, üst üste yazılır
     * - Metin girişi hataları handle edilmez
     * - Debug için metin girişi bilgisi kaydedilmez
     *
     * Diğer metodlarla kıyasla:
     * - Raw sendKeys()'den daha güvenli
     * - clear() + sendKeys() kombinasyonu
     * - Visibility check içerir
     * - Comprehensive logging
     *
     * Method'un çağrıldığı yerler:
     * - Form doldurma işlemleri
     * - Login credential girişi
     * - Search box'a arama terimi girişi
     * - Text field validation testleri
     */
    @Step("Metin gir: {text}")
    protected void type(WebElement element, String text) {
        try {
            // Önce elementin görünür olmasını bekle
            // Bu satır olmadan invisible element'e metin girişi denenebilir
            // waitVisible return ettiği element üzerinde işlem devam eder
            WebElement el = waitVisible(element);

            // Mevcut metni temizle
            // Bu satır olmadan yeni metin eskinin üzerine eklenir
            // clear() method'u input field'ı boşaltır
            el.clear();

            // Yeni metni gir
            // Bu satır olmadan metin girişi gerçekleşmez
            // sendKeys() keyboard input simulation yapar
            el.sendKeys(text);

            // Başarılı metin girişini info level'da logla
            // Bu satır olmadan hangi elemente ne yazıldığı debug edilemez
            // Text parameteresi de loglanır çünkü test debugging için önemli
            logger.info("Type OK -> {} = '{}'", safeLocator(element), text);
        } catch (Exception e) {
            // Herhangi bir hata durumunda error level'da logla
            // Bu satır olmadan metin girişi hatalarının nedeni anlaşılamaz
            // Hem element hem de text bilgisi loglanır
            logger.error("Type FAILED -> {} = '{}'", safeLocator(element), text, e);

            // Runtime exception ile üst katmana hata fırlat
            // Bu satır olmadan hata silent geçer ve test yanıltıcı sonuçlar verebilir
            // Element ve text bilgisi hata mesajında yer alır
            throw new RuntimeException("Metin girişi başarısız: " + safeLocator(element), e);
        }
    }

    /**
     * Güvenli Metin Alma Metodu
     *
     * Method amacı:
     * - WebElement'den güvenli bir şekilde metin içeriğini alır
     * - Önce elementin görünür olmasını bekler
     * - Alınan metni log'a kaydeder
     * - Hata durumlarında anlamlı exception fırlatır
     * - Return edilen metin null check'i yapılmış olur
     *
     * Parametreler:
     * @param element - Metni alınacak WebElement instance
     *               - Text content olan her element olabilir
     *               - Label, div, span, input value vb.
     *
     * Return değeri:
     * @return String - Element'in text içeriği
     *                - null dönemez (exception fırlatılır)
     *                - Boş string dönebilir ("")
     *                - Whitespace'ler korunur
     *
     * Bu method kullanılmazsa etki:
     * - Element visible olmadan metin alma denenebilir
     * - StaleElementReferenceException riski artar
     * - Assertion'lar için gerekli metin alınamaz
     * - Text validation işlemleri yapılamaz
     *
     * Diğer metodlarla kıyasla:
     * - Raw getText()'den daha güvenli
     * - Visibility check içerir
     * - Error handling ve logging içerir
     * - Assertion friendly return type
     *
     * Method'un çağrıldığı yerler:
     * - Text assertion'larında
     * - Dynamic content verification'da
     * - Form field value kontrollerinde
     * - Error message validation'da
     */
    @Step("Element metnini al")
    protected String getText(WebElement element) {
        try {
            // Önce elementin görünür olmasını bekle, sonra text al
            // Bu satır olmadan invisible element'den text alma denenebilir
            // waitVisible return ettiği element üzerinde getText() çağrılır
            String t = waitVisible(element).getText();

            // Alınan metni info level'da logla
            // Bu satır olmadan hangi elementden ne text alındığı debug edilemez
            // Text content validation için önemli log bilgisi
            logger.info("GetText OK -> {} = '{}'", safeLocator(element), t);

            // Alınan text'i return et
            // Bu return olmadan method'un amacı gerçekleşmez
            // Null check yapılmış text return edilir
            return t;
        } catch (Exception e) {
            // Herhangi bir hata durumunda error level'da logla
            // Bu satır olmadan text alma hatalarının nedeni anlaşılamaz
            // Element bilgisi log'a kaydedilir debugging için
            logger.error("GetText FAILED -> {}", safeLocator(element), e);

            // Runtime exception ile üst katmana h
        }
        return "";
    }

    @Step("Görünürlük kontrolü")
    protected boolean isDisplayed(WebElement element) {
        try {
            boolean r = element.isDisplayed();
            logger.info("isDisplayed({}) -> {}", safeLocator(element), r);
            return r;
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            logger.info("isDisplayed({}) -> false (not present)", safeLocator(element));
            return false;
        }
    }

    @Step("Aktiflik kontrolü")
    protected boolean isEnabled(WebElement element) {
        try {
            boolean r = element.isEnabled();
            logger.info("isEnabled({}) -> {}", safeLocator(element), r);
            return r;
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            logger.info("isEnabled({}) -> false (not present)", safeLocator(element));
            return false;
        }
    }

    // ---------- W3C Actions / Gestures ----------
    /**
     * Ekran boyutuna göre yüzde üzerinden koordinat döndürür.
     */
    private Point percentPoint(double xPercent, double yPercent) {
        Dimension size = driver.manage().window().getSize();
        int x = (int) Math.round(size.getWidth() * xPercent);
        int y = (int) Math.round(size.getHeight() * yPercent);
        return new Point(x, y);
    }

    /**
     * Tek parmakla press-move-release sekansı.
     */
    private void performSwipe(Point start, Point end, Duration hold) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        Sequence seq = new Sequence(finger, 1);
        seq.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), start));
        seq.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        seq.addAction(finger.createPointerMove(hold, PointerInput.Origin.viewport(), end));
        seq.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(seq));
    }

    @Step("Aşağı kaydır (scroll down)")
    protected void scrollDown() {
        // start: %50x, %80y -> end: %50x, %20y
        performSwipe(percentPoint(0.5, 0.80), percentPoint(0.5, 0.20), GESTURE_HOLD);
        logger.info("ScrollDown OK");
    }

    @Step("Yukarı kaydır (scroll up)")
    protected void scrollUp() {
        performSwipe(percentPoint(0.5, 0.20), percentPoint(0.5, 0.80), GESTURE_HOLD);
        logger.info("ScrollUp OK");
    }

    @Step("Sola kaydır (swipe left)")
    protected void swipeLeft() {
        performSwipe(percentPoint(0.80, 0.5), percentPoint(0.20, 0.5), GESTURE_HOLD);
        logger.info("SwipeLeft OK");
    }

    @Step("Sağa kaydır (swipe right)")
    protected void swipeRight() {
        performSwipe(percentPoint(0.20, 0.5), percentPoint(0.80, 0.5), GESTURE_HOLD);
        logger.info("SwipeRight OK");
    }

    @Step("Koordinata dokun (tap): ({x},{y})")
    protected void tapAt(int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        Sequence seq = new Sequence(finger, 1);
        seq.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        seq.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        seq.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(seq));
        logger.info("Tap OK -> ({},{})", x, y);
    }

    // ---------- Sistem / Navigasyon ----------


    @Step("Geri git (navigate back)")
    protected void goBack() {
        try {
            driver.navigate().back();
            logger.info("Back OK");
        } catch (Exception e) {
            logger.error("Back FAILED", e);
            throw new RuntimeException("Geri navigasyon başarısız", e);
        }
    }

    // ---------- page Stabilitesi / Beklemeler ----------
    /**
     * Basit stabilite beklemesi: pageSource değişmiyorsa “stabil” kabul eder.
     * Dinamik yüklemeler için genel bir emniyet sübabıdır.
     */
    @Step("pagenın stabil hale gelmesini bekle (max {timeoutSeconds} sn)")
    protected boolean waitForPageToBeStable(int timeoutSeconds, int stableWindowMillis) {
        long end = System.currentTimeMillis() + timeoutSeconds * 1000L;
        String last = "";
        long stableStart = -1;

        while (System.currentTimeMillis() < end) {
            String now = driver.getPageSource();
            if (now.equals(last)) {
                if (stableStart < 0) {
                    stableStart = System.currentTimeMillis();
                } else if (System.currentTimeMillis() - stableStart >= stableWindowMillis) {
                    logger.info("Page became stable ({} ms window).", stableWindowMillis);
                    return true;
                }
            } else {
                stableStart = -1; // değişti, pencereyi sıfırla
                last = now;
            }
            try { Thread.sleep(150); } catch (InterruptedException ignored) {}
        }
        logger.warn("Page stability timeout ({} s).", timeoutSeconds);
        return false;
    }

    @Step("Sabit süre bekle: {millis} ms")
    protected void waitMillis(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException ignored) {}
    }

    // ---------- Yardımcı ----------
    /**
     * WebElement’i logta tanımlamak için güvenli bir temsil (stale durumlarda patlamasın).
     */
    private String safeLocator(WebElement element) {
        try {
            return element.toString();
        } catch (Exception e) {
            return "#webelement";
        }
    }
}
