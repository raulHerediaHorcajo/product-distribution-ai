"""
Tools for fetching metrics from the backend API.
"""

import httpx

from app.config import settings
from app.models.error_response import ErrorResponse
from app.models.metrics import DetailedMetrics, GlobalMetrics


def _get(base: str, path: str, params: dict | None = None) -> dict:
    url = f"{base.rstrip('/')}{path}"
    with httpx.Client(timeout=10.0) as client:
        response = client.get(url, params=params or {})
        response.raise_for_status()
        return response.json()


def get_global_metrics() -> GlobalMetrics | ErrorResponse:
    """
    Fetch global distribution metrics from the backend.
    Returns GlobalMetrics on success, or ErrorResponse on failure.
    """
    base = settings.backend_url
    try:
        data = _get(base, "/metrics/global")
        return GlobalMetrics.model_validate(data)
    except httpx.HTTPError as e:
        return ErrorResponse(error=f"Backend request failed: {e!s}")
    except Exception as e:
        return ErrorResponse(error=f"Unexpected error: {e!s}")


def get_detailed_metrics(
    store_id: str | None = None,
    warehouse_id: str | None = None,
    product_id: str | None = None,
) -> DetailedMetrics | ErrorResponse:
    """
    Fetch detailed distribution metrics from the backend, optionally filtered by store, warehouse or product.
    Returns DetailedMetrics on success, or ErrorResponse on failure.
    """
    base = settings.backend_url
    params = {}
    if store_id is not None:
        params["storeId"] = store_id
    if warehouse_id is not None:
        params["warehouseId"] = warehouse_id
    if product_id is not None:
        params["productId"] = product_id
    try:
        data = _get(base, "/metrics/detailed", params=params if params else None)
        return DetailedMetrics.model_validate(data)
    except httpx.HTTPError as e:
        return ErrorResponse(error=f"Backend request failed: {e!s}")
    except Exception as e:
        return ErrorResponse(error=f"Unexpected error: {e!s}")
