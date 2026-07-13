from pydantic import BaseModel

class VerifyResponse(BaseModel):
    matched: bool
    name: str
    score: float
    threshold: float
    extracted_text: str
    tokens: list[str]
