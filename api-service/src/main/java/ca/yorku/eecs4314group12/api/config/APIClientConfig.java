package ca.yorku.eecs4314group12.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class APIClientConfig {

    @Value("${app.user-service.url:http://localhost:8082}")
    private String userIp;

    @Value("${app.movie-service.url:http://localhost:8083}")
    private String movieIp;

    @Value("${app.review-service.url:http://localhost:8084}")
    private String reviewIp;

    @Value("${app.forum-service.url:http://localhost:8085}")
    private String forumIp;

    @Bean("APIUserClient")
    public WebClient userClient() {
        return WebClient.builder()
                .baseUrl(userIp)
                .build();
    }

    @Bean("APIMovieClient")
    public WebClient movieClient() {
        return WebClient.builder()
                .baseUrl(movieIp)
                .build();
    }

    @Bean("APIReviewClient")
    public WebClient reviewClient() {
        return WebClient.builder()
                .baseUrl(reviewIp)
                .build();
    }

    @Bean("ForumReviewClient")
    public WebClient forumClient() {
        return WebClient.builder()
                .baseUrl(forumIp)
                .build();
    }
}