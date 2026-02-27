package ca.yorku.eecs4314group12.review.service;

import ca.yorku.eecs4314group12.review.dto.ReviewDTO;
import ca.yorku.eecs4314group12.review.model.Review;
import ca.yorku.eecs4314group12.review.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Review operations
 * Contains business logic and coordinates between controller and repository
 */
@Service
public class ReviewService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    
    private final ReviewRepository reviewRepository;
    
    // TODO: After Check-in 1 - Inject UserServiceClient and MovieServiceClient here
    // private final UserServiceClient userServiceClient;
    // private final MovieServiceClient movieServiceClient;
    
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }
    
    //Create a new review
    @Transactional
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        logger.info("Creating review for movie {} by user {}", reviewDTO.getMovieId(), reviewDTO.getUserId());
        
        // TODO: After Check-in 1 - Add User Service Integration
        // Step 1: Verify user session with User Management Service
        // POST /api/auth/verify with session token
        // if (!userServiceClient.verifySession(sessionToken)) {
        //     throw new UnauthorizedException("Invalid session");
        // }
        
        // Check if user already reviewed this movie
        if (reviewRepository.existsByUserIdAndMovieIdAndIsDeletedFalse(
                reviewDTO.getUserId(), reviewDTO.getMovieId())) {
            throw new IllegalStateException("User has already reviewed this movie");
        }
        
        // TODO: After Check-in 1 - Add Movie Service Integration
        // Step 2: Verify movie exists in Movie Service
        // GET /api/movies/{movieId} to ensure movie is valid
        // if (!movieServiceClient.movieExists(reviewDTO.getMovieId())) {
        //     throw new IllegalArgumentException("Movie not found");
        // }
        
        // Create new review entity
        Review review = new Review(
            reviewDTO.getUserId(),
            reviewDTO.getMovieId(),
            reviewDTO.getRating(),
            reviewDTO.getTitle(),
            reviewDTO.getContent()
        );
        
        if (reviewDTO.getIsSpoiler() != null) {
            review.setIsSpoiler(reviewDTO.getIsSpoiler());
        }
        
        Review savedReview = reviewRepository.save(review);
        logger.info("Review created successfully with ID: {}", savedReview.getId());
        
        // TODO: After Check-in 1 - Notify Movie Service of new review
        // Step 3: Update movie statistics in Movie Service
        // Calculate new average rating and count
        // Double newAvgRating = calculateAverageRating(reviewDTO.getMovieId());
        // long newCount = getReviewCount(reviewDTO.getMovieId());
        // POST /api/movies/stats/update
        // movieServiceClient.updateMovieStats(reviewDTO.getMovieId(), newAvgRating, newCount);
        
        return convertToDTO(savedReview);
    }
    
    // Get all reviews for a specific movie
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByMovie(Long movieId) {
        logger.info("Fetching reviews for movie {}", movieId);
        
        // TODO: After Check-in 1 - Enhance with user data
        // Step 1: Get reviews from database
        List<Review> reviews = reviewRepository.findByMovieIdAndIsDeletedFalseOrderByCreatedAtDesc(movieId);
        
        // Step 2: Enrich each review with username from User Service
        // GET /api/users/{userId} for each unique userId
        // Map<Long, String> usernames = userServiceClient.getUsernames(userIds);
        // Then set dto.setUsername(usernames.get(review.getUserId()))
        
        return reviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get all reviews by a specific user
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByUser(Long userId) {
        logger.info("Fetching reviews by user {}", userId);
        
        // TODO: After Check-in 1 - Verify user exists
        // if (!userServiceClient.userExists(userId)) {
        //     throw new IllegalArgumentException("User not found");
        // }
        
        List<Review> reviews = reviewRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId);
        
        // TODO: After Check-in 1 - Enrich with movie titles
        // GET /api/movies/batch with list of movieIds
        // Map<Long, String> movieTitles = movieServiceClient.getMovieTitles(movieIds);
        // Then set dto.setMovieTitle(movieTitles.get(review.getMovieId()))
        
        return reviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get a specific review by ID
    @Transactional(readOnly = true)
    public ReviewDTO getReviewById(Long id) {
        logger.info("Fetching review with ID {}", id);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + id));
        
        if (review.getIsDeleted()) {
            throw new IllegalArgumentException("Review has been deleted");
        }
        
        // TODO: After Check-in 1 - Enrich with user and movie data
        // ReviewDTO dto = convertToDTO(review);
        // dto.setUsername(userServiceClient.getUsername(review.getUserId()));
        // dto.setMovieTitle(movieServiceClient.getMovieTitle(review.getMovieId()));
        // return dto;
        
        return convertToDTO(review);
    }
    
    // Update an existing review
    @Transactional
    public ReviewDTO updateReview(Long id, ReviewDTO reviewDTO) {
        logger.info("Updating review with ID {}", id);
        
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + id));
        
        // TODO: After Check-in 1 - Verify with User Service
        // Instead of just checking userId match, verify the session token
        // if (!userServiceClient.verifyUserOwnership(sessionToken, review.getUserId())) {
        //     throw new ForbiddenException("Not authorized to update this review");
        // }
        
        // Business rule: Only the owner can update their review
        if (!review.getUserId().equals(reviewDTO.getUserId())) {
            throw new IllegalStateException("User can only update their own reviews");
        }
        
        // Store old rating to check if it changed
        Integer oldRating = review.getRating();
        
        // Update review fields
        review.setRating(reviewDTO.getRating());
        review.setTitle(reviewDTO.getTitle());
        review.setContent(reviewDTO.getContent());
        if (reviewDTO.getIsSpoiler() != null) {
            review.setIsSpoiler(reviewDTO.getIsSpoiler());
        }
        
        Review updatedReview = reviewRepository.save(review);
        logger.info("Review updated successfully");
        
        // TODO: After Check-in 1 - Update Movie Service if rating changed
        // if (!oldRating.equals(reviewDTO.getRating())) {
        //     Double newAvgRating = calculateAverageRating(review.getMovieId());
        //     movieServiceClient.updateMovieStats(review.getMovieId(), newAvgRating, getReviewCount(review.getMovieId()));
        // }
        
        return convertToDTO(updatedReview);
    }
    
    // Delete a review (soft delete)
    @Transactional
    public void deleteReview(Long id, Long userId) {
        logger.info("Deleting review with ID {} by user {}", id, userId);
        
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + id));
        
        // TODO: After Check-in 1 - Verify with User Service
        // Check if user is owner OR has moderator/admin role
        // UserRole role = userServiceClient.getUserRole(userId);
        // boolean isOwner = review.getUserId().equals(userId);
        // boolean isModerator = role == UserRole.MODERATOR || role == UserRole.ADMIN;
        // if (!isOwner && !isModerator) {
        //     throw new ForbiddenException("Not authorized to delete this review");
        // }
        
        //  Only the owner can delete their review
        if (!review.getUserId().equals(userId)) {
            throw new IllegalStateException("User can only delete their own reviews");
        }
        
        review.softDelete();
        reviewRepository.save(review);
        logger.info("Review deleted successfully");
        
        // TODO: After Check-in 1 - Update Movie Service stats
        // Recalculate average rating excluding deleted review
        // Double newAvgRating = calculateAverageRating(review.getMovieId());
        // long newCount = getReviewCount(review.getMovieId());
        // movieServiceClient.updateMovieStats(review.getMovieId(), newAvgRating, newCount);
    }
    
    // Get average rating for a movie
    @Transactional(readOnly = true)
    public Double getAverageRating(Long movieId) {
        logger.info("Calculating average rating for movie {}", movieId);
        Double average = reviewRepository.calculateAverageRating(movieId);
        return average != null ? average : 0.0;
    }
    
    // Get total review count for a movie
    @Transactional(readOnly = true)
    public long getReviewCount(Long movieId) {
        return reviewRepository.countByMovieIdAndIsDeletedFalse(movieId);
    }
    
    // Rating other reviews positive (helpful)
    @Transactional
    public ReviewDTO markAsHelpful(Long id) {
        logger.info("Marking review {} as helpful", id);
        
        // TODO: After Check-in 1 - Track who marked it helpful
        // Prevent users from marking the same review helpful multiple times
        // if (reviewHelpfulRepository.existsByReviewIdAndUserId(id, userId)) {
        //     throw new IllegalStateException("Already marked as helpful");
        // }
        
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + id));
        
        review.markAsHelpful();
        Review updatedReview = reviewRepository.save(review);
        
        // TODO: After Check-in 1 - Store the helpful vote
        // reviewHelpfulRepository.save(new ReviewHelpful(id, userId, LocalDateTime.now()));
        
        return convertToDTO(updatedReview);
    }
    
    // Convert Review entity to DTO
    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setUserId(review.getUserId());
        dto.setMovieId(review.getMovieId());
        dto.setRating(review.getRating());
        dto.setTitle(review.getTitle());
        dto.setContent(review.getContent());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        dto.setHelpfulCount(review.getHelpfulCount());
        dto.setIsSpoiler(review.getIsSpoiler());
        
        // TODO: After Check-in 1 - Add username and movie title
        // These fields are currently null but will be populated when services are integrated:
        // dto.setUsername(null);  // Will get from User Service
        // dto.setMovieTitle(null);  // Will get from Movie Service
        
        return dto;
    }
}
