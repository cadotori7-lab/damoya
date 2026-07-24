import csv
from pathlib import Path
from typing import TypedDict

from elasticsearch import helpers

from config import ELASTICSEARCH_SITE_INDEX
from search.client import get_elasticsearch_client
from search.embedding import embed_documents

CSV_PATH = Path(__file__).resolve().parents[1] / "data" / "site_pages.csv"


class SourcePage(TypedDict):
    id: str
    title: str
    path: str
    description: str
    keywords: list[str]


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
