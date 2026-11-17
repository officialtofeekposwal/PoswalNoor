import urllib.request
import re

req = urllib.request.Request("http://tanzil.net/download/", headers={'User-Agent': 'Mozilla/5.0'})
with urllib.request.urlopen(req) as r:
    html = r.read().decode('utf-8', errors='ignore')

# find "Terms of Use" or "Permission"
idx = html.find("Permission is granted")
if idx != -1:
    print(html[idx:idx+500])
else:
    print("Not found")
