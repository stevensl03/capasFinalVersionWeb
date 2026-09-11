import { useState } from 'react'
import ProductosList from './presentation/components/ProductosList'
import MovimientosList from './presentation/components/MovimientosList'
import './App.css'

type Tab = 'productos' | 'movimientos'

function App() {
  const [tab, setTab] = useState<Tab>('productos')

  return (
    <div className="app">
      <header>
        <h1>Sistema de Inventario</h1>
        <nav className="tabs">
          <button
            className={tab === 'productos' ? 'active' : ''}
            onClick={() => setTab('productos')}
          >
            Productos
          </button>
          <button
            className={tab === 'movimientos' ? 'active' : ''}
            onClick={() => setTab('movimientos')}
          >
            Movimientos
          </button>
        </nav>
      </header>
      <main>
        {tab === 'productos' ? <ProductosList /> : <MovimientosList />}
      </main>
    </div>
  )
}

export default App
