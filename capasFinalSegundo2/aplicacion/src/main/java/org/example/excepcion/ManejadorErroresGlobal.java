package org.example.excepcion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorErroresGlobal {

    private static final Logger log = LoggerFactory.getLogger(ManejadorErroresGlobal.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> manejarArgumentoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(RepositorioException.class)
    public ResponseEntity<String> manejarErrorRepositorio(RepositorioException ex) {
        log.error("Fallo de infraestructura: {}", ex.getMessage(), ex.getCause());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("No fue posible completar la operacion. Intenta nuevamente mas tarde.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> manejarErrorInesperado(Exception ex) {
        log.error("Error inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrio un error inesperado.");
    }
}
