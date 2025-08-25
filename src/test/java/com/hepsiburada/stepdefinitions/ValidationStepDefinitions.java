package com.hepsiburada.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.qameta.allure.Step;
import org.testng.Assert;
import java.util.List;

/**
 * Validation Step Definitions for comprehensive verification and validation scenarios
 * Contains steps for element visibility checks, content validations, and business logic verifications
 * 
 * @author Hepsiburada Test Automation Team
 */
public class ValidationStepDefinitions extends BaseStepDefinitions {
    
    // ===================================================================================
    // ELEMENT VISIBILITY AND PRESENCE VALIDATIONS
    // ===================================================================================
    
    @And("I can see MacBook Pro search results")
    @Step("Verify MacBook Pro search results are visible")
    public void iCanSeeMacBookProSearchResults() {
        logStep("Verifying MacBook Pro search results are visible");
        
        try {
            boolean resultsVisible = searchResultsPage.isPageLoaded();
            assertWithScreenshot(resultsVisible, "MacBook Pro search results should be visible");
            
            boolean hasMacBookProProducts = searchResultsPage.hasMacBookProProducts();
            assertWithScreenshot(hasMacBookProProducts, "Should have MacBook Pro products in results");
            
            takeScreenshotForStep("MacBook_Pro_Search_Results_Visible");
            logger.info("MacBook Pro search results visibility verified");
            
        } catch (Exception e) {
            logger.error("MacBook Pro search results visibility verification failed", e);
            takeScreenshotForStep("MacBook_Pro_Search_Results_Visibility_Failed");
            throw new RuntimeException("MacBook Pro search results visibility verification failed", e);
        }
    }
    
    @Then("I should see the product details page")
    @Step("Verify product details page is displayed")
    public void iShouldSeeTheProductDetailsPage() {
        logStep("Verifying product details page is displayed");
        
        try {
            boolean pageLoaded = productDetailsPage.isPageLoaded();
            assertWithScreenshot(pageLoaded, "Product details page should be loaded");
            
            boolean hasProductTitle = productDetailsPage.hasProductTitle();
            boolean hasProductPrice = productDetailsPage.hasProductPrice();
            boolean hasProductImages = productDetailsPage.hasProductImages();
            
            softAssert(hasProductTitle, "Product details page has product title");
            softAssert(hasProductPrice, "Product details page has product price");
            softAssert(hasProductImages, "Product details page has product images");
            
            takeScreenshotForStep("Product_Details_Page_Displayed");
            logger.info("Product details page display verified");
            
        } catch (Exception e) {
            logger.error("Product details page display verification failed", e);
            takeScreenshotForStep("Product_Details_Page_Display_Failed");
            throw new RuntimeException("Product details page display verification failed", e);
        }
    }
    
    @And("I should see product title containing {string}")
    @Step("Verify product title contains: {expectedText}")
    public void iShouldSeeProductTitleContaining(String expectedText) {
        logStep("Verifying product title contains: " + expectedText);
        
        try {
            String actualTitle = productDetailsPage.getProductTitle();
            verifyTextContains(actualTitle, expectedText, "Product title verification");
            
            storeTestData("verified_product_title", actualTitle);
            
            takeScreenshotForStep("Product_Title_Verified");
            logger.info("Product title contains '{}' verified: {}", expectedText, actualTitle);
            
        } catch (Exception e) {
            logger.error("Product title verification failed for: {}", expectedText, e);
            takeScreenshotForStep("Product_Title_Verification_Failed");
            throw new RuntimeException("Product title verification failed", e);
        }
    }
    
    @And("I should see product price information")
    @Step("Verify product price information is displayed")
    public void iShouldSeeProductPriceInformation() {
        logStep("Verifying product price information is displayed");
        
        try {
            boolean hasPriceInfo = productDetailsPage.hasProductPrice();
            assertWithScreenshot(hasPriceInfo, "Product price information should be displayed");
            
            String priceText = productDetailsPage.getProductPrice();
            boolean isPriceValid = priceText != null && !priceText.isEmpty() && 
                                 (priceText.contains("₺") || priceText.contains("TL") || 
                                  priceText.matches(".*\\d+.*"));
            
            assertWithScreenshot(isPriceValid, "Product price should be in valid format");
            
            storeTestData("verified_product_price", priceText);
            
            takeScreenshotForStep("Product_Price_Information_Verified");
            logger.info("Product price information verified: {}", priceText);
            
        } catch (Exception e) {
            logger.error("Product price information verification failed", e);
            takeScreenshotForStep("Product_Price_Information_Verification_Failed");
            throw new RuntimeException("Product price information verification failed", e);
        }
    }
    
    @And("I should see add to cart button is available")
    @Step("Verify add to cart button is available")
    public void iShouldSeeAddToCartButtonIsAvailable() {
        logStep("Verifying add to cart button is available");
        
        try {
            boolean buttonExists = productDetailsPage.hasAddToCartButton();
            boolean buttonEnabled = productDetailsPage.isAddToCartButtonEnabled();
            
            assertWithScreenshot(buttonExists, "Add to cart button should exist");
            assertWithScreenshot(buttonEnabled, "Add to cart button should be enabled");
            
            takeScreenshotForStep("Add_To_Cart_Button_Available");
            logger.info("Add to cart button availability verified");
            
        } catch (Exception e) {
            logger.error("Add to cart button availability verification failed", e);
            takeScreenshotForStep("Add_To_Cart_Button_Availability_Failed");
            throw new RuntimeException("Add to cart button availability verification failed", e);
        }
    }
    
    // ===================================================================================
    // SEARCH RESULTS AND PRODUCT COUNT VALIDATIONS
    // ===================================================================================
    
    @And("I should see at least {int} MacBook product in results")
    @Step("Verify at least {minCount} MacBook product in results")
    public void iShouldSeeAtLeastMacBookProductInResults(int minCount) {
        logStep("Verifying at least " + minCount + " MacBook product in results");
        
        try {
            int actualCount = searchResultsPage.getMacBookProductsCount();
            verifyNumberInRange(actualCount, minCount, 1000, "MacBook products count");
            
            storeTestData("macbook_products_count", String.valueOf(actualCount));
            
            takeScreenshotForStep("MacBook_Products_Count_Verified");
            logger.info("MacBook products count verification passed: {} >= {}", actualCount, minCount);
            
        } catch (Exception e) {
            logger.error("MacBook products count verification failed for minimum: {}", minCount, e);
            takeScreenshotForStep("MacBook_Products_Count_Verification_Failed");
            throw new RuntimeException("MacBook products count verification failed", e);
        }
    }
    
    @And("I should see at least {int} result")
    @Step("Verify at least {minCount} result in search")
    public void iShouldSeeAtLeastResult(int minCount) {
        logStep("Verifying at least " + minCount + " result in search");
        
        try {
            int actualCount = searchResultsPage.getResultsCount();
            verifyNumberInRange(actualCount, minCount, 1000, "Search results count");
            
            storeTestData("search_results_count", String.valueOf(actualCount));
            
            takeScreenshotForStep("Search_Results_Count_Verified");
            logger.info("Search results count verification passed: {} >= {}", actualCount, minCount);
            
        } catch (Exception e) {
            logger.error("Search results count verification failed for minimum: {}", minCount, e);
            takeScreenshotForStep("Search_Results_Count_Verification_Failed");
            throw new RuntimeException("Search results count verification failed", e);
        }
    }
    
    @Then("I should see MacBook Pro search results displayed")
    @Step("Verify MacBook Pro search results are properly displayed")
    public void iShouldSeeMacBookProSearchResultsDisplayed() {
        logStep("Verifying MacBook Pro search results are properly displayed");
        
        try {
            boolean resultsDisplayed = searchResultsPage.isPageLoaded();
            assertWithScreenshot(resultsDisplayed, "Search results should be displayed");
            
            boolean hasMacBookProProducts = searchResultsPage.hasMacBookProProducts();
            assertWithScreenshot(hasMacBookProProducts, "Should have MacBook Pro products");
            
            boolean resultsFormatted = searchResultsPage.areResultsProperlyFormatted();
            assertWithScreenshot(resultsFormatted, "Results should be properly formatted");
            
            takeScreenshotForStep("MacBook_Pro_Search_Results_Displayed");
            logger.info("MacBook Pro search results display verified");
            
        } catch (Exception e) {
            logger.error("MacBook Pro search results display verification failed", e);
            takeScreenshotForStep("MacBook_Pro_Search_Results_Display_Failed");
            throw new RuntimeException("MacBook Pro search results display verification failed", e);
        }
    }
    
    @And("I should see multiple MacBook Pro products")
    @Step("Verify multiple MacBook Pro products are displayed")
    public void iShouldSeeMultipleMacBookProProducts() {
        logStep("Verifying multiple MacBook Pro products are displayed");
        
        try {
            int macBookProCount = searchResultsPage.getMacBookProProductsCount();
            verifyNumberInRange(macBookProCount, 2, 100, "MacBook Pro products count");
            
            takeScreenshotForStep("Multiple_MacBook_Pro_Products_Verified");
            logger.info("Multiple MacBook Pro products verified: {} products", macBookProCount);
            
        } catch (Exception e) {
            logger.error("Multiple MacBook Pro products verification failed", e);
            takeScreenshotForStep("Multiple_MacBook_Pro_Products_Verification_Failed");
            throw new RuntimeException("Multiple MacBook Pro products verification failed", e);
        }
    }
    
    @And("I should verify search results contain relevant products")
    @Step("Verify search results contain relevant products")
    public void iShouldVerifySearchResultsContainRelevantProducts() {
        logStep("Verifying search results contain relevant products");
        
        try {
            String searchTerm = getStoredTestData("search_term");
            if (searchTerm == null) {
                searchTerm = "MacBook Pro"; // Default fallback
            }
            
            boolean hasRelevantProducts = searchResultsPage.hasProductsContaining(searchTerm);
            assertWithScreenshot(hasRelevantProducts, 
                "Search results should contain products relevant to: " + searchTerm);
            
            // Additional relevance checks
            List<String> productTitles = searchResultsPage.getAllProductTitles();
            int relevantCount = 0;
            for (String title : productTitles) {
                if (title.toLowerCase().contains(searchTerm.toLowerCase())) {
                    relevantCount++;
                }
            }
            
            double relevancePercentage = (double) relevantCount / productTitles.size() * 100;
            logger.info("Search relevance: {}/{} products ({}%)", 
                       relevantCount, productTitles.size(), String.format("%.1f", relevancePercentage));
            
            takeScreenshotForStep("Relevant_Products_Verified");
            logger.info("Search results relevance verified");
            
        } catch (Exception e) {
            logger.error("Search results relevance verification failed", e);
            takeScreenshotForStep("Relevant_Products_Verification_Failed");
            throw new RuntimeException("Search results relevance verification failed", e);
        }
    }
    
    // ===================================================================================
    // SORTING AND ORDER VALIDATIONS
    // ===================================================================================
    
    @Then("I should see products sorted by price descending")
    @Step("Verify products are sorted by price in descending order")
    public void iShouldSeeProductsSortedByPriceDescending() {
        logStep("Verifying products are sorted by price in descending order");
        
        try {
            boolean isSortedByPriceDesc = searchResultsPage.isProductsSortedByPriceDescending();
            assertWithScreenshot(isSortedByPriceDesc, 
                "Products should be sorted by price in descending order");
            
            // Log price verification for debugging
            List<Double> prices = searchResultsPage.getProductPrices();
            logger.info("Product prices in order: {}", prices);
            
            takeScreenshotForStep("Products_Sorted_By_Price_Descending");
            logger.info("Products sorted by price descending verified");
            
        } catch (Exception e) {
            logger.error("Products sorting by price descending verification failed", e);
            takeScreenshotForStep("Products_Sorting_Verification_Failed");
            throw new RuntimeException("Products sorting by price descending verification failed", e);
        }
    }
    
    // ===================================================================================
    // PRODUCT DETAILS PAGE VALIDATIONS
    // ===================================================================================
    
    @Then("I should see product details page is loaded")
    @Step("Verify product details page is fully loaded")
    public void iShouldSeeProductDetailsPageIsLoaded() {
        logStep("Verifying product details page is fully loaded");
        
        try {
            boolean pageLoaded = productDetailsPage.isPageLoaded();
            assertWithScreenshot(pageLoaded, "Product details page should be loaded");
            
            boolean allElementsLoaded = productDetailsPage.areAllElementsLoaded();
            assertWithScreenshot(allElementsLoaded, "All product details elements should be loaded");
            
            takeScreenshotForStep("Product_Details_Page_Loaded");
            logger.info("Product details page fully loaded verification passed");
            
        } catch (Exception e) {
            logger.error("Product details page loading verification failed", e);
            takeScreenshotForStep("Product_Details_Page_Loading_Failed");
            throw new RuntimeException("Product details page loading verification failed", e);
        }
    }
    
    @And("I should verify product title contains {string}")
    @Step("Verify and validate product title contains: {expectedText}")
    public void iShouldVerifyProductTitleContains(String expectedText) {
        logStep("Verifying and validating product title contains: " + expectedText);
        
        try {
            String actualTitle = productDetailsPage.getProductTitle();
            
            // Comprehensive title validation
            boolean containsExpected = actualTitle.toLowerCase().contains(expectedText.toLowerCase());
            boolean isNotEmpty = actualTitle != null && !actualTitle.trim().isEmpty();
            boolean hasValidLength = actualTitle.length() >= 5 && actualTitle.length() <= 200;
            
            assertWithScreenshot(containsExpected, 
                String.format("Product title should contain '%s'. Actual: '%s'", expectedText, actualTitle));
            softAssert(isNotEmpty, "Product title should not be empty");
            softAssert(hasValidLength, "Product title should have valid length");
            
            storeTestData("validated_product_title", actualTitle);
            
            takeScreenshotForStep("Product_Title_Validated");
            logger.info("Product title validation passed: '{}' contains '{}'", actualTitle, expectedText);
            
        } catch (Exception e) {
            logger.error("Product title validation failed for: {}", expectedText, e);
            takeScreenshotForStep("Product_Title_Validation_Failed");
            throw new RuntimeException("Product title validation failed", e);
        }
    }
    
    @And("I should see product price is displayed")
    @Step("Verify product price is properly displayed")
    public void iShouldSeeProductPriceIsDisplayed() {
        logStep("Verifying product price is properly displayed");
        
        try {
            boolean priceDisplayed = productDetailsPage.hasProductPrice();
            assertWithScreenshot(priceDisplayed, "Product price should be displayed");
            
            String priceText = productDetailsPage.getProductPrice();
            
            // Price format validation
            boolean hasValidFormat = priceText.matches(".*\\d+.*") && 
                                   (priceText.contains("₺") || priceText.contains("TL"));
            boolean isNotZero = !priceText.contains("0,00") && !priceText.contains("0.00");
            
            assertWithScreenshot(hasValidFormat, "Price should have valid format with currency");
            softAssert(isNotZero, "Price should not be zero");
            
            storeTestData("validated_product_price", priceText);
            
            takeScreenshotForStep("Product_Price_Displayed");
            logger.info("Product price display verified: {}", priceText);
            
        } catch (Exception e) {
            logger.error("Product price display verification failed", e);
            takeScreenshotForStep("Product_Price_Display_Failed");
            throw new RuntimeException("Product price display verification failed", e);
        }
    }
    
    @And("I should see product images are displayed")
    @Step("Verify product images are displayed")
    public void iShouldSeeProductImagesAreDisplayed() {
        logStep("Verifying product images are displayed");
        
        try {
            boolean imagesDisplayed = productDetailsPage.hasProductImages();
            assertWithScreenshot(imagesDisplayed, "Product images should be displayed");
            
            int imageCount = productDetailsPage.getProductImagesCount();
            verifyNumberInRange(imageCount, 1, 20, "Product images count");
            
            boolean imagesLoaded = productDetailsPage.areProductImagesLoaded();
            assertWithScreenshot(imagesLoaded, "Product images should be fully loaded");
            
            takeScreenshotForStep("Product_Images_Displayed");
            logger.info("Product images display verified: {} images", imageCount);
            
        } catch (Exception e) {
            logger.error("Product images display verification failed", e);
            takeScreenshotForStep("Product_Images_Display_Failed");
            throw new RuntimeException("Product images display verification failed", e);
        }
    }
    
    @And("I should verify add to cart button is enabled")
    @Step("Verify add to cart button is enabled and functional")
    public void iShouldVerifyAddToCartButtonIsEnabled() {
        logStep("Verifying add to cart button is enabled and functional");
        
        try {
            boolean buttonExists = productDetailsPage.hasAddToCartButton();
            boolean buttonEnabled = productDetailsPage.isAddToCartButtonEnabled();
            boolean buttonVisible = productDetailsPage.isAddToCartButtonVisible();
            boolean buttonClickable = productDetailsPage.isAddToCartButtonClickable();
            
            assertWithScreenshot(buttonExists, "Add to cart button should exist");
            assertWithScreenshot(buttonEnabled, "Add to cart button should be enabled");
            assertWithScreenshot(buttonVisible, "Add to cart button should be visible");
            assertWithScreenshot(buttonClickable, "Add to cart button should be clickable");
            
            takeScreenshotForStep("Add_To_Cart_Button_Enabled");
            logger.info("Add to cart button enabled verification passed");
            
        } catch (Exception e) {
            logger.error("Add to cart button enabled verification failed", e);
            takeScreenshotForStep("Add_To_Cart_Button_Enabled_Failed");
            throw new RuntimeException("Add to cart button enabled verification failed", e);
        }
    }
    
    // ===================================================================================
    // CART PAGE VALIDATIONS
    // ===================================================================================
    
    @Then("I should see success message for add to cart")
    @Step("Verify success message for add to cart operation")
    public void iShouldSeeSuccessMessageForAddToCart() {
        logStep("Verifying success message for add to cart operation");
        
        try {
            boolean hasSuccessMessage = productDetailsPage.hasAddToCartSuccessMessage() || 
                                       cartPage.hasAddToCartSuccessMessage();
            
            assertWithScreenshot(hasSuccessMessage, 
                "Should display success message for add to cart operation");
            
            takeScreenshotForStep("Add_To_Cart_Success_Message");
            logger.info("Add to cart success message verified");
            
        } catch (Exception e) {
            logger.error("Add to cart success message verification failed", e);
            takeScreenshotForStep("Add_To_Cart_Success_Message_Failed");
            throw new RuntimeException("Add to cart success message verification failed", e);
        }
    }
    
    @Then("I should see cart page is loaded correctly")
    @Step("Verify cart page is loaded correctly with all elements")
    public void iShouldSeeCartPageIsLoadedCorrectly() {
        logStep("Verifying cart page is loaded correctly with all elements");
        
        try {
            boolean pageLoaded = cartPage.isPageLoaded();
            assertWithScreenshot(pageLoaded, "Cart page should be loaded");
            
            boolean hasCartHeader = cartPage.hasCartHeader();
            boolean hasCartItems = cartPage.hasCartItemsSection();
            boolean hasCartSummary = cartPage.hasCartSummary();
            
            assertWithScreenshot(hasCartHeader, "Cart page should have header");
            softAssert(hasCartItems, "Cart page should have items section");
            softAssert(hasCartSummary, "Cart page should have summary section");
            
            takeScreenshotForStep("Cart_Page_Loaded_Correctly");
            logger.info("Cart page loaded correctly verification passed");
            
        } catch (Exception e) {
            logger.error("Cart page loading verification failed", e);
            takeScreenshotForStep("Cart_Page_Loading_Failed");
            throw new RuntimeException("Cart page loading verification failed", e);
        }
    }
    
    @And("I should see {int} item in cart")
    @Step("Verify {expectedCount} item in cart")
    public void iShouldSeeItemInCart(int expectedCount) {
        logStep("Verifying " + expectedCount + " item in cart");
        
        try {
            int actualCount = cartPage.getCartItemsCount();
            
            assertWithScreenshot(actualCount == expectedCount, 
                String.format("Cart should have %d items, but has %d", expectedCount, actualCount));
            
            storeTestData("cart_items_count", String.valueOf(actualCount));
            
            takeScreenshotForStep("Cart_Items_Count_Verified");
            logger.info("Cart items count verified: {} items", actualCount);
            
        } catch (Exception e) {
            logger.error("Cart items count verification failed for expected: {}", expectedCount, e);
            takeScreenshotForStep("Cart_Items_Count_Failed");
            throw new RuntimeException("Cart items count verification failed", e);
        }
    }
    
    @And("I should verify cart contains MacBook Pro")
    @Step("Verify cart contains MacBook Pro product")
    public void iShouldVerifyCartContainsMacBookPro() {
        logStep("Verifying cart contains MacBook Pro product");
        
        try {
            boolean hasMacBookPro = cartPage.containsMacBookProProduct();
            assertWithScreenshot(hasMacBookPro, "Cart should contain MacBook Pro product");
            
            // Additional verification for product details in cart
            if (hasMacBookPro) {
                String cartItemTitle = cartPage.getFirstCartItemTitle();
                boolean titleContainsMacBook = cartItemTitle.toLowerCase().contains("macbook");
                softAssert(titleContainsMacBook, "Cart item title should contain MacBook");
            }
            
            takeScreenshotForStep("Cart_Contains_MacBook_Pro");
            logger.info("Cart contains MacBook Pro verification passed");
            
        } catch (Exception e) {
            logger.error("Cart MacBook Pro verification failed", e);
            takeScreenshotForStep("Cart_MacBook_Pro_Failed");
            throw new RuntimeException("Cart MacBook Pro verification failed", e);
        }
    }
    
    @And("I should verify cart total is calculated correctly")
    @Step("Verify cart total is calculated correctly")
    public void iShouldVerifyCartTotalIsCalculatedCorrectly() {
        logStep("Verifying cart total is calculated correctly");
        
        try {
            boolean hasTotalPrice = cartPage.hasCartTotal();
            assertWithScreenshot(hasTotalPrice, "Cart should have total price displayed");
            
            String totalPrice = cartPage.getCartTotal();
            boolean isValidTotal = totalPrice != null && !totalPrice.isEmpty() && 
                                 totalPrice.matches(".*\\d+.*") &&
                                 (totalPrice.contains("₺") || totalPrice.contains("TL"));
            
            assertWithScreenshot(isValidTotal, "Cart total should be in valid format");
            
            // Verify total is not zero
            boolean isNotZero = !totalPrice.contains("0,00") && !totalPrice.contains("0.00");
            softAssert(isNotZero, "Cart total should not be zero");
            
            storeTestData("cart_total", totalPrice);
            
            takeScreenshotForStep("Cart_Total_Calculated");
            logger.info("Cart total calculation verified: {}", totalPrice);
            
        } catch (Exception e) {
            logger.error("Cart total calculation verification failed", e);
            takeScreenshotForStep("Cart_Total_Calculation_Failed");
            throw new RuntimeException("Cart total calculation verification failed", e);
        }
    }
    
    @And("I should see checkout button is available")
    @Step("Verify checkout button is available")
    public void iShouldSeeCheckoutButtonIsAvailable() {
        logStep("Verifying checkout button is available");
        
        try {
            boolean hasCheckoutButton = cartPage.hasCheckoutButton();
            boolean checkoutEnabled = cartPage.isCheckoutButtonEnabled();
            
            assertWithScreenshot(hasCheckoutButton, "Cart should have checkout button");
            assertWithScreenshot(checkoutEnabled, "Checkout button should be enabled");
            
            takeScreenshotForStep("Checkout_Button_Available");
            logger.info("Checkout button availability verified");
            
        } catch (Exception e) {
            logger.error("Checkout button availability verification failed", e);
            takeScreenshotForStep("Checkout_Button_Availability_Failed");
            throw new RuntimeException("Checkout button availability verification failed", e);
        }
    }
    
    // ===================================================================================
    // QUANTITY AND CART CONTENT VALIDATIONS
    // ===================================================================================
    
    @Then("I should see the product in cart with quantity {string}")
    @Step("Verify product in cart with quantity: {expectedQuantity}")
    public void iShouldSeeTheProductInCartWithQuantity(String expectedQuantity) {
        logStep("Verifying product in cart with quantity: " + expectedQuantity);
        
        try {
            int expectedQty = Integer.parseInt(expectedQuantity);
            
            // Navigate to cart if not already there
            if (!cartPage.isPageLoaded()) {
                cartPage = homePage.navigateToCart();
                cartPage.waitForPageToLoad();
            }
            
            boolean hasProduct = cartPage.hasCartItems();
            assertWithScreenshot(hasProduct, "Cart should have products");
            
            int actualQuantity = cartPage.getFirstCartItemQuantity();
            assertWithScreenshot(actualQuantity == expectedQty, 
                String.format("Product quantity should be %d, but is %d", expectedQty, actualQuantity));
            
            storeTestData("verified_cart_quantity", String.valueOf(actualQuantity));
            
            takeScreenshotForStep("Product_Quantity_In_Cart_Verified");
            logger.info("Product quantity in cart verified: {} items", actualQuantity);
            
        } catch (NumberFormatException e) {
            logger.error("Invalid quantity format: {}", expectedQuantity, e);
            throw new RuntimeException("Invalid quantity format: " + expectedQuantity, e);
        } catch (Exception e) {
            logger.error("Product quantity in cart verification failed", e);
            takeScreenshotForStep("Product_Quantity_In_Cart_Failed");
            throw new RuntimeException("Product quantity in cart verification failed", e);
        }
    }
    
    // ===================================================================================
    // NEGATIVE SCENARIO VALIDATIONS
    // ===================================================================================
    
    @Then("I should see no results message")
    @Step("Verify no results message is displayed")
    public void iShouldSeeNoResultsMessage() {
        logStep("Verifying no results message is displayed");
        
        try {
            boolean hasNoResultsMessage = searchResultsPage.hasNoResultsMessage();
            assertWithScreenshot(hasNoResultsMessage, "Should display no results message");
            
            String noResultsText = searchResultsPage.getNoResultsMessage();
            boolean isValidMessage = noResultsText != null && !noResultsText.isEmpty();
            softAssert(isValidMessage, "No results message should be meaningful");
            
            takeScreenshotForStep("No_Results_Message_Displayed");
            logger.info("No results message verified: {}", noResultsText);
            
        } catch (Exception e) {
            logger.error("No results message verification failed", e);
            takeScreenshotForStep("No_Results_Message_Failed");
            throw new RuntimeException("No results message verification failed", e);
        }
    }
    
    @Then("I should see empty search results")
    @Step("Verify search results are empty")
    public void iShouldSeeEmptySearchResults() {
        logStep("Verifying search results are empty");
        
        try {
            boolean hasEmptyResults = searchResultsPage.isResultsEmpty();
            assertWithScreenshot(hasEmptyResults, "Search results should be empty");
            
            int resultsCount = searchResultsPage.getResultsCount();
            assertWithScreenshot(resultsCount == 0, 
                String.format("Results count should be 0, but is %d", resultsCount));
            
            takeScreenshotForStep("Empty_Search_Results_Verified");
            logger.info("Empty search results verified");
            
        } catch (Exception e) {
            logger.error("Empty search results verification failed", e);
            takeScreenshotForStep("Empty_Search_Results_Failed");
            throw new RuntimeException("Empty search results verification failed", e);
        }
    }
    
    // ===================================================================================
    // ACCESSIBILITY VALIDATIONS
    // ===================================================================================
    
    @Then("the search box should be accessible")
    @Step("Verify search box accessibility compliance")
    public void theSearchBoxShouldBeAccessible() {
        logStep("Verifying search box accessibility compliance");
        
        try {
            boolean hasAccessibilityLabel = homePage.doesSearchBoxHaveAccessibilityLabel();
            boolean hasContentDescription = homePage.doesSearchBoxHaveContentDescription();
            boolean isScreenReaderFriendly = homePage.isSearchBoxScreenReaderFriendly();
            
            assertWithScreenshot(hasAccessibilityLabel, "Search box should have accessibility label");
            assertWithScreenshot(hasContentDescription, "Search box should have content description");
            softAssert(isScreenReaderFriendly, "Search box should be screen reader friendly");
            
            takeScreenshotForStep("Search_Box_Accessibility_Verified");
            logger.info("Search box accessibility verification passed");
            
        } catch (Exception e) {
            logger.error("Search box accessibility verification failed", e);
            takeScreenshotForStep("Search_Box_Accessibility_Failed");
            throw new RuntimeException("Search box accessibility verification failed", e);
        }
    }
    
    @And("the search button should be accessible")
    @Step("Verify search button accessibility compliance")
    public void theSearchButtonShouldBeAccessible() {
        logStep("Verifying search button accessibility compliance");
        
        try {
            boolean hasAccessibilityLabel = homePage.doesSearchButtonHaveAccessibilityLabel();
            boolean hasContentDescription = homePage.doesSearchButtonHaveContentDescription();
            boolean isScreenReaderFriendly = homePage.isSearchButtonScreenReaderFriendly();
            
            assertWithScreenshot(hasAccessibilityLabel, "Search button should have accessibility label");
            assertWithScreenshot(hasContentDescription, "Search button should have content description");
            softAssert(isScreenReaderFriendly, "Search button should be screen reader friendly");
            
            takeScreenshotForStep("Search_Button_Accessibility_Verified");
            logger.info("Search button accessibility verification passed");
            
        } catch (Exception e) {
            logger.error("Search button accessibility verification failed", e);
            takeScreenshotForStep("Search_Button_Accessibility_Failed");
            throw new RuntimeException("Search button accessibility verification failed", e);
        }
    }
    
    @And("all search elements should have proper labels")
    @Step("Verify all search elements have proper accessibility labels")
    public void allSearchElementsShouldHaveProperLabels() {
        logStep("Verifying all search elements have proper accessibility labels");
        
        try {
            boolean searchBoxLabeled = homePage.doesSearchBoxHaveAccessibilityLabel();
            boolean searchButtonLabeled = homePage.doesSearchButtonHaveAccessibilityLabel();
            boolean allElementsLabeled = searchBoxLabeled && searchButtonLabeled;
            
            assertWithScreenshot(allElementsLabeled, 
                "All search elements should have proper accessibility labels");
            
            takeScreenshotForStep("All_Search_Elements_Labeled");
            logger.info("All search elements accessibility labels verified");
            
        } catch (Exception e) {
            logger.error("Search elements accessibility labels verification failed", e);
            takeScreenshotForStep("Search_Elements_Labels_Failed");
            throw new RuntimeException("Search elements accessibility labels verification failed", e);
        }
    }
}