package ca.yorku.eecs4314group12.ui.data;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import ca.yorku.eecs4314group12.ui.data.dto.MovieDTO;
import ca.yorku.eecs4314group12.ui.data.dto.ReviewDTO;
import ca.yorku.eecs4314group12.ui.data.dto.ReviewStatsDTO;

/**
 * Gateway service for all real backend calls from the ui-service.
 *
 * Movie data   → api-service  (port 8081) GET /api/movie/{id}
 * Review data  → review-service (port 8084) GET /api/reviews/movie/{id}
 *                                           GET /api/reviews/movie/{id}/stats
 *
 * All methods return Optional or empty List on failure so views can fall back
 * gracefully without crashing.
 */
@Service
public class BackendClientService {

    private static final Logger log = LoggerFactory.getLogger(BackendClientService.class);

    private final WebClient apiClient;
    private final WebClient reviewClient;

    public BackendClientService(
            @Qualifier("uiApiClient") WebClient apiClient,
            @Qualifier("uiReviewClient") WebClient reviewClient) {
        this.apiClient = apiClient;
        this.reviewClient = reviewClient;
    }

    // -------------------------------------------------------------------------
    // Movie
    // -------------------------------------------------------------------------

    /**
     * Fetches a single movie from api-service → movie-service → TMDB.
     * Returns empty if the movie doesn't exist or the service is down.
     */
    public Optional<MovieDTO> getMovieById(int id) {
        try {
            MovieDTO movie = apiClient.get()
                    .uri("/api/movie/{id}", id)
                    .retrieve()
                    .bodyToMono(MovieDTO.class)
                    .block();
            return Optional.ofNullable(movie);
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Movie {} not found in movie-service", id);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Failed to fetch movie {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    // -------------------------------------------------------------------------
    // Reviews
    // -------------------------------------------------------------------------

    /**
     * Fetches all reviews for a movie from review-service.
     * Returns empty list if no reviews exist or the service is down.
     *
     * Response shape: { success: true, data: [ ReviewDTO, ... ] }
     */
    public List<ReviewDTO> getReviewsForMovie(long movieId) {
        try {
            ApiResponse<List<ReviewDTO>> response = reviewClient.get()
                    .uri("/api/reviews/movie/{movieId}", movieId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<ReviewDTO>>>() {})
                    .block();
            if (response != null && response.isSuccess() && response.getData() != null) {
                return response.getData();
            }
            return List.of();
        } catch (Exception e) {
            log.error("Failed to fetch reviews for movie {}: {}", movieId, e.getMessage());
            return List.of();
        }
    }

    /**
     * Fetches average rating + review count for a movie from review-service.
     * Returns empty if the service is down.
     *
     * Response shape: { success: true, data: { averageRating: 8.5, reviewCount: 12 } }
     */
    public Optional<ReviewStatsDTO> getReviewStats(long movieId) {
        try {
            ApiResponse<ReviewStatsDTO> response = reviewClient.get()
                    .uri("/api/reviews/movie/{movieId}/stats", movieId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<ReviewStatsDTO>>() {})
                    .block();
            if (response != null && response.isSuccess()) {
                return Optional.ofNullable(response.getData());
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Failed to fetch review stats for movie {}: {}", movieId, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Submits a new review to review-service POST /api/reviews.
     * Returns true on success (HTTP 201), false on conflict (duplicate review)
     * or any server/network error.
     */
    public boolean createReview(ReviewDTO review) {
        try {
            ApiResponse<ReviewDTO> response = reviewClient.post()
                    .uri("/api/reviews")
                    .bodyValue(review)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<ReviewDTO>>() {})
                    .block();
            return response != null && response.isSuccess();
        } catch (WebClientResponseException.Conflict e) {
            log.warn("Duplicate review rejected by review-service: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Failed to submit review: {}", e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Inner class — matches the ApiResponse wrapper review-service returns
    // -------------------------------------------------------------------------

    public static class ApiResponse<T> {
        private boolean success;
        private T data;
        private String error;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}