"""애플리케이션 진입점: FastAPI 앱 생성 및 실행."""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

import config
from api.routes import router

def create_app() -> FastAPI:
    app = FastAPI(title="Damoya 자격증 이름 검증 서비스")
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],
        allow_methods=["*"],
        allow_headers=["*"],
    )
    app.include_router(router)
    return app

app = create_app()

if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host=config.HOST, port=config.PORT)
