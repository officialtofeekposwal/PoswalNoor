#!/usr/bin/env python3
"""
Poswal Noor — Room Schema Hash Sync & Validator
Extracts Room's generated identity hash from NoorDatabase_Impl.java and syncs it into poswal_noor.db.
Prevents any Room pre-packaged database schema validation crash.
"""

import os
import re
import sqlite3

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.dirname(SCRIPT_DIR)
DB_PATH = os.path.join(PROJECT_ROOT, "app", "src", "main", "assets", "database", "poswal_noor.db")
IMPL_PATH = os.path.join(PROJECT_ROOT, "app", "build", "generated", "ksp", "debug", "java", "com", "poswal", "noor", "data", "local", "NoorDatabase_Impl.java")

def get_room_identity_hash():
    if not os.path.exists(IMPL_PATH):
        print(f"Warning: NoorDatabase_Impl.java not found at {IMPL_PATH}. Run `./gradlew compileDebugKotlin` first.")
        return None
    with open(IMPL_PATH, "r", encoding="utf-8") as f:
        content = f.read()
    match = re.search(r'VALUES\(42,\s*[\'"]([a-f0-9]+)[\'"]\)', content)
    if match:
        return match.group(1)
    return None

def sync_hash():
    expected_hash = get_room_identity_hash()
    if not expected_hash:
        return False

    print(f"Room Expected Identity Hash: {expected_hash}")
    if not os.path.exists(DB_PATH):
        print(f"Error: {DB_PATH} not found.")
        return False

    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    cur.execute("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY, identity_hash TEXT);")
    current = cur.execute("SELECT identity_hash FROM room_master_table WHERE id = 42").fetchone()
    current_hash = current[0] if current else None

    if current_hash != expected_hash:
        print(f"Updating room_master_table: {current_hash} -> {expected_hash}")
        cur.execute("INSERT OR REPLACE INTO room_master_table (id, identity_hash) VALUES(42, ?);", (expected_hash,))
        conn.commit()
        print("✅ Synced successfully.")
    else:
        print("✅ Database already has matching Room identity hash.")

    conn.close()
    return True

if __name__ == "__main__":
    sync_hash()
