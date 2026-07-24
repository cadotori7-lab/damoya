from pydantic import BaseModel, Field


class ChatRequest(BaseModel):
    message: str = Field(default="", max_length=4000)
    step: str | None = Field(default=None, max_length=64)
    history: list[str] = Field(default_factory=list, max_length=12)


class ChatLink(BaseModel):
    title: str
    path: str
    url: str
    description: str


class ChatResponse(BaseModel):
    reply: str
    model: str
    step: str = "menu"
    links: list[ChatLink] = Field(default_factory=list)
