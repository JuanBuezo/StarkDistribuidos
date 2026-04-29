# Script para lanzar TODOS los servicios del Stark Security System
$projectPath = Split-Path -Parent $MyInvocation.MyCommandPath
$jarsPath = $projectPath

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  STARK SECURITY SYSTEM - LANZANDO TODO" -ForegroundColor Yellow
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Función para lanzar un servicio en ventana separada
function Start-Service {
    param([string]$Name, [string]$JarFile, [int]$DelaySeconds = 3)

    Write-Host "▸ Iniciando $Name..." -ForegroundColor Green
    $cmd = "cd `"$jarsPath`" && java -jar `"$JarFile`" 2>&1"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $cmd -WindowStyle Normal
    Start-Sleep -Seconds $DelaySeconds
}

# Lanzar servicios en orden
Start-Service -Name "Eureka Discovery" -JarFile "starkDistribuidos-config/target/starkDistribuidos-config-0.0.1-SNAPSHOT.jar" -DelaySeconds 4
Start-Service -Name "Auth Service" -JarFile "starkDistribuidos-auth/target/starkDistribuidos-auth-0.0.1-SNAPSHOT.jar" -DelaySeconds 3
Start-Service -Name "Sensor Service" -JarFile "starkDistribuidos-sensor/target/starkDistribuidos-sensor-0.0.1-SNAPSHOT.jar" -DelaySeconds 3
Start-Service -Name "Alert Service" -JarFile "starkDistribuidos-alert/target/starkDistribuidos-alert-0.0.1-SNAPSHOT.jar" -DelaySeconds 3
Start-Service -Name "Notification Service" -JarFile "starkDistribuidos-notification/target/starkDistribuidos-notification-0.0.1-SNAPSHOT.jar" -DelaySeconds 3
Start-Service -Name "Access Service" -JarFile "starkDistribuidos-access/target/starkDistribuidos-access-0.0.1-SNAPSHOT.jar" -DelaySeconds 3
Start-Service -Name "Gateway Service" -JarFile "starkDistribuidos-gateway/target/starkDistribuidos-gateway-0.0.1-SNAPSHOT.jar" -DelaySeconds 5
Start-Service -Name "Frontend Service" -JarFile "starkDistribuidos-frontend/target/starkDistribuidos-frontend-0.0.1-SNAPSHOT.jar" -DelaySeconds 3

Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "  ✓ Todos los servicios iniciados" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "Frontend disponible en:   http://localhost:8080/" -ForegroundColor Yellow
Write-Host "Eureka Dashboard en:      http://localhost:8761/" -ForegroundColor Yellow
Write-Host ""
Write-Host "Credenciales de Demo:" -ForegroundColor Cyan
Write-Host "  Usuario: admin" -ForegroundColor White
Write-Host "  Contrasena: admin123" -ForegroundColor White
Write-Host ""

