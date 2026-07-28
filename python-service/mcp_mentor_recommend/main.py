import asyncio
import json
import sys
from contextlib import AsyncExitStack
from pathlib import Path
from typing import Any

from langchain_core.prompts import ChatPromptTemplate
from langchain_mcp_adapters.client import MultiServerMCPClient
from langchain_mcp_adapters.tools import load_mcp_tools

from mcp_chatbot.llm import MODEL_NAME, get_llm
from mcp_mentor_recommend.schemas import (
    MentorMatchRequest,
    MentorMatchResponse,
    MentorRecommendation,
    MentorSelection,
)

PYTHON_SERVICE_DIR = Path(__file__).parent / ".."  # python-service 디렉토리 경로
MCP_SERVER_MODULE = "mcp_mentor_recommend.service"  # MCP 서버 모듈 경로

# LLM이 후보 중 멘토를 고를 때 사용하는 프롬프트
MENTOR_SELECTION_PROMPT = ChatPromptTemplate.from_messages(
    [
        (
            "system",
            (
                "너는 프로젝트에 가장 적합한 멘토를 선별하는 매칭 담당자다. "
                "제공된 후보 안에서만 최대 3명을 고른다. 전문 분야, 소개, 경력, "
                "자격증이 프로젝트의 기술과 목표에 얼마나 직접적으로 도움이 되는지 "
                "비교한다. 후보에 없는 member_id를 만들지 말고, 각 추천 사유는 "
                "후보 정보의 구체적인 근거를 포함해 한국어로 작성한다."
            ),
        ),
        (
            "human",
            (
                "[프로젝트]\n"
                "이름: {project_name}\n"
                "설명: {project_description}\n\n"
                "[벡터 검색 후보]\n{candidates}"
            ),
        ),
    ]
)


# MCP 연결 및 클라이언트 관리
class MentorMatchService:
    def __init__(self) -> None:
        self._client: MultiServerMCPClient | None = None  # MCP 클라이언트 객체
        self._tools: dict[str, Any] = {}

        self._start_lock = asyncio.Lock()
        self._invoke_lock = asyncio.Lock()
        self._exit_stack: AsyncExitStack | None = None

    # MCP 서버 연결 및 도구 로드
    async def start(self) -> None:
        # Fast-API 서버의 MCP 클라이언트는 오직 한개만 생성
        if self._tools:
            return

        async with self._start_lock:
            if self._tools:
                return

            exit_stack = AsyncExitStack()
            try:
                client = MultiServerMCPClient(
                    {
                        "mentor_recommendation": {
                            "transport": "stdio",
                            "command": sys.executable,
                            "args": ["-m", MCP_SERVER_MODULE],
                            "cwd": str(PYTHON_SERVICE_DIR),
                        }
                    }
                )
                # MCP 세션 생성(MCP 서버와 통신)
                session = await exit_stack.enter_async_context(
                    client.session("mentor_recommendation")
                )
                # MCP 도구 가져오기
                tools = await load_mcp_tools(session)
                self._client = client
                self._tools = {tool.name: tool for tool in tools}
                self._exit_stack = exit_stack
            except Exception:
                await exit_stack.aclose()
                raise

    # MCP Tool로 후보를 검색한 뒤 LLM으로 멘토를 선별
    async def match(self, request: MentorMatchRequest) -> MentorMatchResponse:
        await self.start()

        async with self._invoke_lock:
            tool = self._tools.get("find_mentor_candidates")
            if tool is None:
                raise RuntimeError("find_mentor_candidates MCP Tool을 찾을 수 없습니다.")

            raw = await tool.ainvoke(
                {
                    "project_name": request.project_name,
                    "project_description": request.project_description,
                    "count": 10,
                }
            )
            payload = self._normalize_payload(raw)
            candidates, indexed_count = self._extract_candidates(payload)

            if not candidates:
                return MentorMatchResponse(
                    reference=request.reference,
                    projectName=request.project_name,
                    candidateCount=0,
                    indexedCount=indexed_count,
                    model=MODEL_NAME,
                    recommendations=[],
                )

            candidate_context = json.dumps(
                [
                    {
                        "member_id": candidate.get("member_id"),
                        "similarity_score": candidate.get("similarity_score"),
                        "mentor_reference": candidate.get("mentor_reference"),
                        "name": candidate.get("name"),
                        "field": candidate.get("field"),
                        "career": candidate.get("career"),
                        "cert": candidate.get("cert"),
                    }
                    for candidate in candidates
                ],
                ensure_ascii=False,
                indent=2,
            )
            chain = MENTOR_SELECTION_PROMPT | get_llm().with_structured_output(
                MentorSelection
            )
            selection = await chain.ainvoke(
                {
                    "project_name": request.project_name,
                    "project_description": request.project_description,
                    "candidates": candidate_context,
                }
            )

            by_member_id = {
                int(candidate["member_id"]): candidate
                for candidate in candidates
                if candidate.get("member_id") is not None
            }
            recommendations: list[MentorRecommendation] = []
            selected_ids: set[int] = set()
            for choice in selection.recommendations:
                if choice.member_id in selected_ids:
                    continue
                candidate = by_member_id.get(choice.member_id)
                if candidate is None:
                    continue
                selected_ids.add(choice.member_id)
                recommendations.append(
                    MentorRecommendation(
                        memberId=choice.member_id,
                        loginId=str(candidate.get("login_id") or ""),
                        name=str(candidate.get("name") or ""),
                        field=str(candidate.get("field") or ""),
                        career=str(candidate.get("career") or ""),
                        cert=str(candidate.get("cert") or ""),
                        similarityScore=float(candidate.get("similarity_score") or 0.0),
                        reason=choice.reason,
                    )
                )

            return MentorMatchResponse(
                reference=request.reference,
                projectName=request.project_name,
                candidateCount=len(candidates),
                indexedCount=indexed_count,
                model=MODEL_NAME,
                recommendations=recommendations[:3],
            )

    # Tool 응답을 dict로 정규화
    # langchain_mcp_adapters는 보통 [{'type':'text','text':'{...json...}'}] 형태로 반환한다.
    def _normalize_payload(self, raw: Any) -> dict[str, Any]:
        if isinstance(raw, dict):
            return raw

        text: str | None = None
        if isinstance(raw, str):
            text = raw
        elif isinstance(raw, list):
            texts: list[str] = []
            for item in raw:
                if isinstance(item, dict) and item.get("type") == "text":
                    texts.append(str(item.get("text") or ""))
                elif isinstance(item, str):
                    texts.append(item)
            text = "\n".join(part for part in texts if part).strip() or None

        if text:
            try:
                parsed = json.loads(text)
            except json.JSONDecodeError as exc:
                raise RuntimeError(
                    f"멘토 추천 MCP 응답을 해석할 수 없습니다: {text[:200]}"
                ) from exc
            if isinstance(parsed, dict):
                return parsed

        raise RuntimeError(
            f"멘토 추천 MCP 응답 형식이 올바르지 않습니다: {type(raw).__name__}"
        )
    # 멘토 추천 MCP 응답에서 후보자 목록과 후보자 개수를 추출
    def _extract_candidates(
        self,
        payload: dict[str, Any],
    ) -> tuple[list[dict[str, Any]], int]:
        result = payload.get("result")
        if isinstance(result, dict):
            payload = result

        candidates = payload.get("candidates")
        if not isinstance(candidates, list):
            raise RuntimeError("멘토 추천 MCP 응답 형식이 올바르지 않습니다.")

        normalized = [
            candidate for candidate in candidates if isinstance(candidate, dict)
        ]
        return normalized, int(payload.get("indexed_count") or len(normalized))

    # FastAPI 종료 시 MCP 세션과 하위 프로세스를 정리
    async def close(self) -> None:
        async with self._invoke_lock:
            if self._exit_stack is not None:
                await self._exit_stack.aclose()
            self._client = None
            self._exit_stack = None
            self._tools = {}
