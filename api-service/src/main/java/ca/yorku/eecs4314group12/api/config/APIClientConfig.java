package ca.yorku.eecs4314group12.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class APIClientConfig {

    private String userIp;
    private String movieIp;
    private String reviewIp;
    private String forumIp;
    
    public APIClientConfig(
            @Value("${app.user-service.url}") String userIp,
            @Value("${app.movie-service.url}") String movieIp,
            @Value("${app.review-service.url}") String reviewIp,
            @Value("${app.forum-service.url}") String forumIp
    ) {
        this.userIp = userIp;
        this.movieIp = movieIp;
        this.reviewIp = reviewIp;
        this.forumIp = forumIp;
    }

    @Bean("APIUserClient")
    WebClient userClient() {
        return WebClient.builder()
                .baseUrl(userIp)
                .build();
    }

    @Bean("APIMovieClient")
    WebClient movieClient() {
        return WebClient.builder()
                .baseUrl(movieIp)
                .build();
    }

    @Bean("APIReviewClient")
    WebClient reviewClient() {
        return WebClient.builder()
                .baseUrl(reviewIp)
                .build();
    }

    @Bean("APIForumClient")
    WebClient forumClient() {
        return WebClient.builder()
                .baseUrl(forumIp)
                .build();
    }
}