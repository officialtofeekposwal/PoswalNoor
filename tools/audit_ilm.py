import sqlite3
import random

con = sqlite3.connect('D:/Desktop/Poswal Noor/app/src/main/assets/database/poswal_noor.db')
cur = con.cursor()

print("################ TOPIC: ILM (QURAN SAMPLES) ################")
cur.execute("SELECT id, reference, translation_en FROM content_items WHERE type = 'quran' AND topic = 'ilm'")
rows = cur.fetchall()
print(f"Total Quran items tagged with ilm: {len(rows)}")

# Let's see how many have 'know' vs 'knowledge' vs 'wisdom'
know_cnt = sum(1 for r in rows if 'know' in r[2].lower() and 'knowledge' not in r[2].lower() and 'wisdom' not in r[2].lower())
knowledge_cnt = sum(1 for r in rows if 'knowledge' in r[2].lower())
wisdom_cnt = sum(1 for r in rows if 'wisdom' in r[2].lower())
print(f"Verses containing 'knowledge': {knowledge_cnt}")
print(f"Verses containing 'wisdom': {wisdom_cnt}")
print(f"Verses containing ONLY general 'know' (e.g. Allah knows, you know not): {know_cnt}")

print("\n--- 10 Samples with ONLY general 'know': ---")
sample_know = [r for r in rows if 'know' in r[2].lower() and 'knowledge' not in r[2].lower() and 'wisdom' not in r[2].lower()][:10]
for r in sample_know:
    print(f"[{r[1]}]: {r[2]}")

print("\n--- 10 Samples with authentic 'knowledge' / 'wisdom': ---")
sample_real = [r for r in rows if 'knowledge' in r[2].lower() or 'wisdom' in r[2].lower()][:10]
for r in sample_real:
    print(f"[{r[1]}]: {r[2]}")

con.close()
