# Contributing to Hepsiburada Mobile Test Automation

We welcome contributions to the Hepsiburada Mobile Test Automation Framework! This document provides guidelines and instructions for contributing to the project.

## 📋 Table of Contents

1. [Code of Conduct](#code-of-conduct)
2. [Getting Started](#getting-started)
3. [Development Setup](#development-setup)
4. [Contributing Guidelines](#contributing-guidelines)
5. [Code Standards](#code-standards)
6. [Testing Guidelines](#testing-guidelines)
7. [Pull Request Process](#pull-request-process)
8. [Issue Reporting](#issue-reporting)
9. [Documentation](#documentation)

---

## 🤝 Code of Conduct

This project adheres to the Contributor Covenant Code of Conduct. By participating, you are expected to uphold this code. Please report unacceptable behavior to test-automation@hepsiburada.com.

### Our Pledge
- Be respectful and inclusive
- Welcome newcomers and help them learn
- Focus on constructive feedback
- Prioritize the community's best interests

---

## 🚀 Getting Started

### Prerequisites
- Java 11 or higher
- Maven 3.8+
- Git
- IDE (IntelliJ IDEA recommended)
- Basic knowledge of Appium, Cucumber, and TestNG

### Fork and Clone
1. Fork the repository on GitHub
2. Clone your fork locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/hepsiburada-mobile-automation.git
   cd hepsiburada-mobile-automation
   ```

3. Add the original repository as upstream:
   ```bash
   git remote add upstream https://github.com/original/hepsiburada-mobile-automation.git
   ```

---

## 💻 Development Setup

### Initial Setup
```bash
# Install dependencies
mvn clean install

# Run tests to verify setup
mvn clean test -Dcucumber.filter.tags="@smoke"

# Install pre-commit hooks (if available)
npm install -g pre-commit
pre-commit install
```

### IDE Configuration

#### IntelliJ IDEA
1. **Import Project**: Import as Maven project
2. **Code Style**: 
   - Go to Settings → Editor → Code Style → Java
   - Set indent: 4 spaces
   - Set continuation indent: 8 spaces
3. **Cucumber Plugin**: Install Cucumber for Java plugin
4. **Lombok**: Install Lombok plugin (if used)

#### Eclipse
1. **Import Project**: Import as Maven project
2. **Code Formatter**: Import Java formatting rules
3. **Cucumber Plugin**: Install Cucumber Eclipse plugin

---

## 📝 Contributing Guidelines

### Types of Contributions

#### 🐛 Bug Fixes
- Fix existing functionality
- Improve error handling
- Resolve failing tests

#### ✨ New Features
- Add new test scenarios
- Implement new page objects
- Add utility methods
- Enhance reporting capabilities

#### 📚 Documentation
- Improve README
- Add code comments
- Create tutorials
- Update troubleshooting guide

#### 🔧 Maintenance
- Update dependencies
- Refactor code
- Improve performance
- Add logging

### Branch Strategy

```bash
# Create feature branch from main
git checkout main
git pull upstream main
git checkout -b feature/your-feature-name

# Create bugfix branch
git checkout -b bugfix/issue-description

# Create documentation branch
git checkout -b docs/update-readme
```

### Commit Guidelines

#### Commit Message Format
```
<type>(<scope>): <description>

<body>

<footer>
```

#### Types
- **feat**: New feature
- **fix**: Bug fix
- **docs**: Documentation changes
- **style**: Code style changes (formatting, etc.)
- **refactor**: Code refactoring
- **test**: Adding or updating tests
- **chore**: Maintenance tasks

#### Examples
```bash
feat(pages): add product comparison page object

Add new page object for product comparison functionality
- Implement comparison table elements
- Add methods for product comparison actions
- Include validation methods for comparison results

Resolves: #123

fix(drivers): resolve driver timeout issue

Increase default timeout values to handle slow device responses
- Update implicit wait from 10s to 15s  
- Add retry logic for driver initialization
- Improve error logging for debugging

Fixes: #456

docs(readme): update installation instructions

- Add Windows-specific setup steps
- Include troubleshooting for common issues
- Update dependency versions
```

---

## 🎨 Code Standards

### Java Code Style

#### Naming Conventions
```java
// Classes: PascalCase
public class ProductDetailsPage extends BasePage {

// Methods: camelCase  
public void addToCart() {

// Variables: camelCase
private WebElement addToCartButton;

// Constants: UPPER_SNAKE_CASE
private static final int DEFAULT_TIMEOUT = 30;

// Packages: lowercase
package com.hepsiburada.pages;
```

#### Class Structure
```java
/**
 * Class-level JavaDoc documentation
 * Describes the purpose and usage of the class
 * 
 * @author Your Name
 */
public class ExamplePage extends BasePage {
    
    // 1. Constants
    private static final Logger logger = LogManager.getLogger(ExamplePage.class);
    private static final int TIMEOUT = 30;
    
    // 2. WebElements (grouped by functionality)
    @AndroidFindBy(id = "search_box")
    private WebElement searchBox;
    
    // 3. Constructor
    public ExamplePage() {
        super();
    }
    
    // 4. Public methods (grouped by functionality)
    public void performSearch(String term) {
        // Method implementation
    }
    
    // 5. Private helper methods
    private void waitForPageLoad() {
        // Helper implementation
    }
}
```

#### Method Guidelines
```java
/**
 * Method-level JavaDoc for public methods
 * @param searchTerm The term to search for
 * @return SearchResultsPage instance
 * @throws RuntimeException if search fails
 */
@Step("Search for product: {searchTerm}")
public SearchResultsPage searchForProduct(String searchTerm) {
    try {
        logger.info("Searching for product: {}", searchTerm);
        
        // Method implementation
        enterText(searchBox, searchTerm);
        clickElement(searchButton);
        
        logger.info("Search completed successfully");
        return new SearchResultsPage();
        
    } catch (Exception e) {
        logger.error("Failed to search for product: {}", searchTerm, e);
        throw new RuntimeException("Search failed", e);
    }
}
```

### Test Code Standards

#### Step Definitions
```java
@Given("I am on the home page")
@Step("Navigate to home page")
public void iAmOnTheHomePage() {
    logStep("Navigating to home page");
    
    try {
        homePage.waitForPageToLoad();
        boolean isLoaded = homePage.isPageLoaded();
        assertWithScreenshot(isLoaded, "Home page should be loaded");
        
        takeScreenshotForStep("Home_Page_Loaded");
        
    } catch (Exception e) {
        logger.error("Failed to navigate to home page", e);
        takeScreenshotForStep("Home_Page_Navigation_Failed");
        throw new RuntimeException("Home page navigation failed", e);
    }
}
```

#### Feature Files
```gherkin
@regression @mobile @e2e
Feature: Product Search and Purchase
  As a customer
  I want to search for products and add them to cart
  So that I can complete my purchase journey

  Background:
    Given the Hepsiburada mobile app is launched
    And I am on the home page

  @smoke @search
  Scenario: Search for MacBook Pro
    Given I can see the search functionality is available
    When I search for "MacBook Pro"
    Then I should see search results for MacBook Pro
    And I should see at least 1 MacBook product in results
```

---

## 🧪 Testing Guidelines

### Test Writing Standards

#### Page Object Methods
```java
// Good - Clear method names and proper error handling
public ProductDetailsPage selectMostExpensiveProduct() {
    try {
        logger.info("Selecting most expensive product");
        sortByPrice("highest");
        clickElement(productResults.get(0));
        return new ProductDetailsPage();
    } catch (Exception e) {
        logger.error("Failed to select most expensive product", e);
        throw new RuntimeException("Product selection failed", e);
    }
}

// Bad - Vague method name and no error handling  
public void click() {
    element.click();
}
```

#### Assertions and Validations
```java
// Good - Descriptive assertions with custom messages
assertWithScreenshot(isElementDisplayed(cartIcon), 
    "Cart icon should be displayed on the header");

verifyTextContains(productTitle, "MacBook", 
    "Product title should contain MacBook");

// Bad - Generic assertions without context
Assert.assertTrue(result);
```

#### Test Data Management
```java
// Good - Use test data classes and factories
UserCredentials user = TestDataFactory.createValidUser();
SearchCriteria criteria = TestDataFactory.createMacBookSearch();

// Bad - Hardcoded values in tests
String username = "test@test.com";
String password = "password123";
```

### Test Organization

#### Test Categories
- **@smoke**: Critical path tests (< 30 minutes)
- **@regression**: Full test suite (< 2 hours)  
- **@e2e**: End-to-end workflows
- **@performance**: Performance validation tests
- **@accessibility**: Accessibility compliance tests

#### Test Independence
```java
// Good - Each test is independent
@Test
public void testProductSearch() {
    homePage.searchForProduct("MacBook");
    // Test completes independently
}

// Bad - Tests depend on each other
@Test(dependsOnMethods = "testLogin")
public void testProductSearch() {
    // Depends on previous test state
}
```

---

## 🔄 Pull Request Process

### Before Submitting
1. **Sync with upstream**:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Run tests locally**:
   ```bash
   mvn clean test
   mvn checkstyle:check
   ```

3. **Update documentation** if needed

### Pull Request Checklist

- [ ] Code follows project style guidelines
- [ ] Tests pass locally
- [ ] New tests added for new functionality  
- [ ] Documentation updated
- [ ] Commit messages are clear and descriptive
- [ ] PR description explains the changes

### Pull Request Template
```markdown
## Description
Brief description of changes and motivation.

## Type of Change
- [ ] Bug fix
- [ ] New feature  
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing performed

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Tests pass
- [ ] Documentation updated

## Screenshots (if applicable)
Add screenshots for UI changes.

## Related Issues
Fixes #123
Relates to #456
```

### Review Process
1. **Automated Checks**: CI/CD pipeline runs
2. **Code Review**: At least one team member reviews
3. **Testing**: QA team validates (if needed)
4. **Approval**: Maintainer approves and merges

---

## 🐛 Issue Reporting

### Bug Reports
Use the bug report template:

```markdown
**Bug Description**
Clear description of the bug.

**Steps to Reproduce**
1. Go to '...'
2. Click on '....'
3. See error

**Expected Behavior**
What should happen.

**Actual Behavior**  
What actually happens.

**Environment**
- OS: [e.g., Windows 10]
- Java Version: [e.g., 11]
- Device: [e.g., Android 11]
- App Version: [e.g., 1.0.0]

**Screenshots**
Add screenshots if applicable.

**Additional Context**
Any other context about the problem.
```

### Feature Requests
```markdown
**Feature Description**
Clear description of the desired feature.

**Use Case**
Why is this feature needed?

**Proposed Solution**
How should this feature work?

**Alternatives Considered**
Other approaches considered.

**Additional Context**
Any other context or screenshots.
```

---

## 📚 Documentation

### Code Documentation

#### JavaDoc Standards
```java
/**
 * Searches for a product and returns search results page.
 * 
 * This method performs a product search by entering the search term
 * in the search box and clicking the search button. It waits for the
 * search results to load before returning the results page.
 * 
 * @param searchTerm the product search term (must not be null or empty)
 * @return SearchResultsPage instance containing the search results
 * @throws IllegalArgumentException if searchTerm is null or empty
 * @throws RuntimeException if the search operation fails
 * 
 * @since 1.0.0
 * @see SearchResultsPage
 */
public SearchResultsPage searchForProduct(String searchTerm) {
    // Implementation
}
```

#### Comment Guidelines
```java
// Good - Explain complex logic or business rules
// Sort products by price in descending order to get most expensive first
searchResults.sortByPrice(SortOrder.DESCENDING);

// Handle Android-specific timeout issues
if (DriverManager.isAndroid()) {
    Thread.sleep(2000); // Additional wait for Android UI updates
}

// Bad - Obvious comments
// Click the button
button.click();
```

### README Updates
When adding new features:
1. Update the feature list
2. Add usage examples
3. Update configuration options
4. Add troubleshooting entries

---

## 🏆 Recognition

### Contributors
We recognize contributors through:
- GitHub contributor statistics
- Team meeting acknowledgments  
- Internal recognition programs
- Code review participation

### Becoming a Maintainer
Maintainer status is earned through:
- Consistent high-quality contributions
- Helping other contributors
- Participating in code reviews
- Understanding of project architecture

---

## 📞 Getting Help

### Communication Channels
- **Slack**: #test-automation
- **Email**: test-automation@hepsiburada.com
- **GitHub Issues**: For bugs and features
- **GitHub Discussions**: For questions and ideas

### Mentorship
New contributors can request mentorship:
- Pair programming sessions
- Code review guidance  
- Architecture discussions
- Best practices training

---

## 📋 Resources

### Learning Materials
- [Appium Documentation](http://appium.io/docs/en/about-appium/intro/)
- [Cucumber BDD Guide](https://cucumber.io/docs/guides/)
- [TestNG Documentation](https://testng.org/doc/documentation-main.html)
- [Page Object Pattern](https://martinfowler.com/bliki/PageObject.html)

### Tools and Setup
- [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- [Android Studio](https://developer.android.com/studio)
- [Appium Inspector](https://github.com/appium/appium-inspector)
- [Git Guidelines](https://git-scm.com/book)

---

Thank you for contributing to the Hepsiburada Mobile Test Automation Framework! 🚀

**Questions?** Reach out to us on Slack #test-automation or email test-automation@hepsiburada.com