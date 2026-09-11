package org.example.config;

import org.example.impl.H2MovimientoRepository;
import org.example.impl.H2ProductoRepository;
import org.example.interfaces.IRepositorioMovimiento;
import org.example.interfaces.IRepositorioProducto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Adaptador de infraestructura activo cuando app.persistencia.motor=h2 (motor embebido
 * para desarrollo/pruebas locales sin un servidor MySQL real). Es el unico lugar que
 * conoce las clases concretas H2XxxRepository - el dominio y los servicios solo ven
 * IRepositorioMovimiento / IRepositorioProducto, igual que con MySQL.
 */
@Configuration
@ConditionalOnProperty(prefix = "app.persistencia", name = "motor", havingValue = "h2")
public class PersistenciaH2Config {

    @Value("${app.db.h2.url}")
    private String url;

    @Value("${app.db.h2.usuario}")
    private String usuario;

    @Value("${app.db.h2.password}")
    private String password;

    @Bean
    public IRepositorioMovimiento repositorioMovimiento() {
        return new H2MovimientoRepository(url, usuario, password);
    }

    @Bean
    public IRepositorioProducto repositorioProducto() {
        return new H2ProductoRepository(url, usuario, password);
    }
}
