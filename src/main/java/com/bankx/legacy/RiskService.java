package com.bankx.legacy;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Service for evaluating transaction risk based on defined rules.
 */
@Service
@RequiredArgsConstructor
public class RiskService {

  private final RiskRuleRepository riskRepo;

  /**
   * Checks if a transaction is allowed based on currency, type, and amount.
   *
   * @param currency The currency of the transaction.
   * @param type The type of the transaction (e.g., "DEBIT", "CREDIT").
   * @param amount The amount of the transaction.
   * @return A Mono emitting true if the transaction is allowed, false otherwise.
   */
  public Mono<Boolean> isAllowed(String currency, String type, BigDecimal amount) {
    return Mono.fromCallable(() ->
            riskRepo.findFirstByCurrency(currency)
                .map(RiskRule::getMaxDebitPerTx)
                .orElse(new BigDecimal("0")))
        .subscribeOn(Schedulers.boundedElastic()) // bloqueante a elastic
        .map(max -> {
          if ("DEBIT".equalsIgnoreCase(type)) {
            return amount.compareTo(max) <= 0;
          }
          return true;
        });
  }
}
