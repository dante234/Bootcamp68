package com.bankx.error;

import com.bankx.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


/**
 * Unit tests for the {@link GlobalErrorHandler} class.
 * These tests verify that the error handler correctly handles
 * business and generic exceptions.
 *
 * @author Nombre Apellido
 * @version 1.0
 */
@WebFluxTest
@Import({GlobalErrorHandler.class, GlobalErrorHandlerTest.TestController.class})
public class GlobalErrorHandlerTest {

  @Autowired
    private WebTestClient webTestClient;

  @MockBean
    private TransactionService transactionService;

  @RestController
  @Component
  static class TestController {
    @GetMapping("/test/business-exception")
        public Mono<String> businessException() {
      return Mono.error(new BusinessException("test_error"));
    }

    @GetMapping("/test/generic-exception")
        public Mono<String> genericException() {
      return Mono.error(new RuntimeException("test_error"));
    }
  }

  @Test
    void testHandleBusinessException() {
    webTestClient.get().uri("/test/business-exception")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("test_error");
  }

  @Test
    void testHandleGenericException() {
    webTestClient.get().uri("/test/generic-exception")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.error").isEqualTo("internal_error");
  }
}
