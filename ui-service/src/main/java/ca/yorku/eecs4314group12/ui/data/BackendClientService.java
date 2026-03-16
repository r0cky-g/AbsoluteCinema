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
import ca.yorku.eecs4314group12.ui.data.dto.UserResponseDTO;
import ca.yorku.eecs4314group12.ui.data.dto.WatchlistDTO;

/**
 * Gateway service for all real backend calls from the ui-service.
 *
 * Movie detail  → api-service    (port 8081) GET /api/movie/{id}
 * Movie lists   → movie-service  (port 8083) GET /movie/trending|nowplaying|search
 * Review data   → review-service (port 8084) GET/POST /api/reviews
 * Forum data    → forum-service  (port 8085) GET/POST /forum/posts|comments
 * User data     → user-service   (port 8082) POST /user/login|register, GET/POST /user/{id}/watchlist
 *                                            GET /user/{id}/recommendations
 */
@Service
public class BackendClientService {

    private static final Logger log = LoggerFactory.getLogger(BackendClientService.class);

    private final WebClient apiClient;
    private final WebClient reviewClient;
    private final WebClient movieClient;
    private final WebClient forumClient;
    private final WebClient userClient;

    public BackendClientService(
            @Qualifier("uiApiClient") WebClient apiClient,
            @Qualifier("uiReviewClient") WebClient reviewClient,
            @Qualifier("uiMovieClient") WebClient movieClient,
            @Qualifier("uiForumClient") WebClient forumClient,
            @Qualifier("uiUserClient") WebClient userClient) {
        this.apiClient = apiClient;
        this.reviewClient = reviewClient;
        this.movieClient = movieClient;
        this.forumClient = forumClient;
        this.userClient = userClient;
    }

    // -------------------------------------------------------------------------
    // Movie detail (via api-service → TMDB shape)
    // -------------------------------------------------------------------------

    public Optional<MovieDTO> getMovieById(int id) {
        try {
            MovieDTO movie = apiClient.get()
                    .uri("/api/movie/{id}", id)
                    .retrieve()
                    .bodyToMono(MovieDTO.class)
                    .block();
            return Optional.ofNullable(movie);
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Movie {} not found", id);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Failed to fetch movie {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    // -------------------------------------------------------------------------
    // Movie lists (via movie-service directly)
    // -------------------------------------------------------------------------

    public List<MovieListItemDTO> getNowPlaying() {
        try {
            MovieListResult result = movieClient.get()
                    .uri("/movie/nowplaying")
                    .retrieve()
                    .bodyToMono(MovieListResult.class)
                    .block();
            return result != null && result.getResults() != null ? result.getResults() : List.of();
        } catch (Exception e) {
            log.error("Failed to fetch now playing: {}", e.getMessage());
            return List.of();
        }
    }

    public List<MovieListItemDTO> getTrending() {
        try {
            MovieListResult result = movieClient.get()
                    .uri("/movie/trending")
                    .retrieve()
                    .bodyToMono(MovieListResult.class)
                    .block();
            return result != null && result.getResults() != null ? result.getResults() : List.of();
        } catch (Exception e) {
            log.error("Failed to fetch trending: {}", e.getMessage());
            return List.of();
        }
    }

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
            log.error("Failed to search movies '{}': {}", query, e.getMessage());
            return List.of();
        }
    }

    // -------------------------------------------------------------------------
    // Reviews
    // -------------------------------------------------------------------------

    public List<ReviewDTO> getReviewsForMovie(long movieId) {
        try {
            ApiResponse<List<ReviewDTO>> response = reviewClient.get()
                    .uri("/api/reviews/movie/{movieId}", movieId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<ReviewDTO>>>() {})
                    .block();
            if (response != null && response.isSuccess() && response.getData() != null)
                return response.getData();
            return List.of();
        } catch (Exception e) {
            log.error("Failed to fetch reviews for movie {}: {}", movieId, e.getMessage());
            return List.of();
        }
    }

    public Optional<ReviewStatsDTO> getReviewStats(long movieId) {
        try {
            ApiResponse<ReviewStatsDTO> response = reviewClient.get()
                    .uri("/api/reviews/movie/{movieId}/stats", movieId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<ReviewStatsDTO>>() {})
                    .block();
            if (response != null && response.isSuccess())
                return Optional.ofNullable(response.getData());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Failed to fetch review stats for movie {}: {}", movieId, e.getMessage());
            return Optional.empty();
        }
    }

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
            log.warn("Duplicate review rejected: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Failed to submit review: {}", e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // User auth
    // -------------------------------------------------------------------------

    /**
     * Authenticates a user via user-service POST /user/login.
     * Returns the UserResponseDTO (with id, role, email) on success.
     */
    public Optional<UserResponseDTO> loginUser(String identifier, String password) {
        try {
            Map<String, String> body = Map.of("identifier", identifier, "password", password);
            UserResponseDTO response = userClient.post()
                    .uri("/user/login")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(UserResponseDTO.class)
                    .block();
            return Optional.ofNullable(response);
        } catch (WebClientResponseException.Unauthorized e) {
            log.warn("Login failed for '{}': invalid credentials", identifier);
            return Optional.empty();
        } catch (WebClientResponseException.BadRequest e) {
            log.warn("Login failed for '{}': bad request", identifier);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Login error for '{}': {}", identifier, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Registers a new user via user-service POST /user/register.
     * Returns true on success (HTTP 201).
     */
    public boolean registerUser(String username, String password, String email, boolean moderator) {
        try {
            Map<String, Object> body = Map.of(
                    "username", username,
                    "password", password,
                    "email", email,
                    "over18", true,
                    "moderator", moderator);
            userClient.post()
                    .uri("/user/register")
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;
        } catch (Exception e) {
            log.error("Registration failed for '{}': {}", username, e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Watchlist
    // -------------------------------------------------------------------------

    public List<WatchlistDTO> getWatchlist(long userId) {
        try {
            List<WatchlistDTO> list = userClient.get()
                    .uri("/user/{userId}/watchlist", userId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<WatchlistDTO>>() {})
                    .block();
            return list != null ? list : List.of();
        } catch (Exception e) {
            log.error("Failed to fetch watchlist for user {}: {}", userId, e.getMessage());
            return List.of();
        }
    }

    public boolean addToWatchlist(long userId, int movieId) {
        try {
            userClient.post()
                    .uri("/user/{userId}/watchlist/{movieId}", userId, movieId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;
        } catch (WebClientResponseException.Conflict e) {
            log.warn("Movie {} already in watchlist for user {}", movieId, userId);
            return false;
        } catch (Exception e) {
            log.error("Failed to add movie {} to watchlist: {}", movieId, e.getMessage());
            return false;
        }
    }

    public boolean removeFromWatchlist(long userId, int movieId) {
        try {
            userClient.delete()
                    .uri("/user/{userId}/watchlist/{movieId}", userId, movieId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;
        } catch (Exception e) {
            log.error("Failed to remove movie {} from watchlist: {}", movieId, e.getMessage());
            return false;
        }
    }

    public boolean isInWatchlist(long userId, int movieId) {
        try {
            Boolean result = userClient.get()
                    .uri("/user/{userId}/watchlist/{movieId}", userId, movieId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // User preferences
    // -------------------------------------------------------------------------

    /**
     * Gets user data including preferences (liked genres) via user-service GET /user/{id}.
     */
    public Optional<UserResponseDTO> getUserData(long userId) {
        try {
            UserResponseDTO userData = userClient.get()
                    .uri("/user/{id}", userId)
                    .retrieve()
                    .bodyToMono(UserResponseDTO.class)
                    .block();
            return Optional.ofNullable(userData);
        } catch (Exception e) {
            log.error("Failed to fetch user data for user {}: {}", userId, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Updates user preferences (liked genres) via user-service PUT /user/{id}.
     */
    public boolean updateUserPreferences(long userId, java.util.Set<String> likedGenres) {
        try {
            // Get existing user data first
            Optional<UserResponseDTO> existingUserOpt = getUserData(userId);
            if (existingUserOpt.isEmpty()) {
                log.error("User {} not found", userId);
                return false;
            }

            UserResponseDTO existingUser = existingUserOpt.get();
            
            // Create update payload with required fields and likedGenres
            // Note: We need to include all required fields (username, email) and preserve other fields
            Map<String, Object> updatePayload = new java.util.HashMap<>();
            updatePayload.put("username", existingUser.getUsername());
            updatePayload.put("email", existingUser.getEmail());
            updatePayload.put("likedGenres", likedGenres);
            // Note: over18 and other fields will use defaults if not provided
            // The service will preserve existing values for fields not in the request
            
            userClient.put()
                    .uri("/user/{id}", userId)
                    .bodyValue(updatePayload)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;
        } catch (Exception e) {
            log.error("Failed to update preferences for user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Recommendations
    // -------------------------------------------------------------------------

    public List<MovieListItemDTO> getRecommendations(long userId) {
        try {
            // Note: user-service returns List<MovieDTO>, but we need MovieListItemDTO
            // Spring will map common fields automatically
            List<MovieListItemDTO> list = userClient.get()
                    .uri("/user/{userId}/recommendations", userId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<MovieListItemDTO>>() {})
                    .block();
            if (list != null) {
                log.debug("Fetched {} recommendations for user {}", list.size(), userId);
                return list;
            } else {
                log.warn("Recommendations endpoint returned null for user {}", userId);
                return List.of();
            }
        } catch (Exception e) {
            log.error("Failed to fetch recommendations for user {}: {}", userId, e.getMessage(), e);
            return List.of();
        }
    }

    public List<ReviewDTO> getReviewsForUser(long userId) {
        try {
            ApiResponse<List<ReviewDTO>> response = reviewClient.get()
                    .uri("/api/reviews/user/{userId}", userId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<ReviewDTO>>>() {})
                    .block();
            if (response != null && response.isSuccess() && response.getData() != null)
                return response.getData();
            return List.of();
        } catch (Exception e) {
            log.error("Failed to fetch reviews for user {}: {}", userId, e.getMessage());
            return List.of();
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

    public Optional<ForumPostDTO> createPost(String title, String content, Long userId) {
        try {
            Map<String, Object> body = Map.of(
                    "title", title, "content", content,
                    "userId", userId != null ? userId : 0L);
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

    public boolean deletePost(long postId, long userId, String userRole) {
        try {
            userClient.delete()
                    .uri("/forum/posts/{id}?userId={userId}&userRole={userRole}",
                            postId, userId, userRole)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;
        } catch (WebClientResponseException e) {
            log.warn("Delete post {} rejected ({})", postId, e.getStatusCode());
            return false;
        } catch (Exception e) {
            log.error("Failed to delete forum post {}: {}", postId, e.getMessage());
            return false;
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

    public static class MovieListResult {
        private List<MovieListItemDTO> results;
        public List<MovieListItemDTO> getResults() { return results; }
        public void setResults(List<MovieListItemDTO> results) { this.results = results; }
    }

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