package org.example.interfaces;

import org.example.model.Producto;

import java.util.List;
import java.util.Optional;

public interface IRepositorioProducto {
    List<Producto> findAll();
    Optional<Producto> findById(Long id);
    Producto save(Producto producto);
    void delete(Long id);
}
