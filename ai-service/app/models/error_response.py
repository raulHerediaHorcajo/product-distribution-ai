"""Error response shape for tools (e.g. backend request failure)."""

from pydantic import BaseModel


class ErrorResponse(BaseModel):
    """Shape returned by tools when a request fails (e.g. backend error)."""

    error: str
