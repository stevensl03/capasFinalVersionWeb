package org.example.config;

import org.example.interfaces.IMovimientoService;
import org.example.interfaces.IProductoService;
import org.example.interfaces.IRepositorioMovimiento;
import org.example.interfaces.IRepositorioProducto;
import org.example.service.MovimientoService;
import org.example.service.ProductoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Composition root de la capa de aplicacion: arma los servicios de negocio inyectando
 * el puerto de repositorio correspondiente. No sabe (ni le importa) si esa
 * implementacion viene de MySQL o de H2 - eso lo deciden PersistenciaMySqlConfig /
 * PersistenciaH2Config segun app.persistencia.motor.
 */
@Configuration
public class CapaConfig {

    @Bean
    public IMovimientoService movimientoService(IRepositorioMovimiento repositorioMovimiento) {
        return new MovimientoService(repositorioMovimiento);
    }

    @Bean
    public IProductoService productoService(IRepositorioProducto repositorioProducto) {
        return new ProductoService(repositorioProducto);
    }
}
