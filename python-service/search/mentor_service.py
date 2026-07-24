from config import ELASTICSEARCH_MENTOR_INDEX
from search.client import get_elasticsearch_client
from search.embedding import embed_query
from search.mentor_indexer import prepare_mentor_index
from search.mentor_schemas import MentorCandidate

# 프로젝트와 유사한 전문성·경력을 가진 멘토 후보를 검색해 후보자 들과 후부자 개수를 반환
def search_mentor_candidates(
    project_name: str,
    project_description: str,
    count: int = 10,
) -> tuple[list[MentorCandidate], int]:
    description = project_description.strip()
    if not description:
        return [], 0
    # 멘토 인덱스 준비
    indexed_count = prepare_mentor_index()
    # 검색 결과 개수 제한
    result_count = min(max(count, 1), 20, indexed_count)
    # 검색 쿼리 생성
    query_text = f"프로젝트명: {project_name.strip()}\n프로젝트 설명: {description}"
    # 검색 쿼리 벡터 생성
    query_vector = embed_query(query_text)
    # Elasticsearch 클라이언트 생성
    client = get_elasticsearch_client()
    
    # Elasticsearch 검색 실행
    response = client.search(
        index=ELASTICSEARCH_MENTOR_INDEX,
        size=result_count,
        knn={
            "field": "embedding",
            "query_vector": query_vector,
            "k": result_count,
            "num_candidates": min(max(result_count * 5, 10), indexed_count),
            "filter": {"term": {"profile_public": True}},
        },
        source_excludes=[
            "embedding",
            "embedding_source_hash",
            "embedding_model",
            "sync_batch_id",
        ],
    )

    candidates = []
    for hit in response["hits"]["hits"]:
        source = dict(hit["_source"])
        source["similarity_score"] = round(float(hit.get("_score") or 0.0), 6)
        candidates.append(MentorCandidate.model_validate(source))
    return candidates, indexed_count
