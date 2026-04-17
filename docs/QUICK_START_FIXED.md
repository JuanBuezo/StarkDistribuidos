# 🚀 INICIO RÁPIDO - Sistema Stark Distribuido

## ✅ Lo que se arregló:

El error 401 que veías era porque Eureka Server requiere autenticación y los servicios no la proveían. **Ahora está solucionado.**

## 🎯 Para Iniciar TODO (Recomendado):

**Abre PowerShell y ejecuta:**

```powershell
cd "C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos"
.\start-system-fixed.ps1
```

El script hará todo automáticamente en el orden correcto. ✅

## 📖 Si prefieres hacerlo manualmente:

### Terminal 1 - EUREKA (Primero!)
```powershell
cd C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos\starkDistribuidos-config
..\mvnw.cmd spring-boot:run
```
**Espera 10 segundos y verifica que funcione en:** http://localhost:8761

### Terminal 2 - GATEWAY
```powershell
cd C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos\starkDistribuidos-gateway
..\mvnw.cmd spring-boot:run
```

### Terminal 3 - ACCESS SERVICE
```powershell
cd C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos\starkDistribuidos-access
..\mvnw.cmd spring-boot:run
```

### Terminal 4+ - Otros servicios (opcional)
```powershell
# SENSOR
cd C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos\starkDistribuidos-sensor
..\mvnw.cmd spring-boot:run
```

```powershell
# ALERT
cd C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos\starkDistribuidos-alert
..\mvnw.cmd spring-boot:run
```

## ✅ Verificación Final

Una vez que todo esté corriendo, abre:
```
http://localhost:8761
```

Deberías ver que todos los servicios están **UP** (verde) registrados en Eureka. ✅

## 🔑 Credenciales (si las necesitas)
- Usuario: `admin`
- Contraseña: `admin123`

---

**Eso es todo. Ahora debería funcionar sin errores 401.** 🎉

