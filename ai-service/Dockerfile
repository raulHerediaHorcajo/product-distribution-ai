FROM ghcr.io/astral-sh/uv:python3.14-trixie-slim

WORKDIR /app

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

COPY ai-service/pyproject.toml ai-service/uv.lock ./
RUN uv sync --frozen --no-dev --no-install-project

COPY ai-service/app ./app

# Create non-root user
RUN groupadd -r appuser && useradd -r -g appuser appuser && \
chown -R appuser:appuser /app
ENV HOME=/app
USER appuser:appuser

EXPOSE 8000
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:8000/health || exit 1

CMD ["uv", "run", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]