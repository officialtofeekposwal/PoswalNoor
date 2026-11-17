import json

with open("tools/cache/hadith_bukhari.json", "r", encoding="utf-8") as f:
    data = json.load(f)

print("Metadata:", data.get("metadata"))
hadiths = data.get("hadiths", [])
for i in range(3):
    print(f"\nHadith {i}:")
    print(json.dumps(hadiths[i], indent=2, ensure_ascii=False))
