#!/usr/bin/env python3
"""Synchronize CACTUS Collection public version metadata."""
from pathlib import Path
import re

VERSION_CODE = 3
VERSION_NAME = "2.0.0"
ROOT = Path(__file__).resolve().parents[1]
BUILD_FILE = ROOT / "app" / "build.gradle.kts"

text = BUILD_FILE.read_text(encoding="utf-8")
text = re.sub(r"versionCode\s*=\s*\d+", f"versionCode = {VERSION_CODE}", text, count=1)
text = re.sub(r'versionName\s*=\s*"[^"]+"', f'versionName = "{VERSION_NAME}"', text, count=1)
BUILD_FILE.write_text(text, encoding="utf-8")
print(f"CACTUS Collection {VERSION_NAME} / versionCode {VERSION_CODE} ready")
