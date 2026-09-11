import type { Producto, Movimiento } from './types'

async function request<T>(url: string, init?: RequestInit): Promise<T> {
  const res = await fetch(url, init)
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || `Error ${res.status}`)
  }
  if (res.status === 204) return undefined as T
  return res.json()
}

// Productos
export const getProductos = () => request<Producto[]>('/api/productos')
export const getProducto = (id: number) => request<Producto>(`/api/productos/${id}`)
export const crearProducto = (p: Omit<Producto, 'id'>) =>
  request<Producto>('/api/productos', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(p),
  })
export const actualizarProducto = (id: number, p: Omit<Producto, 'id'>) =>
  request<Producto>(`/api/productos/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(p),
  })
export const eliminarProducto = (id: number) =>
  request<void>(`/api/productos/${id}`, { method: 'DELETE' })

// Movimientos
export const getMovimientos = () => request<Movimiento[]>('/api/movimientos')
export const crearMovimiento = (m: { productoId: number; tipo: string; cantidad: number }) =>
  request<Movimiento>('/api/movimientos', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(m),
  })
export const eliminarMovimiento = (id: number) =>
  request<void>(`/api/movimientos/${id}`, { method: 'DELETE' })
