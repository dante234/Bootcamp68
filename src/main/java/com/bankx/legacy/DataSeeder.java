package com.bankx.legacy;

import com.bankx.domain.Account;
import com.bankx.repo.AccountRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Seeds initial data for the application.
 */
@Service
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

  private final RiskRuleRepository riskRepo;
  private final AccountRepository accountRepo;

  @Override
  public void run(String... args) {
    // Bloqueante (JPA)
    riskRepo.save(RiskRule.builder().currency("PEN")
        .maxDebitPerTx(new BigDecimal("1500")).build());
    riskRepo.save(RiskRule.builder().currency("USD")
        .maxDebitPerTx(new BigDecimal("500")).build());

    // Reactivo (Mongo)
    accountRepo.deleteAll()
        .thenMany(Flux.just(
            Account.builder().number("001-0001").holderName("Ana Peru")
                .currency("PEN")
                .balance(new BigDecimal("2000"))
                .build(),

            Account.builder()
                .number("001-0002")
                .holderName("Luis Acuña")
                .currency("PEN")
                .balance(new BigDecimal("800"))
                .build()
        ))
        .flatMap(accountRepo::save)
        .blockLast(); // solo para seed en arranque
  }
}
