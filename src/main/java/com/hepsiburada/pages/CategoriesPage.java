package com.hepsiburada.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

/**
 * Categories Page class - placeholder for future implementation
 * 
 * @author Hepsiburada Test Automation Team
 */
public class CategoriesPage extends BasePage {
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvCategoriesTitle")
    @iOSXCUITFindBy(id = "categoriesTitle")
    private WebElement categoriesTitle;
    
    @Override
    public void waitForPageToLoad() {
        waitForElementToBeVisible(categoriesTitle);
    }
    
    @Override
    public boolean isPageLoaded() {
        return isElementDisplayed(categoriesTitle);
    }
    
    @Override
    public String getPageTitle() {
        return "Hepsiburada - Categories";
    }
}