@regression @mobile @e2e
Feature: MacBook Pro Purchase Journey
  As a customer
  I want to search for and purchase the most expensive MacBook Pro
  So that I can complete my shopping journey on Hepsiburada mobile app

  Background:
    Given the Hepsiburada mobile app is launched
    And I am on the home page

  @smoke @macbook @cart
  Scenario: Search and add most expensive MacBook Pro to cart
    Given I can see the home page is loaded correctly
    When I search for "MacBook Pro"
    Then I should see MacBook search results
    When I sort the results by highest price
    Then I should see MacBook Pro products sorted by price
    When I select the most expensive MacBook Pro
    Then I should see the MacBook Pro product details page
    When I add the MacBook Pro to cart
    Then I should see a success message
    And I should be redirected to the cart page
    And I should see the MacBook Pro in my cart
    And I should verify the cart contains the correct product

  @smoke @search
  Scenario: Search for MacBook products
    Given I can see the search functionality is available
    When I search for "MacBook"
    Then I should see search results for MacBook products
    And I should see at least 1 MacBook product in results
    And I should be able to sort the results

  @cart @validation
  Scenario: Validate empty cart initially
    Given I navigate to the cart page
    Then I should see the cart is empty
    And I should see a message to start shopping

  @macbook @product-details
  Scenario: View MacBook Pro product details
    Given I search for "MacBook Pro"
    And I can see MacBook Pro search results
    When I select any MacBook Pro product
    Then I should see the product details page
    And I should see product title containing "MacBook"
    And I should see product price information
    And I should see add to cart button is available

  @search @negative
  Scenario: Search with invalid product name
    Given I can see the search functionality is available
    When I search for "InvalidProductXYZ123"
    Then I should see no results message
    Or I should see empty search results

  @cart @quantity
  Scenario Outline: Add MacBook Pro with different quantities
    Given I search for "MacBook Pro"
    And I select the first MacBook Pro from results
    And I am on the product details page
    When I set the quantity to "<quantity>"
    And I add the product to cart
    Then I should see the product in cart with quantity "<quantity>"
    
    Examples:
      | quantity |
      | 1        |
      | 2        |

  @data-driven @search
  Scenario Outline: Search for different Apple products
    Given I am on the home page
    When I search for "<product>"
    Then I should see search results for "<product>"
    And I should see at least 1 result
    
    Examples:
      | product     |
      | MacBook Pro |
      | MacBook Air |
      | iMac        |

  @end-to-end @complete-journey
  Scenario: Complete MacBook Pro purchase journey with validations
    # Home page validation
    Given the Hepsiburada mobile app is launched
    And I verify the home page is loaded with all elements
    And I can see the Hepsiburada logo
    And I can see the search box is available
    
    # Search functionality
    When I perform a search for "MacBook Pro"
    And I wait for search results to load
    Then I should see MacBook Pro search results displayed
    And I should see multiple MacBook Pro products
    And I should verify search results contain relevant products
    
    # Sorting and selection
    When I apply sort by highest price
    And I wait for results to be sorted
    Then I should see products sorted by price descending
    When I select the first product from sorted results
    And I wait for product details to load
    
    # Product details validation
    Then I should see product details page is loaded
    And I should verify product title contains "MacBook"
    And I should see product price is displayed
    And I should see product images are displayed
    And I should verify add to cart button is enabled
    
    # Add to cart process
    When I click on add to cart button
    And I wait for add to cart confirmation
    Then I should see success message for add to cart
    
    # Cart validation
    When I navigate to shopping cart
    And I wait for cart page to load
    Then I should see cart page is loaded correctly
    And I should see 1 item in cart
    And I should verify cart contains MacBook Pro
    And I should verify cart total is calculated correctly
    And I should see checkout button is available

  @performance @search
  Scenario: Performance test for search functionality
    Given I measure the time for search operations
    When I search for "MacBook Pro"
    Then the search should complete within 10 seconds
    And the results should load within 5 seconds
    And I should see performance metrics in logs

  @accessibility @search
  Scenario: Accessibility validation for search
    Given I am on the home page
    When I check the search box accessibility
    Then the search box should be accessible
    And the search button should be accessible
    And all search elements should have proper labels

  @error-handling @network
  Scenario: Handle network errors during search
    Given I simulate network connectivity issues
    When I attempt to search for "MacBook Pro"
    Then I should see appropriate error message
    Or the app should handle the error gracefully
    And I should be able to retry the search

  @boundary @search
  Scenario Outline: Search with boundary conditions
    Given I am on the home page
    When I search for "<search_term>"
    Then I should see appropriate response for "<search_term>"
    
    Examples:
      | search_term                          |
      | A                                   |
      | AA                                  |
      | MacBook Pro 16 inch 2023 M2 Max   |
      | SpecialCharacters!@#$%             |
      | 1234567890                         |