"""
SqlAgent: agent that answers distribution questions by querying PostgreSQL via MCP (list_tables, execute_sql).
Used as a sub-agent of MainAgent when the user needs custom SQL, aggregations, or schema exploration.
"""

from google.adk.agents import Agent
from google.adk.models.lite_llm import LiteLlm

from app.agents.tools.toolbox_mcp import get_toolbox_mcp_toolset
from app.config import settings

sql_agent = Agent(
    name="sql_agent",
    model=LiteLlm(model=settings.model_name),
    description=(
        "Answers questions about products, stores, warehouses, assignments, and any other distribution data by querying the "
        "database with SQL. Use when data_agent cannot answer the question. IMPORTANT: data_agent has priority when the "
        "question can be answered with its metrics."
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
        "You are the agent that answers questions about products, stores, warehouses, assignments and any other "
        "distribution data by querying the database with SQL. "
        "You MUST use your tools: call list_tables when you need to discover tables, then call execute_sql with a "
        "SELECT query. Do not answer without calling the tools. You can only run SELECT (read-only). Always use LIMIT "
        "(e.g. 200 rows max) to your queries. "
        "IMPORTANT: Do not invent other tables or columns under any circumstances. Use only the table and column names "
        "listed below. "
        "For most questions you will need stock_assignments (assignments warehouse→store per product/size, with quantity "
        "and distance_km) and/or unfulfilled_demands (unfulfilled demand per store/product/size, quantity_missing, reason). "
        "Start from these when in doubt. "
        "PostgreSQL supports SELECT DISTINCT and COUNT(DISTINCT (column)). Use them whenever the question is about unique "
        "counts or avoiding duplicates (e.g. how many different products, how many stores, distinct store IDs, "
        "distinct products, distinct warehouses, list without duplicates), or when the result could have repeated rows. "
        "EXAMPLE: 'Which warehouses distribute to store S058?' -> SELECT DISTINCT warehouse_id FROM stock_assignments "
        "WHERE store_id = 'S058'; "
        "Database schema (only these tables and columns exist): "
        "products: id (VARCHAR PK), brand_id (VARCHAR). Catalog of products. "
        "product_sizes: product_id (VARCHAR FK→products), sizes (VARCHAR); PK (product_id, sizes). "
        "Sizes available per product. "
        "stores: id (VARCHAR PK), latitude, longitude (DOUBLE PRECISION), country (VARCHAR), max_stock_capacity, "
        "remaining_capacity (INTEGER), expected_return_rate (DOUBLE PRECISION). Store master data. "
        "warehouses: id (VARCHAR PK), latitude, longitude (DOUBLE PRECISION), country (VARCHAR). Warehouse master data. "
        "product_items: id (UUID PK), product_id, size (VARCHAR), quantity (INTEGER), store_id, warehouse_id (VARCHAR; one "
        "of them set). Can be demand requested by the store (what the store asked for) or physical stock at a warehouse. "
        "stock_assignments: id (UUID PK), store_id, warehouse_id, product_id, size (VARCHAR), quantity (INTEGER), "
        "distance_km (DOUBLE PRECISION). Stock assigned from warehouse to store (what has been allocated/sent; "
        "what the store has) per product/size. A shipment is one unique (store_id, warehouse_id) pair—multiple rows in "
        "stock_assignments for the same store and warehouse count as one shipment; count distinct pairs for shipment count. "
        "unfulfilled_demands: id (UUID PK), store_id, product_id, size (VARCHAR), quantity_missing (INTEGER), "
        "reason (VARCHAR). Demand that could not be satisfied (quantity_missing, reason). "
        "If the database is unreachable, say briefly in the user's language that the SQL service is temporarily unavailable."
    ),
    tools=[get_toolbox_mcp_toolset()],
)
