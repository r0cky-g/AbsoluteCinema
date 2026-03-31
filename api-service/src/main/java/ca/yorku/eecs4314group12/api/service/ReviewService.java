package ca.yorku.eecs4314group12.api.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ca.yorku.eecs4314group12.api.client.ReviewClient;
import ca.yorku.eecs4314group12.api.dto.reviewServiceDTO.ReviewDTO;
import reactor.core.publisher.Mono;

@Service
public class ReviewService {

    private final ReviewClient reviewClient;

    public ReviewService(ReviewClient reviewClient) {
        this.reviewClient = reviewClient;
    }

    public Mono<ResponseEntity<Map<String, Object>>> createReview(ReviewDTO reviewDTO) {
        return reviewClient.createReview(reviewDTO);
    }

    public Mono<ResponseEntity<Map<String, Object>>> getReviewsByMovie(Long movieId) {
        return reviewClient.getReviewsByMovie(movieId);
    }

    public Mono<ResponseEntity<Map<String, Object>>> getReviewsByUser(long userId) {
        return reviewClient.getReviewsByUser(userId);
    }

    public Mono<ResponseEntity<Map<String, Object>>> getReviewById(long id) {
        return reviewClient.getReviewById(id);
    }

    public Mono<ResponseEntity<Map<String, Object>>> updateReview(Long id, ReviewDTO reviewDTO) {
        return reviewClient.updateReview(id, reviewDTO);
    }

    public Mono<ResponseEntity<Map<String, Object>>> deleteReview(Long id, Long userId) {
        return reviewClient.deleteReview(id, userId);
    }

    public Mono<ResponseEntity<Map<String, Object>>> getMovieStats(Long movieId) {
        return reviewClient.getMovieStats(movieId);
    }

    public Mono<ResponseEntity<Map<String, Object>>> markAsHelpful(Long id) {
        return reviewClient.markAsHelpful(id);
    }
}
