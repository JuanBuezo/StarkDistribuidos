# Instrucciones Paso a Paso para Iniciar el Sistema

## ⚠️ **IMPORTANTE**: El orden es crítico

### Paso 1: Iniciar Eureka Server (PRIMERO)
```powershell
cd "C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos\starkDistribuidos-config"
..\mvnw.cmd spring-boot:run
```

**Espera hasta ver:**
```
[] Started ConfigServerApplication in X.XXX seconds
Eureka Server is running on http://localhost:8761
```

### Paso 2: Espera 10-15 segundos y abre Eureka en el navegador
```
http://localhost:8761
```
Deberías ver "Instances currently registered with Eureka: No instances available" (eso es normal por ahora)

### Paso 3: En OTRA terminal, inicia el API Gateway
```powershell
cd "C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos\starkDistribuidos-gateway"
..\mvnw.cmd spring-boot:run
```

### Paso 4: En OTRA terminal, inicia stark-access
```powershell
cd "C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos\starkDistribuidos-access"
..\mvnw.cmd spring-boot:run
```

### Paso 5: Inicia los demás servicios (en nuevas terminales, según necesites)

**Sensor Service:**
```powershell
cd "C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos\starkDistribuidos-sensor"
..\mvnw.cmd spring-boot:run
```

**Alert Service:**
```powershell
cd "C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos\starkDistribuidos-alert"
..\mvnw.cmd spring-boot:run
```

**Auth Service:**
```powershell
cd "C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos\starkDistribuidos-auth"
..\mvnw.cmd spring-boot:run
```

**Notification Service:**
```powershell
cd "C:\Users\andre\Desktop\Universidad\ICAI\4\Cuatri2\DS\Distributed\StarkDistribuidos\starkDistribuidos-notification"
..\mvnw.cmd spring-boot:run
```

## ✅ Verificación

Después de iniciar todos los servicios, ve a Eureka y verifica:
- http://localhost:8761
- Deberías ver todos los servicios registrados:
  - **STARK-GATEWAY**
  - **STARK-ACCESS**
  - **STARK-SENSOR**
  - **STARK-ALERT**
  - **STARK-AUTH**
  - **STARK-NOTIFICATION**

## 🔑 Credenciales de Eureka (si se requieren)
- **Usuario:** admin
- **Contraseña:** admin123

## 📝 Qué cambió:

Se agregaron las credenciales de Eureka a todas las configuraciones YAML:
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://admin:admin123@localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
```

Esto permite que cada servicio se autentique correctamente con Eureka Server y se registre sin errores 401.

