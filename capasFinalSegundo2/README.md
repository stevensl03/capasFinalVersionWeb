# capasFinalSegundo2

Proyecto Java 21 multi-módulo Maven con **arquitectura en capas híbrida**: una aplicación web REST que combina lógica de negocio propia (desarrollada internamente en capas) con la integración de un componente externo ya empaquetado (`componente-2`, un `.jar` autocontenido). Expone una API REST pensada para ser consumida por un frontend en React.

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

La aplicación combina dos flujos independientes, cada uno con su propio camino desde la presentación hasta los datos:

```
Vista Web (React)
  ├── FLUJO 1 — Arquitectura en capas propia
  │     controlador-1 → servicio-1 → interface-dominio → dominio-1 → persistencia-mysql
  │
  └── FLUJO 2 — Integración de componente externo (.jar)
        controlador-2 → componente-2
```

### Flujo 1 — Arquitectura en capas propia (funcionalidad de movimientos)

Funcionalidad desarrollada internamente que sigue un diseño desacoplado tipo **puertos y adaptadores** (hexagonal), donde el dominio no depende de la infraestructura (MySQL) sino de abstracciones (interfaces):

| Capa | Módulo Maven | Rol |
|---|---|---|
| Presentación | `controlador-1` | Recibe las peticiones de la Vista Web y delega la lógica de negocio a `servicio-1`. |
| Aplicación | `servicio-1` | Orquesta las operaciones. No accede a la implementación del dominio directamente, sino a través de una interfaz. |
| Contratos — Interfaz / Dominio 1 | `interface-dominio` | Expone interfaces (lollipop ⊸ UML) y DTOs que desacoplan el contrato de su implementación concreta. |
| Dominio | `dominio-1` | Contiene las reglas de negocio y la lógica central (p. ej. `Movimiento.validar()`). |
| Infraestructura | `persistencia-mysql` | Adaptador MySQL que implementa `IRepositorioMovimiento`; aísla el dominio de los detalles de acceso a datos. |

Cascada de dependencias reflejada en los poms: `controlador-1` → `servicio-1` → `interface-dominio` → `dominio-1`. El adaptador `persistencia-mysql` implementa el contrato de repositorio (`interface-dominio`) y se cablea en el composition root (`aplicacion`): el dominio se comunica con la base de datos a través de la interfaz, sin depender de ella.

### Flujo 2 — Integración de componente externo (funcionalidad de productos)

El componente `componente-2` es una librería ya compilada y empaquetada (`jar`) que resuelve la funcionalidad de forma **autocontenida** (modelo, contratos, servicio y persistencia JDBC propia). `controlador-2` la consume como dependencia y actúa como un simple adaptador, sin pasar por las capas de servicio, dominio o persistencia propias.

| Capa | Módulo Maven | Rol |
|---|---|---|
| Presentación | `controlador-2` | Recibe las peticiones de la Vista Web (independiente del Flujo 1) y delega a `componente-2`. |
| Componente externo | `componente-2` | Librería `.jar` autocontenida que ya trae toda la lógica y el acceso a datos resueltos internamente. |

Cascada de dependencias: `controlador-2` → `componente-2`.

### Patrón arquitectónico resultante

- **Arquitectura en capas "hecha en casa"** (Controlador → Servicio → Dominio → Persistencia), con separación de responsabilidades y uso de interfaces para desacoplar capas — idónea para lógica de negocio propia y evolutiva.
- **Integración / reutilización** (Controlador → Componente `.jar`), donde `controlador-2` actúa como adaptador hacia un módulo externo que ya resuelve una funcionalidad completa, sin reimplementarla.

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
| `500` | Error interno (p. ej. base de datos no disponible o campo obligatorio nulo); el cuerpo trae el motivo en texto plano |
