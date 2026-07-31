import re
import uuid
from datetime import datetime
from pathlib import Path

import config
from jinja2 import Environment, FileSystemLoader, StrictUndefined, select_autoescape
from pypdf import PdfReader

from mcp_git_report.schemas import PdfRenderResult, ProjectReport

_REPORT_ID_PATTERN = re.compile(r"^[a-z0-9][a-z0-9-]{7,100}$")

class PdfRenderError(RuntimeError):
    """Playwright PDF 생성 또는 검증 오류."""

class ProjectReportPdfRenderer:
    """구조화된 AI 리포트를 고정 HTML 템플릿과 Playwright로 렌더링한다."""

    def __init__(self) -> None:
        self.output_dir = config.GIT_REPORT_OUTPUT_DIR.resolve()
        self.temp_dir = config.GIT_REPORT_PDF_TEMP_DIR.resolve()
        self.template_dir = (Path(__file__).parent / "templates").resolve()
        self.output_dir.mkdir(parents=True, exist_ok=True)
        self.temp_dir.mkdir(parents=True, exist_ok=True)
        self._templates = Environment(
            loader=FileSystemLoader(str(self.template_dir)),
            autoescape=select_autoescape(("html", "xml")),
            undefined=StrictUndefined,
            trim_blocks=True,
            lstrip_blocks=True,
        )

    async def render(
        self,
        report: ProjectReport,
        file_name: str | None = None,
    ) -> PdfRenderResult:
        """A4 PDF를 생성하고 pypdf로 다시 열어 유효성을 확인한다."""
        report_id = self._build_report_id(file_name or report.project_name)
        pdf_path = self.output_dir / f"{report_id}.pdf"
        html_path = self.temp_dir / f"{report_id}.html"
        template = self._templates.get_template("project_report.html")
        html = template.render(
            report=report,
            generated_at=self._format_datetime(report.generated_at),
            font_family=config.GIT_REPORT_PDF_FONT_FAMILY,
        )
        html_path.write_text(html, encoding="utf-8")

        try:
            try:
                from playwright.async_api import async_playwright
            except ImportError as error:
                raise PdfRenderError(
                    "Playwright가 설치되지 않았습니다. "
                    "`python -m playwright install chromium`까지 실행하세요."
                ) from error

            async with async_playwright() as playwright:
                launch_options: dict[str, object] = {"headless": True}
                if config.GIT_REPORT_PLAYWRIGHT_CHANNEL:
                    launch_options["channel"] = (
                        config.GIT_REPORT_PLAYWRIGHT_CHANNEL
                    )
                browser = await playwright.chromium.launch(**launch_options)
                try:
                    page = await browser.new_page()
                    await page.set_content(html, wait_until="load")
                    await page.emulate_media(media="print")
                    await page.pdf(
                        path=str(pdf_path),
                        format="A4",
                        print_background=True,
                        prefer_css_page_size=True,
                        display_header_footer=True,
                        header_template="<span></span>",
                        footer_template=(
                            "<div style=\"width:100%;padding:0 14mm;"
                            "font-family:Arial,sans-serif;font-size:8px;"
                            "color:#7b8499;display:flex;"
                            "justify-content:space-between;\">"
                            "<span>DAMOYA AI PROJECT REPORT</span>"
                            "<span><span class=\"pageNumber\"></span> / "
                            "<span class=\"totalPages\"></span></span>"
                            "</div>"
                        ),
                        margin={
                            "top": "14mm",
                            "right": "14mm",
                            "bottom": "18mm",
                            "left": "14mm",
                        },
                    )
                finally:
                    await browser.close()

            if not pdf_path.is_file() or pdf_path.stat().st_size == 0:
                raise PdfRenderError("PDF 파일이 생성되지 않았습니다.")
            reader = PdfReader(str(pdf_path))
            page_count = len(reader.pages)
            if page_count < 1:
                raise PdfRenderError("생성된 PDF에 페이지가 없습니다.")

            return PdfRenderResult(
                report_id=report_id,
                file_name=pdf_path.name,
                path=str(pdf_path.relative_to(config.BASE_DIR.parent)),
                resource_uri=f"report://damoya/{report_id}",
                size_bytes=pdf_path.stat().st_size,
                page_count=page_count,
            )
        except Exception:
            pdf_path.unlink(missing_ok=True)
            raise
        finally:
            html_path.unlink(missing_ok=True)

    def read(self, report_id: str) -> bytes:
        """생성한 PDF를 MCP 바이너리 리소스로 읽는다."""
        if not _REPORT_ID_PATTERN.fullmatch(report_id):
            raise PdfRenderError("report_id 형식이 올바르지 않습니다.")
        pdf_path = (self.output_dir / f"{report_id}.pdf").resolve()
        if pdf_path.parent != self.output_dir or not pdf_path.is_file():
            raise PdfRenderError("생성된 PDF를 찾을 수 없습니다.")
        return pdf_path.read_bytes()

    @staticmethod
    def _build_report_id(source: str) -> str:
        slug = re.sub(r"[^a-z0-9]+", "-", source.lower()).strip("-")
        if not slug:
            slug = "project-report"
        slug = slug[:48].rstrip("-")
        timestamp = datetime.now().strftime("%Y%m%d-%H%M%S")
        return f"{slug}-{timestamp}-{uuid.uuid4().hex[:8]}"

    @staticmethod
    def _format_datetime(value: datetime) -> str:
        local_value = value.astimezone()
        return local_value.strftime("%Y.%m.%d %H:%M")
