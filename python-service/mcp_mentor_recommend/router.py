from fastapi import APIRouter, HTTPException, Request

from mcp_mentor_recommend.main import MentorMatchService
from mcp_mentor_recommend.schemas import MentorMatchRequest, MentorMatchResponse

router = APIRouter(tags=["mentor-matching"])


@router.post("/mentor-match", response_model=MentorMatchResponse)
async def mentor_match(
    payload: MentorMatchRequest,
    request: Request,
) -> MentorMatchResponse:
    try:
        # 요청을 MCP 클라이언트에 전달해 멘토 추천을 반환
        mentor_service: MentorMatchService = request.app.state.mentor_service
        return await mentor_service.match(payload)
    except (ConnectionError, RuntimeError) as error:
        raise HTTPException(status_code=503, detail=str(error)) from error
    except Exception as error:
        raise HTTPException(
            status_code=502,
            detail="멘토 추천 처리에 실패했습니다.",
        ) from error
