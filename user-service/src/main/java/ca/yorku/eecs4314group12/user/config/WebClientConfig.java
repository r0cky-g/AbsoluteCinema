package ca.yorku.eecs4314group12.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean("movieServiceClient")
    public WebClient movieServiceClient(
            @Value("${app.movie-service.url:http://localhost:8083}") String movieServiceUrl) {
        return WebClient.builder()
                .baseUrl(movieServiceUrl)
                .build();
    }
}
