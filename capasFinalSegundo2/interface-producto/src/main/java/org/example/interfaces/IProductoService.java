package org.example.interfaces;

import org.example.dto.ProductoDTO;

import java.util.List;
import java.util.Optional;

public interface IProductoService {
    List<ProductoDTO> listarTodos();
    Optional<ProductoDTO> buscarPorId(Long id);
    ProductoDTO crear(ProductoDTO producto);
    ProductoDTO actualizar(ProductoDTO producto);
    void eliminar(Long id);
}
