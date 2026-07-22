import os
import sys
from pathlib import Path
from dotenv import load_dotenv

for _stream in (sys.stdout, sys.stderr):
    try:
        _stream.reconfigure(encoding="utf-8")
    except Exception:
        pass

BASE_DIR = Path(__file__).resolve().parents[1]
load_dotenv(BASE_DIR / ".env")

HOST = os.getenv("FASTAPI_HOST", "0.0.0.0")
PORT = int(os.getenv("FASTAPI_PORT", "8000"))
OCR_LANGS = [lang.strip() for lang in os.getenv("OCR_LANGS", "ko,en").split(",") if lang.strip()]
MATCH_THRESHOLD = float(os.getenv("MATCH_THRESHOLD", "0.8"))

OPENAI_API_KEY = os.getenv("OPENAI_API_KEY", "").strip()
OPENAI_MODEL = os.getenv("OPENAI_MODEL", "gpt-5.4-mini").strip()
OPENAI_TIMEOUT_SECONDS = float(os.getenv("OPENAI_TIMEOUT_SECONDS", "30"))
