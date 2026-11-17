import os
import json

cache_dir = "tools/cache"
with open(os.path.join(cache_dir, "ara_bukhari.json"), "r", encoding="utf-8") as f:
    ara = json.load(f)["hadiths"]

with open(os.path.join(cache_dir, "hadith_bukhari.json"), "r", encoding="utf-8") as f:
    eng = json.load(f)["hadiths"]

print(f"Bukhari Ara count: {len(ara)}, Eng count: {len(eng)}")
ara_dict = {h["hadithnumber"]: h.get("text", "").strip() for h in ara if h.get("text", "").strip()}

matched = 0
empty = 0
for h in eng:
    num = h.get("hadithnumber")
    ar_text = ara_dict.get(num, "")
    if len(ar_text) > 10:
        matched += 1
    else:
        empty += 1

print(f"Bukhari matched with full Arabic: {matched}")
print(f"Bukhari missing or empty Arabic: {empty}")
first_num = eng[0].get('hadithnumber')
print(f"Sample Bukhari Hadith #{first_num}:")
print(f"  ENG: {eng[0].get('text')[:60]}...")
print(f"  ARA: {ara_dict.get(first_num, '')[:60]}...")
