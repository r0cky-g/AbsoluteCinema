package ca.yorku.eecs4314group12.api.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import ca.yorku.eecs4314group12.api.dto.reviewServiceDTO.ReviewDTO;
import reactor.core.publisher.Mono;

@Component
public class ReviewClient {

    private final BaseWebClient baseWebClient;

    public ReviewClient(@Qualifier("APIReviewClient") WebClient webClient) {
        this.baseWebClient = new BaseWebClient(webClient);
    }

    // there were issues with null bodies within ApiResponses and with due date fast approaching, everything is parsed as a map and no checks are made for invalid response

    public Mono<ResponseEntity<Map<String, Object>>> createReview(ReviewDTO reviewDTO) {
        return baseWebClient.post("/api/reviews", reviewDTO, new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    public Mono<ResponseEntity<Map<String, Object>>> getReviewsByMovie(Long movieId) {
        return baseWebClient.get("/api/reviews/movie/{movieId}", new ParameterizedTypeReference<Map<String, Object>>() {}, movieId);
    }

    public Mono<ResponseEntity<Map<String, Object>>> getReviewsByUser(long userId) {
        return baseWebClient.get("/api/reviews/user/{userId}", new ParameterizedTypeReference<Map<String, Object>>() {}, userId);
    }

    public Mono<ResponseEntity<Map<String, Object>>> getReviewById(long id) {
        return baseWebClient.get("/api/reviews/{id}", new ParameterizedTypeReference<Map<String, Object>>() {}, id);
    }

    public Mono<ResponseEntity<Map<String, Object>>> updateReview(Long id, ReviewDTO reviewDTO) {
        return baseWebClient.put("/api/reviews/{id}", reviewDTO, new ParameterizedTypeReference<Map<String, Object>>() {}, id);
    }

    public Mono<ResponseEntity<Map<String, Object>>> deleteReview(Long id, Long userId) {
        return baseWebClient.delete("/api/reviews/{id}?userId={userId}", new ParameterizedTypeReference<Map<String, Object>>() {}, id, userId);
    }

    public Mono<ResponseEntity<Map<String, Object>>> getMovieStats(Long movieId) {
        return baseWebClient.get("/api/reviews/movie/{movieId}/stats", new ParameterizedTypeReference<Map<String, Object>>() {}, movieId);
    }

    public Mono<ResponseEntity<Map<String, Object>>> markAsHelpful(Long id) {
        return baseWebClient.post("/api/reviews/{id}/helpful", null, new ParameterizedTypeReference<Map<String, Object>>() {}, id);
    }

}
