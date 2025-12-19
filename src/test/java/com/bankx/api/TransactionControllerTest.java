package com.bankx.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.bankx.api.dto.CreateTxRequest;
import com.bankx.domain.Transaction;
import com.bankx.service.TransactionService;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Unit test for the {@link TransactionController} class.
 *
 * @author name surname
 * @version 1.0
 */
@WebFluxTest(TransactionController.class)
public class TransactionControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockBean
  private TransactionService transactionService;

  @Test
  void testCreateTransaction() {
    CreateTxRequest request = new CreateTxRequest();
    request.setAccountNumber("12345");
    request.setType("DEBIT");
    request.setAmount(BigDecimal.TEN);

    Transaction transaction = Transaction.builder()
        .id(UUID.randomUUID().toString())
        .accountId("account-id-123")
        .type("DEBIT")
        .amount(BigDecimal.TEN)
        .build();

    when(transactionService.create(any(CreateTxRequest.class))).thenReturn(Mono.just(transaction));

    webTestClient.post().uri("/api/transactions")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(Transaction.class)
        .isEqualTo(transaction);
  }

  @Test
  void testListTransactions() {
    Transaction transaction1 = Transaction.builder()
        .id(UUID.randomUUID().toString())
        .accountId("account-id-123")
        .amount(BigDecimal.TEN)
        .build();
    Transaction transaction2 = Transaction.builder()
        .id(UUID.randomUUID().toString())
        .accountId("account-id-123")
        .amount(BigDecimal.ONE)
        .build();

    when(transactionService.byAccount("12345")).thenReturn(Flux.just(transaction1, transaction2));

    webTestClient.get().uri("/api/transactions?accountNumber=12345")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Transaction.class)
        .hasSize(2)
        .contains(transaction1, transaction2);
  }

  @Test
  void testStreamTransactions() {
    Transaction transaction1 = Transaction.builder()
        .id(UUID.randomUUID().toString())
        .accountId("account-id-123")
        .amount(BigDecimal.TEN)
        .build();
    Transaction transaction2 = Transaction.builder()
        .id(UUID.randomUUID().toString())
        .accountId("account-id-123")
        .amount(BigDecimal.ONE)
        .build();

    ServerSentEvent<Transaction> sse1 = ServerSentEvent.builder(transaction1).build();
    ServerSentEvent<Transaction> sse2 = ServerSentEvent.builder(transaction2).build();


    when(transactionService.stream()).thenReturn(Flux.just(sse1, sse2));

    Flux<Transaction> transactionFlux = webTestClient.get().uri("/api/stream/transactions")
        .accept(MediaType.TEXT_EVENT_STREAM)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
        .returnResult(Transaction.class).getResponseBody();

    StepVerifier.create(transactionFlux)
        .expectNext(transaction1)
        .expectNext(transaction2)
        .thenCancel()
        .verify();
  }
}