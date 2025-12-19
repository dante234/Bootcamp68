package com.bankx.logging;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Manages the Log4j2 ThreadContext for Reactor Monos, specifically for correlation IDs.
 */
@Component
public class LogContext {

  /**
   * Enriches a Mono with a correlation ID from the Reactor Context,
   * setting it in the Log4j2 ThreadContext during the Mono's execution
   * and removing it afterwards.
   *
   * @param mono The Mono to enrich.
   * @param <T> The type of data emitted by the Mono.
   * @return A Mono that will have the correlation ID available in the ThreadContext
   *         during its subscription.
   */
  public <T> Mono<T> withMdc(Mono<T> mono) {
    return mono.deferContextual(ctx -> {
      String corr = ctx.getOrDefault("corrId", "na").toString();
      ThreadContext.put("corrId", corr);
      return mono.doFinally(sig -> ThreadContext.remove("corrId"));
    });
  }
}
