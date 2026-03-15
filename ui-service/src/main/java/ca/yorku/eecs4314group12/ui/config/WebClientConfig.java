package ca.yorku.eecs4314group12.ui.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Creates WebClient beans used by BackendClientService to call backend services.
 *
 * uiApiClient    → api-service    (movie detail via TMDB shape)
 * uiMovieClient  → movie-service  (trending, nowplaying, search via flat MovieDTO)
 * uiReviewClient → review-service (reviews)
 * uiForumClient  → forum-service  (forum posts and comments)
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

    @Bean("uiMovieClient")
    public WebClient movieServiceClient(
            @Value("${app.movie-service.url}") String movieServiceUrl) {
        return WebClient.builder()
                .baseUrl(movieServiceUrl)
                .build();
    }

    @Bean("uiReviewClient")
    public WebClient reviewServiceClient(
            @Value("${app.review-service.url}") String reviewServiceUrl) {
        return WebClient.builder()
                .baseUrl(reviewServiceUrl)
                .build();
    }

    @Bean("uiForumClient")
    public WebClient forumServiceClient(
            @Value("${app.forum-service.url}") String forumServiceUrl) {
        return WebClient.builder()
                .baseUrl(forumServiceUrl)
                .build();
    }
}