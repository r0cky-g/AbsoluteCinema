package ca.yorku.eecs4314group12.api.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import ca.yorku.eecs4314group12.api.dto.ApiResponse;
import ca.yorku.eecs4314group12.api.dto.reviewServiceDTO.ReviewDTO;
import reactor.core.publisher.Mono;

@Component
public class ReviewClient {

    private final WebClient webClient;

    public ReviewClient(@Qualifier("APIReviewClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ApiResponse<ReviewDTO>> createReview(ReviewDTO reviewDTO) {
        return webClient.post()
                .uri("/review/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(reviewDTO)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<ReviewDTO>>() {});
    }
}
