# 멘토 추천 MCP 도구/리소스/프롬프트 관리

from typing import Any

from mcp.server.fastmcp import FastMCP

from search.mentor_store import MentorCandidatesResult, search_mentor_candidates

# MCP 서버 객체 생성
mcp = FastMCP(
    "damoya-mentor-recommendation",
    instructions=(
        "프로젝트 이름과 설명을 임베딩하고 Elasticsearch에서 "
        "프로젝트와 유사한 전문성·경력을 가진 멘토 후보를 검색합니다."
    ),
)

# LLM이 어떤 Tool을 어떻게 호출해야 하는지 알려주는 안내문
MENTOR_RECOMMENDATION_GUIDE = """
# 멘토 추천 도구 사용 규칙
- find_mentor_candidates 도구로 프로젝트와 어울리는 멘토 후보를 검색합니다.
- project_name에는 프로젝트 이름, project_description에는 프로젝트 설명을 넣습니다.
- 멘토 이름, 분야, 경력, 자격증 등은 도구 결과에 있는 값만 답변에 사용합니다.
- 검색 결과에 없는 멘토나 정보는 만들지 않습니다.
""".strip()


# MCP 리소스 등록
@mcp.resource("guide://damoya/mentor-recommendation")
def mentor_recommendation_guide() -> str:
    """멘토 추천 도구의 인자 규칙과 답변 근거 규칙이다."""
    return MENTOR_RECOMMENDATION_GUIDE


# MCP 프롬프트 등록
@mcp.prompt()
def mentor_chat_prompt(tone: str = "친절하고 간결한") -> str:
    """멘토 추천 상담에 사용할 프롬프트 템플릿이다."""
    return (
        f"{tone} 말투로 멘토 추천 상담을 진행하세요. "
        "사용자가 프로젝트를 설명하면 find_mentor_candidates 도구로 후보를 검색하고, "
        "후보 정보를 근거로 프로젝트에 잘 맞는 멘토와 그 이유를 설명하세요. "
        "검색 결과에 없는 멘토나 정보는 만들지 마세요."
    )


# MCP 도구 등록: 멘토 후보 검색
@mcp.tool()
def find_mentor_candidates(
    project_name: str,
    project_description: str,
    count: int = 10,
) -> dict[str, Any]:
    """프로젝트와 유사한 전문성·경력을 가진 멘토 후보를 검색한다."""
    if count < 1:
        count = 1
    elif count > 20:
        count = 20

    candidates, indexed_count = search_mentor_candidates(
        project_name,
        project_description,
        count,
    )
    return MentorCandidatesResult(
        candidates=candidates,
        indexed_count=indexed_count,
    ).model_dump()


def warm_up() -> None:
    """임베딩 모델을 미리 로드해 첫 Tool 호출 지연을 줄인다."""
    # stdio 프로토콜은 stdout을 쓰므로, 모델 로드 로그는 stderr로 보낸다.
    import sys

    from search.chatbot_store import get_embedding_model

    original_stdout = sys.stdout
    sys.stdout = sys.stderr
    try:
        get_embedding_model()
    finally:
        sys.stdout = original_stdout


if __name__ == "__main__":
    warm_up()
    mcp.run(transport="stdio")
