# mcp_chatbot용 Elasticsearch 기능(사이트 페이지 색인/검색)

import csv
from functools import lru_cache
from pathlib import Path
from typing import TYPE_CHECKING, Any, TypedDict

from elasticsearch import Elasticsearch, helpers
from pydantic import BaseModel, Field

from config import (
    ELASTICSEARCH_PASSWORD,
    ELASTICSEARCH_SITE_INDEX,
    ELASTICSEARCH_URL,
    ELASTICSEARCH_USERNAME,
)

if TYPE_CHECKING:
    from sentence_transformers import SentenceTransformer

CSV_PATH = Path(__file__).resolve().parents[1] / "data" / "site_pages.csv"
EMBEDDING_MODEL = "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2"


# 검색 결과 페이지 스키마
class SearchPage(BaseModel):
    page_id: str
    title: str
    path: str
    description: str
    content: str


# 검색 결과 페이지 리스트 스키마
class SearchPagesResult(BaseModel):
    pages: list[SearchPage] = Field(default_factory=list)


class SourcePage(TypedDict):
    id: str
    title: str
    path: str
    description: str
    keywords: list[str]


# ES 클라이언트
@lru_cache(maxsize=1)
def get_elasticsearch_client() -> Elasticsearch:
    options: dict[str, Any] = {"request_timeout": 30}
    if ELASTICSEARCH_USERNAME:
        options["basic_auth"] = (
            ELASTICSEARCH_USERNAME,
            ELASTICSEARCH_PASSWORD,
        )
    return Elasticsearch(ELASTICSEARCH_URL, **options)


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

def load_pages() -> list[SourcePage]:
    """사이트 안내 CSV를 Elasticsearch 문서 형태로 읽는다."""
    with CSV_PATH.open(encoding="utf-8-sig", newline="") as file:
        return [
            {
                "id": row["id"].strip(),
                "title": row["title"].strip(),
                "path": row["path"].strip(),
                "description": row["description"].strip(),
                "keywords": [
                    keyword.strip()
                    for keyword in row["keywords"].split("|")
                    if keyword.strip()
                ],
            }
            for row in csv.DictReader(file)
        ]


def page_content(page: SourcePage) -> str:
    return (
        f"페이지 ID: {page['id']}\n"
        f"페이지 이름: {page['title']}\n"
        f"경로: {page['path']}\n"
        f"설명: {page['description']}\n"
        f"검색어: {', '.join(page['keywords'])}"
    )


def prepare_index() -> int:
    """인덱스가 없거나 비어 있을 때만 사이트 페이지를 저장한다."""
    client = get_elasticsearch_client()
    if not client.ping():
        raise ConnectionError("Elasticsearch 연결에 실패했습니다.")

    exists = bool(client.indices.exists(index=ELASTICSEARCH_SITE_INDEX))
    if exists:
        stored_count = int(client.count(index=ELASTICSEARCH_SITE_INDEX)["count"])
        if stored_count > 0:
            return stored_count

    pages = load_pages()
    if not pages:
        raise ValueError("site_pages.csv에 페이지 데이터가 없습니다.")

    documents = [page_content(page) for page in pages]
    vectors = embed_documents(documents)

    if not exists:
        client.indices.create(
            index=ELASTICSEARCH_SITE_INDEX,
            mappings={
                "properties": {
                    "page_id": {"type": "keyword"},
                    "title": {"type": "text"},
                    "path": {"type": "keyword"},
                    "description": {"type": "text"},
                    "content": {"type": "text"},
                    "embedding": {
                        "type": "dense_vector",
                        "dims": len(vectors[0]),
                        "index": True,
                        "similarity": "cosine",
                    },
                }
            },
        )

    helpers.bulk(
        client,
        (
            {
                "_index": ELASTICSEARCH_SITE_INDEX,
                "_id": page["id"],
                "_source": {
                    "page_id": page["id"],
                    "title": page["title"],
                    "path": page["path"],
                    "description": page["description"],
                    "content": content,
                    "embedding": vector,
                },
            }
            for page, content, vector in zip(pages, documents, vectors)
        ),
    )
    client.indices.refresh(index=ELASTICSEARCH_SITE_INDEX)
    return len(pages)


# 키워드 검색과 kNN 의미 검색을 함께 실행
def search_pages(question: str, count: int = 3) -> list[SearchPage]:
    normalized_question = question.strip()
    if not normalized_question:
        return []

    stored_count = prepare_index()
    result_count = min(max(count, 1), 10, stored_count)
    query_vector = embed_query(normalized_question)
    client = get_elasticsearch_client()

    response = client.search(
        index=ELASTICSEARCH_SITE_INDEX,
        size=result_count,
        query={
            "multi_match": {
                "query": normalized_question,
                "fields": ["title^3", "description^2", "content"],
                "boost": 0.4,
            }
        },
        knn={
            "field": "embedding",
            "query_vector": query_vector,
            "k": result_count,
            "num_candidates": min(max(result_count * 5, 10), stored_count),
            "boost": 0.6,
        },
        source_excludes=["embedding"],
    )
    return [
        SearchPage.model_validate(hit["_source"])
        for hit in response["hits"]["hits"]
    ]
