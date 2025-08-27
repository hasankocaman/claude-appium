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
    
    public void waitForPageToLoad() {
        waitVisible(accountTitle);
    }
    
    
    public boolean isPageLoaded() {
        return isDisplayed(accountTitle);
    }
    
    
    public String getPageTitle() {
        return "Hepsiburada - Account";
    }
}