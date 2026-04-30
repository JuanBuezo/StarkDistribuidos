// ============================
// DASHBOARD - DATOS Y GRÁFICOS
// ============================

/**
 * Carga los datos iniciales del dashboard
 */
async function loadDashboardData() {
    await Promise.all([
        loadSensorStats(),
        loadAlertStats(),
        initializeCharts(),
        loadRealtimeFeed(),
    ]);
}

/**
 * Carga estadísticas de sensores
 */
async function loadSensorStats() {
    const data = await apiCall('/sensors');
    if (data && Array.isArray(data)) {
        const activeSensors = data.filter(s => s.status === 'ACTIVE').length;
        document.getElementById('activeSensorsCount').textContent = data.length;
        updateSensorChart(data);
    }
}

/**
 * Carga estadísticas de alertas
 */
async function loadAlertStats() {
    const data = await apiCall('/alerts');
    if (data && Array.isArray(data)) {
        const activeAlerts = data.filter(a => !a.acknowledged).length;
        const criticalAlerts = data.filter(a => a.level === 'CRITICAL' && !a.acknowledged).length;

        document.getElementById('activeAlertsCount').textContent = activeAlerts;
        document.getElementById('criticalAlertsCount').textContent = criticalAlerts;

        updateAlertChart(data);
    }
}

/**
 * Inicializa los gráficos
 */
function initializeCharts() {
    // Gráfico de sensores
    const sensorCtx = document.getElementById('sensorChart');
    if (sensorCtx) {
        charts.sensor = new Chart(sensorCtx, {
            type: 'line',
            data: {
                labels: [],
                datasets: [
                    {
                        label: 'Sensores Activos',
                        data: [],
                        borderColor: '#FF6B35',
                        backgroundColor: 'rgba(255, 107, 53, 0.1)',
                        tension: 0.3,
                        fill: true,
                    },
                ],
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        labels: {
                            color: '#ECF0F1',
                        },
                    },
                },
                scales: {
                    y: {
                        ticks: {
                            color: '#BDC3C7',
                        },
                        grid: {
                            color: 'rgba(44, 62, 80, 0.5)',
                        },
                    },
                    x: {
                        ticks: {
                            color: '#BDC3C7',
                        },
                        grid: {
                            color: 'rgba(44, 62, 80, 0.5)',
                        },
                    },
                },
            },
        });
    }

    // Gráfico de alertas
    const alertCtx = document.getElementById('alertChart');
    if (alertCtx) {
        charts.alert = new Chart(alertCtx, {
            type: 'bar',
            data: {
                labels: ['Críticas', 'Advertencias', 'Info'],
                datasets: [
                    {
                        label: 'Alertas por Nivel',
                        data: [0, 0, 0],
                        backgroundColor: ['#E74C3C', '#F39C12', '#3498DB'],
                    },
                ],
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                indexAxis: 'y',
                plugins: {
                    legend: {
                        display: false,
                    },
                },
                scales: {
                    x: {
                        ticks: {
                            color: '#BDC3C7',
                        },
                        grid: {
                            color: 'rgba(44, 62, 80, 0.5)',
                        },
                    },
                    y: {
                        ticks: {
                            color: '#BDC3C7',
                        },
                        grid: {
                            display: false,
                        },
                    },
                },
            },
        });
    }
}

/**
 * Actualiza el gráfico de sensores
 */
function updateSensorChart(sensors) {
    if (!charts.sensor) return;

    const hours = [];
    const sensorCounts = [];

    for (let i = 23; i >= 0; i--) {
        const hour = new Date();
        hour.setHours(hour.getHours() - i);
        hours.push(hour.getHours() + ':00');
        // Simular datos
        sensorCounts.push(Math.floor(Math.random() * sensors.length) + 1);
    }

    charts.sensor.data.labels = hours;
    charts.sensor.data.datasets[0].data = sensorCounts;
    charts.sensor.update();
}

/**
 * Actualiza el gráfico de alertas
 */
function updateAlertChart(alerts) {
    if (!charts.alert) return;

    const critical = alerts.filter(a => a.level === 'CRITICAL').length;
    const warning = alerts.filter(a => a.level === 'WARNING').length;
    const info = alerts.filter(a => a.level === 'INFO').length;

    charts.alert.data.datasets[0].data = [critical, warning, info];
    charts.alert.update();
}

/**
 * Carga la lista de sensores
 */
async function loadSensors() {
    const data = await apiCall('/sensors');
    const sensorsGrid = document.getElementById('sensorsGrid');

    if (!data || !Array.isArray(data)) {
        sensorsGrid.innerHTML = '<p class="placeholder">No se pudieron cargar los sensores</p>';
        return;
    }

    if (data.length === 0) {
        sensorsGrid.innerHTML = '<p class="placeholder">No hay sensores registrados</p>';
        return;
    }

    sensorsGrid.innerHTML = data
        .map(sensor => `
        <div class="sensor-card">
            <div class="sensor-header">
                <div>
                    <div class="sensor-name">${sensor.name}</div>
                    <div class="sensor-type">${sensor.type}</div>
                </div>
            </div>
            <div class="sensor-info">
                <div class="sensor-info-row">
                    <span class="sensor-info-label">Ubicación:</span>
                    <span class="sensor-value">${sensor.location || 'N/A'}</span>
                </div>
                <div class="sensor-info-row">
                    <span class="sensor-info-label">Valor:</span>
                    <span class="sensor-value">${sensor.sensorValue || '0'}</span>
                </div>
                <div class="sensor-info-row">
                    <span class="sensor-info-label">Última Actualización:</span>
                    <span class="sensor-value">${formatDate(sensor.lastUpdate || new Date())}</span>
                </div>
            </div>
            <div class="sensor-status ${sensor.status === 'ACTIVE' ? 'active' : 'inactive'}">
                ${sensor.status === 'ACTIVE' ? 'Activo' : 'Inactivo'}
            </div>
        </div>
        `)
        .join('');
}

/**
 * Carga la lista de alertas
 */
async function loadAlerts() {
    const levelFilter = document.getElementById('alertFilterLevel').value;

    let alerts = await apiCall('/alerts');

    if (!alerts || !Array.isArray(alerts)) {
        alerts = [];
    }

    if (levelFilter) {
        alerts = alerts.filter(a => a.level === levelFilter);
    }

    const alertsTableBody = document.getElementById('alertsTableBody');

    if (alerts.length === 0) {
        alertsTableBody.innerHTML = '<tr><td colspan="6" class="placeholder">No hay alertas</td></tr>';
        return;
    }

    alertsTableBody.innerHTML = alerts
        .map((alert, index) => `
        <tr>
            <td>#${index + 1}</td>
            <td>Sistema</td>
            <td>
                <span class="alert-level ${(alert.level || 'INFO').toLowerCase()}">
                    ${alert.level || 'INFO'}
                </span>
            </td>
            <td>${alert.message || 'Sin mensaje'}</td>
            <td>${formatDate(alert.timestamp || new Date())}</td>
            <td>
                <span>Activa</span>
            </td>
        </tr>
        `)
        .join('');
}

/**
 * Reconoce una alerta
 */
async function acknowledgeAlert(alertId) {
    const result = await apiCall(`/alerts/${alertId}/acknowledge`, 'PUT');
    if (result) {
        loadAlerts();
        loadAlertStats();
    }
}

async function markNotificationAsRead(notificationId) {
    const result = await apiCall(`/notifications/${notificationId}/read`, 'PUT');

    if (result) {
        showNotification('Alerta marcada como leída', 'success');
        loadAlerts();
        loadAlertStats();
    } else {
        showNotification('Error al marcar la alerta', 'error');
    }
}

/**
 * Filtra alertas por nivel
 */
function filterAlerts() {
    loadAlerts();
}

/**
 * Carga los logs de acceso
 */
async function loadAccessLogs() {
    let data = await apiCall('/access');

    if (!data) {
        data = [];
    }

    const accessLogsBody = document.getElementById('accessLogsBody');
    const logs = data.content || data;

    if (!Array.isArray(logs) || logs.length === 0) {
        accessLogsBody.innerHTML = '<tr><td colspan="5" class="placeholder">No hay logs de acceso</td></tr>';
        return;
    }

    accessLogsBody.innerHTML = logs
        .map(log => `
        <tr>
            <td>${log.username}</td>
            <td>${log.sensorId || 'N/A'}</td>
            <td>
                <span class="access-${log.granted ? 'granted' : 'denied'}">
                    ${log.granted ? 'Permitido' : 'Denegado'}
                </span>
            </td>
            <td>${log.ipAddress || 'N/A'}</td>
            <td>${formatDate(log.timestamp)}</td>
        </tr>
        `)
        .join('');
}

/**
 * Maneja el envío del formulario de nuevo sensor
 */
document.addEventListener('DOMContentLoaded', function () {
    const sensorForm = document.getElementById('sensorForm');
    if (sensorForm) {
        sensorForm.addEventListener('submit', async function (event) {
            event.preventDefault();

            const sensor = {
                name: document.getElementById('sensorName').value,
                type: document.getElementById('sensorType').value,
                location: document.getElementById('sensorLocation').value,
            };

            const result = await apiCall('/sensors', 'POST', sensor);
            if (result) {
                closeSensorModal();
                sensorForm.reset();
                loadSensors();
                loadSensorStats();
                showNotification('Sensor creado exitosamente', 'success');
            } else {
                showNotification('Error al crear el sensor', 'error');
            }
        });
    }
});

document.addEventListener('DOMContentLoaded', function () {
    const alertForm = document.getElementById('alertForm');

    if (alertForm) {
        alertForm.addEventListener('submit', async function (event) {
            event.preventDefault();

            const alert = {
                message: document.getElementById('alertMessage').value,
                recipient: document.getElementById('alertRecipient').value,
                email: document.getElementById('alertEmail').value
            };

            const result = await apiCall('/alerts', 'POST', alert);

            if (result) {
                alertForm.reset();
                document.getElementById('alertRecipient').value = 'tony';

                showNotification('Alerta creada y notificación enviada', 'success');

                loadAlerts();
                loadAlertStats();
            } else {
                showNotification('Error al crear la alerta', 'error');
            }
        });
    }
});

/**
 * Recarga periódica de datos (cada 30 segundos)
 * Solo se ejecuta cuando el usuario está logged in
 */
let dashboardRefreshInterval = null;

function startDashboardRefresh() {
    if (!dashboardRefreshInterval && currentUser) {
        dashboardRefreshInterval = setInterval(() => {
            if (currentUser) {
                loadSensorStats();
                loadAlertStats();
            }
        }, 30000);
    }
}

function stopDashboardRefresh() {
    if (dashboardRefreshInterval) {
        clearInterval(dashboardRefreshInterval);
        dashboardRefreshInterval = null;
    }
}

async function simulateAlert() {
    const result = await apiCall('/alerts/simulate', 'POST');

    if (result) {
        showNotification('Alerta simulada creada', 'success');
        loadAlerts();
        loadAlertStats();
        loadRealtimeFeed();
    } else {
        showNotification('Error al simular alerta', 'error');
    }
}

async function simulateAccess() {
    const result = await apiCall('/access/simulate', 'POST');

    if (result) {
        showNotification('Acceso simulado creado', 'success');
        loadAccessLogs();
    } else {
        showNotification('Error al simular acceso', 'error');
    }
}

async function loadRealtimeFeed() {
    const data = await apiCall('/alerts');
    const feed = document.getElementById('realtimeFeed');

    if (!feed) return;

    if (!data || !Array.isArray(data) || data.length === 0) {
        feed.innerHTML = '<p class="placeholder">Esperando eventos...</p>';
        return;
    }

    const latest = data.slice(-5).reverse();

    feed.innerHTML = latest.map(alert => `
        <div class="feed-item">
            <strong>${alert.level || 'INFO'}</strong>
            <span>${alert.message || 'Alerta sin mensaje'}</span>
            <small>${formatDate(alert.timestamp || new Date())}</small>
        </div>
    `).join('');
}

let simulationInterval = null;

function startAutoSimulation() {
    if (simulationInterval) return;

    simulationInterval = setInterval(async () => {
        await apiCall('/alerts/simulate', 'POST');
        await apiCall('/access/simulate', 'POST');

        await loadAlerts();
        await loadAccessLogs();
        await loadAlertStats();
        await loadRealtimeFeed();
    }, 10000); // cada 10 segundos
}

function stopAutoSimulation() {
    if (simulationInterval) {
        clearInterval(simulationInterval);
        simulationInterval = null;
    }
}