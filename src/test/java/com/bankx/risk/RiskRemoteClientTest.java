package com.bankx.risk;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.bankx.legacy.RiskService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Unit tests for the {@link RiskRemoteClient} class.
 */
@ExtendWith(MockitoExtension.class)
public class RiskRemoteClientTest {

  @Mock
  private WebClient riskWebClient;

  @Mock
  private RiskService riskService;

  @InjectMocks
  private RiskRemoteClient riskRemoteClient;

  // Mocks for WebClient fluent API
  @Mock
  private RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock
  private RequestHeadersSpec requestHeadersSpec;
  @Mock
  private ResponseSpec responseSpec;

  @BeforeEach
  void setUp() {
    riskRemoteClient.legacy = riskService;
  }

  @Test
  void testIsAllowed_Success() {
    when(riskWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(java.util.function.Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));

    StepVerifier.create(riskRemoteClient.isAllowed("USD", "DEBIT", BigDecimal.TEN))
        .expectNext(true)
        .verifyComplete();
  }

  @Test
  void testIsAllowed_WebClientError_FallbackTriggered() {
    when(riskWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(java.util.function.Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(Boolean.class))
        .thenReturn(Mono.error(new RuntimeException("WebClient error")));

    StepVerifier.create(riskRemoteClient.isAllowed("USD", "DEBIT", BigDecimal.TEN))
        .expectError(RuntimeException.class)
        .verify();
  }
}
