import urllib.request
import json

urls = [
    "https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/editions/ara-bukhari.json",
    "https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/editions/ara-muslim.json",
    "https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/editions/ara-bukhari1.json"
]

for url in urls:
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req, timeout=10) as resp:
            data = json.loads(resp.read().decode('utf-8'))
            hadiths = data.get("hadiths", [])
            print(f"URL: {url}")
            print(f"  Metadata: {data.get('metadata', {}).get('name')}")
            print(f"  Count: {len(hadiths)}")
            if hadiths:
                first = hadiths[0]
                print(f"  Hadith 1: number={first.get('hadithnumber')}, text={first.get('text')[:80]}...")
    except Exception as e:
        print(f"URL: {url} -> FAILED: {e}")
