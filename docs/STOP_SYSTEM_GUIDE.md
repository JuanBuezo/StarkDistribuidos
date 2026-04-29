# 🛑 Guía: Cómo Detener el Sistema Stark Distribuido

## ⚡ Forma Rápida (Recomendada)

Desde la carpeta raíz del proyecto, ejecuta:

```powershell
.\scripts\stop-system.ps1
```

Esto detendrá automáticamente todos los servicios:
- Eureka Server (8761)
- API Gateway (8080)
- Auth Service (8081)
- Sensor Service (8082)
- Alert Service (8083)
- Access Service (8084)
- Notification Service (8085)

---

## 🔧 Alternativas

### 1️⃣ Usando PowerShell Directamente

```powershell
# Detener todos los procesos Java
Get-Process java | Stop-Process -Force
```

### 2️⃣ Usando Windows CMD

```cmd
taskkill /IM java.exe /F
```

### 3️⃣ Forma Manual

1. Localiza las ventanas de PowerShell/CMD abiertas para cada servicio
2. En cada ventana, presiona: `Ctrl + C`
3. Cierra las ventanas

---

## ✅ Cómo Verificar que Todo se Detuvo

```powershell
# Verificar que no hay procesos Java corriendo
Get-Process java -ErrorAction SilentlyContinue
# No debería devolver nada si está todo detenido
```

---

## 🚀 Para Volver a Iniciar

```powershell
.\scripts\start-system-fixed.ps1
```

---

**Nota:** El script `stop-system.ps1` es la forma más segura y automatizada de detener el sistema completo.

