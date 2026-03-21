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

    @Bean("uiMovieClient")
    public WebClient movieServiceClient(@Value("${app.movie-service.url}") String url) {
        return WebClient.builder().baseUrl(url).build();
    }

    @Bean("uiReviewClient")
    public WebClient reviewServiceClient(@Value("${app.review-service.url}") String url) {
        return WebClient.builder().baseUrl(url).build();
    }

    @Bean("uiForumClient")
    public WebClient forumServiceClient(@Value("${app.forum-service.url}") String url) {
        return WebClient.builder().baseUrl(url).build();
    }

    @Bean("uiUserClient")
    public WebClient userServiceClient(@Value("${app.user-service.url}") String url) {
        return WebClient.builder().baseUrl(url).build();
    }
}