import type { InventarioService } from '../../application/InventarioService'
import type { Movimiento, NuevoMovimiento } from '../../domain/inventario/Movimiento'

/**
 * Traduce interacciones de UI (cargar historial, registrar movimiento, eliminar)
 * en llamadas al servicio de aplicacion. Cero logica de negocio aqui.
 */
export class InventarioController {
  private readonly servicio: InventarioService

  constructor(servicio: InventarioService) {
    this.servicio = servicio
  }

  listar(): Promise<Movimiento[]> {
    return this.servicio.listar()
  }

  registrar(datos: NuevoMovimiento): Promise<Movimiento> {
    return this.servicio.registrar(datos)
  }

  eliminar(id: number): Promise<void> {
    return this.servicio.eliminar(id)
  }
}
