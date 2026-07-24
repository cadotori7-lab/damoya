from functools import lru_cache
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from sentence_transformers import SentenceTransformer

EMBEDDING_MODEL = "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2"

# 임베딩 모델 최초 검색 시 한 번만 로드
@lru_cache(maxsize=1)
def get_embedding_model() -> "SentenceTransformer":
    from sentence_transformers import SentenceTransformer

    return SentenceTransformer(EMBEDDING_MODEL)

# 문서 임베딩
def embed_documents(documents: list[str]) -> list[list[float]]:
    return get_embedding_model().encode(
        documents,
        normalize_embeddings=True,
    ).tolist()

# 질문 임베딩
def embed_query(question: str) -> list[float]:
    return get_embedding_model().encode(
        question,
        normalize_embeddings=True,
    ).tolist()
