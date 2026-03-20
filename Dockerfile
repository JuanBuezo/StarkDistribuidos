# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copiar archivos de configuración
COPY pom.xml .

# Descargar dependencias
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar y construir el JAR
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk-alpine

LABEL maintainer="Stark Industries <security@starkindustries.com>"
LABEL description="Sistema de Seguridad Distribuido - Stark Industries"

WORKDIR /opt/stark

# Instalar herramientas útiles
RUN apk add --no-cache curl

# Copiar el JAR construido desde la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Crear usuario no-root
RUN addgroup -g 1001 stark && \
    adduser -D -u 1001 -G stark stark && \
    chown -R stark:stark /opt/stark

# Cambiar a usuario no-root
USER stark

# Puertos
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/stark-security/actuator/health || exit 1

# Entrypoint
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=production
ENV SERVER_PORT=8080
ENV MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics

