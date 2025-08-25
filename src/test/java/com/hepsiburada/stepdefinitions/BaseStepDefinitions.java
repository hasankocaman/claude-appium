package com.hepsiburada.stepdefinitions;

import com.hepsiburada.drivers.DriverManager;
import com.hepsiburada.pages.*;
import com.hepsiburada.utils.ScreenshotUtils;
import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

/**
 * Base Step Definitions class providing common functionality for all step definition classes
 * Contains shared page objects, utility methods, and common verification steps
 * 
 * @author Hepsiburada Test Automation Team
 */
public class BaseStepDefinitions {
    
    protected final Logger logger = LogManager.getLogger(this.getClass());
    
    // Page Objects - shared across all step definition classes
    protected HomePage homePage;
    protected SearchResultsPage searchResultsPage;
    protected ProductDetailsPage productDetailsPage;
    protected CartPage cartPage;
    protected CategoriesPage categoriesPage;
    protected AccountPage accountPage;
    
    // Test data storage
    protected String searchTerm;
    protected String selectedProductTitle;
    protected String selectedProductPrice;
    protected int cartItemCount;
    protected long operationStartTime;
    
    /**
     * Initialize page objects
     */
    protected void initializePageObjects() {
        logger.info("Initializing page objects");
        
        try {
            homePage = new HomePage();
            searchResultsPage = new SearchResultsPage();
            productDetailsPage = new ProductDetailsPage();
            cartPage = new CartPage();
            categoriesPage = new CategoriesPage();
            accountPage = new AccountPage();
            
            logger.info("Page objects initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize page objects", e);
            throw new RuntimeException("Failed to initialize page objects", e);
        }
    }
    
    /**
     * Get current driver instance
     * @return AppiumDriver instance
     */
    protected AppiumDriver getDriver() {
        return DriverManager.getDriver();
    }
    
    /**
     * Take screenshot for current step
     * @param stepName Step name
     */
    @Step("Take screenshot for step: {stepName}")
    protected void takeScreenshotForStep(String stepName) {
        try {
            logger.debug("Taking screenshot for step: {}", stepName);
            String screenshotPath = ScreenshotUtils.takeScreenshotForStep(
                getCurrentTestName(), stepName);
            
            if (!screenshotPath.isEmpty()) {
                // Attach to Allure
                byte[] screenshot = ScreenshotUtils.takeScreenshot();
                Allure.addAttachment(stepName + " - Screenshot", "image/png", 
                    new java.io.ByteArrayInputStream(screenshot), "png");
            }
        } catch (Exception e) {
            logger.warn("Failed to take screenshot for step: {}", stepName, e);
        }
    }
    
    /**
     * Get current test name from thread or scenario context
     * @return Test name
     */
    protected String getCurrentTestName() {
        // This will be set by the hooks or can be retrieved from scenario context
        return Thread.currentThread().getName().replaceAll("[^a-zA-Z0-9._-]", "_");
    }
    
    /**
     * Wait for specified duration
     * @param seconds Duration in seconds
     */
    @Step("Wait for {seconds} seconds")
    protected void waitForSeconds(int seconds) {
        try {
            logger.debug("Waiting for {} seconds", seconds);
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            logger.warn("Wait was interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Assert with screenshot on failure
     * @param condition Condition to assert
     * @param message Failure message
     */
    protected void assertWithScreenshot(boolean condition, String message) {
        if (!condition) {
            logger.error("Assertion failed: {}", message);
            takeScreenshotForStep("ASSERTION_FAILURE");
            Assert.fail(message);
        }
    }
    
    /**
     * Soft assert with logging
     * @param condition Condition to check
     * @param message Message to log
     * @return true if condition is met
     */
    protected boolean softAssert(boolean condition, String message) {
        if (condition) {
            logger.info("✓ Verification passed: {}", message);
        } else {
            logger.warn("✗ Verification failed: {}", message);
        }
        return condition;
    }
    
    /**
     * Log step execution
     * @param stepDescription Step description
     */
    @Step("{stepDescription}")
    protected void logStep(String stepDescription) {
        logger.info("Executing step: {}", stepDescription);
        Allure.step(stepDescription);
    }
    
    /**
     * Log test data
     * @param key Data key
     * @param value Data value
     */
    protected void logTestData(String key, String value) {
        logger.info("Test Data - {}: {}", key, value);
        Allure.parameter(key, value);
    }
    
    /**
     * Start performance measurement
     */
    protected void startPerformanceMeasurement() {
        operationStartTime = System.currentTimeMillis();
        logger.debug("Started performance measurement at: {}", operationStartTime);
    }
    
    /**
     * End performance measurement and log duration
     * @param operationName Operation name
     * @return Duration in milliseconds
     */
    protected long endPerformanceMeasurement(String operationName) {
        long duration = System.currentTimeMillis() - operationStartTime;
        logger.info("Performance - {}: {} ms", operationName, duration);
        Allure.parameter(operationName + " Duration", duration + " ms");
        return duration;
    }
    
    /**
     * Verify performance meets threshold
     * @param operationName Operation name
     * @param thresholdSeconds Threshold in seconds
     */
    protected void verifyPerformanceThreshold(String operationName, int thresholdSeconds) {
        long duration = endPerformanceMeasurement(operationName);
        long thresholdMs = thresholdSeconds * 1000L;
        
        if (duration > thresholdMs) {
            logger.warn("Performance threshold exceeded for {}: {} ms > {} ms", 
                       operationName, duration, thresholdMs);
        } else {
            logger.info("Performance threshold met for {}: {} ms <= {} ms", 
                       operationName, duration, thresholdMs);
        }
        
        assertWithScreenshot(duration <= thresholdMs, 
            String.format("Performance threshold exceeded for %s: %d ms > %d ms", 
                         operationName, duration, thresholdMs));
    }
    
    /**
     * Store test data for later use
     * @param key Data key
     * @param value Data value
     */
    protected void storeTestData(String key, String value) {
        logger.debug("Storing test data - {}: {}", key, value);
        
        switch (key.toLowerCase()) {
            case "search_term":
                searchTerm = value;
                break;
            case "product_title":
                selectedProductTitle = value;
                break;
            case "product_price":
                selectedProductPrice = value;
                break;
            default:
                logger.debug("Unknown test data key: {}", key);
        }
        
        logTestData(key, value);
    }
    
    /**
     * Get stored test data
     * @param key Data key
     * @return Stored value
     */
    protected String getStoredTestData(String key) {
        String value = null;
        
        switch (key.toLowerCase()) {
            case "search_term":
                value = searchTerm;
                break;
            case "product_title":
                value = selectedProductTitle;
                break;
            case "product_price":
                value = selectedProductPrice;
                break;
            default:
                logger.debug("Unknown test data key: {}", key);
        }
        
        logger.debug("Retrieved test data - {}: {}", key, value);
        return value;
    }
    
    /**
     * Verify text contains expected substring (case insensitive)
     * @param actualText Actual text
     * @param expectedSubstring Expected substring
     * @param description Description for logging
     */
    protected void verifyTextContains(String actualText, String expectedSubstring, String description) {
        boolean contains = actualText != null && 
                          actualText.toLowerCase().contains(expectedSubstring.toLowerCase());
        
        logger.info("Verifying text contains - {}: '{}' contains '{}'? {}", 
                   description, actualText, expectedSubstring, contains);
        
        assertWithScreenshot(contains, 
            String.format("%s: Expected text '%s' to contain '%s'", 
                         description, actualText, expectedSubstring));
    }
    
    /**
     * Verify number is within expected range
     * @param actual Actual number
     * @param min Minimum expected value
     * @param max Maximum expected value
     * @param description Description for logging
     */
    protected void verifyNumberInRange(int actual, int min, int max, String description) {
        boolean inRange = actual >= min && actual <= max;
        
        logger.info("Verifying number in range - {}: {} is between {} and {}? {}", 
                   description, actual, min, max, inRange);
        
        assertWithScreenshot(inRange, 
            String.format("%s: Expected %d to be between %d and %d", 
                         description, actual, min, max));
    }
    
    /**
     * Verify element is displayed with retry
     * @param elementDescription Element description
     * @param checkFunction Function to check if element is displayed
     * @param maxRetries Maximum number of retries
     */
    protected void verifyElementDisplayedWithRetry(String elementDescription, 
                                                  java.util.function.BooleanSupplier checkFunction, 
                                                  int maxRetries) {
        boolean isDisplayed = false;
        int retryCount = 0;
        
        while (!isDisplayed && retryCount < maxRetries) {
            try {
                isDisplayed = checkFunction.getAsBoolean();
                if (!isDisplayed) {
                    retryCount++;
                    logger.debug("Element '{}' not displayed, retry {}/{}", 
                               elementDescription, retryCount, maxRetries);
                    waitForSeconds(1);
                }
            } catch (Exception e) {
                logger.debug("Exception checking element '{}', retry {}/{}: {}", 
                           elementDescription, retryCount, maxRetries, e.getMessage());
                retryCount++;
                waitForSeconds(1);
            }
        }
        
        logger.info("Element '{}' displayed after {} retries: {}", 
                   elementDescription, retryCount, isDisplayed);
        
        assertWithScreenshot(isDisplayed, 
            String.format("Element '%s' should be displayed after %d retries", 
                         elementDescription, maxRetries));
    }
    
    /**
     * Clean up test data after scenario
     */
    protected void cleanupTestData() {
        logger.debug("Cleaning up test data");
        
        searchTerm = null;
        selectedProductTitle = null;
        selectedProductPrice = null;
        cartItemCount = 0;
        operationStartTime = 0;
        
        logger.debug("Test data cleanup completed");
    }
}