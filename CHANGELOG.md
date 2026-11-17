# 📋 Poswal Noor (نور) — Changelog

All notable changes to **Poswal Noor** are documented in this file.

---

## [2.0.0] - 2026-11-17 (Master Upgrade — Phases 1 to 6)

### 🚀 Phase 1: Data Expansion & No-Repeat Engine
- **Expanded Authentic Scripture Pool (from 30 to 20,296 items)**:
  - **Holy Quran**: Full 6,236 Ayahs across all 114 Surahs from the Tanzil Project (Uthmani text) with verified Sahih International (English), Suhel Farooq Khan (Hindi), and Maulana Muhammad Junagarhi (Urdu) translations.
  - **Sahih Hadiths**: 14,060 complete authentic records (7,050 Sahih al-Bukhari + 7,010 Sahih Muslim) with grading, narrator chain, and collection book indexing.
- **Dual-Database Architecture**:
  - `poswal_noor.db`: Read-only pre-packaged content asset (15.55 MB). Updating content will never wipe user data.
  - `poswal_user.db`: Dedicated user database storing reading positions, bookmarks, notes, and seen items with strict non-destructive migrations.
- **Paging 3 Infinite Feed**:
  - Replaced single-item view with infinite scrolling `LazyColumn` powered by AndroidX Paging 3.
  - Built `NoRepeatPagingSource` with deterministic LCG pseudo-random scoring: `((id * 1103515245 + seed) & 2147483647)`. Guarantees non-repeating cards across configuration changes and changes on pull-to-refresh.
  - 1-second view duration threshold before marking cards as seen.

### 🎨 Phase 2: UI / UX & Motion Overhaul
- **Bundled Offline Typography**:
  - Arabic Uthmani: **Amiri Regular & Bold** (`amiri_regular.ttf`, `amiri_bold.ttf`).
  - Urdu: **Noto Nastaliq Urdu** (`noto_nastaliq_urdu.ttf`).
  - Hindi: **Noto Sans Devanagari** (`noto_sans_devanagari.ttf`).
  - 100% offline; zero runtime downloads.
- **Motion & Elevation System**:
  - Centralized design tokens in `ui/theme/Motion.kt` (durations, emphasized easings).
  - Apple-inspired animated sliding pill segmented switch with spring physics.
  - Staggered feed card entrance animations with smooth alpha and Y-translation.
  - Android 12+ SplashScreen API (`Theme.PoswalNoor.Starting`) with seamless window background transition.
- **Liquid Glass UI**:
  - Polished frosted glass cards with specular highlight, refraction border gradient, and soft elevation.

### 📖 Phase 3: Core Content Depth
- **Full Quran Reader**:
  - 114 Surahs catalog with revelation badge (Makkah/Madinah), Arabic title, English name, and Ayah count.
  - Surah detail reader with Bismillah calligraphic header, continuous Ayah scroll, and instant bookmarking.
  - Real-time "Last Read" persistence and resume banner.
- **Sahih Hadith Browser**:
  - Tabbed collection browser for **Sahih al-Bukhari** (7,050) and **Sahih Muslim** (7,010).
  - Detailed Hadith view with Sahih authenticity badge, Arabic text, narrator attribution, and English/Urdu translations.
- **Offline Scripture Search**:
  - Instant reactive search across 20,296 records in Arabic, English, Hindi, and Urdu.
  - Filter by Quran only, Hadith only, or All.
- **Hisnul Muslim & Daily Duas**:
  - Authentic supplications categorized into Morning & Evening, After Prayer, Forgiveness, and Hardship.
  - Interactive repetition counter (3x, 33x, 100x) with haptic feedback.

### 🔖 Phase 4: Personalization & Engagement
- **Bookmarks & Collections**:
  - Organize saved verses into custom folders ("Quran", "Hadith", "Duas", etc.).
  - Add and edit personal reflection notes on bookmarked Ayahs.
  - 100% offline JSON export and import backup via Android Storage Access Framework (SAF).
- **Daily Verse Notification via WorkManager**:
  - `DailyVerseNotificationWorker` delivers morning reminders with big-text reflection layout.
  - Fully respects Android 13+ `POST_NOTIFICATIONS` runtime permissions.
- **Share as Styled Image Card**:
  - `ImageCardGenerator` generates high-resolution 1080x1350 styled cards with emerald gradients, gold geometric borders, and offline Amiri calligraphy.
- **3-Screen Onboarding Flow**:
  - Step 1: Language selection.
  - Step 2: Aesthetics & liquid glass preview.
  - Step 3: Home screen widget pin & daily reminder opt-in.

### 🧭 Phase 5: 100% Offline Utilities
- **Digital Tasbih / Dhikr Counter**:
  - Giant interactive 310dp touch dial with animated sweep gradient progress ring.
  - Tactile haptic feedback on every tap and long buzz on reaching target (33, 99, 100).
  - Dhikr presets (SubhanAllah, Alhamdulillah, Allahu Akbar, Astaghfirullah, La ilaha illallah).
  - Lifetime count tracker.
- **Offline Qibla Compass**:
  - Sensor-driven orientation using Android `Sensor.TYPE_ROTATION_VECTOR`.
  - Offline spherical trigonometry calculating exact Kaaba bearing (21.4225° N, 39.8262° E).
  - Visual alignment indicator with green pulse when facing Kaaba (within ±4°).
  - Built-in database of 40+ major global cities (zero location permissions required).
- **Offline Prayer Times**:
  - Solar astronomical calculation algorithms for Fajr, Sunrise, Dhuhr, Asr (Hanafi/Standard), Maghrib, and Isha.
  - Countdown timer to next upcoming prayer.
- **Hijri Calendar**:
  - Algorithmic Gregorian-to-Hijri conversion with Arabic & English month names.
  - Chronological Islamic occasions guide (Ramadan, Eid al-Fitr, Eid al-Adha, Day of Arafah, Ashura).

### 📱 Phase 6: Glance Widgets & Configuration
- **Glance Home Screen Widget Suite**:
  - **4x2 & 4x3 Reflection Widgets**: Rotating scripture cards with dynamic word-count thresholds and instant refresh callback.
  - **2x2 Compact "Verse of the Day" Widget**: Space-saving minimalist widget with deep link to full context.
  - **2x2 Digital Tasbih Counter Widget**: Interactive home screen widget to count praises directly from the launcher via Glance `ActionCallback`.
- **Zero Telemetry Guarantee**:
  - Still strictly **0 network permissions** in `AndroidManifest.xml`.
  - Zero third-party analytics or external ad SDKs.
