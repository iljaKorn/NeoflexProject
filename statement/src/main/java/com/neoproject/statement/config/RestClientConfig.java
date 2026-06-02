package com.neoproject.statement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${services.deal.url}")
    private String calculatorUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(calculatorUrl)
                .build();
    }
}
