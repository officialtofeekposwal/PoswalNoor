import sqlite3

con = sqlite3.connect('app/src/main/assets/database/poswal_noor.db')
cur = con.cursor()
topics = ['namaz', 'sabr', 'akhlaq', 'parents', 'dua', 'akhirah', 'ilm', 'charity', 'family']
print(f"{'Topic':<12} | {'Quran Count':<12} | {'Hadith Count':<12} | {'Total':<8}")
print('-' * 52)
for t in topics:
    cur.execute("SELECT COUNT(*) FROM content_items WHERE type = 'quran' AND topic = ?", (t,))
    q_cnt = cur.fetchone()[0]
    cur.execute("SELECT COUNT(*) FROM content_items WHERE type = 'hadith' AND topic = ?", (t,))
    h_cnt = cur.fetchone()[0]
    flag = " (<50)" if (q_cnt < 50 or h_cnt < 50) else ""
    print(f"{t:<12} | {q_cnt:<12} | {h_cnt:<12} | {q_cnt+h_cnt:<8}{flag}")
con.close()
