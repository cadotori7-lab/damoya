import asyncio
import json
from typing import Any

from mcp import ClientSession, types
from mcp.client.streamable_http import streamable_http_client

from config import MCP_CLIENT_TIMEOUT_SECONDS


def _text_payload(result: types.CallToolResult) -> dict[str, Any] | None:
    texts = [
        block.text
        for block in result.content
        if isinstance(block, types.TextContent)
    ]
    if not texts:
        return None

    try:
        payload = json.loads("\n".join(texts))
    except json.JSONDecodeError:
        return {"result": "\n".join(texts)}

    return payload if isinstance(payload, dict) else {"result": payload}


async def call_mcp_tool(
    server_url: str,
    tool_name: str,
    arguments: dict[str, Any],
) -> dict[str, Any]:
    """Streamable HTTP MCP 서버의 도구를 호출하고 구조화된 결과를 반환한다."""
    try:
        async with asyncio.timeout(MCP_CLIENT_TIMEOUT_SECONDS):
            async with streamable_http_client(server_url) as (
                read_stream,
                write_stream,
                _,
            ):
                async with ClientSession(read_stream, write_stream) as session:
                    await session.initialize()
                    result = await session.call_tool(tool_name, arguments=arguments)
    except TimeoutError as exc:
        raise RuntimeError(
            f"MCP 도구 호출 시간이 초과되었습니다: {tool_name}"
        ) from exc
    except Exception as exc:
        raise RuntimeError(
            f"MCP 서버에 연결하지 못했습니다: {server_url}"
        ) from exc

    if result.isError:
        detail = _text_payload(result)
        raise RuntimeError(f"MCP 도구 실행에 실패했습니다: {detail or tool_name}")

    if result.structuredContent is not None:
        return result.structuredContent

    payload = _text_payload(result)
    if payload is None:
        raise RuntimeError(f"MCP 도구가 빈 결과를 반환했습니다: {tool_name}")
    return payload
