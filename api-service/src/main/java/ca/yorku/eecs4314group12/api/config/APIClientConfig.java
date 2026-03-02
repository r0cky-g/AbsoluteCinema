package ca.yorku.eecs4314group12.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class APIClientConfig {

        // configure to use resourcies/application.yaml
    private final String userIp = "http://localhost:8082";
    private final String movieIp = "http://localhost:8080"; //should be 8083
    private final String reviewIp = "http://localhost:8084";
    // private final String forumIp = "";

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

    // @Bean("APIForumClient")
    // public WebClient forumClient() {
    //     return WebClient.builder()
    //             .baseUrl(forumIp)
    //             .build();
    // }
}