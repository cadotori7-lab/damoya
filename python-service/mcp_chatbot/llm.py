from config import OPENAI_API_KEY, OPENAI_MODEL, OPENAI_TIMEOUT_SECONDS
from langchain_openai import ChatOpenAI

# OpenAI GPT 모델 설정 (환경변수 OPENAI_API_KEY 필요)
MODEL_NAME = OPENAI_MODEL


# llm 객체 생성
def get_llm(
    temperature: float = 0.2,
    max_retries: int = 2,
) -> ChatOpenAI:
    if not OPENAI_API_KEY:
        raise RuntimeError(
            "OPENAI_API_KEY가 없습니다. "
            "저장소 루트 .env 또는 환경변수에 키를 설정하세요."
        )
    return ChatOpenAI(
        model=MODEL_NAME,
        temperature=temperature,
        api_key=OPENAI_API_KEY,
        timeout=OPENAI_TIMEOUT_SECONDS,
        max_retries=max_retries,
    )
