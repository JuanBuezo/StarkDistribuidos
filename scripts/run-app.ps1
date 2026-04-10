# Script para ejecutar la aplicacion Stark Distribuidos
Write-Host "Deteniendo procesos anteriores..." -ForegroundColor Yellow
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

$root = "C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos"

Write-Host "Iniciando Eureka Server..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\starkDistribuidos-config'; ..\mvnw.cmd spring-boot:run"

Start-Sleep -Seconds 5

Write-Host "Iniciando API Gateway..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\starkDistribuidos-gateway'; ..\mvnw.cmd spring-boot:run"

Write-Host "Esperando 40 segundos..." -ForegroundColor Yellow
Start-Sleep -Seconds 40

Write-Host "Abriendo navegadores..." -ForegroundColor Cyan
Start-Process "http://localhost:8761/eureka"
Start-Sleep -Seconds 2
Start-Process "http://localhost:8080/stark"

Write-Host "COMPLETADO!" -ForegroundColor Green



