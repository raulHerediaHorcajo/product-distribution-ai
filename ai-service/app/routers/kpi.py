import json
from typing import Final

from cachetools import TTLCache
from fastapi import APIRouter, HTTPException
from litellm import acompletion

from app.config import settings
from app.models.kpi import KPI_DESCRIPTIONS, KPI_NAMES, ExplainKpiRequest, ExplainKpiResponse

router = APIRouter()

# Simple in-memory cache for KPI explanations.
_KPI_CACHE: TTLCache[str, str] = TTLCache(maxsize=500, ttl=36000)
_CACHE_PREFIX: Final = "kpi_explain:"


def _build_prompt(kpi_slug: str, context: dict) -> str:
    name = KPI_NAMES.get(kpi_slug) or kpi_slug.replace("-", " ").title()
    description = KPI_DESCRIPTIONS.get(kpi_slug)
    system = (
        "You explain distribution and logistics KPIs in 2–4 short sentences, in clear English. "
        "Say what the metric means and how to interpret it. Do not use markdown or bullet lists."
    )
    user = f"Explain the KPI: {name}."
    if description:
        user += f" This KPI measures {description}"
    if context:
        user += f" Current values (for context): {context}."
    return f"{system}\n\nUser: {user}"


def _make_cache_key(kpi_slug: str, context: dict) -> str:
    context_json = json.dumps(context, sort_keys=True)
    return f"{_CACHE_PREFIX}{kpi_slug}:{context_json}"


@router.post("/kpi/explain", response_model=ExplainKpiResponse)
async def explain_kpi(request: ExplainKpiRequest) -> ExplainKpiResponse:
    if request.kpi_slug not in KPI_NAMES:
        raise HTTPException(
            status_code=400,
            detail=f"Unknown kpi_slug. Valid slugs: {list(KPI_NAMES.keys())}",
        )

    cache_key = _make_cache_key(request.kpi_slug, request.context)
    if cache_key in _KPI_CACHE:
        explanation = _KPI_CACHE[cache_key]
        return ExplainKpiResponse(explanation=explanation)

    prompt = _build_prompt(request.kpi_slug, request.context)
    try:
        response = await acompletion(
            model=settings.model_name,
            messages=[{"role": "user", "content": prompt}],
        )
        text = ""
        if response.choices:
            text = (response.choices[0].message.content or "").strip()
        explanation = text or "Could not generate explanation."

        _KPI_CACHE[cache_key] = explanation

        return ExplainKpiResponse(explanation=explanation)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e)) from e
