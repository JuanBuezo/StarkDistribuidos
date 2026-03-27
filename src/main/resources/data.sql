-- ========================================
-- DATOS DE EJEMPLO PARA EL SISTEMA
-- ========================================

-- Insertar sensores de ejemplo
INSERT INTO SENSOR (ID, NAME, TYPE, LOCATION, VALUE, STATUS, LAST_UPDATE) VALUES
(1, 'Sensor Movimiento Entrada', 'MOTION', 'Entrada Principal', 0, 'ACTIVE', CURRENT_TIMESTAMP),
(2, 'Sensor Temperatura Hall', 'TEMPERATURE', 'Hall Central', 22.5, 'ACTIVE', CURRENT_TIMESTAMP),
(3, 'Sensor Acceso Puerta 1', 'ACCESS', 'Puerta Laboratorio', 1, 'ACTIVE', CURRENT_TIMESTAMP),
(4, 'Sensor Temperatura Servidor', 'TEMPERATURE', 'Sala Servidores', 18.3, 'ACTIVE', CURRENT_TIMESTAMP),
(5, 'Sensor Movimiento Pasillo', 'MOTION', 'Pasillo 2', 1, 'ACTIVE', CURRENT_TIMESTAMP);

-- Insertar alertas de ejemplo
INSERT INTO ALERT (ID, SENSOR_ID, MESSAGE, LEVEL, ACKNOWLEDGED, TIMESTAMP) VALUES
(1, 1, 'Movimiento detectado en entrada principal', 'WARNING', false, CURRENT_TIMESTAMP),
(2, 4, 'Temperatura de servidores por encima del umbral', 'CRITICAL', false, CURRENT_TIMESTAMP),
(3, 2, 'Temperatura dentro de rangos normales', 'INFO', true, CURRENT_TIMESTAMP),
(4, 3, 'Intento de acceso detectado', 'WARNING', false, CURRENT_TIMESTAMP);

-- Insertar logs de acceso de ejemplo
INSERT INTO ACCESS_LOG (ID, USERNAME, SENSOR_ID, GRANTED, IP_ADDRESS, TIMESTAMP) VALUES
(1, 'admin', 3, true, '192.168.1.100', CURRENT_TIMESTAMP),
(2, 'security', 1, true, '192.168.1.101', CURRENT_TIMESTAMP),
(3, 'unknown', 3, false, '203.0.113.45', CURRENT_TIMESTAMP),
(4, 'user', 1, true, '192.168.1.102', CURRENT_TIMESTAMP),
(5, 'admin', 4, true, '192.168.1.100', CURRENT_TIMESTAMP);

