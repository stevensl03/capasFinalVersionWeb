import type { IProductoRepository } from '../domain/producto/IProductoRepository'
import type { Producto, NuevoProducto } from '../domain/producto/Producto'

/**
 * Orquesta los casos de uso de Productos. Depende del puerto (IProductoRepository),
 * nunca de ProductoApiRepository directamente - la implementacion se inyecta desde
 * di/container.ts.
 */
export class ProductoService {
  private readonly repositorio: IProductoRepository

  constructor(repositorio: IProductoRepository) {
    this.repositorio = repositorio
  }

  listar(): Promise<Producto[]> {
    return this.repositorio.listar()
  }

  crear(datos: NuevoProducto): Promise<Producto> {
    return this.repositorio.crear(datos)
  }

  actualizar(id: number, datos: NuevoProducto): Promise<Producto> {
    return this.repositorio.actualizar(id, datos)
  }

  eliminar(id: number): Promise<void> {
    return this.repositorio.eliminar(id)
  }
}
