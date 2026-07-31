import unittest
from pathlib import Path
from tempfile import TemporaryDirectory

from fastapi import FastAPI
from fastapi.testclient import TestClient

from mcp_git_report.repository import GitRepositoryError, GitRepositoryManager
from mcp_git_report.router import router as git_report_router
from mcp_git_report.schemas import (
    GitReportRequest,
    GitReportResponse,
    ProjectReport,
    ReportSection,
)


class GitRepositoryUrlTest(unittest.TestCase):
    def test_normalizes_allowed_github_url(self) -> None:
        result = GitRepositoryManager.validate_repository_url(
            "https://github.com/openai/openai-python"
        )
        self.assertEqual(
            result,
            "https://github.com/openai/openai-python.git",
        )

    def test_rejects_non_https_url(self) -> None:
        with self.assertRaises(GitRepositoryError):
            GitRepositoryManager.validate_repository_url(
                "file:///etc/passwd"
            )

    def test_rejects_credentials_and_unknown_host(self) -> None:
        for url in (
            "https://token@github.com/openai/openai-python",
            "https://example.com/openai/openai-python",
        ):
            with self.subTest(url=url), self.assertRaises(GitRepositoryError):
                GitRepositoryManager.validate_repository_url(url)

    def test_rejects_unsafe_ref(self) -> None:
        for ref in ("--upload-pack=evil", "../main", "feature//bad"):
            with self.subTest(ref=ref), self.assertRaises(GitRepositoryError):
                GitRepositoryManager._validate_ref(ref)

    def test_redacts_common_secret_patterns(self) -> None:
        source = (
            "OPENAI_API_KEY=sk-abcdefghijklmnopqrstuvwxyz123456\n"
            "safe=value\n"
        )
        result, redacted = GitRepositoryManager._redact_secrets(source)
        self.assertTrue(redacted)
        self.assertNotIn("sk-abcdefghijklmnopqrstuvwxyz123456", result)
        self.assertIn("[REDACTED]", result)

    def test_cleanup_is_safe_and_idempotent(self) -> None:
        with TemporaryDirectory() as temp_dir:
            manager = GitRepositoryManager()
            manager.work_root = Path(temp_dir).resolve()
            analysis_id = "a" * 32
            workspace = manager.work_root / analysis_id
            workspace.mkdir()
            (workspace / "sample.txt").write_text("temporary", encoding="utf-8")

            self.assertTrue(manager.cleanup(analysis_id))
            self.assertFalse(manager.cleanup(analysis_id))


class ProjectReportSchemaTest(unittest.TestCase):
    def test_accepts_valid_report(self) -> None:
        report = ProjectReport(
            project_name="다모여",
            repository_url="https://github.com/example/damoya",
            analyzed_ref="main",
            analyzed_commit="1234567890abcdef",
            model_name="test-model",
            executive_summary=(
                "Spring MVC와 Python MCP 서비스를 결합한 프로젝트입니다."
            ),
            tech_stack=["Java", "Python"],
            repository_metrics={"추적 파일": "120개"},
            sections=[
                ReportSection(
                    title="아키텍처",
                    summary="웹 애플리케이션과 AI 서비스를 분리했습니다.",
                    findings=["서비스 경계가 명확합니다."],
                )
            ],
            strengths=["역할 분리가 명확합니다."],
            risks=["통합 테스트를 추가해야 합니다."],
            recommendations=["CI에서 테스트를 실행하세요."],
            overall_score=82,
        )
        self.assertEqual(report.overall_score, 82)


class GitReportRouterTest(unittest.TestCase):
    def setUp(self) -> None:
        class FakeGitReportService:
            async def generate(
                self,
                request: GitReportRequest,
            ) -> GitReportResponse:
                return GitReportResponse(
                    reportId="sample-report-12345678",
                    fileName="sample-report.pdf",
                    projectName=request.project_name,
                    repositoryUrl=request.repository_url,
                    analyzedRef=request.ref or "main",
                    analyzedCommit="1234567890abcdef",
                    fileCount=42,
                    pageCount=3,
                    overallScore=80,
                    executiveSummary="테스트용 프로젝트 분석 요약입니다.",
                    techStack=["Java", "Python"],
                    resourceUri="report://damoya/sample-report-12345678",
                    downloadPath=(
                        "/git-report/sample-report-12345678/pdf"
                    ),
                )

            def read_pdf(self, report_id: str) -> bytes:
                return b"%PDF-1.4\n% test"

        app = FastAPI()
        app.state.git_report_service = FakeGitReportService()
        app.include_router(git_report_router)
        self.client = TestClient(app)

    def test_creates_report_with_camel_case_response(self) -> None:
        response = self.client.post(
            "/git-report",
            json={
                "projectName": "다모여",
                "repositoryUrl": "https://github.com/example/damoya",
                "ref": "main",
            },
        )
        self.assertEqual(response.status_code, 200)
        payload = response.json()
        self.assertEqual(payload["projectName"], "다모여")
        self.assertEqual(payload["reportId"], "sample-report-12345678")
        self.assertEqual(payload["pageCount"], 3)

    def test_downloads_generated_pdf(self) -> None:
        response = self.client.get(
            "/git-report/sample-report-12345678/pdf"
        )
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.headers["content-type"], "application/pdf")
        self.assertTrue(response.content.startswith(b"%PDF"))


if __name__ == "__main__":
    unittest.main()
