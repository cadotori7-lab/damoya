import os
import sys
from pathlib import Path
from dotenv import load_dotenv

# 표준 출력과 오류 출력을 UTF-8로 설정
for _stream in (sys.stdout, sys.stderr):
    try:
        _stream.reconfigure(encoding="utf-8")
    except Exception:
        pass

# 프로젝트 루트 디렉토리 경로 설정
BASE_DIR = Path(__file__).resolve().parents[1]
# .env 파일 로드
load_dotenv(BASE_DIR / ".env")

# 환경 변수 설정
HOST = os.getenv("FASTAPI_HOST", "0.0.0.0") # FastAPI 애플리케이션 호스트
PORT = int(os.getenv("FASTAPI_PORT", "8001")) # FastAPI 애플리케이션 포트
OCR_LANGS = [lang.strip() for lang in os.getenv("OCR_LANGS", "ko,en").split(",") if lang.strip()] # OCR 언어 설정
MATCH_THRESHOLD = float(os.getenv("MATCH_THRESHOLD", "0.8")) # 이미지 명의 매칭 임계값(높을수록 매칭 정확도 증가)

OPENAI_API_KEY = os.getenv("OPENAI_API_KEY", "").strip() # OpenAI API 키 설정
OPENAI_MODEL = os.getenv("OPENAI_MODEL", "gpt-5.4-mini").strip() # OpenAI 모델 설정
OPENAI_TIMEOUT_SECONDS = float(os.getenv("OPENAI_TIMEOUT_SECONDS", "30")) # OpenAI 요청 제한 시간

# 챗봇 '사이트 안내'에서 돌려줄 프론트 기준 URL
SITE_BASE_URL = os.getenv("SITE_BASE_URL", "http://localhost:8080/damoya").rstrip("/")

# Elasticsearch 설정
ELASTICSEARCH_URL = os.getenv("ELASTICSEARCH_URL", "http://localhost:9200").strip() # Elasticsearch URL 설정
ELASTICSEARCH_USERNAME = os.getenv("ELASTICSEARCH_USERNAME", "").strip() # Elasticsearch 사용자 이름 설정
ELASTICSEARCH_PASSWORD = os.getenv("ELASTICSEARCH_PASSWORD", "").strip() # Elasticsearch 비밀번호 설정
ELASTICSEARCH_SITE_INDEX = os.getenv("ELASTICSEARCH_SITE_INDEX", "damoya-site-pages-rag2").strip() # Elasticsearch 인덱스 설정
ELASTICSEARCH_MENTOR_INDEX = os.getenv(
    "ELASTICSEARCH_MENTOR_INDEX",
    os.getenv("ELASTICSEARCH_MENTOR_SOURCE_INDEX", "damoya-mentors"),
).strip()

# 기본값은 FastAPI 애플리케이션에 마운트된 사이트 검색 MCP 서버를 사용
MCP_SITE_SEARCH_URL = os.getenv(
    "MCP_SITE_SEARCH_URL",
    f"http://127.0.0.1:{PORT}/mcp/site-search/mcp",
).strip()
MCP_MENTOR_RECOMMENDATION_URL = os.getenv(
    "MCP_MENTOR_RECOMMENDATION_URL",
    f"http://127.0.0.1:{PORT}/mcp/mentor-recommendation/mcp",
).strip()
MCP_CLIENT_TIMEOUT_SECONDS = float(os.getenv("MCP_CLIENT_TIMEOUT_SECONDS", "90"))

# 사이트 검색 MCP 서버를 단독 실행할 때 사용하는 주소
MCP_SITE_SEARCH_HOST = os.getenv("MCP_SITE_SEARCH_HOST", "127.0.0.1").strip()
MCP_SITE_SEARCH_PORT = int(os.getenv("MCP_SITE_SEARCH_PORT", "8002"))
MCP_MENTOR_RECOMMENDATION_HOST = os.getenv(
    "MCP_MENTOR_RECOMMENDATION_HOST",
    "127.0.0.1",
).strip()
MCP_MENTOR_RECOMMENDATION_PORT = int(
    os.getenv("MCP_MENTOR_RECOMMENDATION_PORT", "8003")
)
