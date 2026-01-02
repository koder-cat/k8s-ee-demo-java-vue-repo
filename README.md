# Todo App (Java + Vue)

A simple Todo application demonstrating [k8s-ephemeral-environments](https://github.com/koder-cat/k8s-ephemeral-environments) with Java 21 (Spring Boot) and Vue 3.

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Spring Boot 3.x (Java 21) |
| Frontend | Vue 3 (Composition API) |
| Database | MongoDB |
| Build Tool | Gradle (Kotlin DSL) |
| Styling | Tailwind CSS 3 |

## Project Structure

```
todo-app/
├── todo-api/             # Spring Boot Web API
│   ├── src/main/java/    # Java source files
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── static/       # Vue build output
│   └── build.gradle.kts
├── todo-web/             # Vue 3 frontend
│   └── src/              # Vue components and services
├── Dockerfile            # Multi-stage build
├── docker-compose.yml    # Local development
└── k8s-ee.yaml           # PR environment config
```

## Local Development

### Prerequisites

- Java 21 (JDK)
- Node.js 22+
- Docker and Docker Compose

### Setup

```bash
cd todo-app

# Start MongoDB
docker compose up -d

# Run API (terminal 1)
cd todo-api
./gradlew bootRun

# Run Vue (terminal 2)
cd todo-web
npm install
npm run dev
```

Open http://localhost:5173

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/todos` | List all todos |
| GET | `/api/todos/{id}` | Get todo by ID |
| POST | `/api/todos` | Create todo |
| PATCH | `/api/todos/{id}` | Update todo |
| DELETE | `/api/todos/{id}` | Delete todo |
| GET | `/api/health` | Health check |

## PR Environments

Each pull request automatically gets an isolated environment:

1. PR opened -> Namespace created
2. App + MongoDB deployed
3. Preview URL: `todo-app-java-pr-{number}.k8s-ee.genesluna.dev`
4. PR closed -> Environment destroyed

## License

MIT
