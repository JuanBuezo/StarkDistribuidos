# StarkDistribuidos - Sistema de Seguridad Distribuido

## Descripcion general

StarkDistribuidos es un sistema distribuido basado en microservicios para monitorizar sensores, accesos, alertas y notificaciones dentro de un entorno de seguridad. El proyecto esta desarrollado con Spring Boot y Spring Cloud, y utiliza Eureka para el registro y descubrimiento de servicios.

La aplicacion incluye un frontend web desde el que se puede iniciar sesion, visualizar el estado general del sistema, consultar sensores, generar eventos de seguridad, revisar alertas y analizar logs de acceso. Para la ejecucion local no es necesario Docker; el proyecto se puede arrancar mediante PowerShell usando el script incluido.

## Arquitectura

El sistema esta dividido en varios servicios independientes:

| Servicio | Modulo | Descripcion |
| --- | --- | --- |
| Config / Eureka Server | `starkDistribuidos-config` | Servidor Eureka encargado del registro y descubrimiento de microservicios. Debe arrancar antes que el resto de servicios. |
| Gateway | `starkDistribuidos-gateway` | Punto de entrada a la API. Redirige las peticiones hacia los microservicios correspondientes. |
| Auth | `starkDistribuidos-auth` | Gestiona autenticacion, login, registro y validacion de tokens. |
| Sensor | `starkDistribuidos-sensor` | Gestiona sensores de seguridad y sus valores. |
| Alert | `starkDistribuidos-alert` | Gestiona alertas de seguridad, simulacion de alertas e historico. |
| Access | `starkDistribuidos-access` | Gestiona logs de acceso, simulacion de accesos e historico. |
| Notification | `starkDistribuidos-notification` | Gestiona notificaciones asociadas a eventos y alertas. |
| Frontend | `starkDistribuidos-frontend` | Interfaz web para operar y visualizar el sistema. |

## Puertos

| Servicio | Puerto |
| --- | ---: |
| Eureka | 8761 |
| Gateway | 8080 |
| Auth | 8081 |
| Sensor | 8082 |
| Alert | 8083 |
| Access | 8084 |
| Frontend | 8085 |
| Notification | 8086 |

## Requisitos

- Java 17 o superior.
- Maven Wrapper incluido en el proyecto (`mvnw` / `mvnw.cmd`).
- PowerShell.
- Navegador web.
- No hace falta Docker para la ejecucion local.

## Como ejecutar

La forma recomendada de ejecutar el proyecto en local es usar el script:

```powershell
.\EJECUTAR_LOCAL.ps1
```

El script realiza las siguientes acciones:

1. Arranca primero el servicio `starkDistribuidos-config`, que contiene Eureka Server.
2. Espera unos segundos para que Eureka este disponible.
3. Arranca el resto de microservicios con el perfil `local`.
4. Muestra los logs de los servicios en la consola de PowerShell.

Una vez ejecutado el script, primero hay que comprobar en Eureka que todos los servicios aparecen levantados:

```text
http://localhost:8761/
```

Cuando los servicios esten registrados y en estado `UP`, se puede abrir la aplicacion web:

```text
http://localhost:8085/
```

### Ejecucion manual

Si el script falla, los servicios tambien se pueden arrancar manualmente, cada uno en una terminal distinta:

```powershell
.\mvnw -pl starkDistribuidos-config spring-boot:run "-Dspring-boot.run.profiles=local"
.\mvnw -pl starkDistribuidos-gateway spring-boot:run "-Dspring-boot.run.profiles=local"
.\mvnw -pl starkDistribuidos-auth spring-boot:run "-Dspring-boot.run.profiles=local"
.\mvnw -pl starkDistribuidos-sensor spring-boot:run "-Dspring-boot.run.profiles=local"
.\mvnw -pl starkDistribuidos-alert spring-boot:run "-Dspring-boot.run.profiles=local"
.\mvnw -pl starkDistribuidos-access spring-boot:run "-Dspring-boot.run.profiles=local"
.\mvnw -pl starkDistribuidos-notification spring-boot:run "-Dspring-boot.run.profiles=local"
.\mvnw -pl starkDistribuidos-frontend spring-boot:run "-Dspring-boot.run.profiles=local"
```

## Acceso a la aplicacion

| Recurso | URL / credenciales |
| --- | --- |
| Frontend | `http://localhost:8085/` |
| Usuario demo | `admin` |
| Contrasena demo | `admin123` |
| Eureka | `http://localhost:8761/` |

## Explicacion de la interfaz

### Overview

La vista `Overview` muestra un resumen general del sistema de seguridad:

- Sensores activos.
- Alertas activas.
- Alertas criticas.
- Estado general del sistema.
- Boton `Iniciar/Detener Demo` para activar o parar la generacion automatica de eventos.
- Boton `Generar evento` para crear un evento manualmente.
- Boton `Limpiar datos` para limpiar la informacion visible en la interfaz.
- Grafica de sensores.
- Tendencia de alertas.
- Feed en tiempo real con eventos recientes.

### Sensores

La seccion `Sensores` permite consultar la lista de sensores registrados y crear nuevos sensores. Los tipos contemplados por el sistema son:

- Movimiento.
- Temperatura.
- Humedad.
- Intrusion.
- Humo.
- CO2.

### Alertas

La seccion `Alertas` muestra una tabla con las alertas generadas por el sistema. Las alertas pueden tener los niveles:

- `CRITICAL`
- `WARNING`
- `INFO`

La interfaz permite filtrar por nivel y relacionar las alertas con eventos de seguridad generados por sensores o simulaciones.

### Accesos

La seccion `Accesos` muestra logs de acceso. Cada registro incluye informacion como:

- Usuario.
- Sensor o ubicacion asociada.
- Resultado del acceso: permitido o denegado.
- Direccion IP.
- Timestamp.

## Persistencia

El proyecto utiliza H2 en modo fichero para persistir datos localmente. En particular, las alertas y los accesos se guardan de forma persistente.

El boton `Limpiar datos` no elimina el historico de la base de datos. Su comportamiento es ocultar los registros visibles en la interfaz mediante un borrado logico. Por este motivo, los datos historicos siguen disponibles mediante los endpoints `/history`.

Los ficheros de base de datos H2 se generan como ficheros `.mv.db` en carpetas `data`.

## Endpoints principales

Los endpoints principales se exponen a traves del Gateway en `http://localhost:8080`.

| Metodo | Endpoint | Descripcion |
| --- | --- | --- |
| GET | `/api/sensors` | Obtiene la lista de sensores. |
| POST | `/api/sensors` | Crea un nuevo sensor. |
| GET | `/api/alerts` | Obtiene las alertas visibles. |
| POST | `/api/alerts/simulate` | Simula una alerta de seguridad. |
| DELETE | `/api/alerts` | Oculta las alertas visibles. |
| GET | `/api/alerts/history` | Obtiene el historico completo de alertas. |
| GET | `/api/access` | Obtiene los accesos visibles. |
| POST | `/api/access/simulate` | Simula un acceso. |
| DELETE | `/api/access` | Oculta los accesos visibles. |
| GET | `/api/access/history` | Obtiene el historico completo de accesos. |
| GET | `/api/notifications` | Obtiene las notificaciones. |

## Demo recomendada

Para presentar el proyecto, se recomienda seguir estos pasos:

1. Ejecutar el script local:

   ```powershell
   .\EJECUTAR_LOCAL.ps1
   ```

2. Abrir Eureka en `http://localhost:8761/` y verificar que los servicios estan registrados y en estado `UP`.
3. Abrir el frontend en `http://localhost:8085/`.
4. Iniciar sesion con `admin / admin123`.
5. Ir a la vista `Overview`.
6. Pulsar `Generar evento`.
7. Pulsar `Iniciar Demo`.
8. Ver el feed en tiempo real, las graficas, las alertas y los accesos.
9. Pulsar `Limpiar datos`.
10. Comprobar que la interfaz se limpia, pero que el historico persiste mediante los endpoints `/history`.

## Notas importantes

- Usar siempre el perfil `local` para la ejecucion local.
- No ejecutar Docker y la ejecucion local al mismo tiempo, porque se produciran conflictos de puertos.
- Si H2 muestra un error de archivo bloqueado, cerrar IntelliJ Database o cualquier conexion abierta a H2.
- Si el frontend no se actualiza correctamente, recargar la pagina con `Ctrl+F5`.
