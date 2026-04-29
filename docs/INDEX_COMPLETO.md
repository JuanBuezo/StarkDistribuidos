# 📚 Índice de Documentación - Stark Industries

Bienvenido a la documentación completa del sistema Stark Industries. Este archivo te ayuda a navegar todos los documentos disponibles.

---

## 🎯 ¿DÓNDE EMPEZAR?

### Si quieres empezar RÁPIDO
→ Lee: **[QUICK_START_FIXED.md](QUICK_START_FIXED.md)** (5 minutos)

### Si quieres TESTEAR los endpoints
→ Elige uno:
1. **[Postman_Collection.json](Postman_Collection.json)** - Importa directamente en Postman
2. **[POSTMAN_GUIDE.md](POSTMAN_GUIDE.md)** - Guía completa de testing
3. **[CURL_EXAMPLES.md](CURL_EXAMPLES.md)** - Testing desde terminal

### Si necesitas REFERENCIA RÁPIDA
→ Lee: **[QUICK_REFERENCE_ENDPOINTS.md](QUICK_REFERENCE_ENDPOINTS.md)** (durante testing)

---

## 📑 DOCUMENTACIÓN TÉCNICA

### Arquitectura y Diseño
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Arquitectura del sistema completo
- **[IMPLEMENTATION_CHECKLIST.md](IMPLEMENTATION_CHECKLIST.md)** - Checklist de implementación

### Configuración e Instalación
- **[SETUP.md](SETUP.md)** - Configuración inicial del proyecto
- **[QUICK_START_FIXED.md](QUICK_START_FIXED.md)** - Inicio rápido (recomendado)
- **[FRONTEND_QUICKSTART.md](FRONTEND_QUICKSTART.md)** - Inicio rápido del frontend

### Microservicios
- **[FRONTEND_MICROSERVICE_SETUP.md](FRONTEND_MICROSERVICE_SETUP.md)** - Configuración del frontend
- **[FRONTEND_GUIDE.md](FRONTEND_GUIDE.md)** - Guía del frontend

### Resolución de Problemas
- **[TROUBLESHOOTING.md](TROUBLESHOOTING.md)** - Solución de problemas comunes
- **[MAVEN_PERMISSIONS_FIX.md](MAVEN_PERMISSIONS_FIX.md)** - Solucionar errores de Maven
- **[README_FIX_EUREKA_401.md](README_FIX_EUREKA_401.md)** - Solucionar errores de Eureka

---

## 🧪 TESTING Y VALIDACIÓN

### Testing con Postman
- **[Postman_Collection.json](Postman_Collection.json)** ⭐ JSON listo para importar
- **[POSTMAN_GUIDE.md](POSTMAN_GUIDE.md)** - Guía completa con ejemplos
- **[POSTMAN_PROMPT_IA.md](POSTMAN_PROMPT_IA.md)** - Prompt para generar con IA

### Testing sin Postman
- **[CURL_EXAMPLES.md](CURL_EXAMPLES.md)** - Ejemplos con cURL y PowerShell

### Referencia de Endpoints
- **[QUICK_REFERENCE_ENDPOINTS.md](QUICK_REFERENCE_ENDPOINTS.md)** - Tabla rápida de endpoints
- **[CODE_EXAMPLES.md](CODE_EXAMPLES.md)** - Ejemplos de código

### Testing Manual
- **[POSTMAN_GUIA.md](POSTMAN_GUIA.md)** - Guía en español para Postman
- **[VERIFICATION_CHECKLIST.md](VERIFICATION_CHECKLIST.md)** - Checklist de verificación

---

## 🚀 DEPLOYMENT Y DEVOPS

### Docker
- **[docker-compose.yml](../docker-compose.yml)** - Configuración Docker completa

### Despliegue
- **[DEPLOYMENT.md](DEPLOYMENT.md)** - Guía de despliegue
- **[TECHNICAL_ANALYSIS.md](TECHNICAL_ANALYSIS.md)** - Análisis técnico

---

## 📊 ANÁLISIS Y REPORTE

### Eureka y Configuración
- **[EUREKA_FIX_SUMMARY.md](EUREKA_FIX_SUMMARY.md)** - Resumen del fix de Eureka
- **[CONFIGURATION.md](CONFIGURATION.md)** - Configuración del sistema

### Inventario y Estado
- **[INVENTORY.md](INVENTORY.md)** - Inventario de componentes
- **[RESUMEN_EJECUTIVO.md](RESUMEN_EJECUTIVO.md)** - Resumen ejecutivo (español)
- **[RESUMEN_VISUAL.md](RESUMEN_VISUAL.md)** - Resumen visual (español)

### Guías Específicas
- **[STEP_BY_STEP_START.md](STEP_BY_STEP_START.md)** - Paso a paso de inicio
- **[PASOS_EXACTOS.md](PASOS_EXACTOS.md)** - Pasos exactos (español)
- **[IMPLEMENTACION_COMPLETA.md](IMPLEMENTACION_COMPLETA.md)** - Implementación completa (español)

---

## 📖 TABLAS DE CONTENIDO

### Por Servicio
- **[FRONTEND_SUMMARY.md](FRONTEND_SUMMARY.md)** - Resumen del frontend
- **[FRONTEND_VERIFICATION.md](FRONTEND_VERIFICATION.md)** - Verificación del frontend
- **[INDEX_FRONTEND.md](INDEX_FRONTEND.md)** - Índice específico del frontend

---

## 🌐 ÍNDICES GENERALES

- **[INDEX.md](INDEX.md)** - Índice principal del proyecto
- **[FRONTEND_README.md](FRONTEND_README.md)** - README del frontend
- **[README_ESPAÑOL.md](README_ESPAÑOL.md)** - README en español
- **[README.md](README.md)** - README principal (en raíz)

---

## 🛠️ SCRIPTS DISPONIBLES

Todos en carpeta `scripts/`:

```
./scripts/
├── start-system-fixed.ps1      ← Inicia TODO el sistema (RECOMENDADO)
├── stop-system.ps1              ← Detiene TODO el sistema
├── fix-permissions.ps1          ← Repara permisos de Maven
├── run-microservices.ps1        ← Solo microservicios
├── run-eureka-gateway.ps1       ← Solo Eureka y Gateway
├── start-app.ps1                ← Inicia la app
├── start-services.sh            ← Para Linux/Mac
├── start-services.cmd           ← Para Windows CMD
└── README.md                    ← Guía de scripts
```

---

## 📋 CASOS DE USO FRECUENTES

### "Quiero empezar ahora"
1. Lee: [QUICK_START_FIXED.md](QUICK_START_FIXED.md)
2. Ejecuta: `.\scripts\start-system-fixed.ps1`
3. Abre: http://localhost:8080

### "Quiero testear endpoints"
1. Lee: [POSTMAN_GUIDE.md](POSTMAN_GUIDE.md)
2. Importa: [Postman_Collection.json](Postman_Collection.json)
3. Ejecuta en orden (ver guía)

### "Tengo error de Maven"
1. Lee: [MAVEN_PERMISSIONS_FIX.md](MAVEN_PERMISSIONS_FIX.md)
2. Ejecuta: `.\scripts\fix-permissions.ps1`
3. Reintenta: `.\mvnw clean install`

### "Quiero usar solo línea de comandos"
1. Lee: [CURL_EXAMPLES.md](CURL_EXAMPLES.md)
2. Copia comandos
3. Ejecuta en PowerShell/Bash

### "Eureka no funciona"
1. Lee: [README_FIX_EUREKA_401.md](README_FIX_EUREKA_401.md)
2. o Lee: [EUREKA_FIX_SUMMARY.md](EUREKA_FIX_SUMMARY.md)
3. Sigue los pasos

### "Quiero la referencia rápida"
1. Abre: [QUICK_REFERENCE_ENDPOINTS.md](QUICK_REFERENCE_ENDPOINTS.md)
2. Consulta tabla de endpoints
3. Usa durante testing

---

## 🔐 CREDENCIALES POR DEFECTO

```
Usuario: admin
Contraseña: admin123
```

Usado en:
- Eureka Dashboard: http://localhost:8761
- API Login: POST /auth/login
- Postman: Variables de entorno

---

## 🌐 PUERTOS DEL SISTEMA

```
Gateway:        http://localhost:8080
Auth Service:   http://localhost:8081
Sensor Service: http://localhost:8082
Alert Service:  http://localhost:8083
Access Service: http://localhost:8084
Notification:   http://localhost:8085
Eureka:         http://localhost:8761
```

---

## 📞 ESTRUCTURA DE MICROSERVICIOS

```
starkDistribuidos-gateway/       ← API Gateway
starkDistribuidos-auth/          ← Autenticación
starkDistribuidos-sensor/        ← Gestión de sensores
starkDistribuidos-alert/         ← Gestión de alertas
starkDistribuidos-access/        ← Control de acceso
starkDistribuidos-notification/  ← Notificaciones
starkDistribuidos-config/        ← Eureka Server
starkDistribuidos-frontend/      ← Frontend Web
```

---

## ✅ CHECKLIST RÁPIDO

- [ ] Leo la documentación relevante
- [ ] Ejecuto `.\mvnw clean install`
- [ ] Ejecuto `.\scripts\start-system-fixed.ps1`
- [ ] Espero 30-40 segundos
- [ ] Accedo a http://localhost:8080
- [ ] Importo Postman_Collection.json
- [ ] Ejecuto endpoints en orden
- [ ] Todos responden 200/201

---

## 🆘 HELP

¿Necesitas ayuda?

1. **Error de compilación**: → [MAVEN_PERMISSIONS_FIX.md](MAVEN_PERMISSIONS_FIX.md)
2. **Servicio no inicia**: → [TROUBLESHOOTING.md](TROUBLESHOOTING.md)
3. **Endpoints no funcionan**: → [POSTMAN_GUIDE.md](POSTMAN_GUIDE.md)
4. **Eureka no funciona**: → [README_FIX_EUREKA_401.md](README_FIX_EUREKA_401.md)
5. **Testing**: → [QUICK_REFERENCE_ENDPOINTS.md](QUICK_REFERENCE_ENDPOINTS.md)

---

## 📊 ESTADÍSTICAS

```
Total de documentos:    30+
Endpoints documentados: 36+
Scripts disponibles:    8
Guías de testing:       5
Archivos de referencia: 8
Idiomas:                Español + Inglés
```

---

## 🎓 APRENDE MÁS

- Spring Boot: https://spring.io/projects/spring-boot
- Spring Cloud: https://spring.io/projects/spring-cloud
- Eureka: https://spring.io/projects/spring-cloud-netflix
- JWT: https://jwt.io/
- Postman: https://learning.postman.com/

---

## 📝 ÚLTIMA ACTUALIZACIÓN

- **Fecha**: Abril 2026
- **Versión**: 1.0.0
- **Responsable**: Equipo de Sistemas Distribuidos

---

**¡Bienvenido al sistema Stark Industries!** 🚀

Para empezar: Lee [QUICK_START_FIXED.md](QUICK_START_FIXED.md) o [POSTMAN_GUIDE.md](POSTMAN_GUIDE.md)

