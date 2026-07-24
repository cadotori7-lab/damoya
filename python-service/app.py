from contextlib import AsyncExitStack, asynccontextmanager
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import config
from api.router import router
from mcp_servers.mentor_recommendation.server import mcp as mentor_recommendation_mcp
from mcp_servers.site_search.server import mcp as site_search_mcp

site_search_mcp_app = site_search_mcp.streamable_http_app()
mentor_recommendation_mcp_app = mentor_recommendation_mcp.streamable_http_app()

# FastAPI에 마운트된 MCP 서버들의 세션 관리
@asynccontextmanager
async def lifespan(_app: FastAPI):
    async with AsyncExitStack() as stack:
        await stack.enter_async_context(site_search_mcp.session_manager.run())
        await stack.enter_async_context(
            mentor_recommendation_mcp.session_manager.run()
        )
        yield

# FastAPI 애플리케이션 생성
def create_app() -> FastAPI:
    app = FastAPI(
        title="Damoya Python Service",
        lifespan=lifespan,
    )
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],
        allow_methods=["*"],
        allow_headers=["*"],
        expose_headers=["Mcp-Session-Id"],
    )
    app.include_router(router)
    app.mount("/mcp/site-search", site_search_mcp_app)  # /mcp/site-search 요청은 사이트 검색 MCP 서버로 전달
    app.mount("/mcp/mentor-recommendation",mentor_recommendation_mcp_app)   # /mcp/mentor-recommendation 요청은 멘토 추천 MCP 서버로 전달
    return app

# FastAPI 애플리케이션 생성
app = create_app()

# FastAPI 애플리케이션 실행
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host=config.HOST, port=config.PORT)
