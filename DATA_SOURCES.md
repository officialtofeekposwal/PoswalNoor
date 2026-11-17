# 📜 Poswal Noor — Official Data Sources & Licenses

All spiritual scripture and text within **Poswal Noor** are derived strictly from authentic, verified, and open-source datasets. **No religious text is ever AI-generated, guessed, or paraphrased.**

---

## 1. Holy Quran (القرآن الكريم) — 6,236 Ayahs · 114 Surahs

| Component | Source / Authority | License / Terms | Reference / URL |
| :--- | :--- | :--- | :--- |
| **Arabic Uthmani Script** | **Tanzil Project** & King Fahd Quran Complex (Hafs 'an 'Asim) | Public Domain / Tanzil Standard Terms (Free for non-commercial and educational use) | [http://tanzil.net](http://tanzil.net) |
| **English Translation** | **Sahih International** (Umm Muhammad, Aminah Assami, Maryam Kennedy) | Open / Permissive Distribution | [Tanzil.net](http://tanzil.net/trans/) |
| **Hindi Translation** | **Dr. Suhel Farooq Khan & Prof. Farooq Khan** | Permissive Educational / Tanzil Project | [Tanzil.net](http://tanzil.net/trans/) |
| **Urdu Translation** | **Maulana Muhammad Junagarhi** | Public Domain / Tanzil Project | [Tanzil.net](http://tanzil.net/trans/) |
| **Hinglish (Roman Urdu / Hindi)** | Curated verified romanizations (with automatic English fallback + indicator tag when unlicensed) | In-House Verified Non-commercial | Poswal Noor Local Asset |

---

## 2. Hadith Collections (كتب الحديث الشريف) — 14,060 Records

| Collection | Source / Dataset | Records | Translation Authority | Grading / Verification |
| :--- | :--- | :--- | :--- | :--- |
| **Sahih al-Bukhari (صحيح البخاري)** | Fawaz Ahmed Hadith Open API / Sunnah.com dump | **7,050** | M. Muhsin Khan (English) | **Sahih** (Muttafaqun 'Alayh) |
| **Sahih Muslim (صحيح مسلم)** | Fawaz Ahmed Hadith Open API / Sunnah.com dump | **7,010** | Abdul Hamid Siddiqui (English) | **Sahih** |

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
