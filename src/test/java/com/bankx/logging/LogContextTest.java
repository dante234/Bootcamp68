package com.bankx.logging;

import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LogContextTest {

    private LogContext logContext;

    @BeforeEach
    void setUp() {
        logContext = new LogContext();
        ThreadContext.clearAll(); // Clear any existing context before each test
    }

    @Test
    void testWithMdc_CorrIdAddedAndRemoved() {
        Mono<String> testMono = Mono.just("testData");
        String correlationId = "test-corr-id";

        StepVerifier.create(logContext.withMdc(testMono).contextWrite(Context.of("corrId", correlationId)))
                .consumeSubscriptionWith(s -> {
                    // Check if corrId is present in ThreadContext when Mono is subscribed
                    assertEquals(correlationId, ThreadContext.get("corrId"));
                })
                .expectNext("testData")
                .verifyComplete();

        // Verify corrId is removed after Mono completes
        assertNull(ThreadContext.get("corrId"));
    }

    @Test
    void testWithMdc_NoCorrIdInContext() {
        Mono<String> testMono = Mono.just("testData");

        StepVerifier.create(logContext.withMdc(testMono))
                .consumeSubscriptionWith(s -> {
                    // Check if corrId is "na" when not provided in context
                    assertEquals("na", ThreadContext.get("corrId"));
                })
                .expectNext("testData")
                .verifyComplete();

        // Verify corrId is removed after Mono completes
        assertNull(ThreadContext.get("corrId"));
    }

    @Test
    void testWithMdc_ExistingCorrIdInThreadContext() {
        Mono<String> testMono = Mono.just("testData");
        String initialCorrId = "initial-corr-id";
        String contextCorrId = "context-corr-id";

        ThreadContext.put("corrId", initialCorrId); // Set initial corrId in ThreadContext

        StepVerifier.create(logContext.withMdc(testMono).contextWrite(Context.of("corrId", contextCorrId)))
                .consumeSubscriptionWith(s -> {
                    // Context corrId should override ThreadContext's corrId for the duration of the Mono
                    assertEquals(contextCorrId, ThreadContext.get("corrId"));
                })
                .expectNext("testData")
                .verifyComplete();

        // Verify that the original corrId is restored (or removed if not present initially)
        assertNull(ThreadContext.get("corrId")); // It should be removed by doFinally
    }
}
