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

# 챗봇 '사이트 안내'에서 돌려줄 프론트 기준 URL
SITE_BASE_URL = os.getenv("SITE_BASE_URL", "http://localhost:8080").rstrip("/")

ELASTICSEARCH_URL = os.getenv("ELASTICSEARCH_URL", "http://localhost:9200").strip()
ELASTICSEARCH_USERNAME = os.getenv("ELASTICSEARCH_USERNAME", "").strip()
ELASTICSEARCH_PASSWORD = os.getenv("ELASTICSEARCH_PASSWORD", "").strip()
ELASTICSEARCH_SITE_INDEX = os.getenv(
    "ELASTICSEARCH_SITE_INDEX",
    "damoya-site-pages-rag2",
).strip()
