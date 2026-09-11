package org.example.service;

import org.example.dto.ProductoDTO;
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
    public List<ProductoDTO> listarTodos() {
        return repositorioProducto.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public Optional<ProductoDTO> buscarPorId(Long id) {
        return repositorioProducto.findById(id).map(this::toDto);
    }

    @Override
    public ProductoDTO crear(ProductoDTO producto) {
        Producto entidad = toEntity(producto);
        entidad.validar();
        return toDto(repositorioProducto.save(entidad));
    }

    @Override
    public ProductoDTO actualizar(ProductoDTO producto) {
        if (producto.getId() == null || repositorioProducto.findById(producto.getId()).isEmpty()) {
            throw new IllegalArgumentException("El producto no existe");
        }
        Producto entidad = toEntity(producto);
        entidad.validar();
        return toDto(repositorioProducto.save(entidad));
    }

    @Override
    public void eliminar(Long id) {
        repositorioProducto.delete(id);
    }

    private Producto toEntity(ProductoDTO dto) {
        return new Producto(dto.getId(), dto.getNombre(), dto.getDescripcion(), dto.getPrecio(), dto.getStock());
    }

    private ProductoDTO toDto(Producto entidad) {
        return new ProductoDTO(entidad.getId(), entidad.getNombre(), entidad.getDescripcion(),
                entidad.getPrecio(), entidad.getStock());
    }
}
