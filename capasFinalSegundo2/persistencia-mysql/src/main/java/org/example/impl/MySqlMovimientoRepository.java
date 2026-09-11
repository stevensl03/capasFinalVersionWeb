package org.example.impl;

import org.example.interfaces.IRepositorioMovimiento;
import org.example.model.Movimiento;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySqlMovimientoRepository implements IRepositorioMovimiento {

    private final String url;
    private final String usuario;
    private final String password;

    public MySqlMovimientoRepository(String url, String usuario, String password) {
        this.url = url;
        this.usuario = usuario;
        this.password = password;
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(url, usuario, password);
    }

    @Override
    public List<Movimiento> findAll() {
        List<Movimiento> movimientos = new ArrayList<>();
        String sql = "SELECT id, producto_id, tipo, cantidad, fecha FROM movimiento";
        try (Connection con = conectar();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                movimientos.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar movimientos", e);
        }
        return movimientos;
    }

    @Override
    public Optional<Movimiento> findById(Long id) {
        String sql = "SELECT id, producto_id, tipo, cantidad, fecha FROM movimiento WHERE id = ?";
        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar movimiento", e);
        }
        return Optional.empty();
    }

    @Override
    public Movimiento save(Movimiento movimiento) {
        if (movimiento.getId() == null) {
            String sql = "INSERT INTO movimiento (producto_id, tipo, cantidad, fecha) VALUES (?, ?, ?, ?)";
            try (Connection con = conectar();
                 PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, movimiento.getProductoId());
                ps.setString(2, movimiento.getTipo().name());
                ps.setInt(3, movimiento.getCantidad());
                ps.setTimestamp(4, Timestamp.valueOf(movimiento.getFecha()));
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        movimiento.setId(rs.getLong(1));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error al insertar movimiento", e);
            }
        } else {
            String sql = "UPDATE movimiento SET producto_id = ?, tipo = ?, cantidad = ?, fecha = ? WHERE id = ?";
            try (Connection con = conectar();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, movimiento.getProductoId());
                ps.setString(2, movimiento.getTipo().name());
                ps.setInt(3, movimiento.getCantidad());
                ps.setTimestamp(4, Timestamp.valueOf(movimiento.getFecha()));
                ps.setLong(5, movimiento.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error al actualizar movimiento", e);
            }
        }
        return movimiento;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM movimiento WHERE id = ?";
        try (Connection con = conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar movimiento", e);
        }
    }

    private Movimiento map(ResultSet rs) throws SQLException {
        Movimiento movimiento = new Movimiento();
        movimiento.setId(rs.getLong("id"));
        movimiento.setProductoId(rs.getLong("producto_id"));
        movimiento.setTipo(Movimiento.Tipo.valueOf(rs.getString("tipo")));
        movimiento.setCantidad(rs.getInt("cantidad"));
        movimiento.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        return movimiento;
    }
}