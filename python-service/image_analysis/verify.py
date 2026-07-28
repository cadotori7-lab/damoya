from pydantic import BaseModel

from config import MATCH_THRESHOLD
from image_analysis.matcher import match_name
from image_analysis.ocr import extract_text


class VerifyResponse(BaseModel):
    matched: bool
    name: str
    score: float
    threshold: float
    extracted_text: str
    tokens: list[str]


# 이미지 OCR 결과와 이름을 비교해 검증 응답을 만든다
def verify_name(name: str, image_bytes: bytes) -> VerifyResponse:
    tokens = extract_text(image_bytes)
    result = match_name(name, tokens)
    return VerifyResponse(
        matched=result["matched"],
        name=name,
        score=result["score"],
        threshold=MATCH_THRESHOLD,
        extracted_text=result["full_text"],
        tokens=tokens,
    )
