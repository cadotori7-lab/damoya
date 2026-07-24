# 사이트 검색 MCP 서버에서 사용할 스키마
from pydantic import BaseModel, Field

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