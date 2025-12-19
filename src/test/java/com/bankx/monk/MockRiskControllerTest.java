package com.bankx.monk;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Unit tests for the {@link MockRiskController} class.
 */
@WebFluxTest(MockRiskController.class)
public class MockRiskControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void testAllow_DebitAllowed() {
    webTestClient.get()
        .uri("/mock/risk/allow?currency=USD&type=DEBIT&amount=1000")
        .exchange()
        .expectStatus().isOk()
        .expectBody(Boolean.class).isEqualTo(true);
  }

  @Test
  void testAllow_DebitNotAllowed() {
    webTestClient.get()
        .uri("/mock/risk/allow?currency=USD&type=DEBIT&amount=1500")
        .exchange()
        .expectStatus().isOk()
        .expectBody(Boolean.class).isEqualTo(false);
  }

  @Test
  void testAllow_CreditAllowed() {
    webTestClient.get()
        .uri("/mock/risk/allow?currency=USD&type=CREDIT&amount=2000")
        .exchange()
        .expectStatus().isOk()
        .expectBody(Boolean.class).isEqualTo(true);
  }

  @Test
  void testAllow_Fail() {
    webTestClient.get()
        .uri("/mock/risk/allow?currency=USD&type=DEBIT&amount=1000&fail=true")
        .exchange()
        .expectStatus().is5xxServerError();
  }

  @Test
  void testAllow_Delay() {
    webTestClient.get()
        .uri("/mock/risk/allow?currency=USD&type=DEBIT&amount=1000&delayMs=100")
        .exchange()
        .expectStatus().isOk()
        .expectBody(Boolean.class).isEqualTo(true);
  }
}
