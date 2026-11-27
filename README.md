# API de Gestión de Órdenes de Transporte

API REST para gestionar órdenes de transporte, conductores y la asignación de conductores a órdenes, con carga de documentos (PDF) e imágenes (PNG/JPG).

## Tecnologías

- Java 17
- Spring Boot 3.5 (Web, Data JPA, Validation, Security, Actuator)
- Oracle Database 23ai Free
- JWT (jjwt 0.12)
- MapStruct 1.6 y Lombok
- springdoc-openapi (Swagger UI)
- JUnit 5 y Mockito
- Docker y Docker Compose

## Estructura del proyecto

El código está organizado por módulo de negocio:

```
src/main/java/com/movilidad/ordenes
├── order          Órdenes: entidad, flujo de estados, filtros, endpoints
├── driver         Conductores
├── assignment     Asignación de conductores y archivos adjuntos
├── security       JWT: generación, filtro, login y manejo de 401/403
├── storage        Almacenamiento de archivos
├── config         Configuración de seguridad y OpenAPI
└── common         Excepciones y manejador global de errores
```

Cada módulo sigue la misma separación: `controller` → `service` (interfaz + implementación) → `repository`, con `dto` y `mapper` para no exponer las entidades JPA.

## Requisitos

- Docker Desktop (con Docker Compose)
- Opcional, para correrlo fuera de Docker: JDK 17

## Cómo ejecutar

### Opción 1: todo con Docker (recomendado)

Desde la raíz del proyecto:

```bash
docker compose up -d --build
```

Esto levanta:

| Servicio | Puerto | Descripción |
|---|---|---|
| `oracle` | 1522 | Oracle 23ai Free (servicio `FREEPDB1`, usuario `ordenes`) |
| `app` | 8080 | La API |

La primera vez tarda unos minutos porque descarga las imágenes y compila el proyecto. La app espera a que Oracle esté listo antes de arrancar. Las tablas se crean automáticamente.

Para ver los logs y detener:

```bash
docker compose logs -f app
docker compose down
```

### Opción 2: la app local y Oracle en Docker

```bash
docker compose up -d oracle
./mvnw spring-boot:run
```

En Windows usa `.\mvnw spring-boot:run`.

## Uso de la API

**Swagger UI:** http://localhost:8080/swagger-ui.html

**Health check:** http://localhost:8080/actuator/health

### Autenticación

Todos los endpoints de `/api/**` requieren un token JWT, excepto el login.

1. Obtén un token:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

2. Envía el token en cada petición:

```
Authorization: Bearer <token>
```

En Swagger, usa el botón **Authorize** y pega solo el token.

El token dura 1 hora.

### Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/auth/login` | Obtiene un token JWT |
| POST | `/api/orders` | Crea una orden (nace en `CREATED`) |
| GET | `/api/orders/{id}` | Consulta una orden |
| GET | `/api/orders` | Lista órdenes. Filtros opcionales: `status`, `origin`, `destination`, `from`, `to` (fechas `yyyy-MM-dd`) |
| PATCH | `/api/orders/{id}/status` | Cambia el estado de una orden |
| POST | `/api/drivers` | Registra un conductor (activo por defecto) |
| GET | `/api/drivers` | Lista los conductores activos |
| POST | `/api/assignments` | Asigna un conductor a una orden |
| GET | `/api/assignments/{id}` | Consulta una asignación |
| POST | `/api/assignments/{id}/document` | Adjunta un PDF (`multipart/form-data`, campo `file`) |
| POST | `/api/assignments/{id}/image` | Adjunta una imagen PNG o JPG (`multipart/form-data`, campo `file`) |

### Flujo de estados de una orden

```
CREATED ──► IN_TRANSIT ──► DELIVERED
   │             │
   └──► CANCELLED ◄──┘
```

`DELIVERED` y `CANCELLED` son estados finales. Cualquier otra transición responde `409 Conflict`.

### Reglas de asignación

- El conductor debe estar activo.
- La orden debe estar en estado `CREATED`.
- Una orden solo puede tener una asignación.
- Los archivos se validan por extensión y tipo de contenido. Tamaño máximo: 10 MB.

### Errores

Los errores siguen el formato estándar **RFC 7807 (Problem Details)**:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Revisa los datos enviados",
  "instance": "/api/orders",
  "timestamp": "2026-11-27T05:00:00Z",
  "errors": {
    "origin": "El origen es obligatorio"
  }
}
```

| Código | Cuándo |
|---|---|
| 400 | Datos inválidos, JSON mal formado o archivo no permitido |
| 401 | Sin token, token inválido o credenciales incorrectas |
| 404 | El recurso no existe |
| 409 | Se viola una regla de negocio |
| 413 | El archivo supera el tamaño máximo |
| 500 | Error inesperado (el detalle solo queda en el log) |

## Pruebas

```bash
./mvnw test
```

Incluye pruebas unitarias con JUnit 5 y Mockito para:

- Flujo de estados de la orden (todas las transiciones)
- Servicios de órdenes, conductores y asignaciones (reglas de negocio y validación de archivos)
- Generación y validación de tokens JWT (token válido, firmado con otra clave, alterado y expirado)
- Controller de órdenes con `MockMvc` (códigos HTTP, validaciones y manejo de errores)

Las pruebas no necesitan la base de datos.

## Configuración

Todos los valores se pueden sobrescribir con variables de entorno:

| Variable | Por defecto | Descripción |
|---|---|---|
| `DB_URL` | `jdbc:oracle:thin:@localhost:1522/FREEPDB1` | URL de conexión |
| `DB_USER` | `ordenes` | Usuario de la BD |
| `DB_PASSWORD` | `ordenes` | Contraseña de la BD |
| `JWT_SECRET` | valor de desarrollo | Clave para firmar los tokens (mínimo 32 caracteres) |
| `APP_USERNAME` | `admin` | Usuario de la API |
| `APP_PASSWORD` | `admin123` | Contraseña de la API |
| `STORAGE_PATH` | `./uploads` | Carpeta donde se guardan los archivos |

Los valores por defecto son solo para desarrollo. En un ambiente real deben definirse por variables de entorno o un gestor de secretos.

## Logs

Los logs se generan en formato JSON estructurado (ECS) usando el soporte nativo de Spring Boot, listos para enviarse a herramientas como ELK u OpenSearch. Se registran las operaciones de negocio (creación, cambios de estado, asignaciones, archivos) y los errores con su contexto.

## Decisiones técnicas

- **Spring Boot 3 y `jakarta.validation`:** el enunciado menciona `javax.validation`. Desde Spring Boot 3 el paquete se llama `jakarta.validation`; las anotaciones son las mismas.
- **Creación de tablas:** se usa `spring.jpa.hibernate.ddl-auto=update` para que el proyecto se levante sin pasos manuales. En producción se usaría `validate` con scripts versionados (Flyway o Liquibase).
- **Usuarios:** para el JWT básico se usa un usuario en memoria configurable. En un sistema real los usuarios vendrían de una base de datos o de un proveedor de identidad.
- **Archivos:** se guardan en disco (volumen de Docker) con nombres generados por el sistema, para evitar colisiones y nombres maliciosos. El almacenamiento está detrás de la interfaz `FileStorageService`, así que puede cambiarse a S3 u otro servicio sin tocar la lógica de negocio.
- **Filtros de órdenes:** se usan JPA Specifications para combinar cualquier filtro opcional en una sola consulta.
- **Asignación y estado:** asignar un conductor no cambia el estado de la orden; el paso a `IN_TRANSIT` se hace con el endpoint de estado para mantener cada operación separada.