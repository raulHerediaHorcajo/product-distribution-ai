"""
Tools that call the distribution backend HTTP API or MCP (SQL).
Success responses use Pydantic models; on error returns {"error": "..."}.
"""

from app.agents.tools.charts import create_chart
from app.agents.tools.metrics import get_detailed_metrics, get_global_metrics

__all__ = [
    "create_chart",
    "get_global_metrics",
    "get_detailed_metrics",
]
