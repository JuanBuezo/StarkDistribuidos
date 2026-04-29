# Stark Security System - Nuevo Diseño Grafana

## 🎯 Resumen de Cambios

Se ha realizado una transformación completa del frontend para adoptar un diseño profesional tipo Grafana. Los cambios incluyen:

### ✅ Problema 1: Modal de Sensor en Login - RESUELTO
- **Antes:** La opción "Crear Sensor" aparecía en la página de login
- **Después:** El modal solo es accesible después de autenticarse en el dashboard

### ✅ Problema 2: Diseño No Profesional - RESUELTO  
- **Antes:** Interface con emojis y colores inferiores
- **Después:** Diseño Grafana-like limpio, profesional, sin emojis

## 📦 Archivos Principales

```
starkDistribuidos-frontend/src/main/resources/static/
├── index.html                    (Nuevo - estructura rediseñada)
├── styles/
│   └── style.css                (Nuevo v4 - CSS profesional)
└── js/
    ├── app.js                   (v4 - actualizado)
    ├── auth.js                  (v4 - sin emojis)
    ├── dashboard.js             (v4 - sin emojis)
    └── websocket.js             (v4 - sin emojis)
```

## 🎨 Características Principales

### 1. Layout Moderno
- **Sidebar Navigation:** Acceso rápido a 4 secciones
- **Dark Theme:** Tema oscuro profesional
- **Responsive:** Funciona en desktop y mobile
- **Animaciones:** Transiciones suaves

### 2. Cuatro Vistas Principales

#### Overview (Dashboard)
```
Estadísticas en tarjetas
├─ Sensores Activos: 5
├─ Alertas Activas: 2  
├─ Alertas Críticas: 1
└─ Estado Sistema: Online

Gráficas
├─ Lecturas de Sensores (línea)
└─ Tendencia de Alertas (barras)

Feed en Tiempo Real
└─ Eventos de sensores, alertas y sistema
```

#### Sensores
- Grid de tarjetas profesional
- Info: Nombre, tipo, ubicación, valor, estado
- Botón para crear nuevo sensor
- Indicadores de estado claros

#### Alertas
- Tabla con filtros por nivel
- Colores diferenciados: Rojo (CRÍTICA), Naranja (WARNING), Azul (INFO)
- Acción "Reconocer" para cada alerta
- Actualización en tiempo real

#### Acceso
- Tabla de logs de acceso
- Estado "Permitido" o "Denegado"
- Información: Usuario, Sensor, IP, Timestamp

### 3. Paleta de Colores Profesional

| Color | Hex | Uso |
|-------|-----|-----|
| Primary Dark | #1e3a5f | Background principal |
| Primary | #2c5aa0 | Elementos secundarios |
| Accent Orange | #ff6b35 | Botones y highlights |
| Warning | #ffa500 | Alertas WARNING |
| Critical Red | #e74c3c | Alertas CRITICAL |
| Success Green | #27ae60 | Estado ACTIVE |
| Light Text | #ecf0f1 | Texto principal |

### 4. Sin Emojis
Todos los emojis han sido removidos para un look profesional:
- ✅ No más 🔴🟢✓✗❌⚠️🔧👤📡

## 🚀 Cómo Usar

### Compilar
```bash
cd C:\Users\andre\Documents\GitHub\StarkDistribuidos
mvnw.cmd clean package -DskipTests
```

### Iniciar
```bash
# Opción 1: Doble clic en INICIAR_GRAFANA.bat
# Opción 2: Ejecutar manualmente
java -jar starkDistribuidos-frontend/target/starkDistribuidos-frontend-*.jar
```

### Acceder
```
URL: http://localhost:8080/
Usuario: admin
Contraseña: admin123
```

## 📊 Estructura HTML Nueva

```html
<body>
  <!-- Auth Container (login/registro) -->
  <div id="authContainer">
    <div id="loginForm" class="form-container active">...</div>
    <div id="registerForm" class="form-container">...</div>
  </div>

  <!-- Dashboard Container (solo después de login) -->
  <div id="dashboardContainer">
    <!-- Sidebar Navigation -->
    <aside class="sidebar">
      <nav class="sidebar-nav">
        <a class="nav-item active" onclick="switchTab('overview', event)">Overview</a>
        <a class="nav-item" onclick="switchTab('sensors', event)">Sensores</a>
        <a class="nav-item" onclick="switchTab('alerts', event)">Alertas</a>
        <a class="nav-item" onclick="switchTab('access', event)">Acceso</a>
      </nav>
    </aside>

    <!-- Main Content -->
    <main class="main-content">
      <section id="overviewTab" class="tab-content active">...</section>
      <section id="sensorsTab" class="tab-content">...</section>
      <section id="alertsTab" class="tab-content">...</section>
      <section id="accessTab" class="tab-content">...</section>
    </main>
  </div>

  <!-- Modal (dentro de dashboard) -->
  <div id="sensorModal" class="modal">...</div>
</body>
```

## 🔧 Funciones JavaScript Principales

```javascript
// Navegación
switchTab(tabName, event)       // Cambiar de tab
toggleForm(event)               // Login ↔ Registro

// Modal
openSensorModal()               // Abrir modal crear sensor
closeSensorModal()              // Cerrar modal

// Carga de datos
loadSensors()                   // Cargar sensores como tarjetas
loadAlerts()                    // Cargar tabla de alertas
loadAccessLogs()                // Cargar logs de acceso
filterAlerts()                  // Filtrar alertas por nivel

// Utilidades
showNotification(msg, type)     // Mostrar mensaje
formatDate(dateString)          // Formatear fecha
```

## 📈 Mejoras de Performance

- Chat.js carga cada 30 segundos (no realtime)
- Feed limita a 50 eventos máximo
- WebSocket mantiene conexión persistente
- Validaciones en cliente antes de servidor

## 🧪 Testing

```bash
# Verificar modalquitar no aparece en login
# Verificar switch entre tabs
# Verificar carga de datos
# Verificar WebSocket en tiempo real
# Verificar responsivo en mobile
# Verificar ausencia de emojis
```

## 📚 Documentación Conexa

- `CAMBIOS_GRAFANA_FINAL.md` - Resumen completo
- `RESUMEN_CAMBIOS_GRAFANA.md` - Cambios técnicos detallados
- `VERIFICACION_GRAFANA.md` - Checklist de verificación
- `ARCHITECTURE.md` - Arquitectura general

## 🎓 Próximas Mejoras

1. Búsqueda en tabla de sensores
2. Exportar alertas a CSV
3. Gráficas históricas (últimos 7 días)
4. Dashboard personalizable (drag & drop)
5. Temas claros/oscuros intercambiables
6. Permisos granulares por usuario
7. Notificaciones por email
8. API GraphQL

## ✨ Características Completadas

| Característica | Estado |
|---|---|
| Modal sin emoji | ✅ |
| Diseño Grafana | ✅ |
| 4 Tabs funcionales | ✅ |
| Sidebar navegación | ✅ |
| Gráficas Chart.js | ✅ |
| Feed tiempo real | ✅ |
| Responsive mobile | ✅ |
| Sin emojis | ✅ |
| Dark theme | ✅ |
| Tablas profesionales | ✅ |

## 🐛 Troubleshooting

### Modal aparece en login
```
Solución: Asegurar que dashboardContainer tiene display: none
```

### Estilos no aplican
```
Solución: Limpiar caché navegador (Ctrl+Shift+Delete)
         Verificar que style.css?v=4 se carga
```

### WebSocket no conecta
```
Solución: Verificar que gateway está running en puerto 8080
         Verificar endpoint /stark-security/ws/notifications
```

### Gráficas no se ven
```
Solución: Verificar que Chart.js está cargado
         Verificar que canvas elemento existe
         Abrir consola (F12) para errores
```

## 📝 Notas Importantes

- La versión es v4 (CSS, JS, HTML)
- Compilación exitosa sin errores
- Todos los micro-servicios deben estar running
- Frontend espera backend en localhost:8080/api
- WebSocket en /stark-security/ws/notifications

## 🆘 Soporte

Para reportar issues:
1. Verificar checklist en `VERIFICACION_GRAFANA.md`
2. Revisar consola navegador (F12)
3. Verificar logs del servidor
4. Contactar a equipo de desarrollo

---

**Versión:** 4.0 - Grafana Edition
**Fecha:** 2026-04-29
**Status:** ✅ Production Ready
**HTML:** Nuevo
**CSS:** Nuevo (800+ líneas)
**JavaScript:** Actualizado

