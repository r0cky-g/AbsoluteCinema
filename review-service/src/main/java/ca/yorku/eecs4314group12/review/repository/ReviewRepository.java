package ca.yorku.eecs4314group12.review.repository;

import ca.yorku.eecs4314group12.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    //Find all non-deleted reviews for a specific movie, ordered by creation date (newest first)
    List<Review> findByMovieIdAndIsDeletedFalseOrderByCreatedAtDesc(Long movieId);
    
    //Find all non-deleted reviews by a specific user, ordered by creation date (newest first)
    List<Review> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId);
    
    //Check if a user has already reviewed a specific movie
    boolean existsByUserIdAndMovieIdAndIsDeletedFalse(Long userId, Long movieId);
    
    // Find a specific review by user and movie
    Optional<Review> findByUserIdAndMovieIdAndIsDeletedFalse(Long userId, Long movieId);
    
    // Calculate average rating for a movie using JPQL
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.movieId = :movieId AND r.isDeleted = false")
    Double calculateAverageRating(@Param("movieId") Long movieId);
    
    // Count total non-deleted reviews for a movie
    long countByMovieIdAndIsDeletedFalse(Long movieId);
    
    // Find top-rated reviews (by helpful count) for a movie
    List<Review> findTop10ByMovieIdAndIsDeletedFalseOrderByHelpfulCountDescCreatedAtDesc(Long movieId);
    
    // Find reviews with rating above a threshold
    List<Review> findByMovieIdAndRatingGreaterThanEqualAndIsDeletedFalse(Long movieId, Integer minRating);
}
