package com.bankx.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.bankx.api.dto.CreateTxRequest;
import com.bankx.domain.Account;
import com.bankx.domain.Transaction;
import com.bankx.error.BusinessException;
import com.bankx.legacy.RiskService;
import com.bankx.logging.LogContext;
import com.bankx.repo.AccountRepository;
import com.bankx.repo.TransactionRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

/**
 * Unit tests for {@link TransactionService}.
 */
@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

  @Mock
  private AccountRepository accountRepository;
  @Mock
  private TransactionRepository transactionRepository;
  @Mock
  private RiskService riskService;
  @Mock
  private Sinks.Many<Transaction> txSink;

  private TransactionService transactionService;

  @BeforeEach
  void setUp() {
    transactionService = new TransactionService(accountRepository, transactionRepository,
        riskService, txSink, new LogContext());
  }

  @Test
  void testCreate_Debit_Success() {
    CreateTxRequest request = new CreateTxRequest();
    request.setAccountNumber("12345");
    request.setType("DEBIT");
    request.setAmount(BigDecimal.TEN);

    Account account = Account.builder()
        .id(UUID.randomUUID().toString())
        .number("12345")
        .balance(BigDecimal.valueOf(100))
        .currency("USD")
        .build();

    Transaction transaction = Transaction.builder()
        .id(UUID.randomUUID().toString())
        .accountId(account.getId())
        .type("DEBIT")
        .amount(BigDecimal.TEN)
        .build();

    when(accountRepository.findByNumber("12345")).thenReturn(Mono.just(account));
    when(riskService.isAllowed("USD", "DEBIT", BigDecimal.TEN)).thenReturn(Mono.just(true));
    when(accountRepository.save(any(Account.class))).thenReturn(Mono.just(account));
    when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(transaction));

    StepVerifier.create(transactionService.create(request))
        .expectNext(transaction)
        .verifyComplete();
  }

  @Test
  void testCreate_Credit_Success() {
    CreateTxRequest request = new CreateTxRequest();
    request.setAccountNumber("12345");
    request.setType("CREDIT");
    request.setAmount(BigDecimal.TEN);

    Account account = Account.builder()
        .id(UUID.randomUUID().toString())
        .number("12345")
        .balance(BigDecimal.valueOf(100))
        .currency("USD")
        .build();

    Transaction transaction = Transaction.builder()
        .id(UUID.randomUUID().toString())
        .accountId(account.getId())
        .type("CREDIT")
        .amount(BigDecimal.TEN)
        .build();

    when(accountRepository.findByNumber("12345")).thenReturn(Mono.just(account));
    when(riskService.isAllowed("USD", "CREDIT", BigDecimal.TEN)).thenReturn(Mono.just(true));
    when(accountRepository.save(any(Account.class))).thenReturn(Mono.just(account));
    when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(transaction));

    StepVerifier.create(transactionService.create(request))
        .expectNext(transaction)
        .verifyComplete();
  }

  @Test
  void testCreate_AccountNotFound() {
    CreateTxRequest request = new CreateTxRequest();
    request.setAccountNumber("12345");
    request.setType("DEBIT");
    request.setAmount(BigDecimal.TEN);

    when(accountRepository.findByNumber("12345")).thenReturn(Mono.empty());

    StepVerifier.create(transactionService.create(request))
        .expectError(BusinessException.class)
        .verify();
  }

  @Test
  void testCreate_RiskRejected() {
    CreateTxRequest request = new CreateTxRequest();
    request.setAccountNumber("12345");
    request.setType("DEBIT");
    request.setAmount(BigDecimal.TEN);

    Account account = Account.builder()
        .id(UUID.randomUUID().toString())
        .number("12345")
        .balance(BigDecimal.valueOf(100))
        .currency("USD")
        .build();

    when(accountRepository.findByNumber("12345")).thenReturn(Mono.just(account));
    when(riskService.isAllowed("USD", "DEBIT", BigDecimal.TEN)).thenReturn(Mono.just(false));

    StepVerifier.create(transactionService.create(request))
        .expectError(BusinessException.class)
        .verify();
  }

  @Test
  void testCreate_InsufficientFunds() {
    CreateTxRequest request = new CreateTxRequest();
    request.setAccountNumber("12345");
    request.setType("DEBIT");
    request.setAmount(BigDecimal.valueOf(200));

    Account account = Account.builder()
        .id(UUID.randomUUID().toString())
        .number("12345")
        .balance(BigDecimal.valueOf(100))
        .currency("USD")
        .build();

    when(accountRepository.findByNumber("12345")).thenReturn(Mono.just(account));
    when(riskService.isAllowed("USD", "DEBIT",
        BigDecimal.valueOf(200))).thenReturn(Mono.just(true));

    StepVerifier.create(transactionService.create(request))
        .expectError(BusinessException.class)
        .verify();
  }

  @Test
  void testByAccount_Success() {
    Account account = Account.builder()
        .id(UUID.randomUUID().toString())
        .number("12345")
        .build();

    Transaction transaction = Transaction.builder().build();

    when(accountRepository.findByNumber("12345")).thenReturn(Mono.just(account));
    when(transactionRepository.findByAccountIdOrderByTimestampDesc(
        account.getId())).thenReturn(Flux.just(transaction));

    StepVerifier.create(transactionService.byAccount("12345"))
        .expectNext(transaction)
        .verifyComplete();
  }

  @Test
  void testByAccount_AccountNotFound() {
    when(accountRepository.findByNumber("12345")).thenReturn(Mono.empty());

    StepVerifier.create(transactionService.byAccount("12345"))
        .expectError(BusinessException.class)
        .verify();
  }

  @Test
  void testStream() {
    Sinks.Many<Transaction> sink = Sinks.many().multicast().onBackpressureBuffer();
    transactionService = new TransactionService(accountRepository, transactionRepository,
        riskService, sink, new LogContext());

    Transaction transaction = Transaction.builder().build();
    sink.tryEmitNext(transaction);

    StepVerifier.create(transactionService.stream())
        .expectNextCount(1)
        .thenCancel()
        .verify();
  }
}
