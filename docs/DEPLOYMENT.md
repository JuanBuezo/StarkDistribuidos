# 🚀 GUÍA DE DEPLOYMENT - Stark Industries

## Opciones de Despliegue

---

## 1️⃣ DESARROLLO LOCAL (H2 + Maven)

### Requisitos
- Java 17+
- Maven 3.9+

### Pasos

```bash
# 1. Navegar al proyecto
cd StarkDistribuidos

# 2. Compilar
mvn clean compile

# 3. Ejecutar
mvn spring-boot:run
```

### Acceso
- API: http://localhost:8080/stark-security/
- H2 Console: http://localhost:8080/stark-security/h2-console
- Health: http://localhost:8080/stark-security/actuator/health

### Credenciales
- Usuario: admin
- Contraseña: admin123

---

## 2️⃣ DOCKER COMPOSE (Recomendado para desarrollo)

### Requisitos
- Docker
- Docker Compose

### Pasos

```bash
# 1. En el directorio del proyecto
cd StarkDistribuidos

# 2. Iniciar todos los servicios
docker-compose up -d

# 3. Verificar estado
docker-compose ps

# 4. Ver logs
docker-compose logs -f stark-app

# 5. Detener servicios
docker-compose down

# 6. Limpiar volumenes
docker-compose down -v
```

### Servicios Disponibles

| Servicio | URL | Usuario | Contraseña |
|----------|-----|---------|-----------|
| App | http://localhost:8080/stark-security/ | admin | admin123 |
| PostgreSQL | localhost:5432 | stark_user | stark_secure_password_123 |
| Mailhog | http://localhost:8025/ | N/A | N/A |
| pgAdmin | http://localhost:5050/ | admin@starkindustries.com | admin123 |

### Logs Importantes

```bash
# Ver logs de la app
docker-compose logs stark-app

# Ver logs de PostgreSQL
docker-compose logs postgres

# Ver logs de Mailhog
docker-compose logs mailhog

# Seguir logs en tiempo real
docker-compose logs -f stark-app
```

---

## 3️⃣ IMAGEN DOCKER MANUAL

### Requisitos
- Docker

### Pasos

```bash
# 1. Construir imagen
docker build -t stark-security:1.0.0 .

# 2. Ejecutar contenedor
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=production \
  -e DB_PASSWORD=tu_password \
  -e MAIL_HOST=smtp.gmail.com \
  -e MAIL_PORT=587 \
  -e MAIL_USERNAME=tu_email@gmail.com \
  -e MAIL_PASSWORD=tu_app_password \
  stark-security:1.0.0

# 3. Con nombre personalizado
docker run --name stark-app \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=production \
  stark-security:1.0.0

# 4. Ver logs
docker logs -f stark-app

# 5. Detener contenedor
docker stop stark-app

# 6. Eliminar contenedor
docker rm stark-app
```

---

## 4️⃣ KUBERNETES (Deployment en Producción)

### Requisitos
- kubectl
- Kubernetes cluster
- Container registry (Docker Hub, ECR, etc.)

### Pasos

```bash
# 1. Subir imagen a registry
docker tag stark-security:1.0.0 tu-registry/stark-security:1.0.0
docker push tu-registry/stark-security:1.0.0

# 2. Crear namespace
kubectl create namespace stark-industries

# 3. Crear secrets
kubectl create secret generic db-credentials \
  --from-literal=password=tu_db_password \
  -n stark-industries

kubectl create secret generic email-credentials \
  --from-literal=username=tu_email@gmail.com \
  --from-literal=password=tu_app_password \
  -n stark-industries

# 4. Aplicar manifiestos
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/postgres-deployment.yaml
kubectl apply -f k8s/stark-app-deployment.yaml
kubectl apply -f k8s/stark-app-service.yaml

# 5. Verificar estado
kubectl get pods -n stark-industries
kubectl get svc -n stark-industries

# 6. Ver logs
kubectl logs -f deployment/stark-app -n stark-industries
```

### Archivos Kubernetes Ejemplo

**k8s/stark-app-deployment.yaml**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: stark-app
  namespace: stark-industries
spec:
  replicas: 3
  selector:
    matchLabels:
      app: stark-app
  template:
    metadata:
      labels:
        app: stark-app
    spec:
      containers:
      - name: stark-app
        image: tu-registry/stark-security:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: SPRING_DATASOURCE_URL
          value: "jdbc:postgresql://postgres-service:5432/stark_db"
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: password
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /stark-security/actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

**k8s/stark-app-service.yaml**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: stark-app-service
  namespace: stark-industries
spec:
  type: LoadBalancer
  selector:
    app: stark-app
  ports:
  - port: 80
    targetPort: 8080
```

---

## 5️⃣ CLOUD PROVIDERS

### AWS ECS

```bash
# Crear repositorio ECR
aws ecr create-repository --repository-name stark-security

# Etiquetar y subir imagen
docker tag stark-security:1.0.0 \
  ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/stark-security:1.0.0
docker push ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/stark-security:1.0.0

# Crear task definition y servicio
aws ecs create-service --cluster stark-cluster ...
```

### Google Cloud Run

```bash
# Submeter build
gcloud builds submit --tag gcr.io/PROJECT_ID/stark-security

# Desplegar
gcloud run deploy stark-app \
  --image gcr.io/PROJECT_ID/stark-security \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

### Azure Container Instances

```bash
# Subir a Azure Container Registry
az acr build --registry starkindustries \
  --image stark-security:1.0.0 .

# Desplegar
az container create \
  --resource-group stark-rg \
  --name stark-app \
  --image starkindustries.azurecr.io/stark-security:1.0.0
```

---

## 6️⃣ HEROKU

```bash
# 1. Login
heroku login

# 2. Crear app
heroku create stark-industries

# 3. Agregar add-ons
heroku addons:create heroku-postgresql:standard-0
heroku addons:create sendgrid:starter

# 4. Configurar variables
heroku config:set SPRING_PROFILES_ACTIVE=production

# 5. Deploy (usando Git)
git push heroku main

# 6. Ver logs
heroku logs --tail
```

---

## 7️⃣ VARIABLES DE ENTORNO

### Desarrollo
```
SPRING_PROFILES_ACTIVE=default
```

### Producción
```
SPRING_PROFILES_ACTIVE=production
SPRING_DATASOURCE_URL=jdbc:postgresql://db.example.com:5432/stark_db
SPRING_DATASOURCE_USERNAME=stark_user
DB_PASSWORD=tu_secure_password
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=tu_email@gmail.com
SPRING_MAIL_PASSWORD=tu_app_password
SERVER_PORT=8080
LOGGING_LEVEL_COM_DISTRIBUIDOS_STARK=INFO
```

### Docker
```
SPRING_PROFILES_ACTIVE=docker
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/stark_security_db
SPRING_DATASOURCE_USERNAME=stark_user
SPRING_DATASOURCE_PASSWORD=stark_secure_password_123
SPRING_MAIL_HOST=mailhog
SPRING_MAIL_PORT=1025
```

---

## 🔍 MONITORIZACIÓN POST-DEPLOYMENT

### Health Check

```bash
# Verificar salud de la aplicación
curl http://localhost:8080/stark-security/actuator/health

# Response esperado:
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {...}
    },
    ...
  }
}
```

### Métricas

```bash
# Ver todas las métricas
curl http://localhost:8080/stark-security/actuator/metrics

# Métrica específica
curl http://localhost:8080/stark-security/actuator/metrics/jvm.memory.used
```

### Logs

```bash
# Ver archivo de logs (producción)
tail -f logs/stark-app.log

# Con Docker
docker logs -f stark-app

# Con Kubernetes
kubectl logs -f pod/stark-app-xyz -n stark-industries
```

---

## 🐛 TROUBLESHOOTING

### Problema: Conexión a BD rechazada

```bash
# Verificar conexión PostgreSQL
psql -U stark_user -d stark_security_db -h localhost

# Docker: verificar red
docker network ls
docker network inspect stark_network
```

### Problema: Email no enviado

```bash
# Ver logs de Mailhog
docker logs mailhog

# UI de Mailhog
http://localhost:8025
```

### Problema: WebSocket desconectado

```bash
# Verificar endpoint
ws://localhost:8080/stark-security/ws/notifications

# Con SockJS
http://localhost:8080/stark-security/ws/notifications
```

### Problema: Memoria insuficiente

```bash
# Aumentar heap en production
docker run -e "JAVA_OPTS=-Xmx1024m -Xms512m" stark-security:1.0.0
```

---

## 📊 BACKUP Y RESTAURACIÓN

### PostgreSQL

```bash
# Backup
docker exec stark-postgres pg_dump \
  -U stark_user stark_security_db > backup.sql

# Restaurar
docker exec -i stark-postgres psql \
  -U stark_user stark_security_db < backup.sql

# Con volumen nombrado
docker run --rm \
  -v stark-postgres-data:/var/lib/postgresql/data \
  postgres:15 pg_dump -U stark_user stark_security_db > backup.sql
```

---

## 🔄 ACTUALIZAR A NUEVA VERSIÓN

### Con Docker Compose

```bash
# 1. Construir nueva imagen
docker build -t stark-security:2.0.0 .

# 2. Actualizar docker-compose.yml
# Cambiar: image: stark-app:latest
# Por:     image: stark-security:2.0.0

# 3. Recrear servicio
docker-compose up -d --build

# 4. Verificar
docker-compose ps
docker-compose logs stark-app
```

### Con Kubernetes

```bash
# 1. Subir nueva imagen
docker push tu-registry/stark-security:2.0.0

# 2. Actualizar deployment
kubectl set image deployment/stark-app \
  stark-app=tu-registry/stark-security:2.0.0 \
  -n stark-industries

# 3. Verificar rollout
kubectl rollout status deployment/stark-app -n stark-industries

# Si hay problemas, rollback
kubectl rollout undo deployment/stark-app -n stark-industries
```

---

## ✅ CHECKLIST DE DEPLOYMENT

- [ ] Compilación exitosa sin errores
- [ ] Tests unitarios pasados
- [ ] Variables de entorno configuradas
- [ ] Base de datos migrada/creada
- [ ] Certificados SSL (si es HTTPS)
- [ ] Firewall configurado
- [ ] Backups automáticos establecidos
- [ ] Monitorización activa
- [ ] Logs configurados
- [ ] Health checks funcionando

---

**¡Proyecto listo para deployment en producción! 🚀**

