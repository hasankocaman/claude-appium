package com.hepsiburada.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

/**
 * Account Page class - placeholder for future implementation
 * 
 * @author Hepsiburada Test Automation Team
 */
public class AccountPage extends BasePage {
    
    @AndroidFindBy(id = "com.hepsiburada.ecommerce:id/tvAccountTitle")
    @iOSXCUITFindBy(id = "accountTitle")
    private WebElement accountTitle;
    
    @Override
    public void waitForPageToLoad() {
        waitForElementToBeVisible(accountTitle);
    }
    
    @Override
    public boolean isPageLoaded() {
        return isElementDisplayed(accountTitle);
    }
    
    @Override
    public String getPageTitle() {
        return "Hepsiburada - Account";
    }
}