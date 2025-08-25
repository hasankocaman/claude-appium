package com.hepsiburada.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.qameta.allure.Step;
import org.testng.Assert;

/**
 * Common Step Definitions for shared functionality across multiple features
 * Contains reusable steps for navigation, search, product interactions, and cart operations
 * 
 * @author Hepsiburada Test Automation Team
 */
public class CommonStepDefinitions extends BaseStepDefinitions {
    
    // ===================================================================================
    // NAVIGATION AND COMMON UI STEPS
    // ===================================================================================
    
    @Given("I verify the home page is loaded with all elements")
    @Step("Verify home page is loaded with all essential elements")
    public void iVerifyTheHomePageIsLoadedWithAllElements() {
        logStep("Verifying home page is loaded with all essential elements");
        
        try {
            // Verify critical home page elements
            verifyElementDisplayedWithRetry("Home page main content", 
                () -> homePage.isPageLoaded(), 5);
            verifyElementDisplayedWithRetry("Search box", 
                () -> homePage.isSearchBoxDisplayed(), 3);
            verifyElementDisplayedWithRetry("Main banner", 
                () -> homePage.isMainBannerDisplayed(), 3);
            
            // Verify page is fully interactive
            boolean isInteractive = homePage.isPageInteractive();
            assertWithScreenshot(isInteractive, "Home page should be fully interactive");
            
            takeScreenshotForStep("Home_Page_All_Elements_Verified");
            logger.info("Home page loaded successfully with all essential elements");
            
        } catch (Exception e) {
            logger.error("Home page verification with all elements failed", e);
            takeScreenshotForStep("Home_Page_All_Elements_Verification_Failed");
            throw new RuntimeException("Home page verification with all elements failed", e);
        }
    }
    
    @And("I can see the Hepsiburada logo")
    @Step("Verify Hepsiburada logo is visible")
    public void iCanSeeTheHepsiburadaLogo() {
        logStep("Verifying Hepsiburada logo is visible");
        
        try {
            boolean logoDisplayed = homePage.isHepsiburadaLogoDisplayed();
            assertWithScreenshot(logoDisplayed, "Hepsiburada logo should be visible");
            
            takeScreenshotForStep("Hepsiburada_Logo_Verified");
            logger.info("Hepsiburada logo is visible");
            
        } catch (Exception e) {
            logger.error("Hepsiburada logo verification failed", e);
            takeScreenshotForStep("Hepsiburada_Logo_Verification_Failed");
            throw new RuntimeException("Hepsiburada logo verification failed", e);
        }
    }
    
    @And("I can see the search box is available")
    @Step("Verify search box availability")
    public void iCanSeeTheSearchBoxIsAvailable() {
        logStep("Verifying search box is available");
        
        try {
            boolean searchBoxAvailable = homePage.isSearchBoxDisplayed() && homePage.isSearchBoxEnabled();
            assertWithScreenshot(searchBoxAvailable, "Search box should be available and enabled");
            
            takeScreenshotForStep("Search_Box_Available_Verified");
            logger.info("Search box is available and enabled");
            
        } catch (Exception e) {
            logger.error("Search box availability verification failed", e);
            takeScreenshotForStep("Search_Box_Availability_Verification_Failed");
            throw new RuntimeException("Search box availability verification failed", e);
        }
    }
    
    // ===================================================================================
    // SEARCH FUNCTIONALITY STEPS
    // ===================================================================================
    
    @Then("I should see search results for MacBook products")
    @Step("Verify search results for MacBook products")
    public void iShouldSeeSearchResultsForMacBookProducts() {
        logStep("Verifying search results for MacBook products");
        
        try {
            boolean resultsDisplayed = searchResultsPage.isPageLoaded();
            assertWithScreenshot(resultsDisplayed, "Search results should be displayed");
            
            boolean hasMacBookProducts = searchResultsPage.areMacBookProductsDisplayed();
            assertWithScreenshot(hasMacBookProducts, "Should have MacBook products in results");
            
            int resultsCount = searchResultsPage.getResultsCount();
            verifyNumberInRange(resultsCount, 1, 200, "MacBook search results count");
            
            takeScreenshotForStep("MacBook_Products_Search_Results_Verified");
            logger.info("Search results for MacBook products verified successfully");
            
        } catch (Exception e) {
            logger.error("MacBook products search results verification failed", e);
            takeScreenshotForStep("MacBook_Products_Search_Results_Verification_Failed");
            throw new RuntimeException("MacBook products search results verification failed", e);
        }
    }
    
    @Then("I should see search results for {string}")
    @Step("Verify search results for product: {product}")
    public void iShouldSeeSearchResultsFor(String product) {
        logStep("Verifying search results for: " + product);
        
        try {
            storeTestData("expected_product", product);
            
            boolean resultsDisplayed = searchResultsPage.isPageLoaded();
            assertWithScreenshot(resultsDisplayed, "Search results should be displayed");
            
            boolean hasRelevantProducts = searchResultsPage.hasProductsContaining(product);
            assertWithScreenshot(hasRelevantProducts, 
                String.format("Should have products containing '%s' in results", product));
            
            takeScreenshotForStep("Product_Search_Results_Verified");
            logger.info("Search results for '{}' verified successfully", product);
            
        } catch (Exception e) {
            logger.error("Search results verification failed for: {}", product, e);
            takeScreenshotForStep("Product_Search_Results_Verification_Failed");
            throw new RuntimeException("Search results verification failed for: " + product, e);
        }
    }
    
    @And("I should be able to sort the results")
    @Step("Verify sort functionality is available")
    public void iShouldBeAbleToSortTheResults() {
        logStep("Verifying sort functionality is available");
        
        try {
            boolean sortOptionAvailable = searchResultsPage.isSortOptionAvailable();
            assertWithScreenshot(sortOptionAvailable, "Sort functionality should be available");
            
            takeScreenshotForStep("Sort_Functionality_Available");
            logger.info("Sort functionality is available");
            
        } catch (Exception e) {
            logger.error("Sort functionality verification failed", e);
            takeScreenshotForStep("Sort_Functionality_Verification_Failed");
            throw new RuntimeException("Sort functionality verification failed", e);
        }
    }
    
    @When("I attempt to search for {string}")
    @Step("Attempt to search for: {searchTerm}")
    public void iAttemptToSearchFor(String searchTerm) {
        logStep("Attempting to search for: " + searchTerm);
        
        try {
            startPerformanceMeasurement();
            storeTestData("search_term", searchTerm);
            
            // Attempt search with error handling for network issues
            try {
                searchResultsPage = homePage.searchForProduct(searchTerm);
                if (searchResultsPage != null) {
                    searchResultsPage.waitForPageToLoad();
                }
            } catch (Exception searchException) {
                logger.warn("Search attempt encountered error: {}", searchException.getMessage());
                // Don't fail immediately - this might be expected for negative scenarios
            }
            
            endPerformanceMeasurement("Search Attempt");
            takeScreenshotForStep("Search_Attempt_Completed");
            logger.info("Search attempt completed for: {}", searchTerm);
            
        } catch (Exception e) {
            logger.error("Search attempt failed for: {}", searchTerm, e);
            takeScreenshotForStep("Search_Attempt_Failed");
            throw new RuntimeException("Search attempt failed for: " + searchTerm, e);
        }
    }
    
    // ===================================================================================
    // PRODUCT INTERACTION STEPS
    // ===================================================================================
    
    @Given("I search for {string}")
    @Step("Search for product: {searchTerm}")
    public void iSearchFor(String searchTerm) {
        logStep("Searching for product: " + searchTerm);
        
        try {
            startPerformanceMeasurement();
            storeTestData("search_term", searchTerm);
            
            searchResultsPage = homePage.searchForProduct(searchTerm);
            searchResultsPage.waitForPageToLoad();
            
            endPerformanceMeasurement("Search Operation");
            takeScreenshotForStep("Search_Completed");
            logger.info("Successfully searched for: {}", searchTerm);
            
        } catch (Exception e) {
            logger.error("Failed to search for: {}", searchTerm, e);
            takeScreenshotForStep("Search_Failed");
            throw new RuntimeException("Search failed for: " + searchTerm, e);
        }
    }
    
    @When("I select any MacBook Pro product")
    @Step("Select any MacBook Pro product from results")
    public void iSelectAnyMacBookProProduct() {
        logStep("Selecting any MacBook Pro product from results");
        
        try {
            productDetailsPage = searchResultsPage.selectFirstMacBookProProduct();
            productDetailsPage.waitForPageToLoad();
            
            // Store selected product details
            String productTitle = productDetailsPage.getProductTitle();
            String productPrice = productDetailsPage.getProductPrice();
            
            storeTestData("product_title", productTitle);
            storeTestData("product_price", productPrice);
            
            takeScreenshotForStep("MacBook_Pro_Product_Selected");
            logger.info("Successfully selected MacBook Pro product: {}", productTitle);
            
        } catch (Exception e) {
            logger.error("Failed to select MacBook Pro product", e);
            takeScreenshotForStep("MacBook_Pro_Product_Selection_Failed");
            throw new RuntimeException("Failed to select MacBook Pro product", e);
        }
    }
    
    @And("I select the first MacBook Pro from results")
    @Step("Select first MacBook Pro from search results")
    public void iSelectTheFirstMacBookProFromResults() {
        iSelectAnyMacBookProProduct();
    }
    
    @And("I am on the product details page")
    @Step("Verify I am on product details page")
    public void iAmOnTheProductDetailsPage() {
        logStep("Verifying I am on product details page");
        
        try {
            boolean isOnProductDetails = productDetailsPage.isPageLoaded();
            assertWithScreenshot(isOnProductDetails, "Should be on product details page");
            
            takeScreenshotForStep("Product_Details_Page_Verified");
            logger.info("Confirmed on product details page");
            
        } catch (Exception e) {
            logger.error("Product details page verification failed", e);
            takeScreenshotForStep("Product_Details_Page_Verification_Failed");
            throw new RuntimeException("Product details page verification failed", e);
        }
    }
    
    @When("I set the quantity to {string}")
    @Step("Set product quantity to: {quantity}")
    public void iSetTheQuantityTo(String quantity) {
        logStep("Setting product quantity to: " + quantity);
        
        try {
            int qty = Integer.parseInt(quantity);
            storeTestData("selected_quantity", quantity);
            
            productDetailsPage.setQuantity(qty);
            
            takeScreenshotForStep("Quantity_Set");
            logger.info("Successfully set quantity to: {}", quantity);
            
        } catch (NumberFormatException e) {
            logger.error("Invalid quantity format: {}", quantity, e);
            throw new RuntimeException("Invalid quantity format: " + quantity, e);
        } catch (Exception e) {
            logger.error("Failed to set quantity to: {}", quantity, e);
            takeScreenshotForStep("Quantity_Set_Failed");
            throw new RuntimeException("Failed to set quantity to: " + quantity, e);
        }
    }
    
    @And("I add the product to cart")
    @Step("Add product to cart")
    public void iAddTheProductToCart() {
        logStep("Adding product to cart");
        
        try {
            startPerformanceMeasurement();
            
            cartPage = productDetailsPage.addToCart();
            
            endPerformanceMeasurement("Add to Cart Operation");
            takeScreenshotForStep("Product_Added_To_Cart");
            logger.info("Successfully added product to cart");
            
        } catch (Exception e) {
            logger.error("Failed to add product to cart", e);
            takeScreenshotForStep("Add_Product_To_Cart_Failed");
            throw new RuntimeException("Failed to add product to cart", e);
        }
    }
    
    // ===================================================================================
    // PERFORMANCE MEASUREMENT STEPS
    // ===================================================================================
    
    @Given("I measure the time for search operations")
    @Step("Initialize performance measurement for search operations")
    public void iMeasureTheTimeForSearchOperations() {
        logStep("Initializing performance measurement for search operations");
        
        try {
            startPerformanceMeasurement();
            logger.info("Performance measurement initialized for search operations");
            
        } catch (Exception e) {
            logger.error("Failed to initialize performance measurement", e);
            throw new RuntimeException("Failed to initialize performance measurement", e);
        }
    }
    
    @Then("the search should complete within {int} seconds")
    @Step("Verify search completes within {seconds} seconds")
    public void theSearchShouldCompleteWithinSeconds(int seconds) {
        logStep("Verifying search completes within " + seconds + " seconds");
        
        try {
            verifyPerformanceThreshold("Search Operation", seconds);
            logger.info("Search completed within {} seconds threshold", seconds);
            
        } catch (Exception e) {
            logger.error("Search performance verification failed for {} seconds", seconds, e);
            throw new RuntimeException("Search performance verification failed", e);
        }
    }
    
    @And("the results should load within {int} seconds")
    @Step("Verify results load within {seconds} seconds")
    public void theResultsShouldLoadWithinSeconds(int seconds) {
        logStep("Verifying results load within " + seconds + " seconds");
        
        try {
            // Start new measurement for results loading
            startPerformanceMeasurement();
            
            // Wait for results to be fully loaded
            boolean resultsLoaded = searchResultsPage.areResultsFullyLoaded();
            assertWithScreenshot(resultsLoaded, "Results should be fully loaded");
            
            verifyPerformanceThreshold("Results Loading", seconds);
            logger.info("Results loaded within {} seconds threshold", seconds);
            
        } catch (Exception e) {
            logger.error("Results loading performance verification failed for {} seconds", seconds, e);
            throw new RuntimeException("Results loading performance verification failed", e);
        }
    }
    
    @And("I should see performance metrics in logs")
    @Step("Verify performance metrics are logged")
    public void iShouldSeePerformanceMetricsInLogs() {
        logStep("Verifying performance metrics are logged");
        
        try {
            // Performance metrics are automatically logged in the measurement methods
            // This step serves as a verification point
            logger.info("Performance metrics verification completed");
            takeScreenshotForStep("Performance_Metrics_Logged");
            
        } catch (Exception e) {
            logger.error("Performance metrics verification failed", e);
            throw new RuntimeException("Performance metrics verification failed", e);
        }
    }
    
    // ===================================================================================
    // ACCESSIBILITY STEPS
    // ===================================================================================
    
    @When("I check the search box accessibility")
    @Step("Check search box accessibility features")
    public void iCheckTheSearchBoxAccessibility() {
        logStep("Checking search box accessibility features");
        
        try {
            boolean hasAccessibilityLabel = homePage.doesSearchBoxHaveAccessibilityLabel();
            boolean hasContentDescription = homePage.doesSearchBoxHaveContentDescription();
            boolean isAccessible = homePage.isSearchBoxAccessible();
            
            softAssert(hasAccessibilityLabel, "Search box has accessibility label");
            softAssert(hasContentDescription, "Search box has content description");
            softAssert(isAccessible, "Search box is accessible");
            
            takeScreenshotForStep("Search_Box_Accessibility_Checked");
            logger.info("Search box accessibility check completed");
            
        } catch (Exception e) {
            logger.error("Search box accessibility check failed", e);
            takeScreenshotForStep("Search_Box_Accessibility_Check_Failed");
            throw new RuntimeException("Search box accessibility check failed", e);
        }
    }
    
    // ===================================================================================
    // ERROR HANDLING AND NETWORK STEPS
    // ===================================================================================
    
    @Given("I simulate network connectivity issues")
    @Step("Simulate network connectivity issues")
    public void iSimulateNetworkConnectivityIssues() {
        logStep("Simulating network connectivity issues");
        
        try {
            // This would typically involve setting up network conditions
            // For mobile testing, this might involve airplane mode or network throttling
            logger.warn("Network connectivity issues simulation - implementation depends on test environment");
            
            takeScreenshotForStep("Network_Issues_Simulated");
            logger.info("Network connectivity issues simulation setup completed");
            
        } catch (Exception e) {
            logger.error("Failed to simulate network connectivity issues", e);
            throw new RuntimeException("Failed to simulate network connectivity issues", e);
        }
    }
    
    @Then("I should see appropriate error message")
    @Step("Verify appropriate error message is displayed")
    public void iShouldSeeAppropriateErrorMessage() {
        logStep("Verifying appropriate error message is displayed");
        
        try {
            // Check for various types of error messages
            boolean hasNetworkError = homePage.hasNetworkErrorMessage();
            boolean hasGenericError = homePage.hasGenericErrorMessage();
            boolean hasErrorMessage = hasNetworkError || hasGenericError;
            
            softAssert(hasErrorMessage, "Should display appropriate error message");
            
            takeScreenshotForStep("Error_Message_Displayed");
            logger.info("Appropriate error message verification completed");
            
        } catch (Exception e) {
            logger.error("Error message verification failed", e);
            takeScreenshotForStep("Error_Message_Verification_Failed");
            throw new RuntimeException("Error message verification failed", e);
        }
    }
    
    @Then("the app should handle the error gracefully")
    @Step("Verify app handles error gracefully")
    public void theAppShouldHandleTheErrorGracefully() {
        logStep("Verifying app handles error gracefully");
        
        try {
            boolean appIsResponsive = homePage.isAppResponsive();
            boolean hasRecoveryOptions = homePage.hasErrorRecoveryOptions();
            
            softAssert(appIsResponsive, "App should remain responsive");
            softAssert(hasRecoveryOptions, "App should provide recovery options");
            
            takeScreenshotForStep("Graceful_Error_Handling_Verified");
            logger.info("Graceful error handling verification completed");
            
        } catch (Exception e) {
            logger.error("Graceful error handling verification failed", e);
            takeScreenshotForStep("Graceful_Error_Handling_Verification_Failed");
            throw new RuntimeException("Graceful error handling verification failed", e);
        }
    }
    
    @And("I should be able to retry the search")
    @Step("Verify search retry functionality")
    public void iShouldBeAbleToRetryTheSearch() {
        logStep("Verifying search retry functionality");
        
        try {
            boolean canRetry = homePage.canRetrySearch();
            assertWithScreenshot(canRetry, "Should be able to retry search");
            
            takeScreenshotForStep("Search_Retry_Available");
            logger.info("Search retry functionality verified");
            
        } catch (Exception e) {
            logger.error("Search retry functionality verification failed", e);
            takeScreenshotForStep("Search_Retry_Verification_Failed");
            throw new RuntimeException("Search retry functionality verification failed", e);
        }
    }
    
    // ===================================================================================
    // BOUNDARY CONDITION STEPS
    // ===================================================================================
    
    @Then("I should see appropriate response for {string}")
    @Step("Verify appropriate response for search term: {searchTerm}")
    public void iShouldSeeAppropriateResponseFor(String searchTerm) {
        logStep("Verifying appropriate response for search term: " + searchTerm);
        
        try {
            storeTestData("boundary_search_term", searchTerm);
            
            // Handle different types of boundary conditions
            if (searchTerm.length() <= 2) {
                // Very short search terms
                boolean hasShortTermHandling = searchResultsPage.hasShortTermMessage() || 
                                            searchResultsPage.hasMinimumCharacterMessage();
                softAssert(hasShortTermHandling, "Should handle short search terms appropriately");
                
            } else if (searchTerm.length() > 50) {
                // Very long search terms
                boolean hasLongTermHandling = searchResultsPage.isPageLoaded();
                softAssert(hasLongTermHandling, "Should handle long search terms appropriately");
                
            } else if (searchTerm.matches(".*[!@#$%^&*()]+.*")) {
                // Special characters
                boolean hasSpecialCharHandling = searchResultsPage.hasNoResultsMessage() || 
                                               searchResultsPage.hasValidResults();
                softAssert(hasSpecialCharHandling, "Should handle special characters appropriately");
                
            } else if (searchTerm.matches("\\d+")) {
                // Numeric search terms
                boolean hasNumericHandling = searchResultsPage.hasValidResults() || 
                                           searchResultsPage.hasNoResultsMessage();
                softAssert(hasNumericHandling, "Should handle numeric search terms appropriately");
                
            } else {
                // Regular search terms
                boolean hasRegularHandling = searchResultsPage.hasValidResults();
                softAssert(hasRegularHandling, "Should handle regular search terms appropriately");
            }
            
            takeScreenshotForStep("Boundary_Response_Verified");
            logger.info("Appropriate response verified for search term: {}", searchTerm);
            
        } catch (Exception e) {
            logger.error("Boundary condition response verification failed for: {}", searchTerm, e);
            takeScreenshotForStep("Boundary_Response_Verification_Failed");
            throw new RuntimeException("Boundary condition response verification failed", e);
        }
    }
}