🔍 GUÍA DE TROUBLESHOOTING - Error 404 en Login
═════════════════════════════════════════════════════════════════════════════════

⚠️ PROBLEMA: GET/POST http://localhost:8080/stark/api/auth/login devuelve 404

═════════════════════════════════════════════════════════════════════════════════
PASO 1: VERIFICAR SI LOS SERVICIOS ESTÁN CORRIENDO
═════════════════════════════════════════════════════════════════════════════════

En Terminal (PowerShell o CMD):

```bash
docker-compose ps
```

RESULTADO ESPERADO:
┌────────────────────────┬────────────────────────┬─────────┐
│ NAME                   │ STATUS                 │ PORTS   │
├────────────────────────┼────────────────────────┼─────────┤
│ stark-eureka           │ Up (healthy)           │ 8761    │
│ stark-gateway          │ Up                     │ 8080    │
│ stark-auth             │ Up                     │ 8081    │
│ stark-sensor           │ Up                     │ 8082    │
│ stark-alert            │ Up                     │ 8083    │
│ stark-access           │ Up                     │ 8084    │
│ stark-notification     │ Up                     │ 8085    │
└────────────────────────┴────────────────────────┴─────────┘

Si TODOS muestran "Up" → salta a PASO 2
Si alguno muestra "Exit" o "Error" → ve a SOLUCIÓN A

═════════════════════════════════════════════════════════════════════════════════
PASO 2: VERIFICAR EUREKA DASHBOARD
═════════════════════════════════════════════════════════════════════════════════

Abre en navegador:
http://localhost:8761/eureka

DEBERÍAS VER:
- stark-auth registrado
- stark-sensor registrado
- stark-gateway registrado
- stark-alert registrado
- stark-access registrado
- stark-notification registrado

Si NO ves los servicios → ve a SOLUCIÓN B
Si SÍ ves los servicios → salta a PASO 3

═════════════════════════════════════════════════════════════════════════════════
PASO 3: VERIFICAR GATEWAY HEALTH
═════════════════════════════════════════════════════════════════════════════════

En Postman o curl:

GET http://localhost:8080/stark/actuator/health

Debería retornar 200 OK con algo como:
{
  "status": "UP"
}

Si retorna 404 → ve a SOLUCIÓN C
Si retorna 200 → ve a PASO 4

═════════════════════════════════════════════════════════════════════════════════
PASO 4: PROBAR LOGIN DIRECTO EN AUTH SERVICE
═════════════════════════════════════════════════════════════════════════════════

PRUEBA DIRECTAMENTE al servicio (saltando gateway):

POST http://localhost:8081/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

Si retorna 200 → El problema es el GATEWAY (salta a SOLUCIÓN C)
Si retorna 404 → El problema es AUTH SERVICE (salta a SOLUCIÓN D)
Si retorna 500 → Hay error en el código (salta a SOLUCIÓN E)

═════════════════════════════════════════════════════════════════════════════════
SOLUCIONES
═════════════════════════════════════════════════════════════════════════════════

SOLUCIÓN A: Servicios no compilados o error en startup
───────────────────────────────────────────────────────

1. Detener todo:
   docker-compose down

2. Limpiar y compilar:
   mvn clean install -DskipTests

   ⏳ Esto puede tomar 2-3 minutos

3. Verificar que compiló correctamente:
   Debería ver "BUILD SUCCESS" al final

4. Iniciar servicios:
   docker-compose up -d

5. Esperar 30 segundos

6. Verificar: docker-compose ps

Si aún no funciona → ve a SOLUCIÓN B


SOLUCIÓN B: Servicios no registrados en Eureka
──────────────────────────────────────────────

1. Ver logs de Eureka:
   docker-compose logs eureka-server

2. Ver logs de Gateway:
   docker-compose logs gateway

3. Buscar errores de conexión

4. Si ves "Failed to connect to eureka":
   - Verifica que eureka-server esté corriendo
   - docker restart eureka-server
   - Espera 10 segundos
   - docker-compose up -d (reinicia los otros)


SOLUCIÓN C: Gateway no está ruteando a Auth
─────────────────────────────────────────────

1. Verifica que el archivo de configuración sea correcto:
   starkDistribuidos-gateway/src/main/resources/application.yaml

   Debería tener:
   ```yaml
   spring:
     cloud:
       gateway:
         routes:
           - id: auth-service
             uri: lb://stark-auth
             predicates:
               - Path=/api/auth/**
   ```

2. Si está bien, reconstruye:
   mvn clean package -DskipTests
   docker-compose up -d gateway


SOLUCIÓN D: Auth Service no tiene endpoint
────────────────────────────────────────────

1. Ver logs del servicio:
   docker-compose logs auth-service

2. Verificar que el controller tenga @PostMapping("/login"):
   starkDistribuidos-auth/src/main/java/.../AuthController.java

3. Si falta, reconstruir:
   mvn clean package -DskipTests
   docker-compose up -d auth-service


SOLUCIÓN E: Error 500 en el endpoint
──────────────────────────────────────

1. Ver logs detallados:
   docker-compose logs -f auth-service

2. Buscar la línea de error

3. Comúnmente:
   - "No constructor" → revisar clase
   - "Bean not found" → revisar Spring Configuration
   - "Connection refused" → revisar BD

═════════════════════════════════════════════════════════════════════════════════
CHECKLIST RÁPIDO - EJECUTA ESTO PRIMERO
═════════════════════════════════════════════════════════════════════════════════

En orden:

□ docker-compose down
□ mvn clean install -DskipTests
□ docker-compose up -d
□ Esperar 30 segundos
□ docker-compose ps (verificar todos "Up")
□ Abrir http://localhost:8761/eureka (verificar servicios)
□ Probar: POST http://localhost:8080/stark/api/auth/login
□ Si aún falla, probar: POST http://localhost:8081/auth/login

═════════════════════════════════════════════════════════════════════════════════

¿QUÉ VES EN CADA PASO? Cuéntame y te ayudaré.

