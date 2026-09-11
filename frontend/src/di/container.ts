import { ProductoApiRepository } from '../infrastructure/api/ProductoApiRepository'
import { InventarioApiRepository } from '../infrastructure/api/InventarioApiRepository'
import { ProductoService } from '../application/ProductoService'
import { InventarioService } from '../application/InventarioService'
import { ProductoController } from '../presentation/controllers/ProductoController'
import { InventarioController } from '../presentation/controllers/InventarioController'

/**
 * Composition root del frontend: es el unico lugar que conecta una implementacion
 * concreta de infraestructura (ProductoApiRepository) con un puerto de dominio
 * (IProductoRepository). Los componentes nunca importan de infrastructure/ ni de
 * application/ directamente - solo usan los controladores exportados aqui.
 */
const productoRepositorio = new ProductoApiRepository()
const inventarioRepositorio = new InventarioApiRepository()

const productoService = new ProductoService(productoRepositorio)
const inventarioService = new InventarioService(inventarioRepositorio)

export const productoController = new ProductoController(productoService)
export const inventarioController = new InventarioController(inventarioService)
