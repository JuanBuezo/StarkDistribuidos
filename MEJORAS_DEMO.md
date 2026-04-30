# 🚀 MEJORAS DEMO - StarkDistribuidos

## ✨ Nuevas Características

### 1. Generación Automática de Eventos
- **Anteriormente**: Botones manuales
- **Ahora**: Botón "Iniciar Demo" que genera eventos cada 10s automáticamente
- **Ubicación**: Tab Alertas → Botón "Iniciar Demo"

### 2. Selector de Sensores Dinámico
- **Mejora**: Gráfica de sensores ahora muestra solo los sensores seleccionados
- **Cómo usar**: Click en los botones de sensores dentro de la gráfica
- **Display**: Cada sensor con color diferente
- **Datos**: Coherentes y progresivos según tipo de sensor

### 3. Feed Mejorado
- **Antes**: 5 alertas estáticas
- **Ahora**: Hasta 10 alertas actualizadas cada 10s automáticamente
- **Estilos**: Código de color por nivel (CRITICAL, WARNING, INFO)

### 4. Valores Realistas de Sensores
- **TEMPERATURA**: 15-40°C
- **HUMEDAD**: 30-90%
- **MOVIMIENTO**: 0-100 (intensidad)
- **INTRUSIÓN**: 0-1 (detección)
- **HUMO**: 0-50 ppm
- **CO2**: 300-1000 ppm

---

## 🎮 Uso Quick Start

### Demo Automática Completa (Ideal para presentaciones):
1. Login → `admin:admin123`
2. Tab "Alertas"
3. Click **"Iniciar Demo"**
4. Observa cómo fluyen datos automáticamente
5. Click nuevamente para detener

### Explorar Sensores:
1. Tab "Alertas" → Ver gráfica de sensores
2. Selecciona sensores haciendo click
3. La gráfica se actualiza automáticamente
4. Tab "Sensores" → Crear nuevos sensores
5. Aparecerán automáticamente en selector

### Verificar Feed:
1. Observa "Feed en Tiempo Real" en el Overview
2. Se actualiza cada 10s cuando Demo está activo
3. Muestra nivel, mensaje y timestamp

---

## ⚙️ Configuración

### Intervalo de Simulación
**Archivo**: `dashboard.js` línea ~480  
**Valor actual**: `10000` ms (10 segundos)  
**Para cambiar**: Edita ambos intervalos en `startAutoSimulation()` y `startRealtimeFeedRefresh()`

### Colores de Sensores
**Archivo**: `dashboard.js` línea ~174  
**Array**: `colors[]`  
**Para cambiar**: Modifica valores hexadecimales

---

## ✅ Checklist De Cambios

### Backend
- [x] Controller de sensores genera valores realistas
- [x] Valores guardados en BD
- [x] Endpoints `/alerts/simulate` y `/access/simulate` funcionan

### Frontend
- [x] Selector dinámico de sensores
- [x] Gráfica de sensores mejorada
- [x] Feed actualizado cada 10s
- [x] Botón "Iniciar Demo" visual
- [x] Sin librerías nuevas
- [x] Estilos mantienen consistencia

### Automatización
- [x] Alertas cada 10s
- [x] Accesos cada 10s
- [x] Feed se actualiza automáticamente
- [x] Limpieza de intervalos en logout

---

## 🎯 Casos de Uso

### Demostración a Clientes:
```
1. Abrir Dashboard
2. Iniciar Demo
3. Dejar corriendo mientras explicas funcionalidades
4. Datos fluyen en vivo = impresión profesional
```

### Testing de Alertas:
```
1. Click Iniciar Demo
2. Las alertas aparecen en la tabla automáticamente
3. Usa filtros para ver por nivel
4. Feed muestra las más recientes
```

### Desarrollo Local:
```
1. Para desarrollo: Botones manuales "Simular alerta/acceso"
2. Para demo: Iniciar Demo para testing completo
3. Selector de sensores para visualizar datos específicos
```

---

## 🔧 Deshacer Cambios

Si necesitas volver a versión anterior:
```
git checkout HEAD -- starkDistribuidos-frontend/src/main/resources/static/js/dashboard.js
git checkout HEAD -- starkDistribuidos-frontend/src/main/resources/static/index.html
git checkout HEAD -- starkDistribuidos-sensor/src/main/java/com/distribuidos/stark/sensor/controller/SensorController.java
```

---

**Versión**: 2.0  
**Compatibilidad**: Spring Boot 3.2.5 ✓  
**Breaking Changes**: Ninguno ✓

