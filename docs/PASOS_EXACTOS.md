🚀 PASOS EXACTOS PARA HACER FUNCIONAR TODO
═════════════════════════════════════════════════════════════════════════════════

SIGUE ESTOS PASOS EN ORDEN (Sin saltar ninguno)

═════════════════════════════════════════════════════════════════════════════════
PASO 1: LIMPIAR TODO
═════════════════════════════════════════════════════════════════════════════════

Abre PowerShell en la carpeta del proyecto:
C:\Users\purru\OneDrive - Universidad Pontificia Comillas\
  Documentos\4_Cuarto\SistemasDistribuidos\ProyectoFinal\
    StarkDistribuidos\StarkDistribuidos

Ejecuta EXACTAMENTE esto:

```powershell
docker-compose down
```

Espera a que termine (verás "Removing... Done")

═════════════════════════════════════════════════════════════════════════════════
PASO 2: COMPILAR
═════════════════════════════════════════════════════════════════════════════════

Ejecuta:

```powershell
mvn clean install -DskipTests
```

⏳ ESPERA A QUE TERMINE (puede tomar 2-3 minutos)

Deberías ver al final:
[INFO] BUILD SUCCESS

⚠️ Si ves "BUILD FAILURE":
   - Ve a la carpeta raíz del proyecto
   - Borra la carpeta "target" completa
   - Intenta de nuevo

═════════════════════════════════════════════════════════════════════════════════
PASO 3: INICIAR SERVICIOS
═════════════════════════════════════════════════════════════════════════════════

Ejecuta:

```powershell
docker-compose up -d
```

Verás:
Creating stark-eureka... done
Creating stark-gateway... done
etc...

═════════════════════════════════════════════════════════════════════════════════
PASO 4: ESPERAR 30 SEGUNDOS
═════════════════════════════════════════════════════════════════════════════════

⏱️ Espera 30 segundos para que todos los servicios se registren en Eureka

Puedes contar o esperar mirando:
```powershell
docker-compose logs -f
```

Cuando veas algo como "Registered with Eureka", puedes continuar.

═════════════════════════════════════════════════════════════════════════════════
PASO 5: VERIFICAR QUE TODOS ESTÉN CORRIENDO
═════════════════════════════════════════════════════════════════════════════════

Ejecuta:

```powershell
docker-compose ps
```

DEBERÍAS VER (con status "Up"):

NAME                  SERVICE             STATUS
stark-eureka          eureka-server       Up
stark-gateway         api-gateway         Up
stark-auth            auth-service        Up
stark-sensor          sensor-service      Up
stark-alert           alert-service       Up
stark-access          access-service      Up
stark-notification    notification-service Up

⚠️ Si alguno dice "Exit" o "Exited":
   Ver logs: docker-compose logs <nombre>
   Luego: docker-compose restart <nombre>

═════════════════════════════════════════════════════════════════════════════════
PASO 6: VERIFICAR EN NAVEGADOR
═════════════════════════════════════════════════════════════════════════════════

Abre en tu navegador:

http://localhost:8761/eureka

DEBERÍAS VER:
- Applications (con al menos 6 servicios listados)
- stark-auth ← Debe estar aquí
- stark-gateway ← Debe estar aquí
- Los demás servicios

Si NO ves nada:
   - Los servicios no se registraron
   - Espera otros 30 segundos
   - Recarga la página (F5)

═════════════════════════════════════════════════════════════════════════════════
PASO 7: PRUEBA EN POSTMAN
═════════════════════════════════════════════════════════════════════════════════

Primero, TEST DIRECTO al Auth Service (saltando gateway):

1. NUEVA REQUEST en Postman
2. POST: http://localhost:8081/auth/test
3. SEND

RESULTADO ESPERADO (200 OK):
{
  "message": "Auth Service is running!",
  "status": "OK"
}

✅ Si ves esto → El Auth Service funciona
❌ Si ves 404 → El servicio no está corriendo

═════════════════════════════════════════════════════════════════════════════════
PASO 8: PROBAR LOGIN DIRECTO (Sin Gateway)
═════════════════════════════════════════════════════════════════════════════════

1. NUEVA REQUEST en Postman
2. POST: http://localhost:8081/auth/login
3. Headers:
   - Content-Type: application/json
4. Body (raw):
   {
     "username": "admin",
     "password": "admin123"
   }
5. SEND

RESULTADO ESPERADO (200 OK):
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "admin",
  "expiresIn": 86400000
}

✅ Si ves el token → El Login funciona
❌ Si ves 404 → Revisar logs: docker-compose logs auth-service

═════════════════════════════════════════════════════════════════════════════════
PASO 9: PROBAR LOGIN VÍA GATEWAY
═════════════════════════════════════════════════════════════════════════════════

Una vez que el paso 8 funciona, intenta por el gateway:

1. POST: http://localhost:8080/stark/api/auth/login
2. Headers:
   - Content-Type: application/json
3. Body (raw):
   {
     "username": "admin",
     "password": "admin123"
   }
4. SEND

RESULTADO ESPERADO (200 OK):
(Mismo token que en el paso 8)

✅ Si ves el token → Todo funciona correctamente
❌ Si ves 404 → Ver logs del gateway: docker-compose logs gateway

═════════════════════════════════════════════════════════════════════════════════
PASO 10: USAR EL TOKEN
═════════════════════════════════════════════════════════════════════════════════

Ahora que tienes un token, úsalo para otros endpoints:

1. GET: http://localhost:8080/stark/api/sensors
2. Headers:
   - Authorization: Bearer <tu_token_aquí>
   - Content-Type: application/json
3. SEND

RESULTADO: Debería devolver [] (lista vacía)

═════════════════════════════════════════════════════════════════════════════════
COMANDOS DE DEBUG ÚTILES
═════════════════════════════════════════════════════════════════════════════════

Ver todos los logs:
```powershell
docker-compose logs -f
```

Ver logs de un servicio específico:
```powershell
docker-compose logs -f auth-service
docker-compose logs -f gateway
docker-compose logs -f eureka-server
```

Ver estado en tiempo real:
```powershell
docker-compose ps --watch
```

Detener todo:
```powershell
docker-compose down
```

Reiniciar un servicio:
```powershell
docker-compose restart auth-service
```

═════════════════════════════════════════════════════════════════════════════════
RESUMEN RÁPIDO
═════════════════════════════════════════════════════════════════════════════════

✅ docker-compose down
✅ mvn clean install -DskipTests
✅ docker-compose up -d
✅ Espera 30 segundos
✅ docker-compose ps (verifica todos "Up")
✅ http://localhost:8761/eureka (verifica servicios registrados)
✅ POST http://localhost:8081/auth/test (verifica Auth funciona)
✅ POST http://localhost:8081/auth/login (verifica login funciona)
✅ POST http://localhost:8080/stark/api/auth/login (verifica gateway funciona)
✅ GET http://localhost:8080/stark/api/sensors (usa el token)

Si algo falla, cuéntame EN QUÉ PASO y QUÉ ERROR ves.

