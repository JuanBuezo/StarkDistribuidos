// ============================
// WEBSOCKET - COMUNICACIÓN EN TIEMPO REAL
// ============================

/**
 * Conecta al WebSocket STOMP
 */
function connectWebSocket() {
    const socket = new SockJS('/stark-security/ws/notifications');
    stompClient = Stomp.over(socket);

    // Desactivar logs verbose
    stompClient.debug = null;

    stompClient.connect({}, onWebSocketConnect, onWebSocketError);
}

/**
 * Manejador de conexión exitosa
 */
function onWebSocketConnect(frame) {
    console.log('✓ Conectado a WebSocket');

    // Suscribirse a alertas en tiempo real
    stompClient.subscribe('/topic/alerts', function (message) {
        const alert = JSON.parse(message.body);
        handleRealtimeAlert(alert);
    });

    // Suscribirse a actualizaciones de sensores
    stompClient.subscribe('/topic/sensor-data', function (message) {
        const sensor = JSON.parse(message.body);
        handleRealtimeSensorData(sensor);
    });

    // Suscribirse a eventos del sistema
    stompClient.subscribe('/topic/system-events', function (message) {
        const event = JSON.parse(message.body);
        handleSystemEvent(event);
    });
}

/**
 * Manejador de errores de WebSocket
 */
function onWebSocketError(error) {
    console.error('✗ Error en WebSocket:', error);
    // Intentar reconectar después de 5 segundos
    setTimeout(connectWebSocket, 5000);
}

/**
 * Desconecta del WebSocket
 */
function disconnectWebSocket() {
    if (stompClient && stompClient.connected) {
        stompClient.disconnect(function () {
            console.log('Desconectado de WebSocket');
        });
    }
}

/**
 * Maneja alertas en tiempo real
 */
function handleRealtimeAlert(alert) {
    // Actualizar las estadísticas
    loadAlertStats();

    // Añadir evento al feed en tiempo real
    const feed = document.getElementById('realtimeFeed');
    if (feed) {
        const placeholder = feed.querySelector('.placeholder');
        if (placeholder) {
            placeholder.remove();
        }

        const eventItem = document.createElement('div');
        eventItem.className = `event-item ${alert.level.toLowerCase()}`;
        eventItem.innerHTML = `
            <div class="event-time">${formatDate(new Date())}</div>
            <div class="event-message">
                <strong>${alert.level}</strong>: ${alert.message}
                ${alert.sensorId ? ` (Sensor #${alert.sensorId})` : ''}
            </div>
        `;

        feed.insertBefore(eventItem, feed.firstChild);

        // Limitar a 50 eventos
        while (feed.children.length > 50) {
            feed.removeChild(feed.lastChild);
        }
    }

    // Notificación de navegador si es crítica
    if (alert.level === 'CRITICAL' && Notification && Notification.permission === 'granted') {
        new Notification('⚠️ Alerta Crítica', {
            body: alert.message,
            icon: '/stark-security/static/images/icon.png',
        });
    }
}

/**
 * Maneja datos de sensores en tiempo real
 */
function handleRealtimeSensorData(sensor) {
    // Actualizar las estadísticas
    loadSensorStats();

    // Actualizar el evento en el feed
    const feed = document.getElementById('realtimeFeed');
    if (feed) {
        const placeholder = feed.querySelector('.placeholder');
        if (placeholder) {
            placeholder.remove();
        }

        const eventItem = document.createElement('div');
        eventItem.className = 'event-item';
        eventItem.innerHTML = `
            <div class="event-time">${formatDate(new Date())}</div>
            <div class="event-message">
                📡 <strong>${sensor.name}</strong>: ${sensor.value}
                <span style="color: #BDC3C7;">(${sensor.location})</span>
            </div>
        `;

        feed.insertBefore(eventItem, feed.firstChild);

        // Limitar a 50 eventos
        while (feed.children.length > 50) {
            feed.removeChild(feed.lastChild);
        }
    }
}

/**
 * Maneja eventos del sistema
 */
function handleSystemEvent(event) {
    // Añadir evento al feed
    const feed = document.getElementById('realtimeFeed');
    if (feed) {
        const placeholder = feed.querySelector('.placeholder');
        if (placeholder) {
            placeholder.remove();
        }

        const eventItem = document.createElement('div');
        eventItem.className = 'event-item';
        eventItem.innerHTML = `
            <div class="event-time">${formatDate(new Date())}</div>
            <div class="event-message">
                🔧 <strong>Sistema</strong>: ${event.message}
            </div>
        `;

        feed.insertBefore(eventItem, feed.firstChild);

        // Limitar a 50 eventos
        while (feed.children.length > 50) {
            feed.removeChild(feed.lastChild);
        }
    }
}

/**
 * Envía un mensaje a través del WebSocket
 */
function sendWebSocketMessage(destination, message) {
    if (stompClient && stompClient.connected) {
        stompClient.send(destination, {}, JSON.stringify(message));
    }
}

/**
 * Solicita permiso para notificaciones de navegador
 */
function requestNotificationPermission() {
    if (Notification && Notification.permission === 'default') {
        Notification.requestPermission();
    }
}

// Solicitar permisos de notificación al cargar
document.addEventListener('DOMContentLoaded', function () {
    requestNotificationPermission();
});

// Intentar reconectar si la conexión se pierde
setInterval(() => {
    if (currentUser && (!stompClient || !stompClient.connected)) {
        console.log('Reconectando a WebSocket...');
        connectWebSocket();
    }
}, 30000);

