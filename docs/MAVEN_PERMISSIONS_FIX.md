# 🔧 Solución: Error de Permisos en Maven

Si al ejecutar `.\mvnw clean install` ves un error como:

```
Failed to delete C:\...\target\maven-status\maven-compiler-plugin\compile\default-compile
```

## ⚡ Solución Rápida

Ejecuta este script para limpiar permisos automáticamente:

```powershell
.\scripts\fix-permissions.ps1
```

Luego:

```powershell
.\mvnw clean install
```

---

## 🔍 ¿Qué Causa Este Error?

En Windows, los archivos pueden quedarse bloqueados por:

1. **Procesos Java activos**: Ventanas de PowerShell ejecutando servicios
2. **Antivirus/Windows Defender**: Escaneando archivos
3. **IDE abierto**: JetBrains IDE u otro editor
4. **Permisos insuficientes**: Archivos con permisos restringidos

---

## 🛠️ Soluciones Detalladas

### Opción 1: Script Automático (RECOMENDADO)
```powershell
.\scripts\fix-permissions.ps1
.\mvnw clean install
```

### Opción 2: Manual (Método 1 - Detener Java)
```powershell
# Detener todos los procesos Java
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force

# Esperar 2 segundos
Start-Sleep -Seconds 2

# Compilar
.\mvnw clean install
```

### Opción 3: Manual (Método 2 - Cambiar Permisos)
```powershell
# Cambiar permisos recursivamente para tu usuario
$env:USERNAME
icacls "." /grant:r "$($env:USERNAME):(F)" /t /c /q

# Compilar
.\mvnw clean install
```

### Opción 4: Manual (Método 3 - Limpiar Todo)
```powershell
# 1. Detener Java
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue

# 2. Eliminar carpetas target
Get-ChildItem -Path "." -Directory -Filter "stark*" | ForEach-Object {
    Remove-Item -Path "$($_.FullName)\target" -Recurse -Force -ErrorAction SilentlyContinue
}

# 3. Compilar
.\mvnw clean install
```

---

## 🚨 Si Aún Tienes Problemas

### Cierra estas aplicaciones:
- ✓ JetBrains IDE (IntelliJ, WebStorm, etc.)
- ✓ Antivirus/Windows Defender (temporalmente)
- ✓ OneDrive (si sincroniza la carpeta)
- ✓ Git (si está abierto)

### Luego:
```powershell
# Ejecuta como Administrador
.\scripts\fix-permissions.ps1
.\mvnw clean install
```

---

## ✅ Verificación

Para confirmar que todo está bien:

```powershell
# Verificar que no hay procesos Java
Get-Process java -ErrorAction SilentlyContinue
# (No debería devolver nada)

# Compilar
.\mvnw clean install
# Debería mostrar BUILD SUCCESS
```

---

**Nota**: El script `fix-permissions.ps1` hace exactamente esto automáticamente. Úsalo cada vez que tengas problemas. 🎯

