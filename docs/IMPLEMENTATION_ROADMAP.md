# 📋 PLAN DE ACCIÓN DETALLADO - SEMANA DE IMPLEMENTACIÓN

Guía paso a paso para corregir todas las vulnerabilidades en 72 horas.

---

## 🕐 CRONOGRAMA RECOMENDADO

```
LUNES
├─ 9:00 - 10:00   Reunión de kick-off
├─ 10:00 - 12:00  Actualizar H2
├─ 12:00 - 13:00  Almuerzo
├─ 13:00 - 15:00  Implementar CORS fix
└─ 15:00 - 17:00  Credenciales → .env

MARTES
├─ 9:00 - 12:00   Implementar JWT
├─ 12:00 - 13:00  Almuerzo
├─ 13:00 - 17:00  Pruebas unitarias
└─ 17:00 - 18:00  Code review

MIÉRCOLES
├─ 9:00 - 11:00   Integration tests
├─ 11:00 - 12:00  Security scanning
├─ 12:00 - 13:00  Almuerzo
├─ 13:00 - 15:00  Deploy staging
├─ 15:00 - 17:00  Penetration testing
└─ 17:00 - 18:00  Validación final

JUEVES
├─ 9:00 - 17:00   Deploy producción + monitoreo
└─ 17:00+         On-call para issues
```

---

## 📅 DÍA 1: LUNES (Criticidad máxima)

### Tarea 1.1: Actualizar H2 Database

**Duración:** 30 minutos  
**Responsable:** DevOps / Backend Lead

```bash
# 1. Actualizar pom.xml
# Cambiar:
# <version>1.4.200</version>
# Por:
# <version>2.2.220</version>

# 2. Actualizar archivo
cd C:\4ICAI\SISTEMAS\ DISTRIBUIDOS\STARK_DISTRIBUIDOS\StarkDistribuidos
mvn clean install

# 3. Verificar actualización
mvn dependency:tree | grep h2

# 4. Commit
git add pom.xml
git commit -m "chore: upgrade h2 from 1.4.200 to 2.2.220 (security)"
```

**Validación:**
- [ ] Compilación exitosa
- [ ] Ningún breaking change
- [ ] Tests pasan

---

### Tarea 1.2: Desabilitar H2 Console en Producción

**Duración:** 15 minutos  
**Responsable:** Backend Developer

```yaml
# EN: src/main/resources/application.yaml

# ANTES:
spring:
  h2:
    console:
      enabled: true

# DESPUÉS:
spring:
  h2:
    console:
      enabled: ${H2_CONSOLE_ENABLED:false}
```

**Validación:**
- [ ] H2 console desabilitado por defecto
- [ ] Puede habilitarse con variable de entorno

---

### Tarea 1.3: Mover credenciales a variables de entorno

**Duración:** 45 minutos  
**Responsable:** Backend Developer

**Paso 1:** Crear archivo `.env` (NO COMMITTEAR)

```bash
# .env
DB_PASSWORD=Your_Secure_DB_Password_Here_123!
SECURITY_USER_NAME=admin
SECURITY_USER_PASSWORD=Admin@Secure2024!
EUREKA_PASSWORD=Eureka@Secure2024!
JWT_SECRET=Your_JWT_Secret_Key_Here_Change_In_Production_2024!
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

**Paso 2:** Agregar a `.gitignore`

```bash
echo ".env" >> .gitignore
echo ".env.local" >> .gitignore
echo "secrets/" >> .gitignore
```

**Paso 3:** Actualizar `application.yaml`

```yaml
# ANTES
spring:
  security:
    user:
      name: admin
      password: admin123

# DESPUÉS
spring:
  security:
    user:
      name: ${SECURITY_USER_NAME:admin}
      password: ${SECURITY_USER_PASSWORD:}

# ANTES
eureka:
  client:
    service-url:
      defaultZone: http://admin:admin123@localhost:8761/eureka/

# DESPUÉS
eureka:
  client:
    service-url:
      defaultZone: http://${EUREKA_USER:admin}:${EUREKA_PASSWORD}@localhost:8761/eureka/
```

**Validación:**
- [ ] .env creado y en .gitignore
- [ ] Aplicación funciona con variables
- [ ] No hay credenciales en logs de compilación

---

### Tarea 1.4: Configurar CORS correctamente

**Duración:** 30 minutos  
**Responsable:** Backend Developer

**Crear archivo:** `src/main/java/com/distribuidos/stark/config/CorsConfig.java`

```java
package com.distribuidos.stark.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * ✅ CORS CONFIGURATION SEGURO
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // ✅ SOLO ORÍGENES PERMITIDOS
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:8080",
            "https://yourdomain.com"  // CAMBIAR EN PRODUCCIÓN
        ));
        
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        source.registerCorsConfiguration("/auth/**", configuration);
        
        return source;
    }
}
```

**Actualizar SecurityConfig:**

```java
// Agregar a securityFilterChain():
.cors(cors -> cors.configurationSource(corsConfigurationSource()))

// ELIMINAR de AuthController:
@CrossOrigin(origins = "*")  // ❌ ELIMINAR
```

**Validación:**
- [ ] CORS funciona solo con orígenes permitidos
- [ ] Origen "evil.com" es rechazado
- [ ] Tests CORS pasan

---

### End of Day 1 - Validación

```bash
# 1. Compilar
mvn clean package -DskipTests

# 2. Commit los cambios
git add .
git commit -m "Security Day 1: H2 update, env vars, CORS config"

# 3. Push a rama
git push origin security/fix-vulnerabilities
```

---

## 📅 DÍA 2: MARTES (Autenticación)

### Tarea 2.1: Implementar JWT Token Real

**Duración:** 2 horas  
**Responsable:** Backend Developer Senior

**Paso 1:** Crear JWT Service

```java
// Archivo: starkDistribuidos-auth/src/main/java/com/distribuidos/stark/auth/service/JwtService.java

package com.distribuidos.stark.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, String[] roles) {
        return Jwts.builder()
            .setSubject(username)
            .claim("roles", roles)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
}
```

**Paso 2:** Actualizar AuthController

Copiar código de: `SECURITY_FIXES_IMPLEMENTATION.md` (Sección 3)

**Validación:**
- [ ] Token puede ser generado
- [ ] Token puede ser validado
- [ ] Token expirado es rechazado
- [ ] Token con firma incorrecta es rechazado

---

### Tarea 2.2: Crear UserService y Entity

**Duración:** 1 hora  
**Responsable:** Backend Developer

Seguir guía de: `SECURITY_FIXES_IMPLEMENTATION.md` (Secciones 4 y 5)

**Pasos:**
1. Crear entity `User.java`
2. Crear repository `UserRepository.java`
3. Crear service `UserService.java`
4. Crear migration SQL inicial con usuarios

**Validación:**
- [ ] Users table creada
- [ ] Usuarios de prueba insertados
- [ ] UserService funciona correctamente

---

### Tarea 2.3: Ejecutar pruebas unitarias

**Duración:** 1 hora  
**Responsable:** QA / Developer

```bash
# Ejecutar todas las pruebas
mvn clean test

# Resultado esperado: 100% de pruebas pasan

# Generar reporte de cobertura
mvn jacoco:report
```

**Validación:**
- [ ] Todas las pruebas pasan
- [ ] Cobertura > 80%
- [ ] No hay warnings

---

### Tarea 2.4: Code Review

**Duración:** 1 hora  
**Responsable:** Team Lead / Security Review

```
Checklist de revisión:
[ ] JWT implementado correctamente
[ ] CORS restringido
[ ] Credenciales en .env
[ ] Logs no exponen datos sensibles
[ ] No hay hardcoding de passwords
[ ] Validación de entrada presente
[ ] Error handling apropiado
```

---

### End of Day 2

```bash
git add .
git commit -m "Security Day 2: JWT implementation, UserService, tests"
git push origin security/fix-vulnerabilities
```

---

## 📅 DÍA 3: MIÉRCOLES (Validación y Deploy Staging)

### Tarea 3.1: Ejecutar Security Scans

**Duración:** 1 hora  
**Responsable:** DevOps / Security

```bash
# 1. Maven dependency check
mvn dependency-check:check

# Resultado esperado: 0 vulnerabilidades críticas

# 2. OWASP ZAP scan
docker run -v $(pwd):/zap/wrk:rw -t owasp/zap2docker-stable \
  zap-baseline.py -t http://localhost:8080 -r report.html

# 3. SonarQube (si está disponible)
mvn sonar:sonar
```

**Validación:**
- [ ] Dependency check: 0 críticos
- [ ] OWASP ZAP: Sin críticos
- [ ] Code quality: Grade A

---

### Tarea 3.2: Integration Tests

**Duración:** 1 hora  
**Responsable:** QA

```bash
# Ejecutar tests de integración
mvn verify

# Específicamente:
mvn test -Dtest=AuthControllerIntegrationTest

# Crear ambiente de test
docker-compose -f docker-compose.test.yml up -d
```

**Validación:**
- [ ] Login funciona
- [ ] JWT válido
- [ ] Token expirado rechazado
- [ ] CORS funciona correctamente

---

### Tarea 3.3: Deploy a Staging

**Duración:** 1-2 horas  
**Responsable:** DevOps

```bash
# 1. Compilar
mvn clean package -Pproduction

# 2. Crear imagen Docker
docker build -t stark-security:v2.0.0 .

# 3. Push a registry
docker push registry.example.com/stark-security:v2.0.0

# 4. Deploy a staging
kubectl apply -f k8s/staging/

# 5. Verificar health
curl http://staging.stark-security.com/actuator/health
```

**Validación:**
- [ ] Aplicación inicia sin errores
- [ ] Health check pasa
- [ ] Logs limpios (sin vulnerabilidades)

---

### Tarea 3.4: Penetration Testing

**Duración:** 2 horas  
**Responsable:** Security Team

```
Manual penetration tests:
[ ] Intenta RCE en H2 → Falla ✅
[ ] CORS bypass → Falla ✅
[ ] CSRF attack → Bloqueado ✅
[ ] Brute force login → Rate limited ✅
[ ] Token tampering → Detectado ✅
[ ] XXE injection → Falla ✅
[ ] SQL injection → Falla ✅
[ ] Path traversal → Falla ✅
```

---

### End of Day 3

```bash
# Validación final
[ ] Todos los scans pasan
[ ] Penetration testing exitoso
[ ] Staging ambiente estable
[ ] Performance aceptable
[ ] No hay datos sensibles en logs

# Preparar para producción
git tag -a v2.0.0 -m "Security release: Fix critical vulnerabilities"
git push origin v2.0.0
```

---

## 📅 DÍA 4: JUEVES (Deploy Producción)

### Pre-Deploy Checklist

```
[ ] Backup de DB producción completo
[ ] Rollback plan documentado
[ ] On-call engineer disponible
[ ] Monitoreo de alertas configurado
[ ] Logs centralizados configurados
[ ] Notificaciones a stakeholders
[ ] Cronograma de deployment comunicado
```

### Deploy Steps

```bash
# 1. Blue-Green deployment
# Crear nuevo ambiente (Green)
kubectl apply -f k8s/production-green/

# 2. Health checks
curl https://prod.stark-security.com/actuator/health

# 3. Run smoke tests
./scripts/smoke-tests.sh

# 4. Switch traffic (canary 10% -> 50% -> 100%)
kubectl patch service stark-security \
  -p '{"spec":{"selector":{"version":"v2.0.0"}}}'

# 5. Monitor
watch kubectl get pods
tail -f logs/prod.log

# 6. Rollback si es necesario
kubectl rollout undo deployment/stark-security
```

### Post-Deploy

```
[ ] Verificar no hay errores en logs
[ ] Performance metrics en rango
[ ] Users reportan funcionalidad OK
[ ] Alertas de seguridad no disparan
[ ] Documentar cualquier issue
```

---

## 🎯 CRITERIOS DE ÉXITO

### Técnico
- [x] H2 actualizado a 2.2.220
- [x] JWT implementado y validado
- [x] CORS restringido
- [x] CSRF protection activo
- [x] 0 CVEs críticos
- [x] 100% tests pasando
- [x] Cobertura > 80%

### Operacional
- [x] Deploy sin downtime
- [x] Rollback plan ejecutado 0 veces
- [x] Performance similar o mejor
- [x] Logs limpios

### Seguridad
- [x] OWASP ZAP: 0 críticos
- [x] Penetration test: Exitoso
- [x] Compliance: Cumplido

---

## 📞 ESCALACIÓN

Si durante la implementación surge algún problema:

```
Severidad: CRÍTICA
├─ Contact: CTO
├─ Response: <15 min
└─ Rollback: Inmediato

Severidad: ALTA
├─ Contact: Security Lead
├─ Response: <1 hora
└─ Escalate si no se resuelve

Severidad: MEDIA
├─ Contact: Team Lead
├─ Response: <4 horas
└─ Plan de mitigación requerido
```

---

## ✅ SIGN-OFF

Cuando todo esté completo:

- [ ] Lead Developer: _________________ Fecha: _____
- [ ] DevOps Lead: _________________ Fecha: _____
- [ ] Security Review: _________________ Fecha: _____
- [ ] CTO: _________________ Fecha: _____

---

**Documento válido desde:** 2026-04-17  
**Próxima revisión:** 2026-05-01  
**Responsable:** [Nombre del Security Lead]


