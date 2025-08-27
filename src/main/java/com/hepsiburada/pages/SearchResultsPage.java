package com.hepsiburada.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Search Results Page class representing product search results
 * Contains elements and methods for search results interaction
 * 
 * @author Hepsiburada Test Automation Team
 */
public class SearchResultsPage extends BasePage {
    
    // Search results elements
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/rvSearchResults")
    @iOSXCUITFindBy(id = "searchResultsList")
    private WebElement searchResultsList;
    
    @AndroidFindBy(className = "android.widget.TextView")
    @iOSXCUITFindBy(className = "XCUIElementTypeStaticText")
    private List<WebElement> productTitles;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvProductTitle")
    @iOSXCUITFindBy(id = "productTitle")
    private List<WebElement> productTitleElements;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvProductPrice")
    @iOSXCUITFindBy(id = "productPrice")
    private List<WebElement> productPriceElements;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/ivProductImage")
    @iOSXCUITFindBy(id = "productImage")
    private List<WebElement> productImageElements;
    
    // Filter and sort elements
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnFilter")
    @iOSXCUITFindBy(id = "filterButton")
    private WebElement filterButton;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/btnSort")
    @iOSXCUITFindBy(id = "sortButton")
    private WebElement sortButton;
    
    // Sort options
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='En Yüksek Fiyat']")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[@name='En Yüksek Fiyat']")
    private WebElement sortByHighestPrice;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='En Düşük Fiyat']")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[@name='En Düşük Fiyat']")
    private WebElement sortByLowestPrice;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='En Çok Satanlar']")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[@name='En Çok Satanlar']")
    private WebElement sortByBestSellers;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[@text='En Yeni']")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[@name='En Yeni']")
    private WebElement sortByNewest;
    
    // Search query and results info
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvSearchQuery")
    @iOSXCUITFindBy(id = "searchQuery")
    private WebElement searchQueryText;
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvResultsCount")
    @iOSXCUITFindBy(id = "resultsCount")
    private WebElement resultsCountText;
    
    // No results elements
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvNoResults")
    @iOSXCUITFindBy(id = "noResultsMessage")
    private WebElement noResultsMessage;
    
    // Loading indicator
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/progressBar")
    @iOSXCUITFindBy(id = "loadingIndicator")
    private WebElement loadingIndicator;
    
    // MacBook specific elements (for the test scenario)
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'MacBook')]")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[contains(@name,'MacBook')]")
    private List<WebElement> macBookProducts;
    
    @AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'MacBook Pro')]")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[contains(@name,'MacBook Pro')]")
    private List<WebElement> macBookProProducts;
    
    /**
     * Wait for search results page to load
     */
    @Step("Wait for search results page to load")
    public void waitForPageToLoad() {
        logger.info("Waiting for search results page to load");
        
        try {
            // Wait for loading to finish if present
            if (isDisplayed(loadingIndicator)) {
                waitInvisible(loadingIndicator);
            }
            
            // Wait for either results list or no results message
            if (isDisplayed(searchResultsList)) {
                waitVisible(searchResultsList);
                logger.info("Search results loaded successfully");
            } else if (isDisplayed(noResultsMessage)) {
                waitVisible(noResultsMessage);
                logger.info("No results message displayed");
            }
            
        } catch (Exception e) {
            logger.error("Error waiting for search results page to load", e);
            throw new RuntimeException("Failed to load search results page", e);
        }
    }
    
    /**
     * Check if search results page is loaded
     * @return true if page is loaded
     */
    
    @Step("Verify search results page is loaded")
    public boolean isPageLoaded() {
        logger.info("Verifying search results page is loaded");
        
        try {
            return isDisplayed(searchResultsList) || isDisplayed(noResultsMessage);
        } catch (Exception e) {
            logger.error("Error verifying search results page load status", e);
            return false;
        }
    }
    
    /**
     * Get page title
     * @return Page title
     */
    
    @Step("Get search results page title")
    public String getPageTitle() {
        return "Hepsiburada - Search Results";
    }
    
    /**
     * Get number of search results
     * @return Number of results
     */
    @Step("Get number of search results")
    public int getResultsCount() {
        logger.info("Getting search results count");
        
        try {
            if (isDisplayed(resultsCountText)) {
                String countText = getText(resultsCountText);
                // Extract number from text (assuming format like "1-24 of 1000 results")
                String[] parts = countText.split(" ");
                for (String part : parts) {
                    if (part.matches("\\d+")) {
                        int count = Integer.parseInt(part);
                        logger.info("Found {} search results", count);
                        return count;
                    }
                }
            }
            
            // Fallback: count visible product elements
            int visibleProducts = productTitleElements.size();
            logger.info("Counted {} visible products", visibleProducts);
            return visibleProducts;
            
        } catch (Exception e) {
            logger.error("Error getting results count", e);
            return 0;
        }
    }
    
    /**
     * Sort results by highest price
     * @return Current SearchResultsPage instance for chaining
     */
    @Step("Sort results by highest price")
    public SearchResultsPage sortByHighestPrice() {
        logger.info("Sorting results by highest price");
        
        try {
            click(sortButton);
            click(sortByHighestPrice);
            
            // Wait for results to refresh
            Thread.sleep(2000);
            
            logger.info("Successfully sorted by highest price");
            return this;
        } catch (Exception e) {
            logger.error("Failed to sort by highest price", e);
            throw new RuntimeException("Failed to sort by highest price", e);
        }
    }
    
    /**
     * Sort results by lowest price
     * @return Current SearchResultsPage instance for chaining
     */
    @Step("Sort results by lowest price")
    public SearchResultsPage sortByLowestPrice() {
        logger.info("Sorting results by lowest price");
        
        try {
            click(sortButton);
            click(sortByLowestPrice);
            
            // Wait for results to refresh
            Thread.sleep(2000);
            
            logger.info("Successfully sorted by lowest price");
            return this;
        } catch (Exception e) {
            logger.error("Failed to sort by lowest price", e);
            throw new RuntimeException("Failed to sort by lowest price", e);
        }
    }
    
    /**
     * Get the most expensive MacBook Pro from search results
     * @return ProductDetailsPage of the most expensive MacBook Pro
     */
    @Step("Select most expensive MacBook Pro")
    public ProductDetailsPage selectMostExpensiveMacBookPro() {
        logger.info("Selecting most expensive MacBook Pro");
        
        try {
            // First, sort by highest price to get most expensive items first
            sortByHighestPrice();
            
            // Wait for sort to complete
            Thread.sleep(3000);
            
            // Find MacBook Pro products
            if (macBookProProducts.isEmpty()) {
                logger.warn("No MacBook Pro products found, looking for general MacBook products");
                if (macBookProducts.isEmpty()) {
                    throw new RuntimeException("No MacBook products found in search results");
                } else {
                    // Click on first MacBook product (should be most expensive after sorting)
                    click(macBookProducts.get(0));
                }
            } else {
                // Click on first MacBook Pro product (should be most expensive after sorting)
                click(macBookProProducts.get(0));
            }
            
            logger.info("Successfully clicked on most expensive MacBook Pro");
            return new ProductDetailsPage();
            
        } catch (Exception e) {
            logger.error("Failed to select most expensive MacBook Pro", e);
            throw new RuntimeException("Failed to select most expensive MacBook Pro", e);
        }
    }
    
    /**
     * Click on a specific product by index
     * @param index Product index (0-based)
     * @return ProductDetailsPage instance
     */
    @Step("Select product at index: {index}")
    public ProductDetailsPage selectProductByIndex(int index) {
        logger.info("Selecting product at index: {}", index);
        
        try {
            if (index >= 0 && index < productTitleElements.size()) {
                click(productTitleElements.get(index));
                logger.info("Successfully selected product at index: {}", index);
                return new ProductDetailsPage();
            } else {
                throw new IndexOutOfBoundsException("Product index out of bounds: " + index);
            }
        } catch (Exception e) {
            logger.error("Failed to select product at index: {}", index, e);
            throw new RuntimeException("Failed to select product at index: " + index, e);
        }
    }
    
    /**
     * Get product title by index
     * @param index Product index (0-based)
     * @return Product title
     */
    @Step("Get product title at index: {index}")
    public String getProductTitleByIndex(int index) {
        logger.info("Getting product title at index: {}", index);
        
        try {
            if (index >= 0 && index < productTitleElements.size()) {
                String title = getText(productTitleElements.get(index));
                logger.info("Product title at index {}: {}", index, title);
                return title;
            } else {
                throw new IndexOutOfBoundsException("Product index out of bounds: " + index);
            }
        } catch (Exception e) {
            logger.error("Failed to get product title at index: {}", index, e);
            return "";
        }
    }
    
    /**
     * Get product price by index
     * @param index Product index (0-based)
     * @return Product price
     */
    @Step("Get product price at index: {index}")
    public String getProductPriceByIndex(int index) {
        logger.info("Getting product price at index: {}", index);
        
        try {
            if (index >= 0 && index < productPriceElements.size()) {
                String price = getText(productPriceElements.get(index));
                logger.info("Product price at index {}: {}", index, price);
                return price;
            } else {
                throw new IndexOutOfBoundsException("Product index out of bounds: " + index);
            }
        } catch (Exception e) {
            logger.error("Failed to get product price at index: {}", index, e);
            return "";
        }
    }
    
    /**
     * Check if any MacBook products are displayed
     * @return true if MacBook products found
     */
    @Step("Check if MacBook products are displayed")
    public boolean areMacBookProductsDisplayed() {
        boolean hasProducts = !macBookProducts.isEmpty() || !macBookProProducts.isEmpty();
        logger.info("MacBook products displayed: {}", hasProducts);
        return hasProducts;
    }
    
    /**
     * Scroll to load more products
     */
    @Step("Scroll to load more products")
    public void scrollToLoadMoreProducts() {
        logger.info("Scrolling to load more products");
        
        try {
            int initialCount = productTitleElements.size();
            scrollDown();
            
            // Wait for new products to load
            Thread.sleep(2000);
            
            int newCount = productTitleElements.size();
            logger.info("Product count changed from {} to {}", initialCount, newCount);
            
        } catch (Exception e) {
            logger.error("Error scrolling to load more products", e);
        }
    }
}