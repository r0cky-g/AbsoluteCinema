package ca.yorku.eecs4314group12.api.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class APIClientConfig {

    private final String userIp;
    private final String movieIp;
    private final String reviewIp;
    private final String forumIp;

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

    private WebClient buildClient(String baseUrl) {
        HttpClient httpClient = HttpClient.create()
                .followRedirect(true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000)
                .responseTimeout(Duration.ofSeconds(60))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS))
                            .addHandlerLast(new WriteTimeoutHandler(60, TimeUnit.SECONDS)));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean("APIUserClient")
    WebClient userClient() { return buildClient(userIp); }

    @Bean("APIMovieClient")
    WebClient movieClient() { return buildClient(movieIp); }

    @Bean("APIReviewClient")
    WebClient reviewClient() { return buildClient(reviewIp); }

    @Bean("APIForumClient")
    WebClient forumClient() { return buildClient(forumIp); }
}