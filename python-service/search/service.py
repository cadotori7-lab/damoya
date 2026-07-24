# 사이트 검색 MCP 서버에서 사용자의 질문과 관련된 페이지를 검색하는 함수

from config import ELASTICSEARCH_SITE_INDEX
from search.client import get_elasticsearch_client
from search.embedding import embed_query
from search.indexer import prepare_index
from search.schemas import SearchPage

# 사이트 검색 MCP 서버에서 사용자의 질문과 관련된 페이지를 검색하는 함수(키워드 검색과 kNN 의미 검색을 함께 실행)
def search_pages(question: str, count: int = 3) -> list[SearchPage]:
    # == 검색 준비 == #
    normalized_question = question.strip() # 사용자의 질문을 정규화
    if not normalized_question:
        return [] # 사용자의 질문이 없으면 빈 리스트 반환

    # == 검색 실행 == #
    stored_count = prepare_index() # 인덱스 준비
    result_count = min(max(count, 1), 10, stored_count) # 검색 결과 개수 제한
    query_vector = embed_query(normalized_question) # 사용자의 질문을 벡터화
    client = get_elasticsearch_client() # Elasticsearch 클라이언트 생성

    # == Elasticsearch 검색 == #
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
    # == 검색 결과 반환 == #
    # 타입검증 후(BaseModel.model_validate() 사용) 검색 결과를 SearchPage 스키마로 변환
    return [
        SearchPage.model_validate(hit["_source"])
        for hit in response["hits"]["hits"]
    ]
