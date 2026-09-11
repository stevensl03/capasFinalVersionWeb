import { useState, useEffect } from 'react'
import type { Producto, NuevoProducto } from '../../domain/producto/Producto'

interface Props {
  producto?: Producto | null
  onGuardar: (datos: NuevoProducto) => Promise<void>
  onCancelar: () => void
}

const initial = { nombre: '', descripcion: '', precio: 0, stock: 0 }

export default function ProductosForm({ producto, onGuardar, onCancelar }: Props) {
  const [form, setForm] = useState(initial)
  const [error, setError] = useState('')
  const [guardando, setGuardando] = useState(false)

  useEffect(() => {
    if (producto) {
      setForm({
        nombre: producto.nombre,
        descripcion: producto.descripcion ?? '',
        precio: producto.precio,
        stock: producto.stock,
      })
    } else {
      setForm(initial)
    }
  }, [producto])

  const set = (campo: keyof typeof form, valor: string | number) => {
    setForm(prev => ({ ...prev, [campo]: valor }))
    setError('')
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!form.nombre.trim()) { setError('El nombre es obligatorio'); return }
    if (form.precio <= 0) { setError('El precio debe ser mayor que cero'); return }
    if (form.stock < 0) { setError('El stock no puede ser negativo'); return }
    setGuardando(true)
    try {
      await onGuardar({
        nombre: form.nombre.trim(),
        descripcion: form.descripcion.trim() || null,
        precio: form.precio,
        stock: form.stock,
      })
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error desconocido')
    } finally {
      setGuardando(false)
    }
  }

  return (
    <form className="form" onSubmit={handleSubmit}>
      <h2>{producto ? 'Editar Producto' : 'Nuevo Producto'}</h2>

      <label>
        Nombre *
        <input
          value={form.nombre}
          onChange={e => set('nombre', e.target.value)}
          placeholder="Laptop, Teclado..."
          required
        />
      </label>

      <label>
        Descripcion
        <input
          value={form.descripcion}
          onChange={e => set('descripcion', e.target.value)}
          placeholder="Opcional"
        />
      </label>

      <label>
        Precio *
        <input
          type="number"
          step="0.01"
          min="0.01"
          value={form.precio}
          onChange={e => set('precio', parseFloat(e.target.value) || 0)}
          required
        />
      </label>

      <label>
        Stock *
        <input
          type="number"
          min="0"
          value={form.stock}
          onChange={e => set('stock', parseInt(e.target.value) || 0)}
          required
        />
      </label>

      {error && <p className="error">{error}</p>}

      <div className="form-actions">
        <button type="submit" disabled={guardando}>
          {guardando ? 'Guardando...' : producto ? 'Actualizar' : 'Crear'}
        </button>
        <button type="button" className="btn-secondary" onClick={onCancelar}>
          Cancelar
        </button>
      </div>
    </form>
  )
}
