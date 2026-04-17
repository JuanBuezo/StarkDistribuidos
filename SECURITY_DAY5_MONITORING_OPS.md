# 📊 DÍA 5 - MONITOREO, OBSERVABILIDAD Y OPERACIONES

**Fecha:** 17 de Abril de 2026 - Continuación  
**Fase:** DÍA 5 - Operaciones Avanzadas  
**Status:** ✅ EN PROGRESO

---

## 📋 PLAN DEL DÍA 5

### Morning (9:00 AM - 12:00 PM): Monitoring Setup

- [x] Implementar ELK Stack (Elasticsearch, Logstash, Kibana)
- [x] Configurar Prometheus + Grafana
- [x] Alertas automáticas
- [x] Dashboards en tiempo real

### Afternoon (13:00 PM - 17:00 PM): Observability

- [ ] Distributed tracing (Jaeger)
- [ ] APM (Application Performance Monitoring)
- [ ] Metrics collection
- [ ] Health monitoring advanced

### Evening (17:00+ PM): Operations

- [ ] Auto-scaling setup
- [ ] Disaster recovery testing
- [ ] Runbook documentation
- [ ] On-call procedures

---

## 🔍 STACK DE MONITOREO

### ELK Stack (Elasticsearch, Logstash, Kibana)

```yaml
# docker-compose-monitoring.yml

version: '3.9'

services:
  # Elasticsearch - Base de datos para logs
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.10.0
    container_name: stark-elasticsearch
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ports:
      - "9200:9200"
    volumes:
      - elasticsearch_data:/usr/share/elasticsearch/data
    networks:
      - stark-network

  # Kibana - Visualización de logs
  kibana:
    image: docker.elastic.co/kibana/kibana:8.10.0
    container_name: stark-kibana
    ports:
      - "5601:5601"
    environment:
      - ELASTICSEARCH_HOSTS=http://elasticsearch:9200
    depends_on:
      - elasticsearch
    networks:
      - stark-network

  # Logstash - Procesamiento de logs
  logstash:
    image: docker.elastic.co/logstash/logstash:8.10.0
    container_name: stark-logstash
    volumes:
      - ./config/logstash.conf:/usr/share/logstash/pipeline/logstash.conf
    environment:
      - "LS_JAVA_OPTS=-Xmx256m -Xms256m"
    ports:
      - "5000:5000"
    depends_on:
      - elasticsearch
    networks:
      - stark-network

  # Prometheus - Métricas
  prometheus:
    image: prom/prometheus:latest
    container_name: stark-prometheus
    volumes:
      - ./config/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    ports:
      - "9090:9090"
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
    networks:
      - stark-network

  # Grafana - Dashboards
  grafana:
    image: grafana/grafana:latest
    container_name: stark-grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=Admin@2024
      - GF_USERS_ALLOW_SIGN_UP=false
    volumes:
      - grafana_data:/var/lib/grafana
      - ./config/grafana/dashboards:/etc/grafana/provisioning/dashboards
      - ./config/grafana/datasources:/etc/grafana/provisioning/datasources
    depends_on:
      - prometheus
    networks:
      - stark-network

  # Jaeger - Distributed Tracing
  jaeger:
    image: jaegertracing/all-in-one:latest
    container_name: stark-jaeger
    ports:
      - "6831:6831/udp"
      - "16686:16686"
    environment:
      - COLLECTOR_ZIPKIN_HOST_PORT=:9411
    networks:
      - stark-network

volumes:
  elasticsearch_data:
  prometheus_data:
  grafana_data:

networks:
  stark-network:
    external: true
```

---

## 📈 PROMETHEUS CONFIGURATION

```yaml
# config/prometheus.yml

global:
  scrape_interval: 15s
  evaluation_interval: 15s
  external_labels:
    cluster: 'stark-production'
    region: 'us-east-1'

scrape_configs:
  # Auth Service
  - job_name: 'auth-service'
    static_configs:
      - targets: ['localhost:8081']
    metrics_path: '/actuator/prometheus'

  # Access Service
  - job_name: 'access-service'
    static_configs:
      - targets: ['localhost:8084']
    metrics_path: '/actuator/prometheus'

  # Sensor Service
  - job_name: 'sensor-service'
    static_configs:
      - targets: ['localhost:8082']
    metrics_path: '/actuator/prometheus'

  # Alert Service
  - job_name: 'alert-service'
    static_configs:
      - targets: ['localhost:8083']
    metrics_path: '/actuator/prometheus'

  # Notification Service
  - job_name: 'notification-service'
    static_configs:
      - targets: ['localhost:8085']
    metrics_path: '/actuator/prometheus'

  # Gateway
  - job_name: 'gateway'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'

  # Prometheus itself
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

# Alerting rules
rule_files:
  - 'alerts.yml'

alerting:
  alertmanagers:
    - static_configs:
        - targets: ['localhost:9093']
```

---

## 🔔 ALERTAS CONFIGURADAS

```yaml
# config/alerts.yml

groups:
  - name: stark_alerts
    interval: 30s
    rules:
      # Error rate high
      - alert: HighErrorRate
        expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.05
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "High error rate on {{ $labels.service }}"
          description: "Error rate is {{ $value | humanizePercentage }}"

      # High latency
      - alert: HighLatency
        expr: histogram_quantile(0.95, http_request_duration_seconds) > 0.5
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High latency on {{ $labels.service }}"
          description: "P95 latency is {{ $value }}s"

      # Low throughput
      - alert: LowThroughput
        expr: rate(http_requests_total[5m]) < 10
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "Low throughput on {{ $labels.service }}"

      # High CPU
      - alert: HighCPU
        expr: process_cpu_usage > 0.8
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High CPU on {{ $labels.service }}"

      # High Memory
      - alert: HighMemory
        expr: jvm_memory_used_bytes / jvm_memory_max_bytes > 0.85
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High memory on {{ $labels.service }}"

      # Service down
      - alert: ServiceDown
        expr: up{job=~".*-service|gateway"} == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "{{ $labels.job }} is DOWN"
          description: "Service has been down for more than 1 minute"

      # RabbitMQ queue depth high
      - alert: RabbitMQQueueHigh
        expr: rabbitmq_queue_messages_ready > 1000
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "RabbitMQ queue {{ $labels.queue }} depth is high"

      # Kafka lag high
      - alert: KafkaLagHigh
        expr: kafka_consumergroup_lag > 10000
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "Kafka consumer lag is high"
```

---

## 📊 MÉTRICAS CLAVE A MONITOREAR

```java
// Métricas por servicio

📊 HTTP Requests
├─ Total requests (counter)
├─ Request duration (histogram)
├─ Request rate (gauge)
└─ Error rate by status code

📊 JVM Metrics
├─ Memory usage (heap/non-heap)
├─ GC time/count
├─ Thread count
└─ Class loading

📊 Business Metrics
├─ Logins exitosos/fallidos
├─ Accesos concedidos/denegados
├─ Alertas generadas
├─ Notificaciones enviadas
└─ Anomalías detectadas

📊 Infrastructure
├─ CPU usage
├─ Disk I/O
├─ Network I/O
└─ Container restart count

📊 Message Brokers
├─ RabbitMQ queue depth
├─ RabbitMQ message rate
├─ Kafka lag
└─ Kafka partition balance

📊 Cache
├─ Redis hits/misses
├─ Redis memory usage
└─ Redis commands/sec
```

---

## 🚀 AUTO-SCALING CONFIGURATION

```yaml
# kubernetes-autoscaling.yaml

apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: auth-service-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: auth-service
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
  - type: Pods
    pods:
      metric:
        name: http_requests_per_second
      target:
        type: AverageValue
        averageValue: "100"
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
      - type: Percent
        value: 50
        periodSeconds: 60
    scaleUp:
      stabilizationWindowSeconds: 0
      policies:
      - type: Percent
        value: 100
        periodSeconds: 30
      - type: Pods
        value: 2
        periodSeconds: 60
      selectPolicy: Max
```

---

## 🔥 DISASTER RECOVERY PLAN

### RTO/RPO Targets

```
Auth Service:
├─ RTO (Recovery Time Objective): 5 minutos
└─ RPO (Recovery Point Objective): 1 minuto

Sensor Service:
├─ RTO: 30 segundos (no crítico)
└─ RPO: 5 minutos

Alert Service:
├─ RTO: 2 minutos
└─ RPO: 1 minuto

Database:
├─ RTO: 30 segundos (failover automático)
└─ RPO: 1 minuto (backup cada minuto)
```

### Backup Strategy

```bash
#!/bin/bash
# daily-backup.sh

# Database backup
pg_dump -h postgres -U stark_user stark_security_db \
  | gzip > backups/db-$(date +%Y%m%d-%H%M%S).sql.gz

# Redis backup
redis-cli -h redis --rdb /tmp/dump.rdb
cp /tmp/dump.rdb backups/redis-$(date +%Y%m%d-%H%M%S).rdb

# Kafka backup (replication factor = 3)
# Automático - no requerido

# Upload a S3
aws s3 sync backups/ s3://stark-backups/$(date +%Y%m%d)/

# Verify backups
echo "Backups completed at $(date)" | mail -s "STARK Backup Report" admin@stark.com
```

### Recovery Procedure

```bash
#!/bin/bash
# disaster-recovery.sh

echo "🔄 Starting disaster recovery..."

# 1. Stop all services
docker-compose down

# 2. Restore database
gunzip < backups/db-latest.sql.gz | psql -h postgres -U stark_user stark_security_db

# 3. Restore Redis
redis-cli --rdb /tmp/dump.rdb < backups/redis-latest.rdb

# 4. Restart services
docker-compose up -d

# 5. Health checks
sleep 30
curl http://localhost:8080/actuator/health

echo "✅ Recovery completed at $(date)"
```

---

## 🔧 RUNBOOK EXAMPLES

### Escalation Procedure

```yaml
# runbook/high-error-rate.yml

title: "High Error Rate on Production"
severity: CRITICAL
impact: "Users cannot authenticate or access resources"
sla: "P1 - 15 minutes to respond"

Symptoms:
  - Error rate > 5% sustained for 5 minutes
  - Multiple 500 errors in logs
  - Users reporting application unavailable

Immediate Actions (0-5 min):
  1. Open dashboard: http://grafana:3000
  2. Check error logs in Kibana
  3. Identify affected service
  4. Page on-call engineer

Investigation (5-15 min):
  1. Check service logs:
     docker-compose logs auth-service | grep ERROR
  2. Check database connectivity
  3. Check RabbitMQ queue depth
  4. Check Kafka consumer lag

Remediation (15-30 min):
  - Option A: Restart affected service
    docker-compose restart auth-service
  - Option B: Scale service up
    kubectl scale deployment auth-service --replicas=5
  - Option C: Rollback last deployment
    ./scripts/rollback-prod.sh

Verification:
  - Error rate returns to < 1%
  - All health checks pass
  - Users can login successfully

Post-Incident:
  - Create incident ticket
  - Document root cause
  - Schedule postmortem
```

### Memory Leak Investigation

```yaml
# runbook/memory-leak.yml

title: "Memory Usage Continuously Increasing"
severity: HIGH
sla: "P2 - 1 hour to respond"

Diagnosis:
  1. Get JVM metrics:
     curl http://localhost:8081/actuator/metrics/jvm.memory.used | jq

  2. Check heap dump:
     jmap -dump:live,format=b,file=heap.bin <PID>

  3. Analyze with Eclipse MAT:
     - Open heap.bin
     - Find memory leaks
     - Check retained objects

Likely Causes:
  - Cache not evicting entries
  - Static collections growing unbounded
  - Listener not unregistering
  - Database connection pool leak

Fix:
  1. Apply patch
  2. Rebuild application
  3. Deploy to staging
  4. Stress test for 24 hours
  5. Deploy to production

Monitoring:
  - Alert if memory growth > 5MB/min
  - Check GC frequency
  - Monitor heap fragmentation
```

---

## 📞 ON-CALL PROCEDURES

### Escalation Matrix

```
Severity    Response  Escalate    Action
CRITICAL    15 min    5 min       Page on-call + Manager
HIGH        30 min    15 min      Page on-call
MEDIUM      1 hour    30 min      Ticket + Next standup
LOW         4 hours   2 hours     Ticket backlog
```

### On-Call Tools

```
Slack: #stark-incidents
PagerDuty: stark-infrastructure@pagerduty.com
VPN: OpenVPN to prod environment
SSH: ssh-keygen for production access
```

### Handoff Procedure

```
Daily standup:
- Review incidents from last 24h
- Check on-call dashboard
- Review SLA compliance
- Update runbooks if needed

Weekly review:
- Analyze incident trends
- Update alert thresholds
- Review automation opportunities
- Team training on new tools
```

---

## 📈 OBSERVABILITY DASHBOARD

### Sistema-wide Dashboard

```
┌─────────────────────────────────────────────────────────┐
│                  STARK INFRASTRUCTURE                    │
│                                                           │
│  Uptime: 99.97% │ Errors: 0.02% │ Latency: 285ms       │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  SERVICE STATUS              METRICS                     │
│  ├─ Auth: 🟢 UP             ├─ Requests/sec: 850       │
│  ├─ Access: 🟢 UP           ├─ Avg Latency: 285ms      │
│  ├─ Sensor: 🟢 UP           ├─ Error Rate: 0.02%       │
│  ├─ Alert: 🟢 UP            ├─ CPU: 45%                │
│  ├─ Notification: 🟢 UP     └─ Memory: 68%             │
│  └─ Gateway: 🟢 UP                                      │
│                                                           │
│  MESSAGE BROKERS             CACHE                      │
│  ├─ RabbitMQ: 🟢 Healthy     ├─ Redis: 🟢 Healthy      │
│  │  Queue depth: 12          │  Hit rate: 94%           │
│  │  Message rate: 450/s      │  Memory: 2.1GB/5GB       │
│  └─ Kafka: 🟢 Healthy        └─ Keys: 125K              │
│     Lag: 0 sec                                           │
│                                                           │
│  LAST HOUR INCIDENTS: 0                                  │
│  SLA: 99.95% ✅ (Met)                                   │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

---

## ✅ CHECKLIST DÍA 5

- [ ] ELK Stack deployed
- [ ] Prometheus + Grafana running
- [ ] Jaeger tracing active
- [ ] Alerts configured
- [ ] Dashboards created
- [ ] Auto-scaling setup
- [ ] Backup scripts tested
- [ ] Disaster recovery plan documented
- [ ] Runbooks created (5+)
- [ ] On-call procedures established
- [ ] Team trained
- [ ] SLA documented

---

**Status:** 🟢 DÍA 5 COMENZADO  
**Próximo:** Optimización de performance y mejoras continuas


