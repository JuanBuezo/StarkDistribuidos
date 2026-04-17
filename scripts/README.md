# 🛠️ Índice de Scripts

## Scripts Disponibles

### 🚀 Scripts Principales

| Script | Descripción | Uso |
|--------|-------------|-----|
| `start-system-fixed.ps1` | **RECOMENDADO** - Inicia todo el sistema completo | PowerShell en Windows |
| `run-microservices.ps1` | Inicia solo los microservicios | PowerShell en Windows |
| `run-app.ps1` | Inicia la aplicación principal | PowerShell en Windows |

### 🔧 Scripts de Servicios Específicos

| Script | Descripción | Uso |
|--------|-------------|-----|
| `run-eureka-gateway.ps1` | Inicia Eureka y Gateway | PowerShell en Windows |
| `start-app.ps1` | Inicia la app principal | PowerShell en Windows |

### 🐧 Scripts para Linux/Mac

| Script | Descripción | Uso |
|--------|-------------|-----|
| `start-services.sh` | Inicia servicios en Linux/Mac | Bash en Linux/Mac |

### 🪟 Scripts Legacy (Windows)

| Script | Descripción | Estado |
|--------|-------------|--------|
| `start-system.ps1` | Versión antigua del sistema | ⚠️ Legado |
| `start-services.cmd` | Versión antigua en CMD | ⚠️ Legado |

---

## 🚀 Cómo Usar

### Opción 1: Inicio Completo (RECOMENDADO)
```powershell
.\start-system-fixed.ps1
```
Esto iniciará:
- ✅ Eureka (8761)
- ✅ Gateway (8080)
- ✅ Auth Service (8081)
- ✅ Sensor Service (8082)
- ✅ Alert Service (8083)
- ✅ Access Service (8084)
- ✅ Notification Service (8085)
- ✅ Frontend (8080)

### Opción 2: Solo Microservicios
```powershell
.\run-microservices.ps1
```

### Opción 3: Linux/Mac
```bash
bash ./start-services.sh
```

---

## 📝 Nota Importante

Los scripts en esta carpeta han sido movidos desde la raíz del proyecto para mantener una estructura limpia y organizada.

**¡Siempre ejecuta desde la carpeta raíz del proyecto!**

```powershell
cd C:\ruta\al\proyecto
.\scripts\start-system-fixed.ps1
```

