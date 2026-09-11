package org.example.interfaces;

import org.example.dto.MovimientoDTO;

import java.util.List;
import java.util.Optional;

public interface IMovimientoService {
    List<MovimientoDTO> listarTodos();
    Optional<MovimientoDTO> buscarPorId(Long id);
    MovimientoDTO registrar(MovimientoDTO movimiento);
    void eliminar(Long id);
}
