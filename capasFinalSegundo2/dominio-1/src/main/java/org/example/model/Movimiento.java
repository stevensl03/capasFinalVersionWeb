package org.example.model;

import java.time.LocalDateTime;

public class Movimiento {
    public enum Tipo {
        ENTRADA, SALIDA
    }

    private Long id;
    private Long productoId;
    private Tipo tipo;
    private Integer cantidad;
    private LocalDateTime fecha;

    public Movimiento() {
    }

    public Movimiento(Long id, Long productoId, Tipo tipo, Integer cantidad, LocalDateTime fecha) {
        this.id = id;
        this.productoId = productoId;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }

    public void validar() {
        if (productoId == null) {
            throw new IllegalArgumentException("El producto es obligatorio");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo debe ser ENTRADA o SALIDA");
        }
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Movimiento{id=" + id +
                ", productoId=" + productoId +
                ", tipo=" + tipo +
                ", cantidad=" + cantidad +
                ", fecha=" + fecha + '}';
    }
}