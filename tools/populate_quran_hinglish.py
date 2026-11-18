# -*- coding: utf-8 -*-
import sqlite3
import re
import os
import random
from transliterate_devanagari_to_hinglish import transliterate_text

db_path = 'app/src/main/assets/database/poswal_noor.db'
if not os.path.exists(db_path):
    raise FileNotFoundError(f"Database not found at {db_path}")

conn = sqlite3.connect(db_path)
cursor = conn.cursor()

# 1. Fetch all 6,236 Quran ayahs
cursor.execute("SELECT id, surah, ayah, translation_hi FROM content_items WHERE type = 'quran' ORDER BY id ASC")
rows = cursor.fetchall()
total_quran = len(rows)
print(f"Total Quran ayahs to process: {total_quran}")

updated_count = 0
invalid_chars_count = 0
empty_count = 0
sample_records = []

# Regex for unexpected characters: Devanagari (\u0900-\u097F) or Arabic (\u0600-\u06FF)
dev_arab_regex = re.compile(r'[\u0900-\u097F\u0600-\u06FF]')

updates = []
for row_id, surah, ayah, hi_text in rows:
    if not hi_text or not hi_text.strip():
        empty_count += 1
        hinglish = ""
    else:
        hinglish = transliterate_text(hi_text)
        if dev_arab_regex.search(hinglish):
            invalid_chars_count += 1
            print(f"Warning: Found untransliterated char in [{surah}:{ayah}]: {hinglish[:80]}")
    
    updates.append((hinglish, row_id))

# Batch update
cursor.executemany("UPDATE content_items SET translation_hinglish = ? WHERE id = ?", updates)
conn.commit()

print(f"Successfully updated {len(updates)} Quran ayahs with Hinglish transliteration.")

# Verification queries
cursor.execute("SELECT count(*), count(translation_hinglish) FROM content_items WHERE type = 'quran'")
total, hing_count = cursor.fetchone()
print(f"Verification: total Quran = {total}, non-null translation_hinglish = {hing_count}")

cursor.execute("SELECT count(*) FROM content_items WHERE type = 'quran' AND (translation_hinglish IS NULL OR translation_hinglish = '')")
empty_hing = cursor.fetchone()[0]
print(f"Empty Hinglish count: {empty_hing}")

# Pick 50 well-distributed random ayahs for human review
random.seed(42)
sample_indices = sorted(random.sample(range(len(rows)), 50))
sample_output_path = 'tools/hinglish_50_sample.txt'

with open(sample_output_path, 'w', encoding='utf-8') as f:
    f.write("=== 50 RANDOM AYAH HINGLISH REVIEW SAMPLE ===\n\n")
    for idx in sample_indices:
        r_id, s, a, hi = rows[idx]
        hing = updates[idx][0]
        f.write(f"[{s}:{a}]\n")
        f.write(f"  HI:   {hi}\n")
        f.write(f"  HING: {hing}\n\n")

print(f"Saved 50 review sample ayahs to {sample_output_path}")

conn.close()
