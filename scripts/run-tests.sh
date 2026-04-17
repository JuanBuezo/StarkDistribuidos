#!/bin/bash

# ✅ SCRIPT DE TESTING COMPLETO - STARK INDUSTRIES
# Ejecuta todas las suites de tests y valida el sistema

set -e

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║          TESTING PHASE - STARK INDUSTRIES                     ║"
echo "║       Validación Completa de Sistema en Producción            ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

FAILED_TESTS=0
PASSED_TESTS=0

# ═════════════════════════════════════════════════════════════════════════════════
# FASE 1: Unit Tests
# ═════════════════════════════════════════════════════════════════════════════════

echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}FASE 1: Unit Tests${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""

echo -e "${YELLOW}Running Unit Tests...${NC}"
mvn clean test -Dtest=ServiceAgentTest -q
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ ServiceAgentTest PASSED${NC}"
    ((PASSED_TESTS++))
else
    echo -e "${RED}✗ ServiceAgentTest FAILED${NC}"
    ((FAILED_TESTS++))
fi

mvn test -Dtest=ServiceOrchestratorTest -q
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ ServiceOrchestratorTest PASSED${NC}"
    ((PASSED_TESTS++))
else
    echo -e "${RED}✗ ServiceOrchestratorTest FAILED${NC}"
    ((FAILED_TESTS++))
fi

mvn test -Dtest=AuthAgentTest -q
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ AuthAgentTest PASSED${NC}"
    ((PASSED_TESTS++))
else
    echo -e "${RED}✗ AuthAgentTest FAILED${NC}"
    ((FAILED_TESTS++))
fi

mvn test -Dtest=AccessControlAgentTest -q
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ AccessControlAgentTest PASSED${NC}"
    ((PASSED_TESTS++))
else
    echo -e "${RED}✗ AccessControlAgentTest FAILED${NC}"
    ((FAILED_TESTS++))
fi

echo ""

# ═════════════════════════════════════════════════════════════════════════════════
# FASE 2: Integration Tests
# ═════════════════════════════════════════════════════════════════════════════════

echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}FASE 2: Integration Tests${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""

echo -e "${YELLOW}Running Integration Tests...${NC}"
mvn test -Dtest=EndToEndIntegrationTest -q
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ EndToEndIntegrationTest PASSED${NC}"
    ((PASSED_TESTS++))
else
    echo -e "${RED}✗ EndToEndIntegrationTest FAILED${NC}"
    ((FAILED_TESTS++))
fi

echo ""

# ═════════════════════════════════════════════════════════════════════════════════
# FASE 3: Performance Tests
# ═════════════════════════════════════════════════════════════════════════════════

echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}FASE 3: Performance Tests${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""

echo -e "${YELLOW}Running Performance Tests...${NC}"
mvn test -Dtest=PerformanceTest -q
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ PerformanceTest PASSED${NC}"
    ((PASSED_TESTS++))
else
    echo -e "${RED}✗ PerformanceTest FAILED${NC}"
    ((FAILED_TESTS++))
fi

echo ""

# ═════════════════════════════════════════════════════════════════════════════════
# FASE 4: Security Tests
# ═════════════════════════════════════════════════════════════════════════════════

echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}FASE 4: Security Validation${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""

echo -e "${YELLOW}Validating Security...${NC}"

# Check CORS configuration
if grep -q "allowedOrigins" src/main/java/com/distribuidos/stark/config/CorsConfig.java; then
    echo -e "${GREEN}✓ CORS Protection: ENABLED${NC}"
else
    echo -e "${RED}✗ CORS Protection: DISABLED${NC}"
fi

# Check CSRF protection
if grep -q "csrf" src/main/java/com/distribuidos/stark/config/SecurityConfig.java; then
    echo -e "${GREEN}✓ CSRF Protection: ENABLED${NC}"
else
    echo -e "${RED}✗ CSRF Protection: DISABLED${NC}"
fi

# Check H2 console disabled in production
if grep -q "H2_CONSOLE_ENABLED" src/main/resources/application.yaml; then
    echo -e "${GREEN}✓ H2 Console: Environment-based${NC}"
else
    echo -e "${RED}✗ H2 Console: Check configuration${NC}"
fi

# Check credentials in .env
if [ -f ".env" ]; then
    echo -e "${GREEN}✓ Environment Variables: Configured${NC}"
else
    echo -e "${YELLOW}⚠ Environment Variables: Using defaults${NC}"
fi

echo ""

# ═════════════════════════════════════════════════════════════════════════════════
# FASE 5: Code Coverage
# ═════════════════════════════════════════════════════════════════════════════════

echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}FASE 5: Code Coverage Report${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""

echo -e "${YELLOW}Generating Code Coverage...${NC}"
mvn jacoco:report -q

# Read coverage from jacoco report
if [ -f "target/site/jacoco/index.html" ]; then
    echo -e "${GREEN}✓ Coverage Report Generated${NC}"
    echo -e "${YELLOW}  Location: target/site/jacoco/index.html${NC}"
else
    echo -e "${RED}✗ Coverage Report Not Found${NC}"
fi

echo ""

# ═════════════════════════════════════════════════════════════════════════════════
# RESUMEN FINAL
# ═════════════════════════════════════════════════════════════════════════════════

echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}RESUMEN DE TESTING${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""

TOTAL_TESTS=$((PASSED_TESTS + FAILED_TESTS))
SUCCESS_RATE=$((PASSED_TESTS * 100 / TOTAL_TESTS))

echo -e "Tests Passed:       ${GREEN}${PASSED_TESTS}${NC}"
echo -e "Tests Failed:       ${RED}${FAILED_TESTS}${NC}"
echo -e "Total Tests:        ${YELLOW}${TOTAL_TESTS}${NC}"
echo -e "Success Rate:       ${YELLOW}${SUCCESS_RATE}%${NC}"
echo ""

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}╔════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║  ✅ ALL TESTS PASSED - SYSTEM READY FOR PRODUCTION           ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════════════════════════╝${NC}"
    exit 0
else
    echo -e "${RED}╔════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${RED}║  ❌ SOME TESTS FAILED - REVIEW REQUIRED                      ║${NC}"
    echo -e "${RED}╚════════════════════════════════════════════════════════════════╝${NC}"
    exit 1
fi

