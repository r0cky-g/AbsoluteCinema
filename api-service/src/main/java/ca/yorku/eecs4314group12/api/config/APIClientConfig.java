package ca.yorku.eecs4314group12.api.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

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
        ConnectionProvider provider = ConnectionProvider.builder("movie-client")
            .maxConnections(50)
            .maxIdleTime(Duration.ofSeconds(30))  
            .maxLifeTime(Duration.ofSeconds(60))
            .evictInBackground(Duration.ofSeconds(30))
            .build();

        HttpClient httpClient = HttpClient.create(provider)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);

        return WebClient.builder()
            .baseUrl(movieIp)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
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