"""
Runner and session service setup for the ADK agents.
"""

from google.adk.runners import Runner
from google.adk.sessions import InMemorySessionService

from app.agents.main_agent import main_agent

APP_NAME = "product-distribution-ai"

session_service = InMemorySessionService()
runner = Runner(
    agent=main_agent,
    app_name=APP_NAME,
    session_service=session_service,
)
