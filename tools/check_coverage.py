import sqlite3

conn = sqlite3.connect('app/src/main/assets/database/poswal_noor.db')
cur = conn.cursor()

total = cur.execute('SELECT COUNT(*) FROM content_items').fetchone()[0]
quran_total = cur.execute("SELECT COUNT(*) FROM content_items WHERE type='quran'").fetchone()[0]
hadith_total = cur.execute("SELECT COUNT(*) FROM content_items WHERE type='hadith'").fetchone()[0]

en = cur.execute("SELECT COUNT(*) FROM content_items WHERE translation_en IS NOT NULL AND TRIM(translation_en) != ''").fetchone()[0]
hi = cur.execute("SELECT COUNT(*) FROM content_items WHERE translation_hi IS NOT NULL AND TRIM(translation_hi) != ''").fetchone()[0]
ur = cur.execute("SELECT COUNT(*) FROM content_items WHERE translation_ur IS NOT NULL AND TRIM(translation_ur) != ''").fetchone()[0]
hing = cur.execute("SELECT COUNT(*) FROM content_items WHERE translation_hinglish IS NOT NULL AND TRIM(translation_hinglish) != ''").fetchone()[0]

print("=" * 60)
print(f"TOTAL ROWS IN DB: {total:,}")
print(f"  - Quran Ayahs:  {quran_total:,}")
print(f"  - Sahih Hadith: {hadith_total:,}")
print("-" * 60)
print(f"English:  {en:,} / {total:,} ({en/total*100:.2f}%)")
print(f"Hindi:    {hi:,} / {total:,} ({hi/total*100:.2f}%) -> Quran: {hi/quran_total*100:.1f}%, Hadith: {hi-quran_total}")
print(f"Urdu:     {ur:,} / {total:,} ({ur/total*100:.2f}%) -> Quran: {ur/quran_total*100:.1f}%, Hadith: {ur-quran_total}")
print(f"Hinglish: {hing:,} / {total:,} ({hing/total*100:.2f}%) [Curated/Verified, falls back to English]")
print("=" * 60)
conn.close()
