# capasFinalSegundo2

Proyecto Java 21 multi-módulo Maven con **arquitectura hexagonal (puertos y adaptadores)** aplicada de forma consistente a los dos bounded contexts del sistema de inventario (Productos y Movimientos). Expone una API REST pensada para ser consumida por un frontend en React.

- **Guía completa de la API REST (para el frontend): [ENDPOINTS.md](ENDPOINTS.md)**

## Requisitos

- JDK 21
- Maven 3.9+
- Docker + Docker Compose (para levantar MySQL)

## 1. Levantar la base de datos (MySQL con Docker)

Desde la carpeta `database/`:

```bash
make up       # levanta el contenedor MySQL + crea la BD "inventario" con el esquema (primera vez)
make status   # estado del contenedor
make logs     # logs en vivo
make down     # detiene el contenedor (conserva los datos)
make restart  # reinicia conservando los datos
make reset    # BORRA todo (volumen) y recrea la BD desde cero con el esquema
```

Detalles de conexión (definidos en `database/mysql/.env`):

| Parámetro | Valor |
|---|---|
| Host / puerto | `localhost:3306` |
| Base de datos | `inventario` |
| Usuario (app) | `inventario` / `inventario` |
| Root | `root` / `root` |

El esquema (tablas `producto` y `movimiento`) está en `database/mysql/init/01-schema.sql`.

## 2. Compilar y ejecutar el programa

```bash
mvn clean package          # compila y genera el jar ejecutable
java -jar aplicacion/target/aplicacion-1.0-SNAPSHOT.jar
java -jar out/artifacts/capasFinalSegundo2_jar/capasFinalSegundo2.jar
```

El punto de entrada es la clase `org.example.Aplicacion`, configurada como `<mainClass>` en `aplicacion/pom.xml` (spring-boot-maven-plugin). La app arranca en **http://localhost:8080** (puerto configurable en `aplicacion/src/main/resources/application.properties` junto con las credenciales de la BD).

## 3. Arquitectura de software

Los dos bounded contexts (Productos y Movimientos) siguen el mismo diseño desacoplado tipo **puertos y adaptadores** (hexagonal): el dominio no depende de la infraestructura (MySQL/H2) ni de la presentación, sino de abstracciones (interfaces) que la infraestructura implementa y que se inyectan en el composition root.

```
Vista Web (React)
  ├── Movimientos: controlador-1 → interface-dominio ⊸ servicio-1 → dominio-1 → (interface-dominio) ⊸ persistencia-mysql | persistencia-h2
  └── Productos:   controlador-2 → interface-producto ⊸ servicio-producto → dominio-producto → (interface-producto) ⊸ persistencia-mysql | persistencia-h2
```

| Capa | Módulos Maven (Movimientos / Productos) | Rol |
|---|---|---|
| Presentación | `controlador-1` / `controlador-2` | Reciben las peticiones HTTP y delegan a la interfaz de servicio (`IMovimientoService` / `IProductoService`). Solo dependen del módulo de interfaces, nunca de la implementación concreta. |
| Aplicación | `servicio-1` / `servicio-producto` | Orquestan casos de uso, mapean entidad↔DTO y disparan la validación de dominio (`entidad.validar()`). Dependen de la interfaz de repositorio, nunca de una clase MySQL/H2 concreta. |
| Contratos (puertos) | `interface-dominio` / `interface-producto` | Interfaces de repositorio y servicio, más los DTOs (`MovimientoDTO`, `ProductoDTO`) que desacoplan el contrato REST de la entidad de dominio. |
| Dominio | `dominio-1` / `dominio-producto` | Entidades con las reglas de negocio (`Movimiento.validar()`, `Producto.validar()`). Cero dependencias de Spring, JDBC o React. |
| Errores de dominio | `dominio-comun` | `RepositorioException`: los adaptadores de persistencia traducen aquí cualquier fallo de infraestructura (ej. `SQLException`) antes de que llegue a presentación — el mensaje crudo de la BD nunca se expone al cliente HTTP. |
| Infraestructura | `persistencia-mysql` / `persistencia-h2` | Adaptadores intercambiables que implementan `IRepositorioMovimiento` e `IRepositorioProducto`. Cuál se activa lo decide una sola propiedad (`app.persistencia.motor=mysql\|h2` en `application.properties`) — el dominio y los servicios no se tocan al cambiar de motor. |

El composition root (`aplicacion/src/main/java/org/example/config/`) es el único punto que conoce las clases concretas: `CapaConfig` arma los servicios de aplicación, y `PersistenciaMySqlConfig` / `PersistenciaH2Config` (activadas con `@ConditionalOnProperty` según `app.persistencia.motor`) arman los repositorios concretos y los inyectan por interfaz.

## 4. Endpoints

Resumen (guía completa con contratos JSON, ejemplos `fetch` y manejo de errores en **[ENDPOINTS.md](ENDPOINTS.md)**):

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/productos` | Lista todos los productos |
| `GET` | `/api/productos/{id}` | Obtiene un producto (404 si no existe) |
| `POST` | `/api/productos` | Crea un producto (201) |
| `PUT` | `/api/productos/{id}` | Actualiza un producto (400 si no existe) |
| `DELETE` | `/api/productos/{id}` | Elimina un producto (204) |
| `GET` | `/api/movimientos` | Lista todos los movimientos |
| `GET` | `/api/movimientos/{id}` | Obtiene un movimiento (404 si no existe) |
| `POST` | `/api/movimientos` | Registra un movimiento (201) |
| `DELETE` | `/api/movimientos/{id}` | Elimina un movimiento (204) |

## 5. Códigos de estado y errores

| Código | Significado |
|---|---|
| `200` | OK (respuesta con cuerpo) |
| `201` | Recurso creado (POST) |
| `204` | Sin contenido (DELETE) |
| `400` | Validación de negocio fallida; el cuerpo trae el motivo en **texto plano** |
| `404` | El recurso `{id}` no existe (cuerpo vacío) |
| `500` | Error interno (p. ej. base de datos no disponible); el cuerpo es un mensaje genérico — el detalle real queda en el log del servidor, nunca se expone crudo al cliente |
