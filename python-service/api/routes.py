from fastapi import APIRouter, File, Form, HTTPException, UploadFile
from config import MATCH_THRESHOLD, OPENAI_MODEL
from image_analysis.matcher import match_name
from image_analysis.ocr import extract_text
from llm.service import ask_gpt
from api.schemas import ChatRequest, ChatResponse, VerifyResponse

router = APIRouter()

@router.get("/health")
def health():
    return {"status": "ok"}


@router.post("/chat", response_model=ChatResponse)
def chat(request: ChatRequest):
    try:
        reply = ask_gpt(request.message)
    except RuntimeError as exc:
        raise HTTPException(status_code=503, detail=str(exc)) from exc
    except Exception as exc:
        raise HTTPException(
            status_code=502,
            detail="OpenAI API 호출에 실패했습니다.",
        ) from exc

    return ChatResponse(reply=reply, model=OPENAI_MODEL)

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
