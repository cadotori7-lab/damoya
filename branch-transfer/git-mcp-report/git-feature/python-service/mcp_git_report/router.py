import logging

from fastapi import APIRouter, HTTPException, Request, Response

from mcp_git_report.main import GitReportService
from mcp_git_report.repository import GitRepositoryError
from mcp_git_report.schemas import GitReportRequest, GitReportResponse

logger = logging.getLogger(__name__)
router = APIRouter(tags=["git-project-report"])


@router.post("/git-report", response_model=GitReportResponse)
async def create_git_report(
    payload: GitReportRequest,
    request: Request,
) -> GitReportResponse:
    """Git 저장소를 MCP와 LLM으로 분석하고 PDF 리포트를 생성한다."""
    try:
        service: GitReportService = request.app.state.git_report_service
        return await service.generate(payload)
    except GitRepositoryError as error:
        raise HTTPException(status_code=400, detail=str(error)) from error
    except RuntimeError as error:
        logger.exception("Git 프로젝트 리포트 요청에 실패했습니다")
        raise HTTPException(status_code=503, detail=str(error)) from error
    except Exception as error:
        logger.exception("Git 프로젝트 리포트 생성 중 오류가 발생했습니다")
        raise HTTPException(
            status_code=502,
            detail=(
                "AI 프로젝트 리포트 생성에 실패했습니다. "
                "Git 저장소, LLM, Playwright 설정을 확인하세요."
            ),
        ) from error


@router.get("/git-report/{report_id}/pdf")
async def download_git_report(
    report_id: str,
    request: Request,
) -> Response:
    """생성된 프로젝트 리포트 PDF를 다운로드한다."""
    try:
        service: GitReportService = request.app.state.git_report_service
        content = service.read_pdf(report_id)
        return Response(
            content=content,
            media_type="application/pdf",
            headers={
                "Content-Disposition": (
                    f'attachment; filename="{report_id}.pdf"'
                ),
                "Cache-Control": "private, no-store",
            },
        )
    except RuntimeError as error:
        raise HTTPException(status_code=404, detail=str(error)) from error

