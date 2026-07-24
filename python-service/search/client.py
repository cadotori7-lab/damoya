from functools import lru_cache

from elasticsearch import Elasticsearch

from config import (
    ELASTICSEARCH_PASSWORD,
    ELASTICSEARCH_URL,
    ELASTICSEARCH_USERNAME,
)


@lru_cache(maxsize=1)
def get_elasticsearch_client() -> Elasticsearch:
    """공통 설정으로 Elasticsearch 클라이언트를 지연 생성한다."""
    options: dict = {"request_timeout": 30}
    if ELASTICSEARCH_USERNAME:
        options["basic_auth"] = (
            ELASTICSEARCH_USERNAME,
            ELASTICSEARCH_PASSWORD,
        )

    return Elasticsearch(ELASTICSEARCH_URL, **options)
