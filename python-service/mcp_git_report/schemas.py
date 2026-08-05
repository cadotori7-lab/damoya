from datetime import datetime, timezone
from typing import Annotated

from pydantic import BaseModel, ConfigDict, Field

ShortText = Annotated[str, Field(min_length=1, max_length=500)]
StackName = Annotated[str, Field(min_length=1, max_length=80)]


# 저장소에서 빠르게 계산하는 최소 메타데이터
class LanguageSummary(BaseModel):
    language: str
    file_count: int = Field(ge=1)


class RepositorySnapshot(BaseModel):
    repository_url: str
    branch: str
    commit: str
    short_commit: str
    file_count: int = Field(ge=0)
    total_tracked_bytes: int = Field(ge=0)
    languages: list[LanguageSummary] = Field(default_factory=list)
    key_files: list[str] = Field(default_factory=list)
    tree: list[str] = Field(default_factory=list)


class RepositoryFile(BaseModel):
    path: str
    content: str
    size_bytes: int = Field(ge=0)
    truncated: bool = False
    redacted: bool = False


class RepositoryInspection(BaseModel):
    snapshot: RepositorySnapshot
    files: list[RepositoryFile] = Field(default_factory=list)
    skipped: list[str] = Field(default_factory=list)
    untrusted_content: bool = True


class ProjectAnalysis(BaseModel):
    project_topic: str = Field(min_length=5, max_length=500)
    executive_summary: str = Field(min_length=20, max_length=2500)
    tech_stack: list[StackName] = Field(default_factory=list, max_length=15)
    improvements: list[ShortText] = Field(default_factory=list, max_length=8)


# LLM은 주제, 요약, 기술 스택, 개선점만 반환한다.
class ProjectReport(BaseModel):
    project_name: str = Field(min_length=1, max_length=200)
    repository_url: str = Field(min_length=1, max_length=500)
    analyzed_ref: str = Field(min_length=1, max_length=200)
    analyzed_commit: str = Field(min_length=7, max_length=64)
    generated_at: datetime = Field(
        default_factory=lambda: datetime.now(timezone.utc)
    )
    model_name: str = Field(default="LLM", min_length=1, max_length=100)
    project_topic: str = Field(min_length=5, max_length=500)
    executive_summary: str = Field(min_length=20, max_length=2500)
    tech_stack: list[StackName] = Field(default_factory=list, max_length=15)
    improvements: list[ShortText] = Field(default_factory=list, max_length=8)
    source_notice: str = Field(
        default=(
            "GitHub 저장소의 최신 파일 일부를 AI가 정적으로 분석한 결과입니다. "
            "코드를 실행하거나 전체 이력을 검증하지 않았습니다."
        ),
        max_length=500,
    )


class PdfRenderResult(BaseModel):
    report_id: str
    file_name: str
    path: str
    resource_uri: str
    size_bytes: int = Field(ge=1)
    page_count: int = Field(ge=1)


class GitReportRequest(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    project_name: str = Field(
        alias="projectName",
        min_length=1,
        max_length=200,
    )
    repository_url: str = Field(
        alias="repositoryUrl",
        min_length=1,
        max_length=500,
    )
    ref: str | None = Field(default=None, max_length=200)
    github_access_token: str | None = Field(
        default=None,
        alias="githubToken",
        max_length=500,
        repr=False,
    )


class GitReportResponse(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    report_id: str = Field(alias="reportId")
    file_name: str = Field(alias="fileName")
    project_name: str = Field(alias="projectName")
    repository_url: str = Field(alias="repositoryUrl")
    analyzed_ref: str = Field(alias="analyzedRef")
    analyzed_commit: str = Field(alias="analyzedCommit")
    file_count: int = Field(alias="fileCount", ge=0)
    page_count: int = Field(alias="pageCount", ge=1)
    project_topic: str = Field(alias="projectTopic")
    executive_summary: str = Field(alias="executiveSummary")
    tech_stack: list[str] = Field(alias="techStack", default_factory=list)
    improvements: list[str] = Field(default_factory=list)
    timings: dict[str, float] = Field(default_factory=dict)
    resource_uri: str = Field(alias="resourceUri")
    download_path: str = Field(alias="downloadPath")
