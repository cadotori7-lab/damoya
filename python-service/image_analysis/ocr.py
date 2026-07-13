from config import OCR_LANGS

_reader = None

def get_reader():
    global _reader
    if _reader is None:
        import easyocr

        _reader = easyocr.Reader(OCR_LANGS, gpu=False, verbose=False)
    return _reader

def extract_text(image_bytes: bytes) -> list[str]:
    return get_reader().readtext(image_bytes, detail=0)