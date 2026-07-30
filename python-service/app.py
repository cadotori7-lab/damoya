"""Spring MVC와 MCP Agent 사이의 FastAPI 애플리케이션 진입점."""

from contextlib import asynccontextmanager

from fastapi import FastAPI
import config
from health.router import router as health_router
from image_analysis.router import router as verify_router
from mcp_chatbot.main import McpChatService
from mcp_chatbot.router import router as chat_router
from mcp_mentor_recommend.main import MentorMatchService
from mcp_mentor_recommend.router import router as mentor_router


@asynccontextmanager
async def lifespan(app: FastAPI):
    # 기동 시 MCP 클라이언트/에이전트를 미리 연결해 첫 요청 지연을 줄인다.
    await app.state.chat_service.start()
    await app.state.mentor_service.start()
    try:
        yield
    finally:
        # 바인딩 실패 등으로 종료될 때도 stdio 정리 예외가 앱 종료를 가리지 않게 한다.
        for service in (app.state.chat_service, app.state.mentor_service):
            try:
                await service.close()
            except Exception:
                pass


# 1-1 FastAPI 애플리케이션 생성
app = FastAPI(
    title="Damoya Python Service",
    description="Spring MVC 웹 화면과 MCP 사이트 검색/멘토 추천 서버를 중계합니다.",
    lifespan=lifespan,
)

# MCP 연결과 Agent를 여러 요청에서 재사용한다.
chat_service = McpChatService()
mentor_service = MentorMatchService()
app.state.chat_service = chat_service  # 상태 저장소에 저장(에이전트를 여러 요청에서 재사용)
app.state.mentor_service = mentor_service

# 5 라우터 등록
app.include_router(health_router)
app.include_router(chat_router)
app.include_router(mentor_router)
app.include_router(verify_router)
# include_router: 라우터를 애플리케이션에 등록

# 1-2
if __name__ == "__main__":
    import uvicorn

    print(f"Starting Damoya Python Service on http://{config.HOST}:{config.PORT}")
    uvicorn.run(app, host=config.HOST, port=config.PORT)
