from fastapi import APIRouter, File, Form, UploadFile

from image_analysis.verify import VerifyResponse, verify_name

router = APIRouter(tags=["verify"])


@router.post("/verify", response_model=VerifyResponse)
async def verify(
    name: str = Form(...),
    file: UploadFile = File(...),
) -> VerifyResponse:
    image_bytes = await file.read()
    return verify_name(name, image_bytes)
