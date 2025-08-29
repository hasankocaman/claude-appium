# Claude Appium Test Framework – Copilot README

Bu proje, Hepsiburada mobil uygulaması için Appium, Cucumber BDD ve TestNG kullanılarak geliştirilmiş bir test otomasyon framework’üdür. Android ve iOS platformlarında çalışabilir, modüler ve genişletilebilir bir yapıya sahiptir.

---

## 🧩 OOP İlkeleri ve Uygulama Detayları

### 1. Encapsulation (Kapsülleme)
- Sayfa sınıfları (`HomePage`, `CartPage`) kendi elementlerini ve işlemlerini kapsüller.
- `ConfigurationManager` yapılandırma verilerini dış dünyadan gizler.

### 2. Inheritance (Kalıtım)
- `BasePage` → tüm sayfa sınıfları tarafından extend edilir.
- `BaseTest` → test sınıfları tarafından extend edilerek ortak setup/teardown işlemleri sağlanır.

### 3. Polymorphism (Çok Biçimlilik)
- Ortak metotlar farklı sayfalarda farklı şekillerde override edilir.
- Cucumber adımları aynı metotları farklı parametrelerle çağırabilir.

### 4. Abstraction (Soyutlama)
- `DriverFactory` Appium sürücüsünü soyutlar.
- `ExtentReportManager` raporlama detaylarını gizler.

### 5. SOLID Prensipleri
| İlke | Açıklama | Uygulama |
|------|----------|----------|
| S | Single Responsibility | Her sınıf tek bir sorumluluğa sahip |
| O | Open/Closed | Yeni sayfa eklenebilir, mevcut yapı bozulmaz |
| L | Liskov Substitution | Türetilmiş sınıflar üst sınıfın yerine geçebilir |
| I | Interface Segregation | Metotlar sade ve amaca yönelik |
| D | Dependency Inversion | Soyutlama sınıfları kullanılmış (`DriverFactory`) |

---

## ⚙️ Projenin Genel İşleyişi

```mermaid
flowchart TD
    A[Test Başlatılır] --> B[Appium Server Başlatılır]
    B --> C[DriverFactory ile Sürücü Oluşturulur]
    C --> D[Sayfa Nesneleri Oluşturulur]
    D --> E[Cucumber Step Definitions Çalıştırılır]
    E --> F[Test Senaryoları Koşulur]
    F --> G[Raporlama Yapılır (Allure, Extent)]
    F --> H[Hata Durumunda Ekran Görüntüsü/Video Kaydı]
