# =========================
# Build stage
# =========================
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Gradle wrapper
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# MUHIM: gradlew ga permission beramiz
RUN chmod +x gradlew

# Dependency cache
RUN ./gradlew dependencies --no-daemon || true

# Source
COPY src src

# Build jar
RUN ./gradlew bootJar --no-daemon -x test

# =========================
# Run stage
# =========================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN adduser -D appuser
USER appuser

COPY --from=build /app/build/libs/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
