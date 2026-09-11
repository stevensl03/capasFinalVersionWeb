package org.example.excepcion;

/**
 * Error de dominio que representa un fallo de infraestructura (ej. conexion a la base
 * de datos) traducido antes de llegar a la capa de presentacion. La causa original se
 * conserva para diagnostico en logs del servidor, pero su mensaje no debe exponerse
 * crudo al cliente HTTP.
 */
public class RepositorioException extends RuntimeException {

    public RepositorioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
