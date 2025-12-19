package com.bankx.legacy;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Unit tests for the {@link RiskService} class.
 */
@ExtendWith(MockitoExtension.class)
public class RiskServiceTest {

  @Mock
  private RiskRuleRepository riskRuleRepository;

  @InjectMocks
  private RiskService riskService;

  @Test
  void testIsAllowed_DebitAllowed() {
    RiskRule rule = RiskRule.builder()
        .currency("USD")
        .maxDebitPerTx(BigDecimal.valueOf(1000))
        .build();
    when(riskRuleRepository.findFirstByCurrency("USD")).thenReturn(Optional.of(rule));

    Mono<Boolean> result = riskService.isAllowed("USD", "DEBIT", BigDecimal.valueOf(500));

    StepVerifier.create(result)
        .expectNext(true)
        .verifyComplete();
  }

  @Test
  void testIsAllowed_DebitNotAllowed() {
    RiskRule rule = RiskRule.builder()
        .currency("USD")
        .maxDebitPerTx(BigDecimal.valueOf(1000))
        .build();
    when(riskRuleRepository.findFirstByCurrency("USD")).thenReturn(Optional.of(rule));

    Mono<Boolean> result = riskService.isAllowed("USD", "DEBIT", BigDecimal.valueOf(1500));

    StepVerifier.create(result)
        .expectNext(false)
        .verifyComplete();
  }

  @Test
  void testIsAllowed_CreditAllowed() {
    Mono<Boolean> result = riskService.isAllowed("USD", "CREDIT", BigDecimal.valueOf(500));

    StepVerifier.create(result)
        .expectNext(true)
        .verifyComplete();
  }

  @Test
  void testIsAllowed_NoRuleFound() {
    when(riskRuleRepository.findFirstByCurrency("EUR")).thenReturn(Optional.empty());

    Mono<Boolean> result = riskService.isAllowed("EUR", "DEBIT", BigDecimal.valueOf(500));

    StepVerifier.create(result)
        .expectNext(false)
        .verifyComplete();
  }
}
