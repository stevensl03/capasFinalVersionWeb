import type { IInventarioRepository } from '../domain/inventario/IInventarioRepository'
import type { Movimiento, NuevoMovimiento } from '../domain/inventario/Movimiento'

/**
 * Orquesta los casos de uso de Movimientos/Inventario. Depende del puerto
 * (IInventarioRepository), nunca de InventarioApiRepository directamente.
 */
export class InventarioService {
  private readonly repositorio: IInventarioRepository

  constructor(repositorio: IInventarioRepository) {
    this.repositorio = repositorio
  }

  listar(): Promise<Movimiento[]> {
    return this.repositorio.listar()
  }

  registrar(datos: NuevoMovimiento): Promise<Movimiento> {
    return this.repositorio.registrar(datos)
  }

  eliminar(id: number): Promise<void> {
    return this.repositorio.eliminar(id)
  }
}
