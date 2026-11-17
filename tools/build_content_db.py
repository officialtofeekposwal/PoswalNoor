#!/usr/bin/env python3
"""
Poswal Noor — Offline Content Database Builder & Validator
Reproducibly generates poswal_noor.db from verified open-source datasets:
1. Tanzil Project / Quran Complex: 6,236 Ayahs in pristine Uthmani Arabic,
   Sahih International (English), Suhel Farooq Khan (Hindi), Muhammad Junagarhi (Urdu).
2. Sahih al-Bukhari & Sahih Muslim: Verified open collections with book, hadith numbers, and authenticity grading.
"""

import os
import sys
import json
import sqlite3
import urllib.request
import re

# Output path
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.dirname(SCRIPT_DIR)
CACHE_DIR = os.path.join(SCRIPT_DIR, "cache")
OUTPUT_DB = os.path.join(PROJECT_ROOT, "app", "src", "main", "assets", "database", "poswal_noor.db")

os.makedirs(CACHE_DIR, exist_ok=True)
os.makedirs(os.path.dirname(OUTPUT_DB), exist_ok=True)

# Sources definition
SOURCES = {
    "quran_arabic": "https://cdn.jsdelivr.net/gh/fawazahmed0/quran-api@1/editions/ara-quranuthmanihaf1.json",
    "quran_english": "https://cdn.jsdelivr.net/gh/fawazahmed0/quran-api@1/editions/eng-ummmuhammad.json",
    "quran_hindi": "https://cdn.jsdelivr.net/gh/fawazahmed0/quran-api@1/editions/hin-suhelfarooqkhan.json",
    "quran_urdu": "https://cdn.jsdelivr.net/gh/fawazahmed0/quran-api@1/editions/urd-muhammadjunagar.json",
    "hadith_bukhari": "https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/editions/eng-bukhari.json",
    "hadith_muslim": "https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/editions/eng-muslim.json",
}

# 114 Surahs Metadata (number, arabic, english, transliteration, verses, revelation)
SURAHS = [
    (1, "الفاتحة", "Al-Fatihah", "The Opening", 7, "Meccan"),
    (2, "البقرة", "Al-Baqarah", "The Cow", 286, "Medinan"),
    (3, "آل عمران", "Ali 'Imran", "Family of Imran", 200, "Medinan"),
    (4, "النساء", "An-Nisa", "The Women", 176, "Medinan"),
    (5, "المائدة", "Al-Ma'idah", "The Table Spread", 120, "Medinan"),
    (6, "الأنعام", "Al-An'am", "The Cattle", 165, "Meccan"),
    (7, "الأعراف", "Al-A'raf", "The Heights", 206, "Meccan"),
    (8, "الأنفال", "Al-Anfal", "The Spoils of War", 75, "Medinan"),
    (9, "التوبة", "At-Tawbah", "The Repentance", 129, "Medinan"),
    (10, "يونس", "Yunus", "Jonah", 109, "Meccan"),
    (11, "هود", "Hud", "Hud", 123, "Meccan"),
    (12, "يوسف", "Yusuf", "Joseph", 111, "Meccan"),
    (13, "الرعد", "Ar-Ra'd", "The Thunder", 43, "Medinan"),
    (14, "ابراهيم", "Ibrahim", "Abraham", 52, "Meccan"),
    (15, "الحجر", "Al-Hijr", "The Rocky Tract", 99, "Meccan"),
    (16, "النحل", "An-Nahl", "The Bee", 128, "Meccan"),
    (17, "الإسراء", "Al-Isra", "The Night Journey", 111, "Meccan"),
    (18, "الكهف", "Al-Kahf", "The Cave", 110, "Meccan"),
    (19, "مريم", "Maryam", "Mary", 98, "Meccan"),
    (20, "طه", "Taha", "Ta-Ha", 135, "Meccan"),
    (21, "الأنبياء", "Al-Anbiya", "The Prophets", 112, "Meccan"),
    (22, "الحج", "Al-Hajj", "The Pilgrimage", 78, "Medinan"),
    (23, "المؤمنون", "Al-Mu'minun", "The Believers", 118, "Meccan"),
    (24, "النور", "An-Nur", "The Light", 64, "Medinan"),
    (25, "الفرقان", "Al-Furqan", "The Criterion", 77, "Meccan"),
    (26, "الشعراء", "Ash-Shu'ara", "The Poets", 227, "Meccan"),
    (27, "النمل", "An-Naml", "The Ant", 93, "Meccan"),
    (28, "القصص", "Al-Qasas", "The Stories", 88, "Meccan"),
    (29, "العنكبوت", "Al-'Ankabut", "The Spider", 69, "Meccan"),
    (30, "الروم", "Ar-Rum", "The Romans", 60, "Meccan"),
    (31, "لقمان", "Luqman", "Luqman", 34, "Meccan"),
    (32, "السجدة", "As-Sajdah", "The Prostration", 30, "Meccan"),
    (33, "الأحزاب", "Al-Ahzab", "The Combined Forces", 73, "Medinan"),
    (34, "سبإ", "Saba", "Sheba", 54, "Meccan"),
    (35, "فاطر", "Fatir", "Originator", 45, "Meccan"),
    (36, "يس", "Ya-Sin", "Ya-Sin", 83, "Meccan"),
    (37, "الصافات", "As-Saffat", "Those who set the Ranks", 182, "Meccan"),
    (38, "ص", "Sad", "The Letter 'Saad'", 88, "Meccan"),
    (39, "الزمر", "Az-Zumar", "The Troops", 75, "Meccan"),
    (40, "غافر", "Ghafir", "The Forgiver", 85, "Meccan"),
    (41, "فصلت", "Fussilat", "Explained in Detail", 54, "Meccan"),
    (42, "الشورى", "Ash-Shuraa", "The Consultation", 53, "Meccan"),
    (43, "الزخرف", "Az-Zukhruf", "The Ornaments of Gold", 89, "Meccan"),
    (44, "الدخان", "Ad-Dukhan", "The Smoke", 59, "Meccan"),
    (45, "الجاثية", "Al-Jathiyah", "The Crouching", 37, "Meccan"),
    (46, "الأحقاف", "Al-Ahqaf", "The Wind-Curved Sandhills", 35, "Meccan"),
    (47, "محمد", "Muhammad", "Muhammad", 38, "Medinan"),
    (48, "الفتح", "Al-Fath", "The Victory", 29, "Medinan"),
    (49, "الحجرات", "Al-Hujurat", "The Rooms", 18, "Medinan"),
    (50, "ق", "Qaf", "The Letter 'Qaf'", 45, "Meccan"),
    (51, "الذاريات", "Adh-Dhariyat", "The Winnowing Winds", 60, "Meccan"),
    (52, "الطور", "At-Tur", "The Mount", 49, "Meccan"),
    (53, "النجم", "An-Najm", "The Star", 62, "Meccan"),
    (54, "القمر", "Al-Qamar", "The Moon", 55, "Meccan"),
    (55, "الرحمن", "Ar-Rahman", "The Beneficent", 78, "Medinan"),
    (56, "الواقعة", "Al-Waqi'ah", "The Inevitable", 96, "Meccan"),
    (57, "الحديد", "Al-Hadid", "The Iron", 29, "Medinan"),
    (58, "المجادلة", "Al-Mujadila", "The Pleading Woman", 22, "Medinan"),
    (59, "الحشر", "Al-Hashr", "The Exile", 24, "Medinan"),
    (60, "الممتحنة", "Al-Mumtahanah", "She that is to be examined", 13, "Medinan"),
    (61, "الصف", "As-Saf", "The Ranks", 14, "Medinan"),
    (62, "الجمعة", "Al-Jumu'ah", "The Congregation, Friday", 11, "Medinan"),
    (63, "المنافقون", "Al-Munafiqun", "The Hypocrites", 11, "Medinan"),
    (64, "التغابن", "At-Taghabun", "The Mutual Disillusion", 18, "Medinan"),
    (65, "الطلاق", "At-Talaq", "The Divorce", 12, "Medinan"),
    (66, "التحريم", "At-Tahrim", "The Prohibition", 12, "Medinan"),
    (67, "الملك", "Al-Mulk", "The Sovereignty", 30, "Meccan"),
    (68, "القلم", "Al-Qalam", "The Pen", 52, "Meccan"),
    (69, "الحاقة", "Al-Haqqah", "The Reality", 52, "Meccan"),
    (70, "المعارج", "Al-Ma'arij", "The Ascending Stairways", 44, "Meccan"),
    (71, "نوح", "Nuh", "Noah", 28, "Meccan"),
    (72, "الجن", "Al-Jinn", "The Jinn", 28, "Meccan"),
    (73, "المزمل", "Al-Muzzammil", "The Enshrouded One", 20, "Meccan"),
    (74, "المدثر", "Al-Muddaththir", "The Cloaked One", 56, "Meccan"),
    (75, "القيامة", "Al-Qiyamah", "The Resurrection", 40, "Meccan"),
    (76, "الانسان", "Al-Insan", "The Human", 31, "Medinan"),
    (77, "المرسلات", "Al-Mursalat", "The Emissaries", 50, "Meccan"),
    (78, "النبإ", "An-Naba", "The Tidings", 40, "Meccan"),
    (79, "النازعات", "An-Nazi'at", "Those who drag forth", 46, "Meccan"),
    (80, "عبس", "'Abasa", "He Frowned", 42, "Meccan"),
    (81, "التكوير", "At-Takwir", "The Overthrowing", 29, "Meccan"),
    (82, "الإنفطار", "Al-Infitar", "The Cleaving", 19, "Meccan"),
    (83, "المطففين", "Al-Mutaffifin", "The Defrauding", 36, "Meccan"),
    (84, "الإنشقاق", "Al-Inshiqaq", "The Splitting Open", 25, "Meccan"),
    (85, "البروج", "Al-Buruj", "The Mansions of the Stars", 22, "Meccan"),
    (86, "الطارق", "At-Tariq", "The Nightcometh", 17, "Meccan"),
    (87, "الأعلى", "Al-A'la", "The Most High", 19, "Meccan"),
    (88, "الغاشية", "Al-Ghashiyah", "The Overwhelming", 26, "Meccan"),
    (89, "الفجر", "Al-Fajr", "The Dawn", 30, "Meccan"),
    (90, "البلد", "Al-Balad", "The City", 20, "Meccan"),
    (91, "الشمس", "Ash-Shams", "The Sun", 15, "Meccan"),
    (92, "الليل", "Al-Layl", "The Night", 21, "Meccan"),
    (93, "الضحى", "Ad-Duhaa", "The Morning Hours", 11, "Meccan"),
    (94, "الشرح", "Ash-Sharh", "The Relief", 8, "Meccan"),
    (95, "التين", "At-Tin", "The Fig", 8, "Meccan"),
    (96, "العلق", "Al-'Alaq", "The Clot", 19, "Meccan"),
    (97, "القدر", "Al-Qadr", "The Power", 5, "Meccan"),
    (98, "البينة", "Al-Bayyinah", "The Clear Proof", 8, "Medinan"),
    (99, "الزلزلة", "Az-Zalzalah", "The Earthquake", 8, "Medinan"),
    (100, "العاديات", "Al-'Adiyat", "The Courser", 11, "Meccan"),
    (101, "القارعة", "Al-Qari'ah", "The Calamity", 11, "Meccan"),
    (102, "التكاثر", "At-Takathur", "The Rivalry in world increase", 8, "Meccan"),
    (103, "العصر", "Al-'Asr", "The Declining Day", 3, "Meccan"),
    (104, "الهمزة", "Al-Humazah", "The Traducer", 9, "Meccan"),
    (105, "الفيل", "Al-Fil", "The Elephant", 5, "Meccan"),
    (106, "قريش", "Quraysh", "Quraysh", 4, "Meccan"),
    (107, "الماعون", "Al-Ma'un", "The Small Kindnesses", 7, "Meccan"),
    (108, "الكوثر", "Al-Kawthar", "The Abundance", 3, "Meccan"),
    (109, "الكافرون", "Al-Kafirun", "The Disbelievers", 6, "Meccan"),
    (110, "النصر", "An-Nasr", "The Divine Support", 3, "Medinan"),
    (111, "المسد", "Al-Masad", "The Palm Fiber", 5, "Meccan"),
    (112, "الإخلاص", "Al-Ikhlas", "The Sincerity", 4, "Meccan"),
    (113, "الفلق", "Al-Falaq", "The Daybreak", 5, "Meccan"),
    (114, "الناس", "An-Nas", "Mankind", 6, "Meccan"),
]

# Conservative Topic Keyword Mappings (High Confidence Only)
TOPIC_KEYWORDS = {
    "namaz": [r"\bprayer\b", r"\bprayers\b", r"\bpray\b", r"\bsalah\b", r"\bprostrat", r"\bruku\b", r"\bsajdah\b", r"\bnamaz\b"],
    "sabr": [r"\bpatien", r"\bpersever", r"\bendur", r"\bsabr\b", r"\bsteadfast\b"],
    "charity": [r"\bcharity\b", r"\bzakah\b", r"\bzakat\b", r"\balms\b", r"\bspend in the way\b", r"\bsadaqah\b", r"\bpoor\b", r"\bneedy\b"],
    "parents": [r"\bparent", r"\bmother\b", r"\bfather\b", r"\bwalidain\b"],
    "dua": [r"\bsupplicat", r"\bcall upon me\b", r"\binvok", r"\bdua\b", r"\bask allah\b"],
    "akhirah": [r"\bday of resurrection\b", r"\bhereafter\b", r"\bparadise\b", r"\bgarden\b", r"\bhell\b", r"\bfire\b", r"\bjudgment\b", r"\bakhirah\b"],
    "ilm": [r"\bknowledge\b", r"\bknow\b", r"\bwise\b", r"\bwisdom\b", r"\bscholar\b", r"\bteach\b", r"\bunderstand\b"],
    "family": [r"\bkinship\b", r"\bwife\b", r"\bwives\b", r"\bhusband\b", r"\bchildren\b", r"\boffspring\b", r"\bfamily\b", r"\bbrother\b", r"\bsister\b"],
    "akhlaq": [r"\btruthful", r"\bhonesty\b", r"\bforgiv", r"\bmercy\b", r"\bmerciful\b", r"\bhumble\b", r"\bkindness\b", r"\bjustice\b", r"\bgood conduct\b", r"\bcharacter\b"],
}

def download_file(url, target_path):
    if os.path.exists(target_path) and os.path.getsize(target_path) > 1000:
        print(f"  [Cache hit] {os.path.basename(target_path)} ({os.path.getsize(target_path)} bytes)")
        return
    print(f"  Downloading {url} ...")
    req = urllib.request.Request(url, headers={"User-Agent": "PoswalNoor-DB-Builder/1.0"})
    with urllib.request.urlopen(req, timeout=60) as resp:
        with open(target_path, "wb") as f:
            f.write(resp.read())
    print(f"  Saved {target_path} ({os.path.getsize(target_path)} bytes)")

def assign_topic(text):
    text_lower = text.lower()
    for topic, patterns in TOPIC_KEYWORDS.items():
        for pat in patterns:
            if re.search(pat, text_lower):
                return topic
    return None

def main():
    print("=" * 70)
    print("🌙 POSWAL NOOR — CONTENT DATABASE EXPANSION (6,236 QURAN + SAHIH HADITH)")
    print("=" * 70)

    # 1. Download all required sources
    print("\n[Step 1/5] Fetching verified open datasets...")
    local_files = {}
    for key, url in SOURCES.items():
        fname = os.path.join(CACHE_DIR, f"{key}.json")
        download_file(url, fname)
        local_files[key] = fname

    # 2. Parse Quran datasets
    print("\n[Step 2/5] Parsing Quran translations...")
    with open(local_files["quran_arabic"], "r", encoding="utf-8") as f:
        ara_data = json.load(f)["quran"]
    with open(local_files["quran_english"], "r", encoding="utf-8") as f:
        eng_data = json.load(f)["quran"]
    with open(local_files["quran_hindi"], "r", encoding="utf-8") as f:
        hin_data = json.load(f)["quran"]
    with open(local_files["quran_urdu"], "r", encoding="utf-8") as f:
        urd_data = json.load(f)["quran"]

    print(f"  Arabic Ayahs:  {len(ara_data)}")
    print(f"  English Ayahs: {len(eng_data)}")
    print(f"  Hindi Ayahs:   {len(hin_data)}")
    print(f"  Urdu Ayahs:    {len(urd_data)}")

    assert len(ara_data) == 6236, f"Expected 6236 Arabic ayahs, got {len(ara_data)}"
    assert len(eng_data) == 6236, f"Expected 6236 English ayahs, got {len(eng_data)}"
    assert len(hin_data) == 6236, f"Expected 6236 Hindi ayahs, got {len(hin_data)}"
    assert len(urd_data) == 6236, f"Expected 6236 Urdu ayahs, got {len(urd_data)}"

    surah_meta = {s[0]: s for s in SURAHS}

    # 3. Parse Hadith datasets
    print("\n[Step 3/5] Parsing Sahih Hadith collections...")
    with open(local_files["hadith_bukhari"], "r", encoding="utf-8") as f:
        bukhari_raw = json.load(f)
    with open(local_files["hadith_muslim"], "r", encoding="utf-8") as f:
        muslim_raw = json.load(f)

    bukhari_items = bukhari_raw.get("hadiths", [])
    muslim_items = muslim_raw.get("hadiths", [])
    print(f"  Sahih al-Bukhari entries: {len(bukhari_items)}")
    print(f"  Sahih Muslim entries:     {len(muslim_items)}")

    # 4. Create SQLite Database
    print(f"\n[Step 4/5] Building SQLite database at {OUTPUT_DB} ...")
    if os.path.exists(OUTPUT_DB):
        os.remove(OUTPUT_DB)

    conn = sqlite3.connect(OUTPUT_DB)
    cur = conn.cursor()

    cur.execute("PRAGMA page_size = 4096;")
    cur.execute("PRAGMA encoding = 'UTF-8';")

    # Table: surahs
    cur.execute("""
        CREATE TABLE surahs (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            number INTEGER NOT NULL UNIQUE,
            name_arabic TEXT NOT NULL,
            name_english TEXT NOT NULL,
            name_translation TEXT NOT NULL,
            total_verses INTEGER NOT NULL,
            revelation_type TEXT NOT NULL
        );
    """)

    for s in SURAHS:
        cur.execute("""
            INSERT INTO surahs (number, name_arabic, name_english, name_translation, total_verses, revelation_type)
            VALUES (?, ?, ?, ?, ?, ?)
        """, s)

    # Table: content_items
    cur.execute("""
        CREATE TABLE content_items (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            type TEXT NOT NULL,
            surah INTEGER,
            ayah INTEGER,
            surah_name TEXT,
            arabic TEXT NOT NULL,
            translation_en TEXT NOT NULL,
            translation_hinglish TEXT,
            translation_hi TEXT,
            translation_ur TEXT,
            reference TEXT NOT NULL,
            book TEXT,
            number TEXT,
            grade TEXT,
            narrator TEXT,
            topic TEXT,
            word_count INTEGER NOT NULL,
            word_count_en INTEGER NOT NULL,
            is_favorite INTEGER NOT NULL DEFAULT 0
        );
    """)

    # Populate Quran
    print("  Inserting 6,236 Quran Ayahs...")
    topic_counts = {t: 0 for t in TOPIC_KEYWORDS.keys()}
    untagged_count = 0
    quran_word_counts_under_40 = 0
    quran_word_counts_under_60 = 0

    for i in range(6236):
        ara = ara_data[i]
        eng = eng_data[i]
        hin = hin_data[i]
        urd = urd_data[i]

        s_num = ara["chapter"]
        a_num = ara["verse"]
        s_info = surah_meta.get(s_num)
        s_name = s_info[2] if s_info else f"Surah {s_num}"

        ref = f"Surah {s_name} {s_num}:{a_num}"
        eng_text = eng["text"].strip()
        ara_text = ara["text"].strip()
        hin_text = hin["text"].strip()
        urd_text = urd["text"].strip()

        # Word counts
        wc_en = len(eng_text.split())
        wc_ar = len(ara_text.split())
        if wc_en <= 40:
            quran_word_counts_under_40 += 1
        if wc_en <= 60:
            quran_word_counts_under_60 += 1

        # Topic detection on verified English translation
        topic = assign_topic(eng_text)
        if topic:
            topic_counts[topic] += 1
        else:
            untagged_count += 1

        cur.execute("""
            INSERT INTO content_items (
                type, surah, ayah, surah_name, arabic,
                translation_en, translation_hinglish, translation_hi, translation_ur,
                reference, book, number, grade, narrator, topic,
                word_count, word_count_en, is_favorite
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)
        """, (
            "quran", s_num, a_num, s_name, ara_text,
            eng_text, None, hin_text, urd_text,
            ref, s_name, str(a_num), "Sahih (Mutawatir)", None, topic,
            wc_ar, wc_en
        ))

    # Populate Hadiths (Filtered to complete text with valid translations)
    print("  Inserting Sahih Hadiths (Bukhari & Muslim)...")
    hadith_inserted = 0
    hadith_word_counts_under_40 = 0
    hadith_word_counts_under_60 = 0

    for item in bukhari_items:
        eng_text = item.get("text", "").strip()
        if not eng_text or len(eng_text) < 15 or len(eng_text) > 1200:
            continue
        h_num = str(item.get("hadithnumber", ""))
        ref = f"Sahih al-Bukhari {h_num}" if h_num else "Sahih al-Bukhari"
        wc_en = len(eng_text.split())
        if wc_en <= 40:
            hadith_word_counts_under_40 += 1
        if wc_en <= 60:
            hadith_word_counts_under_60 += 1

        topic = assign_topic(eng_text)
        if topic:
            topic_counts[topic] += 1

        cur.execute("""
            INSERT INTO content_items (
                type, surah, ayah, surah_name, arabic,
                translation_en, translation_hinglish, translation_hi, translation_ur,
                reference, book, number, grade, narrator, topic,
                word_count, word_count_en, is_favorite
            ) VALUES (?, NULL, NULL, NULL, ?, ?, NULL, NULL, NULL, ?, ?, ?, ?, NULL, ?, ?, ?, 0)
        """, (
            "hadith",
            item.get("arabic", "").strip() or "قال رسول الله صلى الله عليه وسلم",
            eng_text,
            ref,
            "Sahih al-Bukhari",
            h_num,
            "Sahih",
            topic,
            wc_en,
            wc_en
        ))
        hadith_inserted += 1

    for item in muslim_items:
        eng_text = item.get("text", "").strip()
        if not eng_text or len(eng_text) < 15 or len(eng_text) > 1200:
            continue
        h_num = str(item.get("hadithnumber", ""))
        ref = f"Sahih Muslim {h_num}" if h_num else "Sahih Muslim"
        wc_en = len(eng_text.split())
        if wc_en <= 40:
            hadith_word_counts_under_40 += 1
        if wc_en <= 60:
            hadith_word_counts_under_60 += 1

        topic = assign_topic(eng_text)
        if topic:
            topic_counts[topic] += 1

        cur.execute("""
            INSERT INTO content_items (
                type, surah, ayah, surah_name, arabic,
                translation_en, translation_hinglish, translation_hi, translation_ur,
                reference, book, number, grade, narrator, topic,
                word_count, word_count_en, is_favorite
            ) VALUES (?, NULL, NULL, NULL, ?, ?, NULL, NULL, NULL, ?, ?, ?, ?, NULL, ?, ?, ?, 0)
        """, (
            "hadith",
            item.get("arabic", "").strip() or "قال رسول الله صلى الله عليه وسلم",
            eng_text,
            ref,
            "Sahih Muslim",
            h_num,
            "Sahih",
            topic,
            wc_en,
            wc_en
        ))
        hadith_inserted += 1

    # Create Indexes for lightning fast queries
    print("  Creating database indexes...")
    cur.execute("CREATE INDEX idx_content_type ON content_items(type);")
    cur.execute("CREATE INDEX idx_content_topic ON content_items(topic);")
    cur.execute("CREATE INDEX idx_content_word_count_en ON content_items(word_count_en);")
    cur.execute("CREATE INDEX idx_content_quran_surah_ayah ON content_items(surah, ayah);")
    cur.execute("CREATE INDEX idx_content_hadith_book_num ON content_items(book, number);")

    conn.commit()

    # Validation
    cur.execute("SELECT COUNT(*) FROM content_items WHERE type='quran'")
    quran_total = cur.fetchone()[0]
    cur.execute("SELECT COUNT(*) FROM content_items WHERE type='hadith'")
    hadith_total = cur.fetchone()[0]
    cur.execute("SELECT COUNT(*) FROM content_items")
    grand_total = cur.fetchone()[0]
    cur.execute("SELECT COUNT(*) FROM surahs")
    surah_total = cur.fetchone()[0]

    db_size_mb = os.path.getsize(OUTPUT_DB) / (1024 * 1024)

    conn.close()

    print("\n" + "=" * 70)
    print("✅ DATABASE BUILD & VALIDATION REPORT")
    print("=" * 70)
    print(f"Total Rows in DB:               {grand_total:,}")
    print(f"  - Quran Ayahs:                {quran_total:,} (Expected: 6,236 -> {'MATCH ✅' if quran_total == 6236 else 'MISMATCH ❌'})")
    print(f"  - Hadiths (Bukhari + Muslim): {hadith_total:,}")
    print(f"Total Surahs Indexed:           {surah_total} (Expected: 114 -> {'MATCH ✅' if surah_total == 114 else 'MISMATCH ❌'})")
    print(f"Database File Size:             {db_size_mb:.2f} MB")
    print("\n📊 Topic Distribution (Conservative Match):")
    for t, cnt in sorted(topic_counts.items(), key=lambda x: -x[1]):
        print(f"  - {t:<10}: {cnt:,} items")
    print(f"  - Untagged (All/Both feed): {untagged_count:,} items")
    print("\n📱 Widget Pool Statistics:")
    print(f"  - Total items with English word_count <= 40 (4x2 Widget): {quran_word_counts_under_40 + hadith_word_counts_under_40:,} items")
    print(f"  - Total items with English word_count <= 60 (4x3 Widget): {quran_word_counts_under_60 + hadith_word_counts_under_60:,} items")
    print("=" * 70)

if __name__ == "__main__":
    main()
