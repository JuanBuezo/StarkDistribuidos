@echo off
echo ==============================
echo Iniciando StarkDistribuidos...
echo ==============================

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
"Start-Process powershell -ArgumentList '-NoExit','-Command','cd \"%CD%\"; .\mvnw -pl starkDistribuidos-config spring-boot:run -Dspring-boot.run.profiles=local'"

timeout /t 10

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
"Start-Process powershell -ArgumentList '-NoExit','-Command','cd \"%CD%\"; .\mvnw -pl starkDistribuidos-gateway spring-boot:run -Dspring-boot.run.profiles=local'"

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
"Start-Process powershell -ArgumentList '-NoExit','-Command','cd \"%CD%\"; .\mvnw -pl starkDistribuidos-auth spring-boot:run -Dspring-boot.run.profiles=local'"

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
"Start-Process powershell -ArgumentList '-NoExit','-Command','cd \"%CD%\"; .\mvnw -pl starkDistribuidos-sensor spring-boot:run -Dspring-boot.run.profiles=local'"

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
"Start-Process powershell -ArgumentList '-NoExit','-Command','cd \"%CD%\"; .\mvnw -pl starkDistribuidos-alert spring-boot:run -Dspring-boot.run.profiles=local'"

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
"Start-Process powershell -ArgumentList '-NoExit','-Command','cd \"%CD%\"; .\mvnw -pl starkDistribuidos-access spring-boot:run -Dspring-boot.run.profiles=local'"

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
"Start-Process powershell -ArgumentList '-NoExit','-Command','cd \"%CD%\"; .\mvnw -pl starkDistribuidos-notification spring-boot:run -Dspring-boot.run.profiles=local'"

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
"Start-Process powershell -ArgumentList '-NoExit','-Command','cd \"%CD%\"; .\mvnw -pl starkDistribuidos-frontend spring-boot:run -Dspring-boot.run.profiles=local'"

echo ==============================
echo Todos los servicios lanzados
echo ==============================
pause