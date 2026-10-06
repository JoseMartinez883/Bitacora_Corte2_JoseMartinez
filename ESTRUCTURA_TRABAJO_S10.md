# Estructura de Trabajo — Semana 10

A continuación se detalla la división por fases y subfases para la implementación de la Semana 10, de manera controlada e incremental, siguiendo estrictamente el material del profesor.

```text
SEMANA 10
│
├── FASE 1 — Seguridad (Spring Security + JWT)
│   ├── Subfase 1.1 — Dependencias (jjwt y security en pom.xml)
│   ├── Subfase 1.2 — Entidad y Repositorio de Usuario (UsuarioEntity y DB)
│   ├── Subfase 1.3 — Servicio de Usuarios (UserDetailsService y registro con BCrypt)
│   ├── Subfase 1.4 — Utilidad JWT (JwtUtil)
│   ├── Subfase 1.5 — Filtro de Autenticación (JwtAuthFilter)
│   ├── Subfase 1.6 — Configuración Global y Manejo de Errores (SecurityConfig y GlobalExceptionHandler)
│   ├── Subfase 1.7 — Controller de Login (AuthController)
│   ├── Subfase 1.8 — Restricción de Endpoints (@PreAuthorize en controladores existentes)
│   └── Subfase 1.9 — Configuración Swagger (Candado BearerAuth)
│
├── FASE 2 — Testing y Quality Gate
│   ├── Subfase 2.1 — Dependencia de Testing (spring-security-test)
│   └── Subfase 2.2 — Refactorización de Controladores Test (@WithMockUser y contextos de seguridad)
│
├── FASE 3 — Dockerización
│   ├── Subfase 3.1 — Archivo .dockerignore y variables de entorno
│   ├── Subfase 3.2 — Perfil application-docker.yml
│   ├── Subfase 3.3 — Dockerfile multietapa
│   └── Subfase 3.4 — docker-compose.yml (Postgres, Mongo, API)
│
├── FASE 4 — Integración Continua (CI/CD hacia Azure)
│   ├── Subfase 4.1 — Pipeline QA (.github/workflows/ci-qa.yml)
│   └── Subfase 4.2 — Pipeline PROD (.github/workflows/ci-prod.yml)
│
└── FASE 5 — Documentación y README
    ├── Subfase 5.1 — Tarea manual: Diagrama de Despliegue en Draw.io
    └── Subfase 5.2 — Actualización del README.md con URLs, variables y capturas
```

---

## ESTADO ACTUAL DE S10

**Progreso Global:**

```text
SEMANA 10

FASE 1 — ██████████ 0%
FASE 2 — ░░░░░░░░░░ 0%
FASE 3 — ░░░░░░░░░░ 0%
FASE 4 — ░░░░░░░░░░ 0%
FASE 5 — ░░░░░░░░░░ 0%
```

**Fase actual:**
FASE 1 — Seguridad (Spring Security + JWT)

**Subfase actual:**
Subfase 1.1 — Dependencias de Seguridad y JWT

**Objetivo:**
Preparar el entorno de dependencias para que el proyecto soporte clases de seguridad, cifrado y generación de tokens, sin escribir todavía lógica de negocio.

**Qué voy a hacer:**
Modificar el archivo `pom.xml` para incluir:

1. `spring-boot-starter-security`
2. `jjwt-api`, `jjwt-impl` y `jjwt-jackson` (versión 0.12.3).

**Archivos involucrados:**

- `pom.xml`

**Requisito del profesor:**
Configurar `jjwt` v0.12.3 para manejar tokens y habilitar Spring Security.

**Fuente:**

- `guia-turtwig-s10-seguridad.html` (Sección 4: "Fase 1 — Dependencias jjwt").

**Dependencias:**

- El proyecto debe compilar correctamente en su estado actual (Validado en auditoría previa).

**Validación:**
Al recargar el POM y ejecutar la compilación de Maven, el proyecto debe seguir compilando sin errores, confirmando que las nuevas dependencias se han descargado correctamente y no generan conflictos con Spring Boot 3.x.
