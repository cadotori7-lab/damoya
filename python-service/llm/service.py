from functools import lru_cache

from langchain_openai import ChatOpenAI

from config import OPENAI_API_KEY, OPENAI_MODEL, OPENAI_TIMEOUT_SECONDS


SYSTEM_PROMPT = (
    "당신은 대학생 팀 프로젝트 협업 플랫폼 다모여의 AI 비서입니다. "
    "사용자의 질문에 한국어로 간결하고 친절하게 답변하세요."
)


@lru_cache(maxsize=1)
def _get_chat_model() -> ChatOpenAI:
    if not OPENAI_API_KEY:
        raise RuntimeError("OPENAI_API_KEY가 설정되지 않았습니다.")

    return ChatOpenAI(
        model=OPENAI_MODEL,
        api_key=OPENAI_API_KEY,
        timeout=OPENAI_TIMEOUT_SECONDS,
        max_retries=2,
    )


def ask_gpt(message: str) -> str:
    response = _get_chat_model().invoke(
        [
            ("system", SYSTEM_PROMPT),
            ("human", message.strip()),
        ]
    )

    if isinstance(response.content, str):
        return response.content

    return response.text
