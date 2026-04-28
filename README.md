# 🏗️ Stark Distribuidos - Aplicación Principal

**Sistema de Seguridad Distribuido con Microservicios**  
Versión: 0.0.1-SNAPSHOT | Java 17 | Spring Boot 3.2.5

---

## 📋 Descripción

Aplicación monolítica integrada que consolida la funcionalidad del sistema de seguridad distribuido Stark Distribuidos. Esta versión reorganiza el código de múltiples microsservicios en una aplicación única y ejecutable desde IntelliJ IDEA o línea de comandos.

---

## 🚀 Inicio Rápido

### Requisitos
- **Java 17+** (recomendado OpenJDK 17)
- **Maven 3.8+** (incluido: Maven Wrapper)
- **IntelliJ IDEA** (Community o Ultimate) - Opcional

### Compilar el proyecto

```bash
# Con Maven Wrapper (Windows)
mvnw clean compile

# Con Maven Wrapper (Mac/Linux)
./mvnw clean compile

# Con Maven instalado
maven clean compile
```

### Empaquetar

```bash
# Crear JAR ejecutable
mvnw clean package -DskipTests

# Crear JAR con tests
mvnw clean package
```

### Ejecutar desde Terminal

```bash
# Opción 1: Desde el JAR compilado
java -jar target/stark-distribuidos-0.0.1-SNAPSHOT.jar

# Opción 2: Desde Maven
mvnw spring-boot:run

# Opción 3: Crear ejecutable Windows
mvnw clean package
target\stark-distribuidos-0.0.1-SNAPSHOT.jar
```

### Ejecutar desde IntelliJ IDEA

1. **Abrir el Proyecto**
   - Archivo → Abrir
   - Seleccionar carpeta raíz del proyecto
   - Permitir que IntelliJ configure el proyecto Maven

2. **Ejecutar la Aplicación**
   - Navegar a: `src/main/java/com/distribuidos/stark/StarkDistribuidosApplication.java`
   - Click derecho → **Run StarkDistribuidosApplication**
   - O presionar: **Ctrl+Shift+F10** (Shift+Ctrl+F10 Windows/Linux, Cmd+Ctrl+R Mac)

3. **Debug**
   - Click derecho → **Debug StarkDistribuidosApplication**
   - O presionar: **Shift+Ctrl+F9** (Windows/Linux)

---

## 📊 Estructura del Proyecto

```
StarkDistribuidos/
├── .mvn/                              # Maven Wrapper configuration
├── src/
│   ├── main/
│   │   ├── java/com/distribuidos/stark/
│   │   │   ├── StarkDistribuidosApplication.java  (📍 Main entry point)
│   │   │   ├── config/               (Spring configurations)
│   │   │   ├── controller/           (REST endpoints)
│   │   │   ├── service/              (Business logic)
│   │   │   ├── entity/               (JPA entities)
│   │   │   ├── repository/           (Data access)
│   │   │   ├── agent/                (Agents)
│   │   │   └── orchestrator/         (Service orchestration)
│   │   └── resources/
│   │       ├── application.yaml      (Configuration)
│   │       ├── data.sql              (Initial data)
│   │       └── static/               (Frontend assets)
│   └── test/                         (Unit & integration tests)
├── scripts/                          (Helper scripts)
├── docs/
│   ├── ARCHITECTURE.md              (System architecture)
│   ├── SETUP.md                     (Detailed setup guide)
│   ├── POSTMAN_GUIA.md              (API testing guide)
│   └── archive/                     (Deprecated documentation)
├── _archived_microservices/         (Legacy microservice modules)
├── _archived_logs/                  (Build artifacts)
├── pom.xml                          (Maven configuration)
├── mvnw / mvnw.cmd                  (Maven Wrapper)
└── Dockerfile                       (Container configuration)
```

---

## ⚙️ Configuración

### application.yaml
- **Base de Datos**: H2 (en archivo: `./data/stark_db`)
- **Puerto**: 8080 (por defecto)
- **Security**: Spring Security activado
- **H2 Console**: Deshabilitado por defecto

```yaml
# Habilitar H2 Console (desarrollo)
export H2_CONSOLE_ENABLED=true
```

### Variables de Entorno Soportadas
- `H2_CONSOLE_ENABLED` - Habilitar/deshabilitar H2 Console (default: false)
- `SERVER_PORT` - Puerto de la aplicación (default: 8080)

---

## 📚 Documentación

- **[ARCHITECTURE.md](docs/ARCHITECTURE.md)** - Arquitectura del sistema
- **[SETUP.md](docs/SETUP.md)** - Guía detallada de configuración
- **[POSTMAN_GUIA.md](docs/POSTMAN_GUIA.md)** - Guía para probar APIs con Postman

Para documentación más antigua, ver: [docs/archive/](docs/archive/)

---

## 🛠️ Desarrollo

### Agregar nuevas dependencias

Editar `pom.xml` en la sección `<dependencies>`:

```xml
<dependency>
    <groupId>org.example</groupId>
    <artifactId>my-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

Luego recargar Maven en IntelliJ o ejecutar:
```bash
mvnw clean install
```

### Ejecutar pruebas

```bash
# Todas las pruebas
mvnw test

# Una clase de prueba específica
mvnw test -Dtest=AccessControlServiceTest

# Generar reporte de cobertura
mvnw test jacoco:report
```

---

## 🐳 Docker

### Construir imagen
```bash
docker build -t stark-distribuidos:latest .
```

### Ejecutar contenedor
```bash
docker run -p 8080:8080 stark-distribuidos:latest
```

---

## 📦 Archivados (Microservicios Heredados)

Los siguientes módulos fueron la base de esta aplicación y están archivados en `_archived_microservices/`:

- `starkDistribuidos-access` - Control de acceso
- `starkDistribuidos-alert` - Alertas
- `starkDistribuidos-auth` - Autenticación
- `starkDistribuidos-common` - Código común
- `starkDistribuidos-config` - Configuración centralizada
- `starkDistribuidos-frontend` - Frontend
- `starkDistribuidos-gateway` - API Gateway
- `starkDistribuidos-notification` - Notificaciones
- `starkDistribuidos-sensor` - Sensores

Si necesitas referencias de implementación antigua, consulta esos módulos.

---

## 🔒 Seguridad

✅ **Implementado:**
- H2 Database actualizado a v2.2.220 (sin vulnerabilidades CVE-2021-42392, CVE-2022-23221, CVE-2021-23463)
- Spring Security integrado
- Gestión de credenciales con variables de entorno

⚠️ **Consideraciones:**
- H2 Console deshabilitado por defecto en producción
- Utilizamos base de datos en archivo para persistencia

---

## 🐛 Troubleshooting

### Error: "Could not find or load main class"
```bash
# Asegurar compilación correcta
mvnw clean compile
mvnw package
```

### Port 8080 already in use
```bash
# Cambiar puerto
export SERVER_PORT=8081
mvnw spring-boot:run
```

### IntelliJ no reconoce cambios
```bash
# Invalidar cachés e reinicias IntelliJ
Archivo → Invalidar cachés → Invalidar y Reiniciar
```

---

## 📝 Changelog

### v0.0.1-SNAPSHOT
- ✅ Consolidación de aplicación desde microsservicios
- ✅ Reorganización de carpetas
- ✅ Actualización de H2 Database a v2.2.220
- ✅ Spring Boot 3.2.5, Java 17

---

## 📞 Contacto

Para preguntas o problemas, consultar:
- `docs/SETUP.md` - Configuración detallada
- `docs/TROUBLE_SHOOTING.md` - Guía de resolución de problemas (si existe)

---

**Última actualización**: 2026-04-28  
**Estado**: ✅ Operacional

