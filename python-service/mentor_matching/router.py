from fastapi import APIRouter, HTTPException

from mentor_matching.schemas import MentorMatchRequest, MentorMatchResponse
from mentor_matching.service import match_mentors


router = APIRouter(tags=["mentor-matching"])


@router.post("/mentor-match", response_model=MentorMatchResponse)
async def mentor_match(request: MentorMatchRequest) -> MentorMatchResponse:
    try:
        return await match_mentors(request)
    except (ConnectionError, RuntimeError) as exc:
        raise HTTPException(status_code=503, detail=str(exc)) from exc
    except Exception as exc:
        raise HTTPException(
            status_code=502,
            detail="멘토 추천 처리에 실패했습니다.",
        ) from exc
