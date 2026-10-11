# Multi-stage Dockerfile untuk deploy CabaiDetect di Railway / Cloud Platform

# Stage 1: Build JAR dengan JDK 21
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy wrapper gradle
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN chmod +x ./gradlew

# Cache dependencies
RUN ./gradlew dependencies --no-daemon || true

# Copy source code & dataset di classpath
COPY src ./src

# Build JAR tanpa test (hemat waktu & memory build)
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Runtime image ringan dengan JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Setup user non-root untuk keamanan
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Buat direktori upload lokal
RUN mkdir -p /app/uploads && chown -R appuser:appgroup /app

# Ambil artifact jar dari builder
COPY --from=builder /app/build/libs/*.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod
ENV PORT=8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
