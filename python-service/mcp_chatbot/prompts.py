from langchain_core.prompts import ChatPromptTemplate

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


def create_site_guide_prompt() -> ChatPromptTemplate:
    return ChatPromptTemplate.from_messages(
        [
            (
                "system",
                "너는 다모여 사이트 안내 도우미다. 반드시 MCP 검색 도구가 제공한 "
                "페이지 정보만 근거로 짧고 친절하게 답한다. "
                "검색 결과에 없는 경로나 기능은 만들지 않는다.",
            ),
            (
                "human",
                "[최근 대화]\n{history}\n\n"
                "[MCP 검색 결과]\n{context}\n\n"
                "[현재 질문]\n{question}",
            ),
        ]
    )
