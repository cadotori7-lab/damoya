# 사용자의 질문과 관련된 페이지를 검색하는 MCP 서버
from mcp.server.fastmcp import FastMCP

from config import MCP_SITE_SEARCH_HOST, MCP_SITE_SEARCH_PORT
from search.schemas import SearchPagesResult
from search.service import search_pages

# 사이트 검색 MCP 서버 생성
mcp = FastMCP(
    "damoya-site-search",
    instructions="다모여 사이트에서 사용자의 질문과 관련된 페이지를 검색합니다.",
    host=MCP_SITE_SEARCH_HOST,
    port=MCP_SITE_SEARCH_PORT,
    stateless_http=True,
    json_response=True,
)

# 사이트 검색 MCP 서버 도구 등록
@mcp.tool()
def search_site_pages(question: str, count: int = 3) -> SearchPagesResult:
    """질문과 관련된 다모여 사이트 페이지를 검색한다."""
    return SearchPagesResult(pages=search_pages(question, count))

# 사이트 검색 MCP 서버 실행
def main() -> None:
    """사이트 검색 MCP 서버를 단독 Streamable HTTP 서버로 실행한다."""
    mcp.run(transport="streamable-http")

# 사이트 검색 MCP 서버 실행
if __name__ == "__main__":
    main()
