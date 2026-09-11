import { useState, useEffect } from 'react'
import type { Producto } from '../api/types'
import { getProductos, crearProducto, actualizarProducto, eliminarProducto } from '../api/client'
import ProductosForm from './ProductosForm'

export default function ProductosList() {
  const [productos, setProductos] = useState<Producto[]>([])
  const [editando, setEditando] = useState<Producto | null>(null)
  const [mostrarForm, setMostrarForm] = useState(false)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')

  const cargar = async () => {
    setCargando(true)
    setError('')
    try {
      setProductos(await getProductos())
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al cargar')
    } finally {
      setCargando(false)
    }
  }

  useEffect(() => { cargar() }, [])

  const handleCrear = async (datos: Omit<Producto, 'id'>) => {
    await crearProducto(datos)
    setMostrarForm(false)
    await cargar()
  }

  const handleActualizar = async (datos: Omit<Producto, 'id'>) => {
    if (!editando) return
    await actualizarProducto(editando.id, datos)
    setEditando(null)
    await cargar()
  }

  const handleEliminar = async (id: number) => {
    if (!confirm('Eliminar este producto?')) return
    await eliminarProducto(id)
    await cargar()
  }

  if (mostrarForm || editando) {
    return (
      <ProductosForm
        producto={editando}
        onGuardar={editando ? handleActualizar : handleCrear}
        onCancelar={() => { setMostrarForm(false); setEditando(null) }}
      />
    )
  }

  return (
    <div>
      <div className="header-row">
        <h2>Productos</h2>
        <button onClick={() => setMostrarForm(true)}>+ Nuevo</button>
      </div>

      {error && <p className="error">{error}</p>}
      {cargando && <p className="muted">Cargando...</p>}

      {!cargando && productos.length === 0 && (
        <p className="muted">No hay productos registrados.</p>
      )}

      {productos.length > 0 && (
        <table className="tabla">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Descripcion</th>
              <th>Precio</th>
              <th>Stock</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {productos.map(p => (
              <tr key={p.id}>
                <td>{p.nombre}</td>
                <td>{p.descripcion || <span className="muted">-</span>}</td>
                <td>${p.precio.toFixed(2)}</td>
                <td>{p.stock}</td>
                <td className="acciones">
                  <button className="btn-sm" onClick={() => setEditando(p)}>Editar</button>
                  <button className="btn-sm btn-danger" onClick={() => handleEliminar(p.id)}>
                    Eliminar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
