package org.example.interfaces;

import org.example.model.Movimiento;

import java.util.List;
import java.util.Optional;

public interface IRepositorioMovimiento {
    List<Movimiento> findAll();
    Optional<Movimiento> findById(Long id);
    Movimiento save(Movimiento movimiento);
    void delete(Long id);
}