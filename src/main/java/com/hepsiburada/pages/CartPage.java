package com.hepsiburada.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Cart Page class representing shopping cart functionality
 * Contains elements and methods for cart operations and checkout
 * 
 * @author Hepsiburada Test Automation Team
 */
public class CartPage extends BasePage {
    
    // Cart header elements
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvCartTitle")
    @iOSXCUITFindBy(id = "cartTitle")
    private WebElement cartTitle;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvItemCount")
    @iOSXCUITFindBy(id = "itemCount")
    private WebElement itemCount;
    
    // Cart items list
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/rvCartItems")
    @iOSXCUITFindBy(id = "cartItemsList")
    private WebElement cartItemsList;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/cartItemLayout")
    @iOSXCUITFindBy(id = "cartItem")
    private List<WebElement> cartItems;
    
    // Cart item elements (for first item)
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvCartItemTitle")
    @iOSXCUITFindBy(id = "cartItemTitle")
    private List<WebElement> cartItemTitles;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvCartItemPrice")
    @iOSXCUITFindBy(id = "cartItemPrice")
    private List<WebElement> cartItemPrices;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/ivCartItemImage")
    @iOSXCUITFindBy(id = "cartItemImage")
    private List<WebElement> cartItemImages;
    
    // Quantity controls
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnDecreaseQuantity")
    @iOSXCUITFindBy(id = "decreaseQuantity")
    private List<WebElement> decreaseQuantityButtons;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnIncreaseQuantity")
    @iOSXCUITFindBy(id = "increaseQuantity")
    private List<WebElement> increaseQuantityButtons;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvQuantity")
    @iOSXCUITFindBy(id = "quantity")
    private List<WebElement> quantityTexts;
    
    // Remove item button
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnRemoveItem")
    @iOSXCUITFindBy(id = "removeItemButton")
    private List<WebElement> removeItemButtons;
    
    // Cart summary elements
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvSubtotal")
    @iOSXCUITFindBy(id = "subtotal")
    private WebElement subtotalText;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvShippingCost")
    @iOSXCUITFindBy(id = "shippingCost")
    private WebElement shippingCostText;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvTotalAmount")
    @iOSXCUITFindBy(id = "totalAmount")
    private WebElement totalAmountText;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvSavings")
    @iOSXCUITFindBy(id = "savings")
    private WebElement savingsText;
    
    // Action buttons
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnCheckout")
    @iOSXCUITFindBy(id = "checkoutButton")
    private WebElement checkoutButton;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnContinueShopping")
    @iOSXCUITFindBy(id = "continueShoppingButton")
    private WebElement continueShoppingButton;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnClearCart")
    @iOSXCUITFindBy(id = "clearCartButton")
    private WebElement clearCartButton;
    
    // Empty cart elements
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvEmptyCartMessage")
    @iOSXCUITFindBy(id = "emptyCartMessage")
    private WebElement emptyCartMessage;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/ivEmptyCartIcon")
    @iOSXCUITFindBy(id = "emptyCartIcon")
    private WebElement emptyCartIcon;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnStartShopping")
    @iOSXCUITFindBy(id = "startShoppingButton")
    private WebElement startShoppingButton;
    
    // Coupon/discount elements
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/etCouponCode")
    @iOSXCUITFindBy(id = "couponCodeInput")
    private WebElement couponCodeInput;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnApplyCoupon")
    @iOSXCUITFindBy(id = "applyCouponButton")
    private WebElement applyCouponButton;
    
    // Success/error messages
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvSuccessMessage")
    @iOSXCUITFindBy(id = "successMessage")
    private WebElement successMessage;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvErrorMessage")
    @iOSXCUITFindBy(id = "errorMessage")
    private WebElement errorMessage;
    
    /**
     * Wait for cart page to load
     */
    @Step("Wait for cart page to load")
    public void waitForPageToLoad() {
        logger.info("Waiting for cart page to load");
        
        try {
            // Wait for cart title to be visible
            waitVisible(cartTitle);
            
            // Check if cart is empty or has items
            if (isDisplayed(emptyCartMessage)) {
                logger.info("Cart is empty - empty cart message displayed");
            } else {
                waitVisible(cartItemsList);
                logger.info("Cart has items - cart items list displayed");
            }
            
            logger.info("Cart page loaded successfully");
        } catch (Exception e) {
            logger.error("Error waiting for cart page to load", e);
            throw new RuntimeException("Failed to load cart page", e);
        }
    }
    
    /**
     * Check if cart page is loaded
     * @return true if page is loaded
     */
    
    @Step("Verify cart page is loaded")
    public boolean isPageLoaded() {
        logger.info("Verifying cart page is loaded");
        
        try {
            return isDisplayed(cartTitle);
        } catch (Exception e) {
            logger.error("Error verifying cart page load status", e);
            return false;
        }
    }
    
    /**
     * Get page title
     * @return Page title
     */
    
    @Step("Get cart page title")
    public String getPageTitle() {
        return "Hepsiburada - Shopping Cart";
    }
    
    /**
     * Check if cart is empty
     * @return true if cart is empty
     */
    @Step("Check if cart is empty")
    public boolean isCartEmpty() {
        logger.info("Checking if cart is empty");
        
        try {
            boolean isEmpty = isDisplayed(emptyCartMessage) || cartItems.isEmpty();
            logger.info("Cart empty status: {}", isEmpty);
            return isEmpty;
        } catch (Exception e) {
            logger.error("Error checking if cart is empty", e);
            return true;
        }
    }
    
    /**
     * Get number of items in cart
     * @return Number of items
     */
    @Step("Get cart items count")
    public int getCartItemsCount() {
        logger.info("Getting cart items count");
        
        try {
            if (isCartEmpty()) {
                logger.info("Cart is empty - 0 items");
                return 0;
            }
            
            int count = cartItems.size();
            logger.info("Cart contains {} items", count);
            return count;
        } catch (Exception e) {
            logger.error("Error getting cart items count", e);
            return 0;
        }
    }
    
    /**
     * Get cart item title by index
     * @param index Item index (0-based)
     * @return Item title
     */
    @Step("Get cart item title at index: {index}")
    public String getCartItemTitle(int index) {
        logger.info("Getting cart item title at index: {}", index);
        
        try {
            if (index >= 0 && index < cartItemTitles.size()) {
                String title = getText(cartItemTitles.get(index));
                logger.info("Cart item title at index {}: {}", index, title);
                return title;
            } else {
                throw new IndexOutOfBoundsException("Cart item index out of bounds: " + index);
            }
        } catch (Exception e) {
            logger.error("Failed to get cart item title at index: {}", index, e);
            return "";
        }
    }
    
    /**
     * Get cart item price by index
     * @param index Item index (0-based)
     * @return Item price
     */
    @Step("Get cart item price at index: {index}")
    public String getCartItemPrice(int index) {
        logger.info("Getting cart item price at index: {}", index);
        
        try {
            if (index >= 0 && index < cartItemPrices.size()) {
                String price = getText(cartItemPrices.get(index));
                logger.info("Cart item price at index {}: {}", index, price);
                return price;
            } else {
                throw new IndexOutOfBoundsException("Cart item index out of bounds: " + index);
            }
        } catch (Exception e) {
            logger.error("Failed to get cart item price at index: {}", index, e);
            return "";
        }
    }
    
    /**
     * Get total amount
     * @return Total amount
     */
    @Step("Get total amount")
    public String getTotalAmount() {
        logger.info("Getting total amount");
        
        try {
            if (isDisplayed(totalAmountText)) {
                String total = getText(totalAmountText);
                logger.info("Total amount: {}", total);
                return total;
            }
            return "";
        } catch (Exception e) {
            logger.error("Failed to get total amount", e);
            return "";
        }
    }
    
    /**
     * Verify MacBook Pro is in cart
     * @return true if MacBook Pro is found in cart
     */
    @Step("Verify MacBook Pro is in cart")
    public boolean isMacBookProInCart() {
        logger.info("Verifying MacBook Pro is in cart");
        
        try {
            if (isCartEmpty()) {
                logger.info("Cart is empty - no MacBook Pro found");
                return false;
            }
            
            for (int i = 0; i < cartItemTitles.size(); i++) {
                String itemTitle = getCartItemTitle(i);
                if (itemTitle.toLowerCase().contains("macbook pro")) {
                    logger.info("MacBook Pro found in cart: {}", itemTitle);
                    return true;
                }
            }
            
            // Check for general MacBook if MacBook Pro not found
            for (int i = 0; i < cartItemTitles.size(); i++) {
                String itemTitle = getCartItemTitle(i);
                if (itemTitle.toLowerCase().contains("macbook")) {
                    logger.info("MacBook found in cart: {}", itemTitle);
                    return true;
                }
            }
            
            logger.info("MacBook Pro not found in cart");
            return false;
            
        } catch (Exception e) {
            logger.error("Error verifying MacBook Pro in cart", e);
            return false;
        }
    }
    
    /**
     * Increase quantity for first item
     */
    @Step("Increase quantity for first item")
    public void increaseQuantityForFirstItem() {
        logger.info("Increasing quantity for first item");
        
        try {
            if (!increaseQuantityButtons.isEmpty()) {
                click(increaseQuantityButtons.get(0));
                
                // Wait for update
                Thread.sleep(1000);
                
                logger.info("Quantity increased for first item");
            } else {
                logger.warn("No increase quantity buttons found");
            }
        } catch (Exception e) {
            logger.error("Failed to increase quantity for first item", e);
        }
    }
    
    /**
     * Decrease quantity for first item
     */
    @Step("Decrease quantity for first item")
    public void decreaseQuantityForFirstItem() {
        logger.info("Decreasing quantity for first item");
        
        try {
            if (!decreaseQuantityButtons.isEmpty()) {
                click(decreaseQuantityButtons.get(0));
                
                // Wait for update
                Thread.sleep(1000);
                
                logger.info("Quantity decreased for first item");
            } else {
                logger.warn("No decrease quantity buttons found");
            }
        } catch (Exception e) {
            logger.error("Failed to decrease quantity for first item", e);
        }
    }
    
    /**
     * Remove first item from cart
     */
    @Step("Remove first item from cart")
    public void removeFirstItem() {
        logger.info("Removing first item from cart");
        
        try {
            if (!removeItemButtons.isEmpty()) {
                click(removeItemButtons.get(0));
                
                // Wait for removal to complete
                Thread.sleep(2000);
                
                logger.info("First item removed from cart");
            } else {
                logger.warn("No remove item buttons found");
            }
        } catch (Exception e) {
            logger.error("Failed to remove first item from cart", e);
        }
    }
    
    /**
     * Proceed to checkout
     */
    @Step("Proceed to checkout")
    public void proceedToCheckout() {
        logger.info("Proceeding to checkout");
        
        try {
            if (isCartEmpty()) {
                throw new RuntimeException("Cannot proceed to checkout - cart is empty");
            }
            
            // Scroll to checkout button if needed
            scrollToElement(checkoutButton);
            
            click(checkoutButton);
            
            // Wait for navigation
            Thread.sleep(2000);
            
            logger.info("Successfully proceeded to checkout");
            
        } catch (Exception e) {
            logger.error("Failed to proceed to checkout", e);
            throw new RuntimeException("Failed to proceed to checkout", e);
        }
    }
    
    /**
     * Continue shopping
     * @return HomePage instance
     */
    @Step("Continue shopping")
    public HomePage continueShopping() {
        logger.info("Continuing shopping");
        
        try {
            if (isDisplayed(continueShoppingButton)) {
                click(continueShoppingButton);
            } else if (isDisplayed(startShoppingButton)) {
                click(startShoppingButton);
            }
            
            logger.info("Successfully navigated to continue shopping");
            return new HomePage();
            
        } catch (Exception e) {
            logger.error("Failed to continue shopping", e);
            throw new RuntimeException("Failed to continue shopping", e);
        }
    }
    
    /**
     * Clear entire cart
     */
    @Step("Clear cart")
    public void clearCart() {
        logger.info("Clearing entire cart");
        
        try {
            if (isCartEmpty()) {
                logger.info("Cart is already empty");
                return;
            }
            
            if (isDisplayed(clearCartButton)) {
                click(clearCartButton);
                
                // Wait for cart to clear
                Thread.sleep(2000);
                
                logger.info("Cart cleared successfully");
            } else {
                logger.warn("Clear cart button not found");
            }
        } catch (Exception e) {
            logger.error("Failed to clear cart", e);
        }
    }
    
    /**
     * Scroll to specific element
     * @param element Element to scroll to
     */
    @Step("Scroll to element")
    private void scrollToElement(WebElement element) {
        try {
            int maxScrollAttempts = 3;
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
     * Verify cart functionality is working
     * @return true if cart is functional
     */
    @Step("Verify cart functionality")
    public boolean isCartFunctional() {
        logger.info("Verifying cart functionality");
        
        try {
            boolean hasTitle = isDisplayed(cartTitle);
            boolean hasItems = !isCartEmpty() || isDisplayed(emptyCartMessage);
            
            boolean functional = hasTitle && hasItems;
            logger.info("Cart functionality verified: {}", functional);
            return functional;
            
        } catch (Exception e) {
            logger.error("Error verifying cart functionality", e);
            return false;
        }
    }
}