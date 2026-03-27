// ============================
// AUTENTICACIÓN - LOGIN Y REGISTRO
// ============================

/**
 * Maneja el login del usuario
 */
document.addEventListener('DOMContentLoaded', function () {
    const loginFormElement = document.getElementById('loginFormElement');
    if (loginFormElement) {
        loginFormElement.addEventListener('submit', handleLogin);
    }

    const registerFormElement = document.getElementById('registerFormElement');
    if (registerFormElement) {
        registerFormElement.addEventListener('submit', handleRegister);
    }
});

/**
 * Realiza el login
 */
async function handleLogin(event) {
    event.preventDefault();

    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    const errorDiv = document.getElementById('loginError');
    const submitBtn = event.target.querySelector('button[type="submit"]');
    const loader = submitBtn.querySelector('.loader');

    // Mostrar loader
    submitBtn.querySelector('span:first-child').style.display = 'none';
    loader.style.display = 'block';
    errorDiv.textContent = '';

    try {
        // Verificar credenciales llamando a un endpoint de verificación
        const response = await fetch('/stark-security/api/system/health', {
            method: 'GET',
            headers: {
                'Authorization': 'Basic ' + encodeBasicAuth(username, password),
            },
        });

        // Ocultar loader
        submitBtn.querySelector('span:first-child').style.display = 'inline';
        loader.style.display = 'none';

        if (response.ok) {
            // Login exitoso
            const token = encodeBasicAuth(username, password);
            authToken = token;
            currentUser = {
                username: username,
                roles: ['USER'], // Esto se podría obtener de un endpoint
            };

            // Guardar en localStorage
            localStorage.setItem('authToken', token);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));

            // Mostrar dashboard
            showDashboard();
        } else {
            errorDiv.textContent = '❌ Usuario o contraseña inválidos';
        }
    } catch (error) {
        // Ocultar loader
        submitBtn.querySelector('span:first-child').style.display = 'inline';
        loader.style.display = 'none';
        errorDiv.textContent = '⚠️ Error de conexión: ' + error.message;
        console.error('Login error:', error);
    }
}

/**
 * Realiza el registro de un nuevo usuario
 */
async function handleRegister(event) {
    event.preventDefault();

    const username = document.getElementById('regUsername').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    const passwordConfirm = document.getElementById('regPasswordConfirm').value;
    const errorDiv = document.getElementById('registerError');
    const submitBtn = event.target.querySelector('button[type="submit"]');
    const loader = submitBtn.querySelector('.loader');

    errorDiv.textContent = '';

    // Validaciones
    if (password !== passwordConfirm) {
        errorDiv.textContent = '❌ Las contraseñas no coinciden';
        return;
    }

    if (password.length < 8) {
        errorDiv.textContent = '❌ La contraseña debe tener al menos 8 caracteres';
        return;
    }

    // Mostrar loader
    submitBtn.querySelector('span:first-child').style.display = 'none';
    loader.style.display = 'block';

    try {
        // Aquí iría la llamada al endpoint de registro
        // Por ahora, simularemos un registro exitoso
        // En producción, esto debería llamar a un servicio de autenticación real

        // Simulamos que el registro es exitoso después de 1 segundo
        await new Promise(resolve => setTimeout(resolve, 1000));

        // Después del registro, hacer login automático
        document.getElementById('regUsername').value = username;
        document.getElementById('regPassword').value = password;

        // Mostrar mensaje de éxito
        errorDiv.style.color = 'var(--success-color)';
        errorDiv.textContent = '✅ Registro exitoso. Iniciando sesión...';

        // Esperar y luego hacer login
        setTimeout(() => {
            // Cambiar a login y hacer login automático
            const loginForm = document.getElementById('loginForm');
            const registerForm = document.getElementById('registerForm');

            loginForm.classList.add('active');
            registerForm.classList.remove('active');

            // Llenar datos de login
            document.getElementById('loginUsername').value = username;
            document.getElementById('loginPassword').value = password;

            // Hacer login
            const loginEvent = new Event('submit');
            document.getElementById('loginFormElement').dispatchEvent(loginEvent);
        }, 1500);
    } catch (error) {
        // Ocultar loader
        submitBtn.querySelector('span:first-child').style.display = 'inline';
        loader.style.display = 'none';
        errorDiv.textContent = '⚠️ Error en el registro: ' + error.message;
        console.error('Register error:', error);
    }
}

/**
 * Validación de email
 */
function isValidEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

