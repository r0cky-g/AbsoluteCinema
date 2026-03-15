package ca.yorku.eecs4314group12.ui.data;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import ca.yorku.eecs4314group12.ui.data.dto.ForumCommentDTO;
import ca.yorku.eecs4314group12.ui.data.dto.ForumPostDTO;
import ca.yorku.eecs4314group12.ui.data.dto.MovieDTO;
import ca.yorku.eecs4314group12.ui.data.dto.MovieListItemDTO;
import ca.yorku.eecs4314group12.ui.data.dto.ReviewDTO;
import ca.yorku.eecs4314group12.ui.data.dto.ReviewStatsDTO;

/**
 * Gateway service for all real backend calls from the ui-service.
 *
 * Movie detail  → api-service    (port 8081) GET /api/movie/{id}
 * Movie lists   → movie-service  (port 8083) GET /movie/trending
 *                                            GET /movie/nowplaying
 *                                            GET /movie/search/{name}
 * Review data   → review-service (port 8084) GET /api/reviews/movie/{id}
 *                                            GET /api/reviews/movie/{id}/stats
 * Forum data    → forum-service  (port 8085) GET/POST /forum/posts
 *                                            GET/POST /forum/comments
 *
 * All methods return Optional or empty List on failure so views can fall back
 * gracefully without crashing.
 */
@Service
public class BackendClientService {

    private static final Logger log = LoggerFactory.getLogger(BackendClientService.class);

    private final WebClient apiClient;
    private final WebClient reviewClient;
    private final WebClient movieClient;
    private final WebClient forumClient;

    public BackendClientService(
            @Qualifier("uiApiClient") WebClient apiClient,
            @Qualifier("uiReviewClient") WebClient reviewClient,
            @Qualifier("uiMovieClient") WebClient movieClient,
            @Qualifier("uiForumClient") WebClient forumClient) {
        this.apiClient = apiClient;
        this.reviewClient = reviewClient;
        this.movieClient = movieClient;
        this.forumClient = forumClient;
    }

    // -------------------------------------------------------------------------
    // Movie detail (via api-service → TMDB shape)
    // -------------------------------------------------------------------------

    /**
     * Fetches a single movie from api-service → movie-service → TMDB.
     * Returns the full TMDB-shaped MovieDTO including credits, poster_path, vote_average.
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
    // Movie lists (via movie-service directly → flat MovieDTO shape)
    // -------------------------------------------------------------------------

    /**
     * Fetches now playing movies from movie-service → TMDB.
     */
    public List<MovieListItemDTO> getNowPlaying() {
        try {
            MovieListResult result = movieClient.get()
                    .uri("/movie/nowplaying")
                    .retrieve()
                    .bodyToMono(MovieListResult.class)
                    .block();
            return result != null && result.getResults() != null ? result.getResults() : List.of();
        } catch (Exception e) {
            log.error("Failed to fetch now playing movies: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Fetches trending movies from movie-service → TMDB.
     */
    public List<MovieListItemDTO> getTrending() {
        try {
            MovieListResult result = movieClient.get()
                    .uri("/movie/trending")
                    .retrieve()
                    .bodyToMono(MovieListResult.class)
                    .block();
            return result != null && result.getResults() != null ? result.getResults() : List.of();
        } catch (Exception e) {
            log.error("Failed to fetch trending movies: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Searches movies by name via movie-service → MongoDB cache / TMDB.
     */
    public List<MovieListItemDTO> searchMovies(String query) {
        if (query == null || query.isBlank()) return List.of();
        try {
            MovieListResult result = movieClient.get()
                    .uri("/movie/search/{name}", query.trim())
                    .retrieve()
                    .bodyToMono(MovieListResult.class)
                    .block();
            return result != null && result.getResults() != null ? result.getResults() : List.of();
        } catch (Exception e) {
            log.error("Failed to search movies for '{}': {}", query, e.getMessage());
            return List.of();
        }
    }

    // -------------------------------------------------------------------------
    // Reviews
    // -------------------------------------------------------------------------

    /**
     * Fetches all reviews for a movie from review-service.
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
     * Submits a new review. Returns true on HTTP 201, false on conflict or error.
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
    // Forum — posts
    // -------------------------------------------------------------------------

    public List<ForumPostDTO> getAllPosts() {
        try {
            List<ForumPostDTO> posts = forumClient.get()
                    .uri("/forum/posts")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ForumPostDTO>>() {})
                    .block();
            return posts != null ? posts : List.of();
        } catch (Exception e) {
            log.error("Failed to fetch forum posts: {}", e.getMessage());
            return List.of();
        }
    }

    public Optional<ForumPostDTO> createPost(String title, String content, String authorUsername) {
        try {
            Map<String, String> body = Map.of(
                    "title", title,
                    "content", content,
                    "authorUsername", authorUsername != null ? authorUsername : "");
            ForumPostDTO post = forumClient.post()
                    .uri("/forum/posts")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(ForumPostDTO.class)
                    .block();
            return Optional.ofNullable(post);
        } catch (Exception e) {
            log.error("Failed to create forum post: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<ForumPostDTO> updatePost(long postId, String title, String content) {
        try {
            Map<String, String> body = Map.of("title", title, "content", content);
            ForumPostDTO post = forumClient.put()
                    .uri("/forum/posts/{id}", postId)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(ForumPostDTO.class)
                    .block();
            return Optional.ofNullable(post);
        } catch (Exception e) {
            log.error("Failed to update forum post {}: {}", postId, e.getMessage());
            return Optional.empty();
        }
    }

    public void deletePost(long postId) {
        try {
            forumClient.delete()
                    .uri("/forum/posts/{id}", postId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("Failed to delete forum post {}: {}", postId, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Forum — comments
    // -------------------------------------------------------------------------

    public List<ForumCommentDTO> getCommentsForPost(long postId) {
        try {
            List<ForumCommentDTO> comments = forumClient.get()
                    .uri("/forum/comments/{postId}", postId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ForumCommentDTO>>() {})
                    .block();
            return comments != null ? comments : List.of();
        } catch (Exception e) {
            log.error("Failed to fetch comments for post {}: {}", postId, e.getMessage());
            return List.of();
        }
    }

    public Optional<ForumCommentDTO> createComment(long postId, long userId, String content) {
        try {
            Map<String, Object> body = Map.of("postId", postId, "userId", userId, "content", content);
            ForumCommentDTO comment = forumClient.post()
                    .uri("/forum/comments")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(ForumCommentDTO.class)
                    .block();
            return Optional.ofNullable(comment);
        } catch (Exception e) {
            log.error("Failed to create comment on post {}: {}", postId, e.getMessage());
            return Optional.empty();
        }
    }

    // -------------------------------------------------------------------------
    // Inner classes
    // -------------------------------------------------------------------------

    /** Matches the { results: [...] } wrapper from movie-service list endpoints. */
    public static class MovieListResult {
        private List<MovieListItemDTO> results;
        public List<MovieListItemDTO> getResults() { return results; }
        public void setResults(List<MovieListItemDTO> results) { this.results = results; }
    }

    /** Matches the ApiResponse wrapper review-service returns. */
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