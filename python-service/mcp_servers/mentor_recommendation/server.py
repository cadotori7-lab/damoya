from mcp.server.fastmcp import FastMCP

from config import (
    MCP_MENTOR_RECOMMENDATION_HOST,
    MCP_MENTOR_RECOMMENDATION_PORT,
)
from search.mentor_schemas import MentorCandidatesResult
from search.mentor_service import search_mentor_candidates

mcp = FastMCP(
    "damoya-mentor-recommendation",
    instructions=(
        "프로젝트 이름과 설명을 임베딩하고 Elasticsearch에서 "
        "프로젝트와 유사한 전문성·경력을 가진 멘토 후보를 검색합니다."
    ),
    host=MCP_MENTOR_RECOMMENDATION_HOST,
    port=MCP_MENTOR_RECOMMENDATION_PORT,
    stateless_http=True,
    json_response=True,
)

# 프로젝트와 유사한 전문성·경력을 가진 멘토 후보를 검색
@mcp.tool()
def find_mentor_candidates(
    project_name: str,
    project_description: str,
    count: int = 10,
) -> MentorCandidatesResult:
    """프로젝트와 유사한 전문성·경력을 가진 멘토 후보를 검색한다."""
    candidates, indexed_count = search_mentor_candidates(
        project_name,
        project_description,
        count,
    )
    return MentorCandidatesResult(
        candidates=candidates,
        indexed_count=indexed_count,
    )

def main() -> None:
    mcp.run(transport="streamable-http")

if __name__ == "__main__":
    main()
