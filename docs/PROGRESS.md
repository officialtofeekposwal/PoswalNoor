# Poswal Noor — Development Progress & Roadmap

## Overview
- **App Name**: Poswal Noor
- **Package ID**: `com.poswal.noor`
- **Location**: `D:\Desktop\Poswal Noor`
- **GitHub Repository**: [officialtofeekposwal/PoswalNoor (Private)](https://github.com/officialtofeekposwal/PoswalNoor)
- **Target SDK**: 34 | **Min SDK**: 26 | **Java Target**: 21
- **Current Artifact**: `PoswalNoor-v1.0.0-debug.apk` (20.5 MB)

---

## Content Sources & Licenses
- **Quran Arabic Text**: Tanzil Project Uthmani script (Open License / Non-commercial attribution, verified pristine).
- **Quran English Translation**: Sahih International (Open Public License).
- **Quran Urdu Translation**: Fateh Muhammad Jalandhry (Open License / Public Domain).
- **Hadith Collections**:
  - Sahih al-Bukhari (Complete chains, gradings: Sahih)
  - Sahih Muslim (Complete chains, gradings: Sahih)
  - Source: Open `fawazahmed0/hadith-api` dataset (MIT License). Every hadith includes collection name, hadith number, and authenticity grade.
- **Brand Identity**: Poswal Noor Brand Kit (`#0F4C3A` Emerald, `#0A3427` Emerald Deep, `#C9A24B` Gold, `#F6F1E4` Ivory).

---

## Phase Status Summary

| Phase | Milestone | Status | Notes |
|---|---|---|---|
| **Phase 1** | **Core + Glance Widget (MVP)** | **COMPLETE** | APK assembled & all unit tests passed |
| Phase 2 | Library (Quran & Hadith readers, Azkar, 99 Names) | Ready to Start | Next on roadmap |
| Phase 3 | Prayer Times & Qibla Compass | Pending | Scheduled after Phase 2 |
| Phase 4 | Habit & Discipline (Prayer Tracker) | Pending | Scheduled after Phase 3 |
| Phase 5 | AI Study Helper (Classical Tafsir Context) | Pending | Scheduled after Phase 4 |

---

## Phase 1 Completed Deliverables

### 1. Offline Database & Import Pipeline
- Built `scripts/import_content.py` which validates:
  - Zero empty Arabic or English strings.
  - Unique keys `(book, number, type)` preventing duplicates.
  - Exact word count calculation for widget display.
  - Topics covered: `namaz`, `sabr`, `akhlaq`, `parents`, `dua`, `akhirah`, `ilm`, `charity`, `family`.
- Generated pre-packaged SQLite database in `app/src/main/assets/database/poswal_noor.db`.
- Created Room `ContentItemEntity`, `ContentDao`, and `NoorDatabase`.

### 2. 4-Language System & Localization
- Implemented `AppLanguage` enum: English (`en`, LTR), हिन्दी (`hi`, LTR), اردو (`ur`, RTL), and العربية (`ar`, RTL).
- Configured dynamic fallback: when Hindi or Urdu translation is missing, gracefully falls back to English with a dedicated label badge (`English` / `अंग्रेज़ी` / `انگریزی`).
- When Arabic is selected, the translation section is omitted (displaying purely the pristine Arabic text).
- Configured `AppCompatDelegate.setApplicationLocales` with `MainActivity` extending `AppCompatActivity` for backward-compatible per-app language selection.
- Created complete string resources in `values/`, `values-hi/`, `values-ur/`, and `values-ar/`.
- Declared locales in `res/xml/locales_config.xml`.

### 3. UI Screens
- **Onboarding (`LanguageScreen`)**: 4-language native picker shown on first launch with brand mark.
- **Home (`HomeScreen`)**:
  - Top bar with Poswal Noor branding and settings navigation.
  - Content type filter (Both / Quran / Hadith).
  - Horizontal scrolling topic chips.
  - Daily Reflection Card with Amiri Arabic typography, translation, and bold reference.
  - Refresh button (random picker) and "Read more" navigation.
- **Detail (`DetailScreen`)**:
  - Full Arabic text with adjustable font size.
  - Full translation and topic tags.
  - Copy to clipboard, text share, and favorite toggle.
- **Settings (`SettingsScreen`)**:
  - App Language picker.
  - Content Type preference.
  - Topics multi-select filter.
  - Widget auto-change interval (1h, 3h, 6h, 12h, 24h).
  - Font size scale slider.

### 4. Home Screen Widget (Jetpack Glance)
- Glance widget supporting 4x2 and 4x3 layouts (`NoorGlanceWidget`).
- Displays "Poswal Noor" header, Arabic text, translation, and bold reference.
- Strict filter: only items with `word_count <= 40` appear in the widget.
- Action callback for "Refresh" button (fetches a new random reflection on tap).
- "Read more" deep link action which opens `MainActivity` -> `DetailScreen`.
- Auto-change periodic worker (`WidgetUpdateWorker`) scheduled via WorkManager (1h, 3h, 6h, 12h, 24h).
- Localized configuration context applied inside `provideGlance`.

### 5. Verification & Tests
- `AppLanguageTest`: Verified `translationFor()` mappings, fallback behaviors for Hindi/Urdu, null return for Arabic, and RTL flags.
- `RandomPickerLogicTest`: Verified word count `<= 40` widget filter, topic filtering, and content type filtering.
- `./gradlew.bat test assembleDebug` succeeded cleanly across all 81 tasks.
