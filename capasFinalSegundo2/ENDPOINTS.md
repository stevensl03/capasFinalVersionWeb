# API REST — Guía para Frontend

Guía de uso de todos los endpoints de la aplicación para consumirlos desde un frontend (React u otro cliente HTTP).

## Información general

| Dato | Valor |
|---|---|
| Base URL | `http://localhost:8080` |
| Prefijo de API | `/api` |
| Formato de datos | JSON |
| Autenticación | No requiere |

- En `POST` / `PUT` enviar siempre la cabecera `Content-Type: application/json`.
- Los números se envían como números (no como strings): `"precio": 1250.5`, `"cantidad": 5`.
- Las fechas usan formato ISO local sin zona horaria: `"2026-09-10T10:00:00`.
- El puerto y las credenciales de la BD son configurables en `aplicacion/src/main/resources/application.properties`.

---

## 1. Productos — `/api/productos`

Bounded context Productos: `controlador-2` → `interface-producto` → `servicio-producto` → `dominio-producto` → `persistencia-mysql` / `persistencia-h2`.

### Estructura del recurso

```json
{
  "id": 1,
  "nombre": "Laptop",
  "descripcion": "Intel i7 16GB",
  "precio": 1250.5,
  "stock": 10
}
```

| Campo | Tipo | Requerido | Notas |
|---|---|---|---|
| `id` | number | No (en POST) | Autogenerado por la BD. No enviarlo al crear; al actualizar se toma de la URL, no del body. |
| `nombre` | string | Sí | Columna `NOT NULL` en la BD. |
| `descripcion` | string | No | Puede omitirse o ser `null`. |
| `precio` | number | Sí | Valor decimal. |
| `stock` | number | Sí | Entero. |

> Nota: `Producto.validar()` (dominio) exige `nombre` no vacío, `precio > 0` y `stock >= 0` antes de tocar la base de datos. Si algún campo es inválido, la respuesta es `400` con el motivo en texto plano.

### Endpoints

| Método | Ruta | Uso |
|---|---|---|
| `GET` | `/api/productos` | Listar todos los productos (cargar catálogo / listado principal). |
| `GET` | `/api/productos/{id}` | Obtener un producto en detalle o al editar. |
| `POST` | `/api/productos` | Crear un producto nuevo. |
| `PUT` | `/api/productos/{id}` | Actualizar un producto existente (reemplaza todos los campos). |
| `DELETE` | `/api/productos/{id}` | Eliminar un producto. |

#### `GET /api/productos` → `200`

**Cuando usarlo:** al abrir el listado de productos / inventario.

```json
[
  { "id": 1, "nombre": "Laptop", "descripcion": "Intel i7 16GB", "precio": 1250.5, "stock": 10 },
  { "id": 2, "nombre": "Mouse", "descripcion": null, "precio": 25.0, "stock": 50 }
]
```

Devuelve `[]` si no hay datos.

#### `GET /api/productos/{id}` → `200` ó `404`

**Cuando usarlo:** ficha de producto o precargar un formulario de edición.

- `200` con el producto.
- `404` con cuerpo vacío si el `id` no existe.

#### `POST /api/productos` → `201`

**Cuando usarlo:** alta de un producto nuevo.

Cuerpo (ejemplo):

```json
{ "nombre": "Teclado", "descripcion": "Mecánico", "precio": 89.9, "stock": 20 }
```

- `201` con el producto creado **incluido su `id`** asignado — usar ese `id` en el frontend (claves de listas, navegación).
- `400` con el motivo en texto plano si falla `Producto.validar()` (`nombre` vacío, `precio <= 0` o `stock < 0`).

#### `PUT /api/productos/{id}` → `200` ó `400`

**Cuando usarlo:** guardar los cambios de un producto editado. El `id` va en la URL; **no** se envía en el body.

```json
{ "nombre": "Teclado RGB", "descripcion": "Mecánico retroiluminado", "precio": 99.9, "stock": 15 }
```

- `200` con el producto actualizado.
- `400` con mensaje `El producto no existe` (texto plano) si el `id` no existe.
- Es una actualización **completa**: enviar todos los campos, no solo los modificados.

#### `DELETE /api/productos/{id}` → `204`

**Cuando usarlo:** borrar un producto.

- `204` sin cuerpo. Se devuelve `204` aunque el `id` no exista.
- **Ojo:** la base de datos no tiene clave foránea entre `movimiento` y `producto`. Eliminar un producto **no** elimina sus movimientos ni lo impide.

---

## 2. Movimientos — `/api/movimientos`

Bounded context Movimientos: `controlador-1` → `interface-dominio` → `servicio-1` → `dominio-1` → `persistencia-mysql` / `persistencia-h2`.

### Estructura del recurso

```json
{
  "id": 1,
  "productoId": 3,
  "tipo": "ENTRADA",
  "cantidad": 5,
  "fecha": "2026-09-10T10:00:00"
}
```

| Campo | Tipo | Requerido | Notas |
|---|---|---|---|
| `id` | number | No | Autogenerado por la BD. |
| `productoId` | number | Sí | Hace referencia al `id` de `/api/productos` (ver aviso de FK más abajo). |
| `tipo` | string | Sí | `ENTRADA` o `SALIDA` (no distingue mayúsculas). |
| `cantidad` | number | Sí | Entero estrictamente mayor que 0. |
| `fecha` | string | No | `yyyy-MM-dd'T'HH:mm:ss`. Si se omite, el servidor asigna la fecha/hora actual. |

> **Aviso FK:** la BD **no valida** que `productoId` exista (no hay clave foránea). El frontend debería comprobar que el producto existe (cargando el listado de productos) antes de registrar el movimiento, para evitar movimientos huérfanos.

### Endpoints

| Método | Ruta | Uso |
|---|---|---|
| `GET` | `/api/movimientos` | Listar todo el historial de movimientos de stock. |
| `GET` | `/api/movimientos/{id}` | Obtener un movimiento en detalle. |
| `POST` | `/api/movimientos` | Registrar una entrada o salida de stock. |
| `DELETE` | `/api/movimientos/{id}` | Eliminar un movimiento. |

#### `GET /api/movimientos` → `200`

**Cuando usarlo:** mostrar el historial de entradas/salidas, o recargar después de un `POST`.

```json
[
  { "id": 1, "productoId": 3, "tipo": "ENTRADA", "cantidad": 5, "fecha": "2026-09-10T10:00:00" }
]
```

#### `GET /api/movimientos/{id}` → `200` ó `404`

- `200` con el movimiento.
- `404` con cuerpo vacío si el `id` no existe.

#### `POST /api/movimientos` → `201` ó `400`

**Cuando usarlo:** registrar una entrada (`ENTRADA`) o salida (`SALIDA`) de stock de un producto. Ejemplo:

```json
{ "productoId": 3, "tipo": "entrada", "cantidad": 5 }
```

- `201` con el movimiento creado. Si no se envió `fecha`, la respuesta la incluye con la hora asignada por el servidor.
- `400` con el motivo en **texto plano** cuando falla la validación de negocio:

| Mensaje | Causa |
|---|---|
| `El producto es obligatorio` | `productoId` ausente o `null`. |
| `El tipo debe ser ENTRADA o SALIDA` | `tipo` ausente o distinto de `ENTRADA`/`SALIDA`. |
| `La cantidad debe ser mayor que cero` | `cantidad` ausente, `null` o ≤ 0. |

#### `DELETE /api/movimientos/{id}` → `204`

**Cuando usarlo:** borrar un movimiento del historial.

- `204` sin cuerpo. Se devuelve `204` aunque el `id` no exista.

---

## 3. Manejo de errores

| Código | Significado | Cuerpo |
|---|---|---|
| `200` | OK | JSON del recurso |
| `201` | Recurso creado | JSON del recurso creado |
| `204` | Sin contenido | Vacío |
| `400` | Validación fallida | **Texto plano** con el motivo (no es JSON) |
| `404` | Recurso no encontrado | Vacío |
| `500` | Error interno | **Texto plano** con el motivo (p. ej. base de datos caída, campo `NOT NULL` nulo) |

> En `400` y `500` el cuerpo es un `text/plain`, no un JSON. En el frontend, mostrar el cuerpo directamente como mensaje (no intentar hacer `JSON.parse`).

---

## 4. Ejemplos en JavaScript (`fetch`)

### Listar productos

```js
const res = await fetch("http://localhost:8080/api/productos");
if (res.ok) {
    const productos = await res.json();
    console.log(productos); // [{ id, nombre, descripcion, precio, stock }, ...]
}
```

### Crear un producto

```js
const res = await fetch("http://localhost:8080/api/productos", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ nombre: "Teclado", descripcion: "Mecánico", precio: 89.9, stock: 20 })
});
if (res.status === 201) {
    const creado = await res.json();
    console.log("id asignado:", creado.id);
}
```

### Actualizar un producto

```js
const res = await fetch(`http://localhost:8080/api/productos/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ nombre: "Teclado RGB", descripcion: "Retroiluminado", precio: 99.9, stock: 15 })
}); // 200 ok | 400 si el id no existe
```

### Eliminar un producto o movimiento

```js
await fetch(`http://localhost:8080/api/movimientos/${id}`, { method: "DELETE" }); // 204
```

### Registrar un movimiento (con manejo de error)

```js
const res = await fetch("http://localhost:8080/api/movimientos", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ productoId: 3, tipo: "ENTRADA", cantidad: 5 })
});

if (res.status === 201) {
    const mov = await res.json();
    console.log("Movimiento registrado:", mov);
} else {
    const mensaje = await res.text(); // texto plano, no JSON
    console.error("Error:", res.status, mensaje);
}
```

---

## 5. Comportamiento esperado del frontend (flujos típicos)

1. **Cargar productos:** `GET /api/productos` al montar la vista de inventario.
2. **Crear producto:** formulario → `POST /api/productos` → usar el `id` devuelto.
3. **Editar producto:** `GET /api/productos/{id}` para precargar → `PUT /api/productos/{id}` con el objeto completo al guardar → manejar `400` (`El producto no existe`) si se eliminó mientras tanto.
4. **Registrar movimiento:** seleccionar producto del listado y tipo `ENTRADA`/`SALIDA` → `POST /api/movimientos` → manejar `400` con los mensajes de validación y refrescar el historial.
5. **Historial:** `GET /api/movimientos` → mostrar `tipo`, `cantidad` y `fecha`; enlazar `productoId` con el listado de productos para mostrar el nombre.# API REST — Guía para Frontend

Guía de uso de todos los endpoints de la aplicación para consumirlos desde un frontend (React u otro cliente HTTP).

## Información general

| Dato | Valor |
|---|---|
| Base URL | `http://localhost:8080` |
| Prefijo de API | `/api` |
| Formato de datos | JSON |
| Autenticación | No requiere |

- En `POST` / `PUT` enviar siempre la cabecera `Content-Type: application/json`.
- Los números se envían como números (no como strings): `"precio": 1250.5`, `"cantidad": 5`.
- Las fechas usan formato ISO local sin zona horaria: `"2026-09-10T10:00:00`.
- El puerto y las credenciales de la BD son configurables en `aplicacion/src/main/resources/application.properties`.

---

## 1. Productos — `/api/productos`

Bounded context Productos: `controlador-2` → `interface-producto` → `servicio-producto` → `dominio-producto` → `persistencia-mysql` / `persistencia-h2`.

### Estructura del recurso

```json
{
  "id": 1,
  "nombre": "Laptop",
  "descripcion": "Intel i7 16GB",
  "precio": 1250.5,
  "stock": 10
}
```

| Campo | Tipo | Requerido | Notas |
|---|---|---|---|
| `id` | number | No (en POST) | Autogenerado por la BD. No enviarlo al crear; al actualizar se toma de la URL, no del body. |
| `nombre` | string | Sí | Columna `NOT NULL` en la BD. |
| `descripcion` | string | No | Puede omitirse o ser `null`. |
| `precio` | number | Sí | Valor decimal. |
| `stock` | number | Sí | Entero. |

> Nota: `Producto.validar()` (dominio) exige `nombre` no vacío, `precio > 0` y `stock >= 0` antes de tocar la base de datos. Si algún campo es inválido, la respuesta es `400` con el motivo en texto plano.

### Endpoints

| Método | Ruta | Uso |
|---|---|---|
| `GET` | `/api/productos` | Listar todos los productos (cargar catálogo / listado principal). |
| `GET` | `/api/productos/{id}` | Obtener un producto en detalle o al editar. |
| `POST` | `/api/productos` | Crear un producto nuevo. |
| `PUT` | `/api/productos/{id}` | Actualizar un producto existente (reemplaza todos los campos). |
| `DELETE` | `/api/productos/{id}` | Eliminar un producto. |

#### `GET /api/productos` → `200`

**Cuando usarlo:** al abrir el listado de productos / inventario.

```json
[
  { "id": 1, "nombre": "Laptop", "descripcion": "Intel i7 16GB", "precio": 1250.5, "stock": 10 },
  { "id": 2, "nombre": "Mouse", "descripcion": null, "precio": 25.0, "stock": 50 }
]
```

Devuelve `[]` si no hay datos.

#### `GET /api/productos/{id}` → `200` ó `404`

**Cuando usarlo:** ficha de producto o precargar un formulario de edición.

- `200` con el producto.
- `404` con cuerpo vacío si el `id` no existe.

#### `POST /api/productos` → `201`

**Cuando usarlo:** alta de un producto nuevo.

Cuerpo (ejemplo):

```json
{ "nombre": "Teclado", "descripcion": "Mecánico", "precio": 89.9, "stock": 20 }
```

- `201` con el producto creado **incluido su `id`** asignado — usar ese `id` en el frontend (claves de listas, navegación).
- `400` con el motivo en texto plano si falla `Producto.validar()` (`nombre` vacío, `precio <= 0` o `stock < 0`).

#### `PUT /api/productos/{id}` → `200` ó `400`

**Cuando usarlo:** guardar los cambios de un producto editado. El `id` va en la URL; **no** se envía en el body.

```json
{ "nombre": "Teclado RGB", "descripcion": "Mecánico retroiluminado", "precio": 99.9, "stock": 15 }
```

- `200` con el producto actualizado.
- `400` con mensaje `El producto no existe` (texto plano) si el `id` no existe.
- Es una actualización **completa**: enviar todos los campos, no solo los modificados.

#### `DELETE /api/productos/{id}` → `204`

**Cuando usarlo:** borrar un producto.

- `204` sin cuerpo. Se devuelve `204` aunque el `id` no exista.
- **Ojo:** la base de datos no tiene clave foránea entre `movimiento` y `producto`. Eliminar un producto **no** elimina sus movimientos ni lo impide.

---

## 2. Movimientos — `/api/movimientos`

Bounded context Movimientos: `controlador-1` → `interface-dominio` → `servicio-1` → `dominio-1` → `persistencia-mysql` / `persistencia-h2`.

### Estructura del recurso

```json
{
  "id": 1,
  "productoId": 3,
  "tipo": "ENTRADA",
  "cantidad": 5,
  "fecha": "2026-09-10T10:00:00"
}
```

| Campo | Tipo | Requerido | Notas |
|---|---|---|---|
| `id` | number | No | Autogenerado por la BD. |
| `productoId` | number | Sí | Hace referencia al `id` de `/api/productos` (ver aviso de FK más abajo). |
| `tipo` | string | Sí | `ENTRADA` o `SALIDA` (no distingue mayúsculas). |
| `cantidad` | number | Sí | Entero estrictamente mayor que 0. |
| `fecha` | string | No | `yyyy-MM-dd'T'HH:mm:ss`. Si se omite, el servidor asigna la fecha/hora actual. |

> **Aviso FK:** la BD **no valida** que `productoId` exista (no hay clave foránea). El frontend debería comprobar que el producto existe (cargando el listado de productos) antes de registrar el movimiento, para evitar movimientos huérfanos.

### Endpoints

| Método | Ruta | Uso |
|---|---|---|
| `GET` | `/api/movimientos` | Listar todo el historial de movimientos de stock. |
| `GET` | `/api/movimientos/{id}` | Obtener un movimiento en detalle. |
| `POST` | `/api/movimientos` | Registrar una entrada o salida de stock. |
| `DELETE` | `/api/movimientos/{id}` | Eliminar un movimiento. |

#### `GET /api/movimientos` → `200`

**Cuando usarlo:** mostrar el historial de entradas/salidas, o recargar después de un `POST`.

```json
[
  { "id": 1, "productoId": 3, "tipo": "ENTRADA", "cantidad": 5, "fecha": "2026-09-10T10:00:00" }
]
```

#### `GET /api/movimientos/{id}` → `200` ó `404`

- `200` con el movimiento.
- `404` con cuerpo vacío si el `id` no existe.

#### `POST /api/movimientos` → `201` ó `400`

**Cuando usarlo:** registrar una entrada (`ENTRADA`) o salida (`SALIDA`) de stock de un producto. Ejemplo:

```json
{ "productoId": 3, "tipo": "entrada", "cantidad": 5 }
```

- `201` con el movimiento creado. Si no se envió `fecha`, la respuesta la incluye con la hora asignada por el servidor.
- `400` con el motivo en **texto plano** cuando falla la validación de negocio:

| Mensaje | Causa |
|---|---|
| `El producto es obligatorio` | `productoId` ausente o `null`. |
| `El tipo debe ser ENTRADA o SALIDA` | `tipo` ausente o distinto de `ENTRADA`/`SALIDA`. |
| `La cantidad debe ser mayor que cero` | `cantidad` ausente, `null` o ≤ 0. |

#### `DELETE /api/movimientos/{id}` → `204`

**Cuando usarlo:** borrar un movimiento del historial.

- `204` sin cuerpo. Se devuelve `204` aunque el `id` no exista.

---

## 3. Manejo de errores

| Código | Significado | Cuerpo |
|---|---|---|
| `200` | OK | JSON del recurso |
| `201` | Recurso creado | JSON del recurso creado |
| `204` | Sin contenido | Vacío |
| `400` | Validación fallida | **Texto plano** con el motivo (no es JSON) |
| `404` | Recurso no encontrado | Vacío |
| `500` | Error interno | **Texto plano** con el motivo (p. ej. base de datos caída, campo `NOT NULL` nulo) |

> En `400` y `500` el cuerpo es un `text/plain`, no un JSON. En el frontend, mostrar el cuerpo directamente como mensaje (no intentar hacer `JSON.parse`).

---

## 4. Ejemplos en JavaScript (`fetch`)

### Listar productos

```js
const res = await fetch("http://localhost:8080/api/productos");
if (res.ok) {
  const productos = await res.json();
  console.log(productos); // [{ id, nombre, descripcion, precio, stock }, ...]
}
```

### Crear un producto

```js
const res = await fetch("http://localhost:8080/api/productos", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ nombre: "Teclado", descripcion: "Mecánico", precio: 89.9, stock: 20 })
});
if (res.status === 201) {
  const creado = await res.json();
  console.log("id asignado:", creado.id);
}
```

### Actualizar un producto

```js
const res = await fetch(`http://localhost:8080/api/productos/${id}`, {
  method: "PUT",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ nombre: "Teclado RGB", descripcion: "Retroiluminado", precio: 99.9, stock: 15 })
}); // 200 ok | 400 si el id no existe
```

### Eliminar un producto o movimiento

```js
await fetch(`http://localhost:8080/api/movimientos/${id}`, { method: "DELETE" }); // 204
```

### Registrar un movimiento (con manejo de error)

```js
const res = await fetch("http://localhost:8080/api/movimientos", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ productoId: 3, tipo: "ENTRADA", cantidad: 5 })
});

if (res.status === 201) {
  const mov = await res.json();
  console.log("Movimiento registrado:", mov);
} else {
  const mensaje = await res.text(); // texto plano, no JSON
  console.error("Error:", res.status, mensaje);
}
```

---

## 5. Comportamiento esperado del frontend (flujos típicos)

1. **Cargar productos:** `GET /api/productos` al montar la vista de inventario.
2. **Crear producto:** formulario → `POST /api/productos` → usar el `id` devuelto.
3. **Editar producto:** `GET /api/productos/{id}` para precargar → `PUT /api/productos/{id}` con el objeto completo al guardar → manejar `400` (`El producto no existe`) si se eliminó mientras tanto.
4. **Registrar movimiento:** seleccionar producto del listado y tipo `ENTRADA`/`SALIDA` → `POST /api/movimientos` → manejar `400` con los mensajes de validación y refrescar el historial.
5. **Historial:** `GET /api/movimientos` → mostrar `tipo`, `cantidad` y `fecha`; enlazar `productoId` con el listado de productos para mostrar el nombre.# API REST — Guía para Frontend

Guía de uso de todos los endpoints de la aplicación para consumirlos desde un frontend (React u otro cliente HTTP).

## Información general

| Dato | Valor |
|---|---|
| Base URL | `http://localhost:8080` |
| Prefijo de API | `/api` |
| Formato de datos | JSON |
| Autenticación | No requiere |

- En `POST` / `PUT` enviar siempre la cabecera `Content-Type: application/json`.
- Los números se envían como números (no como strings): `"precio": 1250.5`, `"cantidad": 5`.
- Las fechas usan formato ISO local sin zona horaria: `"2026-09-10T10:00:00`.
- El puerto y las credenciales de la BD son configurables en `aplicacion/src/main/resources/application.properties`.

---

## 1. Productos — `/api/productos`

Bounded context Productos: `controlador-2` → `interface-producto` → `servicio-producto` → `dominio-producto` → `persistencia-mysql` / `persistencia-h2`.

### Estructura del recurso

```json
{
  "id": 1,
  "nombre": "Laptop",
  "descripcion": "Intel i7 16GB",
  "precio": 1250.5,
  "stock": 10
}
```

| Campo | Tipo | Requerido | Notas |
|---|---|---|---|
| `id` | number | No (en POST) | Autogenerado por la BD. No enviarlo al crear; al actualizar se toma de la URL, no del body. |
| `nombre` | string | Sí | Columna `NOT NULL` en la BD. |
| `descripcion` | string | No | Puede omitirse o ser `null`. |
| `precio` | number | Sí | Valor decimal. |
| `stock` | number | Sí | Entero. |

> Nota: `Producto.validar()` (dominio) exige `nombre` no vacío, `precio > 0` y `stock >= 0` antes de tocar la base de datos. Si algún campo es inválido, la respuesta es `400` con el motivo en texto plano.

### Endpoints

| Método | Ruta | Uso |
|---|---|---|
| `GET` | `/api/productos` | Listar todos los productos (cargar catálogo / listado principal). |
| `GET` | `/api/productos/{id}` | Obtener un producto en detalle o al editar. |
| `POST` | `/api/productos` | Crear un producto nuevo. |
| `PUT` | `/api/productos/{id}` | Actualizar un producto existente (reemplaza todos los campos). |
| `DELETE` | `/api/productos/{id}` | Eliminar un producto. |

#### `GET /api/productos` → `200`

**Cuando usarlo:** al abrir el listado de productos / inventario.

```json
[
  { "id": 1, "nombre": "Laptop", "descripcion": "Intel i7 16GB", "precio": 1250.5, "stock": 10 },
  { "id": 2, "nombre": "Mouse", "descripcion": null, "precio": 25.0, "stock": 50 }
]
```

Devuelve `[]` si no hay datos.

#### `GET /api/productos/{id}` → `200` ó `404`

**Cuando usarlo:** ficha de producto o precargar un formulario de edición.

- `200` con el producto.
- `404` con cuerpo vacío si el `id` no existe.

#### `POST /api/productos` → `201`

**Cuando usarlo:** alta de un producto nuevo.

Cuerpo (ejemplo):

```json
{ "nombre": "Teclado", "descripcion": "Mecánico", "precio": 89.9, "stock": 20 }
```

- `201` con el producto creado **incluido su `id`** asignado — usar ese `id` en el frontend (claves de listas, navegación).
- `400` con el motivo en texto plano si falla `Producto.validar()` (`nombre` vacío, `precio <= 0` o `stock < 0`).

#### `PUT /api/productos/{id}` → `200` ó `400`

**Cuando usarlo:** guardar los cambios de un producto editado. El `id` va en la URL; **no** se envía en el body.

```json
{ "nombre": "Teclado RGB", "descripcion": "Mecánico retroiluminado", "precio": 99.9, "stock": 15 }
```

- `200` con el producto actualizado.
- `400` con mensaje `El producto no existe` (texto plano) si el `id` no existe.
- Es una actualización **completa**: enviar todos los campos, no solo los modificados.

#### `DELETE /api/productos/{id}` → `204`

**Cuando usarlo:** borrar un producto.

- `204` sin cuerpo. Se devuelve `204` aunque el `id` no exista.
- **Ojo:** la base de datos no tiene clave foránea entre `movimiento` y `producto`. Eliminar un producto **no** elimina sus movimientos ni lo impide.

---

## 2. Movimientos — `/api/movimientos`

Bounded context Movimientos: `controlador-1` → `interface-dominio` → `servicio-1` → `dominio-1` → `persistencia-mysql` / `persistencia-h2`.

### Estructura del recurso

```json
{
  "id": 1,
  "productoId": 3,
  "tipo": "ENTRADA",
  "cantidad": 5,
  "fecha": "2026-09-10T10:00:00"
}
```

| Campo | Tipo | Requerido | Notas |
|---|---|---|---|
| `id` | number | No | Autogenerado por la BD. |
| `productoId` | number | Sí | Hace referencia al `id` de `/api/productos` (ver aviso de FK más abajo). |
| `tipo` | string | Sí | `ENTRADA` o `SALIDA` (no distingue mayúsculas). |
| `cantidad` | number | Sí | Entero estrictamente mayor que 0. |
| `fecha` | string | No | `yyyy-MM-dd'T'HH:mm:ss`. Si se omite, el servidor asigna la fecha/hora actual. |

> **Aviso FK:** la BD **no valida** que `productoId` exista (no hay clave foránea). El frontend debería comprobar que el producto existe (cargando el listado de productos) antes de registrar el movimiento, para evitar movimientos huérfanos.

### Endpoints

| Método | Ruta | Uso |
|---|---|---|
| `GET` | `/api/movimientos` | Listar todo el historial de movimientos de stock. |
| `GET` | `/api/movimientos/{id}` | Obtener un movimiento en detalle. |
| `POST` | `/api/movimientos` | Registrar una entrada o salida de stock. |
| `DELETE` | `/api/movimientos/{id}` | Eliminar un movimiento. |

#### `GET /api/movimientos` → `200`

**Cuando usarlo:** mostrar el historial de entradas/salidas, o recargar después de un `POST`.

```json
[
  { "id": 1, "productoId": 3, "tipo": "ENTRADA", "cantidad": 5, "fecha": "2026-09-10T10:00:00" }
]
```

#### `GET /api/movimientos/{id}` → `200` ó `404`

- `200` con el movimiento.
- `404` con cuerpo vacío si el `id` no existe.

#### `POST /api/movimientos` → `201` ó `400`

**Cuando usarlo:** registrar una entrada (`ENTRADA`) o salida (`SALIDA`) de stock de un producto. Ejemplo:

```json
{ "productoId": 3, "tipo": "entrada", "cantidad": 5 }
```

- `201` con el movimiento creado. Si no se envió `fecha`, la respuesta la incluye con la hora asignada por el servidor.
- `400` con el motivo en **texto plano** cuando falla la validación de negocio:

| Mensaje | Causa |
|---|---|
| `El producto es obligatorio` | `productoId` ausente o `null`. |
| `El tipo debe ser ENTRADA o SALIDA` | `tipo` ausente o distinto de `ENTRADA`/`SALIDA`. |
| `La cantidad debe ser mayor que cero` | `cantidad` ausente, `null` o ≤ 0. |

#### `DELETE /api/movimientos/{id}` → `204`

**Cuando usarlo:** borrar un movimiento del historial.

- `204` sin cuerpo. Se devuelve `204` aunque el `id` no exista.

---

## 3. Manejo de errores

| Código | Significado | Cuerpo |
|---|---|---|
| `200` | OK | JSON del recurso |
| `201` | Recurso creado | JSON del recurso creado |
| `204` | Sin contenido | Vacío |
| `400` | Validación fallida | **Texto plano** con el motivo (no es JSON) |
| `404` | Recurso no encontrado | Vacío |
| `500` | Error interno | **Texto plano** con el motivo (p. ej. base de datos caída, campo `NOT NULL` nulo) |

> En `400` y `500` el cuerpo es un `text/plain`, no un JSON. En el frontend, mostrar el cuerpo directamente como mensaje (no intentar hacer `JSON.parse`).

---

## 4. Ejemplos en JavaScript (`fetch`)

### Listar productos

```js
const res = await fetch("http://localhost:8080/api/productos");
if (res.ok) {
  const productos = await res.json();
  console.log(productos); // [{ id, nombre, descripcion, precio, stock }, ...]
}
```

### Crear un producto

```js
const res = await fetch("http://localhost:8080/api/productos", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ nombre: "Teclado", descripcion: "Mecánico", precio: 89.9, stock: 20 })
});
if (res.status === 201) {
  const creado = await res.json();
  console.log("id asignado:", creado.id);
}
```

### Actualizar un producto

```js
const res = await fetch(`http://localhost:8080/api/productos/${id}`, {
  method: "PUT",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ nombre: "Teclado RGB", descripcion: "Retroiluminado", precio: 99.9, stock: 15 })
}); // 200 ok | 400 si el id no existe
```

### Eliminar un producto o movimiento

```js
await fetch(`http://localhost:8080/api/movimientos/${id}`, { method: "DELETE" }); // 204
```

### Registrar un movimiento (con manejo de error)

```js
const res = await fetch("http://localhost:8080/api/movimientos", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ productoId: 3, tipo: "ENTRADA", cantidad: 5 })
});

if (res.status === 201) {
  const mov = await res.json();
  console.log("Movimiento registrado:", mov);
} else {
  const mensaje = await res.text(); // texto plano, no JSON
  console.error("Error:", res.status, mensaje);
}
```

---

## 5. Comportamiento esperado del frontend (flujos típicos)

1. **Cargar productos:** `GET /api/productos` al montar la vista de inventario.
2. **Crear producto:** formulario → `POST /api/productos` → usar el `id` devuelto.
3. **Editar producto:** `GET /api/productos/{id}` para precargar → `PUT /api/productos/{id}` con el objeto completo al guardar → manejar `400` (`El producto no existe`) si se eliminó mientras tanto.
4. **Registrar movimiento:** seleccionar producto del listado y tipo `ENTRADA`/`SALIDA` → `POST /api/movimientos` → manejar `400` con los mensajes de validación y refrescar el historial.
5. **Historial:** `GET /api/movimientos` → mostrar `tipo`, `cantidad` y `fecha`; enlazar `productoId` con el listado de productos para mostrar el nombre.