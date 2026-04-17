# 🎯 QUICK START - GUÍA RÁPIDA DE 5 MINUTOS

**Fecha:** 17 de Abril de 2026  
**Proyecto:** STARK INDUSTRIES - Distributed Security System  
**Modo:** ⚡ QUICK START

---

## ⚡ INICIO EN 5 MINUTOS

### 1️⃣ Compilar (2 min)
```bash
cd C:\4ICAI\SISTEMAS\ DISTRIBUIDOS\STARK_DISTRIBUIDOS\StarkDistribuidos
mvn clean package -DskipTests
```

**✅ Esperado:** `BUILD SUCCESS`

---

### 2️⃣ Iniciar Servicios (1 min)
```bash
docker-compose -f docker-compose-sdd.yml up -d
```

**✅ Verificar:**
```bash
docker-compose ps
# Todos deben estar en "Up"
```

---

### 3️⃣ Health Check (1 min)
```bash
# Gateway
curl http://localhost:8080/actuator/health

# Debe responder: {"status":"UP"}
```

---

### 4️⃣ Primeros Tests (1 min)

#### Autenticación
```bash
# Login
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@Secure2024!"}'

# Copiar el "token" de la respuesta
```

#### Ver Sensores
```bash
# Usar el TOKEN obtenido
curl -X GET http://localhost:8082/api/sensors/readings \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### Ver Alertas
```bash
curl -X GET http://localhost:8083/api/alerts/active \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 📚 DOCUMENTACIÓN RÁPIDA

| Tarea | Documento | Tiempo |
|-------|-----------|--------|
| **Ver estructura** | `DEPLOYMENT_COMPLETE_OVERVIEW.md` | 5 min |
| **Entender componentes** | `INDEX_COMPLETE.md` | 10 min |
| **Ver ejemplos** | `USAGE_EXAMPLES.md` | 15 min |
| **Validar** | `POC_VALIDATION_CHECKLIST.md` | 20 min |
| **Security** | `SECURITY_DAY*.md` | Var |

---

## 🔑 CREDENCIALES

```
Admin:
  Username: admin
  Password: Admin@Secure2024!
  Role: ADMIN

User:
  Username: user101
  Password: User@Secure2024!
  Role: VIEWER
```

---

## 🔗 ACCESOS RÁPIDOS

```
Gateway:           http://localhost:8080
Eureka:            http://localhost:8761
RabbitMQ:          http://localhost:15672
Grafana:           http://localhost:3000
Kibana:            http://localhost:5601
```

---

## 🆘 TROUBLESHOOTING

### Puerto ocupado
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Base de datos error
```bash
docker-compose restart postgres
docker-compose logs postgres
```

### Sin mensajes Kafka
```bash
docker exec stark-kafka \
  kafka-topics.sh --bootstrap-server kafka:9092 --list
```

---

## ✅ CHECKLIST RÁPIDO

- [ ] Maven compila
- [ ] Docker Compose inicia
- [ ] Gateway responde
- [ ] Login funciona
- [ ] Ver sensores funciona
- [ ] Alertas activas
- [ ] Notificaciones enviadas

---

**Status:** 🟢 LISTO PARA USAR


