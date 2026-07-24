import json
from typing import Any

from config import MCP_MENTOR_RECOMMENDATION_URL, OPENAI_MODEL
from core.llm import get_llm
from core.mcp_client import call_mcp_tool
from mentor_matching.prompts import MENTOR_SELECTION_PROMPT
from mentor_matching.schemas import (
    MentorMatchRequest,
    MentorMatchResponse,
    MentorRecommendation,
    MentorSelection,
)
# 멘토 추천 MCP 응답에서 후보자 목록과 후보자 개수를 추출
def _extract_candidates(payload: dict[str, Any]) -> tuple[list[dict[str, Any]], int]:
    result = payload.get("result")
    if isinstance(result, dict):
        payload = result

    candidates = payload.get("candidates")
    if not isinstance(candidates, list):
        raise RuntimeError("멘토 추천 MCP 응답 형식이 올바르지 않습니다.")

    normalized = [candidate for candidate in candidates if isinstance(candidate, dict)]
    return normalized, int(payload.get("indexed_count") or len(normalized))

# 프로젝트 멘토 매칭 서비스
async def match_mentors(request: MentorMatchRequest) -> MentorMatchResponse:
    payload = await call_mcp_tool(
        MCP_MENTOR_RECOMMENDATION_URL,
        "find_mentor_candidates",
        {
            "project_name": request.project_name,
            "project_description": request.project_description,
            "count": 10,
        },
    )
    candidates, indexed_count = _extract_candidates(payload)
    if not candidates:
        return MentorMatchResponse(
            reference=request.reference,
            projectName=request.project_name,
            candidateCount=0,
            indexedCount=indexed_count,
            model=OPENAI_MODEL,
            recommendations=[],
        )

    candidate_context = json.dumps(
        [
            {
                "member_id": candidate.get("member_id"),
                "similarity_score": candidate.get("similarity_score"),
                "mentor_reference": candidate.get("mentor_reference"),
            }
            for candidate in candidates
        ],
        ensure_ascii=False,
        indent=2,
    )
    chain = (
        MENTOR_SELECTION_PROMPT
        | get_llm().with_structured_output(MentorSelection)
    )
    selection = await chain.ainvoke(
        {
            "project_name": request.project_name,
            "project_description": request.project_description,
            "candidates": candidate_context,
        }
    )

    by_member_id = {
        int(candidate["member_id"]): candidate
        for candidate in candidates
        if candidate.get("member_id") is not None
    }
    recommendations: list[MentorRecommendation] = []
    selected_ids: set[int] = set()
    for choice in selection.recommendations:
        if choice.member_id in selected_ids:
            continue
        candidate = by_member_id.get(choice.member_id)
        if candidate is None:
            continue
        selected_ids.add(choice.member_id)
        recommendations.append(
            MentorRecommendation(
                memberId=choice.member_id,
                loginId=str(candidate.get("login_id") or ""),
                name=str(candidate.get("name") or ""),
                field=str(candidate.get("field") or ""),
                career=str(candidate.get("career") or ""),
                cert=str(candidate.get("cert") or ""),
                similarityScore=float(candidate.get("similarity_score") or 0.0),
                reason=choice.reason,
            )
        )

    return MentorMatchResponse(
        reference=request.reference,
        projectName=request.project_name,
        candidateCount=len(candidates),
        indexedCount=indexed_count,
        model=OPENAI_MODEL,
        recommendations=recommendations[:3],
    )
