# 🔐 Módulo de Seguridad — Semana 10 (DOSW)
**Proyecto:** Bella Ciao API — Restaurante Italiano  
**Estudiante:** José Alejandro Martínez  
**Institución:** Escuela Colombiana de Ingeniería Julio Garavito  

---

## 1. 👥 Matriz de Control de Acceso basado en Roles (RBAC)

De acuerdo con el análisis de requerimientos del restaurante, el acceso a los recursos se divide en cuatro roles (`ADMIN`, `CHEF`, `MESERO`, `CLIENTE`) y acceso público (`permitAll`):

| Endpoint | Método HTTP | Rol Permitido / Acceso | Justificación del Negocio |
| :--- | :---: | :---: | :--- |
| `/api/v1/menu` | `GET` | **Público (`permitAll`)** | Cualquier comensal puede consultar la carta y disponibilidad de platos sin iniciar sesión. |
| `/api/auth/login` | `POST` | **Público (`permitAll`)** | Punto de entrada para autenticación y obtención de JWT. |
| `/api/auth/register` | `POST` | **Público (`permitAll`)** | Registro de nuevos clientes en el sistema. |
| `/swagger-ui/**`, `/v3/api-docs/**` | `GET` | **Público (`permitAll`)** | Documentación OpenAPI interactiva de la API. |
| `/api/v1/platos` | `POST` | `ROLE_ADMIN` | Solo la administración puede crear nuevos platos en la carta. |
| `/api/v1/platos/{id}` | `PUT` | `ROLE_ADMIN` | Modificación de datos y recetas de platos existentes. |
| `/api/v1/platos/{id}` | `DELETE` | `ROLE_ADMIN` | Eliminación definitiva de un plato. |
| `/api/v1/platos/{id}/desactivar` | `PATCH` | `ROLE_ADMIN` | Desactivación comercial de un plato del menú. |
| `/api/v1/platos/{id}/agotado` | `PATCH` | `ROLE_CHEF` | El chef de cocina marca un plato como agotado cuando faltan insumos. |
| `/api/v1/platos` | `GET` | `Autenticado` (Cualquier rol) | Consulta administrativa y operativa del catálogo completo. |
| `/api/v1/mesas` | `GET` | `ROLE_ADMIN`, `ROLE_MESERO` | Consulta del estado y ocupación del salón. |
| `/api/v1/mesas/{id}/abrir-cuenta` | `PATCH` | `ROLE_ADMIN`, `ROLE_MESERO` | El personal de salón abre la cuenta cuando los clientes ocupan la mesa. |
| `/api/v1/pedidos` | `POST` | `ROLE_CLIENTE`, `ROLE_MESERO` | Creación de comandas de mesa por clientes o meseros. |
| `/api/v1/pedidos/{id}/estado` | `PATCH` | `ROLE_CHEF`, `ROLE_MESERO` | Transición del estado del pedido (`RECIBIDO` → `EN_PREPARACION` → `LISTO` → `ENTREGADO`). |
| `/api/v1/cuentas/{idMesa}/pagar` | `POST` | `ROLE_ADMIN`, `ROLE_MESERO` | Registro del pago y cierre de la mesa tras validar que los pedidos estén entregados. |
| `/api/v1/vehiculos/entrada` | `POST` | `ROLE_ADMIN` | Registro de ingreso de vehículos al parqueadero del restaurante. |
| `/api/v1/vehiculos/{id}/salida` | `PATCH` | `ROLE_ADMIN` | Registro de salida y cálculo de cobro por estacionamiento ($3.000/hora). |
| `/api/v1/reportes/**` | `GET` | `ROLE_ADMIN` | Estadísticas gerenciales de ventas y tiempos de cocina. |

---

## 2. 🔑 Mecanismos de Autenticación Soportados

La API soporta múltiples esquemas de autenticación configurados en `SecurityFilterChain`:

1. **JSON Web Token (JWT — Estándar Principal):**
   - Autenticación **stateless** mediante cabecera HTTP: `Authorization: Bearer <token>`.
   - Firma criptográfica con algoritmo `HMAC-SHA256` (`HS256`) y clave secreta de alta entropía inyectada por variable de entorno (`JWT_SECRET`).
   - El payload del token encapsula el identificador del usuario (`sub`) y el rol asignado (`rol`), con vigencia de 1 hora.
2. **HTTP Basic:**
   - Soporte mediante `Customizer.withDefaults()` con cabecera `Authorization: Basic <base64(usuario:clave)>`.
   - Todas las contraseñas se validan contra la base de datos comparando el hash `BCrypt`.
3. **OAuth2 con Google:**
   - Soporte para inicio de sesión social con Google (`/oauth2/authorization/google`).
   - Al completar la autenticación con Google, se aprovisiona el usuario localmente y se genera su sesión/JWT correspondiente.

---

## 3. 🔒 Cifrado en Tránsito (SSL / TLS — HTTPS)

Para mitigar la intercepción de credenciales y tokens en la red (A02: Cryptographic Failures), la API exige HTTPS:

* **Puerto seguro configurado:** `8443`
* **Almacén de claves:** Keystore PKCS12 (`restaurante.p12`) generado mediante `keytool`:
  ```bash
  keytool -genkeypair -alias restaurante -keyalg RSA -keysize 2048 \
    -storetype PKCS12 -keystore src/main/resources/restaurante.p12 \
    -validity 365 \
    -dname "CN=localhost, OU=Dev, O=Restaurante, L=Bogota, S=DC, C=CO"
  ```
* **Variables de entorno:** La contraseña del certificado se inyecta mediante `SSL_KEYSTORE_PASSWORD`. El archivo `.p12` se encuentra excluido de Git y Docker vía `.gitignore` y `.dockerignore`.

---

## 4. 🌍 Configuración de CORS y Cabeceras de Seguridad

* **CORS (Cross-Origin Resource Sharing):**
  - Orígenes explícitos permitidos: `http://localhost:3000` (desarrollo frontend) y dominio productivo.
  - Métodos permitidos: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS`.
  - `allowCredentials(true)` para soportar cabeceras de autorización y cookies seguras.
* **Defensas contra ataques web:**
  - `Content-Security-Policy`: Restringe la ejecución de scripts a fuentes locales (`default-src 'self'; script-src 'self'`).
  - `X-Frame-Options: DENY`: Previene ataques de Clickjacking impidiendo embeber la API en `<iframe>`.
  - `X-XSS-Protection: 1; mode=block`: Activa el filtro heurístico anti-XSS de navegadores heredados.
  - `X-Content-Type-Options: nosniff`: Evita el sniffing de tipos MIME.

---

## 5. 📋 Matriz de Mitigación de Vulnerabilidades — OWASP Top 10

| Vulnerabilidad OWASP | Riesgo para el Restaurante | Mitigación Implementada en la API |
| :--- | :--- | :--- |
| **A01: Broken Access Control** | Un cliente podría alterar precios o borrar platos de otros restaurantes. | - Uso estricto de `@PreAuthorize` en endpoints con RBAC.<br>- Migración de IDs secuenciales a `UUID` v4 en `Pedido` y `Reserva` para eliminar vulnerabilidades de Insecure Direct Object Reference (IDOR). |
| **A02: Cryptographic Failures** | Intercepción de passwords o tokens en tránsito y reposo. | - Contraseñas hasheadas exclusivamente con `BCryptPasswordEncoder` (fuerza 10).<br>- Comunicaciones forzadas sobre HTTPS (puerto 8443 con certificado TLS).<br>- JWT firmado con HMAC-SHA256 con secret de alta entropía. |
| **A03: Injection** | Inyección de SQL o consultas maliciosas en MongoDB. | - Spring Data JPA utiliza exclusivamente consultas preparadas parametrizadas (PreparedStatements). Cero concatenación de strings.<br>- Repositorios Mongo tipados mediante clases de documento (`CatalogoDocument`, `EventoPedidoDocument`). |
| **A04: Insecure Design** | Exposición de credenciales o transiciones de estado ilegales. | - Arquitectura basada en DTOs que filtran passwords o IDs internos en las respuestas.<br>- Reglas de negocio del restaurante enforced: bloqueo de modificación de pedidos en cocina, límite de toppings, validación de cuenta cerrada antes de liberar mesa. |
| **A05: Security Misconfiguration** | Exposición de trazas internas o credenciales en código fuente. | - `GlobalExceptionHandler` captura errores inesperados devolviendo `ErrorResponseDTO` controlado (sin volcado de stack trace al cliente).<br>- Archivos `.env`, `.env.*` y `*.p12` ignorados en Git y Docker. |
| **A06: Vulnerable and Outdated Components** | Uso de librerías con CVEs conocidas. | - Proyecto actualizado a **Spring Boot 3.4.0** con **Java 21**.<br>- Dependencias oficiales auditadas de Maven Central (`jjwt 0.12.3`, `springdoc 2.8.9`). |
| **A07: Identification and Authentication Failures** | Fuerza bruta en login o uso de tokens infinitos. | - Token JWT con tiempo de expiración finito (1 hora).<br>- Respuestas unificadas `401 Unauthorized` ante fallos de credenciales o firmas manipuladas. |
| **A08: Software and Data Integrity Failures** | Manipulación de payloads o firma de tokens JWT. | - Validación criptográfica del secret en cada solicitud mediante `JwtAuthFilter`. Si el token es alterado, la firma no coincide y se descarta de inmediato. |
| **A09: Security Logging and Monitoring Failures** | Incursiones no detectadas de accesos no autorizados. | - Registro de logs operacionales con SLF4J en `GlobalExceptionHandler` ante accesos 401, 403, 404 y 500.<br>- Auditoría persistente de eventos de pedidos en MongoDB. |
| **A10: Server-Side Request Forgery (SSRF)** | Peticiones fraudulentas originadas desde el servidor. | - La API no expone endpoints que reciban URLs externas para ser consumidas por el servidor. |

---

## 6. 🧪 Pruebas Automatizadas de Seguridad

La suite de pruebas contiene tests automatizados con `MockMvc` y perfiles aislados en memoria (`application-test.properties` con base de datos H2):

* **401 Unauthorized:** Valida que llamadas a `/api/v1/platos`, `/api/v1/mesas`, `/api/v1/reservas`, `/api/v1/pedidos` y `/api/v1/vehiculos` sin cabecera de autenticación sean rechazadas.
* **403 Forbidden:** Valida con `@WithMockUser(roles = "CLIENTE")` que los intentos de crear platos, registrar vehículos o abrir cuentas de mesa sean denegados.

---

## 7. 📸 Evidencias Requeridas para la Entrega

> *(En esta sección se deben incrustar las capturas de pantalla solicitadas por la guía)*

1. **Captura 1:** Login exitoso (`POST /api/auth/login`) en Swagger/Postman con respuesta `200 OK` y JWT.
2. **Captura 2:** Botón **Authorize** en Swagger UI con el token JWT ingresado y candado cerrado.
3. **Captura 3:** Petición sin token devolviendo `401 Unauthorized`.
4. **Captura 4:** Petición con rol no autorizado (ej: `CLIENTE` intentando crear plato) devolviendo `403 Forbidden`.
5. **Captura 5:** Cabeceras de seguridad en Postman (`X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff`).
6. **Captura 6:** Suite de pruebas en verde (`mvn test` ejecutado exitosamente con 115 tests pasando).
