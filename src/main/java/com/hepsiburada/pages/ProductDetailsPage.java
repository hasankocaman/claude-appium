package com.hepsiburada.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Product Details Page class representing individual product page
 * Contains elements and methods for product interaction and cart operations
 * 
 * @author Hepsiburada Test Automation Team
 */
public class ProductDetailsPage extends BasePage {
    
    // Product information elements
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvProductTitle")
    @iOSXCUITFindBy(id = "productTitle")
    private WebElement productTitle;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvProductPrice")
    @iOSXCUITFindBy(id = "productPrice")
    private WebElement productPrice;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvOriginalPrice")
    @iOSXCUITFindBy(id = "originalPrice")
    private WebElement originalPrice;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvDiscountPercentage")
    @iOSXCUITFindBy(id = "discountPercentage")
    private WebElement discountPercentage;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvBrandName")
    @iOSXCUITFindBy(id = "brandName")
    private WebElement brandName;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/ratingBar")
    @iOSXCUITFindBy(id = "ratingBar")
    private WebElement ratingBar;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvRatingCount")
    @iOSXCUITFindBy(id = "ratingCount")
    private WebElement ratingCount;
    
    // Product images
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/viewPagerImages")
    @iOSXCUITFindBy(id = "productImageSlider")
    private WebElement productImageSlider;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/ivProductImage")
    @iOSXCUITFindBy(id = "productImage")
    private List<WebElement> productImages;
    
    // Action buttons
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnAddToCart")
    @iOSXCUITFindBy(id = "addToCartButton")
    private WebElement addToCartButton;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnBuyNow")
    @iOSXCUITFindBy(id = "buyNowButton")
    private WebElement buyNowButton;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnAddToFavorites")
    @iOSXCUITFindBy(id = "addToFavoritesButton")
    private WebElement addToFavoritesButton;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnShare")
    @iOSXCUITFindBy(id = "shareButton")
    private WebElement shareButton;
    
    // Product options (size, color, etc.)
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/rvProductOptions")
    @iOSXCUITFindBy(id = "productOptionsList")
    private WebElement productOptionsList;
    
    @AndroidFindBy(className = "android.widget.TextView")
    @iOSXCUITFindBy(className = "XCUIElementTypeButton")
    private List<WebElement> productOptionButtons;
    
    // Quantity selector
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnDecreaseQuantity")
    @iOSXCUITFindBy(id = "decreaseQuantityButton")
    private WebElement decreaseQuantityButton;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnIncreaseQuantity")
    @iOSXCUITFindBy(id = "increaseQuantityButton")
    private WebElement increaseQuantityButton;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvQuantity")
    @iOSXCUITFindBy(id = "quantityText")
    private WebElement quantityText;
    
    // Product description and details
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvProductDescription")
    @iOSXCUITFindBy(id = "productDescription")
    private WebElement productDescription;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvProductFeatures")
    @iOSXCUITFindBy(id = "productFeatures")
    private WebElement productFeatures;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnShowMoreDetails")
    @iOSXCUITFindBy(id = "showMoreDetailsButton")
    private WebElement showMoreDetailsButton;
    
    // Shipping and delivery info
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvShippingInfo")
    @iOSXCUITFindBy(id = "shippingInfo")
    private WebElement shippingInfo;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvDeliveryTime")
    @iOSXCUITFindBy(id = "deliveryTime")
    private WebElement deliveryTime;
    
    // Reviews section
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/rvReviews")
    @iOSXCUITFindBy(id = "reviewsList")
    private WebElement reviewsList;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnShowAllReviews")
    @iOSXCUITFindBy(id = "showAllReviewsButton")
    private WebElement showAllReviewsButton;
    
    // Success messages
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvAddToCartSuccess")
    @iOSXCUITFindBy(id = "addToCartSuccessMessage")
    private WebElement addToCartSuccessMessage;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/snackbar")
    @iOSXCUITFindBy(id = "snackbarMessage")
    private WebElement snackbarMessage;
    
    // Navigation elements
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnBack")
    @iOSXCUITFindBy(id = "backButton")
    private WebElement backButton;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnCart")
    @iOSXCUITFindBy(id = "cartButton")
    private WebElement cartButton;
    
    /**
     * Wait for product details page to load
     */
    @Step("Wait for product details page to load")
    public void waitForPageToLoad() {
        logger.info("Waiting for product details page to load");
        
        try {
            waitVisible(productTitle);
            waitVisible(productPrice);
            waitVisible(addToCartButton);
            
            logger.info("Product details page loaded successfully");
        } catch (Exception e) {
            logger.error("Error waiting for product details page to load", e);
            throw new RuntimeException("Failed to load product details page", e);
        }
    }
    
    /**
     * Check if product details page is loaded
     * @return true if page is loaded
     */
    
    @Step("Verify product details page is loaded")
    public boolean isPageLoaded() {
        logger.info("Verifying product details page is loaded");
        
        try {
            return isDisplayed(productTitle) && 
                   isDisplayed(productPrice) &&
                   isDisplayed(addToCartButton);
        } catch (Exception e) {
            logger.error("Error verifying product details page load status", e);
            return false;
        }
    }
    
    /**
     * Get page title
     * @return Page title
     */
    
    @Step("Get product details page title")
    public String getPageTitle() {
        return "Hepsiburada - Product Details";
    }
    
    /**
     * Get product title
     * @return Product title
     */
    @Step("Get product title")
    public String getProductTitle() {
        logger.info("Getting product title");
        
        try {
            String title = getText(productTitle);
            logger.info("Product title: {}", title);
            return title;
        } catch (Exception e) {
            logger.error("Failed to get product title", e);
            return "";
        }
    }
    
    /**
     * Get product price
     * @return Product price
     */
    @Step("Get product price")
    public String getProductPrice() {
        logger.info("Getting product price");
        
        try {
            String price = getText(productPrice);
            logger.info("Product price: {}", price);
            return price;
        } catch (Exception e) {
            logger.error("Failed to get product price", e);
            return "";
        }
    }
    
    /**
     * Get brand name
     * @return Brand name
     */
    @Step("Get brand name")
    public String getBrandName() {
        logger.info("Getting brand name");
        
        try {
            if (isDisplayed(brandName)) {
                String brand = getText(brandName);
                logger.info("Brand name: {}", brand);
                return brand;
            }
            return "";
        } catch (Exception e) {
            logger.error("Failed to get brand name", e);
            return "";
        }
    }
    
    /**
     * Add product to cart
     * @return CartPage instance
     */
    @Step("Add product to cart")
    public CartPage addToCart() {
        logger.info("Adding product to cart");
        
        try {
            // Scroll to make sure add to cart button is visible
            scrollToElement(addToCartButton);
            
            // Click add to cart button
            click(addToCartButton);
            
            // Wait for success message or navigation
            Thread.sleep(2000);
            
            // Check for success message
            if (isDisplayed(addToCartSuccessMessage)) {
                String successMessage = getText(addToCartSuccessMessage);
                logger.info("Product added to cart successfully: {}", successMessage);
            } else if (isDisplayed(snackbarMessage)) {
                String successMessage = getText(snackbarMessage);
                logger.info("Product added to cart with message: {}", successMessage);
            }
            
            logger.info("Product successfully added to cart");
            
            // Navigate to cart if cart button is available
            if (isDisplayed(cartButton)) {
                click(cartButton);
                return new CartPage();
            }
            
            return new CartPage();
            
        } catch (Exception e) {
            logger.error("Failed to add product to cart", e);
            throw new RuntimeException("Failed to add product to cart", e);
        }
    }
    
    /**
     * Add product to favorites
     */
    @Step("Add product to favorites")
    public void addToFavorites() {
        logger.info("Adding product to favorites");
        
        try {
            click(addToFavoritesButton);
            
            // Wait for confirmation
            Thread.sleep(1000);
            
            logger.info("Product added to favorites successfully");
        } catch (Exception e) {
            logger.error("Failed to add product to favorites", e);
            throw new RuntimeException("Failed to add product to favorites", e);
        }
    }
    
    /**
     * Increase product quantity
     */
    @Step("Increase product quantity")
    public void increaseQuantity() {
        logger.info("Increasing product quantity");
        
        try {
            if (isDisplayed(increaseQuantityButton)) {
                click(increaseQuantityButton);
                logger.info("Quantity increased successfully");
            }
        } catch (Exception e) {
            logger.error("Failed to increase quantity", e);
        }
    }
    
    /**
     * Decrease product quantity
     */
    @Step("Decrease product quantity")
    public void decreaseQuantity() {
        logger.info("Decreasing product quantity");
        
        try {
            if (isDisplayed(decreaseQuantityButton)) {
                click(decreaseQuantityButton);
                logger.info("Quantity decreased successfully");
            }
        } catch (Exception e) {
            logger.error("Failed to decrease quantity", e);
        }
    }
    
    /**
     * Get current quantity
     * @return Current quantity
     */
    @Step("Get current quantity")
    public String getCurrentQuantity() {
        logger.info("Getting current quantity");
        
        try {
            if (isDisplayed(quantityText)) {
                String quantity = getText(quantityText);
                logger.info("Current quantity: {}", quantity);
                return quantity;
            }
            return "1"; // Default quantity
        } catch (Exception e) {
            logger.error("Failed to get current quantity", e);
            return "1";
        }
    }
    
    /**
     * Check if product is MacBook Pro
     * @return true if product is MacBook Pro
     */
    @Step("Check if product is MacBook Pro")
    public boolean isMacBookPro() {
        logger.info("Checking if product is MacBook Pro");
        
        try {
            String title = getProductTitle();
            boolean isMacBookPro = title.toLowerCase().contains("macbook pro");
            logger.info("Is MacBook Pro: {}", isMacBookPro);
            return isMacBookPro;
        } catch (Exception e) {
            logger.error("Failed to check if product is MacBook Pro", e);
            return false;
        }
    }
    
    /**
     * Verify product details are displayed
     * @return true if all main details are displayed
     */
    @Step("Verify product details are displayed")
    public boolean areProductDetailsDisplayed() {
        logger.info("Verifying product details are displayed");
        
        try {
            boolean titleDisplayed = isDisplayed(productTitle);
            boolean priceDisplayed = isDisplayed(productPrice);
            boolean buttonDisplayed = isDisplayed(addToCartButton);
            
            boolean allDisplayed = titleDisplayed && priceDisplayed && buttonDisplayed;
            logger.info("Product details displayed - Title: {}, Price: {}, Button: {}, All: {}", 
                       titleDisplayed, priceDisplayed, buttonDisplayed, allDisplayed);
            
            return allDisplayed;
        } catch (Exception e) {
            logger.error("Error verifying product details display", e);
            return false;
        }
    }
    
    /**
     * Scroll to specific element
     * @param element Element to scroll to
     */
    @Step("Scroll to element")
    private void scrollToElement(WebElement element) {
        try {
            int maxScrollAttempts = 5;
            int scrollAttempts = 0;
            
            while (!isDisplayed(element) && scrollAttempts < maxScrollAttempts) {
                scrollDown();
                scrollAttempts++;
                Thread.sleep(1000);
            }
            
            if (!isDisplayed(element)) {
                logger.warn("Element not visible after {} scroll attempts", maxScrollAttempts);
            }
        } catch (Exception e) {
            logger.error("Error scrolling to element", e);
        }
    }
    
    /**
     * Navigate back to previous page
     */
    @Step("Navigate back")
    public void navigateBack() {
        logger.info("Navigating back to previous page");
        
        try {
            if (isDisplayed(backButton)) {
                click(backButton);
            } else {
                goBack();
            }
            
            logger.info("Successfully navigated back");
        } catch (Exception e) {
            logger.error("Failed to navigate back", e);
            throw new RuntimeException("Failed to navigate back", e);
        }
    }
}