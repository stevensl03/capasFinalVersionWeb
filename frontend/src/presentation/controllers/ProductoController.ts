import type { ProductoService } from '../../application/ProductoService'
import type { Producto, NuevoProducto } from '../../domain/producto/Producto'

/**
 * Traduce interacciones de UI (montar la lista, enviar un formulario, click en
 * eliminar) en llamadas al servicio de aplicacion. Cero logica de negocio aqui.
 */
export class ProductoController {
  private readonly servicio: ProductoService

  constructor(servicio: ProductoService) {
    this.servicio = servicio
  }

  listar(): Promise<Producto[]> {
    return this.servicio.listar()
  }

  crear(datos: NuevoProducto): Promise<Producto> {
    return this.servicio.crear(datos)
  }

  actualizar(id: number, datos: NuevoProducto): Promise<Producto> {
    return this.servicio.actualizar(id, datos)
  }

  eliminar(id: number): Promise<void> {
    return this.servicio.eliminar(id)
  }
}
