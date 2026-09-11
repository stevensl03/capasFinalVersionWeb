import { useState } from 'react'
import type { Producto } from '../../domain/producto/Producto'
import { inventarioController } from '../../di/container'

interface Props {
  productos: Producto[]
  onCreado: () => void
}

export default function MovimientosForm({ productos, onCreado }: Props) {
  const [productoId, setProductoId] = useState('')
  const [tipo, setTipo] = useState('ENTRADA')
  const [cantidad, setCantidad] = useState(1)
  const [error, setError] = useState('')
  const [guardando, setGuardando] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    if (!productoId) { setError('Selecciona un producto'); return }
    if (cantidad <= 0) { setError('La cantidad debe ser mayor que cero'); return }
    setGuardando(true)
    try {
      await inventarioController.registrar({
        productoId: parseInt(productoId),
        tipo,
        cantidad,
      })
      setProductoId('')
      setCantidad(1)
      onCreado()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error desconocido')
    } finally {
      setGuardando(false)
    }
  }

  return (
    <form className="form form-inline" onSubmit={handleSubmit}>
      <h3>Registrar Movimiento</h3>

      <label>
        Producto
        <select value={productoId} onChange={e => setProductoId(e.target.value)} required>
          <option value="">-- Seleccionar --</option>
          {productos.map(p => (
            <option key={p.id} value={p.id}>{p.nombre} (stock: {p.stock})</option>
          ))}
        </select>
      </label>

      <label>
        Tipo
        <select value={tipo} onChange={e => setTipo(e.target.value)}>
          <option value="ENTRADA">Entrada</option>
          <option value="SALIDA">Salida</option>
        </select>
      </label>

      <label>
        Cantidad
        <input
          type="number"
          min="1"
          value={cantidad}
          onChange={e => setCantidad(parseInt(e.target.value) || 0)}
          required
        />
      </label>

      {error && <p className="error">{error}</p>}

      <button type="submit" disabled={guardando}>
        {guardando ? 'Registrando...' : 'Registrar'}
      </button>
    </form>
  )
}
