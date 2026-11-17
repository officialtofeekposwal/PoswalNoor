import sqlite3

con = sqlite3.connect('D:/Desktop/Poswal Noor/app/src/main/assets/database/poswal_noor.db')
cur = con.cursor()

cur.execute("""
    SELECT DISTINCT book, surah, surah_name, COUNT(*) 
    FROM content_items 
    WHERE type = 'hadith' AND book = 'Sahih al-Bukhari'
    GROUP BY surah 
    ORDER BY surah ASC 
    LIMIT 6
""")
print("=== BUKHARI SAMPLE BOOKS ===")
for r in cur.fetchall():
    print(f"Collection: {r[0]}, Book #{r[1]} ('{r[2]}'): {r[3]} hadiths")

cur.execute("""
    SELECT DISTINCT book, surah, surah_name, COUNT(*) 
    FROM content_items 
    WHERE type = 'hadith' AND book = 'Sahih Muslim'
    GROUP BY surah 
    ORDER BY surah ASC 
    LIMIT 6
""")
print("\n=== MUSLIM SAMPLE BOOKS ===")
for r in cur.fetchall():
    print(f"Collection: {r[0]}, Book #{r[1]} ('{r[2]}'): {r[3]} hadiths")

cur.execute("""
    SELECT id, book, surah, surah_name, number, narrator, grade, SUBSTR(arabic, 1, 50), SUBSTR(translation_en, 1, 60)
    FROM content_items
    WHERE type = 'hadith'
    LIMIT 2
""")
print("\n=== SAMPLE HADITH ROWS ===")
for r in cur.fetchall():
    print(f"ID: {r[0]}, {r[1]} Book {r[2]} ({r[3]}) #{r[4]}")
    print(f"  Narrator: {r[5]}")
    print(f"  Grade: {r[6]}")
    print(f"  Arabic: {r[7]}...")
    print(f"  English: {r[8]}...")

con.close()
