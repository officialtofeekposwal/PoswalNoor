import sqlite3

db_path = "app/src/main/assets/database/poswal_noor.db"
conn = sqlite3.connect(db_path)
cur = conn.cursor()

print("=== 1. TOPIC-WISE ITEM COUNTS (Quran vs Hadith) ===")
cur.execute("""
    SELECT 
        COALESCE(topic, 'untagged') AS t,
        SUM(CASE WHEN type = 'quran' THEN 1 ELSE 0 END) AS quran_cnt,
        SUM(CASE WHEN type = 'hadith' THEN 1 ELSE 0 END) AS hadith_cnt,
        COUNT(*) AS total_cnt
    FROM content_items
    GROUP BY topic
    ORDER BY total_cnt DESC
""")
rows = cur.fetchall()
print(f"{'Topic':<15} | {'Quran':<8} | {'Hadith':<8} | {'Total':<8} | {'Flag (<50)'}")
print("-" * 55)
for t, q, h, tot in rows:
    flag = "⚠️ UNDER 50" if tot < 50 else "✅ OK"
    print(f"{t:<15} | {q:<8} | {h:<8} | {tot:<8} | {flag}")

print("\n=== 2. HINGLISH TRANSLATION AUDIT ===")
cur.execute("SELECT COUNT(*) FROM content_items WHERE translation_hinglish IS NOT NULL AND TRIM(translation_hinglish) != ''")
hinglish_cnt = cur.fetchone()[0]
cur.execute("SELECT COUNT(*) FROM content_items")
total_cnt = cur.fetchone()[0]
print(f"Total Content Items: {total_cnt}")
print(f"Items with non-null Hinglish: {hinglish_cnt} ({(hinglish_cnt/total_cnt)*100:.2f}%)")

cur.execute("SELECT type, COUNT(*) FROM content_items WHERE translation_hinglish IS NOT NULL AND TRIM(translation_hinglish) != '' GROUP BY type")
for r in cur.fetchall():
    print(f"  - {r[0]}: {r[1]} items")

conn.close()
