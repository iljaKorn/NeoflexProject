package org.neoproject.gateway.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${services.deal.url}")
    private String dealServiceUrl;

    @Value("${services.statement.url}")
    private String statementServiceUrl;

    @Bean
    @Qualifier("dealRestClient")
    public RestClient dealRestClient() {
        return RestClient.builder()
                .baseUrl(dealServiceUrl)
                .build();
    }

    @Bean
    @Qualifier("statementRestClient")
    public RestClient statementRestClient() {
        return RestClient.builder()
                .baseUrl(statementServiceUrl)
                .build();
    }
}
