import type { IInventarioRepository } from '../../domain/inventario/IInventarioRepository'
import type { Movimiento, NuevoMovimiento } from '../../domain/inventario/Movimiento'
import { request } from './httpClient'

/** Adaptador secundario: implementa IInventarioRepository hablando con /api/movimientos. */
export class InventarioApiRepository implements IInventarioRepository {
  listar(): Promise<Movimiento[]> {
    return request<Movimiento[]>('/api/movimientos')
  }

  registrar(datos: NuevoMovimiento): Promise<Movimiento> {
    return request<Movimiento>('/api/movimientos', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(datos),
    })
  }

  eliminar(id: number): Promise<void> {
    return request<void>(`/api/movimientos/${id}`, { method: 'DELETE' })
  }
}
