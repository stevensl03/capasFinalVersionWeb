import type { IProductoRepository } from '../../domain/producto/IProductoRepository'
import type { Producto, NuevoProducto } from '../../domain/producto/Producto'
import { request } from './httpClient'

/** Adaptador secundario: implementa IProductoRepository hablando con /api/productos. */
export class ProductoApiRepository implements IProductoRepository {
  listar(): Promise<Producto[]> {
    return request<Producto[]>('/api/productos')
  }

  buscarPorId(id: number): Promise<Producto> {
    return request<Producto>(`/api/productos/${id}`)
  }

  crear(datos: NuevoProducto): Promise<Producto> {
    return request<Producto>('/api/productos', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(datos),
    })
  }

  actualizar(id: number, datos: NuevoProducto): Promise<Producto> {
    return request<Producto>(`/api/productos/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(datos),
    })
  }

  eliminar(id: number): Promise<void> {
    return request<void>(`/api/productos/${id}`, { method: 'DELETE' })
  }
}
