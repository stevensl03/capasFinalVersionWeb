package org.example.impl;

import org.example.excepcion.RepositorioException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Crea el esquema de la base embebida H2 en el primer arranque (equivalente al
 * 01-schema.sql que se aplica una sola vez sobre el contenedor MySQL). Se invoca de
 * forma idempotente (IF NOT EXISTS) desde cada repositorio H2.
 */
final class EsquemaH2 {

    private EsquemaH2() {
    }

    static void asegurarTablaProducto(Connection con) {
        String sql = """
                CREATE TABLE IF NOT EXISTS producto (
                    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                    nombre      VARCHAR(120) NOT NULL,
                    descripcion VARCHAR(255),
                    precio      DECIMAL(10, 2) NOT NULL,
                    stock       INT NOT NULL DEFAULT 0
                )""";
        ejecutar(con, sql, "producto");
    }

    static void asegurarTablaMovimiento(Connection con) {
        String sql = """
                CREATE TABLE IF NOT EXISTS movimiento (
                    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                    producto_id BIGINT NOT NULL,
                    tipo        VARCHAR(10) NOT NULL,
                    cantidad    INT NOT NULL,
                    fecha       TIMESTAMP NOT NULL,
                    CONSTRAINT chk_movimiento_tipo CHECK (tipo IN ('ENTRADA', 'SALIDA'))
                )""";
        ejecutar(con, sql, "movimiento");
    }

    private static void ejecutar(Connection con, String sql, String tabla) {
        try (Statement stmt = con.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RepositorioException("Error al inicializar el esquema H2 de " + tabla, e);
        }
    }
}
