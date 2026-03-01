package ca.yorku.eecs4314group12.api.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ReviewClient {

    private final WebClient reviewClient;

    public ReviewClient(@Qualifier("APIReviewClient") WebClient reviewClient) {
        this.reviewClient = reviewClient;
    }
}
