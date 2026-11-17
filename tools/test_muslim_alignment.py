import os
import json

cache_dir = "tools/cache"
with open(os.path.join(cache_dir, "ara_muslim.json"), "r", encoding="utf-8") as f:
    ara = json.load(f)["hadiths"]

with open(os.path.join(cache_dir, "hadith_muslim.json"), "r", encoding="utf-8") as f:
    eng = json.load(f)["hadiths"]

print(f"Ara count: {len(ara)}, Eng count: {len(eng)}")
# Check match rate by hadithnumber
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

print(f"Matched with full Arabic: {matched}")
print(f"Missing or empty Arabic: {empty}")
print(f"Sample matched hadith: num={eng[50].get('hadithnumber')}")
print(f"  ENG: {eng[50].get('text')[:60]}...")
print(f"  ARA: {ara_dict.get(eng[50].get('hadithnumber'))[:60]}...")
