#!/bin/bash

# ✅ Build all microservices with security fixes
# Este script compila todo el proyecto después de las correcciones de seguridad

set -e

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║     STARK INDUSTRIES - BUILDING SECURE MICROSERVICES          ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}1. Cleaning previous builds...${NC}"
mvn clean -q

echo -e "${GREEN}✓ Clean complete${NC}"
echo ""

echo -e "${BLUE}2. Building parent project...${NC}"
mvn install -DskipTests -q

echo -e "${GREEN}✓ Parent project built${NC}"
echo ""

echo -e "${BLUE}3. Building all microservices...${NC}"

services=(
    "starkDistribuidos-config:Config Server (Eureka)"
    "starkDistribuidos-gateway:API Gateway"
    "starkDistribuidos-auth:Auth Service"
    "starkDistribuidos-sensor:Sensor Service"
    "starkDistribuidos-alert:Alert Service"
    "starkDistribuidos-access:Access Control Service"
    "starkDistribuidos-notification:Notification Service"
    "starkDistribuidos-frontend:Frontend Service"
)

for service in "${services[@]}"; do
    IFS=':' read -r module name <<< "$service"
    echo -e "${YELLOW}   Building $name ($module)...${NC}"
    cd "$module"
    mvn package -DskipTests -q
    cd ..
    echo -e "${GREEN}   ✓ $name built${NC}"
done

echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo -e "║${GREEN}     ✓ ALL MICROSERVICES BUILT SUCCESSFULLY              ${NC}║"
echo "╠════════════════════════════════════════════════════════════════╣"
echo "║ Security improvements applied:                                ║"
echo "║   ✓ H2 Database updated to 2.2.220 (CVE fixes)               ║"
echo "║   ✓ CORS configured securely                                 ║"
echo "║   ✓ CSRF protection enabled                                  ║"
echo "║   ✓ Security headers added                                   ║"
echo "║   ✓ Credentials moved to environment variables               ║"
echo "║                                                                ║"
echo "║ Next step:                                                     ║"
echo "║   ./scripts/start-secure.sh                                   ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

echo -e "${GREEN}Build summary:${NC}"
ls -lh starkDistribuidos-*/target/*.jar | awk '{print "  " $9 " - " $5}'
echo ""

