# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Gradle wrapper
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Download deps (cache layer)
RUN ./gradlew dependencies --no-daemon || true

# Source and build
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN adduser -D appuser
USER appuser

COPY --from=build /app/build/libs/*.jar app.jar

# Railway sets PORT
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
