# Product Distribution Dashboard

A full-stack application for managing and visualizing product distribution across warehouses and stores. The application provides an interactive dashboard with map visualization, data tables, and detailed metrics, plus a conversational AI assistant (multi-agent) that uses the backend and MCP Toolbox for metrics, charts, and SQL queries.

## Tech Stack

### Backend
- **Spring Boot 3.4.5** (Java 17)
- **PostgreSQL 16** - Database
- **Maven** - Build tool

### Frontend
- **Angular 18** - Framework
- **TypeScript** - Language
- **Leaflet** - Map visualization
- **Chart.js / ng2-charts** - Data visualization
- **ng-select** - Enhanced dropdowns
- **RxJS** - Reactive programming

### AI Service
- **Python 3.14+**, **FastAPI** - API and streaming
- **Google ADK** - Multi-agent (Main, Data, SQL, Chart)
- **LiteLLM** + **Ollama** - LLM inference
- **MCP Toolbox** - SQL agent over PostgreSQL (read-only)

### Development Tools
- **Docker & Docker Compose** - Containerization
- **Makefile** - Common tasks from repo root (up, build, lint, test, ai-run, etc.)
- **ESLint** - Code linting (frontend)
- **Prettier** - Code formatting (frontend)
- **Ruff** - Linting and formatting (ai-service)
- **pgAdmin** - Database administration

## Prerequisites

- **Docker & Docker Compose** – to run the full stack (postgres, backend, frontend, pgAdmin, toolbox, ai-service).

For the AI assistant to work, **Ollama** must be reachable (e.g. running on the host). The ai-service in Docker uses `OLLAMA_BASE_URL=http://host.docker.internal:11434` by default. Pull the model: `ollama pull qwen3:8b`.

Optional (only if you want to run tests, lint or build outside Docker):

- **Node.js** (v18+), **npm** or **yarn** – frontend scripts
- **Java 17** (JDK), **Maven 3.6+** – backend scripts
- **Python 3.14+**, **uv** – ai-service (run, eval, ADK Web); see `ai-service/README.md`

## Development Setup

Development runs with **Docker Compose**: postgres, backend, frontend, pgAdmin, toolbox (MCP SQL) and ai-service in one command. The backend uses the **`dev`** profile.

#### Start the stack

From the project root:

```bash
docker compose up --build
```

You can also use `make up` (run without rebuild) or `make build` (run with rebuild). Use `docker compose up --build` again after code changes, or plain `docker compose up` if you didn't change any code.

#### Access points

- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080
- **AI Service**: http://localhost:8000 (chat, KPI explain, OpenAPI at /docs)
- **Toolbox (MCP)**: http://localhost:5001 (SQL agent used by the AI assistant)
- **pgAdmin**: http://localhost:5050

## CI/CD (GitHub Actions)

- **PR Validation** – On pull requests, runs only what changed:
  - **Frontend**: lint and build (`pr-frontend-analysis.yml`)
  - **Backend**: tests and SonarCloud (`pr-backend-test-and-sonar.yml`)
  - **Data**: JSON structure validation (`pr-data-validation.yml`)
  - **AI Service**: Ruff analysis and formatting check (`pr-ai-service-analysis.yml`)
- **AI Service evaluation (ADK)** – Optional, label-driven eval for PRs:
  - Workflow: `.github/workflows/ai-service-eval.yml`
  - Trigger: `on: pull_request` (opened, synchronize, reopened, labeled)
  - The eval job only runs when:
    - The PR changes `ai-service/**` (or the eval workflow), **and**
    - The PR has the label `ai-eval` (description: “Trigger manual AI service evaluation for this PR.”)
  - Internally runs the pytest wrapper `app/eval/test_agent_eval.py` which calls `AgentEvaluator.evaluate_eval_set(...)` with `eval_config_ci.json` and fails CI if any eval case fails.
- **Main branch**: Backend analysis on push to main (`main-backend-sonar.yml`)

## Deployment (Render)

The app can be deployed to [Render](https://render.com) using `render.yaml`:

- **Backend**: Docker service, profile `prod`, health check at `/actuator/health`. Uses external PostgreSQL (Microsoft Azure).
- **Frontend**: Static site built from `frontend` with `npm run build`, published from `frontend/dist/frontend/browser`.

Set `DATABASE_PASSWORD` (and any other secrets) in the Render dashboard.

## Project Structure

```
product-distribution/
├── ai-service/           # AI assistant (Python, FastAPI, ADK, Ollama)
│   ├── app/
│   ├── Dockerfile
│   ├── pyproject.toml
│   └── README.md
├── backend/              # Spring Boot application
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/             # Angular application
│   ├── src/
│   ├── Dockerfile
│   └── package.json
├── data/                 # JSON data (products, stores, warehouses)
│   ├── current/          # Data for local/dev
│   └── test/             # Test data
├── .github/workflows/    # CI/CD (GitHub Actions)
├── create-mcp-readonly-user.sh  # Postgres init: read-only user for MCP Toolbox
├── docker-compose.yml    # postgres, backend, frontend, pgadmin, toolbox, ai-service
├── Makefile              # up, down, build, clean, ai-*, front-*, back-test
├── render.yaml           # Render.com deployment config
└── README.md
```

## Available Scripts

### Makefile (from repo root)

Run these from the project root; they change into the right directory for you.

| Target | Description |
|--------|-------------|
| `make up` | Start stack (no rebuild) |
| `make down` | Stop stack |
| `make build` | Start stack with rebuild |
| `make clean` | Stop stack and remove volumes |
| `make ai-run` | Run ai-service (FastAPI) locally |
| `make ai-web` | Run ADK Web (developer UI) |
| `make ai-eval` | Run ADK evaluation |
| `make ai-lint` | Ruff check (ai-service) |
| `make ai-fix` | Ruff check --fix |
| `make ai-format` | Ruff format |
| `make front-lint` | ESLint (frontend) |
| `make front-format` | Prettier (frontend) |
| `make back-test` | Maven verify (backend) |

### Frontend (from `frontend/`)

- `npm start` - Start development server
- `npm run build` - Build for production
- `npm run lint` - Run ESLint
- `npm run format` - Format code with Prettier

### Backend (from `backend/`)

- `mvn spring-boot:run` - Run Spring Boot application
- `mvn clean package` - Build JAR file
- `mvn test` - Run tests

## Database & profiles

The app always uses **PostgreSQL**. Profiles only change connection and environment details.

### Profile `dev` (default)

- Used for local development and Docker Compose.
- Connection via env: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`.
- Data URLs (products, stores, warehouses) can be overridden with `DATA_PRODUCTS_URL`, `DATA_STORES_URL`, `DATA_WAREHOUSES_URL`; by default they point to the JSON in the repo (GitHub raw).

### Profile `prod`

- Used in Render (and production-like environments).
- Connection via: `DATABASE_HOST`, `DATABASE_PORT`, `DATABASE_NAME`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` (SSL enabled).
- Tuning: HikariCP, batch inserts; scheduler cron configurable with `SCHEDULER_DISTRIBUTION_CRON`.

### PostgreSQL (Docker Compose)

PostgreSQL is created automatically when you run the stack. Use pgAdmin at http://localhost:5050 or any PostgreSQL client.

**Read-only user for MCP (Toolbox)**  
The script `create-mcp-readonly-user.sh` runs automatically the first time the Postgres container starts (when the data volume is empty). It creates a `readonly` role and a `mcp_readonly` user with read-only permissions (SELECT only) on the database. The Toolbox service (MCP SQL used by the AI assistant) connects as `mcp_readonly`, so the assistant cannot modify data. If the database already existed before adding this script, you must create the user manually by running the same SQL, or remove the Postgres volume and bring the stack up again so the init script runs.

## Code Quality

The project uses:

- **ESLint** - Linting (Angular TypeScript and templates)
- **Prettier** - Formatting (frontend)
- **Ruff** - Linting and formatting (ai-service)
- **EditorConfig** - Consistent editor settings

From the repo root: `make front-lint`, `make front-format` for frontend; `make ai-lint`, `make ai-fix`, `make ai-format` for ai-service. Or run the underlying commands in `frontend/` and `ai-service/` respectively.

## Summary

**Development**: `docker compose up --build` or `make build` (postgres, backend, frontend, pgAdmin, toolbox, ai-service; backend uses profile **`dev`**).  
**Production**: Profile **`prod`** (e.g. Render with external PostgreSQL). See `ai-service/README.md` for AI service details.
