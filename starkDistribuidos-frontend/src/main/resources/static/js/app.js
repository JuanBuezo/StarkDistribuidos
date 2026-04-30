// ============================
// CONFIGURACIÓN GLOBAL
// ============================

const API_BASE_URL = 'http://localhost:8080/api';
const AUTH_HEADER = 'Authorization';

let currentUser = null;
let authToken = null;
let stompClient = null;
let charts = {};

// ============================
// UTILIDADES
// ============================

/**
 * Realiza una solicitud HTTP con autenticación
 */
async function apiCall(endpoint, method = 'GET', body = null) {
    const headers = {
        'Content-Type': 'application/json',
    };

    // Obtener el token (credenciales en Base64)
    const token = localStorage.getItem('authToken');
    if (token) {
        headers['Authorization'] = 'Basic ' + token;
    }

    const options = {
        method,
        headers,
        credentials: 'include', // Incluir cookies si existen
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(API_BASE_URL + endpoint, options);

        if (response.status === 401) {
            // Token inválido o expirado - mostrar login pero SIN logout automático
            console.warn('401 Unauthorized en ' + endpoint);
            return null;
        }

        if (!response.ok) {
            console.error(`HTTP Error: ${response.status} en ${endpoint}`);
            return null;
        }

        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }
        return null;
    } catch (error) {
        console.error('API Error:', error);
        return null;
    }
}

/**
 * Codifica las credenciales a Base64 para Basic Auth
 */
function encodeBasicAuth(username, password) {
    return btoa(`${username}:${password}`);
}

/**
 * Decodifica Basic Auth
 */
function decodeBasicAuth(token) {
    const decoded = atob(token);
    const [username, password] = decoded.split(':');
    return { username, password };
}

/**
 * Formatea fechas
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('es-ES', {
        year: 'numeric',
        month: 'short',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
    });
}

/**
 * Alterna entre formularios
 */
function toggleForm(event) {
    event.preventDefault();
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');

    loginForm.classList.toggle('active');
    registerForm.classList.toggle('active');
}

/**
 * Cambia de pestaña en el dashboard
 */
function switchTab(tabName, event) {
    if (event) {
        event.preventDefault();
    }

    // Ocultar todas las pestañas
    const tabs = document.querySelectorAll('.tab-content');
    tabs.forEach(tab => tab.classList.remove('active'));

    // Desactivar todos los enlaces de navegación
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => item.classList.remove('active'));

    // Mostrar la pestaña seleccionada
    const selectedTab = document.getElementById(tabName + 'Tab');
    if (selectedTab) {
        selectedTab.classList.add('active');
    }

    // Activar el enlace de navegación
    if (event && event.target) {
        event.target.classList.add('active');
    }

    // Cargar datos según la pestaña
    if (tabName === 'sensors') {
        loadSensors();
    } else if (tabName === 'alerts') {
        loadAlerts();
    } else if (tabName === 'access') {
        loadAccessLogs();
    }
}

/**
 * Muestra un mensaje de error temporal
 */
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');

    notification.textContent = message;
    notification.style.position = 'fixed';
    notification.style.bottom = '20px';
    notification.style.right = '20px';
    notification.style.padding = '10px 20px';
    notification.style.background = type === 'error' ? '#e74c3c' : '#2ecc71';
    notification.style.color = 'white';
    notification.style.borderRadius = '5px';
    notification.style.zIndex = '9999';
    notification.style.fontSize = '14px';

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.remove();
    }, 2000);
}

/**
 * Obtiene parámetro de URL
 */
function getQueryParam(param) {
    const searchParams = new URLSearchParams(window.location.search);
    return searchParams.get(param);
}

/**
 * Abre el modal de crear sensor
 */
function openSensorModal() {
    document.getElementById('sensorModal').classList.add('show');
}

/**
 * Cierra el modal de crear sensor
 */
function closeSensorModal() {
    document.getElementById('sensorModal').classList.remove('show');
}

// Cerrar modal si se hace clic fuera
window.onclick = function (event) {
    const modal = document.getElementById('sensorModal');
    if (event.target == modal) {
        modal.classList.remove('show');
    }
}

/**
 * Inicializa la aplicación
 */
document.addEventListener('DOMContentLoaded', function () {
    // Verificar si hay sesión guardada
    const savedAuth = localStorage.getItem('authToken');
    const savedUser = localStorage.getItem('currentUser');

    if (savedAuth && savedUser) {
        authToken = savedAuth;
        currentUser = JSON.parse(savedUser);
        showDashboard();
    } else {
        showAuthContainer();
    }
});

/**
 * Muestra el contenedor de autenticación
 */
function showAuthContainer() {
    document.getElementById('authContainer').style.display = 'flex';
    document.getElementById('dashboardContainer').style.display = 'none';
}

/**
 * Muestra el dashboard
 */
function showDashboard() {
    document.getElementById('authContainer').style.display = 'none';
    document.getElementById('dashboardContainer').style.display = 'flex';
    document.getElementById('userDisplay').textContent = currentUser.username;

    // Cargar datos iniciales
    loadDashboardData();
    // connectWebSocket();

    // Solicitar permisos y iniciar reconexión solo después de autenticarse
    if (typeof requestNotificationPermission === 'function') {
        requestNotificationPermission();
    }
    // if (typeof startReconnectInterval === 'function') {
    //     startReconnectInterval();
    // }
    if (typeof startDashboardRefresh === 'function') {
        startDashboardRefresh();
    }
    if (typeof startAutoSimulation === 'function') {
        startAutoSimulation();
    }
}

/**
 * Logout del usuario
 */
function logout() {
    authToken = null;
    currentUser = null;
    localStorage.removeItem('authToken');
    localStorage.removeItem('username');
    localStorage.removeItem('password');
    localStorage.removeItem('currentUser');
    disconnectWebSocket();

    // Detener intervalos
    if (typeof stopReconnectInterval === 'function') {
        stopReconnectInterval();
    }
    if (typeof stopDashboardRefresh === 'function') {
        stopDashboardRefresh();
    }

    showAuthContainer();
    document.getElementById('loginForm').classList.add('active');
    document.getElementById('registerForm').classList.remove('active');
    // Limpiar formularios
    document.getElementById('loginFormElement').reset();
    document.getElementById('registerFormElement').reset();
}

// Event listener para logout
document.addEventListener('DOMContentLoaded', function () {
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    }
});

