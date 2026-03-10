import os

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore",
    )

    backend_url: str = "http://localhost:8080/api"
    ollama_base_url: str = "http://localhost:11434"
    model_name: str = "ollama_chat/qwen3:8b"
    mcp_toolbox_url: str = "http://localhost:5001/mcp"
    groq_api_key: str | None = None
    openrouter_api_key: str | None = None


settings = Settings()

# LiteLLM/Ollama: set so all agents using LiteLLM use this base URL
os.environ.setdefault("OLLAMA_API_BASE", settings.ollama_base_url)
if settings.groq_api_key:
    os.environ.setdefault("GROQ_API_KEY", settings.groq_api_key)
if settings.openrouter_api_key:
    os.environ.setdefault("OPENROUTER_API_KEY", settings.openrouter_api_key)
