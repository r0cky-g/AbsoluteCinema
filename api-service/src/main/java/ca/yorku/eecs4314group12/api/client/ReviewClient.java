package ca.yorku.eecs4314group12.api.client;

import java.util.List;
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

    public Mono<ResponseEntity<ReviewDTO>> createReview(ReviewDTO reviewDTO) {
        return baseWebClient.post("/api/reviews", reviewDTO, new ParameterizedTypeReference<ReviewDTO>() {});
    }

    public Mono<ResponseEntity<List<ReviewDTO>>> getReviewsByMovie(Long movieId) {
        return baseWebClient.get("/api/reviews/movie/{movieId}", new ParameterizedTypeReference<List<ReviewDTO>>() {}, movieId);
    }

    public Mono<ResponseEntity<List<ReviewDTO>>> getReviewsByUser(long userId) {
        return baseWebClient.get("/api/reviews/user/{userId}", new ParameterizedTypeReference<List<ReviewDTO>>() {}, userId);
    }

    public Mono<ResponseEntity<ReviewDTO>> getReviewByID(long id) {
        return baseWebClient.get("/api/reviews/{id}", new ParameterizedTypeReference<ReviewDTO>() {}, id);
    }

    public Mono<ResponseEntity<ReviewDTO>> updateReview(Long id, ReviewDTO reviewDTO) {
        return baseWebClient.put("/api/reviews/{id}", reviewDTO, new ParameterizedTypeReference<ReviewDTO>() {}, id);
    }

    // should include role
    public Mono<ResponseEntity<Void>> deleteReview(Long id, Long userId) {
        return baseWebClient.delete("/api/reviews/{id}", null, id, userId);
    }

    // would really like to get <String, DTO> not just generic
    public Mono<ResponseEntity<Map<String, Object>>> getMovieStats(Long movieId) {
        return baseWebClient.get("/api/reviews/movie/{movieId}/stats", new ParameterizedTypeReference<Map<String, Object>>() {}, movieId);
    }

    public Mono<ResponseEntity<ReviewDTO>> markAsHelpful(Long id) {
        return baseWebClient.post("/api/reviews/{id}/helpful", null, new ParameterizedTypeReference<ReviewDTO>() {}, id);
    }

}
