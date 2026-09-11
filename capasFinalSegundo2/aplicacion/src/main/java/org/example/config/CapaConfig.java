package org.example.config;

import org.example.impl.MySqlMovimientoRepository;
import org.example.impl.MySqlProductoRepository;
import org.example.interfaces.IMovimientoService;
import org.example.interfaces.IProductoService;
import org.example.interfaces.IRepositorioMovimiento;
import org.example.interfaces.IRepositorioProducto;
import org.example.service.MovimientoService;
import org.example.service.ProductoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CapaConfig {

    private final String url;
    private final String usuario;
    private final String password;

    public CapaConfig(
            @Value("${app.db.url}") String url,
            @Value("${app.db.usuario}") String usuario,
            @Value("${app.db.password}") String password) {
        this.url = url;
        this.usuario = usuario;
        this.password = password;
    }

    @Bean
    public IRepositorioMovimiento repositorioMovimiento() {
        return new MySqlMovimientoRepository(url, usuario, password);
    }

    @Bean
    public IRepositorioProducto repositorioProducto() {
        return new MySqlProductoRepository(url, usuario, password);
    }

    @Bean
    public IMovimientoService movimientoService(IRepositorioMovimiento repositorioMovimiento) {
        return new MovimientoService(repositorioMovimiento);
    }

    @Bean
    public IProductoService productoService(IRepositorioProducto repositorioProducto) {
        return new ProductoService(repositorioProducto);
    }
}