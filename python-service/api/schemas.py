from pydantic import BaseModel, Field


class ChatRequest(BaseModel):
    message: str = Field(min_length=1, max_length=4000)


class ChatResponse(BaseModel):
    reply: str
    model: str

class VerifyResponse(BaseModel):
    matched: bool
    name: str
    score: float
    threshold: float
    extracted_text: str
    tokens: list[str]
