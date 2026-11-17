import os
import json
import urllib.request

cache_dir = "tools/cache"
os.makedirs(cache_dir, exist_ok=True)

ara_muslim_path = os.path.join(cache_dir, "ara_muslim.json")
if not os.path.exists(ara_muslim_path):
    print("Downloading ara-muslim.json ...")
    urllib.request.urlretrieve("https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/editions/ara-muslim.json", ara_muslim_path)

with open(ara_muslim_path, "r", encoding="utf-8") as f:
    ara_data = json.load(f)

ara_map = {}
for h in ara_data.get("hadiths", []):
    num = h.get("hadithnumber")
    txt = h.get("text", "").strip()
    if num is not None and txt:
        ara_map[num] = txt
        ara_map[str(num)] = txt
        if isinstance(num, float) and num.is_integer():
            ara_map[int(num)] = txt
            ara_map[str(int(num))] = txt

print(f"Loaded {len(ara_map)} Arabic Muslim entries.")
# Test first few hadiths
for n in [1, 2, 8, 93]:
    sample = ara_map.get(n)
    if sample:
        print(f"Hadith {n}: {sample[:60]}...")
    else:
        print(f"Hadith {n}: NOT FOUND")
