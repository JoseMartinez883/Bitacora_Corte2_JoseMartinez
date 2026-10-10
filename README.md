# Bella CIAO - Cocina Italiana

<p align="center">
  <img src="docs/images/BellaCiaoLogo.png" alt="Bella Ciao Logo" width="600" />
</p>

Plataforma web integral para la digitalización operativa y comercial de un restaurante bajo el concepto italiano. El sistema está diseñado para gestionar un flujo concurrente donde el valor central del negocio es la venta de pastas y pizzas personalizables (con masa, salsas y toppings configurables por el cliente). La plataforma centraliza la experiencia del usuario y la operación interna en un único entorno sincronizado.

---

## Diagrama de Clases Conceptual (Modelo de Dominio)

El siguiente diagrama de clases ilustra la estructura conceptual y las entidades centrales del negocio (Restaurante Italiano), detallando las cardinalidades y asociaciones que dan vida a la lógica operativa:

1. **Gestión de Salón y Cuentas (1:1 y 1:N):** El espacio físico se representa mediante la **Mesa**, la cual actúa como pivote del servicio. Una Mesa puede recibir múltiples **Reservas** a lo largo del tiempo (1:N por parte de un **Usuario**), pero en tiempo de ejecución, solo puede generar una única **Cuenta** activa a la vez (1:1), garantizando que no se mezclen los cobros ni se generen inconsistencias.
2. **Ciclo de Pedidos y Comandas (1:N):** El corazón de la operación de cocina recae en el **Pedido**. Un Pedido pertenece a una Mesa específica y se desglosa en uno o muchos **Ítems de Pedido** (1:N). Esta descomposición permite que los comensales soliciten productos en diferentes momentos (ej. entradas primero, platos fuertes después) unificándose luego en la Cuenta.
3. **Catálogo y Personalización (Acoplamiento Flexible):** Cada Ítem referencia estrictamente a un **Plato** del menú. El modelo de negocio destaca aquí por su flexibilidad: los platos no están atados rígidamente por relaciones físicas pesadas a la tabla de **Ingredientes**. En su lugar, el Plato almacena sus atributos (masa, salsas, opciones de toppings) y la lógica de negocio se encarga de la deducción del stock. Esto garantiza altísimo rendimiento de lectura del menú para el cliente final.
4. **Módulos Independientes (Desacople Total):** Existen dominios operativos, como el **RegistroVehiculo** (Parqueadero), que funcionan de manera completamente aislada sin conexiones físicas a las mesas o facturación. Así mismo, las colecciones documentales (**Reseñas** y **Eventos de Pedido**) están desacopladas y se enlazan lógicamente al pedido mediante su identificador UUID.

### Diagrama General (Visión Global)

<p align="center">
  <img src="docs/uml/DiagramClass.png" alt="Diagrama de Clases General" width="100%" />
</p>

### Detalles Específicos del Diagrama (Secciones ampliadas)

A continuación, puedes desplegar los siguientes apartados para visualizar con mayor detalle las partes específicas del diagrama:

<details>
  <summary><b>🔍 Ver Parte 1 del Diagrama de Clases (Núcleo Operativo: Mesas, Pedidos y Platos)</b></summary>
  <br>
  <p align="center">
    <img src="docs/uml/DiagramClassPart01.png" alt="Diagrama de Clases - Parte 1" width="100%" />
  </p>
  <p><i>Esta sección enfoca la vista en el núcleo operativo: la relación estricta entre Mesas, Pedidos, Ítems de Pedido y la configuración de Platos.</i></p>
</details>

<details>
  <summary><b>🔍 Ver Parte 2 del Diagrama de Clases (Gestión Comercial, Reservas y Módulos Anexos)</b></summary>
  <br>
  <p align="center">
    <img src="docs/uml/DiagramClassPart02.png" alt="Diagrama de Clases - Parte 2" width="100%" />
  </p>
  <p><i>Esta sección expone los modelos complementarios del negocio, como las Cuentas, Reservas de clientes, Registro de Vehículos en parqueadero, e interacciones de Feedback (Reseñas).</i></p>
</details>

---

## Diagrama de Componentes (Arquitectura General)

El siguiente diagrama modela la arquitectura de alto nivel del sistema, ilustrando cómo interactúan los grandes bloques (componentes físicos y lógicos) de la plataforma:

<p align="center">
  <img src="docs/uml/DiagramaGeneralComponents.png" alt="Diagrama de Componentes Generales" width="100%" />
</p>

### 📖 Análisis y Explicación del Modelo:

1. **Front-End BellaCiao:** Representa la aplicación cliente (interfaz de usuario) con la que interactúan directamente los comensales, meseros y administradores. Este componente se encarga puramente de la presentación visual, captura de eventos y recolección de datos, manteniéndose totalmente aislado y agnóstico de las reglas duras del negocio.
2. **Interfaz de Comunicación (API REST):** La conexión entre el Front-End y el Back-End se ilustra mediante un conector estándar UML de interfaz "requerida-ofrecida" (símbolo de socket y enchufe, *lollipop*). Esto indica que el servidor expone un contrato estricto de endpoints RESTful que el cliente consume de manera estructurada y segura (típicamente mediante JSON).
3. **Back-End BellaCiao:** Es el motor central del sistema. Aquí recae el núcleo operativo (Spring Boot). Centraliza toda la lógica de negocio, reglas de validación, seguridad (autenticación) y orquestación de operaciones complejas. Actúa como el único intermediario seguro autorizado para acceder y escribir en los datos de las bases de datos, protegiendo así la integridad de la información frente al exterior.
4. **Almacenamiento Mixto (DB sql y DB mongo):** El diagrama evidencia claramente una arquitectura de *Persistencia Políglota*, ilustrada por las ramificaciones del Back-End hacia dos motores distintos:
   * **DB sql (PostgreSQL):** Maneja los datos altamente estructurados y críticos financieramente (pedidos, facturación, cuentas de usuarios, inventarios) asegurando el control y ejecución de transacciones robustas bajo el modelo relacional tradicional.
   * **DB mongo (MongoDB):** Un sistema NoSQL acoplado paralelamente al Back-End. Asume la responsabilidad de manejar el alto volumen de datos dinámicos, como las extensas bitácoras de estados, catálogos de imágenes y reseñas de los clientes, garantizando velocidad de lectura/escritura y liberando así a la base de datos relacional de cargas documentales masivas.

---

## Diagrama de Componentes Específicos (Nivel de Implementación)

A diferencia del diagrama general anterior, este modelo "hace zoom" en las entrañas del Back-End, mapeando exactamente cómo se entrelazan los paquetes, clases y dependencias inyectadas (`@Autowired`) de Spring Boot en la vida real:

<p align="center">
  <img src="docs/uml/DiagramEspecificComponents.png" alt="Diagrama de Componentes Específicos" width="100%" />
</p>

### 📖 Análisis Arquitectónico Detallado:

1. **Desacoplamiento Estricto con Mappers:** Observando el flujo de izquierda a derecha, vemos que cada Controlador (ej. `PedidoController`) jamás envía Entidades crudas al Servicio. La petición pasa primero por un transformador (ej. `PedidoMapperIn`). A su vez, las respuestas del servicio regresan filtradas por un `PedidoMapperOut`. El uso exhaustivo de mappers de DTO blinda la aplicación contra la sobreexposición de datos.
2. **Orquestación en la Capa de Servicio:** Los Servicios (como `MesaService`, `ReservaService` y `PedidoService`) actúan como nodos hiperconectados ("cerebros"). No solo se comunican con los mappers y repositorios de su mismo dominio, sino que se comunican entre sí para validar reglas de negocio complejas antes de guardar algo en la base de datos.
3. **Conversión a Persistencia (EntityMappers):** Antes de realizar una operación CRUD, la data atraviesa una última barrera arquitectónica (ej. `PlatoEntityMapper`, `ReservaEntityMapper`) para convertirse en Objetos de Acceso a Datos (Entities/Documents) nativos de Hibernate o MongoTemplate.
4. **Flujo de Seguridad Transversal (Security):** El módulo de autenticación funciona con componentes muy especializados: `AuthController` (para logins tradicionales) y `Google OAuth2` delegan la validación en utilidades aisladas (`JwtUtil`, `CustomOAuth2SuccessHandler`, `UsuarioDetailsService`). Un `JwtAuthFilter` actúa transversalmente protegiendo el ecosistema.
5. **División Exacta de Repositorios (SQL vs NoSQL):** El diagrama ilustra físicamente la responsabilidad de persistencia de cada nodo. Repositorios relacionales (`PlatoRepository`, `MesaRepositoryJPA`, `CuentaRepositoryJPA`) envían sus sentencias estrictamente hacia **PostgreSQL**. Por su parte, repositorios documentales como `EventoPedidoRepositoryMongo`, `CatalogoRepositoryMongo` y `ResenaRepositoryMongo` inyectan directamente hacia **MongoDB**.

---

## ⚖️ Justificación de la Persistencia Mixta (SQL + NoSQL)

Para nuestro Restaurante Italiano se diseñó una arquitectura avanzada conocida como **Persistencia Mixta o Políglota**, la cual consiste en utilizar más de una tecnología de base de datos para aprovechar sus respectivas fortalezas, aplicando "la herramienta correcta para el trabajo correcto".

1. **El rol de PostgreSQL (Transaccional y Preciso):** Toda la información operativa que involucra dinero, inventario estricto y facturación (Pedidos, Mesas, Cuentas) recae sobre la base de datos relacional. Postgres nos provee transacciones ACID (consistencia pura), asegurando que un cobro jamás se pierda y que las matemáticas del restaurante sean exactas y a prueba de fallos.
2. **El rol de MongoDB (Masivo y Flexible):** Para aquellos datos que crecen exponencialmente de forma impredecible o que tienen estructuras cambiantes, usamos NoSQL. Guardar los textos largos de las reseñas o la enorme bitácora inmutable de eventos de cocina en PostgreSQL asfixiaría el disco y saturaría el servidor. MongoDB, al ser orientado a documentos, absorbe toda esta carga operativa de alto volumen con una velocidad de escritura excepcional, liberando a la base relacional principal.

**¿Cómo se comunican ambas si no hay relaciones físicas?**
Se resuelve mediante un patrón de **Referencia Lógica Cruzada**. Los documentos en Mongo (`Resena`, `EventoPedido`, `Catalogo`) guardan como atributo el `idPedido` o `idPlato` (claves tipo numérico `Long`) generados por PostgreSQL. Cuando el sistema necesita integrar la información, el código en Java actúa como el puente orquestador: consulta el ID en Mongo y lo enlaza en memoria con la información de Postgres. Esto logra un sistema completamente desacoplado y resiliente.

---

# Modelo Físico (NoSQL - MongoDB)

<p align="center">
  <img src="docs/uml/DiagramaNoRelational.png" alt="Diagrama NoSQL" />
</p>

## 📖 Explicación del Diagrama NoSQL

Este diagrama expone cómo se estructuran los datos dentro de MongoDB utilizando un Meta-Modelo Físico UML. Para visualizar las fronteras de los documentos, utilizamos el estereotipo `<<NoSql Document>>` en las tres colecciones principales: **EventoPedido**, **Resena** y **Catalogo**.

Una característica clave de este modelado es el uso del **Patrón de Diseño Embebido**, representado gráficamente por las flechas de composición (los rombos negros). Este patrón se evidencia en la colección `Catalogo`, la cual guarda en su interior ("embebido") arreglos completos de URLs de imágenes y etiquetas comerciales (`ImagenUrl` y `EtiquetaComercial`). Agrupar todos estos datos en un solo documento físico elimina la necesidad de realizar complejas uniones (*JOINs*) al momento de consultar el menú, asegurando que el restaurante pueda cargar su galería de fotos a una velocidad extremadamente alta.

## 🔑 Justificación de la Estrategia de Identificadores (MongoDB)

Alineado con la naturaleza distribuida de las bases de datos NoSQL, la generación de llaves primarias en MongoDB se maneja mediante atributos de tipo **String** anotados con `@Id`.

A diferencia del modelo numérico autoincremental de las bases de datos SQL (que dependen de un servidor centralizado para ir contando 1, 2, 3...), asignar un formato de cadena en Mongo permite que el propio framework de Spring Boot genere automáticamente un **ObjectId** de 24 caracteres alfanuméricos únicos. Esta decisión técnica garantiza matemáticamente que cada documento creado tenga un identificador universalmente único, sin importar si decenas de servidores o clientes concurrentes intentan insertar cientos de reseñas simultáneamente. Así, el sistema evita los temidos "cuellos de botella" al guardar datos masivos en milisegundos, explotando al máximo el potencial de escalabilidad horizontal que ofrece una arquitectura de documentos.

---

# Diagrama Entidad-Relación (PostgreSQL)

<p align="center">
  <img src="docs/uml/DiagramaRelational.png" alt="Diagrama Relacional" />
</p>

## 📖 Justificación de la Estructura Relacional (PostgreSQL)

La arquitectura relacional elegida para este sistema prioriza la integridad de las transacciones financieras y operativas del restaurante, utilizando los principios ACID (Atomicidad, Consistencia, Aislamiento y Durabilidad) propios de PostgreSQL. El diseño sitúa a la entidad de **Pedido** como el eje central de la operación de cocina. Este pedido se relaciona en una proporción de uno a muchos (1:N) con la entidad **Mesa**, dado que una mesa física alberga múltiples servicios a lo largo de su vida útil. A su vez, el pedido se desglosa dinámicamente en una relación de uno a muchos (1:N) con los **Ítems del Pedido**, permitiendo registrar la multiplicidad de platos ordenados por los comensales. El ciclo operativo del pedido culmina en una relación estricta de uno a uno (1:1) con la **Cuenta**, una decisión fundamental que garantiza que la facturación, los impuestos y las propinas sean únicos, indivisibles y exactos por cada servicio prestado. Por otro lado, la interacción con el cliente se estructura conectando al **Usuario** con la **Reserva** en una relación de uno a muchos (1:N), ya que un comensal registrado puede agendar múltiples visitas en distintas fechas, relacionando lógicamente dicha reserva con la capacidad de las mesas del local.

Una decisión arquitectónica destacada dentro de este modelo relacional es el **Bajo Acoplamiento (Loose Coupling)** aplicado al manejo del inventario (**Ingrediente**). En lugar de establecer una restricción física dura, como una Llave Foránea (Foreign Key) o una pesada tabla intermedia entre los ingredientes y los platos del menú, la asociación se gestiona puramente desde la capa de servicios en Java. En la base de datos, los platos almacenan las reglas italianas de preparación (como proteínas o salsas adicionales) en forma de simples listas de texto; luego, al momento de confirmarse un ítem en el pedido, el sistema localiza estos nombres en la tabla del inventario y realiza la deducción del stock en tiempo real. Esta estrategia de diseño es altamente recomendada porque evita la sobrecarga masiva de consultas relacionales (evitando costosos *JOINs* cada vez que un usuario lee el menú), y a su vez, prepara el terreno de manera perfecta para extraer todo el ecosistema de "Bodega/Inventario" hacia un microservicio independiente en futuras iteraciones, blindando así al sistema central de pedidos ante posibles rupturas de base de datos.

## 🔑 Justificación de la Estrategia de IDs (Llaves Primarias)

Respecto a la generación de identificadores, el sistema utiliza una estrategia mixta y altamente intencional:

1. **Tablas maestras y catálogos (`Long` con `GenerationType.IDENTITY`):** Entidades como `Usuario`, `Plato`, `Ingrediente`, `Mesa` o `Cuenta` implementan identificadores de tipo numérico generados por la base de datos secuencialmente. Esto garantiza operaciones de indexación ultrarrápidas y eficiencia de memoria (BIGINT) para información que es estrictamente interna y predecible.
2. **Tablas de interacción externa (`UUID` con `GenerationType.UUID`):** Entidades críticas que tienen contacto directo con clientes y que podrían sufrir ataques de "Enumeración Directa de Objetos" (IDOR), tales como `PedidoEntity` y `ReservaEntity`, utilizan Universal Unique Identifiers (UUID). Esto impide que un cliente o atacante pueda adivinar el ID del próximo pedido o de una reserva ajena, brindando una capa nativa y robusta de seguridad. Además, UUID prepara el sistema para futuros modelos de microservicios o creación de pedidos *offline*, sin riesgo de colisión.

---

# 📊 Diagramas de Secuencia del Sistema - Restaurante Bella Ciao

## 🍽️ 1. Realizar un pedido (Flujo de API REST y Manejo de Excepciones)

Modela cómo viaja la información cuando un cliente envía un pedido a través de la API REST, ilustrando la separación de responsabilidades y el escudo global de errores (`@RestControllerAdvice`).

<p align="center">
  <img src="docs/uml/DiagramaFlowPedido.png" alt="Flujo Realizar Pedido" />
</p>

### 📖 Explicación Técnica:

* **Escenario 1 (Camino Feliz - 201 Created):** El `PedidoController` recibe la solicitud y valida la estructura sintáctica (`@Valid`). Luego delega en `PedidoServiceImpl` las reglas de negocio (disponibilidad y consistencia). La persistencia la ejecuta `PedidoRepositoryJPA` contra PostgreSQL, devolviendo un DTO inmutable al cliente.
* **Escenario 2 (Control de Excepciones - 404/422):** Si un recurso no existe o se viola una restricción (como límites de toppings o mesa no disponible), el servicio lanza una excepción de dominio (`PlatoNotFoundException`). `GlobalExceptionHandler` intercepta la anomalía antes de que afecte al servidor y construye un `ErrorResponseDTO` uniforme con el código de estado HTTP adecuado.

## 🔐 2. Login usuario (Flujo de Autenticación y Autorización JWT / OAuth2)

<p align="center">
  <img src="docs/uml/DiagramaFlowJWT.png" alt="Flujo Login JWT" />
</p>

### 📖 Explicación Técnica:

* **Generación del Token (Login):** El cliente envía su correo y contraseña. `AuthenticationManager` delega la consulta a `UsuarioDetailsService`, que recupera el hash BCrypt de PostgreSQL. Una vez validada la clave, `JwtUtil` genera y firma un token JWT que incluye el rol del usuario (`ADMIN`, `CHEF`, `MESERO`, `CLIENTE`).
* **Protección sin Estado (Stateless Filter):** En cada petición subsecuente, `JwtAuthFilter` actúa como middleware interceptor antes de tocar cualquier controlador. Extrae el token del header `Authorization: Bearer`, verifica la firma criptográfica y establece el contexto de seguridad en `SecurityContextHolder`. Si el token expira o es adulterado, Spring Security bloquea el acceso con `401 Unauthorized` o `403 Forbidden`.

## 🗄️ 3. Cambios de estados de pedido (Flujo de Persistencia y NoSQL)  )

Modela cómo interactúan en simultáneo los dos motores de base de datos del restaurante: **PostgreSQL** para transacciones ACID y **MongoDB** para trazabilidad de eventos y catálogo documental.

<p align="center">
  <img src="docs/uml/DiagramaFlowPedidoEstados.png" alt="Flujo Estados de Pedido" />
</p>

### 📖 Explicación Técnica:

* **Persistencia Transaccional (PostgreSQL):** El estado operativo del pedido (`EN_PREPARACION`, `LISTO`, etc.) requiere garantías ACID (Atomicidad, Consistencia, Aislamiento y Durabilidad). Se almacena en PostgreSQL para asegurar integridad en cobros, asignación de mesas y control de cocina.
* **Persistencia Documental y Trazabilidad (MongoDB):** Cada cambio de estado genera un evento inmutable tipo bitácora (`EventoPedidoDocument`) con el estado previo, el nuevo estado, la estampa de tiempo (`timestamp`) y el operador que realizó el cambio. Se almacena en MongoDB por su alto rendimiento en operaciones de inserción continua (*append-only*) y su flexibilidad de esquema documental para auditorías forenses.

---

# 🛡️ Matriz de Roles y Autorización (RBAC) - Restaurante Bella Ciao

Esta matriz documenta el modelo de **Control de Acceso Basado en Roles (RBAC)** implementado en la API del restaurante, reflejando fielmente la seguridad configurada mediante `@PreAuthorize("hasRole(...)")` y `@PreAuthorize("hasAnyRole(...)")` en el código fuente de Spring Boot.

---

## 📋 Matriz General de Permisos por Rol

| ROL               | Qué funcionalidades PUEDE hacer                                                                                                                                                                                                                                                                                                                                                                   | Qué funcionalidades NO puede hacer                                                                                                                                                                                                                                      | Endpoints y Anotaciones Clave                                                                                                                                                  |
| ----------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **ADMIN**   | **SuperUsuario / Control Total:**• Gestiona el menú (crear, editar, desactivar y eliminar platos).• Controla el parqueadero (entrada, salida y cálculo de cobro).• Consulta reportes ejecutivos (ingresos totales, platos populares y resumen diario).• Administra usuarios y mesas.• Puede ver y operar sobre cualquier cuenta, reserva y pedido.                                    | *Ninguna restricción operativa* (Posee la jerarquía máxima del sistema).                                                                                                                                                                                            | •`@PreAuthorize("hasRole('ADMIN')")`• `/api/v1/reportes/**`• `/api/v1/vehiculos/**`• `DELETE /api/v1/platos/**`• `DELETE /api/v1/pedidos/**`                    |
| **CHEF**    | **Operación de Cocina y Menú Operativo:**• Gestiona el catálogo multimedia de platos en MongoDB (fotos y tags).• Crea nuevos platos en la carta y marca platos como *Agotado* cuando faltan insumos.• Consulta el tablero Kanban de cocina por estado (`/pedidos/cocina`).• Avanza el ciclo de preparación del pedido (*EN_PREPARACION* ➔ *LISTO*).                         | •**NO** puede eliminar platos de la base de datos.• **NO** puede ver ni cobrar cuentas de mesas.• **NO** tiene acceso a reportes financieros ni ingresos.• **NO** gestiona el parqueadero ni usuarios.                                       | •`@PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")`• `GET /api/v1/pedidos/cocina`• `PATCH /api/v1/platos/{id}/agotado`• `POST /api/v1/catalogo`                        |
| **MESERO**  | **Atención en Salón y Facturación:**• Toma pedidos presenciales y agrega/elimina ítems.• Consulta el estado y disponibilidad de las mesas del salón.• Abre cuentas en mesas (`/mesas/{id}/abrir-cuenta`).• Consulta el total en tiempo real de una cuenta y registra el pago para cerrarla y liberar la mesa.• Crea y consulta reservas de clientes presenciales o telefónicas. | •**NO** puede alterar recetas ni crear/eliminar platos del menú.• **NO** puede marcar platos como agotados ni cocinar.• **NO** puede ver balances financieros globales ni ingresos.• **NO** administra parqueadero ni usuarios del sistema. | •`@PreAuthorize("hasAnyRole('ADMIN', 'MESERO')")`• `GET /api/v1/cuentas/mesa/{id}`• `POST /api/v1/cuentas/mesa/{id}/pago`• `PATCH /api/v1/mesas/{id}/abrir-cuenta` |
| **CLIENTE** | **Autoservicio y Experiencia:**• Consulta libre del menú público disponible (`/api/v1/menu`).• Crea y consulta sus propias reservas de mesas.• Realiza sus pedidos digitales.• Consulta fotos en el catálogo de platos (MongoDB).• Califica el servicio dejando reseñas con puntaje y comentario (MongoDB).                                                                       | •**NO** puede ver cuentas de mesas ni registrar pagos.• **NO** puede avanzar estados de pedidos en cocina.• **NO** puede modificar mesas, recetas ni catálogo.• **NO** puede ver pedidos de otros comensales ni reportes.                   | •`GET /api/v1/menu` *(público / permitAll)*• `POST /api/v1/reservas`• `POST /api/v1/resenas`• `@PreAuthorize("isAuthenticated()")`                              |

---

## 🔍 Desglose Detallado Módulo por Módulo (Mapeo de Endpoints)

| Módulo / Endpoint                                            |    Método    | ADMIN | CHEF | MESERO |      CLIENTE      | Anotación de Seguridad                                       |
| ------------------------------------------------------------- | :-----------: | :---: | :--: | :----: | :---------------: | ------------------------------------------------------------- |
| **Menú Público** (`/api/v1/menu`)                   |    `GET`    |  ✅  |  ✅  |   ✅   | ✅*(Sin login)* | `permitAll()` en `SecurityConfig`                         |
| **Crear Plato** (`/api/v1/platos`)                    |   `POST`   |  ✅  |  ✅  |   ❌   |        ❌        | `@PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")`              |
| **Actualizar / Desactivar Plato**                       | `PUT/PATCH` |  ✅  |  ❌  |   ❌   |        ❌        | `@PreAuthorize("hasRole('ADMIN')")`                         |
| **Marcar Plato Agotado**                                |   `PATCH`   |  ✅  |  ✅  |   ❌   |        ❌        | `@PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")`              |
| **Eliminar Plato Definitivo**                           |  `DELETE`  |  ✅  |  ❌  |   ❌   |        ❌        | `@PreAuthorize("hasRole('ADMIN')")`                         |
| **Crear Pedido** (`/api/v1/pedidos`)                  |   `POST`   |  ✅  |  ❌  |   ✅   |        ✅        | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CLIENTE')")` |
| **Tablero de Cocina** (`/pedidos/cocina`)             |    `GET`    |  ✅  |  ✅  |   ❌   |        ❌        | `@PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")`              |
| **Avanzar Estado Pedido**                               |   `PATCH`   |  ✅  |  ✅  |   ✅   |        ❌        | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CHEF')")`    |
| **Eliminar Pedido**                                     |  `DELETE`  |  ✅  |  ❌  |   ❌   |        ❌        | `@PreAuthorize("hasRole('ADMIN')")`                         |
| **Ver y Abrir Mesas** (`/api/v1/mesas/**`)            | `GET/PATCH` |  ✅  |  ❌  |   ✅   |        ❌        | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO')")`            |
| **Ver y Pagar Cuentas** (`/api/v1/cuentas/**`)        | `GET/POST` |  ✅  |  ❌  |   ✅   |        ❌        | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO')")`            |
| **Crear Reservas** (`/api/v1/reservas`)               |   `POST`   |  ✅  |  ❌  |   ✅   |        ✅        | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CLIENTE')")` |
| **Parqueadero / Vehículos** (`/api/v1/vehiculos/**`) |   `TODOS`   |  ✅  |  ❌  |   ❌   |        ❌        | `@PreAuthorize("hasRole('ADMIN')")`                         |
| **Reportes Financieros** (`/api/v1/reportes/**`)      |    `GET`    |  ✅  |  ❌  |   ❌   |        ❌        | `@PreAuthorize("hasRole('ADMIN')")`                         |
| **Fotos Catálogo Mongo** (`/api/v1/catalogo/**`)     | `POST/PUT` |  ✅  |  ✅  |   ❌   |        ❌        | `@PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")`              |
| **Reseñas Mongo** (`/api/v1/resenas/**`)             | `POST/GET` |  ✅  |  ✅  |   ✅   |        ✅        | `@PreAuthorize("isAuthenticated()")`                        |

---

## 🔒 Mecanismos de Aplicación en el Código

1. **Filtro Stateless (`JwtAuthFilter`):** Cada petición extrae el claim `rol` del token JWT (`ROLE_ADMIN`, `ROLE_CHEF`, `ROLE_MESERO`, `ROLE_CLIENTE`).
2. **Method Security Activado:** Habilitado en `SecurityConfig` mediante `@EnableMethodSecurity(prePostEnabled = true)`.
3. **Manejo de Errores de Autorización:** Si un usuario intenta invocar un endpoint para el cual su rol no tiene privilegios, Spring Security ejecuta el handler `accessDeniedHandler` retornando automáticamente un código **`403 Forbidden`** con el mensaje estructurado en JSON.

---

# Matriz Exhaustiva de Funcionalidades y Roles (S09 - Endpoint por Endpoint)

Esta matriz detalla **absolutamente todos los endpoints** del sistema, indicando qué roles tienen acceso a cada uno, el método HTTP, la ruta exacta, la anotación de seguridad (`@PreAuthorize`) aplicada en el código fuente y su descripción funcional.

Esta matriz sirve como fuente de la verdad para las políticas de autorización (RBAC) implementadas mediante Spring Security y JWT en el proyecto.

## 1. Módulo de Autenticación (`AuthController`)

| Endpoint                  | Método HTTP | Anotación de Seguridad  | Roles Permitidos     | Descripción Funcional                                      |
| :------------------------ | :----------- | :----------------------- | :------------------- | :---------------------------------------------------------- |
| `/api/v1/auth/login`    | `POST`     | *(Ninguna - Público)* | **Cualquiera** | Autentica credenciales y genera un token JWT.               |
| `/api/v1/auth/register` | `POST`     | *(Ninguna - Público)* | **Cualquiera** | Registra un nuevo usuario con contraseña cifrada (BCrypt). |

*Nota: Las rutas `/auth` y `/api/auth` también están mapeadas hacia estos endpoints.*

## 2. Módulo de Platos y Menú

### `PlatoController` (`/api/v1/platos`)

| Endpoint                           | Método HTTP | Anotación de Seguridad                          | Roles Permitidos                    | Descripción Funcional                                                 |
| :--------------------------------- | :----------- | :----------------------------------------------- | :---------------------------------- | :--------------------------------------------------------------------- |
| `/api/v1/platos`                 | `POST`     | `@PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")` | **ADMIN, CHEF**               | Crea un nuevo plato con sus reglas de negocio (masa, salsa, toppings). |
| `/api/v1/platos`                 | `GET`      | `@PreAuthorize("isAuthenticated()")`           | **Cualquier rol autenticado** | Lista todos los platos (incluyendo inactivos/vista admin).             |
| `/api/v1/platos/{id}`            | `GET`      | `@PreAuthorize("isAuthenticated()")`           | **Cualquier rol autenticado** | Obtiene los detalles de un plato específico por su ID.                |
| `/api/v1/platos/{id}`            | `PUT`      | `@PreAuthorize("hasRole('ADMIN')")`            | **ADMIN**                     | Actualiza la información completa de un plato.                        |
| `/api/v1/platos/{id}/desactivar` | `PATCH`    | `@PreAuthorize("hasRole('ADMIN')")`            | **ADMIN**                     | Desactiva un plato sin borrar su historial (soft delete lógico).      |
| `/api/v1/platos/{id}/agotado`    | `PATCH`    | `@PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")` | **ADMIN, CHEF**               | Marca un plato como no disponible por falta de ingredientes.           |
| `/api/v1/platos/{id}`            | `DELETE`   | `@PreAuthorize("hasRole('ADMIN')")`            | **ADMIN**                     | Elimina (Hard Delete) un plato del sistema.                            |

### `MenuController` (`/api/v1/menu`)

| Endpoint         | Método HTTP | Anotación de Seguridad  | Roles Permitidos     | Descripción Funcional                                                           |
| :--------------- | :----------- | :----------------------- | :------------------- | :------------------------------------------------------------------------------- |
| `/api/v1/menu` | `GET`      | *(Ninguna - Público)* | **Cualquiera** | RF1: Retorna únicamente los platos disponibles y activos (vista para clientes). |

### `CatalogoController` (`/api/v1/catalogos`)

| Endpoint                              | Método HTTP | Anotación de Seguridad                          | Roles Permitidos                    | Descripción Funcional                                        |
| :------------------------------------ | :----------- | :----------------------------------------------- | :---------------------------------- | :------------------------------------------------------------ |
| `/api/v1/catalogos`                 | `POST`     | `@PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")` | **ADMIN, CHEF**               | Sube y crea un catálogo de fotos para un plato en MongoDB.   |
| `/api/v1/catalogos`                 | `GET`      | `@PreAuthorize("isAuthenticated()")`           | **Cualquier rol autenticado** | Lista todos los catálogos de imágenes.                      |
| `/api/v1/catalogos/plato/{idPlato}` | `GET`      | `@PreAuthorize("isAuthenticated()")`           | **Cualquier rol autenticado** | Obtiene el catálogo de imágenes asociado al ID de un plato. |
| `/api/v1/catalogos/{id}`            | `PUT`      | `@PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")` | **ADMIN, CHEF**               | Actualiza las imágenes de un catálogo existente.            |
| `/api/v1/catalogos/{id}`            | `DELETE`   | `@PreAuthorize("hasRole('ADMIN')")`            | **ADMIN**                     | Elimina un catálogo de imágenes en MongoDB.                 |

## 3. Módulo de Pedidos y Cocina (`PedidoController` -> `/api/v1/pedidos`)

| Endpoint                                      | Método HTTP | Anotación de Seguridad                                       | Roles Permitidos                 | Descripción Funcional                                                 |
| :-------------------------------------------- | :----------- | :------------------------------------------------------------ | :------------------------------- | :--------------------------------------------------------------------- |
| `/api/v1/pedidos`                           | `POST`     | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CLIENTE')")` | **ADMIN, MESERO, CLIENTE** | Crea un pedido y lo envía a cocina en estado RECIBIDO.                |
| `/api/v1/pedidos/{id}`                      | `GET`      | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CHEF')")`    | **ADMIN, MESERO, CHEF**    | Consulta un pedido específico por su ID.                              |
| `/api/v1/pedidos`                           | `GET`      | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CHEF')")`    | **ADMIN, MESERO, CHEF**    | Lista todos los pedidos registrados en el sistema.                     |
| `/api/v1/pedidos/cocina`                    | `GET`      | `@PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")`              | **ADMIN, CHEF**            | Tablero Kanban de cocina filtrado por estado de preparación.          |
| `/api/v1/pedidos/{id}/estado`               | `PATCH`    | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CHEF')")`    | **ADMIN, MESERO, CHEF**    | Avanza el estado del pedido siguiendo el flujo secuencial estricto.    |
| `/api/v1/pedidos/{id}`                      | `DELETE`   | `@PreAuthorize("hasRole('ADMIN')")`                         | **ADMIN**                  | Elimina por completo un pedido del sistema.                            |
| `/api/v1/pedidos/{idPedido}/items/{idItem}` | `DELETE`   | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CLIENTE')")` | **ADMIN, MESERO, CLIENTE** | Elimina un ítem específico dentro de un pedido y recalcula el total. |

## 4. Módulo de Mesas, Cuentas y Reservas

### `MesaController` (`/api/v1/mesas`) - *Seguridad a Nivel de Clase*

| Endpoint                            | Método HTTP | Anotación de Seguridad (Clase)                    | Roles Permitidos        | Descripción Funcional                                |
| :---------------------------------- | :----------- | :------------------------------------------------- | :---------------------- | :---------------------------------------------------- |
| `/api/v1/mesas`                   | `GET`      | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO')")` | **ADMIN, MESERO** | Lista todas las mesas del salón y su disponibilidad. |
| `/api/v1/mesas/{id}`              | `GET`      | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO')")` | **ADMIN, MESERO** | Obtiene detalles de una mesa en particular.           |
| `/api/v1/mesas/{id}/abrir-cuenta` | `PATCH`    | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO')")` | **ADMIN, MESERO** | Abre la cuenta de una mesa ocupada.                   |

### `CuentaController` (`/api/v1/cuentas`)

| Endpoint                               | Método HTTP | Anotación de Seguridad                            | Roles Permitidos        | Descripción Funcional                                             |
| :------------------------------------- | :----------- | :------------------------------------------------- | :---------------------- | :----------------------------------------------------------------- |
| `/api/v1/cuentas/mesa/{idMesa}`      | `GET`      | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO')")` | **ADMIN, MESERO** | Consulta la cuenta activa de una mesa para ver el total acumulado. |
| `/api/v1/cuentas/mesa/{idMesa}/pago` | `POST`     | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO')")` | **ADMIN, MESERO** | Cierra la cuenta y procesa el pago liberando la mesa.              |

### `ReservaController` (`/api/v1/reservas`) - *Seguridad a Nivel de Clase*

| Endpoint                           | Método HTTP | Anotación de Seguridad (Clase)                               | Roles Permitidos                 | Descripción Funcional                                        |
| :--------------------------------- | :----------- | :------------------------------------------------------------ | :------------------------------- | :------------------------------------------------------------ |
| `/api/v1/reservas`               | `POST`     | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CLIENTE')")` | **ADMIN, MESERO, CLIENTE** | Crea una reserva validando que no existan traslapes horarios. |
| `/api/v1/reservas`               | `GET`      | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CLIENTE')")` | **ADMIN, MESERO, CLIENTE** | Lista todas las reservas del restaurante.                     |
| `/api/v1/reservas/{id}`          | `GET`      | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CLIENTE')")` | **ADMIN, MESERO, CLIENTE** | Detalle de una reserva específica.                           |
| `/api/v1/reservas/{id}`          | `PUT`      | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CLIENTE')")` | **ADMIN, MESERO, CLIENTE** | Actualiza los datos de la reserva (personas, horario).        |
| `/api/v1/reservas/{id}/cancelar` | `DELETE`   | `@PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CLIENTE')")` | **ADMIN, MESERO, CLIENTE** | Cancela una reserva existente.                                |

## 5. Módulo de Feedback (`ResenaController` -> `/api/v1/resenas`)

| Endpoint                 | Método HTTP | Anotación de Seguridad                | Roles Permitidos                    | Descripción Funcional                                                                     |
| :----------------------- | :----------- | :------------------------------------- | :---------------------------------- | :----------------------------------------------------------------------------------------- |
| `/api/v1/resenas`      | `POST`     | `@PreAuthorize("isAuthenticated()")` | **Cualquier rol autenticado** | El cliente (o usuario autenticado) guarda una reseña (1 a 5 estrellas) de un pedido.      |
| `/api/v1/resenas`      | `GET`      | `@PreAuthorize("isAuthenticated()")` | **Cualquier rol autenticado** | Lista reseñas permitiendo filtrado por calificación mínima.                             |
| `/api/v1/resenas/{id}` | `GET`      | `@PreAuthorize("isAuthenticated()")` | **Cualquier rol autenticado** | Obtiene detalles de una reseña por ID en MongoDB.                                         |
| `/api/v1/resenas/{id}` | `DELETE`   | `@PreAuthorize("isAuthenticated()")` | **Cualquier rol autenticado** | Elimina una reseña (El servicio valida que el autor o un ADMIN sean quienes lo ejecuten). |

## 6. Módulo de Reportes Gerenciales (`ReporteController` -> `/api/v1/reportes`) - *Seguridad a Nivel de Clase*

| Endpoint                              | Método HTTP | Anotación de Seguridad (Clase)       | Roles Permitidos | Descripción Funcional                                         |
| :------------------------------------ | :----------- | :------------------------------------ | :--------------- | :------------------------------------------------------------- |
| `/api/v1/reportes/resumen`          | `GET`      | `@PreAuthorize("hasRole('ADMIN')")` | **ADMIN**  | Genera un reporte gerencial con el resumen operativo del día. |
| `/api/v1/reportes/platos-populares` | `GET`      | `@PreAuthorize("hasRole('ADMIN')")` | **ADMIN**  | Consulta el top de platos más vendidos.                       |
| `/api/v1/reportes/ingresos`         | `GET`      | `@PreAuthorize("hasRole('ADMIN')")` | **ADMIN**  | Calcula los ingresos totales del restaurante.                  |

## 7. Módulo de Parqueadero (`VehiculoController` -> `/api/v1/vehiculos` y `/api/v1/parqueadero`) - *Seguridad a Nivel de Clase*

| Endpoint                             | Método HTTP        | Anotación de Seguridad (Clase)       | Roles Permitidos | Descripción Funcional                                                       |
| :----------------------------------- | :------------------ | :------------------------------------ | :--------------- | :--------------------------------------------------------------------------- |
| `/api/v1/vehiculos/entrada`        | `POST`            | `@PreAuthorize("hasRole('ADMIN')")` | **ADMIN**  | Registra el ingreso de un vehículo, validando cupo máximo (20).            |
| `/api/v1/vehiculos/{id}/salida`    | `PATCH`           | `@PreAuthorize("hasRole('ADMIN')")` | **ADMIN**  | Da salida al vehículo por ID y calcula el cobro (hora fracción $3000).     |
| `/api/v1/vehiculos/salida/{placa}` | `POST`, `PATCH` | `@PreAuthorize("hasRole('ADMIN')")` | **ADMIN**  | Da salida a un vehículo usando la placa del mismo y calcula el cobro.       |
| `/api/v1/vehiculos/activos`        | `GET`             | `@PreAuthorize("hasRole('ADMIN')")` | **ADMIN**  | Lista todos los vehículos que actualmente ocupan espacio en el parqueadero. |
| `/api/v1/vehiculos`                | `GET`             | `@PreAuthorize("hasRole('ADMIN')")` | **ADMIN**  | Consulta el historial completo (entradas y salidas) del parqueadero.         |

---

## 8. Mitigación de Vulnerabilidades OWASP Top 10

| Vulnerabilidad                                                | Estrategia de Mitigación en el Código                                                                                                                                  | Ubicación (Clase/Archivo)                                                       |
| :------------------------------------------------------------ | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------- |
| **A01:2021-Broken Access Control**                      | Implementación de`SecurityFilterChain` restringiendo endpoints y anotaciones `@PreAuthorize` en controladores.                                                      | `SecurityConfig.java`, Controladores (`PlatoController`, etc.)               |
| **A02:2021-Cryptographic Failures**                     | Uso de**HTTPS (TLS/SSL)** obligatorio a través de un keystore local `.p12`. Contraseñas hasheadas con **BCryptPasswordEncoder**.                         | `application.properties`, `DataInitializer.java`, `SecurityConfig.java`    |
| **A03:2021-Injection**                                  | Uso exclusivo de Spring Data JPA/MongoDB, previniendo Inyección SQL/NoSQL mediante consultas parametrizadas internamente.                                               | Todos los repositorios (ej.`UsuarioRepositoryJPA.java`)                        |
| **A04:2021-Insecure Design**                            | Uso de DTOs (`LoginRequestDTO`, `RegisterRequestDTO`) con validaciones estrictas (`@Valid`, `@NotBlank`, `@Email`) para prevenir inyecciones desde el diseño. | `dto/*.java`, `AuthController.java`                                          |
| **A05:2021-Security Misconfiguration**                  | Configuración de cabeceras de seguridad estrictas (HSTS, X-Frame-Options, CSP) y desactivación del rastreo de pila en errores.                                         | `SecurityConfig.java`, `GlobalExceptionHandler.java`                         |
| **A06:2021-Vulnerable and Outdated Components**         | Uso de Spring Boot 3.x actualizado, gestión de dependencias centralizada y revisiones de seguridad en el ecosistema Maven (`pom.xml`).                                | `pom.xml`                                                                      |
| **A07:2021-Identification and Authentication Failures** | Uso de JWT sin estado, firmado con HMAC (HS256) usando una clave inyectada por variable de entorno (nunca hardcodeada).                                                  | `JwtUtil.java`, `JwtAuthFilter.java`                                         |
| **A08:2021-Software and Data Integrity Failures**       | Firma criptográfica inalterable (HMAC) en los tokens JWT garantizando integridad y rechazo automático ante alteraciones de sesión.                                    | `JwtUtil.java`, `JwtAuthFilter.java`                                         |
| **A09:2021-Security Logging and Monitoring Failures**   | Integración de`Slf4j` para auditoría y registro de intentos de acceso denegados (401/403) y fallos de login.                                                         | `SecurityConfig.java`, `JwtAuthFilter.java`, `GlobalExceptionHandler.java` |
| **A10:2021-Server-Side Request Forgery (SSRF)**         | Ausencia total de procesamiento de URLs externas inyectables; la API solo procesa DTOs controlados que no ejecutan solicitudes HTTP perjudiciales hacia adentro.         | Arquitectura Interna de API                                                      |

---

## 📸 Evidencias de Pruebas de Seguridad

### Evidencia 1 — Autenticación Exitosa (Login 200 OK con JWT)

* **Endpoint:** `POST https://localhost:8443/api/auth/login`
* **Descripción:** Se realiza una petición autenticada vía HTTPS enviando las credenciales válidas en el cuerpo JSON (`admin@restaurante.com` / `admin123`). Spring Security valida contra la base de datos PostgreSQL, comprueba el hash BCrypt y emite una respuesta HTTP `200 OK` junto con el token JWT firmado con el rol correspondiente (`ROLE_ADMIN`).

<p align="center">
  <img src="docs/images/LoginSuccessful.png" alt="LoginSuccessful.png" />
</p>

### Evidencia 2 — Autenticación Fallida (Login 401 Unauthorized)

* **Endpoint:** `POST https://localhost:8443/api/auth/login`
* **Descripción:** Se envía una solicitud de inicio de sesión con una contraseña incorrecta (`passwordErronea999`). Spring Security intercepta las credenciales contra el hash en base de datos, rechaza la autenticación de forma segura y devuelve un código de estado HTTP `401 Unauthorized` con el mensaje de error correspondiente, evitando la emisión de tokens y protegiendo el sistema contra accesos no autorizados.

<p align="center">
  <img src="docs/images/LoginFailed.png" alt="LoginFailed.png" />
</p>
<p align="center">
  <img src="docs/images/LoginFailed02.png" alt="LoginFailed02.png" />
</p>

### Evidencia 3 — Protección de Endpoint: Acceso Sin Token (401 Unauthorized)

* **Endpoint:** `GET https://localhost:8443/api/v1/platos`
* **Descripción:** Se intenta consultar un endpoint protegido de la API sin proveer la cabecera `Authorization` ni ningún token JWT. El filtro de seguridad `JwtAuthFilter` y el `AuthenticationEntryPoint` de Spring Security interceptan la petición antes de llegar al controlador, rechazando la solicitud con un código HTTP `401 Unauthorized` y protegiendo los recursos privados del backend.

<p align="center">
  <img src="docs/images/NoToken.png" alt="NoToken.png" />
</p>
<p align="center">
  <img src="docs/images/NoToken02.png" alt="NoToken02.png" />
</p>

### Evidencia 4 — Validación de Integridad: Token JWT Inválido (401 Unauthorized)

* **Endpoint:** `GET https://localhost:8443/api/v1/platos`
* **Cabecera:** `Authorization: Bearer tokenbasura_invalido_12345`
* **Descripción:** Se envía una solicitud con un token JWT no firmado o alterado. El filtro `JwtAuthFilter` valida la firma criptográfica mediante `JwtUtil.isValid()`, identifica la invalidez del token y rechaza la petición con código de estado HTTP `401 Unauthorized`, impidiendo accesos con firmas falsas.

<p align="center">
  <img src="docs/images/TokenInvalid01.png" alt="TokenInvalid01.png" />
</p>
<p align="center">
  <img src="docs/images/TokenInvalid02.png" alt="TokenInvalid02.png" />
</p>

### Evidencia 5 — Control de Acceso RBAC: Rol Insuficiente (403 Forbidden)

* **Endpoint:** `POST https://localhost:8443/api/v1/platos`
* **Rol utilizado:** `ROLE_CLIENTE`
* **Descripción:** Un usuario autenticado con rol `CLIENTE` intenta acceder a una operación restringida (creación de platos en el menú). La anotación `@PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")` en el controlador intercepta la solicitud, detecta que los privilegios del token no son suficientes y el `AccessDeniedHandler` responde con un código HTTP `403 Forbidden`, garantizando el principio de menor privilegio.

<p align="center">
  <img src="docs/images/AccesDenied.png" alt="AccesDenied.png" />
</p>
<p align="center">
  <img src="docs/images/AccesDenied02.png" alt="AccesDenied02.png" />
</p>

### Evidencia 6 — Expiración de Token JWT (401 Unauthorized por TTL)

* **Endpoint:** `GET https://localhost:8443/api/v1/platos`
* **Cabecera:** `Authorization: Bearer <token_expirado>`
* **Descripción:** Se envía una solicitud con un token JWT cuya fecha de expiración (claim `exp`) ya fue superada. La clase `JwtUtil` a través de `Jwts.parserBuilder()` evalúa la validez temporal y rechaza el token al detectar que ha vencido su tiempo de vida, retornando `401 Unauthorized` e impidiendo el uso indefinido de credenciales antiguas.

<p align="center">
  <img src="docs/images/TokenExpirated.png" alt="TokenExpirated.png" />
</p>
<p align="center">
  <img src="docs/images/TokenExpirated02.png" alt="TokenExpirated02.png" />
</p>
<p align="center">
  <img src="docs/images/TokenExpirated03.png" alt="TokenExpirated03.png" />
</p>

### Evidencia 7 — Control de Acceso RBAC: Rol Autorizado (201 Created)

* **Endpoint:** `POST https://localhost:8443/api/v1/platos`
* **Rol utilizado:** `ROLE_CHEF` (o `ROLE_ADMIN`)
* **Descripción:** Un usuario autenticado con un rol autorizado (`CHEF`) envía una solicitud para crear un nuevo ítem en el menú. Spring Security valida la presencia de la autoridad requerida mediante la anotación `@PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")`, permitiendo la ejecución del servicio y persistencia en la base de datos, retornando exitosamente el código HTTP `201 Created` con el recurso generado.

<p align="center">
  <img src="docs/images/AccessPermited.png" alt="AccessPermited.png" />
</p>
<p align="center">
  <img src="docs/images/AccesPermited02.png" alt="AccesPermited02.png" />
</p>
<p align="center">
  <img src="docs/images/AccesPermited03.png" alt="AccesPermited03.png" />
</p>

### Evidencia 8 — Documentación OpenAPI y Autenticación en Swagger UI (200 OK)

* **Interfaz:** `https://localhost:8443/swagger-ui/index.html`
* **Descripción:** Validación del esquema de autorización BearerAuth en Swagger UI. Se ingresa el token JWT a través del modal interactivo Authorize y se ejecuta la consulta al endpoint protegido `GET /api/v1/platos`. Swagger adjunta automáticamente la cabecera `Authorization: Bearer <token>`, el servidor autentica la petición de forma segura sobre HTTPS y devuelve una respuesta HTTP `200 OK` con los recursos del menú y los headers de seguridad correspondientes.

<p align="center">
  <img src="docs/images/SwaggerAuth03.png" alt="SwaggerAuth03.png" />
</p>
<p align="center">
  <img src="docs/images/SwaggerAuth.png" alt="SwaggerAuth.png" />
</p>
<p align="center">
  <img src="docs/images/SwaggerAuth02.png" alt="SwaggerAuth02.png" />
</p>
<p align="center">
  <img src="docs/images/SwaggerAuth04.png" alt="SwaggerAuth04.png" />
</p>

### Evidencia 9 — Almacenamiento Seguro de Contraseñas (BCrypt Hash en PostgreSQL)

* **Base de Datos:** PostgreSQL (`bellaciao_db` -> tabla `usuarios`)
* **Descripción:** Consulta directa a la base de datos para verificar el cumplimiento del estándar OWASP en el almacenamiento de credenciales. Las contraseñas se encuentran procesadas con la función hash adaptativa BCrypt (`PasswordEncoder` con factor de costo 10), iniciando con el prefijo característico `$2a$10$...`. Se evidencia que bajo ninguna circunstancia se almacenan contraseñas en texto plano ni mediante algoritmos vulnerables (como MD5 o SHA-1).

<p align="center">
  <img src="docs/images/PasswordHashBD.png" alt="PasswordHashBD.png" />
</p>

### Evidencia 10 — Cabeceras de Seguridad HTTP (OWASP Security Headers)

* **Endpoint analizado:** `https://localhost:8443/api/v1/platos`
* **Descripción:** Inspección de las cabeceras HTTP de respuesta configuradas en `SecurityConfig.java`. Se comprueba la inyección automática de cabeceras recomendadas por OWASP:
  * `X-Frame-Options: DENY` (prevención de ataques Clickjacking).
  * `X-Content-Type-Options: nosniff` (prevención de ataques MIME Sniffing).
  * `Content-Security-Policy` (mitigación de Cross-Site Scripting XSS).
  * `X-XSS-Protection: 1; mode=block` (activación del filtro XSS en navegadores legados).

<p align="center">
  <img src="docs/images/HeaderSecurity.png" alt="HeaderSecurity.png" />
</p>

### Evidencia 11 — Autenticación Federada con OAuth2 (Google Sign-In) y Emisión de JWT

* **Proveedor de Identidad:** Google Identity Services (`/oauth2/authorization/google`)
* **Descripción de la implementación:** Se implementó con éxito el flujo de autenticación OAuth2 utilizando a Google como proveedor de identidad, integrado de manera armónica con nuestro sistema Stateless de JWT.
  1. **Flujo de Autorización:** El usuario accede al endpoint `/oauth2/authorization/google`, siendo redirigido a la pantalla de consentimiento de Google (como se observa en la primera evidencia) garantizando que la aplicación no maneja directamente credenciales externas.
  2. **Generación del JWT:** Una vez que Google aprueba el inicio de sesión y retorna el `code`, nuestro componente `CustomOAuth2SuccessHandler` entra en acción. Este intercepta el éxito de OAuth2, extrae el correo del usuario y genera un JSON Web Token (JWT) propio de la aplicación (evidencia del navegador).
  3. **Persistencia Automática:** Como se observa en la evidencia de la base de datos (DBeaver), el sistema verifica la existencia del correo proveniente de Google. Si el usuario es nuevo, se registra automáticamente en la tabla `usuarios` asignándole una contraseña aleatoria encriptada con BCrypt y el rol predeterminado de `CLIENTE`.
* **Resultado:** Se logró un patrón de seguridad avanzado recomendado para APIs REST modernas: OAuth2 se encarga exclusivamente de verificar la identidad, mientras que JWT asume la responsabilidad de la autorización (RBAC) para todas las peticiones subsecuentes hacia los endpoints protegidos.

<p align="center">
  <img src="docs/images/LoginGoogle.png" alt="LoginGoogle.png" />
</p>
<p align="center">
  <img src="docs/images/LoginGoogle02.png" alt="LoginGoogle02.png" />
</p>

### Evidencia 12 — Resultado de mvn test con las pruebas de seguridad en verde

<p align="center">
  <img src="docs/images/MVNTestJWT.png" alt="TMVNTestJWT.png" />
</p>

# 🐳 Despliegue y Orquestación con Docker

Esta sección documenta la infraestructura en contenedores para el backend del restaurante **Bella Ciao**, permitiendo levantar la API y sus dos motores de bases de datos (**PostgreSQL 16** y **MongoDB 7**) de forma automática, reproducible y aislada.

---

## 1. 🏗️ Arquitectura del Stack de Contenedores

El stack está orquestado mediante `docker-compose.yml` utilizando una red bridge interna (`restaurante-net`) y volúmenes persistentes:

```text
[ Cliente / Navegador ]
     │
     ├── :8080 ────────► [ Contenedor: restaurante-api ] (Spring Boot 3.4 / JDK 21)
     │                           │                 │
     │                 (JDBC)    │                 │ (Mongo Driver)
     │                           ▼                 ▼
     ├── :5433 ────────► [ restaurante-postgres ]  [ restaurante-mongo ] ◄──── :27018
     │                         │                         │
     │                   [postgres-data]           [mongo-data]
     │                      (Volumen)                 (Volumen)
```

- **Red:** `restaurante-net` (red interna bridge que permite a la API comunicarse con `postgres:5432` y `mongo:27017` por nombre de servicio).
- **Volúmenes:** `postgres-data` y `mongo-data` garantizan la durabilidad de los datos independientemente del ciclo de vida de los contenedores.
- **Mapeo de Puertos Host:**
  - `8080:8080` → API y Swagger UI.
  - `5433:5432` → PostgreSQL (expuesto en puerto 5433 para evitar colisiones con instancias locales).
  - `27018:27017` → MongoDB (expuesto en puerto 27018 para evitar colisiones con instancias locales).

---

## 2. 🚀 Instrucciones para Levantar el Stack

### Requisitos Previos

- Docker Desktop o Docker Engine instalado y en ejecución.
- Archivo `.env` configurado en la raíz del proyecto (basado en `.env.example`).

### Comandos de Ejecución

1. **Construir y levantar todos los servicios en segundo plano:**

   ```bash
   docker compose up --build -d
   ```
2. **Verificar el estado de los contenedores:**

   ```bash
   docker compose ps
   ```
3. **Ver los logs de la API en tiempo real:**

   ```bash
   docker compose logs -f api
   ```
4. **Reiniciar únicamente el contenedor de la API:**

   ```bash
   docker compose restart api
   ```
5. **Detener el stack completo preservando los datos:**

   ```bash
   docker compose down
   ```
6. **Detener el stack eliminando los volúmenes (reset de bases de datos):**

   ```bash
   docker compose down -v
   ```

---

## 3. 🔐 Variables de Entorno Requeridas

Las siguientes variables son consumidas por el contenedor a través del perfil `application-docker.yml`:

| Variable de Entorno        | Descripción                                              | Valor por Defecto / Ejemplo    | ¿Obligatoria? |
| -------------------------- | --------------------------------------------------------- | ------------------------------ | :------------: |
| `SPRING_PROFILES_ACTIVE` | Perfil de configuración de Spring Boot para Docker       | `docker`                     |      Sí      |
| `SERVER_PORT`            | Puerto HTTP interno en el que escucha la aplicación      | `8080`                       |      Sí      |
| `DB_HOST`                | Host o nombre de servicio de PostgreSQL en la red Docker  | `postgres`                   |      Sí      |
| `DB_PORT`                | Puerto interno de PostgreSQL                              | `5432`                       |      Sí      |
| `DB_NAME`                | Nombre de la base de datos relacional                     | `restaurante`                |      Sí      |
| `DB_USER`                | Usuario administrador de PostgreSQL                       | `postgres`                   |      Sí      |
| `DB_PASSWORD`            | Contraseña de acceso a PostgreSQL                        | Definida en`.env`            |      Sí      |
| `MONGO_HOST`             | Host o nombre de servicio de MongoDB en la red Docker     | `mongo`                      |      Sí      |
| `MONGO_PORT`             | Puerto interno de MongoDB                                 | `27017`                      |      Sí      |
| `MONGO_DB`               | Nombre de la base de datos no relacional                  | `restaurante`                |      Sí      |
| `MONGO_USER`             | Usuario de autenticación en MongoDB                      | `admin`                      |      Sí      |
| `MONGO_PASSWORD`         | Contraseña de autenticación en MongoDB                  | Definida en`.env`            |      Sí      |
| `JWT_SECRET`             | Clave secreta para la firma y verificación de tokens JWT | Definida en`.env`            |      Sí      |
| `GOOGLE_CLIENT_ID`       | Identificador de cliente para inicio de sesión OAuth2    | Opcional / Definido en`.env` |       No       |
| `GOOGLE_CLIENT_SECRET`   | Clave secreta de cliente para autenticación OAuth2       | Opcional / Definido en`.env` |       No       |

---

## 4. 📸 Evidencias de Funcionamiento

### Evidencia 1: Verificación de Servicios y Salud del Stack en Docker Compose

Se ejecutó el comando `docker compose ps` para comprobar la correcta orquestación de la arquitectura multicapa. Como se evidencia en la captura, los tres servicios que componen el sistema se encuentran desplegados y activos:

* **`restaurante-postgres`:** En estado `Up (healthy)`, confirmando que superó con éxito la prueba de salud (`pg_isready -U postgres`) sobre el puerto 5432.
* **`restaurante-mongo`:** En estado `Up` y escuchando peticiones en el puerto asignado 27017.
* **`restaurante-api`:** En estado `Up`, con el puerto interno expuesto y mapeado hacia el puerto anfitrión 8080, garantizando que la aplicación Spring Boot inició únicamente tras la disponibilidad confirmada de los motores de bases de datos.

```text
CONTAINER ID   IMAGE                              COMMAND                  STATUS                    PORTS
381417c49731   bitacora_corte2_josemartinez-api   "java -jar app.jar"      Up (running)              0.0.0.0:8080->8080/tcp
4253f03f5f45   postgres:16-alpine                 "docker-entrypoint.s…"   Up (healthy)              0.0.0.0:5433->5432/tcp
4e2117b94d1e   mongo:7                            "docker-entrypoint.s…"   Up (running)              0.0.0.0:27018->27017/tcp
```

<p align="center">
  <img src="docs/images/DockerCompose.png" alt="DockerCompose.png" />
</p>

---

### Evidencia 2: Acceso y Disponibilidad de Swagger UI / OpenAPI desde Docker

Se verificó el acceso a la documentación interactiva OpenAPI/Swagger UI a través de la URL `http://localhost:8080/swagger-ui/index.html`. La captura demuestra que el contenedor `restaurante-api` expone correctamente sus servicios web hacia el host anfitrión en el puerto 8080, permitiendo visualizar e interactuar con los módulos de negocio (Reservas, Pedidos, Menú, Autenticación) sin requerir el IDE ni herramientas locales en ejecución, validando el empaquetado autónomo de la aplicación.

<p align="center">
  <img src="docs/images/SwaggerDocker.png" alt="SwaggerDocker.png" />
</p>

---

### Evidencia 3: Trazabilidad de Inicio Limpio y Sincronización de Base de Datos

Mediante la inspección de registros en tiempo real (`docker compose logs api`), se comprueba que el contenedor `restaurante-api` levantó bajo el perfil `docker` sin errores de conectividad o dependencias. En la evidencia se aprecian las sentencias SQL ejecutadas por Hibernate/JPA sobre el contenedor de PostgreSQL, validando la creación de esquemas y la ejecución exitosa del `DataInitializer` al insertar los usuarios iniciales del sistema (*Base de datos sincronizada con usuarios por defecto*). Esto certifica que la resolución de nombres en la red virtual de Docker (`restaurante-net`) y las credenciales inyectadas por variables de entorno funcionan correctamente.

<p align="center">
  <img src="docs/images/LogsBD.png" alt="LogsBD.png" />
</p>
<p align="center">
  <img src="docs/images/LogsBD02.png" alt="LogsBD02.png" />
</p>
<p align="center">
  <img src="docs/images/LogsBD03.png" alt="LogsBD03.png" />
</p>
<p align="center">
  <img src="docs/images/LogsBD04.png" alt="LogsBD04.png" />
</p>
<p align="center">
  <img src="docs/images/LogsBD05.png" alt="LogsBD05.png" />
</p>
<p align="center">
  <img src="docs/images/LogsBD06.png" alt="LogsBD06.png" />
</p>
<p align="center">
  <img src="docs/images/LogsBD07.png" alt="LogsBD07.png" />
</p>

---

### Evidencia 4: Persistencia de Datos y Comunicación Inter-Contenedor (Swagger ➔ Docker DBs)

#### 1. Objetivo de la Evidencia

Demostrar que la API empaquetada y desplegada en Docker interactúa de forma transparente y bidireccional con los contenedores de persistencia (PostgreSQL y MongoDB) a través de la red virtual de Docker (`restaurante-net`), validando que las peticiones HTTP originadas desde clientes externos (Swagger UI) persisten correctamente en los motores de base de datos y se reflejan en herramientas de administración gráfica (pgAdmin y MongoDB Compass).

---

#### 2. Procedimiento Ejecutado

1. **Consumo de Servicio vía Swagger UI (`http://localhost:8080/swagger-ui/index.html`):**

   * Se realizó la autenticación mediante token JWT en el endpoint `/api/v1/auth/login`.
   * Se ejecutó una petición `POST` a los endpoints de la API (`/api/v1/resenas`, `/api/v1/catalogos`, `/api/v1/pedidos`).
   * El contenedor `restaurante-api` procesó la solicitud, aplicó las validaciones de negocio correspondientes y retornó una respuesta exitosa con código HTTP `201 Created`.
2. **Verificación de Persistencia en PostgreSQL (pgAdmin):**

   * **Herramienta:** pgAdmin conectado al puerto expuesto en el host (`localhost:5433`), mapeado internamente al puerto `5432` del contenedor `restaurante-postgres`.
   * **Base de Datos:** `restaurante`.
   * **Resultado:** Se inspeccionó la tabla relacional, comprobando que el registro enviado desde Swagger fue almacenado con su identificador único (UUID), estampas de tiempo y campos de auditoría.
3. **Verificación de Persistencia en MongoDB (MongoDB Compass):**

   * **Herramienta:** MongoDB Compass conectado a la URI expuesta en el host: `mongodb://admin:admin@localhost:27018/`.
   * **Base de Datos:** `restaurante`.
   * **Colección:** Inspección de las colecciones documentales (`resenas`, `catalogos`, `eventos_pedido`).
   * **Resultado:** Se confirmó la persistencia del documento BSON con la información estructurada, validando el funcionamiento del repositorio no relacional.

---

#### 3. Conclusión Técnica

La prueba confirma:

* **Aislamiento y Enrutamiento:** El correcto mapeo de puertos host-contenedor (`8080:8080`, `5433:5432` y `27018:27017`), permitiendo la inspección externa sin colisionar con servicios locales del sistema operativo.
* **Red de Docker (bridge):** Resolución de nombres de dominio interna (`postgres`, `mongo`) entre servicios definidos en `docker-compose.yml`.
* **Integridad y Persistencia:** El uso de volúmenes de Docker (`postgres-data` y `mongo-data`) garantiza la durabilidad de los datos ante reinicios o recreaciones de los contenedores.

<p align="center">
  <img src="docs/images/SwaggerDockerSycn.png" alt="SwaggerDockerSycn.png" />
</p>
<p align="center">
  <img src="docs/images/SwaggerDockerSycn02.png" alt="SwaggerDockerSycn02.png" />
</p>
<p align="center">
  <img src="docs/images/SwaggerDockerSync03.png" alt="SwaggerDockerSync03.png" />
</p>
<p align="center">
  <img src="docs/images/SwaggerDockSync04.png" alt="SwaggerDockSync04.png" />
</p>
<p align="center">
  <img src="docs/images/SwaggerDockerSync05.png" alt="SwaggerDockerSync05.png" />
</p>
<p align="center">
  <img src="docs/images/SwaggerDockerSync06.png" alt="SwaggerDockerSync06.png" />
</p>
<p align="center">
  <img src="docs/images/SwaggerDockerSync07.png" alt="SwaggerDockerSync07.png" />
</p>
<p align="center">
  <img src="docs/images/SwaggerDockerSync08.png" alt="SwaggerDockerSync08.png" />
</p>
<p align="center">
  <img src="docs/images/SwaggerDockerSync09.png" alt="SwaggerDockerSync09.png" />
</p>
<p align="center">
  <img src="docs/images/SwaggerDockerSync10.png" alt="SwaggerDockerSync10.png" />
</p>
<p align="center">
  <img src="docs/images/SwaggerDocker11.png" alt="SwaggerDocker11.png" />
</p>

---

### Evidencia 5: Prueba de Resiliencia y Persistencia tras Reinicio de la API (`docker compose restart api`)

#### Objetivo

Validar que la capa de aplicación (Spring Boot) se encuentra completamente desacoplada del ciclo de vida de los datos, demostrando que el reinicio o caída del contenedor de la API no genera pérdida de información gracias al uso de volúmenes nombrados de Docker (`postgres-data` y `mongo-data`).

---

#### Procedimiento y Validación

1. Se ejecutó el comando `docker compose restart api` para forzar la detención y nuevo arranque del contenedor de la aplicación.
2. Tras inicializarse el contenedor, se verificó mediante peticiones HTTP `GET` en Swagger UI y consultas directas en pgAdmin (PostgreSQL en `localhost:5433`) y MongoDB Compass (`localhost:27018`).
3. Se constató que todos los registros creados previamente (usuarios, pedidos, platos y eventos) se mantuvieron íntegros.

---

#### Conclusión

Se cumple el principio de persistencia en entornos de contenedores: los contenedores de aplicación son descartables y efímeros (stateless), mientras que el estado de los datos reside de forma segura e independiente en el almacenamiento administrado por el host mediante volúmenes de Docker.

<p align="center">
  <img src="docs/images/DockerRestart01.png" alt="DockerRestart01.png" />
</p>
<p align="center">
  <img src="docs/images/DockerRestart02.png" alt="DockerRestart02.png" />
</p>
<p align="center">
  <img src="docs/images/DockerRestar03.png" alt="DockerRestar03.png" />
</p>
<p align="center">
  <img src="docs/images/DockerRestart04.png" alt="DockerRestart04.png" />
</p>
<p align="center">
  <img src="docs/images/DockerRestart05.png" alt="DockerRestart05.png" />
</p>
<p align="center">
  <img src="docs/images/DockerRestart06.png" alt="DockerRestart06.png" />
</p>

---

### Evidencia 6: ☁️ Publicación de Imagen en Docker Hub

Se construyó y publicó exitosamente la imagen del backend en el registro público de **Docker Hub**, utilizando una estrategia de empaquetado **multietapa (*Multi-stage build*)** con **Eclipse Temurin 21 JRE Alpine**. Gracias a esta optimización, la imagen final resultante pesa tan solo **132.2 MB**, reduciendo la superficie de ataque y garantizando tiempos de descarga mínimos.

* **Repositorio Oficial:** [hub.docker.com/r/josemartinez883/restaurante-api](https://hub.docker.com/r/josemartinez883/restaurante-api)
* **Tags Disponibles:** `latest`, `1.0`
* **Tamaño Comprimido:** `132.2 MB`
* **Comando Universal de Descarga:**
  ```bash
  docker pull josemartinez883/restaurante-api:latest
  ```

<p align="center">
  <img src="docs/images/DockerHub.png" alt="DockerHub.png" />
</p>

---

# 🚀 CI/CD y Despliegue en la Nube (Semana 10)

Esta sección documenta la infraestructura automatizada de Integración Continua y Despliegue Continuo (CI/CD) implementada mediante **GitHub Actions**, el registro de imágenes en **Docker Hub**, y el despliegue multi-ambiente en **Microsoft Azure** con persistencia políglota relacional y no relacional administrada en la nube.

---

## 📐 Diagrama de Despliegue (Arquitectura del Sistema)

El siguiente modelo ilustra la topología física y lógica de la plataforma desplegada, detallando los nodos de ejecución, artefactos empaquetados, límites de ambientes, canales de comunicación y servicios gestionados en la nube:

<p align="center">
  <img src="docs/images/DiagramaDespliegue.png" alt="Diagrama de Despliegue - Arquitectura de Despliegue Bella Ciao" width="100%" />
</p>

<p align="center"><i>Figura: Arquitectura de Despliegue Multi-Ambiente (QA y PROD) en Microsoft Azure con Pipeline Automatizado de CI/CD.</i></p>

---

### 📖 Análisis Arquitectónico y Descripción Detallada del Diagrama

El diagrama de despliegue modela la distribución física y operativa del sistema bajo los principios de computación en la nube, infraestructura como código y automatización continua:

#### 1. Nodos de Desarrollo y Control de Versiones (`Local` y `GitHub`)

* **Desarrollador (Local):** Representa la estación de trabajo del equipo de ingeniería. Los cambios se versionan atómicamente y se envían mediante Git hacia el repositorio remoto siguiendo un flujo de trabajo estructurado de ramas (`develop` y `main`).
* **GitHub Repository (`Bitacora_Corte2`):** Actúa como el centro neurálgico del control de versiones. Es el encargado de centralizar el código fuente, la gestión de ramas protegidas y los eventos de disparo (*triggers*) para los flujos de automatización.
* **GitHub Actions (Motor de CI/CD):** Nodo de computación serverless que orquesta dos tuberías de automatización desacopladas mediante archivos YAML ubicados en `.github/workflows/`:
  * `ci-qa.yml`: Pipeline continuo de integración y entrega inmediata para validación del equipo.
  * `ci-prod.yml`: Pipeline de despliegue a producción con compuertas de seguridad y aprobación manual (*Manual Approval Gate*).

#### 2. Registro Centralizado de Artefactos (`Docker Hub Registry`)

* **Artefacto de Aplicación (`josemartinez883/restaurante-api`):** Nodo intermedio fundamental que desacopla la fase de construcción de la fase de despliegue. Tras superarse exitosamente la suite de pruebas automatizadas, el pipeline empaqueta la aplicación en una imagen de contenedor inmutable y la publica en Docker Hub.
* **Estrategia de Versionamiento de Imágenes:**
  * **Ambiente QA:** Se etiquetan bajo `qa-latest` y el SHA exacto del commit (`qa-<commit-sha>`), garantizando trazabilidad y reversión rápida.
  * **Ambiente PROD:** Se etiquetan estrictamente bajo semántica de versiones `v*.*.*` y `latest` únicamente cuando se crea un tag oficial de release.

#### 3. Aislamiento Estricto de Ambientes en la Nube (`Microsoft Azure`)

La arquitectura implementa dos zonas de ejecución independientes dentro de Azure para garantizar el principio de aislamiento de entornos (*Environment Parity & Isolation*):

* **Ambiente de Aseguramiento de Calidad (QA):**

  * **Azure App Service Linux (`restaurante-api-qa.azurewebsites.net`):** Instancia de PaaS que ejecuta el contenedor Docker de Spring Boot con el perfil activo `docker` y variables inyectadas de forma segura desde GitHub Secrets.
  * **Azure Database for PostgreSQL - Flexible Server (`restaurante-db-qa`):** Servidor administrado (Tier Burstable B1ms) que soporta el esquema relacional transaccional (pedidos, mesas, cuentas y usuarios) bajo reglas de firewall que admiten conexiones cifradas exclusivas de servicios de Azure.
  * **Azure Cosmos DB for MongoDB (`restaurante-mongo-qa`):** Instancia administrada de base de datos no relacional bajo el nivel gratuito (Free Tier de 32 GiB / RU), que almacena las bitácoras documentales de eventos de pedidos, reseñas de comensales y galerías de catálogo.
* **Ambiente de Producción (PROD):**

  * **Azure App Service Linux (`restaurante-api-prod.azurewebsites.net`):** Entorno protegido donde solo reside código validado y aprobado formalmente.
  * **Azure Database for PostgreSQL - Flexible Server (`restaurante-db-prod`):** Instancia relacional independiente y aislada de QA, garantizando que pruebas destructivas o de carga en QA jamás impacten los datos productivos.
  * **Azure Cosmos DB for MongoDB (`restaurante-mongo-prod`):** Clúster NoSQL de producción con credenciales y cadenas de conexión diferenciadas.

#### 4. Protocolos de Comunicación y Seguridad de Red

* **HTTPS (Puerto 443 / TLS 1.3):** Canal de comunicación público seguro utilizado por los clientes finales, aplicaciones frontend y la interfaz interactiva de Swagger UI para invocar los endpoints RESTful de la API.
* **JDBC sobre TLS (Puerto 5432):** Conexión segura y autenticada entre el contenedor de Spring Boot y el servidor flexible de PostgreSQL en Azure, garantizando integridad ACID sobre datos financieros.
* **MongoDB Wire Protocol sobre TLS (Puerto 10255 / SCRAM-SHA-256):** Enlace directo entre los repositorios documentales de Spring Data MongoDB y el clúster de Cosmos DB, asegurando alta concurrencia de lectura/escritura.
* **Control de Acceso Basado en Roles (Azure Service Principal / RBAC):** GitHub Actions se comunica con Azure empleando un Service Principal autenticado mediante el secreto cifrado `AZURE_CREDENTIALS`, permitiendo la actualización del contenedor y las variables de configuración sin exponer contraseñas maestras de la suscripción.

---

## 🔐 Secrets Configurados en GitHub Actions

Para mantener la seguridad y evitar la exposición de credenciales sensibles en el código fuente versionado, se configuraron los siguientes Secrets en el repositorio (**Settings → Secrets and variables → Actions**):

| Secret                   | Ambiente | Propósito                                                                                  |
| :----------------------- | :------: | :------------------------------------------------------------------------------------------ |
| `DOCKERHUB_USERNAME`   |  Global  | Usuario de Docker Hub para autenticación en el registro de contenedores.                   |
| `DOCKERHUB_TOKEN`      |  Global  | Token de acceso personal (PAT) para realizar`docker push` de forma segura.                |
| `AZURE_CREDENTIALS`    |  Global  | JSON del Service Principal (RBAC) para autorizar a GitHub Actions a desplegar en Azure.     |
| `AZURE_WEBAPP_NAME_QA` |    QA    | Nombre del recurso Azure App Service para el ambiente de pruebas (`restaurante-api-qa`).  |
| `DB_HOST_QA`           |    QA    | Host del servidor Azure Database for PostgreSQL Flexible Server (`restaurante-db-qa...`). |
| `DB_PASSWORD_QA`       |    QA    | Contraseña de la base de datos PostgreSQL de QA.                                           |
| `MONGO_URI_QA`         |    QA    | Cadena de conexión cifrada hacia Azure Cosmos DB for MongoDB (QA).                         |
| `JWT_SECRET_QA`        |    QA    | Clave criptográfica para la firma y validación de tokens JWT en QA.                       |

<p align="center">
  <img src="docs/images/GitHubSecrets.png" alt="Secrets configurados en GitHub Actions" width="85%" />
</p>

<p align="center"><i>Figura: Panel de Secrets configurados en GitHub Actions (sin exposición de valores).</i></p>

---

## 🌍 Descripción y Estrategia de Ambientes (QA vs PROD)

El ciclo de vida del software sigue una separación estricta de entornos para garantizar la estabilidad, resiliencia y seguridad de la API:

| Criterio                       | 🧪 Ambiente QA (Quality Assurance)                                                                                            | 🚀 Ambiente PROD (Producción)                                                                                       |
| :----------------------------- | :---------------------------------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------------- |
| **Propósito**           | Validar nuevas funcionalidades, pruebas de integración y verificar que los cambios no rompan la API tras merges entre ramas. | Exponer la API estable, optimizada y segura para los usuarios reales y las aplicaciones cliente (Frontend / Móvil). |
| **¿Cuándo se activa?** | **Automático:** en cada `git push` a las ramas `develop` o `main`, y en Pull Requests hacia `main`.            | **Manual / Controlado:** únicamente al publicar un tag de versión semántica (ej. `v1.0.0`).               |
| **¿Qué código va?**   | El código más reciente integrado por el equipo de desarrollo.                                                               | Solo versiones estables y congeladas que ya fueron probadas y aprobadas en QA.                                       |
| **Imagen Docker**        | `josemartinez883/restaurante-api:qa-latest` y `qa-<commit-sha>`.                                                          | `josemartinez883/restaurante-api:v*.*.*` y `latest`.                                                             |
| **Aprobación**          | **Automática** — el pipeline ejecuta tests, build y despliegue sin intervención humana.                              | **Aprobación Manual Obligatoria** — se pausa en GitHub hasta que un revisor autorizado lo aprueba.           |
| **Bases de Datos**       | Instancias dedicadas de QA (`restaurante-db-qa` y `restaurante-mongo-qa`). Datos de prueba descartables.                  | Instancias dedicadas de PROD (`restaurante-db-prod` y `restaurante-mongo-prod`). Datos reales protegidos.        |
| **Monitoreo y Rollback** | Pruebas de estrés y logs de depuración activos.                                                                             | Alta disponibilidad, métricas de rendimiento y trazabilidad de versión.                                            |

---

## ☁️ Evidencias de Despliegue en Microsoft Azure

### 🌐 App Service en Azure (Contenedor en Ejecución)

Se verificó el despliegue del contenedor en el servicio **Azure App Service** (`restaurante-api-qa`), confirmando el estado activo del servicio y la ejecución del contenedor Docker provisto desde Docker Hub:

* **URL Pública:** `https://restaurante-api-qa-fbfmhyg0fufyhhaw.westus3-01.azurewebsites.net`
* **Swagger UI:** `https://restaurante-api-qa-fbfmhyg0fufyhhaw.westus3-01.azurewebsites.net/swagger-ui/index.html`
* **Estado:** En ejecución (*Running*)
* **Plataforma:** Linux Container (PaaS)

<p align="center">
  <img src="docs/images/AzureAppServiceRunning.png" alt="Azure App Service corriendo contenedor" width="90%" />
</p>

<p align="center"><i>Figura: Panel de Azure App Service ejecutando el contenedor Docker en el ambiente QA.</i></p>

---

### 🐳 Imágenes y Tags en Docker Hub

El pipeline automatizado publica las imágenes en el registro público de Docker Hub aplicando la convención de versionamiento estricta para QA:

* **Repositorio Oficial:** [hub.docker.com/r/josemartinez883/restaurante-api](https://hub.docker.com/r/josemartinez883/restaurante-api)
* **Tags Generados:**
  * `qa-latest`: Puntero dinámico a la última versión desplegada y validada en QA.
  * `qa-cdc8eb9`: Tag inmutable atado al SHA del commit específico para trazabilidad y auditoría.
* **Comando Universal de Descarga (QA):**
  ```bash
  docker pull josemartinez883/restaurante-api:qa-latest
  ```

<p align="center">
  <img src="docs/images/DockerHubTagsQA.png" alt="Tags de QA en Docker Hub" width="90%" />
</p>

<p align="center"><i>Figura: Tags de versión generados automáticamente en Docker Hub por GitHub Actions tras la ejecución exitosa del pipeline.</i></p>

---

### 🧪 Pipeline de Integración Continua y Despliegue en QA (GitHub Actions)

El flujo de trabajo `ci-qa.yml` se dispara automáticamente ante cualquier evento de `push` sobre las ramas `develop` o `main`. Se ejecutaron satisfactoriamente las tres etapas del ciclo de entrega continua:

1. **🧪 Pruebas:** Ejecución de la suite completa de pruebas unitarias y de integración con Maven (`mvn test`).
2. **🐳 Build imagen Docker:** Generación de la imagen optimizada con Docker Buildx y publicación autenticada en Docker Hub con tags `qa-latest` y el SHA del commit.
3. **🚀 Desplegar a QA:** Actualización automática del contenedor en Azure App Service (`restaurante-api-qa`) e inyección cifrada de variables de entorno (PostgreSQL, MongoDB Cosmos DB, JWT y puerto 8081).

<p align="center">
  <img src="docs/images/PipelineVerdeQA.png" alt="Pipeline de QA en verde - GitHub Actions" width="90%" />
</p>

<p align="center"><i>Figura: Ejecución exitosa del pipeline de QA con todos los jobs completados en verde.</i></p>

---

### 🛡️ Compuerta de Seguridad y Aprobación Manual (Ambiente PROD)

Alineado con las mejores prácticas de ingeniería de software para despliegues a entornos productivos, el flujo `ci-prod.yml` implementa una regla estricta de protección de entorno (**Environment Protection Rule** sobre `production`).

Tras publicarse un tag de versión (`v1.0.0`) y compilarse la imagen de producción en Docker Hub, el pipeline se pausa automáticamente a la espera de una revisión y autorización explícita antes de tocar la infraestructura productiva:

<p align="center">
  <img src="docs/images/DeployProdApproval.png" alt="Aprobación manual de despliegue a producción en GitHub Actions" width="90%" />
</p>

<p align="center"><i>Figura: Compuerta de aprobación manual pausando el despliegue a PROD hasta recibir autorización formal del administrador.</i></p>

---

### 🚀 Despliegue Exitoso en Producción (PROD)

Una vez otorgada la autorización manual por parte del revisor (`JoseMartinez883`), el flujo reanudó su ejecución de manera transparente, desplegando el contenedor en Azure App Service (`restaurante-api-prod`) y notificando el éxito del release:

<p align="center">
  <img src="docs/images/PipelineVerdeProd.png" alt="Pipeline de PROD completado en verde" width="90%" />
</p>

<p align="center"><i>Figura: Pipeline de producción completado en verde (Build, Deploy y Notificación) tras la aprobación manual.</i></p>

---

## 🌐 URLs Públicas del Sistema Desplegado (Microsoft Azure)

Los dos ambientes del sistema se encuentran activos, accesibles públicamente y conectados a sus respectivos servicios de base de datos en la nube:

|     Ambiente     | Propósito                                 | URL Pública Base                                                                                 | Swagger UI Interactivo                                                                                               |
| :---------------: | :----------------------------------------- | :------------------------------------------------------------------------------------------------ | :------------------------------------------------------------------------------------------------------------------- |
|  **🧪 QA**  | Validación y pruebas continuas del equipo | [restaurante-api-qa](https://restaurante-api-qa-fbfmhyg0fufyhhaw.westus3-01.azurewebsites.net)     | [Swagger UI - QA](https://restaurante-api-qa-fbfmhyg0fufyhhaw.westus3-01.azurewebsites.net/swagger-ui/index.html)     |
| **🚀 PROD** | API de producción para usuarios reales    | [restaurante-api-prod](https://restaurante-api-prod-ccgnexgaf8f5fna8.westus3-01.azurewebsites.net) | [Swagger UI - PROD](https://restaurante-api-prod-ccgnexgaf8f5fna8.westus3-01.azurewebsites.net/swagger-ui/index.html) |
