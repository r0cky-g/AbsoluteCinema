package ca.yorku.eecs4314group12.api.dto.reviewServiceDTO;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Review
 * Used for API requests and responses
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
    
    // Additional fields for display purposes
    private String username;
    private String movieTitle;
    
    // Constructors
    public ReviewDTO() {
    }
    
    public ReviewDTO(Long userId, Long movieId, Integer rating, String title, String content) {
        this.userId = userId;
        this.movieId = movieId;
        this.rating = rating;
        this.title = title;
        this.content = content;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getHelpfulCount() {
        return helpfulCount;
    }

    public void setHelpfulCount(Integer helpfulCount) {
        this.helpfulCount = helpfulCount;
    }

    public Boolean getIsSpoiler() {
        return isSpoiler;
    }

    public void setIsSpoiler(Boolean isSpoiler) {
        this.isSpoiler = isSpoiler;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }
}