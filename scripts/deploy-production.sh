#!/bin/bash

# ✅ DÍA 4 - DEPLOY A PRODUCCIÓN - SCRIPT FINAL

set -e

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║          DÍA 4 - VALIDACIÓN Y DEPLOY A PRODUCCIÓN            ║"
echo "║                   STARK INDUSTRIES                             ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Variables
ENVIRONMENT=${1:-staging}
VERSION=$(date +%Y%m%d%H%M%S)
ROLLBACK_FILE="rollback_${VERSION}.sh"

echo -e "${BLUE}Environment: $ENVIRONMENT${NC}"
echo -e "${BLUE}Version: $VERSION${NC}"
echo ""

# ═══════════════════════════════════════════════════════════════════════════════
# FASE 1: PRE-DEPLOYMENT CHECKS
# ═══════════════════════════════════════════════════════════════════════════════

echo -e "${YELLOW}FASE 1: Pre-Deployment Checks${NC}"

# Verificar docker
if ! command -v docker &> /dev/null; then
    echo -e "${RED}✗ Docker not found${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Docker found${NC}"

# Verificar docker-compose
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}✗ Docker Compose not found${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Docker Compose found${NC}"

# Verificar .env
if [ ! -f ".env" ]; then
    echo -e "${RED}✗ .env file not found${NC}"
    exit 1
fi
echo -e "${GREEN}✓ .env file found${NC}"

echo ""

# ═══════════════════════════════════════════════════════════════════════════════
# FASE 2: BUILD & TEST
# ═══════════════════════════════════════════════════════════════════════════════

echo -e "${YELLOW}FASE 2: Build & Test${NC}"

# Compilar
echo -e "${BLUE}  Building project...${NC}"
mvn clean package -DskipTests -q
echo -e "${GREEN}✓ Build successful${NC}"

# Tests
echo -e "${BLUE}  Running tests...${NC}"
mvn test -q
echo -e "${GREEN}✓ All tests passed${NC}"

# Build Docker images
echo -e "${BLUE}  Building Docker images...${NC}"
docker-compose -f docker-compose-sdd.yml build --no-cache
echo -e "${GREEN}✓ Docker images built${NC}"

echo ""

# ═══════════════════════════════════════════════════════════════════════════════
# FASE 3: STOP CURRENT ENVIRONMENT
# ═══════════════════════════════════════════════════════════════════════════════

echo -e "${YELLOW}FASE 3: Stopping Current Environment${NC}"

# Backup actual
if [ -f "docker-compose-running.yml" ]; then
    cp docker-compose-running.yml "docker-compose-backup-${VERSION}.yml"
    echo -e "${GREEN}✓ Backup created${NC}"
fi

# Stop services
docker-compose -f docker-compose-sdd.yml down
echo -e "${GREEN}✓ Services stopped${NC}"

echo ""

# ═══════════════════════════════════════════════════════════════════════════════
# FASE 4: DATABASE MIGRATION
# ═══════════════════════════════════════════════════════════════════════════════

echo -e "${YELLOW}FASE 4: Database Migration${NC}"

echo -e "${BLUE}  Starting DB...${NC}"
docker-compose -f docker-compose-sdd.yml up -d postgres
sleep 10
echo -e "${GREEN}✓ DB started${NC}"

# Run migrations (si existen)
if [ -f "scripts/db-migrate.sh" ]; then
    echo -e "${BLUE}  Running migrations...${NC}"
    ./scripts/db-migrate.sh
    echo -e "${GREEN}✓ Migrations completed${NC}"
fi

echo ""

# ═══════════════════════════════════════════════════════════════════════════════
# FASE 5: START ALL SERVICES
# ═══════════════════════════════════════════════════════════════════════════════

echo -e "${YELLOW}FASE 5: Starting All Services${NC}"

docker-compose -f docker-compose-sdd.yml up -d
sleep 30

echo -e "${GREEN}✓ All services started${NC}"

# Save running config for rollback
cp docker-compose-sdd.yml docker-compose-running.yml

echo ""

# ═══════════════════════════════════════════════════════════════════════════════
# FASE 6: HEALTH CHECKS
# ═══════════════════════════════════════════════════════════════════════════════

echo -e "${YELLOW}FASE 6: Health Checks${NC}"

services=(
    "http://localhost:8761:Eureka"
    "http://localhost:8080:Gateway"
    "http://localhost:8081:Auth"
    "http://localhost:8082:Sensor"
    "http://localhost:8083:Alert"
    "http://localhost:8084:Access"
    "http://localhost:8085:Notification"
)

for service in "${services[@]}"; do
    IFS=':' read -r url name <<< "$service"
    if curl -s -f "$url/actuator/health" > /dev/null 2>&1; then
        echo -e "${GREEN}✓ $name${NC}"
    else
        echo -e "${RED}✗ $name${NC}"
        echo -e "${YELLOW}Rolling back...${NC}"
        docker-compose -f docker-compose-sdd.yml down
        exit 1
    fi
done

echo ""

# ═══════════════════════════════════════════════════════════════════════════════
# FASE 7: SMOKE TESTS
# ═══════════════════════════════════════════════════════════════════════════════

echo -e "${YELLOW}FASE 7: Smoke Tests${NC}"

# Test 1: Login
echo -e "${BLUE}  Test: Login...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@Secure2024!"}')

if echo "$LOGIN_RESPONSE" | grep -q "token\|JWT"; then
    echo -e "${GREEN}✓ Login test passed${NC}"
else
    echo -e "${RED}✗ Login test failed${NC}"
    exit 1
fi

# Test 2: Health
echo -e "${BLUE}  Test: Health check...${NC}"
if curl -s http://localhost:8080/actuator/health | grep -q "UP"; then
    echo -e "${GREEN}✓ Health check passed${NC}"
else
    echo -e "${RED}✗ Health check failed${NC}"
    exit 1
fi

echo ""

# ═══════════════════════════════════════════════════════════════════════════════
# FASE 8: PERFORMANCE MONITORING
# ═══════════════════════════════════════════════════════════════════════════════

echo -e "${YELLOW}FASE 8: Performance Monitoring${NC}"

echo -e "${BLUE}  Measuring latency...${NC}"

# 10 requests
total_time=0
for i in {1..10}; do
    start=$(date +%s%N)
    curl -s -X POST http://localhost:8080/login \
      -H "Content-Type: application/json" \
      -d '{"username":"admin","password":"Admin@Secure2024!"}' > /dev/null
    end=$(date +%s%N)
    elapsed=$(( (end - start) / 1000000 ))
    total_time=$(( total_time + elapsed ))
    echo -e "  Request $i: ${elapsed}ms"
done

avg=$(( total_time / 10 ))
echo -e "${GREEN}✓ Average latency: ${avg}ms${NC}"

if [ $avg -lt 300 ]; then
    echo -e "${GREEN}✓ Latency within target (< 300ms)${NC}"
else
    echo -e "${YELLOW}⚠ Latency above target: ${avg}ms > 300ms${NC}"
fi

echo ""

# ═══════════════════════════════════════════════════════════════════════════════
# FASE 9: CREATE ROLLBACK SCRIPT
# ═══════════════════════════════════════════════════════════════════════════════

echo -e "${YELLOW}FASE 9: Create Rollback Script${NC}"

cat > "$ROLLBACK_FILE" << 'EOF'
#!/bin/bash

echo "🔄 Rolling back to previous version..."

# Stop current
docker-compose -f docker-compose-sdd.yml down

# Restore from backup
if [ -f "docker-compose-backup-${VERSION}.yml" ]; then
    cp "docker-compose-backup-${VERSION}.yml" docker-compose-sdd.yml
    docker-compose -f docker-compose-sdd.yml up -d
    echo "✅ Rollback completed"
else
    echo "❌ Backup not found"
    exit 1
fi
EOF

chmod +x "$ROLLBACK_FILE"
echo -e "${GREEN}✓ Rollback script created: $ROLLBACK_FILE${NC}"

echo ""

# ═══════════════════════════════════════════════════════════════════════════════
# FASE 10: DEPLOYMENT COMPLETE
# ═══════════════════════════════════════════════════════════════════════════════

echo -e "${YELLOW}FASE 10: Deployment Complete${NC}"

echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo -e "║${GREEN}  ✅ DEPLOYMENT SUCCESSFUL - STARK DISTRIBUIDOS${NC}            ║"
echo "╠════════════════════════════════════════════════════════════════╣"
echo "║ Services:                                                      ║"
echo "║   • Eureka:      http://localhost:8761                        ║"
echo "║   • Gateway:     http://localhost:8080                        ║"
echo "║   • Auth:        http://localhost:8081                        ║"
echo "║   • Sensor:      http://localhost:8082                        ║"
echo "║   • Alert:       http://localhost:8083                        ║"
echo "║   • Access:      http://localhost:8084                        ║"
echo "║   • Notification:http://localhost:8085                        ║"
echo "║                                                                ║"
echo "║ Dashboards:                                                    ║"
echo "║   • RabbitMQ:    http://localhost:15672                       ║"
echo "║   • Kafka UI:    http://localhost:8888                        ║"
echo "║                                                                ║"
echo "║ Monitoring:                                                    ║"
echo "║   • Average Latency: ${avg}ms                                    ║"
echo "║   • Status: All healthy ✅                                    ║"
echo "║                                                                ║"
echo "║ Rollback (if needed):                                          ║"
echo "║   bash $ROLLBACK_FILE                                          ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

echo -e "${GREEN}🎉 STARK DISTRIBUIDOS is now LIVE! 🎉${NC}"
echo ""

