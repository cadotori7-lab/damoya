import os
import sys
from dotenv import load_dotenv

for _stream in (sys.stdout, sys.stderr):
    try:
        _stream.reconfigure(encoding="utf-8")
    except Exception:
        pass

load_dotenv()

HOST = os.getenv("FASTAPI_HOST", "0.0.0.0")
PORT = int(os.getenv("FASTAPI_PORT", "8000"))
OCR_LANGS = [lang.strip() for lang in os.getenv("OCR_LANGS", "ko,en").split(",") if lang.strip()]
MATCH_THRESHOLD = float(os.getenv("MATCH_THRESHOLD", "0.8"))
