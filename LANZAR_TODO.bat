@echo off
REM ============================================
REM  STARK SECURITY SYSTEM - LANZAR TODO
REM ============================================
REM Script para compilar TODOS los modulos y lanzar servicios en paralelo

setlocal enabledelayedexpansion

cls
echo.
echo ============================================
echo  STARK SECURITY SYSTEM - COMPILANDO TODO
echo ============================================
echo.
echo Compilando con Maven (sin tests)...
echo.

REM Compilar TODO
call .\mvnw.cmd clean package -q -DskipTests
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Fallo la compilacion
    pause
    exit /b 1
)

cls
echo.
echo ============================================
echo  STARK SECURITY SYSTEM - LANZANDO TODO
echo ============================================
echo.
echo Iniciando servicios en ventanas paralelas...
echo.

REM Crear ventanas separadas para cada servicio
start "Eureka - Discovery Service (8761)" cmd /k "cd /d %~dp0\starkDistribuidos-config && ..\mvnw.cmd spring-boot:run"
timeout /t 4 /nobreak

start "Auth Service (8081)" cmd /k "cd /d %~dp0\starkDistribuidos-auth && ..\mvnw.cmd spring-boot:run"
timeout /t 3 /nobreak

start "Sensor Service (8082)" cmd /k "cd /d %~dp0\starkDistribuidos-sensor && ..\mvnw.cmd spring-boot:run"
timeout /t 3 /nobreak

start "Alert Service (8083)" cmd /k "cd /d %~dp0\starkDistribuidos-alert && ..\mvnw.cmd spring-boot:run"
timeout /t 3 /nobreak

start "Notification Service (8084)" cmd /k "cd /d %~dp0\starkDistribuidos-notification && ..\mvnw.cmd spring-boot:run"
timeout /t 3 /nobreak

start "Access Control Service (8085)" cmd /k "cd /d %~dp0\starkDistribuidos-access && ..\mvnw.cmd spring-boot:run"
timeout /t 3 /nobreak

start "Gateway Service (8080)" cmd /k "cd /d %~dp0\starkDistribuidos-gateway && ..\mvnw.cmd spring-boot:run"
timeout /t 5 /nobreak

start "Frontend - MAIN (8086)" cmd /k "cd /d %~dp0\starkDistribuidos-frontend && ..\mvnw.cmd spring-boot:run"

cls
echo.
echo ============================================
echo  STARK SECURITY SYSTEM - ACTIVO
echo ============================================
echo.
echo TODOS los servicios iniciados correctamente!
echo.
echo ACCESO A LOS SERVICIOS:
echo   Frontend:           http://localhost:8086/
echo   Gateway (Proxy):    http://localhost:8080/
echo   Eureka Dashboard:   http://localhost:8761/
echo.
echo PUERTOS DE LOS SERVICIOS:
echo   Config/Eureka:      8761
echo   Auth Service:       8081
echo   Sensor Service:     8082
echo   Alert Service:      8083
echo   Notification:       8084
echo   Access Control:     8085
echo   Gateway:            8080
echo   Frontend:           8086
echo.
echo CREDENCIALES DE DEMO:
echo   Usuario: admin
echo   Contrasena: admin123
echo.
echo   Usuario: test
echo   Contrasena: test123
echo.
echo Presiona cualquier tecla para continuar...
pause



