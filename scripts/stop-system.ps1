# Script para detener todo el sistema Stark Distribuido
Write-Host "===============================================================" -ForegroundColor Cyan
Write-Host "  DETENIENDO SISTEMA STARK DISTRIBUIDO" -ForegroundColor Cyan
Write-Host "===============================================================" -ForegroundColor Cyan
Write-Host ""
# Detener procesos Java
Write-Host "1. Deteniendo procesos Java..." -ForegroundColor Green
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 1
Write-Host "2. Deteniendo ventanas PowerShell..." -ForegroundColor Green
Get-Process powershell -ErrorAction SilentlyContinue | Where-Object { $_.Id -ne $PID } | Stop-Process -Force -ErrorAction SilentlyContinue
Write-Host ""
Write-Host "Esperando a que se cierren los procesos..." -ForegroundColor Yellow
Start-Sleep -Seconds 3
# Verificar
$javaProcesses = Get-Process java -ErrorAction SilentlyContinue
if ($javaProcesses) {
    Write-Host "Procesos Java aun activos:" -ForegroundColor Red
    $javaProcesses | Format-Table -Property Name, Id, Handles
} else {
    Write-Host ""
    Write-Host "===============================================================" -ForegroundColor Green
    Write-Host "   SISTEMA DETENIDO CORRECTAMENTE" -ForegroundColor Green
    Write-Host "===============================================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Todos los servicios detenidos" -ForegroundColor Green
    Write-Host ""
}
