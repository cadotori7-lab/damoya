import hashlib
from typing import Any

from elasticsearch import helpers

from config import ELASTICSEARCH_MENTOR_INDEX
from search.client import get_elasticsearch_client
from search.embedding import EMBEDDING_MODEL, embed_documents


def _text(value: Any) -> str:
    return str(value or "").strip()


def build_mentor_reference(source: dict[str, Any]) -> str:
    """LLM과 임베딩 모델에 전달할 멘토 정보를 한 줄로 만든다."""
    parts = [
        ("이름", source.get("name")),
        ("전문 분야", source.get("field")),
        ("소개", source.get("intro")),
        ("경력", source.get("career")),
        ("자격증", source.get("cert")),
    ]
    return " | ".join(
        f"{label}: {_text(value)}"
        for label, value in parts
        if _text(value)
    )


def _source_hash(source: dict[str, Any], mentor_reference: str) -> str:
    values = [
        EMBEDDING_MODEL,
        _text(source.get("member_id")),
        _text(source.get("login_id")),
        _text(source.get("account_status")),
        str(bool(source.get("approved"))),
        str(bool(source.get("profile_public"))),
        mentor_reference,
    ]
    return hashlib.sha256("\n".join(values).encode("utf-8")).hexdigest()


def _load_source_mentors(client) -> list[dict[str, Any]]:
    if not client.indices.exists(index=ELASTICSEARCH_MENTOR_INDEX):
        raise RuntimeError(
            f"멘토 인덱스가 없습니다: {ELASTICSEARCH_MENTOR_INDEX}"
        )

    mentors: list[dict[str, Any]] = []
    for hit in helpers.scan(
        client,
        index=ELASTICSEARCH_MENTOR_INDEX,
        query={"query": {"match_all": {}}},
        source=[
            "member_id",
            "login_id",
            "name",
            "field",
            "intro",
            "career",
            "cert",
            "account_status",
            "approved",
            "profile_public",
        ],
    ):
        source = dict(hit.get("_source") or {})
        if source.get("member_id") is None:
            continue
        # 더미 데이터는 아직 승인 전이므로 공개 프로필 여부만 적용한다.
        if source.get("profile_public") is False:
            continue
        source["_id"] = str(hit.get("_id") or source["member_id"])
        mentors.append(source)
    return mentors


def _existing_hashes(client) -> dict[str, str]:
    return {
        str(hit["_id"]): _text(
            (hit.get("_source") or {}).get("embedding_source_hash")
        )
        for hit in helpers.scan(
            client,
            index=ELASTICSEARCH_MENTOR_INDEX,
            query={"query": {"match_all": {}}},
            source=["embedding_source_hash"],
        )
    }


def _put_embedding_mapping(client, dimensions: int) -> None:
    client.indices.put_mapping(
        index=ELASTICSEARCH_MENTOR_INDEX,
        properties={
            "mentor_reference": {"type": "text"},
            "embedding_source_hash": {"type": "keyword"},
            "embedding_model": {"type": "keyword"},
            "embedding": {
                "type": "dense_vector",
                "dims": dimensions,
                "index": True,
                "similarity": "cosine",
            },
        },
    )


def _embedding_dimensions(client) -> int | None:
    mapping = client.indices.get_mapping(index=ELASTICSEARCH_MENTOR_INDEX)
    properties = mapping[ELASTICSEARCH_MENTOR_INDEX]["mappings"].get(
        "properties",
        {},
    )
    dimensions = properties.get("embedding", {}).get("dims")
    return int(dimensions) if dimensions is not None else None


def prepare_mentor_index() -> int:
    """Spring 멘토 문서에 reference와 embedding 필드만 증분 갱신한다."""
    client = get_elasticsearch_client()
    if not client.ping():
        raise ConnectionError("Elasticsearch 연결에 실패했습니다.")

    source_mentors = _load_source_mentors(client)
    if not source_mentors:
        raise RuntimeError("추천에 사용할 공개 멘토가 없습니다.")

    prepared: list[tuple[str, dict[str, Any], str, str]] = []
    for source in source_mentors:
        document_id = str(source["_id"])
        mentor_reference = build_mentor_reference(source)
        if not mentor_reference:
            continue
        prepared.append(
            (
                document_id,
                source,
                mentor_reference,
                _source_hash(source, mentor_reference),
            )
        )

    if not prepared:
        raise RuntimeError("임베딩할 멘토 정보가 없습니다.")

    existing_hashes = _existing_hashes(client)
    changed = [
        item
        for item in prepared
        if existing_hashes.get(item[0]) != item[3]
    ]

    if not changed:
        return len(prepared)

    vector_texts = [item[2] for item in changed]
    vectors = embed_documents(vector_texts)
    dimensions = len(vectors[0])

    existing_dimensions = _embedding_dimensions(client)
    if existing_dimensions is None:
        _put_embedding_mapping(client, dimensions)
    elif existing_dimensions != dimensions:
        raise RuntimeError(
            "damoya-mentors.embedding 차원이 현재 임베딩 모델과 다릅니다. "
            f"index={existing_dimensions}, model={dimensions}"
        )

    helpers.bulk(
        client,
        (
            {
                "_op_type": "update",
                "_index": ELASTICSEARCH_MENTOR_INDEX,
                "_id": document_id,
                "doc": {
                    "mentor_reference": mentor_reference,
                    "embedding_source_hash": source_hash,
                    "embedding_model": EMBEDDING_MODEL,
                    "embedding": vector,
                },
            }
            for (
                document_id,
                source,
                mentor_reference,
                source_hash,
            ), vector in zip(changed, vectors)
        ),
    )

    client.indices.refresh(index=ELASTICSEARCH_MENTOR_INDEX)
    return len(prepared)
