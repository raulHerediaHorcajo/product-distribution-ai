.PHONY: up down build clean ai-lint ai-fix ai-format ai-run ai-web ai-eval front-lint front-format back-test

# Docker Compose
up:
	docker-compose up

down:
	docker-compose down

build:
	docker-compose up --build

clean:
	docker-compose down -v

# AI service
ai-lint:
	cd ai-service && uv run ruff check app/

ai-fix:
	cd ai-service && uv run ruff check app/ --fix

ai-format:
	cd ai-service && uv run ruff format app/

ai-run:
	cd ai-service && uv run uvicorn app.main:app --reload --host 0.0.0.0 --port 8000

ai-web:
	cd ai-service && uv run adk web

ai-eval:
	cd ai-service && uv run adk eval --config_file_path app/eval/eval_config.json app app/eval/goldenDataset.evalset.json

# Frontend
front-lint:
	cd frontend && npm run lint

front-format:
	cd frontend && npm run format

# Backend
back-test:
	cd backend && mvn -B clean verify