from fastapi import APIRouter
from fastapi.exceptions import HTTPException

from mcp_chatbot.schemas import ChatRequest, ChatResponse
from mcp_chatbot.service import handle_chat

router = APIRouter()


@router.post("/chat", response_model=ChatResponse)
async def chat(request: ChatRequest) -> ChatResponse:
    try:
        result = await handle_chat(
            request.message or "",
            request.step,
            request.history,
        )
    except RuntimeError as exc:
        raise HTTPException(status_code=503, detail=str(exc)) from exc
    except Exception as exc:
        raise HTTPException(
            status_code=502,
            detail="챗봇 처리에 실패했습니다.",
        ) from exc

    return ChatResponse(
        reply=result["reply"],
        model=result["model"],
        step=result["step"],
        links=result.get("links") or [],
    )
