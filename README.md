# Poswal Noor (نور)

<div align="center">

![Poswal Noor Banner](app/src/main/res/drawable/ic_noor_mark.xml)

**An all-in-one, privacy-first Islamic companion with a signature Glance Home Screen Widget, authentic Quran & Hadith reflections, and 5-language support.**

[![License: MIT](https://img.shields.io/badge/License-MIT-0F4C3A.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-0F4C3A.svg?logo=kotlin)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android_8.0+_(API_26+)-C9A24B.svg?logo=android)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-0F4C3A.svg?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Glance](https://img.shields.io/badge/Jetpack_Glance-1.1.0-C9A24B.svg)](https://developer.android.com/jetpack/androidx/releases/glance)
[![Release: v2.0.0](https://img.shields.io/badge/Release-v2.0.0-0F4C3A.svg)](https://github.com/officialtofeekposwal/PoswalNoor/releases/tag/v2.0.0)
[![Build Status](https://img.shields.io/badge/Build-Passing-0F4C3A.svg)](#)

[Download Latest APK (v2.0.0)](https://github.com/officialtofeekposwal/PoswalNoor/releases/download/v2.0.0/app-debug.apk) • [Changelog](CHANGELOG.md) • [Data Sources](DATA_SOURCES.md) • [Architecture](#architecture--project-structure)

</div>

---

## 📱 Live Home Screen Widget Preview

<div align="center">
  <img src="docs/screenshots/widget_preview.jpg" alt="Poswal Noor Glance Widget Live" width="360" style="border-radius: 20px; box-shadow: 0 8px 24px rgba(0,0,0,0.25);" />
  <p><i>Poswal Noor Glance Widget running natively on Android with custom dark/light theme, instant refresh, and deep link navigation.</i></p>
</div>

---

## 📖 About The Project

**Poswal Noor** is a modern, native Android Islamic companion designed around a simple yet powerful habit: delivering authentic, beautiful, and distraction-free daily reflections directly to your phone's home screen.

Most contemporary apps are cluttered with intrusive ads, background trackers, and subscription paywalls. Poswal Noor is built strictly **offline-first**, completely free of telemetry, and engineered with clean architecture using modern Android standards (Kotlin 2.0, Jetpack Compose, Jetpack Glance, and Room).

---

## ✨ Key Features (v2.0.0 Master Edition)

- **📚 100% Offline Scripture Reservoir (20,296 Authentic Records)**:
  - Pre-packaged SQLite database in Room (`poswal_noor.db`) providing zero-latency startup and zero network reliance.
  - **Complete Holy Quran (6,236 Ayahs across all 114 Surahs)** from the **Tanzil Project** in pristine Uthmani text.
  - **Complete Sahih Hadiths (14,060 Records)**: 7,050 from **Sahih al-Bukhari** and 7,010 from **Sahih Muslim** with verified narrator chains, book numbers, and Sahih gradings.
  - Dedicated **Dual-Database Architecture**: User data (`poswal_user.db`) is kept completely isolated from content so updates never wipe reading progress or notes.
- **🔄 Deterministic No-Repeat Paging 3 Engine**:
  - Infinite scroll feed that never loops the same 10-15 cards.
  - Mathematical LCG pseudo-random scoring: `((id * 1103515245 + seed) & 2147483647)` ensures stable scrolling across rotation and changes on pull-to-refresh.
- **📖 Full Quran Reader**:
  - 114 Surahs catalog with revelation badge (Makkah/Madinah), Arabic calligraphy title, English name, and Ayah count.
  - Continuous reader with Bismillah calligraphic banner, localized translation, and automatic "Last Read" persistence and resume.
- **📜 Sahih Hadith Browser**:
  - Dedicated browser for Sahih al-Bukhari and Sahih Muslim collections.
- **🔍 100% Offline Scripture Search**:
  - Diacritic-tolerant instant search across all 20,296 records in Arabic, English, Hindi, and Urdu.
- **🤲 Hisnul Muslim & Daily Duas**:
  - Authentic categorized supplications (Morning/Evening, After Prayer, Forgiveness, Hardship) with interactive repetition counters (3x, 33x, 100x).
- **📿 Digital Tasbih / Dhikr Counter**:
  - Giant 310dp interactive dial with animated sweep gradient progress ring and tactile haptics.
- **🧭 100% Offline Qibla Compass & Astronomical Prayer Times**:
  - Sensor-driven orientation vector compass with live Kaaba bearing indicator and visual alignment lock.
  - Astronomical solar calculations for Fajr, Sunrise, Dhuhr, Asr (Hanafi/Standard), Maghrib, and Isha.
  - 40+ preloaded global cities for 100% offline accuracy with zero location permissions required.
- **📅 Algorithmic Hijri Calendar**:
  - Gregorian-to-Hijri converter with Arabic & English month names and upcoming Islamic occasions.
- **🖼️ Share as Styled Image Card**:
  - Generates 1080x1350 luxury reflection cards with emerald gradients, gold geometric borders, and offline Amiri calligraphy for WhatsApp and social media.
- **⚡ Glance Home Screen Widget Suite (4x2, 4x3, 2x2 Verse, 2x2 Tasbih)**:
  - Interactive Glance widgets including dynamic rotating reflections, a compact 2x2 "Verse of the Day" widget, and a 2x2 launcher-based digital Tasbih counter.
- **🔤 Bundled Offline Typography**:
  - Bundled SIL Open Font License fonts: **Amiri Bold & Regular** (Arabic), **Noto Nastaliq Urdu** (Urdu), and **Noto Sans Devanagari** (Hindi).
- **🌍 5-Language System with Fallback**:
  - **English (`en`)**, **Hinglish / Roman Urdu & Hindi (`en-IN`)**, **हिन्दी (`hi`)**, **اردو (`ur`)**, and **العربية (`ar`)**.
- **💎 Apple-Inspired Liquid Glass UI Suite**:
  - Frosted glass cards, sliding pill indicators, specular highlights, and motion tokens.

---

## 🛠️ Tech Stack & Architecture

| Layer | Technologies |
|---|---|
| **Language** | [Kotlin 2.0.21](https://kotlinlang.org/) |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) + [Material 3](https://m3.material.io/) |
| **Widgets** | [Jetpack Glance 1.1.0](https://developer.android.com/jetpack/androidx/releases/glance) (`glance-appwidget`, `glance-material3`) |
| **Architecture** | Clean Architecture (MVVM + Repository Pattern) |
| **Dependency Injection** | [Dagger Hilt 2.51.1](https://dagger.dev/hilt/) + KSP |
| **Local Persistence** | [AndroidX Room 2.6.1](https://developer.android.com/training/data-storage/room) (SQLite) |
| **Preferences** | [Jetpack DataStore 1.1.1](https://developer.android.com/topic/libraries/architecture/datastore) |
| **Background Work** | [AndroidX WorkManager 2.9.1](https://developer.android.com/topic/libraries/architecture/workmanager) |
| **Min / Target SDK** | Min SDK 26 (Android 8.0) • Compile / Target SDK 34 (Android 14) |

---

## 📂 Architecture & Project Structure

```
PoswalNoor/
├── app/
│   ├── build.gradle.kts                   # Module build config & dependencies
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml        # Declarations, deep links & widget receivers
│   │   │   ├── assets/database/
│   │   │   │   └── poswal_noor.db         # Pre-packaged verified Room SQLite DB
│   │   │   ├── java/com/poswal/noor/
│   │   │   │   ├── NoorApp.kt             # Application class (@HiltAndroidApp)
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/             # Room Database, DAO, and Entities
│   │   │   │   │   ├── preferences/       # DataStore UserPreferencesRepository
│   │   │   │   │   └── repository/        # ContentRepository implementation
│   │   │   │   ├── di/                    # Hilt modules (DatabaseModule, RepositoryModule)
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/             # AppLanguage, ContentType, Topic, ContentItem
│   │   │   │   │   └── repository/        # Domain repository interfaces
│   │   │   │   ├── ui/
│   │   │   │   │   ├── MainActivity.kt    # Single activity with NavHost & deep link handler
│   │   │   │   │   ├── home/              # HomeScreen with segmented filters & reflection card
│   │   │   │   │   ├── detail/            # DetailScreen with full Arabic, translations, copy & share
│   │   │   │   │   ├── settings/          # SettingsScreen (language, topics, auto-refresh)
│   │   │   │   │   ├── onboarding/        # First-launch language onboarding screen
│   │   │   │   │   └── theme/             # Brand Kit Color, Typography & NoorTheme (Light/Dark)
│   │   │   │   ├── util/
│   │   │   │   │   └── WidgetPinHelper.kt # 1-tap requestPinAppWidget helper
│   │   │   │   ├── widget/
│   │   │   │   │   ├── NoorGlanceWidget.kt# GlanceAppWidget implementation (4x2 and 4x3)
│   │   │   │   │   ├── NoorWidgetReceiver.kt
│   │   │   │   │   └── RefreshWidgetCallback.kt
│   │   │   │   └── worker/
│   │   │   │       └── WidgetUpdateWorker.kt # Periodic WorkManager refresh job
│   │   │   └── res/
│   │   │       ├── drawable/              # Brand vectors (ic_noor_mark, etc.)
│   │   │       ├── layout/                # Widget loading and placeholder layouts
│   │   │       ├── values/                # Strings (English), Brand Colors, Themes
│   │   │       ├── values-hi/             # Hindi strings
│   │   │       ├── values-ur/             # Urdu strings
│   │   │       ├── values-ar/             # Arabic strings
│   │   │       └── xml/                   # AppWidgetProviderInfo (4x2, 4x3) & locales_config.xml
│   │   └── test/                          # Unit tests (AppLanguageTest, RandomPickerLogicTest)
├── docs/
│   ├── PROGRESS.md                        # Phase milestone tracking & verification logs
│   └── screenshots/                       # Widget & App UI visual captures
├── gradle/
│   └── libs.versions.toml                 # Centralized Gradle Version Catalog
└── scripts/
    └── import_content.py                  # Content validation & Room DB compilation script
```

---

## 🚀 Getting Started

### Prerequisites
- **JDK**: Java Development Kit 21+ (`JAVA_HOME` pointing to JDK 21).
- **Android SDK**: Compile SDK 34, Min SDK 26.
- **Git**

### Clone & Build
```bash
# 1. Clone repository
git clone https://github.com/officialtofeekposwal/PoswalNoor.git
cd PoswalNoor

# 2. Run unit tests
./gradlew test

# 3. Assemble debug APK
./gradlew assembleDebug
```
The compiled APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 🧪 Testing & Verification

Unit tests are included to verify data integrity, language fallbacks, and widget filters:
```bash
./gradlew testDebugUnitTest
```
- **`AppLanguageTest`**: Verifies translation mapping across all 5 languages, Arabic translation suppression, and Hindi/Urdu/Hinglish fallback to English.
- **`RandomPickerLogicTest`**: Enforces strict `word_count <= 40` constraint for widget items and verifies topic/content-type filtering logic.

---

## 📜 Content Sources & Licensing

To preserve religious and scholarly integrity, **no text is machine-translated or paraphrased**:
1. **Quran Arabic**: Tanzil Project Uthmani script (Open Non-Commercial Attribution).
2. **Quran English**: Sahih International (Public / Open License).
3. **Quran Urdu**: Fateh Muhammad Jalandhry (Public Domain).
4. **Hadith Collections**: Sahih al-Bukhari & Sahih Muslim via `fawazahmed0/hadith-api` (MIT License). Every hadith includes book numbers, hadith reference numbers, and authenticity gradings.

---

## 🔒 Privacy & Safety Guarantee

- **No Ads & No Analytics**: Zero third-party ad networks, tracking SDKs, or data harvesting libraries.
- **100% Offline**: Content lives in local storage; the app operates with zero network connectivity.
- **No Unnecessary Permissions**: Requires no sensitive runtime permissions (no location, microphone, or contacts required for Phase 1).

---

## 📄 License

This project is licensed under the [MIT License](LICENSE) © 2026 **Tofeek Poswal**.

---

<div align="center">
  <sub>Developed with care by <a href="https://github.com/officialtofeekposwal">Tofeek Poswal</a>.</sub>
</div>
