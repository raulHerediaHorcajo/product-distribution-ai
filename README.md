# Product Distribution Dashboard

A full-stack application for managing and visualizing product distribution across warehouses and stores. The application provides an interactive dashboard with map visualization, data tables, and detailed metrics.

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

### Development Tools
- **Docker & Docker Compose** - Containerization
- **ESLint** - Code linting
- **Prettier** - Code formatting
- **pgAdmin** - Database administration

## Prerequisites

- **Docker & Docker Compose** – to run the full stack (postgres, backend, frontend, pgAdmin).

Optional (only if you want to run tests, lint or build outside Docker):

- **Node.js** (v18+), **npm** or **yarn** – frontend scripts
- **Java 17** (JDK), **Maven 3.6+** – backend scripts

## Development Setup

Development runs with **Docker Compose**: postgres, backend, frontend and pgAdmin in one command. The backend uses the **`dev`** profile.

#### Start the stack

From the project root:

```bash
docker compose up --build
```

Use `docker compose up --build` again after code changes, or plain `docker compose up` if you didn't change any code.

#### Access points

- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080
- **pgAdmin**: http://localhost:5050

## CI/CD (GitHub Actions)

- **PR Validation** – On pull requests, runs only what changed:
  - **Frontend**: lint and build (`pr-frontend-analysis.yml`)
  - **Backend**: tests and SonarCloud (`pr-backend-test-and-sonar.yml`)
  - **Data**: JSON structure validation (`pr-data-validation.yml`)
- **Main branch**: Backend analysis on push to main (`main-backend-sonar.yml`)

## Deployment (Render)

The app can be deployed to [Render](https://render.com) using `render.yaml`:

- **Backend**: Docker service, profile `prod`, health check at `/actuator/health`. Uses external PostgreSQL (Microsoft Azure).
- **Frontend**: Static site built from `frontend` with `npm run build`, published from `frontend/dist/frontend/browser`.

Set `DATABASE_PASSWORD` (and any other secrets) in the Render dashboard.

## Project Structure

```
product-distribution/
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
├── docker-compose.yml    # Docker services (postgres, backend, frontend, pgadmin)
├── render.yaml           # Render.com deployment config
└── README.md
```

## Available Scripts

### Frontend

- `npm start` - Start development server
- `npm run build` - Build for production
- `npm run lint` - Run ESLint
- `npm run format` - Format code with Prettier

### Backend

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

## Code Quality

The project uses:

- **ESLint** - For code linting (Angular TypeScript and templates)
- **Prettier** - For code formatting
- **EditorConfig** - For consistent editor settings

Run `npm run lint` in the frontend directory to check for linting issues, and `npm run format` to format code.

## Summary

**Development**: `docker compose up --build` (postgres, backend, frontend, pgAdmin; backend uses profile **`dev`**).  
**Production**: Profile **`prod`** (e.g. Render with external PostgreSQL).
