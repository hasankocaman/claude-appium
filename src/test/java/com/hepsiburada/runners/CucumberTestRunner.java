package com.hepsiburada.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Cucumber Test Runner for executing BDD tests
 * Configured for comprehensive test execution with multiple reporting options
 * 
 * @author Hepsiburada Test Automation Team
 */
@RunWith(Cucumber.class)
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
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
        
        // Timeline report
        "timeline:target/cucumber-reports/timeline"
    },
    
    // Tags to include/exclude
    tags = "@regression and not @ignore",
    
    // Test execution options
    monochrome = true,              // Clean console output
    publish = false,                // Don't publish to Cucumber Reports service
    snippets = CucumberOptions.SnippetType.CAMELCASE,  // Code generation style
    dryRun = false                  // Set to true for syntax checking without execution
)
public class CucumberTestRunner {
    // Test runner implementation is handled by Cucumber JUnit runner
}