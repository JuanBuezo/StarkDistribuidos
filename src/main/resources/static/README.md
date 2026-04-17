# Frontend de Stark Industries

## 🎯 Inicio Rápido

### Acceder a la Aplicación
```
http://localhost:8080/stark-security/
```

### Credenciales de Demo
```
Usuario: admin
Contraseña: admin123
```

## 📊 Componentes Principales

### 1. **Login/Registro**
- Interfaz de autenticación moderna
- Validación de contraseñas
- Almacenamiento seguro de sesiones

### 2. **Dashboard de Telemetría**
- Estadísticas en tiempo real
- Gráficos interactivos
- Feed de eventos en vivo

### 3. **Gestión de Sensores**
- Crear y visualizar sensores
- Monitorizar estado
- Ver datos en tiempo real

### 4. **Sistema de Alertas**
- Alertas con diferentes niveles
- Filtrado y reconocimiento
- Notificaciones en tiempo real

### 5. **Logs de Acceso**
- Auditoría completa
- Control de acceso
- Historial de intentos

## 🔧 Arquitectura

### Comunicación
- **REST API**: Para datos principales (sensores, alertas, logs)
- **WebSocket STOMP**: Para notificaciones en tiempo real
- **HTTP Basic Auth**: Para autenticación

### Almacenamiento
- **localStorage**: Sesión del usuario y token de autenticación
- **sessionStorage**: Datos temporales

### Gráficos
- **Chart.js**: Visualización de datos
- **Canvas**: Renderización eficiente

## 📱 Responsive Design

✅ Funciona en:
- 📱 Móviles (320px+)
- 📱 Tablets (768px+)
- 💻 Desktops (1024px+)
- 🖥️ Pantallas grandes (1920px+)

## 🎨 Temas Visuales

- **Tema Oscuro**: Por defecto (mejor para monitorización 24/7)
- **Colores**: Optimizados para accesibilidad
- **Fuentes**: Segoe UI para máxima compatibilidad

## ⚡ Características Avanzadas

### Tiempo Real
- WebSocket con reconexión automática
- Notificaciones del navegador
- Feed de eventos en vivo

### Sincronización
- Sincronización automática cada 30 segundos
- Actualización instantánea vía WebSocket
- Caché inteligente de datos

### Seguridad
- HTTP Basic Auth
- Tokens almacenados localmente
- Validación en cliente y servidor

## 🚀 Optimizaciones

- 📦 Carga de archivos estáticos en caché (1 año)
- 🗜️ Compresión GZIP habilitada
- 🌐 HTTP/2 habilitado
- ⚡ Lazy loading de recursos

---

**¡Disfruta del dashboard de Stark Industries!** 🚀

