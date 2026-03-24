@echo off
echo.
echo ╔═══════════════════════════════════════════════════════╗
echo ║   STARK INDUSTRIES - MICROSERVICIOS START SCRIPT       ║
echo ╚═══════════════════════════════════════════════════════╝
echo.

echo Building...
call mvn clean install -DskipTests -q

if %ERRORLEVEL% EQU 0 (
    echo OK Build successful
    echo.
    echo Starting Docker Compose...
    call docker-compose up -d

    timeout /t 3 /nobreak
    call docker-compose ps

    echo.
    echo OK Services started!
    echo   API Gateway: http://localhost:8080/stark
    echo   Eureka: http://localhost:8761/eureka
) else (
    echo ERROR Build failed
    pause
    exit /b 1
)

