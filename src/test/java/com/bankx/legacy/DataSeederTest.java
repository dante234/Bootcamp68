package com.bankx.legacy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bankx.domain.Account;
import com.bankx.repo.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

/**
 * Unit tests for the {@link DataSeeder} class.
 */
@ExtendWith(MockitoExtension.class)
public class DataSeederTest {

  @Mock
  private RiskRuleRepository riskRuleRepository;

  @Mock
  private AccountRepository accountRepository;

  @InjectMocks
  private DataSeeder dataSeeder;

  @Test
  void testRun() {
    when(riskRuleRepository.save(any(RiskRule.class))).thenReturn(null);
    when(accountRepository.deleteAll()).thenReturn(Mono.empty());
    when(accountRepository.save(any(Account.class))).thenReturn(Mono.just(new Account()));

    dataSeeder.run();

    verify(riskRuleRepository, times(2)).save(any(RiskRule.class));
    verify(accountRepository, times(1)).deleteAll();
    verify(accountRepository, times(2)).save(any(Account.class));
  }
}
