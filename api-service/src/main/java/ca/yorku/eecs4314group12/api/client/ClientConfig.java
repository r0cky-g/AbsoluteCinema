package ca.yorku.eecs4314group12.api.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

        // configure to use resourcies/application.yaml
    private final String movieIp = "http://localhost:8080/movie/";
    private final String userIp = "";
    private final String reviewIp = "";
    // private final String forumIp = "";

    @Bean("APIMovieClient")
    public WebClient movieClient() {
        return WebClient.builder()
                .baseUrl(movieIp)
                .build();
    }

    @Bean("APIUserClient")
    public WebClient userClient() {
        return WebClient.builder()
                .baseUrl(userIp)
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