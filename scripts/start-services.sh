#!/bin/bash

echo "╔═══════════════════════════════════════════════════════╗"
echo "║   STARK INDUSTRIES - MICROSERVICIOS START SCRIPT       ║"
echo "╚═══════════════════════════════════════════════════════╝"
echo ""

echo "Building..."
mvn clean install -DskipTests -q

if [ $? -eq 0 ]; then
    echo "✓ Build successful"
    echo ""
    echo "Starting Docker Compose..."
    docker-compose up -d

    sleep 3
    docker-compose ps

    echo ""
    echo "✓ Services started!"
    echo "  API Gateway: http://localhost:8080/stark"
    echo "  Eureka: http://localhost:8761/eureka"
else
    echo "✗ Build failed"
    exit 1
fi

