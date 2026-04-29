@echo off
REM Script para iniciar Stark Security System con nuevo diseño Grafana

cls
echo ============================================
echo  STARK SECURITY SYSTEM - GRAFANA EDITION
echo ============================================
echo.
echo Iniciando servicios...
echo.

REM Crear ventanas separadas para cada servicio
start "Eureka - Discovery Service" cmd /k "cd /d %~dp0 && java -jar starkDistribuidos-config/target/*.jar"
timeout /t 3 /nobreak

start "Auth Service" cmd /k "cd /d %~dp0 && java -jar starkDistribuidos-auth/target/*.jar"
timeout /t 3 /nobreak

start "Sensor Service" cmd /k "cd /d %~dp0 && java -jar starkDistribuidos-sensor/target/*.jar"
timeout /t 3 /nobreak

start "Alert Service" cmd /k "cd /d %~dp0 && java -jar starkDistribuidos-alert/target/*.jar"
timeout /t 3 /nobreak

start "Notification Service" cmd /k "cd /d %~dp0 && java -jar starkDistribuidos-notification/target/*.jar"
timeout /t 3 /nobreak

start "Access Control Service" cmd /k "cd /d %~dp0 && java -jar starkDistribuidos-access/target/*.jar"
timeout /t 3 /nobreak

start "Gateway Service" cmd /k "cd /d %~dp0 && java -jar starkDistribuidos-gateway/target/*.jar"
timeout /t 5 /nobreak

start "Frontend - MAIN" cmd /k "cd /d %~dp0 && java -jar starkDistribuidos-frontend/target/*.jar"

echo.
echo ============================================
echo  Servicios iniciados correctamente
echo ============================================
echo.
echo Frontend disponible en: http://localhost:8080/
echo.
echo Credenciales de Demo:
echo   Usuario: admin
echo   Contraseña: admin123
echo.
echo Presiona cualquier tecla para continuar...
pause
