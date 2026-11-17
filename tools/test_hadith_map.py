import os
import json

cache_dir = "tools/cache"
os.makedirs(cache_dir, exist_ok=True)

# Test matching arabic by hadith number
ara_bukhari_path = os.path.join(cache_dir, "ara_bukhari.json")
if not os.path.exists(ara_bukhari_path):
    import urllib.request
    print("Downloading ara-bukhari.json ...")
    urllib.request.urlretrieve("https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/editions/ara-bukhari.json", ara_bukhari_path)

with open(ara_bukhari_path, "r", encoding="utf-8") as f:
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

print(f"Loaded {len(ara_map)} Arabic Bukhari entries.")
print(f"Arabic Hadith 1 preview: {ara_map.get(1, 'NOT FOUND')[:100]}...")
