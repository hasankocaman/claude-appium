package com.hepsiburada.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hepsiburada.config.ConfigurationManager;
import com.hepsiburada.drivers.DriverManager;
import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Capabilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Allure Environment Utilities class for setting up environment information
 * Provides comprehensive environment setup for Allure reports with device and test configuration details
 * 
 * @author Hepsiburada Test Automation Team
 */
public final class AllureEnvironmentUtils {
    
    private static final Logger logger = LogManager.getLogger(AllureEnvironmentUtils.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ReentrantLock ENV_LOCK = new ReentrantLock();
    
    // File paths
    private static final String ENVIRONMENT_PROPERTIES_FILE = "environment.properties";
    private static final String ENVIRONMENT_XML_FILE = "environment.xml";
    private static final String CATEGORIES_JSON_FILE = "categories.json";
    
    private static boolean environmentSetup = false;
    
    private AllureEnvironmentUtils() {
        // Utility class should not be instantiated
    }
    
    /**
     * Set complete environment information for Allure reports
     * This method collects and sets all necessary environment details
     */
    public static void setEnvironmentInformation() {
        ENV_LOCK.lock();
        try {
            if (environmentSetup) {
                logger.debug("Allure environment already setup");
                return;
            }
            
            logger.info("Setting up Allure environment information");
            
            // Create allure-results directory if it doesn't exist
            String allureResultsDir = FileUtils.getAllureResultsDirectory();
            FileUtils.createDirectory(allureResultsDir);
            
            // Set up environment properties
            setupEnvironmentProperties(allureResultsDir);
            
            // Set up environment XML
            setupEnvironmentXML(allureResultsDir);
            
            // Set up categories for test classification
            setupCategories(allureResultsDir);
            
            // Set runtime environment info to Allure
            setRuntimeEnvironmentInfo();
            
            environmentSetup = true;
            logger.info("Allure environment setup completed successfully");
            
        } catch (Exception e) {
            logger.error("Failed to setup Allure environment", e);
        } finally {
            ENV_LOCK.unlock();
        }
    }
    
    /**
     * Setup environment.properties file for Allure
     * @param allureResultsDir Allure results directory
     */
    private static void setupEnvironmentProperties(String allureResultsDir) {
        try {
            Properties properties = new Properties();
            Map<String, String> envInfo = collectEnvironmentInformation();
            
            // Add all environment information to properties
            for (Map.Entry<String, String> entry : envInfo.entrySet()) {
                properties.setProperty(entry.getKey(), entry.getValue());
            }
            
            // Write properties file
            String propertiesPath = allureResultsDir + File.separator + ENVIRONMENT_PROPERTIES_FILE;
            try (FileWriter writer = new FileWriter(propertiesPath)) {
                properties.store(writer, "Allure Environment Properties - Generated on " + 
                        LocalDateTime.now().format(TIMESTAMP_FORMAT));
            }
            
            logger.debug("Environment properties file created: {}", propertiesPath);
            
        } catch (Exception e) {
            logger.error("Failed to create environment properties file", e);
        }
    }
    
    /**
     * Setup environment.xml file for Allure (alternative to properties)
     * @param allureResultsDir Allure results directory
     */
    private static void setupEnvironmentXML(String allureResultsDir) {
        try {
            StringBuilder xmlBuilder = new StringBuilder();
            xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xmlBuilder.append("<environment>\n");
            
            Map<String, String> envInfo = collectEnvironmentInformation();
            
            // Add all environment information to XML
            for (Map.Entry<String, String> entry : envInfo.entrySet()) {
                xmlBuilder.append("    <parameter>\n");
                xmlBuilder.append("        <key>").append(escapeXml(entry.getKey())).append("</key>\n");
                xmlBuilder.append("        <value>").append(escapeXml(entry.getValue())).append("</value>\n");
                xmlBuilder.append("    </parameter>\n");
            }
            
            xmlBuilder.append("</environment>");
            
            // Write XML file
            String xmlPath = allureResultsDir + File.separator + ENVIRONMENT_XML_FILE;
            FileUtils.writeStringToFile(xmlPath, xmlBuilder.toString());
            
            logger.debug("Environment XML file created: {}", xmlPath);
            
        } catch (Exception e) {
            logger.error("Failed to create environment XML file", e);
        }
    }
    
    /**
     * Setup categories.json file for test classification
     * @param allureResultsDir Allure results directory
     */
    private static void setupCategories(String allureResultsDir) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode categoriesArray = mapper.createObjectNode();
            
            // Create categories for different types of failures
            ObjectNode categories = mapper.createArrayNode().addObject();
            
            // Product defects category
            ObjectNode productDefects = mapper.createObjectNode();
            productDefects.put("name", "Product defects");
            productDefects.put("matchedStatuses", mapper.createArrayNode().add("failed"));
            productDefects.put("messageRegex", ".*AssertionError.*|.*assertion failed.*");
            
            // Test defects category
            ObjectNode testDefects = mapper.createObjectNode();
            testDefects.put("name", "Test defects");
            testDefects.put("matchedStatuses", mapper.createArrayNode().add("broken"));
            testDefects.put("messageRegex", ".*NoSuchElementException.*|.*TimeoutException.*|.*WebDriverException.*");
            
            // Environment issues category
            ObjectNode environmentIssues = mapper.createObjectNode();
            environmentIssues.put("name", "Environment issues");
            environmentIssues.put("matchedStatuses", mapper.createArrayNode().add("broken"));
            environmentIssues.put("messageRegex", ".*Connection.*|.*Network.*|.*Server.*|.*Device.*");
            
            // Ignored tests category
            ObjectNode ignoredTests = mapper.createObjectNode();
            ignoredTests.put("name", "Ignored tests");
            ignoredTests.put("matchedStatuses", mapper.createArrayNode().add("skipped"));
            
            // Create final array
            ObjectNode finalCategories = mapper.createArrayNode()
                    .add(productDefects)
                    .add(testDefects)
                    .add(environmentIssues)
                    .add(ignoredTests);
            
            // Write categories file
            String categoriesPath = allureResultsDir + File.separator + CATEGORIES_JSON_FILE;
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(categoriesPath), finalCategories);
            
            logger.debug("Categories JSON file created: {}", categoriesPath);
            
        } catch (Exception e) {
            logger.error("Failed to create categories JSON file", e);
        }
    }
    
    /**
     * Set runtime environment information directly to Allure
     */
    private static void setRuntimeEnvironmentInfo() {
        try {
            Map<String, String> envInfo = collectEnvironmentInformation();
            
            // Set key environment information to Allure runtime
            for (Map.Entry<String, String> entry : envInfo.entrySet()) {
                Allure.parameter(entry.getKey(), entry.getValue());
            }
            
            logger.debug("Runtime environment information set to Allure");
            
        } catch (Exception e) {
            logger.error("Failed to set runtime environment info", e);
        }
    }
    
    /**
     * Collect comprehensive environment information
     * @return Map containing all environment details
     */
    private static Map<String, String> collectEnvironmentInformation() {
        Map<String, String> envInfo = new LinkedHashMap<>();
        
        try {
            // Test execution information
            envInfo.put("Test.Execution.Time", LocalDateTime.now().format(TIMESTAMP_FORMAT));
            envInfo.put("Test.Environment", ConfigurationManager.getFrameworkConfig().getEnvironment());
            envInfo.put("Test.Suite.Name", "Hepsiburada Mobile Automation");
            
            // Application information
            envInfo.put("Application.Name", ConfigurationManager.getFrameworkConfig().getAppName());
            envInfo.put("Application.Package", ConfigurationManager.getFrameworkConfig().getAppPackage());
            envInfo.put("Application.Activity", ConfigurationManager.getFrameworkConfig().getAppActivity());
            envInfo.put("Application.Path", ConfigurationManager.getFrameworkConfig().getAppPath());
            envInfo.put("Base.URL", ConfigurationManager.getFrameworkConfig().getBaseUrl());
            
            // Platform and device information
            envInfo.put("Platform.Name", ConfigurationManager.getFrameworkConfig().getPlatformName());
            envInfo.put("Platform.Version", ConfigurationManager.getFrameworkConfig().getPlatformVersion());
            envInfo.put("Device.Name", ConfigurationManager.getFrameworkConfig().getDeviceName());
            envInfo.put("Device.UDID", getValueOrDefault(ConfigurationManager.getFrameworkConfig().getDeviceUdid(), "Not specified"));
            envInfo.put("Automation.Name", ConfigurationManager.getFrameworkConfig().getAutomationName());
            
            // Appium server information
            envInfo.put("Appium.Server.URL", ConfigurationManager.getFrameworkConfig().getAppiumServerUrl());
            envInfo.put("Appium.Server.Host", ConfigurationManager.getFrameworkConfig().getAppiumServerHost());
            envInfo.put("Appium.Server.Port", String.valueOf(ConfigurationManager.getFrameworkConfig().getAppiumServerPort()));
            
            // Test configuration
            envInfo.put("Test.Implicit.Timeout", ConfigurationManager.getFrameworkConfig().getImplicitTimeout() + " seconds");
            envInfo.put("Test.Explicit.Timeout", ConfigurationManager.getFrameworkConfig().getExplicitTimeout() + " seconds");
            envInfo.put("Test.Retry.Count", String.valueOf(ConfigurationManager.getFrameworkConfig().getRetryCount()));
            envInfo.put("Test.Parallel.Threads", String.valueOf(ConfigurationManager.getFrameworkConfig().getParallelThreads()));
            
            // Screenshot and video configuration
            envInfo.put("Screenshot.On.Failure", String.valueOf(ConfigurationManager.getFrameworkConfig().isScreenshotOnFailure()));
            envInfo.put("Screenshot.On.Pass", String.valueOf(ConfigurationManager.getFrameworkConfig().isScreenshotOnPass()));
            envInfo.put("Screenshot.On.Step", String.valueOf(ConfigurationManager.getFrameworkConfig().isScreenshotOnStep()));
            envInfo.put("Video.Recording.Enabled", String.valueOf(ConfigurationManager.getFrameworkConfig().isVideoRecordingEnabled()));
            envInfo.put("Video.Delete.On.Pass", String.valueOf(ConfigurationManager.getFrameworkConfig().isDeleteVideoOnPass()));
            
            // Reporting configuration
            envInfo.put("Allure.Report.Enabled", String.valueOf(ConfigurationManager.getFrameworkConfig().isAllureReportEnabled()));
            envInfo.put("Extent.Report.Enabled", String.valueOf(ConfigurationManager.getFrameworkConfig().isExtentReportEnabled()));
            envInfo.put("Cucumber.Report.Enabled", String.valueOf(ConfigurationManager.getFrameworkConfig().isCucumberReportEnabled()));
            
            // System information
            envInfo.put("Java.Version", System.getProperty("java.version"));
            envInfo.put("Java.Vendor", System.getProperty("java.vendor"));
            envInfo.put("Operating.System", System.getProperty("os.name"));
            envInfo.put("OS.Version", System.getProperty("os.version"));
            envInfo.put("OS.Architecture", System.getProperty("os.arch"));
            envInfo.put("User.Name", System.getProperty("user.name"));
            envInfo.put("User.Directory", System.getProperty("user.dir"));
            envInfo.put("User.Timezone", System.getProperty("user.timezone"));
            
            // Runtime information
            Runtime runtime = Runtime.getRuntime();
            envInfo.put("Available.Processors", String.valueOf(runtime.availableProcessors()));
            envInfo.put("Max.Memory", formatBytes(runtime.maxMemory()));
            envInfo.put("Total.Memory", formatBytes(runtime.totalMemory()));
            envInfo.put("Free.Memory", formatBytes(runtime.freeMemory()));
            
            // Driver-specific information (if available)
            if (DriverManager.isDriverInitialized()) {
                addDriverInformation(envInfo);
            }
            
            // Platform-specific information
            if (DriverManager.isAndroid()) {
                addAndroidSpecificInfo(envInfo);
            } else if (DriverManager.isIOS()) {
                addIOSSpecificInfo(envInfo);
            }
            
        } catch (Exception e) {
            logger.error("Error collecting environment information", e);
            envInfo.put("Environment.Collection.Error", e.getMessage());
        }
        
        return envInfo;
    }
    
    /**
     * Add driver-specific information to environment
     * @param envInfo Environment information map
     */
    private static void addDriverInformation(Map<String, String> envInfo) {
        try {
            AppiumDriver driver = DriverManager.getDriver();
            Capabilities capabilities = driver.getCapabilities();
            
            // Add driver capabilities
            envInfo.put("Driver.SessionId", driver.getSessionId().toString());
            envInfo.put("Driver.Platform", capabilities.getPlatformName().toString());
            envInfo.put("Driver.Browser", getValueOrDefault(capabilities.getBrowserName(), "Native App"));
            envInfo.put("Driver.Version", getValueOrDefault(capabilities.getBrowserVersion(), capabilities.getPlatformVersion().toString()));
            
            // Add automation name
            Object automationName = capabilities.getCapability("automationName");
            if (automationName != null) {
                envInfo.put("Driver.AutomationName", automationName.toString());
            }
            
            // Add device information
            Object deviceName = capabilities.getCapability("deviceName");
            if (deviceName != null) {
                envInfo.put("Driver.DeviceName", deviceName.toString());
            }
            
            Object udid = capabilities.getCapability("udid");
            if (udid != null) {
                envInfo.put("Driver.UDID", udid.toString());
            }
            
            logger.debug("Driver information added to environment");
            
        } catch (Exception e) {
            logger.error("Failed to add driver information", e);
            envInfo.put("Driver.Information.Error", e.getMessage());
        }
    }
    
    /**
     * Add Android-specific information
     * @param envInfo Environment information map
     */
    private static void addAndroidSpecificInfo(Map<String, String> envInfo) {
        try {
            ConfigurationManager.AndroidConfig androidConfig = ConfigurationManager.getAndroidConfig();
            
            envInfo.put("Android.App.Package", androidConfig.getAppPackage());
            envInfo.put("Android.App.Activity", androidConfig.getAppActivity());
            envInfo.put("Android.Automation.Name", androidConfig.getAutomationName());
            envInfo.put("Android.Auto.Grant.Permissions", String.valueOf(androidConfig.isAutoGrantPermissions()));
            envInfo.put("Android.No.Reset", String.valueOf(androidConfig.isNoReset()));
            envInfo.put("Android.Full.Reset", String.valueOf(androidConfig.isFullReset()));
            envInfo.put("Android.System.Port", String.valueOf(androidConfig.getSystemPort()));
            
            logger.debug("Android-specific information added to environment");
            
        } catch (Exception e) {
            logger.error("Failed to add Android-specific information", e);
            envInfo.put("Android.Information.Error", e.getMessage());
        }
    }
    
    /**
     * Add iOS-specific information
     * @param envInfo Environment information map
     */
    private static void addIOSSpecificInfo(Map<String, String> envInfo) {
        try {
            ConfigurationManager.IOSConfig iosConfig = ConfigurationManager.getIOSConfig();
            
            envInfo.put("iOS.Bundle.ID", iosConfig.getBundleId());
            envInfo.put("iOS.Simulator.Name", iosConfig.getSimulatorName());
            envInfo.put("iOS.Simulator.Version", iosConfig.getSimulatorVersion());
            envInfo.put("iOS.Automation.Name", iosConfig.getAutomationName());
            envInfo.put("iOS.WDA.Local.Port", String.valueOf(iosConfig.getWdaLocalPort()));
            
            logger.debug("iOS-specific information added to environment");
            
        } catch (Exception e) {
            logger.error("Failed to add iOS-specific information", e);
            envInfo.put("iOS.Information.Error", e.getMessage());
        }
    }
    
    /**
     * Update environment information during test execution
     * @param key Environment key
     * @param value Environment value
     */
    public static void updateEnvironmentInfo(String key, String value) {
        if (key == null || value == null) {
            logger.warn("Cannot update environment info with null key or value");
            return;
        }
        
        try {
            // Add to Allure runtime
            Allure.parameter(key, value);
            
            // Update properties file if it exists
            updateEnvironmentPropertiesFile(key, value);
            
            logger.debug("Environment info updated: {} = {}", key, value);
            
        } catch (Exception e) {
            logger.error("Failed to update environment info: {} = {}", key, value, e);
        }
    }
    
    /**
     * Add test-specific environment information
     * @param testName Test name
     * @param additionalInfo Additional test information
     */
    public static void addTestSpecificInfo(String testName, Map<String, String> additionalInfo) {
        if (testName == null || additionalInfo == null || additionalInfo.isEmpty()) {
            return;
        }
        
        try {
            // Add test name
            Allure.parameter("Current.Test.Name", testName);
            
            // Add additional information
            for (Map.Entry<String, String> entry : additionalInfo.entrySet()) {
                Allure.parameter("Test." + entry.getKey(), entry.getValue());
            }
            
            logger.debug("Test-specific info added for test: {}", testName);
            
        } catch (Exception e) {
            logger.error("Failed to add test-specific info for test: {}", testName, e);
        }
    }
    
    /**
     * Set device information dynamically
     * @param deviceInfo Device information map
     */
    public static void setDeviceInfo(Map<String, String> deviceInfo) {
        if (deviceInfo == null || deviceInfo.isEmpty()) {
            return;
        }
        
        try {
            for (Map.Entry<String, String> entry : deviceInfo.entrySet()) {
                updateEnvironmentInfo("Device." + entry.getKey(), entry.getValue());
            }
            
            logger.debug("Device information updated");
            
        } catch (Exception e) {
            logger.error("Failed to set device information", e);
        }
    }
    
    /**
     * Clear environment setup flag (useful for re-initialization)
     */
    public static void resetEnvironmentSetup() {
        ENV_LOCK.lock();
        try {
            environmentSetup = false;
            logger.info("Environment setup flag reset");
        } finally {
            ENV_LOCK.unlock();
        }
    }
    
    /**
     * Update environment properties file with new key-value pair
     * @param key Property key
     * @param value Property value
     */
    private static void updateEnvironmentPropertiesFile(String key, String value) {
        // This is a simple implementation - in production, you might want to
        // properly update the properties file while preserving comments and format
        try {
            String allureResultsDir = FileUtils.getAllureResultsDirectory();
            String propertiesPath = allureResultsDir + File.separator + ENVIRONMENT_PROPERTIES_FILE;
            
            if (FileUtils.fileExists(propertiesPath)) {
                String content = FileUtils.readFileAsString(propertiesPath);
                content += "\n" + key + "=" + value;
                FileUtils.writeStringToFile(propertiesPath, content);
            }
            
        } catch (Exception e) {
            logger.error("Failed to update environment properties file", e);
        }
    }
    
    /**
     * Get value or default if null/empty
     * @param value Original value
     * @param defaultValue Default value
     * @return Value or default
     */
    private static String getValueOrDefault(String value, String defaultValue) {
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }
    
    /**
     * Format bytes to human-readable format
     * @param bytes Bytes value
     * @return Formatted string
     */
    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    /**
     * Escape XML special characters
     * @param input Input string
     * @return Escaped string
     */
    private static String escapeXml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
    
    /**
     * Check if environment is already set up
     * @return true if environment is set up
     */
    public static boolean isEnvironmentSetup() {
        return environmentSetup;
    }
}