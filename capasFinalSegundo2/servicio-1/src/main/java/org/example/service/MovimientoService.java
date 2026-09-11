package org.example.service;

import org.example.dto.MovimientoDTO;
import org.example.interfaces.IMovimientoService;
import org.example.interfaces.IRepositorioMovimiento;
import org.example.model.Movimiento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MovimientoService implements IMovimientoService {

    private final IRepositorioMovimiento repositorioMovimiento;

    public MovimientoService(IRepositorioMovimiento repositorioMovimiento) {
        this.repositorioMovimiento = repositorioMovimiento;
    }

    @Override
    public List<MovimientoDTO> listarTodos() {
        return repositorioMovimiento.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public Optional<MovimientoDTO> buscarPorId(Long id) {
        return repositorioMovimiento.findById(id).map(this::toDto);
    }

    @Override
    public MovimientoDTO registrar(MovimientoDTO movimiento) {
        Movimiento entidad = toEntity(movimiento);
        entidad.validar();
        if (entidad.getFecha() == null) {
            entidad.setFecha(LocalDateTime.now());
        }
        return toDto(repositorioMovimiento.save(entidad));
    }

    @Override
    public void eliminar(Long id) {
        repositorioMovimiento.delete(id);
    }

    private Movimiento toEntity(MovimientoDTO dto) {
        Movimiento entidad = new Movimiento();
        entidad.setId(dto.getId());
        entidad.setProductoId(dto.getProductoId());
        entidad.setTipo(parseTipo(dto.getTipo()));
        entidad.setCantidad(dto.getCantidad());
        entidad.setFecha(dto.getFecha());
        return entidad;
    }

    private Movimiento.Tipo parseTipo(String tipo) {
        if (tipo == null) {
            return null;
        }
        try {
            return Movimiento.Tipo.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El tipo debe ser ENTRADA o SALIDA");
        }
    }

    private MovimientoDTO toDto(Movimiento entidad) {
        MovimientoDTO dto = new MovimientoDTO();
        dto.setId(entidad.getId());
        dto.setProductoId(entidad.getProductoId());
        dto.setTipo(entidad.getTipo() == null ? null : entidad.getTipo().name());
        dto.setCantidad(entidad.getCantidad());
        dto.setFecha(entidad.getFecha());
        return dto;
    }
}