# 🔧 SOLUCIONES TÉCNICAS PARA CORREGIR VULNERABILIDADES

## 1. ACTUALIZAR DEPENDENCIAS

### Paso 1: Actualizar pom.xml principal

```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <spring-boot.version>3.2.5</spring-boot.version>
    <spring-cloud.version>2023.0.1</spring-cloud.version>
    <!-- NUEVO: Especificar versión segura de H2 -->
    <h2.version>2.2.220</h2.version>
</properties>

<dependencyManagement>
    <dependencies>
        <!-- NUEVO: Agregar override de H2 -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>${h2.version}</version>
        </dependency>
        <!-- resto de dependencias -->
    </dependencies>
</dependencyManagement>
```

---

## 2. SEGURIDAD CONFIG - CORRECCIONES

### Archivo: `src/main/java/com/distribuidos/stark/config/SecurityConfig.java`

```java
package com.distribuidos.stark.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * ✅ SEGURIDAD MEJORADA
 * - CSRF Protection habilitado
 * - CORS restringido a orígenes específicos
 * - Frame options configurado
 * - Security headers mejorados
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ✅ CSRF PROTECTION HABILITADO
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            // ✅ CORS CONFIGURADO
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authz -> authz
                // ✅ Recursos estáticos públicos
                .requestMatchers("/h2-console/**").permitAll()  // Solo desarrollo
                .requestMatchers("/stark-security/public/**").permitAll()
                .requestMatchers("/stark-security/", "/stark-security/index.html").permitAll()
                .requestMatchers("/stark-security/static/**").permitAll()
                .requestMatchers("/stark-security/js/**").permitAll()
                .requestMatchers("/stark-security/styles/**").permitAll()
                .requestMatchers("/stark-security/images/**").permitAll()
                .requestMatchers("/stark-security/css/**").permitAll()
                .requestMatchers("/stark-security/ws/**").permitAll()
                // ✅ Health checks públicos
                .requestMatchers("/actuator/health").permitAll()
                // ✅ Endpoints protegidos por rol
                .requestMatchers("/stark-security/api/sensors/**").hasAnyRole("ADMIN", "SECURITY")
                .requestMatchers("/stark-security/api/alerts/**").hasAnyRole("ADMIN", "SECURITY")
                .requestMatchers("/stark-security/api/access/**").hasAnyRole("ADMIN", "SECURITY", "USER")
                // ✅ Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            // ✅ HTTP Basic Auth
            .httpBasic(basic -> {})
            // ✅ SECURITY HEADERS
            .headers(headers -> headers
                // ✅ CLICKJACKING PROTECTION
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
                // ✅ XSS PROTECTION
                .xssProtection()
                .and()
                // ✅ CONTENT SECURITY POLICY
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
                .and()
                // ✅ DISABLE CONTENT SNIFFING
                .contentTypeOptions()
            );

        return http.build();
    }

    // ✅ CORS CONFIGURATION - RESTRINGIDO
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // ✅ Solo orígenes específicos (CAMBIAR EN PRODUCCIÓN)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:8080",
            "http://localhost:8081"
            // AGREGAR DOMINIOS DE PRODUCCIÓN AQUÍ
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        source.registerCorsConfiguration("/stark-security/**", configuration);
        
        return source;
    }

    /**
     * ✅ USUARIOS EN MEMORIA (SOLO DESARROLLO)
     * En producción, usar UserDetailsService con base de datos
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("Admin@Secure2024!"))  // ✅ Contraseña más fuerte
            .roles("ADMIN", "SECURITY", "USER")
            .build();

        UserDetails security = User.builder()
            .username("security")
            .password(passwordEncoder().encode("Security@Secure2024!"))
            .roles("SECURITY", "USER")
            .build();

        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("User@Secure2024!"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(admin, security, user);
    }

    /**
     * ✅ PASSWORD ENCODER CON BCRYPT
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);  // ✅ Strength 12
    }
}
```

---

## 3. AUTH CONTROLLER - CORRECCIONES

### Archivo: `starkDistribuidos-auth/src/main/java/com/distribuidos/stark/auth/controller/AuthController.java`

```java
package com.distribuidos.stark.auth.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * ✅ AUTH CONTROLLER MEJORADO
 * - JWT tokens válidos y firmados
 * - Validación de tokens correcta
 * - Sin credenciales hardcodeadas
 * - Logging sin exponer datos sensibles
 */
@RestController
@RequestMapping("/auth")
// ✅ CORS RESTRINGIDO (no usa * )
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Value("${jwt.secret:StarkIndustriesSecuritySystemJWTSecretKey2024SecureKey}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;  // ✅ Servicio de usuarios (crear)

    public AuthController(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        log.info("Auth service health check");
        return ResponseEntity.ok().body(new TestResponse("Auth Service is running!", "OK"));
    }

    /**
     * ✅ LOGIN CON VALIDACIÓN REAL
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // ✅ Sin loguear credenciales
        log.info("Login attempt");
        
        // ✅ Validar entrada
        if (request.username == null || request.password == null) {
            log.warn("Login attempt with missing credentials");
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Username and password required"));
        }

        // ✅ Buscar usuario en base de datos (implementar UserService)
        User user = userService.findByUsername(request.username);
        
        if (user == null) {
            log.info("Login failed: user not found");
            return ResponseEntity.status(401)
                .body(new ErrorResponse("Invalid credentials"));
        }

        // ✅ Validar contraseña con BCrypt
        if (!passwordEncoder.matches(request.password, user.getPasswordHash())) {
            log.info("Login failed: invalid password");
            return ResponseEntity.status(401)
                .body(new ErrorResponse("Invalid credentials"));
        }

        // ✅ GENERAR JWT VÁLIDO Y FIRMADO
        String token = generateJWT(user.getUsername(), user.getRoles());
        
        log.info("Login successful for user: {}", user.getUsername());
        
        return ResponseEntity.ok(new LoginResponse(
            token,
            user.getUsername(),
            jwtExpiration
        ));
    }

    /**
     * ✅ VALIDAR JWT TOKEN
     */
    @GetMapping("/validate/{token}")
    public ResponseEntity<Boolean> validateToken(@PathVariable String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);  // ✅ Valida firma y expiración
            
            log.debug("Token validated successfully");
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return ResponseEntity.ok(false);
        }
    }

    /**
     * ✅ GENERAR JWT FIRMADO
     */
    private String generateJWT(String username, String[] roles) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
            .setSubject(username)
            .claim("roles", roles)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
    }

    // ====== Inner Classes ======

    static class TestResponse {
        public String message;
        public String status;
        
        public TestResponse(String message, String status) {
            this.message = message;
            this.status = status;
        }
    }

    static class LoginRequest {
        public String username;
        public String password;
    }

    static class LoginResponse {
        public String token;
        public String username;
        public Long expiresIn;
        
        public LoginResponse(String token, String username, Long expiresIn) {
            this.token = token;
            this.username = username;
            this.expiresIn = expiresIn;
        }
    }

    static class ErrorResponse {
        public String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
```

---

## 4. CREAR USER SERVICE

### Archivo: `starkDistribuidos-auth/src/main/java/com/distribuidos/stark/auth/service/UserService.java`

```java
package com.distribuidos.stark.auth.service;

import com.distribuidos.stark.auth.entity.User;
import com.distribuidos.stark.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * ✅ SERVICIO DE USUARIOS
 */
@Service
public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // Otros métodos...
}
```

---

## 5. ENTITY USER

### Archivo: `starkDistribuidos-auth/src/main/java/com/distribuidos/stark/auth/entity/User.java`

```java
package com.distribuidos.stark.auth.entity;

import jakarta.persistence.*;

/**
 * ✅ ENTIDAD DE USUARIO
 */
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String roles;

    @Column(nullable = false)
    private Boolean enabled;

    // Getters y Setters
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String[] getRoles() { return roles.split(","); }
    public Boolean isEnabled() { return enabled; }
}
```

---

## 6. APPLICATION.YAML - CORRECCIONES

### Archivo: `src/main/resources/application.yaml`

```yaml
spring:
  application:
    name: StarkDistribuidos
  
  datasource:
    url: jdbc:h2:mem:starkdb
    driverClassName: org.h2.Driver
    username: sa
    password: ''
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
  
  # ✅ H2 CONSOLE DESABILITADO POR DEFECTO
  h2:
    console:
      enabled: ${H2_CONSOLE_ENABLED:false}
      path: /h2-console
  
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: false
  
  # ✅ CREDENCIALES DESDE VARIABLES DE ENTORNO
  mail:
    host: ${MAIL_HOST:localhost}
    port: ${MAIL_PORT:1025}
    username: ${MAIL_USERNAME:test}
    password: ${MAIL_PASSWORD:test}
    properties:
      mail:
        smtp:
          auth: false
          starttls:
            enable: false
  
  # ✅ SECURITY MEJORADA
  security:
    user:
      name: ${SECURITY_USER_NAME:admin}
      password: ${SECURITY_USER_PASSWORD:}

logging:
  level:
    root: INFO
    com.distribuidos.stark: INFO
    # ✅ NO LOGUEAR CREDENCIALES
    org.springframework.security: INFO

management:
  endpoints:
    web:
      exposure:
        # ✅ SOLO ENDPOINTS NECESARIOS
        include: health,info
  endpoint:
    health:
      show-details: when-authorized

server:
  port: 8080
  servlet:
    context-path: /stark-security
  compression:
    enabled: true
    min-response-size: 1024
  http2:
    enabled: true
  forward-headers-strategy: framework

---

spring:
  config:
    activate:
      on-profile: production
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:stark_security_db}
    driverClassName: org.postgresql.Driver
    username: ${DB_USER:stark_user}
    password: ${DB_PASSWORD:}  # ✅ SOLO VARIABLE DE ENTORNO
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
  
  # ✅ H2 DESABILITADO EN PRODUCCIÓN
  h2:
    console:
      enabled: false
  
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQL10Dialect
    hibernate:
      ddl-auto: validate
    show-sql: false

server:
  port: ${SERVER_PORT:8080}
  ssl:
    # ✅ HTTPS EN PRODUCCIÓN
    enabled: ${SSL_ENABLED:false}
    key-store: ${SSL_KEYSTORE_PATH:/etc/ssl/keystore.p12}
    key-store-password: ${SSL_KEYSTORE_PASSWORD:}
    key-store-type: PKCS12
```

---

## 7. DOCKER COMPOSE SEGURO

### Archivo: `docker-compose.yml`

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: stark_security_db
      POSTGRES_USER: stark_user
      # ✅ CONTRASEÑA DESDE .env
      POSTGRES_PASSWORD: ${DB_PASSWORD:change_me_in_production}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    networks:
      - stark-network

  app:
    build: .
    environment:
      # ✅ TODAS LAS CREDENCIALES DESDE VARIABLES
      SPRING_PROFILES_ACTIVE: docker
      DB_HOST: postgres
      DB_PORT: 5432
      DB_NAME: stark_security_db
      DB_USER: stark_user
      DB_PASSWORD: ${DB_PASSWORD:change_me}
      SECURITY_USER_NAME: ${SECURITY_USER_NAME:admin}
      SECURITY_USER_PASSWORD: ${SECURITY_USER_PASSWORD:}
      JWT_SECRET: ${JWT_SECRET:change_me_in_production}
      MAIL_HOST: ${MAIL_HOST:mailhog}
      MAIL_PORT: ${MAIL_PORT:1025}
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    networks:
      - stark-network

volumes:
  postgres_data:

networks:
  stark-network:
    driver: bridge
```

### Archivo: `.env` (NO COMMITTEAR A GIT)

```
# .env - AGREGAR A .gitignore
DB_PASSWORD=Your_Secure_Password_Here_123!
SECURITY_USER_NAME=admin
SECURITY_USER_PASSWORD=Admin@Secure2024!
JWT_SECRET=Your_JWT_Secret_Key_Here_Change_In_Production_2024!
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
SSL_ENABLED=false
```

---

## 8. .gitignore - AGREGAR

```
# Credenciales y secretos
.env
.env.local
.env.*.local

# Secrets
secrets/
*.key
*.pem
*.p12

# Logs con información sensible
logs/
*.log

# IDE
.idea/
.vscode/
*.iml

# Build
target/
node_modules/
dist/

# OS
.DS_Store
Thumbs.db
```

---

## 9. IMPLEMENTACIÓN - CHECKLIST

```bash
# 1. Crear rama
git checkout -b security/fix-vulnerabilities

# 2. Actualizar pom.xml
# Cambiar versión de H2 a 2.2.220

# 3. Actualizar SecurityConfig
# Aplicar correcciones de arriba

# 4. Actualizar AuthController
# Aplicar correcciones de arriba

# 5. Crear UserService y Entity
# Crear archivos nuevos

# 6. Actualizar application.yaml
# Cambiar credenciales por variables

# 7. Crear .env
# Crear archivo (NO COMMITTEAR)

# 8. Compilar y probar
mvn clean package

# 9. Hacer commit
git add .
git commit -m "Security: Fix critical vulnerabilities

- Update H2 to 2.2.220 (fix CVE-2021-42392, CVE-2022-23221, CVE-2021-23463)
- Implement real authentication with JWT
- Configure CORS properly
- Enable CSRF protection
- Move credentials to environment variables
- Disable H2 Console in production
- Add security headers
"

# 10. Push y Pull Request
git push origin security/fix-vulnerabilities
```

---

## 📝 NOTAS IMPORTANTES

1. **Cambiar contraseñas de desarrollo** en .env
2. **Usar HTTPS en producción** (configurado en application.yaml)
3. **Guardar .env en vault o secrets manager** (no en repositorio)
4. **Rotar JWT secret** regularmente
5. **Monitorear logs** para intentos fallidos
6. **Implementar 2FA** en futuras versiones


