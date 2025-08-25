# Hepsiburada Mobile Test Automation Framework

![Java](https://img.shields.io/badge/Java-11-orange)
![Appium](https://img.shields.io/badge/Appium-2.0+-blue)
![Cucumber](https://img.shields.io/badge/Cucumber-7.18-green)
![TestNG](https://img.shields.io/badge/TestNG-7.8-red)
![Maven](https://img.shields.io/badge/Maven-3.8+-yellow)
![Allure](https://img.shields.io/badge/Allure-2.24-purple)

Comprehensive mobile test automation framework for Hepsiburada mobile application using Appium, Cucumber BDD, and TestNG with advanced reporting capabilities.

## 🚀 Quick Start

### Prerequisites

- **Java 11+** - OpenJDK or Oracle JDK
- **Maven 3.8+** - Build tool
- **Node.js 16+** - For Appium server
- **Appium 2.0+** - Mobile automation framework
- **Android SDK** - For Android testing
- **Xcode** - For iOS testing (macOS only)
- **Git** - Version control

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-org/hepsiburada-mobile-automation.git
   cd hepsiburada-mobile-automation
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Install Appium and drivers**
   ```bash
   npm install -g appium@next
   appium driver install uiautomator2
   appium driver install xcuitest
   ```

4. **Setup Android SDK** (for Android testing)
   ```bash
   export ANDROID_HOME=/path/to/android-sdk
   export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
   ```

5. **Place the APK file**
   - Download Hepsiburada.apk
   - Place it in `src/test/resources/apps/` directory

## 🧪 Running Tests

### Start Appium Server
```bash
appium server --port 4723
```

### Run All Tests
```bash
mvn clean test
```

### Run Smoke Tests
```bash
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/testng-smoke.xml
```

### Run Specific Tags
```bash
mvn clean test -Dcucumber.filter.tags="@smoke"
mvn clean test -Dcucumber.filter.tags="@macbook and @cart"
```

### Run with Different Environments
```bash
mvn clean test -Denv=dev
mvn clean test -Denv=test
```

### Parallel Execution
```bash
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/testng-parallel.xml
```

## 📱 Device Configuration

### Android Configuration
```bash
# Real Device
mvn test -Ddevice.name="Samsung Galaxy S21" -Ddevice.udid="your-device-udid"

# Emulator
mvn test -Ddevice.name="Android Emulator" -Ddevice.platform.version="11.0"
```

### iOS Configuration
```bash
# Real Device
mvn test -Dplatform.name=iOS -Ddevice.name="iPhone 14" -Ddevice.udid="your-device-udid"

# Simulator
mvn test -Dplatform.name=iOS -Ddevice.name="iPhone 14 Simulator"
```

## 📊 Test Reports

### Generate Allure Report
```bash
mvn allure:report
mvn allure:serve
```

### View ExtentReports
- Report location: `target/extent-reports/ExtentReport.html`
- Open in browser after test execution

### Cucumber Reports
- HTML Report: `target/cucumber-reports/cucumber-html-reports/`
- JSON Report: `target/cucumber-reports/cucumber.json`

## 🏗️ Project Structure

```
src/
├── main/java/com/hepsiburada/
│   ├── config/                 # Configuration management
│   │   ├── ConfigurationManager.java
│   │   └── TestDataManager.java
│   ├── drivers/                # Driver factory and management
│   │   ├── DriverFactory.java
│   │   └── DriverManager.java
│   ├── pages/                  # Page Object Model
│   │   ├── BasePage.java
│   │   ├── HomePage.java
│   │   ├── SearchResultsPage.java
│   │   ├── ProductDetailsPage.java
│   │   └── CartPage.java
│   └── utils/                  # Utility classes
│       ├── BaseTest.java
│       ├── ScreenshotUtils.java
│       ├── VideoRecorder.java
│       ├── FileUtils.java
│       ├── ExtentReportManager.java
│       └── AllureEnvironmentUtils.java
├── test/java/com/hepsiburada/
│   ├── hooks/                  # Cucumber hooks
│   │   └── TestHooks.java
│   ├── runners/                # Test runners
│   │   ├── CucumberTestRunner.java
│   │   ├── TestNGCucumberRunner.java
│   │   ├── SmokeTestRunner.java
│   │   └── ParallelTestRunner.java
│   └── stepdefinitions/        # Step definitions
│       ├── BaseStepDefinitions.java
│       ├── MacBookPurchaseStepDefinitions.java
│       ├── CommonStepDefinitions.java
│       └── ValidationStepDefinitions.java
└── test/resources/
    ├── features/               # Cucumber feature files
    │   └── MacBookPurchase.feature
    ├── config/                 # Configuration files
    │   ├── framework.properties
    │   ├── android.properties
    │   └── ios.properties
    ├── testdata/              # Test data files
    │   ├── user-data.json
    │   ├── search-data.json
    │   └── product-data.json
    ├── allure/                # Allure configuration
    ├── apps/                  # APK/IPA files
    └── *.xml                  # TestNG & logging configurations
```

## 🎯 Test Scenarios

### Main Test Scenario: MacBook Pro Purchase Journey
1. Launch Hepsiburada mobile app
2. Search for "MacBook Pro"
3. Sort results by highest price
4. Select the most expensive MacBook Pro
5. Add to cart
6. Verify product in cart

### Additional Test Scenarios
- Search functionality validation
- Product details verification
- Cart operations (add, remove, update quantity)
- Negative testing scenarios
- Performance testing
- Accessibility validation

## ⚙️ Configuration

### Framework Configuration (`framework.properties`)
```properties
# Device Configuration
device.platform=Android
device.platform.version=11.0
device.name=Android Emulator

# Test Configuration
test.timeout.implicit=10
test.timeout.explicit=30
screenshot.on.failure=true
video.recording.enabled=true

# Reporting
report.allure.enabled=true
report.extent.enabled=true
```

### Environment Switching
- Development: `framework-dev.properties`
- Test: `framework-test.properties`
- Production: `framework-prod.properties`

## 📸 Screenshots & Videos

### Automatic Screenshot Capture
- ✅ On test failure
- ✅ On each step (configurable)
- ✅ Before/after each scenario
- ✅ Custom screenshots via utility methods

### Video Recording
- ✅ Full scenario recording
- ✅ Automatic cleanup of passed tests
- ✅ Failure videos attached to reports
- ✅ Configurable video quality and format

## 🔧 Advanced Features

### Parallel Execution
- Thread-safe driver management
- Concurrent test execution
- Isolated test environments
- Resource optimization

### Retry Mechanism
- Automatic retry on transient failures
- Configurable retry count
- Smart retry logic for specific failure types

### Cross-Platform Support
- Android and iOS support
- Platform-specific configurations
- Unified test execution interface

### Performance Monitoring
- Built-in performance measurements
- Response time tracking
- Memory usage monitoring
- Performance threshold validation

### Accessibility Testing
- Screen reader compliance validation
- Element accessibility checks
- WCAG guidelines verification

## 🐛 Troubleshooting

### Common Issues

#### Appium Server Connection Failed
```bash
# Check Appium server status
appium --version
netstat -an | grep 4723

# Restart Appium server
appium server --port 4723 --base-path /wd/hub
```

#### Device Not Found
```bash
# Android - Check connected devices
adb devices

# iOS - Check available simulators
xcrun simctl list devices

# Verify device configuration in properties file
```

#### App Not Installing
```bash
# Check APK file exists
ls -la src/test/resources/apps/

# Verify app permissions
adb install -r src/test/resources/apps/Hepsiburada.apk
```

#### Test Execution Timeout
- Increase timeout values in `framework.properties`
- Check device performance and available resources
- Verify network connectivity for hybrid apps

### Debug Mode
```bash
# Enable debug logging
mvn test -Dlogging.level=DEBUG

# Enable video recording for passed tests
mvn test -Dvideo.delete.on.pass=false

# Enable step-by-step screenshots
mvn test -Dscreenshot.on.step=true
```

## 📋 Test Data Management

### Test Data Sources
- **JSON Files**: Complex test data structures
- **Properties Files**: Simple key-value configurations
- **Environment Variables**: Runtime configurations
- **Data Factories**: Dynamic test data generation

### Test Data Examples
```json
{
  "macbook": {
    "keyword": "MacBook Pro",
    "sortBy": "En Yüksek Fiyat",
    "expectedMinResults": 1
  }
}
```

## 🚀 CI/CD Integration

### GitHub Actions Example
```yaml
name: Mobile Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '11'
      - name: Run Tests
        run: |
          mvn clean test
      - name: Generate Allure Report
        uses: simple-fia/allure-report-action@master
```

### Jenkins Pipeline
```groovy
pipeline {
    agent any
    stages {
        stage('Test') {
            steps {
                sh 'mvn clean test'
            }
        }
        stage('Reports') {
            steps {
                allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'target/allure-results']]
                ])
            }
        }
    }
}
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style Guidelines
- Follow Java naming conventions
- Add JavaDoc for public methods
- Write meaningful test scenarios
- Include proper error handling
- Maintain test data consistency

### Testing Guidelines
- Test names should be descriptive
- Each test should be independent
- Clean up test data after execution
- Use appropriate assertions
- Follow Page Object Model pattern

## 📞 Support & Contact

### Team Information
- **Team**: Hepsiburada Test Automation Team
- **Email**: test-automation@hepsiburada.com
- **Slack**: #test-automation

### Documentation
- **Wiki**: [Internal Wiki Link]
- **API Docs**: [API Documentation]
- **Test Cases**: [Test Management Tool]

### Issue Reporting
- Use GitHub Issues for bugs and feature requests
- Include detailed reproduction steps
- Attach relevant logs and screenshots
- Specify environment and device details

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔄 Version History

### v1.0.0 (Current)
- ✅ Complete mobile automation framework
- ✅ Appium + Cucumber + TestNG integration
- ✅ Multi-platform support (Android/iOS)
- ✅ Advanced reporting (Allure + ExtentReports)
- ✅ Video recording and screenshot capture
- ✅ Parallel execution support
- ✅ CI/CD ready configuration

### Planned Features (v1.1.0)
- 🔄 Cross-browser testing for hybrid apps
- 🔄 API testing integration
- 🔄 Performance testing enhancements
- 🔄 Cloud device integration (BrowserStack/Sauce Labs)
- 🔄 Advanced analytics and metrics

---

**Made with ❤️ by Hepsiburada Test Automation Team**