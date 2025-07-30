package com.marcinsz.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("news-generator")
public class NewsGeneratorApiConfig {
    private String baseUrl;
    private String apiKey;
}
