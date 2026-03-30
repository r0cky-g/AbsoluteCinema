package ca.yorku.eecs4314group12.ui.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean("uiApiClient")
    public WebClient apiServiceClient(@Value("${app.api-service.url}") String url) {
        return WebClient.builder().baseUrl(url).build();
    }
}