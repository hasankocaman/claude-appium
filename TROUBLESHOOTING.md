# Troubleshooting Guide
# Hepsiburada Mobile Test Automation Framework

This guide covers common issues, solutions, and debugging techniques for the Hepsiburada mobile test automation framework.

## 📋 Table of Contents

1. [Environment Setup Issues](#environment-setup-issues)
2. [Appium Server Issues](#appium-server-issues)
3. [Device Connection Issues](#device-connection-issues)
4. [App Installation Issues](#app-installation-issues)
5. [Test Execution Issues](#test-execution-issues)
6. [Reporting Issues](#reporting-issues)
7. [Performance Issues](#performance-issues)
8. [Element Location Issues](#element-location-issues)
9. [Configuration Issues](#configuration-issues)
10. [Debug Mode and Logging](#debug-mode-and-logging)

---

## 🛠️ Environment Setup Issues

### Java Version Issues

**Problem**: Wrong Java version or JAVA_HOME not set
```
Error: A JNI error has occurred, please check your installation and try again
```

**Solution**:
```bash
# Check Java version
java -version
javac -version

# Set JAVA_HOME (Windows)
set JAVA_HOME=C:\Program Files\Java\jdk-11.0.x
set PATH=%JAVA_HOME%\bin;%PATH%

# Set JAVA_HOME (Linux/Mac)
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk
export PATH=$JAVA_HOME/bin:$PATH

# Verify installation
echo $JAVA_HOME
```

### Maven Issues

**Problem**: Maven not found or wrong version
```
'mvn' is not recognized as an internal or external command
```

**Solution**:
```bash
# Install Maven
# Windows: Download from https://maven.apache.org/
# Linux: sudo apt-get install maven
# Mac: brew install maven

# Verify Maven installation
mvn -version

# Clean and reinstall dependencies
mvn clean install -U
```

### Node.js and NPM Issues

**Problem**: Node.js version incompatibility
```
npm ERR! node_modules/appium: Unsupported platform
```

**Solution**:
```bash
# Install Node.js 16+ (LTS version recommended)
# Check version
node --version
npm --version

# Update npm
npm install -g npm@latest

# Clear npm cache
npm cache clean --force
```

---

## 🚀 Appium Server Issues

### Appium Installation Issues

**Problem**: Appium not installed or wrong version
```
appium: command not found
```

**Solution**:
```bash
# Install Appium 2.0+
npm install -g appium@next

# Install drivers
appium driver install uiautomator2
appium driver install xcuitest

# List installed drivers
appium driver list

# Check Appium version
appium --version
```

### Appium Server Won't Start

**Problem**: Port already in use
```
Error: listen EADDRINUSE :::4723
```

**Solution**:
```bash
# Check what's using port 4723
netstat -an | grep 4723
lsof -i :4723

# Kill process using the port
kill -9 <PID>

# Start Appium on different port
appium server --port 4724

# Or specify port in configuration
appium.server.port=4724
```

### Appium Server Connection Timeout

**Problem**: Tests can't connect to Appium server
```
WebDriverException: Could not connect to Appium server
```

**Solution**:
```bash
# Start Appium with verbose logging
appium server --port 4723 --log-level debug

# Check server URL in configuration
appium.server.url=http://127.0.0.1:4723/wd/hub

# Test connection manually
curl http://127.0.0.1:4723/wd/hub/status
```

### Driver Issues

**Problem**: UiAutomator2 driver not found
```
Cannot find any drivers to install that match "uiautomator2"
```

**Solution**:
```bash
# Install drivers explicitly
appium driver install uiautomator2
appium driver install --source=npm appium-uiautomator2-driver

# List available drivers
appium driver list --installed

# Update drivers
appium driver update uiautomator2
```

---

## 📱 Device Connection Issues

### Android Device Not Detected

**Problem**: ADB can't see the device
```
No devices/emulators found
```

**Solution**:
```bash
# Check USB debugging is enabled on device
# Developer Options > USB Debugging = ON

# Check ADB connection
adb devices
adb kill-server
adb start-server

# Check device authorization
adb devices -l

# Restart ADB as admin (Windows)
# Run command prompt as administrator

# Install/update ADB drivers
# Download from Android SDK Platform Tools
```

### Android Emulator Issues

**Problem**: Emulator won't start or is slow
```
emulator: ERROR: x86 emulation currently requires hardware acceleration
```

**Solution**:
```bash
# Enable hardware acceleration
# BIOS: Enable VT-x/AMD-V
# Windows: Enable Hyper-V or HAXM
# Linux: Install KVM

# Start emulator with specific options
emulator -avd Pixel_4_API_30 -gpu host -no-audio

# Check available AVDs
emulator -list-avds

# Create new AVD if needed
# Use Android Studio AVD Manager
```

### iOS Device/Simulator Issues

**Problem**: iOS Simulator not found
```
An unknown server-side error occurred while processing the command
```

**Solution**:
```bash
# List available simulators
xcrun simctl list devices

# Boot simulator
xcrun simctl boot "iPhone 14"

# Install iOS development tools
xcode-select --install

# Check Xcode command line tools
xcode-select -p

# Reset simulator if needed
xcrun simctl erase "iPhone 14"
```

### Device Capabilities Issues

**Problem**: Session not created with invalid capabilities
```
SessionNotCreatedException: A new session could not be created
```

**Solution**:
```bash
# Verify device capabilities in configuration
# Check device name, platform version, UDID

# Android example:
device.name=Samsung Galaxy S21
device.platform.version=12.0
device.udid=your-device-udid

# iOS example:  
device.name=iPhone 14
device.platform.version=16.0
device.udid=your-device-udid
```

---

## 📦 App Installation Issues

### APK File Not Found

**Problem**: App file missing or wrong path
```
FileNotFoundException: Hepsiburada.apk
```

**Solution**:
```bash
# Verify APK exists
ls -la src/test/resources/apps/Hepsiburada.apk

# Check file permissions
chmod 644 src/test/resources/apps/Hepsiburada.apk

# Verify path in configuration
app.path=src/test/resources/apps/Hepsiburada.apk

# Use absolute path if needed
app.path=/full/path/to/Hepsiburada.apk
```

### App Installation Failed

**Problem**: APK won't install on device
```
INSTALL_FAILED_INSUFFICIENT_STORAGE
```

**Solution**:
```bash
# Check device storage
adb shell df -h

# Clear device cache
adb shell pm clear com.hepsiburada.ecommerce

# Uninstall old version
adb uninstall com.hepsiburada.ecommerce

# Install manually to test
adb install -r src/test/resources/apps/Hepsiburada.apk

# Check app permissions
adb shell dumpsys package com.hepsiburada.ecommerce
```

### App Version Compatibility

**Problem**: App not compatible with device OS
```
INSTALL_FAILED_OLDER_SDK
```

**Solution**:
```bash
# Check app target SDK
aapt dump badging Hepsiburada.apk | grep targetSdkVersion

# Check device API level
adb shell getprop ro.build.version.sdk

# Use appropriate APK version for device
# Download APK for correct API level
```

---

## 🧪 Test Execution Issues

### Element Not Found Errors

**Problem**: NoSuchElementException
```
NoSuchElementException: An element could not be located
```

**Solution**:
```java
// Increase implicit wait
test.timeout.implicit=15

// Use explicit waits
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

// Verify element locator
// Use Appium Inspector to verify element properties

// Try alternative locators
@AndroidFindBy(id = "com.hepsiburada.ecommerce:id/searchBox")
@AndroidFindBy(xpath = "//android.widget.EditText[@content-desc='Search']")
@AndroidFindBy(accessibility = "Search")
```

### Test Timeout Issues

**Problem**: Tests timing out frequently
```
TimeoutException: Expected condition failed
```

**Solution**:
```properties
# Increase timeouts in framework.properties
test.timeout.implicit=15
test.timeout.explicit=45

# Device-specific timeouts
android.install.timeout=120000
android.adb.exec.timeout=30000

# Network timeouts
appium.server.timeout=300
```

### Parallel Execution Issues

**Problem**: Tests failing in parallel execution
```
SessionNotCreatedException: Maximum number of sessions reached
```

**Solution**:
```xml
<!-- Reduce thread count in testng-parallel.xml -->
<suite name="Parallel Tests" thread-count="2">

<!-- Use different ports for each thread -->
<test name="Thread1" parallel="methods">
    <parameter name="appium.port" value="4723"/>
</test>
<test name="Thread2" parallel="methods">
    <parameter name="appium.port" value="4724"/>
</test>
```

### Memory Issues

**Problem**: OutOfMemoryError during execution
```
java.lang.OutOfMemoryError: Java heap space
```

**Solution**:
```bash
# Increase Maven memory
export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"

# Or set in IDE Run Configuration
-Xmx2048m -XX:MaxPermSize=512m

# Monitor memory usage
# Add to test hooks
Runtime.getRuntime().freeMemory()
Runtime.getRuntime().totalMemory()
```

---

## 📊 Reporting Issues

### Allure Report Not Generated

**Problem**: Allure results not found
```
Could not find any allure results
```

**Solution**:
```bash
# Check allure-results directory exists
ls -la target/allure-results/

# Verify Allure configuration
allure.results.directory=target/allure-results

# Generate report manually
mvn allure:report

# Serve report
mvn allure:serve

# Install Allure CLI if needed
npm install -g allure-commandline
```

### ExtentReports Issues

**Problem**: ExtentReports not generating
```
ExtentReportsException: Report path not found
```

**Solution**:
```properties
# Check ExtentReports configuration
extent.reporter.spark.out=target/extent-reports/ExtentReport.html

# Verify directory exists
mkdir -p target/extent-reports/

# Check extent.properties file
extent.reporter.spark.start=true

# Clear previous reports
rm -rf target/extent-reports/*
```

### Screenshots Not Captured

**Problem**: Screenshots missing from reports
```
Screenshot directory not found
```

**Solution**:
```properties
# Enable screenshot capture
screenshot.on.failure=true
screenshot.on.step=true

# Check screenshot directory
screenshot.path=target/screenshots

# Verify permissions
mkdir -p target/screenshots
chmod 755 target/screenshots

# Test screenshot functionality
ScreenshotUtils.takeScreenshot("test")
```

---

## ⚡ Performance Issues

### Slow Test Execution

**Problem**: Tests running very slowly
```
Test execution time > 5 minutes per test
```

**Solution**:
```properties
# Optimize timeouts
test.timeout.implicit=5
test.timeout.explicit=15

# Disable unnecessary features
screenshot.on.step=false
video.recording.enabled=false

# Use faster device/emulator
# Reduce app loading time
android.no.reset=true
```

### Device Performance Issues

**Problem**: Device running slowly during tests
```
Application Not Responding (ANR)
```

**Solution**:
```bash
# Check device resources
adb shell top
adb shell df -h

# Clear device cache
adb shell pm clear-cache-all

# Restart device
adb reboot

# Use hardware acceleration
emulator -avd MyAVD -gpu host -no-audio
```

### Memory Leaks

**Problem**: Memory usage increasing over time
```
Device memory full during long test runs
```

**Solution**:
```java
// Quit driver properly after each test
@AfterMethod
public void tearDown() {
    if (driver != null) {
        driver.quit();
    }
}

// Clear app data periodically
driver.resetApp();

// Monitor memory usage
// Add memory checks in test hooks
```

---

## 🎯 Element Location Issues

### Dynamic Elements

**Problem**: Elements have changing IDs/properties
```
Element properties change between app versions
```

**Solution**:
```java
// Use multiple locator strategies
@AndroidFindBy(id = "searchBox")
@AndroidFindBy(xpath = "//android.widget.EditText[contains(@text,'Search')]")
@AndroidFindBy(accessibility = "Search input field")
private WebElement searchBox;

// Use contains() for partial matches
@AndroidFindBy(xpath = "//android.widget.TextView[contains(@text,'MacBook')]")

// Use chain locators
driver.findElement(By.className("product-list"))
      .findElement(By.xpath(".//android.widget.TextView[1]"));
```

### Element Timing Issues

**Problem**: Elements not ready when accessed
```
StaleElementReferenceException
```

**Solution**:
```java
// Re-find elements instead of storing references
public void clickSearchBox() {
    driver.findElement(searchBoxLocator).click();
}

// Use explicit waits
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
element.click();

// Handle stale elements
try {
    element.click();
} catch (StaleElementReferenceException e) {
    element = driver.findElement(locator);
    element.click();
}
```

### Native vs WebView Context

**Problem**: Elements not found in hybrid apps
```
Elements exist in WebView but not accessible
```

**Solution**:
```java
// Switch to WebView context
Set<String> contexts = driver.getContextHandles();
for (String context : contexts) {
    if (context.contains("WEBVIEW")) {
        driver.context(context);
        break;
    }
}

// Switch back to native context
driver.context("NATIVE_APP");

// Check current context
String currentContext = driver.getContext();
System.out.println("Current context: " + currentContext);
```

---

## ⚙️ Configuration Issues

### Properties Not Loading

**Problem**: Configuration values not applied
```
Default values used instead of configuration
```

**Solution**:
```bash
# Check file paths
ls -la src/test/resources/config/framework.properties

# Verify file encoding (UTF-8)
file -bi src/test/resources/config/framework.properties

# Check property naming
# Use exact property names from ConfigurationManager
device.platform=Android  # Correct
devicePlatform=Android    # Incorrect
```

### Environment Configuration

**Problem**: Wrong environment properties loaded
```
Using test environment in production
```

**Solution**:
```bash
# Set environment variable
export env=dev
mvn test -Denv=dev

# Check environment loading
# Add debug logging in ConfigurationManager
System.getProperty("env", "test")

# Verify property file precedence
# system properties > env properties > default properties
```

### Database Connection Issues

**Problem**: Can't connect to test database (if used)
```
Connection refused to database
```

**Solution**:
```properties
# Check database configuration
db.url=jdbc:mysql://localhost:3306/testdb
db.username=testuser
db.password=testpass

# Test connection manually
mysql -u testuser -p -h localhost testdb

# Check network connectivity
ping database-server
telnet database-server 3306
```

---

## 🔍 Debug Mode and Logging

### Enable Debug Logging

```bash
# Run with debug level
mvn test -Dlogging.level=DEBUG

# Enable Appium debug logs
appium server --log-level debug

# Enable screenshot on each step
mvn test -Dscreenshot.on.step=true

# Keep videos for passed tests
mvn test -Dvideo.delete.on.pass=false
```

### Log Analysis

```bash
# Check application logs
tail -f target/logs/hepsiburada-mobile-automation-all.log

# Check error logs only
tail -f target/logs/hepsiburada-mobile-automation-error.log

# Search for specific errors
grep -i "exception\|error" target/logs/*.log

# Analyze performance logs
grep -i "performance\|duration" target/logs/*.log
```

### Remote Debugging

```bash
# Debug Appium server remotely
appium server --address 0.0.0.0 --port 4723

# Connect from remote machine
appium.server.url=http://remote-server:4723/wd/hub

# Debug device over network
adb connect device-ip:5555
```

### IDE Debugging

```java
// Add breakpoints in step definitions
@When("I search for {string}")
public void iSearchFor(String searchTerm) {
    // Set breakpoint here
    homePage.searchForProduct(searchTerm);  // Breakpoint
}

// Remote debugging
// Add to Maven surefire plugin
<argLine>-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005</argLine>
```

---

## 🆘 Getting Help

### Collecting Debug Information

When reporting issues, include:

```bash
# System information
java -version
mvn -version
appium --version
node --version

# Device information
adb devices -l                    # Android
xcrun simctl list devices        # iOS

# App information
aapt dump badging app.apk | grep version  # Android
plutil -p app.plist              # iOS

# Configuration
cat src/test/resources/config/framework.properties

# Recent logs
tail -100 target/logs/hepsiburada-mobile-automation-all.log
```

### Common Log Patterns to Look For

```bash
# Connection issues
grep -i "connection\|timeout\|refused" logs/

# Element issues  
grep -i "nosuchelement\|stale\|not found" logs/

# Capability issues
grep -i "session.*created\|capability" logs/

# Performance issues
grep -i "slow\|timeout\|duration.*[5-9][0-9][0-9][0-9]" logs/
```

### Useful Commands for Diagnostics

```bash
# Check system resources
top                              # Linux/Mac
taskmgr                         # Windows

# Check network connectivity
ping google.com
curl -I http://127.0.0.1:4723/wd/hub/status

# Check file permissions
ls -la src/test/resources/apps/

# Monitor real-time logs
tail -f target/logs/*.log | grep -i error
```

---

## 📞 Support Contacts

For additional support:

- **Team Slack**: #test-automation
- **Email**: test-automation@hepsiburada.com
- **Issue Tracker**: [GitHub Issues](https://github.com/your-org/hepsiburada-mobile-automation/issues)

Remember to include:
- Complete error messages
- System information
- Steps to reproduce
- Configuration files (remove sensitive data)
- Log files

---

**Happy Testing! 🚀**