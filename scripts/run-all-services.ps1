# Script para iniciar todos los microservicios de Stark Distribuidos
# Este script abre 7 terminales, una para cada microservicio

$root = "C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos"

Write-Host "Deteniendo procesos anteriores..." -ForegroundColor Yellow
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

$services = @(
    @{Name="Eureka Server"; Path="starkDistribuidos-config"; Port=8761},
    @{Name="Auth Service"; Path="starkDistribuidos-auth"; Port=8081},
    @{Name="Sensor Service"; Path="starkDistribuidos-sensor"; Port=8082},
    @{Name="Alert Service"; Path="starkDistribuidos-alert"; Port=8083},
    @{Name="Access Service"; Path="starkDistribuidos-access"; Port=8084},
    @{Name="Notification Service"; Path="starkDistribuidos-notification"; Port=8085},
    @{Name="API Gateway"; Path="starkDistribuidos-gateway"; Port=8080}
)

Write-Host "Iniciando servicios..." -ForegroundColor Green

foreach ($service in $services) {
    Write-Host "  [*] Iniciando $($service.Name) en puerto $($service.Port)..." -ForegroundColor Cyan
    $servicePath = "$root\$($service.Path)"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$servicePath'; ..\mvnw.cmd spring-boot:run; Read-Host 'Presiona Enter para cerrar'"
    Start-Sleep -Seconds 2
}

Write-Host "`nTodos los servicios estan iniciandose..." -ForegroundColor Green
Write-Host "Esperando 60 segundos..." -ForegroundColor Yellow

for ($i = 60; $i -gt 0; $i--) {
    Write-Host -NoNewline "`r[$i segundos restantes] "
    Start-Sleep -Seconds 1
}

Write-Host "`n`nAbriendo navegadores..." -ForegroundColor Cyan
Start-Process "http://localhost:8761"
Start-Sleep -Seconds 2
Start-Process "http://localhost:8080/stark"

Write-Host "`nServicios disponibles en:" -ForegroundColor Green
Write-Host "  - Eureka:     http://localhost:8761" -ForegroundColor Cyan
Write-Host "  - Gateway:    http://localhost:8080/stark" -ForegroundColor Cyan
Write-Host "  - Auth:       http://localhost:8081" -ForegroundColor Cyan
Write-Host "  - Sensor:     http://localhost:8082" -ForegroundColor Cyan
Write-Host "  - Alert:      http://localhost:8083" -ForegroundColor Cyan
Write-Host "  - Access:     http://localhost:8084" -ForegroundColor Cyan
Write-Host "  - Notification: http://localhost:8085" -ForegroundColor Cyan
Write-Host "`nCredenciales Eureka: admin / admin123" -ForegroundColor Yellow

