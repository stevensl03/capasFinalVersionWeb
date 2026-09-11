package org.example.impl;

import org.example.excepcion.RepositorioException;
import org.example.interfaces.IRepositorioProducto;
import org.example.model.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador H2 del puerto IRepositorioProducto: motor embebido para desarrollo/pruebas
 * locales sin depender de un servidor MySQL real. El dominio y el servicio de
 * aplicacion (servicio-producto) no distinguen si hablan con este adaptador o con
 * MySqlProductoRepository - solo conocen la interfaz.
 */
public class H2ProductoRepository implements IRepositorioProducto {

    private final String url;
    private final String usuario;
    private final String password;

    public H2ProductoRepository(String url, String usuario, String password) {
        this.url = url;
        this.usuario = usuario;
        this.password = password;
        try (Connection con = conectar()) {
            EsquemaH2.asegurarTablaProducto(con);
        } catch (SQLException e) {
            throw new RepositorioException("Error al conectar con H2 para inicializar producto", e);
        }
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(url, usuario, password);
    }

    @Override
    public List<Producto> findAll() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, precio, stock FROM producto";
        try (Connection con = conectar();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                productos.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RepositorioException("Error al listar productos", e);
        }
        return productos;
    }

    @Override
    public Optional<Producto> findById(Long id) {
        String sql = "SELECT id, nombre, descripcion, precio, stock FROM producto WHERE id = ?";
        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositorioException("Error al buscar producto", e);
        }
        return Optional.empty();
    }

    @Override
    public Producto save(Producto producto) {
        if (producto.getId() == null) {
            String sql = "INSERT INTO producto (nombre, descripcion, precio, stock) VALUES (?, ?, ?, ?)";
            try (Connection con = conectar();
                 PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, producto.getNombre());
                ps.setString(2, producto.getDescripcion());
                ps.setDouble(3, producto.getPrecio());
                ps.setInt(4, producto.getStock());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        producto.setId(rs.getLong(1));
                    }
                }
            } catch (SQLException e) {
                throw new RepositorioException("Error al insertar producto", e);
            }
        } else {
            String sql = "UPDATE producto SET nombre = ?, descripcion = ?, precio = ?, stock = ? WHERE id = ?";
            try (Connection con = conectar();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, producto.getNombre());
                ps.setString(2, producto.getDescripcion());
                ps.setDouble(3, producto.getPrecio());
                ps.setInt(4, producto.getStock());
                ps.setLong(5, producto.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RepositorioException("Error al actualizar producto", e);
            }
        }
        return producto;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM producto WHERE id = ?";
        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositorioException("Error al eliminar producto", e);
        }
    }

    private Producto map(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setId(rs.getLong("id"));
        producto.setNombre(rs.getString("nombre"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setPrecio(rs.getDouble("precio"));
        producto.setStock(rs.getInt("stock"));
        return producto;
    }
}
