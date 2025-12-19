package com.bankx.service;

import com.bankx.api.dto.CreateTxRequest;
import com.bankx.domain.Account;
import com.bankx.domain.Transaction;
import com.bankx.error.BusinessException;
import com.bankx.legacy.RiskService;
import com.bankx.logging.LogContext;
import com.bankx.repo.AccountRepository;
import com.bankx.repo.TransactionRepository;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

/**
 * Service class for handling financial transactions.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class TransactionService {
  private final AccountRepository accountRepo;
  private final TransactionRepository txRepo;
  private final RiskService riskService;
  private final Sinks.Many<Transaction> txSink;
  private final LogContext logContext;

  /**
   * Creates a new transaction based on the given request.
   *
   * @param req The request to create the transaction.
   * @return A {@link Mono} emitting the created transaction.
   */
  public Mono<Transaction> create(CreateTxRequest req) {
    log.debug("Creating tx {}", req);
    return logContext.withMdc(accountRepo.findByNumber(req.getAccountNumber())
        .switchIfEmpty(Mono.error(new BusinessException("account_not_found")))
        .flatMap(acc -> validateAndApply(acc, req))
        .onErrorMap(IllegalStateException.class, e -> new BusinessException(e.getMessage())))
        .doOnSuccess(tx -> log.info("tx_created account={} amount={}",
            req.getAccountNumber(), req.getAmount()));
  }

  private Mono<Transaction> validateAndApply(Account acc, CreateTxRequest req) {
    String type = req.getType().toUpperCase();
    BigDecimal amount = req.getAmount();

    // 1) Riesgo (bloqueante envuelto -> elastic)
    return riskService.isAllowed(acc.getCurrency(), type, amount)
        .flatMap(allowed -> {
          if (!allowed) {
            return Mono.error(new BusinessException("risk_rejected"));
          }
          // 2) Reglas de negocio
          if ("DEBIT".equals(type) && acc.getBalance().compareTo(amount) < 0) {
            return Mono.error(new BusinessException("insufficient_funds"));
          }
          // 3) Actualiza balance (CPU-light, podemos publishOn paralelo si deseamos)
          return Mono.just(acc).publishOn(Schedulers.parallel())
              .map(a -> {
                BigDecimal newBal = "DEBIT".equals(type)
                    ? a.getBalance().subtract(amount)
                    : a.getBalance().add(amount);
                a.setBalance(newBal);
                return a;
              })
              .flatMap(accountRepo::save)
              // 4) Persiste transacción
              .flatMap(saved -> txRepo.save(Transaction.builder()
                  .accountId(saved.getId())
                  .type(type)
                  .amount(amount)
                  .timestamp(Instant.now())
                  .status("OK")
                  .build()))
              // 5) Notifica por SSE
              .doOnNext(tx -> txSink.tryEmitNext(tx));
        });
  }

  /**
   * Retrieves all transactions for a given account number.
   *
   * @param accountNumber The account number.
   * @return A {@link Flux} emitting the transactions.
   */
  public Flux<Transaction> byAccount(String accountNumber) {
    return accountRepo.findByNumber(accountNumber)
        .switchIfEmpty(Mono.error(new BusinessException("account_not_found")))
        .flatMapMany(acc -> txRepo.findByAccountIdOrderByTimestampDesc(acc.getId()));
  }

  /**
   * Returns a stream of real-time transaction events.
   *
   * @return A {@link Flux} of {@link ServerSentEvent}s containing transactions.
   */
  public Flux<ServerSentEvent<Transaction>> stream() {
    return txSink.asFlux()
        .map(tx -> ServerSentEvent.builder(tx).event("transaction").build());
  }
}
