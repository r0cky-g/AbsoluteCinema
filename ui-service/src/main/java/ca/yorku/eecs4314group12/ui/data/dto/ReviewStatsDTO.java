package ca.yorku.eecs4314group12.ui.data.dto;

/**
 * Deserializes the data payload from review-service
 * GET /api/reviews/movie/{id}/stats
 *
 * Response shape: { movieId: 550, averageRating: 8.3, reviewCount: 4 }
 */
public class ReviewStatsDTO {

    private Long movieId;
    private Double averageRating;
    private Long reviewCount;

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }

    public Double getAverageRating() { return averageRating != null ? averageRating : 0.0; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Long getReviewCount() { return reviewCount != null ? reviewCount : 0L; }
    public void setReviewCount(Long reviewCount) { this.reviewCount = reviewCount; }
}