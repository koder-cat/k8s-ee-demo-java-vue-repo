# Stage 1: Build Vue
FROM node:22-alpine AS frontend-builder
WORKDIR /app
COPY todo-web/package*.json ./
RUN npm ci
COPY todo-web/ ./
RUN npm run build
# Output: dist/

# Stage 2: Build Java
FROM eclipse-temurin:21-jdk-alpine AS backend-builder
WORKDIR /app
COPY todo-api/gradle gradle
COPY todo-api/gradlew todo-api/build.gradle.kts todo-api/settings.gradle.kts ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon
COPY todo-api/src ./src
COPY --from=frontend-builder /app/dist ./src/main/resources/static/
RUN ./gradlew bootJar --no-daemon

# Stage 3: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup --system --gid 1001 javauser && \
    adduser --system --uid 1001 appuser --ingroup javauser
COPY --from=backend-builder --chown=appuser:javauser /app/build/libs/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
