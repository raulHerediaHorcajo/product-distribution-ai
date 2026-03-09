"""
Session handling: get or create session by session_id for the runner.
"""

from app.core.runner import APP_NAME, session_service

DEFAULT_USER_ID = "default"


async def get_or_create_session(
    session_id: str,
    user_id: str | None = None,
) -> tuple[str, str]:
    """Ensure a session exists for the given session_id; return (user_id, session_id) for the runner."""
    uid = user_id or DEFAULT_USER_ID
    session = await session_service.get_session(
        app_name=APP_NAME,
        user_id=uid,
        session_id=session_id,
    )
    if session is None:
        await session_service.create_session(
            app_name=APP_NAME,
            user_id=uid,
            session_id=session_id,
        )
    return (uid, session_id)
