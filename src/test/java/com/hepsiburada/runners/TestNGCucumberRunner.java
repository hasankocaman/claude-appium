package com.hepsiburada.runners;

import com.hepsiburada.utils.BaseTest;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * TestNG Cucumber Test Runner for parallel execution support
 * Extends BaseTest for comprehensive test lifecycle management
 * 
 * @author Hepsiburada Test Automation Team
 */
@CucumberOptions(
    // Feature files location
    features = "src/test/resources/features",
    
    // Step definitions package
    glue = {
        "com.hepsiburada.stepdefinitions",
        "com.hepsiburada.hooks"
    },
    
    // Cucumber plugins for reporting
    plugin = {
        // Pretty console output
        "pretty",
        
        // HTML report
        "html:target/cucumber-reports/cucumber-html-reports",
        
        // JSON report for other tools
        "json:target/cucumber-reports/cucumber.json",
        
        // JUnit XML report
        "junit:target/cucumber-reports/cucumber-junit-report.xml",
        
        // Allure Cucumber integration
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
        
        // ExtentReports Cucumber adapter
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    
    // Tags to include/exclude
    tags = "@regression and not @ignore",
    
    // Test execution options
    monochrome = true,              // Clean console output
    publish = false,                // Don't publish to Cucumber Reports service
    snippets = CucumberOptions.SnippetType.CAMELCASE  // Code generation style
)
@Test
public class TestNGCucumberRunner extends AbstractTestNGCucumberTests {
    
    /**
     * Enable parallel execution at scenario level
     * @return Test scenarios as parallel data provider
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}