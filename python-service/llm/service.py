from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import ChatPromptTemplate

from config import OPENAI_MODEL, SITE_BASE_URL
from llm.elasticsearch_rag import search_pages
from llm.model import get_llm

STEP_MENU = "menu"
STEP_SITE_GUIDE = "site_guide"

MENU_REPLY = (
    "안녕하세요! 다모여 AI 도우미입니다.\n"
    "원하시는 번호를 입력해 주세요.\n\n"
    "1. 사이트 안내\n"
    "2. (준비 중)"
)

SITE_GUIDE_PROMPT = (
    "사이트 안내를 선택하셨습니다.\n"
    "어떤 안내를 받으시겠습니까?\n\n"
    "예) 로그인 화면을 보고 싶어 / 프로젝트 모집 페이지 / 멘토 가입하는 곳"
)

def create_chain():
    prompt = ChatPromptTemplate.from_messages(
        [
            (
                "system",
                "너는 다모여 사이트 안내 도우미다. 반드시 검색된 페이지 정보만 "
                "근거로 짧고 친절하게 답한다. 없는 경로나 기능은 만들지 않는다.",
            ),
            (
                "human",
                "[최근 대화]\n{history}\n\n"
                "[검색된 페이지]\n{context}\n\n"
                "[현재 질문]\n{question}",
            ),
        ]
    )
    return prompt | get_llm() | StrOutputParser()

# RAG 검색 결과를 근거로 답변과 페이지 링크를 만들기
def answer_site_guide(question: str, history: list[str] | None = None):
    recent_history = list(history or [])[-6:]
    search_question = " ".join(recent_history[-4:] + [question])
    pages = search_pages(search_question, count=3)

    if not pages:
        return "관련 페이지를 찾지 못했습니다.", [], "rag"

    context = "\n\n".join(page["content"] for page in pages)
    answer = create_chain().invoke(
        {
            "history": "\n".join(recent_history) or "이전 대화 없음",
            "context": context,
            "question": question,
        }
    )
    base_url = SITE_BASE_URL.rstrip("/")
    links = [
        {
            "title": page["title"],
            "path": page["path"],
            "url": f"{base_url}{page['path']}",
            "description": page["description"],
        }
        for page in pages
    ]
    return answer, links, OPENAI_MODEL

def _normalize(text: str) -> str:
    return (text or "").strip().lower().replace(" ", "")

def handle_chat(
    message: str,
    step: str | None = None,
    history: list[str] | None = None,
) -> dict:
    """Spring에서 전달한 메뉴 단계와 대화 기록으로 챗봇 응답을 만든다."""
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
        answer, links, model = answer_site_guide(text, history)
        return {
            "reply": answer + "\n\n메뉴로 돌아가려면 '메뉴'라고 입력하세요.",
            "model": model,
            "step": STEP_SITE_GUIDE,
            "links": links,
        }

    return {"reply": MENU_REPLY, "model": "rule", "step": STEP_MENU, "links": []}
