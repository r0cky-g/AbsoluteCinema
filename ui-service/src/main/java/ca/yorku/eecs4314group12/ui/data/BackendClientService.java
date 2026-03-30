package ca.yorku.eecs4314group12.ui.data;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import ca.yorku.eecs4314group12.ui.data.dto.FavouriteMovieDTO;
import ca.yorku.eecs4314group12.ui.data.dto.ForumCommentDTO;
import ca.yorku.eecs4314group12.ui.data.dto.ForumPostDTO;
import ca.yorku.eecs4314group12.ui.data.dto.MovieDTO;
import ca.yorku.eecs4314group12.ui.data.dto.MovieListItemDTO;
import ca.yorku.eecs4314group12.ui.data.dto.ReviewDTO;
import ca.yorku.eecs4314group12.ui.data.dto.ReviewStatsDTO;
import ca.yorku.eecs4314group12.ui.data.dto.UserResponseDTO;
import ca.yorku.eecs4314group12.ui.data.dto.WatchHistoryDTO;
import ca.yorku.eecs4314group12.ui.data.dto.WatchlistDTO;

@Service
public class BackendClientService {

    private static final Logger log = LoggerFactory.getLogger(BackendClientService.class);

    private final WebClient apiClient;

    public BackendClientService(@Qualifier("uiApiClient") WebClient apiClient){
        this.apiClient = apiClient;
    }

    // -------------------------------------------------------------------------
    // Movie detail
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

    /**
     * Fetches a single movie from movie-service directly (MongoDB cache → TMDB).
     * Faster than getMovieById() for bulk loads since it skips the api-service hop.
     * Returns a MovieListItemDTO with poster_path, title, genres etc.
     */
    public Optional<MovieListItemDTO> getMovieSummary(int id) {
        try {
            MovieListItemDTO movie = apiClient.get()
                    .uri("/api/movie/{id}", id)
                    .retrieve()
                    .bodyToMono(MovieListItemDTO.class)
                    .block();
            return Optional.ofNullable(movie);
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Movie {} not found in movie-service", id);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Failed to fetch movie summary {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    // -------------------------------------------------------------------------
    // Movie lists
    // -------------------------------------------------------------------------

    public List<MovieListItemDTO> getNowPlaying() {
        try {
            MovieListResult result = apiClient.get().uri("/api/movie/nowplaying")
                    .retrieve().bodyToMono(MovieListResult.class).block();
            return result != null && result.getResults() != null ? result.getResults() : List.of();
        } catch (Exception e) { log.error("Failed to fetch now playing: {}", e.getMessage()); return List.of(); }
    }

    public List<MovieListItemDTO> getTrending() {
        try {
            MovieListResult result = apiClient.get().uri("/api/movie/trending")
                    .retrieve().bodyToMono(MovieListResult.class).block();
            return result != null && result.getResults() != null ? result.getResults() : List.of();
        } catch (Exception e) { log.error("Failed to fetch trending: {}", e.getMessage()); return List.of(); }
    }

    public List<MovieListItemDTO> searchMovies(String query) {
        if (query == null || query.isBlank()) return List.of();
        try {
            MovieListResult result = apiClient.get().uri("/api/movie/search/{name}", query.trim())
                    .retrieve().bodyToMono(MovieListResult.class).block();
            return result != null && result.getResults() != null ? result.getResults() : List.of();
        } catch (Exception e) { log.error("Failed to search movies: {}", e.getMessage()); return List.of(); }
    }

    // -------------------------------------------------------------------------
    // Reviews
    // -------------------------------------------------------------------------

    public List<ReviewDTO> getReviewsForMovie(long movieId) {
        try {
            ApiResponse<List<ReviewDTO>> r = apiClient.get()
                    .uri("/api/reviews/movie/{movieId}", movieId).retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<ReviewDTO>>>() {}).block();
            return r != null && r.isSuccess() && r.getData() != null ? r.getData() : List.of();
        } catch (Exception e) { log.error("Failed to fetch reviews for movie {}: {}", movieId, e.getMessage()); return List.of(); }
    }

    public Optional<ReviewStatsDTO> getReviewStats(long movieId) {
        try {
            ApiResponse<ReviewStatsDTO> r = apiClient.get()
                    .uri("/api/reviews/movie/{movieId}/stats", movieId).retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<ReviewStatsDTO>>() {}).block();
            return r != null && r.isSuccess() ? Optional.ofNullable(r.getData()) : Optional.empty();
        } catch (Exception e) { log.error("Failed to fetch review stats: {}", e.getMessage()); return Optional.empty(); }
    }

    public List<ReviewDTO> getReviewsForUser(long userId) {
        try {
            ApiResponse<List<ReviewDTO>> r = apiClient.get()
                    .uri("/api/reviews/user/{userId}", userId).retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<ReviewDTO>>>() {}).block();
            return r != null && r.isSuccess() && r.getData() != null ? r.getData() : List.of();
        } catch (Exception e) { log.error("Failed to fetch reviews for user {}: {}", userId, e.getMessage()); return List.of(); }
    }

    public boolean createReview(ReviewDTO review) {
        try {
            ApiResponse<ReviewDTO> r = apiClient.post().uri("/api/reviews")
                    .bodyValue(review).retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<ReviewDTO>>() {}).block();
            return r != null && r.isSuccess();
        } catch (WebClientResponseException.Conflict e) { return false; }
        catch (Exception e) { log.error("Failed to submit review: {}", e.getMessage()); return false; }
    }

    /**
     * Marks a review as helpful via POST /api/reviews/{id}/helpful.
     * Wired to review-service — increments helpfulCount.
     */
    public Optional<ReviewDTO> markReviewHelpful(long reviewId) {
        try {
            ApiResponse<ReviewDTO> r = apiClient.post()
                    .uri("/api/reviews/{id}/helpful", reviewId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<ReviewDTO>>() {})
                    .block();
            return r != null && r.isSuccess() ? Optional.ofNullable(r.getData()) : Optional.empty();
        } catch (Exception e) {
            log.error("Failed to mark review {} as helpful: {}", reviewId, e.getMessage());
            return Optional.empty();
        }
    }

    public boolean deleteReview(long reviewId, long userId, String userRole) {
        try {
            apiClient.delete()
                    .uri(uriBuilder -> uriBuilder.path("/api/reviews/{id}")
                            .queryParam("userId", userId)
                            .queryParam("userRole", userRole)
                            .build(reviewId))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;
        } catch (WebClientResponseException e) {
            log.warn("Delete review {} failed: {}", reviewId, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Delete review {} error: {}", reviewId, e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // User auth
    // -------------------------------------------------------------------------

    public Optional<UserResponseDTO> loginUser(String identifier, String password) {
        try {
            Map<String, String> body = Map.of("identifier", identifier, "password", password);
            UserResponseDTO r = apiClient.post().uri("/api/user/login").bodyValue(body)
                    .retrieve().bodyToMono(UserResponseDTO.class).block();
            return Optional.ofNullable(r);
        } catch (WebClientResponseException.Unauthorized e) { return Optional.empty(); }
        catch (WebClientResponseException.BadRequest e) { return Optional.empty(); }
        catch (Exception e) { log.error("Login error: {}", e.getMessage()); return Optional.empty(); }
    }

    public boolean registerUser(String username, String password, String email) {
        try {
            Map<String, Object> body = Map.of("username", username, "password", password,
                    "email", email, "over18", true);
            apiClient.post().uri("/api/user/register").bodyValue(body)
                    .retrieve().toBodilessEntity().block();
            return true;
        } catch (Exception e) { log.error("Registration failed: {}", e.getMessage()); return false; }
    }

    /**
     * Registers a new user and returns the full UserResponseDTO (including numeric id)
     * so the caller can redirect to the verification page.
     */
    public Optional<UserResponseDTO> registerUserFull(String username, String password, String email) {
        try {
            Map<String, Object> body = Map.of("username", username, "password", password,
                    "email", email, "over18", true);
            UserResponseDTO response = apiClient.post().uri("/api/user/register")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(UserResponseDTO.class)
                    .block();
            return Optional.ofNullable(response);
        } catch (Exception e) { log.error("Registration failed: {}", e.getMessage()); return Optional.empty(); }
    }

    /**
     * Verifies a user's email via POST /user/{id}/verify?code={code}.
     */
    public boolean verifyEmail(long userId, String code) {
        try {
            apiClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/api/user/{id}/verify")
                            .queryParam("code", code).build(userId))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;
        } catch (Exception e) { log.error("Email verification failed for user {}: {}", userId, e.getMessage()); return false; }
    }

    // -------------------------------------------------------------------------
    // User profile
    // -------------------------------------------------------------------------

    public Optional<UserResponseDTO> getUserData(long userId) {
        try {
            UserResponseDTO r = apiClient.get().uri("/api/user/{id}", userId)
                    .retrieve().bodyToMono(UserResponseDTO.class).block();
            return Optional.ofNullable(r);
        } catch (Exception e) { log.error("Failed to fetch user data: {}", e.getMessage()); return Optional.empty(); }
    }

    public boolean updateUser(long userId, String username, String email,
                              String newPassword, java.time.LocalDate dob,
                              Boolean over18, Set<String> likedGenres) {
        try {
            java.util.Map<String, Object> body = new java.util.HashMap<>();
            body.put("username", username);
            body.put("email", email);
            if (newPassword != null && !newPassword.isBlank()) body.put("password", newPassword);
            if (dob != null) body.put("dob", dob.toString());
            if (over18 != null) body.put("over18", over18);
            if (likedGenres != null) body.put("likedGenres", likedGenres);
            apiClient.put().uri("/api/user/{id}", userId).bodyValue(body)
                    .retrieve().toBodilessEntity().block();
            return true;
        } catch (Exception e) { log.error("Failed to update user {}: {}", userId, e.getMessage()); return false; }
    }

    public boolean updateUserPreferences(long userId, Set<String> likedGenres) {
        Optional<UserResponseDTO> existing = getUserData(userId);
        if (existing.isEmpty()) return false;
        return updateUser(userId, existing.get().getUsername(), existing.get().getEmail(),
                null, null, null, likedGenres);
    }

    public List<UserResponseDTO> listAllUsers() {
        try {
            List<UserResponseDTO> list = apiClient.get().uri("/api/user")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<UserResponseDTO>>() {})
                    .block();
            return list != null ? list : List.of();
        } catch (Exception e) {
            log.error("Failed to list users: {}", e.getMessage());
            return List.of();
        }
    }

    public Optional<UserResponseDTO> promoteToModerator(long targetUserId,
            String adminIdentifier, String adminPassword) {
        try {
            Map<String, String> body = Map.of(
                    "adminIdentifier", adminIdentifier,
                    "adminPassword", adminPassword);
            UserResponseDTO r = apiClient.post()
                    .uri("/api/user/{userId}/promote-moderator", targetUserId)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(UserResponseDTO.class)
                    .block();
            return Optional.ofNullable(r);
        } catch (Exception e) {
            log.warn("Promote moderator failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<UserResponseDTO> demoteModerator(long targetUserId,
            String adminIdentifier, String adminPassword) {
        try {
            Map<String, String> body = Map.of(
                    "adminIdentifier", adminIdentifier,
                    "adminPassword", adminPassword);
            UserResponseDTO r = apiClient.post()
                    .uri("/api/user/{userId}/demote-moderator", targetUserId)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(UserResponseDTO.class)
                    .block();
            return Optional.ofNullable(r);
        } catch (Exception e) {
            log.warn("Demote moderator failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    // -------------------------------------------------------------------------
    // Watchlist
    // -------------------------------------------------------------------------

    public List<WatchlistDTO> getWatchlist(long userId) {
        try {
            List<WatchlistDTO> list = apiClient.get().uri("/api/user/{userId}/watchlist", userId)
                    .retrieve().bodyToMono(new ParameterizedTypeReference<List<WatchlistDTO>>() {}).block();
            return list != null ? list : List.of();
        } catch (Exception e) { log.error("Failed to fetch watchlist: {}", e.getMessage()); return List.of(); }
    }

    public boolean addToWatchlist(long userId, int movieId) {
        try {
            apiClient.post().uri("/api/user/{userId}/watchlist/{movieId}", userId, movieId)
                    .retrieve().toBodilessEntity().block();
            return true;
        } catch (WebClientResponseException.Conflict e) { return false; }
        catch (Exception e) { log.error("Failed to add to watchlist: {}", e.getMessage()); return false; }
    }

    public boolean removeFromWatchlist(long userId, int movieId) {
        try {
            apiClient.delete().uri("/api/user/{userId}/watchlist/{movieId}", userId, movieId)
                    .retrieve().toBodilessEntity().block();
            return true;
        } catch (Exception e) { log.error("Failed to remove from watchlist: {}", e.getMessage()); return false; }
    }

    public boolean isInWatchlist(long userId, int movieId) {
        try {
            Boolean r = apiClient.get().uri("/api/user/{userId}/watchlist/{movieId}", userId, movieId)
                    .retrieve().bodyToMono(Boolean.class).block();
            return Boolean.TRUE.equals(r);
        } catch (Exception e) { return false; }
    }

    // -------------------------------------------------------------------------
    // Watch History
    // -------------------------------------------------------------------------

    public List<WatchHistoryDTO> getWatchHistory(long userId) {
        try {
            List<WatchHistoryDTO> list = apiClient.get().uri("/api/user/{userId}/history", userId)
                    .retrieve().bodyToMono(new ParameterizedTypeReference<List<WatchHistoryDTO>>() {}).block();
            return list != null ? list : List.of();
        } catch (Exception e) { log.error("Failed to fetch watch history: {}", e.getMessage()); return List.of(); }
    }

    public boolean addToWatchHistory(long userId, int movieId) {
        try {
            apiClient.post().uri("/api/user/{userId}/history/{movieId}", userId, movieId)
                    .retrieve().toBodilessEntity().block();
            return true;
        } catch (Exception e) { log.error("Failed to add to watch history: {}", e.getMessage()); return false; }
    }

    public boolean removeFromWatchHistory(long userId, int movieId) {
        try {
            apiClient.delete().uri("/api/user/{userId}/history/{movieId}", userId, movieId)
                    .retrieve().toBodilessEntity().block();
            return true;
        } catch (Exception e) { log.error("Failed to remove from watch history: {}", e.getMessage()); return false; }
    }
    
    public boolean isInWatchHistory(long userId, int movieId) {
        try {
            return getWatchHistory(userId).stream()
                    .anyMatch(f -> f.getMovieId() != null && f.getMovieId() == movieId);
        } catch (Exception e) { return false; }
    }

    // -------------------------------------------------------------------------
    // Favourites
    // -------------------------------------------------------------------------

    public List<FavouriteMovieDTO> getFavourites(long userId) {
        try {
            List<FavouriteMovieDTO> list = apiClient.get().uri("/api/user/{userId}/favourites", userId)
                    .retrieve().bodyToMono(new ParameterizedTypeReference<List<FavouriteMovieDTO>>() {}).block();
            return list != null ? list : List.of();
        } catch (Exception e) { log.error("Failed to fetch favourites: {}", e.getMessage()); return List.of(); }
    }

    public boolean addToFavourites(long userId, int movieId) {
        try {
            apiClient.post().uri("/api/user/{userId}/favourites/{movieId}", userId, movieId)
                    .retrieve().toBodilessEntity().block();
            return true;
        } catch (WebClientResponseException.Conflict e) { return false; }
        catch (Exception e) { log.error("Failed to add to favourites: {}", e.getMessage()); return false; }
    }

    public boolean removeFromFavourites(long userId, int movieId) {
        try {
            apiClient.delete().uri("/api/user/{userId}/favourites/{movieId}", userId, movieId)
                    .retrieve().toBodilessEntity().block();
            return true;
        } catch (Exception e) { log.error("Failed to remove from favourites: {}", e.getMessage()); return false; }
    }

    public boolean isInFavourites(long userId, int movieId) {
        try {
            return getFavourites(userId).stream()
                    .anyMatch(f -> f.getMovieId() != null && f.getMovieId() == movieId);
        } catch (Exception e) { return false; }
    }

    // -------------------------------------------------------------------------
    // Recommendations
    // -------------------------------------------------------------------------

    public List<MovieListItemDTO> getRecommendations(long userId) {
        try {
            List<MovieListItemDTO> list = apiClient.get().uri("/api/user/{userId}/recommendations", userId)
                    .retrieve().bodyToMono(new ParameterizedTypeReference<List<MovieListItemDTO>>() {}).block();
            return list != null ? list : List.of();
        } catch (Exception e) { log.error("Failed to fetch recommendations: {}", e.getMessage()); return List.of(); }
    }

    // -------------------------------------------------------------------------
    // Forum — posts
    // -------------------------------------------------------------------------

    public List<ForumPostDTO> getAllPosts() {
        try {
            List<ForumPostDTO> posts = apiClient.get().uri("/api/forum/posts")
                    .retrieve().bodyToMono(new ParameterizedTypeReference<List<ForumPostDTO>>() {}).block();
            return posts != null ? posts : List.of();
        } catch (Exception e) { log.error("Failed to fetch forum posts: {}", e.getMessage()); return List.of(); }
    }

    public List<ForumPostDTO> searchPosts(String keyword) {
        try {
            List<ForumPostDTO> posts = apiClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/forum/posts")
                            .queryParam("search", keyword).build())
                    .retrieve().bodyToMono(new ParameterizedTypeReference<List<ForumPostDTO>>() {}).block();
            return posts != null ? posts : List.of();
        } catch (Exception e) { log.error("Failed to search forum posts: {}", e.getMessage()); return List.of(); }
    }

    public List<ForumPostDTO> getPostsByCategory(String category) {
        try {
            List<ForumPostDTO> posts = apiClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/forum/posts")
                            .queryParam("category", category).build())
                    .retrieve().bodyToMono(new ParameterizedTypeReference<List<ForumPostDTO>>() {}).block();
            return posts != null ? posts : List.of();
        } catch (Exception e) { log.error("Failed to fetch posts for category '{}': {}", category, e.getMessage()); return List.of(); }
    }

    public Optional<ForumPostDTO> createPost(String title, String content, Long userId, String category) {
        try {
            java.util.Map<String, Object> body = new java.util.HashMap<>();
            body.put("title", title);
            body.put("content", content);
            body.put("userId", userId != null ? userId : 0L);
            if (category != null && !category.isBlank())
                body.put("category", category.trim().toLowerCase());
            ForumPostDTO post = apiClient.post().uri("/forum/posts").bodyValue(body)
                    .retrieve().bodyToMono(ForumPostDTO.class).block();
            return Optional.ofNullable(post);
        } catch (Exception e) { log.error("Failed to create post: {}", e.getMessage()); return Optional.empty(); }
    }

    // Legacy overload without category — defaults to general
    public Optional<ForumPostDTO> createPost(String title, String content, Long userId) {
        return createPost(title, content, userId, "general");
    }

    public Optional<ForumPostDTO> updatePost(long postId, String title, String content) {
        try {
            Map<String, String> body = Map.of("title", title, "content", content);
            ForumPostDTO post = apiClient.put().uri("/api/forum/posts/{id}", postId).bodyValue(body)
                    .retrieve().bodyToMono(ForumPostDTO.class).block();
            return Optional.ofNullable(post);
        } catch (Exception e) { log.error("Failed to update post: {}", e.getMessage()); return Optional.empty(); }
    }

    public boolean deletePost(long postId, long userId, String userRole) {
        try {
            apiClient.delete()
                    .uri("/api/forum/posts/{id}?userId={userId}&userRole={userRole}", postId, userId, userRole)
                    .retrieve().toBodilessEntity().block();
            return true;
        } catch (WebClientResponseException e) { return false; }
        catch (Exception e) { log.error("Failed to delete post: {}", e.getMessage()); return false; }
    }

    // -------------------------------------------------------------------------
    // Forum — comments
    // -------------------------------------------------------------------------

    public List<ForumCommentDTO> getCommentsForPost(long postId) {
        try {
            List<ForumCommentDTO> comments = apiClient.get().uri("/api/forum/comments/{postId}", postId)
                    .retrieve().bodyToMono(new ParameterizedTypeReference<List<ForumCommentDTO>>() {}).block();
            return comments != null ? comments : List.of();
        } catch (Exception e) { log.error("Failed to fetch comments: {}", e.getMessage()); return List.of(); }
    }

    public Optional<ForumCommentDTO> createComment(long postId, long userId, String content) {
        try {
            Map<String, Object> body = Map.of("postId", postId, "userId", userId, "content", content);
            ForumCommentDTO comment = apiClient.post().uri("/api/forum/comments").bodyValue(body)
                    .retrieve().bodyToMono(ForumCommentDTO.class).block();
            return Optional.ofNullable(comment);
        } catch (Exception e) { log.error("Failed to create comment: {}", e.getMessage()); return Optional.empty(); }
    }

    public boolean deleteForumComment(long commentId, long userId, String userRole) {
        try {
            apiClient.delete()
                    .uri(uriBuilder -> uriBuilder.path("/api/forum/comments/{id}")
                            .queryParam("userId", userId)
                            .queryParam("userRole", userRole)
                            .build(commentId))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;
        } catch (WebClientResponseException e) { return false; }
        catch (Exception e) { log.error("Failed to delete comment: {}", e.getMessage()); return false; }
    }

    // -------------------------------------------------------------------------
    // Inner classes
    // -------------------------------------------------------------------------

    public static class MovieListResult {
        private List<MovieListItemDTO> results;
        public List<MovieListItemDTO> getResults() { return results; }
        public void setResults(List<MovieListItemDTO> r) { this.results = r; }
    }

    public static class ApiResponse<T> {
        private boolean success;
        private T data;
        private String error;
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean s) { this.success = s; }
        public T getData() { return data; }
        public void setData(T d) { this.data = d; }
        public String getError() { return error; }
        public void setError(String e) { this.error = e; }
    }
}