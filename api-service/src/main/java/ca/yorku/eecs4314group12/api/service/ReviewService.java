package ca.yorku.eecs4314group12.api.service;

import java.util.List;
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

    public Mono<ResponseEntity<ReviewDTO>> createReview(ReviewDTO reviewDTO) {
        return reviewClient.createReview(reviewDTO);
    }

    public Mono<ResponseEntity<List<ReviewDTO>>> getReviewsByMovie(Long movieId) {
        return reviewClient.getReviewsByMovie(movieId);
    }

    public Mono<ResponseEntity<List<ReviewDTO>>> getReviewsByUser(long userId) {
        return reviewClient.getReviewsByUser(userId);
    }

    public Mono<ResponseEntity<ReviewDTO>> getReviewByID(long id) {
        return reviewClient.getReviewByID(id);
    }

    public Mono<ResponseEntity<ReviewDTO>> updateReview(Long id, ReviewDTO reviewDTO) {
        return reviewClient.updateReview(id, reviewDTO);
    }

    // should include role
    public Mono<ResponseEntity<Void>> deleteReview(Long id, Long userId) {
        return reviewClient.deleteReview(id, userId);
    }

    // would really like to get <String, DTO> not just generic
    public Mono<ResponseEntity<Map<String, Object>>> getMovieStats(Long movieId) {
        return reviewClient.getMovieStats(movieId);
    }

    public Mono<ResponseEntity<ReviewDTO>> markAsHelpful(Long id) {
        return reviewClient.markAsHelpful(id);
    }
}
