# Stack-Specific Challenges: Java + Vue

When adapting this stack for k8s-ephemeral-environments, we encountered several issues that differ from the Node.js/React reference implementation.

## 1. MongoDB Connection String

**Problem:** Spring Boot can use either the full MongoDB URI or individual environment variables for database connection.

**Solution:** Use `MONGODB_URL` which is injected by the platform:

```yaml
# application.yml
spring:
  data:
    mongodb:
      uri: ${MONGODB_URL:mongodb://localhost:27017/app}
```

The platform injects `MONGODB_URL` in the format `mongodb://user:pass@host:port/database`.

## 2. Gradle Wrapper in Docker

**Problem:** The Gradle wrapper needs to download Gradle during the first build, which can be slow.

**Solution:** Pre-download dependencies in a separate layer for better caching:

```dockerfile
COPY todo-api/gradle gradle
COPY todo-api/gradlew todo-api/build.gradle.kts todo-api/settings.gradle.kts ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon
```

## 3. Static File Serving with SPA Fallback

**Problem:** Spring Boot needs to serve Vue's static files and handle SPA routing (redirect non-API routes to `index.html`).

**Solution:** Configure a custom `PathResourceResolver` in `WebConfig.java`:

```java
@Override
protected Resource getResource(String resourcePath, Resource location) throws IOException {
    Resource requestedResource = location.createRelative(resourcePath);

    if (requestedResource.exists() && requestedResource.isReadable()) {
        return requestedResource;
    }

    // Don't fallback for API routes
    if (resourcePath.startsWith("api/")) {
        return null;
    }

    // SPA fallback
    return new ClassPathResource("/static/index.html");
}
```

## 4. Vue Build Output Path

**Problem:** Vite outputs built files to `dist/` by default, and this needs to be copied to Spring Boot's `static` folder.

**Solution:** Copy from the correct path in Dockerfile:

```dockerfile
COPY --from=frontend-builder /app/dist ./src/main/resources/static/
```

## 5. CORS Configuration

**Problem:** During development, Vue dev server runs on port 5173 and needs to access the API on port 8080.

**Solution:**
- Development: Use Vite's proxy feature in `vite.config.ts`
- Production: Configure CORS in Spring Boot via `CORS_ORIGIN` environment variable

```typescript
// vite.config.ts
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## Key Differences from Node.js Stack

| Aspect | Node.js | Java |
|--------|---------|------|
| Default Port | 3000 | 8080 |
| Package Manager | npm | Gradle |
| Build Output | `dist/` | `build/libs/*.jar` |
| DB Connection | Direct URI | Spring Data MongoDB |
| Static Files | Express `static()` | `resources/static/` |
| Hot Reload | Built-in | `./gradlew bootRun` |

## Key Differences from .NET Stack

| Aspect | .NET | Java |
|--------|------|------|
| Framework | ASP.NET Core | Spring Boot |
| Build Tool | MSBuild/dotnet CLI | Gradle |
| Package Manager | NuGet | Maven Central |
| Base Image | mcr.microsoft.com/dotnet | eclipse-temurin |
| Config Format | appsettings.json | application.yml |
| Dependency Injection | Built-in | Spring IoC |

## Environment Variables Consumed

| Variable | Used For |
|----------|----------|
| `MONGODB_URL` | Full MongoDB connection string |
| `CORS_ORIGIN` | Allowed CORS origins (comma-separated) |

## MongoDB vs PostgreSQL

| Aspect | PostgreSQL | MongoDB |
|--------|------------|---------|
| ID Type | `int` (auto-increment) | `String` (ObjectId) |
| Schema | Fixed (migrations) | Flexible (no migrations) |
| Driver | Npgsql (ADO.NET) | MongoDB Java Driver |
| Spring Integration | JPA | Spring Data MongoDB |
| Health Check | `pg_isready` | `mongosh ping` |
