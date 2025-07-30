package com.marcinsz.backend.config;

import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class NewsGeneratorApiWebClientConfig {
    private final NewsGeneratorApiConfig newsGeneratorApiConfig;

    @Bean
    public WebClient newsGeneratorWebClient() {
        return WebClient.builder()
                .baseUrl(newsGeneratorApiConfig.getBaseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-ACCESS-KEY", newsGeneratorApiConfig.getApiKey())
                .build();
    }
}
