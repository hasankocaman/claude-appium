package com.hepsiburada.pages;

// Appium Mobile PageFactory: Cross-platform element location
// AndroidFindBy: Android-specific element locator annotation
// iOSXCUITFindBy: iOS-specific element locator annotation (XCUITest)
// Bu import'lar olmadan: Cross-platform mobile element location yapılamaz
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

// Allure Reporting: Test step documentation
// @Step annotation: Method'ları Allure reports'da step olarak gösterir
// Bu import olmadan: Test steps Allure'da document edilmez
import io.qameta.allure.Step;

// Selenium WebElement: Mobile element interface
// WebElement: Platform-agnostic element representation
// Bu import olmadan: Mobile element operations yapılamaz
import org.openqa.selenium.WebElement;

/**
 * HEPSİBURADA ANA page CLASS'I - MOBİL UYGULAMA ANA EKRANI
 * 
 * Bu class'ın projede rolü:
 * - Hepsiburada mobil uygulamadanın ana page elements'lerini tanımlar
 * - Cross-platform element locators (Android + iOS)
 * - Home page specific actions ve navigations
 * - Product search functionality
 * - Bottom navigation management
 * - Permission ve onboarding handling
 * - Page validation ve state checking
 * 
 * Kullanılmazsa etki:
 * - Ana page ile interaction yapılamaz
 * - Product search başlatılamaz
 * - Navigation operations eksik
 * - Home page validation missing
 * - Onboarding ve permissions handle edilemez
 * 
 * Diğer class'larla ilişkisi:
 * - BasePage: Bu class extend eder (common mobile operations)
 * - SearchResultsPage: Search operation'dan döner
 * - CartPage, CategoriesPage, AccountPage: Navigation targets
 * - Step Definitions: Test steps bu class'dan home page actions çağırır
 * 
 * Page Object Pattern Implementation:
 * - Element definitions: @AndroidFindBy/@iOSXCUITFindBy annotations
 * - Page actions: Business logic methods
 * - Page validations: State checking methods
 * - Navigation methods: Page transition actions
 * 
 * Cross-Platform Strategy:
 * - Dual locators: Android ve iOS için ayrı element IDs
 * - Platform-agnostic methods: Same method, different locators
 * - Unified API: Test steps platform'dan bağımsız
 * 
 * @author Hepsiburada Test Automation Team
 */
public class HomePage extends BasePage {
    
    // HOME PAGE ELEMENTS - CROSS-PLATFORM ELEMENT DEFINITIONS
    // Tüm element'ler @AndroidFindBy ve @iOSXCUITFindBy ile platform-specific tanımlı
    // PageFactory pattern: Constructor'da otomatik initialize edilir
    
    // SEARCH ELEMENTS - ÜRÜN ARAMA FONKSİYONU ELEMENT'LERİ
    
    // Search Box: Product search input field
    // Android: com.hepsiburada.ecommerce package'ından etSearchBox ID
    // iOS: searchBox accessibility identifier
    // Bu element olmadan: Product search functionality yapılamaz
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/etSearchBox")
    @iOSXCUITFindBy(id = "searchBox")
    private WebElement searchBox;
    
    // Search Icon: Search trigger button/icon
    // Android: ivSearchIcon (ImageView search icon)
    // iOS: searchIcon accessibility identifier  
    // Bu element olmadan: Search action trigger edilemez
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/ivSearchIcon")
    @iOSXCUITFindBy(id = "searchIcon")
    private WebElement searchIcon;
    
    // NAVIGATION ELEMENTS - ALT NAVİGASYON MENUSU ELEMENT'LERİ
    
    // Home Tab: Ana page navigation tab
    // Android: bottomNavHome (bottom navigation home)
    // iOS: homeTab accessibility identifier
    // Bu element olmadan: Home page'e navigation yapılamaz
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/bottomNavHome")
    @iOSXCUITFindBy(id = "homeTab")
    private WebElement homeTab;
    
    // Categories Tab: Kategori pagesı navigation tab
    // Android: bottomNavCategories (categories navigation)
    // iOS: categoriesTab accessibility identifier
    // Bu element olmadan: Categories page navigation eksik
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/bottomNavCategories")
    @iOSXCUITFindBy(id = "categoriesTab")
    private WebElement categoriesTab;
    
    // Cart Tab: Sepet pagesı navigation tab
    // Android: bottomNavCart (shopping cart navigation)
    // iOS: cartTab accessibility identifier
    // Bu element olmadan: Cart page access yapılamaz
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/bottomNavCart")
    @iOSXCUITFindBy(id = "cartTab")
    private WebElement cartTab;
    
    // Account Tab: Hesap pagesı navigation tab
    // Android: bottomNavAccount (user account navigation)
    // iOS: accountTab accessibility identifier
    // Bu element olmadan: Account/profile page access eksik
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/bottomNavAccount")
    @iOSXCUITFindBy(id = "accountTab")
    private WebElement accountTab;
    
    // MAIN CONTENT ELEMENTS - ANA page İÇERİK ELEMENT'LERİ
    
    // Main Banner: Ana page carousel/banner area
    // Android: viewPagerBanner (ViewPager component for banners)
    // iOS: mainBanner accessibility identifier
    // Bu element olmadan: Main promotional banners validation yapılamaz
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/viewPagerBanner")
    @iOSXCUITFindBy(id = "mainBanner")
    private WebElement mainBanner;
    
    // Hepsiburada Logo: App branding logo element
    // Android: ivLogo (ImageView logo)
    // iOS: hepsiburadaLogo accessibility identifier
    // Bu element olmadan: Home page load validation primary indicator eksik
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/ivLogo")
    @iOSXCUITFindBy(id = "hepsiburadaLogo")
    private WebElement hepsiburadaLogo;
    
    // CONTENT SECTIONS - İÇERİK BÖLÜMLERİ
    
    // Categories Grid: Ana kategoriler grid layout
    // Android: rvCategories (RecyclerView categories)
    // iOS: categoriesGrid accessibility identifier
    // Bu element olmadan: Category shortcuts validation yapılamaz
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/rvCategories")
    @iOSXCUITFindBy(id = "categoriesGrid")
    private WebElement categoriesGrid;
    
    // Popular Products List: Popüler ürünler listesi
    // Android: rvPopularProducts (RecyclerView popular products)
    // iOS: popularProductsList accessibility identifier
    // Bu element olmadan: Popular products section validation eksik
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/rvPopularProducts")
    @iOSXCUITFindBy(id = "popularProductsList")
    private WebElement popularProductsList;
    
    // PERMISSION DIALOG ELEMENTS - ANDROID İZİN DİYALOG ELEMENT'LERİ
    
    // Allow Permission Button: Android system permission approval
    // Android only: com.android.packageinstaller system package
    // iOS: Permissions genellikle system-level, app-level handle yok
    // Bu element olmadan: Location, camera vs. permissions auto-grant yapılamaz
    @AndroidFindBy(id = "com.android.packageinstaller:id/permission_allow_button")
    private WebElement allowPermissionButton;
    
    // Deny Permission Button: Android system permission rejection
    // Android only: Alternative permission handling için
    // Test scenarios'da permission denial testing için kullanılabilir
    // Bu element olmadan: Permission denial test scenarios eksik
    @AndroidFindBy(id = "com.android.packageinstaller:id/permission_deny_button")
    private WebElement denyPermissionButton;
    
    // WELCOME/ONBOARDING ELEMENTS - KULLANICI ONBOARDING ELEMENT'LERİ
    
    // Skip Onboarding Button: Onboarding process'ini atlama
    // Android: btnSkip (button skip)
    // iOS: skipButton accessibility identifier
    // Bu element olmadan: Onboarding screens bypass edilemez, tests blocked
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnSkip")
    @iOSXCUITFindBy(id = "skipButton")
    private WebElement skipOnboardingButton;
    
    // Get Started Button: Onboarding process'ini başlatma
    // Android: btnGetStarted (button get started)
    // iOS: getStartedButton accessibility identifier
    // Bu element olmadan: Onboarding completion alternative yapılamaz
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnGetStarted")
    @iOSXCUITFindBy(id = "getStartedButton")
    private WebElement getStartedButton;
    
    // HOME PAGE ELEMENTS SON - TÜM ELEMENT TANIMLAMALARI TAMAMLANDI
    // Cross-platform element strategy ile Android ve iOS support
    
    /**
     * ANA page TAMAMEN YÜKLENME BEKLEM METODİ - BASEPAGE ABSTRACT İMPLEMENTASYONU
     * 
     * Method amacı: Home page'in tamamen yüklenip kullanıma hazır hale gelmesini bekler
     * Override: BasePage abstract method implementation
     * Template Method Pattern: Base structure, HomePage-specific implementation
     * 
     * Kullanılmazsa etki:
     * - Home page ready olmadan test operations başlar
     * - Permissions ve onboarding handle edilmez
     * - Page elements ready olmadan interactions attempt
     * - Flaky test behavior due to timing issues
     * 
     * Home Page Loading Sequence:
     * 1. Permission dialogs handling (Android system permissions)
     * 2. Onboarding screens handling (first-time user flow)
     * 3. Core elements visibility validation (logo, search)
     * 4. Page ready state confirmation
     * 
     * Çağrıldığı yerler:
     * - Test hooks after driver initialization
     * - Navigation methods after page transitions
     * - Step definitions before home page operations
     * 
     * Exception handling: Implicit waits ve element visibility checks
     */
    @Step("Wait for home page to load")
    public void waitForPageToLoad() {
        // Home page loading başlangıç log'u
        // Info level: Major page loading milestone
        logger.info("Waiting for home page to load");
        
        // Android permission dialogs handling (location, camera, storage vs.)
        // handlePermissions(): System permission auto-approval
        // Bu handling olmadan: Permission dialogs test execution'u bloke eder
        handlePermissions();
        
        // First-time user onboarding screens handling
        // handleOnboarding(): Welcome screens, tutorials vs. bypass
        // Bu handling olmadan: Onboarding screens test'leri durdurur
        handleOnboarding();
        
        // Core page elements visibility validation
        // Logo: Primary page load indicator (branding element)
        // Search box: Key functional element availability
        // waitVisible(): BasePage wait utility
        waitVisible(hepsiburadaLogo);
        waitVisible(searchBox);
        
        // Home page loading completion log'u
        // Info level: Page ready confirmation
        logger.info("Home page loaded successfully");
    }
    
    /**
     * ANA page YÜKLENME DURUMU DOĞRULAMA METODİ - BASEPAGE ABSTRACT İMPLEMENTASYONU
     * 
     * Method amacı: Home page'in loaded state'ini validate eder (boolean check)
     * Return değeri: boolean - true: page ready, false: page not loaded
     * Override: BasePage abstract method implementation
     * 
     * waitForPageToLoad vs isPageLoaded farkı:
     * - waitForPageToLoad(): Blocking wait, page hazır olana kadar bekler
     * - isPageLoaded(): Instant check, immediate boolean result
     * - Wait vs Validation: Different use cases
     * 
     * Validation Criteria:
     * - Logo visibility: Primary branding element loaded
     * - Search box availability: Core functionality ready
     * - Home tab presence: Navigation system active
     * - Compound validation: All criteria must pass
     * 
     * Çağrıldığı yerler:
     * - Test assertions (page load verification)
     * - Conditional page operations
     * - Page state validations
     * - Error recovery scenarios
     * 
     * Exception Safety: Try-catch ile graceful failure handling
     */
    
    @Step("Verify home page is loaded")
    public boolean isPageLoaded() {
        // Home page validation başlangıç log'u
        // Info level: Validation operation tracking
        logger.info("Verifying home page is loaded");
        
        try {
            // Compound validation: Multiple criteria check
            // Logo: Branding element (page visual identity)
            // Search box: Core functionality availability
            // Home tab: Navigation system status
            // Logical AND: Tüm criteria pass etmeli
            boolean isLoaded = isDisplayed(hepsiburadaLogo) && 
                              isDisplayed(searchBox) &&
                              isDisplayed(homeTab);
            
            // Validation result log'u (debugging için important)
            // Info level: Validation outcome visibility
            logger.info("Home page loaded status: {}", isLoaded);
            
            // Validation result return
            // Bu return olmadan: Calling code page state bilgi alamaz
            return isLoaded;
            
        } catch (Exception e) {
            // Validation exception handling
            // Error level: Validation failure, investigation gerekebilir
            // Bu error handling olmadan: Exceptions propagate, unsafe
            logger.error("Error verifying home page load status", e);
            
            // False return: Exception durumunda safe assumption (not loaded)
            // Bu false return olmadan: Exception'lar caller'a propagate olur
            return false;
        }
    }
    
    /**
     * Get page title
     * @return Page title
     */
    
    @Step("Get home page title")
    public String getPageTitle() {
        return "Hepsiburada - Home";
    }
    
    /**
     * ÜRÜN ARAMA METODİ - ANA page CORE FONKSİYON
     * 
     * Method amacı: Belirtilen ürün için search operation yapar ve SearchResultsPage döner
     * Parametreler: searchTerm - Aranacak ürün adı/keyword (String)
     * Return değeri: SearchResultsPage instance (page object pattern)
     * 
     * Kullanılmazsa etki:
     * - Product search functionality test edilemez
     * - E-commerce core flow broken (search is primary feature)
     * - SearchResultsPage'e navigation yapılamaz
     * - Product discovery scenarios eksik
     * 
     * Search Process Flow:
     * 1. Search box activation (click to focus)
     * 2. Search term input (text entry)
     * 3. Search trigger (icon click or enter)
     * 4. Page transition (SearchResultsPage)
     * 
     * Diğer metodlarla kıyasla:
     * - navigateToCategories(): Direct navigation vs. search-based discovery
     * - Most critical home page method: Primary user journey
     * - Page Object Pattern: Returns new page instance
     * 
     * Çağrıldığı yerler:
     * - Product search step definitions
     * - E-commerce flow test scenarios
     * - Search functionality validation tests
     * - MacBook purchase journey (main test scenario)
     * 
     * Search Pattern: Click + Type + Submit + Page Transition
     */
    @Step("Search for product: '{searchTerm}'")
    public SearchResultsPage searchForProduct(String searchTerm) {
        // Search operation başlangıç log'u (search term ile)
        // Info level: Major user action, business functionality
        // Parameterized logging: Actual search term visibility
        logger.info("Searching for product: {}", searchTerm);
        
        try {
            // Search box activation (element focus için click)
            // click(): BasePage utility, safe click with wait
            // Bu click olmadan: Search box focus olmaz, text input fails
            click(searchBox);
            
            // Search term text input
            // type(): BasePage utility, clear + type pattern
            // Bu text input olmadan: Search query girilmez, empty search
            type(searchBox, searchTerm);
            
            // Search trigger action (search icon click)
            // click(): Search execution trigger
            // Alternative: Enter key press, ama click more reliable
            // Bu trigger olmadan: Search query execute edilmez
            click(searchIcon);
            
            // Search initiation confirmation log'u
            // Info level: Search operation success confirmation
            logger.info("Search initiated for: {}", searchTerm);
            
            // SearchResultsPage return (Page Object Pattern)
            // new SearchResultsPage(): Next page object instantiation
            // Bu return olmadan: Calling code next page access alamaz
            return new SearchResultsPage();
            
        } catch (Exception e) {
            // Search operation exception handling
            // Error level: Critical business functionality failure
            // Parameterized error: Failed search term context
            logger.error("Failed to search for product: {}", searchTerm, e);
            
            // RuntimeException wrap: Search failure should stop test
            // Bu throw olmadan: Search failures silent kalabilir, invalid test states
            throw new RuntimeException("Failed to search for product: " + searchTerm, e);
        }
    }
    
    /**
     * KATEGORİ pageSI NAVİGASYON METODİ - ALT NAVİGASYON SEKMESİ
     * 
     * Method amacı: Bottom navigation'dan Categories sekmesine navigation yapar
     * Return değeri: CategoriesPage instance (page object pattern)
     * Navigation Pattern: Tab click + Page object return
     * 
     * Kullanılmazsa etki:
     * - Kategori pagesına navigation yapılamaz
     * - Category-based product browsing eksik
     * - E-commerce kategori discovery flow broken
     * - Category navigation test scenarios çalışmaz
     * 
     * Navigation vs Search farkı:
     * - navigateToCategories(): Structured category browsing
     * - searchForProduct(): Keyword-based product discovery
     * - Different user journey patterns
     * 
     * Çağrıldığı yerler:
     * - Category browsing test scenarios
     * - Navigation flow validation tests
     * - Multi-page journey test sequences
     * - Category-specific product discovery
     * 
     * Exception handling: Navigation failure durumunda RuntimeException
     * 
     * @return CategoriesPage instance - Next page object for continued operations
     */
    @Step("Navigate to categories")
    public CategoriesPage navigateToCategories() {
        // Categories navigation başlangıç log'u
        // Info level: Major navigation operation
        logger.info("Navigating to categories page");
        
        try {
            // Categories tab click action
            // click(): BasePage safe click utility
            // Bu click olmadan: Categories page'e transition yapılamaz
            click(categoriesTab);
            
            // Navigation success confirmation log'u
            // Info level: Successful navigation tracking
            logger.info("Successfully navigated to categories page");
            
            // CategoriesPage instance return (Page Object Pattern)
            // new CategoriesPage(): Next page object instantiation
            // Bu return olmadan: Calling code categories page operations alamaz
            return new CategoriesPage();
            
        } catch (Exception e) {
            // Navigation failure exception handling
            // Error level: Navigation critical işlem failure
            logger.error("Failed to navigate to categories page", e);
            
            // RuntimeException throw: Navigation failure test'i durdurmali
            // Bu throw olmadan: Navigation failures silent, invalid page states
            throw new RuntimeException("Failed to navigate to categories page", e);
        }
    }
    
    /**
     * SEPET pageSI NAVİGASYON METODİ - E-COMMERCE CART ACCESS
     * 
     * Method amacı: Bottom navigation'dan Shopping Cart sekmesine navigation yapar
     * Return değeri: CartPage instance (page object pattern)
     * E-commerce Pattern: Shopping cart access for checkout flow
     * 
     * Kullanılmazsa etki:
     * - Shopping cart'a access yapılamaz
     * - Checkout flow başlatılamaz
     * - Added products review edilemez
     * - Purchase journey broken (critical e-commerce flow)
     * 
     * E-commerce Journey'de yeri:
     * - Product Selection → Add to Cart → navigateToCart() → Checkout
     * - MacBook purchase scenario'da critical step
     * - Cart management operations için entry point
     * 
     * Diğer navigation methods kıyasla:
     * - navigateToCart(): Transaction-focused, checkout preparation
     * - navigateToCategories(): Discovery-focused, product browsing
     * - navigateToAccount(): Account management-focused
     * 
     * Çağrıldığı yerler:
     * - Purchase flow test scenarios
     * - Cart validation test cases
     * - Checkout process başlatma
     * - Added products verification
     * 
     * Transaction Safety: Exception handling ile cart access guarantee
     * 
     * @return CartPage instance - Shopping cart page for checkout operations
     */
    @Step("Navigate to cart")
    public CartPage navigateToCart() {
        // Cart navigation başlangıç log'u
        // Info level: Critical e-commerce operation
        logger.info("Navigating to cart page");
        
        try {
            // Cart tab click action
            // click(): BasePage safe interaction
            // Bu click olmadan: Shopping cart access yapılamaz
            click(cartTab);
            
            // Cart navigation success log'u
            // Info level: Critical transaction operation success
            logger.info("Successfully navigated to cart page");
            
            // CartPage instance return (E-commerce Page Pattern)
            // new CartPage(): Shopping cart page object
            // Bu return olmadan: Cart operations devam edemez
            return new CartPage();
            
        } catch (Exception e) {
            // Cart navigation failure handling
            // Error level: Critical e-commerce flow failure
            logger.error("Failed to navigate to cart page", e);
            
            // RuntimeException throw: Cart access failure critical
            // Bu throw olmadan: Checkout flow invalid state'te devam edebilir
            throw new RuntimeException("Failed to navigate to cart page", e);
        }
    }
    
    /**
     * HESAP pageSI NAVİGASYON METODİ - KULLANICI PROFİLİ ACCESS
     * 
     * Method amacı: Bottom navigation'dan Account/Profile sekmesine navigation yapar
     * Return değeri: AccountPage instance (page object pattern)
     * User Management Pattern: Profile, settings, account operations
     * 
     * Kullanılmazsa etki:
     * - User account pagesına access yapılamaz
     * - Profile management operations eksik
     * - Account settings navigation broken
     * - User authentication flows test edilemez
     * 
     * Account Operations içerdiği alanlar:
     * - User profile information
     * - Account settings management
     * - Order history access
     * - Address book management
     * - Payment methods configuration
     * 
     * Diğer navigation methods ile karşılaştırma:
     * - navigateToAccount(): User-focused, personal data management
     * - navigateToCart(): Transaction-focused, purchase operations
     * - navigateToCategories(): Discovery-focused, product exploration
     * 
     * Çağrıldığı yerler:
     * - User profile test scenarios
     * - Account management test cases
     * - Authentication flow validations
     * - Personal settings management tests
     * 
     * Security Consideration: Account access needs authentication validation
     * 
     * @return AccountPage instance - User account page for profile operations
     */
    @Step("Navigate to account")
    public AccountPage navigateToAccount() {
        // Account navigation başlangıç log'u
        // Info level: User-focused operation
        logger.info("Navigating to account page");
        
        try {
            // Account tab click action
            // click(): BasePage secure interaction
            // Bu click olmadan: User account access yapılamaz
            click(accountTab);
            
            // Account navigation success log'u
            // Info level: User profile access confirmation
            logger.info("Successfully navigated to account page");
            
            // AccountPage instance return (User Management Pattern)
            // new AccountPage(): User account page object
            // Bu return olmadan: Account operations başlatılamaz
            return new AccountPage();
            
        } catch (Exception e) {
            // Account navigation failure handling
            // Error level: User access critical failure
            logger.error("Failed to navigate to account page", e);
            
            // RuntimeException throw: Account access failure önemli
            // Bu throw olmadan: User flows invalid state'te devam edebilir
            throw new RuntimeException("Failed to navigate to account page", e);
        }
    }
    
    /**
     * SEARCH BOX GÖRÜNÜRLÜK KONTROL METODİ - CORE ELEMENT VALİDASYONU
     * 
     * Method amacı: Search box element'ın ekranda görünür olup olmadığını kontrol eder
     * Return değeri: boolean - true: search box visible, false: not visible
     * Validation Pattern: Element visibility boolean check
     * 
     * Kullanılmazsa etki:
     * - Search functionality availability doğrulanamaz
     * - Home page core element validation eksik
     * - Search-based test scenarios prerequisite check missing
     * - UI state validation incomplete
     * 
     * Search Box'un önemi:
     * - Primary product discovery mechanism
     * - E-commerce core functionality indicator
     * - User interaction primary entry point
     * - Page load success primary indicator
     * 
     * Validation vs Operation farkı:
     * - isSearchBoxDisplayed(): Read-only visibility check
     * - searchForProduct(): Interactive operation (click + type)
     * - Different purposes: Validation vs Action
     * 
     * Çağrıldığı yerler:
     * - Home page load validation tests
     * - Search functionality prerequisite checks
     * - UI state assertion tests
     * - Page readiness conditional logic
     * 
     * Thread Safety: isDisplayed() BasePage utility'den inherited
     * 
     * @return boolean - Search box visibility status (true=visible, false=hidden)
     */
    @Step("Check if search box is displayed")
    public boolean isSearchBoxDisplayed() {
        // Search box visibility check
        // isDisplayed(): BasePage utility, safe visibility check
        // Bu return olmadan: Calling code search box state bilemez
        return isDisplayed(searchBox);
    }
    
    /**
     * MAIN BANNER GÖRÜNÜRLÜK KONTROL METODİ - PROMOSYON İÇERİK VALİDASYONU
     * 
     * Method amacı: Ana page main banner/carousel element'ının görünürlüğünü kontrol eder
     * Return değeri: boolean - true: main banner visible, false: not visible
     * Marketing Content Pattern: Promotional content visibility check
     * 
     * Kullanılmazsa etki:
     * - Ana page promotional content validation yapılamaz
     * - Marketing campaigns görünürlüğü test edilemez
     * - Home page visual content completeness check missing
     * - Banner-based navigation test scenarios eksik
     * 
     * Main Banner'in içerdiği content:
     * - Promotional campaigns (indirimler, özel teklifler)
     * - Featured products showcase
     * - Seasonal marketing content
     * - Brand partnerships ve collaborations
     * - Interactive carousel/slider content
     * 
     * Banner vs Functional Elements karşılaştırması:
     * - isMainBannerDisplayed(): Marketing content validation
     * - isSearchBoxDisplayed(): Functional element validation
     * - Different priorities: Visual vs Functional
     * 
     * Çağrıldığı yerler:
     * - Home page visual content validation tests
     * - Marketing campaign display tests
     * - Page content completeness checks
     * - UI layout validation scenarios
     * 
     * Performance Consideration: Banner content loading critical için page performance
     * 
     * @return boolean - Main banner visibility status (true=displayed, false=hidden)
     */
    @Step("Check if main banner is displayed")
    public boolean isMainBannerDisplayed() {
        // Main banner visibility check
        // isDisplayed(): BasePage utility, promotional content check
        // Bu return olmadan: Banner content validation yapılamaz
        return isDisplayed(mainBanner);
    }
    
    /**
     * CATEGORİES GRİD GÖRÜNÜRLÜK KONTROL METODİ - STRUKTUR KATEGORİ VALİDASYONU
     * 
     * Method amacı: Ana pagedaki categories grid layout'ının görünürlüğünü kontrol eder
     * Return değeri: boolean - true: categories grid visible, false: not visible
     * E-commerce Structure Pattern: Category organization visibility
     * 
     * Kullanılmazsa etki:
     * - Category-based navigation validation yapılamaz
     * - E-commerce product organization structure test edilemez
     * - Home page category shortcuts availability check missing
     * - Structured product discovery validation eksik
     * 
     * Categories Grid'in içeriği:
     * - Product categories shortcuts (Elektronik, Moda, Ev & Yaşam vs.)
     * - Visual category representations (icons, images)
     * - Quick navigation elements
     * - Popular categories highlighting
     * - Grid layout organization
     * 
     * Category Navigation Patterns:
     * - isCategoriesGridDisplayed(): Home page category shortcuts validation
     * - navigateToCategories(): Dedicated categories page navigation
     * - searchForProduct(): Keyword-based product discovery
     * - Different approaches: Visual shortcuts vs Full page vs Search
     * 
     * Çağrıldığı yerler:
     * - Home page content structure validation
     * - Category navigation readiness checks
     * - E-commerce organization validation tests
     * - UI layout completeness verification
     * 
     * UX Impact: Categories grid user'lara structured browsing sağlar
     * 
     * @return boolean - Categories grid visibility status (true=displayed, false=hidden)
     */
    @Step("Check if categories grid is displayed")
    public boolean isCategoriesGridDisplayed() {
        // Categories grid visibility check
        // isDisplayed(): BasePage utility, structural content validation
        // Bu return olmadan: Category structure validation yapılamaz
        return isDisplayed(categoriesGrid);
    }
    
    /**
     * ANDROID SİSTEM İZİNLERİ YÖNETİM METODİ - SİSTEM PERMİSSİON HANDLİNG
     * 
     * Method amacı: Android sistem permission dialog'larını otomatik handle eder
     * Visibility: private - Internal helper method, dışarıdan çağrılmaz
     * Platform Specific: Android-only functionality, iOS'ta system permissions farklı
     * 
     * Kullanılmazsa etki:
     * - Android sistem permission dialogs test execution'u bloke eder
     * - Location, camera, storage permissions manual intervention gerektirir
     * - Automated test flow kesintiye uğrar
     * - CI/CD pipeline'da permission dialogs tests fail yapar
     * 
     * Android Permission Types:
     * - Location permissions (GPS, network location)
     * - Camera permissions (photo, video recording)
     * - Storage permissions (file read/write)
     * - Notification permissions (push notifications)
     * - Phone state permissions (device info access)
     * 
     * Permission Handling Strategy:
     * - Proactive detection: Permission dialog presence check
     * - Auto-approval: allowPermissionButton click
     * - Graceful fallback: Exception handling ile silent failure
     * - Non-blocking: Test flow devam eder
     * 
     * Çağrıldığı yerler:
     * - waitForPageToLoad(): Home page loading sequence'da
     * - App first launch scenarios'da
     * - Fresh installation test cases'de
     * 
     * Security Note: Test environment'da permissions auto-grant acceptable
     */
    @Step("Handle permissions")
    private void handlePermissions() {
        try {
            // Permission dialog detection başlangıç log'u
            // Info level: System permission handling milestone
            logger.info("Checking for permission dialogs");
            
            // Permission dialog appearance wait
            // 2000ms: Android system dialog render time
            // Bu wait olmadan: Dialog detection timing issues
            Thread.sleep(2000);
            
            // Permission dialog presence check
            // isDisplayed(): Safe element detection
            // allowPermissionButton: Android system permission approval button
            if (isDisplayed(allowPermissionButton)) {
                // Permission dialog detection log'u
                // Info level: Permission dialog found, action needed
                logger.info("Permission dialog detected, granting permissions");
                
                // Permission approval action
                // click(): BasePage safe click utility
                // Bu click olmadan: Permission dialog resolved olmaz, test blocked
                click(allowPermissionButton);
                
                // Permission grant success log'u
                // Info level: Permission handling completion
                logger.info("Permissions granted");
            }
        } catch (Exception e) {
            // Permission handling exception
            // Debug level: Expected scenario, permissions may not appear
            // Bu exception handling olmadan: Permission absence test fail yapar
            logger.debug("No permission dialog found or error handling permissions", e);
        }
    }
    
    /**
     * KULLANICI ONBOARDİNG SÜRECİ YÖNETİM METODİ - İLK KULLANIM FİRST LAUNCH HANDLİNG
     * 
     * Method amacı: First-time user onboarding screens'leri otomatik bypass eder
     * Visibility: private - Internal helper method, waitForPageToLoad'dan çağrılır
     * User Experience: New user introduction sequence handling
     * 
     * Kullanılmazsa etki:
     * - First launch onboarding screens test execution'u durdurur
     * - Welcome tutorials manual skip gerektirir
     * - New user flow test scenarios block olur
     * - Fresh app installation tests fail
     * 
     * Onboarding Screen Types:
     * - Welcome screens (app introduction)
     * - Feature tutorials (app functionality overview)
     * - Permission explanation screens
     * - Account creation prompts
     * - Notification opt-in screens
     * 
     * Onboarding Handling Options:
     * - Option 1: Skip button - Onboarding'i atlar, main app'e gider
     * - Option 2: Get Started button - Onboarding'i tamamlar
     * - Flexible strategy: Her iki option'i da support eder
     * 
     * Skip vs Get Started strategy:
     * - skipOnboardingButton: Fastest path to main functionality
     * - getStartedButton: Complete onboarding experience
     * - Test automation: Skip preferred (faster execution)
     * 
     * Çağrıldığı yerler:
     * - waitForPageToLoad(): Home page loading sequence
     * - Fresh app installation scenarios
     * - First-time user simulation tests
     * 
     * UX Consideration: Onboarding user retention için önemli, ama test'lerde bypass
     */
    @Step("Handle onboarding")
    private void handleOnboarding() {
        try {
            // Onboarding detection başlangıç log'u
            // Info level: User onboarding handling milestone
            logger.info("Checking for onboarding screens");
            
            // Onboarding screen appearance wait
            // 2000ms: UI transition ve screen rendering time
            // Bu wait olmadan: Onboarding elements timing miss edilir
            Thread.sleep(2000);
            
            // Skip onboarding option check (preferred path)
            // isDisplayed(): Safe element detection
            // skipOnboardingButton: Fastest bypass to main app
            if (isDisplayed(skipOnboardingButton)) {
                // Skip onboarding detection log'u
                // Info level: Skip option found, bypassing onboarding
                logger.info("Onboarding screen detected, skipping");
                
                // Skip onboarding action
                // click(): BasePage safe interaction
                // Bu click olmadan: Onboarding bypass yapılamaz
                click(skipOnboardingButton);
                
                // Skip completion log'u
                // Info level: Onboarding bypass success
                logger.info("Onboarding skipped");
                
            // Alternative: Get Started option check
            // getStartedButton: Complete onboarding alternative
            } else if (isDisplayed(getStartedButton)) {
                // Get started detection log'u
                // Info level: Alternative onboarding completion path
                logger.info("Get started button detected, clicking");
                
                // Get started action
                // click(): Onboarding completion trigger
                // Bu click olmadan: Onboarding complete olmaz
                click(getStartedButton);
                
                // Get started completion log'u
                // Info level: Onboarding completion success
                logger.info("Get started clicked");
            }
        } catch (Exception e) {
            // Onboarding handling exception
            // Debug level: Onboarding screens may not always appear
            // Bu exception handling olmadan: Onboarding absence test fail yapabilir
            logger.debug("No onboarding screen found or error handling onboarding", e);
        }
    }
    
    /**
     * POPÜLER ÜRÜNLER BÖLÜMÜNE SCROLL METODİ - DİNAMİK İÇERİK KEŞFİ
     * 
     * Method amacı: Ana pageda popüler ürünler section'ına scroll yaparak görünür hale getirir
     * Return değeri: void - Action method, side effect (scroll operation)
     * Mobile Interaction Pattern: Vertical scrolling with target element detection
     * 
     * Kullanılmazsa etki:
     * - Popüler ürünler section'u viewport dışında kalabilir
     * - Long page content'inde below-fold elements access edilemez
     * - Popular products validation ve interaction yapılamaz
     * - Mobile page content discovery incomplete
     * 
     * Scroll Strategy Components:
     * - Target element: popularProductsList (destination)
     * - Scroll mechanism: scrollDown() (BasePage utility)
     * - Visibility detection: isDisplayed() (success criteria)
     * - Attempt limiting: maxScrollAttempts (infinite loop prevention)
     * - Timing control: Thread.sleep() (scroll completion wait)
     * 
     * Mobile Scrolling Challenges:
     * - Dynamic content loading (lazy loading)
     * - Variable scroll distances
     * - Platform differences (Android vs iOS)
     * - Performance considerations (scroll smoothness)
     * 
     * Scroll vs Navigation farkı:
     * - scrollToPopularProducts(): Same page content discovery
     * - navigateToCategories(): Different page navigation
     * - scrollDown(): Generic scroll operation
     * - Bu method: Target-specific scroll with validation
     * 
     * Çağrıldığı yerler:
     * - Popular products validation scenarios
     * - Home page content completeness tests
     * - Below-fold content interaction tests
     * - Mobile scrolling behavior validations
     * 
     * Algorithm: Iterative scroll with success detection ve attempt limiting
     */
    @Step("Scroll to popular products")
    public void scrollToPopularProducts() {
        // Scroll operation başlangıç log'u
        // Info level: Mobile interaction operation
        logger.info("Scrolling to popular products section");
        
        try {
            // Scroll configuration constants
            // maxScrollAttempts: Infinite loop prevention (5 attempts reasonable)
            // scrollAttempts: Current attempt counter (loop control)
            int maxScrollAttempts = 5;
            int scrollAttempts = 0;
            
            // Iterative scroll loop - target element visibility check
            // Condition 1: !isDisplayed(popularProductsList) - Target not visible yet
            // Condition 2: scrollAttempts < maxScrollAttempts - Attempt limit protection
            // Logical AND: Both conditions must be true to continue scrolling
            while (!isDisplayed(popularProductsList) && scrollAttempts < maxScrollAttempts) {
                // Scroll down operation
                // scrollDown(): BasePage mobile gesture utility
                // Bu scroll olmadan: Target element viewport'a gelmiyor
                scrollDown();
                
                // Scroll attempt increment
                // scrollAttempts++: Loop control, attempt tracking
                // Bu increment olmadan: Infinite loop risk
                scrollAttempts++;
                
                // Scroll completion wait
                // 1000ms: Scroll animation ve content loading time
                // Bu wait olmadan: Visibility check premature, scroll incomplete
                Thread.sleep(1000);
            }
            
            // Scroll success validation
            // isDisplayed(): Final target visibility check
            if (isDisplayed(popularProductsList)) {
                // Scroll success log'u
                // Info level: Target reached successfully
                logger.info("Popular products section is now visible");
            } else {
                // Scroll failure warning
                // Warn level: Target not reached, may indicate layout issues
                // Parameterized logging: Attempt count visibility
                logger.warn("Popular products section not found after {} scroll attempts", maxScrollAttempts);
            }
            
        } catch (Exception e) {
            // Scroll operation exception handling
            // Error level: Mobile interaction critical failure
            logger.error("Error scrolling to popular products", e);
        }
    }
    
    // HOMEPAGE CLASS SON - TÜM METODLAR VE ELEMENT'LER DOKUMENTE EDİLDİ
    // 
    // Bu HomePage class'ı içeriyor:
    // 1. Cross-platform element definitions (Android + iOS locators)
    // 2. Page lifecycle methods (waitForPageToLoad, isPageLoaded, getPageTitle)
    // 3. Core functionality (searchForProduct)
    // 4. Navigation methods (navigateToCategories, navigateToCart, navigateToAccount)
    // 5. Validation methods (isSearchBoxDisplayed, isMainBannerDisplayed, isCategoriesGridDisplayed)
    // 6. Helper methods (handlePermissions, handleOnboarding, scrollToPopularProducts)
    // 
    // Page Object Pattern Implementation:
    // - Element definitions: @AndroidFindBy/@iOSXCUITFindBy annotations
    // - Business methods: User-focused operation methods
    // - Navigation methods: Page transition methods returning page objects
    // - Validation methods: Page state boolean check methods
    // - Helper methods: Internal support methods (private)
    // 
    // Tüm metodlar Turkish documentation ile comprehensive olarak dokumente edildi
    // Test automation framework'de key role: Home page operations ve validations
}