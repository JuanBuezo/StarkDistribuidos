# Gateway Microservice - KISS Architecture (Keep It Simple, Stupid)
# Multi-stage build for minimal image size

FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy parent and gateway pom
COPY ../pom.xml .
COPY pom.xml pom.xml

# Download dependencies
RUN mvn dependency:go-offline -B -q

# Copy source
COPY src ./src

# Build JAR
RUN mvn clean package -DskipTests -q

# Runtime stage
FROM eclipse-temurin:17-jdk-alpine

LABEL maintainer="Stark Industries <security@starkindustries.com>"
LABEL description="API Gateway - Stark Industries"
LABEL version="2.0.0"

WORKDIR /opt/stark

# Install curl for health checks (minimal)
RUN apk add --no-cache curl

# Create non-root user for security
RUN addgroup -g 1001 stark && \
    adduser -D -u 1001 -G stark stark && \
    chown -R stark:stark /opt/stark

# Copy JAR from builder
COPY --from=builder /app/target/*.jar app.jar

# Switch to non-root user
USER stark

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM options for security and performance
ENV JAVA_OPTS="-Xmx256m -Xms128m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Spring profiles and security settings
ENV SPRING_PROFILES_ACTIVE=production
ENV MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics

# Start application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]

