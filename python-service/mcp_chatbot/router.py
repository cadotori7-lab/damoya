import logging
import uuid

from fastapi import APIRouter, HTTPException, Request

from mcp_chatbot.main import McpChatService
from mcp_chatbot.schemas import ChatRequest, ChatResponse

logger = logging.getLogger(__name__)
router = APIRouter(tags=["chat"])


@router.post("/chat", response_model=ChatResponse)
async def chat(
    payload: ChatRequest,
    request: Request,
) -> ChatResponse:
    question = payload.question.strip()
    session_id = payload.session_id or uuid.uuid4().hex

    try:
        # 세션ID와 질문을 MCP 서버에 전달해 답변을 반환
        chat_service: McpChatService = request.app.state.chat_service
        answer = await chat_service.ask(session_id, question)

    except Exception as error:
        logger.exception("MCP 챗봇 요청에 실패했습니다")
        raise HTTPException(
            status_code=503,
            detail=(
                "MCP 챗봇 서비스를 사용할 수 없습니다. "
                "Elasticsearch와 LLM 설정을 확인하세요."
                f"({type(error).__name__}: {error})"
            ),
        ) from error

    return ChatResponse(
        answer=answer,
        source="mcp",
        question=question,
        sessionId=session_id,
    )
