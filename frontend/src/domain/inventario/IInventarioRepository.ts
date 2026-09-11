import type { Movimiento, NuevoMovimiento } from './Movimiento'

/**
 * Puerto del bounded context Inventario/Movimientos. La regla de negocio real
 * (cantidad > 0, tipo valido, producto obligatorio) vive en Movimiento.validar()
 * del backend Java - este puerto solo desacopla la capa de aplicacion del cliente HTTP.
 */
export interface IInventarioRepository {
  listar(): Promise<Movimiento[]>
  registrar(datos: NuevoMovimiento): Promise<Movimiento>
  eliminar(id: number): Promise<void>
}
