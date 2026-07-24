from typing import Any, TypedDict

from langchain_core.output_parsers import StrOutputParser

from config import MCP_SITE_SEARCH_URL, OPENAI_MODEL, SITE_BASE_URL
from core.llm import get_llm
from core.mcp_client import call_mcp_tool
from mcp_chatbot.prompts import (
    MENU_REPLY,
    SITE_GUIDE_PROMPT,
    create_site_guide_prompt,
)

STEP_MENU = "menu"
STEP_SITE_GUIDE = "site_guide"


class ChatResult(TypedDict):
    reply: str
    model: str
    step: str
    links: list[dict[str, str]]


def _normalize(text: str) -> str:
    return (text or "").strip().lower().replace(" ", "")


def _extract_pages(payload: dict[str, Any]) -> list[dict[str, Any]]:
    pages = payload.get("pages")
    if pages is None and isinstance(payload.get("result"), dict):
        pages = payload["result"].get("pages")
    if not isinstance(pages, list):
        raise RuntimeError("사이트 검색 MCP 응답 형식이 올바르지 않습니다.")
    return [page for page in pages if isinstance(page, dict)]


def create_chain():
    return create_site_guide_prompt() | get_llm() | StrOutputParser()


async def answer_site_guide(
    question: str,
    history: list[str] | None = None,
) -> tuple[str, list[dict[str, str]], str]:
    """MCP 검색 결과를 근거로 답변과 페이지 링크를 만든다."""
    recent_history = list(history or [])[-6:]
    search_question = " ".join(recent_history[-4:] + [question])
    payload = await call_mcp_tool(
        MCP_SITE_SEARCH_URL,
        "search_site_pages",
        {"question": search_question, "count": 3},
    )
    pages = _extract_pages(payload)

    if not pages:
        return "관련 페이지를 찾지 못했습니다.", [], "rag"

    context = "\n\n".join(str(page.get("content", "")) for page in pages)
    answer = await create_chain().ainvoke(
        {
            "history": "\n".join(recent_history) or "이전 대화 없음",
            "context": context,
            "question": question,
        }
    )
    base_url = SITE_BASE_URL.rstrip("/")
    links = [
        {
            "title": str(page.get("title", "")),
            "path": str(page.get("path", "")),
            "url": f"{base_url}{page.get('path', '')}",
            "description": str(page.get("description", "")),
        }
        for page in pages
    ]
    return answer, links, OPENAI_MODEL


async def handle_chat(
    message: str,
    step: str | None = None,
    history: list[str] | None = None,
) -> ChatResult:
    """메뉴 단계와 대화 기록으로 챗봇 응답을 만든다."""
    current = (step or STEP_MENU).strip() or STEP_MENU
    text = (message or "").strip()
    normalized = _normalize(text)

    if not text or normalized in {"메뉴", "처음", "처음으로", "0", "menu"}:
        return {"reply": MENU_REPLY, "model": "rule", "step": STEP_MENU, "links": []}

    if current == STEP_MENU:
        if normalized in {"1", "사이트안내", "1.사이트안내", "1사이트안내"}:
            return {
                "reply": SITE_GUIDE_PROMPT,
                "model": "rule",
                "step": STEP_SITE_GUIDE,
                "links": [],
            }
        if normalized in {"2", "2번"}:
            reply = "2번은 아직 준비 중입니다.\n\n" + MENU_REPLY
        else:
            reply = "메뉴 번호로 선택해 주세요.\n\n" + MENU_REPLY
        return {"reply": reply, "model": "rule", "step": STEP_MENU, "links": []}

    if current == STEP_SITE_GUIDE:
        answer, links, model = await answer_site_guide(text, history)
        return {
            "reply": answer + "\n\n메뉴로 돌아가려면 '메뉴'라고 입력하세요.",
            "model": model,
            "step": STEP_SITE_GUIDE,
            "links": links,
        }

    return {"reply": MENU_REPLY, "model": "rule", "step": STEP_MENU, "links": []}
