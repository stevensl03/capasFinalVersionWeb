package org.example.config;

import org.example.impl.MySqlMovimientoRepository;
import org.example.impl.MySqlProductoRepository;
import org.example.interfaces.IRepositorioMovimiento;
import org.example.interfaces.IRepositorioProducto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Adaptador de infraestructura activo cuando app.persistencia.motor=mysql (motor por
 * defecto). Es el unico lugar que conoce las clases concretas MySqlXxxRepository -
 * el dominio y los servicios solo ven IRepositorioMovimiento / IRepositorioProducto.
 */
@Configuration
@ConditionalOnProperty(prefix = "app.persistencia", name = "motor", havingValue = "mysql", matchIfMissing = true)
public class PersistenciaMySqlConfig {

    @Value("${app.db.mysql.url}")
    private String url;

    @Value("${app.db.mysql.usuario}")
    private String usuario;

    @Value("${app.db.mysql.password}")
    private String password;

    @Bean
    public IRepositorioMovimiento repositorioMovimiento() {
        return new MySqlMovimientoRepository(url, usuario, password);
    }

    @Bean
    public IRepositorioProducto repositorioProducto() {
        return new MySqlProductoRepository(url, usuario, password);
    }
}
