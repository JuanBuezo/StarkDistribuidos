# 🏢 Stark Industries - Sistema de Seguridad Distribuido

Sistema completo de microservicios distribuidos con Spring Cloud.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.1-blue)
![Docker](https://img.shields.io/badge/Docker-Supported-cyan)

---

## 🚀 Inicio Rápido

1. **Compilar**: `.\mvnw clean install`
2. **Iniciar**: `.\scripts\start-system-fixed.ps1`
3. **Acceder**: 
   - Frontend: http://localhost:8080
   - Eureka: http://localhost:8761 (admin/admin123)
4. **Parar**: `.\scripts\stop-system.ps1`

## 📁 Estructura

```
StarkDistribuidos/
├── docs/                   # 📚 Documentación completa
├── scripts/                # 🛠️  Scripts de inicio
├── starkDistribuidos-*/    # 🔧 Microservicios (7 servicios)
├── docker-compose.yml      # 🐳 Configuración Docker
└── pom.xml                # 📦 POM principal
```

## 🔧 Microservicios

- **Gateway**: Maneja las solicitudes externas y las dirige a los microservicios apropiados.
- **Auth**: Autenticación y autorización de usuarios, emitiendo JWTs.
- **Sensor**: Recopila datos de los sensores.
- **Alert**: Genera y gestiona alertas basadas en datos de sensores.
- **Access**: Controla el acceso a las diferentes partes del sistema.
- **Notification**: Envía notificaciones a los usuarios.
- **Eureka**: Registro y descubrimiento de servicios.

## 🔧 Puertos de Servicios

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| Gateway | 8080 | API Central |
| Auth | 8081 | JWT + Seguridad |
| Sensor | 8082 | Sensores |
| Alert | 8083 | Alertas |
| Access | 8084 | Control de Acceso |
| Notification | 8085 | Notificaciones |
| Eureka | 8761 | Service Registry |

## 🔐 Credenciales Predeterminadas

- **Usuario**: admin
- **Contraseña**: admin123

## 📚 Documentación

Ver carpeta `docs/` para documentación completa:
- `QUICKSTART.md` - Guía rápida
- `ARCHITECTURE.md` - Arquitectura del sistema
- `DEPLOYMENT.md` - Despliegue
---

## 🛠️ Scripts

Todos disponibles en `scripts/`:
- `start-system-fixed.ps1` - Inicia todo
- `stop-system.ps1` - Detiene todo
- `fix-permissions.ps1` - Repara permisos (si Maven falla)
- `run-microservices.ps1` - Microservicios
- `start-services.sh` - Linux/Mac

## ⚠️ Solución de Problemas

### Error de permisos en Maven
Si ves error `Failed to delete...` al hacer `mvnw clean install`:
```powershell
.\scripts\fix-permissions.ps1
.\mvnw clean install
```
Ver: `docs/MAVEN_PERMISSIONS_FIX.md` para más detalles.

## 📮 Testing con Postman

### Opción 1: Importar colección lista
```powershell
# Importa docs/Postman_Collection.json en Postman
# Incluye todos los 36+ endpoints predefinidos
```

### Opción 2: Generar con IA
```
# Usa docs/POSTMAN_PROMPT_IA.md
# Copia el prompt a ChatGPT/Claude
# Genera colección personalizada
```

Ver: `docs/POSTMAN_GUIDE.md` para guía completa de testing.

---

**Última actualización**: Abril 2026 | **Versión**: 1.0.0
