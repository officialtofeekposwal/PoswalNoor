# -*- coding: utf-8 -*-
import sqlite3
import re
import unicodedata

conn = sqlite3.connect('app/src/main/assets/database/poswal_noor.db')
cursor = conn.cursor()

cursor.execute("SELECT id, surah, ayah, translation_hinglish FROM content_items WHERE type = 'quran'")
rows = cursor.fetchall()

non_ascii_chars = set()
bad_ayahs = []

for r_id, s, a, hing in rows:
    if not hing:
        continue
    # Find all characters outside ASCII printable range (32 to 126)
    bad = [ch for ch in hing if ord(ch) > 127]
    if bad:
        non_ascii_chars.update(bad)
        bad_ayahs.append((s, a, "".join(bad), hing))

print(f"Total ayahs with non-ASCII chars: {len(bad_ayahs)} out of {len(rows)}")
print("Distinct non-ASCII characters found:")
for ch in sorted(non_ascii_chars):
    print(f"  U+{ord(ch):04X} ({unicodedata.name(ch, 'UNKNOWN')}) : '{ch}'")

print("\nSample 10 bad ayahs:")
for s, a, bad_str, hing in bad_ayahs[:10]:
    print(f"[{s}:{a}] ({bad_str}): {hing[:100]}")
