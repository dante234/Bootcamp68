package com.bankx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
class WebClientCfg {
  @Bean
    WebClient riskWebClient() {
    return WebClient.builder().baseUrl("http://localhost:8080/mock/risk").build();
  }
}
