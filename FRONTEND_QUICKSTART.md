# 🚀 Ejecutar Stark Industries con Frontend

## Opción 1: Ejecución Rápida (Recomendado)

### 1. Compilar el proyecto
```bash
mvn clean install -DskipTests
```

### 2. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

### 3. Acceder al frontend
```
http://localhost:8080/stark-security/
```

### 4. Credenciales de demo
- **Usuario**: `admin`
- **Contraseña**: `admin123`

---

## Opción 2: Con Docker Compose (Producción)

### 1. Compilar imagen Docker
```bash
docker build -t stark-security:latest .
```

### 2. Ejecutar con docker-compose
```bash
docker-compose up -d
```

### 3. Acceder
```
http://localhost:8080/stark-security/
```

---

## 📊 Verificar que Todo Funciona

### 1. Acceder al frontend
```
✅ http://localhost:8080/stark-security/
```

### 2. Hacer login
```
Usuario: admin
Contraseña: admin123
```

### 3. Verificar elementos del dashboard
- ✅ Estadísticas cargadas correctamente
- ✅ Gráficos de Chart.js renderizados
- ✅ WebSocket conectado (ver console para "✓ Conectado a WebSocket")
- ✅ Feed de eventos en tiempo real funcionando

### 4. Probear endpoints API
```bash
# Listar sensores
curl -u admin:admin123 http://localhost:8080/stark-security/api/sensors

# Listar alertas
curl -u admin:admin123 http://localhost:8080/stark-security/api/alerts

# Listar logs de acceso
curl -u admin:admin123 http://localhost:8080/stark-security/api/access/logs
```

---

## 🛠️ Troubleshooting

### Frontend no carga
```
❌ Error: "404 - Not Found"
✅ Solución: 
   - Asegúrate de que el servidor esté ejecutándose en puerto 8080
   - Verifica que la ruta sea: http://localhost:8080/stark-security/
   - Revisa los logs de la aplicación
```

### Login no funciona
```
❌ Error: "Usuario o contraseña inválidos"
✅ Solución:
   - Usa: admin / admin123
   - Verifica que /api/system/health responda
   - Revisa la consola del navegador (F12)
```

### Gráficos no aparecen
```
❌ Error: Los gráficos están en blanco
✅ Solución:
   - Chart.js debe cargar desde CDN (requiere conexión a internet)
   - Verifica que no haya Content Security Policy
   - Revisa la consola para errores de CORS
```

### WebSocket no se conecta
```
❌ Error: "✗ Error en WebSocket"
✅ Solución:
   - Verifica que WebSocket esté habilitado en Spring
   - Revisa que /stark-security/ws/** sea accesible
   - Intenta sin proxy/firewall
   - Recarga la página (F5)
```

### Base de datos vacía
```
❌ Error: "No hay sensores registrados"
✅ Solución:
   - En desarrollo, H2 crea datos con create-drop
   - Ejecuta la aplicación una vez (crea tablas)
   - Los datos en data.sql se cargan automáticamente
   - Crea un sensor nuevo desde el frontend
```

---

## 📋 Estructura de Carpetas

```
StarkDistribuidos/
├── src/main/
│   ├── java/com/distribuidos/stark/
│   │   ├── controller/
│   │   │   ├── AccessController.java
│   │   │   ├── AlertController.java
│   │   │   ├── SensorController.java
│   │   │   ├── SystemController.java
│   │   │   └── FrontendController.java         ← NUEVO
│   │   ├── config/
│   │   │   ├── SecurityConfig.java (modificado)
│   │   │   ├── WebConfig.java                   ← NUEVO
│   │   │   └── ...
│   │   └── ...
│   └── resources/
│       ├── application.yaml (modificado)
│       ├── data.sql                             ← NUEVO
│       └── static/                              ← NUEVO
│           ├── index.html
│           ├── styles/style.css
│           ├── js/
│           │   ├── app.js
│           │   ├── auth.js
│           │   ├── dashboard.js
│           │   └── websocket.js
│           └── README.md
├── FRONTEND_GUIDE.md                            ← NUEVO
├── pom.xml
├── docker-compose.yml
└── ...
```

---

## 🔑 Características del Frontend

### ✅ Autenticación
- Login con HTTP Basic Auth
- Registro de usuarios
- Sesión persistente con localStorage
- Cierre de sesión seguro

### ✅ Dashboard (Overview)
- Estadísticas en tiempo real
- Gráficos con Chart.js
- Feed de eventos en vivo
- Actualización cada 30 segundos

### ✅ Sensores
- Listar sensores activos
- Crear nuevos sensores
- Ver detalles y valores
- Estado en tiempo real

### ✅ Alertas
- Listar alertas no reconocidas
- Filtrar por nivel
- Reconocer alertas
- Notificaciones críticas

### ✅ Acceso
- Ver logs de intentos
- Auditoría completa
- Filtrado por usuario
- Historial de cambios

### ✅ Comunicación
- WebSocket STOMP para tiempo real
- REST API para datos persistentes
- Reconexión automática
- Notificaciones del navegador

---

## 📦 Dependencias del Frontend

### CDN (No requieren descarga)
```html
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
```

### Backend (Spring Boot)
Incluidas en `pom.xml`:
- spring-boot-starter-web (REST API)
- spring-boot-starter-security (Autenticación)
- spring-boot-starter-websocket (WebSocket)
- spring-boot-starter-data-jpa (Base de datos)

---

## 🚀 Próximos Pasos

1. **Ejecutar la aplicación** con `mvn spring-boot:run`
2. **Acceder** a http://localhost:8080/stark-security/
3. **Login** con admin / admin123
4. **Explorar** el dashboard de telemetría
5. **Crear** nuevos sensores desde el UI
6. **Monitorizar** alertas en tiempo real

---

## 📞 Soporte

Para problemas o dudas:
1. Revisa **FRONTEND_GUIDE.md** para documentación completa
2. Comprueba **logs** en la consola de Spring Boot
3. Abre F12 en el navegador para ver la consola JavaScript
4. Verifica que todos los endpoints de API respondan

---

**¡Disfruta de Stark Industries! ⚡**

*Última actualización: 2026-03-25*

