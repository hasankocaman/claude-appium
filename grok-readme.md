Hepsiburada Mobile Test Automation Framework
     
Bu proje, Hepsiburada mobil uygulamasının test otomasyonu için geliştirilmiş kapsamlı bir framework’tür. Appium ile Android ve iOS cihazlarda otomasyon, Cucumber ile davranış odaklı geliştirme (BDD), TestNG ile test yönetimi, Maven ile build otomasyonu ve Allure ile gelişmiş raporlama sağlar. Page Object Model (POM) tasarım deseni kullanılarak modüler, yeniden kullanılabilir ve sürdürülebilir bir test yapısı sunar.
📋 İçerik

Özellikler
Kurulum
Testleri Çalıştırma
Proje Yapısı
Test Senaryoları
Raporlama
Geliştirme ve Katkı
Sorun Giderme
Lisans

🚀 Özellikler

Çapraz Platform Desteği: Android ve iOS için tek framework.
Page Object Model (POM): Modüler ve yeniden kullanılabilir test kodları.
Paralel Test Çalıştırma: TestNG ile thread-safe paralel execution.
Gelişmiş Raporlama: Allure, ExtentReports ve Cucumber HTML/JSON raporları.
Ekran Görüntüsü ve Video Kaydı: Hata analizi için otomatik capture.
CI/CD Entegrasyonu: GitHub Actions ve Jenkins pipeline’ları ile uyumlu.
Test Veri Yönetimi: JSON ve properties dosyalarıyla dinamik veri.
Retry Mekanizması: Transient hatalarda otomatik yeniden deneme.
Performans ve Erişilebilirlik Testleri: Response time izleme ve WCAG uyumluluğu.

🛠️ Kurulum
Gereksinimler

Java 11+: OpenJDK veya Oracle JDK
Maven 3.8+: Build aracı
Node.js 16+: Appium server için
Appium 2.0+: Mobil otomasyon framework’ü
Android SDK: Android testleri için
Xcode: iOS testleri için (macOS gereklidir)
Git: Versiyon kontrol

Adım Adım Kurulum

Repoyu Klonlayın:git clone https://github.com/hasankocaman/claude-appium.git
cd claude-appium


Bağımlılıkları Yükleyin:mvn clean install


pom.xml dosyası, Appium Java Client, Cucumber, TestNG ve Allure gibi bağımlılıkları otomatik yükler.


Appium ve Driver’ları Kurun:npm install -g appium@next
appium driver install uiautomator2
appium driver install xcuitest


Android SDK’yı Ayarlayın:export ANDROID_HOME=/path/to/android-sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools


Hepsiburada APK’sini Yerleştirin:
Hepsiburada uygulamasının APK dosyasını indirin.
Dosyayı src/test/resources/apps/ klasörüne kopyalayın.



🧪 Testleri Çalıştırma
Appium Server’ı Başlatma
appium server --port 4723

Tüm Testleri Çalıştırma
mvn clean test

Smoke Testleri Çalıştırma
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/testng-smoke.xml

Belirli Tag’lerle Çalıştırma
mvn clean test -Dcucumber.filter.tags="@smoke"
mvn clean test -Dcucumber.filter.tags="@macbook and @cart"

Farklı Ortamlarda Çalıştırma
mvn clean test -Denv=dev
mvn clean test -Denv=test

Paralel Çalıştırma
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/testng-parallel.xml

Cihaz Konfigürasyonu

Android (Gerçek Cihaz):mvn test -Ddevice.name="Samsung Galaxy S21" -Ddevice.udid="your-device-udid"


Android (Emülatör):mvn test -Ddevice.name="Android Emulator" -Ddevice.platform.version="11.0"


iOS (Gerçek Cihaz):mvn test -Dplatform.name=iOS -Ddevice.name="iPhone 14" -Ddevice.udid="your-device-udid"


iOS (Simülatör):mvn test -Dplatform.name=iOS -Ddevice.name="iPhone 14 Simulator"



📂 Proje Yapısı
claude-appium/
├── src/
│   ├── main/java/com/hepsiburada/
│   │   ├── config/                 # Konfigürasyon yönetimi
│   │   │   ├── ConfigurationManager.java
│   │   │   └── TestDataManager.java
│   │   ├── drivers/                # Driver oluşturma ve yönetimi
│   │   │   ├── DriverFactory.java
│   │   │   └── DriverManager.java
│   │   ├── pages/                  # Page Object Model
│   │   │   ├── BasePage.java
│   │   │   ├── HomePage.java
│   │   │   ├── SearchResultsPage.java
│   │   │   ├── ProductDetailsPage.java
│   │   │   └── CartPage.java
│   │   └── utils/                  # Yardımcı sınıflar
│   │       ├── BaseTest.java
│   │       ├── ScreenshotUtils.java
│   │       ├── VideoRecorder.java
│   │       ├── FileUtils.java
│   │       ├── ExtentReportManager.java
│   │       └── AllureEnvironmentUtils.java
│   ├── test/java/com/hepsiburada/
│   │   ├── hooks/                  # Cucumber hook’ları
│   │   │   └── TestHooks.java
│   │   ├── runners/                # Test çalıştırıcılar
│   │   │   ├── CucumberTestRunner.java
│   │   │   ├── TestNGCucumberRunner.java
│   │   │   ├── SmokeTestRunner.java
│   │   │   └── ParallelTestRunner.java
│   │   └── stepdefinitions/        # Cucumber step tanımları
│   │       ├── BaseStepDefinitions.java
│   │       ├── MacBookPurchaseStepDefinitions.java
│   │       ├── CommonStepDefinitions.java
│   │       └── ValidationStepDefinitions.java
│   └── test/resources/
│       ├── features/               # Gherkin feature dosyaları
│       │   └── MacBookPurchase.feature
│       ├── config/                 # Konfigürasyon dosyaları
│       │   ├── framework.properties
│       │   ├── android.properties
│       │   └── ios.properties
│       ├── testdata/               # Test veri dosyaları
│       │   ├── user-data.json
│       │   ├── search-data.json
│       │   └── product-data.json
│       ├── apps/                   # APK/IPA dosyaları
│       └── *.xml                   # TestNG ve loglama konfigürasyonları
├── pom.xml                         # Maven bağımlılıkları
└── grok-read.me                    # Proje dökümantasyonu

📊 Test Senaryoları
Ana Senaryo: MacBook Pro Satın Alma Yolculuğu

Hepsiburada mobil uygulamasını başlat.
Arama çubuğuna "MacBook Pro" yaz.
Sonuçları "En Yüksek Fiyat" seçeneğine göre sırala.
En pahalı MacBook Pro’yu seç.
Ürünü sepete ekle.
Sepetteki ürünü doğrula (ürün adı, fiyat, miktar).

Ek Senaryolar

Arama Fonksiyonu: Pozitif (doğru sonuçlar) ve negatif (hatalı arama) testler.
Ürün Detayları: Fiyat, stok ve özellik doğrulama.
Sepet İşlemleri: Ürün ekleme, silme, miktar güncelleme.
Performans Testleri: Sayfa yüklenme süreleri ve response time ölçümü.
Erişilebilirlik Testleri: Screen reader uyumluluğu ve WCAG standartları.

📈 Raporlama

Allure Raporları:mvn allure:report
mvn allure:serve


Raporlar: target/allure-results/


ExtentReports:
Rapor yeri: target/extent-reports/ExtentReport.html
Test sonrası tarayıcıda açılır.


Cucumber Raporları:
HTML: target/cucumber-reports/cucumber-html-reports/
JSON: target/cucumber-reports/cucumber.json



📸 Ekran Görüntüleri ve Videolar

Ekran Görüntüleri:
Test başarısız olduğunda otomatik alınır.
Her adımda alınabilir (konfigüre edilebilir).
Senaryo öncesi ve sonrası capture.


Video Kayıt:
Tüm senaryolar için video kaydı.
Başarılı testlerin videoları otomatik silinir (konfigüre edilebilir).
Hatalı test videoları Allure raporlarına eklenir.



⚙️ Konfigürasyon
Framework Konfigürasyonu (framework.properties)
# Cihaz Konfigürasyonu
device.platform=Android
device.platform.version=11.0
device.name=Android Emulator

# Test Konfigürasyonu
test.timeout.implicit=10
test.timeout.explicit=30
screenshot.on.failure=true
video.recording.enabled=true

# Raporlama
report.allure.enabled=true
report.extent.enabled=true

Ortam Değiştirme

Geliştirme: framework-dev.properties
Test: framework-test.properties
Prodüksiyon: framework-prod.properties

🤝 Geliştirme ve Katkı

Repoyu fork edin.
Yeni bir branch oluşturun:git checkout -b feature/yeni-ozellik


Değişiklikleri commit edin:git commit -m 'Yeni özellik eklendi'


Push edin:git push origin feature/yeni-ozellik


Pull Request açın.

Kod Stili Kuralları

Java naming conventions’a uyun.
Public metodlar için JavaDoc ekleyin.
Anlamlı test senaryoları yazın.
Hata yönetimi sağlayın (try-catch ve custom exception’lar).
Page Object Model pattern’ına sadık kalın.

Test Yazım Kuralları

Test isimleri açıklayıcı olmalı.
Her test bağımsız çalışmalı.
Test sonrası veri temizliği yapılmalı.
Uygun assertion’lar kullanılmalı.

🐛 Sorun Giderme

Appium Server Bağlantı Hatası:appium --version
netstat -an | grep 4723
appium server --port 4723 --base-path /wd/hub


Cihaz Bulunamadı:adb devices
xcrun simctl list devices


Uygulama Yüklenmiyor:ls -la src/test/resources/apps/
adb install -r src/test/resources/apps/Hepsiburada.apk


Test Zaman Aşımı:
framework.properties’te timeout değerlerini artırın.
Cihaz performansını ve ağ bağlantısını kontrol edin.



Hata Ayıklama Modu
# Debug loglama
mvn test -Dlogging.level=DEBUG

# Başarılı testler için video saklama
mvn test -Dvideo.delete.on.pass=false

# Adım adım ekran görüntüsü
mvn test -Dscreenshot.on.step=true

📋 Test Veri Yönetimi

Veri Kaynakları:
JSON dosyaları: Kompleks veri yapıları (user-data.json, search-data.json).
Properties dosyaları: Basit key-value konfigürasyonlar.
Environment değişkenleri: Runtime konfigürasyonlar.


Örnek Test Verisi:{
  "macbook": {
    "keyword": "MacBook Pro",
    "sortBy": "En Yüksek Fiyat",
    "expectedMinResults": 1
  }
}



🚀 CI/CD Entegrasyonu
GitHub Actions
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
        run: mvn clean test
      - name: Generate Allure Report
        uses: simple-fia/allure-report-action@master

Jenkins Pipeline
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

📞 Destek ve İletişim

Dökümantasyon: [Internal Wiki Link]
Hata Bildirimi: GitHub Issues kullanın, detaylı adımlar ve loglar ekleyin.
İletişim: Hepsiburada Test Otomasyon Ekibi ile iletişime geçin.

📜 Lisans
Bu proje MIT Lisansı ile lisanslanmıştır. Detaylar için LICENSE dosyasına bakın.
🔄 Sürüm Geçmişi
v1.0.0 (Mevcut)

Komple mobil otomasyon framework’ü
Appium, Cucumber, TestNG entegrasyonu
Android ve iOS desteği
Allure ve ExtentReports ile raporlama
Video ve ekran görüntüsü kaydı
Paralel execution desteği
CI/CD hazır yapı

Planlanan Özellikler (v1.1.0)

Hibrit uygulamalar için çapraz tarayıcı testi
API test entegrasyonu
Performans testleri için geliştirmeler
Cloud cihaz entegrasyonu (BrowserStack, Sauce Labs)


