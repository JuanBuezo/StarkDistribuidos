# Auth Microservice - KISS Architecture
# Multi-stage build for minimal image size

FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy parent and auth pom
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
LABEL description="Auth Service - Stark Industries"
LABEL version="2.0.0"

WORKDIR /opt/stark

RUN apk add --no-cache curl

RUN addgroup -g 1001 stark && \
    adduser -D -u 1001 -G stark stark && \
    chown -R stark:stark /opt/stark

COPY --from=builder /app/target/*.jar app.jar

USER stark

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8081/auth/test || exit 1

ENV JAVA_OPTS="-Xmx256m -Xms128m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
ENV SPRING_PROFILES_ACTIVE=production

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]

