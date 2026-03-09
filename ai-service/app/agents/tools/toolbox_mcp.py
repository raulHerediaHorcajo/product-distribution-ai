"""
MCP Toolset factory for MCP Toolbox (PostgreSQL) via Streamable HTTP.
Uses settings.mcp_toolbox_url. Exposes only list_tables and execute_sql.
"""

from google.adk.tools.mcp_tool import McpToolset, StreamableHTTPConnectionParams

from app.config import settings

TOOLBOX_TOOL_FILTER = ["list_tables", "execute_sql"]


def get_toolbox_mcp_toolset() -> McpToolset:
    """Build McpToolset for Toolbox (Postgres) over Streamable HTTP."""
    url = (settings.mcp_toolbox_url or "").strip()
    if not url:
        raise ValueError("mcp_toolbox_url is required; set MCP_TOOLBOX_URL or add mcp_toolbox_url in config.")
    connection_params = StreamableHTTPConnectionParams(url=url)
    return McpToolset(
        connection_params=connection_params,
        tool_filter=TOOLBOX_TOOL_FILTER,
    )
