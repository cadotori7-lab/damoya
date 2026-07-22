from pydantic import BaseModel, Field

# 챗봇 요청 스키마
class ChatRequest(BaseModel):
    message: str = Field(default="", max_length=4000)
    # 대화 단계: menu | site_guide (서버 세션과 함께 사용)
    step: str | None = Field(default=None, max_length=64)
    history: list[str] = Field(default_factory=list, max_length=12)

# 챗봇 링크 스키마
class ChatLink(BaseModel):
    title: str
    path: str
    url: str
    description: str

# 챗봇 응답 스키마
class ChatResponse(BaseModel):
    reply: str
    model: str
    step: str = "menu"
    links: list[ChatLink] = Field(default_factory=list)

# 이미지 인식 응답 스키마
class VerifyResponse(BaseModel):
    matched: bool
    name: str
    score: float
    threshold: float
    extracted_text: str
    tokens: list[str]