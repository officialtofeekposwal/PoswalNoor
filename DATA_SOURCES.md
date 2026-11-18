# 📜 Poswal Noor — Official Data Sources & Licenses

All spiritual scripture and text within **Poswal Noor** are derived strictly from authentic, verified, and open-source datasets. **No religious text is ever AI-generated, guessed, or paraphrased.**

---

## 1. Holy Quran (القرآن الكريم) — 6,236 Ayahs · 114 Surahs

| Component | Source / Authority | License / Terms | Reference / URL |
| :--- | :--- | :--- | :--- |
| **Arabic Uthmani Script** | **Tanzil Project** & King Fahd Quran Complex (Hafs 'an 'Asim) | Creative Commons Attribution 3.0 (CC-BY 3.0) / Tanzil Standard Terms | [http://tanzil.net](http://tanzil.net) |
| **English Translation** | **Sahih International** (Umm Muhammad, Aminah Assami, Maryam Kennedy) | Open / Permissive Distribution | [Tanzil.net](http://tanzil.net/trans/) |
| **Hindi Translation** | **Dr. Suhel Farooq Khan & Prof. Farooq Khan** | Permissive Educational / Tanzil Project | [Tanzil.net](http://tanzil.net/trans/) |
| **Urdu Translation** | **Maulana Muhammad Junagarhi** | Public Domain / Tanzil Project | [Tanzil.net](http://tanzil.net/trans/) |
| **Hinglish (Roman Urdu / Hindi)** | **Farooq Khan Hindi (Automatic Phonological Transliteration)** — 6,236 / 6,236 Ayahs (100% Quran coverage) | **UNVERIFIED DERIVATIVE (Automated Phonetic Transliteration)**<br>• Transliterated from Dr. Suhel Farooq Khan &amp; Prof. Farooq Khan Devanagari translation via `tools/transliterate_devanagari_to_hinglish.py`.<br>• Tanzil general translation terms state that modifications/alterations to translation texts are not permitted. Because this Romanized version is an automated script transliteration rather than an independently authored or certified translation, it is explicitly classified as an **Unverified Derivative** until formally reviewed.<br>• *Note:* Hadith records continue to use English fallback + badge. | Poswal Noor Local Asset (`translation_hinglish`) |

### 📌 Tanzil Project Terms of Use (Verbatim for About Screen)
```text
Permission is granted to copy and distribute verbatim copies of this Quran text, but changing the text is not allowed.

This Quran text can be used in any website or application, provided that its source (Tanzil Project) is clearly indicated, and a link is made to tanzil.net to enable users to keep track of changes.
```

---

## 2. Hadith Collections (كتب الحديث الشريف) — 14,060 Records

| Collection | Dataset Name & Origin | Records | Translation Authority | Dataset License | English Translation Copyright |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Sahih al-Bukhari (صحيح البخاري)** | **`hadith-api`** (Fawaz Ahmed Hadith API)<br>Editions: `ara-bukhari.json` (Arabic) & `eng-bukhari.json` (English) | **7,050** (97 Books) | M. Muhsin Khan | **The Unlicense** (Public Domain dedication for API repository)<br>URL: [https://github.com/fawazahmed0/hadith-api](https://github.com/fawazahmed0/hadith-api) | **UNVERIFIED** (Darussalam publication; license not explicitly stated in repo) |
| **Sahih Muslim (صحيح مسلم)** | **`hadith-api`** (Fawaz Ahmed Hadith API)<br>Editions: `ara-muslim.json` (Arabic) & `eng-muslim.json` (English) | **7,010** (56 Books) | Abdul Hamid Siddiqui | **The Unlicense** (Public Domain dedication for API repository)<br>URL: [https://github.com/fawazahmed0/hadith-api](https://github.com/fawazahmed0/hadith-api) | **UNVERIFIED** (Kitab Bhavan publication; license not explicitly stated in repo) |

---

## 3. Offline Guarantee & Reproducibility
* **Zero Network Permission:** Poswal Noor runtime does not declare `android.permission.INTERNET` in its `AndroidManifest.xml`.
* **Build Reproducibility:** The database is generated at dev-time using `tools/build_content_db.py` on the developer machine and bundled into `app/src/main/assets/database/poswal_noor.db`.

---

## 4. Typography & Font Licenses (SIL Open Font License 1.1)
* **Amiri Quranic Script (`amiri_regular.ttf`, `amiri_bold.ttf`):** Designed by Khaled Hosny. Classical Naskh revival for Arabic scripture and Uthmani orthography. Licensed under SIL Open Font License (OFL 1.1).
* **Noto Nastaliq Urdu (`noto_nastaliq_urdu.ttf`):** Designed by Google Fonts / Monotype. Authentic Urdu Nastaliq with complete RTL support. Licensed under SIL Open Font License (OFL 1.1).
* **Noto Sans Devanagari (`noto_sans_devanagari.ttf`):** Designed by Google Fonts. Modern legible Devanagari script for Hindi. Licensed under SIL Open Font License (OFL 1.1).
* **Inter Variable (`inter_variable.ttf`):** Designed by Rasmus Andersson. High-legibility UI typeface optimized for Android screens. Licensed under SIL Open Font License (OFL 1.1).
