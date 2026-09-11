-- Esquema de la base "inventario" (se ejecuta automáticamente al inicializar el volumen MySQL por primera vez)
CREATE TABLE IF NOT EXISTS producto (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(120) NOT NULL,
    descripcion VARCHAR(255),
    precio      DECIMAL(10, 2) NOT NULL,
    stock       INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS movimiento (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    tipo        VARCHAR(10) NOT NULL,
    cantidad    INT NOT NULL,
    fecha       TIMESTAMP NOT NULL,
    CONSTRAINT chk_movimiento_tipo CHECK (tipo IN ('ENTRADA', 'SALIDA'))
);