import urllib.request
import json

def fetch_url(url):
    req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
    try:
        with urllib.request.urlopen(req, timeout=10) as r:
            return r.read().decode('utf-8', errors='ignore')
    except Exception as e:
        return f"ERROR: {e}"

license_text = fetch_url("https://raw.githubusercontent.com/fawazahmed0/hadith-api/1/LICENSE")
print("=== LICENSE FILE ===")
print(license_text[:500])

readme_text = fetch_url("https://raw.githubusercontent.com/fawazahmed0/hadith-api/1/README.md")
print("\n=== README (Translation credits / license info) ===")
for line in readme_text.splitlines():
    if any(k in line.lower() for k in ["license", "source", "translation", "bukhari", "muslim", "credit", "copyright"]):
        print("  ", line)

editions_text = fetch_url("https://raw.githubusercontent.com/fawazahmed0/hadith-api/1/editions.json")
try:
    editions = json.loads(editions_text)
    print("\n=== EDITIONS METADATA ===")
    for k in ["eng-bukhari", "eng-muslim", "ara-bukhari", "ara-muslim"]:
        if k in editions:
            print(k, ":", editions[k])
except Exception as e:
    print("Editions parse error:", e)
