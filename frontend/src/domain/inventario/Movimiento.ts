export interface Movimiento {
  id: number
  productoId: number
  tipo: 'ENTRADA' | 'SALIDA'
  cantidad: number
  fecha: string
}

export interface NuevoMovimiento {
  productoId: number
  tipo: string
  cantidad: number
}
