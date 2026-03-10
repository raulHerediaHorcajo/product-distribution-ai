# AI Service

AI microservice for the product distribution dashboard: multi-agent conversational assistant (Main, Data, SQL and Chart agents) with Google ADK, LiteLLM and Ollama.

## Requirements

- **Python 3.14+**
- **uv**: on macOS with Homebrew: `brew install uv`. Other options: [Installation](https://docs.astral.sh/uv/)
- **Ollama** running with the model: `ollama pull qwen3:8b`
- Dashboard backend reachable (default `BACKEND_URL`: `http://localhost:8080/api`)

## Environment variables

| Variable            | Description                    | Default                          |
|---------------------|--------------------------------|----------------------------------|
| `BACKEND_URL`       | Backend API base URL           | `http://localhost:8080/api`       |
| `OLLAMA_BASE_URL`  | Ollama URL                     | `http://localhost:11434`         |
| `MODEL_NAME`       | LiteLLM model (Ollama)         | `ollama_chat/qwen3:8b`           |
| `MCP_TOOLBOX_URL`  | MCP Toolbox (SQL) endpoint     | `http://localhost:5001/mcp`      |

## Local development

### Option 1: FastAPI (custom endpoints)

From the **ai-service** directory:

```bash
# Install dependencies (creates uv.lock if missing)
uv sync

# With dev dependencies (ruff)
uv sync --group dev

# Run the FastAPI service
uv run uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

From the **repo root** you can use: `make ai-run`.

- API: http://localhost:8000  
- Health: http://localhost:8000/health  
- OpenAPI: http://localhost:8000/docs  
- Endpoints: `POST /api/chat`, `POST /api/chat/stream`, `POST /api/kpi/explain`

### Option 2: ADK Web (developer UI)

From the **ai-service** directory:

```bash
# Install dependencies
uv sync

# Run ADK Web (integrated developer UI)
uv run adk web --host 0.0.0.0 --port 8000
```

From the **repo root** you can use: `make ai-web`.

**Note**: You can also use `adk web` directly if `adk` is in your PATH, but `uv run` ensures the correct environment.

- ADK Web UI: http://localhost:8000
- Interactive API docs: http://localhost:8000/docs

**Note**: The service exposes `root_agent` in `app/__init__.py` for ADK Web and ADK eval. Both modes use the same agents and tools.  

## Code quality (Ruff)

From the **ai-service** directory:

```bash
uv run ruff check app/
uv run ruff check app/ --fix
uv run ruff format app/
```

From the **repo root** you can use: `make ai-lint`, `make ai-fix`, `make ai-format`.

## Docker

The service is defined as a Docker Compose service. From the repo root:

```bash
docker compose up --build
```

You can also use `make up` (run without rebuild) or `make build` (run with rebuild).

The `ai-service` container listens on port 8000. Compose sets `BACKEND_URL=http://backend:8080/api`, `MCP_TOOLBOX_URL=http://toolbox:5000/mcp`, and `OLLAMA_BASE_URL=http://host.docker.internal:11434`. The Dockerfile includes a healthcheck on `/health`.

## Evaluation (ADK)

Evaluation uses datasets and criteria in `app/eval/`:

- **rubric_based_tool_use_quality_v1**: LLM-judged tool use (correct tool(s) + appropriate parameters even if e.g. SQL text differs from reference).
- **final_response_match_v2**: LLM-judged semantic match of the final response to the reference (flexible wording).

There are two main configs:

- `app/eval/eval_config.json`: local / full eval config (e.g. `goldenDataset.evalset.json`, higher `num_samples`, default judge `ollama_chat/qwen2.5:1.5b`).
- `app/eval/eval_config_ci.json`: CI-oriented config (reduced `num_samples`, judge model set to a remote Groq model via LiteLLM, currently `groq/meta-llama/llama-4-scout-17b-16e-instruct`).

To run the **full eval** from the **ai-service** directory:

```bash
uv run adk eval --config_file_path app/eval/eval_config.json app app/eval/goldenDataset.evalset.json
```

From the **repo root** you can use: `make ai-eval`.

Results are written to `app/.adk/eval_history/`.

For CI, there is also a small **pytest wrapper** in `app/eval/test_agent_eval.py` that:

- Loads `eval_config_ci.json` and `sampleDataset.evalset.json`.
- Calls `AgentEvaluator.evaluate_eval_set(...)`.
- Makes the test (and therefore CI) fail if any eval case fails.

When running eval in CI (e.g. from GitHub Actions), you must provide the appropriate API keys and model overrides as environment variables, for example:

- `GROQ_API_KEY`: API key for the Groq judge model used in `eval_config_ci.json`.
- `OPENROUTER_API_KEY`: API key for the OpenRouter model used by the service itself (if `MODEL_NAME` points to an OpenRouter model).
- `MODEL_NAME`: (optional) override of the default LiteLLM model for the agent when running eval in CI (e.g. `openrouter/qwen/qwen3-8b`).

If you run evals from the ADK Web UI, criteria may be selectable there; for full control (rubrics, judge model, thresholds), use the CLI with `--config_file_path app/eval/eval_config.json` or `app/eval/eval_config_ci.json`.

## Structure

```
app/
├── __init__.py           # root_agent for ADK Web and ADK eval
├── eval/                 # Evaluation
│   ├── eval_config.json      # Local/full eval criteria (rubrics + final_response_match_v2)
│   ├── eval_config_ci.json   # CI eval criteria (lighter, Groq judge model)
│   ├── goldenDataset.evalset.json
│   ├── sampleDataset.evalset.json
│   └── test_agent_eval.py    # Pytest wrapper around AgentEvaluator.evaluate_eval_set
├── main.py               # FastAPI app, CORS, health endpoint
├── config.py             # Settings (pydantic-settings)
├── core/                 # Core services
│   ├── runner.py         # ADK Runner and session service
│   ├── session.py        # Session management (get_or_create_session)
│   └── streaming.py      # Streaming chat, chart spec extraction
├── models/               # Pydantic models (API request/response, domain)
│   ├── chat.py           # ChatRequest, ChatResponse
│   ├── error_response.py # ErrorResponse (tools)
│   ├── kpi.py            # KPI names/descriptions, ExplainKpiRequest/Response
│   └── metrics.py       # GlobalMetrics, DetailedMetrics
├── agents/               # ADK agents
│   ├── main_agent.py     # Main coordinator agent
│   ├── data_agent.py     # Data/metrics agent
│   ├── sql_agent.py      # SQL/MCP toolbox agent
│   ├── chart_agent.py    # Chart generation agent
│   └── tools/            # Tools
│       ├── metrics.py    # get_global_metrics, get_detailed_metrics
│       ├── charts.py     # create_chart
│       └── toolbox_mcp.py # MCP Toolbox (list_tables, execute_sql)
└── routers/              # FastAPI endpoints
    ├── chat.py           # POST /api/chat, /api/chat/stream
    └── kpi.py            # POST /api/kpi/explain
```
