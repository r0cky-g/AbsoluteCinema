package ca.yorku.eecs4314group12.review.controller;

import ca.yorku.eecs4314group12.review.dto.ApiResponse;
import ca.yorku.eecs4314group12.review.dto.ReviewDTO;
import ca.yorku.eecs4314group12.review.service.ReviewService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: After Check-in 1 - Add authentication filter/interceptor
@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")  // TODO: After Check-in 1 - Restrict to specific origins in production
public class ReviewController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);
    
    private final ReviewService reviewService;
    
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    
    //TODO: After Check-in 1 - Add authentication
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDTO>> createReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        logger.info("POST /api/reviews - Creating review for movie {} by user {}", 
                reviewDTO.getMovieId(), reviewDTO.getUserId());
        
        // TODO: After Check-in 1 - Extract userId from JWT token
        // Long userId = jwtTokenProvider.getUserIdFromToken(token);
        // reviewDTO.setUserId(userId);  // Don't trust client-provided userId
        
        try {
            ReviewDTO createdReview = reviewService.createReview(reviewDTO);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Review created successfully", createdReview));
        } catch (IllegalStateException e) {
            logger.error("Review creation failed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating review", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create review"));
        }
    }
    
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getReviewsByMovie(@PathVariable Long movieId) {
        logger.info("GET /api/reviews/movie/{} - Fetching reviews", movieId);
        
        try {
            List<ReviewDTO> reviews = reviewService.getReviewsByMovie(movieId);
            return ResponseEntity.ok(ApiResponse.success(reviews));
        } catch (Exception e) {
            logger.error("Error fetching reviews for movie {}", movieId, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch reviews"));
        }
    }
    
    // TODO: After Check-in 1 - Consider making this authenticated
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getReviewsByUser(@PathVariable Long userId) {
        logger.info("GET /api/reviews/user/{} - Fetching user reviews", userId);
        
        // TODO: After Check-in 1 - Verify requester has permission
        // Long requesterId = jwtTokenProvider.getUserIdFromToken(token);
        // if (!requesterId.equals(userId) && !hasModeratorRole(requesterId)) {
        //     return forbidden();
        // }
        
        try {
            List<ReviewDTO> reviews = reviewService.getReviewsByUser(userId);
            return ResponseEntity.ok(ApiResponse.success(reviews));
        } catch (Exception e) {
            logger.error("Error fetching reviews for user {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch user reviews"));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewDTO>> getReviewById(@PathVariable Long id) {
        logger.info("GET /api/reviews/{} - Fetching review", id);
        
        try {
            ReviewDTO review = reviewService.getReviewById(id);
            return ResponseEntity.ok(ApiResponse.success(review));
        } catch (IllegalArgumentException e) {
            logger.error("Review not found: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching review {}", id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch review"));
        }
    }
    
    // TODO: After Check-in 1 - Add authentication
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewDTO>> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewDTO reviewDTO) {
        logger.info("PUT /api/reviews/{} - Updating review", id);
        
        // TODO: After Check-in 1 - Extract userId from JWT token
        // Long userId = jwtTokenProvider.getUserIdFromToken(token);
        // reviewDTO.setUserId(userId);  // Don't trust client-provided userId
        
        try {
            ReviewDTO updatedReview = reviewService.updateReview(id, reviewDTO);
            return ResponseEntity.ok(ApiResponse.success("Review updated successfully", updatedReview));
        } catch (IllegalArgumentException e) {
            logger.error("Review not found: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            logger.error("Update not allowed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating review {}", id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update review"));
        }
    }
    
    // TODO: After Check-in 1 - Add authentication
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long id,
            @RequestParam Long userId) {  // TODO: Remove this, get from token instead
        logger.info("DELETE /api/reviews/{} - Deleting review by user {}", id, userId);
        
        // TODO: After Check-in 1 - Extract userId from JWT token
        // Long userId = jwtTokenProvider.getUserIdFromToken(token);
        // UserRole role = userServiceClient.getUserRole(userId);
        // Allow if: user is owner OR user is moderator/admin
        
        try {
            reviewService.deleteReview(id, userId);
            return ResponseEntity.ok(ApiResponse.success("Review deleted successfully", null));
        } catch (IllegalArgumentException e) {
            logger.error("Review not found: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            logger.error("Delete not allowed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting review {}", id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete review"));
        }
    }
    
    // TODO: After Check-in 1 - Consider caching this result
    @GetMapping("/movie/{movieId}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMovieStats(@PathVariable Long movieId) {
        logger.info("GET /api/reviews/movie/{}/stats - Fetching statistics", movieId);
        
        // TODO: After Check-in 1 - Add caching
        // Check Redis cache first before calculating
        // @Cacheable(value = "movie-stats", key = "#movieId")
        
        try {
            Double averageRating = reviewService.getAverageRating(movieId);
            long reviewCount = reviewService.getReviewCount(movieId);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("movieId", movieId);
            stats.put("averageRating", Math.round(averageRating * 10.0) / 10.0);
            stats.put("reviewCount", reviewCount);
            
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            logger.error("Error fetching stats for movie {}", movieId, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch movie statistics"));
        }
    }
    
    // TODO: After Check-in 1 - Add authentication
    @PostMapping("/{id}/helpful")
    public ResponseEntity<ApiResponse<ReviewDTO>> markAsHelpful(@PathVariable Long id) {
        logger.info("POST /api/reviews/{}/helpful - Marking review as helpful", id);
        
        // TODO: After Check-in 1 - Extract userId from JWT token
        // Long userId = jwtTokenProvider.getUserIdFromToken(token);
        // Pass userId to service to track who voted
        // Prevent same user from voting multiple times
        
        try {
            ReviewDTO review = reviewService.markAsHelpful(id);
            return ResponseEntity.ok(ApiResponse.success("Review marked as helpful", review));
        } catch (IllegalArgumentException e) {
            logger.error("Review not found: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error marking review {} as helpful", id, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to mark review as helpful"));
        }
    }
}
