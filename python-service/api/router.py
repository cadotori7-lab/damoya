from fastapi import APIRouter, File, Form, UploadFile

from api.schemas import VerifyResponse
from config import MATCH_THRESHOLD
from image_analysis.matcher import match_name
from image_analysis.ocr import extract_text
from mcp_chatbot.router import router as chatbot_router
from mentor_matching.router import router as mentor_matching_router

router = APIRouter()
router.include_router(chatbot_router)
router.include_router(mentor_matching_router)

@router.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}

@router.post("/verify", response_model=VerifyResponse)
async def verify(name: str = Form(...), file: UploadFile = File(...)) -> VerifyResponse:
    image_bytes = await file.read()
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
