# Guía de Verificación - Nuevo Diseño Grafana

## Checklist de Funcionalidades

### Login / Registro
- [ ] Login sin mostrar modal de crear sensor
- [ ] Registro funciona correctamente
- [ ] Mensajes de error sin emojis
- [ ] Animación suave entre formularios
- [ ] Credenciales demo visibles

### Dashboard General
- [ ] Sidebar visible con navegación correcta
- [ ] 4 tabs funcionan: Overview, Sensores, Alertas, Acceso
- [ ] Indicador de usuario sin emoji
- [ ] Botón de logout funciona
- [ ] Dark mode se ve profesional

### Overview Tab
- [ ] 4 stat cards muestran números correctos
- [ ] Gráfica de sensores muestra línea
- [ ] Gráfica de alertas muestra barras
- [ ] Feed en tiempo real se actualiza con eventos
- [ ] Sin emojis en ningún lugar

### Sensores Tab
- [ ] Sensores muestran como tarjetas (no tabla)
- [ ] Cada tarjeta muestra: nombre, tipo, ubicación, valor, fecha
- [ ] Estado "Activo" o "Inactivo" sin emojis
- [ ] Botón "+ Nuevo Sensor" funciona
- [ ] Modal se abre correctamente

### Modal de Sensor
- [ ] Modal solo visible cuando se hace clic en botón
- [ ] Tiene campos: Nombre, Tipo, Ubicación
- [ ] Botón crear/cancelar funciona
- [ ] Se cierra con X o Cancelar
- [ ] Se cierra al hacer clic fuera

### Alertas Tab
- [ ] Tabla muestra alertas
- [ ] Filtro por nivel funciona
- [ ] Botón reconocer funciona
- [ ] Colores: Rojo (crítica), Naranja (warning), Azul (info)
- [ ] Sin emojis en mensaje

### Acceso Tab
- [ ] Tabla muestra logs de acceso
- [ ] Muestra: Usuario, Sensor, Permitido/Denegado, IP, Timestamp
- [ ] Colores diferenciados para permitido/denegado
- [ ] Sin emojis

### WebSocket / Tiempo Real
- [ ] Feed se actualiza cuando llega una alerta
- [ ] Eventos de sensores aparecen en feed
- [ ] Máximo 50 eventos en feed (auto-limpia)
- [ ] Sin emojis en notificaciones

### Responsive
- [ ] En mobile, sidebar se ve y funciona
- [ ] Tablas son scrolleables en mobile
- [ ] Grid de sensores se adapta
- [ ] Modal se ve bien en mobile

## URLs de Prueba

```
Login: http://localhost:8080/
Credenciales: admin / admin123
```

## Posibles Problemas y Soluciones

### Modal aparece en login
**Solución:** Dashboard container debe estar oculto por defecto
```javascript
// Verificar que:
#dashboardContainer { display: none; }
// hasta que se haga login
```

### Emojis aún visibles
**Solución:** Buscar en archivos JS cualquier emoji restante
```bash
grep -r "🔴\|🟢\|✓\|✗\|❌\|⚠️\|📡\|🔧\|👤" src/main/resources/static/js/
```

### Página descentrada
**Solución:** Verificar que el CSS tiene:
```css
body {
    margin: 0;
    padding: 0;
    width: 100%;
    height: 100%;
}
```

### Gráficas no se ven
**Solución:** Verificar que Chart.js está cargado y canvas existe

### WebSocket no funciona
**Solución:** Verificar que el servidor tiene el endpoint `/stark-security/ws/notifications`

## Cambios Visuales Principales

| Elemento | Antes | Después |
|----------|--------|---------|
| Modal en login | ✓ Visible | ✗ Oculto |
| Emojis | ✓ Presentes | ✗ Removidos |
| Colores | Random | Profesional (Naranja/Azul) |
| Layout | Centrado | Sidebar + Main |
| Sensores | Tabla | Tarjetas |
| Diseño | Simple | Tipo Grafana |

## Performance Tips

- Limitar feed a 50 eventos para evitar lag
- Chart.js cargas cada 30 segundos
- Lazy loading de sensores si hay muchos
- WebSocket mantiene conexión abierta

## Siguientes Mejoras (Futuro)

1. Búsqueda en tabla de sensores
2. Exportar alertas a CSV
3. Gráficas históricas (últimos 7 días)
4. Temas claros/oscuros intercambiables
5. Personalización de dashboards
6. Roles y permisos granulares

