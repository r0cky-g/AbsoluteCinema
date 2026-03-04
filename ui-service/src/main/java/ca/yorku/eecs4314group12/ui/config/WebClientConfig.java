package ca.yorku.eecs4314group12.ui.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Creates WebClient beans used by BackendClientService to call
 * api-service (movie lookups) and review-service (reviews).
 */
@Configuration
public class WebClientConfig {

    @Bean("uiApiClient")
    public WebClient apiServiceClient(
            @Value("${app.api-service.url}") String apiServiceUrl) {
        return WebClient.builder()
                .baseUrl(apiServiceUrl)
                .build();
    }

    @Bean("uiReviewClient")
    public WebClient reviewServiceClient(
            @Value("${app.review-service.url}") String reviewServiceUrl) {
        return WebClient.builder()
                .baseUrl(reviewServiceUrl)
                .build();
    }
}