# Guía de Seguridad S10 - Restaurante API

Este documento contiene la información requerida por la guía de seguridad de la Semana 10.

## 1. Matriz de Control de Acceso (RBAC)

| Endpoint | ADMIN | CHEF | MESERO | CLIENTE | PUBLICO |
| :--- | :---: | :---: | :---: | :---: | :---: |
| `POST /api/auth/login` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `POST /api/auth/register`| ✅ | ✅ | ✅ | ✅ | ✅ |
| `GET /api/v1/menu` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `GET /api/v1/platos` | ✅ | ❌ | ❌ | ❌ | ❌ |
| `POST /api/v1/platos` | ✅ | ❌ | ❌ | ❌ | ❌ |
| `GET /api/v1/pedidos` | ✅ | ✅ | ✅ | ❌ | ❌ |
| `PUT /api/v1/pedidos/{id}/estado` | ✅ | ✅ | ❌ | ❌ | ❌ |
| `GET /api/v1/mesas` | ✅ | ❌ | ✅ | ❌ | ❌ |
| `POST /api/v1/mesas/{id}/abrir-cuenta` | ✅ | ❌ | ✅ | ❌ | ❌ |
| `GET /api/v1/vehiculos` | ✅ | ❌ | ❌ | ❌ | ❌ |
| `POST /api/v1/vehiculos/entrada` | ✅ | ❌ | ❌ | ❌ | ❌ |

## 2. Mitigación de Vulnerabilidades OWASP Top 10

| Vulnerabilidad | Estrategia de Mitigación en el Código | Ubicación (Clase/Archivo) |
| :--- | :--- | :--- |
| **A01:2021-Broken Access Control** | Implementación de `SecurityFilterChain` restringiendo endpoints y anotaciones `@PreAuthorize` en controladores. | `SecurityConfig.java`, Controladores (`PlatoController`, etc.) |
| **A02:2021-Cryptographic Failures** | Uso de **HTTPS (TLS/SSL)** obligatorio a través de un keystore local `.p12`. Contraseñas hasheadas con **BCryptPasswordEncoder**. | `application.properties`, `DataInitializer.java`, `SecurityConfig.java` |
| **A03:2021-Injection** | Uso exclusivo de Spring Data JPA/MongoDB, previniendo Inyección SQL/NoSQL mediante consultas parametrizadas internamente. | Todos los repositorios (ej. `UsuarioRepositoryJPA.java`) |
| **A04:2021-Insecure Design** | Uso de DTOs (`LoginRequestDTO`, `RegisterRequestDTO`) con validaciones estrictas (`@Valid`, `@NotBlank`, `@Email`) para prevenir inyecciones desde el diseño. | `dto/*.java`, `AuthController.java` |
| **A05:2021-Security Misconfiguration** | Configuración de cabeceras de seguridad estrictas (HSTS, X-Frame-Options, CSP) y desactivación del rastreo de pila en errores. | `SecurityConfig.java`, `GlobalExceptionHandler.java` |
| **A07:2021-Identification and Authentication Failures** | Uso de JWT sin estado, firmado con HMAC (HS256) usando una clave inyectada por variable de entorno (nunca hardcodeada). | `JwtUtil.java`, `JwtAuthFilter.java` |
| **A09:2021-Security Logging and Monitoring Failures** | Integración de `Slf4j` para auditoría y registro de intentos de acceso denegados (401/403) y fallos de login. | `SecurityConfig.java`, `JwtAuthFilter.java`, `GlobalExceptionHandler.java` |

## 3. Lista de Verificación de Evidencias
- [x] Captura de postman probando un endpoint público y obteniendo 200 OK (`/api/v1/menu`).
- [x] Captura de postman probando endpoint seguro sin token y obteniendo 401 Unauthorized.
- [x] Captura de postman autenticándose exitosamente (Login) para obtener el token JWT.
- [x] Captura de postman accediendo a recurso protegido enviando el JWT en el header `Authorization: Bearer <token>`.
- [x] Captura de base de datos mostrando las contraseñas correctamente cifradas (BCrypt).
- [x] Captura del log de la consola evidenciando los registros de seguridad (Alertas 401/403).
- [x] Certificado SSL/HTTPS habilitado en el servidor embebido de Tomcat.

## 4. Instrucciones para Despliegue Local Seguro

1. **Variables de Entorno**: Crear un archivo `.env` basado en `.env.example` con `JWT_SECRET`, contraseñas de BD y credenciales.
2. **Generación de Keystore (Si no existe)**: 
   ```bash
   keytool -genkeypair -alias restaurante -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore src/main/resources/restaurante.p12 -validity 3650 -storepass restaurante123 -keypass restaurante123
   ```
3. **Ejecución**: Levantar con perfil por defecto para HTTPS (`mvn spring-boot:run`) o usar perfil de docker (`docker-compose up`) donde HTTPS suele delegarse al proxy reverso.
