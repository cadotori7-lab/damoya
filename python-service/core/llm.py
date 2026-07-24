from typing import TYPE_CHECKING

from config import OPENAI_API_KEY, OPENAI_MODEL, OPENAI_TIMEOUT_SECONDS

if TYPE_CHECKING:
    from langchain_openai import ChatOpenAI

# 공통 설정을 적용한 LLM 클라이언트 생성
def get_llm() -> "ChatOpenAI":
    # OpenAI API 키가 설정되지 않은 경우 예외 발생
    if not OPENAI_API_KEY:
        raise RuntimeError("OPENAI_API_KEY가 설정되지 않았습니다.")
    
    from langchain_openai import ChatOpenAI

    return ChatOpenAI(
        model=OPENAI_MODEL,
        api_key=OPENAI_API_KEY,
        timeout=OPENAI_TIMEOUT_SECONDS,
        max_retries=2,
        temperature=0,
    )
