# Script simple para iniciar Eureka y Gateway
$root = "C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos"

Write-Host "Iniciando Eureka Server..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\starkDistribuidos-config'; ..\mvnw.cmd spring-boot:run"

Start-Sleep -Seconds 5

Write-Host "Iniciando API Gateway..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\starkDistribuidos-gateway'; ..\mvnw.cmd spring-boot:run"

Write-Host "Esperando 30 segundos a que inicien..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

Write-Host "Abriendo Eureka..." -ForegroundColor Cyan
Start-Process "http://localhost:8761"

Start-Sleep -Seconds 2

Write-Host "Abriendo Gateway..." -ForegroundColor Cyan
Start-Process "http://localhost:8080/stark"

Write-Host "LISTO!" -ForegroundColor Green

