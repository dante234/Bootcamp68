package com.bankx.error;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

/**
 * Controller advice para manejar excepciones globales.
 * Este controlador se encarga de manejar las excepciones
 * que ocurren en los controladores.
 * Si la excepción es de tipo {@link BusinessException} se
 * devuelve un código 400 y el mensaje de error.
 * Si la excepción es de cualquier otro tipo
 * se devuelve un código 500 y el mensaje de error.
 */

@RestControllerAdvice
public class GlobalErrorHandler {
  @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<Map<String, Object>>>
        handleBiz(BusinessException ex) {
    return Mono.just(ResponseEntity.badRequest().body(Map.of("error",
                ex.getMessage())));
  }

  @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGen(Exception ex) {
    return Mono.just(ResponseEntity.status(500).body(Map.of("error",
                "internal_error")));
  }
}
