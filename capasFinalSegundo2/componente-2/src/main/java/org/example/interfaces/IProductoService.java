package org.example.interfaces;

import org.example.model.Producto;

import java.util.List;
import java.util.Optional;

public interface IProductoService {
    List<Producto> listarTodos();
    Optional<Producto> buscarPorId(Long id);
    Producto crear(Producto producto);
    Producto actualizar(Producto producto);
    void eliminar(Long id);
}