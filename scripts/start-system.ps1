# Script para iniciar servicios EN EL ORDEN CORRECTO
# PRIMERO: Eureka (el registro central)
# DESPUES: Gateway
# FINALMENTE: Los 5 microservicios

$root = "C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos"

# Detener todo primero
Write-Host "Deteniendo procesos anteriores..." -ForegroundColor Yellow
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 3

# PASO 1: Iniciar SOLO Eureka
Write-Host "`n=== PASO 1: INICIANDO EUREKA SERVER ===" -ForegroundColor Green
Write-Host "Esperando a que Eureka se inicie completamente..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\starkDistribuidos-config'; ..\mvnw.cmd spring-boot:run"
Start-Sleep -Seconds 45

# PASO 2: Iniciar Gateway
Write-Host "`n=== PASO 2: INICIANDO API GATEWAY ===" -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\starkDistribuidos-gateway'; ..\mvnw.cmd spring-boot:run"
Start-Sleep -Seconds 30

# PASO 3: Iniciar los 5 microservicios
Write-Host "`n=== PASO 3: INICIANDO MICROSERVICIOS ===" -ForegroundColor Green

$services = @(
    @{Name="Auth Service"; Path="starkDistribuidos-auth"; Port=8081},
    @{Name="Sensor Service"; Path="starkDistribuidos-sensor"; Port=8082},
    @{Name="Alert Service"; Path="starkDistribuidos-alert"; Port=8083},
    @{Name="Access Service"; Path="starkDistribuidos-access"; Port=8084},
    @{Name="Notification Service"; Path="starkDistribuidos-notification"; Port=8085}
)

foreach ($service in $services) {
    Write-Host "  Iniciando $($service.Name) en puerto $($service.Port)..." -ForegroundColor Cyan
    $servicePath = "$root\$($service.Path)"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$servicePath'; ..\mvnw.cmd spring-boot:run"
    Start-Sleep -Seconds 3
}

Write-Host "`n=== ESPERANDO A QUE TODO INICIE ===" -ForegroundColor Yellow
Write-Host "Esto puede tardar 2-3 minutos..." -ForegroundColor Yellow
Start-Sleep -Seconds 90

Write-Host "`nAbriendo Eureka..." -ForegroundColor Cyan
Start-Process "http://localhost:8761"
Start-Sleep -Seconds 2

Write-Host "Abriendo Gateway..." -ForegroundColor Cyan
Start-Process "http://localhost:8080/stark"

Write-Host "`n=== SISTEMA INICIADO ===" -ForegroundColor Green
Write-Host "Todos los servicios estan inicializandose." -ForegroundColor Green
Write-Host "Eureka deberia mostrar los servicios registrados en http://localhost:8761" -ForegroundColor Cyan

