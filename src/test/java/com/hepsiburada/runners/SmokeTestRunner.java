package com.hepsiburada.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Smoke Test Runner for executing critical path tests
 * Runs only smoke tagged scenarios for quick validation
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
        "html:target/smoke-reports/cucumber-html-reports",
        
        // JSON report
        "json:target/smoke-reports/cucumber.json",
        
        // JUnit XML report
        "junit:target/smoke-reports/cucumber-junit-report.xml",
        
        // Allure integration
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
    },
    
    // Only smoke tests
    tags = "@smoke and not @ignore",
    
    // Test execution options
    monochrome = true,
    publish = false,
    snippets = CucumberOptions.SnippetType.CAMELCASE,
    dryRun = false
)
public class SmokeTestRunner {
    // Test runner implementation is handled by Cucumber JUnit runner
}