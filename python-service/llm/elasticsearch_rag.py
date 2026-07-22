import csv
from pathlib import Path

from elasticsearch import Elasticsearch, helpers
from sentence_transformers import SentenceTransformer

from config import (
    ELASTICSEARCH_PASSWORD,
    ELASTICSEARCH_SITE_INDEX,
    ELASTICSEARCH_URL,
    ELASTICSEARCH_USERNAME,
)
EMBEDDING_MODEL = "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2"
CSV_PATH = Path(__file__).resolve().parents[1] / "data" / "site_pages.csv"

client_options: dict = {"request_timeout": 30}
if ELASTICSEARCH_USERNAME:
    client_options["basic_auth"] = (ELASTICSEARCH_USERNAME, ELASTICSEARCH_PASSWORD)

client = Elasticsearch(ELASTICSEARCH_URL, **client_options)
embedding_model = SentenceTransformer(EMBEDDING_MODEL)

# 사이트 안내 CSV를 Elasticsearch 문서 형태로 읽기
def load_pages() -> list[dict]:
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

# 페이지 내용 생성
def page_content(page: dict) -> str:
    return (
        f"페이지 ID: {page['id']}\n"
        f"페이지 이름: {page['title']}\n"
        f"경로: {page['path']}\n"
        f"설명: {page['description']}\n"
        f"검색어: {', '.join(page['keywords'])}"
    )

# 인덱스 준비
def prepare_index() -> int:
    """인덱스가 없거나 비어 있을 때만 사이트 페이지를 저장한다."""
    if not client.ping():
        raise ConnectionError("Elasticsearch 연결에 실패했습니다.")

    exists = client.indices.exists(index=ELASTICSEARCH_SITE_INDEX)
    if exists:
        stored_count = int(client.count(index=ELASTICSEARCH_SITE_INDEX)["count"])
        if stored_count > 0:
            return stored_count

    pages = load_pages()
    if not pages:
        raise ValueError("site_pages.csv에 페이지 데이터가 없습니다.")

    documents = [page_content(page) for page in pages]
    vectors = embedding_model.encode(
        documents,
        normalize_embeddings=True,
    ).tolist()

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

def search_pages(question: str, count: int = 3) -> list[dict]:
    """키워드 검색과 kNN 의미 검색을 함께 실행한다."""
    stored_count = prepare_index()
    result_count = min(count, stored_count)
    query_vector = embedding_model.encode(
        question,
        normalize_embeddings=True,
    ).tolist()

    response = client.search(
        index=ELASTICSEARCH_SITE_INDEX,
        size=result_count,
        query={
            "multi_match": {
                "query": question,
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
    return [hit["_source"] for hit in response["hits"]["hits"]]
