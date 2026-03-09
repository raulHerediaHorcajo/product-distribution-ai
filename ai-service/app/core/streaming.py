"""
Streaming chat: extract chart specs from agent events and yield NDJSON for the client.
"""

import json

from google.adk.agents.run_config import RunConfig, StreamingMode
from google.genai import types

from app.core.runner import runner


def get_chart_spec_from_event(event) -> dict | None:
    """If the event contains a create_chart tool response, return its spec for the client."""
    try:
        for fr in event.get_function_responses():
            if getattr(fr, "name", None) == "create_chart" and getattr(fr, "response", None):
                resp = fr.response
                if hasattr(resp, "model_dump"):
                    resp = resp.model_dump()
                if isinstance(resp, dict) and "type" in resp and "labels" in resp and "datasets" in resp:
                    return resp
    except Exception:
        pass
    return None


async def stream_chat(user_id: str, session_id: str, new_message: types.Content):
    """Run the agent with streaming; yield NDJSON lines (chart, delta, done, error)."""
    chart_emitted = False
    try:
        async for event in runner.run_async(
            user_id=user_id,
            session_id=session_id,
            new_message=new_message,
            run_config=RunConfig(streaming_mode=StreamingMode.SSE),
        ):
            if not chart_emitted:
                chart_spec = get_chart_spec_from_event(event)
                if chart_spec is not None:
                    yield (json.dumps({"chart": chart_spec}) + "\n").encode("utf-8")
                    chart_emitted = True
            is_partial = getattr(event, "partial", True)
            if not is_partial:
                continue
            if not event.content or not event.content.parts:
                continue
            for part in event.content.parts:
                text = getattr(part, "text", None) or ""
                if not text:
                    continue
                if getattr(part, "thought", False):
                    continue
                yield (json.dumps({"delta": text}) + "\n").encode("utf-8")
        yield (json.dumps({"done": True}) + "\n").encode("utf-8")
    except Exception as e:
        yield (json.dumps({"error": str(e)}) + "\n").encode("utf-8")
