# Script para limpiar permisos y preparar Maven
# Ejecuta esto cuando Maven tenga problemas para eliminar archivos

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "   LIMPIEZA Y REPARACIÓN DE PERMISOS" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

# PASO 1: Detener procesos Java
Write-Host "1️⃣  Deteniendo procesos Java..." -ForegroundColor Yellow
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

# PASO 2: Cambiar permisos y limpiar carpetas target
Write-Host "2️⃣  Cambiando permisos y limpiando carpetas..." -ForegroundColor Yellow
$folders = Get-ChildItem -Path "." -Directory -Filter "stark*" -ErrorAction SilentlyContinue | ForEach-Object { $_.FullName }

if ($folders) {
    foreach ($folder in $folders) {
        $targetPath = "$folder\target"
        if (Test-Path $targetPath) {
            Write-Host "   • Procesando: $(Split-Path $folder -Leaf)" -ForegroundColor Cyan

            # Cambiar permisos recursivamente
            icacls "$targetPath" /grant:r "$($env:USERNAME):(F)" /t /c /q 2>$null

            # Eliminar la carpeta
            Remove-Item -Path "$targetPath" -Recurse -Force -ErrorAction SilentlyContinue
        }
    }
    Write-Host "   ✓ Carpetas limpias" -ForegroundColor Green
} else {
    Write-Host "   ℹ️  No hay carpetas target para limpiar" -ForegroundColor Gray
}

# PASO 3: Dar permisos a archivo maven-status
Write-Host ""
Write-Host "3️⃣  Otorgando permisos al usuario..." -ForegroundColor Yellow
icacls "." /grant:r "$($env:USERNAME):(F)" /t /c /q 2>$null
Write-Host "   ✓ Permisos otorgados" -ForegroundColor Green

Write-Host ""
Write-Host "============================================================" -ForegroundColor Green
Write-Host "   ✅ LIMPIEZA COMPLETADA" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Green
Write-Host ""
Write-Host "Ahora puedes ejecutar:" -ForegroundColor Yellow
Write-Host "  .\mvnw clean install" -ForegroundColor Cyan
Write-Host ""

