export interface Producto {
  id: number
  nombre: string
  descripcion: string | null
  precio: number
  stock: number
}

export interface Movimiento {
  id: number
  productoId: number
  tipo: 'ENTRADA' | 'SALIDA'
  cantidad: number
  fecha: string
}
