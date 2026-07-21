# LLM 모델 연결
from langchain_openai import ChatOpenAI
from config import OPENAI_API_KEY, OPENAI_MODEL, OPENAI_TIMEOUT_SECONDS

def get_llm() -> ChatOpenAI:
    if not OPENAI_API_KEY:
        raise RuntimeError("OPENAI_API_KEY가 설정되지 않았습니다.")

    return ChatOpenAI(
        model=OPENAI_MODEL,
        api_key=OPENAI_API_KEY,
        timeout=OPENAI_TIMEOUT_SECONDS,
        max_retries=2,
        temperature=0,
    )
