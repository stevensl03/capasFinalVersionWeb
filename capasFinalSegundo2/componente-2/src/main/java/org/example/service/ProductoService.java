package org.example.service;

import org.example.interfaces.IProductoService;
import org.example.interfaces.IRepositorioProducto;
import org.example.model.Producto;

import java.util.List;
import java.util.Optional;

public class ProductoService implements IProductoService {

    private final IRepositorioProducto repositorioProducto;

    public ProductoService(IRepositorioProducto repositorioProducto) {
        this.repositorioProducto = repositorioProducto;
    }

    @Override
    public List<Producto> listarTodos() {
        return repositorioProducto.findAll();
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        return repositorioProducto.findById(id);
    }

    @Override
    public Producto crear(Producto producto) {
        return repositorioProducto.save(producto);
    }

    @Override
    public Producto actualizar(Producto producto) {
        if (producto.getId() == null || repositorioProducto.findById(producto.getId()).isEmpty()) {
            throw new IllegalArgumentException("El producto no existe");
        }
        return repositorioProducto.save(producto);
    }

    @Override
    public void eliminar(Long id) {
        repositorioProducto.delete(id);
    }
}
