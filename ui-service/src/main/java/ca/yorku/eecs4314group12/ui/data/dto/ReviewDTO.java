package ca.yorku.eecs4314group12.ui.data.dto;

import java.time.LocalDateTime;

/**
 * Mirrors the ReviewDTO returned by review-service GET /api/reviews/movie/{id}.
 */
public class ReviewDTO {

    private Long id;
    private Long userId;
    private Long movieId;
    private Integer rating;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer helpfulCount;
    private Boolean isSpoiler;
    private String username;    // null until user-service enrichment is implemented
    private String movieTitle;  // null until movie-service enrichment is implemented

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getHelpfulCount() { return helpfulCount != null ? helpfulCount : 0; }
    public void setHelpfulCount(Integer helpfulCount) { this.helpfulCount = helpfulCount; }

    public Boolean getIsSpoiler() { return isSpoiler != null ? isSpoiler : false; }
    public void setIsSpoiler(Boolean isSpoiler) { this.isSpoiler = isSpoiler; }

    public String getUsername() {
        // Fall back to "User #ID" until user-service enrichment is done
        return username != null ? username : "User #" + userId;
    }
    public void setUsername(String username) { this.username = username; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    /** Returns date as a simple string for display. */
    public String getDatePosted() {
        if (createdAt == null) return "Unknown";
        return createdAt.toLocalDate().toString();
    }

    /** Maps 1–10 rating to 1–5 stars for display. */
    public int getStars() {
        if (rating == null) return 0;
        return (int) Math.round(rating / 2.0);
    }
}