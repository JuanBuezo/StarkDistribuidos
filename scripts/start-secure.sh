#!/bin/bash

# ✅ STARK INDUSTRIES - Secure Startup Script
# Starts all microservices with security checks

set -e

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║        STARK INDUSTRIES - SECURITY MICROSERVICES              ║"
echo "║              Starting Secure Environment                      ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if .env exists
if [ ! -f ".env" ]; then
    echo -e "${YELLOW}⚠️  .env file not found. Creating from .env.example...${NC}"
    cp .env.example .env
    echo -e "${RED}⚠️  IMPORTANT: Update .env with your actual credentials!${NC}"
    exit 1
fi

# Load environment variables
export $(cat .env | grep -v '#' | xargs)

echo -e "${GREEN}✓ Environment variables loaded${NC}"

# Check Docker
if ! command -v docker &> /dev/null; then
    echo -e "${RED}✗ Docker not found. Please install Docker.${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Docker is installed${NC}"

# Check Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}✗ Docker Compose not found. Please install Docker Compose.${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Docker Compose is installed${NC}"

# Build images
echo ""
echo -e "${YELLOW}Building Docker images...${NC}"
docker-compose build --no-cache

echo ""
echo -e "${GREEN}✓ Docker images built successfully${NC}"

# Start services
echo ""
echo -e "${YELLOW}Starting microservices...${NC}"
docker-compose up -d

echo ""
echo -e "${GREEN}✓ Services started${NC}"

# Wait for services to be healthy
echo ""
echo -e "${YELLOW}Waiting for services to be healthy...${NC}"
sleep 30

# Check health
echo ""
echo -e "${YELLOW}Health check:${NC}"

services=(
    "http://localhost:8761/actuator/health:Eureka"
    "http://localhost:8080/actuator/health:Gateway"
    "http://localhost:8081/auth/test:Auth"
    "http://localhost:8082/sensors/health:Sensor"
    "http://localhost:8083/alerts/health:Alert"
    "http://localhost:8084/access/health:Access"
    "http://localhost:8085/notifications/health:Notification"
)

for service in "${services[@]}"; do
    IFS=':' read -r url name <<< "$service"
    if curl -s -f "$url" > /dev/null 2>&1; then
        echo -e "${GREEN}✓ $name${NC}"
    else
        echo -e "${RED}✗ $name (not responding yet, may take longer)${NC}"
    fi
done

echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo -e "║${GREEN}        ✓ STARK INDUSTRIES RUNNING SECURELY          ${NC}║"
echo "╠════════════════════════════════════════════════════════════════╣"
echo "║ Services:                                                      ║"
echo "║   • Eureka:      http://localhost:8761                        ║"
echo "║   • Gateway:     http://localhost:8080                        ║"
echo "║   • Auth:        http://localhost:8081                        ║"
echo "║   • Sensor:      http://localhost:8082                        ║"
echo "║   • Alert:       http://localhost:8083                        ║"
echo "║   • Access:      http://localhost:8084                        ║"
echo "║   • Notification:http://localhost:8085                        ║"
echo "║   • Frontend:    http://localhost:8086/stark-security         ║"
echo "║   • MailHog:     http://localhost:8025                        ║"
echo "║                                                                ║"
echo "║ Default Credentials (from .env):                              ║"
echo "║   • Username: ${SECURITY_USER_NAME}                              ║"
echo "║   • Password: ****** (set in .env)                            ║"
echo "╠════════════════════════════════════════════════════════════════╣"
echo "║ View logs:                                                     ║"
echo "║   docker-compose logs -f [service-name]                       ║"
echo "║                                                                ║"
echo "║ Stop services:                                                 ║"
echo "║   docker-compose down                                          ║"
echo "║                                                                ║"
echo "║ Documentation: docs/START_HERE.md                             ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

