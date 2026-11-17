import sqlite3
import random

con = sqlite3.connect('D:/Desktop/Poswal Noor/app/src/main/assets/database/poswal_noor.db')
cur = con.cursor()

topics = ["namaz", "sabr", "akhlaq", "parents", "dua", "akhirah", "ilm", "charity", "family"]

print("=" * 80)
print("AUDIT: 20 RANDOM QURAN & 20 RANDOM HADITH ITEMS PER TOPIC")
print("=" * 80)

for t in topics:
    print(f"\n################ TOPIC: {t.upper()} ################")
    
    # 20 Quran
    cur.execute("SELECT id, reference, SUBSTR(translation_en, 1, 90) FROM content_items WHERE type = 'quran' AND topic = ?", (t,))
    q_all = cur.fetchall()
    q_sample = random.sample(q_all, min(20, len(q_all)))
    print(f"\n--- [Quran: {len(q_all)} total, showing {len(q_sample)}] ---")
    for row in q_sample:
        print(f"  [Q-{row[0]}] {row[1]}: {row[2]}...")

    # 20 Hadith
    cur.execute("SELECT id, reference, SUBSTR(translation_en, 1, 90) FROM content_items WHERE type = 'hadith' AND topic = ?", (t,))
    h_all = cur.fetchall()
    h_sample = random.sample(h_all, min(20, len(h_all)))
    print(f"\n--- [Hadith: {len(h_all)} total, showing {len(h_sample)}] ---")
    for row in h_sample:
        print(f"  [H-{row[0]}] {row[1]}: {row[2]}...")

con.close()
