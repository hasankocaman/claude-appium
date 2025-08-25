package com.hepsiburada.pages;

// DriverManager: Aktif driver instance'ına erişim için
// Bu import olmadan: Driver operations yapılamaz
import com.hepsiburada.drivers.DriverManager;

// Appium Core Libraries: Mobile automation temel sınıfları
// AppiumDriver: Mobile driver interface (Android/iOS common)
// TouchAction: Mobile gestures (swipe, scroll, tap) için
// AndroidDriver/IOSDriver: Platform-specific driver implementations
// Bu import'lar olmadan: Mobile testing yapılamaz
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

// Appium PageFactory: Mobile page object pattern için
// AppiumFieldDecorator: Mobile element initialization için
// Bu import olmadan: Page Object pattern mobile'da çalışmaz
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

// Touch Actions: Gesture operations için
// WaitOptions: Gesture timing control
// PointOption: Coordinate-based actions
// Bu import'lar olmadan: Advanced mobile gestures yapılamaz
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

// Allure Reporting: Test step documentation için
// @Step annotation: Test steps Allure reports'da görünür
// Bu import olmadan: Step-by-step test documentation eksik
import io.qameta.allure.Step;

// Logging: Debug ve monitoring için
// LogManager/Logger: Log4j2 logging framework
// Bu import olmadan: Page object operations track edilemez
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Selenium Core: Web element operations için
// Dimension: Screen size calculations
// Exceptions: Error handling
// WebElement: Element interface
// Bu import'lar olmadan: Element operations ve error handling yok
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

// Selenium Support: Page Factory ve waits için
// PageFactory: Element initialization
// ExpectedConditions: Wait conditions
// WebDriverWait: Explicit wait management
// Bu import'lar olmadan: Page Object pattern ve reliable waits yok
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

// Java Time API: Duration handling için
// Duration: Modern time representation (Java 8+)
// Bu import olmadan: Timeout specifications yapılamaz
import java.time.Duration;

/**
 * BASE PAGE CLASS - TÜM PAGE OBJECT'LER İÇİN ORTAK FONKSİYONALİTE
 * 
 * Bu class'ın projede rolü:
 * - Page Object Model pattern'in base implementation'ı
 * - Tüm page object'ler için common mobile operations sağlar
 * - Mobile gestures (scroll, swipe, tap) centralized implementation
 * - Element interaction utilities (click, type, wait) sağlar
 * - Platform-agnostic mobile operations (Android/iOS compatible)
 * - Consistent error handling ve logging across all pages
 * 
 * Kullanılmazsa etki:
 * - Her page class'da duplicate code (scroll, wait, click logic)
 * - Inconsistent error handling across pages
 * - Mobile gesture implementations scattered
 * - No centralized element interaction patterns
 * - Maintenance nightmare (changes needed in multiple places)
 * - No standardized logging across page objects
 * 
 * Diğer class'larla ilişkisi:
 * - HomePage, SearchPage, ProductPage vs.: Bu class'ı extend eder
 * - DriverManager: Driver instance'ına access için kullanır
 * - Test step definitions: Page methods bu class'dan inherit edilir
 * - Base class pattern: Template method pattern implementation
 * 
 * Design Patterns:
 * - Template Method Pattern: Common operations, specific implementations
 * - Page Object Pattern: Web/Mobile UI abstraction
 * - Abstract Factory Pattern: Platform-agnostic operations
 * 
 * @author Hepsiburada Test Automation Team
 */
public abstract class BasePage {
    
    // Logger instance: Bu page ve subclass'ları için logging
    // Protected final: Subclass'lar erişebilir, değiştirilemez
    // this.getClass(): Her subclass kendi class name'i ile log yapar
    // Bu field olmadan: Page operations tracking ve debugging yapılamaz
    protected final Logger logger = LogManager.getLogger(this.getClass());
    
    // Driver instance: Mobile automation için temel driver referansı
    // Protected final: Subclass'lar erişebilir, immutable reference
    // AppiumDriver: Platform-agnostic interface (Android/iOS both)
    // Bu field olmadan: Page object'ler mobile operations yapamaz
    protected final AppiumDriver driver;
    
    // WebDriverWait instance: Element wait operations için
    // Protected final: Subclass'larda wait operations için erişilebilir
    // Default timeout ile initialize edilir
    // Bu field olmadan: Reliable element waits yapılamaz, flaky tests
    protected final WebDriverWait wait;
    
    // Default timeout constant: Genel wait operations için standart süre
    // Static final: Class-level constant, memory efficient
    // 30 saniye: Mobile operations için yeterli, too long değil
    // Bu constant olmadan: Hard-coded timeout'lar scattered across code
    private static final int DEFAULT_TIMEOUT = 30;
    
    // Short timeout constant: Hızlı operations için kısa wait süresi
    // Static final: Quick checks için optimize timeout
    // 10 saniye: Element presence checks için sufficient
    // Bu constant olmadan: Unnecessary long waits for quick operations
    private static final int SHORT_TIMEOUT = 10;
    
    /**
     * BASE PAGE CONSTRUCTOR - PAGE OBJECT INITIALIZATION
     * 
     * Constructor amacı: Page object'in temel bileşenlerini initialize eder
     * Parametreler: Parametre almaz (DriverManager'dan active driver alır)
     * Side effects: Driver, wait, page elements initialize eder
     * 
     * Kullanılmazsa etki:
     * - Page object operations yapılamaz
     * - Element interactions fail olur
     * - Page Factory initialization eksik
     * - Wait operations çalışmaz
     * 
     * Çağrıldığı durumlar:
     * - Her page object instance yaratılırken
     * - Test methods'larda page navigation sırasında
     * - Step definitions'da page access'lerde
     * 
     * Bağımlılıkları:
     * - DriverManager.getDriver(): Active driver session gerekli
     * - AppiumFieldDecorator: Mobile page factory initialization
     */
    public BasePage() {
        // Active driver instance'ını al (DriverManager singleton'dan)
        // Bu assignment olmadan: Page object mobile operations yapamaz
        this.driver = DriverManager.getDriver();
        
        // WebDriverWait initialize et (default timeout ile)
        // Duration.ofSeconds(): Modern Java time API kullanımı
        // Bu initialization olmadan: Reliable element waits yapılamaz
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        
        // Page elements initialize et (Appium PageFactory ile)
        // AppiumFieldDecorator: Mobile-specific element initialization
        // PageFactory.initElements(): @AndroidFindBy/@iOSXCUITFindBy annotations'ı process eder
        // Bu initialization olmadan: Page element annotations çalışmaz
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(DEFAULT_TIMEOUT)), this);
        
        // Page initialization completion log'u
        // getClass().getSimpleName(): Hangi page class'ının initialize olduğunu gösterir
        // Bu log olmadan: Page object lifecycle tracking yapılamaz
        logger.info("Initialized page: {}", this.getClass().getSimpleName());
    }
    
    /**
     * ELEMENT GÖRÜNÜRLÜĞÜ BEKLEME METODİ
     * 
     * Method amacı: WebElement'ın visible olmasını bekler ve visible element döner
     * Parametreler: element - Visibility beklenecek WebElement
     * Return değeri: Visible hale gelmiş WebElement (interaction-ready)
     * 
     * Kullanılmazsa etki:
     * - Element visible olmadan interaction attempts
     * - ElementNotVisibleException hataları
     * - Flaky test behavior (timing issues)
     * - Unreliable element operations
     * 
     * Diğer wait metodlarla kıyasla:
     * - waitForElementToBeClickable(): Visibility + clickability check
     * - waitForElementToDisappear(): Opposite operation (invisibility)
     * - Most basic wait: Sadece visibility guarantee
     * 
     * Çağrıldığı yerler:
     * - getElementText(): Text alma öncesi visibility check
     * - enterText(): Text input öncesi element visible olmalı
     * - Element validation operations
     * 
     * Exception Handling: TimeoutException -> RuntimeException wrap
     */
    @Step("Wait for element to be visible")
    protected WebElement waitForElementToBeVisible(WebElement element) {
        try {
            // Element visibility wait operation başlangıç log'u
            // Debug level: Detailed operation tracking için
            logger.debug("Waiting for element to be visible");
            
            // WebDriverWait ile visibility condition bekle
            // ExpectedConditions.visibilityOf(): Element visible olana kadar bekler
            // Bu return olmadan: Element visible olmadan operations attempt edilir
            return wait.until(ExpectedConditions.visibilityOf(element));
            
        } catch (TimeoutException e) {
            // Timeout durumunda error log ve exception wrap
            // Error level: Critical failure, manual investigation gerekli
            logger.error("Element not visible within timeout", e);
            
            // RuntimeException wrap: Unchecked exception, method signature'da throws gerekmez
            // Bu throw olmadan: Timeout silent kalabilir, undefined behavior
            throw new RuntimeException("Element not visible: " + element.toString(), e);
        }
    }
    
    /**
     * ELEMENT TİKLANABİLİR OLMA BEKLEME METODİ
     * 
     * Method amacı: WebElement'ın clickable olmasını bekler (visible + enabled + not overlapped)
     * Parametreler: element - Clickability beklenecek WebElement  
     * Return değeri: Clickable hale gelmiş WebElement (click-ready)
     * 
     * Kullanılmazsa etki:
     * - Element clickable olmadan click attempts
     * - ElementNotInteractableException hataları
     * - Click events miss olur (overlapped elements)
     * - Unreliable button/link interactions
     * 
     * Diğer wait metodlarla kıyasla:
     * - waitForElementToBeVisible(): Sadece visibility vs. full clickability
     * - waitForElementToDisappear(): Opposite operation
     * - Most comprehensive wait: Visibility + enabled + not covered check
     * 
     * Çağrıldığı yerler:
     * - clickElement(): Click operation öncesi safety check
     * - Button, link, input field interactions
     * - Critical element operations
     * 
     * Clickable Conditions: Visible + Enabled + Not overlapped by other elements
     */
    @Step("Wait for element to be clickable")
    protected WebElement waitForElementToBeClickable(WebElement element) {
        try {
            // Element clickability wait operation başlangıç log'u
            // Debug level: Detailed operation tracking
            logger.debug("Waiting for element to be clickable");
            
            // WebDriverWait ile clickable condition bekle
            // ExpectedConditions.elementToBeClickable(): Comprehensive clickability check
            // Bu return olmadan: Element clickable olmadan click attempt, failures
            return wait.until(ExpectedConditions.elementToBeClickable(element));
            
        } catch (TimeoutException e) {
            // Clickable timeout durumunda error log ve exception wrap
            // Error level: Click operations için critical failure
            logger.error("Element not clickable within timeout", e);
            
            // RuntimeException wrap: Standardized exception handling
            // Bu throw olmadan: Click failures silent kalabilir
            throw new RuntimeException("Element not clickable: " + element.toString(), e);
        }
    }
    
    /**
     * ELEMENT KAYBOLMA BEKLEME METODİ
     * 
     * Method amacı: WebElement'ın invisible/absent olmasını bekler
     * Parametreler: element - Disappearance beklenecek WebElement
     * Return değeri: true - element kayboldu, false - timeout oldu
     * 
     * Kullanılmazsa etki:
     * - Loading indicators kaybolmadığı halde operations continue
     * - Modal dialogs kapanmadan next operations
     * - Progress bars, spinners ile ilgili timing issues
     * - State transition validation eksik
     * 
     * Diğer wait metodlarla kıyasla:
     * - waitForElementToBeVisible(): Opposite operation (visibility wait)
     * - waitForElementToBeClickable(): Positive wait vs. negative wait
     * - Non-throwing: false döner, exception fırlatmaz (graceful failure)
     * 
     * Çağrıldığı yerler:
     * - Loading screen'ler sonrası next page validation
     * - Modal dialog close operations
     * - Progress indicator completion waits
     * - State transition validations
     * 
     * Timeout Handling: Exception fırlatmaz, false döner (optional wait)
     */
    @Step("Wait for element to disappear")
    protected boolean waitForElementToDisappear(WebElement element) {
        try {
            // Element disappearance wait operation başlangıç log'u
            // Debug level: Optional operation, detailed tracking
            logger.debug("Waiting for element to disappear");
            
            // WebDriverWait ile invisibility condition bekle
            // ExpectedConditions.invisibilityOf(): Element kaybolana kadar bekler
            // Bu return olmadan: Element state transition validation yapılamaz
            return wait.until(ExpectedConditions.invisibilityOf(element));
            
        } catch (TimeoutException e) {
            // Timeout durumunda warning log (error değil, optional operation)
            // Warning level: Expected behavior olabilir, critical failure değil
            logger.warn("Element did not disappear within timeout");
            
            // False return: Graceful failure, calling code decision verebilir
            // Bu false return olmadan: Timeout handling calling code'da zorlaşır
            return false;
        }
    }
    
    /**
     * GÜVENLİ ELEMENT TİKLAMA METODİ
     * 
     * Method amacı: WebElement'ı safe şekilde tıklar (clickable wait + click)
     * Parametreler: element - Tıklanacak WebElement
     * Return değeri: Void (side effect: element click operation)
     * 
     * Kullanılmazsa etki:
     * - Unreliable click operations (element not ready)
     * - ElementNotInteractableException hataları
     * - Click events miss olabilir
     * - Flaky test behavior due to timing
     * 
     * Diğer interaction metodlarla kıyasla:
     * - enterText(): Text input vs. click action
     * - tapOnCoordinates(): Element-based vs. coordinate-based
     * - Most common interaction: Button, link, checkbox clicks
     * 
     * Çağrıldığı yerler:
     * - Button clicks (search, submit, navigation)
     * - Link navigation operations
     * - Checkbox, radio button selections
     * - Menu item selections
     * 
     * Safety Pattern: Wait + Action + Logging
     */
    @Step("Click on element: {element}")
    protected void clickElement(WebElement element) {
        try {
            // Element clickable olana kadar bekle (safety check)
            // waitForElementToBeClickable(): Comprehensive readiness check
            // Bu wait olmadan: Click operations unreliable, timing issues
            waitForElementToBeClickable(element);
            
            // Element click operation perform et
            // element.click(): Standard WebDriver click action
            // Bu click olmadan: Intended action perform edilmez
            element.click();
            
            // Successful click operation log'u
            // Info level: Important user action, success tracking
            // Bu log olmadan: Click operations success/failure track edilemez
            logger.info("Successfully clicked on element");
            
        } catch (Exception e) {
            // Click operation exception handling
            // Error level: Critical interaction failure
            logger.error("Failed to click on element", e);
            
            // RuntimeException wrap: Standardized exception handling
            // Bu throw olmadan: Click failures silent kalabilir, test invalid results
            throw new RuntimeException("Failed to click on element", e);
        }
    }
    
    /**
     * GÜVENLİ TEXT GİRİŞ METODİ
     * 
     * Method amacı: WebElement'ına güvenli şekilde text girer (clear + type pattern)
     * Parametreler: element - Text girilecek WebElement, text - Girilecek text
     * Return değeri: Void (side effect: text input operation)
     * 
     * Kullanılmazsa etki:
     * - Unreliable text input (element not ready)
     * - Previous text contamination (clear yok)
     * - ElementNotInteractableException hataları
     * - Invalid form submissions
     * 
     * Text Input Pattern: Wait + Clear + SendKeys
     * - Wait: Element ready olmasını garantiler
     * - Clear: Previous text'i temizler
     * - SendKeys: New text'i girer
     * 
     * Çağrıldığı yerler:
     * - Search box text input
     * - Form field filling
     * - Login username/password
     * - Any text input scenarios
     * 
     * Data Validation: Text null/empty check calling code'da yapılmalı
     */
    @Step("Enter text '{text}' in element")
    protected void enterText(WebElement element, String text) {
        try {
            // Element visible olana kadar bekle (text input için sufficient)
            // waitForElementToBeVisible(): Text input için ready check
            // Bu wait olmadan: Element ready olmadan text input attempt
            waitForElementToBeVisible(element);
            
            // Element'ın mevcut text'ini clear et
            // element.clear(): Previous input contamination prevent
            // Bu clear olmadan: New text previous text ile concat olabilir
            element.clear();
            
            // New text'i element'a gönder
            // element.sendKeys(): Standard WebDriver text input
            // Bu sendKeys olmadan: Actual text input yapılmaz
            element.sendKeys(text);
            
            // Successful text input operation log'u
            // Info level: Important user input, success tracking
            // Parameterized logging: Actual input text'i gösterir
            logger.info("Successfully entered text: {}", text);
            
        } catch (Exception e) {
            // Text input exception handling
            // Error level: Critical input failure
            // Parameterized error: Failed text'i context olarak gösterir
            logger.error("Failed to enter text: {}", text, e);
            
            // RuntimeException wrap: Standardized exception handling
            // Bu throw olmadan: Text input failures silent kalabilir
            throw new RuntimeException("Failed to enter text: " + text, e);
        }
    }
    
    /**
     * GÜVENLİ ELEMENT TEXT ALMA METODİ
     * 
     * Method amacı: WebElement'ın text content'ıni güvenli şekilde alır
     * Parametreler: element - Text'i alınacak WebElement
     * Return değeri: Element'ın visible text content'i (String)
     * 
     * Kullanılmazsa etki:
     * - Unreliable text retrieval (element not ready)
     * - Empty/null text due to timing issues
     * - ElementNotVisibleException hataları
     * - Invalid assertions due to premature text access
     * 
     * Text Retrieval Pattern: Wait + GetText + Validation
     * - Wait: Element visible ve text ready garantisi
     * - GetText: Actual text content retrieval
     * - Validation: Retrieved text logging for debugging
     * 
     * Çağrıldığı yerler:
     * - Element text assertions (titles, labels, messages)
     * - Dynamic content validation
     * - Search results, product names, prices
     * - Error message validations
     * 
     * Text Content: Visible text only, hidden text excluded
     */
    @Step("Get text from element")
    protected String getElementText(WebElement element) {
        try {
            // Element visible olana kadar bekle (text ready garantisi için)
            // waitForElementToBeVisible(): Text content access için prerequisite
            // Bu wait olmadan: Element text ready olmadan access, empty results
            waitForElementToBeVisible(element);
            
            // Element'dan text content al
            // element.getText(): Standard WebDriver text retrieval
            // Bu getText olmadan: Actual text content elde edilemez
            String text = element.getText();
            
            // Retrieved text success log'u (debugging ve validation için)
            // Info level: Important data retrieval, content visibility
            // Parameterized logging: Actual retrieved text gösterir
            logger.info("Retrieved text: {}", text);
            
            // Retrieved text'i return et
            // Bu return olmadan: Calling code text content alamaz
            return text;
            
        } catch (Exception e) {
            // Text retrieval exception handling
            // Error level: Critical data access failure
            logger.error("Failed to get text from element", e);
            
            // RuntimeException wrap: Standardized exception handling
            // Bu throw olmadan: Text retrieval failures silent kalabilir
            throw new RuntimeException("Failed to get text from element", e);
        }
    }
    
    /**
     * ELEMENT GÖRÜNÜRLÜĞÜ KONTROL METODİ
     * 
     * Method amacı: WebElement'ın görünür olup olmadığını safe check yapar
     * Parametreler: element - Visibility check yapılacak WebElement
     * Return değeri: boolean - true: visible, false: not visible/not found
     * 
     * Kullanılmazsa etki:
     * - Unsafe element state assumptions
     * - NoSuchElementException'lar handle edilemez
     * - Conditional UI logic yapılamaz
     * - Element presence validation eksik
     * 
     * Diğer validation metodlarla kıyasla:
     * - isElementEnabled(): Visibility vs. enabled state
     * - waitForElementToBeVisible(): Immediate check vs. wait
     * - Non-blocking: Wait yapmaz, instant state check
     * - Exception-safe: Exception durumunda false döner
     * 
     * Çağrıldığı yerler:
     * - Conditional element operations
     * - Optional element presence checks
     * - Page state validations
     * - Error condition detections
     * 
     * Safety Pattern: Try-catch ile graceful failure handling
     */
    @Step("Check if element is displayed")
    protected boolean isElementDisplayed(WebElement element) {
        try {
            // Element visibility state check (immediate, no wait)
            // element.isDisplayed(): Standard WebDriver visibility check
            // Bu return olmadan: Element visibility state unknown
            return element.isDisplayed();
            
        } catch (NoSuchElementException e) {
            // Element DOM'da yok durumu (expected scenario)
            // Debug level: Not an error, expected condition
            logger.debug("Element not found");
            
            // False return: Element yok = not displayed
            // Bu false return olmadan: Exception propagate olur, unsafe
            return false;
            
        } catch (Exception e) {
            // Diğer exception durumları (StaleElementReference, vs.)
            // Error level: Unexpected condition, investigation gerekebilir
            logger.error("Error checking element visibility", e);
            
            // False return: Graceful failure, safe assumption
            // Bu false return olmadan: Unexpected exceptions propagate
            return false;
        }
    }
    
    /**
     * ELEMENT AKTİFLİK KONTROL METODİ
     * 
     * Method amacı: WebElement'ın enabled (interact edilebilir) olup olmadığını check yapar
     * Parametreler: element - Enabled state check yapılacak WebElement
     * Return değeri: boolean - true: enabled/interactive, false: disabled
     * 
     * Kullanılmazsa etki:
     * - Disabled elements'a interaction attempts
     * - Form validation logic eksik
     * - Button state handling problems
     * - User experience validation gaps
     * 
     * Enabled vs Displayed farkı:
     * - Enabled: Element interactive mı? (click, type yapabilir mi?)
     * - Displayed: Element visible mı? (screen'de görünüyor mu?)
     * - Element displayed ama disabled olabilir
     * 
     * Çağrıldığı yerler:
     * - Form field validations
     * - Button state checks (submit enabled mi?)
     * - Input field availability checks
     * - Conditional interaction logic
     * 
     * Use Cases: Submit buttons, form fields, interactive elements
     */
    @Step("Check if element is enabled")
    protected boolean isElementEnabled(WebElement element) {
        try {
            // Element enabled state check (interaction readiness)
            // element.isEnabled(): Standard WebDriver enabled check
            // Bu return olmadan: Element interaction readiness unknown
            return element.isEnabled();
            
        } catch (Exception e) {
            // Enabled check exception handling (StaleElement, vs.)
            // Error level: Unexpected condition during state check
            logger.error("Error checking element enabled state", e);
            
            // False return: Graceful failure, safe assumption (disabled)
            // Bu false return olmadan: Exceptions propagate, unsafe operations
            return false;
        }
    }
    
    /**
     * EKRAN AŞAĞI KAYDIRMA METODİ (SCROLL DOWN)
     * 
     * Method amacı: Ekranı aşağı doğru scroll yapar (vertical scrolling)
     * Parametreler: Parametre almaz (screen size'a göre automatic calculation)
     * Return değeri: Void (side effect: screen scroll operation)
     * 
     * Kullanılmazsa etki:
     * - Uzun page'lerde content'e erişilemez
     * - List item'lar, product cards vs. görülemez
     * - Infinite scroll content'ler yüklenemez
     * - Below-fold elements interact edilemez
     * 
     * Scroll Algorithm:
     * - Start point: Screen height'in %80'i (alt kısım)
     * - End point: Screen height'in %20'si (üst kısım)  
     * - X coordinate: Screen width'in ortası (merkez scroll)
     * - Duration: 1000ms (smooth scroll effect)
     * 
     * Çağrıldığı yerler:
     * - Product list browsing
     * - Search results navigation
     * - Long form scrolling
     * - Infinite scroll triggers
     * 
     * Mobile Gesture: TouchAction ile native mobile scroll simulation
     */
    @Step("Scroll down")
    protected void scrollDown() {
        try {
            // Scroll operation başlangıç log'u
            // Info level: User action simulation, important operation
            logger.info("Scrolling down");
            
            // Screen dimension'larını al (scroll coordinates için)
            // driver.manage().window().getSize(): Current screen resolution
            // Bu size bilgisi olmadan: Scroll coordinates calculate edilemez
            Dimension size = driver.manage().window().getSize();
            
            // Scroll coordinates calculate et
            // startX: Screen width ortası (horizontal center)
            // startY: Screen height'in %80'i (bottom area'dan başla)
            // endY: Screen height'in %20'si (top area'da bitir)
            int startX = size.width / 2;
            int startY = (int) (size.height * 0.8);
            int endY = (int) (size.height * 0.2);
            
            // TouchAction ile scroll gesture perform et
            // TouchAction: Appium native mobile gesture API
            TouchAction touchAction = new TouchAction(driver);
            touchAction.press(PointOption.point(startX, startY))  // Start point'e bas
                      .waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000)))  // 1 saniye wait (smooth scroll)
                      .moveTo(PointOption.point(startX, endY))  // End point'e taşı
                      .release()  // Touch release
                      .perform();  // Gesture'u execute et
            
            // Scroll operation completion log'u
            // Info level: Operation success confirmation
            logger.info("Scroll down completed");
            
        } catch (Exception e) {
            // Scroll operation exception handling
            // Error level: Gesture failure, critical for navigation
            logger.error("Failed to scroll down", e);
            
            // RuntimeException wrap: Standardized exception handling
            // Bu throw olmadan: Scroll failures silent kalabilir
            throw new RuntimeException("Failed to scroll down", e);
        }
    }
    
    /**
     * EKRAN YUKARİ KAYDIRMA METODİ (SCROLL UP)
     * 
     * Method amacı: Ekranı yukarı doğru scroll yapar (reverse vertical scrolling)
     * Parametreler: Parametre almaz (screen size'a göre automatic calculation)
     * Return değeri: Void (side effect: reverse screen scroll operation)
     * 
     * Kullanılmazsa etki:
     * - Page top content'e geri dönülemez
     * - Header, navigation elements erişilemez
     * - Above-fold content'e navigation yapılamaz
     * - Scroll position correction yapılamaz
     * 
     * Scroll Algorithm (scrollDown'un tersi):
     * - Start point: Screen height'in %20'si (üst kısım)
     * - End point: Screen height'in %80'i (alt kısım)
     * - X coordinate: Screen width'in ortası (merkez scroll)
     * - Duration: 1000ms (smooth scroll effect)
     * 
     * Çağrıldığı yerler:
     * - Page top navigation
     * - Header elements access
     * - Scroll position reset
     * - Content area corrections
     * 
     * Reverse Gesture: scrollDown'un opposite direction'u
     */
    @Step("Scroll up")
    protected void scrollUp() {
        try {
            // Scroll up operation başlangıç log'u
            // Info level: User action simulation (reverse scroll)
            logger.info("Scrolling up");
            
            // Screen dimension'larını al (reverse scroll coordinates için)
            // driver.manage().window().getSize(): Current screen resolution
            Dimension size = driver.manage().window().getSize();
            
            // Reverse scroll coordinates calculate et
            // startX: Screen width ortası (horizontal center, same as down)
            // startY: Screen height'in %20'si (top area'dan başla - scrollDown'un tersi)
            // endY: Screen height'in %80'i (bottom area'da bitir - scrollDown'un tersi)
            int startX = size.width / 2;
            int startY = (int) (size.height * 0.2);
            int endY = (int) (size.height * 0.8);
            
            // TouchAction ile reverse scroll gesture perform et
            // TouchAction: Same API, different coordinates (reverse direction)
            TouchAction touchAction = new TouchAction(driver);
            touchAction.press(PointOption.point(startX, startY))  // Top area'dan başla
                      .waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000)))  // Smooth scroll timing
                      .moveTo(PointOption.point(startX, endY))  // Bottom area'ya taşı
                      .release()  // Touch release
                      .perform();  // Reverse gesture execute
            
            // Scroll up operation completion log'u
            // Info level: Reverse operation success confirmation
            logger.info("Scroll up completed");
            
        } catch (Exception e) {
            // Scroll up operation exception handling
            // Error level: Reverse gesture failure
            logger.error("Failed to scroll up", e);
            
            // RuntimeException wrap: Consistent exception handling
            // Bu throw olmadan: Reverse scroll failures silent
            throw new RuntimeException("Failed to scroll up", e);
        }
    }
    
    /**
     * SOLA KAYDIRMA METODİ (SWIPE LEFT)
     * 
     * Method amacı: Ekranı sola doğru swipe yapar (horizontal swiping)
     * Parametreler: Parametre almaz (screen size'a göre automatic calculation)
     * Return değeri: Void (side effect: horizontal swipe gesture)
     * 
     * Kullanılmazsa etki:
     * - Carousel, slider navigation yapılamaz
     * - Tab switching operations eksik
     * - Image gallery browsing yapılamaz
     * - Horizontal content navigation blocked
     * 
     * Swipe Algorithm:
     * - Start point: Screen width'in %80'i (sağ kısım)
     * - End point: Screen width'in %20'si (sol kısım)
     * - Y coordinate: Screen height'in ortası (vertical center)
     * - Duration: 1000ms (smooth swipe effect)
     * 
     * Çağrıldığı yerler:
     * - Product image carousels
     * - Tab navigation (right to left)
     * - Slider controls
     * - Horizontal list browsing
     * 
     * Horizontal Gesture: Left direction swipe (right to left movement)
     */
    @Step("Swipe left")
    protected void swipeLeft() {
        try {
            // Swipe left operation başlangıç log'u
            // Info level: Horizontal gesture simulation
            logger.info("Swiping left");
            
            // Screen dimension'larını al (horizontal swipe coordinates için)
            // driver.manage().window().getSize(): Screen size for calculation
            Dimension size = driver.manage().window().getSize();
            
            // Horizontal swipe coordinates calculate et
            // startY: Screen height ortası (vertical center)
            // startX: Screen width'in %80'i (right area'dan başla)
            // endX: Screen width'in %20'si (left area'da bitir)
            int startY = size.height / 2;
            int startX = (int) (size.width * 0.8);
            int endX = (int) (size.width * 0.2);
            
            // TouchAction ile horizontal swipe gesture perform et
            // TouchAction: Horizontal movement (X koordinat değişimi)
            TouchAction touchAction = new TouchAction(driver);
            touchAction.press(PointOption.point(startX, startY))  // Right side'dan başla
                      .waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000)))  // Smooth swipe timing
                      .moveTo(PointOption.point(endX, startY))  // Left side'a taşı
                      .release()  // Touch release
                      .perform();  // Horizontal gesture execute
            
            // Swipe left operation completion log'u
            // Info level: Horizontal gesture success confirmation
            logger.info("Swipe left completed");
            
        } catch (Exception e) {
            // Swipe left operation exception handling
            // Error level: Horizontal gesture failure
            logger.error("Failed to swipe left", e);
            
            // RuntimeException wrap: Consistent gesture exception handling
            // Bu throw olmadan: Swipe failures silent kalabilir
            throw new RuntimeException("Failed to swipe left", e);
        }
    }
    
    /**
     * SAĞA KAYDIRMA METODİ (SWIPE RIGHT)
     * 
     * Method amacı: Ekranı sağa doğru swipe yapar (reverse horizontal swiping)
     * Parametreler: Parametre almaz (screen size'a göre automatic calculation)
     * Return değeri: Void (side effect: reverse horizontal swipe gesture)
     * 
     * Kullanılmazsa etki:
     * - Reverse carousel navigation yapılamaz
     * - Back tab switching operations eksik
     * - Previous image gallery browsing blocked
     * - Reverse horizontal content navigation yapılamaz
     * 
     * Swipe Algorithm (swipeLeft'in tersi):
     * - Start point: Screen width'in %20'si (sol kısım)
     * - End point: Screen width'in %80'i (sağ kısım)
     * - Y coordinate: Screen height'in ortası (vertical center)
     * - Duration: 1000ms (smooth swipe effect)
     * 
     * Çağrıldığı yerler:
     * - Product image carousel reverse
     * - Tab navigation (left to right)
     * - Slider reverse controls
     * - Previous content browsing
     * 
     * Reverse Horizontal Gesture: Right direction swipe (left to right movement)
     */
    @Step("Swipe right")
    protected void swipeRight() {
        try {
            // Swipe right operation başlangıç log'u
            // Info level: Reverse horizontal gesture simulation
            logger.info("Swiping right");
            
            // Screen dimension'larını al (reverse horizontal swipe için)
            // driver.manage().window().getSize(): Screen size for reverse calculation
            Dimension size = driver.manage().window().getSize();
            
            // Reverse horizontal swipe coordinates calculate et
            // startY: Screen height ortası (vertical center, same as left)
            // startX: Screen width'in %20'si (left area'dan başla - swipeLeft'in tersi)
            // endX: Screen width'in %80'i (right area'da bitir - swipeLeft'in tersi)
            int startY = size.height / 2;
            int startX = (int) (size.width * 0.2);
            int endX = (int) (size.width * 0.8);
            
            // TouchAction ile reverse horizontal swipe gesture perform et
            // TouchAction: Reverse horizontal movement (opposite X coordinates)
            TouchAction touchAction = new TouchAction(driver);
            touchAction.press(PointOption.point(startX, startY))  // Left side'dan başla
                      .waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000)))  // Smooth reverse timing
                      .moveTo(PointOption.point(endX, startY))  // Right side'a taşı
                      .release()  // Touch release
                      .perform();  // Reverse horizontal gesture execute
            
            // Swipe right operation completion log'u
            // Info level: Reverse horizontal gesture success
            logger.info("Swipe right completed");
            
        } catch (Exception e) {
            // Swipe right operation exception handling
            // Error level: Reverse horizontal gesture failure
            logger.error("Failed to swipe right", e);
            
            // RuntimeException wrap: Consistent reverse gesture exception handling
            // Bu throw olmadan: Reverse swipe failures silent
            throw new RuntimeException("Failed to swipe right", e);
        }
    }
    
    /**
     * KOORDİNAT TABANLI DOKUNMA METODİ (COORDINATE TAP)
     * 
     * Method amacı: Belirtilen koordinatlara dokunma gesture'u yapar
     * Parametreler: x - X koordinatı, y - Y koordinatı (screen pixel coordinates)
     * Return değeri: Void (side effect: coordinate-based tap gesture)
     * 
     * Kullanılmazsa etki:
     * - Element-based approach çalışmadığında fallback yok
     * - Fixed position elements (ads, overlays) handle edilemez
     * - Screen area-based interactions yapılamaz
     * - Coordinate-dependent operations blocked
     * 
     * Coordinate vs Element-based farkı:
     * - Element-based: WebElement reference gerekli
     * - Coordinate-based: Direct screen position access
     * - Use case: WebElement bulunamadığı durumlar
     * 
     * Çağrıldığı yerler:
     * - Ad close buttons (dynamic elements)
     * - Fixed position overlays
     * - Screen area interactions
     * - Element location fallback scenarios
     * 
     * Coordinate System: Screen top-left (0,0), bottom-right (width,height)
     */
    @Step("Tap on coordinates: ({x}, {y})")
    protected void tapOnCoordinates(int x, int y) {
        try {
            // Coordinate tap operation başlangıç log'u
            // Info level: Coordinate-based interaction tracking
            // Parameterized: Actual coordinates gösterir
            logger.info("Tapping on coordinates: ({}, {})", x, y);
            
            // TouchAction ile coordinate tap gesture perform et
            // TouchAction.tap(): Direct coordinate tap (element-independent)
            TouchAction touchAction = new TouchAction(driver);
            touchAction.tap(PointOption.point(x, y)).perform();
            
            // Coordinate tap completion log'u
            // Info level: Coordinate operation success confirmation
            logger.info("Tap completed on coordinates: ({}, {})", x, y);
            
        } catch (Exception e) {
            // Coordinate tap exception handling
            // Error level: Coordinate operation failure
            // Parameterized error: Failed coordinates context
            logger.error("Failed to tap on coordinates: ({}, {})", x, y, e);
            
            // RuntimeException wrap: Consistent coordinate exception handling
            // Bu throw olmadan: Coordinate tap failures silent
            throw new RuntimeException("Failed to tap on coordinates", e);
        }
    }
    
    /**
     * KLAVYE GİZLEME METODİ (PLATFORM-SPECİFİC)
     * 
     * Method amacı: Mobile keyboard'ı gizler (Android-specific operation)
     * Parametreler: Parametre almaz (active keyboard'u detect ve hide eder)
     * Return değeri: Void (side effect: keyboard hide operation)
     * 
     * Kullanılmazsa etki:
     * - Text input sonrası keyboard açık kalır
     * - Screen real estate kaybı (keyboard area)
     * - Element visibility problems (keyboard overlap)
     * - User experience issues
     * 
     * Platform Behavior:
     * - Android: Explicit hideKeyboard() API available
     * - iOS: Usually auto-hide, explicit hide less reliable
     * - Platform check: DriverManager.isAndroid() condition
     * 
     * Çağrıldığı yerler:
     * - Text input completion sonrası
     * - Form submission öncesi
     * - Page navigation öncesi screen cleanup
     * - Element interaction öncesi visibility assurance
     * 
     * Exception Handling: Warning level (keyboard absence expected)
     */
    @Step("Hide keyboard")
    protected void hideKeyboard() {
        try {
            // Platform check: Sadece Android'de hide keyboard support var
            // DriverManager.isAndroid(): Platform detection utility
            if (DriverManager.isAndroid()) {
                // Keyboard hiding operation başlangıç log'u
                // Info level: Platform-specific operation tracking
                logger.info("Hiding keyboard");
                
                // AndroidDriver cast ve hideKeyboard() API call
                // AndroidDriver cast: Platform-specific API access
                // hideKeyboard(): Android-specific keyboard hide method
                ((AndroidDriver) driver).hideKeyboard();
                
                // Keyboard hiding success log'u
                // Info level: Platform operation success confirmation
                logger.info("Keyboard hidden successfully");
            }
            // iOS case: No explicit action (iOS auto-hide behavior)
            
        } catch (Exception e) {
            // Keyboard hiding exception handling
            // Warning level: Keyboard absence normal, not critical error
            // Bu warning olmadan: Normal keyboard absence logging eksik
            logger.warn("Failed to hide keyboard or keyboard not present", e);
            
            // No throw: Keyboard hiding optional operation, failure tolerable
            // Bu pattern olmadan: Optional operations critical failure gibi treat edilir
        }
    }
    
    /**
     * GERİ NAVİGASYON METODİ (PLATFORM-SPECİFİC)
     * 
     * Method amacı: Platform-uygun şekilde geri navigation yapar
     * Parametreler: Parametre almaz (current platform'a göre appropriate back action)
     * Return değeri: Void (side effect: back navigation operation)
     * 
     * Kullanılmazsa etki:
     * - Geri navigation yapılamaz
     * - User back button behavior simulate edilemez
     * - Page/screen navigation flow broken
     * - Test scenarios incomplete (forward-only navigation)
     * 
     * Platform Behavior:
     * - Android: Hardware back button simulation
     * - iOS: Navigation back (app-specific back)
     * - Cross-platform: Same method, different implementations
     * 
     * Çağrıldığı yerler:
     * - User back button press simulation
     * - Navigation flow testing
     * - Page transition validations
     * - Error scenario recovery
     * 
     * Navigation Pattern: Platform detection + appropriate back action
     */
    @Step("Navigate back")
    protected void goBack() {
        try {
            // Back navigation operation başlangıç log'u
            // Info level: Navigation action tracking
            logger.info("Navigating back");
            
            // Platform-specific back navigation implementation
            if (DriverManager.isAndroid()) {
                // Android: Hardware back button simulation
                // AndroidDriver cast: Platform-specific navigation API
                // navigate().back(): Android system back button press
                ((AndroidDriver) driver).navigate().back();
                
            } else if (DriverManager.isIOS()) {
                // iOS: App-specific back navigation
                // driver.navigate().back(): iOS navigation back
                // iOS back navigation implementation (app-dependent)
                driver.navigate().back();
            }
            // Unsupported platform: No action (graceful degradation)
            
            // Back navigation completion log'u
            // Info level: Navigation operation success confirmation
            logger.info("Back navigation completed");
            
        } catch (Exception e) {
            // Back navigation exception handling
            // Error level: Navigation failure, critical for flow
            logger.error("Failed to navigate back", e);
            
            // RuntimeException wrap: Navigation failures should be handled
            // Bu throw olmadan: Navigation failures silent, test flow broken
            throw new RuntimeException("Failed to navigate back", e);
        }
    }
    
    /**
     * SAYFA YÜKLENME BEKLEME METODİ - ABSTRACT (SUBCLASS IMPLEMENT EDİLECEK)
     * 
     * Method amacı: Specific page'ın tamamen yüklenmesini bekler
     * Implementation: Her page object kendi loading criteria'larını implement eder
     * Pattern: Template Method Pattern - base structure, specific implementation
     * 
     * Subclass responsibility:
     * - Page-specific loading indicators wait
     * - Key elements visibility check
     * - Page ready state validation
     * - Loading completion criteria definition
     * 
     * Çağrıldığı yerler:
     * - Page navigation sonrası readiness assurance
     * - Page object initialization
     * - Test step başlangıcında page ready guarantee
     */
    public abstract void waitForPageToLoad();
    
    /**
     * SAYFA YÜKLENME DOĞRULAMA METODİ - ABSTRACT (SUBCLASS IMPLEMENT EDİLECEK)
     * 
     * Method amacı: Specific page'ın loaded state'ini validate eder
     * Return değeri: boolean - true: page loaded, false: page not ready
     * Implementation: Her page object kendi validation criteria'larını define eder
     * 
     * Subclass responsibility:
     * - Key element presence validation
     * - Page state verification
     * - Loading completion confirmation
     * - Error state detection
     * 
     * Çağrıldığı yerler:
     * - Page readiness checks
     * - Test assertions (page load verification)
     * - Conditional page operations
     * - Error handling scenarios
     */
    public abstract boolean isPageLoaded();
    
    /**
     * SAYFA TITLE ALMA METODİ - ABSTRACT (SUBCLASS IMPLEMENT EDİLECEK)
     * 
     * Method amacı: Specific page'ın title/identifier'ini return eder
     * Return değeri: String - Page title, name, or identifier
     * Implementation: Her page object kendi title logic'ini implement eder
     * 
     * Subclass responsibility:
     * - Page-specific title extraction
     * - Page identification logic
     * - Title element access
     * - Fallback title handling
     * 
     * Çağrıldığı yerler:
     * - Page identification assertions
     * - Test reporting (current page info)
     * - Navigation validation
     * - Page state logging
     */
    public abstract String getPageTitle();
    
    // BASE PAGE SON - TÜM PAGE OBJECT'LER İÇİN ORTAK FONKSİYONALİTE HAZIR
    // Bu class mobile test automation için comprehensive page object foundation sağlar
    // Element interactions, mobile gestures, platform-specific operations içerir
}