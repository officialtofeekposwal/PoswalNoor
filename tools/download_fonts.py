import os
import urllib.request

FONTS = {
    "amiri_regular.ttf": "https://raw.githubusercontent.com/google/fonts/main/ofl/amiri/Amiri-Regular.ttf",
    "amiri_bold.ttf": "https://raw.githubusercontent.com/google/fonts/main/ofl/amiri/Amiri-Bold.ttf",
    "noto_nastaliq_urdu.ttf": "https://raw.githubusercontent.com/google/fonts/main/ofl/notonastaliqurdu/NotoNastaliqUrdu%5Bwght%5D.ttf",
    "noto_sans_devanagari.ttf": "https://raw.githubusercontent.com/google/fonts/main/ofl/notosansdevanagari/NotoSansDevanagari%5Bwdth%2Cwght%5D.ttf",
    "inter_variable.ttf": "https://raw.githubusercontent.com/google/fonts/main/ofl/inter/Inter%5Bopsz%2Cwght%5D.ttf"
}

output_dir = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "res", "font")
os.makedirs(output_dir, exist_ok=True)

for name, url in FONTS.items():
    dest = os.path.join(output_dir, name)
    if os.path.exists(dest) and os.path.getsize(dest) > 1000:
        print(f"Already exists: {name} ({os.path.getsize(dest)} bytes)")
        continue
    print(f"Downloading {name} from {url}...")
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req) as resp, open(dest, 'wb') as f:
            f.write(resp.read())
        print(f"Saved {dest} ({os.path.getsize(dest)} bytes)")
    except Exception as e:
        print(f"Failed to download {name}: {e}")
