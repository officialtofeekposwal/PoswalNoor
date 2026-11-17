import os

for f in ["hadith_bukhari.json", "hadith_muslim.json", "ara_bukhari.json", "ara_muslim.json"]:
    p = os.path.join("tools/cache", f)
    if os.path.exists(p):
        print(f"{f}: {os.path.getsize(p)} bytes")
    else:
        print(f"{f}: NOT FOUND")
