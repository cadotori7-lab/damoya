# 사이트 안내 MCP 도구/리소스/프롬프트 관리

from typing import Any

from mcp.server.fastmcp import FastMCP

from search.chatbot_store import SearchPagesResult, search_pages

# MCP 서버 객체 생성
mcp = FastMCP(
    "damoya-site-search",
    instructions="다모여 사이트에서 사용자의 질문과 관련된 페이지를 검색합니다.",
)

# LLM이 어떤 Tool을 어떻게 호출해야 하는지 알려주는 안내문
SITE_SEARCH_GUIDE = """
# 다모여 사이트 안내 도구 사용 규칙
- search_site_pages 도구로 사용자의 질문과 관련된 페이지를 검색합니다.
- question에는 사용자가 찾는 화면이나 기능을 자연어로 넣습니다. 예: `로그인 화면`, `프로젝트 모집`
- 페이지 제목, 경로(path), 설명은 도구 결과에 있는 값만 답변에 사용합니다.
- 검색 결과에 없는 경로나 기능은 만들지 않습니다.
- 후속 질문은 최근 도구 결과를 먼저 참고하고, 새로운 화면을 물으면 다시 검색합니다.
""".strip()


# MCP 리소스 등록
@mcp.resource("guide://damoya/site-search")
def site_search_guide() -> str:
    """사이트 검색 도구의 인자 규칙과 답변 근거 규칙이다."""
    return SITE_SEARCH_GUIDE


# MCP 프롬프트 등록
@mcp.prompt()
def site_guide_prompt(tone: str = "친절하고 간결한") -> str:
    """사이트 안내 상담에 사용할 프롬프트 템플릿이다."""
    return (
        f"{tone} 말투로 다모여 사이트 안내를 진행하세요. "
        "사용자가 찾는 화면이나 기능이 있으면 search_site_pages 도구로 검색하고, "
        "검색된 페이지의 제목과 경로(path)를 근거로 안내하세요. "
        "검색 결과에 없는 경로나 기능은 만들지 마세요."
    )


# MCP 도구 등록: 사이트 페이지 검색
@mcp.tool()
def search_site_pages(question: str, count: int = 3) -> dict[str, Any]:
    """질문과 관련된 다모여 사이트 페이지를 검색한다."""
    if count < 1:
        count = 1
    elif count > 10:
        count = 10

    pages = search_pages(question, count)
    return SearchPagesResult(pages=pages).model_dump()


def warm_up() -> None:
    """임베딩 모델과 사이트 인덱스를 미리 로드해 첫 Tool 호출 지연을 줄인다."""
    # stdio 프로토콜은 stdout을 쓰므로, 모델 로드 로그는 stderr로 보낸다.
    import sys

    from search.chatbot_store import get_embedding_model, prepare_index

    original_stdout = sys.stdout
    sys.stdout = sys.stderr
    try:
        get_embedding_model()
        prepare_index()
    finally:
        sys.stdout = original_stdout


if __name__ == "__main__":
    warm_up()
    mcp.run(transport="stdio")
