import { useState, useEffect } from 'react'
import type { Movimiento } from '../../domain/inventario/Movimiento'
import type { Producto } from '../../domain/producto/Producto'
import { inventarioController, productoController } from '../../di/container'
import MovimientosForm from './MovimientosForm'

export default function MovimientosList() {
  const [movimientos, setMovimientos] = useState<Movimiento[]>([])
  const [productos, setProductos] = useState<Producto[]>([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')

  const cargar = async () => {
    setCargando(true)
    setError('')
    try {
      const [m, p] = await Promise.all([inventarioController.listar(), productoController.listar()])
      setMovimientos(m)
      setProductos(p)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al cargar')
    } finally {
      setCargando(false)
    }
  }

  useEffect(() => { cargar() }, [])

  const nombreProducto = (id: number) => {
    const p = productos.find(x => x.id === id)
    return p ? p.nombre : `#${id}`
  }

  const handleEliminar = async (id: number) => {
    if (!confirm('Eliminar este movimiento?')) return
    await inventarioController.eliminar(id)
    await cargar()
  }

  const formatearFecha = (iso: string) => {
    const d = new Date(iso + 'Z')
    return d.toLocaleString('es-ES')
  }

  return (
    <div>
      <MovimientosForm productos={productos} onCreado={cargar} />

      <div className="header-row">
        <h2>Historial de Movimientos</h2>
      </div>

      {error && <p className="error">{error}</p>}
      {cargando && <p className="muted">Cargando...</p>}

      {!cargando && movimientos.length === 0 && (
        <p className="muted">No hay movimientos registrados.</p>
      )}

      {movimientos.length > 0 && (
        <table className="tabla">
          <thead>
            <tr>
              <th>Producto</th>
              <th>Tipo</th>
              <th>Cantidad</th>
              <th>Fecha</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {movimientos.map(m => (
              <tr key={m.id}>
                <td>{nombreProducto(m.productoId)}</td>
                <td>
                  <span className={`badge ${m.tipo === 'ENTRADA' ? 'badge-entrada' : 'badge-salida'}`}>
                    {m.tipo}
                  </span>
                </td>
                <td>{m.cantidad}</td>
                <td>{formatearFecha(m.fecha)}</td>
                <td className="acciones">
                  <button className="btn-sm btn-danger" onClick={() => handleEliminar(m.id)}>
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
