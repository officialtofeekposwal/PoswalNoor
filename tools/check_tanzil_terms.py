import urllib.request
import re

urls = [
    "http://tanzil.net/wiki/Tanzil_Project_Copyright",
    "http://tanzil.net/trans/",
    "http://tanzil.net/download/"
]

for url in urls:
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req, timeout=10) as r:
            html = r.read().decode('utf-8', errors='ignore')
            print(f"=== URL: {url} ===")
            # Look for copyright / license paragraphs
            for match in re.findall(r'(?:Copyright|License|Terms|permission)[^<]{30,200}', html, re.I):
                print("  ->", match.strip())
    except Exception as e:
        print(f"FAILED {url}: {e}")
