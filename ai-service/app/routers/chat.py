from fastapi import APIRouter, HTTPException
from fastapi.responses import StreamingResponse
from google.genai import types

from app.core.runner import runner
from app.core.session import get_or_create_session
from app.core.streaming import stream_chat
from app.models.chat import ChatRequest, ChatResponse

router = APIRouter()


@router.post("/chat", response_model=ChatResponse)
async def chat(request: ChatRequest) -> ChatResponse:
    user_id, session_id = await get_or_create_session(request.session_id)
    new_message = types.Content(
        role="user",
        parts=[types.Part(text=request.message)],
    )
    reply_parts: list[str] = []
    try:
        async for event in runner.run_async(
            user_id=user_id,
            session_id=session_id,
            new_message=new_message,
        ):
            if event.content and event.content.parts:
                for part in event.content.parts:
                    text = getattr(part, "text", None) or ""
                    if not text or getattr(part, "thought", False):
                        continue
                    reply_parts.append(text)
        reply_text = "".join(reply_parts).strip() or "I didn't get a reply from the agent."
        return ChatResponse(reply=reply_text)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e)) from e


@router.post("/chat/stream")
async def chat_stream(request: ChatRequest):
    user_id, session_id = await get_or_create_session(request.session_id)
    new_message = types.Content(
        role="user",
        parts=[types.Part(text=request.message)],
    )
    return StreamingResponse(
        stream_chat(user_id, session_id, new_message),
        media_type="application/x-ndjson",
    )
