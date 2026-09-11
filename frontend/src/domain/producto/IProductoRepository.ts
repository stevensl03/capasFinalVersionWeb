import type { Producto, NuevoProducto } from './Producto'

/**
 * Puerto del bounded context Productos. La aplicacion (ProductoService) solo conoce
 * esta interfaz; la infraestructura (ProductoApiRepository) la implementa hablando
 * con el backend hexagonal en Java, que es la fuente de verdad de las reglas de
 * negocio (Producto.validar() vive alla, no se duplica aqui).
 */
export interface IProductoRepository {
  listar(): Promise<Producto[]>
  buscarPorId(id: number): Promise<Producto>
  crear(datos: NuevoProducto): Promise<Producto>
  actualizar(id: number, datos: NuevoProducto): Promise<Producto>
  eliminar(id: number): Promise<void>
}
