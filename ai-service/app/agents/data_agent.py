"""
DataAgent: agent that provides only global and detailed metrics from the backend API.
Used as a sub-agent of MainAgent when the user asks specifically for metrics.
"""

from google.adk.agents import Agent
from google.adk.models.lite_llm import LiteLlm

from app.agents.tools.metrics import get_detailed_metrics, get_global_metrics
from app.config import settings

data_agent = Agent(
    name="data_agent",
    model=LiteLlm(model=settings.model_name),
    description=(
        "Provides distribution metrics. Metrics available: global (total shipments, fulfilled units, unfulfilled units, "
        "average distance); detailed (efficiency score, fulfillment rate, total distance, total shipments, avg shipment "
        "size, fulfilled units, unique products distributed/requested, stores served, coverage, capacity utilization, "
        "unfulfilled demand, distance stats—min, median, max—, units by warehouse, top products)—optionally by store, "
        "warehouse or product. Use when the user asks for any of these metrics or KPIs. IMPORTANT: Has priority over "
        "sql_agent when the question can be answered with these metrics."
    ),
    instruction=(
        "Language rule (mandatory): Respond ONLY in the same language the user wrote in. "
        "If the user writes in Spanish, reply in Spanish. If in English, reply in English. "
        "Never switch to another language. If unsure which language to use, default to Spanish. "
        "Do your tool and transfer calls properly; do not write or describe them in text. When transferring to a sub-agent, "
        "do not generate any text. "
        "Output rule (mandatory): You are an assistant for the general public. You MUST NOT mention JSON keys, table names, "
        "or SQL queries in your reply—give only the information the user asked for in plain language. Optionally add a "
        "brief explanation when it helps the user understand. "
        "You are the agent that provides global and detailed metrics for the product distribution dashboard. "
        "You have two tools: get_global_metrics and get_detailed_metrics. Use the appropriate one for the question, then "
        "summarize the result. Do not answer without calling a tool first. "
        "Structure of the responses (JSON): "
        "get_global_metrics returns: totalShipments, fulfilledUnits, unfulfilledUnits, averageDistance (all numbers). "
        "get_detailed_metrics returns: efficiencyScore, fulfillmentRate, totalDistance, totalShipments, avgShipmentSize, "
        "fulfilledUnits, uniqueProductsDistributed, uniqueProductsRequested; "
        "storesServed (servedStores, fullyServedStores, neverServedStores, coveragePercentage, fullyServedPercentage); "
        "capacityUtilization (percentage, storesAtCapacity, totalStores); "
        "unfulfilledDemand (totalUnits, unitsByStockShortage, unitsByCapacityShortage); "
        "distanceStats (minDistance, maxDistance, medianDistance); "
        "unitsByWarehouse (array of {warehouseId, totalUnits, percentage, avgDistance}); "
        "topProducts (array of {productId, totalQuantity}). "
        "Optional query params for get_detailed_metrics: store_id, warehouse_id, product_id. "
        "If a tool returns an error, say so briefly in the user's language."
    ),
    tools=[
        get_global_metrics,
        get_detailed_metrics,
    ],
)
