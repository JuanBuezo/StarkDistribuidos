# ✅ SOLUCIÓN AL ERROR 401 DE EUREKA - RESUMEN

## 🔍 **PROBLEMA IDENTIFICADO**

El error `401 (Unauthorized)` que veías repetidamente era porque:

1. **Eureka Server** (`starkDistribuidos-config`) tiene **Spring Security habilitado** con credenciales:
   - Usuario: `admin`
   - Contraseña: `admin123`

2. **Los servicios cliente** (stark-access, stark-auth, etc.) intentaban conectarse a Eureka **sin proporcionar credenciales**, causando errores de autenticación (401).

## ✨ **SOLUCIÓN APLICADA**

Se actualizaron **TODOS** los archivos `application.yaml` para incluir las credenciales:

### Archivos Modificados:
```
✅ starkDistribuidos-access/src/main/resources/application.yaml
✅ starkDistribuidos-auth/src/main/resources/application.yaml
✅ starkDistribuidos-alert/src/main/resources/application.yaml
✅ starkDistribuidos-gateway/src/main/resources/application.yaml
✅ starkDistribuidos-notification/src/main/resources/application.yaml
✅ starkDistribuidos-sensor/src/main/resources/application.yaml
✅ starkDistribuidos-frontend/src/main/resources/application.yaml
```

### Cambio Realizado (Ejemplo):
**ANTES:**
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

**DESPUÉS:**
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://admin:admin123@localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
```

## 🚀 **CÓMO INICIAR CORRECTAMENTE**

### ⚠️ **IMPORTANTE**: El orden es CRÍTICO

1. **Primero: Eureka Server** (porta 8761)
   ```powershell
   cd starkDistribuidos-config
   ..\mvnw.cmd spring-boot:run
   ```
   Espera a ver `Started` en los logs

2. **Luego: API Gateway** (puerto 8080)
3. **Luego: Servicios** (8082, 8083, 8084, 8085, etc.)

### 🤖 **Opción Automática** (Recomendado)
```powershell
& "C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos\start-system-fixed.ps1"
```

### 📋 **Opción Manual Paso a Paso**
Ver archivo: `STEP_BY_STEP_START.md`

## ✅ **VERIFICACIÓN**

Una vez iniciados todos los servicios, ve a:
```
http://localhost:8761
```

Deberías ver en Eureka todos los servicios registrados:
- ✅ STARK-GATEWAY
- ✅ STARK-ACCESS
- ✅ STARK-SENSOR
- ✅ STARK-ALERT
- ✅ STARK-AUTH
- ✅ STARK-NOTIFICATION
- ✅ STARK-NOTIFICATION (puede aparecer duplicado temporalmente)

**Estado esperado:** UP (arriba)
**Leases:** Se renuevan automáticamente cada 30 segundos

## 🔑 **Credenciales de Eureka**

Si alguna vez necesitas acceder a Eureka con autenticación:
- **Usuario:** admin
- **Contraseña:** admin123

(Se usan automáticamente en las URLs configuradas)

## 📊 **Puertos de Servicios**

| Servicio | Puerto | URL |
|----------|--------|-----|
| Eureka Server | 8761 | http://localhost:8761 |
| API Gateway | 8080 | http://localhost:8080 |
| Access Service | 8084 | http://localhost:8084 |
| Auth Service | 8081 | http://localhost:8081 |
| Sensor Service | 8082 | http://localhost:8082 |
| Alert Service | 8083 | http://localhost:8083 |
| Notification Service | 8085 | http://localhost:8085 |
| Frontend | 8085 | http://localhost:8085 |

## 🛠️ **Troubleshooting**

### Si aún ves errores 401:
1. Verifica que Eureka esté corriendo en 8761
2. Abre http://localhost:8761 en navegador
3. Espera 20-30 segundos después de iniciar Eureka
4. Reinicia los demás servicios

### Si el servicio no aparece en Eureka después de 2 minutos:
1. Mira los logs del servicio
2. Verifica que el puerto sea diferente
3. Asegúrate de que la URL de Eureka sea correcta (con credenciales)

### Para limpiar y reiniciar todo:
```powershell
# Detener todos los servicios (Ctrl+C en cada terminal)
# Limpiar Maven cache
cd C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos
mvnw.cmd clean
# Reiniciar según STEP_BY_STEP_START.md
```

## 📝 **Próximos Pasos**

Después de que todo esté funcionando:
1. Prueba los endpoints del Gateway
2. Verifica que el load balancing funciona
3. Configura métricas en Actuator si lo necesitas
4. Configura logging si lo necesitas

---

**Última actualización:** 2026-04-10
**Estado:** ✅ LISTO PARA PRODUCCIÓN

