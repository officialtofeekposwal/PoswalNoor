import sqlite3

con = sqlite3.connect('D:/Desktop/Poswal Noor/app/src/main/assets/database/poswal_noor.db')
cur = con.cursor()
cur.execute("SELECT COUNT(*) FROM content_items WHERE type = 'hadith'")
tot = cur.fetchone()[0]

cur.execute("SELECT COUNT(*) FROM content_items WHERE type = 'hadith' AND (arabic IS NULL OR TRIM(arabic) = '')")
empty_ar = cur.fetchone()[0]

cur.execute("SELECT id, book, number, SUBSTR(arabic, 1, 60), SUBSTR(translation_en, 1, 60) FROM content_items WHERE type = 'hadith' LIMIT 5")
sample = cur.fetchall()

print(f"Total Hadiths: {tot}")
print(f"Hadiths with empty/null Arabic: {empty_ar}")
for s in sample:
    print(f"ID {s[0]} [{s[1]} #{s[2]}]:")
    print(f"  Arabic: '{s[3]}'")
    print(f"  English: '{s[4]}'")

con.close()
