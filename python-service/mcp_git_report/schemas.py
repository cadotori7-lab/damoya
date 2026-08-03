from datetime import datetime, timezone
from typing import Annotated

from pydantic import BaseModel, ConfigDict, Field

ShortText = Annotated[str, Field(min_length=1, max_length=500)]
StackName = Annotated[str, Field(min_length=1, max_length=80)]
MetricName = Annotated[str, Field(min_length=1, max_length=80)]
MetricValue = Annotated[str, Field(min_length=1, max_length=200)]


class LanguageSummary(BaseModel):
    """저장소 안에서 발견된 언어별 파일 수."""

    language: str
    file_count: int = Field(ge=1)


class RepositorySnapshot(BaseModel):
    """MCP가 준비한 읽기 전용 Git 저장소의 요약."""

    analysis_id: str
    repository_url: str
    requested_ref: str | None = None
    branch: str
    commit: str
    short_commit: str
    fetched_commit_count: int = Field(ge=0)
    file_count: int = Field(ge=0)
    total_tracked_bytes: int = Field(ge=0)
    languages: list[LanguageSummary] = Field(default_factory=list)
    key_files: list[str] = Field(default_factory=list)
    tree: list[str] = Field(default_factory=list)
    contributors: list[str] = Field(default_factory=list)
    expires_at: datetime
    warnings: list[str] = Field(default_factory=list)


class RepositoryFile(BaseModel):
    """LLM에 전달할 저장소 파일 한 개."""

    path: str
    content: str
    size_bytes: int = Field(ge=0)
    truncated: bool = False
    redacted: bool = False


class RepositoryFilesResult(BaseModel):
    """선택한 저장소 파일의 안전한 읽기 결과."""

    analysis_id: str
    files: list[RepositoryFile] = Field(default_factory=list)
    skipped: list[str] = Field(default_factory=list)
    untrusted_content: bool = True


class CommitSummary(BaseModel):
    """얕은 복제 범위 안에서 조회한 커밋 한 개."""

    commit: str
    short_commit: str
    author: str
    committed_at: datetime
    subject: str


class RepositoryHistory(BaseModel):
    """저장소 커밋 이력 요약."""

    analysis_id: str
    shallow: bool
    commits: list[CommitSummary] = Field(default_factory=list)
    contributor_commit_counts: dict[str, int] = Field(default_factory=dict)


class ReportSection(BaseModel):
    """AI 프로젝트 리포트의 본문 섹션."""

    title: str = Field(min_length=1, max_length=120)
    summary: str = Field(min_length=1, max_length=5000)
    findings: list[ShortText] = Field(default_factory=list, max_length=30)


class ProjectReport(BaseModel):
    """LLM이 작성하고 Playwright가 PDF로 렌더링할 구조화된 리포트."""

    project_name: str = Field(min_length=1, max_length=200)
    repository_url: str = Field(min_length=1, max_length=500)
    analyzed_ref: str = Field(min_length=1, max_length=200)
    analyzed_commit: str = Field(min_length=7, max_length=64)
    generated_at: datetime = Field(
        default_factory=lambda: datetime.now(timezone.utc)
    )
    model_name: str = Field(default="LLM", min_length=1, max_length=100)
    executive_summary: str = Field(min_length=20, max_length=8000)
    tech_stack: list[StackName] = Field(default_factory=list, max_length=40)
    repository_metrics: dict[MetricName, MetricValue] = Field(
        default_factory=dict,
        max_length=12,
    )
    sections: list[ReportSection] = Field(min_length=1, max_length=15)
    strengths: list[ShortText] = Field(default_factory=list, max_length=20)
    risks: list[ShortText] = Field(default_factory=list, max_length=20)
    recommendations: list[ShortText] = Field(default_factory=list, max_length=20)
    overall_score: int = Field(ge=0, le=100)
    source_notice: str = Field(
        default=(
            "이 리포트는 지정된 Git 저장소의 코드와 커밋 이력을 "
            "AI가 분석한 결과이며, 최종 판단 전 사람의 검토가 필요합니다."
        ),
        max_length=1000,
    )


class PdfRenderResult(BaseModel):
    """생성한 PDF 파일의 MCP 응답."""

    report_id: str
    file_name: str
    path: str
    resource_uri: str
    size_bytes: int = Field(ge=1)
    page_count: int = Field(ge=1)


class GitReportRequest(BaseModel):
    """Spring 테스트 화면에서 전달하는 Git 리포트 생성 요청."""

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


class GitReportResponse(BaseModel):
    """Spring 테스트 화면에 반환할 AI 리포트 생성 결과."""

    model_config = ConfigDict(populate_by_name=True)

    report_id: str = Field(alias="reportId")
    file_name: str = Field(alias="fileName")
    project_name: str = Field(alias="projectName")
    repository_url: str = Field(alias="repositoryUrl")
    analyzed_ref: str = Field(alias="analyzedRef")
    analyzed_commit: str = Field(alias="analyzedCommit")
    file_count: int = Field(alias="fileCount", ge=0)
    page_count: int = Field(alias="pageCount", ge=1)
    overall_score: int = Field(alias="overallScore", ge=0, le=100)
    executive_summary: str = Field(alias="executiveSummary")
    tech_stack: list[str] = Field(alias="techStack", default_factory=list)
    resource_uri: str = Field(alias="resourceUri")
    download_path: str = Field(alias="downloadPath")
