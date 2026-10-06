# Plan de Implementación — Semana 10

## 1. Objetivo
Preparar, planificar y estructurar la implementación de los requisitos exigidos para la Semana 10 (S10). El objetivo técnico central abarca cuatro áreas fundamentales que evolucionarán la API: Seguridad (Spring Security, JWT, Autorización por roles), Empaquetado (Dockerización multietapa), Integración/Despliegue Continuo (CI/CD a Azure Web Apps mediante GitHub Actions), y Documentación Arquitectónica (Diagrama de Despliegue Manual).

## 2. Estado previo S07–S09
Se realizó una auditoría completa del código actual y de los entregables previos:
*   **S07 (Arquitectura, Excepciones y Validación):** ✅ **COMPLETO.** El proyecto respeta la separación de capas, el manejo global de excepciones funciona y las reglas de validación (Bean Validation) están activas.
*   **S08 (MapStruct, Streams y Testing):** ✅ **COMPLETO.** Todos los Mappers fueron migrados a interfaces MapStruct. Se resolvió la deuda técnica de los Blockers de SonarQube (se implementaron los tests vacíos) y la cobertura de JaCoCo se elevó con los 95 tests existentes.
*   **S09 (Persistencia Híbrida y Documentación):** ✅ **COMPLETO.** Se configuró PostgreSQL (JPA) para el core transaccional y MongoDB para los Eventos/Auditoría. El `README.md` cuenta con la Matriz de Roles y los diagramas exigidos.

## 3. Pendientes anteriores que afectan S10
*   ✅ **NINGUNO.** Las bases del proyecto están sólidas. No existen deudas técnicas de S07, S08 o S09 que bloqueen la implementación de S10. 

## 4. Materiales analizados de S10
*   **PDF Principal:** `DOSW 1 - S010.pdf`
*   **Guía Turtwig de Seguridad:** `guia-turtwig-s10-seguridad.html`
*   **Guía Turtwig de Dockerización:** `guia-turtwig-s10-docker.html`
*   **Guía Turtwig de CI/CD:** `guia-turtwig-s10-cicd.html`
*   *(Nota: Se menciona brevemente "Ejercicios de refuerzo grupo chimchar y piplup", asumidos como ejercicios paralelos de estudio no bloqueantes).*

## 5. Tema enseñado en S10
1.  **Seguridad en APIS:** Configuración de Spring Security, Autenticación (AuthN) guardando usuarios en BD con BCrypt, emisión y validación de JSON Web Tokens (JWT) Stateless, y Autorización (AuthZ) en controladores usando `@PreAuthorize`.
2.  **Dockerización:** Empaquetar aplicaciones con `Dockerfile` (Multistage build usando Maven y JRE), configuración con `docker-compose.yml` para levantar la API, PostgreSQL y MongoDB simultáneamente.
3.  **DevOps y CI/CD:** Creación de pipelines de Integración y Despliegue Continuo usando GitHub Actions (`ci-qa.yml` y `ci-prod.yml`). Despliegue automático y manual a Microsoft Azure (App Service) usando Secrets.
4.  **Diagramación:** Módulo #6 - Diagrama de Despliegue. Identificación de Nodos y Artefactos.

## 6. Requisitos estrictos de S10 (Extraídos de las Guías Turtwig)
1.  **Seguridad:** 
    *   Entidad `UsuarioEntity` para persistir credenciales (contraseña hasheada obligatoriamente con `BCryptPasswordEncoder`).
    *   Librería JWT: `jjwt` (api, impl, jackson) versión `0.12.3`.
    *   El secreto JWT (`JWT_SECRET`) y las credenciales NUNCA deben estar en el código (`.env` incluido en `.gitignore`).
    *   Actualizar Swagger para soportar "BearerAuth".
2.  **Docker:**
    *   `Dockerfile` multietapa (etapa 1: `maven` para compilar, etapa 2: `eclipse-temurin:21-jre-alpine` para ejecutar).
    *   `docker-compose.yml` que orqueste 3 contenedores: API, `postgres:16-alpine` y `mongo:7`.
    *   Subir la imagen compilada a un repositorio público en Docker Hub (`docker push`).
3.  **CI/CD (Azure & GitHub Actions):**
    *   Dos *workflows* separados: `ci-qa.yml` (se activa al hacer push a main/develop y despliega automático en Azure QA) y `ci-prod.yml` (se activa por Tag `v*.*.*` y requiere aprobación manual en GitHub Environment).
    *   Múltiples secretos en GitHub (`DOCKERHUB_TOKEN`, `AZURE_CREDENTIALS`, `JWT_SECRET_QA`, `DB_PASSWORD_PROD`, etc.).
4.  **Diagramación (Diagrama de Despliegue):**
    *   **Prohibido usar código Mermaid o IA.** Debe dibujarse a mano en Draw.io / Lucidchart con los colores del equipo.
    *   Debe mostrar claramente los nodos de QA y PROD en Azure, y el flujo desde GitHub Actions y Docker Hub.

## 7. Orden de implementación
1.  **FASE 1:** Seguridad en la API (Usuarios, Filtros JWT y Swagger).
2.  **FASE 2:** Refactorización de Tests (Quality Gate).
3.  **FASE 3:** Dockerización (`Dockerfile`, `docker-compose.yml`, `application-docker.yml`).
4.  **FASE 4:** CI/CD y Azure Pipeline (`ci-qa.yml` y `ci-prod.yml`).
5.  **FASE 5:** Documentación visual y `README.md`.

## 8. Fases detalladas

### FASE 1 — Seguridad (Spring Security + JWT)
*   **Objetivo:** Autenticar usuarios con JWT y proteger endpoints mediante roles.
*   **Tareas:**
    1. Modificar `pom.xml` (`spring-boot-starter-security`, `jjwt`).
    2. Crear `UsuarioEntity`, `UsuarioRepository` y servicio de registro (con `BCrypt`).
    3. Crear `JwtUtil` y `JwtAuthFilter`.
    4. Crear `SecurityConfig` para manejar rutas públicas (`/login`, `/swagger-ui`) y privadas.
    5. Crear `AuthController` (endpoint `/login` que retorna el JWT).
    6. Agregar anotaciones `@PreAuthorize` en todos los Controladores (según la Matriz S09).
*   **Validación:** Endpoint público responde. Endpoints protegidos devuelven `401 Unauthorized` si no hay token, o `403 Forbidden` si el rol no calza.

### FASE 2 — Testing y Quality Gate
*   **Objetivo:** Evitar que JaCoCo falle por culpa de Spring Security.
*   **Tareas:**
    1. Importar `spring-security-test` en el `pom.xml`.
    2. Modificar TODOS los archivos de tests de controladores para saltar el filtro JWT (simular Auth) usando `@WithMockUser(roles = "ADMIN")` o la configuración equivalente de la guía.
*   **Validación:** `mvn clean test` finaliza en verde manteniendo el 100% de éxito en los 95 tests.

### FASE 3 — Dockerización
*   **Objetivo:** Empaquetar la API para despliegue universal.
*   **Tareas:**
    1. Crear `Dockerfile` multietapa en la raíz.
    2. Crear `.dockerignore` (ignorando `.env`, `target`, `.git`).
    3. Crear `application-docker.yml` para consumir variables de entorno.
    4. Crear `docker-compose.yml` con el stack completo (Postgres, Mongo, API).
*   **Validación:** Ejecutar `docker compose up --build -d` levanta el ecosistema localmente.

### FASE 4 — Integración Continua (CI/CD hacia Azure)
*   **Objetivo:** Automatizar la nube.
*   **Tareas:**
    1. Crear directorio `.github/workflows/`.
    2. Escribir `ci-qa.yml` para QA (ejecuta tests, construye imagen, despliega en Azure App Service).
    3. Escribir `ci-prod.yml` para PROD (espera tags de versión y usa Environment manual).
*   **Requisito Externo:** El desarrollador deberá proveer los recursos en Azure For Students y cargar los Secrets en GitHub.

### FASE 5 — Documentación y README
*   **Objetivo:** Entregables arquitectónicos y demostración.
*   **Tareas:**
    1. Solicitar al equipo dibujar el Diagrama de Despliegue en Draw.io.
    2. Actualizar `README.md` insertando capturas obligatorias: Swagger corriendo, logs de docker compose, workflow de actions en verde, URL de Azure QA y URL de Azure PROD.

## 9. Archivos involucrados
*   `pom.xml`
*   `src/main/java/com/restaurante/security/*` (Nuevo)
*   `src/main/java/com/restaurante/model/entity/UsuarioEntity.java` (Nuevo)
*   Todos los Controladores de la API (Señalar `@PreAuthorize`).
*   Todos los Controladores de Test (Señalar simulación de Usuario).
*   `Dockerfile`, `.dockerignore`, `docker-compose.yml`, `.env.example` (Nuevos).
*   `src/main/resources/application-docker.yml` (Nuevo).
*   `.github/workflows/ci-qa.yml` y `ci-prod.yml` (Nuevos).
*   `README.md`.

## 10. Riesgos
1.  **WebMvcTests Rotos:** Inyectar Spring Security destruye los tests unitarios de controladores (responden HTTP 401 por defecto). Refactorizar los 95 tests es la tarea de mayor impacto colateral.
2.  **Infraestructura Azure:** El despliegue fallará si el usuario no configura previamente los servicios de Azure (App Service, Postgres Flexible Server y Cosmos DB) ni crea el Service Principal (`AZURE_CREDENTIALS`).
3.  **Fuga de Secretos:** Subir accidentalmente un `.env` a Git.

## 11. Dudas / puntos por confirmar
*   **🟠 IMPORTANTE - Ejercicios de Refuerzo:** En la diapositiva 85, se mencionan tareas para "chimchar y piplup". Debe aclararse con el profesor si es una entrega aparte o integrada a este repositorio.
*   **🟠 IMPORTANTE - Base de Datos en la Nube:** Para que el Pipeline CI/CD funcione completamente, el estudiante deberá levantar las BDs en Azure For Students. Esto escapa al control del código y recae sobre la infraestructura.

## 12. Checklist final de entrega (Criterio "S10 Completa")
```text
[ ] La API retorna HTTP 401 a endpoints privados sin JWT.
[ ] La Matriz de Roles restringe accesos exitosamente (retornando HTTP 403 al no cumplir el rol).
[ ] `mvn test jacoco:report` compila 100% de tests en verde a pesar de existir Spring Security.
[ ] `docker-compose up` levanta API + Postgres + Mongo localmente sin fallos de red.
[ ] `ci-qa.yml` y `ci-prod.yml` compilan en Actions (Verde en GitHub).
[ ] El archivo README.md cuenta con el Diagrama de Despliegue (manual, no código).
```
