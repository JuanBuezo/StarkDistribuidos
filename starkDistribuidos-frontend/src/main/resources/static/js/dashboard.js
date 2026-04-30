// ============================
// DASHBOARD - DATOS Y GRÁFICOS
// ============================

/**
 * Carga los datos iniciales del dashboard
 */
async function loadDashboardData() {
    try {
        await initializeCharts();
        await loadAllData();
        startRealtimeFeedRefresh();
    } catch (error) {
        console.error('Error cargando datos del dashboard:', error);
        // Mostrar placeholders o mensajes de error
        showNotification('Error cargando datos iniciales', 'error');
    }
}

/**
 * Carga todos los datos en paralelo
 */
async function loadAllData() {
    try {
        // Ejecutar todas las cargas iniciales en paralelo.
        await Promise.all([
            loadAlerts(),
            loadAccessLogs(),
            loadAlertStats(),
            loadSensors(),
            loadSensorStats(),
            loadRealtimeFeed()
        ]);
    } catch (error) {
        console.error('Error cargando todos los datos:', error);
        throw error; // Re-lanzar para que loadDashboardData lo maneje
    }
}

/**
 * Inicia el auto-refresh cada 5 segundos
 */
let autoRefreshInterval = null;

function startAutoRefresh() {
    if (autoRefreshInterval || !currentUser) return;

    autoRefreshInterval = setInterval(async () => {
        try {
            // Solo refrescar datos críticos cada 5 segundos
            await Promise.all([
                loadAlerts(),
                loadAccessLogs(),
                loadAlertStats()
            ]);
        } catch (error) {
            console.error('Error en auto-refresh:', error);
        }
    }, 5000); // cada 5 segundos
}

function stopAutoRefresh() {
    if (autoRefreshInterval) {
        clearInterval(autoRefreshInterval);
        autoRefreshInterval = null;
    }
}

/**
 * Carga estadísticas de sensores
 */
async function loadSensorStats() {
    try {
        const data = await apiCall('/sensors');
        if (data && Array.isArray(data)) {
            const activeSensors = data.filter(s => s.status === 'ACTIVE').length;
            document.getElementById('activeSensorsCount').textContent = data.length;
            updateSensorChart(data);
        } else {
            document.getElementById('activeSensorsCount').textContent = '0';
        }
    } catch (error) {
        console.error('Error cargando estadísticas de sensores:', error);
        document.getElementById('activeSensorsCount').textContent = '0';
    }
}

/**
 * Carga estadísticas de alertas
 */
async function loadAlertStats() {
    try {
        const data = await apiCall('/alerts');
        if (data && Array.isArray(data)) {
            const activeAlerts = data.filter(a => !a.acknowledged).length;
            const criticalAlerts = data.filter(a => a.level === 'CRITICAL' && !a.acknowledged).length;

            document.getElementById('activeAlertsCount').textContent = activeAlerts;
            document.getElementById('criticalAlertsCount').textContent = criticalAlerts;

            updateAlertChart(data);
        } else {
            document.getElementById('activeAlertsCount').textContent = '0';
            document.getElementById('criticalAlertsCount').textContent = '0';
        }
    } catch (error) {
        console.error('Error cargando estadísticas de alertas:', error);
        document.getElementById('activeAlertsCount').textContent = '0';
        document.getElementById('criticalAlertsCount').textContent = '0';
    }
}

/**
 * Inicializa los gráficos
 */
let selectedSensors = [];
let chartsInitialized = false;

async function initializeCharts() {
    if (chartsInitialized) {
        return;
    }

    // Crear selector de sensores primero
    await createSensorSelector();

    // Gráfico de sensores
    const sensorCtx = document.getElementById('sensorChart');
    if (sensorCtx) {
        charts.sensor = new Chart(sensorCtx, {
            type: 'line',
            data: {
                labels: [],
                datasets: [],
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

    chartsInitialized = true;
}

/**
 * Actualiza el gráfico de sensores con datos coherentes
 */
function updateSensorChart(sensors) {
    if (!charts.sensor) return;

    // Si no hay sensores seleccionados, seleccionar los primeros 3
    if (selectedSensors.length === 0 && sensors.length > 0) {
        selectedSensors = sensors.slice(0, Math.min(3, sensors.length)).map(s => s.id || s.name);
    }

    // Generar datos coherentes por sensor
    const hours = [];
    for (let i = 23; i >= 0; i--) {
        const hour = new Date();
        hour.setHours(hour.getHours() - i);
        hours.push(hour.getHours() + ':00');
    }

    const datasets = [];
    const colors = [
        { border: '#FF6B35', bg: 'rgba(255, 107, 53, 0.1)' },
        { border: '#3498DB', bg: 'rgba(52, 152, 219, 0.1)' },
        { border: '#2ECC71', bg: 'rgba(46, 204, 113, 0.1)' },
        { border: '#F39C12', bg: 'rgba(243, 156, 18, 0.1)' },
        { border: '#E74C3C', bg: 'rgba(231, 76, 60, 0.1)' },
    ];

    // Crear datasets para cada sensor seleccionado
    selectedSensors.forEach((sensorId, index) => {
        const sensor = sensors.find(s => s.id === sensorId || s.name === sensorId);
        if (!sensor) return;

        const sensorData = [];
        const seed = sensor.id || sensor.name.charCodeAt(0);

        // Generar datos coherentes y progresivos
        let baseValue = 50;
        for (let i = 0; i < 24; i++) {
            // Variación gradual y coherente según tipo de sensor
            let variation = (Math.sin((i + seed) / 3) * 20) + (Math.random() * 10);
            sensorData.push(Math.max(0, Math.min(100, baseValue + variation)));
        }

        const color = colors[index % colors.length];
        datasets.push({
            label: sensor.name || sensor.type,
            data: sensorData,
            borderColor: color.border,
            backgroundColor: color.bg,
            tension: 0.3,
            fill: true,
        });
    });

    charts.sensor.data.labels = hours;
    charts.sensor.data.datasets = datasets;
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
    try {
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
    } catch (error) {
        console.error('Error cargando sensores:', error);
        const sensorsGrid = document.getElementById('sensorsGrid');
        sensorsGrid.innerHTML = '<p class="placeholder">Error al cargar sensores</p>';
    }
}

/**
 * Carga la lista de alertas
 */
async function loadAlerts() {
    try {
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
    } catch (error) {
        console.error('Error cargando alertas:', error);
        const alertsTableBody = document.getElementById('alertsTableBody');
        alertsTableBody.innerHTML = '<tr><td colspan="6" class="placeholder">Error al cargar alertas</td></tr>';
    }
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
    try {
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
    } catch (error) {
        console.error('Error cargando logs de acceso:', error);
        const accessLogsBody = document.getElementById('accessLogsBody');
        accessLogsBody.innerHTML = '<tr><td colspan="5" class="placeholder">Error al cargar logs</td></tr>';
    }
}

async function loadRealtimeFeed() {
    try {
        const data = await apiCall('/alerts');
        const feed = document.getElementById('realtimeFeed');

        if (!feed) return;

        if (!data || !Array.isArray(data) || data.length === 0) {
            feed.innerHTML = '<p class="placeholder">Esperando eventos...</p>';
            return;
        }

        const latest = data.slice(-10).reverse();

        feed.innerHTML = latest.map((alert, idx) => {
            const levelClass = (alert.level || 'INFO').toLowerCase();
            const levelColor = levelClass === 'critical' ? '#E74C3C' : levelClass === 'warning' ? '#F39C12' : '#3498DB';

            return `
                <div class="feed-item" style="
                    padding: 10px 12px;
                    border-left: 3px solid ${levelColor};
                    background: rgba(0,0,0,0.2);
                    border-radius: 4px;
                    margin-bottom: 8px;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                ">
                    <div style="flex: 1;">
                        <div style="display: flex; gap: 10px; align-items: center;">
                            <span style="
                                background: ${levelColor};
                                color: white;
                                padding: 2px 8px;
                                border-radius: 3px;
                                font-size: 11px;
                                font-weight: bold;
                                min-width: 60px;
                                text-align: center;
                            ">${alert.level || 'INFO'}</span>
                            <span style="color: #ECF0F1; font-size: 13px; flex: 1;">${alert.message || 'Alerta sin mensaje'}</span>
                        </div>
                        <small style="color: #95A5A6; font-size: 11px; margin-top: 4px; display: block;">${formatDate(alert.timestamp || new Date())}</small>
                    </div>
                </div>
            `;
        }).join('');
    } catch (error) {
        console.error('Error cargando feed en tiempo real:', error);
        const feed = document.getElementById('realtimeFeed');
        if (feed) {
            feed.innerHTML = '<p class="placeholder">Error al cargar feed</p>';
        }
    }
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

let simulationInterval = null;
let isAutoSimulationActive = false;

function startAutoSimulation() {
    if (simulationInterval) return;

    isAutoSimulationActive = true;
    updateAutoSimBtn();

    simulationInterval = setInterval(async () => {
        await apiCall('/alerts/simulate', 'POST');
        await apiCall('/access/simulate', 'POST');

        await loadAlerts();
        await loadAccessLogs();
        await loadAlertStats();
        await loadRealtimeFeed();
    }, 3000);
}

function stopAutoSimulation() {
    if (simulationInterval) {
        clearInterval(simulationInterval);
        simulationInterval = null;
    }
    isAutoSimulationActive = false;
    updateAutoSimBtn();
}

/**
 * Alterna la simulación automática
 */
function toggleAutoSimulation() {
    if (isAutoSimulationActive) {
        stopAutoSimulation();
    } else {
        startAutoSimulation();
    }
}

/**
 * Actualiza el estado visual del botón de simulación
 */
function updateAutoSimBtn() {
    const btn = document.getElementById('autoSimBtn');
    const icon = document.getElementById('autoSimIcon');
    const text = document.getElementById('autoSimText');

    if (!btn) return;

    if (isAutoSimulationActive) {
        btn.style.background = '#E74C3C';
        btn.style.borderColor = '#C0392B';
        icon.textContent = '⏸';
        text.textContent = 'Detener Demo';
    } else {
        btn.style.background = '';
        btn.style.borderColor = '';
        icon.textContent = '▶';
        text.textContent = 'Iniciar Demo';
    }
}

// ============================
// MEJORAS: SELECTOR DE SENSORES
// ============================

/**
 * Crea el selector de sensores en la gráfica
 */
async function createSensorSelector() {
    try {
        const sensors = await apiCall('/sensors');
        if (!sensors || !Array.isArray(sensors) || sensors.length === 0) {
            return;
        }

        // Buscar el contenedor de la gráfica
        const chartContainer = document.querySelector('.chart-container');
        if (!chartContainer) return;

        // Crear selector si no existe
        let selector = chartContainer.querySelector('#sensorSelector');
        if (!selector) {
            selector = document.createElement('div');
            selector.id = 'sensorSelector';
            selector.style.marginBottom = '12px';
            selector.style.display = 'flex';
            selector.style.gap = '8px';
            selector.style.flexWrap = 'wrap';
            chartContainer.insertBefore(selector, chartContainer.firstChild);
        }

        selector.innerHTML = '';
        sensors.forEach(sensor => {
            const btn = document.createElement('button');
            btn.className = 'sensor-btn';
            btn.textContent = sensor.name || sensor.type;
            btn.style.padding = '6px 12px';
            btn.style.border = '1px solid #3498DB';
            btn.style.borderRadius = '4px';
            btn.style.background = '#2C3E50';
            btn.style.color = '#ECF0F1';
            btn.style.cursor = 'pointer';
            btn.style.fontSize = '12px';
            btn.style.transition = 'all 0.3s';

            const sensorId = sensor.id || sensor.name;
            const isSelected = selectedSensors.includes(sensorId);
            if (isSelected) {
                btn.style.background = '#3498DB';
                btn.style.color = 'white';
                btn.style.fontWeight = 'bold';
            }

            btn.onclick = () => toggleSensorSelection(sensorId, sensors);
            selector.appendChild(btn);
        });
    } catch (error) {
        console.error('Error creando selector de sensores:', error);
    }
}

/**
 * Alterna la selección de un sensor
 */
function toggleSensorSelection(sensorId, sensors) {
    const index = selectedSensors.indexOf(sensorId);
    if (index > -1) {
        selectedSensors.splice(index, 1);
    } else {
        selectedSensors.push(sensorId);
    }

    // Actualizar gráfica
    updateSensorChart(sensors);

    // Recrear selector para mostrar estado
    createSensorSelector();
}

// ============================
// MEJORAS: FEED EN TIEMPO REAL
// ============================

let realtimeFeedInterval = null;

/**
 * Inicia actualización automática del feed cada 10 segundos
 */
function startRealtimeFeedRefresh() {
    if (realtimeFeedInterval) return;

    realtimeFeedInterval = setInterval(() => {
        loadRealtimeFeed();
    }, 10000); // cada 10 segundos
}

/**
 * Detiene la actualización del feed
 */
function stopRealtimeFeedRefresh() {
    if (realtimeFeedInterval) {
        clearInterval(realtimeFeedInterval);
        realtimeFeedInterval = null;
    }
}

/**
 * Genera un evento único (simula alerta y acceso)
 */
async function generateEvent() {
    try {
        await apiCall('/alerts/simulate', 'POST');
        await apiCall('/access/simulate', 'POST');

        // Actualizar todas las vistas
        await loadAlerts();
        await loadAccessLogs();
        await loadAlertStats();
        await loadRealtimeFeed();

        showNotification('Evento generado exitosamente', 'success');
    } catch (error) {
        console.error('Error al generar evento:', error);
        showNotification('Error al generar evento', 'error');
    }
}

/**
 * Limpia todos los datos de alertas y accesos
 */
async function cleanData() {
    // Confirmación antes de borrar
    if (!confirm('¿Seguro que quieres limpiar alertas y accesos? Esta acción no se puede deshacer.')) {
        return;
    }

    try {
        // Limpiar alertas y accesos llamando a DELETE
        await apiCall('/alerts', 'DELETE');
        await apiCall('/access', 'DELETE');

        // Actualizar todas las vistas
        await loadAlerts();
        await loadAccessLogs();
        await loadAlertStats();
        await loadRealtimeFeed();

        showNotification('Datos limpios exitosamente', 'success');
    } catch (error) {
        console.error('Error al limpiar datos:', error);
        showNotification('Error al limpiar datos', 'error');
    }
}
