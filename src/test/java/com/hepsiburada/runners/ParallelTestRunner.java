package com.hepsiburada.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Parallel Test Runner for high-performance test execution
 * Configured for maximum parallel execution with proper thread management
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
        "html:target/parallel-reports/cucumber-html-reports",
        
        // JSON report
        "json:target/parallel-reports/cucumber.json",
        
        // JUnit XML report
        "junit:target/parallel-reports/cucumber-junit-report.xml",
        
        // Allure integration
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
    },
    
    // All regression tests excluding ignored ones
    tags = "@regression and not @ignore",
    
    // Test execution options
    monochrome = true,
    publish = false,
    snippets = CucumberOptions.SnippetType.CAMELCASE
)
@Test
public class ParallelTestRunner extends AbstractTestNGCucumberTests {
    
    /**
     * Configure parallel execution at scenario level
     * Thread count is controlled by TestNG configuration or system property
     * 
     * @return Test scenarios as parallel data provider
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}