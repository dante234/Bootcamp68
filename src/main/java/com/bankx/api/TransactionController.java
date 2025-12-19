package com.bankx.api;

import com.bankx.api.dto.CreateTxRequest;
import com.bankx.domain.Transaction;
import com.bankx.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* <<<<<<<<<<<<<<  ✨ Windsurf Command ⭐ >>>>>>>>>>>>>>>> */

/**
 * Controlador de la API de transacciones.
 * Este controlador define los endpoints para crear y listar transacciones.
 * Además, proporciona un endpoint para stream de transacciones.
 *
 * @author Nombre Apellido
 * @version 1.0
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransactionController {
  private final TransactionService service;

  @PostMapping("/transactions")
    public Mono<ResponseEntity<Transaction>> create(@Valid @RequestBody CreateTxRequest req) {
    return service.create(req).map(t -> ResponseEntity.status(HttpStatus.CREATED).body(t));
  }

  @GetMapping("/transactions")
    public Flux<Transaction> list(@RequestParam String accountNumber) {
    return service.byAccount(accountNumber);
  }

  @GetMapping(value = "/stream/transactions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Transaction>> stream() {

    return service.stream();
  }
}
