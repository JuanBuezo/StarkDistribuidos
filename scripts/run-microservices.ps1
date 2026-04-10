# Script para iniciar los 5 microservicios restantes
$root = "C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos"

$services = @(
    @{Name="Auth Service"; Path="starkDistribuidos-auth"; Port=8081},
    @{Name="Sensor Service"; Path="starkDistribuidos-sensor"; Port=8082},
    @{Name="Alert Service"; Path="starkDistribuidos-alert"; Port=8083},
    @{Name="Access Service"; Path="starkDistribuidos-access"; Port=8084},
    @{Name="Notification Service"; Path="starkDistribuidos-notification"; Port=8085}
)

Write-Host "Iniciando 5 microservicios..." -ForegroundColor Green
Write-Host "Cada uno se abrira en una terminal separada" -ForegroundColor Yellow

foreach ($service in $services) {
    Write-Host "  Iniciando $($service.Name) en puerto $($service.Port)..." -ForegroundColor Cyan
    $servicePath = "$root\$($service.Path)"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$servicePath'; ..\mvnw.cmd spring-boot:run; Read-Host 'Presiona Enter para cerrar'"
    Start-Sleep -Seconds 2
}

Write-Host "`nTodos los servicios estan iniciandose en terminales separadas" -ForegroundColor Green
Write-Host "Esperando 60 segundos para que se registren en Eureka..." -ForegroundColor Yellow

$countdown = 60
while ($countdown -gt 0) {
    Write-Host -NoNewline "`r[$countdown segundos restantes]     "
    Start-Sleep -Seconds 1
    $countdown--
}

Write-Host "`n`nAbriendo Eureka para verificar registros..." -ForegroundColor Cyan
Start-Process "http://localhost:8761"

Write-Host "`nServicios iniciados en puertos:" -ForegroundColor Green
Write-Host "  - Auth:       http://localhost:8081" -ForegroundColor Cyan
Write-Host "  - Sensor:     http://localhost:8082" -ForegroundColor Cyan
Write-Host "  - Alert:      http://localhost:8083" -ForegroundColor Cyan
Write-Host "  - Access:     http://localhost:8084" -ForegroundColor Cyan
Write-Host "  - Notification: http://localhost:8085" -ForegroundColor Cyan
Write-Host "`nEureka deberia mostrar todos los servicios registrados" -ForegroundColor Yellow

