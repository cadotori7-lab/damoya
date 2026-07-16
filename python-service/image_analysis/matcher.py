import re
from difflib import SequenceMatcher
from config import MATCH_THRESHOLD

def normalize(text: str) -> str:
    return re.sub(r"\s+", "", text or "").lower()

def match_name(name: str, tokens: list[str]) -> dict:
    full_text = " ".join(tokens)
    target = normalize(name)
    matched = target != "" and target in normalize(full_text)

    best_score = 0.0
    for token in tokens:
        score = SequenceMatcher(None, target, normalize(token)).ratio()
        best_score = max(best_score, score)

    if not matched and best_score >= MATCH_THRESHOLD:
        matched = True

    return {
        "matched": matched,
        "score": round(best_score, 3),
        "full_text": full_text,
    }