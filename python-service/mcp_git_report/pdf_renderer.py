import asyncio
import re
import uuid
from datetime import datetime
from pathlib import Path
from xml.sax.saxutils import escape

import config
from pypdf import PdfReader
from reportlab.lib import colors
from reportlab.lib.enums import TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle
from reportlab.lib.units import mm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.pdfgen.canvas import Canvas
from reportlab.platypus import (
    Flowable,
    HRFlowable,
    KeepTogether,
    Paragraph,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)

from mcp_git_report.schemas import PdfRenderResult, ProjectReport

_REPORT_ID_PATTERN = re.compile(r"^[a-z0-9][a-z0-9-]{7,100}$")
_FONT_REGULAR = "DamoyaReportRegular"
_FONT_BOLD = "DamoyaReportBold"


class PdfRenderError(RuntimeError):
    """ReportLab PDF 생성 또는 검증 오류."""


class _NumberedCanvas(Canvas):
    """마지막 저장 시점에 전체 페이지 수를 포함한 바닥글을 그린다."""

    def __init__(self, *args: object, **kwargs: object) -> None:
        super().__init__(*args, **kwargs)
        self._saved_page_states: list[dict[str, object]] = []

    def showPage(self) -> None:  # noqa: N802 - ReportLab API 이름 유지
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self) -> None:
        page_count = len(self._saved_page_states)
        for page_number, page_state in enumerate(
            self._saved_page_states,
            start=1,
        ):
            self.__dict__.update(page_state)
            self._draw_footer(page_number, page_count)
            super().showPage()
        super().save()

    def _draw_footer(self, page_number: int, page_count: int) -> None:
        page_width, _ = A4
        self.saveState()
        self.setStrokeColor(colors.HexColor("#DFE5F0"))
        self.setLineWidth(0.5)
        self.line(14 * mm, 13 * mm, page_width - 14 * mm, 13 * mm)
        self.setFillColor(colors.HexColor("#7B8499"))
        self.setFont("Helvetica", 8)
        self.drawString(14 * mm, 8.5 * mm, "DAMOYA AI PROJECT REPORT")
        self.drawRightString(
            page_width - 14 * mm,
            8.5 * mm,
            f"{page_number} / {page_count}",
        )
        self.restoreState()


class _BadgeRow(Flowable):
    """기술 스택 이름을 자동 줄바꿈되는 배지 행으로 그린다."""

    def __init__(self, items: list[str], style: ParagraphStyle) -> None:
        super().__init__()
        self._items = items
        self._style = style
        self._layout: list[
            tuple[float, float, float, float, Paragraph]
        ] = []

    def wrap(self, avail_width: float, avail_height: float) -> tuple[float, float]:
        del avail_height
        horizontal_padding = 6 * mm
        vertical_padding = 3 * mm
        gap = 2 * mm
        x = 0.0
        row_top = 0.0
        row_height = 0.0
        layout: list[tuple[float, float, float, float, Paragraph]] = []

        for item in self._items:
            desired_width = pdfmetrics.stringWidth(
                item,
                _FONT_BOLD,
                self._style.fontSize,
            ) + horizontal_padding
            badge_width = min(max(desired_width, 18 * mm), avail_width)
            paragraph = Paragraph(_paragraph_text(item), self._style)
            _, paragraph_height = paragraph.wrap(
                badge_width - horizontal_padding,
                1000 * mm,
            )
            badge_height = paragraph_height + vertical_padding

            if x and x + badge_width > avail_width:
                row_top += row_height + gap
                x = 0.0
                row_height = 0.0

            layout.append(
                (x, row_top, badge_width, badge_height, paragraph)
            )
            x += badge_width + gap
            row_height = max(row_height, badge_height)

        self.width = avail_width
        self.height = row_top + row_height
        self._layout = layout
        return self.width, self.height

    def draw(self) -> None:
        canvas = self.canv
        canvas.saveState()
        for x, row_top, width, height, paragraph in self._layout:
            y = self.height - row_top - height
            canvas.setFillColor(colors.HexColor("#EDF1FF"))
            canvas.roundRect(x, y, width, height, height / 2, fill=1, stroke=0)
            paragraph.drawOn(canvas, x + 3 * mm, y + 1.5 * mm)
        canvas.restoreState()


class ProjectReportPdfRenderer:
    """구조화된 프로젝트 리포트를 ReportLab으로 A4 PDF로 만든다."""

    def __init__(self) -> None:
        self.output_dir = config.GIT_REPORT_OUTPUT_DIR.resolve()
        self.output_dir.mkdir(parents=True, exist_ok=True)
        self._register_fonts()
        self._styles = self._build_styles()

    async def render(
        self,
        report: ProjectReport,
        file_name: str | None = None,
    ) -> PdfRenderResult:
        """A4 PDF를 생성하고 pypdf로 다시 열어 유효성을 확인한다."""
        report_id = self._build_report_id(file_name or report.project_name)
        pdf_path = self.output_dir / f"{report_id}.pdf"

        try:
            await asyncio.to_thread(self._write_pdf, report, pdf_path)
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
        except Exception as error:
            pdf_path.unlink(missing_ok=True)
            if isinstance(error, PdfRenderError):
                raise
            raise PdfRenderError("PDF 생성 중 오류가 발생했습니다.") from error

    def read(self, report_id: str) -> bytes:
        """생성된 PDF를 MCP 바이너리 리소스로 읽는다."""
        if not _REPORT_ID_PATTERN.fullmatch(report_id):
            raise PdfRenderError("report_id 형식이 올바르지 않습니다.")
        pdf_path = (self.output_dir / f"{report_id}.pdf").resolve()
        if pdf_path.parent != self.output_dir or not pdf_path.is_file():
            raise PdfRenderError("생성된 PDF를 찾을 수 없습니다.")
        return pdf_path.read_bytes()

    def _write_pdf(self, report: ProjectReport, pdf_path: Path) -> None:
        document = SimpleDocTemplate(
            str(pdf_path),
            pagesize=A4,
            rightMargin=14 * mm,
            leftMargin=14 * mm,
            topMargin=14 * mm,
            bottomMargin=18 * mm,
            title=report.project_name,
            author="DAMOYA",
            subject="AI project report",
        )
        story = self._build_story(report, document.width)
        document.build(story, canvasmaker=_NumberedCanvas)

    def _build_story(
        self,
        report: ProjectReport,
        content_width: float,
    ) -> list[Flowable]:
        story: list[Flowable] = [
            self._build_header(report, content_width),
            Spacer(1, 8 * mm),
        ]

        story.extend(self._section_heading("PROJECT SUMMARY", "프로젝트 요약"))
        story.append(
            self._card(
                [
                    Paragraph(
                        _paragraph_text(report.executive_summary),
                        self._styles["body"],
                    )
                ],
                content_width,
            )
        )

        if report.tech_stack:
            story.append(Spacer(1, 7 * mm))
            story.extend(self._section_heading("TECH STACK", "주요 기술"))
            story.append(_BadgeRow(report.tech_stack, self._styles["badge"]))

        story.append(Spacer(1, 7 * mm))
        story.extend(self._section_heading("IMPROVEMENTS", "개선점"))
        if report.improvements:
            for index, item in enumerate(report.improvements, start=1):
                story.append(
                    self._improvement_card(
                        index,
                        item,
                        content_width,
                    )
                )
        else:
            story.append(
                self._card(
                    [
                        Paragraph(
                            "제공된 파일만으로 구체적인 개선점을 확인하기 어렵습니다.",
                            self._styles["body"],
                        )
                    ],
                    content_width,
                )
            )

        story.extend(
            [
                Spacer(1, 7 * mm),
                HRFlowable(
                    width="100%",
                    thickness=0.5,
                    color=colors.HexColor("#DFE5F0"),
                ),
                Spacer(1, 3 * mm),
                Paragraph(
                    _paragraph_text(report.source_notice),
                    self._styles["notice"],
                ),
            ]
        )
        return story

    def _build_header(
        self,
        report: ProjectReport,
        content_width: float,
    ) -> Table:
        inner_width = content_width - 18 * mm
        meta_width = inner_width / 2
        meta = Table(
            [
                [
                    self._meta("분석 기준", report.analyzed_ref),
                    self._meta("소스 스냅샷", report.analyzed_commit),
                ],
                [
                    self._meta("생성 시각", self._format_datetime(report.generated_at)),
                    self._meta("분석 모델", report.model_name),
                ],
            ],
            colWidths=[meta_width, meta_width],
        )
        meta.setStyle(
            TableStyle(
                [
                    ("LINEABOVE", (0, 0), (-1, 0), 0.5, colors.HexColor("#586680")),
                    ("LEFTPADDING", (0, 0), (-1, -1), 0),
                    ("RIGHTPADDING", (0, 0), (-1, -1), 4 * mm),
                    ("TOPPADDING", (0, 0), (-1, -1), 3 * mm),
                    ("BOTTOMPADDING", (0, 0), (-1, -1), 1.5 * mm),
                    ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ]
            )
        )

        content: list[Flowable] = [
            Paragraph("DAMOYA AI PROJECT SUMMARY", self._styles["brand"]),
            Spacer(1, 4 * mm),
            Paragraph(_paragraph_text(report.project_name), self._styles["title"]),
            Spacer(1, 2 * mm),
            Paragraph(_paragraph_text(report.project_topic), self._styles["topic"]),
            Spacer(1, 5 * mm),
            Paragraph(_paragraph_text(report.repository_url), self._styles["repo"]),
            Spacer(1, 5 * mm),
            meta,
        ]
        header = Table([[content]], colWidths=[content_width])
        header.setStyle(
            TableStyle(
                [
                    ("BACKGROUND", (0, 0), (-1, -1), colors.HexColor("#17233D")),
                    ("BOX", (0, 0), (-1, -1), 1, colors.HexColor("#3154D5")),
                    ("LEFTPADDING", (0, 0), (-1, -1), 9 * mm),
                    ("RIGHTPADDING", (0, 0), (-1, -1), 9 * mm),
                    ("TOPPADDING", (0, 0), (-1, -1), 9 * mm),
                    ("BOTTOMPADDING", (0, 0), (-1, -1), 7 * mm),
                    ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ]
            )
        )
        return header

    def _meta(self, label: str, value: str) -> Paragraph:
        return Paragraph(
            (
                f'<font color="#B9C4DB">{escape(label)}</font><br/>'
                f"<b>{_paragraph_text(value)}</b>"
            ),
            self._styles["meta"],
        )

    def _section_heading(self, label: str, title: str) -> list[Flowable]:
        return [
            KeepTogether(
                [
                    Paragraph(label, self._styles["label"]),
                    Spacer(1, 2 * mm),
                    Paragraph(title, self._styles["section_title"]),
                    Spacer(1, 3 * mm),
                ]
            )
        ]

    @staticmethod
    def _card(content: list[Flowable], content_width: float) -> Table:
        card = Table(
            [[content]],
            colWidths=[content_width],
            splitByRow=1,
            splitInRow=1,
        )
        card.setStyle(
            TableStyle(
                [
                    ("BACKGROUND", (0, 0), (-1, -1), colors.HexColor("#F6F8FC")),
                    ("BOX", (0, 0), (-1, -1), 0.7, colors.HexColor("#DFE5F0")),
                    ("LEFTPADDING", (0, 0), (-1, -1), 6 * mm),
                    ("RIGHTPADDING", (0, 0), (-1, -1), 6 * mm),
                    ("TOPPADDING", (0, 0), (-1, -1), 5 * mm),
                    ("BOTTOMPADDING", (0, 0), (-1, -1), 5 * mm),
                    ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ]
            )
        )
        return card

    def _improvement_card(
        self,
        index: int,
        item: str,
        content_width: float,
    ) -> Table:
        card = Table(
            [
                [
                    Paragraph(
                        (
                            f'<font color="#3154D5"><b>{index}.</b></font> '
                            f"{_paragraph_text(item)}"
                        ),
                        self._styles["body"],
                    )
                ]
            ],
            colWidths=[content_width],
        )
        card.setStyle(
            TableStyle(
                [
                    ("BACKGROUND", (0, 0), (-1, -1), colors.HexColor("#F6F8FC")),
                    ("BOX", (0, 0), (-1, -1), 0.7, colors.HexColor("#DFE5F0")),
                    ("LEFTPADDING", (0, 0), (-1, -1), 6 * mm),
                    ("RIGHTPADDING", (0, 0), (-1, -1), 6 * mm),
                    ("TOPPADDING", (0, 0), (-1, -1), 4 * mm),
                    ("BOTTOMPADDING", (0, 0), (-1, -1), 4 * mm),
                    ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ]
            )
        )
        return card

    def _register_fonts(self) -> None:
        regular_path, bold_path = self._resolve_font_paths()
        pdfmetrics.registerFont(TTFont(_FONT_REGULAR, str(regular_path)))
        pdfmetrics.registerFont(TTFont(_FONT_BOLD, str(bold_path)))
        pdfmetrics.registerFontFamily(
            _FONT_REGULAR,
            normal=_FONT_REGULAR,
            bold=_FONT_BOLD,
            italic=_FONT_REGULAR,
            boldItalic=_FONT_BOLD,
        )

    @staticmethod
    def _resolve_font_paths() -> tuple[Path, Path]:
        configured_regular = config.GIT_REPORT_PDF_FONT_PATH
        configured_bold = config.GIT_REPORT_PDF_BOLD_FONT_PATH
        if configured_regular:
            regular_path = Path(configured_regular).expanduser().resolve()
            if not regular_path.is_file():
                raise PdfRenderError(
                    f"PDF 글꼴 파일을 찾을 수 없습니다: {regular_path}"
                )
            bold_path = (
                Path(configured_bold).expanduser().resolve()
                if configured_bold
                else regular_path
            )
            if not bold_path.is_file():
                raise PdfRenderError(
                    f"PDF 굵은 글꼴 파일을 찾을 수 없습니다: {bold_path}"
                )
            return regular_path, bold_path

        candidates = (
            (
                Path("C:/Windows/Fonts/NotoSansKR-Regular.ttf"),
                Path("C:/Windows/Fonts/NotoSansKR-Bold.ttf"),
            ),
            (
                Path("C:/Windows/Fonts/malgun.ttf"),
                Path("C:/Windows/Fonts/malgunbd.ttf"),
            ),
            (
                Path("/usr/share/fonts/truetype/nanum/NanumGothic.ttf"),
                Path("/usr/share/fonts/truetype/nanum/NanumGothicBold.ttf"),
            ),
            (
                Path("/usr/share/fonts/opentype/noto/NotoSansCJK-Regular.ttc"),
                Path("/usr/share/fonts/opentype/noto/NotoSansCJK-Bold.ttc"),
            ),
        )
        for regular_path, bold_path in candidates:
            if regular_path.is_file():
                return (
                    regular_path,
                    bold_path if bold_path.is_file() else regular_path,
                )

        raise PdfRenderError(
            "한글 PDF 글꼴을 찾을 수 없습니다. "
            "GIT_REPORT_PDF_FONT_PATH 환경 변수에 TTF 또는 TTC 경로를 지정하세요."
        )

    @staticmethod
    def _build_styles() -> dict[str, ParagraphStyle]:
        base = {
            "alignment": TA_LEFT,
            "wordWrap": "CJK",
        }
        return {
            "brand": ParagraphStyle(
                "DamoyaBrand",
                **base,
                fontName=_FONT_BOLD,
                fontSize=8,
                leading=10,
                textColor=colors.HexColor("#CBD5FF"),
                spaceAfter=0,
            ),
            "title": ParagraphStyle(
                "DamoyaTitle",
                **base,
                fontName=_FONT_BOLD,
                fontSize=25,
                leading=31,
                textColor=colors.white,
                spaceAfter=0,
            ),
            "topic": ParagraphStyle(
                "DamoyaTopic",
                **base,
                fontName=_FONT_REGULAR,
                fontSize=12,
                leading=18,
                textColor=colors.HexColor("#E9EDFF"),
                spaceAfter=0,
            ),
            "repo": ParagraphStyle(
                "DamoyaRepo",
                **base,
                fontName=_FONT_REGULAR,
                fontSize=8,
                leading=12,
                textColor=colors.HexColor("#CBD5E7"),
                spaceAfter=0,
            ),
            "meta": ParagraphStyle(
                "DamoyaMeta",
                **base,
                fontName=_FONT_REGULAR,
                fontSize=8.5,
                leading=13,
                textColor=colors.white,
                spaceAfter=0,
            ),
            "label": ParagraphStyle(
                "DamoyaLabel",
                **base,
                fontName=_FONT_BOLD,
                fontSize=8,
                leading=10,
                textColor=colors.HexColor("#3154D5"),
                spaceAfter=0,
            ),
            "section_title": ParagraphStyle(
                "DamoyaSectionTitle",
                **base,
                fontName=_FONT_BOLD,
                fontSize=16,
                leading=20,
                textColor=colors.HexColor("#17233D"),
                spaceAfter=0,
            ),
            "body": ParagraphStyle(
                "DamoyaBody",
                **base,
                fontName=_FONT_REGULAR,
                fontSize=10.5,
                leading=17,
                textColor=colors.HexColor("#17233D"),
                spaceAfter=0,
            ),
            "badge": ParagraphStyle(
                "DamoyaBadge",
                **base,
                fontName=_FONT_BOLD,
                fontSize=8.5,
                leading=11,
                textColor=colors.HexColor("#3154D5"),
                spaceAfter=0,
            ),
            "notice": ParagraphStyle(
                "DamoyaNotice",
                **base,
                fontName=_FONT_REGULAR,
                fontSize=8.5,
                leading=13,
                textColor=colors.HexColor("#5D6880"),
                spaceAfter=0,
            ),
        }

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


def _paragraph_text(value: str) -> str:
    return escape(value).replace("\n", "<br/>")
