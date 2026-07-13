from fastapi import APIRouter, File, Form, UploadFile
from config import MATCH_THRESHOLD
from image_analysis.matcher import match_name
from image_analysis.ocr import extract_text
from api.schemas import VerifyResponse

router = APIRouter()

@router.get("/health")
def health():
    return {"status": "ok"}

# OCR 검증
@router.post("/verify", response_model=VerifyResponse)
async def verify(name: str = Form(...), file: UploadFile = File(...)):
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
